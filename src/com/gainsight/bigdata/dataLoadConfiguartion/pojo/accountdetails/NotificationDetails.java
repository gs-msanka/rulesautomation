package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

/**
 * Created by Giribabu on 10/07/15.
 */
public class NotificationDetails {

    private String[] successRecipients;
    private String[] failureRecipients;

    public String[] getSuccessRecipients() {
        return successRecipients;
    }

    public void setSuccessRecipients(String[] successRecipients) {
        this.successRecipients = successRecipients;
    }

    public String[] getFailureRecipients() {
        return failureRecipients;
    }

    public void setFailureRecipients(String[] failureRecipients) {
        this.failureRecipients = failureRecipients;
    }
}
