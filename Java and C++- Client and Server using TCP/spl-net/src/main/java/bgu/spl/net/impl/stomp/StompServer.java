package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MEDImp;
import bgu.spl.net.api.SMPImpl;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Frames.*;
import bgu.spl.net.srv.Frames.Frame;
import bgu.spl.net.srv.NonBlockingConnectionHandler;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Server;
//import sun.plugin2.message.Message;
//import sun.plugin2.message.Serializer;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class StompServer {

    public static void main(String[] args) {
        ConnectionsImpl C = ConnectionsImpl.getInstance();
        int port=Integer.parseInt(args[0]);
        if (args[1].equals("tpc")) {
            Server.threadPerClient(
                    port, //port
                    SMPImpl<Frame>::new, //protocol factory
                    ()-> new MEDImp() //message encoder decoder factory
            ).serve();
        }
        if (args[1].equals("reactor")) {
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    port, //port
                    () -> new SMPImpl<>(), //protocol factory
                    () -> new MEDImp() //message encoder decoder factory
            ).serve();
        }
    }
}
