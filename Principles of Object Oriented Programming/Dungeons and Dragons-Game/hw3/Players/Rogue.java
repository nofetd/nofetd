package Players;
import java.util.LinkedList;
import java.util.Set;

import Boards.*;
import Enemies.*;
import General.*;
import GameFlow.*;

public class Rogue extends Player {
    private  Integer cost;
    private  Integer current_energy;

    public Rogue(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer XPosition, Integer YPosition, Integer experience, Integer Level, Integer cost,CLI cli) {
        super(Name, HealthPool, Attack, Defense, XPosition, YPosition, experience, Level,cli);
        this.cost = cost;
        this.current_energy = 100;
    }

    @Override
    public void levelUp(Integer level) {
        super.levelUp(level);
        this.current_energy=100;
        this.Attack=Attack + (3*level);
    }


    public Integer castAbility(LinkedList<Enemy> EnemyInRange) {
        if (this.current_energy<this.cost){
            this.NotifyMessage("you cant use your ability now");
            return 1;
        }
        else{
            this.NotifyMessage(this.Name+"cast Fan of Knives.");
            for (Enemy enemy :EnemyInRange) {
                this.current_energy=this.current_energy-this.cost;
                if (this.Range(enemy)<2){
                    this.Atackted(this);
                }
            }
            return 0;
        }
    }

    public void Atackted(GameUnit Attacked) {
        int amont=Attacked.getCurrentHealth()-this.getAttack();
        this.NotifyMessage(this.Name+" attact "+Attacked.getName()+"in amont of:"+this.Attack);
        Attacked.setCurrentHealth(amont);
    }

    public void GameTick(){
        this.current_energy = Math.min(this.current_energy + 10, 100);
        this.NotifyMessage(this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense()+"\n"
         +"      "+"Level:"+this.Level+"       "+"Experience: "+this.getExperience()+"/"+this.levelExperience+"     "+"Energy: "+this.current_energy+"/100");

    }

    @Override
    public void printPlayerDetails() {
        this.NotifyMessage(this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense()+"\n"
                +"      "+"Level:"+this.Level+"       "+"Experience: "+this.getExperience()+"     "+"Energy: "+this.current_energy+"/100");
    }
}
