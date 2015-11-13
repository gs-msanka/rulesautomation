package com.gainsight.bigdata.dataLoadConfiguartion.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created by Giribabu on 06/07/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAggProcessJobStatus {
    private String createdBy;
    private String createdByName;
    private String modifiedBy;
    private String modifiedByName;
    private String tenantId;
    private String statusId;
    private String requestId;
    private String status;
    private String messageType;
    private String sourceType;
    private String message;
    private double scheduledTime;
    private double queuedTime;
    private double startTime;
    private double endTime;
    private String processSpecificId;
    private String instructionType;
    private int nextScheduledRun;
    private String errorCode;
    private String createdDateStr;
    private String modifiedDateStr;
    private int retryCount;
    private Map<String, String> parameters;

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    private String[] executionMessages;

    private DataProcessResult result;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedByName() {
        return modifiedByName;
    }

    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(double scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public double getQueuedTime() {
        return queuedTime;
    }

    public void setQueuedTime(double queuedTime) {
        this.queuedTime = queuedTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public String getProcessSpecificId() {
        return processSpecificId;
    }

    public void setProcessSpecificId(String processSpecificId) {
        this.processSpecificId = processSpecificId;
    }

    public String getInstructionType() {
        return instructionType;
    }

    public void setInstructionType(String instructionType) {
        this.instructionType = instructionType;
    }

    public int getNextScheduledRun() {
        return nextScheduledRun;
    }

    public void setNextScheduledRun(int nextScheduledRun) {
        this.nextScheduledRun = nextScheduledRun;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getCreatedDateStr() {
        return createdDateStr;
    }

    public void setCreatedDateStr(String createdDateStr) {
        this.createdDateStr = createdDateStr;
    }

    public String getModifiedDateStr() {
        return modifiedDateStr;
    }

    public void setModifiedDateStr(String modifiedDateStr) {
        this.modifiedDateStr = modifiedDateStr;
    }

    public String[] getExecutionMessages() {
        return executionMessages;
    }

    public void setExecutionMessages(String[] executionMessages) {
        this.executionMessages = executionMessages;
    }

    public DataProcessResult getResult() {
        return result;
    }

    public void setResult(DataProcessResult result) {
        this.result = result;
    }

    public static class DataProcessResult {
        private String message;
        private String messageDisplay;
        private String endDateDisplay;
        private String userSyncSuccessCountDisplay;
        private String startDateString;
        private String startDateStringDisplay;
        private String actionDisplay;
        private String rawSuccessRecordsDisplay;
        private String processResultDisplay;
        private String accountSyncSuccessCountDisplay;
        private String endDateString;
        private String action;
        private String processResult;
        private String startDateDisplay;
        private String notifiedDisplay;
        private String accountType;
        private String endDateStringDisplay;
        private String accountTypeDisplay;
        private String notified;
        private String dayAggTransferSuccessCountDisplay;
        private int accountSyncSuccessCount;
        private int userSyncSuccessCount;
        private int dayAggTransferSuccessCount;
        private int rawSuccessRecords;
        private double startDate;
        private double endDate;

        public String getEndDateDisplay() {
            return endDateDisplay;
        }

        public void setEndDateDisplay(String endDateDisplay) {
            this.endDateDisplay = endDateDisplay;
        }

        public String getUserSyncSuccessCountDisplay() {
            return userSyncSuccessCountDisplay;
        }

        public void setUserSyncSuccessCountDisplay(String userSyncSuccessCountDisplay) {
            this.userSyncSuccessCountDisplay = userSyncSuccessCountDisplay;
        }

        public String getStartDateString() {
            return startDateString;
        }

        public void setStartDateString(String startDateString) {
            this.startDateString = startDateString;
        }

        public String getStartDateStringDisplay() {
            return startDateStringDisplay;
        }

        public void setStartDateStringDisplay(String startDateStringDisplay) {
            this.startDateStringDisplay = startDateStringDisplay;
        }

        public String getRawSuccessRecordsDisplay() {
            return rawSuccessRecordsDisplay;
        }

        public void setRawSuccessRecordsDisplay(String rawSuccessRecordsDisplay) {
            this.rawSuccessRecordsDisplay = rawSuccessRecordsDisplay;
        }

        public String getAccountSyncSuccessCountDisplay() {
            return accountSyncSuccessCountDisplay;
        }

        public void setAccountSyncSuccessCountDisplay(String accountSyncSuccessCountDisplay) {
            this.accountSyncSuccessCountDisplay = accountSyncSuccessCountDisplay;
        }

        public String getEndDateString() {
            return endDateString;
        }

        public void setEndDateString(String endDateString) {
            this.endDateString = endDateString;
        }

        public String getStartDateDisplay() {
            return startDateDisplay;
        }

        public void setStartDateDisplay(String startDateDisplay) {
            this.startDateDisplay = startDateDisplay;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getEndDateStringDisplay() {
            return endDateStringDisplay;
        }

        public void setEndDateStringDisplay(String endDateStringDisplay) {
            this.endDateStringDisplay = endDateStringDisplay;
        }

        public String getAccountTypeDisplay() {
            return accountTypeDisplay;
        }

        public void setAccountTypeDisplay(String accountTypeDisplay) {
            this.accountTypeDisplay = accountTypeDisplay;
        }

        public String getDayAggTransferSuccessCountDisplay() {
            return dayAggTransferSuccessCountDisplay;
        }

        public void setDayAggTransferSuccessCountDisplay(String dayAggTransferSuccessCountDisplay) {
            this.dayAggTransferSuccessCountDisplay = dayAggTransferSuccessCountDisplay;
        }

        public int getAccountSyncSuccessCount() {
            return accountSyncSuccessCount;
        }

        public void setAccountSyncSuccessCount(int accountSyncSuccessCount) {
            this.accountSyncSuccessCount = accountSyncSuccessCount;
        }

        public int getUserSyncSuccessCount() {
            return userSyncSuccessCount;
        }

        public void setUserSyncSuccessCount(int userSyncSuccessCount) {
            this.userSyncSuccessCount = userSyncSuccessCount;
        }

        public int getDayAggTransferSuccessCount() {
            return dayAggTransferSuccessCount;
        }

        public void setDayAggTransferSuccessCount(int dayAggTransferSuccessCount) {
            this.dayAggTransferSuccessCount = dayAggTransferSuccessCount;
        }

        public int getRawSuccessRecords() {
            return rawSuccessRecords;
        }

        public void setRawSuccessRecords(int rawSuccessRecords) {
            this.rawSuccessRecords = rawSuccessRecords;
        }

        public double getStartDate() {
            return startDate;
        }

        public void setStartDate(double startDate) {
            this.startDate = startDate;
        }

        public double getEndDate() {
            return endDate;
        }

        public void setEndDate(double endDate) {
            this.endDate = endDate;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getActionDisplay() {
            return actionDisplay;
        }

        public void setActionDisplay(String actionDisplay) {
            this.actionDisplay = actionDisplay;
        }

        public String getProcessResultDisplay() {
            return processResultDisplay;
        }

        public void setProcessResultDisplay(String processResultDisplay) {
            this.processResultDisplay = processResultDisplay;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getProcessResult() {
            return processResult;
        }

        public void setProcessResult(String processResult) {
            this.processResult = processResult;
        }

        public String getNotifiedDisplay() {
            return notifiedDisplay;
        }

        public void setNotifiedDisplay(String notifiedDisplay) {
            this.notifiedDisplay = notifiedDisplay;
        }

        public String getNotified() {
            return notified;
        }

        public void setNotified(String notified) {
            this.notified = notified;
        }

        public String getMessageDisplay() {
            return messageDisplay;
        }

        public void setMessageDisplay(String messageDisplay) {
            this.messageDisplay = messageDisplay;
        }
    }


}
