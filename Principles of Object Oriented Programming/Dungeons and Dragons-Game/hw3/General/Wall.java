package General;

import GameFlow.CLI;
import Players.Player;

public class Wall extends GameUnit  {
    public Wall(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer XPosition, Integer YPosition,CLI cli) {
        super(Name, HealthPool, Attack, Defense, XPosition, YPosition,cli);
        this.Tile='#';
    }


    public GameUnit[][] gameTick_B(GameUnit[][] board, GameUnit unit,Integer x,Integer y){
        return ((Player)unit).gameTick(board,this,x,y);
    }
    public void printPlayerDetails(){
        this.NotifyMessage(this.getName()+"    "+"Health: "+this.getCurrentHealth()+"    "+"Attact demage: "+this.getAttack()+"    "+"Defence: "+this.getDefense());
    }

}
