#include <iostream>
#include "../Include/Session.h"
#include "sstream"
#include <sstream>
#include <algorithm>
#include "../Include/User.h"

using namespace std;

User::User(const std::string &name):history(vector<Watchable*>()),name(name) {

}

std::string User::getName() const {
    return this->name;
}

bool User::Search(vector<Watchable*> s ,Watchable *wa) {
    for (auto it= s.cbegin(); it!=s.cend();it++) {
        if(*it == wa)
            return true;
    }
    return false;
}

std::vector<Watchable *> User::get_history() const {
    return this->history;
}

void User::AddToHistory(Watchable *Watch) {
    this->history.push_back(Watch);
}

User::~User() {
    history.clear();
}

LengthRecommenderUser::LengthRecommenderUser(const std::string &name) : User(name) {
}

Watchable *LengthRecommenderUser::getRecommendation(Session &s) {

    int index = 0;
    bool find = false;
    Watchable *temp;
    double average=0;
    int sum =0;
    int num =0;
    for (unsigned int i=0; i<this->get_history().size();i++)
    {
        sum = sum +(this->get_history().at(i)->getLength());
        num++;
    }
    average = (double)sum/num;
    while(!find)
    {
        for (unsigned int i=0; i<s.get_content().size()&&!find;i++){
            temp = s.get_content().at(i);
            int k=(average)-(temp->getLength());
            if(abs(k)==index && !Search(this->get_history(),temp)) {
                find = true;
            }
        }
        index++;
    }
    return temp;
    }

User *LengthRecommenderUser::clone(std::string &name) {
    LengthRecommenderUser *copy=new  LengthRecommenderUser(name);
    for (unsigned int i=0; i<this->history.size();i++){
        copy->history.push_back(history.at(i)->clone());
    }
    return copy;
}

RerunRecommenderUser::RerunRecommenderUser(const std::string &name) : User(name),current(0) {
}

Watchable *RerunRecommenderUser::getRecommendation(Session &s) {
    Watchable *tmp;
    Watchable *ans;
    if(current== 0){
        tmp=*this->get_history().begin();
        current=tmp->get_id();
        ans=tmp;
    }
    else {
        int size=this->get_history().size();
         Watchable *end= this->get_history()[size-1];
        if (end != nullptr && current ==end->get_id()) {
            tmp =*this->get_history().begin();
            current = tmp->get_id();
            ans=tmp;
        }
        else {
            bool found = false;
            for (unsigned int i = 0; i < this->get_history().size() && !found; i++) {
                if (this->get_history().at(i)->get_id() == current) {
                    current = this->get_history().at(i + 1)->get_id();
                    ans = this->get_history().at(i + 1);
                    found = true;
                }
            }
        }
    }

    return  ans;
}

User *RerunRecommenderUser::clone(std::string &name) {
    RerunRecommenderUser *copy=new RerunRecommenderUser (name);
    for (unsigned int i=0; i<this->history.size();i++){
        copy->history.push_back(history.at(i)->clone());
    }
    return copy;
}


GenreRecommenderUser::GenreRecommenderUser(const std::string &name) : User(name) {
}

Watchable *GenreRecommenderUser::getRecommendation(Session &s) {
    vector<pair<int,std::string>> TagsCounter;
    Watchable *temp= nullptr;
    for (unsigned int i=0; i<this->get_history().size();i++)
    {
        temp=this->get_history().at(i);
        for (unsigned int j=0; j<temp->get_tags().size();j++)
        {
            bool exist=false;
            for (unsigned int k=0; k<TagsCounter.size()&&!exist;k++) {
                if (TagsCounter.at(k).second == (temp->get_tags().at(j))) {
                    TagsCounter.at(k).first++;
                    exist = true;
                }
            }
                if(!exist){
                pair<int,std::string> a(1,temp->get_tags().at(j));
                TagsCounter.push_back(a);
                }
            }
        }
   sort(TagsCounter.begin(),TagsCounter.end());

    bool found=false; ////???
    int index=TagsCounter.size()-1;
            for (unsigned int i=0; i<s.get_content().size()&&!found; i++) {
                for (unsigned int k =0; k< (s.get_content().at(i))->get_tags().size()&&!found; k++) {
                    if ((s.get_content().at(i))->get_tags().at(k)== (TagsCounter.at(index)).second && !Search(this->get_history(), s.get_content().at(i))) {
                        return s.get_content().at(i);
                    }
                }
        }
    return temp;
}

User *GenreRecommenderUser::clone(std::string &name) {
    GenreRecommenderUser *copy=new GenreRecommenderUser(name);
    for (unsigned int i=0; i<this->history.size();i++){
        copy->history.push_back(history.at(i)->clone());
    }
    return  copy;
}