package Boards;
import Enemies.Enemy;
import Enemies.Monster;
import Enemies.Trap;
import General.Free;
import General.GameUnit;
import General.Wall;
import Players.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class boardLevel {
    public Player player;
    public String path;
    public int Level;
    public GameUnit[][] board;
    public LinkedList<Enemy> enemyList;
    int x;
    int y;

    public boardLevel(String path, int level, Player player) {
       this.enemyList=new LinkedList<Enemy>();
        Level = level;
        this.path = path;
        this.y = 0;
        this.x = -1;
        this.player = player;
    }

    public void build() {
        File f = new File(path);
        try {
            Scanner s = new Scanner(f);
            int length = s.nextLine().length();
            int length2 = 1;
            while (s.hasNext()) {
                String tmp = s.nextLine();
                length2++;
            }
            this.board = new GameUnit[length2][length];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String tmp = s.nextLine();
                this.y = 0;
                this.x++;
                while (y < tmp.length()) {
                    this.board[this.x][this.y] = this.getGameUnit(tmp.charAt(y));
                    y++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private GameUnit getGameUnit(char charAt) {
        if (charAt == '@') {
            player.setXPosition(x);
            player.setYPosition(y);
            return player;
        }
        if (charAt == 's') {
            Monster m1 = new Monster("Lannister Solider", 80, 8, 3, x,y, 's', 3, 25, player.getCli());
            this.enemyList.add(m1);
            return m1;
        }
        if (charAt == 'k') {
            Monster m5 = new Monster("Lannister Knight", 200, 14, 8, x,y, 'k', 4, 50, player.getCli());
            this.enemyList.add(m5);
            return m5;
        }
        if (charAt == 'q') {
            Monster m17 = new Monster("Queen’s Guard", 400, 20, 15, x,y, 'q', 5, 100, player.getCli());
            this.enemyList.add(m17);
            return m17;
        }
        if (charAt == 'z') {
            Monster m3 = new Monster("Wright", 600, 30, 15, x,y, 'z', 3, 100, player.getCli());
            this.enemyList.add(m3);
            return m3;
        }
        if (charAt == 'b') {
            Monster m1 = new Monster("Bear-Wright", 1000, 75, 30, x,y, 'b', 4, 250, player.getCli());
            this.enemyList.add(m1);
            return m1;
        }
        if (charAt == 'g') {
            Monster m8 = new Monster("Giant-Wright", 1500, 100, 40, x,y, 'g', 5, 500, player.getCli());
            this.enemyList.add(m8);
            return m8;
        }
        if (charAt == 'w') {
            Monster m7 = new Monster("White Walker",2000,150,50,x,y,'w',6,1000,player.getCli());
            this.enemyList.add(m7);
            return m7;
        }
        if (charAt == 'M') {
            Monster m9 = new Monster("The Mountain",1000,60,25,x,y,'M',6,500,player.getCli());
            this.enemyList.add(m9);
            return m9;
        }
        if (charAt == 'C') {
            Monster m26 = new Monster("Queen Cersei",100,10,10,x,y,'C',1,1000,player.getCli());
            this.enemyList.add(m26);
            return m26;
        }
        if (charAt == 'K') {
            Monster m21 = new Monster("Night’s King",5000,300,150,x,y,'K',8,5000,player.getCli());
            this.enemyList.add(m21);
            return m21;
        }
        if(charAt=='B')
        {
            Trap t1 = new Trap("Bonus \"Trap\"",1,1,1,x,y,'B',250,6,5,2,0,player.getCli());
            this.enemyList.add(t1);
            return t1;
        }
        if(charAt=='Q')
        {
            Trap t1 = new Trap("Queen’s Trap",250,50,10,x,y,'Q',100,10,4,4,0,player.getCli());
            this.enemyList.add(t1);
            return t1;
        }
        if(charAt=='D')
        {
            Trap t1 = new Trap("Death Trap",500,100,20,x,y,'D',250,10,6,3,0,player.getCli());
            this.enemyList.add(t1);
            return t1;
        }
        if(charAt=='#')
        {
            return  new Wall("",0,0,0,x,y,player.getCli());
        }
        return new Free("",0,0,0,x,y,player.getCli());
    }
}