package Singeltone;
import javax.swing.*;
import java.io.File;
import java.util.List;

public  class Singeltone {
    public static boolean Dmode;
    public static ActionReader actionReaderInstance;
    public static RandomGenerator randomGeneratorInstance;

    public static ActionReader getActionReader(){
        if (actionReaderInstance==null){
            if(Dmode)
                actionReaderInstance=new ActionFile();
            else
                actionReaderInstance = new ActionUser();
        }
        return actionReaderInstance;
    }

    public static RandomGenerator getRandomGenerator(){
        if(randomGeneratorInstance == null)
            if(Dmode)
                randomGeneratorInstance = new FileGenerator();
            else
                randomGeneratorInstance = new UserGenerator();
        return randomGeneratorInstance;
    }

}


