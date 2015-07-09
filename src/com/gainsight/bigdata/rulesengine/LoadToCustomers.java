package com.gainsight.bigdata.rulesengine;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import com.gainsight.utils.ExcelDataProvider;
import com.gainsight.utils.annotations.TestInfo;
import jxl.read.biff.BiffException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.http.ResponseObj;
import com.gainsight.utils.DataProviderArguments;
import org.codehaus.jackson.map.ObjectMapper;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;

public class LoadToCustomers extends RulesUtil {
	private static final String Clean_Up_For_Rules = Application.basedir
			+ "/testdata/newstack/RulesEngine/scripts/CleanUpForRules.apex";
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/LoadToCustomers/LoadToCustomers.xls";
	private final String LOAD_ACCOUNTS_JOB=Application.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Accounts.txt";
	public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

	@BeforeClass
	public void beforeClass() throws Exception {
		sfdc.connect();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Clean_Up_For_Rules));
        Log.info("Calling delete method");
		metaUtil.createFieldsForAccount(sfdc, sfinfo);
		ObjectMapper mapper = new ObjectMapper();
		dataETL=new DataETL();
		JobInfo jobInfo= mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
		dataETL.execute(jobInfo);
		LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
		updateNSURLInAppSettings(nsConfig.getNsURl());


            List<HashMap<String, String>> testDataList = ExcelDataProvider.getDataFromExcel(Application.basedir+TEST_DATA_FILE, "loadToCustomers1");
            if(testDataList.size()>0) {
                loadToCustomers(testDataList.get(0));
            }
            else {
                throw new RuntimeException("Do it again...");
            }
        populateObjMaps();

	}

	@TestInfo(testCaseIds = {"GS-4540"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToCustomers2")
	public void loadToCustomers2(HashMap<String, String> testData) throws Exception {
		setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
		result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
                +ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
                + "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'")[0]
				.getChild("JBCXM__LastRunResult__c").getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);
		int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Boolean_Auto1__c, IsDeleted From Account Where ((IsDeleted = false) AND (PickList_Auto__c IN ('Excellent','Vgood')))");
        Log.info(""+rules1);
		int rules2 = sfdc.getRecordCount("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false and JBCXM__ASV__c=989898");
        Log.info(""+rules2);
		Assert.assertEquals(rules1,rules2);
	}

	@TestInfo(testCaseIds = {"gs-4642"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE,sheet = "loadToCustomers3")
    public void loadToCustomers3(HashMap<String,String> testData) throws Exception{
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
        result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                +ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
                + "\n Request rawBody:{}");
        ResponseObject responseObj = RulesUtil.convertToObject(result
                .getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        waitForCompletion(ruleId, wa, header);
        String LRR = sfdc
                .getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
                        + RuleName + "'")[0]
                .getChild("JBCXM__LastRunResult__c").getValue().toString();
        Assert.assertEquals("SUCCESS", LRR);
        int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Boolean_Auto1__c, IsDeleted, Name From Account Where ((IsDeleted = false) AND (PickList_Auto__c IN ('Excellent','Vgood','Good','Average','Poor','Vpoor')) AND (Name LIKE 'A%') AND (Number_Auto__c > 100))");
        Log.info(""+rules1);
        int rules2 = sfdc.getRecordCount("SELECT Id,JBCXM__Stage__r.Name FROM JBCXM__CustomerInfo__c WHERE JBCXM__Stage__c != null AND isdeleted=false and JBCXM__Stage__r.Name = 'Expert'");
        Log.info(""+rules2);
        Assert.assertEquals(rules1, rules2);

    }

    @TestInfo(testCaseIds = {"gs-4643"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE,sheet = "loadToCustomers4")
    public void loadToCustomers4(HashMap<String,String> testData) throws Exception{
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
        result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                +ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
                + "\n Request rawBody:{}");
        ResponseObject responseObj = RulesUtil.convertToObject(result
                .getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        waitForCompletion(ruleId, wa, header);
        String LRR = sfdc
                .getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
                        + RuleName + "'")[0]
                .getChild("JBCXM__LastRunResult__c").getValue().toString();
        Assert.assertEquals("SUCCESS", LRR);
        int rules1 = sfdc.getRecordCount("Select Id, Boolean_Auto__c, Boolean_Auto1__c, IsDeleted, Name From Account Where ((IsDeleted = false) AND (PickList_Auto__c IN ('Excellent','Vgood','Good','Average','Poor','Vpoor')) AND (Name LIKE 'A%') AND (Number_Auto__c > 100) AND (Date_Auto__c !=YESTERDAY))");
        Log.info(""+rules1);
        int rules2 = sfdc.getRecordCount("SELECT JBCXM__Comments__c FROM JBCXM__CustomerInfo__c where isdeleted=false and JBCXM__OneTimeRevenue__c=123");
        Log.info(""+rules2);
        Assert.assertEquals(rules1, rules2);

    }

    @TestInfo(testCaseIds = {"gs-4644"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToCustomers5")
    public void loadToCustomers5(HashMap<String, String> testData) throws Exception {
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
        result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                +ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
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
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        Log.info("request:" + ApiUrls.APP_API_EVENTRULE +"/"+ ruleId);
        result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                +ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
                + "\n Request rawBody:{}");
        ResponseObject responseObj = RulesUtil.convertToObject(result
                .getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        waitForCompletion(ruleId, wa, header);
        String LRR = sfdc
                .getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
                        + RuleName + "'")[0]
                .getChild("JBCXM__LastRunResult__c").getValue().toString();
        Assert.assertEquals("SUCCESS", LRR);
        int rules1 = sfdc.getRecordCount("Select Id, Name, IsDeleted, CreatedDate, Boolean_Auto__c, DateTime_Auto__c, Email_Auto__c, PickList_Auto__c, URL_Auto__c, Boolean_Auto1__c From Account Where ((Id != null) AND ((Name LIKE 'A%') OR (Name LIKE 'B%')) AND (IsDeleted = false)) AND JBCXM__CustomerInfo__c != null");
        Log.info(""+rules1);
        int rules2 = sfdc.getRecordCount("SELECT JBCXM__MRR__c FROM JBCXM__CustomerInfo__c where isdeleted=false and JBCXM__MRR__c=22222");
        Log.info(""+rules2);
        Assert.assertEquals(rules1, rules2);

    }


    @AfterClass
	public void afterClass() {
		//GSUtil.soql = null;
	}
}
