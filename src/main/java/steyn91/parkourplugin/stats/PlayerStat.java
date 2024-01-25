package steyn91.parkourplugin.stats;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class PlayerStat implements Comparable<PlayerStat>{

    private final String playerName;
    private final String courseId;
    private int seconds;
    private int attempts;
    private Date lastUpdate;

    public PlayerStat(String playerName, String courseId, int seconds, int attempts) {
        this.playerName = playerName;
        this.courseId = courseId;
        this.seconds = seconds;
        this.attempts = attempts;
        this.lastUpdate = new Date();
    }

    public String getPlayerName() {
        return playerName;
    }
    public String getCourseId() {
        return courseId;
    }
    public int getSeconds() {
        return seconds;
    }
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    public int getAttempts() {
        return attempts;
    }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int compareTo(@NotNull PlayerStat o) {
        return o.getSeconds() - seconds;
    }
}
