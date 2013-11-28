package com.gainsight.sfdc.retention.pojos;

public class Task {

    private String subject;
    private String owner;
    private String priority;
    private String status;
    private String date;
    private String ownerdateText;

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
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getOwnerdateText() {
        return ownerdateText;
    }
    public void setOwnerdateText(String ownerdateText) {
        this.ownerdateText = ownerdateText;
    }


}
