package bgu.spl.net.srv.Frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UNSUBSCRIBE extends FrameImpl{
    private int message_ID; //distinct Id for messege
    private String Destination_Header; ///Topic,zaner..
    private String Body; //content
    private int ReciptID;
    public UNSUBSCRIBE(String Name){
        super(Name);
    }

    @Override
    public void PrintAll() {
        System.out.println("UNSUBSCRIBE "+ "\n"+ message_ID + "\n"+ Destination_Header+ "\n"+ getID()+"\n" +Body );
    }

    public int getReciptID() {
        return ReciptID;
    }

    public void setReciptID(int reciptID) {
        ReciptID = reciptID;
    }

    public int getMessage_ID() {
        return message_ID;
    }

    public String getDestination_Header() {
        return Destination_Header;
    }

    public String getBody() {
        return Body;
    }

    public void setMessage_ID(int message_ID) {
        this.message_ID = message_ID;
    }

    public void setDestination_Header(String destination_Header) {
        Destination_Header = destination_Header;
    }

    public void setBody(String body) { Body = body; }

    public byte[] encode(Object message) {
        String tmp = Name + "\n";
        if(!(message_ID == -1))
            tmp = tmp + message_ID + "\n";
        if(!Destination_Header.equals(""))
            tmp = tmp + Destination_Header + "\n";
        if(!Body.equals(""))
            tmp = tmp + Body + "\n";
        tmp = tmp + "\n" + "\u0000";
        byte[] result = tmp.getBytes();
        return result;
    }

    public FrameImpl decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;
            if(result.contains("id:"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.Destination_Header= str;
            }
            else if(result.contains("receipt:"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.ReciptID =Integer.parseInt(str);
            }
        }
        else if(nextByte == '\u0000')
        {
            return this;
        }

        else if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;

        return this;       //maybe null
    }

}