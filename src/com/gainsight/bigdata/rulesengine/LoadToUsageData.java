package com.gainsight.bigdata.rulesengine;

import java.util.HashMap;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.http.ResponseObj;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;

public class LoadToUsageData extends RulesUtil {
	private static final String CleanUpForRules = Application.basedir
			+ "/testdata/newstack/RulesEngine/CleanUpForRules.apex";
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/LoadToUsageData/LoadToUsageData.xls";

	ResponseObj result = null;

	@BeforeClass
	public void beforeClass() throws Exception {
		sfdc.connect();
		metaUtil.createFieldsOnUsageData(sfdc);
		updateNSURLInAppSettings(env.getProperty("ns.appurl"));
	}

	@BeforeMethod
	public void cleanUp() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanUpForRules));
	}

	// Its for UsageData sync with Account Id's only
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
	public void rulesUsageOne(HashMap<String, String> testData)
			throws Exception {
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

		int rules1 = sfdc
				.getRecordCount("SELECT Id FROM Account where IsDeleted=false ");
		int rules2 = sfdc
				.getRecordCount("SELECT Id FROM JBCXM__UsageData__c where IsDeleted=false ");
		Assert.assertEquals(rules1, rules2);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
	public void rulesUsagTwo(HashMap<String, String> testData) throws Exception {
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

		int rules1 = sfdc
				.getRecordCount("SELECT Id FROM Account Where ((Name LIKE 'B%')) AND JBCXM__CustomerInfo__c != null");
		int rules2 = sfdc
				.getRecordCount("SELECT Id FROM JBCXM__UsageData__c where Files_Downloaded__c=12345.0 and IsDeleted=false");
		Assert.assertEquals(rules1, rules2);
	}

	@AfterClass
	public void afterClass() {

	}
}
