CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system
# All Targets
all: main
	g++ -pthread -o  bin/StompBookClubClient bin/client.o bin/CommandProtocol.o bin/connectionHandler.o bin/main.o bin/ServerProtocol.o $(LDFLAGS)
# Tool invocations
# Executable "main" depends on the files main.o
main: bin/client.o bin/CommandProtocol.o bin/connectionHandler.o bin/main.o bin/ServerProtocol.o


# Depends on the source and header files
bin/client.o: src/client.cpp 
	g++ -pthread $(CFLAGS) -o bin/client.o src/client.cpp

# Depends on the source and header files
bin/CommandProtocol.o: src/CommandProtocol.cpp 
	g++ -pthread $(CFLAGS) -o bin/CommandProtocol.o src/CommandProtocol.cpp

# Depends on the source and header files
bin/connectionHandler.o: src/connectionHandler.cpp
	 g++ -pthread $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

# Depends on the source and header files
bin/main.o: src/main.cpp 
	g++ -pthread $(CFLAGS) -o bin/main.o src/main.cpp

# Depends on the source and header files
bin/ServerProtocol.o: src/ServerProtocol.cpp 
	g++ -pthread $(CFLAGS) -o bin/ServerProtocol.o src/ServerProtocol.cpp

#Clean the build directory
clean:
	rm -f bin/*

