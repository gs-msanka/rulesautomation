package com.gainsight.sfdc.rulesEngine.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import com.gainsight.sfdc.adoption.tests.*;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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
   
    public void loadUsageDataForRulesEngine(String type) throws IOException{
    	
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
        if(type.equalsIgnoreCase("AccountMonthly")){
            sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Set_Account_Level_Monthly.apex",isPackageInstance);
        }
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Load_Accounts.apex", isPackageInstance);
        sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/AddAccountsToCustomers.apex", isPackageInstance);
        if(type.equalsIgnoreCase("AccountMonthly")){
        	 //bt.apex.runApexCodeFromFile(env.basedir+"/testdata/sfdc/RulesEngine/LoadUsageData_AccountMonthly.apex", isPackageInstance);
        	JobInfo jobInfo = mapper.readValue(resolveNameSpace(env.basedir + "/resources/datagen/jobs/RulesEngine/Job_Account_Monthly.txt"), JobInfo.class);
            dataLoader.execute(jobInfo);
        }
        else if(type.equalsIgnoreCase("InstanceMonthly")){
       	 sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/LoadUsageData_InstanceMonthly.apex", isPackageInstance);
    	 sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/Aggregation_Script.apex", isPackageInstance);
        }
    }
    
    public void createRulesForRulesEngine() throws IOException{
    	 SFDCUtil sfdc=new SFDCUtil();
    	 sfdc.runApexCodeFromFile(env.basedir+"/apex_scripts/RulesEngine/CreateRules.apex", isPackageInstance);
    }
}
