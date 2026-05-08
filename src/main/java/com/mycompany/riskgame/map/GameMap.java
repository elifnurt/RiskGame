/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.riskgame.map;

import com.mycompany.riskgame.model.Territory;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.riskgame.model.Player;
import java.util.Collections;

public class GameMap {

    private List<Territory> territories;

    public GameMap() {
        territories = new ArrayList<>();
        setupMap();
    }

    private void setupMap() {

    Territory alaska = new Territory("Alaska");
    Territory alberta = new Territory("Alberta");
    Territory northwestTerritory = new Territory("NorthwestTerritory");
    Territory greenland = new Territory("Greenland");
    Territory ontario = new Territory("Ontario");
    Territory quebec = new Territory("Quebec");
    Territory westernUS = new Territory("WesternUS");
    Territory easternUS = new Territory("EasternUS");

    alaska.addNeighbor(alberta);
    alaska.addNeighbor(northwestTerritory);

    alberta.addNeighbor(alaska);
    alberta.addNeighbor(northwestTerritory);
    alberta.addNeighbor(ontario);
    alberta.addNeighbor(westernUS);

    northwestTerritory.addNeighbor(alaska);
    northwestTerritory.addNeighbor(alberta);
    northwestTerritory.addNeighbor(ontario);
    northwestTerritory.addNeighbor(greenland);

    greenland.addNeighbor(northwestTerritory);
    greenland.addNeighbor(ontario);
    greenland.addNeighbor(quebec);

    ontario.addNeighbor(alberta);
    ontario.addNeighbor(northwestTerritory);
    ontario.addNeighbor(greenland);
    ontario.addNeighbor(quebec);
    ontario.addNeighbor(westernUS);
    ontario.addNeighbor(easternUS);

    quebec.addNeighbor(greenland);
    quebec.addNeighbor(ontario);
    quebec.addNeighbor(easternUS);

    westernUS.addNeighbor(alberta);
    westernUS.addNeighbor(ontario);
    westernUS.addNeighbor(easternUS);

    easternUS.addNeighbor(ontario);
    easternUS.addNeighbor(quebec);
    easternUS.addNeighbor(westernUS);

    territories.add(alaska);
    territories.add(alberta);
    territories.add(northwestTerritory);
    territories.add(greenland);
    territories.add(ontario);
    territories.add(quebec);
    territories.add(westernUS);
    territories.add(easternUS);
}

    public Territory findTerritoryByName(String name) {

        for (Territory territory : territories) {

            if (territory.getName().equalsIgnoreCase(name)) {
                return territory;
            }
        }

        return null;
    }
    
    public void autoDistributeTerritories(List<Player> players) {

    Collections.shuffle(territories);

    for (int i = 0; i < territories.size(); i++) {
        Player owner = players.get(i % players.size());

        territories.get(i).setOwner(owner.getName());
        territories.get(i).addTroops(1);
    }
}

    public List<Territory> getTerritories() {
        return territories;
    }
}
