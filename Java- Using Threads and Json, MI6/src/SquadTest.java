package test.java.bgu.spl.mics;

import main.java.bgu.spl.mics.application.passiveObjects.Agent;
import main.java.bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SquadTest {

    private Squad s;
    @BeforeEach
    public void setUp(){
        s = new Squad();
        Agent a1 = new Agent();
        a1.setName("James");
        a1.setSerialNumber("001");
        Agent a2 = new Agent();
        a2.setName("Tom");
        a2.setSerialNumber("002");
        a1.acquire();
        a2.release();
        Agent[] agents = {a1,a2};
        s.load(agents);

    }

    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        List<String> d = new LinkedList<String>();
        d.add("002");
        assertEquals(true, s.getAgents(d));
        List<String> ans=s.getAgentsNames(d);
        assertEquals(true, ans.get(0)=="Tom");
        s.sendAgents(d,60);
        assertEquals(false,s.getAgents(d));
        d.add("001");
        assertEquals(false, s.getAgents(d));
        //fail("Not a good test");
        s.releaseAgents(d);
        assertEquals(true,s.getAgents(d));
    }
}
