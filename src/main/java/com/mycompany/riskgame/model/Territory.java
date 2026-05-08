/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.riskgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elifnur
 */
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
        neighbors.add(territory);
    }

    public List<Territory> getNeighbors() {
        return neighbors;
    }
    
    public boolean isNeighbor(Territory territory) {

    return neighbors.contains(territory);
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
                && neighbor.getOwner().equals(owner)) {

            if (neighbor.hasPathToHelper(target, owner, visited)) {
                return true;
            }
        }
    }

    return false;
}
}
