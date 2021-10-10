//
// Created by nofet@wincs.cs.bgu.ac.il on 14/01/2020.
//
#ifndef UNTITLED_COMMANDPROTOCOL_H
#define UNTITLED_COMMANDPROTOCOL_H
#include "client.h"
#include "../include/client.h"
#include <string>
#include <vector>
#include <map>

using  namespace std;

class CommandProtocol{
public:
    CommandProtocol(client* client);
    string Login(vector<std::string> &v);
    string Join(vector<std::string> &v);
    string Add(vector<std::string> &v);
    string Borrow(vector<std::string> &v);
    string Return(vector<std::string> &v);
    string Status(vector<std::string> &v);
    string Logout(vector<std::string> &v);
    string Exit(vector<std::string> &v);
    vector<string> split(string &Msg);
    void run();
    void CreateMessege(string *s);
    void logout();

private:
    client *Client;
    bool terminate;
};

#endif //UNTITLED_COMMANDPROTOCOL_H