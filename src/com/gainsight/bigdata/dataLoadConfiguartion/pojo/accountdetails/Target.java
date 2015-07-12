package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

/**
 * Created by Giribabu on 10/07/15.
 */
public class Target {
    private String dbName;
    private String displayName;
    private Object properties;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getProperties() {
        return properties;
    }

    public void setProperties(Object properties) {
        this.properties = properties;
    }
}
