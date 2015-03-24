package bean;

import java.util.Date;

/**
 * Created by cjf on 2014/9/17.
 */
public class ParkStatusBean {
    private String parkID;
    private int totalSpaces;
    private int remainingSpaces;
    private int status;
    private Date lastUpdate;

    public static int ONLINE=1;
    public static int OFFLINE=0;

    public String getParkID() {
        return parkID;
    }

    public void setParkID(String parkID) {
        this.parkID = parkID;
    }

    public int getTotalSpaces() {
        return totalSpaces;
    }

    public void setTotalSpaces(int totalSpaces) {
        this.totalSpaces = totalSpaces;
    }

    public int getRemainingSpaces() {
        return remainingSpaces;
    }

    public void setRemainingSpaces(int remainingSpaces) {
        this.remainingSpaces = remainingSpaces;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
