//
// Created by nofet@wincs.cs.bgu.ac.il on 12/01/2020.
//
#ifndef UNTITLED_CLIENT_H
#define UNTITLED_CLIENT_H
#include <string>
#include <vector>
#include <map>
#include <mutex>
#include "connectionHandler.h"

using  namespace std;
class client {

private:
      const string name;
      const string passcode;
      const string host;
      const short port;
      atomic_int RecipId;
      atomic_int TopicId;
      map<string,int> TopicsId;
      map<string,vector<string>*> Book_Lists ;
      map<string,string> Book_Borrow;
      map<string ,string> Receipt_content;
      vector<string> WishBook;
      ConnectionHandler *conection;
      mutex MtxTopicsId;
      mutex MtxBook_Lists;
      mutex MtxBook_Borrow;
      mutex MtxReceipt_content;
      mutex MtxWishBook;
      bool Connect;
public:
    client(const string &name, const string &passcode,const string &host,const short &port);
    client(client &client);
    client & operator=(const client &kt);
    virtual ~client();
    int getRecipt();
    int getId();
    bool connect();
    bool isConnected();
    int getTopicId();
    string getName();
    string getBorrowName(string &s);
    string GetReceiptContent(string id);
    string getListOfBooks(string *dest);
    int getDestId(string *dest);
    const string &getHost() const;
    const short &getPort() const;
    string GetFrame();
    void AddFrame(string *s);
    void Join(string &dest, int Id);
    void Add(string &name, string &dest);
    void Logout();
    void Exit(string &dest);
    void AddReceipt(string id, string content);
    void AddToWish(string BookName);
    bool Wish(string BookName);
    void Borrow(string bookName, string BorrowName, string dest);
    void RemoveBook(string bookName);
    void ReturnBook(string book, string oner);
    bool HaveABook(string *book_name, string *dest);
    bool checkTopic(string &s);

};


#endif //UNTITLED_CLIENT_H
