package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Frames.*;
import org.omg.CORBA.FREE_MEM;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NonBlockingConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final StompMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;
    private final Reactor reactor;


    public NonBlockingConnectionHandler(
            MessageEncoderDecoder<T> reader,
            StompMessagingProtocol<T> protocol,
            SocketChannel chan,
            Reactor reactor) {
        this.chan = chan;
        this.encdec = reader;
        this.protocol = protocol;
        this.reactor = reactor;
    }

    public Runnable continueRead() {
        ByteBuffer buf = leaseBuffer();
        boolean success = false;
        try {
            success = chan.read(buf) != -1;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (success) {
            buf.flip();
            return () -> {
                try {
                    if (buf.hasRemaining()) {
                        FrameImpl frameMessage = DecodeFrame(buf);
                        protocol.process(frameMessage);
                        }

                } finally {
                    releaseBuffer(buf);
                }
            };


        } else {
            releaseBuffer(buf);
            close();
            return null;
        }

    }

    private FrameImpl DecodeFrame(ByteBuffer buf)
    {
        String nextMessage="";
        FrameImpl frame = null;
        while (buf.hasRemaining())
        {
            nextMessage = (String)encdec.decodeNextByte(buf.get());
            if(nextMessage != null)
            {
                if(nextMessage.equals("CONNECT"))
                    frame = new CONNECT("login");
                else
                if(nextMessage.equals("SUBSCRIBE"))
                    frame = new SUBSCRIBE("join");
                else
                if(nextMessage.equals("SEND"))
                    frame = new SEND("SEND");
                else
                if(nextMessage.equals("DISCONNECT"))
                    frame = new DISCONNECT("logout");
                else
                if(nextMessage.equals("UNSUBSCRIBE"))
                    frame = new UNSUBSCRIBE("exit");
                else {
                    char c = nextMessage.charAt(0);
                    System.out.println("error1");
                }
                break;
            }
        }

        while (buf.hasRemaining())
        {
            if(frame == null)
                System.out.println("error2");

            frame = (FrameImpl) frame.decodeNextByte(buf.get());
        }
        return frame;
    }

    public void close() {
        try {
            chan.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isClosed() {

        return !chan.isOpen();
    }

    public void continueWrite() {
        while (!writeQueue.isEmpty()) {
            try {
                ByteBuffer top = writeQueue.peek();
                chan.write(top);
                if (top.hasRemaining()) {
                    return;
                } else {
                    writeQueue.remove();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                close();
            }
        }

        if (writeQueue.isEmpty()) {
            if (protocol.shouldTerminate()) close();
            else reactor.updateInterestedOps(chan, SelectionKey.OP_READ);
        }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }
        buff.clear();
        return buff;
    }

    public StompMessagingProtocol getProtocol() {
        return protocol;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }

    @Override
    public void send(T msg) {
       writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));
       reactor.updateInterestedOps(chan,SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        ((FrameImpl)msg).PrintAll();
    }

    public void open(){
        if (isClosed()){
            chan.socket();//todo check!!
        }
    }
}


//  if (nextMessage != null) {
// T response =
//  if (response != null) {
//    writeQueue.add(ByteBuffer.wrap(encdec.encode(response)));
//  reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
// }