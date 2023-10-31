#include "windowclient.h"
#include "ui_windowclient.h"
#include <QMessageBox>
#include <cstring>
#include <iostream>
#include <unistd.h>
#include <signal.h>
#include <cstdio>
#include "../lib/mynet.h"

using namespace std;

extern WindowClient *w;

//========================
extern int socket_;
int idClient;
float totalCaddie = 0.0;
ARTICLE articleCourrant = {1,"carottes",2.16,0,"carottes.jpg"};
//========================

#define REPERTOIRE_IMAGES "../ressources/images/"


void AnalyseReponse(char* reponse);
//void HandlerSIGINT(int);

void WindowClient::AnalyseConsult(char* reponse)
{
  if(strcmp(strtok(reponse,"#"),"CONSULT") == 0)
  {
    std::cout << "[client] Reponse CONSULT reçue ..."<< reponse  << std::endl;
    int idArticle = atoi(strtok(NULL,"#"));
    if(idArticle != 0)
    {
      articleCourrant.id = idArticle;
      strcpy(articleCourrant.intitule,strtok(NULL,"#"));
      articleCourrant.prix = atof(strtok(NULL,"#"));
      articleCourrant.stock = atoi(strtok(NULL,"#"));
      strcpy(articleCourrant.image,strtok(NULL,"#"));

      setArticle(articleCourrant.intitule,articleCourrant.prix,articleCourrant.stock,articleCourrant.image);
    }
  }
  
}

WindowClient::WindowClient(QWidget *parent) : QMainWindow(parent), ui(new Ui::WindowClient)
{
    ui->setupUi(this);

    // Configuration de la table du panier (ne pas modifer)
    ui->tableWidgetPanier->setColumnCount(3);
    ui->tableWidgetPanier->setRowCount(0);
    QStringList labelsTablePanier;
    labelsTablePanier << "Article" << "Prix à l'unité" << "Quantité";
    ui->tableWidgetPanier->setHorizontalHeaderLabels(labelsTablePanier);
    ui->tableWidgetPanier->setSelectionMode(QAbstractItemView::SingleSelection);
    ui->tableWidgetPanier->setSelectionBehavior(QAbstractItemView::SelectRows);
    ui->tableWidgetPanier->horizontalHeader()->setVisible(true);
    ui->tableWidgetPanier->horizontalHeader()->setDefaultSectionSize(160);
    ui->tableWidgetPanier->horizontalHeader()->setStretchLastSection(true);
    ui->tableWidgetPanier->verticalHeader()->setVisible(false);
    ui->tableWidgetPanier->horizontalHeader()->setStyleSheet("background-color: lightyellow");

    ui->pushButtonPayer->setText("Confirmer achat");
    setPublicite("!!! Bienvenue sur le Maraicher en ligne !!!");


    char data[TAILLE_MAX_DATA];

    bzero(data,sizeof(data));
    sprintf(data,"CONSULT#%d#",1);
    int i = envoyerData(socket_,data,sizeof(data));
    if(i < 0)
    {
      w->dialogueErreur("RIGHT CLICK","1SERVEUR DECONNECTE !");
      ::close(socket_);
      exit(0);
    }

    bzero(data,sizeof(data));
    i = recevoirData(socket_,data);
    if(i < 0 || i == 0)
    {
      w->dialogueErreur("RIGHT CLICK","SERVEUR DECONNECTE !");
      ::close(socket_);
      exit(0);
    }

    AnalyseConsult(data);

    // Exemples à supprimer
    //setArticle("carottes",2.16,18,"carottes.jpg");

    /*struct sigaction signal;
    sigemptyset(&signal.sa_mask);
    signal.sa_flags = 0;
    signal.sa_handler = HandlerSIGINT;
    sigaction(SIGINT,&signal,NULL);

    socket_ = creerSocket(0);

    int i = connecter(socket_,"localhost",50000);
    if(i == 0)
      std::cout << "Bien connecte" << std::endl;
    else 
      std::cout << "Non connecte" << std::endl;*/


    /*char address[NI_MAXHOST];
    char port[NI_MAXSERV];

    localhostInformation(socket_,address,port);
    std::cout << "Informations locale :" << std::endl;
    std::cout << "net-address : " + std::string(address) << std::endl;
    std::cout << "net-port : " + std::string(port) << std::endl << std::endl;

    externalhostInformation(socket_,address,port);
    std::cout << "Informations sur le serveur :" << std::endl;
    std::cout << "net-address : " + std::string(address) << std::endl;
    std::cout << "net-port : " + std::string(port) << std::endl << std::endl;*/
}

WindowClient::~WindowClient()
{
    delete ui;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles : ne pas modifier /////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setNom(const char* Text)
{
  if (strlen(Text) == 0 )
  {
    ui->lineEditNom->clear();
    return;
  }
  ui->lineEditNom->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
const char* WindowClient::getNom()
{
  strcpy(nom,ui->lineEditNom->text().toStdString().c_str());
  return nom;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setMotDePasse(const char* Text)
{
  if (strlen(Text) == 0 )
  {
    ui->lineEditMotDePasse->clear();
    return;
  }
  ui->lineEditMotDePasse->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
const char* WindowClient::getMotDePasse()
{
  strcpy(motDePasse,ui->lineEditMotDePasse->text().toStdString().c_str());
  return motDePasse;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setPublicite(const char* Text)
{
  if (strlen(Text) == 0 )
  {
    ui->lineEditPublicite->clear();
    return;
  }
  ui->lineEditPublicite->setText(Text);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setImage(const char* image)
{
  // Met à jour l'image
  char cheminComplet[80];
  sprintf(cheminComplet,"%s%s",REPERTOIRE_IMAGES,image);
  QLabel* label = new QLabel();
  label->setSizePolicy(QSizePolicy::Ignored, QSizePolicy::Ignored);
  label->setScaledContents(true);
  QPixmap *pixmap_img = new QPixmap(cheminComplet);
  label->setPixmap(*pixmap_img);
  label->resize(label->pixmap()->size());
  ui->scrollArea->setWidget(label);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::isNouveauClientChecked()
{
  if (ui->checkBoxNouveauClient->isChecked()) return 1;
  return 0;
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setArticle(const char* intitule,float prix,int stock,const char* image)
{
  ui->lineEditArticle->setText(intitule);
  if (prix >= 0.0)
  {
    char Prix[20];
    sprintf(Prix,"%.2f",prix);
    ui->lineEditPrixUnitaire->setText(Prix);
  }
  else ui->lineEditPrixUnitaire->clear();
  if (stock >= 0)
  {
    char Stock[20];
    sprintf(Stock,"%d",stock);
    ui->lineEditStock->setText(Stock);
  }
  else ui->lineEditStock->clear();
  setImage(image);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::getQuantite()
{
  return ui->spinBoxQuantite->value();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::setTotal(float total)
{
  if (total >= 0.0)
  {
    char Total[20];
    sprintf(Total,"%.2f",total);
    ui->lineEditTotal->setText(Total);
  }
  else ui->lineEditTotal->clear();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::loginOK()
{
  ui->pushButtonLogin->setEnabled(false);
  ui->pushButtonLogout->setEnabled(true);
  ui->lineEditNom->setReadOnly(true);
  ui->lineEditMotDePasse->setReadOnly(true);
  ui->checkBoxNouveauClient->setEnabled(false);

  ui->spinBoxQuantite->setEnabled(true);
  ui->pushButtonPrecedent->setEnabled(true);
  ui->pushButtonSuivant->setEnabled(true);
  ui->pushButtonAcheter->setEnabled(true);
  ui->pushButtonSupprimer->setEnabled(true);
  ui->pushButtonViderPanier->setEnabled(true);
  ui->pushButtonPayer->setEnabled(true);

  w->videTablePanier();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::logoutOK()
{
  ui->pushButtonLogin->setEnabled(true);
  ui->pushButtonLogout->setEnabled(false);
  ui->lineEditNom->setReadOnly(false);
  ui->lineEditMotDePasse->setReadOnly(false);
  ui->checkBoxNouveauClient->setEnabled(true);

  ui->spinBoxQuantite->setEnabled(false);
  ui->pushButtonPrecedent->setEnabled(false);
  ui->pushButtonSuivant->setEnabled(false);
  ui->pushButtonAcheter->setEnabled(false);
  ui->pushButtonSupprimer->setEnabled(false);
  ui->pushButtonViderPanier->setEnabled(false);
  ui->pushButtonPayer->setEnabled(false);

  setNom("");
  setMotDePasse("");
  ui->checkBoxNouveauClient->setCheckState(Qt::CheckState::Unchecked);

  setArticle("",-1.0,-1,"");

  w->videTablePanier();
  w->setTotal(-1.0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles Table du panier (ne pas modifier) /////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::ajouteArticleTablePanier(const char* article,float prix,int quantite)
{
    char Prix[20],Quantite[20];

    sprintf(Prix,"%.2f",prix);
    sprintf(Quantite,"%d",quantite);

    // Ajout possible
    int nbLignes = ui->tableWidgetPanier->rowCount();
    nbLignes++;
    ui->tableWidgetPanier->setRowCount(nbLignes);
    ui->tableWidgetPanier->setRowHeight(nbLignes-1,10);

    QTableWidgetItem *item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable|Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(article);
    ui->tableWidgetPanier->setItem(nbLignes-1,0,item);

    item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable|Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(Prix);
    ui->tableWidgetPanier->setItem(nbLignes-1,1,item);

    item = new QTableWidgetItem;
    item->setFlags(Qt::ItemIsSelectable|Qt::ItemIsEnabled);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(Quantite);
    ui->tableWidgetPanier->setItem(nbLignes-1,2,item);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::videTablePanier()
{
    ui->tableWidgetPanier->setRowCount(0);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WindowClient::getIndiceArticleSelectionne()
{
    QModelIndexList liste = ui->tableWidgetPanier->selectionModel()->selectedRows();
    if (liste.size() == 0) return -1;
    QModelIndex index = liste.at(0);
    int indice = index.row();
    return indice;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions permettant d'afficher des boites de dialogue (ne pas modifier ////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::dialogueMessage(const char* titre,const char* message)
{
   QMessageBox::information(this,titre,message);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::dialogueErreur(const char* titre,const char* message)
{
   QMessageBox::critical(this,titre,message);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////// CLIC SUR LA CROIX DE LA FENETRE /////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::closeEvent(QCloseEvent *event)
{
  char data[50] = "LOGOUT#";
  envoyerData(socket_,data,sizeof(data));
  
  ::close(socket_);
  std::cout << "[Client] Fin de la socket" << std::endl;
  exit(0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions clics sur les boutons ////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonLogin_clicked()
{
  char data[TAILLE_MAX_DATA];

  if(strcmp(getNom(),"") != 0 && strcmp(getMotDePasse(),"") != 0)
  {
    sprintf(data,"LOGIN#%s#%s#%d#",getNom(),getMotDePasse(),isNouveauClientChecked());

    int i = envoyerData(socket_,data,sizeof(data));
    if(i < 0)
    {
      w->dialogueErreur("LOGIN","SERVEUR DECONNECTE !");
      ::close(socket_);
      exit(0);
    }
    std::cout << "donnee envoye : " << std::string(data) << std::endl;

    bzero(data,sizeof(data));
    i = recevoirData(socket_,data);
    if(i < 0  || i == 0)
    {
      w->dialogueErreur("LOGIN","SERVEUR DECONNECTE !");
      ::close(socket_);
      exit(0);
    }
    //std::cout << "retour de la requete : " << data << std::endl;
    AnalyseReponse(data);
  }
  else
    w->dialogueErreur("LOGIN","Champs manquant !");  
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonLogout_clicked()
{  
  std::cout << "[Client] Envoie requete LOGOUT ..." << std::endl;

  char data[50] = "LOGOUT#";
  int i = envoyerData(socket_,data,sizeof(data));
  if(i < 0)
  {
    w->dialogueErreur("LOGOUT","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }

  idClient = 0;
  totalCaddie = 0.0f;
  w->logoutOK();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSuivant_clicked()
{
  char data[TAILLE_MAX_DATA];

  bzero(data,sizeof(data));
  sprintf(data,"CONSULT#%d#",articleCourrant.id+1);
  int i = envoyerData(socket_,data,sizeof(data));
  if(i < 0)
  {
    w->dialogueErreur("RIGHT CLICK","1SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }


  bzero(data,sizeof(data));
  i = recevoirData(socket_,data);
  if(i < 0 || i == 0)
  {
    w->dialogueErreur("RIGHT CLICK","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }
  
  //std::cout << "retour de la requete : " << data << std::endl;
  AnalyseReponse(data);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPrecedent_clicked()
{
  char data[TAILLE_MAX_DATA];

  bzero(data,sizeof(data));
  sprintf(data,"CONSULT#%d#",articleCourrant.id-1);
  int i = envoyerData(socket_,data,sizeof(data));
  if(i < 0)
  {
    w->dialogueErreur("LEFT CLICK","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }

  bzero(data,sizeof(data));
  i = recevoirData(socket_,data);
  if(i < 0 || i == 0)
  {
    w->dialogueErreur("LEFT CLICK","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }
  
  //std::cout << "retour de la requete : " << data << std::endl;
  AnalyseReponse(data);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonAcheter_clicked()
{
  char data[TAILLE_MAX_DATA];

  bzero(data,sizeof(data));
  sprintf(data,"ACHAT#%d#%d#",articleCourrant.id,getQuantite());
  int i = envoyerData(socket_,data,sizeof(data));
  if(i < 0)
  {
    w->dialogueErreur("ACHAT","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }

  bzero(data,sizeof(data));
  i = recevoirData(socket_,data);
  if(i < 0 || i == 0)
  {
    w->dialogueErreur("ACHAT","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }
  AnalyseReponse(data);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonSupprimer_clicked()
{
  char data[TAILLE_MAX_DATA];

  bzero(data,sizeof(data));
  sprintf(data,"CANCEL#%d#",w->getIndiceArticleSelectionne());
  int i = envoyerData(socket_,data,sizeof(data));
  if(i < 0)
  {
    w->dialogueErreur("SUPRIMMER","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }

  bzero(data,sizeof(data));
  i = recevoirData(socket_,data);
  if(i < 0 || i == 0)
  {
    w->dialogueErreur("SUPRIMMER","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }
  AnalyseReponse(data);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonViderPanier_clicked()
{
  char data[TAILLE_MAX_DATA]; 

  bzero(data,sizeof(data));
  sprintf(data,"CANCEL_ALL#");
  int i = envoyerData(socket_,data,sizeof(data));
  if(i < 0)
  {
    w->dialogueErreur("VIDER PANIER","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }

  bzero(data,sizeof(data));
  i = recevoirData(socket_,data);
  if(i < 0 || i == 0)
  {
    w->dialogueErreur("VIDER PANIER","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }
  AnalyseReponse(data);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WindowClient::on_pushButtonPayer_clicked()
{
  char data[TAILLE_MAX_DATA];
  
  bzero(data,sizeof(data));
  sprintf(data,"CONFIRMER#");
  int i = envoyerData(socket_,data,sizeof(data));
  if(i < 0)
  {
    w->dialogueErreur("PAYER","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }

  bzero(data,sizeof(data));
  i = recevoirData(socket_,data);
  if(i < 0 || i == 0)
  {
    w->dialogueErreur("PAYER","SERVEUR DECONNECTE !");
    ::close(socket_);
    exit(0);
  }
  AnalyseReponse(data);
}

/*
void HandlerSIGINT(int)
{  
  const char data[] = "LOGOUT#";
  envoyerData(socket_,data,sizeof(data));

  ::close(socket_);
  std::cout << "Fin de la socket" << std::endl;
  exit(0);
}*/

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

          char msg[30];
          sprintf(msg,"Utilisateur : %d",idClient);
          w->dialogueMessage("Connexion",msg);
          w->loginOK();
        }
        else
        {
          //std::cout << "\033[91m" << strtok(NULL,"#") << "\033[0m" << std::endl;
          w->dialogueErreur("Connexion",strtok(NULL,"#"));
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

          w->setArticle(articleCourrant.intitule,articleCourrant.prix,articleCourrant.stock,articleCourrant.image);
        }
    }
    else if(strcmp(ptr,"ACHAT") == 0)
    {
        std::cout << "[client] Reponse ACHAT reçue ..."<< std::endl;

        int idArticle = atoi(strtok(NULL,"#"));
        if(idArticle != -1 && idArticle != 0)
        {
          if(w->getQuantite() != 0)
          {
            w->dialogueMessage("ACHAT","Achat reussi!");
            totalCaddie = 0.0f;

            char data[TAILLE_MAX_DATA];

            bzero(data,sizeof(data));
            sprintf(data,"CADDIE#");
            envoyerData(socket_,data,sizeof(data));

            bzero(data,sizeof(data));
            recevoirData(socket_,data);
            AnalyseReponse(data);
          }
          else
            w->dialogueErreur("ACHAT","Quantite = 0!");
        }
        else 
        {
          if(idArticle == 0)
          {
            w->dialogueErreur("ACHAT","Quantite insufisante!");
          }
          else
          {
            std::cout << "\033[91mCaddie plein!\033[0m" << std::endl;
            w->dialogueErreur("ACHAT","Caddie plein!");
          }
        }
    }
    else if(strcmp(ptr,"CADDIE") == 0)
    {
        std::cout << "[client] Reponse CADDIE reçue ..."<< std::endl;
        w->videTablePanier();

        //std::cout << "--- AFFICHAGE PANIER ---" << std::endl;
        char intitule_[20];
        float prix_;
        int quantite_;      
        totalCaddie = 0;

        while((ptr = strtok(NULL,"#")) != NULL)
        {
          strcpy(intitule_,strtok(NULL,"#"));
          prix_ = atof(strtok(NULL,"#"));
          quantite_ = atoi(strtok(NULL,"#"));
          w->ajouteArticleTablePanier(intitule_,prix_,quantite_);
          totalCaddie += quantite_ * prix_;
          w->setTotal(totalCaddie);
        }
        //std::cout << "          Fin!        " << std::endl;
    }
    else if(strcmp(ptr,"CANCEL") == 0)
    {
        std::cout << "[client] Reponse CANCEL reçue ..."<< std::endl;

        if(strcmp(strtok(NULL,"#"),"OK") == 0)
        {
          std::cout << "\033[92mElement bien supprime du panier!\033[0m" << std::endl;
          w->dialogueMessage("CANCEL","Element bien supprime!");

          char data[TAILLE_MAX_DATA];

          bzero(data,sizeof(data));
          sprintf(data,"CADDIE#");
          envoyerData(socket_,data,sizeof(data));

          bzero(data,sizeof(data));
          recevoirData(socket_,data);
          AnalyseReponse(data);
        }
        else
          std::cout << "\033[91mL'Element n'as pas ete supprime!\033[0m" << std::endl;
    }
        else if(strcmp(ptr,"CANCEL_ALL") == 0)
    {
      std::cout << "[client] Reponse CANCEL_ALL reçue ..."<< std::endl;

      w->videTablePanier();
      totalCaddie = 0.0f;
      w->setTotal(totalCaddie);
    }
    else if(strcmp(ptr,"CONFIRMER") == 0)
    {
        std::cout << "[client] Reponse CONFIRMER reçue ..."<< std::endl;

        w->videTablePanier();
        totalCaddie = 0.0f;
        w->setTotal(totalCaddie);

        if(atoi(strtok(NULL,"#")) != -1)
        {
          std::cout << "\033[92m[client] Confirmation de la commande bien validee ...\033[0m"<< std::endl;
          w->dialogueMessage("CONFIRMATION","Confirmation de la commande bien validee!");
        }
        else
        {
          std::cout << "\033[91m[client] Erreur lors de la confirmation ...\033[0m"<< std::endl;
          w->dialogueMessage("CONFIRMATION","Erreur lors de la confirmation!");
        }            
    }
    std::cout << std::endl;
}