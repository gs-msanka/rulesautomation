package com.gainsight.bigdata.zendesk.pojos;

/**
 * Created by Abhilash Thaduka on 2/29/2016.
 */
public class TicketSyncSchedule {

    private String subdomain;
    private long startTime;
    private String cronExpression;

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
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


}
