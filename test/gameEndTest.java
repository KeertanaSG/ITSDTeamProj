import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import actions.HumanPlayer;
import actions.HumanPlayer.*;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import events.TileClicked;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;

/**
 * This is an example of a JUnit test. In this case, we want to be able to test the logic
 * of our system without needing to actually start the web server. We do this by overriding
 * the altTell method in BasicCommands, which means whenever a command would normally be sent
 * to the front-end it is instead discarded. We can manually simulate messages coming from the
 * front-end by calling the processEvent method on the appropriate event processor.
 * @author Richard
 *
 */

	/**
	 * This test simply checks that a boolean vairable is set in GameState when we call the
	 * initalize method for illustration.
	 */

public class gameEndTest {

    @Test
    public void checkGameEnded(){
    CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
    BasicCommands.altTell = altTell; // specify that the alternative tell should be used
    
    // As we are not starting the front-end, we have no GameActor, so lets manually create
    // the components we want to test
    GameState gameState = new GameState(); // create state storage
    TileClicked tileclicked =  new TileClicked(); // create an TileClicked event processor

    //tileclicked.processEvent(null, gameState, null);
    
    assertFalse(gameState.gameEnded); // check that game is still running
    HumanPlayer.humanStats.setHealth(0); // Simulate game end by setting human player health to 0
    ObjectNode eventMessage = Json.newObject(); // create a dummy message
    tileclicked.processEvent(null, gameState, eventMessage); // send it to the tileclicked event processor
    
    assertTrue(gameState.gameEnded); // check gameEnd is now true in updated GameState
   
    }
}
