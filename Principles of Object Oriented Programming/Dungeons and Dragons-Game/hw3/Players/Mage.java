package Players;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import Boards.*;
import Enemies.*;
import General.*;
import GameFlow.*;

public class Mage extends Player {

    private Integer SpellPower;
    private Integer ManaPool;
    private Integer CurrentMana;
    private Integer Cost;
    private Integer HitTimes;
    private Integer Range;

    public Mage(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer XPosition, Integer YPosition, Integer experience, Integer Level, Integer spellPower, Integer manaPool, Integer cost, Integer hitTimes, Integer range,CLI cli) {
        super(Name, HealthPool, Attack, Defense, XPosition, YPosition, experience, Level,cli);
        this.SpellPower = spellPower;
        this.ManaPool = manaPool;
        this.CurrentMana = this.ManaPool / 4;
        this.Cost = cost;
        this.HitTimes = hitTimes;
        this.Range = range;
    }

    @Override
    public void levelUp(Integer level) {
        super.levelUp(level);
        this.ManaPool = ManaPool + (25 * level);
        this.CurrentMana = Math.min(CurrentMana + (ManaPool / 4), ManaPool);
        this.SpellPower = SpellPower + (10 * level);
    }

    /*public GameUnit[][] gameTick_B(GameUnit[][] board, GameUnit unit,Integer x,Integer y){
        return ((Player)unit).gameTick(board,unit,x,y);
    }*/

    public Integer getSpellPower() { return SpellPower; }

    public void setSpellPower(Integer spellPower) { SpellPower = spellPower; }

    public Integer getManaPool() { return ManaPool; }

    public void setManaPool(Integer manaPool) { ManaPool = manaPool; }

    public Integer getCurrentMana() { return CurrentMana; }

    public void setCurrentMana(Integer currentMana) { CurrentMana = currentMana; }

    public Integer getCost() { return Cost; }

    public void setCost(Integer cost) { Cost = cost; }

    public Integer getHitTimes() { return HitTimes; }

    public void setHitTimes(Integer hitTimes) { HitTimes = hitTimes;}

    public Integer getRange(){return this.Range;}

    public void setRange(Integer range) { Range = range; }

    @Override
    public void GameTick() { //?????
        this.CurrentMana = Math.min(ManaPool, CurrentMana + 1);
        this.NotifyMessage("Name: "+this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense()+"\n"
                +"      "+"Level:"+this.Level+"       "+"Experience: "+this.getExperience()+"/50"+"     "+"Spell Power: "+this.SpellPower+"     "+"Mana: "+this.CurrentMana+"/"+this.ManaPool);

    }

    @Override
    public void printPlayerDetails() {
        this.NotifyMessage("Name: "+this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense()+"\n"
                +"      "+"Level:"+this.Level+"       "+"Experience: "+this.getExperience()+"/"+this.levelExperience+"     "+"Spell Power: "+this.SpellPower+"     "+"Mana: "+this.CurrentMana+"/"+this.ManaPool);
    }

    public Integer castAbility(LinkedList<Enemy> Enemies) {
        if (CurrentMana < Cost) {
            this.NotifyMessage("you have't enough Mana to castAbillity");
            return 1;
        }
        else {
            this.NotifyMessage(this.Name+"cast Blizzard");
            this.CurrentMana = CurrentMana - Cost;
            Integer hits = 0;
            Random rnd = new Random();
            LinkedList<Enemy> EnemyInRange = new LinkedList<Enemy>();
            for (Enemy enemy : Enemies) {
                if (this.Range(enemy) < Range)
                    EnemyInRange.add(enemy);
            }
                while (hits < HitTimes)//in boars we need to search after an enemy???
                {
                    if (EnemyInRange.size()==0) {
                        hits=HitTimes;
                    }
                    else {
                        this.Atackted(EnemyInRange.get(0));
                        EnemyInRange.removeFirst();
                        hits++;
                    }
                }
            return 0;
        }

    }

    public void Atackted(Enemy enemy)
    {
        int amont=enemy.getCurrentHealth()-this.SpellPower;
        this.NotifyMessage(this.Name+" attact "+enemy.getName()+"in amont of:"+this.SpellPower);
        enemy.setCurrentHealth(amont);
    }


}
