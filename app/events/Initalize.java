package events;
import com.fasterxml.jackson.databind.JsonNode;
import actions.AIOpponent;
import actions.GameBoard;
import actions.HumanPlayer;
import akka.actor.ActorRef;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import demo.DrawTile;
import structures.GameState;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change

		// CommandDemo.executeDemo(out);

		// return;
		
		gameState.gameInitalised = true;
		
		// gameState.something = true;

		// DrawTile.executeDemo(out);
		if(!gameState.gameEnded){
			GameBoard.drawBoard(out);
			HumanPlayer.loadPlayer(out);
			System.out.println("this is it!");
		    AIOpponent ai = new AIOpponent();

			
            ai.drawCards(out);
            ai.setHealthAndMana(out);

		    Tile tileHuman = BasicObjectBuilders.loadTile(1, 2);
			BasicCommands.addPlayer1Notification(out, "Deploying Human Avatar", 2);
		    Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		    unit.setPositionByTile(tileHuman); 
		    BasicCommands.drawUnit(out, unit, tileHuman);
			 try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			GameState.gameBoard[1][2] = unit;
			GameState.playerTile[1][2] = 1;

			//setUnitAttack
			BasicCommands.setUnitAttack(out, unit, 2);
		    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		    // setUnitHealth
		    BasicCommands.setUnitHealth(out, unit, 20);
		    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.addPlayer1Notification(out, "Deploying Human Avatar", 2);


			Tile tileAIOpponent = BasicObjectBuilders.loadTile(7, 2);
			BasicCommands.addPlayer1Notification(out, "Deploying AI Avatar", 2);
		    Unit AIUnit = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
		    AIUnit.setPositionByTile(tileAIOpponent); 
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			BasicCommands.drawUnit(out, AIUnit, tileAIOpponent);
			GameState.gameBoard[7][2] = AIUnit;
			 try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			System.out.println("here!! 12345");
		    // setUnitHealth
		    BasicCommands.setUnitHealth(out, AIUnit, 20);
		    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			System.out.println("here!! 1234");
			
			//setUnitAttack
			BasicCommands.setUnitAttack(out, AIUnit, 2);
		    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		



		    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			GameState.currentPlayer = "human";

		    
		}else{

		}

	}

}


