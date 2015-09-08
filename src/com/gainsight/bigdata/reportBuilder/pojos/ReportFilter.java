package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 03/09/15.
 */
public class ReportFilter {

    private String dbName;
    private String alias;
    private String dataType;
    private String filterOperator;
    private String logicalOperator;
    private List<Object> filterValues;
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    private String timeFunction;
    private boolean locked;
    private String collectionId;
    private int type;

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    private String aggregateFunction;

    public String getAggregateFunction() {
        return aggregateFunction;
    }

    public void setAggregateFunction(String aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
    }

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

    public List<Object> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<Object> filterValues) {
        this.filterValues = filterValues;
    }

    public String getTimeFunction() {
        return timeFunction;
    }

    public void setTimeFunction(String timeFunction) {
        this.timeFunction = timeFunction;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
