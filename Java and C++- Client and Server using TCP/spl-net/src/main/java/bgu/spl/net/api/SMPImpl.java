package bgu.spl.net.api;

import bgu.spl.net.srv.*;
import bgu.spl.net.srv.Frames.*;
//import com.sun.xml.internal.bind.v2.model.core.ID;
//import sun.plugin2.message.Message;

import java.util.concurrent.atomic.AtomicInteger;

public class SMPImpl<T> implements StompMessagingProtocol<T> {

    private boolean shouldTerminate = false;
    private ConnectionsImpl connect=ConnectionsImpl.getInstance();
    private int IDClient;

    @Override
    public void start(int connectionId, Connections connections) {
        connect = ConnectionsImpl.getInstance();
        IDClient = connectionId;
    }

    @Override
    public void process(Frame msg) {
        if (msg != null) {
            Integer i = IDClient;
            if (i != null) {//TODO Eror Frame?
                if (((Frame) msg).getName() == "login") {
                    this.login((Frame) msg);
                }
                if (((Frame) msg).getName() == "join") {
                    this.join((Frame) msg);
                }
                if (((Frame) msg).getName() == "exit") {
                    this.exit((Frame) msg);
                }
                if (((Frame) msg).getName() == "add") {
                    this.add((Frame) msg);
                }
                if (((Frame) msg).getName() == "borrow") {
                    this.borrow((Frame) msg);
                }
                if (((Frame) msg).getName() == "return") {
                    this.Return((Frame) msg);
                }
                if (((Frame) msg).getName() == "status") {
                    this.status((Frame) msg);
                }
                if (((Frame) msg).getName() == "logout") {
                    this.logout((Frame) msg);
                }
            }
        }
    }

    private void login(Frame msg) {
              connect.Login(msg,IDClient);
            }

    private void exit(Frame msg) {
            String s=((UNSUBSCRIBE) msg).getDestination_Header();
            if (s!=null) {//TODO Eror Frame?
                connect.Exit(msg, IDClient);  //disconnect to the system
                RECEIPT R = new RECEIPT("RECEIPT");
                R.setReciptId("receipt_id:"+((UNSUBSCRIBE) msg).getReciptID());
                connect.send(IDClient, R);     // TODO send recipt to the client? or to the topic?
            }
    }

    private void join(Frame msg) {
        boolean Done = connect.SubscribeToChannel((SUBSCRIBE)msg, IDClient);   //subscribe to channel
            if (Done) {// send recipt to the client
               RECEIPT R = new RECEIPT("RECEIPT");
               R.setReciptId("receipt_id:"+((SUBSCRIBE) msg).getReciptID());
               connect.send(IDClient, R);
            }
            if(!Done){
                System.out.println("error");
            }
    }

    private void add(Frame msg) {
            boolean Done = connect.AddBook(msg,IDClient);
            if (Done){
                MESSAGE M=NewMessage(msg);
                M.setBody(((SEND)msg).getBody());
                connect.send(((SEND)msg).getDestination_Header(),M);//todo-check if the messege is fpr the clieny
                      }
    }

    private void borrow(Frame msg) {
                boolean ToSend = connect.BorrowBook(msg,IDClient);
                if(ToSend){
                    MESSAGE M = NewMessage(msg);
                    M.setBody(((SEND) msg).getBody());
                    connect.send(((SEND) msg).getDestination_Header(), M);
                }
                }

    private void Return(Frame msg) {
              MESSAGE M =NewMessage(msg);
              M.setBody(((SEND)msg).getBody());
              boolean ToSend=connect.Return(msg);
              if(ToSend)
              connect.send(((SEND) msg).getDestination_Header(), M);
      }

    private void status(Frame msg) {
                MESSAGE M =NewMessage(msg);
                M.setBody(((SEND)msg).getBody());
                connect.send(((SEND) msg).getDestination_Header(), M);
    }

    private void logout(Object msg) {
        connect.disconnect(IDClient);
        RECEIPT R = new RECEIPT("RECEIPT");
        R.setReciptId("receipt_id:"+((DISCONNECT) msg).getReciptID());
        connect.send(IDClient, R);
        //shouldTerminate=true;// todo ??
    }

        private MESSAGE NewMessage(Frame msg){
        MESSAGE M = new MESSAGE("MESSAGE");
        M.setDestination_Header("destination:"+((SEND) msg).getDestination_Header());
        M.setMessage_ID("");
        M.setID(String.valueOf(IDClient));
        return M;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
//TODO:
// check the lists on the connection
// syncronyzed

