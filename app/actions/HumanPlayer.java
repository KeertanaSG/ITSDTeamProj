package actions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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

	public static ArrayList<Integer> humanCardNumbers = new ArrayList<Integer>();

	public static Card[] deployedCards = new Card[20];

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

	/// maintains all deployed cards

	public static ArrayList<Card> humanCards = new ArrayList<Card>();

	public static int getHealth(ActorRef out) {
		return 0;
	}

	public static Player humanStats;

	public static String[] PlayerHand = new String[6];

	public static int currentCardNumber = 1;

	public static int humanAttackPower = 2;

	public static Card getCard(ActorRef out, Unit unit) {
		ObjectMapper mapper = new ObjectMapper();
		for (String eachCard : humanPlayerCards) {
			try {
				String json = readFileAsString(eachCard);
				Card card = mapper.readValue(json, Card.class);
				if (unit.getId() == card.getId()) {
					return card;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new Card();

	}

	public static void loadPlayer(ActorRef out) {

		BasicCommands.addPlayer1Notification(out, "setPlayer1Health", 2);
		humanStats = new Player(20, 2);
		BasicCommands.setPlayer1Health(out, humanStats);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Mana
		for (int m = 0; m < 2; m++) {
			int n = m + 1;
			BasicCommands.addPlayer1Notification(out, "setPlayer1Mana (" + n + ")", 1);
			BasicCommands.setPlayer1Mana(out, humanStats);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		int playerHandCount = 0;

		for (String deck1CardFile : humanPlayerCards) {
			// drawCard [1]

			// change to unit and upload number!
			try {

				String json = readFileAsString(deck1CardFile);
				Card card = mapper.readValue(json, Card.class);
				humanCardNumbers.add(card.getId());

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (playerHandCount <= 2) {
				currentCardNumber += 1;

				PlayerHand[playerHandCount] = deck1CardFile;

				BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
				// TODO Add ID for each card uniquely
				Card card = BasicObjectBuilders.loadCard(deck1CardFile, 0, Card.class);
				BasicCommands.drawCard(out, card, playerHandCount, 0);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// drawCard [1] Highlight
				// BasicCommands.addPlayer1Notification(out, deck1CardFile+" Highlight", 2);
				// BasicCommands.drawCard(out, card, playerHandCount, 1);

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

	public static String readFileAsString(String file) throws Exception {
		return new String(Files.readAllBytes(Paths.get(file)));
	}
	// public static void highlightYUnit(ActorRef out, int x, int y, int index) {
	// // drawTile
	// Tile tileCheck = BasicObjectBuilders.loadTile(x, y);
	// if (y >= 0 && y <= 4) {
	// BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
	// BasicCommands.drawTile(out, tileCheck, 1);
	// try {
	// Thread.sleep(100);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// public static void highlightXYUnit(ActorRef out, int x, int y) {
	// // drawTile
	// Tile tileCheck = BasicObjectBuilders.loadTile(x, y);
	// if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
	// BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
	// BasicCommands.drawTile(out, tileCheck, 1);
	// try {
	// Thread.sleep(100);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	public static void deleteCard(ActorRef out) {

		System.out.println("inside delete card!");

		BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
		BasicCommands.deleteCard(out, GameState.selectedHandPosition);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		PlayerHand[GameState.selectedHandPosition] = null;

		// move cards

	}

	public static void addNewCard(ActorRef out) {
		System.out.println("adding a new hard");

		// int currentPosition = 2;
		int position = 0;

		for (int i = 0; i < PlayerHand.length; i++) {

			if (PlayerHand[i] == null) {
				break;
			} else {
				position += 1;
			}
		}

		// PlayerHand[position] = null;
		// BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
		// BasicCommands.deleteCard(out, currentPosition);
		// try {Thread.sleep(1000);} catch (InterruptedException e)
		// {e.printStackTrace();}
		/// load new card if available
		System.out.println(position + " this is position");
		if (position < 6 && currentCardNumber < humanPlayerCards.length) {
			BasicCommands.addPlayer1Notification(out, humanPlayerCards[currentCardNumber], 2);
			Card card = BasicObjectBuilders.loadCard(humanPlayerCards[currentCardNumber], 0, Card.class);
			BasicCommands.drawCard(out, card, position, 0);
			PlayerHand[position] = humanPlayerCards[currentCardNumber];
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentCardNumber += 1;

		} else if (position >= 6) {
			// TODO write logic loose cards
			currentCardNumber += 1;
		}

		/*
		 * @dev when no card to draw and no code in hand! Player loose
		 */
		if(currentCardNumber >= HumanPlayer.humanCards.size()){
				boolean checkHandEmpty = true;
				for(int i = 0; i < PlayerHand.length;i++ ){
					if(PlayerHand[i] != null){
						checkHandEmpty = false;
					}
				}

				if(checkHandEmpty) { 
					BasicCommands.addPlayer1Notification(out, "Game Ended! AI won!", 2);
					GameState.gameEnded = true;
				}
			}

	}

	public static int getUnitAttack(ActorRef out, Unit unit) {
		if (unit != null) {
			System.out.println(unit.getId() + " we are attack");
			if (unit.getId() == 100) {
				Card a = HumanPlayer.getCard(out, unit);
				return a.getBigCard().getAttack();
			} else if (unit.getId() == 200) {
				Card a = AIOpponent.getCard(out, unit);
				return a.getBigCard().getAttack();
			} else {
				Card curCard = AIOpponent.getCard(out, unit);
				if (curCard != null) {
					System.out.println(curCard.getBigCard().getHealth() + " this is unit health!");
					return curCard.getBigCard().getAttack();
				}
			}
		}
		return 2;
	}
}
