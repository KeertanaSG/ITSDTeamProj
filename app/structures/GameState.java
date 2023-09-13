package structures;

import actions.*;
import java.util.*;

import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;

	public boolean something = false;

	public static String currentPlayer = "human";

	public boolean gameEnded = false;

    public boolean isCardSelected = false;
   
    public Card currentCard = null;

	public static boolean isUnitSelected = false;


	// public static HumanPlayer humanPlayer = null;

	// public AIOpponent aiOpponent = null;

	public static Unit[][] gameBoard = new Unit[9][5];
	public static int[][] playerTile = new int[9][5];
	//public Player player1 = playerObject
	// public AIOpponent opponent = opponentObject	
}
