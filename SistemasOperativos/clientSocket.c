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
#include <dirent.h>




#define BUFF_SIZE 1024



void enviarArchivo(int fdConnection, FILE *fdFile, char *name);
void addWatchToTree(int fdDirectory, char *_route, int wdCreate);
void buscarArchivo(char *routeRoot, char *name, char *_route);
void clientSocket(char *_ip, int _port, char *_routeRoot, char *name, int fdDirectory, int wdCreate);
void directoryMonitor(char *_ip, int _port, char _route[]);


// Proceso principal
int main(int argc, char ** argv) { 
    directoryMonitor(argv[1], atoi(argv[2]), argv[3]);
    exit(0);
} 


// Inotify
void directoryMonitor(char *_ip, int _port, char _route[]){
    int fdDirectory, wdCreate;      // File Descriptor de la carpeta a monitorizar y watch desciptor del evento
    char buffInotify[BUFF_SIZE];     // Buffer de la informacion de InotifyEvent
    ssize_t lengthRd;               // Longitud del evento leido
    pid_t pid;

    // Crea una instancia de Inotify
    fdDirectory = inotify_init();
    if (fdDirectory == -1) {
        fprintf(stderr, "[INotify-error]: Creacion de Inotify Fallida. %d: %s \n", errno, strerror(errno));
        exit(-1);
    }
    printf("[INotify]: Se ha Creado Inotify\n");

    addWatchToTree(fdDirectory, _route, wdCreate);

    // Ciclo que monitorea constante mente el directorio
    while(1){
        // Espera a que ocurra un evento y guarda la informacion y su longitud
        lengthRd = read(fdDirectory, buffInotify, BUFF_SIZE);
        if(lengthRd <= 0){
            fprintf(stderr, "[INotify-error]: La lectura de inotify es <= 0.\n");
            exit(-1);
        }
        printf("[INotify]: Se ha detectado un cambio.\n");

        pid = fork();
        if(pid == 0){
            //close(fdDirectory);

            printf("[INotify]: El archivo creado es: %s \n", ((struct inotify_event*)buffInotify)->name );

            clientSocket(_ip, _port, _route, ((struct inotify_event*)buffInotify)->name, fdDirectory, wdCreate);

            printf("Saliendo del proceso.\n");
            exit(0);
        }
    }
    close(fdDirectory);
}



// Socket cliente (Transmision de archivos)
void clientSocket(char *_ip, int _port, char *_routeRoot, char *name, int fdDirectory, int wdCreate){

    int fdSocket;                       // File Descriptor del socket
    struct sockaddr_in serverAddr;      // Direccion del Socket del Servidor
    char buffRx[BUFF_SIZE];              // Buffer de Entrada

    // Creacion del Socket
    if ((fdSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)) == -1){
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

    // Obtiene la ruta absoluta del archivo creado
    char _absoluteRoute[BUFF_SIZE] = {0};
    buscarArchivo(_routeRoot, name, _absoluteRoute);

    char _routeTemp1[BUFF_SIZE];
    char _absoluteTemp1[BUFF_SIZE];
    char _relativeRoute[BUFF_SIZE] = {0};

    char delim1[2] = "/";
    char *tokenOld;
    char *tokenNew;

    strcpy(_routeTemp1, _routeRoot);
    strcpy(_absoluteTemp1, _absoluteRoute);
    tokenNew = strtok(_routeTemp1, delim1);
    while(tokenNew != NULL){
        tokenOld = tokenNew;
        tokenNew = strtok(NULL, delim1);
    }

    tokenNew = strtok(_absoluteTemp1, delim1);
    bool flag = false;
    while(tokenNew != NULL){
        if((strcmp(tokenNew, tokenOld)==0)){
            flag = true;
        } else if(flag){
            strcat(_relativeRoute, "/");
            strcat(_relativeRoute, tokenNew);
        }
        tokenNew = strtok(NULL, delim1);
    }

    printf("[CLIENTE]: Ruta absoluta del archivo: %s\n", _absoluteRoute);
    printf("[CLIENTE]: Ruta relativa del archivo: %s\n", _relativeRoute);

    // Abre el archivo creado en modo lectura binaria
    FILE *fileOut = fopen(_absoluteRoute,"rb");
    if(fileOut == NULL){
		    printf("[CLIENTE-error]: Archivo no encontrado\n");
		    exit(-1);
	  }
    printf("[CLIENTE]: Archivo creado abierto exitosamente. \n");

    // Intenta abrir el archivo en forma de directorio
    // para saber si es directorio o archivo
    // En caso de que sea directorio le añade un watch descriptor
    DIR *isDir = opendir(_absoluteRoute);
    if (!(isDir = opendir(_absoluteRoute))){
        printf("[CLIENTE]: Es un archivo\n");
    } else {
        addWatchToTree(fdDirectory, _absoluteRoute, wdCreate);
        strcat(_relativeRoute, "/");
        printf("[CLIENTE]: Es directorio\n");
    }
    closedir(isDir);

    // Intenta conectarse al socket del servidor
    if (connect(fdSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) != 0){
        fprintf(stderr, "[CLIENTE-error]: No Pudo Conectar el Socket. %d: %s \n", errno, strerror( errno ));
        exit(-1);
    }
    printf("[CLIENTE]: Conexion establecida con %s.\n", (char *)inet_ntoa(serverAddr.sin_addr));

    enviarArchivo(fdSocket, fileOut, _relativeRoute);

    printf("[CLIENTE]: Cerrando conexion.\n");
    close(fdSocket);

    printf("[CLIENTE]: Cerrando Archivo.\n");
    fclose (fileOut);
}



// Busca el archivo creado en los subdirectorios y regresa la ruta abtoluta de este
void buscarArchivo(char *routeRoot, char *name, char *_route){
    DIR *dir;
    struct dirent *entry;

    if (!(dir = opendir(routeRoot))){
        fprintf(stderr, "[INotify-error]: No se pudo Abrir el directorio. %d: %s \n", errno, strerror(errno));
    } else {
        while ((entry = readdir(dir)) != NULL) {
            if(entry->d_type == DT_DIR){
                char path[BUFF_SIZE];
                if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
                    continue;
                snprintf(path, sizeof(path), "%s/%s", routeRoot, entry->d_name);

                //printf("\nDirectorio: %s:%s", entry->d_name, name);
                if(strcmp(entry->d_name, name) == 0){
                    strcpy(_route, path);
                    break;
                } else {
                    buscarArchivo(path, name, _route);
                    if(_route[0] != 0) break;
                }
            } else {
                char path[BUFF_SIZE];
                snprintf(path, sizeof(path), "%s/%s", routeRoot, entry->d_name);
                //printf("\nArchivo: %s:-%s", path, entry->d_name);
                if(strcmp(entry->d_name, name) == 0){
                    strcpy(_route, path);
                    break;
                }
            }
        }
        closedir(dir);
    }
}




// Añade una Directorio y sus subdirectorios un watch descriptor
void addWatchToTree(int fdDirectory, char *_route, int wdCreate){
    DIR *dir;
    struct dirent *entry;

    if (!(dir = opendir(_route))){
            fprintf(stderr, "[INotify-error]: No se pudo Abrir el directorio. %d: %s \n", errno, strerror(errno));
    } else if(_route != ""){
        // Crea un Watch Descriptor para la ruta dada
        wdCreate = inotify_add_watch(fdDirectory, _route, IN_CREATE);
        if (wdCreate == -1) {
            fprintf(stderr, "[INotify-error]: No se pudo añadir el Watch. %d: %s \n", errno, strerror(errno));
            exit(-1);
        }
        printf("[INotify]: Se ha añadodo watch a %s \n", _route );

        while ((entry = readdir(dir)) != NULL) {
            char _routeTemp[BUFF_SIZE] = {0};
            if (entry->d_type == DT_DIR) {
                char path[BUFF_SIZE];
                if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
                    continue;
                snprintf(path, sizeof(path), "%s/%s", _route, entry->d_name);
                addWatchToTree(fdDirectory, path, wdCreate);
            }
        }
        closedir(dir);
    }
}



// Se conecta con el servidor y envia el archivo
void enviarArchivo(int fdConnection, FILE *fdFile, char *name){
    char data[BUFF_SIZE] = {0};
    char msj[BUFF_SIZE] = {0};

    // Le envia la ruta relativa del archivo y recibe confirmacion
    printf("[CLIENTE]: Mandando ruta de archivo.\n");
    write(fdConnection, name, BUFF_SIZE);
    read(fdConnection, msj, BUFF_SIZE);
    printf("[SERVIDOR]: %s.\n");

    // Inicia la tranferencia del archivo
    printf("[CLIENTE]: Iniciando Transferencia\n");
    while(fgets(data, BUFF_SIZE, fdFile) != NULL) {
        if (send(fdConnection, data, sizeof(data), 0) == -1) {
            perror("[CLIENTE]: Error al mandar el archivo.");
            exit(-1);
        }
        bzero(data, BUFF_SIZE);
    }
    printf("[CLIENTE]: Transferencia Completada\n");
    close(fdConnection);
}
