package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by vmenon on 9/13/2015.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CTAAction {

    private String name = "";
    private String priority = "";
    private String type = "";
    private String status = "";
    private String playbook = "";
    private String reason = "";
    private String ownerField = "";
    private String dueDate = "";
    private String defaultOwner = "";
    private String comments = "";
    private boolean ctaUpsert=false;

    public boolean isCtaUpsert() {
		return ctaUpsert;
	}

	public void setCtaUpsert(boolean ctaUpsert) {
		this.ctaUpsert = ctaUpsert;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlaybook() {
        return playbook;
    }

    public void setPlaybook(String playbook) {
        this.playbook = playbook;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOwnerField() {
        return ownerField;
    }

    public void setOwnerField(String ownerField) {
        this.ownerField = ownerField;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDefaultOwner() {
        return defaultOwner;
    }

    public void setDefaultOwner(String defaultOwner) {
        this.defaultOwner = defaultOwner;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
