package bgu.spl.net.srv;

import bgu.spl.net.srv.Frames.Frame;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, Frame msg);

    void send(String channel, Frame msg);

    void disconnect(int connectionId);
}
