#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include <limits.h>
#include "LineParser.h"
#include <unistd.h>
#include <sys/wait.h>

#define MAX 2048

void execute(cmdLine *pCmdLine)
{
    int out = execvp(pCmdLine->arguments[0],pCmdLine->arguments);
    if(out==-1){
        perror("The wanted command cannot be execute");
        _exit(1);       //??
    }
}

int main(int argc, char **argv){
    bool debugState = false;
    
    for(int i=0; i<argc; i++)
    {
        char *arg = argv[i];
        if(strncmp(arg, "-d",2)==0)
        {
            debugState = true;
        }
    }

    int status = NULL;
    while (1)
    {
        char buf[PATH_MAX];
        char* command = (char*)calloc(MAX,sizeof(char));
        char* newPath =(char*)calloc(MAX,sizeof(char));
        printf("The current working directory: ");
        getcwd(buf,PATH_MAX);          
        printf("%s\n", buf);
        printf("Please enter a command\n");
        fgets(command, MAX, stdin);
        if(feof(stdin)){
            exit(0);
        }
        if(strncmp("quit" ,command, 4)==0){
            exit(0);      
        }
        if(strncmp("cd",command,2)==0){ ///???check!
            newPath = command+3; 
            int out = chdir(newPath);
            if(out==-1){
                fprintf(stderr,"The command to change the working directory failed!",MAX);
            }
            else{
                fprintf(stderr,"succes",MAX);
            }
        }

        cmdLine* line = parseCmdLines(command);
        pid_t idProcess = fork();
       
        if(idProcess==-1)
        {
            printf("You are in problem!!");
            printf("\n");
        }
        else{
            if(debugState){
                printf("The PID of the process: %d\n",idProcess);
                printf("The command: %s\n", command);
            } 
        }
        if(idProcess>0){
        if(line->blocking==1){
            printf("%d\n",idProcess);
            printf("line->blocking==1");
            printf("\n");
            waitpid(idProcess,&status,0);
        }
        }
        
        if(idProcess==0){
        execute(line);
        freeCmdLines(line);
         }

        printf("end program");
        printf("\n");
    }
    
	return 0;
}