package GameFlow;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import Boards.*;
import Enemies.*;
import General.*;
import ObserverTools.*;
import Players.*;
import Singeltone.*;

public class CLI extends GAMEObserver  {

    private Player player;
    //boardLevel[] boards=new boardLevel[5];
    private Board MainBoard;       //collection of boards

//location of player is different between levels
    public void StartGame(String path) {
        Scanner s = new Scanner(System.in);
        this.printTable();
        System.out.println("please select player");
        char c = Singeltone.getActionReader().nextAction();
            while (!(c == '1') && !(c == '2') && !(c == '3')&&!(c == '4') && !(c == '5') && !(c == '6')) {
                System.out.println("please choose again");
                c = s.next().charAt(0);
            }
            if(c=='1'){
                player = new Warrior("Jon Snow", 300, 30, 4, 2, 10, 0, 1, 6, this);
                }
            if (c == '2') {
                player = new Warrior("The Hound", 400, 20, 6, 2, 10, 0, 1, 4, this);
            }
            if (c == '3') {
                player = new Mage("Melisandre", 160, 10, 1, 2, 10, 0, 1, 40, 300, 30, 5, 6, this);
                }
            if
            (c == '4') {
                player = new Mage("Thoros of Myr", 250, 25, 3, 2, 10, 0, 1, 15, 150, 50, 3, 3, this);
                }
            if (c == '5') {
                player = new Rogue("Arya Stark", 150, 40, 2, 2, 10, 0, 1, 20, this);
            }
            if (c == '6') {
                player = new Rogue("Bronn", 250, 35, 3, 2, 10, 0, 1, 60, this);
                }
        System.out.println("you have selected:");
        player.printPlayerDetails();
        System.out.println("Move on the board:" + "\n" +
                "          Click 'w' - to move up" + "\n" +
                "          Click 's' - to move down" + "\n" +
                "          Click 'a' - to move left" + "\n" +
                "          Click 'd' - to move right" + "\n" +
                "          Click 'e' - to cast your ability" + "\n" +
                "          Click 'q' - for do nothing");

        File folder = new File(path);
        List<String> levels = Arrays.stream(folder.listFiles())
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
        int level = 0;
        while(this.player.getCurrentHealth()>0) {
                boolean flag = true;
                Level(levels.get(level),level);
                PrintBoard();//print the board

                while (flag) {
                    if (!Singeltone.actionReaderInstance.hasNext())
                        System.out.println("the file-'ActionFile' arrived to the final line,pleas write more lines.");
                    else {
                        char x = Singeltone.actionReaderInstance.nextAction();
                        int res;
                        if (x == 'e') {
                            res = this.MainBoard.playPlayerAbility();
                        } else {
                            Integer moveX = player.getXPosition();
                            Integer moveY = player.getYPosition();
                            if (x == 'w')
                                moveX--;
                            else if (x == 's')
                                moveX++;
                            else if (x == 'a')
                                moveY--;
                            else if (x == 'd')
                                moveY++;
                            //0-winner level 1-player died 2-continue
                            res = this.MainBoard.playerMove(moveX, moveY);
                        }
                        if (res == 0) {
                            level++;
                            flag = false;
                            player.levelUp(level);
                            System.out.println("Greetings!!! You moved to the next level!!!!");
                        } else if (res == 1) {
                            flag = false;
                            System.out.println("oops..Game Over");
                        }
                        else if (res == 2) {
                            //PrintBoard();
                            int outcome = this.MainBoard.monsterTick();
                            PrintBoard();
                            if (outcome == 0) {
                                level++;
                                player.levelUp(level);
                                flag = false;
                                System.out.println("Greetings!!! You moved to the next level!!!!");
                            } else if (outcome == 1) {
                                flag = false;
                            }
                        }
                    }
                }
        }
        if(level==levels.size())
            System.out.println("WHooo!! You are the winner!!!");
        else
            System.out.println("Oops.. Game Over.. Maybe next time!");
        }

    private void printTable() {
        System.out.println(" _____________________________________________________________________________________________________________________");
        System.out.println("|____________________________________WARRIORS_________________________________________________________________________|");
        System.out.println("|__NUMBER__|_____NAME______|__HEALTH__|__ATTACT__|_DEFENCE_|_COOLDOWN_|_______________________________________________|");
        System.out.println("|____1_____|___Jone snow___|___300____|____30____|____4____|_____6____|_______________________________________________|");
        System.out.println("|____2_____|___The hound___|___400____|_____20___|____6____|_____4____|_______________________________________________|");
        System.out.println("|---------------------------------------------------------------------------------------------------------------------|");
        System.out.println("|_____________________________________________________________________________________________________________________|");
        System.out.println("|_____________________________________MAGES___________________________________________________________________________|");
        System.out.println("|__NUMBER__|____NAME_______|__HEALTH__|__ATTACT__|_DEFENCE__|_SPELL_POWER_|_MANA_POOL_|_MANA_COST_|_HIT_TIMES_|_RANGE_|");
        System.out.println("|____3_____|___Melisandre__|___160____|_____10___|____1_____|_____40______|____300____|____30_____|_____5_____|___6___|");
        System.out.println("|____4_____|_Thoros of Myr_|___250____|_____25___|____3_____|_____15______|____150____|____50_____|_____3_____|___3___|");
        System.out.println("|---------------------------------------------------------------------------------------------------------------------|");
        System.out.println("|_____________________________________ROGUES__________________________________________________________________________|");
        System.out.println("|__NUMBER__|_____NAME______|__HEALTH__|__ATTACT__|__DEFENCE_|_____COST____|___________________________________________|");
        System.out.println("|____5_____|__Arya Stark___|___150____|____40____|_____2____|______20_____|___________________________________________|");
        System.out.println("|____6_____|_____Bronn_____|___250____|____35____|_____3____|______60_____|___________________________________________|");
        System.out.println("|_____________________________________________________________________________________________________________________|");

    }


    public void Level(String path,int level)
    {
        boardLevel board = new boardLevel(path,level,player);
        board.build();
        this.MainBoard = new Board(board.board,board.enemyList,player,this);
    }

    public void PrintBoard()
    {
        for(int i=0;i<this.MainBoard.getBoard().length;i++) {
            for (int j = 0; j < MainBoard.getBoard()[0].length; j++) {
                char forprint=MainBoard.getBoard()[i][j].getTile();
                System.out.print(forprint);
            }
            System.out.println();
        }

    }
}