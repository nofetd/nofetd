package Players;
import java.util.LinkedList;

import Boards.*;
import Enemies.*;
import General.*;
import GameFlow.*;

public class Warrior extends Player {

    private Integer Cooldown;
    private Integer Remaining;

    public Warrior(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer XPosition, Integer YPosition, Integer experience, Integer Level, Integer Cooldown,CLI cli) {
        super(Name, HealthPool, Attack, Defense, XPosition, YPosition, experience, Level,cli);
        this.Cooldown = Cooldown;
        this.Remaining = 0;
    }

    public GameUnit[][] gameTick_B(GameUnit[][] board, GameUnit unit, int x,int y){
        return ((Enemy)unit).gameTick(board,this);
    }

    @Override
    public Integer castAbility(LinkedList<Enemy> EnemyInRange) {
        if (Remaining>0) {
            this.NotifyMessage("you cant use your ability now");
            return 1;
        }
        else {
            Remaining = Cooldown;
            CurrentHealth = Math.min(CurrentHealth+(2*Defense), HealthPool);
            Defense = Defense - 2;
            this.NotifyMessage("your health better!");
            return 0;
        }
    }

    @Override
    public void levelUp(Integer level) {
        super.levelUp(level);
        this.Remaining = 0;
        this.HealthPool = HealthPool + (5 * level);
        this.Defense = Defense + level;
    }

    @Override
    public void GameTick()
    {
        this.Remaining = Remaining - 1;
        this.NotifyMessage("Name: "+this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense()+"\n"
              +"      "+"Level:"+this.Level+"       "+"Experience: "+this.getExperience()+"/50"+"     "+"Ability Cooldown: "+this.Cooldown+"     "+"Remaining: "+this.Remaining);
    }

    @Override
    public void printPlayerDetails() {
        this.NotifyMessage("Name: "+this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense()+"\n"
                        +"      "+"Level:"+this.Level+"       "+"Experience: "+this.getExperience()+"/"+this.levelExperience+"     "+"Ability Cooldown: "+this.Cooldown+"     "+"Remaining: "+this.Remaining+"\n");
    }
}
