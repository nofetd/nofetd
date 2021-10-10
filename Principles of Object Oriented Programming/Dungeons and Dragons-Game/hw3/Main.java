import GameFlow.CLI;
import Singeltone.*;

public class Main {

    public Singeltone singeltone;
    public static void main(String[] args)
    {

        if(args.length>1 && args[1].equals("-D"))
        {
            Singeltone.Dmode=true;
            Singeltone.getActionReader().Start("user_actions.txt");  //send user_actions.txt
            Singeltone.getRandomGenerator().Start("random_numbers.txt");  //send random_numbers.txt
        }
        else
        {
            Singeltone.Dmode=false;
            Singeltone.getActionReader().Start("user_actions.txt");  //send user_actions.txt
            Singeltone.getRandomGenerator().Start("random_numbers.txt");

        }
        CLI cli=new CLI();
        cli.StartGame(args[0]);   //send all folder
    }
}
