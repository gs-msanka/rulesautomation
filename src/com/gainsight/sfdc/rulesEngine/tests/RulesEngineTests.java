package com.gainsight.sfdc.rulesEngine.tests;

import java.io.IOException;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sforce.soap.partner.sobject.SObject;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.sfdc.rulesEngine.tests.*;
public class RulesEngineTests extends BaseTest {
	private static final String TEST_DATA_FILE = "testdata/sfdc/RulesEngine/RulesEngineTests.xls";
	RuleEngineDataSetup rSetup= new RuleEngineDataSetup();
	RulesEngineDataValidation rValidate = new RulesEngineDataValidation();
	
	
	@BeforeClass
	public void setUp() {
		//1. load usage data
		//basepage.login();
		try{
				//rSetup.initialCleanUp();
				//rSetup.createRulesForRulesEngine();
				//rSetup.loadUsageDataForRulesEngine("InstanceMonthly");
		}catch (Exception ex){
			System.out.println(ex.getLocalizedMessage());
		}
		
		//2. Create Rules From Backend
		
	}
	 
	@AfterMethod
	private void refresh() {
	        basepage.refreshPage();
	    }
	 
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R1")
	public void Rule1(HashMap<String, String> testData) throws IOException, InterruptedException {
		HashMap<String, String> alertCriteria = getMapFromData(testData.get("AlertCriteria"));
		HashMap<String,String> tc =getMapFromData(testData.get("RuleCriteria"));
		 SObject[] alertSeverity=soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Severity' and Name='"+alertCriteria.get("alertSeverity")+"'"));
		 SObject[] alertReason=soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Reason' and Name='"+alertCriteria.get("alertReason")+"'"));
		 SObject[] alertType=soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Type' and Name='"+alertCriteria.get("alertType")+"'"));
		 SObject[] alertStatus=soql.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Status' and Name='"+alertCriteria.get("alertStatus")+"'"));

		 String AdvanceCriteria="{\"filterLogic\":\""+alertCriteria.get("filterLogic")+"\",\"filterCriteria\":[]}";
		 String alertCriteriaJson="{\"alertSeverity\":\""+alertSeverity[0].getId()+"\",\"alertReason\":\""+alertReason[0].getId()+"\",\"alertType\":\""+alertType[0].getId()+"\",\"alertStatus\":\""+alertStatus[0].getId()+"\",\"alertComment\":\""+alertCriteria.get("alertComment")+"\",\"alertSubject\":\""+alertCriteria.get("alertSubject")+"\"}";
		 String triggerCriteriaJson="[{\"valueRef\":"+(tc.get("valueRef").equals("")?"null":"\""+tc.get("valueRef")+"\"}")+",\"type\":\""+tc.get("type")+"\",\"trendParam\":{\"traillingMonths\":"+tc.get("trendParams-traillingMonths")+",\"isByMonth\":"+(tc.get("trendParams-isByMonth").equals("true")?"true":"false")+",\"operation\":\""+tc.get("trendParams-operation")+"\",\"name\":\""+tc.get("trendParams-name")+"\"},\"value\":"+tc.get("value")+",\"honorNull\":"+tc.get("honorNull")+",\"name\":\""+tc.get("name")+"\",\"logicalOperator\":\""+tc.get("logicalOperator")+"\",\"operator\":\""+tc.get("operator")+"\"}]";
		 System.out.println("alert criteria:"+alertCriteriaJson);
		 System.out.println("rule criteria:"+triggerCriteriaJson);
		 System.out.println("Advance criteria:"+AdvanceCriteria);
		 rSetup.initialCleanUp();
		 rSetup.loadUsageDataForRulesEngine(getMapFromData(testData.get("UsageData")).get("type"),true);
		 rSetup.createRulesForRulesEngine(AdvanceCriteria,alertCriteria.get("AlertCount"),alertCriteriaJson,alertCriteria.get("SourceType"),alertCriteria.get("TaskOwnerField"),triggerCriteriaJson,alertCriteria.get("TriggeredUsageOn"));
		 rSetup.loadUsageDataForRulesEngine(getMapFromData(testData.get("UsageData")).get("type"),false);
		
		 rValidate.checkAlertsCreated(alertCriteria);	 
	}
}
