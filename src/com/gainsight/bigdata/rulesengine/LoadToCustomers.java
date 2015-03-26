package com.gainsight.bigdata.rulesengine;

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

public class LoadToCustomers extends NSTestBase{
    private static final String CleanUpForRules=Application.basedir+"/testdata/newstack/RulesEngine/CleanUpForRules.apex";
	private final String TEST_DATA_FILE="/testdata/newstack/RulesEngine/LoadToCustomers/LoadToCustomers.xls";

    ResponseObj result=null;

    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

    // Work In Progress Need to optimize the code as we will proceed

    @BeforeClass
    public void beforeClass() throws Exception {
        sfdc.connect();
        LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
    }
    @BeforeMethod
	public void cleanUp() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanUpForRules));
    }

    // Its for CustomerInfo Sync when Checkbox Apply to Gainsight customers is
    // not enabled
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
    public void Rule1(HashMap<String,String> testData) throws Exception {
    		RulesUtil ru=new RulesUtil();
    		ru.setupRule(testData);  
    		String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));    
    		System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
            result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{}");
        	Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");            
            
        	ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            RulesUtil.waitForCompletion(ruleId, wa, header);            
            
            String LRR = getSFId("SFID:JBCXM__LastRunResult__c:JBCXM__AutomatedAlertRules__c:Id:"+ruleId+"");
                Assert.assertEquals("SUCCESS", LRR);
        
                int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
                int rules2 = sfdc.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
                Assert.assertEquals(rules1,rules2);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
    // Load to customer with Account names starts with A and ASV=4545
    public void Rule2(HashMap<String,String> testData) throws Exception {
    	RulesUtil ru=new RulesUtil();
		ru.setupRule(testData);  
		String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));    
		System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
        result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{}");
    	Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");            
        
    	ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        RulesUtil.waitForCompletion(ruleId, wa, header);            
        
        String LRR = getSFId("SFID:JBCXM__LastRunResult__c:JBCXM__AutomatedAlertRules__c:Id:"+ruleId+"");
            Assert.assertEquals("SUCCESS", LRR);
    
            int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
            int rules2 = sfdc.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
            Assert.assertEquals(rules1,rules2);
    }

    // Load to customer with picklist excludes all in where condition
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
    public void Rule3(HashMap<String,String> testData) throws Exception {
    	RulesUtil ru=new RulesUtil();
		ru.setupRule(testData);  
		String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));    
		System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
        result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{}");
    	Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");            
        
    	ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        RulesUtil.waitForCompletion(ruleId, wa, header);            
        
        String LRR = getSFId("SFID:JBCXM__LastRunResult__c:JBCXM__AutomatedAlertRules__c:Id:"+ruleId+"");
            Assert.assertEquals("SUCCESS", LRR);
    
            int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
            int rules2 = sfdc.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
            Assert.assertEquals(rules1,rules2);
    }

    // In FIlters And+Or condition
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
    public void Rule4(HashMap<String,String> testData) throws Exception {
    	RulesUtil ru=new RulesUtil();
		ru.setupRule(testData);  
		String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));    
		System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
        result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{}");
    	Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");            
        
    	ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        RulesUtil.waitForCompletion(ruleId, wa, header);            
        
        String LRR = getSFId("SFID:JBCXM__LastRunResult__c:JBCXM__AutomatedAlertRules__c:Id:"+ruleId+"");
            Assert.assertEquals("SUCCESS", LRR);
    
            int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
            int rules2 = sfdc.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
            Assert.assertEquals(rules1,rules2);
    }

    // Date Sync for Load to Customer with Today's date (In Where Account Name
    // contains B)
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
    public void Rule5(HashMap<String,String> testData) throws Exception {
    	RulesUtil ru=new RulesUtil();
		ru.setupRule(testData);  
		String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));    
		System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
        result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{}");
    	Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");            
        
    	ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        RulesUtil.waitForCompletion(ruleId, wa, header);            
        
        String LRR = getSFId("SFID:JBCXM__LastRunResult__c:JBCXM__AutomatedAlertRules__c:Id:"+ruleId+"");
            Assert.assertEquals("SUCCESS", LRR);
    
            int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
            int rules2 = sfdc.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
            Assert.assertEquals(rules1,rules2);
    }

    // Data Sync for load to customers with aggregation in setup rule and
    // advance criteria in setup actions.
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
    public void Rule6(HashMap<String,String> testData) throws Exception {
    	RulesUtil ru=new RulesUtil();
		ru.setupRule(testData);  
		String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));    
		System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
        result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{}");
    	Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");            
        
    	ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        RulesUtil.waitForCompletion(ruleId, wa, header);            
        
        String LRR = getSFId("SFID:JBCXM__LastRunResult__c:JBCXM__AutomatedAlertRules__c:Id:"+ruleId+"");
            Assert.assertEquals("SUCCESS", LRR);
    
            int rules1 = sfdc.getRecordCount("SELECT count(Id) FROM Account");
            int rules2 = sfdc.getRecordCount("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
            Assert.assertEquals(rules1,rules2);
    }

    // Preview Results
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule7")
    public void Rule7(HashMap<String,String> testData) throws Exception {
    	RulesUtil ru=new RulesUtil();
		ru.setupRule(testData);  
		String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));    
		System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
        result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{\"numberOfRecords\": \"10\"}");
    	Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");            
            ResponseObject responseObj = RulesUtil.convertToObject(result
                    .getContent());
            // Assert.assertEquals(ro.getData().size(), 10);
            LinkedHashMap<Object,Object> data = (LinkedHashMap<Object,Object>) responseObj.getData();
            Assert.assertTrue(data.size() <= 10);
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
    }

    @AfterClass
    public void afterClass() {
        //GSUtil.soql = null;
    }
}
