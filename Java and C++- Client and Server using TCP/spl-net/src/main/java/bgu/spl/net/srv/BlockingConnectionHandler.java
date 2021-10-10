package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Frames.*;
import bgu.spl.net.srv.Frames.Frame;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final StompMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private boolean isConnected;
    private ConnectionsImpl connections=ConnectionsImpl.getInstance();
    private int id;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, StompMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.isConnected=false;
        this.id = connections.Add(this);
        this.protocol.start(id, connections);
    }

    @Override
    public void run() {
        isConnected=true;
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());

            while (!protocol.shouldTerminate() && connected) {
                Frame nextMessage = decodeFrame(in);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
                }
           disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void disconnect(){
        isConnected=false;
    }


    public Frame decodeFrame(BufferedInputStream in) throws IOException {
        int read;
        FrameImpl frame = null;
        while (!protocol.shouldTerminate() &&  (read = in.read()) >= 0)
        {
            String nextMessage = (String)encdec.decodeNextByte((byte)read);
            if(nextMessage != null)
            {
                if(nextMessage.equals("CONNECT"))
                    frame = new CONNECT("login");
                if(nextMessage.equals("SUBSCRIBE"))
                    frame = new SUBSCRIBE("join");
                if(nextMessage.equals("SEND"))
                    frame = new SEND("SEND");
                if(nextMessage.equals("DISCONNECT"))
                    frame = new DISCONNECT("logout");
                break;
            }

        }
        read=in.read();
        while (!protocol.shouldTerminate() && read >= 0)
        {
            frame = (FrameImpl) frame.decodeNextByte((byte)read);
            if(read==0) {
                break;
            }
            read = in.read();
        }
        return frame;
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        if(msg!=null ) {
            try {
                out = new BufferedOutputStream(sock.getOutputStream());
                out.write(encdec.encode(msg));
                out.flush();
                ((FrameImpl)msg).PrintAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }

    public StompMessagingProtocol getProtocol() {
        return protocol;
    }

    }