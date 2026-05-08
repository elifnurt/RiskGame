/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.riskgame.map;

import com.mycompany.riskgame.model.Territory;
import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private List<Territory> territories;

    public GameMap() {
        territories = new ArrayList<>();
        setupMap();
    }

    private void setupMap() {

        Territory alaska = new Territory("Alaska");
        Territory alberta = new Territory("Alberta");
        Territory ontario = new Territory("Ontario");

        alaska.addNeighbor(alberta);
        alaska.addNeighbor(ontario);

        alberta.addNeighbor(alaska);

        ontario.addNeighbor(alaska);

        alaska.setOwner("Elif");
        alberta.setOwner("Oyuncu2");
        ontario.setOwner("Elif");

        alaska.addTroops(20);
        alberta.addTroops(5);
        ontario.addTroops(3);

        territories.add(alaska);
        territories.add(alberta);
        territories.add(ontario);
    }

    public Territory findTerritoryByName(String name) {

        for (Territory territory : territories) {

            if (territory.getName().equalsIgnoreCase(name)) {
                return territory;
            }
        }

        return null;
    }

    public List<Territory> getTerritories() {
        return territories;
    }
}
