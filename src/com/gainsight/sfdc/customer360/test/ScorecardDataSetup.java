package com.gainsight.sfdc.customer360.test;
import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.accounts.tests.AccountDataSetup;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.SOQLUtil;

public class ScorecardDataSetup {

    TestEnvironment env;
    static boolean isPackageInstance = false;
    static String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";

    public ScorecardDataSetup() {
        TestEnvironment env = new TestEnvironment();
        isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    }

    public static void main(String[] args) {
    	ScorecardDataSetup setup = new ScorecardDataSetup();
    	TestEnvironment env=new TestEnvironment();
        AccountDataSetup accSetup = new AccountDataSetup();
        DataETL dataLoader = new DataETL();
        ObjectMapper mapper = new ObjectMapper();
        BaseTest bt=new BaseTest();
        /*
        //STEP 1 : Create External Ids in Account and all the Scorecard objects 
        System.out.println("STEP 1 : Create External Ids in Account and all the Scorecard objects");
        accSetup.createExtIdFieldOnAccount();
        setup.createExtIdFieldInScorecardObjects();
        System.out.println("STEP 1 : DONE!");
        
        
        //STEP 2 : Enable Scorecard in C360 sections and in admin Scorecard Config
        System.out.println("STEP 2 : Enable Scorecard in C360 sections");
        SFDCUtil sfdc=new SFDCUtil();
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/scorecard.apex", isPackageInstance);  
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Scorecard_config.apex", isPackageInstance);
        System.out.println("STEP 2 : DONE!");
             
        try{
        //STEP 3 :  Upsert the existing Account Fields with Account ext id
        System.out.println("STEP 3 :  Upsert the existing Account Fields with Account ext id");
        JobInfo job_Account= mapper.readValue(new File(env.basedir+"/resources/datagen/jobs/Job_Accounts.txt"), JobInfo.class);
        dataLoader.execute(job_Account);
        System.out.println("STEP 3: DONE!");
        
        //STEP 4 : Add accounts to customers and then Update the existing CustomerInfo records with CInfo External Id
        System.out.println("STEP 4 : Adding accounts to customers");
        dataLoader.cleanUp(bt.resolveStrNameSpace(CUSTOMER_INFO), null);
        JobInfo job_Cust=mapper.readValue(new File(env.basedir+"/resources/datagen/jobs/Job_Customers.txt"), JobInfo.class);
        dataLoader.execute(job_Cust);
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Update_CustomerInfo.apex", isPackageInstance);  
        System.out.println("STEP 4: DONE!");
         
        //STEP 5 : Insert Scorecard Scoring Scheme and definition
        System.out.println("STEP 5 : Insert Scorecard Scoring Scheme");
        JobInfo job_SCScheme= mapper.readValue(new File(env.basedir+"/resources/datagen/jobs/Scorecard/Job_SCScheme_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_SCScheme);
        System.out.println("STEP 5 : DONE!");
        System.out.println("STEP 5.1 : Insert Scorecard Scoring Scheme Definition");
        JobInfo job_SCScheme_Defn= mapper.readValue(new File(env.basedir+"/resources/datagen/jobs/Scorecard/Job_SCScheme_Defn_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_SCScheme_Defn);
        System.out.println("STEP 5.1 : DONE!");
        
       //STEP 6: Enable CustomerScheme as Numeric Scoring Scheme
        System.out.println("STEP 6: Enable CustomerScheme as Numeric Scoring Scheme");
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Scorecard_enable_numeric.apex", isPackageInstance);
        System.out.println("STEP 6 : DONE!");
        
        //STEP 7 : Create Scorecard Metrics
        System.out.println("STEP 7 : Create Scorecard Groups and Measures -  Metrics");
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Create_ScorecardMetrics.apex", isPackageInstance);
        System.out.println("STEP 7: DONE");
        
        //STEP 8 : Upload Scorecard Fact
        System.out.println("STEP 8 : Upload Scorecard Fact");
        JobInfo job_SCFact= mapper.readValue(new File(env.basedir+"/resources/datagen/jobs/Scorecard/Job_SCFact_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_SCFact);
        System.out.println("STEP 8 : DONE!");
        
        //STEP 9 : Upload Scorecard History
        System.out.println("STEP 9 : Upload Scorecard History - Skipped");
        /*JobInfo job_SCHistory= mapper.readValue(new File(env.basedir+"/resources/datagen/jobs/Job_SCHistory_Insert.txt"), JobInfo.class);
        dataLoader.execute(job_SCHistory);
        System.out.println("STEP 9 : DONE!");

        //STEP 10 : Upsert Customer Info with Scores and Scorecard Comments
        System.out.println("STEP 10 : Upsert Customer Info with Scores and Scorecard Comments");
        JobInfo job_CustInfo= mapper.readValue(new File(env.basedir+"/resources/datagen/jobs/Scorecard/Job_ScorecardCustomerInfo_Upsert.txt"), JobInfo.class);
        dataLoader.execute(job_CustInfo);
        System.out.println("STEP 10: DONE");
               
        }catch(Exception ex){
        	Report.logInfo(ex.getLocalizedMessage());
        	ex.printStackTrace();
        }
         */
    }

    public void createExtIdFieldInScorecardObjects() {

        CreateObjectAndFields cObjFields = new CreateObjectAndFields();
        String Scorecard_Scoring_Scheme = "JBCXM__ScoringScheme__c";
        String[] SCScheme_ExtId = new String[]{"SCScheme ExternalID"};

        String Scorecard_Scoring_Scheme_Defn = "JBCXM__ScoringSchemeDefinition__c";
        String[] SCScheme_Defn_ExtId = new String[]{"SCCheme Defn ExternalID"};

        String Scorecard_Fact ="JBCXM__ScorecardFact__c";
        String[] SCFact_ExtId=new String[]{"SCFact ExternalID"};
        
        String Scorecard_Metrics="JBCXM__ScorecardMetric__c";
        String[] SCMetric_ExtId=new String[]{"SCMetric ExternalID"};
        
        String Scorecard_History="JBCXM__ScorecardHistory__c";
        String[] SCHistory=new String[]{"SCHistory ExternalID"};
        
        String CustomerInfo_obj="JBCXM__CustomerInfo__c";
        String[] CInfo_ExtId=new String[]{"CInfo ExternalID"};
        
        try {
            cObjFields.createTextFields(removeNameSpace(Scorecard_Scoring_Scheme), SCScheme_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(Scorecard_Scoring_Scheme_Defn), SCScheme_Defn_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(Scorecard_Fact), SCFact_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(Scorecard_Metrics), SCMetric_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(Scorecard_History), SCHistory, true, true, true, false, false);
            cObjFields.createTextFields(removeNameSpace(CustomerInfo_obj), CInfo_ExtId, true, true, true, false, false);
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
