package bgu.spl.net.srv.Frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MESSAGE extends FrameImpl{

    private String message_ID; //distinct Id for messege
    private String Destination_Header; ///Topic,zaner..
    private String Body; //content

    @Override
    public void PrintAll() {
        System.out.println("MESSAGE "+"\n"+getID()+ "\n"+ message_ID + "\n"+ Destination_Header+ "\n"+ Body );
    }

    public MESSAGE(String Name){
        super(Name);
    }

    public String getMessage_ID() {
        return message_ID;
    }

    public void setMessage_ID(String message_ID) {
        this.message_ID = message_ID;
    }

    public String getDestination_Header() {
        return Destination_Header;
    }

    public void setDestination_Header(String destination_Header) {
        Destination_Header = destination_Header;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) { Body = body; }

    @Override
    public Frame decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;
            if(result.contains("added"))
            {
                this.Body = result;
                setName("add");
            }
            else if(result.contains("borrow"))
            {
                this.Body = result;
                setName("borrow");
            }
            else if(result.contains("has"))
            {
                this.Body = result;
            }
            else if(result.contains("Taking"))
            {
                this.Body = result;
            }
            else if(result.contains("Returning"))
            {
                this.Body = result;
                setName("return");
            }
            else if(result.contains("status"))
            {
                this.Body = result;
                setName("status");
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

        return this;
    }

    @Override
    public byte[] encode(Object message) {
        String tmp = Name + "\n";
        if(!(ID_Header_subscriber.equals("")))
            tmp = tmp +  ID_Header_subscriber + "\n";
        if(!message_ID.equals(""))
            tmp = tmp +  message_ID + "\n";
        if(!(Destination_Header.equals("")))
            tmp = tmp + Destination_Header + "\n";
        if(!(Body.equals("")))
            tmp = tmp + Body + "\n";
        tmp = tmp + "\u0000";
        byte[] result = tmp.getBytes();
        return result;
    }
}
