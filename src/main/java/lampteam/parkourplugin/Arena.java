package lampteam.parkourplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class Arena {

    private String id;
    private String displayName;
    private int minY;
    private Location lobbyLocation;
    private String difficulty;

    private List<Location> checkpoints = new ArrayList<>();
    private final HashMap<Player, Integer> currentCheckpoint = new HashMap<>();
    private final HashMap<Player, Integer> currentTime = new HashMap<>();
    private final List<Player> players = new ArrayList<>();

    public String getId(){
        return id;
    }

    public void init(String id, String displayName, ChatColor nameColor, String difficulty, ChatColor difficultyColor, int minY, Location lobbyLocation, List<Location> checkpoints){
        this.id = id;
        this.displayName = nameColor + displayName;
        this.difficulty = difficultyColor + difficulty;
        this.minY = minY;
        this.lobbyLocation = lobbyLocation;
        this.checkpoints = checkpoints;
    }

    // Для перезагрузок
    public void stop(){
        for (Player player : players){
            leave(player);
        }
    }

    // Вернуть на чекпоинт
    public void returnToLastCheckpoint(Player player){
        player.teleport(checkpoints.get(currentCheckpoint.get(player) - 1));
    }

    // Выход с арены
    public void leave(Player player){
        player.setMetadata("parkour_min_y", new FixedMetadataValue(ParkourPlugin.getPlugin(), -1));
        player.setMetadata("parkour_arena_id", new FixedMetadataValue(ParkourPlugin.getPlugin(), null));
        player.sendMessage(ChatColor.GRAY + "Вы покинули курс " + displayName);
        player.teleport(lobbyLocation);
        players.remove(player);
        currentCheckpoint.remove(player);
        currentTime.remove(player);
        player.getInventory().clear();
    }

    // Вход на арену
    public void join(Player player){
        player.setMetadata("parkour_min_y", new FixedMetadataValue(ParkourPlugin.getPlugin(), minY));
        player.setMetadata("parkour_arena_id", new FixedMetadataValue(ParkourPlugin.getPlugin(), id));
        player.sendTitle(displayName, "Сложонсть: " + difficulty);
        player.teleport(checkpoints.get(0));
        players.add(player);
        setCheckpoint(player, 1);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit parkour " + player.getName());
        currentTime.put(player, 0);
        BukkitRunnable playerTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (!players.contains(player)){
                    this.cancel();
                    return;
                }
                player.sendActionBar("Время: " + ChatColor.YELLOW + currentTime.get(player));
                currentTime.put(player, currentTime.get(player) + 1);
            }
        };
        playerTimer.runTaskTimer(ParkourPlugin.getPlugin(), 0, 20);
    }

    // Задать чекпоинт
    public void setCheckpoint(Player player, int checkpoint){

        // Проверка на достижение уже достигнутой
        if (currentCheckpoint.get(player) != null && currentCheckpoint.get(player) >= checkpoint){
            return;
        }

        currentCheckpoint.put(player, checkpoint);

        // Проверка на достижение последнего чекпоинта
        if (checkpoint == checkpoints.size()){
            win(player);
            return;
        }


        if (checkpoint != 1) {
            player.sendTitle( ChatColor.GREEN + "Контрольная точка № " + (checkpoint - 1), "Пройдено " + (checkpoint - 1) + "/" + (checkpoints.size() - 1));
        }
    }

    // Вернуться в начало
    public void restart(Player player){
        currentCheckpoint.put(player, 1);
        player.teleport(checkpoints.get(0));
        currentTime.put(player, 0);
    }

    private void win(Player player){
        player.sendTitle(ChatColor.GREEN + "Вы прошли " + displayName, "За " + currentTime.get(player) + " секунд!");
        leave(player);
    }

}
