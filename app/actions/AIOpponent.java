package actions;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class AIOpponent {

    public static Player AIStats;
    public static Card[] deployedCards = new Card[20];
    public static ArrayList<Integer> AICardNumbers = new ArrayList<Integer>();

    public static String[] AICards = {
            StaticConfFiles.c_blaze_hound,
            StaticConfFiles.c_bloodshard_golem,
            StaticConfFiles.c_entropic_decay,
            StaticConfFiles.c_hailstone_golem,
            StaticConfFiles.c_planar_scout,
            StaticConfFiles.c_pyromancer,
            StaticConfFiles.c_serpenti,
            StaticConfFiles.c_rock_pulveriser,
            StaticConfFiles.c_staff_of_ykir,
            StaticConfFiles.c_windshrike,
            StaticConfFiles.c_blaze_hound,
            StaticConfFiles.c_bloodshard_golem,
            StaticConfFiles.c_entropic_decay,
            StaticConfFiles.c_hailstone_golem,
            StaticConfFiles.c_planar_scout,
            StaticConfFiles.c_pyromancer,
            StaticConfFiles.c_serpenti,
            StaticConfFiles.c_rock_pulveriser,
            StaticConfFiles.c_staff_of_ykir,
            StaticConfFiles.c_windshrike,
    };

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

    public static ArrayList<Unit> deployedUnits = new ArrayList<Unit>();
    public static ArrayList<Unit> movedUnits = new ArrayList<Unit>();
    public static ArrayList<Unit> attackedUnits = new ArrayList<Unit>();
    public static ArrayList<Card> AIOpponentCards = new ArrayList<Card>();
    public static String[] AIHand = new String[6];
    public static int currentCardNumber = 1;
    public static ArrayList<ArrayList<Integer>> permissiblePositions = new ArrayList<ArrayList<Integer>>();
    public static int AITurn = 0;
    public static Unit currentUnit = GameState.gameBoard[7][2];
    public static int selectedUnitX = -1;
    public static int selectedUnitY = -1;
    public static boolean isAttackMade = false;
    public static int moveCounter = 0;
    public static int cnt = 1;

    public static int AIAttackPower = 2;

    public static String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public static Card getCard(ActorRef out, Unit unit) {
        ObjectMapper mapper = new ObjectMapper();
        for (String eachCard : AICards) {
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

        System.out.println("not found");

        return null;

    }

    public static void loadPlayer(ActorRef out) {
        BasicCommands.addPlayer1Notification(out, "setPlayer2Health", 2);
        AIStats = new Player(20, 2);
        BasicCommands.setPlayer2Health(out, AIStats);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Mana
        for (int m = 0; m < 2; m++) {
            // int n = m + 1;
            // BasicCommands.add(out, "setPlayer1Mana ("+n+")", 1);
            BasicCommands.setPlayer2Mana(out, AIStats);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        int playerHandCount = 0;
        for (String deck1CardFile : AICards) {
            // change to unit and upload number!
            try {

                String json = readFileAsString(deck1CardFile);
                Card card = mapper.readValue(json, Card.class);
                AICardNumbers.add(card.getId());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (playerHandCount <= 2) {
                currentCardNumber += 1;

                AIHand[playerHandCount] = deck1CardFile;
                System.out.println("ai details " + playerHandCount + " " + AIHand[playerHandCount] + deck1CardFile);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            playerHandCount++;
        }
    }

    public static int getUnitAttack(ActorRef out, Unit unit) {
        System.out.println(unit.getId() + "  we are inside get unit attack");
        if (unit != null) {
            System.out.println(unit.getId() + "  we are inside get unit attack");
            if (unit.getId() == 100) {
                return HumanPlayer.humanAttackPower;
            
            } else if (unit.getId() == 200) {

                return AIOpponent.AIAttackPower;
                
                // return AIOpponent.AIStats.ge
            } else {
                Card curCard = AIOpponent.getCard(out, unit);
                System.out.println(curCard + " this is cur card!");
                if (curCard != null) {
                    System.out.println(curCard.getBigCard().getHealth() + " this is unit health!");
                    return curCard.getBigCard().getAttack();
                }
            }
        }
        return -1;
    }

    public static int getUnitHealth(ActorRef out, Unit unit) {
        System.out.println(unit.getId() + " we are health");
        if (unit.getId() == 100) {
            return HumanPlayer.humanStats.getHealth();
        } else if (unit.getId() == 200) {
            return AIOpponent.AIStats.getHealth();
        } else {
            for(int i =0; i < HumanPlayer.humanCards.size();i++){
                if(HumanPlayer.humanCards.get(i).getId() == unit.getId()){
                    return HumanPlayer.humanCards.get(i).getBigCard().getHealth();
                }
            }

            for(int i =0; i < AIOpponent.AIOpponentCards.size();i++){
                if(AIOpponent.AIOpponentCards.get(i).getId() == unit.getId()){
                    return AIOpponent.AIOpponentCards.get(i).getBigCard().getHealth();
                }
            }

            return 0;
        }
    }

    public static boolean canCounterAttack(ActorRef out) {
        // @dev check if any adjacent tile is provoke then if the counter attack is a
        // provoke then attack else don't attack
        // check if the opposite unit is an adjacent unit
        return true;
    }

    public static void counterAttack(ActorRef out, int x, int y) {
        System.out.println("inside counter attack!");
        if (canCounterAttack(out)) {
            int reattack = HumanPlayer.getUnitAttack(out, GameState.gameBoard[x][y]);
            System.out.println(reattack + " this is re-attack!");
            BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
            BasicCommands.playUnitAnimation(out, GameState.gameBoard[x][y], UnitAnimationType.attack);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // run a for loop for current health
            int reAttackCurrenttHealth = getUnitHealth(out, currentUnit);
            if (currentUnit.getId() == 200) {
                reAttackCurrenttHealth = AIOpponent.AIStats.getHealth();
            }
            for (int i = 0; i < AIOpponent.AIOpponentCards.size(); i++) {
                if (AIOpponent.AIOpponentCards.get(i).getId() == currentUnit.getId()) {
                    reAttackCurrenttHealth = AIOpponent.AIOpponentCards.get(i).getBigCard().getHealth();
                    break;
                }
            }

            int reAttackUpdatedHealth = reAttackCurrenttHealth - reattack;

            System.out.println(reAttackCurrenttHealth + " this is reattach current health");

            System.out.println(reAttackUpdatedHealth + " this is reattack updated health");

            // @dev
            // if opposite player is AI
            if (currentUnit.getId() == 200) {

                System.out.println("opposite player AI");
                if (reAttackUpdatedHealth <= 0) {
                    AIOpponent.AIStats.setHealth(0);
                    BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
                    BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                    BasicCommands.setUnitHealth(out, currentUnit, 0);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // death animation of avatar -- game ends here
                    BasicCommands.addPlayer1Notification(out, "Game Ended! Human player won!", 3);
                    BasicCommands.playUnitAnimation(out, currentUnit, UnitAnimationType.death);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                AIOpponent.AIStats.setHealth(reAttackUpdatedHealth);
                BasicCommands.setPlayer2Health(out, AIOpponent.AIStats);
                BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                BasicCommands.setUnitHealth(out, currentUnit, reAttackUpdatedHealth);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // death animation of avatar -- game ends here
                BasicCommands.addPlayer1Notification(out, "Counter attack", 3);
    
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("opposite not AI player but AI unit");
                for (int i = 0; i < AIOpponent.AIOpponentCards.size(); i++) {
                    System.out.println("inside herre " + AIOpponent.AIOpponentCards.get(i).getId() + " "
                            + currentUnit.getId());
                    if (AIOpponent.AIOpponentCards.get(i).getId() == currentUnit.getId()) {
                        System.out.println("check point 1!");
                        if (reAttackUpdatedHealth <= 0) {
                            System.out.println("check point 2!");
                            // will die
                            AIOpponent.AIOpponentCards.get(i).getBigCard().setHealth(0);
                            BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                            BasicCommands.setUnitHealth(out, currentUnit, 0);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            BasicCommands.addPlayer1Notification(out, "Unit dead!", 3);
                            BasicCommands.playUnitAnimation(out, currentUnit, UnitAnimationType.death);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // @devRemove unit
                            BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
                            BasicCommands.deleteUnit(out, currentUnit);
                            deployedUnits.remove(currentUnit);
                            System.out.println("removed deployed unit after attack " + deployedUnits.size());
                            if (GameState.currentCard.getId() == 16) {
                                AIOpponent.addNewCard(out);
                            }
                            GameState.gameBoard[selectedUnitX][selectedUnitY] = null;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        System.out.println("check point 3!");
                        System.out.println(
                                AIOpponent.AIOpponentCards.get(i).getBigCard().getHealth() + " this is here health");
                        AIOpponent.AIOpponentCards.get(i).getBigCard().setHealth(reAttackUpdatedHealth);
                        BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                        BasicCommands.setUnitHealth(out, currentUnit, reAttackUpdatedHealth);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void attackUnit(ActorRef out, int x, int y) {
        System.out.println("inside attack unit method");
        System.out.println(x + " this is x " + " this is y " + y);
        if (GameState.gameBoard[x][y] != null) {
            System.out.println("inside if attack unit method " + GameState.gameBoard[x][y].getId());
            if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[x][y].getId())
                    || GameState.gameBoard[x][y].getId() == 200) {
                // throw warning same player!
                System.out.println("this is a AI thingy");
                // remove selected unit
            } else {
                System.out.println(currentUnit + " this is the currentUnit");
                int attack = getUnitAttack(out, currentUnit);
                // System.out.println("this is unit attack");
                System.out.println(attack + " this is attack!");
                BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
                BasicCommands.playUnitAnimation(out, currentUnit, UnitAnimationType.attack);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int currentHealth = getUnitHealth(out, GameState.gameBoard[x][y]);

                System.out.println(currentHealth + " this is current health!");

                int updatedHealth = currentHealth - attack;

                System.out.println(updatedHealth + " this is updated health!");



                if (GameState.gameBoard[x][y].getId() == 100) {
                    if (updatedHealth <= 0) {
                        // death animation of the opposite player
                        System.out.println("we are here!!");
                        HumanPlayer.humanStats.setHealth(0);
                        BasicCommands.setPlayer1Health(out, HumanPlayer.humanStats);
                        BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                        BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], 0);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // death animation of avatar -- game ends here
                        BasicCommands.addPlayer1Notification(out, "Game Ended! Player won!", 3);
                        BasicCommands.playUnitAnimation(out, GameState.gameBoard[x][y], UnitAnimationType.death);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        attackedUnits.add(currentUnit);
                        GameState.gameEnded = true;
                        // permissiblePositions.clear();
                        return;

                    }
                    HumanPlayer.humanStats.setHealth(updatedHealth);
                    BasicCommands.setPlayer1Health(out, HumanPlayer.humanStats);
                    BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                    BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], updatedHealth);
                    attackedUnits.add(currentUnit);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    counterAttack(out, x, y);

                } else {
                    for (int i = 0; i < HumanPlayer.humanCards.size(); i++) {
                        if (HumanPlayer.humanCards.get(i).getId() == GameState.gameBoard[x][y].getId()) {
                            if (updatedHealth <= 0) {
                                System.out.println(" inisde this 0 health");
                                HumanPlayer.humanCards.get(i).getBigCard().setHealth(0);
                                BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                                BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], 0);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                BasicCommands.addPlayer1Notification(out, "Unit dead!", 3);
                                BasicCommands.playUnitAnimation(out, GameState.gameBoard[x][y],
                                        UnitAnimationType.death);

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
                                BasicCommands.deleteUnit(out, GameState.gameBoard[x][y]);
                                GameState.gameBoard[x][y] = null;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            HumanPlayer.humanCards.get(i).getBigCard().setHealth(updatedHealth);
                            System.out.println("After updating health " + HumanPlayer.humanCards.get(i).getBigCard().getHealth());
                            BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
                            BasicCommands.setUnitHealth(out, GameState.gameBoard[x][y], updatedHealth);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(" befor counter attack!");
                            counterAttack(out, x, y);
                        }
                    }
                }
                isAttackMade = true;
            }
        }
    }

    public static void AIlogic(ActorRef out) {
        // @dev check previous move

        // condition 1:
        // if previous move of the human player is to deploy a unit then
        // check AI hand of cars.. and run a loop to check if a card is there with that
        // can be deployed with the mana in the pool.. then deploy unit in a random tile
        // that is free
        // update the card in the AI card.
        BasicCommands.addPlayer1Notification(out, "AI turn", 2);
        System.out.println("inside AI class " + GameState.previousMove + " " + AIOpponent.AIHand.length);
        boolean isUnitDeployed = false;
        AITurn++;
        if (AITurn >= 2) {
            GameState.AIcurrentRound += 1;
            int newManaValue = GameState.AIcurrentRound + 1;
            System.out.println(newManaValue + " this is new mana value for AI");
            loadMana(out, newManaValue);
        }
        if (AITurn < 2) {
            currentUnit = GameState.gameBoard[7][2];
            deployedUnits.add(currentUnit);
            // highlightPossibleMoves(out, 7, 2);
            selectedUnitX = 7;
            selectedUnitY = 2;
        }
        if (currentUnit.getId() == 200) {
            highlightPossibleMoves(out, selectedUnitX, selectedUnitY);
        }
        for (int i = 0; i < AIOpponent.AIHand.length; i++) {
            System.out.println("AI hand  " + AIOpponent.AIHand[i]);
            if (AIOpponent.AIHand[i] != null) {
                // check if the card can be deployed then deploy.
                // set isUnitDeployed to true
                // remove card from the hand
                // else move to next card
                ObjectMapper mapper = new ObjectMapper();
                String json;
                try {
                    json = readFileAsString(AIOpponent.AIHand[i]);
                    Card card = mapper.readValue(json, Card.class);
                    GameState.currentCard = card;
                } catch (Exception e) {
                 
                    e.printStackTrace();
                }
                
                if (GameState.currentCard.getManacost() <= AIOpponent.AIStats.getMana()) {
                    // @dev
                    // deploy unit
                    // delete the card from the AI hand.
                    if (GameState.currentCard.getId() == 1003) {
                        int max=-1;
                        int maxval=0;
                        int unitx=-1;
                        int unity=-1;
                        System.out.println("spell should be used");
                        for (int k=0;k<9;k++){
                            for (int j=0;j<5;j++){
                                if (GameState.gameBoard[k][j]!=null){
                                    System.out.println("first if running");
                                    if (HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[k][j].getId()) 
                                    && GameState.gameBoard[k][j].getId() != 100){
                                        System.out.println("second if running");
                                        System.out.println(HumanPlayer.humanCards.size());
                                        for(int x = 0; x < HumanPlayer.humanCards.size();x++){
                                            if(HumanPlayer.humanCards.get(x).getId() == GameState.gameBoard[k][j].getId()){
                                                System.out.println("third if running");
                                                if (HumanPlayer.humanCards.get(x).getBigCard().getHealth()>max){
                                                    System.out.println("fourth if running");
                                                    max=HumanPlayer.humanCards.get(x).getBigCard().getHealth();
                                                    maxval=x;
                                                    unitx=k;
                                                    unity=j;
                                                }
                                                
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        HumanPlayer.humanCards.get(maxval).getBigCard().setHealth(0);
                        BasicCommands.setUnitHealth(out, GameState.gameBoard[unitx][unity], 0);
                        EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
                        Tile tile = BasicObjectBuilders.loadTile(unitx, unity);
                        BasicCommands.playEffectAnimation(out, ef, tile);
                        try{
                            Thread.sleep(100);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        BasicCommands.playUnitAnimation(out, GameState.gameBoard[unitx][unity], UnitAnimationType.death);
                        AIOpponent.deleteCard(out, i);
                        for (int a = 0; a < GameState.currentCard.getManacost(); a++) {
                            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() - 1);
                            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
            
                        }
                        BasicCommands.deleteUnit(out, GameState.gameBoard[unitx][unity]);
                        GameState.gameBoard[unitx][unity] = null;
                        cnt++;
                        increaseUnitHealth(out,cnt);
                        break;
                    }
                    if (GameState.currentCard.getId()==1004){
                        for (int k=0;k<9;k++){
                            for (int j=0;j<5;j++){
                                if (GameState.gameBoard[k][j] != null){
                                    if (GameState.gameBoard[k][j].getId() == 200){
                                        System.out.println("1004 first if running");
                                        EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
                                        Tile tile = BasicObjectBuilders.loadTile(k, j);
                                        BasicCommands.playEffectAnimation(out, ef, tile);
                                        try{
                                            Thread.sleep(100);
                                        }
                                        catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        int att=0;
                                        AIOpponent.AIAttackPower = AIOpponent.AIAttackPower + 2;
                                        BasicCommands.setUnitAttack(out, GameState.gameBoard[k][j], AIOpponent.AIAttackPower);
                                        
                                        
             
                                       
                                    }
                                    
                                }
                            }
                        }
                        AIOpponent.deleteCard(out, i);
                        cnt++;
                        increaseUnitHealth(out,cnt);
                        for (int a = 0; a < GameState.currentCard.getManacost(); a++) {
                            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() - 1);
                            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
            
                        }
                        
                        break;
                    }
                }
            }
        }
        Random randNum = new Random();
        int selectType = randNum.nextInt(3); // 0 - deploy, 1 - attack , 2 - move
        if (selectType == 0) {
            System.out.println("Type is deploy unit" + currentUnit.getId());
            checkForDeployUnit(out, isUnitDeployed);
        }

        // if not enough AI.. then move any one of the unit on the board if movable.

        // Condition2:
        // if previous move is attack.. then attack any nearest unit if possible.
        // else move a unit randomly on a tile if possible.
        isAttackMade = false;
        if (selectType == 1) {
            // run a for loop on the game board.. if not null and it is a AI Opponent check
            // if it can attack then attack/move and attack.
            Random rand = new Random();
            int tilex = rand.nextInt(9);
            int tiley = rand.nextInt(5);
            boolean isPermissible = false;
            highlightPossibleMoves(out, selectedUnitX, selectedUnitY);
            System.out.println("inside attack unit " + permissiblePositions.size());
            boolean canAttack = false;
            int posnx = 0;
            int posny = 0;
            for (int i = 0; i < permissiblePositions.size(); i++) {
                posnx = permissiblePositions.get(i).get(0);
                posny = permissiblePositions.get(i).get(1);
                if (GameState.gameBoard[posnx][posny] != null
                        && HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[posnx][posny].getId())) {
                    System.out.println("Inside can attack");
                    canAttack = true;
                    break;
                }
            }
            if (canAttack) {
                attackUnit(out, posnx, posny);
            } else {
                while (!isPermissible) {
                    for (int i = 0; i < permissiblePositions.size(); i++) {
                        if (permissiblePositions.get(i).get(0) == tilex
                                && permissiblePositions.get(i).get(1) == tiley) {
                            isPermissible = true;
                            break;

                        }
                    }
                    if (!isPermissible) {
                        tilex = rand.nextInt(9);
                        tiley = rand.nextInt(5);
                    }

                }
                attackUnit(out, tilex, tiley);
                if (isAttackMade == false) {
                    // run a for loop and check if you can move any unit then move a unit.
                    checkAndMove(out);
                }
            }

        }

        // condition3
        // if previous move is move .. move any unit randomly if possible

        if (selectType == 2) {
    
            checkAndMove(out);
           
        }

    }

    public static void checkForDeployUnit(ActorRef out, boolean isUnitDeployed) {
        for (int i = 0; i < AIOpponent.AIHand.length; i++) {
            System.out.println("AI hand  " + AIOpponent.AIHand[i]);
            if (AIOpponent.AIHand[i] != null) {
                // check if the card can be deployed then deploy.
                // set isUnitDeployed to true
                // remove card from the hand
                // else move to next card
                ObjectMapper mapper = new ObjectMapper();
                String json;
                try {
                    json = readFileAsString(AIOpponent.AIHand[i]);
                    Card card = mapper.readValue(json, Card.class);
                    GameState.currentCard = card;
                } catch (Exception e) {
                    // @dev Auto-generated catch block
                    e.printStackTrace();
                }
                if (GameState.currentCard.getManacost() <= AIOpponent.AIStats.getMana()) {
                    // @dev
                    // deploy unit
                    // delete the card from the AI hand.
                    deployUnit(out, i);
                    isUnitDeployed = true;
                    break;
                }

            }
        }

        if (isUnitDeployed == false) {
            // @dev
            // move any possible unit on the board.. by running a for loop
            // run for loop ..
            checkAndMove(out);
        }
    }

    public static void checkAndMove(ActorRef out) {

        highlightPossibleMoves(out, selectedUnitX, selectedUnitY);

        boolean canMove = true;
        System.out.println("current unit id " + currentUnit.getId());
        // if (currentUnit.getId() == 13) {
        for (int i = 0; i < permissiblePositions.size(); i++) {
            int posnx = permissiblePositions.get(i).get(0);
            int posny = permissiblePositions.get(i).get(1);
            if (GameState.gameBoard[posnx][posny] != null && (GameState.gameBoard[posnx][posny].getId() == 9
                    || GameState.gameBoard[posnx][posny].getId() == 15)) {
                System.out.println("Inside checkand move");
                canMove = false;
                break;
            }
        }
        // }
        System.out.println("going to moveunit method");
        if (canMove) {
            System.out.println("Can move this unit "  + canMove);
            moveUnit(out);
        }
    }

 public static void increaseUnitHealth(ActorRef out,int cnt){
        for (int k=0;k<9;k++){
            for (int j=0;j<5;j++){
                if (GameState.gameBoard[k][j] != null){
                    if (GameState.gameBoard[k][j].getId()==11){
                        if(getCard(out, GameState.gameBoard[k][j] )!= null){
                            Card a = getCard(out, GameState.gameBoard[k][j]);
                            a.getBigCard().setAttack(cnt);
                            a.getBigCard().setHealth(cnt+3);
                            
                       }
                        BasicCommands.setUnitHealth(out, GameState.gameBoard[k][j], cnt+3);
                        BasicCommands.setUnitAttack(out, GameState.gameBoard[k][j], cnt);
                    }
                }
            }
        }
    }

    public static void deployUnit(ActorRef out, int AIHandPosition) {
        boolean isSpecial = specialAbilityUnit(out, AIHandPosition);
        if (!isSpecial) {
            Random rand = new Random();
            int tilex = rand.nextInt(9);
            int tiley = rand.nextInt(5);
            boolean isPermissible = false;
            highlightUnitTiles(out);
            System.out.println("inside deploy unit " + permissiblePositions.size());
            while (!isPermissible) {
                for (int i = 0; i < permissiblePositions.size(); i++) {
                    if (permissiblePositions.get(i).get(0) == tilex
                            && permissiblePositions.get(i).get(1) == tiley
                            && GameState.gameBoard[tilex][tiley] == null) {
                        isPermissible = true;
                        break;

                    }
                }
                if (!isPermissible) {
                    tilex = rand.nextInt(9);
                    tiley = rand.nextInt(5);
                }
            }
            System.out.println("go to load new unit for AI");
            // @dev
            // ifselected card is a unit then deploy
            // if selected card is a spell and can deployed
            // if (GameState.gameBoard[tilex][tiley] == null) {
            // @dev move this to AI player
            loadNewUnit(out, tilex, tiley, AIHandPosition);
            // } else if (GameState.gameBoard[tilex][tiley] != null) {
            // @dev
            // if
            // if selected card is a unit then throw error
            // if selected card is spell and current unit is player unit perform action
            // if selected card is spell and current unit is opposite perform action

            // }
        }
    }

    public static void moveUnit(ActorRef out) {
        Random rand = new Random();
        int tilex = rand.nextInt(9);
        int tiley = rand.nextInt(5);
        boolean isPermissible = false;
        System.out.println("inside move AI unit " + permissiblePositions.size());
        while (!isPermissible) {
            for (int i = 0; i < permissiblePositions.size(); i++) {
                if (permissiblePositions.get(i).get(0) == tilex
                        && permissiblePositions.get(i).get(1) == tiley) {
                    isPermissible = true;
                    break;

                }
            }
            if (!isPermissible) {
                tilex = rand.nextInt(9);
                tiley = rand.nextInt(5);
            }
        }
        if (GameState.gameBoard[tilex][tiley] == null) {
            Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
            System.out.println("Load tile for AI to move " + currentUnit + " " + GameState.gameBoard[tilex][tiley]);
            // if (GameState.gameBoard[tilex][tiley] == null) {
            // move unit
            Unit unit = currentUnit;
            boolean unitMoved = false;
            if (movedUnits.contains(unit) || attackedUnits.contains(unit)) {
                BasicCommands.addPlayer1Notification(out, "Unit already moved", 2);
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
            System.out.println(selectedUnitX + " this is selectedUnitX");
            System.out.println(selectedUnitY + " this is selectedUnitY");
            GameState.gameBoard[tilex][tiley] = unit;
            GameState.gameBoard[selectedUnitX][selectedUnitY] = null;
            currentUnit = unit;

            System.out.println("inside tile for AI to move " + currentUnit + " " + GameState.gameBoard[tilex][tiley]);
            selectedUnitX = tilex;
            selectedUnitY = tiley;
            // create a new set of permissible positions
            removeHighlight(out);
            unitMoved = true;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // }
            // }
            if (!unitMoved) {
                selectedUnitX = -1;
                selectedUnitY = -1;

            }
        } else if (GameState.gameBoard[tilex][tiley] != null
                && HumanPlayer.humanCardNumbers.contains(GameState.gameBoard[tilex][tiley].getId())) {

            // @dev check provoked, if provoked only it can attack a unit that made it to
            // provoke.
            System.out.println("moved units length " + movedUnits.size());
            System.out.println("attacked units length " + attackedUnits.size());
            int count = 0;
            if (currentUnit.getId() == 14) {
                for (int p = 0; p < attackedUnits.size(); p++) {
                    if (attackedUnits.get(p).getId() == 14) {
                        System.out.println("double attack");
                        count++;
                    }
                }
                System.out.println("thsi is count " + count);
                if (count >= 2) {
                    BasicCommands.addPlayer1Notification(out, "Unit Already used for attacking", 2);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    removeHighlight(out);
                    System.out.println("already attacked");
                }
            } else if (attackedUnits.contains(currentUnit)) {
                BasicCommands.addPlayer1Notification(out, "Unit Already used for attacking", 2);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                removeHighlight(out);
                System.out.println("already attacked");
            }

            // @dev current unit is two steps ahead? then move one adjacent tile then attack
            int xDiff = Math.abs(selectedUnitX - tilex);
            int yDiff = Math.abs(selectedUnitY - tiley);
            Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
            System.out.println(xDiff + " this is xDiff");
            System.out.println(yDiff + " this is yDiff");
            if (xDiff == 2 || yDiff == 2) {
                if ((xDiff == 2 && yDiff < 2)) {
                    if (selectedUnitX > tilex) {
                        // comeback
                        Tile newTile = BasicObjectBuilders.loadTile(selectedUnitX - 1,
                                selectedUnitY);
                        Unit unit = currentUnit;

                        BasicCommands.moveUnitToTile(out, unit, newTile);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        unit.setPositionByTile(tile);
                        GameState.isUnitSelected = false;
                        GameState.gameBoard[selectedUnitX][selectedUnitY] = null;
                        GameState.gameBoard[selectedUnitX - 1][selectedUnitY] = unit;
                    } else if (selectedUnitX < tilex) {
                        // comeback
                        Tile newTile = BasicObjectBuilders.loadTile(selectedUnitX + 1,
                                selectedUnitY);
                        Unit unit = currentUnit;

                        BasicCommands.moveUnitToTile(out, unit, newTile);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        unit.setPositionByTile(tile);
                        GameState.isUnitSelected = false;
                        GameState.gameBoard[selectedUnitX][selectedUnitY] = null;
                        GameState.gameBoard[selectedUnitX + 1][selectedUnitY] = unit;
                    }

                } else if (xDiff < 2 && yDiff == 2) {
                    if (selectedUnitY > tiley) {
                        // comeback
                        Tile newTile = BasicObjectBuilders.loadTile(selectedUnitX,
                                selectedUnitY - 1);
                        Unit unit = currentUnit;

                        BasicCommands.moveUnitToTile(out, unit, newTile);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        unit.setPositionByTile(tile);
                        GameState.isUnitSelected = false;
                        GameState.gameBoard[selectedUnitX][selectedUnitY] = null;
                        GameState.gameBoard[selectedUnitX][selectedUnitY - 1] = unit;
                    } else if (selectedUnitY < tiley) {
                        // comeback
                        Tile newTile = BasicObjectBuilders.loadTile(selectedUnitX,
                                selectedUnitY + 1);
                        Unit unit = currentUnit;

                        BasicCommands.moveUnitToTile(out, unit, newTile);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        unit.setPositionByTile(tile);
                        GameState.isUnitSelected = false;
                        GameState.gameBoard[selectedUnitX][selectedUnitY] = null;
                        GameState.gameBoard[selectedUnitX][selectedUnitY + 1] = unit;
                    }
                }
            }

            System.out.println(selectedUnitX + " this is x " + selectedUnitY + " this is Y ");
            // removeHighlight(out);
            attackUnit(out, tilex, tiley);

            GameState.isUnitSelected = false;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // GameState.isPlayerTurnCompleted = true;

            removeHighlight(out);

        } else {
            moveUnit(out);
        }
    }

    public static String getUnit(Card card) {
        ObjectMapper mapper = new ObjectMapper();
        for (String eachUnit : AllUnits) {
            try {
                String json = readFileAsString(eachUnit);
                Unit unit = mapper.readValue(json, Unit.class);
                System.out.println("Inside getunit " + unit + " " + unit.getId() + " " + card + " " + card.getId());
                if (unit.getId() == card.getId()) {
                    return eachUnit;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void highlightPossibleMoves(ActorRef out, int x, int y) {
        // System.out.println(GameState.gameBoard[x][y].getId());
        if (GameState.gameBoard[x][y].getId() == 12) {
            System.out.println("I am a pyromancer!");
            // comefor
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 5; j++) {
                    // if()
                    if (GameState.gameBoard[i][j] != null) {
                        highlightTile(out, i, j);
                    }
                }
            }
        }
        if (GameState.gameBoard[x][y].getId() == 16) {
            System.out.println("I am a windshrike!");
            // comefor
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
            System.out.println("inside x,y-2");
        }

        if (y - 1 >= 0) {
            highlightTile(out, x, y - 1);
            System.out.println("inside x,y-1");
        }

        if (y + 1 <= 4) {
            highlightTile(out, x, y + 1);
            System.out.println("inside x,y+1");

        }
        if (y + 2 <= 4) {
            highlightTile(out, x, y + 2);
            System.out.println("inside x,y+2");

        }

        if (x - 2 >= 0) {
            highlightTile(out, x - 2, y);
            System.out.println("inside x-2,y");
        }

        if (x - 1 >= 0) {
            highlightTile(out, x - 1, y);
            System.out.println("inside x-1,y");
        }

        if (x + 1 <= 8) {
            highlightTile(out, x + 1, y);
            System.out.println("inside x+1,y");
        }

        if (x + 2 <= 8) {
            highlightTile(out, x + 2, y);
            System.out.println("inside x+2,y");
        }

        if (x - 1 >= 0 && y - 1 >= 0) {
            highlightTile(out, x - 1, y - 1);
            System.out.println("inside x-1,y-1");

        }

        if (x + 1 <= 8 && y - 1 >= 0) {
            highlightTile(out, x + 1, y - 1);
            System.out.println("inside x+1,y-1");
        }

        if (x - 1 >= 0 && y + 1 <= 4) {
            highlightTile(out, x - 1, y + 1);
            System.out.println("inside x-1,y+1");
        }

        if (x + 1 <= 8 && y + 1 <= 4) {
            highlightTile(out, x + 1, y + 1);
            System.out.println("inside x+1,y+1");

        }
    }

    public static void highlightTile(ActorRef out, int x, int y) {
        // int mode; 1003
        // System.out.println(GameState.gameBoard[x][y].getId() +" this b id");
        if (GameState.gameBoard[x][y] != null) {
            System.out.println(GameState.gameBoard[x][y].getId() + " this pos");
            if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[x][y].getId())
                    || GameState.gameBoard[x][y].getId() == 200) {
                // do not hughlight and add in highlighted list!
                System.out.println("yes AI contains");
                System.out.println(GameState.gameBoard[x][y].getId() + " this id");
            } else {
                // highlight in red and add in list
                System.out.println("inside highlightTile!");
                Tile tile = BasicObjectBuilders.loadTile(x, y);
                BasicCommands.addPlayer1Notification(out, "Highlight enemy", 2);
                // BasicCommands.drawTile(out, tile, 2);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
                eachPermissiblePosition.add(x);
                eachPermissiblePosition.add(y);
                permissiblePositions.add(eachPermissiblePosition);
                // try {Thread.sleep(2000);} catch (InterruptedException e)
                // {e.printStackTrace();}
            }
        } else {
            // add in list
            // Tile tile = BasicObjectBuilders.loadTile(x, y);
            // BasicCommands.addPlayer1Notification(out, "Highlight", 2);
            // BasicCommands.drawTile(out, tile, 1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<Integer> eachPermissiblePosition = new ArrayList<Integer>();
            eachPermissiblePosition.add(x);
            eachPermissiblePosition.add(y);
            permissiblePositions.add(eachPermissiblePosition);
            // try {Thread.sleep(2000);} catch (InterruptedException e)
            // {e.printStackTrace();}
            // highlight in white!
        }
    }

    public static void highlightUnitTiles(ActorRef out) {
        ///////////////// the code to know all possible tiles that can be
        ///////////////// deployed//////////
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
                // j,i-1
                if (i - 1 >= 0) {
                    if (GameState.gameBoard[i - 1][j] != null) {
                        System.out.println("inside [i - 1][j] first " + GameState.gameBoard[i - 1][j].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i - 1][j].getId())
                                || GameState.gameBoard[i - 1][j].getId() == 200) {
                            System.out.println("inside [i - 1][j]");
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (i + 1 <= 8) {
                    if (GameState.gameBoard[i + 1][j] != null) {
                        System.out.println("inside [i + 1][j] first " + GameState.gameBoard[i + 1][j].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i + 1][j].getId())
                                || GameState.gameBoard[i + 1][j].getId() == 200) {
                            System.out.println("inside [i + 1][j]");
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j - 1 >= 0) {
                    if (GameState.gameBoard[i][j - 1] != null) {
                        System.out.println("inside [i ][j - 1] first " + GameState.gameBoard[i][j - 1].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i][j - 1].getId())
                                || GameState.gameBoard[i][j - 1].getId() == 200) {
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            System.out.println("inside [i][j - 1]");
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j + 1 <= 4) {
                    if (GameState.gameBoard[i][j + 1] != null) {
                        System.out.println("inside [i ][j + 1] first " + GameState.gameBoard[i][j + 1].getId() + " "
                                + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i][j + 1].getId())
                                || GameState.gameBoard[i][j + 1].getId() == 200) {
                            System.out.println("inside [i][j + 1]");
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j - 1 >= 0 && i - 1 >= 0) {
                    if (GameState.gameBoard[i - 1][j - 1] != null) {
                        System.out.println(
                                "inside [i - 1 ][j - 1] first " + GameState.gameBoard[i - 1][j - 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i - 1][j - 1].getId())
                                || GameState.gameBoard[i - 1][j - 1].getId() == 200) {
                            System.out.println("inside [i - 1][j-1]");
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j + 1 <= 4 && i - 1 >= 0) {
                    if (GameState.gameBoard[i - 1][j + 1] != null) {
                        System.out.println(
                                "inside [i - 1][j - 1] first " + GameState.gameBoard[i - 1][j + 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i - 1][j + 1].getId())
                                || GameState.gameBoard[i - 1][j + 1].getId() == 200) {
                            System.out.println("inside [i - 1][j + 1]");
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }

                }
                if (j - 1 >= 0 && i + 1 <= 8) {
                    if (GameState.gameBoard[i + 1][j - 1] != null) {
                        System.out.println(
                                "inside [i + 1][j - 1] first " + GameState.gameBoard[i + 1][j - 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i + 1][j - 1].getId())
                                || GameState.gameBoard[i + 1][j - 1].getId() == 200) {
                            System.out.println("inside [i + 1][j -1]");
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }
                }
                if (j + 1 <= 4 && i + 1 <= 8) {
                    if (GameState.gameBoard[i + 1][j + 1] != null) {
                        System.out.println(
                                "inside [i + 1 ][j + 1] first " + GameState.gameBoard[i + 1][j + 1].getId() + " "
                                        + AIOpponent.AICardNumbers);
                        if (AIOpponent.AICardNumbers.contains(GameState.gameBoard[i + 1][j + 1].getId())
                                || GameState.gameBoard[i + 1][j + 1].getId() == 200) {
                            System.out.println("inside [i+1][j+1]");
                            // Tile tile = BasicObjectBuilders.loadTile(i, j);
                            // BasicCommands.addPlayer1Notification(out, "deploy Highlight", 2);
                            // BasicCommands.drawTile(out, tile, 1);
                            ArrayList<Integer> newPosition = new ArrayList<Integer>();
                            newPosition.add(i);
                            newPosition.add(j);
                            permissiblePositions.add(newPosition);
                            System.out.println("done unit positions");
                        }
                    }

                }
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void loadMana(ActorRef out, int newManaValue) {

        while (AIOpponent.AIStats.getMana() <= 9 && newManaValue > 0 && newManaValue <= 9) {
            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() + 1);
            BasicCommands.addPlayer1Notification(out, "setPlayer2Mana", 1);
            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            newManaValue = newManaValue - 1;
        }
        AIOpponent.addNewCard(out);
    }

    public static void deleteCard(ActorRef out, int AIHandPosition) {

        System.out.println("inside delete card!");

        BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
        // BasicCommands.deleteCard(out, AIHandPosition);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AIHand[AIHandPosition] = null;

        // move cards
    }

    public static void addNewCard(ActorRef out) {

        try{

      
        System.out.println("adding a new hand");
        int position = 0;

        for (int i = 0; i < AIHand.length; i++) {

            if (AIHand[i] == null) {
                break;
            } else {
                position += 1;
            }
        }
        System.out.println(
                position + " this is position" + currentCardNumber + " " + AICards + " " + AICards[currentCardNumber]);
        if (position < 6 && currentCardNumber < AICards.length) {
            BasicCommands.addPlayer1Notification(out, AICards[currentCardNumber], 2);
            AIHand[position] = AICards[currentCardNumber];
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentCardNumber += 1;
            System.out.println("done add new card");
        } else if (position >= 6) {
            // @dev write logic loose cards
            currentCardNumber += 1;
        }

            /* */

        	// playerHandCount++;
			if(currentCardNumber >= AIOpponentCards.size()){
				boolean checkHandEmpty = true;
				for(int i = 0; i < AIHand.length;i++ ){
					if(AIHand[i] != null){
						checkHandEmpty = false;
					}
				}

				if(checkHandEmpty) { 
					BasicCommands.addPlayer1Notification(out, "Game Ended! Player won!", 2);
					GameState.gameEnded = true;
				}
			}
              }catch ( Exception e){
                System.out.println("Draw Unit, error!");
              }
    }

    public static boolean specialAbilityUnit(ActorRef out, int AIHandPosition) {
        if (GameState.currentCard.getId() == 10) {
            Random rand = new Random();
            int tilex = rand.nextInt(9);
            int tiley = rand.nextInt(5);
            boolean isPermissible = false;
            System.out.println("inside specialAbilityUnit planar" + permissiblePositions.size());
            while (!isPermissible) {
                if (GameState.gameBoard[tilex][tiley] == null) {
                    isPermissible = true;
                    break;

                }
                if (!isPermissible) {
                    tilex = rand.nextInt(9);
                    tiley = rand.nextInt(5);
                }
            }
            loadNewUnit(out, tilex, tiley, AIHandPosition);
            System.out.println("Load tile for AI specialAbilityUnit");
            return true;
        }
        return false;
    }

    public static void loadNewUnit(ActorRef out, int tilex, int tiley, int AIHandPosition) {
        try {
            Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
            BasicCommands.addPlayer1Notification(out, "Deploying AI Unit", 2);
            System.out.println(GameState.currentCard.getId() + " this is the id of the unit");
            Unit unit = BasicObjectBuilders.loadUnit(getUnit(GameState.currentCard),
                    GameState.currentCard.getId(), Unit.class);
            System.out.println("test before posn");
            unit.setPositionByTile(tile);
            System.out.println("after set");
            BasicCommands.drawUnit(out, unit, tile);
            System.out.println("after draw unit");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GameState.gameBoard[tilex][tiley] = unit;
            // @dev delete all permissible positions
            currentUnit = unit;
            deployedUnits.add(currentUnit);
            selectedUnitX = tilex;
            selectedUnitY = tiley;
            // create new set of permissible positions
            System.out.println(
                    "inside deploy unit " + GameState.gameBoard[tilex][tiley] + " " + unit + " " + currentUnit);

            // Set unit Health
            BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
            BasicCommands.setUnitHealth(out, unit, GameState.currentCard.getBigCard().getHealth());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // setUnitAttack
            BasicCommands.addPlayer1Notification(out, "setUnitAttack", 2);
            BasicCommands.setUnitAttack(out, unit, GameState.currentCard.getBigCard().getAttack());
            AIOpponentCards.add(GameState.currentCard);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // delete card
            AIOpponent.deleteCard(out, AIHandPosition);
            if (GameState.currentCard.getId() == 3) {
                AIOpponent.addNewCard(out);
                HumanPlayer.addNewCard(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // @dev Reduce mana here;
        for (int i = 0; i < GameState.currentCard.getManacost(); i++) {
            AIOpponent.AIStats.setMana(AIOpponent.AIStats.getMana() - 1);
            BasicCommands.setPlayer2Mana(out, AIOpponent.AIStats);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void removeHighlight(ActorRef out) {
        for (int i = 0; i < permissiblePositions.size(); i++) {
            Tile tile = BasicObjectBuilders.loadTile(permissiblePositions.get(i).get(0),
                    permissiblePositions.get(i).get(1));
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
}