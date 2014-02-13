package com.gainsight.sfdc.survey.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;

public class SurveyDataSetup  {

    TestEnvironment env;
    boolean isPackageInstance = false;
    public SurveyDataSetup() {
        TestEnvironment env = new TestEnvironment();
        isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));

    }

    public static void main(String[] args) {
        SurveyDataSetup setup = new SurveyDataSetup();
        setup.createExtIdFieldsInSurvey();
    }

    public void createExtIdFieldsInSurvey() {

        CreateObjectAndFields cObjFields = new CreateObjectAndFields();
        String surMaster_Obj = "JBCXM__Survey__c";
        String[] surMaster_ExtId = new String[]{"SurMaster ExternalID"};

        String surQues_Obj = "JBCXM__SurveyQuestion__c";
        String[] surQues_ExtId = new String[]{"SurQues ExternalID"};

        String surAllowAns_Obj = "JBCXM__SurveyAllowedAnswers__c";
        String[] surAllowAns_ExtId = new String[]{"SurAllowAns ExternalID"};

        String surDisSch_Obj = "JBCXM__SurveyDistributionSchedule__c";
        String[] surDisSch_ExtId = new String[]{"SurDisSch ExternalID"};

        String surPart_Obj = "JBCXM__SurveyParticipant__c";
        String[] surPart_ExtId = new String[]{"SurPart ExternalID"};

        String surUserAns_Obj = "JBCXM__SurveyUserAnswer__c";
        String[] surUserAns_ExtId = new String[]{"SurUserAns ExternalID"};

        String surResTask_Obj = "JBCXM__SurveyResponseTask__c";
        String[] surResTask_ExtId = new String[]{"SurResTask ExternalID"};

        String surLogicRules_Obj = "JBCXM__SurveyLogicRules__c";
        String[] surLogicRules_ExtId = new String[]{"surLogicRules ExternalID"};


        try {
            cObjFields.createTextFields(removeNameSpace(surMaster_Obj), surMaster_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surQues_Obj), surQues_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surAllowAns_Obj), surAllowAns_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surDisSch_Obj), surDisSch_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surPart_Obj), surPart_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surUserAns_Obj), surUserAns_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surResTask_Obj), surResTask_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surLogicRules_Obj), surLogicRules_ExtId, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create fields");
            e.printStackTrace();
        }

    }


    public String removeNameSpace(String s) {
        if(!isPackageInstance) {
            return s.replaceAll("JBCXM__", "");
        }
        return s;
    }
}
