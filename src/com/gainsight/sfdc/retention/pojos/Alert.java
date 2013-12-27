package com.gainsight.sfdc.retention.pojos;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 20/12/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Alert {

    private String customer;
    private String subject;
    private String severity;
    private String date;
    private String asv;
    private String type;
    private String reason;
    private String status;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAsv() {
        return asv;
    }

    public void setAsv(String asv) {
        this.asv = asv;
    }



}
