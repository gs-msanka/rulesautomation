package com.gainsight.bigdata.rulesengine;

/**
 * Created by pawel on 10/4/15.
 */
import java.util.HashMap;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import com.gainsight.utils.annotations.TestInfo;
import org.testng.Assert;
import org.testng.annotations.*;

import com.gainsight.http.ResponseObj;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;

public class LoadToFeature extends RulesUtil {
	private static final String CleanupFeatures = Application.basedir
			+ "/testdata/newstack/RulesEngine/LoadToFeature/FeatureInsertion.apex";
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/LoadToFeature/LoadToFeature.xls";
	ResponseObj result = null;

	public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

	@BeforeClass
	public void beforeClass() throws Exception {
		sfdc.connect();
		LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanupFeatures));
	}

    @TestInfo(testCaseIds = {"GS-4687"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature1")
	public void loadToFeature1(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.populateObjMaps();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
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
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=true and JBCXM__Licensed__c=true");
		Assert.assertEquals(rules1,rules2);

	}

	@TestInfo(testCaseIds = {"GS-4688"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature2")
	public void loadToFeature2(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.populateObjMaps();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
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
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=true and JBCXM__Licensed__c=false");
		Assert.assertEquals(rules1,rules2);

	}

	@TestInfo(testCaseIds = {"GS-4689"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature3")
	public void loadToFeature3(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.populateObjMaps();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
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
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=false and JBCXM__Licensed__c=false");
		Assert.assertEquals(rules1,rules2);

	}

	@TestInfo(testCaseIds = {"GS-4690"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature4")
	public void loadToFeature4(HashMap<String, String> testData) throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.populateObjMaps();
		ru.setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
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

		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=false and JBCXM__Licensed__c=false");
		Assert.assertEquals(rules1,rules2);

	}

}
