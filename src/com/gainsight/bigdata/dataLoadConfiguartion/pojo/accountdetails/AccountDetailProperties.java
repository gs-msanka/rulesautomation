package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Giribabu on 10/07/15.
 */
public class AccountDetailProperties {
    private String collectionId;
    private String timeZone;

    @JsonProperty("VIEW_FOR_BDA")
    private String viewForBDA; //dbCollectionName for the Day Agg collection.

    @JsonProperty("VIEW_FOR_BDA_COLLECTION_MASTER_ID")
    private String viewForBDACollectionMaserId; //Collection Master - collectionId for the Day Agg collection

    @JsonProperty("MONGO_TO_STAGE")
    private String mongoToStage; //Staging table for postgres/redshift where data is transferred from the source collection which can be either mongo or redshift

    @JsonProperty("REDSHIFT_DAY_AGG_TABLE")
    private String redShiftDayAggTable; // In case redshift is enabled, this is the table which contains Day Agg collection data

    @JsonProperty("REDSHIFT_FM_TABLE")
    private String redShiftFlippedMeasureTable; // In case redshift is enabled, this is the table which contains Flipped Measures collection data

    private String showFetchData;
    private String processPeopleEngagement;

    public String getViewForBDA() {
        return viewForBDA;
    }

    public void setViewForBDA(String viewForBDA) {
        this.viewForBDA = viewForBDA;
    }

    public String getViewForBDACollectionMaserId() {
        return viewForBDACollectionMaserId;
    }

    public void setViewForBDACollectionMaserId(String viewForBDACollectionMaserId) {
        this.viewForBDACollectionMaserId = viewForBDACollectionMaserId;
    }

    public String getMongoToStage() {
        return mongoToStage;
    }

    public void setMongoToStage(String mongoToStage) {
        this.mongoToStage = mongoToStage;
    }

    public String getRedShiftDayAggTable() {
        return redShiftDayAggTable;
    }

    public void setRedShiftDayAggTable(String redShiftDayAggTable) {
        this.redShiftDayAggTable = redShiftDayAggTable;
    }

    public String getRedShiftFlippedMeasureTable() {
        return redShiftFlippedMeasureTable;
    }

    public void setRedShiftFlippedMeasureTable(String redShiftFlippedMeasureTable) {
        this.redShiftFlippedMeasureTable = redShiftFlippedMeasureTable;
    }

    public String getShowFetchData() {
        return showFetchData;
    }

    public void setShowFetchData(String showFetchData) {
        this.showFetchData = showFetchData;
    }

    public String getProcessPeopleEngagement() {
        return processPeopleEngagement;
    }

    public void setProcessPeopleEngagement(String processPeopleEngagement) {
        this.processPeopleEngagement = processPeopleEngagement;
    }

    public String getCollectionId() {
        return collectionId;

    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
