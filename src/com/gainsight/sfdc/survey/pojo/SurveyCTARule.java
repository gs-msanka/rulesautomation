package com.gainsight.sfdc.survey.pojo;

import com.gainsight.sfdc.workflow.pojos.CTA;

import java.util.List;

/**
 * Created by gainsight on 13/03/15.
 */
public class SurveyCTARule {
    private String status;
    private CTA cta;
    private String ownerField;
    private String playbook;
    private String chatterUpdate;
    private List<SurveyQuestion> surveyQuestions;
    private String whereLogic;
    private String advanceField;
    private String advanceOperator;
    private String advanceValue;
    

    public String getAdvanceField() {
        return advanceField;
    }

    public void setAdvanceField(String advanceField) {
        this.advanceField = advanceField;
    }
    
    public String getAdvanceOperator() {
        return advanceOperator;
    }

    public void setAdvanceOperator(String advanceOperator) {
        this.advanceOperator = advanceOperator;
    }
    
    public String getAdvanceValue() {
        return advanceValue;
    }

    public void setAdvanceValue(String advanceValue) {
        this.advanceValue = advanceValue;
    }
    
    public String getChatterUpdate() {
        return chatterUpdate;
    }

    public void setChatterUpdate(String chatterUpdate) {
        this.chatterUpdate = chatterUpdate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CTA getCta() {
        return cta;
    }

    public void setCta(CTA cta) {
        this.cta = cta;
    }

    public String getOwnerField() {
        return ownerField;
    }

    public void setOwnerField(String ownerField) {
        this.ownerField = ownerField;
    }

    public String getPlaybook() {
        return playbook;
    }

    public void setPlaybook(String playbook) {
        this.playbook = playbook;
    }

    public List<SurveyQuestion> getSurveyQuestions() {
        return surveyQuestions;
    }

    public void setSurveyQuestions(List<SurveyQuestion> surveyQuestions) {
        this.surveyQuestions = surveyQuestions;
    }

    public String getWhereLogic() {
        return whereLogic;
    }

    public void setWhereLogic(String whereLogic) {
        this.whereLogic = whereLogic;
    }
}
