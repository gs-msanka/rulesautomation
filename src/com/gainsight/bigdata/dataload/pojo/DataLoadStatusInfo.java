package com.gainsight.bigdata.dataload.pojo;

import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;

/**
 * Created by Giribabu on 13/05/15.
 */
public class DataLoadStatusInfo {

    private String collectionId;
    private String dataLoadOperation;
    private String collectionName;
    private DataLoadStatusType statusType;
    private int successCount;
    private int failureCount;
    private String failedRecordLink;
    private String message;

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getDataLoadOperation() {
        return dataLoadOperation;
    }

    public void setDataLoadOperation(String dataLoadOperation) {
        this.dataLoadOperation = dataLoadOperation;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public DataLoadStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(DataLoadStatusType statusType) {
        this.statusType = statusType;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public String getFailedRecordLink() {
        return failedRecordLink;
    }

    public void setFailedRecordLink(String failedRecordLink) {
        this.failedRecordLink = failedRecordLink;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
