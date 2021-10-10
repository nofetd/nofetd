all: splflix

splflix: bin/Action.o bin/main.o bin/Session.o bin/User.o bin/Watchable.o
	@echo 'linking'
	g++ -o bin/splflix bin/Action.o bin/main.o bin/Session.o bin/User.o bin/Watchable.o

bin/Action.o: src/Action.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Include -o bin/Action.o src/Action.cpp

bin/main.o: src/main.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Include -o bin/main.o src/main.cpp

bin/Session.o: src/Session.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Include -o bin/Session.o src/Session.cpp

bin/User.o: src/User.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Include -o bin/User.o src/User.cpp

bin/Watchable.o: src/Watchable.cpp
		g++ -g -Wall -Weffc++ -std=c++11 -c -Include -o bin/Watchable.o src/Watchable.cpp

clean:
	@echo 'cleaning'
	rm -f bin/*
