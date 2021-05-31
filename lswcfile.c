#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <errno.h>
#include <fcntl.h>

#define READ_END  0 // Index pipe (Extremo de escritura)
#define WRITE_END 1 // Index pipe (Extremo de lectura)

#define FILE_NAME "file.txt"

int main(int argc, char* argv[]){
    int fd1[2], fd2;
    int status, pid;

    pipe(fd1);      // Crea pipe para hijos
    pid = fork();   // Creo hijos

    if(pid == 0){   // Hijo 1 //
        close(fd1[READ_END]);   // Cerrar lectura para hijo 1

        dup2(fd1[WRITE_END], STDOUT_FILENO);    // Redirecciona escritura de STDOUT_FILENO a fd1[WRITE_END]
        close(fd1[WRITE_END]);

        execlp("/bin/ls", "ls", "-l", NULL);    // Ejecuta ls

    } else {        // Padre //
        close(fd1[WRITE_END]);  // Cerrar lectura para padre

        pid = fork();
        if(pid == 0){   // Hijo 2 //
            fd2 = open(FILE_NAME, O_WRONLY);    // Abre un archivo en modo escritura
            dup2(fd1[READ_END], STDIN_FILENO);  // Redirecciona el STDIN_FILENO a fd1[READ_END]
            close(fd1[READ_END]);               // Cierra fd1[READ_END]

            dup2(fd2, STDOUT_FILENO);           // Redirecciona STDOUT_FILENO a fd2
            execlp("/usr/bin/wc", "wc, NULL");  // Ejecuta wc

        } else {       // Padre //
            close(fd1[READ_END]);   // Cerrar extremo fd1[READ_END]
        }
    }

    wait(&status);
    wait(&status);

    return 0;
}
