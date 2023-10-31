#include <iostream>
#include <iomanip>
#include <signal.h>
#include <string.h>
#include "../lib/mynet.h"


void HandlerSIGINT(int);
void AnalyseReponse(char* reponse);


int socket_;
int idClient = 0;
ARTICLE articleCourrant = {1,"carottes",2.16,0,"carottes.jpg"};

char login[50];
char password[50];
bool isLogged = false;
std::string option;
std::string option2;

char data[TAILLE_MAX_DATA];

int main()
{

    struct sigaction signal;
	sigemptyset(&signal.sa_mask);
	signal.sa_flags = 0;
	signal.sa_handler = HandlerSIGINT;
	sigaction(SIGINT,&signal,NULL);

    socket_ = creerSocket(0);

    std::cout << std::endl;
    std::cout << "Debut client" << std::endl;

    int i = 0;
    do
    {
        i = connecter(socket_,"localhost",50000);
        if(i == 0)
            std::cout << "Bien connecte" << std::endl;
        else 
            std::cout << "Non connecte" << std::endl;
    } while (i != 0);
    std::cout << std::endl;


    std::string choix = "0";
    while(std::stoi(choix) != 9)
    {    
        std::cout << "=======================" << std::endl;
        std::cout << "--- ARTICLE COURRANT ---"<< std::endl;
        std::cout << "- id      :" << articleCourrant.id << std::endl;
        std::cout << "- intitule:" << articleCourrant.intitule << std::endl;
        std::cout << "- prix    :" << articleCourrant.prix << std::endl;
        std::cout << "- stock   :" << articleCourrant.stock << std::endl;
        std::cout << "- image   :" << articleCourrant.image << std::endl;
        std::cout << "=======================" << std::endl;

        std::cout << "=======================" << std::endl;
        std::cout << "Selectionner Requete : " << std::endl;
        std::cout << "[1] LOGIN"                << std::endl;
        std::cout << "[2] CONSULT"              << std::endl;
        std::cout << "[3] ACHAT"                << std::endl;
        std::cout << "[4] CADDIE"               << std::endl;
        std::cout << "[5] RETIRER ARTICLE"      << std::endl;
        std::cout << "[6] VIDER LE PANIER"      << std::endl;
        std::cout << "[7] CONFIRMER"            << std::endl;
        std::cout << "[8] LOGOUT"               << std::endl;
        std::cout << "[9] QUITTER"              << std::endl;
        std::cout << "=======================" << std::endl;
        std::flush(std::cout);

        std::cout << "Reponse: ";
        std::cin >> choix;

        bzero(data,sizeof(data));

        switch (std::stoi(choix))
        {
            case 1:
                    if(!isLogged)
                    {
                        do
                        {
                            std::cout << "[0] Connexion" << std::endl;
                            std::cout << "[1] Nouveau client " << std::endl;
                            std::cout << "=======================" << std::endl;
                            std::cout << "Reponse: ";
                            std::cin >> option;
                            
                        }while(std::stoi(option) != 0 && std::stoi(option) != 1);

                        std::cout << "Entrez un nom : ";
                        std::cin >> login;

                        std::cout << "Entrez le mot de passe : ";
                        std::cin >> password;

                        bzero(data,sizeof(data));
                        sprintf(data,"LOGIN#%s#%s#%d#",login,password,std::stoi(option));
                        envoyerData(socket_,data,sizeof(data));
                        
                        bzero(data,sizeof(data));
                        recevoirData(socket_,data);
                        AnalyseReponse(data);  
                    } 
                    else 
                        std::cout << "Vous devez vous deconnecter pour vous reconnecter!" << std::endl;               
                break;

            case 2:
            case 3:
                    if(isLogged)
                    {
                        do
                        {
                            std::cout << "=======================" << std::endl;
                            std::cout << "--- ARTICLE COURRANT ---"<< std::endl;
                            std::cout << "- id      :" << articleCourrant.id << std::endl;
                            std::cout << "- intitule:" << articleCourrant.intitule << std::endl;
                            std::cout << "- prix    :" << articleCourrant.prix << std::endl;
                            std::cout << "- stock   :" << articleCourrant.stock << std::endl;
                            std::cout << "- image   :" << articleCourrant.image << std::endl;
                            std::cout << "=======================" << std::endl;
                            std::cout << "[0] Suivant" << std::endl;
                            std::cout << "[1] Precedent " << std::endl;
                            std::cout << "[2] Acheter l'article" <<std::endl;
                            std::cout << "[3] Arreter de naviguer " << std::endl;
                            std::cout << "=======================" << std::endl;
                            std::cout << "Reponse: ";
                            std::cin >> option;

                            if(std::stoi(option) != 3)
                            {
                                bzero(data,sizeof(data));
                                if(std::stoi(option) == 0)
                                {
                                    bzero(data,sizeof(data));
                                    sprintf(data,"CONSULT#%d#",articleCourrant.id+1);
                                    envoyerData(socket_,data,sizeof(data));

                                    bzero(data,sizeof(data));
                                    recevoirData(socket_,data);
                                    AnalyseReponse(data);
                                }
                                else if(std::stoi(option) == 1)
                                {
                                    bzero(data,sizeof(data));
                                    sprintf(data,"CONSULT#%d#",articleCourrant.id-1);
                                    envoyerData(socket_,data,sizeof(data));

                                    bzero(data,sizeof(data));
                                    recevoirData(socket_,data);
                                    AnalyseReponse(data);
                                }
                                else if(std::stoi(option) == 2)
                                {
                                    std::cout << "Quel quantite voulez vous? " << std::endl;
                                    std::cout << "[0] Annuler" << std::endl;
                                    std::cout << "Reponse: ";
                                    std::cin >> option2;

                                    if(std::stoi(option2) != 0 && std::stoi(option2) > 0)
                                    {
                                        bzero(data,sizeof(data));
                                        sprintf(data,"ACHAT#%d#%d#",articleCourrant.id,std::stoi(option2));
                                        envoyerData(socket_,data,sizeof(data));

                                        bzero(data,sizeof(data));
                                        recevoirData(socket_,data);
                                        AnalyseReponse(data);
                                    }
                                }
                            }
                        }while(std::stoi(option) != 3);
                    }
                    else
                        std::cout << "Vous devez etre connecter pour faire cela!" << std::endl;

                break;
                
            case 4:
                    if(isLogged)
                    {
                        bzero(data,sizeof(data));
                        sprintf(data,"CADDIE#");
                        envoyerData(socket_,data,sizeof(data));

                        bzero(data,sizeof(data));
                        recevoirData(socket_,data);
                        AnalyseReponse(data);
                    }
                    else
                        std::cout << "Vous devez etre connecter pour faire cela!" << std::endl;
                break;
                
            case 5:
                    if(isLogged)
                    {
                        do
                        {
                            std::cout << "[0] Retirer un article" << std::endl;
                            std::cout << "[1] Annuler " << std::endl;
                            std::cout << "=======================" << std::endl;
                            std::cout << "Reponse: ";
                            std::cin >> option;
                            
                        }while(std::stoi(option) != 0 && std::stoi(option) != 1);

                        if(std::stoi(option) == 0)
                        {
                            std::cout << "Identifiant: ";
                            std::cin >> option;

                            bzero(data,sizeof(data));
                            sprintf(data,"CANCEL#%d#",std::stoi(option));
                            envoyerData(socket_,data,sizeof(data));

                            bzero(data,sizeof(data));
                            recevoirData(socket_,data);
                            AnalyseReponse(data);
                        }
                    }
                    else
                        std::cout << "Vous devez etre connecter pour faire cela!" << std::endl;
                break;

            case 6:
                    if(isLogged)
                    {
                        do
                        {
                            std::cout << "[0] Confirmer" << std::endl;
                            std::cout << "[1] Annuler " << std::endl;
                            std::cout << "=======================" << std::endl;
                            std::cout << "Reponse: ";
                            std::cin >> option;
                            
                        }while(std::stoi(option) != 0 && std::stoi(option) != 1);

                        if(std::stoi(option) == 0)
                        {
                            bzero(data,sizeof(data));
                            sprintf(data,"CANCEL_ALL#");
                            envoyerData(socket_,data,sizeof(data));

                            bzero(data,sizeof(data));
                            recevoirData(socket_,data);
                            AnalyseReponse(data);
                        }
                    }
                    else
                        std::cout << "Vous devez etre connecter pour faire cela!" << std::endl;
                break;
                
            case 7:
                    if(isLogged)
                    {
                        bzero(data,sizeof(data));
                        sprintf(data,"CONFIRMER#");
                        envoyerData(socket_,data,sizeof(data));

                        bzero(data,sizeof(data));
                        recevoirData(socket_,data);
                        AnalyseReponse(data);
                    }
                    else
                        std::cout << "Vous devez etre connecter pour faire cela!" << std::endl;
                break;

            case 8:
                    if(isLogged)
                    {
                        bzero(data,sizeof(data));
                        sprintf(data,"LOGOUT#");
                        envoyerData(socket_,data,sizeof(data));

                        /*bzero(data,sizeof(data));
                        recevoirData(socket_,data);
                        AnalyseReponse(data);*/
                        isLogged = false;
                        std::cout << "Vous etes deconnecte!" << std::endl;
                    }
                    else
                        std::cout << "Vous devez etre connecter pour vous deconnecter!" << std::endl;
                break;
                
            case 9:
                break;
            
            default:
                std::cout << "Valeur non valide!" << std::endl;
                break;
        }
    }
    std::cout << "Fermeture de la socket" << std::endl;
    close(socket_); 
}


void HandlerSIGINT(int)
{
    std::cout << "Fermeture de la socket" << std::endl;
    close(socket_); 
    exit(0);
}


void AnalyseReponse(char* reponse)
{
    char* ptr = strtok(reponse,"#");

    if(strcmp(ptr,"LOGIN") == 0)
    {
        std::cout << "[client] Reponse LOGIN reçue ..."<< std::endl;  
        if(strcmp("OK",strtok(NULL,"#")) == 0)
        {
            idClient = atoi(strtok(NULL,"#"));
            std::cout << "[client] Connecte - id: " << idClient << std::endl;
            isLogged = true;
        }
        else
        {
            std::cout << "\033[91m" << strtok(NULL,"#") << "\033[0m" << std::endl;
        }     
    }
    else if(strcmp(ptr,"CONSULT") == 0)
    {
        std::cout << "[client] Reponse CONSULT reçue ..."<< std::endl;

        int idArticle = atoi(strtok(NULL,"#"));
        if(idArticle != 0)
        {
            articleCourrant.id = idArticle;
            strcpy(articleCourrant.intitule,strtok(NULL,"#"));
            articleCourrant.prix = atof(strtok(NULL,"#"));
            articleCourrant.stock = atoi(strtok(NULL,"#"));
            strcpy(articleCourrant.image,strtok(NULL,"#"));
        }
        /*std::cout << "--- ARTICLE COURRANT ---"<< std::endl;
        std::cout << "- id      :" << articleCourrant.id << std::endl;
        std::cout << "- intitule:" << articleCourrant.intitule << std::endl;
        std::cout << "- prix    :" << articleCourrant.prix << std::endl;
        std::cout << "- stock   :" << articleCourrant.stock << std::endl;
        std::cout << "- image   :" << articleCourrant.image << std::endl;*/
    }
    else if(strcmp(ptr,"ACHAT") == 0)
    {
        std::cout << "[client] Reponse ACHAT reçue ..."<< std::endl;

        int idArticle = atoi(strtok(NULL,"#"));
        int quantite;
        float prix;
        if(idArticle != -1 && idArticle != 0)
        {
            quantite = atoi(strtok(NULL,"#"));
            prix = atof(strtok(NULL,"#"));

            std::cout << "---\033[92m ARTICLE ACHETE \033[0m---"<< std::endl;
            std::cout << "- id         :\033[92m" << idArticle << "\033[0m" << std::endl;
            std::cout << "- prix       :\033[92m" << prix << "\033[0m" << std::endl;
            std::cout << "- quantite   :\033[92m" << quantite << "\033[0m" << std::endl;
        }
        else 
            if(idArticle == 0)
                std::cout << "\033[91mQuantite insuffisante!\033[0m" << std::endl;
            else
                std::cout << "\033[91mCaddie plein!\033[0m" << std::endl;
    }
    else if(strcmp(ptr,"CADDIE") == 0)
    {
        std::cout << "[client] Reponse CADDIE reçue ..."<< std::endl;
        std::cout << "--- AFFICHAGE PANIER ---" << std::endl;
        
        while((ptr = strtok(NULL,"#")) != NULL)
        {
            std::cout << "-id      :" << ptr << std::endl;
            std::cout << "-intitule:" << strtok(NULL,"#") << std::endl;
            std::cout << "-prix    :" << atof(strtok(NULL,"#")) << std::endl;
            std::cout << "-quantite:" << atoi(strtok(NULL,"#")) << std::endl;
            std::cout << "-----------------------" << std::endl;
        }
        std::cout << "          Fin!        " << std::endl;
    }
    else if(strcmp(ptr,"CANCEL") == 0)
    {
        std::cout << "[client] Reponse CANCEL reçue ..."<< std::endl;

        if(strcmp(strtok(NULL,"#"),"OK") == 0)
        {
            std::cout << "\033[92mElement bien supprime du panier!\033[0m" << std::endl;
        }
        std::cout << "\033[91mL'Element n'as pas ete supprime!\033[0m" << std::endl;
    }
    else if(strcmp(ptr,"CANCEL_ALL") == 0)
    {
        std::cout << "[client] Reponse CANCEL_ALL reçue ..."<< std::endl;
    }
    else if(strcmp(ptr,"CONFIRMER") == 0)
    {
        std::cout << "[client] Reponse CONFIRMER reçue ..."<< std::endl;

        if(atoi(strtok(NULL,"#")) != -1)
            std::cout << "\033[92m[client] Confirmation de la commande bien validee ...\033[0m"<< std::endl;
        else 
            std::cout << "\033[91m[client] Erreur lors de la confirmation ...\033[0m"<< std::endl;
    }
    std::cout << std::endl;
}