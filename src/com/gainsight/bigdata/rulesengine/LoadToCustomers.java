package com.gainsight.bigdata.rulesengine;

import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import com.gainsight.utils.annotations.TestInfo;
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
	private static final String Clean_Up_For_Rules = Application.basedir
			+ "/testdata/newstack/RulesEngine/scripts/CleanUpForRules.apex";
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/LoadToCustomers/LoadToCustomers.xls";
	private final String LOAD_ACCOUNTS_JOB=env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Accounts.txt";
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
		LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
		updateNSURLInAppSettings(PropertyReader.nsAppUrl);

	}

	@BeforeMethod
	public void cleanUp() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(Clean_Up_For_Rules));
	}

	@TestInfo(testCaseIds = {"GS-4578"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToCustomers1")
	public void loadToCustomers1(HashMap<String, String> testData) throws Exception {
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

		int rules1 = sfdc.getRecordCount("Select Id, IsDeleted From Account Where ((IsDeleted = false))");
		int rules2 = sfdc
				.getRecordCount("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false");
		Assert.assertEquals(rules1, rules2);
	}

	@TestInfo(testCaseIds = {"GS-4540"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToCustomers2")
	public void loadToCustomers2(HashMap<String, String> testData) throws Exception {
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
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Boolean_Auto1__c, IsDeleted From Account Where ((IsDeleted = false) AND (PickList_Auto__c IN ('Excellent','Vgood')))");
		int rules2 = sfdc.getRecordCount("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false and JBCXM__ASV__c=989898");
		Assert.assertEquals(rules1,rules2);
	}

	@TestInfo(testCaseIds = {"gs-4642"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE,sheet = "loadToCustomers3")
    public void loadToCustomers3(HashMap<String,String> testData) throws Exception{
        RulesUtil ru=new RulesUtil();
        ru.populateObjMaps();
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
        int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Boolean_Auto1__c, IsDeleted, Name From Account Where ((IsDeleted = false) AND (PickList_Auto__c IN ('Excellent','Vgood','Good','Average','Poor','Vpoor')) AND (Name LIKE 'A%') AND (Number_Auto__c > 100))");
        int rules2 = sfdc.getRecordCount("SELECT Id,JBCXM__Stage__r.Name FROM JBCXM__CustomerInfo__c WHERE JBCXM__Stage__c != null AND isdeleted=false and JBCXM__Stage__r.Name = 'Expert'");
        Assert.assertEquals(rules1, rules2);

    }

    @TestInfo(testCaseIds = {"gs-4643"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE,sheet = "loadToCustomers4")
    public void loadToCustomers4(HashMap<String,String> testData) throws Exception{
        RulesUtil ru=new RulesUtil();
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
        int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Boolean_Auto1__c, IsDeleted, Name From Account Where ((IsDeleted = false) AND (PickList_Auto__c IN ('Excellent','Vgood','Good','Average','Poor','Vpoor')) AND (Name LIKE 'A%') AND (Number_Auto__c > 100) AND (Date_Auto__c !=YESTERDAY))");
        int rules2 = sfdc.getRecordCount("SELECT JBCXM__Comments__c FROM JBCXM__CustomerInfo__c where isdeleted=false and JBCXM__OneTimeRevenue__c=123");
        Assert.assertEquals(rules1, rules2);

    }

    @TestInfo(testCaseIds = {"gs-4644"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToCustomers5")
    public void loadToCustomers5(HashMap<String, String> testData) throws Exception {
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
        LinkedHashMap<Object, Object> data = (LinkedHashMap<Object, Object>) responseObj
                .getData();
        Assert.assertTrue(data.size() <= 10);
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
    }

    @TestInfo(testCaseIds = {"gs-4650"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE,sheet = "loadToCustomers6")
    public void loadToCustomers6(HashMap<String,String> testData) throws Exception{
        RulesUtil ru=new RulesUtil();
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
        int rules1 = sfdc.getRecordCount("Select Id, Name, IsDeleted, CreatedDate, Boolean_Auto__c, DateTime_Auto__c, Email_Auto__c, PickList_Auto__c, URL_Auto__c, Boolean_Auto1__c From Account Where ((Id != null) AND ((Name LIKE 'A%') OR (Name LIKE 'B%')) AND (IsDeleted = false)) AND JBCXM__CustomerInfo__c != null");
        int rules2 = sfdc.getRecordCount("SELECT JBCXM__MRR__c FROM JBCXM__CustomerInfo__c where isdeleted=false and JBCXM__MRR__c=22222");
        Assert.assertEquals(rules1, rules2);

    }


    @AfterClass
	public void afterClass() {
		//GSUtil.soql = null;
	}
}
