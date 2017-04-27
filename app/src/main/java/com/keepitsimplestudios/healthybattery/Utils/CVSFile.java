package com.keepitsimplestudios.healthybattery.Utils;

import java.util.Date;

/**
 * Created by nunop on 18/01/2017.
 */

public class CVSFile {

    Date date;
    int batteryLevel;

    public CVSFile(int batteryLevel, Date date) {
        this.batteryLevel = batteryLevel;
        this.date = date;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CVSFile{" +
                "batteryLevel=" + batteryLevel +
                ", date=" + date +
                '}' + "\n";
    }
}
