package bgu.spl.net.srv.Frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ERROR extends FrameImpl {
    private int message_ID; //distinct Id for messege
    private String Body; //content
    public ERROR(String Name){
        super(Name);
    }

    @Override
    public void PrintAll() {
        System.out.println("ERROR "+"\n"+getID()+ "\n"+ message_ID +"\n"+ Body );
    }
    public int getMessage_ID() {
        return message_ID;
    }

    public void setMessage_ID(int message_ID) {
        this.message_ID = message_ID;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public byte[] encode(Object message) {
        String tmp = Name + "\n";
        if(!(message_ID == -1))
            tmp = tmp +"receipt-id: "+ message_ID + "\n";
        if(!Body.equals(""))
            tmp = tmp +"message: "+ Body + "\n";
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
            if (result.contains("id")) {
                int index = result.indexOf(":");
                String str = result.substring(index + 1);
                this.message_ID = Integer.parseInt(str);
            }
            else if (result.contains("REQUIRED")) {
                this.Body = result;
                setName("ERROR");
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

        return this;       //maybe null?
    }
}
