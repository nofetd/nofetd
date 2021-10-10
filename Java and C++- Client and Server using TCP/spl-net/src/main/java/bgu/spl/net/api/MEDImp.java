package bgu.spl.net.api;

import bgu.spl.net.srv.Frames.FrameImpl;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
                                // TODO change this to be <T> (implements T)
public class MEDImp implements MessageEncoderDecoder {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;


    @Override
    public byte[] encode(Object message) {
        FrameImpl f = (FrameImpl) message;
        return f.encode(message);
    }

    public String decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            return popString();
        }
        if (nextByte != '\u0000')
            pushByte(nextByte);
        return null; //not a line yet??? return this
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }
}
