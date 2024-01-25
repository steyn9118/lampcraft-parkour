package steyn91.parkourplugin.stats;

import steyn91.parkourplugin.ParkourPlugin;
import org.bukkit.Bukkit;

import java.sql.*;

public class Database {
    private static final ParkourPlugin plugin = ParkourPlugin.getPlugin();
    private static Connection connection;

    // Для получения подключения
    public static Connection getConnection() {

        String url = plugin.getConfig().getString("url");
        assert url != null;
        String user = plugin.getConfig().getString("user");
        String pwd = plugin.getConfig().getString("password");

        if (connection == null){
            // Подключение к БД
            try {
                connection = DriverManager.getConnection(url, user, pwd);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            Bukkit.getLogger().info("Подключено к БД");
        }

        try {
            connection.createStatement().execute("SELECT 1");
        } catch (SQLException e) {
            // Подключение к БД
            try {
                connection = DriverManager.getConnection(url, user, pwd);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            Bukkit.getLogger().info("Переподключено к БД");
        }
        return connection;
    }

    public static void initDatabase(){
        try {
            Statement statement = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS parkour_stats (playerName VARCHAR(16), courseId VARCHAR(16), seconds INT, attempts INT, lastUpdate DATE, PRIMARY KEY (playerName, courseId))";
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerStat getPlayerStatOnArena(String playerName, String courseId){
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM parkour_stats WHERE (playerName = ?) AND (courseId = ?)");
            statement.setString(1, playerName);
            statement.setString(2, courseId);

            ResultSet resultSet = statement.executeQuery();
            PlayerStat statsModel;
            if (resultSet.next()){
                statsModel = new PlayerStat(
                        resultSet.getString("playerName"),
                        resultSet.getString("courseId"),
                        resultSet.getInt("seconds"),
                        resultSet.getInt("attempts"));
                statement.close();

                return statsModel;
            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removePlayerStatOnArena(String playerName, String courseId){
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM parkour_stats WHERE (playerName = ?) AND (courseId = ?)");

            statement.setString(1, playerName);
            statement.setString(2, courseId);

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createPlayerStat(String playerName, String courseId){
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO parkour_stats(playerName, courseId, seconds, attempts, lastUpdate) VALUES (?, ?, ?, ?, ?)");

            statement.setString(1, playerName);
            statement.setString(2, courseId);
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setDate(5, new Date(new java.util.Date().getTime()));

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayerStat(PlayerStat statsModel){
        String playerName = statsModel.getPlayerName();
        String courseId = statsModel.getCourseId();

        if (getPlayerStatOnArena(playerName, courseId) == null) createPlayerStat(playerName, courseId);

        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE parkour_stats SET seconds = ?, attempts = ?, lastUpdate = ? WHERE (playerName = ?) AND (courseId = ?)");

            statement.setInt(1, statsModel.getSeconds());
            statement.setInt(2, statsModel.getAttempts());
            statement.setDate(3, new Date(new java.util.Date().getTime()));
            statement.setString(4, playerName);
            statement.setString(5, courseId);

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
