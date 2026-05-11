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

        Territory olympus = new Territory("Olympus");
        Territory sparta = new Territory("Sparta");
        Territory athens = new Territory("Athens");
        Territory delphi = new Territory("Delphi");
        Territory arcadia = new Territory("Arcadia");
        Territory troy = new Territory("Troy");
        Territory elysium = new Territory("Elysium");
        Territory mycenae = new Territory("Mycenae");
        Territory rhodes = new Territory("Rhodes");
        Territory corinth = new Territory("Corinth");
        Territory crete = new Territory("Crete");
        Territory olympia = new Territory("Olympia");

        olympus.addNeighbor(sparta);
        olympus.addNeighbor(delphi);
        
        sparta.addNeighbor(olympus);
        sparta.addNeighbor(athens);
        sparta.addNeighbor(delphi);
        
        athens.addNeighbor(sparta);
        athens.addNeighbor(delphi);

        delphi.addNeighbor(olympus);
        delphi.addNeighbor(sparta);
        delphi.addNeighbor(athens);
        
        arcadia.addNeighbor(troy);
        
        troy.addNeighbor(arcadia);
        troy.addNeighbor(elysium);
        
        elysium.addNeighbor(troy);
        elysium.addNeighbor(mycenae);
        
        mycenae.addNeighbor(elysium);
          
        rhodes.addNeighbor(corinth);
        rhodes.addNeighbor(crete);
  
        corinth.addNeighbor(rhodes);
        corinth.addNeighbor(crete);
        corinth.addNeighbor(olympia);
        
        crete.addNeighbor(rhodes);
        crete.addNeighbor(corinth);
        crete.addNeighbor(olympia);
 
        olympia.addNeighbor(corinth);
        olympia.addNeighbor(crete);
        
        // --- ADALAR ARASI DENİZ YOLU BAĞLANTILARI ---
        
        // Sol Üst ile Sol Alt bağlantısı
        athens.addNeighbor(arcadia);
        arcadia.addNeighbor(athens);
        
        // Sol Üst ile Sağ bağlantısı
        delphi.addNeighbor(olympia);
        olympia.addNeighbor(delphi);
        
        // Sol Alt ile Sağ bağlantısı
        mycenae.addNeighbor(rhodes);
        rhodes.addNeighbor(mycenae);

        territories.add(olympus);
        territories.add(sparta);
        territories.add(athens);
        territories.add(delphi);
        territories.add(arcadia);
        territories.add(troy);
        territories.add(elysium);
        territories.add(mycenae);
        territories.add(rhodes);
        territories.add(corinth);
        territories.add(crete);
        territories.add(olympia);
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
