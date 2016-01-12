package com.gainsight.bigdata.rulesengine.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.annotations.TestInfo;
import com.google.gson.JsonObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Abhilash Thaduka on 1/11/2016.
 */
public class SetupRuleTestWithNativeData extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private NSTestBase nsTestBase = new NSTestBase();
    private DataETL dataETL = new DataETL();
    private ObjectMapper mapper = new ObjectMapper();
    private RulesUtil rulesUtil = new RulesUtil();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private String rulesManagerPageUrl;
    private RulesManagerPage rulesManagerPage;

    @BeforeClass
    public void setup() throws Exception {
        basepage.login();
        sfdc.connect();
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
/*        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObject.txt"), JobInfo.class);
        dataETL.execute(jobInfo);*/
    }

    @BeforeMethod
    public void cleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
    }

    @TestInfo(testCaseIds = {"GS-4969"})
    @Test
    public void testDataInShowFieldsAndFilterFields() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4649/Input.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        List<Map<String, String>> PreviewResults = rulesUtil.getPreviewResults(rulesPojo.getRuleName(), "{\"numberOfRecords\":\"100\"}");
        Log.info("Total Records are " + mapper.writeValueAsString(PreviewResults));
        String records[] = {"C_lookup__r.Name", "rules_c_Checkbox__c", "Custom_Currency__c", "rules_c_Email__c", "rules_c_Number__c", "rules_c_Percent__c", "rules_phone__c", "rules_c_Picklist__c", "rules_c_MultiPicklist__c", "rules_c_Text__c", "rules_c_TextArea__c", "rules_URL__c"};
        List<Map<String, String>> actualData = rulesUtil.getRecordsFromListofMap(PreviewResults, records);
        String expectedString = "[{\"Custom_Currency__c\":\"110.0\",\"C_lookup__r.Name\":\"RULESUI Account 1\",\"rules_c_Number__c\":\"1.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue1\",\"rules_c_TextArea__c\":\"RULESUI Account 1 TextArea 1\",\"rules_c_Percent__c\":\"1000.0\",\"rules_c_Checkbox__c\":\"true\",\"rules_c_Text__c\":\"RULESUI Account 1 Text 1\",\"rules_URL__c\":\"http:://gainsight.com\",\"rules_c_Picklist__c\":\"Pvalue1\"},{\"Custom_Currency__c\":\"200.0\",\"C_lookup__r.Name\":\"RULESUI Account 2\",\"rules_c_Number__c\":\"2.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue2\",\"rules_c_TextArea__c\":\"RULESUI Account 2 TextArea 1\",\"rules_c_Percent__c\":\"100.0\",\"rules_c_Checkbox__c\":\"false\",\"rules_c_Text__c\":\"RULESUI Account 2 Text 1\",\"rules_URL__c\":\"http:://google.com\",\"rules_c_Picklist__c\":\"Pvalue2\"},{\"Custom_Currency__c\":\"300.0\",\"C_lookup__r.Name\":\"RULESUI Account 3\",\"rules_c_Number__c\":\"3.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue3\",\"rules_c_TextArea__c\":\"RULESUI Account 3 TextArea 1\",\"rules_c_Percent__c\":\"12.0\",\"rules_c_Checkbox__c\":\"true\",\"rules_c_Text__c\":\"RULESUI Account 3 Text 1\",\"rules_URL__c\":\"http:://gainsight.com\",\"rules_c_Picklist__c\":\"Pvalue3\"},{\"Custom_Currency__c\":\"400.0\",\"C_lookup__r.Name\":\"RULESUI Account 4\",\"rules_c_Number__c\":\"4.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue1\",\"rules_c_TextArea__c\":\"RULESUI Account 4 TextArea 1\",\"rules_c_Percent__c\":\"50.0\",\"rules_c_Checkbox__c\":\"false\",\"rules_c_Text__c\":\"RULESUI Account 4 Text 1\",\"rules_URL__c\":\"http:://google.com\",\"rules_c_Picklist__c\":\"Pvalue1\"},{\"Custom_Currency__c\":\"500.0\",\"C_lookup__r.Name\":\"RULESUI Account 5\",\"rules_c_Number__c\":\"4.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue2\",\"rules_c_TextArea__c\":\"RULESUI Account 5 TextArea 1\",\"rules_c_Percent__c\":\"1000.0\",\"rules_c_Checkbox__c\":\"true\",\"rules_c_Text__c\":\"RULESUI Account 5 Text 1\",\"rules_URL__c\":\"http:://gainsight.com\",\"rules_c_Picklist__c\":\"Pvalue2\"},{\"Custom_Currency__c\":\"600.0\",\"C_lookup__r.Name\":\"RULESUI Account 6\",\"rules_c_Number__c\":\"6.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue3\",\"rules_c_TextArea__c\":\"RULESUI Account 6 TextArea 1\",\"rules_c_Percent__c\":\"100.0\",\"rules_c_Checkbox__c\":\"false\",\"rules_c_Text__c\":\"RULESUI Account 6 Text 1\",\"rules_URL__c\":\"http:://google.com\",\"rules_c_Picklist__c\":\"Pvalue3\"},{\"Custom_Currency__c\":\"700.0\",\"C_lookup__r.Name\":\"RULESUI Account 7\",\"rules_c_Number__c\":\"7.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue1\",\"rules_c_TextArea__c\":\"RULESUI Account 7 TextArea 1\",\"rules_c_Percent__c\":\"12.0\",\"rules_c_Checkbox__c\":\"true\",\"rules_c_Text__c\":\"RULESUI Account 7 Text 1\",\"rules_URL__c\":\"http:://gainsight.com\",\"rules_c_Picklist__c\":\"Pvalue1\"},{\"Custom_Currency__c\":\"800.0\",\"C_lookup__r.Name\":\"RULESUI Account 8\",\"rules_c_Number__c\":\"8.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue2\",\"rules_c_TextArea__c\":\"RULESUI Account 8 TextArea 1\",\"rules_c_Percent__c\":\"50.0\",\"rules_c_Checkbox__c\":\"false\",\"rules_c_Text__c\":\"RULESUI Account 8 Text 1\",\"rules_URL__c\":\"http:://google.com\",\"rules_c_Picklist__c\":\"Pvalue2\"},{\"Custom_Currency__c\":\"900.0\",\"C_lookup__r.Name\":\"RULESUI Account 9\",\"rules_c_Number__c\":\"9.0\",\"rules_c_Email__c\":\"athaduka@gainsight.com\",\"rules_phone__c\":\"123456789\",\"rules_c_MultiPicklist__c\":\"MPvalue3\",\"rules_c_TextArea__c\":\"RULESUI Account 9 TextArea 1\",\"rules_c_Percent__c\":\"1000.0\",\"rules_c_Checkbox__c\":\"true\",\"rules_c_Text__c\":\"RULESUI Account 9 Text 1\",\"rules_URL__c\":\"http:://google.com\",\"rules_c_Picklist__c\":\"Pvalue3\"}]";
        List<Map<String, String>> expectedData = mapper.readValue(expectedString, new TypeReference<List<Map<String, String>>>() {});
        Log.info("ExpectedData : " + mapper.writeValueAsString(expectedData));
        Log.info("ActualData : " + mapper.writeValueAsString(actualData));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(actualData.size(), expectedData.size(), "Number of records are not matched");
        Assert.assertEquals(differenceData.size(), 0, "expectedData and actualData is not matched!! , check the Diff above.");
    }

    @TestInfo(testCaseIds = {"GS-4974"})
    @Test
    public void testDataInShowFieldsAndFilterFields2() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4974/Input.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        List<Map<String, String>> PreviewResults = rulesUtil.getPreviewResults(rulesPojo.getRuleName(), "{\"numberOfRecords\":\"10\"}");
        Log.info("Total Records are " + mapper.writeValueAsString(PreviewResults));
        String records[] = {"C_lookup__r.Name"};
        List<Map<String, String>> actualData = rulesUtil.getRecordsFromListofMap(PreviewResults, records);
        String expectedString = "[{\"C_lookup__r.Name\":\"RULESUI Account 1\"},{\"C_lookup__r.Name\":\"RULESUI Account 2\"},{\"C_lookup__r.Name\":\"RULESUI Account 4\"},{\"C_lookup__r.Name\":\"RULESUI Account 6\"},{\"C_lookup__r.Name\":\"RULESUI Account 8\"}]";
        List<Map<String, String>> expectedData = mapper.readValue(expectedString, new TypeReference<List<Map<String, String>>>() {});
        Log.info("ExpectedData : " + mapper.writeValueAsString(expectedData));
        Log.info("ActualData : " + mapper.writeValueAsString(actualData));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(actualData.size(), expectedData.size(), "Number of records are not matched after applying filters");
        Assert.assertEquals(differenceData.size(), 0, "expectedData and actualData is not matched!! , check the Diff above.");
    }

    @TestInfo(testCaseIds = {"GS-4985"})
    @Test
    public void testDataInShowFieldsAndFilterFields3() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4985/Input.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        List<Map<String, String>> PreviewResults = rulesUtil.getPreviewResults(rulesPojo.getRuleName(), "{\"numberOfRecords\":\"10\"}");
        Log.info("Total Records are " + mapper.writeValueAsString(PreviewResults));
        String records[] = {"C_lookup__r.Name"};
        List<Map<String, String>> actualData = rulesUtil.getRecordsFromListofMap(PreviewResults, records);
        String expectedString = "[{\"C_lookup__r.Name\":\"RULESUI Account 1\"},{\"C_lookup__r.Name\":\"RULESUI Account 2\"},{\"C_lookup__r.Name\":\"RULESUI Account 3\"},{\"C_lookup__r.Name\":\"RULESUI Account 4\"},{\"C_lookup__r.Name\":\"RULESUI Account 5\"},{\"C_lookup__r.Name\":\"RULESUI Account 6\"},{\"C_lookup__r.Name\":\"RULESUI Account 7\"},{\"C_lookup__r.Name\":\"RULESUI Account 8\"},{\"C_lookup__r.Name\":\"RULESUI Account 9\"}]";
        List<Map<String, String>> expectedData = mapper.readValue(expectedString, new TypeReference<List<Map<String, String>>>() {});
        Log.info("ExpectedData : " + mapper.writeValueAsString(expectedData));
        Log.info("ActualData : " + mapper.writeValueAsString(actualData));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(actualData.size(), expectedData.size(), "Number of records are not matched after applying filters");
        Assert.assertEquals(differenceData.size(), 0, "expectedData and actualData is not matched!! , check the Diff above.");
    }
}
