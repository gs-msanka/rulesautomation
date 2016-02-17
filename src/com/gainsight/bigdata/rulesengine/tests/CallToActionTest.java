package com.gainsight.bigdata.rulesengine.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CloseCtaAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;
import com.gainsight.util.Comparator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Abhilash Thaduka on 1/8/2016.
 */
public class CallToActionTest extends BaseTest {
    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts.txt";
    private static final String CLEANUP_DATA = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
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
        sfdc.connect();
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        rulesUtil.populateObjMaps();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_DATA));
    }

    @TestInfo(testCaseIds = {"GS-4185", "GS-4186", "GS-4257"})
    @Test
    public void testCtaWithUpsertPriorityOption() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
        // Creating cta with Low priority
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC23.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias())));
        Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // Updating cta with high priority
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo TC23_Upsert = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC23_Upsert.json"), RulesPojo.class);
        RuleAction ruleActions2 = TC23_Upsert.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction2 = mapper.readValue(TC23_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
            rulesManagerPage.editRuleByName(TC23_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(TC23_Upsert);
            Assert.assertTrue(rulesUtil.runRule(TC23_Upsert.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction2.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction2.getReason(), ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction2.getName() + "' and  JBCXM__Source__c='Rules' and isdeleted=false"))));
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo2 = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act2 = mapper.readValue(TC23_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo2.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act2.getPlaybook() + "'");
        dataETL.execute(jobInfo2);
        List<Map<String, String>> expectedTasks2 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks2 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData2 = Comparator.compareListData(expectedTasks2, actualTasks2);
        Assert.assertEquals(actualTasks2.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData2));
        Assert.assertEquals(differenceData2.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // Updating same cta with Low priority again
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction3 = mapper.readValue(TC23_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
            // Setting cta priority to Low
            ctaAction3.setPriority("Low");
            ruleActions2.setAction(mapper.convertValue(ctaAction3, JsonNode.class));
            rulesManagerPage.editRuleByName(TC23_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(TC23_Upsert);
            Assert.assertTrue(rulesUtil.runRule(TC23_Upsert.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully("High", ctaAction3.getStatus(), sfdcInfo.getUserId(), ctaAction3.getType(), ctaAction3.getReason(), ctaAction3.getComments(), ctaAction3.getName(), ctaAction3.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction3.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo1 = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act1 = mapper.readValue(TC23_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo1.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act1.getPlaybook() + "'");
        dataETL.execute(jobInfo1);
        List<Map<String, String>> expectedTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedTasks1, actualTasks1);
        Assert.assertEquals(actualTasks1.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4256", "GS-4257"})
    @Test
    public void testCtaWithUpdateCommentsAlwaysOption() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC24.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias())));
        Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo1 = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act1 = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo1.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act1.getPlaybook() + "'");
        dataETL.execute(jobInfo1);
        List<Map<String, String>> expectedTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedTasks1, actualTasks1);
        Assert.assertEquals(actualTasks1.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo TC24_Upsert = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC24_Upsert.json"), RulesPojo.class);
        RuleAction ruleActions2 = TC24_Upsert.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction2 = mapper.readValue(TC24_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
            rulesManagerPage.editRuleByName(TC24_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(TC24_Upsert);
            Assert.assertTrue(rulesUtil.runRule(TC24_Upsert.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction2.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction2.getReason(), ctaAction2.getComments() + "\n" + "\n" + ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted in  correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction2.getName() + "' and  JBCXM__Source__c='Rules' and isdeleted=false"))));
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(TC24_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4257"})
    @Test
    public void testCtaWithUpdateCommentsNeverOption() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC25.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), null, ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias())));
        Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 18, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4258"})
    @Test
    public void testCtaWithAddOrReplacePlaybook() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
        // Creating cta with no playbook
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC26.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details!");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), null), "verify whether cta action configured resulted correct cta or not");
        int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias())));
        Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));

        // Updating same cta with another playbook
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo upsertJson = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC26_Upsert.json"), RulesPojo.class);
        RuleAction ruleActions2 = upsertJson.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction2 = mapper.readValue(upsertJson.getSetupActions().get(0).getAction(), CTAAction.class);
            rulesManagerPage.editRuleByName(upsertJson.getRuleName());
            rulesEngineUtil.createRuleFromUi(upsertJson);
            Assert.assertTrue(rulesUtil.runRule(upsertJson.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction2.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction2.getReason(), ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction2.getName() + "' and  JBCXM__Source__c='Rules' and isdeleted=false"))));
        }
        //Verifying CS tasks of a CTA for the playbook applied above
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(upsertJson.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // updating cta to other playbook
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo upsert_Cta = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC26_Upsert_2.json"), RulesPojo.class);
        RuleAction ruleActions3 = upsertJson.getSetupActions().get(0);
        if (ruleActions3.getActionType().name().contains("CTA") && ruleActions3.isUpsert()) {
            CTAAction ctaAction3 = mapper.readValue(upsert_Cta.getSetupActions().get(0).getAction(), CTAAction.class);
            // Setting playbook to other
            ctaAction3.setPlaybook("Decline in usage");
            ruleActions3.setAction(mapper.convertValue(ctaAction3, JsonNode.class));
            rulesManagerPage.editRuleByName(upsert_Cta.getRuleName());
            rulesEngineUtil.createRuleFromUi(upsert_Cta);
            Assert.assertTrue(rulesUtil.runRule(upsert_Cta.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction3.getPriority(), ctaAction3.getStatus(), sfdcInfo.getUserId(), ctaAction3.getType(), ctaAction3.getReason(), ctaAction3.getComments(), ctaAction3.getName(), "Collections"), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction3.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));
        }
        //Verifying CS tasks of a CTA for the playbook changed above(Playbook should not be changed)
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        List<Map<String, String>> expectedTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedTasks1, actualTasks1);
        Assert.assertEquals(actualTasks1.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-6247"})
    @Test
    public void testCtaWithRuleNameChangeOption() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC27.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias())));
        Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo TC27_Upsert = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC27_Upsert.json"), RulesPojo.class);
        RuleAction ruleActions2 = TC27_Upsert.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction2 = mapper.readValue(TC27_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
            // Changing cta name
            ctaAction2.setName(ctaAction.getName() + "NewName");
            ruleActions2.setAction(mapper.convertValue(ctaAction2, JsonNode.class));
            rulesManagerPage.editRuleByName(TC27_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(TC27_Upsert);
            Assert.assertTrue(rulesUtil.runRule(TC27_Upsert.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction2.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction.getReason(), ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted in  correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and  JBCXM__Source__c='Rules' and isdeleted=false"))));
        }
        //Verifying CS tasks of a CTA for the playbook above
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo1 = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act1 = mapper.readValue(TC27_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo1.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act1.getPlaybook() + "'");
        dataETL.execute(jobInfo1);
        List<Map<String, String>> expectedTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks1 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedTasks1, actualTasks1);
        Assert.assertEquals(actualTasks1.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4264"})
    @Test
    public void testCtaUpsertWithSnoozeOption() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC28.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias())));
        Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))));

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // Updating all Cta's to snooze till Date.today()+5 via script
        sfdc.runApexCode(resolveStrNameSpace("JBCXM__PickList__c pick=[SELECT Id,JBCXM__SystemName__c,JBCXM__ShortName__c  FROM JBCXM__PickList__c where JBCXM__SystemName__c like '%snooze%' and JBCXM__ShortName__c like '%Other%' limit 1];List<JBCXM__CTA__c> cta = [select Id,JBCXM__SnoozedUntil__c, JBCXM__SnoozeReason__c from JBCXM__CTA__c];for(JBCXM__CTA__c snooze :cta){snooze.JBCXM__SnoozeReason__c = pick.Id;snooze.JBCXM__SnoozedUntil__c=Date.today()+5;}update cta;"));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo TC28_Upsert = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC28_Upsert.json"), RulesPojo.class);
        RuleAction ruleActions2 = TC28_Upsert.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction2 = mapper.readValue(TC28_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
            rulesManagerPage.editRuleByName(TC28_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(TC28_Upsert);
            Assert.assertTrue(rulesUtil.runRule(TC28_Upsert.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction2.getReason(), ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted in  correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where isdeleted=false"))));
        }

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo2 = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act2 = mapper.readValue(TC28_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo2.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act2.getPlaybook() + "'");
        dataETL.execute(jobInfo2);
        List<Map<String, String>> expectedTasks2 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks2 = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData2 = Comparator.compareListData(expectedTasks2, actualTasks2);
        Assert.assertEquals(actualTasks2.size(), 18, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData2));
        Assert.assertEquals(differenceData2.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4261"})
    @Test
    public void testCtaActionWithDonNotSkipWeekendOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC29.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        String date = DateUtil.getDateWithRequiredFormat(Integer.valueOf(ctaAction.getDueDate()), 0, "yyyy-MM-dd");
        Log.info("Duedate is " + date);
        SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name,JBCXM__DueDate__c FROM JBCXM__CTA__c  where isdeleted=false"));
        Log.info("Total Records : " + objRecords.length);
        for (SObject sObject : objRecords) {
            Assert.assertEquals(date, sObject.getField(resolveStrNameSpace("JBCXM__DueDate__c")), "Check DueDate is not matching !!");
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 9, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }


    @TestInfo(testCaseIds = {"GS-4261"})
    @Test
    public void testCtaActionWithSkipAllWeekendsOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC30.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone(sfdcInfo.getUserTimeZone()));
        int weekEndDays = RulesEngineUtil.getcountOfDaysIfCtaCreatedOnWeekend(0);
        System.out.println(weekEndDays);
        String dueDate = RulesEngineUtil.getCtaDateForCTASkipAllWeekendsOption(Integer.valueOf(ctaAction.getDueDate()) - weekEndDays);
        Log.info("Duedate is " + dueDate);
        SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name,JBCXM__DueDate__c FROM JBCXM__CTA__c where isdeleted=false"));
        Log.info("Total Records : " + objRecords.length);
        for (SObject sObject : objRecords) {
            Assert.assertEquals(dueDate, sObject.getField(resolveStrNameSpace("JBCXM__DueDate__c")), "Check DueDate is not matching !!");
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 9, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4261"})
    @Test
    public void testCtaActionWithSkipWeekendIfDueOnWeekEndOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC31.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        int days = RulesEngineUtil.getcountOfDaysIfCtaCreatedOnWeekend(Integer.valueOf(ctaAction.getDueDate()));
        String date = DateUtil.getDateWithRequiredFormat(Integer.valueOf(ctaAction.getDueDate()) + days, 0, "yyyy-MM-dd");
        SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name,JBCXM__DueDate__c FROM JBCXM__CTA__c"));
        Log.info("Total Records : " + objRecords.length);
        for (SObject sObject : objRecords) {
            Assert.assertEquals(date, sObject.getField(resolveStrNameSpace("JBCXM__DueDate__c")), "Check DueDate is not matching !!");
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 9, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-3873", "GS-4185"})
    @Test
    // This testcase handles owner field userlookup and cta token also for create cta action
    public void testCloseCtaFromSpecificSource() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC32.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 9, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
        //   Again Editing same cta, since scenario is to create close cta action for the cta which is already existing
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo closeCtaPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC32-CloseCta.json"), RulesPojo.class);
        CloseCtaAction closeCtaAction = null;
        if (closeCtaPojo.getSetupActions().get(0).getActionType().name().contains("CloseCTA")) {
            JsonNode actionObject = closeCtaPojo.getSetupActions().get(0).getAction();
            closeCtaAction = mapper.readValue(actionObject, CloseCtaAction.class);
            rulesManagerPage.editRuleByName(closeCtaPojo.getRuleName());
            rulesEngineUtil.createRuleFromUi(closeCtaPojo);
            Assert.assertTrue(rulesUtil.runRule(closeCtaPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTAclosedSuccessfully(closeCtaAction), "check cta is closed with correct parammers or not");
            CTAAction ctaAction = mapper.readValue(closeCtaPojo.getSetupActions().get(0).getAction(), CTAAction.class);
            SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
            SObject[] records = sfdc.getRecords((resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias()))));
            int srcObjRecCount = records.length;
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and  JBCXM__Source__c='Rules' and JBCXM__ClosedDate__c!=null and isdeleted=false"))));
            String names = getAccountNamesAsStringFromAccountObject(records, "Name", null, false);
            SObject[] ctarecords = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name,JBCXM__Account__r.Name,JBCXM__Account__r.Id,JBCXM__Comments__c,JBCXM__Account__r.C_Picklist__c," + "JBCXM__Account__r.Percent_Auto__c FROM JBCXM__CTA__c where JBCXM__Account__r.Name in (" + names + ") and isDeleted=false"));
            // Since UI names and API names are different, writing a common util will be error prone always.
            for (SObject tokenRecords : ctarecords) {
                String ctaComment = (String) tokenRecords.getField(resolveStrNameSpace("JBCXM__Comments__c"));
                String actualTokenComments = (String) tokenRecords.getChild((resolveStrNameSpace("JBCXM__Account__r"))).getChild("Name").getValue() + tokenRecords.getChild(resolveStrNameSpace("JBCXM__Account__r")).getChild("Id").getValue() + tokenRecords.getChild(resolveStrNameSpace("JBCXM__Account__r")).getChild("Percent_Auto__c").getValue() + tokenRecords.getChild(resolveStrNameSpace("JBCXM__Account__r")).getChild("C_Picklist__c").getValue();
                // Asserting both create cta and close cta comments
                Assert.assertEquals(ctaComment, actualTokenComments + "\n" + "\n" + actualTokenComments);
            }
        }
    }

    @TestInfo(testCaseIds = {"GS-3874"})
    @Test
    public void testCloseCtaFromAllSources() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC33.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv"), null)));
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 9, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
        // Again Editing same cta, since scenario is to create close cta action for the cta which is already existing
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo closeCtaPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC33-CloseCta.json"), RulesPojo.class);
        CloseCtaAction closeCtaAction = null;
        if (closeCtaPojo.getSetupActions().get(0).getActionType().name().contains("CloseCTA")) {
            JsonNode actionObject = closeCtaPojo.getSetupActions().get(0).getAction();
            closeCtaAction = mapper.readValue(actionObject, CloseCtaAction.class);
            rulesManagerPage.editRuleByName(closeCtaPojo.getRuleName());
            rulesEngineUtil.createRuleFromUi(closeCtaPojo);
            Assert.assertTrue(rulesUtil.runRule(closeCtaPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            CTAAction ctaAction = mapper.readValue(closeCtaPojo.getSetupActions().get(0).getAction(), CTAAction.class);
            Assert.assertTrue((rulesUtil.isCTAclosedSuccessfully(closeCtaAction)), "check cta is closed with correct parammers or not");
            SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
            int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(rulesPojo.getSetupActions().get(0).getCriterias())));
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and  JBCXM__Source__c='Rules' and JBCXM__ClosedDate__c!=null and isdeleted=false"))));
        }
    }

    private static String getAccountNamesAsStringFromAccountObject(SObject[] records, String accountIdentifier1, String accountIdentifier2, boolean isRelation) {
        List<String> accounts = new ArrayList<String>();
        String accountNames = "";
        for (SObject record : records) {
            if (!isRelation) {
                accountNames += record.getField(accountIdentifier1) + ",";
            } else {
                accountNames += record.getChild(accountIdentifier1).getChild(accountIdentifier2).getValue().toString() + ",";
            }
        }
        if (accountNames.endsWith(",")) {
            accountNames = accountNames.substring(0, accountNames.length() - 1);
        }
        List<String> temp = Arrays.asList(accountNames.split(","));
        for (int i = 0; i < temp.size(); i++) {
            accounts.add("'" + temp.get(i) + "'");
        }
        String names = accounts.toString().substring(accounts.toString().indexOf("'"), accounts.toString().length() - 1);
        return names;
    }
}