package steyn91.parkourplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import steyn91.parkourplugin.commands.AdminCommands;
import steyn91.parkourplugin.commands.ParkourCommands;
import steyn91.parkourplugin.listeners.PlayerListeners;
import steyn91.parkourplugin.models.Arena;
import steyn91.parkourplugin.stats.Database;
import steyn91.parkourplugin.stats.PlaceholderManager;
import steyn91.parkourplugin.stats.StatsManager;
import steyn91.parkourplugin.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ParkourPlugin extends JavaPlugin {

    List<Arena> arenas = new ArrayList<>();
    private static ParkourPlugin plugin;

    public static ParkourPlugin getPlugin(){
        return plugin;
    }
    public List<Arena> getArenas() {
        return arenas;
    }

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        getCommand("parkourse").setExecutor(new ParkourCommands());
        getCommand("padmin").setExecutor(new AdminCommands());
        new PlaceholderManager().register();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Database.initDatabase();
        StatsManager.startSavingCycle();
        loadArenasFromCfg();
    }

    public void loadArenasFromCfg(){
        File arenasFolder = new File(ParkourPlugin.getPlugin().getDataFolder() + "/Arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdir();
        }
        File[] arenasFiles = arenasFolder.listFiles();

        assert arenasFiles != null;
        for (File file : arenasFiles) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            arenas.add(new Arena(config.getString("id"),
                    config.getString("displayName"),
                    Utils.toColor(config.getString("nameColor")),
                    config.getString("difficulty"),
                    Utils.toColor(config.getString("difficultyColor")),
                    config.getInt("minY"),
                    config.getLocation("lobbyLocation"),
                    (List<Location>) config.getList("checkpoints")));
        }
    }

    public void reload(){
        if (arenas.size() > 0){
            for (Arena arena : arenas){
                arena.stop();
            }
            arenas.clear();
        }

        loadArenasFromCfg();
    }

    @Override
    public void onDisable() {
        StatsManager.saveAllToDB();
    }
}
