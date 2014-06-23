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
	private static final String USAGEDATA_FILE = "";
	RuleEngineDataSetup rSetup = new RuleEngineDataSetup();
	RulesEngineDataValidation rValidate = new RulesEngineDataValidation();

	@BeforeClass
	public void setUp() {
		// 1. load usage data
		// basepage.login();
		try {
			rSetup.initialCleanUp();
			// rSetup.createRulesForRulesEngine();
			//rSetup.loadUsageDataForRulesEngine("InstanceMonthly", true,
			//		USAGEDATA_FILE);
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}

		// 2. Create Rules From Backend

	}

	@AfterMethod
	private void refresh() {
		basepage.refreshPage();
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R1")
	public void Rule1(HashMap<String, String> testData) throws IOException,
			InterruptedException {
		HashMap<String, String> alertCriteria = getMapFromData(testData
				.get("AlertCriteria"));
		HashMap<String, String> tc = getMapFromData(testData
				.get("RuleCriteria"));

		// Directly giving Advance and trigger criteria as json...instead of constructing the json here.
		// Goto Admin->rules engine and create a rule..then go to object AutomatedAlertRules and get the jsons..and give them in the testdata for first time.
		String alertCriteriaJson = rSetup.getAlertCriteriaJson(
				alertCriteria.get("alertSeverity"),
				alertCriteria.get("alertReason"),
				alertCriteria.get("alertType"),
				alertCriteria.get("alertStatus"),
				alertCriteria.get("alertSubject"),
				alertCriteria.get("alertComments"));
		
		System.out.println("alert criteria:" + alertCriteriaJson);
		rSetup.initialCleanUp();
		System.out.println("Clean up done! Loading fresh data....");
		 rSetup.loadUsageDataForRulesEngine(
		 getMapFromData(testData.get("UsageData")).get("type"), true ,
		 getMapFromData(testData.get("UsageFiles")).get("First"));
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		 rSetup.loadUsageDataForRulesEngine(
		 getMapFromData(testData.get("UsageData")).get("type"),
		 false,getMapFromData(testData.get("UsageFiles")).get("Second"));

		Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R2")
	public void Rule2(HashMap<String, String> testData) throws IOException,
			InterruptedException {
		HashMap<String, String> alertCriteria = getMapFromData(testData
				.get("AlertCriteria"));
		HashMap<String, String> tc = getMapFromData(testData
				.get("RuleCriteria"));
		SObject[] alertSeverity = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Severity' and Name='"
						+ alertCriteria.get("alertSeverity") + "'"));
		SObject[] alertReason = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Reason' and Name='"
						+ alertCriteria.get("alertReason") + "'"));
		SObject[] alertType = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Type' and Name='"
						+ alertCriteria.get("alertType") + "'"));
		SObject[] alertStatus = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Status' and Name='"
						+ alertCriteria.get("alertStatus") + "'"));

		String alertCriteriaJson = "{\"alertSeverity\":\""
				+ alertSeverity[0].getId() + "\",\"alertReason\":\""
				+ alertReason[0].getId() + "\",\"alertType\":\""
				+ alertType[0].getId() + "\",\"alertStatus\":\""
				+ alertStatus[0].getId() + "\",\"alertComment\":\""
				+ alertCriteria.get("alertComment") + "\",\"alertSubject\":\""
				+ alertCriteria.get("alertSubject") + "\"}";
		System.out.println("alert criteria:" + alertCriteriaJson);
		rSetup.initialCleanUp();
		System.out.println("Clean up done! Loading fresh data....");
		rSetup.loadUsageDataForRulesEngine(
				getMapFromData(testData.get("UsageData")).get("type"), true,
				getMapFromData(testData.get("UsageFiles")).get("First"));
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.loadUsageDataForRulesEngine(
				getMapFromData(testData.get("UsageData")).get("type"), false,
				getMapFromData(testData.get("UsageFiles")).get("Second"));

		Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R3")
	public void Rule3(HashMap<String, String> testData) throws IOException,
			InterruptedException {
		HashMap<String, String> alertCriteria = getMapFromData(testData
				.get("AlertCriteria"));
		HashMap<String, String> tc = getMapFromData(testData
				.get("RuleCriteria"));
		SObject[] alertSeverity = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Severity' and Name='"
						+ alertCriteria.get("alertSeverity") + "'"));
		SObject[] alertReason = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Reason' and Name='"
						+ alertCriteria.get("alertReason") + "'"));
		SObject[] alertType = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Type' and Name='"
						+ alertCriteria.get("alertType") + "'"));
		SObject[] alertStatus = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Status' and Name='"
						+ alertCriteria.get("alertStatus") + "'"));

		String alertCriteriaJson = "{\"alertSeverity\":\""
				+ alertSeverity[0].getId() + "\",\"alertReason\":\""
				+ alertReason[0].getId() + "\",\"alertType\":\""
				+ alertType[0].getId() + "\",\"alertStatus\":\""
				+ alertStatus[0].getId() + "\",\"alertComment\":\""
				+ alertCriteria.get("alertComment") + "\",\"alertSubject\":\""
				+ alertCriteria.get("alertSubject") + "\"}";
		System.out.println("alert criteria:" + alertCriteriaJson);
		rSetup.initialCleanUp();
		System.out.println("Clean up done! Loading fresh data....");
		rSetup.loadUsageDataForRulesEngine(
				getMapFromData(testData.get("UsageData")).get("type"), true,
				getMapFromData(testData.get("UsageFiles")).get("First"));
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.loadUsageDataForRulesEngine(
				getMapFromData(testData.get("UsageData")).get("type"), false,
				getMapFromData(testData.get("UsageFiles")).get("Second"));

		Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
	}
}
