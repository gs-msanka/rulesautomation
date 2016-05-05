package com.gainsight.bigdata.rulesengine.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CloseCtaAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.util.MDACollectionCreator;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.http.Header;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

/**
 * Created by msanka on 4/19/2016.
 */
public class CallToActionTestUsingMatrixData extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts.txt";
    private static final String CLEANUP_DATA = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private ObjectMapper mapper = new ObjectMapper();
    private RulesUtil rulesUtil = new RulesUtil();
    private String rulesManagerPageUrl;
    private NSTestBase nsTestBase = new NSTestBase();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private RulesManagerPage rulesManagerPage;
    private TenantDetails tenantDetails = null;
    private DataETL dataETL = new DataETL();
    private TenantManager tenantManager;
    MongoDBDAO mongoDBDAO = null;
    private Date date = Calendar.getInstance().getTime();
    private String collectionName = null;
    private static final String RULE_SCRIPTS_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/";
    private static final String GLOBAL_TEST_DATA_DIR = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/";
    private static final String TEST_DATA_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/";
    private static final String RULE_JOBS = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/";
    private static final String EXPECTED_UI_TESTDATA_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/";



    @BeforeClass
    @Parameters("dbStoreType")
    public void setUp(@Optional("Redshift") String dbStoreType) throws Exception {
        nsTestBase.sfdc = sfdc;
        nsTestBase.setGSHeaders();
        tenantManager = new TenantManager();
        String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
        tenantDetails = tenantManager.getTenantDetail(null, tenantId);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        if (StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Mongo")) {
            if(tenantDetails.isRedshiftEnabled()){
                mongoDBDAO = MongoDBDAO.getGlobalMongoDBDAOInstance();
                assertTrue(mongoDBDAO.disableRedshift(tenantId), "Failed updating dataStoreType to Mongo.");
            }
        }
        else if(StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Redshift")){
            if(!tenantDetails.isRedshiftEnabled()) {
                assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails), "Failed updating dataStoreType to Redshift");
            }
        }
        ExecutorService executors = Executors.newFixedThreadPool(4);
        Future<String> task = null;
        boolean isLoadTestDataGlobally = true;
        if (isLoadTestDataGlobally) {
            dataETL.execute(mapper.readValue(resolveNameSpace(GLOBAL_TEST_DATA_DIR + "DataLoadJobMatrixCTA.txt"),JobInfo.class));
            JobInfo loadTransform = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "DataloadJob.txt")), JobInfo.class);
            File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
            String[] validFields = new String[] { "ID", "AccountName", "CustomDate1", "PageViews", "Logins", "Description"};
            task = executors.submit(new MDACollectionCreator("CollectionSchemaForCTA.json", dbStoreType, dataFile, validFields));
        }
        sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__ActionTemplates__c];"));
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        rulesUtil.populateObjMaps();
        this.collectionName = task.get();
        assertTrue(StringUtils.isNotBlank(this.collectionName),"Collection name can not be Blank;");
    }


    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_DATA));
    }
/*
    @TestInfo(testCaseIds = {"GS-4185", "GS-4186", "GS-4257", "GS-4256", "GS-4257"})
    @Test(enabled = true)
    public void testCtaWithUpsertPriorityAndCommentsAlwaysOption() throws Exception {
        // Creating cta with Low priority
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-4185/GS-4185-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"),JobInfo.class));
        List<Map<String, String>> filteredAccounts = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
        int  srcObjRecCount = filteredAccounts.size();
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"No.of accounts for which cta's should be created is not matching");

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // Updating cta with high priority
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo CTA_Upsert = mapper.readValue(new File(TEST_DATA_DIR + "GS-4185/GS-4185-Matrix-Upsert-input.json"), RulesPojo.class);
        RuleAction ruleActions2 = CTA_Upsert.getSetupActions().get(0);
        CTAAction ctaAction2 = null;
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            ctaAction2 = mapper.readValue(CTA_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
            rulesManagerPage.editRuleByName(CTA_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(CTA_Upsert);
            Assert.assertTrue(rulesUtil.runRule(CTA_Upsert.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction2.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction2.getReason(), ctaAction.getComments()+ "\n" + "\n" + ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction2.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"No.of accounts for which cta's should be created is not matching");
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo2 = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act2 = mapper.readValue(CTA_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo2.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act2.getPlaybook() + "'");
        dataETL.execute(jobInfo2);
        List<Map<String, String>> expectedTasks2 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks2 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData2 = Comparator.compareListData(expectedTasks2, actualTasks2);
        Assert.assertEquals(actualTasks2.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData2));
        Assert.assertEquals(differenceData2.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // Updating same cta with Low priority again
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction3 = mapper.readValue(CTA_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
            // Setting cta priority to Low
            ctaAction3.setPriority("Low");
            ruleActions2.setAction(mapper.convertValue(ctaAction3, JsonNode.class));
            rulesManagerPage.editRuleByName(CTA_Upsert.getRuleName());
            rulesEngineUtil.createRuleFromUi(CTA_Upsert);
            Assert.assertTrue(rulesUtil.runRule(CTA_Upsert.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully("High", ctaAction3.getStatus(), sfdcInfo.getUserId(), ctaAction3.getType(), ctaAction3.getReason(),ctaAction.getComments()+ "\n" + "\n" + ctaAction2.getComments()+ "\n" + "\n" + ctaAction3.getComments(), ctaAction3.getName(), ctaAction3.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction3.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"No.of accounts for which cta's should be created is not matching");
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo1 = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act1 = mapper.readValue(CTA_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo1.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act1.getPlaybook() + "'");
        dataETL.execute(jobInfo1);
        List<Map<String, String>> expectedTasks1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedTasks1, actualTasks1);
        Assert.assertEquals(actualTasks1.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }*/


    @TestInfo(testCaseIds = { "GS-4257"})
    @Test(enabled = false)
    public void testCtaWithUpdateCommentsNeverOption() throws Exception {
        // Creating cta with Low priority
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-4257/GS-4257-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), null, ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"),JobInfo.class));
        List<Map<String, String>> filteredAccounts = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
        int  srcObjRecCount = filteredAccounts.size();
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"No.of accounts for which cta's should be created is not matching");

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4258"})
    @Test(enabled = false)
    public void testCtaWithAddOrReplacePlaybook() throws Exception {
        // Creating cta with no playbook
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-4258/GS-4258-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details!");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), null), "verify whether cta action configured resulted correct cta or not");
        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"),JobInfo.class));
        List<Map<String, String>> filteredAccounts = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
        int  srcObjRecCount = filteredAccounts.size();
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount, "No.of accounts for which cta's should be created is not matching");

        // Updating same cta with another playbook
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo upsertJson = mapper.readValue(new File(TEST_DATA_DIR + "GS-4258/GS-4258-Matrix-Upsert-input.json"), RulesPojo.class);
        RuleAction ruleActions2 = upsertJson.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction2 = mapper.readValue(upsertJson.getSetupActions().get(0).getAction(), CTAAction.class);
            rulesManagerPage.editRuleByName(upsertJson.getRuleName());
            rulesEngineUtil.createRuleFromUi(upsertJson);
            Assert.assertTrue(rulesUtil.runRule(upsertJson.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction2.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction2.getReason(), ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction2.getName() + "' and  JBCXM__Source__c='Rules' and isdeleted=false"))),"No.of accounts for which cta's should be created is not matching");
        }
        //Verifying CS tasks of a CTA for the playbook applied above
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(upsertJson.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // updating cta to other playbook
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction3 = mapper.readValue(upsertJson.getSetupActions().get(0).getAction(), CTAAction.class);
            // Setting playbook to other
            ctaAction3.setPlaybook("Decline in usage");
            ruleActions2.setAction(mapper.convertValue(ctaAction3, JsonNode.class));
            rulesManagerPage.editRuleByName(upsertJson.getRuleName());
            rulesEngineUtil.createRuleFromUi(upsertJson);
            Assert.assertTrue(rulesUtil.runRule(upsertJson.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction3.getPriority(), ctaAction3.getStatus(), sfdcInfo.getUserId(), ctaAction3.getType(), ctaAction3.getReason(), ctaAction3.getComments(), ctaAction3.getName(), "Collections"), "verify whether cta action configured resulted correct cta or not");
            Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction3.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount, "No.of accounts for which cta's should be created is not matching");
        }
        //Verifying CS tasks of a CTA for the playbook changed above(Playbook should not be changed)
        dataETL.execute(mapper.readValue(resolveNameSpace(EXPECTED_UI_TESTDATA_DIR + "CSTasks.txt"), JobInfo.class));
        List<Map<String, String>> expectedTasks1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedTasks1, actualTasks1);
        Assert.assertEquals(actualTasks1.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    /*@TestInfo(testCaseIds = {"GS-6247"})
    @Test()
    public void testCtaWithRuleNameChangeOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-6247/GS-6247-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"),JobInfo.class));
        List<Map<String, String>> filteredAccounts = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
        int  srcObjRecCount = filteredAccounts.size();
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount, "No.of accounts for which cta's should be created is not matching" );

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo TC27_Upsert = mapper.readValue(new File(TEST_DATA_DIR + "GS-6247/GS-6247-Matrix-Upsert-input.json"), RulesPojo.class);
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
            Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and  JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount, "No.of accounts for which cta's should be created is not matching");
        }
        //Verifying CS tasks of a CTA for the playbook above
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo1 = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act1 = mapper.readValue(TC27_Upsert.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo1.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act1.getPlaybook() + "'");
        dataETL.execute(jobInfo1);
        List<Map<String, String>> expectedTasks1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedTasks1, actualTasks1);
        Assert.assertEquals(actualTasks1.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }
*/

   /* @TestInfo(testCaseIds = {"GS-4264"})
    @Test()
    public void testCtaUpsertWithSnoozeOption() throws Exception {

        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-4264/GS-4264-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"), JobInfo.class));
        List<Map<String, String>> filteredAccounts = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
        int  srcObjRecCount = filteredAccounts.size();
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount, "No.of accounts for which cta's should be created is not matching" );

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR +"CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");

        // Updating all Cta's to snooze till Date.today()+5 via script
        sfdc.runApexCode(resolveStrNameSpace("JBCXM__PickList__c pick=[SELECT Id,JBCXM__SystemName__c,JBCXM__ShortName__c  FROM JBCXM__PickList__c where JBCXM__SystemName__c like '%snooze%' and JBCXM__ShortName__c like '%Other%' limit 1];List<JBCXM__CTA__c> cta = [select Id,JBCXM__SnoozedUntil__c, JBCXM__SnoozeReason__c from JBCXM__CTA__c];for(JBCXM__CTA__c snooze :cta){snooze.JBCXM__SnoozeReason__c = pick.Id;snooze.JBCXM__SnoozedUntil__c=Date.today()+5;}update cta;"));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo upsertCTA = mapper.readValue(new File(TEST_DATA_DIR + "GS-4264/GS-4264-Matrix-Upsert-input.json"), RulesPojo.class);
        RuleAction ruleActions2 = upsertCTA.getSetupActions().get(0);
        if (ruleActions2.getActionType().name().contains("CTA") && ruleActions2.isUpsert()) {
            CTAAction ctaAction2 = mapper.readValue(upsertCTA.getSetupActions().get(0).getAction(), CTAAction.class);
            rulesManagerPage.editRuleByName(upsertCTA.getRuleName());
            rulesEngineUtil.createRuleFromUi(upsertCTA);
            Assert.assertTrue(rulesUtil.runRule(upsertCTA.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction2.getStatus(), sfdcInfo.getUserId(), ctaAction2.getType(), ctaAction2.getReason(), ctaAction2.getComments(), ctaAction2.getName(), ctaAction2.getPlaybook()), "verify whether cta action configured resulted in  correct cta or not");
            Assert.assertEquals( sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where isdeleted=false"))), srcObjRecCount,  "No.of accounts for which cta's should be created is not matching");
        }

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo2 = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act2 = mapper.readValue(upsertCTA.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo2.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act2.getPlaybook() + "'");
        dataETL.execute(jobInfo2);
        List<Map<String, String>> expectedTasks2 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks2 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData2 = Comparator.compareListData(expectedTasks2, actualTasks2);
        Assert.assertEquals(actualTasks2.size(), 12, "Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData2));
        Assert.assertEquals(differenceData2.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }*/

    @TestInfo(testCaseIds = {"GS-4261"})
    @Test(enabled = true)
    public void testCtaActionWithDonNotSkipWeekendOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-4261/GS-4261-Matrix-input1.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        DateUtil.timeZone = userTimezone;
        String date = DateUtil.getDateWithRequiredFormat(Integer.valueOf(ctaAction.getDueDate()), 0, "yyyy-MM-dd");
        Log.info("Duedate is " + date);
        SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name,JBCXM__DueDate__c FROM JBCXM__CTA__c  where isdeleted=false"));
        Log.info("Total Records : " + objRecords.length);
        for (SObject sObject : objRecords) {
            Assert.assertEquals(date, sObject.getField(resolveStrNameSpace("JBCXM__DueDate__c")), "Check DueDate is not matching !!");
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 6, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4261"})
    @Test(enabled = false)
    public void testCtaActionWithSkipAllWeekendsOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR +"GS-4261/GS-4261-Matrix-input2.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
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
        Log.info("No.of weekEndDays: "+weekEndDays);
        String dueDate = RulesEngineUtil.getCtaDateForCTASkipAllWeekendsOption(Integer.valueOf(ctaAction.getDueDate()) - weekEndDays);
        Log.info("Duedate is " + dueDate);
        SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name,JBCXM__DueDate__c FROM JBCXM__CTA__c where isdeleted=false"));
        Log.info("Total Records : " + objRecords.length);
        for (SObject sObject : objRecords) {
            Assert.assertEquals(dueDate, sObject.getField(resolveStrNameSpace("JBCXM__DueDate__c")), "Check DueDate is not matching !!");
        }
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 6, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-4261"})
    @Test(enabled = true)
    public void testCtaActionWithSkipWeekendIfDueOnWeekEndOption() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR +"GS-4261/GS-4261-Matrix-input3.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
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
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR +"CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 6, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-3873", "GS-4185"})
    @Test(enabled = false)
    // This testcase handles owner field userlookup and cta token also for create cta action
    public void testCloseCtaFromSpecificSource() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-4185/GS-4185_3873-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);

        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 6, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
        //   Again Editing same cta, since scenario is to create close cta action for the cta which is already existing
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo closeCtaPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-4185/GS-4185_3873-Matrix-CloseCTA-input.json"), RulesPojo.class);
        CloseCtaAction closeCtaAction = null;
        if (closeCtaPojo.getSetupActions().get(0).getActionType().name().contains("CloseCTA")) {
            JsonNode actionObject = closeCtaPojo.getSetupActions().get(0).getAction();
            closeCtaAction = mapper.readValue(actionObject, CloseCtaAction.class);
            rulesManagerPage.editRuleByName(closeCtaPojo.getRuleName());
            rulesEngineUtil.createRuleFromUi(closeCtaPojo);
            Assert.assertTrue(rulesUtil.runRule(closeCtaPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
            Assert.assertTrue(rulesUtil.isCTAclosedSuccessfully(closeCtaAction), "check cta is closed with correct parammers or not");
            CTAAction ctaAction = mapper.readValue(closeCtaPojo.getSetupActions().get(0).getAction(), CTAAction.class);
            dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"),JobInfo.class));
            List<Map<String, String>> filteredAccounts = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
            int srcObjRecCount = filteredAccounts.size();
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and  JBCXM__Source__c='Rules' and JBCXM__ClosedDate__c!=null and isdeleted=false"))));

            String names = "";
            for(Map<String, String> namesMap : filteredAccounts){
                names = "'" + namesMap.get("AccountName") + "',";
            }
            names = names.substring(0 , names.length()-1);
            String query = resolveStrNameSpace("SELECT Id,Name,JBCXM__Account__r.Name,JBCXM__Account__r.Id,JBCXM__Comments__c," + "FROM JBCXM__CTA__c where JBCXM__Account__r.Name in (" + names + ") and isDeleted=false");
            Log.info("Resolved query: "+query);
            SObject[] ctarecords = sfdc.getRecords(query);
            Log.debug("CTA Records: "+Arrays.toString(ctarecords));
            // Since UI names and API names are different, writing a common util will be error prone always.
            for (SObject tokenRecords : ctarecords) {
                String ctaComment = (String) tokenRecords.getField(resolveStrNameSpace("JBCXM__Comments__c"));
                Log.info("Expected comment:"+ctaComment);
                String actualTokenComments = (String) tokenRecords.getChild((resolveStrNameSpace("JBCXM__Account__r"))).getChild("Name").getValue() + tokenRecords.getChild(resolveStrNameSpace("JBCXM__Account__r")).getChild("Id").getValue();
                Log.info("Actual comment:"+actualTokenComments);
                // Asserting both create cta and close cta comments
                Assert.assertEquals(ctaComment, actualTokenComments + "\n" + "\n" + actualTokenComments);
            }

        }
    }

    @TestInfo(testCaseIds = {"GS-3874"})
    @Test
    public void testCloseCtaFromAllSources() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-3874/GS-3874-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.info("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check rule execution attachment for more details !");
        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(RULE_JOBS + "CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(EXPECTED_UI_TESTDATA_DIR + "CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 6, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
        // Again Editing same cta, since scenario is to create close cta action for the cta which is already existing
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        RulesPojo closeCtaPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-3874/GS-3874-Matrix-CloseCTA-input.json"), RulesPojo.class);
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
            dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"),JobInfo.class));
            List<Map<String, String>> filteredAccounts = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
            int srcObjRecCount = filteredAccounts.size();
            Assert.assertEquals(srcObjRecCount, sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and  JBCXM__Source__c='Rules' and JBCXM__ClosedDate__c!=null and isdeleted=false"))));
        }
    }



}
