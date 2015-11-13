package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

/**
 * Created by Giribabu on 10/07/15.
 */
public class EventMeasureMapping {

    private String event;
    private String aggregationFunction;
    private String aggregationKey;
    private String flippedMeasureDisplayName;
    private String flippedMeasureDbName;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getAggregationFunction() {
        return aggregationFunction;
    }

    public void setAggregationFunction(String aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }

    public String getAggregationKey() {
        return aggregationKey;
    }

    public void setAggregationKey(String aggregationKey) {
        this.aggregationKey = aggregationKey;
    }

    public String getFlippedMeasureDisplayName() {
        return flippedMeasureDisplayName;
    }

    public void setFlippedMeasureDisplayName(String flippedMeasureDisplayName) {
        this.flippedMeasureDisplayName = flippedMeasureDisplayName;
    }

    public String getFlippedMeasureDbName() {
        return flippedMeasureDbName;
    }

    public void setFlippedMeasureDbName(String flippedMeasureDbName) {
        this.flippedMeasureDbName = flippedMeasureDbName;
    }
}
