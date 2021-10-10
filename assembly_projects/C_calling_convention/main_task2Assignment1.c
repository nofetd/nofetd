#include <stdio.h>
#include <stdlib.h>
#define	MAX_LEN 34			/* maximal input string size */
					/* enough to get 32-bit string + '\n' + null terminator */
extern int convertor(char* buf);

int main(int argc, char** argv)
{
  char buf[MAX_LEN];
  while(1){
  //fprint("please enter a number: \n");
  fgets(buf, MAX_LEN, stdin);/* get user input string */
  if (buf[0]=='q')
      exit(0);
  convertor(buf);			/* call your assembly function */
  }
  return 0;
}
