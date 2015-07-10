package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 10/07/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Source {
    private String type;
    private String objectName;
    private String dbName;
    private String displayName;
    private Object properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

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
