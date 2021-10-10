package Boards;
import Enemies.*;
import GameFlow.*;
import General.*;
import ObserverTools.*;
import Players.*;

import java.util.LinkedList;

import static sun.java2d.loops.CompositeType.General;

public class Board extends GAMEObservable { ///new
    private GameUnit[][] board;
  //  int Level;
    private LinkedList<Enemy> enemyList;
    private Player player;
    private Combat combat;
    private  CLI cli;

    //constructor
    public Board(GameUnit[][] board, LinkedList<Enemy> enemyList ,Player player,CLI cli) {
        this.board = board;
        //this.Level = board.Level;
        this.enemyList = enemyList;
        this.player = player;
        this.combat = new Combat(cli);
        //this.Register(cli);
        this.cli=cli;
    }


    //cast ability of player
    public int playPlayerAbility() {
        this.player.castAbility(this.enemyList);
        int index=0;
        while(index<this.enemyList.size()) {
           // for (int i=0;i<this.enemyList.size();i++) {
                if (this.enemyList.get(index).getCurrentHealth() <= 0) {
                    board[this.enemyList.get(index).getXPosition()][this.enemyList.get(index).getYPosition()] = new Free("", 0, 0, 0, this.enemyList.get(index).getXPosition(), this.enemyList.get(index).getYPosition(), cli);
                    player.Level(this.enemyList.get(index).getDefense());
                    enemyList.remove(index);

                }
                else
                    index++;
            }

            if (player.getCurrentHealth() <= 0)
                return 1;
            if (enemyList.isEmpty())
                return 0;
        return 2;
    }

    //return 0 to victory level, 1 if PLayer died and GameOver, return 2 to continue
    public int playerMove(Integer x, Integer y) {
        if (x > 0 && y > 0 && x < this.board.length && y < this.board[0].length) {
            GameUnit tmp = board[x][y];
            this.board=tmp.gameTick_B(this.board,player,x,y);
            if (player.getCurrentHealth()<=0) {   //if the player died
                player.setTile('X');    //???
                return 1;
            }
            if (enemyList.contains(tmp)&&tmp.getCurrentHealth()<=0){////????
                this.enemyList.remove(tmp);
            }
            if (enemyList.isEmpty()|player.getExperience()>=50) {
                return 0;
            }
        }
            return 2;
    }

    //return 0 to victory level, 1 if PLayer died and GameOver, return 2 to continue
    public int monsterTick() {
        for (Enemy enemy : enemyList) {
            this.board = enemy.gameTick(this.board, player);
            if (player.getCurrentHealth() <= 0) {
                player.setTile('X');    //???
                return 1;
            }
        }
        if (enemyList.isEmpty()|player.getExperience()>=50) {
            return 0;
        }
            return 2;
    }

    public Player getPlayer() { return player; }

    public GameUnit[][] getBoard() { return board; }

    public LinkedList<Enemy> getEnemyList() { return enemyList; }

    public Combat getCombat() { return combat; }
}
