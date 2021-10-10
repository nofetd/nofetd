#ifndef USER_H_
#define USER_H_

#include <vector>
#include <string>
#include "../Include/Session.h"
#include <unordered_set>
#include <unordered_map>
#include "sstream"
#include <sstream>


class User{
public:
    User(const std::string& name);
    virtual ~User();
    virtual Watchable* getRecommendation(Session& s) = 0;
    virtual User* clone(std::string &name)=0;
    std::string getName() const;
    void AddToHistory(Watchable *Watch);
    std::vector<Watchable*> get_history() const;
    bool Search(vector<Watchable*> s,Watchable *wa);
protected:
    std::vector<Watchable*> history;
private:
    const std::string name;

};


class LengthRecommenderUser : public User {
public:
    LengthRecommenderUser(const std::string& name);
    virtual Watchable* getRecommendation(Session& s);
    virtual User* clone(std::string &name);
    ;
private:
};

class RerunRecommenderUser : public User {
public:
    RerunRecommenderUser(const std::string& name);
    virtual Watchable* getRecommendation(Session& s);
    virtual User* clone(std::string &name);

private:
    int current;
};

class GenreRecommenderUser : public User {
public:
    GenreRecommenderUser(const std::string& name);
    virtual Watchable* getRecommendation(Session& s);
    virtual User* clone(std::string &name);
private:
};

#endif

