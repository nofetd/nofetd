//
// Created by nofet@wincs.cs.bgu.ac.il on 12/01/2020.
//

#include <map>
#include "../include/client.h"
#include <string>
#include <vector>

client::client(const string &name, const string &passcode,const string &host,const short &port):
name(name),
passcode(passcode),
host(host),
port(port),
RecipId(0),
TopicId(0),
TopicsId(map<string,int>()),
Book_Lists(map<string,vector<string>*>()),
Book_Borrow(map<string,string>()),
Receipt_content(map<string ,string>()),
WishBook(vector<string>()),
conection(new ConnectionHandler(host,port)),
MtxTopicsId(),
MtxBook_Lists(),
MtxBook_Borrow(),
MtxReceipt_content(),
MtxWishBook(),
Connect(false){
}

//copyconstractor
client::client(client &other):
name(other.name),
passcode(other.passcode),
host(other.getHost()),
port(other.getPort()),
RecipId(other.getRecipt()),
TopicId(other.getTopicId()),
TopicsId(other.TopicsId),
Book_Lists(map<string,vector<string>*>()),
Book_Borrow(map<string,string>()),
Receipt_content(map<string ,string>()),
WishBook(vector<string>()),
conection(new ConnectionHandler(other.getHost(),other.getPort())),
MtxTopicsId(),
MtxBook_Lists(),
MtxBook_Borrow(),
MtxReceipt_content(),
MtxWishBook(),
Connect(false)
    {
}

//distractor
client::~client() {
TopicsId.clear();
Book_Lists.clear();
Book_Borrow.clear();
Receipt_content.clear();
WishBook.clear();
delete(conection);
}

int client::getRecipt() {
    return RecipId.fetch_add(1);
}

int client::getId() {
    return TopicId.fetch_add(0);
}

int client::getTopicId() {
    return TopicId.fetch_add(1);;
}

string client::getName() {
    return name;
}

string client::getBorrowName(string &bookName) {
    MtxBook_Borrow.lock();
     for(pair<string,string> p : Book_Borrow) {
     if(p.first.compare(bookName)==0){
         MtxBook_Borrow.unlock();
         return  p.second;
     }
 }
     MtxBook_Borrow.unlock();
    return "";
}

string client::GetReceiptContent(string id) {
   string s="";
   s=Receipt_content.at(id);
    return s;
}

void client::AddFrame(string *s) {
    conection->sendFrameAscii(*s,'\0');
}

void client::Join(string &dest, int Id) {
    pair<string,vector<string>*> P_B;
    P_B.first=dest;
    P_B.second=new vector<string>();
    Book_Lists.insert(P_B);
    pair<string,int> P_T;
    P_T.first=dest;
    P_T.second= Id;
    TopicsId.insert(P_T);
}

void client::Add(string &name, string &dest) {
   MtxBook_Lists.lock();
    for(pair<string,vector<string>*> p : Book_Lists) {
        if (p.first.compare(dest) == 0) {
            p.second->push_back(name);
        }
    }
    MtxBook_Lists.unlock();
}

void client::Exit(string &dest) {
    MtxBook_Lists.lock();
    for(pair<string,vector<string>*> p : Book_Lists) {
        if (p.first.compare(dest) == 0) {
            for (string book : *p.second) {
                int index=0;
                MtxWishBook.lock();
                for (string s : WishBook ){
                    if (s.compare(book) == 0)
                    {
                        WishBook.erase(WishBook.begin()+index);
                    }
                    index++;
                }
                MtxWishBook.unlock();
                MtxBook_Borrow.lock();
                Book_Borrow.erase(book);
                MtxBook_Borrow.unlock();
            }
        }
    }
    Book_Lists.erase(dest);
    MtxBook_Lists.unlock();
    MtxTopicsId.lock();
    TopicsId.erase(dest);
    MtxTopicsId.unlock();
}

void client::AddReceipt(string id, string content) {
pair<string,string> p;
p.first=id;
p.second=content;
MtxReceipt_content.lock();
Receipt_content.insert(p);
MtxReceipt_content.unlock();
}

bool client::HaveABook(string *book_name,string *dest) {
    MtxBook_Lists.lock();
    for(pair<string,vector<string>*> p :  Book_Lists) {
        if(p.first.compare(*dest)== 0)
        {
            for(string name:*p.second){
                if(name.compare(*book_name)==0){
                    MtxBook_Lists.unlock();
                    return true;
                }
            }
        }
    }
    MtxBook_Lists.unlock();
    return false;
}

string client::getListOfBooks(string *dest) {
    string ListOfBooks = "";
    MtxBook_Lists.lock();
    for(pair<string,vector<string>*> p :  Book_Lists) {
        if(p.first.compare(*dest)== 0)
        {
            for(string name:*p.second)
                {
                    ListOfBooks = ListOfBooks + name + ",";
                }
        }
    }
    MtxBook_Lists.unlock();
    ListOfBooks = ListOfBooks.substr(0,ListOfBooks.length()-1);
    return ListOfBooks;
}

void client::AddToWish(string BookName) {
   MtxWishBook.lock();
   WishBook.push_back(BookName);
   MtxWishBook.unlock();
}

void client::Borrow(string bookName, string BorrowName, string dest) {
    pair<string,string> p;
    p.first=bookName;
    p.second=BorrowName;
    MtxBook_Borrow.lock();
    Book_Borrow.insert(p);
    MtxBook_Borrow.unlock();
    MtxBook_Lists.lock();
    for(pair<string,vector<string>*> p : Book_Lists)
    {
        if(p.first.compare(dest)==0){
            p.second->push_back(bookName);
        }
    }
    MtxBook_Lists.unlock();
    MtxWishBook.lock();
    int index=0;
    for(string s : WishBook){
        if(s.compare(bookName)==0){
            WishBook.erase(WishBook.begin()+index);
        }
        index++;
    }
    MtxWishBook.unlock();
}

void client::RemoveBook(string bookName) {
    MtxBook_Lists.lock();
    for(pair<string,vector<string>*> p : Book_Lists)
    {
        int index=0;
        for(string book: *p.second){
            if(book.compare(bookName)==0){
                p.second->erase(p.second->begin()+index);
            }
            index++;
        }
    }
    MtxBook_Lists.unlock();
}


bool client::Wish(string BookName) {
    MtxWishBook.lock();
    for(string s :WishBook){
        if (s.compare(BookName)==0){
            MtxWishBook.unlock();
            return  true;
        }
    }
    MtxWishBook.unlock();
    return false;
}

void client::ReturnBook(string book, string oner) {
    RemoveBook(book);
    MtxBook_Borrow.lock();
    Book_Borrow.erase(book);
    MtxBook_Borrow.unlock();
}

void client::Logout() {
    conection->close();
    Connect=false;
}

int client::getDestId(string *dest) {
    MtxTopicsId.lock();
   for(pair<string,int> p : TopicsId){
       if(p.first.compare(*dest)==0){
           MtxTopicsId.unlock();
           return p.second;
       }
   }
   MtxTopicsId.unlock();
    return -1;
}

const string &client::getHost() const {
    return host;
}

const short& client::getPort() const {
    return port;
}

string client::GetFrame() {
   string frame="";
   conection->getFrameAscii(frame, '\0');
    return frame;
}

bool client::connect() {
    Connect=conection->connect();
    return Connect;
}

bool client::isConnected() {
    return Connect;
}

bool client::checkTopic(string &s) {
    for(pair<string,int> p :TopicsId){
        if (p.first.compare(s)==0){
            return true;
        }
    }
    return false;
}

client &client::operator=(const client &kt) {
    return *this;
}
