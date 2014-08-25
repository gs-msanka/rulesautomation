package com.gainsight.sfdc.rulesEngine.tests;

import com.gainsight.sfdc.accounts.tests.AccountDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.sforce.soap.partner.sobject.SObject;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RuleEngineDataSetup extends BaseTest {

	// load Usage Data
	// Create Rules as per data loaded

	static boolean isPackageInstance = false;

	public RuleEngineDataSetup() {
		isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
	}
	public static void main(String args[]){
		RuleEngineDataSetup rsd=new RuleEngineDataSetup();
		rsd.runRule("8/20/2014", "ACCOUNTLEVEL");
	}

	public void loadUsageDataForRulesEngine(String type,String fileName) throws IOException, InterruptedException {
		AccountDataSetup accSetup = new AccountDataSetup();
		DataETL dataLoader = new DataETL();
		ObjectMapper mapper = new ObjectMapper();
		SFDCUtil sfdc = new SFDCUtil();
		String USAGE_NAME = "JBCXM__UsageData__c";
		String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
		createExtIdFieldOnAccount();
		createFieldsOnUsageData();
		sfdc.runApexCodeFromFile(env.basedir+ "/apex_scripts/RulesEngine/UsageData_Measures.apex",isPackageInstance);
		dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
		sfdc.runApexCodeFromFile(env.basedir+ "/apex_scripts/RulesEngine/Load_Accounts_n_Customers.apex",isPackageInstance);

		JobInfo jobInfo = null;
		String isMonthly = "true", isWeekly = "false";
		if (type.contains("Weekly")) {
			isWeekly = "true";
			isMonthly = "false";
		}
		// Open job file..change weekly/monthly label , file name and write back
		// to a temp job file.
		BufferedReader reader;
		BufferedWriter writer;
		String inFile = env.basedir	+ "/resources/datagen/jobs/RulesEngine/Job_UsageData.txt";
		String outFile = env.basedir+ "/resources/datagen/jobs/RulesEngine/temp_job.txt";

		String line = null;
		String code = "";
		reader = new BufferedReader(new FileReader(inFile));
		writer = new BufferedWriter(new FileWriter(outFile));
		while ((line = reader.readLine()) != null) {
			line = line.replace("ISMONTHLY", isMonthly);
			line = line.replace("ISWEEKLY", isWeekly);
			line = line.replace("FILE_NAME", fileName);
			writer.write(line);
			writer.newLine();
		}
		reader.close();
		writer.close();
		jobInfo = mapper.readValue(resolveNameSpace(env.basedir	+ "/resources/datagen/jobs/RulesEngine/temp_job.txt"),JobInfo.class);
		dataLoader.execute(jobInfo);
		System.out.println("TYPE= "+type);
		if (type.contains("Instance")) {
			if (type.contains("Weekly"))
				runAdoptionAggregation(4,true,true,"Wed");
			else
				runAdoptionAggregation(4,false,false,"");
			System.out.println("Usage Data load completed....");
		}
	}
	
	public void createRulesForRulesEngine(String AdvanceCriteria,
			String AlertCount, String alertCriteriaJson, String SourceType,
			String TaskOwnerField, String triggerCriteriaJson,
			String TriggeredUsageOn) throws IOException {
		SFDCUtil sfdc = new SFDCUtil();
		sfdc.runApex(resolveStrNameSpace("List<JBCXM__AutomatedAlertRules__c>  rules =  new List<JBCXM__AutomatedAlertRules__c>();"
				+ "JBCXM__AutomatedAlertRules__c  rule = null; rule=new JBCXM__AutomatedAlertRules__c(JBCXM__AdvanceCriteria__c='"
				+ AdvanceCriteria+ "'"+ ",JBCXM__AlertCount__c=0 , JBCXM__AlertCriteria__c='"+ alertCriteriaJson+ "'"
				+ ",JBCXM__PlayBookIds__c=''"+ ",JBCXM__SourceType__c='"+ SourceType+ "'"+ ",JBCXM__Status__c=true"
				+ ",JBCXM__TaskDefaultOwner__c=Userinfo.getUserId()"+ ",JBCXM__TaskOwnerField__c='"	+ TaskOwnerField+ "'"
				+ ",JBCXM__TriggerCriteria__c='"+ triggerCriteriaJson+ "'"+ ",JBCXM__TriggeredUsageOn__c='"+ TriggeredUsageOn
				+ "');"	+ "rules.add(rule);" + "insert rules;"));
	}

	public void initialCleanUp() {
		SFDCUtil sfdc = new SFDCUtil();
		sfdc.runApexCodeFromFile(env.basedir+ "/apex_scripts/RulesEngine/CleanUp.apex", isPackageInstance);

	}

	public String getAlertCriteriaJson(String severity, String reason,
			String type, String status, String subject, String comments,String isAlert) {
		SObject[] alertSeverity = soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Severity' and Name='"+ severity + "'"));
		System.out.println("running Query: -- select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Reason' and Name='"+ reason + "'");
		SObject[] alertReason = soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Reason' and Name='"+ reason + "'"));
		SObject[] alertType = soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Type' and Name='"+ type + "'"));
		SObject[] alertStatus = soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Status' and Name='"+ status + "'"));
		return ("{\"alertSeverity\":\"" + alertSeverity[0].getId()
				+ "\",\"alertReason\":\"" + alertReason[0].getId()
				+ "\",\"alertType\":\"" + alertType[0].getId()
				+ "\",\"isAlertRuleType\":\""+isAlert
				+ "\",\"alertStatus\":\"" + alertStatus[0].getId()
				+ "\",\"alertSubject\":\"" + subject + "\"}");
	}

	public void runRule(String usageLevel) {
		SFDCUtil sfdc = new SFDCUtil();
		sfdc.runApex(resolveStrNameSpace(
				"Map<String,Object>  ruleParams=new Map<String,Object>();"
						+ "List<JBCXM__AutomatedAlertRules__c> RuleId=[select id from JBCXM__AutomatedAlertRules__c];"
						+ "String rId=RuleId.get(0).Id;"
						+ "ruleParams.put('ruleId',rId);"
						+ "ruleParams.put('ruleRunDate',"+getDatewithFormat(0,0));+"
						+ "ruleParams.put('isAlertCreate',true);"
						+ "ruleParams.put('usageLevel','"+usageLevel+"');"
						+ "ruleParams.put('criteriaList',new List<Object>());"
						+ "ruleParams.put('areaName','usageData');"
						+ "ruleParams.put('actionType','runRule');"
						+ "JBCXM.CEHandler.handleCall(ruleParams);"));

	}

	public void clearAlertsFromPreviousTest() {
		apex.runApex("delete [select id from JBCXM__AutomatedAlertRules__c]; delete [select id from JBCXM__Alert__c where JBCXM__Account__c in (select id from Account where name like 'Rules%')];",isPackageInstance);
	}
}
