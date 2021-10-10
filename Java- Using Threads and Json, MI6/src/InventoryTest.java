package test.java.bgu.spl.mics;

import main.java.bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryTest {

    private Inventory forExample;
    @BeforeEach
    public void setUp(){
        forExample=new Inventory();
        String[] gadget = {"gun","firearm","hand grenade","boa and arrow","sword"};
        forExample.load(gadget);
    }

    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        assertEquals(true,forExample.getItem("gun"));
        assertEquals(false,forExample.getItem("blabla"));
        //fail("Not a good test");
    }
}
