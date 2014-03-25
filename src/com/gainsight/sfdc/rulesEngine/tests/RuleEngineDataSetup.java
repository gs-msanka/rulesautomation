package com.gainsight.sfdc.rulesEngine.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        rds.loadUsageDataForRulesEngine("AccountMonthly");
    }
	//RuleEngineDataSetup setup = new RuleEngineDataSetup();
   
    public void loadUsageDataForRulesEngine(String type) throws IOException, InterruptedException{
    	
    	AccountDataSetup accSetup = new AccountDataSetup();
    	    DataETL dataLoader = new DataETL();
    	    ObjectMapper mapper = new ObjectMapper();
    	    SFDCUtil sfdc=new SFDCUtil();
        String USAGE_NAME = "JBCXM__UsageData__c";
        String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
        //bt.apex.runApex(resolveStrNameSpace(QUERY));
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/UsageData_Measures.apex", isPackageInstance);
     /*   if(type.equalsIgnoreCase("AccountMonthly")){
            sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Account_Level_Monthly.apex",isPackageInstance);
        }
        else if(type.equalsIgnoreCase("InstanceWeekly")){
        	sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Instance_Level_Weekly.apex", isPackageInstance);
        }*/
        	
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Load_Accounts.apex", isPackageInstance);
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/AddAccountsToCustomers.apex", isPackageInstance);
        JobInfo jobInfo=null;
        if(type.equalsIgnoreCase("AccountMonthly")){
        	 //bt.apex.runApexCodeFromFile(env.basedir+"/testdata/sfdc/RulesEngine/LoadUsageData_AccountMonthly.apex", isPackageInstance);
            sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Account_Level_Monthly.apex",isPackageInstance);
        	jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/Job_Account_Monthly.txt"), JobInfo.class);
            dataLoader.execute(jobInfo);
        }
        else if (type.equalsIgnoreCase("AccountWeekly")){
        	 sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Account_Level_Monthly.apex",isPackageInstance);
         	jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/Job_Account_Monthly.txt"), JobInfo.class);
             dataLoader.execute(jobInfo);
        }
        else if (type.contains("Instance")||type.contains("User")){
        	if(type.contains("Weekly"))
        	{
        	    Calendar c = Calendar.getInstance();
        	    Boolean isAggBatchsCompleted = false;
        		if(type.equalsIgnoreCase("InstanceWeekly")){
                	sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Instance_Level_Weekly.apex", isPackageInstance);
        			jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/Job_Instance_Weekly.txt"), JobInfo.class);
        		}
        		else if(type.equalsIgnoreCase("UserWeekly")){
                	sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_User_Level_Weekly.apex", isPackageInstance);
        		   	jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/Job_User_Weekly.txt"), JobInfo.class);
        		}
        		dataLoader.execute(jobInfo);
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
        		if(type.equalsIgnoreCase("InstanceMonthly")){
                	sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Instance_Level_Monthly.apex", isPackageInstance);
        			jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/Job_Instance_Monthly.txt"), JobInfo.class);
        		}
        		else if(type.equalsIgnoreCase("UserMonthly")){
                	sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_User_Level_Monthly.apex", isPackageInstance);
        			jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/Job_User_Monthly.txt"), JobInfo.class);
        		}
        	   dataLoader.execute(jobInfo);
               //sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Aggregation_Script.apex", isPackageInstance);
              /* String fileName = env.basedir+"/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
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
               }*/
        	}
        }
    }
    public void createRulesForRulesEngine() throws IOException{
    	 SFDCUtil sfdc=new SFDCUtil();
    	 sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/CreateRules.apex", isPackageInstance);
    }
	public void initialCleanUp() {
		// TODO Auto-generated method stub
		SFDCUtil sfdc = new SFDCUtil();
		sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/CleanUp.apex", isPackageInstance);
		
	}
}
