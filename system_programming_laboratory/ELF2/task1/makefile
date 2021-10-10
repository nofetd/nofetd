all: lab8

lab8: task1.o
	gcc -m32 -g -Wall -o lab8 task1.o

task1.o: task1.c
	gcc -m32 -g -Wall -c -o task1.o task1.c


.PHONY: clean

clean:
	rm -rf ./*.o task1



