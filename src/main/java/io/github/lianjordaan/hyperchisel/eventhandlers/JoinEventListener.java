package io.github.lianjordaan.hyperchisel.eventhandlers;

import io.github.lianjordaan.hyperchisel.HyperChisel;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Check if the player is in the target world
        if (player.getWorld().equals(HyperChisel.worldName)) {
            // Set the player's game mode to creative
            player.setGameMode(GameMode.CREATIVE);
        }
    }
}