package com.gainsight.sfdc.rulesEngine.pojos;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 23/08/14
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {"isAlertRuleType":"false","alertSeverity":"a0XF0000006JRkfMAG","alertType":"a0FF000000DX6ijMAD",
 * "alertStatus":"a0XF0000006JRjfMAG","alertReason":"a0XF0000006JRk9MAG",
 * "alertComment":"adfafdadfadfafdaf","alertSubject":"My First Rule."}
 */
public class RuleAlertCriteria {

    @JsonProperty("isAlertRuleType")
    String alertRuleType;

    String alertSeverity;
    String alertType;
    String alertStatus;
    String alertReason;
    String alertComment;
    String alertSubject;

    public String getAlertRuleType() {
        return alertRuleType;
    }

    public void setAlertRuleType(String alertRuleType) {
        this.alertRuleType = alertRuleType;
    }

    public String getAlertSeverity() {
        return alertSeverity;
    }

    public void setAlertSeverity(String alertSeverity) {
        this.alertSeverity = alertSeverity;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(String alertStatus) {
        this.alertStatus = alertStatus;
    }

    public String getAlertReason() {
        return alertReason;
    }

    public void setAlertReason(String alertReason) {
        this.alertReason = alertReason;
    }

    public String getAlertComment() {
        return alertComment;
    }

    public void setAlertComment(String alertComment) {
        this.alertComment = alertComment;
    }

    public String getAlertSubject() {
        return alertSubject;
    }

    public void setAlertSubject(String alertSubject) {
        this.alertSubject = alertSubject;
    }
}
