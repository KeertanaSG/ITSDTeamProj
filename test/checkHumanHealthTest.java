import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import actions.AIOpponent;
import actions.HumanPlayer;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import events.TileClicked;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;
public class checkHumanHealthTest {
    @Test
    public void checkHumanHealth(){
    CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
    BasicCommands.altTell = altTell; // specify that the alternative tell should be used
    // As we are not starting the front-end, we have no GameActor, so lets manually create
    // the components we want to test
    GameState gameState = new GameState(); // create state storage
    Initalize initalizeProcessor =  new Initalize();
    ObjectNode eventMessage = Json.newObject(); // create a dummy message
	initalizeProcessor.processEvent(null, gameState, eventMessage); // send it to the initalize event processor
	assertTrue(HumanPlayer.humanStats.getHealth()==20); //checking initial health is 20	
    //TileClicked tileclicked=new TileClicked();
    //tileclicked.processEvent(null, gameState, null);
    
    }
}
