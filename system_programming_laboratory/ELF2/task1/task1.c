#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include "elf.h"
#include <sys/mman.h>
#include <fcntl.h>

#define ELF32_R_SYM(info)             ((info)>>8)
#define ELF32_R_TYPE(info)            ((unsigned char)(info))
#define ELF32_R_INFO(sym, type)       (((sym)<<8)+(unsigned char)(type))

typedef struct {
  char debug_mode;
  char file_name[128];
  int unit_size;
  unsigned char mem_buf[10000];
  size_t mem_count;
  char display_mode;
  
  int fd;
  void *map_start;          /* will point to the start of the memory mapped file */
  struct stat fd_stat;      /* this is needed to  the size of the file */
  Elf32_Ehdr *header;       /* this will point to the header structure */
  int num_of_section_headers;

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
///0
void ELF_File(state* s){
    printf("Please enter file Name\n");
    //fread(s->file_name,100,1,stdin);
    char input[100];
    scanf("%s", &input);
    fgetc(stdin);
    strncpy(s->file_name,input,100); 
    if(s->debug_mode == '1'){
      printf("Debug: file name set to %s\n",s->file_name);
    }
//
   if( (s->fd = open(s->file_name, O_RDWR)) < 0 ) {
      perror("error in open");
      exit(-1);
   }

   if(fstat(s->fd, &s->fd_stat) != 0 ) {
      perror("stat failed");
      exit(-1);
   }

   if ( (s->map_start = mmap(0, s->fd_stat.st_size, PROT_READ | PROT_WRITE , MAP_SHARED, s->fd, 0)) == MAP_FAILED ) {
      perror("mmap failed");
      exit(-4);
   }

   s->header = (Elf32_Ehdr *)s->map_start;
      
    printf("Magic Number: %c %c %c\n", s->header->e_ident[0], s->header->e_ident[1], s->header->e_ident[2]);    //Bytes 1,2,3 of the magic number (in ASCII).
                                           // Henceforth, you should check that the number is consistent with an ELF file,
                                            // and refuse to continue if it is not.?
                                             
   if(s->header->e_ident[5] == ELFDATANONE){          // The data encoding scheme of the object file.
       printf("Data: %s\n", "Unknown data format");
   }
   else if(s->header->e_ident[5] == ELFDATA2LSB){
       printf("Data: %s\n", "Two's complement, little-endian");
   }
   else if(s->header->e_ident[5] == ELFDATA2MSB){
       printf("Data: %s\n", "Two's complement, big-endian");
   }
   printf("Entry point address: %x\n", s->header->e_entry);        // Entry point (hexadecimal) 
   // ?? address.
                                
   printf("Start of program headers: %d\n", s->header->e_phoff);        //The file offset in which the section header table resides.
   
   printf("Number of section headers: %d\n", s->header->e_shnum);        // The number of section header entries.

   printf("Size of section headers: %d\n", s->header->e_shentsize);     // The size of each section header entry.

   printf("Start of section headers: %d\n", s->header->e_shoff);        // The file offset in which the program header table resides.

   printf("Number of program headers: %d\n", s->header->e_phnum);        // The number of program header entries.

   printf("Size of program headers: %d\n", s->header->e_phentsize);    // The size of each program header entry. 
} 

void Print_Name(state* s){ 
    Elf32_Shdr *sec = (Elf32_Shdr *)((char *)s->map_start + (s->header->e_shoff));
    Elf32_Shdr *sh_strtab = &sec[s->header->e_shstrndx];
    const char *const sh_strtab_p = s->map_start + sh_strtab->sh_offset;
    for (int i = 0; i < s->header->e_shnum; i++) {
    printf("[%2d]\t %4s\t %16x\t %4x\t %4x\t %4d\n", i, sh_strtab_p + sec[i].sh_name, sec[i].sh_addr, sec[i].sh_offset, sec[i].sh_size, sec[i].sh_type);
  }
}

void Print_Symbols(state* s){
    Elf32_Shdr *sections = (Elf32_Shdr *)((char *)s->map_start + (s->header->e_shoff));
    Elf32_Sym *symtab;
    //char* section_name;
    char *sh_strtab;
    int num;
    char *symbol_names;
    for (int i = 0; i < s->header->e_shnum ; i++){
        if (sections[i].sh_type == SHT_SYMTAB || sections[i].sh_type == SHT_DYNSYM ) {
            symtab = (Elf32_Sym *)(((char *)s->map_start) + sections[i].sh_offset);
            num = sections[i].sh_size / sections[i].sh_entsize;
            symbol_names= (char *)(s->map_start + sections[sections[i].sh_link].sh_offset);
            }
    }
    
    for(int j=0; j<num; j++){
        Elf32_Shdr *sh_strtab = &sections[s->header->e_shstrndx];
        const char *const sh_strtab_p = s->map_start + sh_strtab->sh_offset;
     
        if(symtab[j].st_shndx == SHN_ABS){
         printf("[%2d]\t %8x\t %s\t %s\n", j, symtab[j].st_value ,"ABS", symbol_names + symtab[j].st_name);
        }
        else if(symtab[j].st_shndx == 0){
          printf("[%2d]\t %8x\t %s\t %s\n", j, symtab[j].st_value ,"UND", symbol_names + symtab[j].st_name); 
        }
        else{
        char* name = sh_strtab_p + sections[symtab[j].st_shndx].sh_name;
        printf("[%2d]\t %8x\t %d\t %s\t %s\n", j, symtab[j].st_value ,symtab[j].st_shndx, symbol_names + symtab[j].st_name, name);
         }
    }
}

void quit(state* s){
    if(s->debug_mode=='1'){
        printf("quitting\n");
    }
     munmap(s->map_start, s->fd_stat.st_size);
     exit(0);
}

void Relocation_Tables(state* s){
    Elf32_Shdr *sections = (Elf32_Shdr *)((char *)s->map_start + (s->header->e_shoff));
    Elf32_Rel *Rel;
    Elf32_Sym *symtab;
    char *symbol_names;
    int num;
    int info_1;
    int info_2;
    for (int i = 0; i < s->header->e_shnum ; i++){
        if (sections[i].sh_type == SHT_REL) {
            Rel = (Elf32_Rel *)(((char *)s->map_start) + sections[i].sh_offset);
            num = sections[i].sh_size / sections[i].sh_entsize;
        }
      if (sections[i].sh_type == SHT_SYMTAB || sections[i].sh_type == SHT_DYNSYM ) {
            symtab = (Elf32_Sym *)(((char *)s->map_start) + sections[i].sh_offset);
            symbol_names= (char *)(s->map_start + sections[sections[i].sh_link].sh_offset);
            }
    }
    printf("done\n");
    for(int i=0; i<num; i++)
    {
            info_1 = Rel[i].r_info;
            info_2 = Rel[i].r_info;
            ELF32_R_SYM(info_1);
            ELF32_R_TYPE(info_2);
        printf("%x\t %x %x %s\n", Rel[i].r_offset, Rel[i].r_info ,symtab[i].st_value, symbol_names + symtab[i].st_name);
    }
}

struct fun_desc {
  char *name;
  void (*fun)(state*);
};

struct fun_desc menu[]={
{"-Toggle Debug Mode", Debug},
{"-Examine ELF File", ELF_File},         
{"-Print Section Names", Print_Name},    
{"-Print Symbols", Print_Symbols},
{"-Relocation Tables", Relocation_Tables},       
{"-Quit",quit},{NULL, NULL}};


int main(int argc, char **argv){
  
    state *s=(state*)malloc(sizeof(state));
    strncpy(s->file_name,"",1);
    s->debug_mode='0';
    s->display_mode='0';
    s->unit_size = 1;
    while(1){
    printf("%s","\n> menu\nPlease Choose action:\n");
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



/*void File_Name(state* s){
    printf("%s","Please enter file Name\n");
    fread(s->file_name,1,100,stdin); 
    //char input[100];
    //fgets(input, 100, stdin);
    //strncpy(s->file_name,input,100);
    if(strncmp(s->debug_mode,'1',1)==0){
    printf("Debug: file name set to %s",s->file_name);
}       
 }*/

 /*
        if(sections[i].sh_type==SHT_STRTAB && i==sections[i].sh_link) {
            
            Elf32_Shdr *sh_sectionStrTbl_p;
            char *sh_sectionStrTbl;
            sh_sectionStrTbl_p=&sections[s->header->e_shoff];
            sh_sectionStrTbl=s->map_start + sh_sectionStrTbl_p->sh_offset;
            printf("%s\n", sh_sectionStrTbl);
            
            printf("test1!!\n");
            char *tmp = sections[i].sh_name+sh_sectionStrTbl;
            printf("%s\n", tmp);
            if(strcmp(tmp , ".strtab")) {
              printf("test2!!\n"); 
             
            Elf32_Shdr *sh_strtab_p=&sections[i];
            sh_strtab=(char*) s->map_start+sh_strtab_p->sh_offset;
            numSectionsFound++;
            //}
        }
        */