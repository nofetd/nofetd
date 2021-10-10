all: Task1

Task1: task1c.o
	gcc -m32 -g -Wall -o Task1 task1c.o

task1c.o: task1c.c
	gcc -m32 -g -Wall -c -o task1c.o task1c.c


.PHONY: clean

clean:
	rm -rf ./*.o task1c



