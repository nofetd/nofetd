package bgu.spl.net.srv.Frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CONNECTED extends FrameImpl {

    private String Version;


    @Override
    public void PrintAll() {
        System.out.println("CONNECTED "+"\n"+Version);
    }
    public CONNECTED(String Name){
        super(Name);
    }

    public String getVersion() { return Version;}

    public void setVersion(String version) { Version = version; }

     @Override
    public Frame decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;
            if(result.contains("version"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.Version = str;
                //setName("login");
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

    public byte[] encode(Object message) {
        String tmp = Name + "\n";
        if(!Version.equals(""))
            tmp = tmp + Version + "\n";
        tmp = tmp + "\n" + "\u0000";
        byte[] result = tmp.getBytes();
        return result;
    }
}
