package com.gainsight.sfdc.rulesEngine.pojos;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 23/08/14
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;

/**
 * {"actionInfo":[{"conditionList":[{"name":"Name","value":"sdfgsdfg","objectName":"Account",
 *  "fieldType":"STRING","operator":"n","logicalOperator":"AND","fieldLabel":"Account::Account Name",
 *  "operatorLabel":"not equal to"},{"name":"Tag__c","value":"(Active^,^Churned^,^Established)",
 *  "objectName":"CustomerInfo__c","fieldType":"MULTIPICKLIST","operator":"u","logicalOperator":"AND",
 *  "fieldLabel":"CustomerInfo__c::Tag","operatorLabel":"includes"}],"actionList":[{"operation":"updateMetricScore",
 *  "metric":"a0gF0000004gYaVIAU","score":"a0hF0000003QswyIAC","comment":"li,kfvdcx;.l,kmjhngbvc"}]},
 *  {"conditionList":[],"actionList":[{"operation":"updateMetricScore","metric":"a0gF0000004gYaWIAU",
 *  "score":"a0hF0000003QswwIAC","comment":"'/p;.lk,jmbgfvcdfghjkilo;p/o.luikjhrgerrtherthertherth"}]}]}
 */
public class RuleScorecardCriteria {

    ArrayList<ActionInfo> actionInfo;

    public ArrayList<ActionInfo> getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(ArrayList<ActionInfo> actionInfo) {
        this.actionInfo = actionInfo;
    }

    public static class ActionInfo {
        @JsonProperty("conditionList")
        ArrayList<ConditionList> conditionList;
        @JsonProperty("actionList")
        ArrayList<ActionList> actionList;

        public ArrayList<ConditionList> getConditionList() {
            return conditionList;
        }

        public void setConditionList(ArrayList<ConditionList> conditionList) {
            this.conditionList = conditionList;
        }

        public ArrayList<ActionList> getActionList() {
            return actionList;
        }

        public void setActionList(ArrayList<ActionList> actionList) {
            this.actionList = actionList;
        }


    }

    public static class ConditionList {
        String name;
        String value;
        String objectName;
        String fieldType;
        String operator;
        String logicalOperator;
        String fieldLabel;
        String operatorLabel;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getObjectName() {
            return objectName;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getLogicalOperator() {
            return logicalOperator;
        }

        public void setLogicalOperator(String logicalOperator) {
            this.logicalOperator = logicalOperator;
        }

        public String getFieldLabel() {
            return fieldLabel;
        }

        public void setFieldLabel(String fieldLabel) {
            this.fieldLabel = fieldLabel;
        }

        public String getOperatorLabel() {
            return operatorLabel;
        }

        public void setOperatorLabel(String operatorLabel) {
            this.operatorLabel = operatorLabel;
        }
    }

    public static class ActionList {
        String operation;
        String metric;
        String score;
        String comment;

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
