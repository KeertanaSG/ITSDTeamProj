package actions;

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

public class AIOpponent {
    String[] deckAICards = {
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

    public void drawCards(ActorRef out) {
        // Player Cards
        BasicCommands.addPlayer1Notification(out, "AI Test", 2);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setHealthAndMana(ActorRef out) {
        // setAIPlayerHealth
        BasicCommands.addPlayer1Notification(out, "setPlayer2Health", 2);
        Player aiPlayer = new Player(20, 2);
        BasicCommands.setPlayer2Health(out, aiPlayer);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mana
        for (int m = 0; m < 3; m++) {
            BasicCommands.addPlayer1Notification(out, "setPlayer2Mana (" + m + ")", 1);
            aiPlayer.setMana(m);
            BasicCommands.setPlayer2Mana(out, aiPlayer);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteCards(ActorRef out) {
        // deleteCard
        BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
        BasicCommands.deleteCard(out, 1);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addUnit(ActorRef out) {
        // loadTile for unit
        Tile tile = BasicObjectBuilders.loadTile(7, 2);
        // drawUnit
        BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
        unit.setPositionByTile(tile);
        BasicCommands.drawUnit(out, unit, tile);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // setUnitAttack
        BasicCommands.addPlayer1Notification(out, "setUnitAttack", 2);
        BasicCommands.setUnitAttack(out, unit, 2);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // setUnitHealth
        BasicCommands.addPlayer1Notification(out, "setUnitHealth", 2);
        BasicCommands.setUnitHealth(out, unit, 20);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}