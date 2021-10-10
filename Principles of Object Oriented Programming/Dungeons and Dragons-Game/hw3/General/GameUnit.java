package General;

import Boards.Board;
import GameFlow.CLI;
import GameFlow.Combat;
import ObserverTools.*;

public abstract class GameUnit extends GAMEObservable {

    protected String Name;
    protected Integer HealthPool;
    protected Integer CurrentHealth;
    protected Integer Attack;
    protected Integer Defense;
    protected Integer XPosition;
    protected Integer YPosition;
    protected char Tile;
    protected Combat combat;
    protected CLI cli;



    public GameUnit(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer XPosition, Integer YPosition, CLI cli)
    {
        this.Name=Name;
        this.HealthPool=HealthPool;
        this.CurrentHealth=HealthPool;
        this.Attack=Attack;
        this.Defense=Defense;
        this.XPosition=XPosition;
        this.YPosition=YPosition;
        this.Register(cli);
        this.combat=new Combat(cli);
        this.cli=cli;
        //this.Register(cli);
    }

    public Double Range(GameUnit unit)
    {
        Double rangeX=Math.pow(this.XPosition-unit.XPosition,2);
        Double rangeY=Math.pow(this.YPosition-unit.YPosition,2);
        return Math.sqrt(rangeX + rangeY);
    }

    public abstract void printPlayerDetails();

    public String getName() {
        return Name;
    }

    public Integer getHealthPool() {
        return HealthPool;
    }

    public Integer getCurrentHealth() {
        return CurrentHealth;
    }

    public Integer getAttack() {
        return Attack;
    }

    public Integer getDefense() {
        return Defense;
    }

    public Integer getXPosition() {
        return XPosition;
    }

    public Integer getYPosition() {
        return YPosition;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setHealthPool(Integer healthPool) {
        HealthPool = healthPool;
    }

    public void setCurrentHealth(Integer currentHealth) {
        CurrentHealth = currentHealth;
    }

    public void setAttack(Integer attack) {
        Attack = attack;
    }

    public void setDefense(Integer defense) {
        Defense = defense;
    }

    public void setXPosition(Integer XPosition) {
        this.XPosition = XPosition;
    }

    public void setYPosition(Integer YPosition) {
        this.YPosition = YPosition;
    }

    public char getTile() { return Tile; }

    public CLI getCli() { return cli; }
    public void setTile(char tile) {
        Tile = tile;
    }

    public abstract GameUnit[][] gameTick_B(GameUnit[][] board, GameUnit unit, Integer x, Integer y);

    //protected abstract GameUnit[][] gameTick(GameUnit[][] board, GameUnit unit, Integer x, Integer y);
}
