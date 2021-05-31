//Simbolos Estandar y salir
#include <unistd.h>
#include <stdlib.h> 
#include <sys/wait.h>

//Errores y Strings
#include <stdio.h> 
#include <string.h> 
#include <cerrno>

//Sockets
#include <netinet/in.h>
#include <sys/socket.h> 
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



void directoryMonitor(char _ip[], int _port, char _route[]){
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

int main(int argc, char ** argv) { 
      
    directoryMonitor(argv[1], atoi(argv[2]), argv[3]); 
    
    exit(0);
} 

