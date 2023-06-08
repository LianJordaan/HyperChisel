package io.github.lianjordaan.hyperchisel.eventhandlers;

import io.github.lianjordaan.hyperchisel.HyperChisel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Check if the player is in the target world
        if (player.getWorld().getName().equals(HyperChisel.worldName)) {
            // Cancel the block break event
            event.setCancelled(true);
        }
    }
}
