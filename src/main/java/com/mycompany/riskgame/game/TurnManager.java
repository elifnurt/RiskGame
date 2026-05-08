/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.riskgame.game;

import com.mycompany.riskgame.model.Player;
import java.util.ArrayList;
import java.util.List;

public class TurnManager {

    private List<Player> players;
    private int currentTurnIndex;

    public TurnManager() {
        players = new ArrayList<>();
        currentTurnIndex = 0;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public boolean hasPlayers() {
        return !players.isEmpty();
    }

    public int getPlayerCount() {
        return players.size();
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            return null;
        }

        return players.get(currentTurnIndex);
    }

    public Player nextTurn() {
        if (players.isEmpty()) {
            return null;
        }

        currentTurnIndex = (currentTurnIndex + 1) % players.size();
        return getCurrentPlayer();
    }

    public List<Player> getPlayers() {
        return players;
    }
}