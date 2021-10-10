package Singeltone;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class ActionFile implements ActionReader{

    private LinkedList<Character> actions;

    public ActionFile() {
        this.actions=new LinkedList<Character>();
    }

    public void Start(String path)
    {
        File f=new File(path);
        try {
            Scanner s=new Scanner(f);
            while (s.hasNext())
                actions.addLast(s.nextLine().charAt(0));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public char nextAction()
    {
        char output=actions.getFirst();
        actions.remove(0);
        return output;
    }

    public boolean hasNext()
    {

        return !actions.isEmpty();
    }

}
