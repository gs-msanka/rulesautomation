package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

/**
 * Created by Giribabu on 10/07/15.
 */
public class UsageConfiguration {

    private String configType = "ACCOUNTLEVEL";
    private String frequency= "MONTHLY";
    private String day = "MONDAY";
    private String weekType = "START";

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeekType() {
        return weekType;
    }

    public void setWeekType(String weekType) {
        this.weekType = weekType;
    }
}
