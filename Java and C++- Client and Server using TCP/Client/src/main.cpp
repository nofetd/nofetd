#include <iostream>
#include <vector>
#include <sstream>
#include <thread>
#include <client.h>
#include "../include/CommandProtocol.h"
#include <string>
#include <vector>
#include <unordered_set>

#include <ServerProtocol.h>
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
using namespace std;

int main() {

    bool begin=false;
    client *client1;
    while(!begin) {
        string phrase;
        string word;
        getline(std::cin, phrase);
        stringstream ss(phrase);
        vector<string> words;

        while (getline(ss, word, ' ')) {
            words.push_back(word);
        }
        if (words.at(0).compare("login") == 0) {
            begin=true;
            string hostAndPort = words.at(1);
            size_t location = hostAndPort.find(':');
            string host = hostAndPort.substr(0, location);
            string port = hostAndPort.substr(location + 1);
            short port_ = stoi(port);
            string user_name = words.at(2);
            string pascode = words.at(3);
            client1 = new client(user_name, pascode, host, port_);
            bool connect = client1->connect();
            if (connect) {
                CommandProtocol KeyBord(client1);
                KeyBord.Login(words);
                ServerProtocol server(client1);
                thread servMsg(&ServerProtocol::run, &server);
                thread comand(&CommandProtocol::run, &KeyBord);
                servMsg.join();
                comand.join();
            }
        }
    }
    delete(client1);
    return 0;
}


/*login 127.0.0.1:3445 alic bob*/