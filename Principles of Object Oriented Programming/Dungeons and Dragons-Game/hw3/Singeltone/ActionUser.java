
package Singeltone;
import java.util.Scanner;
public class ActionUser implements ActionReader
{
    public char nextAction(){
        Scanner s = new Scanner(System.in);
        char ans =s.nextLine().charAt(0);
        return  ans;
    }

    @Override
    public void Start(String path) {

    }


    public boolean hasNext()
    {
        return true;
    }
}
