package UnitTest;

import Boards.*;
import Enemies.*;
import GameFlow.*;
import General.*;
import Players.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class CombatTest {

    private CLI cli;
    private Board MainBoard;
    private LinkedList<Enemy> enemies;
    private Player player;
    private Combat combat;


    @Test
    public void attact() {
        cli = new CLI();
        combat= new Combat(cli);
        GameUnit[][] board = new GameUnit[5][5];
        enemies = new LinkedList<Enemy>();
        player = new Warrior("Jon Snow", 300, 30, 4, 2, 1, 0, 1, 6,cli);
        for(int i=0;i<5;i++) {
            for (int j=0; j<5; j++)
                board[i][j] = new Free("", 0, 0, 0, i, j, cli);
        }
        board[2][1] = player;
        Enemy e = new Monster("Wright", 600, 30, 15, 1,1, 'z', 3, 100, player.getCli());
        enemies.add(e);
        MainBoard = new Board(board,enemies,player,cli);

        int x=combat.attact(e,player);
        while (x==1)
        {
            x=combat.attact(e,player);
        }

        boolean flag = player.getCurrentHealth()!=300;
        Assert.assertEquals(flag,true);
    }
}