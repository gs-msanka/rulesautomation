package com.gainsight.bigdata.rulesengine.bean;

import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInfo;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.TriggerCriteria;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class Rule {

    private String relatedId;
    private String ruleType;
    private String description;
    private TriggerCriteria triggerCriteria;
    private String sourceType;
    private String triggerUsageOn;
    private List<ActionInfo> actionInfoList;

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

    public TriggerCriteria getTriggerCriteria() {
        return triggerCriteria;
    }

    public void setTriggerCriteria(TriggerCriteria triggerCriteria) {
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

    public List<ActionInfo> getActionInfoList() {
        return actionInfoList;
    }

    public void setActionInfoList(List<ActionInfo> actionInfoList) {
        this.actionInfoList = actionInfoList;
    }
}
