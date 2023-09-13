package actions;

import java.util.*;
import akka.actor.ActorRef;
import commands.BasicCommands;

import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class GameBoard {

        public static Tile[][] gameBoard = new Tile[9][5];
        public static void drawBoard(ActorRef out) {
                for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 5; j++) {
                                // drawTile
                                BasicCommands.addPlayer1Notification(out, "drawingBoard", 2);
                                Tile tile = BasicObjectBuilders.loadTile(i, j);
                                BasicCommands.drawTile(out, tile, 0);
                                try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                                gameBoard[i][j] = tile;
   
                        }
                }



        }

        //moveUnit

        //attack
        public static void attack(){
                if(GameState.currentCard.getBigCard().getHealth() == -1){
                        //operations for spell
                }
                else{
                        //operations for unit
                        if(GameState.currentCard.getCardname() == "Azure Herald"){
                                        // HumanPlayer.humanStats.setHealth(0);
                        }
                }
                
        }

        //endTurn

}
