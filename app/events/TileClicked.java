package events;

import actions.AIOpponent;
import actions.GameBoard;
import actions.HumanPlayer;
import akka.actor.ActorRef;
import akka.actor.typed.ActorRef.ActorRefOps;
import akka.protobufv3.internal.Api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.BasicCommands;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import play.libs.Json;
import scala.concurrent.java8.FuturesConvertersImpl.P;
import structures.GameState;
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


  /**
   * Array list to store all possible movable positions of the selected unit on the board
   */
  public static ArrayList<ArrayList<Integer>> permissiblePositions = new ArrayList<ArrayList<Integer>>();
  

  /**
   * Array lists to keep track all activities of the current player
   */
  public static ArrayList<Unit> movedUnits = new ArrayList<Unit>();
  public static ArrayList<Unit> attackedUnits = new ArrayList<Unit>();
  public static ArrayList<Unit> currentlyDeployedUnits = new ArrayList<Unit>();

  /**
   * List of all unit of human player
   */

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


/**
   * 
   * special method to update attack Silverguard Knight Card
   */
  public static void updateAttackOfSilverKnight(ActorRef out) {
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 5; j++) {
        if (GameState.gameBoard[i][j] != null) {
          if (GameState.gameBoard[i][j].getId() == 15) {
            for (int k = 0; k < HumanPlayer.humanCards.size(); k++) {
              if (HumanPlayer.humanCards.get(k).getId() == 15) {
                int newAttack =
                  HumanPlayer.humanCards.get(k).getBigCard().getAttack() + 2;
                BasicCommands.setUnitAttack(
                  out,
                  GameState.gameBoard[i][j],
                  newAttack
                );
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        }
      }
    }
  }

  public static String readFileAsString(String file) throws Exception {
    return new String(Files.readAllBytes(Paths.get(file)));
  }

/**
   * 
   *get current unit of the card
   */

  public String getUnit(Card card) {
    ObjectMapper mapper = new ObjectMapper();
    for (String eachUnit : AllUnits) {
      try {
        String json = readFileAsString(eachUnit);
        Unit unit = mapper.readValue(json, Unit.class);
        if (unit.getId() == card.getId()) {
          return eachUnit;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 
   *Highlight the tile based on the tile state
   */

  public static void highlightTile(ActorRef out, int x, int y) {

    if (GameState.gameBoard[x][y] != null) {
      System.out.println(GameState.gameBoard[x][y].getId() + " this pos");
      if (
        HumanPlayer.humanCardNumbers.contains(
          GameState.gameBoard[x][y].getId()
        ) ||
        GameState.gameBoard[x][y].getId() == 100
      ) {
        //do not hughlight and add in highlighted list!
        System.out.println("yes human contains");
        System.out.println(GameState.gameBoard[x][y].getId() + " this id");
      } else {
        // highlight in red and add in list
        //if provoked then set if provoked to true.

        System.out.println("inside here!");
        Tile tile = BasicObjectBuilders.loadTile(x, y);
        BasicCommands.addPlayer1Notification(out, "Highlight enemy", 2);
        BasicCommands.drawTile(out, tile, 2);
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
        eachPermissiblePosition.add(x);
        eachPermissiblePosition.add(y);
        permissiblePositions.add(eachPermissiblePosition);
        // try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

      }
    } else {
      //add in list
      Tile tile = BasicObjectBuilders.loadTile(x, y);
      BasicCommands.addPlayer1Notification(out, "Highlight", 2);
      BasicCommands.drawTile(out, tile, 1);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
      eachPermissiblePosition.add(x);
      eachPermissiblePosition.add(y);
      permissiblePositions.add(eachPermissiblePosition);

    }
  }


   /**
   *@dev method to highlight all possible move of a particular selected unit.
   */
  public void highlightPossibleMoves(ActorRef out, int x, int y) {
    System.out.println(GameState.gameBoard[x][y].getId());
    //logic for firespitter
    if (GameState.gameBoard[x][y].getId() == 6) {
      System.out.println("I am fire spitter!");
      //comefor
      for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 5; j++) {
          // if()
          if (GameState.gameBoard[i][j] != null) {
            highlightTile(out, i, j);
          }
        }
      }
    }
    if (y - 2 >= 0) {
      highlightTile(out, x, y - 2);
      System.out.println("isnide x,y-2");
    }

    if (y - 1 >= 0) {
      highlightTile(out, x, y - 1);
      System.out.println("isnide x,y-1");
    }

    if (y + 1 <= 4) {
      highlightTile(out, x, y + 1);
      System.out.println("isnide x,y+1");
    }
    if (y + 2 <= 4) {
      highlightTile(out, x, y + 2);
      System.out.println("isnide x,y+2");
    }

    if (x - 2 >= 0) {
      highlightTile(out, x - 2, y);
      System.out.println("isnide x-2,y");
    }

    if (x - 1 >= 0) {
      highlightTile(out, x - 1, y);
      System.out.println("isnide x-1,y");
    }

    if (x + 1 <= 8) {
      highlightTile(out, x + 1, y);
      System.out.println("isnide x+1,y");
    }

    if (x + 2 <= 8) {
      highlightTile(out, x + 2, y);
      System.out.println("isnide x+2,y");
    }

    if (x - 1 >= 0 && y - 1 >= 0) {
      highlightTile(out, x - 1, y - 1);
      System.out.println("isnide x-1,y-1");
    }

    if (x + 1 <= 8 && y - 1 >= 0) {
      highlightTile(out, x + 1, y - 1);
      System.out.println("isnide x+1,y-1");
    }

    if (x - 1 >= 0 && y + 1 <= 4) {
      highlightTile(out, x - 1, y + 1);
      System.out.println("isnide x-1,y+1");
    }

    if (x + 1 <= 8 && y + 1 <= 4) {
      highlightTile(out, x + 1, y + 1);
      System.out.println("isnide x+1,y+1");
    }
  }


    /**
   *@dev method to remove all hughlight before next move.
   */

  public static void removeHighlight(ActorRef out) {
    for (int i = 0; i < permissiblePositions.size(); i++) {
      Tile tile = BasicObjectBuilders.loadTile(
        permissiblePositions.get(i).get(0),
        permissiblePositions.get(i).get(1)
      );
      BasicCommands.addPlayer1Notification(out, "drawTile Highlight", 2);
      BasicCommands.drawTile(out, tile, 0);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    permissiblePositions.clear();
  }


  public static int getUnitAttack(ActorRef out, Unit unit) {
    System.out.println(unit.getId() + " we are attack");
    if (unit.getId() == 100) {
      return HumanPlayer.humanAttackPower;
    } else if (unit.getId() == 200) {
      // return AIOpponent.aiA;
      return AIOpponent.AIAttackPower;
      // Card a = HumanPlayer.getCard(out,unit);
      // return a.getBigCard().getAttack();
    } else {
      Card curCard = HumanPlayer.getCard(out, unit);
      System.out.println(
        curCard.getBigCard().getHealth() + " this is unit health!"
      );
      return curCard.getBigCard().getAttack();
    }
  }

  public static int getUnitHealth(ActorRef out, Unit unit) {
    System.out.println(unit.getId() + " we are health");
    if (unit.getId() == 100) {
      // HumanPlayer.humanStats.getHealth();
      return HumanPlayer.humanStats.getHealth();
    } else if (unit.getId() == 200) {
      return AIOpponent.AIStats.getHealth();
    } else {
      System.out.println("befor cur card");
      Card curCard = AIOpponent.getCard(out, unit);
      if (curCard == null) {
        curCard = HumanPlayer.getCard(out, unit);
      }

      System.out.println("this is curCard!" + curCard.getId());
      // System.out.println(curCard.getBigCard().getHealth() + " this is unit health!");
      return curCard.getBigCard().getHealth();
    }
  }

    /**
   *@dev method to check if counter unit is possible. Provoke/In range
   */

  public static boolean canCounterAttack(ActorRef out, int x, int y) {
 
    //check if the opposite unit is an adjacent unit

    int xDiff = Math.abs(GameState.selectedUnitX - x);
    int yDiff = Math.abs(GameState.selectedUnitY - y);
    System.out.println(xDiff + " this is xdiff " + yDiff);
    if (xDiff > 2 || yDiff > 2) {
      System.out.println("cannot counter attack!");
      return false;
    }
    return true;
  }

  public static boolean checkProvoke(ActorRef out, int x, int y) {
    boolean provoked = false;
    if (y - 1 >= 0) {
      if (GameState.gameBoard[x][y - 1] != null) {
        if (GameState.gameBoard[x][y - 1].getId() == 13) {
          provoked = true;
          highlightTile(out, x, y - 1);
        }
      }

      System.out.println("isnide x,y-1");
    }

    if (y + 1 <= 4) {
      if (GameState.gameBoard[x][y + 1] != null) {
        if (GameState.gameBoard[x][y + 1].getId() == 13) {
          provoked = true;
          highlightTile(out, x, y + 1);
        }
      }
    }

    if (x - 1 >= 0) {
      if (GameState.gameBoard[x - 1][y] != null) {
        if (GameState.gameBoard[x - 1][y].getId() == 13) {
          provoked = true;
          highlightTile(out, x - 1, y);
        }
      }
    }

    if (x + 1 <= 8) {
      if (GameState.gameBoard[x + 1][y] != null) {
        if (GameState.gameBoard[x + 1][y].getId() == 13) {
          provoked = true;
          highlightTile(out, x + 1, y);
        }
      }
    }

    if (x - 1 >= 0 && y - 1 >= 0) {
      if (GameState.gameBoard[x - 1][y - 1] != null) {
        if (GameState.gameBoard[x - 1][y - 1].getId() == 13) {
          provoked = true;
          highlightTile(out, x - 1, y - 1);
        }
      }
    }

    if (x + 1 <= 8 && y - 1 >= 0) {
      if (GameState.gameBoard[x + 1][y - 1] != null) {
        if (GameState.gameBoard[x + 1][y - 1].getId() == 13) {
          provoked = true;
          highlightTile(out, x + 1, y - 1);
        }
      }
    }

    if (x - 1 >= 0 && y + 1 <= 4) {
      if (GameState.gameBoard[x - 1][y + 1] != null) {
        if (GameState.gameBoard[x - 1][y + 1].getId() == 13) {
          provoked = true;
          highlightTile(out, x - 1, y + 1);
        }
      }
    }

    if (x + 1 <= 8 && y + 1 <= 4) {
      if (GameState.gameBoard[x + 1][y + 1] != null) {
        if (GameState.gameBoard[x + 1][y + 1].getId() == 13) {
          provoked = true;
          highlightTile(out, x + 1, y + 1);
        }
      }
    }

    ///check all 8 tiles if a provoke unit is prsent if present move that unit to permissible position and highlight the unit with red
    return provoked;
  }
  
    /**
   *@dev method to counter Attack the opponent
   */

  public static void counterAttack(ActorRef out, int x, int y) {
    System.out.println("inside counter attack!");
    if (canCounterAttack(out, x, y)) {
      // int reattack =	 AIOpponent.getUnitAttackg(out,GameState.gameBoard[x][y]);
      int reattack = AIOpponent.getUnitAttack(out, GameState.gameBoard[x][y]);
      System.out.println(reattack + " this is re-attack!");
      BasicCommands.addPlayer1Notification(
        out,
        "playUnitAnimation [Attack]",
        2
      );
      BasicCommands.playUnitAnimation(
        out,
        GameState.gameBoard[x][y],
        UnitAnimationType.attack
      );
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      //run a for loop for current health
      int reAttackCurrenttHealth = getUnitHealth(out, GameState.currentunit);
      if (GameState.currentunit.getId() == 100) {
        reAttackCurrenttHealth = HumanPlayer.humanStats.getHealth();
      }
      for (int i = 0; i < HumanPlayer.humanCards.size(); i++) {
        if (
          HumanPlayer.humanCards.get(i).getId() == GameState.currentunit.getId()
        ) {
          reAttackCurrenttHealth =
            HumanPlayer.humanCards.get(i).getBigCard().getHealth();
          break;
        }
      }

      int reAttackUpdatedHealth = reAttackCurrenttHealth - reattack;

      System.out.println(
        reAttackCurrenttHealth + " this is reattach current health"
      );

      System.out.println(
        reAttackUpdatedHealth + " this is reattack updated health"
      );

     
      if (GameState.currentunit.getId() == 100) {
        System.out.println("opposite player human");

        if (reAttackUpdatedHealth <= 0) {
          HumanPlayer.humanStats.setHealth(0);
          BasicCommands.setPlayer1Health(out, HumanPlayer.humanStats);
          BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
          BasicCommands.setUnitHealth(out, GameState.currentunit, 0);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          //death animation of avatar -- game ends here
          BasicCommands.addPlayer1Notification(out, "Game Ended! AI won!", 3);
          GameState.gameEnded = true;
          BasicCommands.playUnitAnimation(
            out,
            GameState.currentunit,
            UnitAnimationType.death
          );
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          updateAttackOfSilverKnight(out);
        }
        HumanPlayer.humanStats.setHealth(reAttackUpdatedHealth);
        BasicCommands.setPlayer1Health(out, HumanPlayer.humanStats);
        BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
        BasicCommands.setUnitHealth(
          out,
          GameState.currentunit,
          reAttackUpdatedHealth
        );
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        //death animation of avatar -- game ends here
        BasicCommands.addPlayer1Notification(out, "Counter attack", 3);
        // BasicCommands.playUnitAnimation(out, GameState.currentunit, UnitAnimationType.death);
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        updateAttackOfSilverKnight(out);
      } else {
        System.out.println("opposite not human player but human unit");
        for (int i = 0; i < HumanPlayer.humanCards.size(); i++) {
          System.out.println(
            "inside herre " +
            HumanPlayer.humanCards.get(i).getId() +
            " " +
            GameState.currentunit.getId()
          );
          if (
            HumanPlayer.humanCards.get(i).getId() ==
            GameState.currentunit.getId()
          ) {
            System.out.println("check point 1!");
            if (reAttackUpdatedHealth <= 0) {
              System.out.println("check point 2!");
              //will die
              HumanPlayer.humanCards.get(i).getBigCard().setHealth(0);
              BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
              BasicCommands.setUnitHealth(out, GameState.currentunit, 0);
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
			  System.out.println("error might be here 1");
              BasicCommands.addPlayer1Notification(out, "Unit dead!", 3);
              BasicCommands.playUnitAnimation(
                out,
                GameState.currentunit,
                UnitAnimationType.death
              );
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              //TODORemove unit
			  System.out.println("we are removing!" + GameState.selectedUnitX);
              BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
              BasicCommands.deleteUnit(out, GameState.currentunit);
            	// if (GameState.currentCard.getId() == 16) {
                //         AIOpponent.addNewCard(out);
                // }			  
              GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] =
                null;
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return;
            }
            System.out.println("check point 3!");
            System.out.println(
              HumanPlayer.humanCards.get(i).getBigCard().getHealth() +
              " this is here health"
            );
            HumanPlayer.humanCards
              .get(i)
              .getBigCard()
              .setHealth(reAttackUpdatedHealth);
            BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
            BasicCommands.setUnitHealth(
              out,
              GameState.currentunit,
              reAttackUpdatedHealth
            );
            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
      //else do this

    }
  }

    /**
   *@dev method to attack opposite player's unit
   */

  public void attackUnit(ActorRef out, int x, int y) {
    // if()

    if (
      HumanPlayer.humanCardNumbers.contains(
        GameState.gameBoard[x][y].getId()
      ) ||
      GameState.gameBoard[x][y].getId() == 100
    ) {
      //throw warning same player!
      System.out.println("this is a human thingy");
      //remove selected unit
    } else {
      // int id = GameState.gameBoard[x][y].getId();
      int attack = getUnitAttack(out, GameState.currentunit);
      System.out.println(attack + " this is attack!");
      BasicCommands.addPlayer1Notification(
        out,
        "playUnitAnimation [Attack]",
        2
      );
	  EffectAnimation projectile = null;
	  if(GameState.currentunit.getId() == 6) { 
		///comehere
		projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		// Tile tile2  = new
		//sooon

	  }
      BasicCommands.playUnitAnimation(
        out,
        GameState.currentunit,
        UnitAnimationType.attack
      );
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
	  if(GameState.currentunit.getId() == 6) { 
		Tile tile = BasicObjectBuilders.loadTile(x, y);
		Tile tile2 = BasicObjectBuilders.loadTile(GameState.selectedUnitX, GameState.selectedUnitY);
		BasicCommands.playProjectileAnimation(out, projectile, 0, tile2, tile);
		      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

	  }


      int currentHealth = getUnitHealth(out, GameState.gameBoard[x][y]);

      int updatedHealth = currentHealth - attack;

      if (GameState.gameBoard[x][y].getId() == 200) {
        if (updatedHealth <= 0) {
          //death animation of the opposite player
          System.out.println("we are here!!");
          AIOpponent.AIStats.setHealth(0);
          BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
          BasicCommands.addPlayer1Notification(out, "Set unit health", 2);
          BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], 0);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          //death animation of avatar -- game ends here
          BasicCommands.addPlayer1Notification(
            out,
            "Game Ended! Player won!",
            3
          );
          BasicCommands.playUnitAnimation(
            out,
            GameState.gameBoard[x][y],
            UnitAnimationType.death
          );
		  BasicCommands.deleteUnit(out,GameState.gameBoard[x][y]);
		  GameState.gameBoard[x][y] = null;
          GameState.gameEnded = true;

          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          attackedUnits.add(GameState.currentunit);
          GameState.gameEnded = true;
          // permissiblePositions.clear();
          return;
        }
        AIOpponent.AIStats.setHealth(updatedHealth);
        BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
        BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
        BasicCommands.setUnitHealth(
          out,
          GameState.gameBoard[x][y],
          updatedHealth
        );
        attackedUnits.add(GameState.currentunit);
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }


        counterAttack(out, x, y);
      } else {
        for (int i = 0; i < AIOpponent.AIOpponentCards.size(); i++) {
          if (
            AIOpponent.AIOpponentCards.get(i).getId() ==
            GameState.gameBoard[x][y].getId()
          ) {
            if (updatedHealth <= 0) {
              //will die
              AIOpponent.AIOpponentCards.get(i).getBigCard().setHealth(0);
              BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
              BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], 0);
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
			  System.out.println("we are removing! errror might be here 2" + GameState.selectedUnitX);
              BasicCommands.addPlayer1Notification(out, "Unit dead!", 3);
              BasicCommands.playUnitAnimation(
                out,
                GameState.gameBoard[x][y],
                UnitAnimationType.death
              );
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
              BasicCommands.deleteUnit(out, GameState.gameBoard[x][y]);
              GameState.gameBoard[x][y] = null;
              attackedUnits.add(GameState.currentunit);
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
			  System.out.println("before return");
              return;
            }
            AIOpponent.AIOpponentCards
              .get(i)
              .getBigCard()
              .setHealth(updatedHealth);
            BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
            BasicCommands.setUnitHealth(
              out,
              GameState.gameBoard[x][y],
              updatedHealth
            );
            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            attackedUnits.add(GameState.currentunit);
            counterAttack(out, x, y);
          }
        }
      }

    }
  }


  @Override
  public void processEvent(
    ActorRef out,
    GameState gameState,
    JsonNode message
  ) {
	if (HumanPlayer.humanStats.getHealth()==0 || AIOpponent.AIStats.getHealth() == 0){
		GameState.gameEnded=true;
		return;
	}



	if(GameState.gameEnded == true){

		BasicCommands.addPlayer1Notification(out,"Game ended Already!" , 2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}	
		return;
	}

    int tilex = message.get("tilex").asInt();
    int tiley = message.get("tiley").asInt();
    Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);



    System.out.println(GameState.isPlayerTurnCompleted);

    System.out.println(GameState.gameBoard[tilex][tiley]);

    if (GameState.isPlayerTurnCompleted != true) {
 
      if (GameState.isUnitSelected) {
        if (GameState.gameBoard[tilex][tiley] == null) {
          //move unit
          Unit unit = GameState.currentunit;

          boolean unitMoved = false;
  

          for (int i = 0; i < permissiblePositions.size(); i++) {
            if (
              permissiblePositions.get(i).get(0) == tilex &&
              permissiblePositions.get(i).get(1) == tiley
            ) {
     

              System.out.println("moved units length " + movedUnits.size());
              System.out.println(
                "attacked units length " + attackedUnits.size()
              );

              if (movedUnits.contains(unit) || attackedUnits.contains(unit) || currentlyDeployedUnits.contains(unit)) {
                BasicCommands.addPlayer1Notification(
                  out,
                  "Unit Cannot be moved",
                  2
                );
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                removeHighlight(out);
                System.out.println("unit already moved this turn");
                GameState.isUnitSelected = false;
                return;
              }

              BasicCommands.moveUnitToTile(out, unit, tile);
              unit.setPositionByTile(tile);
              GameState.isUnitSelected = false;
              GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] =
                null;
              GameState.gameBoard[tilex][tiley] = unit;
              removeHighlight(out);
              unitMoved = true;
              //unhighligh all tiles.
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              GameState.previousMove = "move";
              // permissiblePositions.clear();
              movedUnits.add(unit);
              // GameState.isPlayerTurnCompleted = true;

            }
          }

          if (!unitMoved) {
            GameState.isUnitSelected = false;
            GameState.selectedUnitX = -1;
            GameState.selectedUnitY = -1;
         
            removeHighlight(out);
            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        } else if (GameState.gameBoard[tilex][tiley] != null) {
         
          System.out.println("moved units length " + movedUnits.size());
          System.out.println("attacked units length " + attackedUnits.size());
          int count = 0;
          if (GameState.currentunit.getId() == 2) {
            for (int p = 0; p < attackedUnits.size(); p++) {
              if (attackedUnits.get(p).getId() == 2) {
                count++;
              }
            }
            System.out.println("thsi is count " + count);
            if (count >= 2) {
              GameState.isUnitSelected = false;
              BasicCommands.addPlayer1Notification(
                out,
                "Unit Already used for attacking",
                2
              );
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              removeHighlight(out);
              System.out.println("already attacked");

              return;
            }
          } else if (attackedUnits.contains(GameState.currentunit)  || currentlyDeployedUnits.contains(GameState.currentunit)) {
            GameState.isUnitSelected = false;
            BasicCommands.addPlayer1Notification(
              out,
              "Unit Cannot be used",
              2
            );
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            removeHighlight(out);
            System.out.println("already attacked");

            return;
          }


          int xDiff = Math.abs(GameState.selectedUnitX - tilex);
          int yDiff = Math.abs(GameState.selectedUnitY - tiley);

          System.out.println(xDiff + " this is xDiff");
          System.out.println(yDiff + " this is yDiff");
          boolean isMovedBeforeAtttack = false;
          boolean canMoveBeforeAttack = false;
          if (xDiff == 2 || yDiff == 2) {
            canMoveBeforeAttack = true;
            if ((xDiff == 2 && yDiff < 2)) {
              if (GameState.selectedUnitX > tilex) {
                //comeback
                if (
                  GameState.gameBoard[GameState.selectedUnitX -
                    1][GameState.selectedUnitY] ==
                  null
                ) {
                  Tile newTile = BasicObjectBuilders.loadTile(
                    GameState.selectedUnitX - 1,
                    GameState.selectedUnitY
                  );
                  Unit unit = GameState.currentunit;

                  BasicCommands.moveUnitToTile(out, unit, newTile);
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  unit.setPositionByTile(tile);
                  GameState.isUnitSelected = false;
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] =
                    null;
                  GameState.gameBoard[GameState.selectedUnitX -
                    1][GameState.selectedUnitY] =
                    unit;
                  isMovedBeforeAtttack = true;
                }
              } else if (GameState.selectedUnitX < tilex) {
                //comeback
                if (
                  GameState.gameBoard[GameState.selectedUnitX +
                    1][GameState.selectedUnitY] ==
                  null
                ) {
                  Tile newTile = BasicObjectBuilders.loadTile(
                    GameState.selectedUnitX + 1,
                    GameState.selectedUnitY
                  );
                  Unit unit = GameState.currentunit;

                  BasicCommands.moveUnitToTile(out, unit, newTile);
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  unit.setPositionByTile(tile);
                  GameState.isUnitSelected = false;
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] =
                    null;
                  GameState.gameBoard[GameState.selectedUnitX +
                    1][GameState.selectedUnitY] =
                    unit;
                  isMovedBeforeAtttack = true;
                }
              }
            } else if (xDiff < 2 && yDiff == 2) {
              if (GameState.selectedUnitY > tiley) {
                //comeback
                if (
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY -
                    1] ==
                  null
                ) {
                  Tile newTile = BasicObjectBuilders.loadTile(
                    GameState.selectedUnitX,
                    GameState.selectedUnitY - 1
                  );
                  Unit unit = GameState.currentunit;

                  BasicCommands.moveUnitToTile(out, unit, newTile);
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  unit.setPositionByTile(tile);
                  GameState.isUnitSelected = false;
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] =
                    null;
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY -
                    1] =
                    unit;
                  isMovedBeforeAtttack = true;
                }
              } else if (GameState.selectedUnitY < tiley) {
                //comeback
                if (
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY +
                    1] ==
                  null
                ) {
                  Tile newTile = BasicObjectBuilders.loadTile(
                    GameState.selectedUnitX,
                    GameState.selectedUnitY + 1
                  );
                  Unit unit = GameState.currentunit;

                  BasicCommands.moveUnitToTile(out, unit, newTile);
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  unit.setPositionByTile(tile);
                  GameState.isUnitSelected = false;
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY] =
                    null;
                  GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY +
                    1] =
                    unit;
                  isMovedBeforeAtttack = true;
                }
              }
            }
          }

          System.out.println(
            GameState.selectedUnitX +
            " this is x " +
            GameState.selectedUnitY +
            " this is Y "
          );
          // if(GameState.currentunit = ) {

          // }
		  boolean ifAttacked = false;
          for (int check = 0; check < permissiblePositions.size(); check++) {
            if (
              permissiblePositions.get(check).get(0) == tilex &&
              permissiblePositions.get(check).get(1) == tiley
            ) {
              if (isMovedBeforeAtttack == canMoveBeforeAttack ) {

				// if(GameState.gameBoard[GameState.selectedUnitX][GameState.selectedUnitY].getId() != 6){

				// }
                attackUnit(out, tilex, tiley);
				ifAttacked = true;
              }
              // if()

            }
          }
		  if(ifAttacked == false)  { 
          	if (GameState.gameBoard[tilex][tiley].getId() == 6 && !currentlyDeployedUnits.contains(GameState.gameBoard[tilex][tiley])) {
			System.out.println("inside here! " + currentlyDeployedUnits.contains(GameState.gameBoard[tilex][tiley]));
            attackUnit(out, tilex, tiley);
          	}

		  }

          removeHighlight(out);

          GameState.isUnitSelected = false;

          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          GameState.previousMove = "attack";
          // GameState.isPlayerTurnCompleted = true;

          removeHighlight(out);
        }
      } else if (GameState.isCardSelected) {
        System.out.println("inside here 123");

        if (
          GameState.currentCard.getBigCard().getAttack() == -1 &&
          GameState.currentCard.getBigCard().getHealth() == -1 &&
          GameState.gameBoard[tilex][tiley] != null
        ) {
          if (GameState.gameBoard[tilex][tiley] == null) {
            //throw error
          } else {
            int manaToReduce = GameState.currentCard.getManacost();
            if (HumanPlayer.humanStats.getMana() >= manaToReduce) {
			
				/**
				 * Condtions for two different spells of human player.
				 */
              if (GameState.currentCard.getId() == 1002) {
                System.out.println("we are herrrrrreee");
                if (GameState.gameBoard[tilex][tiley] != null) {
                  if (
                    HumanPlayer.humanCardNumbers.contains(
                      GameState.gameBoard[tilex][tiley].getId()
                    ) ||
                    GameState.gameBoard[tilex][tiley].getId() == 100
                  ) {} else {
                    int currentHealth = 0;
                    if (GameState.gameBoard[tilex][tiley].getId() == 200) {
                      currentHealth = AIOpponent.AIStats.getHealth();
                      System.out.println("inisde AI health");
                    } else {
                      for (
                        int i = 0;
                        i < AIOpponent.AIOpponentCards.size();
                        i++
                      ) {
                        if (
                          AIOpponent.AIOpponentCards.get(i).getId() ==
                          GameState.gameBoard[tilex][tiley].getId()
                        ) {
                          currentHealth =
                            AIOpponent.AIOpponentCards
                              .get(i)
                              .getBigCard()
                              .getHealth();
                          System.out.println("not here");
                        }
                      }
                    }

                    int updatedHealth = currentHealth - 2;

                    System.out.println(
                      updatedHealth + " this is updatedhealth"
                    );
                    System.out.println(updatedHealth + " " + currentHealth);
                    BasicCommands.addPlayer1Notification(
                      out,
                      StaticConfFiles.f1_inmolation,
                      2
                    );
                    EffectAnimation ef = BasicObjectBuilders.loadEffect(
                      StaticConfFiles.f1_inmolation
                    );
                    BasicCommands.playEffectAnimation(out, ef, tile);
                    try {
                      Thread.sleep(1000);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                    //////////////////
                    if (GameState.gameBoard[tilex][tiley].getId() == 200) {
                      if (updatedHealth <= 0) {
                        AIOpponent.AIStats.setHealth(0);
                        BasicCommands.setPlayer2Health(
                          out,
                          HumanPlayer.humanStats
                        );
                        BasicCommands.addPlayer1Notification(
                          out,
                          "setUnitHealth",
                          2
                        );
                        BasicCommands.setUnitHealth(
                          out,
                          GameState.gameBoard[tilex][tiley],
                          0
                        );
                        try {
                          Thread.sleep(1000);
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        }
                        //death animation of avatar -- game ends here
                        BasicCommands.addPlayer1Notification(
                          out,
                          "Game over!",
                          3
                        );
                        BasicCommands.playUnitAnimation(
                          out,
                          GameState.gameBoard[tilex][tiley],
                          UnitAnimationType.death
                        );
                        BasicCommands.deleteUnit(
                          out,
                          GameState.gameBoard[tilex][tiley]
                        );
                        try {
                          Thread.sleep(2000);
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        }
                        GameState.gameBoard[tilex][tiley] = null;
                        //remove unit from the board
                        // return;

                      } else {
                        AIOpponent.AIStats.setHealth(updatedHealth);
                        System.out.println("inside updating health");
                        BasicCommands.setUnitHealth(
                          out,
                          GameState.gameBoard[tilex][tiley],
                          updatedHealth
                        );
                        BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
                        try {
                          Thread.sleep(2000);
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        }
                      }
                    } else {
                      if (updatedHealth <= 0) {
                        BasicCommands.setUnitHealth(
                          out,
                          GameState.gameBoard[tilex][tiley],
                          0
                        );
                        for (
                          int i = 0;
                          i < AIOpponent.AIOpponentCards.size();
                          i++
                        ) {
                          if (
                            AIOpponent.AIOpponentCards.get(i).getId() ==
                            GameState.gameBoard[tilex][tiley].getId()
                          ) {
                            AIOpponent.AIOpponentCards
                              .get(i)
                              .getBigCard()
                              .setHealth(0);
                          }

                          BasicCommands.setUnitHealth(
                            out,
                            GameState.gameBoard[tilex][tiley],
                            0
                          );
                          try {
                            Thread.sleep(1000);
                          } catch (InterruptedException e) {
                            e.printStackTrace();
                          }
                          //death animation of avatar -- game ends here
						  System.out.println(" error might be here 3!");
                          BasicCommands.addPlayer1Notification(
                            out,
                            "Unit dead!",
                            3
                          );
                          BasicCommands.playUnitAnimation(
                            out,
                            GameState.gameBoard[tilex][tiley],
                            UnitAnimationType.death
                          );
                          BasicCommands.deleteUnit(
                            out,
                            GameState.gameBoard[tilex][tiley]
                          );
                          try {
                            Thread.sleep(1000);
                          } catch (InterruptedException e) {
                            e.printStackTrace();
                          }
                          GameState.gameBoard[tilex][tiley] = null;
						  
                        //   return;

                          //cmback
                        }
                      } else {
                        System.out.println(
                          updatedHealth + " " + currentHealth + " " + 2
                        );
                        BasicCommands.addPlayer1Notification(
                          out,
                          "setUnitHealth",
                          2
                        );
                        BasicCommands.setUnitHealth(
                          out,
                          GameState.gameBoard[tilex][tiley],
                          updatedHealth
                        );
                        for (
                          int i = 0;
                          i < AIOpponent.AIOpponentCards.size();
                          i++
                        ) {
                          if (
                            AIOpponent.AIOpponentCards.get(i).getId() ==
                            GameState.gameBoard[tilex][tiley].getId()
                          ) {
                            AIOpponent.AIOpponentCards
                              .get(i)
                              .getBigCard()
                              .setHealth(updatedHealth);
                          }
                        }
                      }
                      try {
                        Thread.sleep(2000);
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                  }
                }
              } else if (GameState.currentCard.getId() == 1001) {
                System.out.println("isnide here the first spell!");

                if (
                  HumanPlayer.humanCardNumbers.contains(
                    GameState.gameBoard[tilex][tiley].getId()
                  ) ||
                  GameState.gameBoard[tilex][tiley].getId() == 100
                ) {
                  int maximumHealth = getUnitHealth(
                    out,
                    GameState.gameBoard[tilex][tiley]
                  );

                  int healthCanBeIncreased = 5;
                  // int actualHealth;
                  int currentHealth = 0;
                  int updatedHealth = 0;

                  EffectAnimation ef = BasicObjectBuilders.loadEffect(
                    StaticConfFiles.f1_summon
                  );
                  BasicCommands.playEffectAnimation(out, ef, tile);
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  if (GameState.gameBoard[tilex][tiley].getId() == 100) {
                    maximumHealth = 20;
                    currentHealth = HumanPlayer.humanStats.getHealth();
                  }
                  for (int i = 0; i < HumanPlayer.humanCards.size(); i++) {
                    if (
                      HumanPlayer.humanCards.get(i).getId() ==
                      GameState.gameBoard[tilex][tiley].getId()
                    ) {
                      currentHealth =
                        HumanPlayer.humanCards.get(i).getBigCard().getHealth();
                    }
                  }

                  updatedHealth = currentHealth + 5;

                  if (updatedHealth > maximumHealth) {
                    updatedHealth = maximumHealth;
                  }

                  // for(int i = currentHealth ; i < maximumHealth ;i++){
                  // 	if( healthCanBeIncreased > 0 ){
                  // 		healthCanBeIncreased -= 1;
                  // 		updatedHealth += 1;
                  // 	}
                  // }
                  if (GameState.gameBoard[tilex][tiley].getId() == 100) {
                    System.out.println("human player inside " + updatedHealth);
                    HumanPlayer.humanStats.setHealth(updatedHealth);
                    BasicCommands.setPlayer1Health(out, HumanPlayer.humanStats);
                    BasicCommands.setUnitHealth(
                      out,
                      GameState.gameBoard[tilex][tiley],
                      updatedHealth
                    );
                    try {
                      Thread.sleep(2000);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                  } else {
                    System.out.println(
                      maximumHealth + " this is current health"
                    );
                    // if(maximumHealth)
                    // GameState.gameBoard[tilex][tiley]
                    for (int i = 0; i < HumanPlayer.humanCards.size(); i++) {
                      if (
                        HumanPlayer.humanCards.get(i).getId() ==
                        GameState.gameBoard[tilex][tiley].getId()
                      ) {
                        HumanPlayer.humanCards
                          .get(i)
                          .getBigCard()
                          .setHealth(updatedHealth);
                        // actualHealth = HumanPlayer.humanCards.get(i).getBigCard().getHealth();
                      }
                    }
                    BasicCommands.setUnitHealth(
                      out,
                      GameState.gameBoard[tilex][tiley],
                      updatedHealth
                    );
                    try {
                      Thread.sleep(2000);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                  }
                } else {
                  System.out.println(
                    "you cannot use it for opposite player value"
                  );
                }
              }

              // checkForAbilitiesAndMakeitHappen
              GameState.isCardSelected = false;
              GameState.isUnitSelected = false;
              GameState.currentCard = null;

              HumanPlayer.deleteCard(out);
              removeHighlight(out);
              //decrease mana
              for (int i = 0; i < manaToReduce; i++) {
                HumanPlayer.humanStats.setMana(
                  HumanPlayer.humanStats.getMana() - 1
                );
                BasicCommands.setPlayer1Mana(out, HumanPlayer.humanStats);
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
              // for(int i =0)
              // remove all highlights

            } else {
              System.out.println("not enough mana");
              BasicCommands.addPlayer1Notification(out, "Not Enough Mana!", 2);
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        } else if (GameState.gameBoard[tilex][tiley] == null) {
          //deploy unit
          int manaToReduce = GameState.currentCard.getManacost();
          if (HumanPlayer.humanStats.getMana() >= manaToReduce) {
            ObjectMapper mapper = new ObjectMapper();
            try {
              String json = readFileAsString(
                HumanPlayer.PlayerHand[GameState.selectedHandPosition]
              );
              Card card = mapper.readValue(json, Card.class);
              GameState.currentCard = card;

              if (
                GameState.currentCard.getBigCard().getAttack() == -1 &&
                GameState.currentCard.getBigCard().getHealth() == -1
              ) {
                //through error as it is a spell
              } else {
                System.out.println("inside here not a spell! its a unit");

                boolean isDeployed = false;

                for (int i = 0; i < permissiblePositions.size(); i++) {
                  if (
                    permissiblePositions.get(i).get(0) == tilex &&
                    permissiblePositions.get(i).get(1) == tiley
                  ) {
                    BasicCommands.addPlayer1Notification(
                      out,
                      "Deploying Unit",
                      2
                    );

                    System.out.println(
                      GameState.currentCard.getId() +
                      " this is the id of the unit"
                    );
                    HumanPlayer.humanCards.add(GameState.currentCard);
                    Unit unit = BasicObjectBuilders.loadUnit(
                      getUnit(GameState.currentCard),
                      GameState.currentCard.getId(),
                      Unit.class
                    );
                    unit.setPositionByTile(tile);
                    BasicCommands.drawUnit(out, unit, tile);
					currentlyDeployedUnits.add(unit);
                    try {
                      Thread.sleep(2000);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                    GameState.gameBoard[tilex][tiley] = unit;

                    //Set unit Health
                    BasicCommands.addPlayer1Notification(
                      out,
                      "setUnitHealth",
                      2
                    );
                    BasicCommands.setUnitHealth(
                      out,
                      unit,
                      GameState.currentCard.getBigCard().getHealth()
                    );
                    try {
                      Thread.sleep(2000);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }

                    // setUnitAttack
                    BasicCommands.addPlayer1Notification(
                      out,
                      "setUnitAttack",
                      2
                    );
                    BasicCommands.setUnitAttack(
                      out,
                      unit,
                      GameState.currentCard.getBigCard().getAttack()
                    );
                    try {
                      Thread.sleep(2000);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }

                    //if azure herald increase avatar health by 1
                    if (GameState.currentCard.getId() == 1) {
                      System.out.println("deploying azure herald");
                      int currentHealth = HumanPlayer.humanStats.getHealth();
                      currentHealth += 3;
                      if (currentHealth > 20) {
                        currentHealth = 20;
                      }
                      System.out.println(
                        currentHealth + " this is current health"
                      );
                      //backend
                      HumanPlayer.humanStats.setHealth(currentHealth);
                      //find human player unit
                      //front end
                      for (int m = 0; m < 9; m++) {
                        for (int n = 0; n < 5; n++) {
                          if (GameState.gameBoard[m][n] != null) {
                            if (GameState.gameBoard[m][n].getId() == 100) {
                              Unit curUnit = GameState.gameBoard[m][n];
                              System.out.println(curUnit + " thi is cur unit");
                              BasicCommands.setUnitHealth(
                                out,
                                curUnit,
                                currentHealth
                              );
                              try {
                                Thread.sleep(1000);
                              } catch (InterruptedException e) {
                                e.printStackTrace();
                              }
                            }
                          }
                        }
                      }

                      //Avatar
                      BasicCommands.setPlayer1Health(
                        out,
                        HumanPlayer.humanStats
                      );
                      try {
                        Thread.sleep(1000);
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                    }

                    //delete card
                    HumanPlayer.deleteCard(out);

                    //Add new Card! if new card is null then don't add card // add player card over true!

                    // GameState.isPlayerTurnCompleted = true;
                    GameState.isCardSelected = false;
                    GameState.isUnitSelected = false;
                    GameState.currentCard = null;
                    removeHighlight(out);
                    isDeployed = true;
                    for (int j = 0; j < manaToReduce; j++) {
                      HumanPlayer.humanStats.setMana(
                        HumanPlayer.humanStats.getMana() - 1
                      );
                      BasicCommands.setPlayer1Mana(out, HumanPlayer.humanStats);
                      try {
                        Thread.sleep(1000);
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                    GameState.previousMove = "deploy";
                    // GameState.isPlayerTurnCompleted = true;
                  }
                }

                if (!isDeployed) {
                  //throw error
                  System.out.println("cannot deploy unit here!");

                  BasicCommands.addPlayer1Notification(
                    out,
                    GameState.currentCard + " highlight remove",
                    2
                  );
                  BasicCommands.drawCard(
                    out,
                    GameState.currentCard,
                    GameState.selectedHandPosition,
                    0
                  );
                  GameState.isCardSelected = false;
                  GameState.isUnitSelected = false;
                  GameState.currentCard = null;
                  removeHighlight(out);
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
          
            System.out.println("not enough mana");

            BasicCommands.addPlayer1Notification(
              out,
              GameState.currentCard + " highlight remove",
              2
            );
            BasicCommands.drawCard(
              out,
              GameState.currentCard,
              GameState.selectedHandPosition,
              0
            );
            GameState.isCardSelected = false;
            GameState.isUnitSelected = false;
            GameState.currentCard = null;
            removeHighlight(out);
          }
        } else if (GameState.gameBoard[tilex][tiley] != null) {
       
          //This condition never comes

        }
      } else if (GameState.gameBoard[tilex][tiley] != null) {
        if (
          HumanPlayer.humanCardNumbers.contains(
            GameState.gameBoard[tilex][tiley].getId()
          ) ||
          GameState.gameBoard[tilex][tiley].getId() == 100
        ) {
          //throw warning same player!

          if (checkProvoke(out, tilex, tiley) == false) {
            //do nothing
            highlightPossibleMoves(out, tilex, tiley);
          }

          GameState.isUnitSelected = true;
          GameState.currentunit = GameState.gameBoard[tilex][tiley];
          GameState.selectedUnitX = tilex;
          GameState.selectedUnitY = tiley;
          //remove selected unit
        } else {
        

        }
      } else {
        //do nothing
      }

    } else {
      //throw error
      System.out.println("Not human player turn");
    }
  }
}
