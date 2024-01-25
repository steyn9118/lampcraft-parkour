package steyn91.parkourplugin.utils;

import org.bukkit.ChatColor;
import steyn91.parkourplugin.ParkourPlugin;
import steyn91.parkourplugin.models.Arena;
import org.bukkit.entity.Player;

import java.util.List;

public class Utils {

    private static final ParkourPlugin plugin = ParkourPlugin.getPlugin();

    public static List<Arena> getAllCourses(){
        return plugin.getArenas();
    }

    public static Arena getArenaByPlayer(Player player){
        for (Arena arena : plugin.getArenas()){
            if (arena.getPlayers().contains(player)) return arena;
        }
        return null;
    }

    public static Arena getArenaById(String id){
        for (Arena arena : plugin.getArenas()){
            if (arena.getId().equals(id)) return arena;
        }
        return null;
    }

    // Переводчик цвета (для конфигов)
    public static ChatColor toColor(String input){
        if (input == null) return ChatColor.GRAY;
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
}
