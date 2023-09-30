package lampteam.parkourplugin.commands;

import lampteam.parkourplugin.ParkourPlugin;
import lampteam.parkourplugin.PlayerRecord;
import lampteam.parkourplugin.utils.DebugerTools;
import lampteam.parkourplugin.utils.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public class ParkourCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("parkourse") && commandSender.hasPermission("parkourse")){

            // Помощь
            if (args.length == 0){
                commandSender.sendMessage("/parkourse leave <player>");
                commandSender.sendMessage("/parkourse join <player> <arena_id>");
                commandSender.sendMessage("/parkourse back <player>");
                commandSender.sendMessage("/parkourse checkpoint <player> <checkpoint_id>");
                return false;
            }

            ParkourPlugin plugin = ParkourPlugin.getPlugin();

            // Перезагрузка
            if (args[0].equals("reload")){
                plugin.reload();
                return false;
            }

            // Получение игрока
            if (args.length == 1 || Bukkit.getPlayer(args[1]) == null){
                commandSender.sendMessage("Игрок не существует");
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);

            // Выход с арены
            if (args[0].equals("leave")){
                plugin.getArenaByID(player.getMetadata("parkour_arena_id").get(0).asString()).leave(player);
                return false;
            }

            // Вход в арену
            if (args[0].equals("join")){
                plugin.getArenaByID(args[2]).join(player);
                return false;
            }

            // Возвращение на чекпоинт
            if (args[0].equals("back")){
                plugin.getArenaByID(player.getMetadata("parkour_arena_id").get(0).asString()).returnToLastCheckpoint(player);
                return false;
            }

            // Достижение чекпоинта
            if (args[0].equals("checkpoint")){
                plugin.getArenaByID(player.getMetadata("parkour_arena_id").get(0).asString()).setCheckpoint(player, Integer.parseInt(args[2]));
                return false;
            }

            // Возврат в начало
            if (args[0].equals("restart")) {
                plugin.getArenaByID(player.getMetadata("parkour_arena_id").get(0).asString()).restart(player);
                return false;
            }

            // Статистика
            if (args[0].equals("stats")){
                player.sendMessage(ChatColor.YELLOW + "---- ТВОЯ СТАТИСТИКА ----");
                for (PlayerRecord record : StatsManager.getAllRecordsByPlayer(player)){
                    player.sendMessage(plugin.getArenaByID(record.getCourseID()).getDisplayName() + ChatColor.WHITE + " - " + ChatColor.GREEN + record.getSeconds() + "секунд");
                }
                player.sendMessage(ChatColor.YELLOW + "---- ---- ----- ---- ----");
            }

            if (args[0].equals("debug")){

                if (args[2].equals("alltops")){
                    DebugerTools.getAllTops(player);
                }

            }

        }
        return false;
    }
}
