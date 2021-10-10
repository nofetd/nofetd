all: lab8

lab8: task3.o
	gcc -m32 -g -Wall -o lab8 task3.o

task3.o: task3.c
	gcc -m32 -g -Wall -c -o task3.o task3.c


.PHONY: clean

clean:
	rm -rf ./*.o task3



