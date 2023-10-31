#ifndef __PROTO_HPP__
#define __PROTO_HPP__
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <iostream>
#include <mysql/mysql.h>
#include "mynet.h"

/**********************************************
 * Declaration des MACRO
 *********************************************/
#define LOGIN       1
#define CONSULT     2
#define ACHAT       3
#define CADDIE      4
#define CANCEL      5
#define CANCEL_ALL  6
#define CONFIRMER   7
#define LOGOUT      8

//Variable de serveur
extern MYSQL* connexion;

MYSQL_RES  *resultat;
MYSQL_ROW  Tuple;

/***********************************************
 * Ajout de fonctions liées aux messages
 **********************************************/
void CreerMessageLOGIN(char* reponse, bool estReussi, int idClient);
void CreerMessageCONSULT(char* reponse, int idArticle,const char* intitule,float prix,int stock,const char* image);
void CreerMessageACHAT(char* reponse, int idArticle, int quantite,float prix);
void CreerMessageCADDIE(char* reponse,CADDIE_T& panier);
void CreerMessageCANCEL(char* reponse,bool estReussi);
void CreerMessageCANCEL_ALL(char* reponse);
void CreerMessageCONFIRMER(char* reponse,int idFacture);

/***************************************************************
 * idClient > 0  : lorsque le client est trouvé
 *              -- connexion -- 
 *          = -1 : lorsque le client n'existe pas
 *          = -2 : Lorsque le client existe mais mauvais mdp 
 *              -- nouveau compte --  
 *          = -3 : lorsque le client existe deja dans la bd
 **************************************************************/
void CreerMessageLOGIN(char* reponse, bool estReussi, int idClient)
{
    bzero(reponse,sizeof(reponse));

    if(estReussi)
        sprintf(reponse,"LOGIN#OK#%d",idClient);
    else
    {
        if(idClient == -1)
            sprintf(reponse,"LOGIN#KO#Le client n'existe pas#");
        else if(idClient == -2)
            sprintf(reponse,"LOGIN#KO#Le mot de passe est incorrect#");
        else
            sprintf(reponse,"LOGIN#KO#Le client existe deja#");
    }
}

/****************************************
 * idArticle = -1 si article non-trouve
 ***************************************/
void CreerMessageCONSULT(char* reponse, int idArticle,const char* intitule,float prix,int stock,const char* image)
{
    bzero(reponse,sizeof(reponse));

    if(idArticle == -1)
        sprintf(reponse,"CONSULT#%d#",idArticle);
    else
        sprintf(reponse,"CONSULT#%d#%s#%f#%d#%s#",idArticle,intitule,prix,stock,image);
}

/*****************************************************
 * idArticle ou -1 si : article non trouvé
 * quantite ou 0 si   : le stock n'est pas suffisant
 ****************************************************/
void CreerMessageACHAT(char* reponse, int idArticle, int quantite,float prix)
{
    bzero(reponse,sizeof(reponse));

    if(idArticle == -1)
        sprintf(reponse,"ACHAT#%d#",idArticle);
    else
        sprintf(reponse,"ACHAT#%d#%d#%f#",idArticle,quantite,prix);
}

/******************************************
 * Elements du caddie/panier dans le message
 *****************************************/
void CreerMessageCADDIE(char* reponse,CADDIE_T& panier)
{
    bzero(reponse,sizeof(reponse));
    sprintf(reponse,"CADDIE#");

    for(const ARTICLE_T& a : panier.articlesPanier)
    {
        char article_courrant[100] = {0};
        if(a.id != 0)
        {
            sprintf(article_courrant,"%d#%s#%f#%d#",a.id,a.intitule,a.prix,a.quantite);
            strcat(reponse,article_courrant);
            //std::cout << reponse << std::endl;
        }
    }            
}

/*********************************************
 * Operation reussie alors : Oui,sinon : NON
 ********************************************/
void CreerMessageCANCEL(char* reponse,bool estReussi)
{
    bzero(reponse,sizeof(reponse));

    if(estReussi)
        sprintf(reponse,"CANCEL#OK#");
    else
        sprintf(reponse,"CANCEL#KO#");
}

void CreerMessageCANCEL_ALL(char* reponse)
{
    bzero(reponse,sizeof(reponse));
    sprintf(reponse,"CANCEL_ALL#");
}

void CreerMessageCONFIRMER(char* reponse,int idFacture)
{
    bzero(reponse,sizeof(reponse));
    sprintf(reponse,"CONFIRMER#%d#",idFacture);
}


//------------------------------------------------------------------------------------

/******************************************************
 * Methodes d'analyse des requetes
 *****************************************************/
int Login(char* Requete);
ARTICLE Consulter(char* Requete);
ARTICLE_T Acheter(char* Requete);
int Cancel(CADDIE_T& panier,char* Requete);
void Logout(CADDIE_T& panier);
int Confirmer(CADDIE_T& panier);

int getIndiceArticle(CADDIE_T& panier, int idArticle);
int getIdArticle(CADDIE_T& panier, int indice);
void NettoyerIndiceArticle(CADDIE_T& panier, int indice);
int InsererArticle(CADDIE_T& panier, ARTICLE_T article);
int AjouterPanier(CADDIE_T& panier, ARTICLE_T article);
void ViderPanier(CADDIE_T& panier);
void NettoyerPanier(CADDIE_T& panier);
float CalculerMontantPanier(CADDIE_T& panier);

void AnalyserRequete(CADDIE_T& panier,char* Requete,char* Reponse);

/***************************************************************
 * idClient > 0  : lorsque le client est trouvé
 *              -- connexion -- 
 *          = -1 : lorsque le client n'existe pas
 *          = -2 : Lorsque le client existe mais mauvais mdp 
 *              -- nouveau compte --  
 *          = -3 : lorsque le client existe deja dans la bd
 **************************************************************/
int Login(char* Requete)
{
    std::cout << "[thread " << pthread_self() << "] Connexion a la base de donnees ..."<< std::endl;

    int r_value = 0;

    //Etape 1 recuperer les informations login, password et estNouveauClient
    char login[50], password[50];bool estNouveau;

    strcpy(login,strtok(NULL,"#"));
    strcpy(password,strtok(NULL,"#"));
    estNouveau = atoi(strtok(NULL,"#"));

    char SQL_REQUEST[100];
    bzero(SQL_REQUEST,sizeof(SQL_REQUEST));

    sprintf(SQL_REQUEST, "SELECT * FROM clients WHERE login LIKE '%s';", login);
    if (mysql_query(connexion, SQL_REQUEST) == 0)
    {
        resultat = mysql_store_result(connexion);
        if (resultat != NULL)
        {
            if((Tuple = mysql_fetch_row(resultat)) != NULL)
            {
                if(!estNouveau)
                {
                    //comparer le mot de passe
                    // == 0 alors le mdp est juste
                    if(strcmp(password,Tuple[2]) == 0)
                    {
                        //panier.idClient = atoi(Tuple[0]);
                        //r_value = panier.idClient;
                        r_value = atoi(Tuple[0]);
                    }
                    else
                        r_value = -2;
                }
                else
                {
                    //Le client existe donc retour -3
                    r_value = -3;
                }
            }
            else
            {
                if(!estNouveau)
                {
                    r_value = -1;
                }
                else
                {
                    // si le client n'existe pas alors on l'encode
                    sprintf(SQL_REQUEST,"insert into clients values(NULL, '%s', '%s')",login,password);
                    if (mysql_query(connexion, SQL_REQUEST) != 0)
                    {
                        perror("(BD) Erreur lors de l'insertion du nouveau client ");
                        exit(1);
                    }

                    // Récupérer l'identifiant du client ajouté
                    //panier.idClient = mysql_insert_id(connexion);
                    r_value = mysql_insert_id(connexion);
                }
            }
            mysql_free_result(resultat);
        }
    }        
    return r_value;
}

/*****************************************
 * Retourne les informations d'un article
 ****************************************/
ARTICLE Consulter(char* Requete)
{
    std::cout << "[thread " << pthread_self() << "] Connexion a la base de donnees ..."<< std::endl;

    ARTICLE a = {0};

    int id = atoi(strtok(NULL,"#"));

    char SQL_REQUEST[100];
    bzero(SQL_REQUEST,sizeof(SQL_REQUEST));

    sprintf(SQL_REQUEST, "SELECT * FROM articles WHERE id = %d;", id);
    if (mysql_query(connexion, SQL_REQUEST) == 0)
    {
        resultat = mysql_store_result(connexion);
        if(resultat != NULL)
        {
            if((Tuple = mysql_fetch_row(resultat)) != NULL)
            {
                a.id = atoi(Tuple[0]);
                strcpy(a.intitule,Tuple[1]);
                a.prix = atof(Tuple[2]);
                a.stock = atoi(Tuple[3]);
                strcpy(a.image,Tuple[4]);
            }
            mysql_free_result(resultat);
        }
        else
        {
            a.id = -1;
            strcpy(a.intitule,"xxx");
            a.prix = 0.0;
            a.stock = 0;
            strcpy(a.image,"xxx");
        }
    }
    return a;
}

ARTICLE_T Acheter(char* Requete)
{
    std::cout << "[thread " << pthread_self() << "] Connexion a la base de donnees ..."<< std::endl;

    ARTICLE_T a = {0};

    int idArticle = atoi(strtok(NULL,"#"));
    int quantite = atoi(strtok(NULL,"#"));

    char SQL_REQUEST[100];
    bzero(SQL_REQUEST,sizeof(SQL_REQUEST));

    sprintf(SQL_REQUEST, "SELECT * FROM articles WHERE id = %d;",idArticle);
    if (mysql_query(connexion, SQL_REQUEST) == 0)
    {
        resultat = mysql_store_result(connexion);
        if(resultat != NULL)
        {
            if((Tuple = mysql_fetch_row(resultat)) != NULL)
            {
                if(atoi(Tuple[3]) >= quantite && quantite > 0)
                {
                    bzero(SQL_REQUEST,sizeof(SQL_REQUEST));
                    sprintf(SQL_REQUEST,"UPDATE articles SET stock = stock - %d " 
                                        "WHERE id = %d;",quantite,idArticle);
                    if(mysql_query(connexion, SQL_REQUEST) == 0)
                    {
                        a.id = idArticle;
                        strcpy(a.intitule,Tuple[1]);
                        a.prix = atof(Tuple[2]);
                        a.quantite = quantite;
                    }
                }
            }
            mysql_free_result(resultat);
        }
        else
        {
            a.id = -1;
            strcpy(a.intitule,"xxx");
            a.prix = 0.0;
            a.quantite = 0;
        }
    }
    return a;
}

/******************************************************
 * Retourne -1 si l'article n'est pas dans le panier
 * retourne 0 si l'element à ete supprimer de la liste
 ******************************************************/
int Cancel(CADDIE_T& panier,char* Requete)
{
    int indice = atoi(strtok(NULL,"#"));
    int id = getIdArticle(panier,indice);
    std::cout << indice << std::endl;
    if(indice != -1)
    {
        int quantite = panier.articlesPanier[indice].quantite;

        char SQL_REQUEST[100];
        bzero(SQL_REQUEST,sizeof(SQL_REQUEST));
        sprintf(SQL_REQUEST,"UPDATE articles SET stock = stock + %d " 
                            "WHERE id = %d;",quantite,id);
        if (mysql_query(connexion, SQL_REQUEST) == 0)
        {
            //panier.articlesPanier[indice] = {0};
            NettoyerIndiceArticle(panier,indice);
            std::cout << "[Panier] Element bien retire!" << std::endl;
            return 0;
        }
    }
    std::cout << "[Panier] L'element n'a pas ete retire!" << std::endl;
    return -1;
}

/*****************************
 * Effacer les informations
 * du client actuel
 ****************************/
void Logout(CADDIE_T& panier)
{
    panier = {0};
}

/******************************
 * Ajouter une facture a la bd
 * Ajouter les ventes a la bd
 * Return = id de la facture
 * return = -1 si une erreur
 *****************************/
int Confirmer(CADDIE_T& panier)
{
    int idFacture;
    float montant = CalculerMontantPanier(panier);    

    char SQL_REQUEST[100];
    bzero(SQL_REQUEST,sizeof(SQL_REQUEST));

    sprintf(SQL_REQUEST,"INSERT INTO factures VALUES(NULL,%d,NOW(),%f,%d)",panier.idClient,montant,0);
    if (mysql_query(connexion, SQL_REQUEST) == 0)
    {   
        //std::cout << "FACTURE Insertion reussie !" << std::endl;

        idFacture = mysql_insert_id(connexion);
        for(const ARTICLE_T& a : panier.articlesPanier)
        {
            if(a.id != 0)
            {
                sprintf(SQL_REQUEST,"INSERT INTO ventes VALUES(%d,%d,%d)",idFacture,a.id,a.quantite);
                if (mysql_query(connexion, SQL_REQUEST) != 0)
                {   
                    perror("\033[91mErreur impossible d'encoder la ventes\033[0m");
                }
                /*else
                    std::cout << "VENTES Insertion reussie !" << std::endl;*/
            }
        }
        return idFacture;
    }
    //std::cerr << "(BD) Numéro d'erreur : " << mysql_errno(connexion) << std::endl;
    //std::cout << "\033[91mFACTURE Insertion ECHEC !\033[0m" << std::endl;
    return -1;
}
/******************************************************
 * Fonction retourne l'indice de l'article
 * s'il est dans la liste ou -1 
 *****************************************************/
int getIndiceArticle(CADDIE_T& panier, int idArticle)
{
    int i = 0;
    for(const ARTICLE_T& a : panier.articlesPanier)
    {
        if(a.id == idArticle)
            return i;
        i++;
    }
    return -1;
}
/******************************************************
 * Fonction retourne l'id de l'article
 * s'il est dans la liste ou -1 
 *****************************************************/
int getIdArticle(CADDIE_T& panier, int indice)
{
    return panier.articlesPanier[indice].id;
}

/******************************************************
 * Mets à jour l'indice des elements dans le caddie
 *****************************************************/
void NettoyerIndiceArticle(CADDIE_T& panier, int indice)
{
    for(int i = indice; i < TAILLE_PANIER; i++)
    {
        panier.articlesPanier[i] = panier.articlesPanier[i+1];
    }
    panier.articlesPanier[TAILLE_PANIER] = {0};
}

int InsererArticle(CADDIE_T& panier, ARTICLE_T article)
{
    for(ARTICLE_T& a : panier.articlesPanier)
    {
        if(a.id == 0)
        {
            a.id = article.id;
            strcpy(a.intitule,article.intitule);
            a.quantite += article.quantite;
            a.prix = article.prix;
            return 0;
        }
    }
    return -1;
}

/******************************************************
 * Fonction permettant d'enregistrer un article
 * dans le caddie
 * bien inserer  =  0
 * plus de place = -1
 *****************************************************/
int AjouterPanier(CADDIE_T& panier, ARTICLE_T article)
{
    int indice = getIndiceArticle(panier,article.id);
    if(indice != -1)
    {
        panier.articlesPanier[indice].quantite += article.quantite;
        std::cout << "[Panier] " << panier.articlesPanier[indice].intitule 
        << " - quantite: " << panier.articlesPanier[indice].quantite << std::endl;
        return 0;
    }
    else
    {
        if(InsererArticle(panier,article) == 0) 
        {
            std::cout <<"[Panier] Ajout de " << panier.articlesPanier[indice].intitule 
            << " - quantite: " << panier.articlesPanier[indice].quantite << std::endl;
            return 0;
        }
    }
    std::cout << "[Panier] Pas assez de place!" << std::endl;
    return -1;
}

/****************************************
 * Vider le contenu du panier dans la BD
 ***************************************/
void ViderPanier(CADDIE_T& panier)
{
    for(ARTICLE_T& a : panier.articlesPanier)
    {
        if(a.id != 0)
        {
            char SQL_REQUEST[100];
            bzero(SQL_REQUEST,sizeof(SQL_REQUEST));
            sprintf(SQL_REQUEST,"UPDATE articles SET stock = stock + %d " 
                                "WHERE id = %d;",a.quantite,a.id);
            if (mysql_query(connexion, SQL_REQUEST) == 0)             
                std::cout << "[Panier] Element retire du panier!" << std::endl;            
        }
        a = {0};
    }
    std::cout << "[Panier] Panier nettoye!" << std::endl;
}
/****************************************
 * Vider le contenu du panier
 ***************************************/
void NettoyerPanier(CADDIE_T& panier)
{
    for(ARTICLE_T& a : panier.articlesPanier)
    {        
        a = {0};
    }
    std::cout << "[Panier] Panier nettoye!" << std::endl;
}

/***************************************
 * Retourne le montant totale du panier
 ***************************************/
float CalculerMontantPanier(CADDIE_T& panier)
{
    float montant = 0.0f;
    for(ARTICLE_T& a : panier.articlesPanier)
    {
        montant += (float)a.prix * (float)a.quantite;
    }
    return montant;
}

//----------------------------------------------------------------------------------------------------
void AnalyserRequete(CADDIE_T& panier,char* Requete,char* Reponse)
{
    std::cout << "[thread " << pthread_self() << "] Analyse de la requete ..."<< std::endl;
    char *ptr = strtok(Requete,"#");

    if(strcmp(ptr,"LOGIN") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete LOGIN reçue ..."<< std::endl;

        int retour = Login(Requete);
        switch(retour)
        {
            case -1:
            case -2:
            case -3:
                CreerMessageLOGIN(Reponse,false,retour);
                break;

            default:
                panier.idClient = retour;
                CreerMessageLOGIN(Reponse,true,panier.idClient);
                break;
        }
    }
    else if(strcmp(ptr,"CONSULT") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete CONSULT reçue ..."<< std::endl;

        ARTICLE a = Consulter(Requete);
        CreerMessageCONSULT(Reponse,a.id,a.intitule,a.prix,a.stock,a.image);
    }
    else if(strcmp(ptr,"ACHAT") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete ACHAT reçue ..."<< std::endl;

        ARTICLE_T a = Acheter(Requete);
        int insertion = AjouterPanier(panier,a);
        if(insertion == 0)
            CreerMessageACHAT(Reponse,a.id,a.quantite,a.prix);
        else 
        {
            CreerMessageACHAT(Reponse,-1,a.quantite,a.prix);
        }
    }
    else if(strcmp(ptr,"CADDIE") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete CADDIE reçue ..."<< std::endl;

        CreerMessageCADDIE(Reponse,panier);
    }
    else if(strcmp(ptr,"CANCEL") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete CANCEL reçue ..."<< std::endl;

        if(Cancel(panier,Requete) == 0)        
            CreerMessageCANCEL(Reponse,true);
        else        
            CreerMessageCANCEL(Reponse,false);     
    }
    else if(strcmp(ptr,"CANCEL_ALL") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete CANCEL_ALL reçue ..."<< std::endl;

        ViderPanier(panier);

        CreerMessageCANCEL_ALL(Reponse);
    }
    else if(strcmp(ptr,"CONFIRMER") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete CONFIRMER reçue ..."<< std::endl;

        int idFacture = Confirmer(panier);
        CreerMessageCONFIRMER(Reponse,idFacture);
        NettoyerPanier(panier);
    }
    else if(strcmp(ptr,"LOGOUT") == 0)
    {
        std::cout << "[thread " << pthread_self() << "] Requete LOGOUT reçue ..."<< std::endl;

        ViderPanier(panier);
        Logout(panier);
    }
    else
        std::cout << "[thread " << pthread_self() << "] Requete WTF reçue" <<  std::endl;
}

#endif