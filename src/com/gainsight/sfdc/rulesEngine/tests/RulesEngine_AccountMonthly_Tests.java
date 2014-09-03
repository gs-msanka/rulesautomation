package com.gainsight.sfdc.rulesEngine.tests;

import com.gainsight.sfdc.tests.BaseTest;

public class RulesEngine_AccountMonthly_Tests extends BaseTest {
    /*
	private static final String TEST_DATA_FILE = "testdata/sfdc/RulesEngine/RulesEngineTests.xls";
	private static final String USAGEDATA_FILE = "UsageData_Account_Monthly";
	RuleEngineDataSetup rSetup = new RuleEngineDataSetup();
	RulesEngineDataValidation rValidate = new RulesEngineDataValidation();
	static boolean isPackageInstance = false;

	public RulesEngine_AccountMonthly_Tests() {
		isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
	}
	@BeforeClass
	public void setUp() {
		try {
			rSetup.initialCleanUp();
			SFDCUtil sfdc = new SFDCUtil();
			sfdc.runApexCodeFromFile(env.basedir+ "/apex_scripts/RulesEngine/Set_Account_Level_Monthly.apex",isPackageInstance);
			rSetup.loadUsageDataForRulesEngine("AccountMonthly", USAGEDATA_FILE);
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
				alertCriteria.get("alertComments"),alertCriteria.get("isAlert"));
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.runRule("ACCOUNTLEVEL");
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
				alertCriteria.get("alertComments"),alertCriteria.get("isAlert"));
		rSetup.clearPreviousTestData();
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.runRule("ACCOUNTLEVEL");//by default i am giving date=today while running rules
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
				alertCriteria.get("alertComments"),alertCriteria.get("isAlert"));
		rSetup.clearPreviousTestData();
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.runRule(alertCriteria.get("TriggeredUsageOn"));
		Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R4")
	public void Rule4(HashMap<String, String> testData) throws IOException,
			InterruptedException {
		HashMap<String, String> alertCriteria = getMapFromData(testData
				.get("AlertCriteria"));
		String advCriteria = testData.get("AdvanceCriteria");
		if(advCriteria.contains("CUST_INFO_STAGE_EXPERT")){
			System.out.println("contains expert!!");
			SObject[] stageId = soql
					.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__PickList__c WHERE JBCXM__Category__c ='Customer Stage'and Name='Expert'"));
			advCriteria=advCriteria.replace("CUST_INFO_STAGE_EXPERT","\\'"+stageId[0].getId()+"\\'");
		}		
		String alertCriteriaJson = rSetup.getAlertCriteriaJson(
				alertCriteria.get("alertSeverity"),
				alertCriteria.get("alertReason"),
				alertCriteria.get("alertType"),
				alertCriteria.get("alertStatus"),
				alertCriteria.get("alertSubject"),
				alertCriteria.get("alertComments"),alertCriteria.get("isAlert"));
		rSetup.clearPreviousTestData();
		rSetup.createRulesForRulesEngine(advCriteria,
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.runRule(alertCriteria.get("TriggeredUsageOn"));
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
				alertCriteria.get("alertComments"),alertCriteria.get("isAlert"));
		rSetup.clearPreviousTestData();
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.runRule( alertCriteria.get("TriggeredUsageOn"));
		Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
	}
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "R6")
	public void Rule6(HashMap<String, String> testData) throws IOException,
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
				alertCriteria.get("alertComments"),alertCriteria.get("isAlert"));
		rSetup.clearPreviousTestData();
		rSetup.createRulesForRulesEngine(testData.get("AdvanceCriteria"),
				alertCriteria.get("AlertCount"), alertCriteriaJson,
				alertCriteria.get("SourceType"),
				alertCriteria.get("TaskOwnerField"),
				testData.get("RuleCriteria"),
				alertCriteria.get("TriggeredUsageOn"));
		rSetup.runRule(alertCriteria.get("TriggeredUsageOn"));
		Assert.assertTrue(rValidate.checkAlertsCreated(alertCriteria));
	}
	*/
}
