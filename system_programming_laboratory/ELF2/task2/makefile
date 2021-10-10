all: lab8

lab8: task2.o
	gcc -m32 -g -Wall -o lab8 task2.o

task2.o: task2.c
	gcc -m32 -g -Wall -c -o task2.o task2.c


.PHONY: clean

clean:
	rm -rf ./*.o task2



