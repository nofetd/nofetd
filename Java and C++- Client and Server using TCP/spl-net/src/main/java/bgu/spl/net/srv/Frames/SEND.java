package bgu.spl.net.srv.Frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SEND extends FrameImpl {

    private int message_ID; //distinct Id for messege
    private String Destination_Header; ///Topic,zaner..
    private String Book_Name;
    private String Body; //content

    public SEND(String Name){
        super(Name);
    }

    @Override
    public void PrintAll() {
        System.out.println("SEND "+"\n"+ "\n"+ getID()+"\n"+ message_ID + "\n"+ Destination_Header+ "\n"+ Body+ "\n"+Book_Name );
    }

    public String getBook_Name() {
        return Book_Name;
    }

    public void setBook_Name(String book_Name) {
        Book_Name = book_Name;
    }

    public int getMessage_ID() {
        return message_ID;
    }

    public void setMessage_ID(int message_ID) {
        this.message_ID = message_ID;
    }

    public String getDestination_Header() {
        return Destination_Header;
    }

    public void setDestination_Header(String destination_Header) {
        Destination_Header = destination_Header;
    }

    public String getBody() { return Body; }

    public void setBody(String body) {
        Body = body;
    }

    public byte[] encode(Object message) {
        String tmp = Name + "\n";
        if(!(message_ID == -1))
            tmp = tmp + message_ID + "\n";
        if(!Book_Name.equals(""))
            tmp = tmp + Book_Name + "\n";
        if(!Body.equals(""))
            tmp = tmp + Body + "\n";
        tmp = tmp + "\n" + "\u0000";
        byte[] result = tmp.getBytes();
        return result;
    }

    @Override
    public FrameImpl decodeNextByte(byte nextByte)
    {
        if (nextByte == '\n') {
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;
            if(result.contains("destination"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.Destination_Header = str;
            }
            else if(result.contains("added"))
            {
                this.Body = result;
                setName("add");
            }
            else if(result.contains("borrow")||result.contains("has")&&!result.contains("added")||result.contains("aking"))
            {
                this.Body = result;
                setName("borrow");
            }
            else if(result.contains("Returning"))
            {
                this.Body = result;
                setName("return");
            }
            else if(result.contains("status")||result.contains(":"))
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
        if(nextByte != '\n')
            bytes[len++] = nextByte;

        return this;
    }
}
