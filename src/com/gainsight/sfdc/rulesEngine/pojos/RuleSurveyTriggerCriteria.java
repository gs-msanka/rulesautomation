package com.gainsight.sfdc.rulesEngine.pojos;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 05/09/14
 * Time: 8:47 PM
 * To change this template use File | Settings | File Templates.
 */


public  class RuleSurveyTriggerCriteria {
    @JsonProperty("Id")
    private String id;
    private String pId;
    private String operator = "AND";
    private AnswerRecords answerRecords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public AnswerRecords getAnswerRecords() {
        return answerRecords;
    }

    public void setAnswerRecords(AnswerRecords answerRecords) {
        this.answerRecords = answerRecords;
    }

    public  static class AnswerRecords {
        private String[] idList;
        private String operator = "OR";

        public String[] getIdList() {
            return idList;
        }

        public void setIdList(String[] idList) {
            this.idList = idList;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }
}
