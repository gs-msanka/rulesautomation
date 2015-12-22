package com.gainsight.bigdata.copilot.smartlist.pojos;

import java.util.ArrayList;

import com.gainsight.bigdata.copilot.bean.smartlist.ActionDetails;

public class AutomatedRule{
	private String relatedId;
	private String ruleType;
	private String description;
	private String triggerCriteria;
	private String sourceType;
	private ArrayList<ActionDetails> actionDetails;
	private String triggerUsageOn;
	
	
	public ArrayList<ActionDetails> getActionDetails() {
		return actionDetails;
	}
	public void setActionDetails(ArrayList<ActionDetails> actionDetails) {
		this.actionDetails = actionDetails;
	}
	
	
	public String getRelatedId() {
		return relatedId;
	}
	public void setRelatedId(String relatedId) {
		this.relatedId = relatedId;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTriggerCriteria() {
		return triggerCriteria;
	}
	public void setTriggerCriteria(String triggerCriteria) {
		this.triggerCriteria = triggerCriteria;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getTriggerUsageOn() {
		return triggerUsageOn;
	}
	public void setTriggerUsageOn(String triggerUsageOn) {
		this.triggerUsageOn = triggerUsageOn;
	}
	
}
