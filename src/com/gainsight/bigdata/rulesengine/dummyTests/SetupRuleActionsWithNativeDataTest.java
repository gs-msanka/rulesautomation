package com.gainsight.bigdata.rulesengine.dummyTests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class SetupRuleActionsWithNativeDataTest extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private NSTestBase nsTestBase = new NSTestBase();
    private DataETL dataETL = new DataETL();
    private ObjectMapper mapper = new ObjectMapper();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private String rulesManagerPageUrl;
    private RulesManagerPage rulesManagerPage;
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup;

    @BeforeClass
    public void setup() throws Exception {
        basepage.login();
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
        sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObject.txt"), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @Test(dataProvider = "testData")
    public void testDataInShowFieldsAndFilterFields(String fileName) throws Exception {
        Log.info("Creating rule with testdata " + fileName);
        RulesPojo rulesPojo = mapper.readValue(new File(fileName), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }

    @DataProvider(name = "testData")
    public Object[][] getDataFromDataProvider() {

        return new Object[][]{
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4649/Input.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4974/Input.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4985/Input.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4986/Input.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4990/Input.json"}
        };
    }
}
