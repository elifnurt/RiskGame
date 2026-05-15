package com.mycompany.riskgame.map;

import com.mycompany.riskgame.model.Player;
import com.mycompany.riskgame.model.Territory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        connect(olympus, sparta);
        connect(olympus, delphi);
        connect(sparta, athens);
        connect(sparta, delphi);
        connect(athens, delphi);

        connect(arcadia, troy);
        connect(arcadia, elysium);
        connect(troy, elysium);
        connect(troy, mycenae);
        connect(elysium, mycenae);

        connect(rhodes, corinth);
        connect(rhodes, crete);
        connect(corinth, crete);
        connect(corinth, olympia);
        connect(crete, olympia);

        connect(athens, arcadia);
        connect(delphi, olympia);
        connect(mycenae, rhodes);

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

    private void connect(Territory first, Territory second) {
        first.addNeighbor(second);
        second.addNeighbor(first);
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
        if (players == null || players.isEmpty()) {
            return;
        }

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
