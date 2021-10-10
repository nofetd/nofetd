package UnitTest;

import Boards.Board;
import Enemies.Enemy;
import GameFlow.CLI;
import General.Free;
import General.GameUnit;
import Players.Mage;
import Players.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class BoardTest {

    private CLI cli;
    private Board MainBoard;
    private LinkedList<Enemy> enemies;
    private Player player;


    @Test
    public void playerMove() {
        cli = new CLI();
        GameUnit[][] board = new GameUnit[4][4];
        enemies = new LinkedList<Enemy>();
        player = new Mage("Thoros of Myr", 250, 25, 3, 1, 1, 0, 1, 15, 150, 50, 3, 3,cli);
        for(int i=0;i<4;i++) {
            for (int j = 0; j < 4; j++)
                board[i][j] = new Free("", 0, 0, 0, i, j, cli);

        }
        board[1][1] = player;
        MainBoard = new Board(board,enemies,player,cli);
        MainBoard.playerMove(2,1);

        Assert.assertEquals(player,MainBoard.getBoard()[2][1]);
    }
}