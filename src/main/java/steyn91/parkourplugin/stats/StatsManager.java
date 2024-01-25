package steyn91.parkourplugin.stats;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.scheduler.BukkitRunnable;
import steyn91.parkourplugin.ParkourPlugin;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatsManager {

    private static final ParkourPlugin plugin = ParkourPlugin.getPlugin();
    private static final ArrayList<PlayerStat> cache = new ArrayList<>();

    public static PlayerStat getPlayerStatOnCourse(String playerName, String courseId){
        for (PlayerStat stat : cache){
            boolean isPlayerEquals = stat.getPlayerName().equals(playerName);
            boolean isCourseEquals = stat.getCourseId().equals(courseId);
            if (isPlayerEquals && isCourseEquals) return stat;
        }

        PlayerStat stat = Database.getPlayerStatOnArena(playerName, courseId);
        if (stat == null) stat = new PlayerStat(playerName, courseId, 0, 0);

        cache.add(stat);

        return stat;
    }

    public static void removeStatOnArena(String playerName, String courseID){
        Database.removePlayerStatOnArena(playerName, courseID);
        for (PlayerStat record : cache){
            boolean nameEquals = record.getPlayerName().equals(playerName);
            boolean courseEquals = record.getCourseId().equals(courseID);
            if (!nameEquals || !courseEquals) continue;

            cache.remove(record);
            return;
        }
    }

    public static List<PlayerStat> getAllStatsOfPlayer(Player p){
        ArrayList<PlayerStat> records = new ArrayList<>();

        for (PlayerStat record : cache){
            if (!record.getPlayerName().equals(p.getName())) continue;
            records.add(record);
        }
        return records;
    }

    public static void updateSeconds(Player player, String courseID, int newSeconds){
        PlayerStat record = getPlayerStatOnCourse(player.getName(), courseID);
        if (record.getSeconds() == 0) record.setSeconds(newSeconds);
        if (record.getSeconds() <= newSeconds) return;

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 100, 1f);
        player.sendMessage(Component.text("Вы побили свой рекорд!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        record.setSeconds(newSeconds);
        record.setLastUpdate(new Date());
    }

    public static void updateAttempts(Player p, String courseID, int newAttempts){
        PlayerStat record = getPlayerStatOnCourse(p.getName(), courseID);
        record.setAttempts(record.getAttempts() + newAttempts);
        record.setLastUpdate(new Date());
    }

    // TODO Очистка статов
    public void cleanUp(int inactiveDays){
        // Очистка старых рекордов, не находящихся в топах
    }

    public static void startSavingCycle(){
        BukkitRunnable saveToDBCycle = new BukkitRunnable() {
            @Override
            public void run() {
                saveAllToDB();
            }
        };
        saveToDBCycle.runTaskTimerAsynchronously(plugin, 20L * 60 * plugin.getConfig().getInt("DatabaseSavingTimer"), 20L * 60 * plugin.getConfig().getInt("DatabaseSavingTimer"));
    }

    public static void saveAllToDB(){
        for (PlayerStat stat : cache){
            Database.updatePlayerStat(stat);
        }
        // Способ очистки кэша ПОЛНАЯ ХУЙНЯ!!! Очень нужно будет переписать на CacheEntry с датой обновления записи
        cache.clear();
    }
}