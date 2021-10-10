package bgu.spl.net.srv.Frames;

import bgu.spl.net.api.MessageEncoderDecoder;
//import sun.security.jca.GetInstance;

public interface Frame<T> extends MessageEncoderDecoder<T> {
    /**
     * Marker
     */
     String getName();
     void setName(String Name);
     String getID();
     void setID(String id);
     void PrintAll();
}
