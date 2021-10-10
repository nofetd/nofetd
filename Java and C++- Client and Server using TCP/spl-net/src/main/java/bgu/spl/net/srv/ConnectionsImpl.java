package bgu.spl.net.srv;

import bgu.spl.net.srv.Frames.*;
import javafx.util.Pair;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl implements Connections {

    private ConcurrentHashMap<Integer, ConnectionHandler> ConnectionHendlers_Map;
    private ConcurrentHashMap<Integer, User> Users_Map;
    private ConcurrentHashMap<String,LinkedBlockingQueue<Integer>> Topic_Queue;
    private ConcurrentHashMap<Integer,LinkedBlockingQueue<Pair<String,Integer>>> User_Topics;
    private ConcurrentHashMap<Pair<Integer,String>,Integer> Borrowing;
    private AtomicInteger IdP=new AtomicInteger(0);

    private ConnectionsImpl() {
        ConnectionHendlers_Map = new ConcurrentHashMap<Integer, ConnectionHandler>(); //?Integer??
        Topic_Queue = new ConcurrentHashMap<String, LinkedBlockingQueue<Integer>>();
        Users_Map = new ConcurrentHashMap<Integer, User>();
        Borrowing=new ConcurrentHashMap<Pair<Integer,String>,Integer>();
        User_Topics=new ConcurrentHashMap<Integer,LinkedBlockingQueue<Pair<String,Integer>>>();
    }

    public int getDestId(int Id, String dest){
       for(Pair<String,Integer> p:User_Topics.get(Id)){
           if (p.getKey().equals(dest)){
               return  p.getValue();
           }
        }
       return -1;
    }

    public ConnectionHandler<String> getHandler(int port) {
        return ConnectionHendlers_Map.get(port);
    }

    private static class ConnectionsImplHolder {
        private static ConnectionsImpl instance = new ConnectionsImpl();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static ConnectionsImpl getInstance() {
        return ConnectionsImplHolder.instance;
    }

    @Override
    public boolean send(int connectionId, Frame msg) {
        if (ConnectionHendlers_Map.containsKey(connectionId)) {
            synchronized (msg) { /* check!!*/
                msg.setID("subscription:"+connectionId);
                ConnectionHendlers_Map.get(connectionId).send(msg);
              //  msg.PrintAll();
                msg.notifyAll();
                return true;
            }
        }
        return false;
    }

    @Override
    public void send(String channel, Frame msg) {
        if (!Topic_Queue.containsKey(channel)) {
            System.out.println("Error!! the channel not exist");
        }
        else {
                for (Integer I : Topic_Queue.get(channel)
                ) {
                    if (ConnectionHendlers_Map.containsKey(I)) {
                        int id=getDestId(I,channel);
                        int Mid=Users_Map.get(I).getMId(); //todo  syncronize?
                        ((MESSAGE)msg).setMessage_ID("Message-id:"+Mid);
                        msg.setID("subscription:"+id);
                        ConnectionHendlers_Map.get(I).send(msg);  //TODO Check the users are connected?
                    }
                }
             }
    }

    public int Add(ConnectionHandler handler){
        int Id=IdP.incrementAndGet();
        ConnectionHendlers_Map.put(Id,handler);
        return Id;
    }

    @Override
    public void disconnect(int connectionId) {
        for (String s: Topic_Queue.keySet()) {
           for(Integer I : Topic_Queue.get(s)){
               if(I==connectionId){
                   Topic_Queue.get(s).remove(I);
               }
           }
        }
        User_Topics.remove(connectionId);  //todo syncronize?
        for (Pair<Integer,String> p:Borrowing.keySet()){
            if(p.getKey()==connectionId|Borrowing.get(p).intValue()==connectionId){
                Borrowing.remove(p);
            }
        }
        Users_Map.get(connectionId).logout();
    }

    public void Login(Frame msg, int Id){
       boolean Exist=false;
       String name=((CONNECT)msg).getLogin();
       String password=((CONNECT)msg).getPassword();
       User toCheck=null;
        for (User U:Users_Map.values()
             ) {
             if(name.equals(U.getName()))
                 Exist=true;
                 toCheck=U;
        }
        if(!Exist){
            User NewUser=new User(Id,name,password);
            Users_Map.put(Id,NewUser);
            LinkedBlockingQueue Q=new LinkedBlockingQueue();
            NewUser.Connect();
            CONNECTED C=new CONNECTED("CONNECTED");
            C.setVersion("version:"+((CONNECT)msg).getVersion());
            send(Id,C);
        }
        else {
            boolean islogin = toCheck.isConnected();
            if (islogin) {
                ERROR E = new ERROR("ERROR");
                E.setBody("User already logged in");
                E.setMessage_ID(1);
                send(Id, E);
            }
            else {
                if (!toCheck.getPassword().equals(((CONNECT) msg).getPassword())) {
                    ERROR E = new ERROR("ERROR");
                    E.setBody("Wrong password");
                    E.setMessage_ID(toCheck.getMId());
                    send(Id, E);
                }
                else{
                    toCheck.Connect();
                    int idToremove=toCheck.getID();
                    ConnectionHendlers_Map.remove(idToremove);//todo check!
                    Users_Map.remove(idToremove);
                    toCheck.setID(Id);
                    Users_Map.put(Id,toCheck);
                    CONNECTED C=new CONNECTED("CONNECTED");
                    C.setVersion("version:"+((CONNECT)msg).getVersion());
                    send(Id,C);
                }
            }
        }
    }

    public void Exit(Frame msg, int id) {
        String s=((UNSUBSCRIBE) msg).getDestination_Header();
        String topic=this.getTopicName(id,Integer.parseInt(s));
        if(Topic_Queue.keySet().contains(topic)) {
            if (Topic_Queue.get(topic).contains(id)) {
                Topic_Queue.get(topic).remove(id);
            }
        }
        if(User_Topics.keySet().contains(id)){
            for(Pair<String,Integer> p:User_Topics.get(id)){
                if(p.getKey().equals(topic)){
                    User_Topics.get(id).remove(p);
                    break;

                }
            }
        }
        //TODO should we need to send all the subsribers the msg?
    }

   public boolean AddBook(Frame msg,int id) {
        if (!Users_Map.containsKey(id)|
                !Users_Map.get(id).isConnected()){ ;///check the user exist and connect
             //ToDo logout?
             return false;
        }
        return true;
    }

    public boolean SubscribeToChannel(SUBSCRIBE s, int id) {
        String dest=s.getDestination_Header();
        if(dest!=null) {
            if (!Topic_Queue.containsKey(dest))
                this.Topic_Queue.put(dest, new LinkedBlockingQueue<Integer>());
            if (!User_Topics.containsKey(id))
                this.User_Topics.put(id, new LinkedBlockingQueue<Pair<String,Integer>>());
            if (Users_Map.containsKey(id) && Users_Map.get(id).isConnected()) { //check the User exist and Connect;
                String ch = s.getDestination_Header();//??
                try {
                    Topic_Queue.get(ch).put(id);
                    Pair<String,Integer> p =new Pair<String,Integer>(dest,s.getIdToTopic());
                    User_Topics.get(id).put(p);
                    return true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean BorrowBook(Frame msg, int id) {
        return true;
        /*if (!((SEND) msg).getBody().contains("from")) {
            return true;
        }
        if (((SEND) msg).getBody().contains("from")) {
            int index = ((SEND) msg).getBody().indexOf("from");
            String name = ((SEND) msg).getBody().substring(index + 5); ///TODO
            String BookName=((SEND) msg).getBody().substring(7,index-1);
            int WantToBorrow = getName(name);
            if (WantToBorrow != -1) {
                Pair<Integer, String> P = new Pair(id,BookName);
                if (!Borrowing.keySet().contains(P)) {
                    Borrowing.put(P, WantToBorrow);
                    return  true;
                }
            }
        }
        return false;*/
    }

    public boolean Return(Frame msg) {
        int index = ((SEND) msg).getBody().indexOf("to");
        String name = ((SEND) msg).getBody().substring(index + 3); ///TODO
        int ToReturn = getName(name);
        if (ToReturn!= -1) {
            int indexFirst = ((SEND) msg).getBody().indexOf("eturning")+9;
            int indexEnd = ((SEND) msg).getBody().indexOf("to")-1;
            String bookName = ((SEND) msg).getBody().substring(indexFirst,indexEnd); ///TODO
            Pair<Integer, String> P = new Pair(( msg.getID()), bookName);
            if (!Borrowing.keySet().contains(P)) {
                Borrowing.remove(P);
                return  true;
            }
        }
        return  false;
    }

    public String getName(Integer i) {
        if(Users_Map.containsKey(i))
            return Users_Map.get(i).getName();
        return "";
    }

    public int getName(String S) {
        for (User U: Users_Map.values()
             ) {
            if(U.getName().equals(S)){
                return U.getID();
            }
        }
        return-1;
    }
    public int getIdP() {
        return IdP.get();
    }

    public String getTopicName(int Id, int IdTopic){
        String ans="";
        for (Pair<String,Integer> p:User_Topics.get(Id)
             ) {
            if(p.getValue()==IdTopic){
                return p.getKey();
            }
        }
        return ans;
    }
}