package Singeltone;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class FileGenerator implements RandomGenerator {


    LinkedList<Integer> actions;

    public FileGenerator() {
        this.actions = new LinkedList<Integer>();
    }


    public void Start(String path)
    {
        File f=new File(path);
        try {
            Scanner s=new Scanner(f);
            while (s.hasNext())
                actions.addLast(s.nextInt());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int nextInt(int n)
    {
        int output=actions.getFirst();
        actions.remove(0);
        return output;
    }

    public boolean hasNext()
    {
        return !actions.isEmpty();
    }
}
