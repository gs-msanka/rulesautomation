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
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
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
public class CalculatedFieldsAndMeasuresTest extends BaseTest {
	
	private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup2.apex";
	private NSTestBase nsTestBase = new NSTestBase();
	private ObjectMapper mapper = new ObjectMapper();
	private DataETL dataETL = new DataETL();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	private  RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	private RulesUtil rulesUtil = new RulesUtil();
	GSDataImpl gsDataImpl = null;
	private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM RulesSFDCCustom__c];";
	
	@BeforeClass
	public void setup() throws Exception {
		basepage.login();
		sfdc.connect();
		nsTestBase.init();
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		rulesConfigureAndDataSetup.createCustomObjectAndFieldsInSfdc();
		rulesConfigureAndDataSetup.createDataLoadConfiguration();
		rulesConfigureAndDataSetup.createCustomObjectAndFields();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		sfdc.runApexCode("Delete [SELECT Id FROM C_Custom__c];");		
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/LoadDataIntoCustomObject.txt"),JobInfo.class);
		dataETL.execute(jobInfo);		
	}

	@BeforeMethod
	public void cleanup() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
	}

	@TestInfo(testCaseIds = { "GS-4200" })
	@Test(description = "Test case to verify if the calculated Fields for comparision calculation type with percentage is working fine when Fields A and B are from Show Fields")
	public void testCalculatedFields() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	
	@TestInfo(testCaseIds = { "GS-4236" })
	@Test(description = "Test case to verify if the calculated Fields for comparision calculation type with Actual Value is working fine when Fields A and B are from Show Fields")
	public void testCalculatedFields2() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	
	@TestInfo(testCaseIds = { "GS-4048", "GS-4043", "GS-4044", "GS-4042" })
	@Test(description = "Calculated field with AVERAGE as the aggregation over time with adjust for missing data option and also COUNT and COUNT_DISTINCT for weekly granualrity")
	public void testCalculatedFields3() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-ExpectedJob.txt"), JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	
	@TestInfo(testCaseIds = { "GS-4045" })
	@Test(description = "Creating a Calculated field with MAX as aggregation with Weekly Granularity")
	public void testCalculatedFields4() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data for 4 Weeks,4 Months and 2 Years 
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-ExpectedJob.txt"), JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4046", "GS-4047", "GS-6062" })
	@Test(description = "Creating a Calculated field - Aggregation over time  with SUM,MIN as aggregation with Weekly Granularity and verifying same calculated fields are available or not in advanced criteria")
	public void testCalculatedFields5() throws Exception {		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying  expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-ExpectedJob.txt"), JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4237"})
	@Test(description = "Test case to verify if user is able to create a Calculated fields with weekly granularity With Field A from showfield and field B is aggregated value")
	public void testCalculatedFields6() throws Exception {	
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/GS-4237-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying  expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/GS-4237-ExpectedJob.txt"), JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4238"})
	@Test(description = "Test case to verify if user is able to create a Calculated fields with weekly granularity With Field A from aggregated field and Field B from show field")
	public void testCalculatedFields7() throws Exception {	
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/GS-4238-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying  expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/GS-4238-ExpectedJob.txt"), JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4204" })
	@Test(description = "Test case to verify if the calculation type is comparision when Both fields are aggregated over time for  AVG, COUNT, COUNT_DISTINCT and AVG(Adjust for missing data) over weekly granularity")
	public void testCalculatedFields8() throws Exception {		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data 
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
		
		// creating rule again since at a time only 4 configurations aggregated over time can be configured
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
		RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-Input-2.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo1);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo1.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-ExpectedJob-2.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData1 = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/ExpectedData2.csv")));
		List<Map<String, String>> actualData1 = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/ActualData.csv")));
		List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedData1, actualData1);
		Log.info("Actual : " + mapper.writeValueAsString(actualData1));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData1));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
		Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4239" })
	@Test(description = "Test case to verify if the calculation type is Actual Value when Both fields are aggregated over time")
	public void testCalculatedFields9() throws Exception {		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
		
		// creating rule again since at a time only 4 configurations aggregated over time can be configured
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);	
		RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-Input-2.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo1);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo1.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-ExpectedJob-2.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData1 = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/ExpectedData2.csv")));
		List<Map<String, String>> actualData1 = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/ActualData.csv")));
		List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedData1, actualData1);
		Log.info("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData1));
		Log.info("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData1));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
		Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
}
