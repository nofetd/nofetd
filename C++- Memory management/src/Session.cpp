#include "../Include/User.h"
#include <string>
#include "../Include/Session.h"
#include "../Include/Watchable.h"
#include "../Include/Session.h"
#include <iostream>
#include <fstream>
#include <cstring>
#include "../Include/json.hpp"

using namespace std;
using json = nlohmann::json;

Session::Session(const std::string &configFilePath):content(vector<Watchable*>()),actionsLog(vector<BaseAction*>()),userMap(unordered_map<string,User*>()),activeUser(),currAction(""),recomandtion() {
    LengthRecommenderUser *Default = new LengthRecommenderUser("default");
    this->set_activeUser(Default);
    userMap.insert(make_pair(activeUser->getName(),activeUser));
    std:: ifstream js(configFilePath);
    json for_Content;
    js >> for_Content;

    long ID=1;

    for(unsigned int i=0; i<for_Content["movies"].size(); i++){
        string name=for_Content["movies"][i]["name"];
        std::vector<std::string> tags;
        for (unsigned int k=0;k < for_Content["movies"][i]["tags"].size();k++){
            tags.push_back(for_Content["movies"][i]["tags"][k]);
        }
        int len=for_Content["movies"][i]["length"];
        Watchable *movie=new Movie(ID, name, len, tags);
        this->content.push_back(movie);
        ID++;
    }

    for (unsigned int i=0; i< for_Content["tv_series"].size();i++){
        string name=for_Content["tv_series"][i]["name"];
        std::vector<std::string> tags;
        for (unsigned int k=0; k<for_Content["tv_series"][i]["tags"].size();k++){
            tags.push_back(for_Content["tv_series"][i]["tags"][k]);
        }
        int len=for_Content["tv_series"][i]["episode_length"];
        for (unsigned int j=0; j<for_Content["tv_series"][i]["seasons"].size();j++){
            for (int w=0;w<for_Content["tv_series"][i]["seasons"][j];w++){
                Watchable *episode=new Episode(ID, name, len,j+1,w+1, tags);
                this->content.push_back(episode);
                ID++;
            }
        }
    }
}

Session::~Session() {
this->clear();
}

//copy constructor
Session::Session(const Session &other) :content(vector<Watchable*>()),actionsLog(vector<BaseAction*>()),userMap(unordered_map<string,User*>()),activeUser(),currAction(""),recomandtion() {
    string name=other.activeUser->getName();
    activeUser=other.activeUser->clone(name);
    for(pair<string,User*> it: other.get_userMap())
    {
        this->AddToUsers(it.second->clone(it.first));
    }
    for(unsigned int i=0; i<other.actionsLog.size(); i++)
    {
        this->actionsLog.push_back(other.actionsLog.at(i)->clone());
    }
    for (unsigned int i=0; i< other.content.size();i++){
        this->content.push_back(other.content.at(i)->clone());
    }
}
//move constructor
Session::Session(Session &&o):content(vector<Watchable*>()),actionsLog(vector<BaseAction*>()),userMap(unordered_map<string,User*>()),activeUser(),currAction(""),recomandtion() {
    userMap.swap(o.userMap);
    content.swap(o.content);
    actionsLog.swap(o.actionsLog);
    activeUser=o.activeUser;
    o.set_activeUser(nullptr);
}

//copy assignment
Session &Session::operator=(const Session &other) {
   if(this!= &other){
       clear();
       int size=other.get_actionsLog().size();
       for(int i=0;i<size;i++){
           BaseAction* base=other.get_actionsLog()[i]->clone();
           this->actionsLog.push_back(base);
       }
       size=other.get_content().size();
       for(int i=0;i<size;i++){
           Watchable* Watch=other.get_content()[i]->clone();
           this->content.push_back(Watch);
       }
       for(pair<string,User*> it: other.get_userMap())
       {
           this->AddToUsers(it.second->clone(it.first));
       }

       string name=other.get_activeUser()->getName();
       auto it=other.get_userMap().find(name);
       activeUser=(*it).second;
   }
   return *this;
}

//move assignment operator
Session &Session::operator=(Session &&other) {
    if(this != &other)
    {
        this->clear();
        userMap.swap(other.userMap);
        content.swap(other.content);
        actionsLog.swap(other.actionsLog);
        activeUser=other.activeUser;
    }
    return *this;
}
void Session :: clear(){
    for (unsigned int i=0;i<actionsLog.size();i++){
        delete(actionsLog[i]);
        actionsLog[i]= nullptr;
    }
    actionsLog.clear();
    for(Watchable* watch: content){
        delete(watch);
        watch= nullptr;
    }
    content.clear();
    for(unordered_map<string,User*>::iterator it=userMap.begin();it!=userMap.end();it++){
        delete(it->second);
        it->second= nullptr;
    }
    userMap.clear();
    activeUser= nullptr;
}
void Session::start() {
    cout << "SPLFLIX is now on!" << endl;
    bool active = true;
    while (active) {
        string inputAct;
        getline(cin,inputAct);

        this->currAction = inputAct;
        if (inputAct.substr(0, 10).compare("createuser") == 0) {
            CreateUser *create = new  CreateUser();
            create->act(*this);
            this->actionsLog.push_back(create);
        }
else
        if (inputAct.substr(0, 10).compare("changeuser") == 0) {
            ChangeActiveUser *change=new  ChangeActiveUser();
            change->act(*this);
            this->actionsLog.push_back(change);
        }
else
        if (inputAct.substr(0, 10).compare("deleteuser") == 0) {
            DeleteUser *Delete = new  DeleteUser();
            Delete->act(*this);
            this->actionsLog.push_back(Delete);
        }
else
        if (inputAct.substr(0, 7).compare("dupuser") == 0) {
            DuplicateUser *Dupli=new DuplicateUser();
            Dupli->act(*this);
            this->actionsLog.push_back(Dupli);
        }
else
        if (inputAct.compare("content") == 0) {
            PrintContentList *Print= new PrintContentList();
            Print->act(*this);
            this->actionsLog.push_back(Print);
        }
else
        if (inputAct.compare("watchhist") == 0) {
            PrintWatchHistory *PrintW= new PrintWatchHistory() ;
            PrintW->act(*this);
            this->actionsLog.push_back(PrintW);
        }
else
        if (inputAct.substr(0, 5).compare("watch") == 0) {
            bool continu = true;
            while (continu) {
                Watch *W = new Watch() ;
                W->act(*this);
                cout << "We recommend watching " + this->get_recomandtion()->toString().substr(3,this->get_recomandtion()->toString().size()) + ", continue watching? [y/n]"
                     << endl;
                string ans;
                cin >> ans;
                if (ans == "n") {
                    continu = false;
                }
                if (ans == "y") {
                    string curr="watch ";
                    curr += std::to_string(this->get_recomandtion()->get_id()) ;
                    this->currAction =curr;this->get_recomandtion()->get_id();
                }
                this->actionsLog.push_back(W);

            }
        }
else
        if (inputAct.compare("log") == 0) {
            PrintActionsLog *PrintA=new PrintActionsLog() ;
            PrintA->act(*this);
            this->actionsLog.push_back(PrintA);
        }
else
        if (inputAct.compare("exit") == 0) {
            Exit *exit =new Exit();
            exit->act(*this);
            this->actionsLog.push_back(exit);
            active = false;
        }
    }
}

Watchable *Session::get_recomandtion() {
    return this->recomandtion;
}

void Session::set_recomandtion(Watchable *rec) {
    this->recomandtion=rec;
}

std::string Session::get_currAction() {
    return this->currAction;
}

User * Session::get_activeUser() const {
    return this->activeUser;
}

void Session::set_activeUser(User *other) {
    this->activeUser = other;
}

std::vector<BaseAction *> Session::get_actionsLog() const {
    return this->actionsLog;
}

std::unordered_map<std::string, User *> Session::get_userMap() const {
    return this->userMap;
}

void Session::AddToUsers(User *u) {
    pair<std::string, User*> p (u->getName(),u);
    this->userMap.insert(p);
}

std::vector<Watchable *> Session::get_content() const {
    return this->content;
}

void Session::deleteUser(string &name) {
   this->userMap.erase(name);
}
