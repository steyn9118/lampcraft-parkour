package lampteam.parkourplugin.commands;

import lampteam.parkourplugin.ParkourPlugin;
import org.bukkit.Bukkit;
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

            // Получение игрока
            ParkourPlugin plugin = ParkourPlugin.getPlugin();
            if (Bukkit.getPlayer(args[1]) == null){
                commandSender.sendMessage("Игрок не существует");
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);

            // Выход с арены
            if (args[0].equals("leave")){
                plugin.getArena(player.getMetadata("parkour_arena_id").get(0).asString()).leave(player);
                return false;
            }

            // Вход в арену
            if (args[0].equals("join")){
                plugin.getArena(args[2]).join(player);
                return false;
            }

            // Возвращение на чекпоинт
            if (args[0].equals("back")){
                plugin.getArena(player.getMetadata("parkour_arena_id").get(0).asString()).returnToLastCheckpoint(player);
                return false;
            }

            // Достижение чекпоинта
            if (args[0].equals("checkpoint")){
                plugin.getArena(player.getMetadata("parkour_arena_id").get(0).asString()).setCheckpoint(player, Integer.parseInt(args[2]));
                return false;
            }

            // Возврат в начало
            if (args[0].equals("restart")){
                plugin.getArena(player.getMetadata("parkour_arena_id").get(0).asString()).restart(player);
                return false;
            }

            // Перезагрузка
            if (args[0].equals("reload")){
                plugin.reload();
                return false;
            }

        }
        return false;
    }
}
