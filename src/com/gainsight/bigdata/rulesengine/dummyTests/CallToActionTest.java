package com.gainsight.bigdata.rulesengine.dummyTests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CloseCtaAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class CallToActionTest extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts.txt";
    private ObjectMapper mapper = new ObjectMapper();
    private RulesUtil rulesUtil = new RulesUtil();
    private String rulesManagerPageUrl;
    private NSTestBase nsTestBase = new NSTestBase();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private RulesManagerPage rulesManagerPage;
    private DataETL dataETL = new DataETL();


    @BeforeClass
    public void setUp() throws Exception {
        basepage.login();
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        rulesUtil.populateObjMaps();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
    }


    @Test(dataProvider = "testData")
    public void testCallToAction(String fileName) throws Exception {
        Log.info("Creating rule with testdata " + fileName);
        RulesPojo rulesPojo = mapper.readValue(new File(fileName), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }

    @Test
    public void testCtaUpsertWithSnoozeOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC28.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);

        // Updating all Cta's to snooze till Date.today()+5 via script
        sfdc.runApexCode(resolveStrNameSpace("JBCXM__PickList__c pick=[SELECT Id,JBCXM__SystemName__c,JBCXM__ShortName__c  FROM JBCXM__PickList__c where JBCXM__SystemName__c like '%snooze%' and JBCXM__ShortName__c like '%Other%' limit 1];List<JBCXM__CTA__c> cta = [select Id,JBCXM__SnoozedUntil__c, JBCXM__SnoozeReason__c from JBCXM__CTA__c];for(JBCXM__CTA__c snooze :cta){snooze.JBCXM__SnoozeReason__c = pick.Id;snooze.JBCXM__SnoozedUntil__c=Date.today()+5;}update cta;"));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo TC28_Upsert = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC28_Upsert.json"), RulesPojo.class);
        RuleAction ruleActions2 = TC28_Upsert.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            rulesManagerPage.editRuleByName(TC28_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(TC28_Upsert);
        }
    }

    @Test
    public void testCloseCtaFromSpecificSource() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC32.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);

        //   Again Editing same cta, since scenario is to create close cta action for the cta which is already existing
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo closeCtaPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC32-CloseCta.json"), RulesPojo.class);
        CloseCtaAction closeCtaAction = null;
        if (closeCtaPojo.getSetupActions().get(0).getActionType().name().contains("CloseCTA")) {
            JsonNode actionObject = closeCtaPojo.getSetupActions().get(0).getAction();
            closeCtaAction = mapper.readValue(actionObject, CloseCtaAction.class);
            rulesManagerPage.editRuleByName(closeCtaPojo.getRuleName());
            rulesEngineUtil.createRuleFromUi(closeCtaPojo);
        }
    }

    @Test
    public void testCloseCtaFromAllSources() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC33.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);

        // Again Editing same cta, since scenario is to create close cta action for the cta which is already existing
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo closeCtaPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC33-CloseCta.json"), RulesPojo.class);
        CloseCtaAction closeCtaAction = null;
        if (closeCtaPojo.getSetupActions().get(0).getActionType().name().contains("CloseCTA")) {
            JsonNode actionObject = closeCtaPojo.getSetupActions().get(0).getAction();
            closeCtaAction = mapper.readValue(actionObject, CloseCtaAction.class);
            rulesManagerPage.editRuleByName(closeCtaPojo.getRuleName());
            rulesEngineUtil.createRuleFromUi(closeCtaPojo);

        }
    }

    @DataProvider(name = "testData")
    public Object[][] getDataFromDataProvider() {
        return new Object[][]{
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC23.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC24.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC25.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC26.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC27.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC29.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC30.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC31.json"}
        };
    }
}
