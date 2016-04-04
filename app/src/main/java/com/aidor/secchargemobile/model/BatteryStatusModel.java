package com.aidor.secchargemobile.model;

/**
 * Created by sahilsiwach on 2/27/2016.
 */
public class BatteryStatusModel {
    private String batteryStatus;
    private String username;

    /**
     *
     * @return
     * The batteryStatus
     */
    public String getBatteryStatus() {
        return batteryStatus;
    }

    /**
     *
     * @param batteryStatus
     * The batteryStatus
     */
    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
