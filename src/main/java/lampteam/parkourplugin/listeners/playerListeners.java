package lampteam.parkourplugin.listeners;

import lampteam.parkourplugin.ParkourPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

@SuppressWarnings("DataFlowIssue")
public class playerListeners implements Listener {

    // Проверка на падение
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        // Если игрок не в паркуре
        Player player = event.getPlayer();
        if (player.getMetadata("parkour_min_y").get(0).asInt() < 0){
            return;
        }

        if (player.getLocation().getBlockY() < player.getMetadata("parkour_min_y").get(0).asInt()){
            ParkourPlugin.getPlugin().getArena(player.getMetadata("parkour_arena_id").get(0).asString()).returnToLastCheckpoint(player);
        }
    }

    // При входе на севрер
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.setMetadata("parkour_min_y", new FixedMetadataValue(ParkourPlugin.getPlugin(), -1));
        player.setMetadata("parkour_arena_id", new FixedMetadataValue(ParkourPlugin.getPlugin(), null));
    }

    // При выходе с сервера
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (ParkourPlugin.getPlugin().getArena(player.getMetadata("parkour_arena_id").get(0).asString()) == null){
            return;
        }
        ParkourPlugin.getPlugin().getArena(player.getMetadata("parkour_arena_id").get(0).asString()).leave(player);
    }

}
