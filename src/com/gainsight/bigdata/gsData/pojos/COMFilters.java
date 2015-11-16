package com.gainsight.bigdata.gsData.pojos;

/**
 * Created by Giribabu on 19/08/15.
 */
public class COMFilters {
    private String dbName;
    private String alias;
    private String dataType;
    private String filterOperator;
    private String logicalOperator;
    private Object filterValues;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFilterOperator() {
        return filterOperator;
    }

    public void setFilterOperator(String filterOperator) {
        this.filterOperator = filterOperator;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public Object getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(Object filterValues) {
        this.filterValues = filterValues;
    }
}
