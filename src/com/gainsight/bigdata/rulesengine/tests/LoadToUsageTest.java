package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import au.com.bytecode.opencsv.CSVReader;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.annotations.TestInfo;

/**
 * @author Abhilash Thaduka
 *
 */

public class LoadToUsageTest extends BaseTest {

	private static final String USAGE_DATA_MEASURE_FILE = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/UsageData_Measures.apex";
	private static final String ACCOUNT_LEVEL_WEEKLY_FILE = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Set_Account_Level_Weekly.apex";
	private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
	private RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	private ObjectMapper mapper = new ObjectMapper();
	private DataETL dataETL = new DataETL();
	private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
	private NSTestBase nsTestBase = new NSTestBase();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	private RulesUtil rulesUtil = new RulesUtil();
	
	
	@BeforeClass
	public void setup() throws Exception {
		basepage.login();
		sfdc.connect();
		nsTestBase.init();
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		metaUtil.createFieldsOnUsageData(sfdc);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(USAGE_DATA_MEASURE_FILE));
		rulesConfigureAndDataSetup.createCustomObjectAndFields();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
	}

	@BeforeMethod
	public void cleanup() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__UsageData__c];"));

	}

	@TestInfo(testCaseIds = { "GS-5148", "GS-5151" })
	@Test(description = "LoadToUsage data action with ->  Account Level - Weekly with advanced criteria and Verifying Duplication of data is not happening while running rule for second time")
	public void loadToUsageActionWithAccountLevelWeeklyData() throws Exception {
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(ACCOUNT_LEVEL_WEEKLY_FILE));
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/GS-5148-Job-LoadDataIntoCustomObject.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/GS-5148-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/GS-5148-ExpectedJob.txt"),JobInfo.class));
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/ExpectedData.csv"), NAMESPACE)));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/ActualData.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
		
		//GS-5151 starts here
		//Running rule for second time to verify duplication of data is not happening while running rule again
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");	
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/GS-5148-ExpectedJob.txt"),JobInfo.class));
		List<Map<String, String>> expectedData1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/ExpectedData.csv"), NAMESPACE)));
		List<Map<String, String>> actualData1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/ActualData.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedData1, actualData1);
		Log.info("Actual : " + mapper.writeValueAsString(actualData1));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData1));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
		Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
	}
	
	@TestInfo(testCaseIds = { "GS-5150" })
	@Test(description = "LoadToUsage data action with ->  Instance Level - Monthly Data")
	public void loadToUsageActionWithInstanceLevelMonthlyData() throws Exception {
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Set_Instance_Level_Monthly.apex"));
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5150/GS-5150-Job-LoadDataIntoCustomObject.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5150/GS-5150-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed !!!");
		
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5150/GS-5150-ExpectedJob.txt"),JobInfo.class));
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5150/ExpectedData.csv"), NAMESPACE)));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5150/ActualData.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
	}
}
