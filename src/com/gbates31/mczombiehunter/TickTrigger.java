package com.gbates31.mczombiehunter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;

public class TickTrigger implements Listener{
	
	@EventHandler
	public void onSprint(PlayerAnimationEvent evt) {
		Player player = evt.getPlayer();
		if (player.getName().equalsIgnoreCase("gbates31")) {
			if (evt.getAnimationType() == PlayerAnimationType.ARM_SWING) {
				player.chat("/zh tick");
			}
		}
	}
}
