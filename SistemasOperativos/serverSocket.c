//Estandar
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

//Directorios
#include <sys/stat.h>
#include <dirent.h>




#define BUFF_SIZE 1024



void crearDirectorio(char *routeRoot, char *route);
void crearArchivo(int fdConnection, char* routeRoot);
void socketServer(char _ip[], int _port, int _backlog, char _route[]);


/**
 * argv[1] = IP
 * argv[2] = PUERTO
 * argv[3] = BACKLOG
 * argv[4] = RUTA
 */
int main(int argc, char ** argv){
    socketServer(argv[1], atoi(argv[2]), atoi(argv[3]), argv[4]);
    exit(0);
}


// Crea e inicia el socket servidor
void socketServer(char _ip[], int _port, int _backlog, char _route[]){

    int fdSocket, fdConnection;                     // File Descriptors
    struct sockaddr_in serverAddr, clientAddr;      // Formato de direccion del servidor y cliente
    socklen_t clientAddrLen;                        // Tamaño de la direccion del cliente
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
        printf("[SERVIDOR]: Conexion Detectada %s.\n", (char *)inet_ntoa(clientAddr.sin_addr));

        if (fdConnection < 0){
            // Error en la conexion
            fprintf(stderr, "[SERVIDOR-error]: Conexion no aceptada. %d: %s \n", errno, strerror( errno ));
        } else {
            pIdSocket = fork();
            if(pIdSocket == 0){   // Proceso hijo que servira al cliente
                crearArchivo(fdConnection, _route);

                printf("[SERVIDOR-hijo]: Cerrando conexion\n");
                close(fdConnection);

                printf("[SERVIDOR-hijo]: Saliendo de proceso hijo.\n");
                exit(0);                                     // Sale del proceso hijo
            } else { close(fdConnection); }     // Cierra la coneccion para el padre
        }
        memset(&fdConnection, 0, sizeof(fdConnection));
    }

}



// Crea el archivo en la carpeta del servidor
void crearArchivo(int fdConnection, char* routeRoot){
    int _len;
    FILE *fdFile;
    char _buffer[BUFF_SIZE];
    char route[BUFF_SIZE];

    printf("[SERVIDOR-hijo]: Obteniendo ruta de archivo.\n");
    read(fdConnection, route, BUFF_SIZE);

    printf("[SERVIDOR-hijo]: Ruta obtenida. %s\n", route);
    write(fdConnection, "Recibido", BUFF_SIZE);

    // Rutas temporales
    char _tempRouteDir[BUFF_SIZE];
    strcpy(_tempRouteDir,routeRoot);
    strcat(_tempRouteDir,route);

    // Distingue en tre archivo y directorio
    int contador = 0;
    while( route[contador] != 0 ) contador++;
    contador--;
    if(route[contador] == '/'){
        printf("[SERVIDOR-hijo]: Es un directorio.\n");
        DIR *dirFD = opendir(_tempRouteDir);
        if(!(dirFD = opendir(_tempRouteDir))){
            fprintf(stderr, "[SERVIDOR-error]:  Iniciando creacion de directorio. %d: %s \n", errno, strerror(errno));
            crearDirectorio(routeRoot, route);
        } else { printf("El directorio ya existe\n"); }
        closedir(dirFD);
    } else {
        printf("[SERVIDOR-hijo]: Es un archivo.\n");
        fdFile = fopen(_tempRouteDir, "wb");
        if(fdFile == NULL){
            // No existe el directorio donde se quiere crear el archivo
            fprintf(stderr, "[SERVIDOR-error]: No existe el directorio. %d: %s \n", errno, strerror(errno));

            // Intenta crear el directorio faltante
            int index = 0;
            int indexcut = 0;
            char routeCut[BUFF_SIZE] = {0};
            char routeRelative[BUFF_SIZE] = {0};

            while(route[index] != 0){
                routeCut[indexcut] = route[index];
                indexcut++;
                index++;
                if(route[index] == '/'){
                    strcat(routeRelative, routeCut);
                    strcat(routeRelative, "/");
                    bzero(routeCut, BUFF_SIZE);
                    indexcut = 0;
                }
            }
            crearDirectorio(routeRoot, routeRelative);

            // Intenta crear nuevamente el archivo
            fdFile = fopen(_tempRouteDir, "wb");
            if(fdFile == NULL){
                fprintf(stderr, "[SERVIDOR-error]: No se pudo crear el directorio. %d: %s \n", errno, strerror(errno));
                fclose(fdFile);
                exit(-1);
            }

            // Inicia la tranferencia del archivo
            while((_len=recv(fdConnection, _buffer, BUFF_SIZE,0))!=0) {
                fwrite(_buffer, sizeof(_buffer), 1, fdFile);
                printf("[RECIBIDO]: %s\n", _buffer);
                bzero(_buffer, BUFF_SIZE);
            }
            printf("[SERVIDOR]: Tranferencia finalizada\n");
            fclose(fdFile);
            exit(-1);
        } else {

            // Inicia la transferencia del archivo
            while((_len=recv(fdConnection, _buffer, BUFF_SIZE,0) != 0)) {
                fwrite(_buffer, sizeof(_buffer), 1, fdFile);
                printf("[RECIBIDO]: %s\n", _buffer);
                bzero(_buffer, BUFF_SIZE);
            }
            printf("[SERVIDOR]: Transferencia finalizada\n");
            fclose(fdFile);
            exit(-1);
        }
    }
}



// Crea la ruta especificada
void crearDirectorio(char *routeRoot, char *route){
    char _routeTemp[BUFF_SIZE];
    char delim[2] = "/";
    char *token1;
    char *token2;
    char _routeFoward[BUFF_SIZE] = {0};

    strcpy(_routeFoward, routeRoot);
    strcat(_routeFoward, "/");

    strcpy(_routeTemp, route);

    token1 = strtok(_routeTemp, delim);
    while(token1 != NULL){
        token2 = token1;
        token1 = strtok(NULL, delim);
        strcat(_routeFoward, token2);
        printf("[SERVIDOR]: Creando directorio: %s\n", _routeFoward);
        if(mkdir(_routeFoward, S_IRWXU) == -1)
            fprintf(stderr, "[SERVIDOR-error]: Este directorio ya existe. %d: %s \n", errno, strerror(errno));
        else printf("[SERVIDOR]: Directorio creado.");

        if(token1 != NULL) strcat(_routeFoward, "/");
    }
}
