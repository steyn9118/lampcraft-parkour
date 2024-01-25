package steyn91.parkourplugin.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import steyn91.parkourplugin.ParkourPlugin;
import steyn91.parkourplugin.models.Arena;
import steyn91.parkourplugin.stats.PlayerStat;
import steyn91.parkourplugin.stats.StatsManager;
import steyn91.parkourplugin.utils.Utils;

@SuppressWarnings("DataFlowIssue")
public class ParkourCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("parkourse") && commandSender.hasPermission("parkourse")){
            if (args.length == 0){
                commandSender.sendMessage("/parkourse leave <player>");
                commandSender.sendMessage("/parkourse stats <player>");
                commandSender.sendMessage("/parkourse back <player>");
                commandSender.sendMessage("/parkourse restart <player>");
                commandSender.sendMessage("/parkourse join <player> <arena_id>");
                commandSender.sendMessage("/parkourse checkpoint <player> <checkpoint_id>");
                return false;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) return false;

            ParkourPlugin.getPlugin().getLogger().info("hoba");

            switch (args.length){
                case (2):
                    Arena playerArena = Utils.getArenaByPlayer(player);
                    ParkourPlugin.getPlugin().getLogger().info("2");
                    switch (args[0]){
                        case "stats":
                            player.sendMessage(ChatColor.YELLOW + "---- ТВОЯ СТАТИСТИКА ----");
                            for (PlayerStat stat : StatsManager.getAllStatsOfPlayer(player)){
                                Arena arena = Utils.getArenaById(stat.getCourseId());
                                if (arena == null) continue;
                                player.sendMessage(Component.text(arena.getDisplayName() + ChatColor.WHITE + " - " + ChatColor.GREEN + stat.getSeconds() + ChatColor.WHITE + " секунд"));
                            }
                            player.sendMessage(ChatColor.YELLOW + "---- ---- ----- ---- ----");
                            break;

                        case "leave":
                            if (playerArena != null) playerArena.leave(player);
                            break;

                        case "back":
                            if (playerArena != null) playerArena.returnToLastCheckpoint(player);
                            break;

                        case "restart":
                            if (playerArena != null) playerArena.restart(player);
                            break;
                    }
                break;

                case (3):
                    ParkourPlugin.getPlugin().getLogger().info("3");
                    switch (args[0]){
                        case "checkpoint":
                            Arena arena = Utils.getArenaByPlayer(player);
                            if (arena != null) arena.setCheckpoint(player, Integer.parseInt(args[2]));
                            break;

                        case "join":
                            Arena argArena = Utils.getArenaById(args[2]);
                            if (argArena != null) argArena.join(player);
                            break;
                    }
                break;
            }
        }
        return false;
    }
}
