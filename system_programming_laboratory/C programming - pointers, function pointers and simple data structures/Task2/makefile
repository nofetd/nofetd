all: Task3

Task3: menu_map.o
	gcc -m32 -g -Wall -o Task3 menu_map.o

menu_map.o: menu_map.c
	gcc -m32 -g -Wall -c -o menu_map.o menu_map.c


.PHONY: clean

clean:
	rm -rf ./*.o menu_map


