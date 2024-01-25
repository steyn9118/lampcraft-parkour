package steyn91.parkourplugin.commands;

import steyn91.parkourplugin.ParkourPlugin;
import steyn91.parkourplugin.models.Arena;
import steyn91.parkourplugin.stats.StatsManager;
import steyn91.parkourplugin.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminCommands implements CommandExecutor {

    private final ParkourPlugin plugin = ParkourPlugin.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("padmin") && sender.hasPermission("padmin")){
            switch (args.length){
                case (0):
                    sender.sendMessage(Component.text("/padmin reload"));
                    sender.sendMessage(Component.text("/padmin arena <arena-id>"));
                    sender.sendMessage(Component.text("/padmin remove <player-name> <arena-id>"));
                    break;

                case (1):
                    if (args[0].equals("reload")){
                        plugin.reload();
                        return false;
                    }
                    break;

                case (2):
                    if (args[0].equals("arena")){
                        Arena arena = Utils.getArenaById(args[1]);
                        if (arena == null) return false;
                        sender.sendMessage("Игроки на " + args[1]);
                        for (Player player : arena.getPlayers()){
                            sender.sendMessage(Component.text(player.getName()));
                        }
                        return false;
                    }
                    break;

                case (3):
                    if (args[0].equals("remove")){
                        StatsManager.removeStatOnArena(args[1], args[2]);
                        return false;
                    }
                    break;
            }
        }
        return false;
    }
}
