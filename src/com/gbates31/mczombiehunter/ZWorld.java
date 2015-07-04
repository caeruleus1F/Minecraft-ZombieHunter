package com.gbates31.mczombiehunter;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Created by Desktop on 11/4/2014.
 */
public class ZWorld extends Thread {

    Tile [][] world;
    World mcworld;
    int size;
    int ticks;
    int hunterSingleKills = 0;
    int hunterDoubleKills = 0;
    int hunterTotalKills = 0;
    int zombieVictimsBitten = 0;
    int zombieHuntersBitten = 0;
    int zombieTotalBites = 0;
    int zombieStumbleUnits = 0;
    int victimFleeUnits = 0;
    int hunterSeekUnits = 0;
    int startingZombies = 0;
    int startingHunters = 0;
    int startingVictims = 0;
    final Material ZOMBIE = Material.REDSTONE_BLOCK;
    final Material HUNTER = Material.EMERALD_BLOCK;
    final Material VICTIM = Material.GOLD_BLOCK;
    final Material EMPTY = Material.QUARTZ_BLOCK;

    public ZWorld (World mcworld) {
    	this.mcworld = mcworld;
        final int DIMENSION = 50;
        this.size = DIMENSION;
        world = new Tile[DIMENSION][DIMENSION];

        for (int i = 0; i < DIMENSION; ++i) {
            for (int j = 0; j < DIMENSION; ++j) {
                world[i][j] = new Tile();
            }
        }
        
        Main.c("Default world size of 50^2 created.");
        spawnEntities();
    }

    public ZWorld (int size) {
    	
        world = new Tile[size][size];
        this.size = size;

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                world[i][j] = new Tile();
            }
        }
        
        Main.c("World of size " + size + "^2 created.");
        spawnEntities();
    }

    public void displayStats() {
        Main.c("Starting zombies: " + startingZombies);
        Main.c("Starting hunters: " + startingHunters);
        Main.c("Starting victims: " + startingVictims);
        Main.c("Zombies killed: " + hunterTotalKills);
        Main.c("Single kills: " + hunterSingleKills);
        Main.c("Double kills: " + hunterDoubleKills);
        Main.c("Victims bitten: " + zombieVictimsBitten);
        Main.c("Hunters bitten: " + zombieHuntersBitten);
        Main.c("Total bites: " + zombieTotalBites);
        Main.c("Zombie moves: " + zombieStumbleUnits);
        Main.c("Hunter moves:" + hunterSeekUnits);
        Main.c("Victim moves: " + victimFleeUnits);
    }

    public void spawnEntities () {
    	
    	startingZombies = 50;
    	startingHunters = 20;
    	startingVictims = 30;
    	ticks = 1000;
    	
    	placeZombies(startingZombies);
    	placeHunters(startingHunters);
    	placeVictims(startingVictims);
    }

    public void placeZombies(int zombieQty) {
        for (int i = 0; i < zombieQty; ++i) {
            int x = (int) (Math.random() * this.size);
            int z = (int) (Math.random() * this.size);

            while (world[z][x].isOccupied == true) {
                x = (int) (Math.random() * this.size);
                z = (int) (Math.random() * this.size);
            }

            world[z][x].hasZombie = true;
            world[z][x].isOccupied = true;
            
            mcworld.getBlockAt(x - 25, 62, z - 25).setType(ZOMBIE);
        }
    }

    public void placeHunters(int hunterQty) {
        for (int i = 0; i < hunterQty; ++i) {
            int x = (int) (Math.random() * this.size);
            int z = (int) (Math.random() * this.size);

            while (world[z][x].isOccupied == true) {
                x = (int) (Math.random() * this.size);
                z = (int) (Math.random() * this.size);
            }

            world[z][x].hasHunter = true;
            world[z][x].isOccupied = true;
            mcworld.getBlockAt(x - 25, 62, z - 25).setType(HUNTER);
        }
    }

    public void placeVictims(int victimQty) {
        for (int i = 0; i < victimQty; ++i) {
            int x = (int) (Math.random() * this.size);
            int z = (int) (Math.random() * this.size);

            while (world[z][x].isOccupied == true) {
                x = (int) (Math.random() * this.size);
                z = (int) (Math.random() * this.size);
            }

            world[z][x].hasVictim = true;
            world[z][x].isOccupied = true;
            mcworld.getBlockAt(x - 25, 62, z - 25).setType(VICTIM);
        }
    }

    public void tick() {
        huntersCheckSurroundings();
        zombiesCheckSurroundings();
        victimsCheckSurroundings();

        huntersMove();
        zombiesMove();
        
        updateMCWorld();
        
        clearMoveAttempts();
    }

    public void huntersCheckSurroundings() {
        for (int z = 0; z < size; ++z) {
            for (int x = 0; x < size; ++x) {
                if (world[z][x].hasHunter) {
                    int turnKillCount = 0;

                    // check top
                    if (z - 1 != -1) {
                        if (world[z - 1][x].hasZombie) {
                            turnKillCount = killZombie(z - 1, x, turnKillCount);
                        }
                    }

                    // check right
                    if (x + 1 != size && turnKillCount != 2) {
                        if (world[z][x + 1].hasZombie) {
                            turnKillCount = killZombie(z, x + 1, turnKillCount);
                        }
                    }
                    
                    // check left
                    if (x - 1 != -1 && turnKillCount != 2) {
                        if (world[z][x - 1].hasZombie) {
                            turnKillCount = killZombie(z, x - 1, turnKillCount);
                        }
                    }

                    // check bottom
                    if (z + 1 != size && turnKillCount != 2) {
                        if (world[z + 1][x].hasZombie) {
                            turnKillCount = killZombie(z + 1, x, turnKillCount);
                        }
                    }

                    //check top left
                    if (x - 1 != -1 && z - 1 != -1) {
                        if (world[z - 1][x - 1].hasZombie) {
                            turnKillCount = killZombie(z - 1, x - 1, turnKillCount);
                        }
                    }

                    // check top right
                    if (x + 1 != size && z - 1 != -1 && turnKillCount != 2) {
                        if (world[z - 1][x + 1].hasZombie) {
                            turnKillCount = killZombie(z - 1, x + 1, turnKillCount);
                        }
                    }

                    // check bottom left
                    if (x - 1 != -1 && z + 1 != size && turnKillCount != 2) {
                        if (world[z + 1][x - 1].hasZombie) {
                            turnKillCount = killZombie(z + 1, x - 1, turnKillCount);
                        }
                    }

                    // check bottom right
                    if (x + 1 != size && z + 1 != size && turnKillCount != 2) {
                        if (world[z + 1][x + 1].hasZombie) {
                            turnKillCount = killZombie(z + 1, x + 1, turnKillCount);
                        }
                    }

                    if (turnKillCount > 0) {
                        updateHunterKillStats(turnKillCount);
                    }
                } // end of if
            }
        } // end of for
    }

    public int killZombie(int z, int x, int turnKillCount) {
        world[z][x].hasZombie = false;
        world[z][x].isOccupied = false;
        
        ++hunterTotalKills;
        Main.c("Zombie killed!");
        return ++turnKillCount;
    }

    public void updateHunterKillStats(int turnKillCount) {
        if (turnKillCount == 1) {
            ++hunterSingleKills;
        }
        else if (turnKillCount == 2) {
            ++hunterDoubleKills;
        }
    }

    public void zombiesCheckSurroundings() {
        for (int z = 0; z < size; ++z) {
            for (int x = 0; x < size; ++x) {
                if (world[z][x].hasZombie) {
                    // check top
                    if (z - 1 != -1) {
                        if (world[z - 1][x].hasHunter || world[z - 1][x].hasVictim) {
                            bite(z - 1, x);
                        }
                    }

                    // check left
                    if (x - 1 != -1) {
                        if (world[z][x - 1].hasHunter || world[z][x - 1].hasVictim) {
                            bite(z, x - 1);
                        }
                    }

                    // check right
                    if (x + 1 != size) {
                        if (world[z][x + 1].hasHunter || world[z][x + 1].hasVictim) {
                            bite(z, x + 1);
                        }
                    }

                    // check bottom
                    if (z + 1 != size) {
                        if (world[z + 1][x].hasHunter || world[z + 1][x].hasVictim) {
                            bite(z + 1, x);
                        }
                    }
                } // end of if
            }
        } // end of for
    }

    public void bite(int z, int x) {

        if (world[z][x].hasHunter) {
            ++zombieHuntersBitten;
            Main.c("Hunter bitten!");
            world[z][x].hasHunter = false;

        }
        else if (world[z][x].hasVictim) {
            ++zombieVictimsBitten;
            Main.c("Victim bitten.");
            world[z][x].hasVictim = false;
        }

        ++zombieTotalBites;
        world[z][x].hasZombie = true;
        
        // change block at location from whatever to zombie
        
        //
        
    }

    public void victimsCheckSurroundings() {
        for (int z = 0; z < size; ++z) {
            for (int x = 0; x < size; ++x) {
                if (world[z][x].hasVictim) {
                    boolean panic = false;

                    // check top
                    if (z - 1 != -1) {
                        if (world[z - 1][x].hasZombie) {
                            panic = true;
                        }
                    }

                    // check right
                    if (x + 1 != size) {
                        if (world[z][x + 1].hasZombie) {
                            panic = true;
                        }
                    }

                    // check left
                    if (x - 1 != -1) {
                        if (world[z][x - 1].hasZombie) {
                            panic = true;
                        }
                    }

                    // check bottom
                    if (z + 1 != size) {
                        if (world[z + 1][x].hasZombie) {
                            panic = true;
                        }
                    }

                    //check top left
                    if (x - 1 != -1 && z - 1 != -1) {
                        if (world[z - 1][x - 1].hasZombie) {
                            panic = true;
                        }
                    }

                    // check top right
                    if (x + 1 != size && z - 1 != -1) {
                        if (world[z - 1][x + 1].hasZombie) {
                            panic = true;
                        }
                    }

                    // check bottom left
                    if (x - 1 != -1 && z + 1 != size) {
                        if (world[z + 1][x - 1].hasZombie) {
                            panic = true;
                        }
                    }

                    // check bottom right
                    if (x + 1 != size && z + 1 != size) {
                        if (world[z + 1][x + 1].hasZombie) {
                            panic = true;
                        }
                    }

                    if (panic) {
                        victimMove(z, x);
                    }
                } // end of if
            }
        } // end of for
    }

    public void huntersMove () {
        for (int z = 0; z < size; ++z) {
            for (int x = 0; x < size; ++x) {
                if (world[z][x].hasHunter && !world[z][x].moveAttempted) {
                    boolean validMoveAttempt = false;

                    while (validMoveAttempt == false) {
                        int randomDirection = (int) (Math.random() * 8);

                        switch (randomDirection) {

                            case 0: // top-left
                                if (z - 1 != -1 && x - 1 != -1) {
                                    validMoveAttempt = true;
                                    if (!world[z - 1][x - 1].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z - 1][x - 1].hasHunter = true;
                                        world[z - 1][x - 1].isOccupied = true;
                                        world[z - 1][x - 1].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            case 1: // top
                                if (z - 1 != -1) {
                                    validMoveAttempt = true;
                                    if (!world[z - 1][x].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z - 1][x].hasHunter = true;
                                        world[z - 1][x].isOccupied = true;
                                        world[z - 1][x].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            case 2: // top-right
                                if (x + 1 != size && z - 1 != -1) {
                                    validMoveAttempt = true;
                                    if (!world[z - 1][x + 1].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z - 1][x + 1].hasHunter = true;
                                        world[z - 1][x + 1].isOccupied = true;
                                        world[z - 1][x + 1].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            case 3: // left
                                if (x - 1 != -1) {
                                    validMoveAttempt = true;
                                    if (!world[z][x - 1].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z][x - 1].hasHunter = true;
                                        world[z][x - 1].isOccupied = true;
                                        world[z][x - 1].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            case 4: // right
                                if (x + 1 != size) {
                                    validMoveAttempt = true;
                                    if (!world[z][x + 1].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z][x + 1].hasHunter = true;
                                        world[z][x + 1].isOccupied = true;
                                        world[z][x + 1].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            case 5: // bottom-left
                                if (x - 1 != -1 && z + 1 != size) {
                                    validMoveAttempt = true;
                                    if (!world[z + 1][x - 1].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z + 1][x - 1].hasHunter = true;
                                        world[z + 1][x - 1].isOccupied = true;
                                        world[z + 1][x - 1].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            case 6: // bottom
                                if (z + 1 != size) {
                                    validMoveAttempt = true;
                                    if (!world[z + 1][x].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z + 1][x].hasHunter = true;
                                        world[z + 1][x].isOccupied = true;
                                        world[z + 1][x].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            case 7: // bottom-right
                                if (x + 1 != size && z + 1 != size) {
                                    validMoveAttempt = true;
                                    if (!world[z + 1][x + 1].isOccupied) {
                                        world[z][x].hasHunter = false;
                                        world[z][x].isOccupied = false;
                                        world[z + 1][x + 1].hasHunter = true;
                                        world[z + 1][x + 1].isOccupied = true;
                                        world[z + 1][x + 1].moveAttempted = true;
                                        ++hunterSeekUnits;
                                    }
                                }
                                break;

                            default: Main.c("Not sure how hunter triggered default.");
                        }
                    } // end of while
                }
            }
        } // end of for loops
    }

    public void zombiesMove () {
        for (int z = 0; z < size; ++z) {
            for (int x = 0; x < size; ++x) {
                if (world[z][x].hasZombie && !world[z][x].moveAttempted) {
                    boolean validMoveAttempt = false;

                    while (validMoveAttempt == false) {
                        int randomDirection = (int) (Math.random() * 4);

                        switch (randomDirection) {

                            case 0: // top
                                if (z - 1 != -1) {
                                    validMoveAttempt = true;
                                    if (!world[z - 1][x].isOccupied) {
                                        world[z][x].hasZombie = false;
                                        world[z][x].isOccupied = false;
                                        world[z - 1][x].hasZombie = true;
                                        world[z - 1][x].isOccupied = true;
                                        world[z - 1][x].moveAttempted = true;
                                        ++zombieStumbleUnits;
                                    }
                                }
                                break;

                            case 1: // left
                                if (x - 1 != -1) {
                                    validMoveAttempt = true;
                                    if (!world[z][x - 1].isOccupied) {
                                        world[z][x].hasZombie = false;
                                        world[z][x].isOccupied = false;
                                        world[z][x - 1].hasZombie = true;
                                        world[z][x - 1].isOccupied = true;
                                        world[z][x - 1].moveAttempted = true;
                                        ++zombieStumbleUnits;
                                    }
                                }
                                break;

                            case 2: // right
                                if (x + 1 != size) {
                                    validMoveAttempt = true;
                                    if (!world[z][x + 1].isOccupied) {
                                        world[z][x].hasZombie = false;
                                        world[z][x].isOccupied = false;
                                        world[z][x + 1].hasZombie = true;
                                        world[z][x + 1].isOccupied = true;
                                        world[z][x + 1].moveAttempted = true;
                                        ++zombieStumbleUnits;
                                    }
                                }
                                break;

                            case 3: // bottom
                                if (z + 1 != size) {
                                    validMoveAttempt = true;
                                    if (!world[z + 1][x].isOccupied) {
                                        world[z][x].hasZombie = false;
                                        world[z][x].isOccupied = false;
                                        world[z + 1][x].hasZombie = true;
                                        world[z + 1][x].isOccupied = true;
                                        world[z + 1][x].moveAttempted = true;
                                        ++zombieStumbleUnits;
                                    }
                                }
                                break;

                            default: Main.c("Not sure how zombie triggered default.");
                        }
                    } // end of while
                }
            }
        } // end of for loops
    }

    public void victimMove (int z, int x) {
        boolean validMoveAttempt = false;

        while (validMoveAttempt == false && !world[z][x].moveAttempted) {
            int randomDirection = (int) (Math.random() * 8);

            switch (randomDirection) {

                case 0: // top-left
                    if (z - 1 != -1 && x - 1 != -1) {
                        validMoveAttempt = true;
                        if (!world[z - 1][x - 1].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z - 1][x - 1].hasVictim = true;
                            world[z - 1][x - 1].isOccupied = true;
                            world[z - 1][x - 1].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                case 1: // top
                    if (z - 1 != -1) {
                        validMoveAttempt = true;
                        if (!world[z - 1][x].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z - 1][x].hasVictim = true;
                            world[z - 1][x].isOccupied = true;
                            world[z - 1][x].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                case 2: // top-right
                    if (x + 1 != size && z - 1 != -1) {
                        validMoveAttempt = true;
                        if (!world[z - 1][x + 1].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z - 1][x + 1].hasVictim = true;
                            world[z - 1][x + 1].isOccupied = true;
                            world[z - 1][x + 1].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                case 3: // left
                    if (x - 1 != -1) {
                        validMoveAttempt = true;
                        if (!world[z][x - 1].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z][x - 1].hasVictim = true;
                            world[z][x - 1].isOccupied = true;
                            world[z][x - 1].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                case 4: // right
                    if (x + 1 != size) {
                        validMoveAttempt = true;
                        if (!world[z][x + 1].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z][x + 1].hasVictim = true;
                            world[z][x + 1].isOccupied = true;
                            world[z][x + 1].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                case 5: // bottom-left
                    if (x - 1 != -1 && z + 1 != size) {
                        validMoveAttempt = true;
                        if (!world[z + 1][x - 1].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z + 1][x - 1].hasVictim = true;
                            world[z + 1][x - 1].isOccupied = true;
                            world[z + 1][x - 1].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                case 6: // bottom
                    if (z + 1 != size) {
                        validMoveAttempt = true;
                        if (!world[z + 1][x].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z + 1][x].hasVictim = true;
                            world[z + 1][x].isOccupied = true;
                            world[z + 1][x].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                case 7: // bottom-right
                    if (x + 1 != size && z + 1 != size) {
                        validMoveAttempt = true;
                        if (!world[z + 1][x + 1].isOccupied) {
                            world[z][x].hasVictim = false;
                            world[z][x].isOccupied = false;
                            world[z + 1][x + 1].hasVictim = true;
                            world[z + 1][x + 1].isOccupied = true;
                            world[z + 1][x + 1].moveAttempted = true;
                            ++victimFleeUnits;
                        }
                    }
                    break;

                default: Main.c("Not sure how victim triggered default.");
            }
        }
    } // end victimMove()
    
    private void updateMCWorld() {
    	
    	for (int z = 0; z < size; ++z) {
    		for (int x = 0; x < size; ++x) {
    			
    			final Block blockAt = mcworld.getBlockAt(x - 25, 62, z - 25);
    			if (blockAt.getType() != Material.AIR) {
        			blockAt.setType(Material.AIR);
    			}
    			
    		}
    	}
    	
    	for (int z = 0; z < size; ++z) {
    		for (int x = 0; x < size; ++x) {
    			
    			if (world[z][x].isOccupied) {
        			final Block blockAt = mcworld.getBlockAt(x - 25, 62, z - 25);
    				
    				if (world[z][x].hasZombie) {
            			blockAt.setType(ZOMBIE);
    				}
    				else if (world[z][x].hasHunter) {
            			blockAt.setType(HUNTER);
    				}
    				else if (world[z][x].hasVictim) {
            			blockAt.setType(VICTIM);
    				}
    			}
    			
    		}
    	}
    } // end of updateMCWorld()

    private void clearMoveAttempts() {
    	for (int z = 0; z < size; ++z) {
    		for (int x = 0; x < size; ++x) {
    			world[z][x].moveAttempted = false;    			
    		}
    	}
    }
}
