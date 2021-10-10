#include <stdlib.h>
#include <stdio.h>
#include <string.h>

char censor(char c);
char encrypt(char c);
char decrypt(char c);
char dprt(char c);
char cprt(char c);
char my_get(char c);
char quit(char c);
char* map(char *array, int array_length, char (*f) (char));

char censor(char c)
{
    if (c == '!')

        return '.';
    else
        return c;
}

char encrypt(char c)
{
    if (c < '~' && ' ' < c)
    {
        return c + 3;
    }
    else
        return c;
}

char decrypt(char c)
{
    if (c < '~' && ' ' < c)
    {
        return c - 3;
    }
    else
        return c;
}

char dprt(char c)
{
    printf("%d\n", c);
    return c;
}

char cprt(char c)
{
    if (c < '~' && ' ' < c)
    {
        printf("%c\n", c);
        return c;
    }
    else
        printf("%c\n", '.');
    return c;
}

char my_get(char c)
{
    char ret = getc(stdin);
    return ret; 
}

char quit(char c){
    if(c=='q'){
     printf("%s\n","exit code with 0");
     exit(0);
    }
    else 
    return c;
}

char *map(char *array, int array_length, char (*f)(char))
{
    char *mapped_array = (char *)(malloc(array_length * sizeof(char)));
    int idx;
    for (idx = 0; idx < array_length; ++idx)
    {
        mapped_array[idx] = f(array[idx]);;
    }
    return mapped_array;
}

struct fun_desc {
  char *name;
  char (*fun)(char);
};

struct fun_desc menu[]={{"Cencer", censor},
{"Encrypt", encrypt},{"Decrypt",decrypt},
{"Print dec", dprt},{"Print string",cprt},
{"Get string",my_get},{"Quit",quit}};

int main(int argc, char **argv){

char* tmpArr;
char *carray = (char *)(calloc(5,5 * sizeof(char)));
char* carrayPtr;
for(carrayPtr=carray; carrayPtr < carray+5; ++carrayPtr){
        *carrayPtr=' ';
    }

while(1){

printf("%s","> menu\n Please choose a function:\n");
int idx;
for (idx=0; idx< 7; ++idx)
printf("%d%c %s\n", idx, ')',menu[idx].name);

char choose=' ';
choose=scanf ("%c", &choose);
printf("Option: %c\n",choose);

if(choose=='q'){
    free(carray);
    quit(choose);
   }

else{
   
   if( choose < '0'||'6' < choose){
   printf("Not within bounds\n");
   free(carray);  
   quit(choose);
    }

    else
    {
    printf("Within bounds\n");
    fgetc(stdin);
    const char *c=&choose;
    int index=atoi(c);
    tmpArr=map(carray, 5, menu[index].fun);
    int i;
    for(i=0; i < 5; ++i){
        carray[i]=tmpArr[i];
    }
    free(tmpArr);
    }
}

}
return 0;
}

/*int i;
for(i=0; i < 5; ++i){
       printf("%c\n",carray[i]);
    }
    // for(i=0; i < 5; ++i){
       //printf("%c",carray[i]);
   // }
   // printf("\n");

   for(i=0; i < 5; ++i){
       printf("%c",carray[i]);
    }
    
*/