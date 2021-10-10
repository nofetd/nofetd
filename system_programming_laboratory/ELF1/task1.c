#include <stdlib.h>
#include <stdio.h>
#include <string.h>

typedef struct {
  char debug_mode;
  char file_name[128];
  int unit_size;
  unsigned char mem_buf[10000];
  size_t mem_count;
  char display_mode;
} state;

void Debug(state* s){
    if(s->debug_mode=='1'){
        s->debug_mode='0';
        printf("%s","Debug flag now off\n");
    }
    else{
    s->debug_mode='1';
    printf("%s","Debug flag now on\n");
    }
}  

void File_Name(state* s){
    printf("%s","Please enter file Name\n");
    char input[100];
    scanf("%s", &input);
    fgetc(stdin);
    strncpy(s->file_name,input,100);
    if(s->debug_mode=='1'){
        printf("Debug: file name set to %s\n",input);
    }       
}

void Unit_Size(state* s){
    printf("%s","Please enter size\n");
    int size=-1;
    scanf("%d", &size);
    fgetc(stdin);
    if(size==1|| size==2|| size==4)
    {
        s->unit_size=size;
        if(s->debug_mode=='1'){
            printf("Debug: set size to %d\n",size);
        }
    }
    else{
        printf("Error: input invalid\n");
    }
}

void Load(state* s){
    if(strcmp(s->file_name,"")==0){
        printf("ERROR: you need to insert file name!!\n");
    }
    else{
        FILE* f = fopen(s->file_name,"r");
        if(f == NULL){
            printf("ERROR: can't open the file!!\n");
        }
        else{
            printf("please enter location (in hexadecimal)\n");
            int location=-1;
            scanf("%x", &location);
            fgetc(stdin);
            printf("please enter length (in decimal)\n");
            int length=-1;
            scanf("%d", &length);
            fgetc(stdin);
            if(s->debug_mode=='1'){
                if(s->display_mode=='0'){
                    printf("Debug:The file name: %s, The location: %d, The length: %d\n", s->file_name, location, length);
                }
                else
                {
                    printf("Debug:The file name: %s, The location: %x, The length: %x\n", s->file_name, location, length);
                }
                
            }
            int mul = length * s->unit_size;
            fseek(f,location,SEEK_SET);
            fread(s->mem_buf, s->unit_size, mul, f);
            if(s->debug_mode=='1'){
                if(s->display_mode=='0'){
                    printf("Loaded %d units into memory\n", mul);
                }
                else
                {
                    printf("Loaded %x units into memory\n", mul);
                }
                
            }
        }
        fclose(f);
    }
}

void Display_Mode(state* s){
    if(s->display_mode=='1'){
        s->debug_mode='0';
        printf("%s","Display flag now off, decimal representation\n");
    }
    else{
        s->display_mode='1';
        printf("%s","Display flag now on, hexadecimal representation\n");
    }
}

void Memory_Display(state* s){
    int u =-1;
    int addr=-1;
    printf("please enter num of units (in decimal)\n");
    scanf("%d", &u);
    fgetc(stdin);
    printf("please enter address (in hexadecimal)\n");
    scanf("%X", &addr);
    fgetc(stdin);

    if(addr==0){
       // for(int i=0; i<u; i = i+ (s->unit_size)){
         //   char *t=+i;
            printf("%x\n",s->mem_buf);
       // }
    }
    
    else{
        char* tmp = addr;
        char* end = tmp + (s->unit_size*u);
        while (tmp < end) {
        if(s->display_mode=='0'){
            fprintf(stdout, "%d\n", tmp);
        }
        else if(s->display_mode=='1'){
            fprintf(stdout, "%X\n", tmp);
        }
        tmp += s->unit_size;
    }
    }
}

void Save_Into_File(state* s){
    int memAddress =-1;
    int target=-1;
    int length=-1;
    printf("please enter source memory address (in hexadecimal)\n");
    scanf("%x", &memAddress);
    fgetc(stdin);
    printf("please enter target file offset (in hexadecimal)\n");
    scanf("%X", &target);
    fgetc(stdin);
    //we need to compute the len of target and the len of the file name
    printf("please enter number of units (in decimal)\n");
    scanf("%d", &length);
    fgetc(stdin);
    printf("%s\n", s->file_name);
    
    FILE* file = fopen(s->file_name,"r+");

    if(file==NULL){
        printf("ERROR: can't open the file!!\n");
    }
    else{
        fseek(file,target,SEEK_SET);

        char* tmp=memAddress; 
        char toCopy[length]; 
        for (int i=0; i < length ;i++ ){
            toCopy[i]=tmp;
            tmp++;
         }
        if(memAddress == 0){
            fwrite(s->mem_buf, s->unit_size, length, file);
        }
        else
        {
            int test = fwrite(toCopy, s->unit_size, length, file);
            printf("%d\n", test);
        }
    }
     fclose(file);
}

void Memory_Modify(state* s){
    int location =-1;
    int val=-1;
    printf("please enter location (in hexadecimal)\n");
    scanf("%x", &location);
    fgetc(stdin);
    printf("please enter new value (in hexadecimal)\n");
    scanf("%x", &val);
    fgetc(stdin);
    if(s->debug_mode=='1'){
         if(s->display_mode=='0'){
                    printf("The location: %d\nThe value: %d\n", location, val);
                }
                else
                {
                    printf("The location: %x\nThe value: %x\n", location, val);
                }
    }
    memcpy(s->mem_buf+location,&val,(s->unit_size));
}

void quit(state* s){
    if(s->debug_mode=='1'){
        printf("quitting\n");
    }
     exit(0);
}

struct fun_desc {
  char *name;
  void (*fun)(state*);
};

struct fun_desc menu[]={{"-Toggle Debug Mode", Debug},
{"-Set File Name", File_Name},{"-Set Unit Size", Unit_Size}, {"-Load Into Memory", Load},
{"-Toggle Display Mode",Display_Mode},{"-Memory_Display",Memory_Display},
{"-Save Into File", Save_Into_File},{"-Memory Modify", Memory_Modify},
{"-Quit",quit},{NULL, NULL}};


int main(int argc, char **argv){
  
    state *s=(state*)malloc(sizeof(state));
    strncpy(s->file_name,"",1);
    s->debug_mode='0';
    s->display_mode='0';
    s->unit_size = 1;
    printf("%c\n",s->debug_mode);
    while(1){

    printf("%s","> menu\n Please Choose action:\n");
    int idx;
    int len = sizeof(menu)/sizeof(menu[0]);
    for (idx=0; idx < len-1; ++idx)
    printf("%d%s\n", idx,menu[idx].name);

    int choose=-1;
    scanf("%d", &choose);
    printf("Option: %d\n",choose);

    if(choose==len-1){
        quit(choose);
    }

    else{
    
    if( choose < 0 || len < choose){
    printf("Not within bounds\n"); 
    exit(0);
        }

        else
        {
        printf("Within bounds\n");
        fgetc(stdin);
        printf("%d\n",choose);
        menu[choose].fun(s);
    }

    }
    }
    return 0;

}
