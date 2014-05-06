package com.gainsight.sfdc.rulesEngine.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import com.gainsight.sfdc.adoption.tests.*;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.accounts.tests.AccountDataSetup;
import com.gainsight.sfdc.survey.tests.SurveyDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;

public class RuleEngineDataSetup extends BaseTest {
	
	// load Usage Data
	// Create Rules as per data loaded

    static boolean isPackageInstance = false;
    public RuleEngineDataSetup() {
        isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    }
   // BaseTest bt =new BaseTest();
    public static void main(String[] args) throws InterruptedException, IOException {
    	
        RuleEngineDataSetup rds=new RuleEngineDataSetup();
        rds.loadUsageDataForRulesEngine("InstanceMonthly",true);
    }
	//RuleEngineDataSetup setup = new RuleEngineDataSetup();
   
    public void loadUsageDataForRulesEngine(String type,boolean isFirst) throws IOException, InterruptedException{
    	AccountDataSetup accSetup = new AccountDataSetup();
	    DataETL dataLoader = new DataETL();
	    ObjectMapper mapper = new ObjectMapper();
	    SFDCUtil sfdc=new SFDCUtil();
    	if(isFirst)
    	{
        String USAGE_NAME = "JBCXM__UsageData__c";
        String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
        //bt.apex.runApex(resolveStrNameSpace(QUERY));
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/UsageData_Measures.apex", isPackageInstance);
        	
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Load_Accounts.apex", isPackageInstance);
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/AddAccountsToCustomers.apex", isPackageInstance);
    	}
        JobInfo jobInfo=null;
        String isMonthly="",isWeekly="",file_name="";
        if(type.contains("Weekly"))      {	isWeekly="true";	isMonthly="false";       }
        else if (type.contains("Monthly"))  { 	isWeekly="false"; 	isMonthly="true";    }
        if(type.equalsIgnoreCase("AccountMonthly")){
        	if(isFirst) {
        		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Account_Level_Monthly.apex",isPackageInstance);
        		file_name="UsageData_Account_Monthly1";
        	}        	
        	else   		file_name="UsageData_Account_Monthly2";
        }
        else if (type.equalsIgnoreCase("AccountWeekly")){
        	if(isFirst)	{
        		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Account_Level_Weekly.apex",isPackageInstance);
             	file_name="UsageData_Account_Weekly1";
        	}
        	else   		file_name="UsageData_Account_Weekly2";
        }
        else if (type.equalsIgnoreCase("InstanceMonthly")){
        	if(isFirst) {
        		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Instance_Level_Monthly.apex", isPackageInstance);
            	file_name="UsageData_Account_Monthly1";
        	}
        	else   file_name="UsageData_Account_Monthly2";
        }
        else if (type.equalsIgnoreCase("InstanceWeekly")){
        	if(isFirst) {
        		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Instance_Level_Weekly.apex", isPackageInstance);
            	file_name="UsageData_Instance_Weekly1";
        	}
        	else   		file_name="UsageData_Instance_Weekly2";
        }
        else if (type.equalsIgnoreCase("UserMonthly")){
        	if(isFirst) {
        		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_User_Level_Monthly.apex", isPackageInstance);
            	file_name="UsageData_User_Monthly1";
        	}
        	else   		file_name="UsageData_User_Monthly2";
        }
        else if (type.equalsIgnoreCase("UserWeekly")){
        	if(isFirst) {
        		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_User_Level_Weekly.apex", isPackageInstance);
            	file_name="UsageData_User_Weekly1";
        	}
        	else   	file_name="UsageData_User_Weekly2";
        }
        
        //Open job file..change weekly/monthly label , file name and write back to a temp job file.
        BufferedReader reader;
        BufferedWriter writer;
        String inFile = env.basedir+"/resources/datagen/jobs/RulesEngine/Job_UsageData.txt";
        String outFile = env.basedir+"/resources/datagen/jobs/RulesEngine/temp_job.txt";

        String line     = null;
        String code     = "";
        reader          = new BufferedReader(new FileReader(inFile));
        writer			= new BufferedWriter(new FileWriter(outFile));
        while ((line = reader.readLine()) != null) {
   
        	line=line.replace("ISMONTHLY", isMonthly);
        	line=line.replace("ISWEEKLY", isWeekly);
        	line=line.replace("FILE_NAME", file_name);
            writer.write(line);
            writer.newLine();
        }
        reader.close();
        writer.close();
		jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/temp_job.txt"), JobInfo.class);
		dataLoader.execute(jobInfo);
		
		if(type.contains("Instance")||type.contains("User"))
		{
        if(type.contains("Weekly"))
		performAggregation("Weekly");
        else performAggregation("Monthly");
        System.out.println("Usage Data load completed....");
        }
    }
    	private void performAggregation(String type) throws IOException, InterruptedException {
    	 if(type.contains("Weekly"))
     	{
     	    Calendar c = Calendar.getInstance();
     	    Boolean isAggBatchsCompleted = false;
     		BufferedReader reader;
             String fileName = env.basedir+"/apex_scripts/RulesEngine/Aggregation_Script.apex";
             String line     = null;
             String code     = "";
             reader          = new BufferedReader(new FileReader(fileName));
             StringBuilder stringBuilder = new StringBuilder();
             while ((line = reader.readLine()) != null) {
                 stringBuilder.append(line).append("\n");
             }
             reader.close();
             int year, month, day;
             String dateStr;
             //Max of only 5 jobs can run in an organization at a given time
             //Care to be taken that there are no apex jobs are running in the organization.
             int i= -7;
             for(int k = 0; k< 5;k++) {
                 for(int m=0; m < 5; m++, i=i-7) {
                     //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                     // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                     dateStr     = getWeekLabelDate("Wed", i, true, false);
                     System.out.println(dateStr);
                     year        = (dateStr != null && dateStr.split("\\|").length > 0) ? Integer.valueOf(dateStr.split("\\|")[0]) : c.get(Calendar.YEAR);
                     month       = (dateStr != null && dateStr.split("\\|").length > 1) ? Integer.valueOf(dateStr.split("\\|")[1]) : c.get(Calendar.MONTH);
                     day         = (dateStr != null && dateStr.split("\\|").length > 2) ? Integer.valueOf(dateStr.split("\\|")[2]) : c.get(Calendar.DATE);
                     code        = stringBuilder.toString();
                     code        = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                             .replaceAll("THEYEARCHANGE", String.valueOf(year))
                             .replace("THEDAYCHANGE", String.valueOf(day));
                     apex.runApex(resolveStrNameSpace(code));
                 }
                 for(int l= 0; l < 200; l++) {
                     String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                             "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                             "and ApexClass.Name = 'AdoptionAggregation'";
                     int noOfRunningJobs = getQueryRecordCount(query);
                     if(noOfRunningJobs==0) {
                         Report.logInfo("Aggregate Jobs are finished.");
                         isAggBatchsCompleted = true;
                         break;
                     } else {
                         Report.logInfo("Waiting for aggregation batch to complete");
                         Thread.sleep(30000L);
                     }
                 }
             }
     	}
     	
     	else if(type.contains("Monthly")){
     		Calendar c = Calendar.getInstance();
       	    Boolean isAggBatchsCompleted = false;
            //sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Aggregation_Script.apex", isPackageInstance);
            String fileName = env.basedir+"/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String code = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            int day = 15;
            //Max of only 5 jobs can run in an organization at a given time
            //Care to be taken that there are no apex jobs are running in the organization.
            for(int k=0;k<1;k++) {
                for(int i =0; i < 5; i++) {
                    if(month == 0) {
                        month = 12;
                        year = year -1;
                    }
                    code = stringBuilder.toString();
                    code = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                            .replaceAll("THEYEARCHANGE", String.valueOf(year))
                            .replace("THEDAYCHANGE", String.valueOf(day));
                    apex.runApex(code);
                    month = month-1; //Need to move backward for executing the aggregation.
                }
                reader.close();
                Thread.sleep(30000L);
                for(int i= 0; i < 200; i++) {
                    String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                            "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                            "and ApexClass.Name = 'AdoptionAggregation'";
                    int noOfRunningJobs = getQueryRecordCount(query);
                    if(noOfRunningJobs==0) {
                        Report.logInfo("Aggregate Jobs are finished.");
                        isAggBatchsCompleted = true;
                        break;
                    } else {
                        Report.logInfo("Waiting");
                        Thread.sleep(30000L);
                    }
                }
            }
     	}
		
	}
	public void createRulesForRulesEngine(String AdvanceCriteria,String AlertCount,String alertCriteriaJson,String SourceType,String TaskOwnerField,String triggerCriteriaJson,String TriggeredUsageOn) throws IOException{
    	 SFDCUtil sfdc=new SFDCUtil();
    	 sfdc.runApex(resolveStrNameSpace("List<JBCXM__AutomatedAlertRules__c>  rules =  new List<JBCXM__AutomatedAlertRules__c>();"
    			 	  +"JBCXM__AutomatedAlertRules__c  rule = null;"
    			 	  +"rule=new JBCXM__AutomatedAlertRules__c(JBCXM__AdvanceCriteria__c='"+AdvanceCriteria+"'"
    			 	  										 +",JBCXM__AlertCount__c="+AlertCount+""
    			 	  										 +",JBCXM__AlertCriteria__c='"+alertCriteriaJson+"'"
    			 	  										 +",JBCXM__PlayBookIds__c=''"
    			 	  										 +",JBCXM__SourceType__c='"+SourceType+"'"
    			 	  										 +",JBCXM__Status__c=true"
    			 	  										 +",JBCXM__TaskDefaultOwner__c=Userinfo.getUserId()"
    			 	  										 +",JBCXM__TaskOwnerField__c='"+TaskOwnerField+"'"
    			 	  										 +",JBCXM__TriggerCriteria__c='"+triggerCriteriaJson+"'"
    			 	  										 +",JBCXM__TriggeredUsageOn__c='"+TriggeredUsageOn+"');"
    			 	 +"rules.add(rule);"
    			 	 +"insert rules;"
    			 	));
    }
	public void initialCleanUp() {
		// TODO Auto-generated method stub
		SFDCUtil sfdc = new SFDCUtil();
		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/CleanUp.apex", isPackageInstance);
		
	}
	public void loadInitialUsageDataForRulesEngine(String string) {
		// TODO Auto-generated method stub
		
	}
}


