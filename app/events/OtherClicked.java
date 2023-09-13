package events;

import com.fasterxml.jackson.databind.JsonNode;

import actions.HumanPlayer;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

	if(GameState.gameEnded == true){

		BasicCommands.addPlayer1Notification(out,"Game ended Already!" , 2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}	
		return;
	}

	TileClicked.removeHighlight(out);
		
		
	}

}


