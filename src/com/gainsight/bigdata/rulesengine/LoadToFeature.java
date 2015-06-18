package com.gainsight.bigdata.rulesengine;

/**
 * Created by pawel on 10/4/15.
 */
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import com.gainsight.utils.ExcelDataProvider;
import com.gainsight.utils.annotations.TestInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;

import com.gainsight.http.ResponseObj;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;

public class LoadToFeature extends RulesUtil {
	private static final String CleanupFeatures = Application.basedir
			+ "/testdata/newstack/RulesEngine/LoadToFeature/FeatureInsertion.apex";
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/LoadToFeature/LoadToFeature.xls";
	private final String LOAD_ACCOUNTS_JOB=env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Accounts.txt";
    private final String TEST_DATA_FILE1 = "/testdata/newstack/RulesEngine/LoadToCustomers/LoadToCustomers.xls";
	public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

	@BeforeClass
	public void beforeClass() throws Exception {
		sfdc.connect();
		metaUtil.createFieldsForAccount(sfdc, sfinfo);
		LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanupFeatures));
		ObjectMapper mapper = new ObjectMapper();
		dataETL=new DataETL();
		JobInfo jobInfo= mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
		dataETL.execute(jobInfo);
		LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
		updateNSURLInAppSettings(PropertyReader.nsAppUrl);
		List<HashMap<String, String>> testDataList = ExcelDataProvider.getDataFromExcel(Application.basedir + TEST_DATA_FILE1, "loadToCustomers1");
		if(testDataList.size()>0) {
			loadToCustomers(testDataList.get(0));
		}
		else {
			throw new RuntimeException("Do it again...");
		}
		populateObjMaps();

	}

    @TestInfo(testCaseIds = {"GS-4687"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature1")
	public void loadToFeature1(HashMap<String, String> testData) throws Exception {
		setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
		result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
				+ "\n Request rawBody:{}");
        ResponseObject responseObj = convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		waitForCompletion(ruleId, wa, header);
        String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
        Log.info(""+rules1);
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=true and JBCXM__Licensed__c=true");
        Log.info(""+rules2);
        Assert.assertEquals(rules1,rules2);

	}

	@TestInfo(testCaseIds = {"GS-4688"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature2")
	public void loadToFeature2(HashMap<String, String> testData) throws Exception {
		setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
		result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
				+ "\n Request rawBody:{}");
        ResponseObject responseObj = convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		waitForCompletion(ruleId, wa, header);
        String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
        Log.info(""+rules1);
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=true and JBCXM__Licensed__c=false");
        Log.info(""+rules2);
		Assert.assertEquals(rules1,rules2);

	}

	@TestInfo(testCaseIds = {"GS-4689"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature3")
	public void loadToFeature3(HashMap<String, String> testData) throws Exception {
		setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
		result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
				+ "\n Request rawBody:{}");
        ResponseObject responseObj = convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		waitForCompletion(ruleId, wa, header);
        String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
        Log.info(""+rules1);
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=false and JBCXM__Licensed__c=false");
        Log.info(""+rules1);
		Assert.assertEquals(rules1,rules2);

	}

	@TestInfo(testCaseIds = {"GS-4690"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToFeature4")
	public void loadToFeature4(HashMap<String, String> testData) throws Exception {
		setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
		result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
				+ "\n Request rawBody:{}");
		ResponseObject responseObj = convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Name From Account Where ((Boolean_Auto__c = false) AND (Currency_Auto__c > 1000) AND (Name LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
		Log.info(""+rules1);
		int rules2 = sfdc.getRecordCount("Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='One' and JBCXM__Features__r.JBCXM__Product__c='One' and JBCXM__Enabled__c=false and JBCXM__Licensed__c=false");
		Log.info(""+rules2);
		Assert.assertEquals(rules1,rules2);

	}

}
