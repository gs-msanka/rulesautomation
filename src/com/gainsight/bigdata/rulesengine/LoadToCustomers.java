package com.gainsight.bigdata.rulesengine;

import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.http.ResponseObj;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;
import org.codehaus.jackson.map.ObjectMapper;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;

public class LoadToCustomers extends RulesUtil {
	private static final String CleanUpForRules = Application.basedir
			+ "/testdata/newstack/RulesEngine/scripts/CleanUpForRules.apex";
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/LoadToCustomers/LoadToCustomers.xls";
	private final String LOAD_ACCOUNTS_JOB=env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Accounts.txt";
	private final String LOAD_CUSTOMERS_JOB=env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Customers.txt";
	private DataETL dataETL;
	ResponseObj result = null;

	public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

	@BeforeClass
	public void beforeClass() throws Exception {
		sfdc.connect();
		Log.info("Calling delete method");
		metaUtil.deleteAccountMetadata(sfdc);
		metaUtil.createFieldsForAccount(sfdc, sfinfo);
		ObjectMapper mapper = new ObjectMapper();
		dataETL=new DataETL();
		JobInfo jobInfo= mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
		dataETL.execute(jobInfo);
		JobInfo jobInfo1=mapper.readValue((new FileReader(LOAD_CUSTOMERS_JOB)),JobInfo.class);
		dataETL.execute(jobInfo1);
		LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
		updateNSURLInAppSettings(env.getProperty("ns.appurl"));

	}

	@BeforeMethod
	public void cleanUp() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanUpForRules));
	}

	// Its for CustomerInfo Sync when Checkbox Apply to Gainsight customers is
	// not enabled
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
	public void Rule1(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		System.out.println("request:" + PropertyReader.nsAppUrl
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
				PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);

		int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
		int rules2 = sfdc
				.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
		Assert.assertEquals(rules1, rules2);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
	// Load to customer with Account names starts with A and ASV=4545
	public void Rule2(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		System.out.println("request:" + PropertyReader.nsAppUrl
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
				PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);

		int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
		int rules2 = sfdc
				.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
		Assert.assertEquals(rules1, rules2);
	}

	// Load to customer with picklist excludes all in where condition
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
	public void Rule3(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		System.out.println("request:" + PropertyReader.nsAppUrl
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
				PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);

		int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
		int rules2 = sfdc
				.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
		Assert.assertEquals(rules1, rules2);
	}

	// In FIlters And+Or condition
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
	public void Rule4(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		System.out.println("request:" + PropertyReader.nsAppUrl
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
				PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);

		int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
		int rules2 = sfdc
				.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
		Assert.assertEquals(rules1, rules2);
	}

	// Date Sync for Load to Customer with Today's date (In Where Account Name
	// contains B)
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
	public void Rule5(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		System.out.println("request:" + PropertyReader.nsAppUrl
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
				PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);

		int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
		int rules2 = sfdc
				.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
		Assert.assertEquals(rules1, rules2);
	}

	// Data Sync for load to customers with aggregation in setup rule and
	// advance criteria in setup actions.
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
	public void Rule6(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		System.out.println("request:" + PropertyReader.nsAppUrl
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
				PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);

		int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
		int rules2 = sfdc
				.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
		Assert.assertEquals(rules1, rules2);
	}

	// Preview Results
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule7")
	public void Rule7(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		System.out.println("request:" + PropertyReader.nsAppUrl
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
				PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{\"numberOfRecords\": \"10\"}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");
		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		// Assert.assertEquals(ro.getData().size(), 10);
		LinkedHashMap<Object, Object> data = (LinkedHashMap<Object, Object>) responseObj
				.getData();
		Assert.assertTrue(data.size() <= 10);
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
	}

	@AfterClass
	public void afterClass() {
		// GSUtil.soql = null;
	}
}
