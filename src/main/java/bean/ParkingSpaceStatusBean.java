package bean;

import java.util.Date;

/**
 * Created by cjf on 2014/9/18.
 */
public class ParkingSpaceStatusBean {
    private String parkingSpaceID;
    private int parkID;
    private int status;
    private Date lastUpdate;

    public static final int AVAILABLE = 0;
    public static final int OCCUPIED_WITHOUT_TIME_OUT = 1;
    public static final int OCCUPIED_WITH_TIME_OUT = 2;
    public static final int OCCUPIED_PAID = 3;

    public String getParkingSpaceID() {
        return parkingSpaceID;
    }

    public void setParkingSpaceID(String parkingSpaceID) {
        this.parkingSpaceID = parkingSpaceID;
    }

    public int getParkID() {
        return parkID;
    }

    public void setParkID(int parkID) {
        this.parkID = parkID;
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
