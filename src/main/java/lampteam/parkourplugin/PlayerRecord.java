package lampteam.parkourplugin;

import java.util.Date;

public class PlayerRecord {

    private final String playerName;
    private final String courseID;
    private int seconds;
    private Date date;

    public PlayerRecord(String playerName, String courseID, int seconds) {
        this.playerName = playerName;
        this.courseID = courseID;
        this.seconds = seconds;
        this.date = new Date();
    }

    public Date getDate(){
        return date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCourseID() {
        return courseID;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
