package Enemies;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import Boards.*;
import GameFlow.*;
import General.*;
import Players.*;

public class Trap extends Enemy {
    Integer Re_spawn;
    Boolean Visibility;
    Integer relocation_range;
    Integer visibility_time;
    Integer ticks_count;
    Character FirstTile;
   // Integer F_Re_spawn;
   // Integer F_visibility_time;


    public Trap(String Name, Integer HealthPool, Integer Attack, Integer Defense, Integer LocationI, Integer LocationJ, Character Tile, Integer Experience, Integer re_spawn, Integer relocation_range, Integer visibility_time, Integer ticks_count,CLI cli) {
        super(Name, HealthPool, Attack, Defense, LocationI, LocationJ, Tile, 2, Experience,cli);
        this.Re_spawn = re_spawn;
        this.Visibility = true;
        this.relocation_range = relocation_range;
        this.visibility_time = visibility_time;
        this.ticks_count = ticks_count;
        this.FirstTile = Tile;
      //  this.F_visibility_time = visibility_time;
       // this.F_Re_spawn = re_spawn;
    }

    public GameUnit[][] gameTick_B(GameUnit[][] board, GameUnit unit, Integer x, Integer y) {
        return ((Player)unit).gameTick(board, this, x, y);
    }

    public GameUnit[][] gameTick(GameUnit[][] board, Player player) {
        if (this.ticks_count == this.Re_spawn) {
            this.ticks_count = 0;
            Integer[] newPlace = this.newLocation(player, board);
            board[this.XPosition][this.YPosition] = board[newPlace[0]][newPlace[1]];
            board[newPlace[0]][newPlace[1]] = this;//?//////////????
            this.setXPosition(newPlace[0]);
            this.setYPosition(newPlace[1]);
            //this.NotifyMessage("the Trap " + this.Name + " move to a new place");
        } else {
            this.ticks_count++;
            if (this.Range(player) < 2) {
                int comb = this.combat.attact(this, player);
            }
            if (ticks_count < visibility_time) {
                this.Visibility = true;
                this.Tile = this.FirstTile;
            } else if (ticks_count == visibility_time) {
                this.Visibility = false;
                this.Tile = '.';
                //this.NotifyMessage("the Trap " + this.Name + " is not visible");
            }
            return board;
        }
        return  board;
    }

    public Integer[] newLocation(Player player, GameUnit[][] board) {
        Random Rnd = new Random();
        int X;
        int i = Rnd.nextInt(this.relocation_range);
        if (Rnd.nextInt(2) == 1) {
            X = this.getXPosition() + i;
        } else {
            X = this.getXPosition() - i;
        }
        int Y;
        int j = Rnd.nextInt(this.relocation_range);
        if (Rnd.nextInt(2) == 1) {
            Y = this.getYPosition() + j;
        } else {
            Y = this.getYPosition() - j;
        }
        if (X>0&&X<board.length&&Y>0&&Y<board[0].length&&board[X][Y].getTile()=='.')
            return new Integer[]{X, Y};
        else
            return newLocation(player, board);
    }


   /* public void ReStart() {
        this.visibility_time = this.F_visibility_time;
        this.Re_spawn = this.F_Re_spawn;
        this.ticks_count = 0;
        this.Tile = this.FirstTile;
    }*/

    public Integer getRe_spawn() {
        return Re_spawn;
    }

    public void setRe_spawn(Integer re_spawn) {
        Re_spawn = re_spawn;
    }

    public Boolean getVisibility() {
        return Visibility;
    }

    public void setVisibility(Boolean visibility) {
        Visibility = visibility;
    }

    public Integer getRelocation_range() {
        return relocation_range;
    }

    public void setRelocation_range(Integer relocation_range) {
        this.relocation_range = relocation_range;
    }

    public Integer getVisibility_time() {
        return visibility_time;
    }

    public void setVisibility_time(Integer visibility_time) {
        this.visibility_time = visibility_time;
    }

    public Integer getTicks_count() {
        return ticks_count;
    }

    public void setTicks_count(Integer ticks_count) {
        this.ticks_count = ticks_count;
    }
}




