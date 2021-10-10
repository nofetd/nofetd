package GameFlow;

import java.util.Random;
import General.*;
import ObserverTools.GAMEObservable;
import Players.*;

public class Combat extends GAMEObservable {

    public Combat(CLI cli) {
        this.Register(cli);
    }

    public int attact(GameUnit  attacter, GameUnit defender) {
        this.NotifyMessage(attacter.getName() + " engaged in battle with " + defender.getName());
        attacter.printPlayerDetails();
        defender.printPlayerDetails();
        Random Rnd = new Random();
        int attactPoints = Rnd.nextInt(attacter.getAttack());
        this.NotifyMessage(attacter.getName() + " rolled " + attactPoints + " points");
        int deffenPoints = Rnd.nextInt(defender.getAttack());
        this.NotifyMessage(defender.getName() + " rolled " + deffenPoints + " points");
        int amount = attactPoints - deffenPoints;
        if (amount > 0) {
            defender.setCurrentHealth(defender.getCurrentHealth() - amount);
            this.NotifyMessage("combat details: " + attacter.getName() + " attact the " + defender.getName() + " in amount of " + amount);
        }
        else{
            this.NotifyMessage("combat details: " + attacter.getName() + " attact the " + defender.getName() + " in amount of " + "0");
        return 1;
         }
        if (defender.getCurrentHealth()<0){
            this.NotifyMessage(defender.getName()+" is died");
            return 0;
        }

        return 2   ;
    }
}
