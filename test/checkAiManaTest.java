import actions.AIOpponent;
import actions.HumanPlayer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;

public class checkAiManaTest {
    @Test
        public void checkAiMana(){
            CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		    BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		// As we are not starting the front-end, we have no GameActor, so lets manually create
		// the components we want to test
		    GameState gameState = new GameState(); // create state storage
		    Initalize initalizeProcessor =  new Initalize(); // create an initalize event processor
		
		    ObjectNode eventMessage = Json.newObject(); // create a dummy message
		    initalizeProcessor.processEvent(null, gameState, eventMessage); // send it to the initalize event processor
		
		    assertTrue(AIOpponent.AIStats.getMana()==2); // check if initial mana of AI avatar is 20
		
        }
}
