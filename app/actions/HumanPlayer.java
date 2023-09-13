package actions;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import structures.basic.*;


public class HumanPlayer {

    public static String[] humanPlayerCards = {
				StaticConfFiles.c_azure_herald,
				StaticConfFiles.c_truestrike,
				StaticConfFiles.c_azurite_lion,
				StaticConfFiles.c_comodo_charger,
				StaticConfFiles.c_fire_spitter,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_ironcliff_guardian,
				StaticConfFiles.c_pureblade_enforcer,
				StaticConfFiles.c_silverguard_knight,
				StaticConfFiles.c_sundrop_elixir,
				StaticConfFiles.c_truestrike,
                StaticConfFiles.c_azurite_lion,
				StaticConfFiles.c_comodo_charger,
				StaticConfFiles.c_fire_spitter,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_ironcliff_guardian,
				StaticConfFiles.c_pureblade_enforcer,
				StaticConfFiles.c_silverguard_knight,
				StaticConfFiles.c_sundrop_elixir,
				StaticConfFiles.c_truestrike
	};

	public static Player humanStats;
	
	public static  Card[] PlayerHand = new Card[6];


    // public HumanPlayer(){
	// 	super(20,2);
		
	// }


    
    public static void loadPlayer(ActorRef out){

        BasicCommands.addPlayer1Notification(out, "setPlayer1Health", 2);
		humanStats = new Player(20, 2);
		BasicCommands.setPlayer1Health(out, humanStats);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		

        // Mana
		for (int m = 0; m<2; m++) {
			BasicCommands.addPlayer1Notification(out, "setPlayer1Mana ("+m+")", 1);
			BasicCommands.setPlayer1Mana(out, humanStats);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		ObjectMapper mapper = new ObjectMapper();
        int playerHandCount = 0;
		
		for (String deck1CardFile : humanPlayerCards) {
			// drawCard [1]

            if(playerHandCount <= 2 ){
    			try{

    				String json = readFileAsString(deck1CardFile);
    				Card card=mapper.readValue(json,Card.class);
    				System.out.println(card.getBigCard().getHealth());
    				PlayerHand[playerHandCount] = card;
    				// BigCard bigcard=mapper.readValue(card.getBigCard(),BigCard.class);

    				// System.out.println(bigcard);
    			}
    			catch(Exception e){
    				e.printStackTrace();
    			}
            	
                BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
			    Card card = BasicObjectBuilders.loadCard(deck1CardFile, 0, Card.class);
			    BasicCommands.drawCard(out, card, playerHandCount, 0);
			    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

			// drawCard [1] Highlight
			BasicCommands.addPlayer1Notification(out, deck1CardFile+" Highlight", 2);
			BasicCommands.drawCard(out, card, playerHandCount, 1);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

            }
            playerHandCount++;
		}

		// addUnit(out);

	}

	public static void addUnit(ActorRef out) {
		// loadTile for unit
		int x = 1;
		int y = 2;
		Tile tile = BasicObjectBuilders.loadTile(x, y);
		// drawUnit
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		unit.setPositionByTile(tile);
		BasicCommands.drawUnit(out, unit, tile);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// setUnitAttack
		BasicCommands.addPlayer1Notification(out, "setUnitAttack", 2);
		BasicCommands.setUnitAttack(out, unit, 2);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// setUnitHealth
		BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
		BasicCommands.setUnitHealth(out, unit, 20);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// assignHighlightUnit(out, x, y);
	}

	public static void assignHighlightUnit(ActorRef out, int x, int y) {
		for (int i = 1; i < 3; i++) {
			// Highlight tiles in x direction with increment
			int position = x + i;
			highlightXUnit(out, position, y, i, true);
			// Highlight tiles in x direction with decrement
			position = x - i;
			highlightXUnit(out, position, y, i, true);
			// Highlight tiles in y direction with increment
			position = y + i;
			highlightXUnit(out, x, position, i, false);
			// Highlight tiles in y direction with decrement
			position = y - i;
			highlightXUnit(out, x, position, i, false);
		}
	}

	public static void highlightXUnit(ActorRef out, int x, int y, int index, boolean isDiagonal) {
		// drawTile
		System.out.print(x + " " + y);
		Tile tile = BasicObjectBuilders.loadTile(x, y);
		// int y = 2;
		if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
			BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
			BasicCommands.drawTile(out, tile, 1);
			if (index < 2 && isDiagonal) {
				highlightXUnit(out, x, y + 1, 0, false);
				highlightXUnit(out, x, y - 1, 0, false);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
	// public static void highlightYUnit(ActorRef out, int x, int y, int index) {
	// 	// drawTile
	// 	Tile tileCheck = BasicObjectBuilders.loadTile(x, y);
	// 	if (y >= 0 && y <= 4) {
	// 		BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
	// 		BasicCommands.drawTile(out, tileCheck, 1);
	// 		try {
	// 			Thread.sleep(100);
	// 		} catch (InterruptedException e) {
	// 			e.printStackTrace();
	// 		}
	// 	}
	// }

	// public static void highlightXYUnit(ActorRef out, int x, int y) {
	// 	// drawTile
	// 	Tile tileCheck = BasicObjectBuilders.loadTile(x, y);
	// 	if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
	// 		BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
	// 		BasicCommands.drawTile(out, tileCheck, 1);
	// 		try {
	// 			Thread.sleep(100);
	// 		} catch (InterruptedException e) {
	// 			e.printStackTrace();
	// 		}
	// 	}
	// }

}
