//
// Created by nofet@wincs.cs.bgu.ac.il on 14/01/2020.
//
#include "../include/CommandProtocol.h"
#include <string>
#include <vector>
#include <iostream>

void CommandProtocol::run() {
    while(!terminate){
      string reciveServer;
      getline(cin, reciveServer);
      CreateMessege(&reciveServer);
       }
}

void CommandProtocol::CreateMessege(string *s) {
if(Client->isConnected()) {
    vector<string> v = split(*s);
    string ans = "";
    string first = v.front();
    if (first.compare("login") == 0) {
        ans = Login(v);
    }
    if (first.compare("join") == 0) {
        ans = this->Join(v);
    }
    if (first.compare("exit") == 0) {
        ans = Exit(v);
    }
    if (first.compare("add") == 0) {
        ans = Add(v);
    }
    if (first.compare("borrow") == 0) {
        ans = Borrow(v);
    }
    if (first.compare("return") == 0) {
        ans = Return(v);
    }
    if (first.compare("status") == 0) {
        ans = Status(v);
    }
    if (first.compare("logout") == 0) {
        ans = Logout(v);
    }
    if (!(ans.compare("") == 0))
        Client->AddFrame(&ans);
} else
    terminate=true;
}

string CommandProtocol::Login(vector<std::string> &v) {
    string UserName=v.at(2);
    string passcode=v.at(3);
    string Frame="CONNECT";
    Frame =Frame+ '\n'+"accept-version:1.2" ;
    Frame= Frame+ '\n'+"host:stomp.cs.bgu.ac.il";
    Frame= Frame+ '\n'+"login:" + UserName ;
    Frame= Frame+ '\n' +"passcode:" +passcode+'\n' + "\0";
    Client->AddFrame(&Frame);
    return Frame;
}

string CommandProtocol::Join(vector<std::string> &v) {
   string dest=v.at(1);
   int id=Client->getTopicId();
       string Id = to_string(id);
       string recipt = to_string(Client->getRecipt());
       string Frame="SUBSCRIBE";
       Frame =Frame+ '\n'+"destination:" + dest ;
       Frame= Frame+ '\n'+"id:" + Id;
       Frame= Frame+ '\n'+"receipt:" + recipt + '\n' + "\0";
       string content = "joined club " + dest;
      // cout<<Frame<<std::endl;
       Client->AddReceipt(recipt, content);
       Client->Join(dest, id);
        return Frame;
}

string CommandProtocol::Add(vector<std::string> &v) {
    string dest=v.at(1);
    string bookName ="";
    for(unsigned int i=2;i<v.size();i++) {
        bookName+=" "+ v.at(i);
    }
    bookName=bookName.substr(1);
    if(!Client->checkTopic(dest)){
        int id=Client->getTopicId();
        string Id = to_string(id);
        string recipt = to_string(Client->getRecipt());
        string Frame="SUBSCRIBE";
        Frame =Frame+ '\n'+"destination:" + dest ;
        Frame= Frame+ '\n'+"id:" + Id;
        Frame= Frame+ '\n'+"receipt:" + recipt + '\n' + "\0";
        string content = "joined club " + dest;
        //cout<<Frame<<std::endl;
        Client->AddReceipt(recipt, content);
        Client->Join(dest,id);
    }
    string name=Client->getName();
    string Frame="SEND" ;
    Frame=Frame+'\n'+"destination:"+ dest;
    Frame=Frame+'\n'+name +" has added the book "+bookName+'\n'+ "\0";
   // cout<<Frame<<endl;
    Client->Add(bookName,dest);
    return Frame;
}

string CommandProtocol::Borrow(vector<std::string> &v) {
    string dest=v.at(1);
    string bookName ="";
    for(unsigned int i=2;i<v.size();i++) {
        bookName+=" "+ v.at(i);
    }
    bookName=bookName.substr(1);
    string name=Client->getName();
    string Frame="SEND";
    Frame=Frame+'\n'+"destination:"+ dest;
    Frame=Frame+'\n' +name +" wish to borrow "+bookName+'\n'+"\0";
   // cout<<Frame<<endl;
    Client->AddToWish( bookName);
    return Frame;
}

string CommandProtocol::Return(vector<std::string> &v) {
    string dest=v.at(1);
    string bookName=v.at(2);  //todo check
    string name=Client->getBorrowName(bookName);
    if(!name.compare("")==0) {
        string Frame = "SEND";
        Frame = Frame + '\n' + "destination:" + dest;
        Frame = Frame + '\n' + "Returning " + bookName + +" to " + name + '\n' + "\0";
      //  cout<<Frame<<endl;
        Client->ReturnBook(bookName, name);
        return Frame;
    }
    cout<<"the book not exist"<<endl;
    return"";
}

string CommandProtocol::Status(vector<std::string> &v) {
    string dest=v.at(1);
    string Frame="SEND"  ;
    Frame=Frame+ '\n'+"destination:"+ dest;
    Frame=Frame+ '\n'+"book status";
    Frame=Frame+ '\n'+"\0";
   // cout<<Frame<<endl;
    return Frame;
}

string CommandProtocol::Logout(vector<std::string> &v) {
   // bool done=Client->ExitFromTopics();
        string recipt = to_string(Client->getRecipt());
        string Frame = "DISCONNECT" ;
        Frame =Frame+ '\n'+"receipt:" + recipt;
        Frame =Frame+ '\n' + "\0";
        cout<<Frame<<endl;
        string content = "logout";
        Client->AddReceipt(recipt, content);
       // this->terminate = false; //??
        return Frame;
}

string CommandProtocol::Exit(vector<std::string> &v) {
    string dest=v.at(1);
    string Id=to_string(Client->getDestId(&dest));
    string recipt=to_string(Client->getRecipt());
    string Frame="UNSUBSCRIBE";
    Frame= Frame+'\n'+"id:" + Id;
    Frame= Frame+'\n'+"receipt:"+ recipt+'\n'+ "\0";
    string content="exited club "+dest;
    //cout<<Frame<<endl;
    Client->AddReceipt(recipt, content);
    Client->Exit(dest);
    return Frame;
}

void CommandProtocol::logout() {
terminate=false;
}

CommandProtocol::CommandProtocol(client* client): Client(client), terminate(false) { ///whatyourproblem
}

vector<string> CommandProtocol::split(string &str) {
    vector<string> tokens;
    string delim=" ";
    size_t prev = 0, pos = 0;
    do
    {
        pos = str.find(delim, prev);
        if (pos == string::npos) pos = str.length();
        string token = str.substr(prev, pos-prev);
        if (!token.empty()) tokens.push_back(token);
        prev = pos + delim.length();
    }
    while (pos < str.length() && prev < str.length());
    return tokens;
}

