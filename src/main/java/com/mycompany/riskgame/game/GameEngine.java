/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.riskgame.game;

import com.mycompany.riskgame.map.GameMap;
import com.mycompany.riskgame.model.Player;
import com.mycompany.riskgame.model.Territory;
import java.util.Random;
import java.util.Arrays;

public class GameEngine {

    private GameMap gameMap;
    private TurnManager turnManager;
    private Random random;
    private GamePhase currentPhase;
    private int remainingDraftTroops;
    private boolean fortifyUsed;
    private boolean gameOver;

    public GameEngine() {
        gameMap = new GameMap();
        turnManager = new TurnManager();
        random = new Random();
        currentPhase = GamePhase.DRAFT;
        remainingDraftTroops = 3;
        fortifyUsed = false;
        gameOver = false;
    }

    public String handleCommand(String message, String playerName) {

        if (message == null) {
            return "EMPTY_COMMAND";
        }

        if (message.startsWith("JOIN ")) {
            return handleJoin(message);
        }

        if (message.equals("MAP")) {
            return handleMap();
        }
        if (gameOver) {
            return "GAME_ALREADY_OVER";
        }

        if (playerName == null) {
            return "PLEASE_JOIN_FIRST";
        }

        if (!turnManager.hasPlayers()) {
            return "NO_PLAYERS";
        }

        String currentPlayer = turnManager.getCurrentPlayer().getName();

        if (!playerName.equals(currentPlayer)) {
            return "NOT_YOUR_TURN";
        }

        if (message.equals("END_TURN")) {
            return handleEndTurn();
        }
        if (message.equals("NEXT_PHASE")) {
            return handleNextPhase();
        }

        if (message.startsWith("DRAFT ")) {
            return handleDraft(message);
        }

        if (message.startsWith("ATTACK ")) {
            return handleAttack(message);
        }

        if (message.startsWith("BLITZ ")) {
            return handleBlitz(message);
        }

        if (message.startsWith("FORTIFY ")) {
            return handleFortify(message);
        }

        return "UNKNOWN_COMMAND";
    }

    private String handleJoin(String message) {
        String playerName = message.substring(5);

        Player player = new Player(playerName);
        turnManager.addPlayer(player);

        System.out.println("Player joined: " + playerName);
        System.out.println("Total players: " + turnManager.getPlayerCount());

        if (turnManager.getPlayerCount() == 2) {
            gameMap.autoDistributeTerritories(turnManager.getPlayers());
            remainingDraftTroops = calculateDraftTroops(turnManager.getCurrentPlayer().getName());

            System.out.println("Two players connected. Game can start!");;
            System.out.println("Territories distributed automatically.");
            System.out.println("Turn: " + turnManager.getCurrentPlayer().getName());

            return "GAME_START Turn: " + turnManager.getCurrentPlayer().getName();
        }

        return "WELCOME " + playerName;
    }

    private String handleEndTurn() {

        if (currentPhase != GamePhase.FORTIFY) {
            return "NOT_FORTIFY_PHASE";
        }

        if (!turnManager.hasPlayers()) {
            return "NO_PLAYERS";
        }

        Player nextPlayer = turnManager.nextTurn();
        currentPhase = GamePhase.DRAFT;
        remainingDraftTroops = calculateDraftTroops(nextPlayer.getName());
        fortifyUsed = false;

        System.out.println("Turn: " + nextPlayer.getName());

        return "TURN " + nextPlayer.getName();
    }

    private String handleNextPhase() {

        if (currentPhase == GamePhase.DRAFT) {

            if (remainingDraftTroops > 0) {
                return "DRAFT_NOT_FINISHED Remaining troops: " + remainingDraftTroops;
            }

            currentPhase = GamePhase.ATTACK;
            return "PHASE_ATTACK";
        }

        if (currentPhase == GamePhase.ATTACK) {
            currentPhase = GamePhase.FORTIFY;
            return "PHASE_FORTIFY";
        }

        return "ALREADY_IN_FORTIFY_USE_END_TURN";
    }

    private String handleDraft(String message) {
        if (currentPhase != GamePhase.DRAFT) {
            return "NOT_DRAFT_PHASE";
        }

        String[] parts = message.split(" ");

        if (parts.length < 3) {
            return "INVALID_COMMAND";
        }

        String territoryName = parts[1];

        int troopAmount;

        try {
            troopAmount = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return "INVALID_TROOP_AMOUNT";
        }

        if (troopAmount <= 0) {
            return "INVALID_TROOP_AMOUNT";
        }

        if (troopAmount > remainingDraftTroops) {
            return "NOT_ENOUGH_DRAFT_TROOPS";
        }

        if (!turnManager.hasPlayers()) {
            return "NO_PLAYERS";
        }

        Territory territory = gameMap.findTerritoryByName(territoryName);

        if (territory == null) {
            return "INVALID_TERRITORY";
        }

        String currentPlayer = turnManager.getCurrentPlayer().getName();

        if (!territory.getOwner().equals(currentPlayer)) {
            return "NOT_YOUR_TERRITORY";
        }

        territory.addTroops(troopAmount);
        remainingDraftTroops -= troopAmount;

        System.out.println(troopAmount + " troops added to " + territory.getName());
        System.out.println(territory.getName() + " total troops: " + territory.getTroops());

        return "DRAFT_SUCCESS Remaining draft troops: " + remainingDraftTroops;
    }

    private String handleAttack(String message) {

        if (currentPhase != GamePhase.ATTACK) {
            return "NOT_ATTACK_PHASE";
        }

        String[] parts = message.split(" ");

        if (parts.length < 3) {
            return "INVALID_COMMAND";
        }

        if (!turnManager.hasPlayers()) {
            return "NO_PLAYERS";
        }

        String attackerTerritoryName = parts[1];
        String defenderTerritoryName = parts[2];

        Territory attackerTerritory = gameMap.findTerritoryByName(attackerTerritoryName);
        Territory defenderTerritory = gameMap.findTerritoryByName(defenderTerritoryName);

        if (attackerTerritory == null || defenderTerritory == null) {
            return "INVALID_TERRITORY";
        }

        String currentPlayer = turnManager.getCurrentPlayer().getName();

        if (!attackerTerritory.getOwner().equals(currentPlayer)) {
            return "NOT_YOUR_TERRITORY";
        }

        if (attackerTerritory.getOwner().equals(defenderTerritory.getOwner())) {
            return "SAME_OWNER";
        }

        if (!attackerTerritory.isNeighbor(defenderTerritory)) {
            return "NOT_NEIGHBORS";
        }

        if (attackerTerritory.getTroops() < 2) {
            return "NOT_ENOUGH_TROOPS";
        }
        int attackerDiceCount = Math.min(3, attackerTerritory.getTroops() - 1);
        int defenderDiceCount = Math.min(2, defenderTerritory.getTroops());

        int[] attackerDice = new int[attackerDiceCount];
        int[] defenderDice = new int[defenderDiceCount];

        for (int i = 0; i < attackerDiceCount; i++) {
            attackerDice[i] = random.nextInt(6) + 1;
        }

        for (int i = 0; i < defenderDiceCount; i++) {
            defenderDice[i] = random.nextInt(6) + 1;
        }

        Arrays.sort(attackerDice);
        Arrays.sort(defenderDice);

        System.out.println(attackerTerritory.getName() + " attacks " + defenderTerritory.getName());
        System.out.println("Attacker dice: " + Arrays.toString(attackerDice));
        System.out.println("Defender dice: " + Arrays.toString(defenderDice));

        int comparisons = Math.min(attackerDiceCount, defenderDiceCount);
        int attackerLosses = 0;
        int defenderLosses = 0;

        for (int i = 0; i < comparisons; i++) {
            int attackerRoll = attackerDice[attackerDice.length - 1 - i];
            int defenderRoll = defenderDice[defenderDice.length - 1 - i];

            if (attackerRoll > defenderRoll) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }

        attackerTerritory.removeTroops(attackerLosses);
        defenderTerritory.removeTroops(defenderLosses);

        System.out.println("Attacker losses: " + attackerLosses);
        System.out.println("Defender losses: " + defenderLosses);
        System.out.println(attackerTerritory.getName() + " troops left: " + attackerTerritory.getTroops());
        System.out.println(defenderTerritory.getName() + " troops left: " + defenderTerritory.getTroops());

        if (defenderTerritory.getTroops() <= 0) {
            String oldOwner = defenderTerritory.getOwner();
            String newOwner = attackerTerritory.getOwner();

            defenderTerritory.setOwner(newOwner);

            attackerTerritory.removeTroops(1);
            defenderTerritory.addTroops(1);

            System.out.println(defenderTerritory.getName() + " captured!");
            System.out.println("Old owner: " + oldOwner);
            System.out.println("New owner: " + newOwner);

            if (checkWinner(newOwner)) {
                gameOver = true;
                return "GAME_OVER Winner: " + newOwner;
            }

            return "TERRITORY_CAPTURED";
        }

        return "ATTACK_RESULT Attacker lost: " + attackerLosses + " Defender lost: " + defenderLosses;

    }

    private String handleBlitz(String message) {

        if (currentPhase != GamePhase.ATTACK) {
            return "NOT_ATTACK_PHASE";
        }

        String[] parts = message.split(" ");

        if (parts.length < 3) {
            return "INVALID_COMMAND";
        }

        while (true) {

            String attackCommand = "ATTACK " + parts[1] + " " + parts[2];
            String result = handleAttack(attackCommand);

            if (result.equals("TERRITORY_CAPTURED")) {
                return "BLITZ_SUCCESS";
            }

            if (result.startsWith("GAME_OVER")) {
                return result;
            }

            Territory attacker = gameMap.findTerritoryByName(parts[1]);
            Territory defender = gameMap.findTerritoryByName(parts[2]);

            if (attacker == null || defender == null) {
                return "INVALID_TERRITORY";
            }

            if (attacker.getOwner().equals(defender.getOwner())) {
                return "BLITZ_SUCCESS";
            }

            if (attacker.getTroops() < 2) {
                return "BLITZ_FAILED";
            }
        }
    }

    private String handleFortify(String message) {

        if (currentPhase != GamePhase.FORTIFY) {
            return "NOT_FORTIFY_PHASE";
        }

        if (fortifyUsed) {
            return "FORTIFY_ALREADY_USED";
        }

        String[] parts = message.split(" ");

        if (parts.length < 4) {
            return "INVALID_COMMAND";
        }

        if (!turnManager.hasPlayers()) {
            return "NO_PLAYERS";
        }

        String fromName = parts[1];
        String toName = parts[2];

        int troopAmount;

        try {
            troopAmount = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            return "INVALID_TROOP_AMOUNT";
        }

        if (troopAmount <= 0) {
            return "INVALID_TROOP_AMOUNT";
        }

        Territory fromTerritory = gameMap.findTerritoryByName(fromName);
        Territory toTerritory = gameMap.findTerritoryByName(toName);

        if (fromTerritory == null || toTerritory == null) {
            return "INVALID_TERRITORY";
        }

        String currentPlayer = turnManager.getCurrentPlayer().getName();

        if (!fromTerritory.getOwner().equals(currentPlayer)) {
            return "NOT_YOUR_TERRITORY";
        }

        if (!toTerritory.getOwner().equals(currentPlayer)) {
            return "TARGET_NOT_YOURS";
        }

        if (!fromTerritory.hasPathTo(toTerritory, currentPlayer)) {
            return "NO_CONNECTED_PATH";
        }

        if (fromTerritory.getTroops() <= troopAmount) {
            return "NOT_ENOUGH_TROOPS";
        }

        fromTerritory.removeTroops(troopAmount);
        toTerritory.addTroops(troopAmount);
        fortifyUsed = true;

        System.out.println(fromTerritory.getName() + " troops: " + fromTerritory.getTroops());
        System.out.println(toTerritory.getName() + " troops: " + toTerritory.getTroops());

        System.out.println(
                troopAmount + " troops moved from "
                + fromTerritory.getName()
                + " to "
                + toTerritory.getName()
        );

        return "FORTIFY_SUCCESS";
    }

    private String handleMap() {
        StringBuilder mapInfo = new StringBuilder();

        mapInfo.append("======== MAP ========\n");

        for (Territory territory : gameMap.getTerritories()) {
            mapInfo.append("\n");
            mapInfo.append("Territory: ").append(territory.getName()).append("\n");
            mapInfo.append("Owner: ").append(territory.getOwner()).append("\n");
            mapInfo.append("Troops: ").append(territory.getTroops()).append("\n");

            mapInfo.append("Neighbors: ");

            for (Territory neighbor : territory.getNeighbors()) {
                mapInfo.append(neighbor.getName()).append(" ");
            }

            mapInfo.append("\n");
        }

        mapInfo.append("=====================");

        System.out.println(mapInfo.toString());

        return mapInfo.toString().replace("\n", " | ");
    }

    private int calculateDraftTroops(String playerName) {

        int ownedTerritories = 0;

        for (Territory territory : gameMap.getTerritories()) {
            if (territory.getOwner().equals(playerName)) {
                ownedTerritories++;
            }
        }

        int troopsFromTerritories = ownedTerritories / 3;
        int baseTroops = Math.max(3, troopsFromTerritories);

        int continentBonus = calculateContinentBonus(playerName);

        return baseTroops + continentBonus;
    }

    private int calculateContinentBonus(String playerName) {

        String[] northAmerica = {
            "Alaska",
            "Alberta",
            "NorthwestTerritory",
            "Greenland",
            "Ontario",
            "Quebec",
            "WesternUS",
            "EasternUS"
        };

        for (String territoryName : northAmerica) {
            Territory territory = gameMap.findTerritoryByName(territoryName);

            if (territory == null || !territory.getOwner().equals(playerName)) {
                return 0;
            }
        }

        return 5;
    }

    private boolean checkWinner(String playerName) {

        for (Territory territory : gameMap.getTerritories()) {

            if (!territory.getOwner().equals(playerName)) {
                return false;
            }
        }

        return true;
    }
}
