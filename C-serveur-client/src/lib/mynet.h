#ifndef __mynet_h__
#define __mynet_h__
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

#define IPV4 AF_INET
#define IPV6 AF_INET6
#define TCP SOCK_STREAM
#define UDP SOCK_DGRAM
#define TAILLE_MAX_DATA 1000

/***********************************************
 * Declaration des structures de données
 ***********************************************
 * ARTICLE => structure definissant un article.
 * ARTICLE_T => resume d'un objet ARTICLE.
 * CADDIE => Ensemble des articles selectionnés
 *           par le client.
 ***********************************************/
#define TAILLE_PANIER 20

typedef struct
{
  int   id;
  char  intitule[20];
  float prix;
  int   stock;  
  char  image[20];
} ARTICLE;

typedef struct
{
  int   id;
  char  intitule[20];
  float prix;
  int   quantite;
} ARTICLE_T;

typedef struct 
{ 
  int idClient;
  ARTICLE_T articlesPanier[TAILLE_PANIER];

} CADDIE_T;
/***********************************************/

/**************************
    int ServerSocket(int port);
    int Accept(int sEcoute,char *ipClient);
    int ClientSocket(char* ipServeur,int portServeur);
    int Send(int sSocket,char* data,int taille);
    int Receive(int sSocket,char* data);
 *************************/

int creerSocket(int port);
void activerListen(int _socket,int queue_size);
int accepterConnexion(int _socket);
void localhostInformation(int _socket,char* address, char* port);
void externalhostInformation(int _socket,char* address, char* port);
int connecter(int _socket,const char* address, int port);
int envoyerData(int _socket,const char* data,const int taille);
int recevoirData(int _socket,char* data);

void FloatFormater(char* valeur);

#endif