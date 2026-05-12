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
    private static final String[] NORTHERN_GREECE = {"Olympus", "Delphi", "Sparta", "Athens"};
    private static final String[] TROY_REGION = {"Arcadia", "Troy", "Elysium", "Mycenae"};
    private static final String[] ISLANDS_REGION = {"Rhodes", "Corinth", "Crete", "Olympia"};

    public GameEngine() {
        gameMap = new GameMap();
        turnManager = new TurnManager();
        random = new Random();
        currentPhase = GamePhase.WAITING_FOR_PLAYERS;
        remainingDraftTroops = 0;
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
        if (currentPhase == GamePhase.WAITING_FOR_PLAYERS) {
            return "WAITING_FOR_PLAYERS";
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
        String[] parts = message.split(" ", 2);

        if (parts.length < 2) {
            return "ERROR: Invalid name";
        }

        String playerName = parts[1].trim();

        if (playerName.isEmpty()) {
            return "ERROR: Invalid name";
        }

        if (playerName.contains(":") || playerName.contains(";") || playerName.contains("|")) {
            return "ERROR: Invalid name characters";
        }

        if (turnManager.getPlayerCount() >= 2) {
            return "ERROR: Game is full";
        }

        for (Player player : turnManager.getPlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return "ERROR: Name already taken";
            }
        }

        Player player = new Player(playerName);
        turnManager.addPlayer(player);

        if (turnManager.getPlayerCount() == 2) {
            gameMap.autoDistributeTerritories(turnManager.getPlayers());
            currentPhase = GamePhase.DRAFT;
            remainingDraftTroops = calculateDraftTroops(turnManager.getCurrentPlayer().getName());
            return "SUCCESS: Game started and territories distributed! TURN "
                    + turnManager.getCurrentPlayer().getName()
                    + " Draft troops: "
                    + remainingDraftTroops;
        }

        return "SUCCESS: Joined as " + playerName + ". Waiting for second player...";
    }

    private String handleEndTurn() {

        if (currentPhase != GamePhase.FORTIFY) {
            return "NOT_FORTIFY_PHASE";
        }

        Player nextPlayer = turnManager.nextTurn();
        currentPhase = GamePhase.DRAFT;
        remainingDraftTroops = calculateDraftTroops(nextPlayer.getName());
        fortifyUsed = false;

        return "TURN " + nextPlayer.getName() + " Draft troops: " + remainingDraftTroops;
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

        if (defenderTerritory.getTroops() <= 0) {
            String newOwner = attackerTerritory.getOwner();

            defenderTerritory.setOwner(newOwner);

            attackerTerritory.removeTroops(1);
            defenderTerritory.addTroops(1);

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

        return "FORTIFY_SUCCESS";
    }

    private String handleMap() {
        StringBuilder sb = new StringBuilder();
        for (Territory t : gameMap.getTerritories()) {
            sb.append(t.getName()).append(":");

            if (t.getOwner() == null) {
                sb.append("NONE");
            } else {
                sb.append(t.getOwner());
            }

            sb.append(":").append(t.getTroops()).append("|");
        }

        return sb.toString();
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
        int totalBonus = 0;

        if (ownsContinent(playerName, NORTHERN_GREECE)) {
            totalBonus += 3;
        }

        if (ownsContinent(playerName, TROY_REGION)) {
            totalBonus += 3;
        }

        if (ownsContinent(playerName, ISLANDS_REGION)) {
            totalBonus += 3;
        }

        return totalBonus;
    }

    private boolean ownsContinent(String playerName, String[] continentTerritories) {
        for (String territoryName : continentTerritories) {
            Territory territory = gameMap.findTerritoryByName(territoryName);
            if (territory == null || territory.getOwner() == null || !territory.getOwner().equals(playerName)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkWinner(String playerName) {

        for (Territory territory : gameMap.getTerritories()) {

            if (!territory.getOwner().equals(playerName)) {
                return false;
            }
        }

        return true;
    }

    public String getGameUpdate() {
        String currentPlayerName = "NONE";

        if (turnManager.getCurrentPlayer() != null) {
            currentPlayerName = turnManager.getCurrentPlayer().getName();
        }

        return currentPhase + ";"
                + currentPlayerName + ";"
                + remainingDraftTroops + ";"
                + handleMap();
    }
}
