package events;


import com.fasterxml.jackson.databind.JsonNode;

import actions.HumanPlayer;
import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt();

		System.out.println(handPosition + " this is handPositon!");
		
		gameState.currentCard = HumanPlayer.PlayerHand[handPosition];
		
		gameState.isCardSelected = true;
		
		

		//highlight card and change player one selected Card

		
		
		
	}

}
