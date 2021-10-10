#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>

class Session;

enum ActionStatus{
	PENDING, COMPLETED, ERROR
};


class BaseAction{
public:
	BaseAction();
	virtual ~BaseAction();
	ActionStatus getStatus() const;
	virtual void act(Session& sess)=0;
	virtual std::string toString() const=0;
	virtual BaseAction* clone() = 0;
protected:
    std::string StatusToString() const;
	void complete();
	void error(const std::string& errorMsg);
	std::string getErrorMsg() const;
	void set_status(ActionStatus s);
private:
	std::string errorMsg;
	ActionStatus status;
};

class CreateUser  : public BaseAction {
public:
    CreateUser();
    //~CreateUser();
	virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};

class ChangeActiveUser : public BaseAction {
public:
    ChangeActiveUser();
   // ~ChangeActiveUser();
	virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};

class DeleteUser : public BaseAction {
public:
    DeleteUser();
   // ~DeleteUser();
	virtual void act(Session & sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};


class DuplicateUser : public BaseAction {
public:
    DuplicateUser();
   // ~DuplicateUser();
	virtual void act(Session & sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};

class PrintContentList : public BaseAction {
public:
    PrintContentList();
   // ~PrintContentList();
	virtual void act (Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};

class PrintWatchHistory : public BaseAction {
public:
    PrintWatchHistory();
    //~ PrintWatchHistory();
	virtual void act (Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};


class Watch : public BaseAction {
public:
    Watch();
   // ~Watch();
	virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};


class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();
   // ~ PrintActionsLog();
	virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};

class Exit : public BaseAction {
public:
    Exit();
    //~Exit();
	virtual void act(Session& sess);
	virtual std::string toString() const;
    virtual BaseAction* clone();
};
#endif
