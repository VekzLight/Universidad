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

void socketServer(char _ip[], int _port, int _backlog, char _route[]){ 

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
