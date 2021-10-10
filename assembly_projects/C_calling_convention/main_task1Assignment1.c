#include <stdio.h>
#include <stdlib.h>
#define	MAX_LEN 34			/* maximal input string size */
					/* enough to get 32-bit string + '\n' + null terminator */
extern void assFunc(int x,int y);

char c_checkValidity(int x,int y){
    if (x<y){
        return '0';
    }
    return '1';
}

int main(int argc, char** argv)
{
    char input[MAX_LEN];
    int x,y;
    fgets(input,MAX_LEN,stdin);
    sscanf(input,"%d",&x);
    fgets(input,MAX_LEN,stdin);
    sscanf(input,"%d",&y);
    assFunc(x,y);
    return 0;
}
