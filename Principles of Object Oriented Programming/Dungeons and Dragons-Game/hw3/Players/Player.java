package Players;
import Enemies.*;
import GameFlow.*;
import General.*;
import Boards.*;
import ObserverTools.*;
import java.util.LinkedList;
import java.util.Set;

public abstract class Player extends GameUnit  {

    protected Integer Experience;
    protected Integer Level;
    protected Integer levelExperience;

    public Player(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer XPosition, Integer YPosition,Integer experience,Integer Level,CLI cli) {
        super(Name,HealthPool,Attack,Defense,XPosition,YPosition,cli);
        this.Experience = experience;
        this.Level = Level;
        this.Tile='@';
        this.levelExperience=50;
    }

    public void levelUp(Integer level)
    {
        Experience = Experience-(50*level);
        Level = Level + 1;
        HealthPool = HealthPool + (10*level);
        CurrentHealth = HealthPool;
        Attack = Attack + (5*level);
        Defense = Defense + (2*level);
    }

    public GameUnit[][] gameTick(GameUnit[][] board,Enemy enemy,int x,int y){
        int comb = this.combat.attact(this, enemy);
        if (comb == 0) {
            Level(enemy.getDefense());
            this.GameTick();
            board[this.getXPosition()][this.getYPosition()]=new Free("",0,0,0,this.getXPosition(),this.getYPosition(),cli);
            board[x][y] = this;
            this.setXPosition(x);
            this.setYPosition(y);
        }
        if (comb == 2) {
            this.GameTick();
        }
        return board;
    }

   public void Level(int amont){
        this.setExperience(this.getExperience()+amont);
        if(this.Experience>this.levelExperience) {
            int tmp=this.Experience-this.levelExperience;
            this.Level++;
            this.levelExperience = this.Level * 50;
            if(tmp>this.levelExperience){
                this.Experience=0;
                Level(tmp);
            }
            else
                this.Experience=tmp;
        }
    }

    public GameUnit[][] gameTick(GameUnit[][] board, Free free,int x,int y){
        this.GameTick();//???
        int xp=this.getXPosition();
        int yp=this.getYPosition();
        board[xp][yp] =free;
        board[x][y] = this;
        this.setXPosition(x);
        this.setYPosition(y);
        return board;
    }
    public GameUnit[][] gameTick(GameUnit[][] board, Wall wall,int x,int y){
        this.GameTick();//???
        return board;
    }

    public GameUnit[][] gameTick_B(GameUnit[][] board, GameUnit unit, Integer x, Integer y){
        return  board;
    }


    public abstract Integer castAbility(LinkedList<Enemy> EnemyInRange);

    public abstract void GameTick();

    public Integer getExperience() { return Experience; }

    public void setExperience(Integer experience) { Experience = experience; }

    public Integer getLevel() { return Level; }

    public void setLevel(Integer level) { Level = level; }

    public void printAfterCombat()
    {
        this.NotifyMessage("The player state after A Combat:" + "\n" +
                "Current Health:" + this.CurrentHealth);
    }


}
