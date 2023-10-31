#include "windowclient.h"
#include <QApplication>
#include "../lib/mynet.h"
#include <time.h>
#include <pthread.h>
#include <signal.h>

void HandlerSIGINT(int);

int socket_;

WindowClient *w;

int main(int argc, char *argv[])
{
    struct sigaction signal;
    sigemptyset(&signal.sa_mask);
    signal.sa_flags = 0;
    signal.sa_handler = HandlerSIGINT;
    sigaction(SIGINT,&signal,NULL);

    socket_ = creerSocket(0);
    int i;
    do
    {
        std::cout << "[client] Tentative de connexion ..." << std::endl;
        i = connecter(socket_,argv[1],atoi(argv[2]));
        if(i == 0)
            std::cout << "Bien connecte" << std::endl;
        else
        {
            std::cout << "Non connecte" << std::endl;
            sleep(1);
        }
    } while (i != 0);    

    char address[NI_MAXHOST];
    char port[NI_MAXSERV];

    localhostInformation(socket_,address,port);
    std::cout << "Informations locale :" << std::endl;
    std::cout << "net-address : " + std::string(address) << std::endl;
    std::cout << "net-port : " + std::string(port) << std::endl << std::endl;

    externalhostInformation(socket_,address,port);
    std::cout << "Informations sur le serveur :" << std::endl;
    std::cout << "net-address : " + std::string(address) << std::endl;
    std::cout << "net-port : " + std::string(port) << std::endl << std::endl;

    QApplication a(argc, argv);
    w = new WindowClient();
    w->show();
    return a.exec();
}

void HandlerSIGINT(int)
{  
  const char data[] = "LOGOUT#";
  envoyerData(socket_,data,sizeof(data));

  ::close(socket_);
  std::cout << "Fin de la socket" << std::endl;
  exit(0);
}