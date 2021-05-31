//Simbolos Estandar y salir 
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>

//Errores y Strings
#include <errno.h>
#include <string.h>

//Sockets
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netdb.h>

//Monitorizacion
#include <sys/inotify.h>

#define BUFF_MAX 256

#define READ_END  0
#define WRITE_END 1

void clientSocket(char _ip[], int _port, char _msg[]){
    int fdSocket;                       // File Descriptor del socket
    struct sockaddr_in serverAddr;      // Direccion del Socket del Servidor 
 
    char buffRx[BUFF_MAX];              // Buffer de Entrada 
    

    // Creacion del Socket
    if ((fdSocket = socket(AF_INET, SOCK_STREAM, 0)) == -1){ 
        printf("[CLIENTE]: Fallo la Creacion del Cliente.\n"); 
        exit(-1);  
    }
    printf("[CLIENTE]: El Socket se Creo Satisfactoriamente.\n"); 
    

    // Limpia el socket 
    memset(&serverAddr, 0, sizeof(serverAddr));


    // Asigna la Familia, IPv4, Puerto
    serverAddr.sin_family = AF_INET; 
    serverAddr.sin_addr.s_addr = inet_addr( _ip ); 
    serverAddr.sin_port = htons( _port ); 
 

    // Intenta conectarse al socket del servidor
    if (connect(fdSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) != 0){ 
        fprintf(stderr, "[CLIENTE-error]: No Pudo Conectar el Socket. %d: %s \n", errno, strerror( errno ));
        exit(-1);
    } 
    printf("[CLIENTE]: Enviando mensaje al Servidor.\n"); 
    write(fdSocket, _msg, BUFF_MAX);     // Envia un Mensaje al Servidor
      
    printf("[CLIENTE]: Cerrando Coneccion.\n");
    // Cierra la Conexion con el Servidor
    close(fdSocket);
}



void directoryMonitor(char _ip[], int _port, char _route[], int _pipeFlag[2] ){
    int fdDirectory, wdCreate;      // File Descriptor de la carpeta a monitorizar y watch desciptor del evento
    char buffInotify[BUFF_MAX];     // Buffer de la informacion de InotifyEvent
    ssize_t lengthRd;               // Longitud del evento leido  
    pid_t pid;

    // Crea una instancia de Inotify 
    fdDirectory = inotify_init();
    if (fdDirectory == -1) {
        fprintf(stderr, "[INotify-error]: Creacion de Inotify Fallida. %d: %s \n", errno, strerror(errno));
        exit(-1);
    }
    printf("[INotify]: Se ha Creado Inotify\n");
   
    // Crea un Watch Descriptor para la ruta dada
    wdCreate = inotify_add_watch(fdDirectory, _route, IN_CREATE);
    if (wdCreate == -1) {
        fprintf(stderr, "[INotify-error]: No se pudo añadir el Watch. %d: %s \n", errno, strerror(errno));
        exit(-1);
    }
    printf("[INotify]: Se ha añadodo watch a %s \n", _route );
    
    // Ciclo que monitorea constante mente el directorio
    while(1){
        // Espera a que ocurra un evento y guarda la informacion y su longitud
        lengthRd = read(fdDirectory, buffInotify, BUFF_MAX);
        if(lengthRd <= 0){
            fprintf(stderr, "[SISTEM-error]: La lectura de inotify es <= 0.\n");
            exit(-1);
        }

        pid = fork();
        if(pid == 0){
            close(fdDirectory);
            char _buffOut[BUFF_MAX] = {0};      
    
            strcat(_buffOut, ((struct inotify_event*)buffInotify)->name);
            printf("[INotify]: El archivo creado es: %s \n", _buffOut );
            
            //char buffPipe[BUFF_MAX];
            //close(_pipeFlag[WRITE_END]);
            //int num = read(_pipeFlag[READ_END], buffPipe, BUFF_MAX);
            //printf("El archivo que creo el Servidor es: %d \n", num);

            struct arrayChar{ char string[BUFF_MAX]; };
            struct arrayChar _aux;
     
            char *_bufferTemp;
            int lines = -1;

            FILE *ipsFile = fopen("ipsFile","r");
            if(ipsFile == NULL){
		            printf("Archivo no encontrado\n");
		            exit(-1);
	          }
            printf("[INotify]: Archivo de Ips y Puertos abierto exitosamente.\n");

            while(1){
                lines += 1;
                _bufferTemp = fgets(_aux.string, BUFF_MAX, ipsFile);
                if(_bufferTemp == NULL){
                    printf("[INotify]: Creacion de Hijos completada.\n");
                    break;
                }
                char delim[] = " ";
                char *token = strtok(_bufferTemp, delim);
                int flag = 0;
                char _tmpIp[20];
                int _tmpPort;
                if(token != NULL)
                    while( token  != NULL){
                        if(flag == 0) { strcpy(_tmpIp, token); flag = 1; }
                        else { _tmpPort = atoi(token); flag = 0; }
                        token = strtok(NULL, delim);
                    }
                pid = fork();
                if(pid == 0){
                    fclose(ipsFile);
                    printf("[IP] %s \t[PORT]%d\n", _tmpIp, _tmpPort);
                    if(_tmpPort != _port) clientSocket(_tmpIp, _tmpPort, _buffOut);
                    exit(0);
                }
            }
            printf("[INotify]: %d direcciones recuperadas exitosamente.\n", lines);

            fclose (ipsFile);
            exit(0);
        }   
    }
}


void socketServer(char _ip[], int _port, int _backlog, char _route[], int _pipeFlag[2]){

    int fdSocket, fdConnection;                     // File Descriptors
    struct sockaddr_in serverAddr, clientAddr;      // Formato de direccion del servidor y cliente
    
    socklen_t clientAddrLen;                        // Tamaño de la direccion del cliente
    int  lengthRx;                                  // Bytes transmitidos y recibidos
    
    char buffRx[BUFF_MAX];                          // Buffer de Recepcion
    
    pid_t pIdSocket;                                // Id para control del proceso padre e hijo
    

    // Crea y Comprueba si el socket se ha creado exitosamente
    if ( (fdSocket = socket(AF_INET, SOCK_STREAM, 0)) == -1 ) {
        fprintf(stderr, "[SERVIDOR-error]: Creacion de Socket Fallida. %d: %s \n", errno, strerror(errno));
        exit(EXIT_FAILURE);
    }
    printf("[SERVIDOR]: Socket Creado Satisfactoriamente.\n"); 


    // Inilializa con ceros la estructura
    memset(&serverAddr, 0, sizeof(serverAddr));

    // Asigna la Familia, IPv4, Puerto
    serverAddr.sin_family      = AF_INET;
    serverAddr.sin_addr.s_addr = inet_addr(_ip);
    serverAddr.sin_port        = htons(_port);


    // Asigna y Comprueba si se asigno correctamente la IP y el Puerto al Socket
    if ( (bind(fdSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr))) != 0 ){
        fprintf(stderr, "[SERVIDOR-error]: Enlazamiento de Socket Fallida. %d: %s \n", errno, strerror( errno ));
        exit(EXIT_FAILURE);
    }
    printf("[SERVIDOR]: Socket Enlazado Satiscatoriamente.\n"); 



    // Pone el Socket en modo escuchar (Listen) 
    if ((listen(fdSocket, _backlog)) != 0){
        fprintf(stderr, "[SERVIDOR-error]: No Pudo Activar el Socket. %d: %s \n", errno, strerror( errno ));
        exit(EXIT_FAILURE);
    } 
    printf("[SERVIDOR]: Escuchando en el Puerto %d \n", ntohs(serverAddr.sin_port) ); 


    
    // Ciclo que espera a que se conecte un cliente y crea un hijo para que lo atienda
    while(1){
        
        // Espera la coneccion de un cliente
        clientAddrLen = sizeof(struct sockaddr_in);
        fdConnection = accept(fdSocket, (struct sockaddr *)&clientAddr, &clientAddrLen);
        printf("[SERVIDOR]: Conexion Detectada %d.\n", htons(clientAddr.sin_port)); 

        if (fdConnection < 0){
            // Error en la conexion
            fprintf(stderr, "[SERVIDOR-error]: Conexion no aceptada. %d: %s \n", errno, strerror( errno ));
        } else {
            pIdSocket = fork();
            
            if(pIdSocket == 0){   // Proceso hijo que servira al cliente
                printf("[SERVIDOR-hijo]: Entrando en proceso hijo.\n");
                while(1){
                    
                    // Obtiene el tamaño del mensaje recibido del cliente
                    lengthRx = read(fdConnection, buffRx, sizeof(buffRx));
                    buffRx[lengthRx] = '\0';
                    if(lengthRx == -1){         // Si no se puede leer manda un mensaje de error.
                        fprintf(stderr, "[SERVIDOR-hijo-error]: fdConnection NO Pudo Ser Leida. %d: %s \n", errno, strerror( errno ));
                    } else if(lengthRx == 0){   // Si el cliente manda un valor de longitud 0 cierra la coneccion
                        printf("[SERVIDOR-hijo]: Cliente ha Sido Cerrado, Cerrando fdConnection.\n");
                        close(fdConnection);
                        break;
                    } else{                     // Se lee el mensaje recibido del cliente                    
                        printf("[SERVIDOR-hijo]: Archivo: %s \n", buffRx);             // Imprime el mensaje recibido del criente
                    
                        char _tmpRoute[BUFF_MAX];
                        strcpy(_tmpRoute, _route);
                        strcat(_tmpRoute, "/");
                        strcat(_tmpRoute, buffRx);
                        
                        printf("[SISTEMA]: Creando Archivo \'%s\' Creado en \'%s\'\n", buffRx, _route);
                        int errorExec = execlp("/bin/mkdir", "mkdir", _tmpRoute); 
                        if(errorExec == -1){
                            fprintf(stderr, "[SISTEMA-error]: Archivo Existente.\n");
                        } else {
                            printf("[SISTEMA]: Archivo Creado con exito.");
                            //close(_pipeFlag[READ_END]);
                            //write(_pipeFlag[WRITE_END], buffRx, BUFF_MAX);
                            //close(_pipeFlag[WRITE_END]);
                        }
                    }
                }
                printf("[SERVIDOR]: Saliendo de proceso hijo.\n");      
                exit(0);                                     // Sale del proceso hijo
            } else { close(fdConnection); }     // Cierra la coneccion para el padre
        }
        memset(&fdConnection, 0, sizeof(fdConnection));
    }

}


/**
 * argv[1] = IP
 * argv[2] = PUERTO
 * argv[3] = BACKLOG
 * argv[4] = RUTA
 */
int main(int argc, char ** argv){
  
    pid_t pid;
    pid = fork();
    
    int _pipeFlag[2];
    pipe(_pipeFlag);


    if(pid == 0){
        directoryMonitor(argv[1], atoi(argv[2]), argv[4], _pipeFlag); 
        exit(0);
    }
    
    //pid = fork();
    //if(pid == 0){
        socketServer(argv[1], atoi(argv[2]), atoi(argv[3]), argv[4], _pipeFlag);
        exit(0);
    //} 
   
    //char _c[BUFF_MAX];
    //getline(_c);
    
    exit(0);
}
