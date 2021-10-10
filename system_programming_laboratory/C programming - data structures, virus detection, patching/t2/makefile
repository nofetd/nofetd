all: Task1

Task1: task2b.o
	gcc -m32 -g -Wall -o Task1 task2b.o

task2b.o: task2b.c
	gcc -m32 -g -Wall -c -o task2b.o task2b.c


.PHONY: clean

clean:
	rm -rf ./*.o task2b



