#include "mynet.h"

int creerSocket(int port)
{
    int _socket;
    struct sockaddr_in info_serv;

    bzero(&info_serv,sizeof(info_serv));

    info_serv.sin_family = AF_INET;
    info_serv.sin_port = htons(port);
    info_serv.sin_addr.s_addr = INADDR_ANY;

    _socket = socket(IPV4,TCP,0);

    if(bind(_socket,(const struct sockaddr *)&info_serv,sizeof(struct sockaddr_in)))
    {
        perror("Bind");
        exit(0);
    }

    return _socket;
}

void localhostInformation(int _socket,char* address, char* port)
{
    socklen_t _size = sizeof(struct sockaddr_in);
    struct sockaddr_in socket_information;

    getsockname(_socket,(struct sockaddr*)&socket_information,&_size);

    if (getnameinfo((const struct sockaddr *)&socket_information, sizeof(struct sockaddr_in),address, NI_MAXHOST, port, NI_MAXSERV, NI_NUMERICHOST | NI_NUMERICSERV) != 0) 
    {
        perror("Erreur lors de la résolution du nom d'hôte et du port");
        exit(0);
    }
}

void externalhostInformation(int _socket,char* address, char* port)
{
    socklen_t _size = sizeof(struct sockaddr_in);
    struct sockaddr_in socket_information;

    if(getpeername(_socket,(struct sockaddr*)&socket_information,&_size))
    {
        perror("print informations");
        exit(0);
    }
    
    if (getnameinfo((const struct sockaddr *)&socket_information, _size, address, NI_MAXHOST, port, NI_MAXSERV, NI_NUMERICSERV | NI_NUMERICHOST) != 0) {
        perror("Erreur lors de la résolution du nom d'hôte et du port");
        exit(0);
    }
}

int accepterConnexion(int _socket)
{
    int socket_client;
    struct sockaddr_in client_information;
    socklen_t _size = sizeof(struct sockaddr_in);

    socket_client = accept(_socket,(struct sockaddr*)&client_information,&_size);

    if(socket_client == -1) return -1;

    return socket_client;
}

void activerListen(int _socket,int queue_size)
{
    if(listen(_socket,queue_size))
    {
        perror("Listen");
        exit(0);
    }
}

/************************************************************
 * Retourne -1 si cela n'as pas marche et 0 si ca a marche
 * address => ipv4 address of the distant host
 * port    => port of the distant host
 ***********************************************************/
int connecter(int _socket,const char* address, int port)
{
    struct sockaddr_in extern_information;
    socklen_t _size = sizeof(struct sockaddr_in);

    bzero(&extern_information,sizeof(extern_information));

    extern_information.sin_family = AF_INET;
    extern_information.sin_port = htons(port);
    inet_aton(address,&extern_information.sin_addr);
    

    return connect(_socket,(const struct sockaddr*)&extern_information,_size);
}

int envoyerData(int _socket,const char* data,const int taille)
{
    if(taille > TAILLE_MAX_DATA) return -1;

    char data_to_send[taille + 2];

    strcpy(data_to_send,data);
    data_to_send[taille] = '\r';
    data_to_send[taille+1] = '\n';
    
    return write(_socket,data_to_send,sizeof(data_to_send))-2;
}

int recevoirData(int _socket,char* data)
{
    int nbrByteLu = 0;
    int i = 0;
    char lecture[2] = {0};

    while(true)
    {
        if((i = read(_socket,&lecture[0],1)) == -1) return -1;
        else if(i == 0) return nbrByteLu;

        if(lecture[0] == '\r')
        {
            if((i = read(_socket,&lecture[1],1)) == -1) return -1;
            else if(i == 0) return nbrByteLu;

            if(lecture[1] == '\n') break;
            else
            {
                data[nbrByteLu] = lecture[0];
                data[nbrByteLu+1] = lecture[1];
                nbrByteLu += 2;
            }
        }
        data[nbrByteLu] = lecture[0];
        nbrByteLu += 1;
    }
    
    return nbrByteLu;
}

/************************************************
 * Permet de convertir un float textuel
 * afin de pouvoir le recuperer en format float
 ***********************************************/
void FloatFormater(char* valeur)
{
    //std::cout << valeur << std::endl;
    char* virgule = strchr(valeur, '.');
    if(virgule != NULL) {
        *virgule = ',';
    }
    //std::cout << valeur << std::endl;
}