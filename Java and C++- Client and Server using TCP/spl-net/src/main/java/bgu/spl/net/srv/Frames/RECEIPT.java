package bgu.spl.net.srv.Frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RECEIPT extends FrameImpl {

    private String reciptId;
    @Override
    public void PrintAll() {
        System.out.println("RECIPT "+"\n"+reciptId);
    }
    public RECEIPT(String Name){
        super(Name);
    }

    public String getReciptId() { return reciptId; }

    public void setReciptId(String reciptId) { this.reciptId = reciptId; }

    @Override
    public Frame decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;
            if(result.contains("receipt"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.reciptId = str;
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
        if(!(reciptId.equals("")))
            tmp = tmp + reciptId + "\n";
        tmp = tmp + "\n" + "\u0000";
        byte[] result = tmp.getBytes();
        return result;
    }
}
