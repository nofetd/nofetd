#include <stdlib.h>
#include <stdio.h>
#include <string.h>

typedef struct virus {
    unsigned short SigSize;
    char virusName[16];
    unsigned char* sig;
} virus;

typedef struct link link;
 
struct link {
    link *nextVirus;
    virus *vir;
};


virus* readVirus(FILE* f ){
virus *vir=(virus*)malloc(sizeof(virus));
int Done=fread(vir,1,18,f);
if(Done==0){
   free(vir);
   return NULL;
}
int size=vir->SigSize*sizeof(unsigned char);  
vir->sig=(unsigned char*) malloc(size);
fread(vir->sig,1,vir->SigSize,f); 
return vir;
}


void printVirus(virus* virus, FILE* output){
fprintf(output,"virus name: %s\n",virus->virusName);
fprintf(output,"SigSize: %d\n",virus->SigSize);
fprintf(output,"sig: \n");
int i;
for(i=0; i<virus->SigSize;i++){
    fprintf(output,"%02x ",(unsigned char)virus->sig[i]);
}
}

void list_print(link *virus_list, FILE* f){
    link *tmp=virus_list;
    while(tmp!=NULL){
       //if(tmp->vir!=NULL){
       printVirus(tmp->vir,f);
       fputs("\n",f);
      // list_print(*virus_list->nextVirus, f);
      tmp=tmp->nextVirus;
      // }
    }
}
 
link* list_append(link* virus_list, virus* data){
  link* next=(link*)malloc(sizeof(link));
  next->vir=data;
  if (virus_list==NULL)
  {
     next->nextVirus=NULL;
  }
  else{
     next->nextVirus=virus_list;
  }
  return next;
}   
    
 /* Free the memory allocated by the list. */
void list_free(link *virus_list){
     if(virus_list!=NULL){
         if (virus_list->nextVirus==NULL)
         {
           free(virus_list->vir->sig);
           free(virus_list->vir);
         }
    }
    else{
   free(virus_list->vir);
   list_free(virus_list->nextVirus);
    }
}

void detect_virus(char *buffer, unsigned int size, link *virus_list){
       link* tmp=virus_list;
        while(tmp!=NULL && tmp->vir!=NULL){
        for(int i=0;i<size;i++){
           int equal=memcmp(buffer+i,tmp->vir->sig,tmp->vir->SigSize);//like that?
           if(equal==0){
               printf("The starting byte location in the suspected file: %d\n",i);//like that?
               printf("The virus name : %s\n",tmp->vir->virusName);//like that?
               printf("The size of the virus signature: %d\n",tmp->vir->SigSize);
           }
            }
            tmp=tmp->nextVirus;
        }
    }

int main(int argc, char **argv){
int choose=-1;
char Filename[100];
FILE* Read;
link* list=NULL;
char buffer[10000];
FILE* Write; 
while (1)
{

printf("%s","> menu\n Please choose a function:\n 1) Load signatures\n 2) Print signatures\n 3) Detected Virus\n 4) Quit\n");

scanf("%d", &choose);
fgetc(stdin);
if( choose < 0||4 < choose){
    printf("Not within bounds\n");
}
else{
    printf("Within bounds\n");
switch (choose)
{
case 1:
    printf("please enter file name\n");
    scanf("%s", Filename);
    fgetc(stdin);
    Read=fopen(Filename, "rb");
    virus* vir=readVirus(Read);
    while (vir!=NULL)
    {
     list=list_append(list,vir);
     vir=readVirus(Read);
    }
    fclose(Read);
    break;

case 2:
     if(list!=NULL){
     list_print(list,stdout);
     }
    break;
case 3:
      Write=fopen(argv[1],"r+");
      int size=fread(buffer,1,sizeof(buffer),Read);
      detect_virus(buffer,size,list);
      fclose(Write);
     break;
case 4:
    list_free(list);
    exit(0);
    break;
}
   }
}
}
/*

 link* next=(link*)malloc(sizeof(link));
  next->vir=data;
  next->nextVirus=NULL;
  if (virus_list==NULL)
  {
      virus_list=(link*)malloc(sizeof(link));
      virus_list=next;
  }
  else{
    virus_list->nextVirus=next;
  }
  return virus_list;
}   
  if(virus_list==NULL){
       virus_list=(link*)malloc(sizeof(link));
       virus_list->nextVirus=NULL;
       virus_list->vir=data;
   } 
   else{
       link* next=(link*)malloc(sizeof(link));
       next->vir=data;
       virus_list->nextVirus=next;
   }
   return virus_list;*/

   //  int len=(int)tmp->vir->SigSize;