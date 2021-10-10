#ifndef SESSION_H_
#define SESSION_H_
#include <vector>
#include <unordered_map>
#include <string>
#include "Action.h"
#include "../Include/Watchable.h"
#include "../Include/json.hpp"
#include <fstream>
#include <cstring>
#include <string>
#include <iostream>

using json = nlohmann::json;

class User;
class Watchable;

class Session{
public:
    Session(const std::string &configFilePath);
    ~Session();
    void start();
    Session(const Session& other);                    //copy constructor
    Session(Session&& other);               //move constructor
    Session& operator= (const Session& other);        //copy assignment
    Session& operator= (Session&& other) ;    //move assignment
    void clear();
    std::vector<Watchable*> get_content() const;
    std::unordered_map<std::string,User*> get_userMap() const ;
    std::string get_currAction();
    User * get_activeUser() const;
    void set_activeUser(User *other);
    Watchable* get_recomandtion() ;
    void set_recomandtion(Watchable *rec);
    std::vector<BaseAction*> get_actionsLog() const;
    void AddToUsers(User *u);
    void deleteUser(std::string &name);
private:
    std::vector<Watchable*> content;
    std::vector<BaseAction*> actionsLog;
    std::unordered_map<std::string,User*> userMap;
    User* activeUser;
    std::string currAction;
    Watchable* recomandtion;
    //vector<Watchable *> get_content();
};
#endif

