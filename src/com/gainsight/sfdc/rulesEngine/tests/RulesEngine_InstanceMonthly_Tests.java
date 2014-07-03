package com.gainsight.sfdc.rulesEngine.tests;

import java.io.IOException;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sforce.soap.partner.sobject.SObject;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.sfdc.rulesEngine.tests.*;


public class RulesEngine_InstanceMonthly_Tests extends BaseTest{
	

		private static final String TEST_DATA_FILE = "testdata/sfdc/RulesEngine/RETests_InstanceMonthly.xls";
		private static final String USAGEDATA_FILE = "UsageData_Instance_Monthly";
		private static final String USAGE_LEVEL="InstanceMonthly";
		RuleEngineDataSetup rSetup = new RuleEngineDataSetup();
		RulesEngineDataValidation rValidate = new RulesEngineDataValidation();

		@BeforeClass
		public void setUp() {
		
			try {
				rSetup.initialCleanUp();
				rSetup.loadUsageDataForRulesEngine(USAGE_LEVEL, true,
						USAGEDATA_FILE);
			} catch (Exception ex) {
				System.out.println(ex.getLocalizedMessage());
			}
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

			// Directly giving Advance and trigger criteria as json...instead of constructing jSon (need to change)
			
			String alertCriteriaJson = rSetup.getAlertCriteriaJson(
					alertCriteria.get("alertSeverity"),
					alertCriteria.get("alertReason"),
					alertCriteria.get("alertType"),
					alertCriteria.get("alertStatus"),
					alertCriteria.get("alertSubject"),
					alertCriteria.get("alertComments"));
			rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
					alertCriteria.get("AlertCount"), alertCriteriaJson,
					alertCriteria.get("SourceType"),
					alertCriteria.get("TaskOwnerField"),
					testData.get("RuleCriteria"),
					alertCriteria.get("TriggeredUsageOn"));
			rSetup.runRule(soql.getUserLocale(), "INSTANCELEVEL");
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
			String alertCriteriaJson = rSetup.getAlertCriteriaJson(
					alertCriteria.get("alertSeverity"),
					alertCriteria.get("alertReason"),
					alertCriteria.get("alertType"),
					alertCriteria.get("alertStatus"),
					alertCriteria.get("alertSubject"),
					alertCriteria.get("alertComments"));
			rSetup.clearAlertsFromPreviousTest();
			rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
					alertCriteria.get("AlertCount"), alertCriteriaJson,
					alertCriteria.get("SourceType"),
					alertCriteria.get("TaskOwnerField"),
					testData.get("RuleCriteria"),
					alertCriteria.get("TriggeredUsageOn"));
			rSetup.runRule("", alertCriteria.get("TriggeredUsageOn"));//First param is for date...by default i am giving date=today while running rules
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
			String alertCriteriaJson = rSetup.getAlertCriteriaJson(
					alertCriteria.get("alertSeverity"),
					alertCriteria.get("alertReason"),
					alertCriteria.get("alertType"),
					alertCriteria.get("alertStatus"),
					alertCriteria.get("alertSubject"),
					alertCriteria.get("alertComments"));
			rSetup.clearAlertsFromPreviousTest();
			rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
					alertCriteria.get("AlertCount"), alertCriteriaJson,
					alertCriteria.get("SourceType"),
					alertCriteria.get("TaskOwnerField"),
					testData.get("RuleCriteria"),
					alertCriteria.get("TriggeredUsageOn"));
			rSetup.runRule("", alertCriteria.get("TriggeredUsageOn"));
			Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
		}
		
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R4")
		public void Rule4(HashMap<String, String> testData) throws IOException,
				InterruptedException {
			HashMap<String, String> alertCriteria = getMapFromData(testData
					.get("AlertCriteria"));
			HashMap<String, String> tc = getMapFromData(testData
					.get("RuleCriteria"));
				
			String alertCriteriaJson = rSetup.getAlertCriteriaJson(
					alertCriteria.get("alertSeverity"),
					alertCriteria.get("alertReason"),
					alertCriteria.get("alertType"),
					alertCriteria.get("alertStatus"),
					alertCriteria.get("alertSubject"),
					alertCriteria.get("alertComments"));

			System.out.println("alert criteria:" + alertCriteriaJson);
			rSetup.clearAlertsFromPreviousTest();
			rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
					alertCriteria.get("AlertCount"), alertCriteriaJson,
					alertCriteria.get("SourceType"),
					alertCriteria.get("TaskOwnerField"),
					testData.get("RuleCriteria"),
					alertCriteria.get("TriggeredUsageOn"));
			rSetup.runRule("", alertCriteria.get("TriggeredUsageOn"));
			Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
		}
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R5")
		public void Rule5(HashMap<String, String> testData) throws IOException,
				InterruptedException {
			HashMap<String, String> alertCriteria = getMapFromData(testData
					.get("AlertCriteria"));
			HashMap<String, String> tc = getMapFromData(testData
					.get("RuleCriteria"));
			String alertCriteriaJson = rSetup.getAlertCriteriaJson(
					alertCriteria.get("alertSeverity"),
					alertCriteria.get("alertReason"),
					alertCriteria.get("alertType"),
					alertCriteria.get("alertStatus"),
					alertCriteria.get("alertSubject"),
					alertCriteria.get("alertComments"));

			System.out.println("alert criteria:" + alertCriteriaJson);
			rSetup.clearAlertsFromPreviousTest();
			rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
					alertCriteria.get("AlertCount"), alertCriteriaJson,
					alertCriteria.get("SourceType"),
					alertCriteria.get("TaskOwnerField"),
					testData.get("RuleCriteria"),
					alertCriteria.get("TriggeredUsageOn"));
			rSetup.runRule("", alertCriteria.get("TriggeredUsageOn"));
			Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
		}
}
