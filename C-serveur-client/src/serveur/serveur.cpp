#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <iostream>
#include <unistd.h>
#include <time.h>
#include <signal.h>
#include <pthread.h>
#include <fstream>
#include <string>
#include <sstream>
#include "../lib/mynet.h"
#include "../lib/protocol.hpp"

#define N_THREADS 50 // nombre MAX de THREADS
#define MAX_CLIENT 10

//Partie etat du serveur
bool stop_serveur;

//Partie configuration serveur
int _socket;
int _port = 50000;
char Adresse[NI_MAXHOST];
char PORT[NI_MAXSERV];

//Partie hôtes connectés
int idLecture = 0;
int idEcriture = 0;
int listAttente[MAX_CLIENT];
int listActive[N_THREADS];

//Partie threads
int N_threads = 10;
pthread_t serveur_thread;
pthread_cond_t cond_net;
pthread_mutex_t mutex_net;

//Partie Mysql
MYSQL* connexion = mysql_init(NULL);

void Start();
void* Threads(void*);
void Config(void);
void InitServeur(int port);
void InitialiserListes();

int RecevoirListAttente(void);
int InsererListAttente(int _external_socket);

void InsererListActive(int _external_socket);
void RetirerListActive(int _external_socket);

void HandlerSIGINT(int);
void AjouterSocket(int new_socket);
void NettoyerListeAttente();
void NettoyerListeActive();

void AfficherlisteAttente();
void AfficherlisteActive();


int main(int argc, char* argv[])
{
    //InitServeur(atoi(argv[1]));
    Config();
    InitialiserListes();
    //AfficherlisteAttente();
    InitServeur(_port);
    Start();
}

void Config(void)
{
    std::fstream a;
    a.open(".config",std::fstream::in);
    if(a.is_open())
    {
        bool isOK = true;
        char conf[50];
        while(a.getline(conf,50,'\n'))
        {
            std::string line(conf);
            std::istringstream ss(line);

            int sep = line.find(":");

            std::string key = line.substr(0,sep);
            std::string value = line.substr(sep+1);

            if(key == "PORT_ACHAT")
            {
                //std::cout << "PORT_ACHAT: " << value << std::endl;
                _port = std::stoi(value);
            } 
            else if(key == "N_THREAD") 
            {
                //std::cout << "N_THREAD: " << value << std::endl;
                N_threads = std::stoi(value);
            }
            else 
            {
                std::cout << "Cle-Valeur indefinie: " << key << value << std::endl;
                isOK = false;
                break;
            }
        }
        a.close();

        if(!isOK)
        {
            a.open(".config",std::fstream::out | std::fstream::trunc);
            if(a.is_open())
            {
                _port = 50000;
                N_threads = 10;

                a << "PORT_ACHAT: " << _port << std::endl;
                a << "N_THREAD: " << N_threads << std::endl;

                a.close();
            }
        }      
    }
    else
    {
        a.open(".config",std::fstream::out | std::fstream::app);
        if(a.is_open())
        {
            _port = 50000;
            N_threads = 10;

            a << "PORT_ACHAT: " << _port << std::endl;
            a << "N_THREAD: " << N_threads << std::endl;

            a.close();
        }
    }
    std::cout << " -- Configuration --" << std::endl;
    std::cout << "[serveur] numero de port    : " << _port << std::endl;
    std::cout << "[serveur] nombre de threads : " << N_threads << std::endl;
    std::cout << " -------------------" << std::endl;
}

void InitServeur(int port)
{
    std::cout << "-- Initialisation --" << std::endl;
    std::cout << "[serveur] Informations de bases : PID = "  + std::to_string(getpid()) << std::endl;

    _socket = creerSocket(port);

    localhostInformation(_socket,Adresse,PORT);
    std::cout << "[serveur] net-address   : " +  std::string(Adresse) << std::endl;
    std::cout << "[serveur] net-port      : " +  std::string(PORT) << std::endl;
    std::cout << "--------------------" << std::endl;

    activerListen(_socket, N_threads);
}


void Start()
{
    std::cout << "[serveur] Demarrage ..." << std::endl;

    int  external_socket;
    char ADDRESS[NI_MAXHOST];
    char PORT[NI_MAXSERV];

    //Mise en place des mask
    sigset_t mask;
	sigemptyset(&mask);
	sigaddset(&mask,SIGINT);
	pthread_sigmask(SIG_UNBLOCK,&mask,NULL);

    struct sigaction signal;
	sigemptyset(&signal.sa_mask);
	signal.sa_flags = 0;
	signal.sa_handler = HandlerSIGINT;
	sigaction(SIGINT,&signal,NULL);

    pthread_cond_init(&cond_net,NULL);
    pthread_mutex_init(&mutex_net,NULL);

    //Connexion Mysql
    printf("[serveur] Connection a la BD...\n");
    mysql_real_connect(connexion,"localhost","Student","PassStudent1_","PourStudent",0,0,0);


    //=============================================================
    std::cout << "[serveur] Creation des threads ..." << std::endl;
    for(int i = 0; i < N_threads; i++)
    {
        pthread_create(&serveur_thread,NULL,Threads,NULL);
    }

    while(!stop_serveur)
    {
        std::cout << std::endl;
        std::cout << "[serveur] En attente de connexion ..." << std::endl << std::endl;
        external_socket = accepterConnexion(_socket);
        if(external_socket != -1)
        {
            externalhostInformation(external_socket,ADDRESS,PORT);

            std::cout << std::endl;
            std::cout << "---- hote connecte - informations ----" << std::endl;
            std::cout << "[serveur] external-host - net-address   : " +  std::string(ADDRESS) << std::endl;
            std::cout << "[serveur] external-host - net-port      : " +  std::string(PORT) << std::endl;
            std::cout << "--------------------------------------" << std::endl;

            //Transfert de la socket dans la liste d'attente
            int resultat_insertion = InsererListAttente(external_socket);
            if(resultat_insertion == -1)
            {
                //envoyer une requete de deconnexion qui affiche une boite d'erreur.
                //Ou fermer la socket car flemme
                std::cout << "[serveur] Pas de place dans la file d'attente ..." << std::endl;
                close(external_socket);
            }
        }
    }

    std::cout << "[serveur] Fermeture des sockets en attentes ..." << std::endl;
    NettoyerListeAttente();

    std::cout << "[serveur] Fermeture des sockets actives ..." << std::endl;
    NettoyerListeActive();

    std::cout << "[serveur] Fermeture de la connexion a la BD ..." << std::endl;
    mysql_close(connexion);

    std::cout << "[serveur] Fermeture du serveur ..." << std::endl;
    close(_socket);    
}

void* Threads(void*)
{
    int _socket = 0;
    bool has_normally_disconnected = false;
    char ADDRESS[NI_MAXHOST];
    char PORT[NI_MAXSERV];

    CADDIE_T panier;
    bzero(&panier,sizeof(CADDIE));
    

    while(true)
    {
        std::cout << "[thread " << pthread_self() << "] En attente de connexion ..." << std::endl;

        _socket = RecevoirListAttente();
        InsererListActive(_socket);

        AfficherlisteActive();

        externalhostInformation(_socket,ADDRESS,PORT);
        std::cout << "---- hote redirige vers " << pthread_self() << " ----" << std::endl;
        std::cout << "[thread] external-host - net-address   : " +  std::string(ADDRESS) << std::endl;
        std::cout << "[thread] external-host - net-port      : " +  std::string(PORT) << std::endl << std::endl;

        int resultat;
        char dataRecevoir[TAILLE_MAX_DATA] = {0};
        char dataEnvoyer[TAILLE_MAX_DATA] = {0};

        std::cout << "[thread " << pthread_self() << "] En attente d'une requete ..." << std::endl;
        while((resultat = recevoirData(_socket,dataRecevoir)) > 0 )
        {
            //gestion des données recue donc inserer ici les fonctions d'analyses
            //OVESP(connexion,dataRecevoir,dataEnvoyer,_socket, panier);
            AnalyserRequete(panier,dataRecevoir,dataEnvoyer);

            if(strcmp(dataEnvoyer,"") != 0)
            {
                std::cout << "[thread " << pthread_self() << "] Envoie de la reponse :: " << dataEnvoyer << std::endl;
                envoyerData(_socket,dataEnvoyer,strlen(dataEnvoyer));
            }

            bzero(&dataRecevoir,TAILLE_MAX_DATA);
            bzero(&dataEnvoyer,TAILLE_MAX_DATA);
            //std::cout << std::endl;
            std::cout << "[thread " << pthread_self() << "] En attente d'une requete ..." << std::endl << std::endl;
        }        
        //Au cas ou le client ne peut pas transmettre un logout
        //il est OBLIGATOIRE de nettoyer le thread en cours
        strcpy(dataRecevoir,"LOGOUT#\r\n");
        AnalyserRequete(panier,dataRecevoir,dataEnvoyer);
        
        std::cout << "[thread " << pthread_self() << "] Hotes deconnecte ..." << std::endl;
        RetirerListActive(_socket);
        close(_socket);
    }
    std::cout << "[thread] Fermeture du thread" << pthread_self() << "..." << std::endl << std::endl;
    pthread_exit(0);
}


void InsererListActive(int _external_socket)
{
    pthread_mutex_lock(&mutex_net);
    for(int& i : listActive)
    {
        if(i == -1)
        {
            i = _external_socket;
            break;
        }
    }
    pthread_mutex_unlock(&mutex_net);
}

void RetirerListActive(int _external_socket)
{
    pthread_mutex_lock(&mutex_net);
    for(int& i : listActive)
    {
        if(i == _external_socket)
        {
            i = -1;
            break;
        }
    }
    pthread_mutex_unlock(&mutex_net);
}


int InsererListAttente(int _external_socket)
{
    int r_value = 0;
    pthread_mutex_lock(&mutex_net);
        if(listAttente[idEcriture] == -1)
        {
            listAttente[idEcriture] = _external_socket;
            if(idEcriture == N_threads-1)
                idEcriture = 0;
            else 
                idEcriture++;
            r_value = 0;
        }
        else 
            r_value = -1;
    pthread_mutex_unlock(&mutex_net);
    pthread_cond_signal(&cond_net);
    return r_value;
}

int RecevoirListAttente(void)
{
    int socket_ = 0;
    pthread_mutex_lock(&mutex_net);
            if(listAttente[idLecture] == -1) pthread_cond_wait(&cond_net,&mutex_net);
                socket_ = listAttente[idLecture];
                listAttente[idLecture] = -1;
                if(idLecture == N_threads-1)
                    idLecture = 0;
                else 
                    idLecture++;
    pthread_mutex_unlock(&mutex_net);
    return socket_;
}

void HandlerSIGINT(int)
{
    std::cout << std::endl;
    std::cout << "[Serveur] Handler de SIGINT ..." << std::endl;
    
    stop_serveur = true;    
}

void NettoyerListeAttente()
{
    pthread_mutex_lock(&mutex_net);
    for(int i : listAttente)
    {
        if(i > 0)
        {
            std::cout << "[handler] Fermeture de la socket en attente : " << i << std::endl;
            close(i); 
        }
    }
    pthread_mutex_unlock(&mutex_net);
}

void NettoyerListeActive()
{
    pthread_mutex_lock(&mutex_net);
    for(int i : listActive)
    {
        if(i > 0)
        {
            std::cout << "[handler] Fermeture de la socket active : " << i << std::endl;
            close(i); 
        }
    }
    pthread_mutex_unlock(&mutex_net);
}

void AfficherlisteAttente()
{
    printf("liste attente\n[");
    for(int i = 0; i < MAX_CLIENT; i++)
    {
        printf("%d,", listAttente[i]);
    }
    printf("]\n");
}

void AfficherlisteActive()
{
    printf("liste active\n[");
    for(int i = 0; i < N_THREADS; i++)
    {
        printf("%d,", listActive[i]);
    }
    printf("]\n");
}


void InitialiserListes()
{
    pthread_mutex_lock(&mutex_net);
    for(int i = 0; i < MAX_CLIENT; i++)
    {
        listAttente[i] = -1;
    }

    for(int i = 0; i < N_THREADS; i++)
    {
        listActive[i] = -1;
    }
    idLecture = 0;
    idEcriture = 0;
    pthread_mutex_unlock(&mutex_net);
}

