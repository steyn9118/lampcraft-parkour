package lampteam.parkourplugin.utils;

import lampteam.parkourplugin.Arena;
import lampteam.parkourplugin.ParkourPlugin;
import lampteam.parkourplugin.PlayerRecord;
import org.bukkit.entity.Player;

public class DebugerTools {

    static ParkourPlugin plugin = ParkourPlugin.getPlugin();

    public static void getAllTops(Player player){

        for (Arena arena : plugin.getArenas()){
            player.sendMessage("Курс " + arena.getId());
            for(PlayerRecord record : arena.getTop()){
                player.sendMessage(record.getPlayerName() + " - " + record.getSeconds() + " * " + record.getDate());
            }
            player.sendMessage("");
        }
    }



}
