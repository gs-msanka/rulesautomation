package com.gainsight.sfdc.retention.pojos;

public class Event {

    private String customer;
    private String type;
    private String subject;
    private String owner;
    private String scDate;
    private String eventStatusMsg;
    private String status;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getScDate() {
        return scDate;
    }
    public void setScDate(String scDate) {
        this.scDate = scDate;
    }
    public String getEventStatusMsg() {
        return eventStatusMsg;
    }
    public void setEventStatusMsg(String eventStatusMsg) {
        this.eventStatusMsg = eventStatusMsg;
    }


}
