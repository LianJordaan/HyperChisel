package io.github.lianjordaan.hyperchisel;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DevCommandExecutor implements CommandExecutor {
    private final Map<UUID, BukkitTask> confirmationTasks = new HashMap<>();
    private final Plugin plugin;

    public DevCommandExecutor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has the 'HyperChisel.dev' permission
        if (!player.hasPermission("HyperChisel.dev")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Get the desired world name from the configuration
        String worldName = HyperChisel.getPlugin(HyperChisel.class).getConfig().getString("worldName");

        if (args.length == 0) {
            // '/dev' command without any subcommand
            showHelpMessage(player);
            return true;
        }

        // Process subcommands
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "tp":
                // '/dev tp' command
                teleportToDevWorld(player, worldName);
                break;
            case "resetworld":
                // '/dev resetworld' command
                resetDevWorld(player, worldName);
                break;
            case "confirm":
                // '/dev confirm' command
                confirmResetWorld(player, worldName);
                break;
            case "help":
                // '/dev help' command
                showHelpMessage(player);
                break;
            default:
                // Invalid subcommand
                player.sendMessage(ChatColor.RED + "Invalid subcommand. Use '/dev help' for assistance.");
                break;
        }

        return true;
    }

    private void teleportToDevWorld(Player player, String worldName) {
        // Check if the world already exists
        World world = Bukkit.getWorld(worldName);
        plugin.getLogger().log(Level.INFO, worldName + "|" + Bukkit.getWorlds());
        if (world == null) {
            // Create the world with custom terrain generation
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.generator(new CustomTerrainGenerator());
            world = worldCreator.createWorld();

            // Set custom gamerules for the world
            assert world != null;
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            generateCodeLines();
        }

        // Teleport the player to the world's spawn location
        world.setSpawnLocation(0, -63, 0);
        Location spawnLocation = world.getSpawnLocation();
        world = Bukkit.getWorld(worldName);
        assert world != null;
        world.setTime(6000);
        spawnLocation.setPitch(0);
        spawnLocation.setYaw(-90);
        spawnLocation.setX(0.5);
        spawnLocation.setZ(0.5);
        player.teleport(spawnLocation);

        // Set the player's game mode to creative
        player.setGameMode(GameMode.CREATIVE);
    }

    private void resetDevWorld(Player player, String worldName) {
        // Check if the world exists
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            // Start the confirmation timer
            BukkitTask confirmationTask = new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.RED + "Reset action canceled. Confirmation timeout reached.");
                    confirmationTasks.remove(player.getUniqueId());
                }
            }.runTaskLater(HyperChisel.getPlugin(HyperChisel.class), 20 * 30); // 30 seconds

            confirmationTasks.put(player.getUniqueId(), confirmationTask);
            player.sendMessage(ChatColor.YELLOW + "Are you sure you want to reset the development world?");
            player.sendMessage(ChatColor.YELLOW + "Type '/dev confirm' to proceed or do nothing to cancel.");
        } else {
            player.sendMessage(ChatColor.RED + "The development world doesn't exist.");
        }
    }

    private void confirmResetWorld(Player player, String worldName) {
        // Check if the player has a pending confirmation task
        UUID playerUUID = player.getUniqueId();
        if (confirmationTasks.containsKey(playerUUID)) {
            // Cancel the confirmation task
            confirmationTasks.get(playerUUID).cancel();
            confirmationTasks.remove(playerUUID);

            // Reset the development world
            // Check if the world exists
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                // Teleport the player to the Overworld
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

                // Delete the world
                Bukkit.unloadWorld(world, false);
                File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                try {
                    FileUtils.deleteDirectory(worldFolder);
                } catch (IOException e) {
                    player.sendMessage(ChatColor.RED + "Failed to reset the development world: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                plugin.getLogger().info("Development world deleted: " + worldName);
            } else {
                player.sendMessage(ChatColor.RED + "The development world doesn't exist.");
                return;
            }

            // Create the world again
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.generator(new CustomTerrainGenerator());
            World newDevWorld = worldCreator.createWorld();
            generateCodeLines();

            // Teleport the player to the development world
            assert newDevWorld != null;
            newDevWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            newDevWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            newDevWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            newDevWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            Location spawnLocation = newDevWorld.getSpawnLocation();
            spawnLocation.setPitch(0);
            spawnLocation.setYaw(-90);
            spawnLocation.setX(0.5);
            spawnLocation.setZ(0.5);
            player.teleport(spawnLocation);

            player.sendMessage(ChatColor.GREEN + "Development world reset successfully.");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a pending reset confirmation.");
        }
    }


    private void showHelpMessage(Player player) {
        // Display the help message
        player.sendMessage(ChatColor.YELLOW + "=== " + ChatColor.GOLD + "HyperChisel Help" + ChatColor.YELLOW + " ===");
        player.sendMessage(ChatColor.GREEN + "/dev tp" + ChatColor.WHITE + " - Teleport to the development world.");
        player.sendMessage(ChatColor.GREEN + "/dev resetworld" + ChatColor.WHITE + " - Reset the development world.");
        player.sendMessage(ChatColor.GREEN + "/dev confirm" + ChatColor.WHITE + " - Confirm the reset of the development world.");
        player.sendMessage(ChatColor.GREEN + "/dev help" + ChatColor.WHITE + " - Display this help message.");
    }

    private void generateCodeLines() {
        if (HyperChisel.worldName == null) {
            plugin.getLogger().warning("Target world is not set. Cannot generate code lines.");
            return;
        }

        Location startLocation = new Location(Bukkit.getWorld(HyperChisel.worldName), 5, -64, 0);

        for (int line = 0; line < HyperChisel.stackedLines; line++) {
            Location lineStart = startLocation.clone().add(0, line * 5, 0);

            for (int i = 0; i < HyperChisel.codeLineLength; i++) {
                for (int ii = 0; ii < HyperChisel.numberOfLines; ii++) {
                    Location location = lineStart.clone().add(ii * 3, 0, i);
                    Block block = location.getBlock();
                    block.setType(Material.GLASS);
                }
            }
        }
    }
}
