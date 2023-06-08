package io.github.lianjordaan.hyperchisel;

import io.github.lianjordaan.hyperchisel.eventhandlers.BlockBreakEventListener;
import io.github.lianjordaan.hyperchisel.eventhandlers.BlockPlaceEventListener;
import io.github.lianjordaan.hyperchisel.eventhandlers.JoinEventListener;
import io.github.lianjordaan.hyperchisel.eventhandlers.RightClickListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HyperChisel extends JavaPlugin implements CommandExecutor {
    public static String worldName;
    public static int codeLineLength;
    public static int numberOfLines;
    public static int stackedLines;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("dev").setExecutor(new DevCommandExecutor(this));

        // Load the configuration
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Get the values from the configuration
        codeLineLength = getConfig().getInt("codeLineLength");
        numberOfLines = getConfig().getInt("numberOfLines");
        stackedLines = getConfig().getInt("stackedLines");
        worldName = getConfig().getString("worldName");

        getServer().getPluginManager().registerEvents(new BlockBreakEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceEventListener(), this);
        getServer().getPluginManager().registerEvents(new JoinEventListener(), this);
        getServer().getPluginManager().registerEvents(new RightClickListener(), this);
        WorldCreator worldCreator = new WorldCreator(worldName);
        World world = Bukkit.createWorld(worldCreator);

        if (world == null){
            getLogger().log(Level.SEVERE, "Failed to load the world");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (Bukkit.getWorld(worldName) != null) {
            Bukkit.getWorld(worldName).save();
        }
    }
}
