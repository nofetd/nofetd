//
// Created by nofet@wincs.cs.bgu.ac.il on 14/01/2020.
//

#include "../include/ServerProtocol.h"
#include <string>
#include <vector>

ServerProtocol::ServerProtocol(client *client):  Client(client),terminate(false), connected(true)   {
}

void ServerProtocol::run() {
    while(connected){
    string read= Client->GetFrame();
    if(!read.compare("")==0){
       process(read);
    }
    }
}

void ServerProtocol::process(string &s) {
   vector<string> results= Split(s, "\n");
    string ans="";
    string first=results.front();
    if(first.compare("RECEIPT")==0){
        Receipt(results);
    }
    else
    if(first.compare("CONNECTED")==0){
        Connected(results);
    }
    else
    if(first.compare("MESSAGE")==0){
        Message(results);
    }
    else
    if(first.compare("ERROR")==0){
        Error(results);

    }
}

vector<string> ServerProtocol::Split(string str, string delim) {
    vector<string> tokens;
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

void ServerProtocol::Connected(vector<string> &v) {
    cout << "Login successful" << endl;
}

void ServerProtocol::Receipt(vector<string> &v) {
    char index = v.at(1).find(':');    //todo check return value
    string idNumber = v.at(1).substr(index+1,string::npos );
    string toPrint = Client->GetReceiptContent(idNumber);
    if(toPrint.find("logout")!= string::npos){
         connected=false;
         Client->Logout();//todo check
    }
        //todo check from here?
    cout << toPrint << endl;
}

void ServerProtocol::Message(vector<string> &v) {
    string toCheck = v.at(4);
    int indexOf = v.at(3).find(':') + 1;
    string dest = v.at(3).substr(indexOf);
    cout<<dest<<endl;
    cout << toCheck << endl;
    if (toCheck.find("wish to borrow") != string::npos)     //todo check
    {
        int index1 = toCheck.find("wish") - 1;
        string UserName = toCheck.substr(0, index1);        //todo check
        int index2 = toCheck.find("borrow") + 7;
        string BookName = toCheck.substr(index2);
        if (UserName.compare(Client->getName()) != 0)               //todo check
        {
            if (Client->HaveABook(&BookName, &dest)) {
                string frame = "SEND";
                frame = frame + '\n' + "destination:" + dest;
                frame = frame + '\n' + Client->getName() + " has " + BookName;
                frame = frame + '\n' + "\0";
                Client->AddFrame(&frame);
            }
        }
    } else if (toCheck.find("has") != string::npos && toCheck.find("added ") == string::npos) {
        int index = toCheck.find("has");
        string borrower = toCheck.substr(0, index - 1);
        string bookName = toCheck.substr(index + 4);
        if (Client->Wish(bookName)) {
            Client->Borrow(bookName, borrower, dest);
            string frame = "SEND";
            frame = frame + '\n' + "destination:" + dest;
            frame = frame + '\n' + "Taking " + bookName + " from " + borrower;
            frame = frame + '\n' + "\0";
            Client->AddFrame(&frame);
        }
    }
    else if (toCheck.find("aking") != string::npos) {
        int index = toCheck.find("from");
        string borrower = toCheck.substr(index+5);
        string bookName = toCheck.substr(7,index-8);
        if (Client->getName().compare(borrower) == 0) {
            Client->RemoveBook(bookName);
        }
    }
    else if(toCheck.find("eturning") != string::npos)
    {
        int index = toCheck.find("to");
        string bookName = toCheck.substr(10,index-11);
        string borrower = toCheck.substr(index+3);
        if(borrower.compare(Client->getName()) == 0)
        {
            Client->Add(bookName, dest);
        }
    }
    else if(toCheck.find("book status") != string::npos)
    {
        string Frame="SEND";
        Frame = Frame+'\n'+"destination:"+ dest ;
        string ListOfBooks = Client->getListOfBooks(&dest);
        Frame =Frame+'\n'+Client->getName()+":" + ListOfBooks;
        Frame =Frame+'\n'+'\0';
        Client->AddFrame(&Frame);
    }
    else if(toCheck.find(":") != string::npos){
        cout << v.at(4)  << endl;
    }
}
//todo check print for the screen every level?

void ServerProtocol::Error(vector<string> &v) {
    string recipt = to_string(Client->getRecipt());
    string Frame = "DISCONNECT" ;
    Frame =Frame+ '\n'+"receipt:" + recipt;
    Frame =Frame+ '\n' + "\0";
    cout<<Frame<<endl;
    string content = "logout";
    Client->AddReceipt(recipt, content);
    Client->AddFrame(&Frame);
   for (unsigned int i=0; i<v.size();i++){
       cout<<v.at(i)<<endl;
   }
}


