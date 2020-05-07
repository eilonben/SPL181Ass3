CFLAGS:= -g -Wall -Weffc++ -std=c++11 -I -I./include

IFLAGS	= -I./usr/include/boost/ 

LFLAGS	= -L./boost/stage/lib/ -lboost_system -lboost_thread

Objects = bin/ConnectionHandler.o bin/BBclient.o

all: bin/BBclient

bin/BBclient: $(Objects)
	g++ -o bin/BBclient $(Objects) $(IFLAGS) $(LFLAGS)

bin/ConnectionHandler.o: src/ConnectionHandler.cpp include/ConnectionHandler.h 
	g++ $(CFLAGS) -c -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/BBclient.o: src/BBclient.cpp
	g++ $(CFLAGS) -c -o bin/BBclient.o src/BBclient.cpp

clean:
	rm -f bin/*
