package lampteam.parkourplugin.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lampteam.parkourplugin.ParkourPlugin;
import lampteam.parkourplugin.PlayerRecord;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatsManager {

    private static final ParkourPlugin plugin = ParkourPlugin.getPlugin();
    private static ArrayList<PlayerRecord> playerRecords = new ArrayList<>();
    private static final Gson gson = new Gson();
    private static final File file = new File(plugin.getDataFolder().getAbsoluteFile() + "/stats.json");

    public static void writeNewRecord(PlayerRecord record){
        playerRecords.add(record);
    }

    public static void removeRecord(Player p, String courseID){

        for (PlayerRecord record : playerRecords){

            if (!record.getPlayerName().equals(p.getName())){
                continue;
            }

            if (!record.getCourseID().equals(courseID)){
                continue;
            }

            playerRecords.remove(record);
            return;
        }

    }

    public static PlayerRecord getRecord(Player p, String courseID){

        for (PlayerRecord record : playerRecords){

            if (!record.getPlayerName().equals(p.getName())){
                continue;
            }

            if (!record.getCourseID().equals(courseID)){
                continue;
            }

            return record;
        }

        return null;

    }

    public static List<PlayerRecord> getAllRecordsByPlayer(Player p){

        ArrayList<PlayerRecord> records = new ArrayList<>();

        for (PlayerRecord record : playerRecords){

            if (!record.getPlayerName().equals(p.getName())){
                continue;
            }

            records.add(record);
        }

        return records;

    }

    public static List<PlayerRecord> getAllRecordsByCourse(String courseid){
        List<PlayerRecord> records = new ArrayList<>();
        for (PlayerRecord record : playerRecords){
            if (record.getCourseID().equals(courseid)){
                records.add(record);
            }
        }
        return records;
    }

    public static void updateRecord(Player p, String courseID, int seconds){

        PlayerRecord record = getRecord(p, courseID);

        if (record == null){
            return;
        }

        record.setSeconds(seconds);
        record.setDate(new Date());

    }

    public static void saveRecords() throws IOException {

        System.out.println("[ParkourPlugin] Рекорды сохраняются...");
        Writer writer = new FileWriter(file, false);
        file.createNewFile();
        gson.toJson(playerRecords, writer);
        writer.flush();
        writer.close();
        System.out.println("[ParkourPlugin] Рекорды сохранены!");

    }

    public static void loadRecords() throws IOException {

        System.out.println("[ParkourPlugin] Рекорды загружаются...");
        playerRecords.clear();
        Reader reader = new FileReader(plugin.getDataFolder().getAbsoluteFile() + "/stats.json");
        Type type = new TypeToken<ArrayList<PlayerRecord>>(){}.getType();
        playerRecords = gson.fromJson(reader, type);
        reader.close();
        System.out.println("[ParkourPlugin] Рекорды загружены");

    }

}
