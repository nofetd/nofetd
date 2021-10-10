#ifndef WATCHABLE_H_
#define WATCHABLE_H_

#include <string>
#include <vector>
#include "../Include/Session.h"

using namespace std;


class Watchable{
public:
    Watchable(long id, int length, const std::vector<std::string>& tags);
    virtual ~Watchable();
    virtual std::string toString() const = 0;
    virtual Watchable* getNextWatchable(Session&) const = 0;
    virtual std:: string get_information() const=0;
    int getLength() const;
    std::vector<std::string> get_tags() const;
    long get_id() const;
    virtual std::string get_Name() const;
    virtual int getsesson() const;
    virtual int getepisode() const;
    virtual Watchable* clone() = 0;
private:
    const long id;
    int length;
    std::vector<std::string> tags;
};

class Movie : public Watchable{
public:
    Movie(long id, const std::string& name, int length, const std::vector<std::string>& tags);
    ~Movie();
    virtual std::string toString() const;
    virtual Watchable* getNextWatchable(Session&) const;
    virtual std:: string get_information() const;
    virtual std::string get_Name() const ;
    virtual int get_Episode();
    virtual int getsesson() const;
    virtual int getepisode() const;
    virtual Watchable* clone();
private:
    std::string name;

};


class Episode: public Watchable{
public:
    Episode(long id, const std::string& seriesName,int length, int season, int episode ,const std::vector<std::string>& tags);
    ~Episode();
    virtual std::string toString() const;
    virtual std:: string get_information() const;
    Watchable* getNextWatchable(Session&) const override;
    virtual int getsesson() const;
    virtual int getepisode() const;
    long getnextEpisodeId() const;
    virtual std::string get_Name() const ;
    virtual int get_Episode() const;
    virtual Watchable* clone();
private:
    std::string seriesName;
    int season;
    int episode;
    long nextEpisodeId;
};

#endif
