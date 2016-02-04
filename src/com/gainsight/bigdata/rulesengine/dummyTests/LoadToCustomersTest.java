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
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class LoadToCustomersTest extends BaseTest {

    private static final String CREATE_ACCOUNTS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts.txt";
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private ObjectMapper mapper = new ObjectMapper();
    private NSTestBase nsTestBase = new NSTestBase();
    RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
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
    }

    @Test()
    public void testLoadToCustomers() throws Exception {
        sfdc.runApexCode(resolveStrNameSpace("Delete [select id from Account where name like '%rule%'];"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS));
        sfdc.runApexCode(resolveStrNameSpace("Delete [select id from C_Custom__c];"));
        JobInfo loadData = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-5152.txt")), JobInfo.class);
        dataETL.execute(loadData);
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC44.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }
}
