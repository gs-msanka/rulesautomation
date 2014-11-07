package com.gainsight.sfdc.workflow.pojos;

import java.util.List;

/**
 * Created by gainsight on 07/11/14.
 */
public class CTA {
    private String type;
    private String subject;
    private String customer;
    private String status;
    private String reason;
    private String dueDate;
    private String comments;
    private boolean isImp;
    private String priority;
    private int taskCount;
    private String assignee;
    private List<Att> attributes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isImp() {
        return isImp;
    }

    public void setImp(boolean isImp) {
        this.isImp = isImp;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<Att> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Att> attributes) {
        this.attributes = attributes;
    }

    public class Att {
        private String attLabel;
        private String attValue;
        private boolean inSummary;

        public boolean isInSummary() {
            return inSummary;
        }

        public void setInSummary(boolean inSummary) {
            this.inSummary = inSummary;
        }

        public String getAttLabel() {
            return attLabel;
        }

        public void setAttLabel(String attLabel) {
            this.attLabel = attLabel;
        }

        public String getAttValue() {
            return attValue;
        }

        public void setAttValue(String attValue) {
            this.attValue = attValue;
        }
    }


}
