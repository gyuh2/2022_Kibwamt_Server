package com.example.smarthome0807;

public class ControlDataInfo {
    private int angle=0;
    private int ac_temp=0;
    private int heater_temp=0;
    private int windowUp =2;
    private int heater =2;
    private int ac =2;
    private int airCleaner = 2;
    private int airOut = 2;
    private int door = 2;
    private String door_passwd = "";

    public int getDoor() {
        return door;
    }

    public String getDoor_passwd() {
        return door_passwd;
    }

    public void setDoor(int door) {
        this.door = door;
    }

    public void setDoor_passwd(String door_passwd) {
        this.door_passwd = door_passwd;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAc_temp() {
        return ac_temp;
    }

    public void setAc_temp(int ac_temp) {
        this.ac_temp = ac_temp;
    }

    public int getHeater_temp() {
        return heater_temp;
    }

    public void setHeater_temp(int heater_temp) {
        this.heater_temp = heater_temp;
    }

    public int getWindowUp() {
        return windowUp;
    }

    public void setWindowUp(int windowUp) {
        this.windowUp = windowUp;
    }

    public int getHeater() {
        return heater;
    }

    public void setHeater(int heater) {
        this.heater = heater;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getAirCleaner() {
        return airCleaner;
    }

    public void setAirCleaner(int airCleaner) {
        this.airCleaner = airCleaner;
    }

    public int getAirOut() {
        return airOut;
    }

    public void setAirOut(int airOut) {
        this.airOut = airOut;
    }
}