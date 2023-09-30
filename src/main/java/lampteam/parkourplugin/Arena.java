package lampteam.parkourplugin;

import lampteam.parkourplugin.utils.StatsManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class Arena {

    private final String id;
    private final String displayName;
    private final int minY;
    private final Location lobbyLocation;
    private final String difficulty;
    private final List<PlayerRecord> top = new ArrayList<>(5);

    private final List<Location> checkpoints;
    private final HashMap<Player, Integer> currentCheckpoint = new HashMap<>();
    private final HashMap<Player, Integer> currentTime = new HashMap<>();
    private final List<Player> players = new ArrayList<>();

    public String getId(){
        return id;
    }
    public String getDisplayName(){
        return displayName;
    }
    public PlayerRecord getTopPlace(int place){
        return top.get(place - 1);
    }
    public List<PlayerRecord> getTop(){
        return top;
    }

    // Конструктор
    Arena(String id, String displayName, ChatColor nameColor, String difficulty, ChatColor difficultyColor, int minY, Location lobbyLocation, List<Location> checkpoints){
        this.id = id;
        this.displayName = nameColor + displayName;
        this.difficulty = difficultyColor + difficulty;
        this.minY = minY;
        this.lobbyLocation = lobbyLocation;
        this.checkpoints = checkpoints;
        generateTop();
    }

    // Создать топ арены из статистики
    private void generateTop(){

        top.clear();
        List<PlayerRecord> records = new ArrayList<>();

        // Ищем все рекорды арены
        for (PlayerRecord record : StatsManager.getAllRecordsByCourse(id)){

            if (record.getCourseID().equals(id)){
                records.add(record);
            }

        }

        // Сортируем рекорды по возрастанию кол-ва секунд и добавляем пять самых маленьких
        records.sort(Comparator.comparing(PlayerRecord::getSeconds));
        for (int i = 0; i < 5; i++){
            if (i > records.size() - 1){
                break;
            }
            top.add(records.get(i));
        }

    }

    // Для перезагрузок
    public void stop(){
        while (players.size() > 0){
            leave(players.get(0));
        }
    }

    // Вернуть на чекпоинт
    public void returnToLastCheckpoint(Player player){

        // Обнуление времени, если игрок упал, не дойдя даже до первого чекпоинта
        if (checkpoints.get(currentCheckpoint.get(player) - 1) == checkpoints.get(0)){
            currentTime.put(player, 0);
        }

        player.teleport(checkpoints.get(currentCheckpoint.get(player) - 1));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 100, 0.5f);
    }

    // Выход с арены
    public void leave(Player player){

        player.setMetadata("parkour_min_y", new FixedMetadataValue(ParkourPlugin.getPlugin(), -1));
        player.setMetadata("parkour_arena_id", new FixedMetadataValue(ParkourPlugin.getPlugin(), null));

        player.sendMessage(ChatColor.GRAY + "Вы покинули курс " + displayName);
        player.playSound(player.getLocation(), Sound.UI_TOAST_OUT, SoundCategory.MASTER, 100, 2);
        player.getInventory().clear();
        player.teleport(lobbyLocation);

        players.remove(player);
        currentCheckpoint.remove(player);
        currentTime.remove(player);

    }

    // Вход на арену
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

        // Если не спавн
        if (checkpoint != 1) {
            player.sendTitle( ChatColor.GREEN + "Контрольная точка № " + (checkpoint - 1), "Пройдено " + (checkpoint - 1) + "/" + (checkpoints.size() - 1));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER, 100, 1f);
        }
    }

    // Вернуться в начало
    public void restart(Player player){

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER, 100, 0.5f);
        player.teleport(checkpoints.get(0));

        currentCheckpoint.put(player, 1);
        currentTime.put(player, 0);

    }

    // Победа
    private void win(Player player){

        player.sendTitle(ChatColor.GREEN + "Вы прошли " + displayName, "За " + currentTime.get(player) + " секунд!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 100, 1f);

        PlayerRecord prevRecord = StatsManager.getRecord(player, id);

        // Проверка на обновление рекорда
        if (prevRecord == null){
            StatsManager.writeNewRecord(new PlayerRecord(player.getName(), id, currentTime.get(player)));
        } else if (prevRecord.getSeconds() > currentTime.get(player)){
            StatsManager.updateRecord(player, id, currentTime.get(player));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 100, 1f);
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Вы побили свой рекорд!");
        }

        // Проверка на обновления топа курса
        for (int i = 0; i < top.size(); i++){
            if (top.get(i).getSeconds() < currentTime.get(player)){
                continue;
            }
            top.set(i, new PlayerRecord(player.getName(), id, currentTime.get(player)));

        }

        leave(player);
    }

}
