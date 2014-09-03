package com.gainsight.sfdc.rulesEngine.pojos;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 02/09/14
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class AutomatedRule {
    private String Name;
    private String JBCXM__Description__c;
    private String JBCXM__ruleType__c;
    private String JBCXM__TriggeredUsageOn__c;
    private String JBCXM__SourceType__c;
    private String JBCXM__PlayBookIds__c;
    private String JBCXM__TaskDefaultOwner__c;
    private String JBCXM__TriggerCriteria__c;
    private String JBCXM__AlertCriteria__c;
    private String JBCXM__AdvanceCriteria__c;
    private String JBCXM__ScorecardCriteria__c;
    private String JBCXM__SelectFields__c;
    private Boolean JBCXM__Status__c = true;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getJBCXM__Description__c() {
        return JBCXM__Description__c;
    }

    public void setJBCXM__Description__c(String JBCXM__Description__c) {
        this.JBCXM__Description__c = JBCXM__Description__c;
    }

    public String getJBCXM__ruleType__c() {
        return JBCXM__ruleType__c;
    }

    public void setJBCXM__ruleType__c(String JBCXM__ruleType__c) {
        this.JBCXM__ruleType__c = JBCXM__ruleType__c;
    }

    public String getJBCXM__TriggeredUsageOn__c() {
        return JBCXM__TriggeredUsageOn__c;
    }

    public void setJBCXM__TriggeredUsageOn__c(String JBCXM__TriggeredUsageOn__c) {
        this.JBCXM__TriggeredUsageOn__c = JBCXM__TriggeredUsageOn__c;
    }

    public String getJBCXM__SourceType__c() {
        return JBCXM__SourceType__c;
    }

    public void setJBCXM__SourceType__c(String JBCXM__SourceType__c) {
        this.JBCXM__SourceType__c = JBCXM__SourceType__c;
    }

    public String getJBCXM__PlayBookIds__c() {
        return JBCXM__PlayBookIds__c;
    }

    public void setJBCXM__PlayBookIds__c(String JBCXM__PlayBookIds__c) {
        this.JBCXM__PlayBookIds__c = JBCXM__PlayBookIds__c;
    }

    public String getJBCXM__TaskDefaultOwner__c() {
        return JBCXM__TaskDefaultOwner__c;
    }

    public void setJBCXM__TaskDefaultOwner__c(String JBCXM__TaskDefaultOwner__c) {
        this.JBCXM__TaskDefaultOwner__c = JBCXM__TaskDefaultOwner__c;
    }

    public String getJBCXM__TriggerCriteria__c() {
        return JBCXM__TriggerCriteria__c;
    }

    public void setJBCXM__TriggerCriteria__c(String JBCXM__TriggerCriteria__c) {
        this.JBCXM__TriggerCriteria__c = JBCXM__TriggerCriteria__c;
    }

    public String getJBCXM__AlertCriteria__c() {
        return JBCXM__AlertCriteria__c;
    }

    public void setJBCXM__AlertCriteria__c(String JBCXM__AlertCriteria__c) {
        this.JBCXM__AlertCriteria__c = JBCXM__AlertCriteria__c;
    }

    public String getJBCXM__AdvanceCriteria__c() {
        return JBCXM__AdvanceCriteria__c;
    }

    public void setJBCXM__AdvanceCriteria__c(String JBCXM__AdvanceCriteria__c) {
        this.JBCXM__AdvanceCriteria__c = JBCXM__AdvanceCriteria__c;
    }

    public String getJBCXM__ScorecardCriteria__c() {
        return JBCXM__ScorecardCriteria__c;
    }

    public void setJBCXM__ScorecardCriteria__c(String JBCXM__ScorecardCriteria__c) {
        this.JBCXM__ScorecardCriteria__c = JBCXM__ScorecardCriteria__c;
    }

    public String getJBCXM__SelectFields__c() {
        return JBCXM__SelectFields__c;
    }

    public void setJBCXM__SelectFields__c(String JBCXM__SelectFields__c) {
        this.JBCXM__SelectFields__c = JBCXM__SelectFields__c;
    }

    public Boolean getJBCXM__Status__c() {
        return JBCXM__Status__c;
    }

    public void setJBCXM__Status__c(Boolean JBCXM__Status__c) {
        this.JBCXM__Status__c = JBCXM__Status__c;
    }
}
