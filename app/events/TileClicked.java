package events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;
import scala.concurrent.java8.FuturesConvertersImpl.P;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * 
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {
	int storedX = 0;
	int storedY = 0;
	public static boolean highlighted[][]=new boolean[100][100];
	public void showAllPossibleMoves(int x, int y) {

		// drawTile
		// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
		// BasicCommands.drawTile(out, tile, 2);
		// try {Thread.sleep(2000);} catch (InterruptedException e)
		// {e.printStackTrace();}

	}
	
    public static String[] AllUnits = {
				StaticConfFiles.u_azure_herald,
				StaticConfFiles.u_azurite_lion,
				StaticConfFiles.u_blaze_hound,
				StaticConfFiles.u_bloodshard_golem,
				StaticConfFiles.u_comodo_charger,
				StaticConfFiles.u_fire_spitter,
				StaticConfFiles.u_hailstone_golem,
				StaticConfFiles.u_hailstone_golemR,
				StaticConfFiles.u_ironcliff_guardian,
				StaticConfFiles.u_planar_scout,
				StaticConfFiles.u_pureblade_enforcer,
				StaticConfFiles.u_pyromancer,
				StaticConfFiles.u_rock_pulveriser,
				StaticConfFiles.u_serpenti,
				StaticConfFiles.u_silverguard_knight,
				StaticConfFiles.u_windshrike,

	};
    
    
	public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
	
	
	public String getUnit(Card card) {
		ObjectMapper mapper = new ObjectMapper();
		for (String eachUnit : AllUnits) {
			
			try{

				String json = readFileAsString(eachUnit);
				Unit unit=mapper.readValue(json,Unit.class);
//				System.out.println();
				if(unit.getId() == card.getId()) {
					return eachUnit;
				}
//				PlayerHand[playerHandCount] = card;
				// BigCard bigcard=mapper.readValue(card.getBigCard(),BigCard.class);

				// System.out.println(bigcard);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
		
		

		
		
		return null;
	}

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);

		System.out.println(GameState.gameBoard[tilex][tiley]);
		if (!gameState.gameEnded) {
			
			if(gameState.isCardSelected && GameState.gameBoard[tilex][tiley] == null ) {
			    Tile newTile = BasicObjectBuilders.loadTile(tilex, tiley);
				BasicCommands.addPlayer1Notification(out, "Deploying Human Avatar", 2);
//				gameState.currentCard.getId();
				
//				Unit newUnit = ;
//				Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
				
			    Unit unit = BasicObjectBuilders.loadUnit(getUnit(gameState.currentCard), 0, Unit.class);
			    unit.setPositionByTile(tile); 
			    BasicCommands.drawUnit(out, unit, tile);
				 try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				GameState.gameBoard[tilex][tiley] = unit;
//				GameState.playerTile[1][2] = 1;
			}

			if (GameState.currentPlayer == "human") {
				System.out.println("hello " + " " + tilex + " " + tiley);
				Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
				if (GameState.gameBoard[tilex][tiley] != null && !GameState.isUnitSelected) {
					// Unit currentUnit = GameState.gameBoard[tilex][tiley];

					// showAllPossibleMoves(tilex,tiley);
					assignHighlightUnit(out, tilex, tiley);

					// if (tilex - 1 >= 0 && tiley - 1 >= 0) {
					// if (GameState.gameBoard[tilex - 1][tiley - 1] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex - 1, tiley - 1);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 2);
					// }
					// if (GameState.gameBoard[tilex - 1][tiley] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex - 1, tiley);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 2);
					// }
					// if (GameState.gameBoard[tilex][tiley - 1] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex, tiley - 1);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 2);
					// }

					// }

					// if (tilex - 1 >= 0 && tilex + 1 <= 4 && tiley - 1 >= 0 && tiley + 1 <= 8) {
					// if (GameState.gameBoard[tilex - 1][tiley + 1] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex - 1, tiley + 1);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 2);
					// }
					// if (GameState.gameBoard[tilex][tiley + 1] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex, tiley + 1);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 2);
					// }
					// if (GameState.gameBoard[tilex][tiley + 1] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex, tiley + 1);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 2);
					// }
					// if (GameState.gameBoard[tilex + 1][tiley - 1] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex + 1, tiley - 1);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 2);
					// }
					// if (GameState.gameBoard[tilex + 1][tiley] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex + 1, tiley);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 2);
					// BasicCommands.drawTile(out, anotherTile, 0);
					// }
					// if (GameState.gameBoard[tilex + 1][tiley + 1] == null) {
					// Tile anotherTile = BasicObjectBuilders.loadTile(tilex + 1, tiley + 1);
					// BasicCommands.addPlayer1Notification(out, "drawTile[3,2] Red Highlight", 1);
					// BasicCommands.drawTile(out, anotherTile, 1);
					// }
					// }

					GameState.isUnitSelected = true;
					GameState.playerTile[storedX][storedY] = 0;
					storedX=tilex;
					storedY=tiley;
				} else if (GameState.gameBoard[tilex][tiley] == null && !GameState.isUnitSelected) {

					// check if card selected

					// else throw error to select a card
					// if(GameState.cardSelected) {
					// Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, 0,
					// Unit.class);
					// unit.setPositionByTile(tile);
					// BasicCommands.drawUnit(out, unit, tile);
					// GameState.gameBoard[tilex][tiley] = unit;
					// try {
					// Thread.sleep(2000);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }
					// }

				} else if (GameState.gameBoard[tilex][tiley] == null && GameState.isUnitSelected && highlighted[tilex][tiley]) {
					System.out.println("hello 55 " + tilex + " " + tiley);
					// move the unit && GameState.playerTile[tilex][tiley] != 0
					
					//BasicCommands.addPlayer1Notification(out, String.valueOf(highlighted[tilex][tiley]), 2);
					BasicCommands.addPlayer1Notification(out, "moveUnitToTile", 2);
					BasicCommands.moveUnitToTile(out, unit, tile);
					unit.setPositionByTile(tile);
					removeHighlights(out, storedX, storedY);
					GameState.isUnitSelected = false;
					GameState.gameBoard[storedX][storedY] = null;
					GameState.gameBoard[tilex][tiley] = unit;
					GameState.playerTile[storedX][storedY] = 0;
					highlighted[tilex][tiley]=false;
					storedX = tilex;
					storedY = tiley;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// removeHighlights(out, storedX, storedY);
					// GameState.isUnitSelected = false;
					// GameState.gameBoard[storedX][storedY] = null;
					// GameState.gameBoard[tilex][tiley] = unit;
					// GameState.playerTile[storedX][storedY] = 0;
					// highlighted[tilex][tiley]=false;
					
					
				}
				else if (GameState.gameBoard[tilex][tiley] == null && GameState.isUnitSelected && !highlighted[tilex][tiley]) {
					System.out.println("hello 55 " + tilex + " " + tiley);
					// move the unit && GameState.playerTile[tilex][tiley] != 0
					
					//BasicCommands.addPlayer1Notification(out, String.valueOf(highlighted[tilex][tiley]), 2);
					// BasicCommands.addPlayer1Notification(out, "moveUnitToTile", 2);
					// BasicCommands.moveUnitToTile(out, unit, tile);
					// unit.setPositionByTile(tile);
					removeHighlights(out, storedX, storedY);
					GameState.isUnitSelected = false;
					GameState.gameBoard[storedX][storedY] = null;
					GameState.gameBoard[storedX][storedY] = unit;
					GameState.playerTile[storedX][storedY] = 0;
					highlighted[storedX][storedY]=false;
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// removeHighlights(out, storedX, storedY);
					// GameState.isUnitSelected = false;
					// GameState.gameBoard[storedX][storedY] = null;
					// GameState.gameBoard[tilex][tiley] = unit;
					// GameState.playerTile[storedX][storedY] = 0;
					// highlighted[tilex][tiley]=false;
					// storedX = 0;
					// storedY = 0;
					
					
				}

				else if (GameState.gameBoard[tilex][tiley] != null && GameState.isUnitSelected && highlighted[tilex][tiley]) {
					// if(opponent) then attack
					// playUnitAnimation [Attack]
					BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
					BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
					removeHighlights(out, storedX, storedY);
					GameState.isUnitSelected = false;
					GameState.gameBoard[storedX][storedY] = null;
					GameState.gameBoard[storedX][storedY] = unit;
					GameState.playerTile[storedX][storedY] = 0;
					highlighted[storedX][storedY]=false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else if (GameState.gameBoard[tilex][tiley] != null && GameState.isUnitSelected && !highlighted[tilex][tiley]){
					removeHighlights(out, storedX, storedY);
					GameState.isUnitSelected = false;
					GameState.gameBoard[storedX][storedY] = null;
					GameState.gameBoard[storedX][storedY] = unit;
					GameState.playerTile[storedX][storedY] = 0;
					highlighted[storedX][storedY]=false;
			
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
					// else if(Same) then throw error
				else {
					removeHighlights(out, storedX, storedY);
					GameState.isUnitSelected = false;
					GameState.gameBoard[storedX][storedY] = null;
					GameState.gameBoard[storedX][storedY] = unit;
					GameState.playerTile[storedX][storedY] = 0;
					highlighted[storedX][storedY]=false;
			
					// try {
					// 	Thread.sleep(100);
					// } catch (InterruptedException e) {
					// 	e.printStackTrace();
					// }
					
				}

			}

			// call AIOpponentMove()
			//storedX = tilex;
			//storedY = tiley; 
		}
	}

	public static void assignHighlightUnit(ActorRef out, int x, int y) {
		for (int i = 1; i < 3; i++) {
			// Highlight tiles in x direction with increment
			int position = x + i;
			highlightUnit(out, position, y, i, true);
			// Highlight tiles in x direction with decrement
			position = x - i;
			highlightUnit(out, position, y, i, true);
			// Highlight tiles in y direction with increment
			position = y + i;
			highlightUnit(out, x, position, i, false);
			// Highlight tiles in y direction with decrement
			position = y - i;
			highlightUnit(out, x, position, i, false);
		}
	}
	
	public static void highlightUnit(ActorRef out, int x, int y, int index, boolean isDiagonal) {
		// drawTile
		
		// int y = 2;
		if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
			//if (GameState.gameBoard[x][y] == null && GameState.isUnitSelected) {
				highlighted[x][y]=true;
				Tile tile = BasicObjectBuilders.loadTile(x, y);
				GameState.playerTile[x][y] = 1;
				BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
				BasicCommands.drawTile(out, tile, 1);
				if (index < 2 && isDiagonal) {
					highlightUnit(out, x, y + 1, 0, false);
					highlightUnit(out, x, y - 1, 0, false);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			//}
		}
	}

	public static void removeHighlights(ActorRef out, int x, int y) {
		for (int i = 1; i < 3; i++) {
			// Highlight tiles in x direction with increment
			int position = x + i;
			removeUnitHighlight(out, position, y, i, true);
			// Highlight tiles in x direction with decrement
			position = x - i;
			removeUnitHighlight(out, position, y, i, true);
			// Highlight tiles in y direction with increment
			position = y + i;
			removeUnitHighlight(out, x, position, i, false);
			// Highlight tiles in y direction with decrement
			position = y - i;
			removeUnitHighlight(out, x, position, i, false);
		}
	}

	public static void removeUnitHighlight(ActorRef out, int x, int y, int index, boolean isDiagonal) {
		// drawTile
		if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
			//if (GameState.gameBoard[x][y] == null) {
				highlighted[x][y]=false;
				System.out.println("game player tile " + GameState.playerTile[x][y]);
				Tile tile = BasicObjectBuilders.loadTile(x, y);
				BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
				BasicCommands.drawTile(out, tile, 0);
				if (index == 1 && isDiagonal) {
					removeUnitHighlight(out, x, y + 1, 0, false);
					removeUnitHighlight(out, x, y - 1, 0, false);
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// GameState.playerTile[x][y] = 0;
			//}
		}
	}

}

//
