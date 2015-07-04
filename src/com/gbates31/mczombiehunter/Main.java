package com.gbates31.mczombiehunter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	ZWorld zworld;
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents (new TickTrigger(), this);
	}
	
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args) {
		boolean successfulExecution = true;
		try {
			
			if (args[0].equalsIgnoreCase("prep")) {
				// prep the world
				clearAllBlocks();
				createStoneBase();
				zworld = new ZWorld(Bukkit.getServer().getWorlds().get(0));
				c("Preparation Finished.");
			}
			else if (args[0].equalsIgnoreCase("tick")) {
				// do a world tick
				zworld.tick();
			}
			else if (args[0].equalsIgnoreCase("stats")) {
				// display stats
				zworld.displayStats();
			}
			
		} catch (Exception ex) {
			successfulExecution = false;
		}
		
		return successfulExecution;
	}
	
	private void clearAllBlocks() {
	
		final Material AIR = Material.AIR;
		final Material LAVA = Material.LAVA;
		final Material WATER = Material.WATER;
		final Material STATIONARY_WATER = Material.STATIONARY_WATER;
		final Material STATIONARY_LAVA = Material.STATIONARY_LAVA;
		final World mcworld = (World) Bukkit.getServer().getWorlds().get(0);
		final int WORLD_DIMENSION = 25;
		final int WORLD_HEIGHT = 256;
		final int base_x = -WORLD_DIMENSION;
		final int base_y = 1;
		final int base_z = -WORLD_DIMENSION;
		final int WORLD_SIZE = WORLD_DIMENSION * 2;
		final int OFFSET = 7;

		// remove all water and lava from the world that can get
		// on the board.
		for (int i = base_x - OFFSET; i < WORLD_SIZE + OFFSET * 2; ++i) {
			for (int j = base_y; j < WORLD_HEIGHT; ++j) {
				for (int k =  base_z - OFFSET; k < WORLD_SIZE + OFFSET * 2; ++k) {
					Block blockAt = mcworld.getBlockAt(i, j, k);
					if (blockAt.getType() == AIR) {
						
					}
					else if (blockAt.getType() == LAVA || blockAt.getType() == WATER || 
							blockAt.getType() == STATIONARY_LAVA || blockAt.getType() == STATIONARY_WATER ||
							blockAt.getType() == Material.REDSTONE_BLOCK){
						blockAt.setType(AIR);
					}
				}
			}
		} // end of nested for loops
		
		// clear the remaining blocks from the board
		for (int i = 0; i < WORLD_SIZE; ++i) {
			for (int j = 0; j < WORLD_HEIGHT; ++j) {
				for (int k = 0; k < WORLD_SIZE; ++k) {
					final Block blockAt = mcworld.getBlockAt(base_x + i, base_y + j, base_z + k);
					if (blockAt.getType() == AIR) {
						
					}
					else {
						blockAt.setType(AIR);
					}
				}
			}
		} // end of nested for loops
		c("All blocks cleared.");
	}
	
	private void createStoneBase() {

		World mcworld = (World) Bukkit.getServer().getWorlds().get(0);
		final int WORLD_DIMENSION = 25;
		final int WORLD_HEIGHT = 62;
		final int base_x = -WORLD_DIMENSION;
		final int base_y = 61;
		final int base_z = -WORLD_DIMENSION;
		final int WORLD_SIZE = WORLD_DIMENSION * 2;
		
		for (int i = base_x; i < WORLD_SIZE; ++i) {
			for (int j = base_y; j < WORLD_HEIGHT; ++j) {
				for (int k =  base_z; k < WORLD_SIZE; ++k) {
					mcworld.getBlockAt(i, j, k).setType(Material.STONE);
				}
			}
		}
		c("Stone base complete.");
	}
	
	public static void c(String text) {
		Bukkit.getLogger().info(text);
	}

	/*
	private void countOres(CommandSender sender) {
		
		if ((sender instanceof Player && sender.getName().equalsIgnoreCase("gbates31")) || !(sender instanceof Player)) {
			final int WORLD_DIMENSION = 25;
			final int WORLD_HEIGHT = 256;
			final Material air = Material.AIR;
			final int base_x = -WORLD_DIMENSION;
			final int base_y = 0;
			final int base_z = -WORLD_DIMENSION;
			final int WORLD_SIZE = WORLD_DIMENSION * 2;
			final World mcworld = (World) Bukkit.getServer().getWorlds().get(0);
			
			int diamonds = 0;
			int coal = 0;
			int iron = 0;
			int gold = 0;
			int lapis = 0;
			int redstone = 0;
			int emerald = 0;
			int total = 0;
			
			for (int i = 0; i < WORLD_SIZE; ++i) {
				for (int j = 0; j < WORLD_HEIGHT; ++j) {
					for (int k = 0; k < WORLD_SIZE; ++k) {
						Block blockAt = mcworld.getBlockAt(base_x + i, base_y + j, base_z + k);
						if (blockAt.getType() == air) {
							
						}
						else if (blockAt.getType() == Material.STONE || blockAt.getType() == Material.DIRT) {
							//blockAt.setType(air);
						}
						else if (blockAt.getType() == Material.DIAMOND_ORE) {
							//blockAt.setType(air);
							++diamonds;
						}
						else if (blockAt.getType() == Material.COAL_ORE) {
							//blockAt.setType(air);
							++coal;
						}
						else if (blockAt.getType() == Material.IRON_ORE) {
							//blockAt.setType(air);
							++iron;
						}
						else if (blockAt.getType() == Material.GOLD_ORE) {
							//blockAt.setType(air);
							++gold;
						}
						else if (blockAt.getType() == Material.LAPIS_ORE) {
							//blockAt.setType(air);
							++lapis;
						}
						else if (blockAt.getType() == Material.REDSTONE_ORE) {
							//blockAt.setType(air);
							++redstone;
						}
						else if (blockAt.getType() == Material.EMERALD_ORE) {
							//blockAt.setType(air);
							++emerald;
						}
					}
				}
			}
			
			total = diamonds + coal + iron + gold + lapis + redstone + emerald;
			c("Complete.");
			c("Diamond Ore: " + diamonds + "(" + String.valueOf((double) diamonds * 100 / total) + "%)");
			c("Coal Ore: " + coal + "(" + ((double) coal * 100 / total) + "%)");
			c("Iron Ore: " + iron + "(" + ((double) iron * 100 / total) + "%)");
			c("Gold Ore: " + gold + "(" + ((double) gold * 100 / total) + "%)");
			c("Lapis Ore: " + lapis + "(" + ((double) lapis * 100 / total) + "%)");
			c("Redstone Ore: " + redstone + "(" + ((double) redstone * 100 / total) + "%)");
			c("Emerald Ore: " + emerald + "(" + ((double) emerald * 100 / total) + "%)");
			c("Total Ores: " + total);
			//gbates31.sendMessage("Diamonds: " + diamonds);
			//gbates31.getInventory().addItem(new ItemStack(Material.DIAMOND, diamonds));
			
		}
	} */
}
