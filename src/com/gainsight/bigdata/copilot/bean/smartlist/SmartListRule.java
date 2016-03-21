package com.gainsight.bigdata.copilot.bean.smartlist;

import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionTrueCase;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.TriggerCriteria;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.util.List;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartListRule {

    private String relatedId;
    private String ruleType = "ENGAGEMENT";
    private String description;
    private String triggerCriteria;  //This String can be parse to TriggerCriteria Class.
    private String sourceType = "Usage";
    private String triggerUsageOn = "ACCOUNTLEVEL";
    private List<ActionDetails> actionDetails;

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

    public List<ActionDetails> getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(List<ActionDetails> actionDetails) {
        this.actionDetails = actionDetails;
    }
}
