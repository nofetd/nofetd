package test.java.bgu.spl.mics;

import main.java.bgu.spl.mics.MessageBroker;
import main.java.bgu.spl.mics.MessageBrokerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageBrokerTest {
    private MessageBroker m;
    @BeforeEach
    public void setUp(){
        m = new MessageBrokerImpl();


    }

    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        /*
        M u = new M();
        m.register(u);
        AgentsAvailableEvent a = new AgentsAvailableEvent();

        m.subscribeEvent(((Event)a.getClass()), u);




        Moneypenny mp = new Moneypenny();
        m.subscribeEvent(((Event)a.getClass()), u);
        GadgetAvailableEvent g = new GadgetAvailableEvent();
        Q q = new Q();

        fail("Not a good test");
        */
    }
}
