package com.gainsight.bigdata.copilot.bean.outreach;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Giribabu on 17/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutReachExecutionHistory {

    private String[] contactIds;
    private int nContacts;
    private String[] accountIds;
    private String status;
    @JsonProperty("nUsb")
    private int noOfUnSubscribed;
    private int nAccounts;
    private int nSent;
    private int passCount;
    private int failCount;
    private long nextScheduledRun;
    private int nOpened;
    private int nRejected;
    private String executionType;
    private long scheduledTime;
    private int nClicked;
    @JsonProperty("nSb")
    private int noOfSoftBounce;
    @JsonProperty("uOpened")
    private int uniqueOpened;

    private int nSpam;
    @JsonProperty("nHb")
    private int noOfHardBounce;

    public int getUniqueOpened() {
        return uniqueOpened;
    }

    public void setUniqueOpened(int uniqueOpened) {
        this.uniqueOpened = uniqueOpened;
    }

    public String[] getContactIds() {
        return contactIds;
    }

    public void setContactIds(String[] contactIds) {
        this.contactIds = contactIds;
    }

    public int getnContacts() {
        return nContacts;
    }

    public void setnContacts(int nContacts) {
        this.nContacts = nContacts;
    }

    public String[] getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(String[] accountIds) {
        this.accountIds = accountIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNoOfUnSubscribed() {
        return noOfUnSubscribed;
    }

    public void setNoOfUnSubscribed(int noOfUnSubscribed) {
        this.noOfUnSubscribed = noOfUnSubscribed;
    }

    public int getnAccounts() {
        return nAccounts;
    }

    public void setnAccounts(int nAccounts) {
        this.nAccounts = nAccounts;
    }

    public int getnSent() {
        return nSent;
    }

    public void setnSent(int nSent) {
        this.nSent = nSent;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public long getNextScheduledRun() {
        return nextScheduledRun;
    }

    public void setNextScheduledRun(long nextScheduledRun) {
        this.nextScheduledRun = nextScheduledRun;
    }

    public int getnOpened() {
        return nOpened;
    }

    public void setnOpened(int nOpened) {
        this.nOpened = nOpened;
    }

    public int getnRejected() {
        return nRejected;
    }

    public void setnRejected(int nRejected) {
        this.nRejected = nRejected;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public int getnClicked() {
        return nClicked;
    }

    public void setnClicked(int nClicked) {
        this.nClicked = nClicked;
    }

    public int getNoOfSoftBounce() {
        return noOfSoftBounce;
    }

    public void setNoOfSoftBounce(int noOfSoftBounce) {
        this.noOfSoftBounce = noOfSoftBounce;
    }

    public int getnSpam() {
        return nSpam;
    }

    public void setnSpam(int nSpam) {
        this.nSpam = nSpam;
    }

    public int getNoOfHardBounce() {
        return noOfHardBounce;
    }

    public void setNoOfHardBounce(int noOfHardBounce) {
        this.noOfHardBounce = noOfHardBounce;
    }
}
