package lampteam.parkourplugin;

import lampteam.parkourplugin.commands.ParkourCommands;
import lampteam.parkourplugin.listeners.playerListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"DataFlowIssue", "ResultOfMethodCallIgnored", "unchecked"})
public final class ParkourPlugin extends JavaPlugin {

    List<Arena> arenas = new ArrayList<>();
    private static ParkourPlugin plugin;

    public static ParkourPlugin getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        Bukkit.getServer().getPluginManager().registerEvents(new playerListeners(), this);
        getCommand("parkourse").setExecutor(new ParkourCommands());

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        loadArenas();

    }

    public Arena getArena(String id){
        for (Arena arena : arenas){
            if (!arena.getId().equals(id)){
                continue;
            }
            return arena;
        }
        return null;
    }

    public void loadArenas(){

        File arenasFolder = new File(ParkourPlugin.getPlugin().getDataFolder() + "/Arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdir();
        }
        File[] arenasFiles = arenasFolder.listFiles();

        assert arenasFiles != null;
        for (File file : arenasFiles) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            Arena arena = new Arena();

            String id = config.getString("id");
            String name = config.getString("displayName");
            ChatColor nameColor = toColor(config.getString("nameColor"));
            String difficulty = config.getString("difficulty");
            ChatColor difficultyColor = toColor(config.getString("difficultyColor"));
            int minY = config.getInt("minY");
            Location lobbyLocation = config.getLocation("lobbyLocation");
            List<Location> checkpoints = (List<Location>) config.getList("checkpoints");

            arena.init(id, name, nameColor, difficulty, difficultyColor, minY, lobbyLocation, checkpoints);
            arenas.add(arena);
        }
    }

    public ChatColor toColor(String input){
        switch (input) {
            case "black":
                return ChatColor.BLACK;
            case "dark blue":
                return ChatColor.DARK_BLUE;
            case "dark green":
                return ChatColor.DARK_GREEN;
            case "aqua":
                return ChatColor.AQUA;
            case "dark red":
                return ChatColor.DARK_RED;
            case "dark purple":
                return ChatColor.DARK_PURPLE;
            case "gold":
                return ChatColor.GOLD;
            case "gray":
                return ChatColor.GRAY;
            case "dark gray":
                return ChatColor.DARK_GRAY;
            case "blue":
                return ChatColor.BLUE;
            case "green":
                return ChatColor.GREEN;
            case "dark aqua":
                return ChatColor.DARK_AQUA;
            case "red":
                return ChatColor.RED;
            case "light purple":
                return ChatColor.LIGHT_PURPLE;
            case "yellow":
                return ChatColor.YELLOW;
            case "white":
                return ChatColor.WHITE;
            case "bold":
                return ChatColor.BOLD;
            default:
                return ChatColor.WHITE;
        }
    }

    public void reload(){
        if (arenas.size() > 0){
            for (Arena arena : arenas){
                arena.stop();
            }
            arenas.clear();
        }
        loadArenas();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
