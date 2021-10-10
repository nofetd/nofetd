//
// Created by nofet@wincs.cs.bgu.ac.il on 14/01/2020.
//

#ifndef UNTITLED_SERVERPROTOCOL_H
#define UNTITLED_SERVERPROTOCOL_H
#include <map>
#include "connectionHandler.h"
#include "client.h"
#include <string>
#include <vector>




class ServerProtocol {
public:
    ServerProtocol( client* client);
    void run();
    void process(string &s);
    vector<string> Split(string s, string delimiter);
    void Connected(vector<string> &v);
    void Receipt(vector<string> &v);
    void Message(vector<string> &v);
    void Error(vector<string> &v);
private:
    client *Client;
    bool terminate;
    bool connected;
};


#endif //UNTITLED_SERVERPROTOCOL_H
