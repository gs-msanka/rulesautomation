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

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class LoadToUsageTest extends BaseTest {

    private static final String USAGE_DATA_MEASURE_FILE = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/UsageData_Measures.apex";
    private static final String ACCOUNT_LEVEL_WEEKLY_FILE = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Set_Account_Level_Weekly.apex";
    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
    private ObjectMapper mapper = new ObjectMapper();
    private DataETL dataETL = new DataETL();
    private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private NSTestBase nsTestBase = new NSTestBase();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();


    @BeforeClass
    public void setup() throws Exception {
        basepage.login();
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        metaUtil.createFieldsOnUsageData(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(USAGE_DATA_MEASURE_FILE));
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
    }

    @BeforeMethod
    public void cleanup() {
        sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__UsageData__c];"));
    }

    @Test()
    public void loadToUsageActionWithAccountLevelWeeklyData() throws Exception {
        sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ACCOUNT_LEVEL_WEEKLY_FILE));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/GS-5148-Job-LoadDataIntoCustomObject.txt"), JobInfo.class);
        dataETL.execute(jobInfo);
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5148/GS-5148-Input.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }
}
