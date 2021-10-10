
/////#include <vector>
#include "../Include/User.h"
#include "../Include/Action.h"


BaseAction::BaseAction():errorMsg(""),status(PENDING)  {
}

ActionStatus BaseAction::getStatus() const {
    return status;
}

void BaseAction::complete()   {
    this->status = COMPLETED;
}

void BaseAction::error(const std::string &errorMsg) {
    this->errorMsg = errorMsg;
    this->status = ERROR;
}

std::string BaseAction::getErrorMsg() const {
    return errorMsg;
}

std::string BaseAction::StatusToString() const {
    if (this->status==COMPLETED){
        return "COMPLETED";
    }
    if(this->status==PENDING){
        return "PENDING";
    }
    if(this->status==ERROR){
        return "ERROR:"+this->getErrorMsg();
    }
    return "";
}

void BaseAction::set_status(ActionStatus s) {
    this->status = s;
}

BaseAction::~BaseAction() {
errorMsg.clear();
}

void CreateUser::act(Session &sess) {
    std::string str = sess.get_currAction();
    int len = str.length();
    std::string type = str.substr(len - 3, 3);
    int index = len - 5;
    char tav = str.at(index);
    int count=0;
    while (tav != ' ') {
        index--;
        tav = str.at(index);
        count++;
    }
    std::string userName = str.substr(index + 1, count);
    User *user;
    bool valid=true;
    if (type.compare("len") == 0)
        user = new LengthRecommenderUser(userName);
    else if (type.compare("rer") == 0)
        user = new RerunRecommenderUser(userName);
    else if (type.compare("gen") == 0)
        user = new GenreRecommenderUser(userName);
    else {
        this->error("There isn't a type of this User");
        cout << "Error-" + this->getErrorMsg() << endl;
        valid=false;
    }

    bool exist = false;
    if (sess.get_userMap().find(userName) != sess.get_userMap().end()) {
        this->error("The name of the User is already exist");
        cout << "Error-" + this->getErrorMsg() << endl;
        exist = true;
    }

    if (!exist&valid) {
        sess.AddToUsers(user);
        sess.set_activeUser(user);
        this->complete();
    }
}

std::string CreateUser::toString() const {
    string ans="CreateUser ";
    ans += this->StatusToString();
    return  ans;
}

CreateUser::CreateUser():BaseAction() {
}

BaseAction *CreateUser::clone() {
    return new CreateUser(*this);

}


void ChangeActiveUser::act(Session &sess) {
    std::string str = sess.get_currAction();
    int len = str.length();
    std::string userName = str.substr(11,len-1);
    bool exist = false;

    User* temp= nullptr;
    for(pair<string,User*> u: sess.get_userMap()){
        if(u.first==userName){
            temp=u.second;
            sess.set_activeUser(temp);
            this->complete();
            exist=true;
        }
    }

    if(!exist)
    {
        this->error("The user doesn't exist in the system");
        cout << "Error-" + this->getErrorMsg() << endl;
    }

    }

std::string ChangeActiveUser::toString() const {
    return "ChangeActiveUser " + this->StatusToString();
}

ChangeActiveUser::ChangeActiveUser():BaseAction() {

}

BaseAction *ChangeActiveUser::clone() {
    return new ChangeActiveUser(*this);
}

void DeleteUser::act(Session &sess) {
    std::string str = sess.get_currAction();
    int len = str.length();
    std::string userName = str.substr(11,len-1);
    bool exist =false;
    for(pair<string,User*> u: sess.get_userMap()){
        if(u.first==userName){
            sess.deleteUser(userName);
           // delete(u.second);
            exist=true;
            this->complete();
        }
    }

    if(!exist) {
        this->error("The user doesn't exist in the system");
        cout << "Error-" + this->getErrorMsg() << endl;
    }
}

std::string DeleteUser::toString() const {
    return "DeleteUser " + this->StatusToString();
}

DeleteUser::DeleteUser():BaseAction() {

}

BaseAction *DeleteUser::clone() {
    return new DeleteUser(*this);
}

void DuplicateUser::act(Session &sess) {
    std::string str = sess.get_currAction();
    int len = str.length();
    bool exist=true;
    std::string originalUser;
    std::string newUser;
    int index = 9;
    int countTav = 1;
    char tav = str.at(index);
    while(tav != ' ')
    {
        countTav++;
        index++;
        tav = str.at(index);
    }
    originalUser = str.substr(8, countTav);
    newUser = str.substr(index+1, len-1);

    User* temp1= nullptr;
    for(pair<string,User*> u: sess.get_userMap()){
        if(u.first==originalUser){
            temp1=u.second;
        }
    }
    if(temp1 == nullptr)
    {
        this->error("The user doesn't exist in the system");
        cout << "Error-" + this->getErrorMsg() << endl;
        exist= false;
    }
    User* temp2= nullptr;
    for(pair<string,User*> u: sess.get_userMap()){
        if(u.first==newUser){
            temp2=u.second;
        }
    }
    if(temp2 != nullptr)
    {
        this->error("The Name of the new user is already taken");
        cout << "Error-" + this->getErrorMsg() << endl;
        exist= false;
    }
if(exist) {
    User *toADD = temp1->clone(newUser);
    sess.AddToUsers(toADD);
    this->complete();
}
}

std::string DuplicateUser::toString() const {
    return "DuplicateUser " + this->StatusToString();
}

DuplicateUser::DuplicateUser():BaseAction() {

}

BaseAction *DuplicateUser::clone() {
    return new DuplicateUser(*this);
}

void PrintContentList::act(Session &sess) {
    std::vector<Watchable *> forPrint= sess.get_content();
    int id=1;
    int size = forPrint.size();
        for( int i=0; i<size;i++){
        Watchable *print = forPrint.at(i);
        string tmp=print->get_information();
        cout<< print->get_information() <<endl;
        id++;
    }
    this->complete();
}

std::string PrintContentList::toString() const {
    return "PrintContentList "+ this->StatusToString();
}

PrintContentList::PrintContentList():BaseAction() {

}

BaseAction *PrintContentList::clone() {
    return new PrintContentList(*this);
}

void PrintWatchHistory::act(Session &sess) {
    string name=sess.get_activeUser()->getName();
    cout<< "Watch history for " +  name <<endl;
    std::vector<Watchable *> forPrint= sess.get_activeUser()->get_history(); ///???check
    int id=1;
    int size = forPrint.size();
    for (int i=0; i<size;i++){
        Watchable *print = forPrint.at(i);
        char tav=print->toString().at(0);
        int index=0;
        while (tav != ' ') {
            index++;
            tav = print->toString().at(index);
        }
        std::string userName = print->toString().substr(index + 1, print->toString().length());
        cout<< std::to_string(id) +" "+ userName  <<endl;
        id++;
    }
    this->complete();
}

std::string PrintWatchHistory::toString() const {
    return "Print Watch History " + this->StatusToString();
}

PrintWatchHistory::PrintWatchHistory():BaseAction() {

}

BaseAction *PrintWatchHistory::clone() {
    return new PrintWatchHistory(*this);
}

void PrintActionsLog::act(Session &sess) {
    std::vector<BaseAction*> forPrint= sess.get_actionsLog();
    int size = forPrint.size();
    for (int i=0;i<size; i++) {
        BaseAction *print = forPrint.at(i);
        string tmp=print->toString();
        cout << print->toString() << endl;
    }
    this->complete();
}

std::string PrintActionsLog::toString() const {
    string ans="PrintActionsLog ";
    ans += this->StatusToString();
    return ans;
}

PrintActionsLog::PrintActionsLog():BaseAction() {

}

BaseAction *PrintActionsLog::clone() {
    return new PrintActionsLog(*this);
}

void Watch::act(Session &sess) {
    int length=sess.get_currAction().length();
    string id=sess.get_currAction().substr(6,length);
    Watchable *toWatch=sess.get_content().at(std::stoi(id)-1);
    if (toWatch != nullptr){
        cout<<"Watching "+ toWatch->toString().substr(3,toWatch->toString().size())<<endl;
        sess.get_activeUser()->AddToHistory(toWatch);
        Watchable *nextWatch = toWatch->getNextWatchable(sess);
        if (nextWatch != nullptr) {
            sess.set_recomandtion(nextWatch);
        }
        if (nextWatch == nullptr) {
        }
        this->complete();
    }
    else
    this->error("the wanted movie isnt found");
}

std::string Watch::toString() const {
    return "Watch " + this->StatusToString();
}

Watch::Watch():BaseAction() {

}

BaseAction *Watch::clone() {
    return new Watch(*this);
}
/*
Watch::~Watch() {

}
*/
Exit::Exit():BaseAction() {

}

std::string Exit::toString() const {
    return "Exit " +this->StatusToString();
}

void Exit::act(Session &sess) {
    this->complete();
}

BaseAction *Exit::clone() {
    return new Exit(*this);
}
