package Enemies;

import Boards.*;
import GameFlow.*;
import General.*;
import Players.*;

public abstract class Enemy extends GameUnit {

    ///protected Character Tile;
    protected Integer vision_range;
    protected Integer Experiense;

    public Enemy(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer LocationI, Integer LocationJ, char Tile, Integer vision_range, Integer Experience,CLI cli)
    {
        super(Name,HealthPool,Attack,Defense,LocationI,LocationJ,cli);
        this.Tile=Tile;
        this.vision_range=vision_range;
        this.Experiense=  Experience;
    }
    public void printPlayerDetails(){
        this.NotifyMessage(this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense());
    }

    @Override
    public char getTile() {
        return Tile;
    }

    public Integer getVision_range() {
        return vision_range;
    }

    public Integer getExperiense() {
        return Experiense;
    }


    public void setVision_range(Integer vision_range) {
        this.vision_range = vision_range;
    }

    public void setExperiense(Integer experiense) {
        Experiense = experiense;
    }

//    public abstract int Attackted(Player attacker);

    public abstract Integer[] newLocation(Player player, GameUnit[][] board);
    public abstract GameUnit[][] gameTick(GameUnit[][] board, Player player);
}
