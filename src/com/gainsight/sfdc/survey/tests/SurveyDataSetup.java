package com.gainsight.sfdc.survey.tests;

import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.accounts.tests.AccountDataSetup;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import com.gainsight.sfdc.tests.BaseTest;

public class SurveyDataSetup  {

    TestEnvironment env;
    static boolean isPackageInstance = false;
    public SurveyDataSetup() {
        TestEnvironment env = new TestEnvironment();
        isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));

    }

    public static void main(String[] args) {
        SurveyDataSetup setup = new SurveyDataSetup();
        AccountDataSetup accSetup = new AccountDataSetup();
        DataETL dataLoader = new DataETL();
        ObjectMapper mapper = new ObjectMapper();
        BaseTest bt=new BaseTest();
        
        //STEP 1 : Create External Ids in Account and all the survey objects 
        System.out.println("STEP 1 : Create External Ids in Account and all the survey objects");
        accSetup.createExtIdFieldOnAccount();
        setup.createExtIdFieldInContact();
        setup.createExtIdFieldsInSurvey();
        System.out.println("STEP 1 : DONE!");
        
        //STEP 2 : Run Survey Creation Scripts
        System.out.println("STEP 2 : Run Survey Creation Scripts");
        SFDCUtil sfdc=new SFDCUtil();
        sfdc.runApexCodeFromFile("./apex_scripts/Surveys/Anonymous_Acc_Tracking.apex", isPackageInstance);  
        sfdc.runApexCodeFromFile("./apex_scripts/Surveys/Anonymous_No_Acc_Tracking.apex", isPackageInstance);
        sfdc.runApexCodeFromFile("./apex_scripts/Surveys/Non_Anonymous_survey.apex", isPackageInstance);
        System.out.println("STEP 2 : DONE!");
        
        try{
        //STEP 3 :  Upsert the existing Account Fields with Account ext id
        System.out.println("STEP 3 :  Upsert the existing Account Fields with Account ext id");
        JobInfo job_Account= mapper.readValue(bt.resolveNameSpace("./resources/datagen/jobs/Job_Survey_Accounts_Upsert.txt"), JobInfo.class);
        dataLoader.execute(job_Account);
        System.out.println("STEP 3: DONE!");
        
        //STEP 4 : Insert Contacts with Ext Ids for few of the above accounts
        System.out.println("STEP 4 : Insert Contacts with Ext Ids for few of the above accounts");
        JobInfo job_Contacts= mapper.readValue(bt.resolveNameSpace("./resources/datagen/jobs/Job_Survey_Contacts_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_Contacts);
        System.out.println("STEP 4 : DONE!");
        
        //STEP 5 : Add contacts to survey by loading Survey Participants
        System.out.println("STEP 5 : Add contacts to survey by loading Survey Participants");
        JobInfo job_SurPart= mapper.readValue(bt.resolveNameSpace("./resources/datagen/jobs/Job_Survey_Participants_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_SurPart);
        System.out.println("STEP 5 : DONE!");
        
/*        //STEP 6: Create NPS SurveyMaster
        sfdc.runApexCodeFromFile("./apex_scripts/Surveys/NonAnonymous_NPSSurveyMaster.apex", isPackageInstance);
        
        //STEP 7 : Upload NPS Survey Response
        System.out.println("STEP 7 : Upload NPS Survey Response");
        JobInfo job_NPSSurResp= mapper.readValue(bt.resolveNameSpace("./resources/datagen/jobs/Job_NPSSurveyResponse_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_NPSSurResp);
        System.out.println("STEP 7 : DONE!");
*/
        //STEP 8 : Upload Survey User Responses
        System.out.println("STEP 8 : Upload Survey User Responses");
        JobInfo job_SurUserAns= mapper.readValue(bt.resolveNameSpace("./resources/datagen/jobs/Job_SurveyUserAnswers_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_SurUserAns);
        System.out.println("STEP 8: DONE");
        
        }catch(Exception ex){
        	Report.logInfo(ex.getLocalizedMessage());
        	ex.printStackTrace();
        }
      
    }
    public void createExtIdFieldInContact(){
    	CreateObjectAndFields cObjFields = new CreateObjectAndFields();
        String Contact_Obj = "Contact";
        String[] contact_ExtId = new String[]{"Contact ExternalId"};
        try {
            cObjFields.createTextFields(removeNameSpace(Contact_Obj), contact_ExtId, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create fields");
            e.printStackTrace();
        }
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
        
        String NPS_SurveyResponse_Obj = "JBCXM__SurveyResponse__c";
        String[] NPSSurResp_ExtId = new String[]{"NPSSurResp ExternalID"};
        
        String NPS_SurveyMaster_Obj = "JBCXM__SurveyMaster__c";
        String[] NPS_SurMaster_ExtId = new String[]{"NPSSurMast ExternalID"};


        try {
            cObjFields.createTextFields(removeNameSpace(surMaster_Obj), surMaster_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surQues_Obj), surQues_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surAllowAns_Obj), surAllowAns_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surDisSch_Obj), surDisSch_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surPart_Obj), surPart_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surUserAns_Obj), surUserAns_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surResTask_Obj), surResTask_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(surLogicRules_Obj), surLogicRules_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(NPS_SurveyResponse_Obj), NPSSurResp_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(NPS_SurveyMaster_Obj), NPS_SurMaster_ExtId, true, true, true, false, false);
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
