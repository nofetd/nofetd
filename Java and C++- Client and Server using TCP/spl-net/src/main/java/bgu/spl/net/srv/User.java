package bgu.spl.net.srv;

import java.util.concurrent.atomic.AtomicInteger;

public class User {

    private int ID;
    private AtomicInteger MId;
    private boolean connected;
    private String Password;
    private String name;

    public User(int id, String name,String password){
        this.ID=id;
        this.connected=false;
        this.Password=password;
        this.name=name;
        MId=new AtomicInteger(0);
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public void Connect() {
        this.connected = true;
    }

    public boolean isConnected() {
        return connected;
    }

    public int getID() { return ID; }

    public String getPassword() {
        return Password;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMId() {
        return MId.incrementAndGet();
    }
    public void logout(){
        this.connected=false;
    }
}
