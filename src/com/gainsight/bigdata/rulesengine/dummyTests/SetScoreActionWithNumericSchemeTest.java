package com.gainsight.bigdata.rulesengine.dummyTests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
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
public class SetScoreActionWithNumericSchemeTest extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String METRICS_CREATE_FILE = Application.basedir + "/apex_scripts/scorecard/Create_ScorecardMetrics.apex";
    private static final String SCORECARD_CLEAN_FILE = Application.basedir + "/apex_scripts/scorecard/Scorecard_CleanUp.txt";
    private static final String NUMERIC_SCHEME_FILE = Application.basedir + "/apex_scripts/scorecard/Scorecard_enable_numeric.apex";
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
    private NSTestBase nsTestBase = new NSTestBase();
    private DataETL dataETL = new DataETL();
    private ObjectMapper mapper = new ObjectMapper();
    private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private AdminScorecardSection adminScorecardSection;
    private String scoreCardDomain;
    private String rulesManagerPageUrl;
    private RulesManagerPage rulesManagerPage;

    @BeforeClass
    public void setup() throws Exception {
        basepage.login();
        nsTestBase.init();
        scoreCardDomain = visualForcePageUrl + "scorecardsetup";
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        metaUtil.createExtIdFieldForScoreCards(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SCORECARD_CLEAN_FILE));
        adminScorecardSection = new AdminScorecardSection("test");
        adminScorecardSection.openScoreCardSectionPage(scoreCardDomain);
        adminScorecardSection.enableScorecard();
        runMetricSetup(METRICS_CREATE_FILE, "Score");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(NUMERIC_SCHEME_FILE));
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
/*        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));*/
        sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3385/GS-3385-Job-LoadDataIntoCustomObject.txt"), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @Test(dataProvider = "testData")
    public void setScoreWithNumericSchemeEnabled(String fileName) throws Exception {
        Log.info("Creating rule with testdata " + fileName);
        RulesPojo rulesPojo = mapper.readValue(new File(fileName), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }

    @DataProvider(name = "testData")
    public Object[][] getDataFromDataProvider() {

        return new Object[][]{
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3385/GS-3385-Input.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3386/GS-3386-Input.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3825/GS-3825-Input.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3837/GS-3837-Input.json"}
        };
    }
}
