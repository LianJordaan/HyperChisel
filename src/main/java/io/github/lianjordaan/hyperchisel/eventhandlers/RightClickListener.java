package io.github.lianjordaan.hyperchisel.eventhandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class RightClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check for right-click event
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            // Perform your desired actions here

            // Cancel the event
            event.setCancelled(true);
        }
    }
}
