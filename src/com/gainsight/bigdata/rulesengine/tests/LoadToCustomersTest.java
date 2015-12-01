package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
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
public class LoadToCustomersTest extends BaseTest {

	private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
	private static final String ACCOUNTS_JOB_FOR_LOAD_TO_CUSTOMERS_ACTION = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts_For_Load_to_Customers_Action.txt";
	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
	private static final String CREATE_ACCOUNTS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts.txt";
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	private ObjectMapper mapper = new ObjectMapper();
	private NSTestBase nsTestBase = new NSTestBase();
	private RulesUtil rulesUtil = new RulesUtil();
	RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	DataETL dataETL = new DataETL();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;

	@BeforeClass
	public void setUpData() throws Exception {
		basepage.login();
		sfdc.connect();
		nsTestBase.init();
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		rulesConfigureAndDataSetup.createCustomObjectAndFields();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
		JobInfo jobInfo = mapper.readValue((new FileReader(ACCOUNTS_JOB_FOR_LOAD_TO_CUSTOMERS_ACTION)), JobInfo.class);
		dataETL.execute(jobInfo);
	}
	
	@BeforeMethod
	public void CleanUpCustomers(){
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		sfdc.runApexCode(resolveStrNameSpace("Delete [select id from JBCXM__CustomerInfo__c];"));
		
	}
	@TestInfo(testCaseIds = {"GS-3149"})
    @Test()
    public void testLoadToCustomers() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC41.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-3149.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-3149-ExpectedData.csv"), NAMESPACE)));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-3149-ActualData.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }
	
	@TestInfo(testCaseIds = {"GS-5135"})
    @Test()
    public void testLoadToCustomers2() throws Exception {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/AccountsAndCustomersForLoadtoCustomerAction.txt"));
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC42.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-5135.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5135-ExpectedData.csv"), NAMESPACE)));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5135-ActualData.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }
	
	
	@TestInfo(testCaseIds = {"GS-5134"})
    @Test()
    public void testLoadToCustomers3() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC43.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-5134.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5134-ExpectedData5.csv"), NAMESPACE)));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5134-ActualData.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
		
		JobInfo jobInfo2 = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-5134-2.txt"),JobInfo.class);	
		dataETL.execute(jobInfo2);
		List<Map<String, String>> expectedData2 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5134-ExpectedData-2.csv"), NAMESPACE)));
		List<Map<String, String>> actualData2 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5134-ActualData3.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData2 = Comparator.compareListData(expectedData2, actualData2);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData2));
		Assert.assertEquals(differenceData2.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
	}
	
	@TestInfo(testCaseIds = {"GS-5152"})
    @Test()
    public void testLoadToCustomers4() throws Exception {
		sfdc.runApexCode(resolveStrNameSpace("Delete [select id from Account where name like '%rule%'];"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS));
		sfdc.runApexCode(resolveStrNameSpace("Delete [select id from C_Custom__c];"));
		JobInfo loadData = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-5152.txt")), JobInfo.class);
		dataETL.execute(loadData);
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC44.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-5152-2.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5152-ExpectedData.csv"), NAMESPACE)));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/GS-5152-ActualData.csv"), NAMESPACE)));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
	}
	
}
