package demo;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class DrawTile {
    public static void executeDemo(ActorRef out) {
            for(int i =0;i<9;i++){
                for(int j =0;j<5;j++){
                    		// drawTile
		BasicCommands.addPlayer1Notification(out, "drawingBoard", 2);
		Tile tile = BasicObjectBuilders.loadTile(i, j);
		BasicCommands.drawTile(out, tile, 0);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
                }
            }
        		// Player Cards
		BasicCommands.addPlayer1Notification(out, "Player Test", 2);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// setPlayer1Health
		BasicCommands.addPlayer1Notification(out, "setPlayer1Health", 2);
		Player humanPlayer = new Player(20, 0);
		BasicCommands.setPlayer1Health(out, humanPlayer);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// setPlayer1Health
		BasicCommands.addPlayer1Notification(out, "setPlayer2Health", 2);
		Player aiPlayer = new Player(20, 0);
		BasicCommands.setPlayer2Health(out, aiPlayer);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// Mana
		for (int m = 0; m<10; m++) {
			BasicCommands.addPlayer1Notification(out, "setPlayer1Mana ("+m+")", 1);
			humanPlayer.setMana(m);
			BasicCommands.setPlayer1Mana(out, humanPlayer);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}

		// Mana
		for (int m = 0; m<10; m++) {
			BasicCommands.addPlayer1Notification(out, "setPlayer2Mana ("+m+")", 1);
			aiPlayer.setMana(m);
			BasicCommands.setPlayer2Mana(out, aiPlayer);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}

		// Player Hand Test
		BasicCommands.addPlayer1Notification(out, "Player Hand Test", 2);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}      
        
        
		String[] deck1Cards = {
				StaticConfFiles.c_azure_herald,
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

        int playerHandCount = 0;

		for (String deck1CardFile : deck1Cards) {
			// drawCard [1]
            if(playerHandCount <= 5 ){
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

			// // deleteCard [1]
			// BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
			// BasicCommands.deleteCard(out, 1);
			// try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}

        
	 
    }
}
