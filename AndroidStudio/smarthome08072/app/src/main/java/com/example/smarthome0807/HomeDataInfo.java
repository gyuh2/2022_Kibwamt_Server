package com.example.smarthome0807;

public class HomeDataInfo {
    private String id;
    private float temp;
    private float humid;
    private float pm;
    private int pmGrade;
    private float API_PM;
    private float API_PMGrade;
    private float API_temp;
    private float API_humid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getHumid() {
        return humid;
    }

    public void setHumid(float humid) {
        this.humid = humid;
    }

    public float getPm() {
        return pm;
    }

    public void setPm(float pm) {
        this.pm = pm;
    }

    public int getPmGrade() {
        return pmGrade;
    }

    public void setPmGrade(int pmGrade) {
        this.pmGrade = pmGrade;
    }

    public float getAPI_PM() {
        return API_PM;
    }

    public void setAPI_PM(float API_PM) {
        this.API_PM = API_PM;
    }

    public float getAPI_PMGrade() {
        return API_PMGrade;
    }

    public void setAPI_PMGrade(float API_PMGrade) {
        this.API_PMGrade = API_PMGrade;
    }

    public float getAPI_temp() {
        return API_temp;
    }

    public void setAPI_temp(float API_temp) {
        this.API_temp = API_temp;
    }

    public float getAPI_humid() {
        return API_humid;
    }

    public void setAPI_humid(float API_humid) {
        this.API_humid = API_humid;
    }
}
