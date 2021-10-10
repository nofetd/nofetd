package Enemies;
import java.util.Random;
import Boards.*;
import GameFlow.*;
import General.*;
import Players.*;
import Singeltone.Singeltone;

public class Monster extends Enemy {

    public Monster(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer LocationI, Integer LocationJ, Character Tile, Integer vision_range, Integer Experience,CLI cli) {
        super(Name, HealthPool, Attack, Defense, LocationI, LocationJ, Tile, vision_range, Experience,cli);
    }


    public GameUnit[][] gameTick_B(GameUnit[][] board, GameUnit unit, Integer x, Integer y){    //??????
        return ((Player)unit).gameTick(board,this,x,y);
    }

    public GameUnit[][] gameTick(GameUnit[][] board,Player player){
        Integer[] newPlace=newLocation(player,board);
        if (newPlace[0]==player.getXPosition()&&newPlace[1]==player.getYPosition()) {
            int comb = this.combat.attact(this, player);
        }
        else
            if(board[newPlace[0]][newPlace[1]].getTile()=='.'){
                board[this.XPosition][this.YPosition]=board[newPlace[0]][newPlace[1]];
                board[newPlace[0]][newPlace[1]]=this;
                this.setXPosition(newPlace[0]);
                this.setYPosition(newPlace[1]);
               // this.NotifyMessage("the monster " +this.Name+" move to a new locaition");
        }
        return  board;
    }

    public Integer[] newLocation(Player player,GameUnit[][] board) {
        Integer[] location=new Integer[2];
        if(this.Range(player)<this.vision_range)
        {
            Integer dx = this.XPosition-player.getXPosition();
            Integer dy = this.YPosition-player.getYPosition();
            if(Math.abs(dx) > Math.abs(dy))
                if (dx > 0)
                {
                    location[0]=this.XPosition-1;
                    location[1]=this.YPosition;
                }
                else
                {
                    location[0]=this.XPosition+1;
                    location[1]=this.YPosition;
                }
            else if(dy > 0)
            {
                location[0]=this.XPosition;
                location[1]=this.YPosition-1;
            }
                 else
            {
                location[0]=this.XPosition;
                location[1]=this.YPosition+1;
            }
        }
        else
        {
            location=this.Relocation();
        }
        return location;
    }

    public Integer[] Relocation() {
        Integer[] newLocation= new Integer[2];
        if(!Singeltone.randomGeneratorInstance.hasNext()){
            this.NotifyMessage("the file arrived to the last line");
        }
        else {
            int i = Singeltone.randomGeneratorInstance.nextInt(4);///?????
            if (i == 0) {
                newLocation[0] = this.XPosition - 1;
                newLocation[1] = this.YPosition;
            } else if (i == 1) {
                newLocation[0] = this.XPosition + 1;
                newLocation[1] = this.YPosition;
            } else if (i == 2) {
                newLocation[0] = this.XPosition;
                newLocation[1] = this.YPosition - 1;
            } else {
                newLocation[0] = this.XPosition;
                newLocation[1] = this.YPosition + 1;
            }
        }
        return newLocation;
    }

}
