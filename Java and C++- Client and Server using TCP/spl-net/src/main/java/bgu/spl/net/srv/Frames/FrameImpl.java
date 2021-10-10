package bgu.spl.net.srv.Frames;

import bgu.spl.net.srv.Frames.Frame;

public abstract class FrameImpl implements Frame {

    protected String Name;
    protected String ID_Header_subscriber;
    protected byte[] bytes = new byte[1 << 10]; //start with 1k
    protected int len = 0;

    public FrameImpl(String Name)// int message_ID, int ID_Header, String Destination_Header, String Body)
    {
        this.Name = Name;
    }

    public byte[] encode(Frame message){
     return new byte[0];
    }

    public Frame decodeNextByte(byte nextByte)
    {
        return this;
    }

    public void setName(String name) { Name = name; }

    public String getName() { return Name; }

    @Override
    public String getID() {
        return ID_Header_subscriber;
    }

    @Override
    public void setID(String id) { ID_Header_subscriber=id; }

}

//protected int message_ID; //distinct Id for messege
//protected int ID_Header; //Id-client that want to subscribe
//protected String Destination_Header; ///Topic,zaner..
//protected String Body; //content
// this.message_ID = message_ID;
//this.ID_Header = ID_Header;
//this.Destination_Header = Destination_Header;
//this.Body = Body;
 /*
    public int getID_Header() {
        return ID_Header;
    }

    public int getMessage_ID() {
        return message_ID;
    }

    public String getBody() {
        return Body;
    }

    public String getDestination_Header() {
        return Destination_Header;
    }
*/