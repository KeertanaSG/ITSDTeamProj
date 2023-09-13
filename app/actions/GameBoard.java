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
                                gameBoard[i][j] = tile;
                                // try {
                                //         Thread.sleep(100);
                                // } catch (InterruptedException e) {
                                //         e.printStackTrace();
                                // }
                        }
                }

                // GameState.gameBoard = gameBoard;

        }

        //moveUnit

        //attack

        //endTurn

}
