#include <iostream>
#include "../Include/Watchable.h"
#include "../Include/Watchable.h"
#include "../Include/Session.h"
#include "../Include/User.h"
#include "../Include/Action.h"

using namespace std;

Watchable::Watchable(long id, int length, const std::vector<std::string> &tags) :id(id),length(length),tags(vector<std::string>()){
    for(auto tag=tags.begin(); tag!=tags.end();tag++){
       std::string tmp=(*tag);
        this->tags.push_back(tmp);
    }
}

int Watchable::getLength() const {
    return length;
}

std::vector<std::string> Watchable::get_tags() const {
    return tags;
}

 long Watchable::get_id() const {
    return this->id;
}

Watchable::~Watchable() {
tags.clear();
}

std::string Watchable::get_Name() const {
    return "";
}

int Watchable::getsesson() const {
    return 0;
}

int Watchable::getepisode() const {
    return 0;
}

Movie::Movie(long id, const std::string &name, int length, const std::vector<std::string> &tags): Watchable(id, length, tags),name(name) {
}

std::string Movie::toString() const {
    string ans;
    ans += std::to_string(this->get_id())+ ". " + this->name;
    return ans ;
}
std::string Movie::get_information() const {
    string ans;
    ans += std::to_string(this->get_id()) +". "+ this->name +" "+std::to_string(this->getLength()) +" minutes [";
    for (unsigned int i=0; i<this->get_tags().size();i++){
        ans+=(this->get_tags().at(i)) + ",";
    }
    ans=ans.substr(0,ans.size()-1);
    ans += + "]";
    return ans;
}

Watchable *Movie::getNextWatchable(Session &s) const {
    return s.get_activeUser()->getRecommendation(s);
}

std::string Movie::get_Name() const {
    return this->name;
}

int Movie::get_Episode() {
    return 0;
}

int Movie::getsesson() const {
    return Watchable::getsesson();
}

int Movie::getepisode() const {
    return Watchable::getepisode();
}

Watchable *Movie::clone() {
    return new Movie(*this);
}

Movie::~Movie() {
name.clear();
}

Episode::Episode(long id, const std::string &seriesName, int length, int season, int episode,
                 const std::vector<std::string> &tags): Watchable(id, length, tags),seriesName(seriesName),season(season),episode(episode),nextEpisodeId(id+1) {
}

int Episode::getsesson() const {
    return this->season;
}

int Episode::getepisode() const {
    return this->episode;
}

long Episode::getnextEpisodeId() const {
    return this->nextEpisodeId;
}

std::string Episode::toString() const {
   string ans;
   ans +=std::to_string(this->get_id()) +". "+ this->seriesName+" S"+std::to_string(this->getsesson()) +"E"+ std::to_string(this->getepisode());
           return ans;
}
std::string Episode::get_information() const {
    string ans;
    ans +=  this->toString() +" "+ std::to_string(this->getLength()) +" minutes [";
    for (unsigned int i=0; i<this->get_tags().size();i++) {
        ans += (this->get_tags().at(i)) + ",";
    }
    ans=ans.substr(0,ans.size()-1);
    ans += + "]";
    return ans;
}

Watchable *Episode::getNextWatchable(Session &s) const {
    Watchable *next=nullptr ;
    bool found=false;
    for (unsigned int i=0;i<s.get_content().size()&&!found;i++){
        if(s.get_content().at(i)->get_Name()==this->get_Name()&&s.get_content().at(i)->get_id()==this->getnextEpisodeId())
        {
            next=s.get_content().at(i);
            found= true;
        }
    }
    if (!found){
        next=s.get_activeUser()->getRecommendation(s);
    }
    return  next;
}

std::string Episode::get_Name() const {
    return this->seriesName;
}

int Episode::get_Episode() const {
    return this->episode;
}

Watchable *Episode::clone() {
    return new Episode(*this);
}

Episode::~Episode() {
seriesName.clear();
}
