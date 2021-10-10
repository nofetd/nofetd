#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include <limits.h>
#include "LineParser.h"
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>
#include <sys/types.h>

#define TERMINATED  -1
#define RUNNING 1
#define SUSPENDED 0
#define MAX 2048

typedef struct process{
    cmdLine* cmd;                             /* the parsed command line*/
    pid_t pid; 		                          /* the process id that is running the command*/
    int status;                               /* status of the process: RUNNING/SUSPENDED/TERMINATED */
    struct process *next;	                  /* next process in chain */
} process;

void addProcess(process** process_list, cmdLine* cmd, pid_t pid){ 
        process* newList = (process*)malloc(sizeof(process));
        newList->cmd = cmd;
        newList->pid=pid;
        newList->status=RUNNING;
        newList->next=(*process_list);
        process_list=&newList;
        printf("new process added\n"); 
        fprintf(stdout,"newList PID: %d\n",newList->pid);
        fprintf(stdout,"process_list PID: %d\n", (*process_list)->pid);
        fprintf(stdout,"process_list command: %s\n", (*process_list)->cmd->arguments[0]);
        fprintf(stdout,"process_list status: %d\n", (*process_list)->status);
        /*fprintf(stdout,"PID: %d\n",newList->pid);*/
}

void printProcessList(process** process_list){
    /*updateProcessList(process_list);*/
   /* struct process* process = *(process_list);*/
   /*  struct process* prev = processs;*/
  struct process* curr = *(process_list);
   while(curr!=NULL)
   {
        printf("succsede to get in printProcessList\n");
        printf("PID: %d\n",curr->pid);
        printf("command: %s\n",curr->cmd->arguments[0]);
        printf("\n");
        /*curr = curr->next;
        prev = curr;
        if(prev->status = TERMINATED)
        {
            freeProcessList(prev);   //delete?
        }*/
     }
}

void freeProcessList(process* process_list){
    if(process_list!=NULL)
    {
        if(process_list->next==NULL)
        {
            free(process_list->cmd);
            free(process_list);
        }
        else
        {
            free(process_list->cmd);
            free(process_list);
            freeProcessList(process_list->next);
        } 
    }
}

int execute(cmdLine *pCmdLine)
{
    
    int out = execvp(pCmdLine->arguments[0],pCmdLine->arguments);
    if(out==-1){
        perror("The wanted command cannot be execute");
        _exit(1);       //??
    }
return out;
} 

int main(int argc, char **argv){
    process** process_list; 
    char* command = (char*)calloc(MAX,sizeof(char));
    char* processID;
    int PID;
    while(1){
        printf("Enter a command\n");

        fgets(command,MAX,stdin);   
        if(strncmp("procs",command,5)==0){
            printf("succsede\n");
            printProcessList(process_list);
        }
        else if(feof(stdin)){
                    exit(0);
                }
        
        else if(strncmp("quit" ,command, 4)==0){
                    exit(0);      
                }
        else{    
            cmdLine* line = parseCmdLines(command);
            pid_t idProcess = fork();
            int out=-1;
            if(idProcess==0){
            out = execute(line);
            }
              /* if(idProcess>0){
            waitpid(idProcess,NULL,0);
        }*/
           addProcess(process_list,line,idProcess);
        }
    }
}

/*
void printProcess(process* process){
    fprintf(stdout,"PID: %d\n",process->pid);
    fprintf(stdout,"command: %s\n",process->cmd->arguments[0]);   //??
    char* status;
    if(process->status==-1){
        status = "TERMINATED";
    }
    else if(process->status==1){
        status = "RUNNING";
    }
    else{
        status = "SUSPENDED";
    }
    fprintf("STATUS: %s\n", status);
}

 if(strncmp(command,"suspend",7)==0){
        processID = command+8;
        PID = atoi(processID);
        signal(SIGTSTP,SIG_DFL);
    }
    if(strncmp(command,"kill",4)==0){
        processID = command+5;
        PID = atoi(processID);
        int out = kill(PID, SIGINT);
        if(out==0){
            printf("The kill command was done successfuly");
            updateProcessStatus(process_list,PID,TERMINATED);
        }
        if(out==-1){
            printf("Error to kill the process");
        }
    }
    if(strncmp(command,"wake",4)==0){
        processID = command+5;
        PID = atoi(processID);
    }

void updateProcessList(process **process_list){
    struct process* process = *(process_list);
    int Pstatus;
    int Wstatus;
    while(process!=NULL)
    {
        Wstatus = waitpid(process->pid,&Pstatus,WNOHANG);
        if(Wstatus==-1){
                perror("Eroor!");
                exit(EXIT_FAILURE);
            }
        if(Wstatus>0){       
            if(WIFSIGNALED(Pstatus)){
                updateProcessStatus(process,process->pid,TERMINATED);
            }
            else if(WIFSTOPPED(Pstatus)){
                updateProcessStatus(process,process->pid,SUSPENDED);
            }
            else if(WIFCONTINUED(Pstatus)){     //???
                updateProcessStatus(process,process->pid,RUNNING);
            }
        }
        process = process->next;
    }
}

void updateProcessStatus(process* process_list, int pid, int status){
    process_list->status = status;
}

*/