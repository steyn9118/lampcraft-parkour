package steyn91.parkourplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import steyn91.parkourplugin.models.Arena;
import steyn91.parkourplugin.utils.Utils;

@SuppressWarnings("DataFlowIssue")
public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Arena arena = Utils.getArenaByPlayer(player);
        if (arena == null) return;

        if (player.getLocation().getBlockY() < arena.getMinY()) arena.returnToLastCheckpoint(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Arena arena = Utils.getArenaByPlayer(player);
        if (arena != null) arena.leave(player);
    }
}
