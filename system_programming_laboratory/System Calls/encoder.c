#include "util.h"
/*extern int system_call(int a,char* b,int c,int d);*/
#define SYS_READ 3
#define SYS_WRITE 4
#define SYS_OPEN 5
#define SYS_CLOSE 6
#define SYS_CREATE 8
#define STDERR 2
#define STDOUT 1
#define STDIN 0
#define SYS_EXIT 1


int main(int args, char *argv[]){
    int debugState = 0;
    int fileDisciptor = STDIN;
    int fileOName = STDOUT;
    char* temp1 = "STDIN";
    char* temp2 = "STDOUT";
    int con=0;
    
int i=0;
for (i = 0 ; i < args ; i++)
    {
        char *arg = argv[i];
        if(strcmp(arg, "-D")==0)
        {
            debugState = 1;
        }

        if(strncmp(arg, "-i",2)==0)
        {
            temp1 = arg+2;
            fileDisciptor = system_call(SYS_OPEN,temp1,0,0644);
        }

        if(strncmp(arg, "-o",2)==0)
        {
            temp2 = arg+2;
            fileOName=system_call(SYS_OPEN,temp2,2|64,0644);
        }
    }
    char* str;
    int a1= system_call(SYS_READ,fileDisciptor, &str, 1);  
    /*while(strcmp(str,"-1")!=0){ */
    while(a1!=0){
    if(debugState==1)
    {
        system_call(SYS_WRITE, STDERR, "ID: 1\n",6);
        system_call(SYS_WRITE, STDERR, "Return Code: ",13); 
        char* tmp = itoa(a1);
        system_call(SYS_WRITE, STDERR,tmp,sizeof(tmp)); 
        system_call(SYS_WRITE,STDERR,"\n",1);
    }

     if(str >= 'a' && str <= 'z')
     {
        str =str-32;
     }
    int a2 = system_call(SYS_WRITE, fileOName, &str, strlen(&str));

    if(debugState==1)
    {
        system_call(SYS_WRITE,STDERR,"\n",1);
        system_call(SYS_WRITE, STDERR, "ID: 4\n",6);
        system_call(SYS_WRITE, STDERR, "Return Code: ",13);
        char* tmp = itoa(a2);
        system_call(SYS_WRITE, STDERR,tmp,sizeof(tmp)); 
        system_call(SYS_WRITE,STDERR,"\n",1);

        system_call(SYS_WRITE, STDERR, "The input file path: ",21);
        system_call(SYS_WRITE, STDERR,temp1,strlen(temp1));
         system_call(SYS_WRITE,STDERR,"\n",1);

        system_call(SYS_WRITE, STDERR, "The output file path: ",22);
        system_call(SYS_WRITE, STDERR,temp2,strlen(temp2));
        system_call(SYS_WRITE,STDERR,"\n",1);
    }
    a1= system_call(SYS_READ,fileDisciptor, &str, 1);  
    }

    if(fileDisciptor!=STDIN)
    {
        system_call(SYS_CLOSE,fileDisciptor,0,0);
    }
    if(fileOName!=STDOUT)
    {
        system_call(SYS_CLOSE,fileOName,0,0);
    }
    system_call(SYS_WRITE,STDERR,"\n",1);
    system_call(SYS_EXIT,1,0,0);
    return 1;

}