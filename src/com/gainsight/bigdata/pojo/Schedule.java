package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 * Created by Giribabu on 03/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Schedule {
    private String createdBy;
    private String createdByName;
    private String modifiedBy;
    private String modifiedByName;
    private String scheduleId;
    private String tenantId;
    private boolean pastRunsAlso;
    private String title;
    private String description;
    private String type;
    private String cronExpression;
    private long startTime;
    private long endTime;
    private int repeatCount =0;
    private int repeatIntervalInMillis = 0;
    private String jobType;
    private String jobIdentifier;
    private long lastSuccessTime;
    private long lastFailureTime;
    private long failingSince;
    private boolean lastRunSuccess;
    private boolean runningNow;
    private long nextRunTime;
    private boolean inactive = false;
    private String timeZoneName;
    private String createdDateStr;
    private String modifiedDateStr;
    private String[] emailIdList;
    private Map<String, String> jobContext;
    private String triggerKey;

    public String getTriggerKey() {
        return triggerKey;
    }

    public void setTriggerKey(String triggerKey) {
        this.triggerKey = triggerKey;
    }

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

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isPastRunsAlso() {
        return pastRunsAlso;
    }

    public void setPastRunsAlso(boolean pastRunsAlso) {
        this.pastRunsAlso = pastRunsAlso;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getRepeatIntervalInMillis() {
        return repeatIntervalInMillis;
    }

    public void setRepeatIntervalInMillis(int repeatIntervalInMillis) {
        this.repeatIntervalInMillis = repeatIntervalInMillis;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(String jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    public long getLastSuccessTime() {
        return lastSuccessTime;
    }

    public void setLastSuccessTime(long lastSuccessTime) {
        this.lastSuccessTime = lastSuccessTime;
    }

    public long getLastFailureTime() {
        return lastFailureTime;
    }

    public void setLastFailureTime(long lastFailureTime) {
        this.lastFailureTime = lastFailureTime;
    }

    public long getFailingSince() {
        return failingSince;
    }

    public void setFailingSince(long failingSince) {
        this.failingSince = failingSince;
    }

    public boolean isLastRunSuccess() {
        return lastRunSuccess;
    }

    public void setLastRunSuccess(boolean lastRunSuccess) {
        this.lastRunSuccess = lastRunSuccess;
    }

    public boolean isRunningNow() {
        return runningNow;
    }

    public void setRunningNow(boolean runningNow) {
        this.runningNow = runningNow;
    }

    public long getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(long nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
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

    public String[] getEmailIdList() {
        return emailIdList;
    }

    public void setEmailIdList(String[] emailIdList) {
        this.emailIdList = emailIdList;
    }

    public Map<String, String> getJobContext() {
        return jobContext;
    }

    public void setJobContext(Map<String, String> jobContext) {
        this.jobContext = jobContext;
    }
}
