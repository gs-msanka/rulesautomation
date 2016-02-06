package com.gainsight.bigdata.rulesengine.dummyTests;

import java.io.File;
import java.io.FileReader;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class LoadToMilestonesAndFeaturesTest extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts.txt";
    private static final String LOAD_DATA_INTO_CUSTOMOBJECT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Load_Data_Into_CustomObject.txt";
    RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
    private ObjectMapper mapper = new ObjectMapper();
    NSTestBase nsTestBase = new NSTestBase();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    DataETL dataETL = new DataETL();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;

    @BeforeClass
    public void setUpData() throws Exception {
        basepage.login();
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
/*        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);*/
        JobInfo loadData = mapper.readValue((new FileReader(LOAD_DATA_INTO_CUSTOMOBJECT)), JobInfo.class);
        dataETL.execute(loadData);
    }

    @Test
    public void testLoadToMileStoneActionUsingDateTimeField() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC36.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }
}
