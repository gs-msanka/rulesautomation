package com.gainsight.sfdc.workflow.pojos;

/**
 * Created by gainsight on 11/11/14.
 */
public class Task {

    private String assignee;
    private String subject;
    private String date = "1";
    private String priority = "Medium";
    private String status = "Open";
    private String playbookName;
    private String comments;
    private boolean isFromCustomer360=false;
    
    public boolean isFromCustomer360() {
		return isFromCustomer360;
	}

	public void setFromCustomer360(boolean isFromCustomer360) {
		this.isFromCustomer360 = isFromCustomer360;
	}

	public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
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

    public String getPlaybookName() {
        return playbookName;
    }

    public void setPlaybookName(String playbookName) {
        this.playbookName = playbookName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
