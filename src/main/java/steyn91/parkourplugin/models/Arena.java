package steyn91.parkourplugin.models;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import steyn91.parkourplugin.ParkourPlugin;
import steyn91.parkourplugin.stats.StatsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class Arena {

    // Из конфига
    private final String id;
    private final String displayName;
    private final int minY;
    private final Location lobbyLocation;
    private final String difficulty;
    private final List<Location> checkpoints;

    // Технические
    private final HashMap<Player, Integer> currentCheckpoint = new HashMap<>();
    private final HashMap<Player, Integer> attempts = new HashMap<>();
    private final HashMap<Player, Integer> currentTime = new HashMap<>();
    private final List<Player> players = new ArrayList<>();

    public int getMinY(){
        return minY;
    }
    public String getId(){
        return id;
    }
    public List<Player> getPlayers(){
        return players;
    }
    public String getDisplayName(){
        return displayName;
    }

    public Arena(String id, String displayName, ChatColor nameColor, String difficulty, ChatColor difficultyColor, int minY, Location lobbyLocation, List<Location> checkpoints){
        this.id = id;
        this.displayName = nameColor + displayName;
        this.difficulty = difficultyColor + difficulty;
        this.minY = minY;
        this.lobbyLocation = lobbyLocation;
        this.checkpoints = checkpoints;
    }

    public void stop(){
        while (players.size() > 0){
            leave(players.get(0));
        }
    }

    public void returnToLastCheckpoint(Player player){
        // Обнуление времени, если игрок упал, не дойдя даже до первого чекпоинта
        if (checkpoints.get(currentCheckpoint.get(player) - 1) == checkpoints.get(0)){
            currentTime.put(player, 0);
        }

        player.teleport(checkpoints.get(currentCheckpoint.get(player) - 1));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 100, 0.5f);
        attempts.put(player, attempts.get(player) + 1);
    }

    public void leave(Player player){
        player.sendMessage(ChatColor.GRAY + "Вы покинули курс " + displayName);
        player.playSound(player.getLocation(), Sound.UI_TOAST_OUT, SoundCategory.MASTER, 100, 2);
        player.getInventory().clear();
        player.teleport(lobbyLocation);
        player.getInventory().setHeldItemSlot(5);

        StatsManager.updateAttempts(player, id, attempts.get(player));

        players.remove(player);
        currentCheckpoint.remove(player);
        currentTime.remove(player);
        attempts.remove(player);
    }

    public void join(Player player){
        player.setMetadata("parkour_min_y", new FixedMetadataValue(ParkourPlugin.getPlugin(), minY));
        player.setMetadata("parkour_arena_id", new FixedMetadataValue(ParkourPlugin.getPlugin(), id));

        player.sendTitle(displayName, "Сложонсть: " + difficulty);
        player.teleport(checkpoints.get(0));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 100, 1);
        player.setGameMode(GameMode.ADVENTURE);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit parkour " + player.getName());

        setCheckpoint(player, 1);
        currentTime.put(player, 0);
        players.add(player);
        attempts.put(player, 1);

        // Отображение времени над инвентарём
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

    public void setCheckpoint(Player player, int checkpoint){
        if (currentCheckpoint.get(player) != null && currentCheckpoint.get(player) >= checkpoint){
            return;
        }

        currentCheckpoint.put(player, checkpoint);

        if (checkpoint == checkpoints.size()){
            win(player);
            return;
        }

        if (checkpoint != 1) {
            player.sendTitle( ChatColor.GREEN + "Контрольная точка №" + (checkpoint - 1), "Пройдено " + (checkpoint - 1) + "/" + (checkpoints.size() - 1));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER, 100, 1f);
            player.sendMessage("Вы достигли контрольной точки №" + (checkpoint - 1) + " за " + ChatColor.RED + currentTime.get(player) + ChatColor.WHITE + " секунд");
        }
    }

    public void restart(Player player){
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 100, 0.5f);
        player.teleport(checkpoints.get(0));

        currentCheckpoint.put(player, 1);
        currentTime.put(player, 0);

    }

    private void win(Player player){
        player.sendTitle(ChatColor.GREEN + "Вы прошли " + displayName, "За " + currentTime.get(player) + " секунд!");
        player.sendMessage(ChatColor.GREEN + "Вы прошли " + displayName + ChatColor.GREEN + " за " + ChatColor.YELLOW + currentTime.get(player) + ChatColor.GREEN + " секунд и " + ChatColor.RED + attempts.get(player) + ChatColor.GREEN + " попыток");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 100, 1f);

        StatsManager.updateSeconds(player, id, currentTime.get(player));
        StatsManager.updateAttempts(player, id, attempts.get(player));

        leave(player);
    }

}
