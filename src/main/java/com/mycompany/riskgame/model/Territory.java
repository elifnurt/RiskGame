package com.mycompany.riskgame.model;

import java.util.ArrayList;
import java.util.List;

public class Territory {

    private String name;
    private String owner;
    private int troops;
    private List<Territory> neighbors;

    public Territory(String name) {
        this.name = name;
        this.owner = null;
        this.troops = 0;
        this.neighbors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getTroops() {
        return troops;
    }

    public void addTroops(int amount) {
        troops += amount;
    }

    public void removeTroops(int amount) {
        troops -= amount;
    }

    public void addNeighbor(Territory territory) {
        if (territory != null && !neighbors.contains(territory)) {
            neighbors.add(territory);
        }
    }

    public boolean isNeighbor(Territory territory) {
        return neighbors.contains(territory);
    }

    public List<Territory> getNeighbors() {
        return neighbors;
    }

    public boolean hasPathTo(Territory target, String owner) {
        return hasPathToHelper(target, owner, new ArrayList<>());
    }

    private boolean hasPathToHelper(Territory target, String owner, List<Territory> visited) {
        if (this == target) {
            return true;
        }

        visited.add(this);

        for (Territory neighbor : neighbors) {
            if (!visited.contains(neighbor)
                    && owner != null
                    && owner.equals(neighbor.getOwner())) {

                if (neighbor.hasPathToHelper(target, owner, visited)) {
                    return true;
                }
            }
        }

        return false;
    }
}