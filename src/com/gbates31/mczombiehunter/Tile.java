package com.gbates31.mczombiehunter;

public class Tile {

    public boolean hasZombie;
    public boolean hasHunter;
    public boolean hasVictim;
    public boolean isOccupied;
    public boolean moveAttempted;

    public Tile () {
        hasZombie = false;
        hasHunter = false;
        hasVictim = false;
        isOccupied = false;
        moveAttempted = false;
        
    }
}