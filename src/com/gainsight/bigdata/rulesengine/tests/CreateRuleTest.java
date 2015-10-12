package com.gainsight.bigdata.rulesengine.tests;

import au.com.bytecode.opencsv.CSVReader;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.enums.ActionType;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToCustomersAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToFeatureAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMileStoneAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToSFDCAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToUsageAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.SetScoreAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBDetail;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBServerDetail;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.Verifier;
import com.sforce.soap.partner.sobject.SObject;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by vmenon on 8/27/2015.
 */
public class CreateRuleTest extends BaseTest {

    private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts.txt";
    private static final String ACCOUNTS_JOB_FOR_LOAD_TO_CUSTOMERS_ACTION = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts_For_Load_to_Customers_Action.txt";
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    MongoDBDAO mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()), nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
    private static final String CLEANUP_FEATURES = Application.basedir + "/testdata/newstack/RulesEngine/LoadToFeature/FeatureInsertion.apex";
    private static final String SET_USAGE_DATA_LEVEL_FILE = Application.basedir + "/testdata/sfdc/rulesEngine/scripts/Set_Account_Level_Monthly.apex";
    private static final String SET_USAGE_DATA_MEASURE_FILE = Application.basedir + "/testdata/sfdc/rulesEngine/scripts/UsageData_Measures.apex";
    private static final String NUMERIC_SCHEME_FILE = Application.basedir + "/apex_scripts/scorecard/Scorecard_enable_numeric.apex";
    private static final String USAGE_DATA_FILE = "/testdata/newstack/RulesEngine/RulesUI-Data/UsageData_Monthly.csv";
    private static final String CREATE_ACCOUNTS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_For_LoadToCustomersAction.txt";
    private static final String COLLECTION_MASTER = "collectionmaster";
    private static final String RULES_LOADABLE_OBJECT = "rulesLoadableObject";
    private ObjectMapper mapper = new ObjectMapper();
    public static SFDCInfo sfinfo;
    private DBDetail dbDetail = null;
    TenantDetails tenantDetails = null;
    RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
    NSTestBase nsTestBase = new NSTestBase();
    ReportManager reportManager = new ReportManager();
    private DataLoadManager dataLoadManager;
    DataETL dataETL = new DataETL();
    String[] dataBaseDetail = null;
    private String host = null;
    private String port = null;
    private String collectionDBName = null;
    private RulesUtil rulesUtil = new RulesUtil();
    public List<String> collectionNames = new ArrayList<String>();


    @BeforeClass
    public void setUp() throws Exception {
        basepage.login();
        sfdc.connect();
        nsTestBase.init();
        nsTestBase.tenantAutoProvision();
        TenantManager tenantManager = new TenantManager();
        tenantDetails = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null);
        dataLoadManager = new DataLoadManager();
        dbDetail = mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
        rulesConfigureAndDataSetup.createCustomObjectAndFieldsInSfdc();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(NUMERIC_SCHEME_FILE));
        AdministrationBasePage administrationBasePage = basepage.clickOnAdminTab();
        AdminScorecardSection adminScorecardSection = administrationBasePage.clickOnScorecardSection();
        adminScorecardSection.enableScorecard();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(NUMERIC_SCHEME_FILE));
        metaUtil.createFieldsOnUsageData(sfdc);
        metaUtil.createFieldsForAccount(sfdc, sfdc.fetchSFDCinfo());
        metaUtil.createFieldsOnAccount(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_FEATURES));
        List<DBServerDetail> dbDetails = dbDetail.getDbServerDetails();
        for (DBServerDetail dbServerDetail : dbDetails) {
            dataBaseDetail = dbServerDetail.getHost().split(":");
            host = dataBaseDetail[0];
            port = dataBaseDetail[1];
        }
        Log.info("Host is" + host + " and Port is " + port);
    }

    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
    }

    @Test
    public void testAllActionsUsingNativeData() throws Exception {
        rulesConfigureAndDataSetup.deleteAllRecordsFromMongoCollectionBasedOnTenantID(dbDetail.getDbName(), RULES_LOADABLE_OBJECT, host, Integer.valueOf(port), tenantDetails.getTenantId());
        rulesConfigureAndDataSetup.createCustomObjectAndFieldsInSfdc();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC1.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
        assertForAllActionsUsingSFDCData(rulesPojo);
    }

    @Test
    public void testLoadToCustomersWithNativeData() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS));
        JobInfo jobInfo = mapper.readValue((new FileReader(ACCOUNTS_JOB_FOR_LOAD_TO_CUSTOMERS_ACTION)), JobInfo.class);
        dataETL.execute(jobInfo);
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC4.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
        assertForAllActionsUsingSFDCData(rulesPojo);
    }

    @Test
    public void loadToMdaActionUsingNativeData() throws Exception {
        rulesConfigureAndDataSetup.deleteCollectionSchemaFromCollectionMaster(dbDetail.getDbName(), COLLECTION_MASTER, host, Integer.valueOf(port), tenantDetails.getTenantId());
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        rulesConfigureAndDataSetup.deleteAllRecordsFromMongoCollectionBasedOnTenantID(dbDetail.getDbName(), RULES_LOADABLE_OBJECT, host, Integer.valueOf(port), tenantDetails.getTenantId());
        rulesConfigureAndDataSetup.createEmptySubjectArea();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/loadToMdaActionUsingNativeData.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule Created and Ran Successfully!");
        String subjectArea = null;
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        for (RuleAction ruleAction : ruleActions) {
            JsonNode actionObject = ruleAction.getAction();
            LoadToMDAAction loadToMDAAction = mapper.readValue(actionObject, LoadToMDAAction.class);
            subjectArea = loadToMDAAction.getObjectName();
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollection(subjectArea);
        collectionDBName = actualCollectionInfo.getCollectionDetails().getDbCollectionName();
        Log.info(collectionDBName);
        String subjectAreaName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        Log.info("subjectAreaName is " + subjectAreaName);
        collectionNames.add(subjectAreaName);
        JobInfo loadTransform = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/LoadToMda_EpectedDate_Job.txt"), JobInfo.class);
        dataETL.execute(loadTransform);
        String list[] = {"ID", "Name", "Description", "LongTextArea", "AnnualRevenue", "Email"};
        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager
                .createTabularReport(actualCollectionInfo, list)), actualCollectionInfo);
        List<Map<String, String>> expData = Comparator
                .getParsedCsvData(new CSVReader(new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/LoadToMDA-Output.csv")));
        Log.info("Actual : " + mapper.writeValueAsString(actualData));
        Log.info("Expected : " + mapper.writeValueAsString(expData));
        List<Map<String, String>> diffData = Comparator.compareListData(expData, actualData);
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(diffData.size(), 0);
    }

    @Test
    public void testCTAActionWithCalculatedFieldsOnUsageDataObject() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        RuleEngineDataSetup ruleEngineDataSetup = new RuleEngineDataSetup();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_LEVEL_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_MEASURE_FILE));
        ruleEngineDataSetup.loadUsageData(dataETL, USAGE_DATA_FILE, false);
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CTAActionWithCalculatedFields.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule Created and Ran Successfully!");
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        for (RuleAction ruleAction : ruleActions) {
            JsonNode actionObject = ruleAction.getAction();
            CTAAction ctaAction = mapper.readValue(actionObject, CTAAction.class);
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(),
                    ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), rulesPojo.getRuleName(), ctaAction.getPlaybook()));
        }
    }

    @Test
    public void testCtaActionWithCalculatedMeasuresUsingMdaSubjectArea() throws Exception {
        rulesConfigureAndDataSetup.deleteCollectionSchemaFromCollectionMaster(dbDetail.getDbName(), COLLECTION_MASTER, host, Integer.valueOf(port), tenantDetails.getTenantId());
        rulesConfigureAndDataSetup.createMdaSubjectAreaWithData();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC3.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        for (RuleAction ruleAction : ruleActions) {
            JsonNode actionObject = ruleAction.getAction();
            CTAAction ctaAction = mapper.readValue(actionObject, CTAAction.class);
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(),
                    ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), rulesPojo.getRuleName(), ctaAction.getPlaybook()));
        }
    }

    @Test
    public void testCtaActionWithCalculatedFieldsAndMeasuresUsingMdaSubjectArea() throws Exception {
        rulesConfigureAndDataSetup.deleteCollectionSchemaFromCollectionMaster(dbDetail.getDbName(), COLLECTION_MASTER, host, Integer.valueOf(port), tenantDetails.getTenantId());
        rulesConfigureAndDataSetup.createMdaSubjectAreaWithData();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CTAActionWithCalculatedFieldsAndMeasures.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();

        for (RuleAction ruleAction : ruleActions) {
            JsonNode actionObject = ruleAction.getAction();
            CTAAction ctaAction = mapper.readValue(actionObject, CTAAction.class);
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(),
                    ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), rulesPojo.getRuleName(), ctaAction.getPlaybook()));
        }
    }

    @Test
    public void testAllActionsUsingMdaData() throws Exception {
        rulesConfigureAndDataSetup.deleteCollectionSchemaFromCollectionMaster(dbDetail.getDbName(), COLLECTION_MASTER, host, Integer.valueOf(port), tenantDetails.getTenantId());
        rulesConfigureAndDataSetup.createMdaSubjectAreaWithData();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC2.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        Verifier verifier = new Verifier();
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        for (RuleAction ruleAction : ruleActions) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode actionObject = ruleAction.getAction();
            ActionType actionType = ruleAction.getActionType();
            int srcObjRecCount = 4;
            switch (actionType) {
                case CTA:
                    CTAAction ctaAction = objectMapper.readValue(actionObject, CTAAction.class);
                    verifier.verifyTrue((rulesUtil.isCTACreateSuccessfully(
                            ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(),
                            ctaAction.getReason(), ctaAction.getComments(), rulesPojo.getRuleName(), ctaAction.getPlaybook())));
                    Log.info("Verification done for cta");
                    break;
                case LoadToCustomers:
                    srcObjRecCount = 9;
                    int trgtObjRecCount = sfdc
                            .getRecordCount(resolveStrNameSpace("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false"));
                    Log.info("Load to customers records count" + trgtObjRecCount);
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    srcObjRecCount = 4;
                    break;
                case LoadToFeature:
                    LoadToFeatureAction loadToFeatureAction = objectMapper.readValue(actionObject, LoadToFeatureAction.class);
                    boolean licenced = false;
                    if (loadToFeatureAction.getLicensed().getUpdateType()
                            .equalsIgnoreCase("Licenced")) {
                        licenced = true;
                    } else {
                        licenced = false;
                    }
                    boolean enabled = false;
                    if (loadToFeatureAction.getEnabled().getUpdateType()
                            .equalsIgnoreCase("Enabled")) {
                        enabled = true;
                    } else {
                        enabled = false;
                    }
                    trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(
                            "Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='"
                                    + loadToFeatureAction.getFeature() + "' and JBCXM__Features__r.JBCXM__Product__c='"
                                    + loadToFeatureAction.getProduct()
                                    + "' and JBCXM__Enabled__c=" + enabled + " and JBCXM__Licensed__c=" + licenced + " and isDeleted=false"));
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    Log.info("Verification done for Load to feature");
                    break;
                case LoadToMileStone:
                    LoadToMileStoneAction loadToMileStoneAction = objectMapper.readValue(actionObject, LoadToMileStoneAction.class);
                    int loadToMilestoneRecords = sfdc.getRecordCount(
                            resolveStrNameSpace("SELECT Id FROM JBCXM__Milestone__c where IsDeleted = false and JBCXM__Milestone__r.name='" + loadToMileStoneAction.getSelectMilestone() + "'"));
                    Log.info("Load to milestone records count" + loadToMilestoneRecords);
                    verifier.verifyEquals(srcObjRecCount, loadToMilestoneRecords);
                    break;
                case LoadToUsage:
                    int loadToUsageRecords = sfdc
                            .getRecordCount(resolveStrNameSpace(
                                    "SELECT Id,JBCXM__Account__c,JBCXM__Date__c,JBCXM__ExternalId__c,JBCXM__InstanceId__c,JBCXM__InstanceName__c,JBCXM__WeekLabel__c FROM JBCXM__UsageData__c where  isDeleted=false"));
                    Log.info("No of records for load to usage" + loadToUsageRecords);
                    verifier.verifyEquals(srcObjRecCount, loadToUsageRecords);
                    break;
                case SetScore:
                    SetScoreAction setScoreAction = objectMapper.readValue(actionObject, SetScoreAction.class);
                    SObject[] JBCXM__MetricId__c = sfdc
                            .getRecords("SELECT Id FROM JBCXM__ScorecardMetric__c where Name = '" + setScoreAction.getSelectMeasure() + "'");
                    int setScoreRecords = sfdc
                            .getRecordCount(resolveStrNameSpace(
                                    "SELECT Id,JBCXM__CurComment__c,JBCXM__CurScoreId__c,JBCXM__MetricId__c FROM JBCXM__ScorecardFact__c where JBCXM__MetricId__c='"
                                            + JBCXM__MetricId__c[0].getField("Id").toString() + "' AND isDeleted=false"));
                    Log.info("No of records for set score" + setScoreRecords);
                    verifier.verifyEquals(srcObjRecCount, setScoreRecords);
                    break;
                case LoadToSFDCObject:
                    trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM RulesSFDCCustom__c where IsDeleted = false"));
                    Log.info("Load to sfdc records count" + trgtObjRecCount);
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    break;
                default:
                    break;
            }
        }
        verifier.assertVerification();
    }

    @Test
    public void testLoadToCustomersWithMatrixData() throws Exception {
        rulesConfigureAndDataSetup.deleteCollectionSchemaFromCollectionMaster(dbDetail.getDbName(), COLLECTION_MASTER, host, Integer.valueOf(port), tenantDetails.getTenantId());
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        rulesConfigureAndDataSetup.createMdaSubjectAreaWithData();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC5.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
        int expectedCount = 9;
        int actualRecordCount = sfdc
                .getRecordCount(resolveStrNameSpace("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false"));
        Log.info("Load to customers records count" + " " + actualRecordCount);
        Assert.assertEquals(expectedCount, actualRecordCount);
    }

    public void assertForAllActionsUsingSFDCData(RulesPojo rulesPojo) throws Exception {
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        Verifier verifier = new Verifier();
        for (RuleAction ruleAction : ruleActions) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode actionObject = ruleAction.getAction();
            ActionType actionType = ruleAction.getActionType();
            SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
            int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(ruleAction.getCriterias())));
            switch (actionType) {
                case CTA:
                    CTAAction ctaAction = objectMapper.readValue(actionObject, CTAAction.class);
                    verifier.verifyTrue((rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(),
                            sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), rulesPojo.getRuleName(), ctaAction.getPlaybook())));
                    Log.info("Verification done for cta");
                    break;
                case LoadToCustomers:
                    int trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false"));
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    break;
                case LoadToFeature:
                    LoadToFeatureAction loadToFeatureAction = objectMapper.readValue(actionObject, LoadToFeatureAction.class);
                    boolean licenced = false;
                    if (loadToFeatureAction.getLicensed().getUpdateType().equalsIgnoreCase("Licenced")) {
                        licenced = true;
                    } else {
                        licenced = false;
                    }
                    boolean enabled = false;
                    if (loadToFeatureAction.getEnabled().getUpdateType().equalsIgnoreCase("Enabled")) {
                        enabled = true;
                    } else {
                        enabled = false;
                    }
                    trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(
                            "Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='"
                                    + loadToFeatureAction.getFeature() + "' and JBCXM__Features__r.JBCXM__Product__c='" + loadToFeatureAction.getProduct() + "' and JBCXM__Enabled__c=" + enabled + " and JBCXM__Licensed__c=" + licenced + " and isDeleted=false"));
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    Log.info("Verification done for Load to feature");
                    break;
                case LoadToMileStone:
                    LoadToMileStoneAction loadToMileStoneAction = objectMapper.readValue(actionObject, LoadToMileStoneAction.class);
                    int loadToMilestoneRecords = sfdc.getRecordCount(
                            resolveStrNameSpace("SELECT Id FROM JBCXM__Milestone__c where IsDeleted = false and JBCXM__Milestone__r.name='" + loadToMileStoneAction.getSelectMilestone() + "'"));
                    Log.info("Load to milestone records count" + loadToMilestoneRecords);
                    verifier.verifyEquals(srcObjRecCount, loadToMilestoneRecords);
                    break;
                case LoadToUsage:
                    int loadToUsageRecords = sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,JBCXM__Account__c,JBCXM__Date__c,JBCXM__ExternalId__c,JBCXM__InstanceId__c,JBCXM__InstanceName__c,JBCXM__WeekLabel__c FROM JBCXM__UsageData__c where  isDeleted=false"));
                    Log.info("No of records for load to usage" + loadToUsageRecords);
                    verifier.verifyEquals(srcObjRecCount, loadToUsageRecords);
                    break;
                case SetScore:
                    SetScoreAction setScoreAction = objectMapper.readValue(actionObject, SetScoreAction.class);
                    SObject[] JBCXM__MetricId__c = sfdc
                            .getRecords("SELECT Id FROM JBCXM__ScorecardMetric__c where Name = '" + setScoreAction.getSelectMeasure() + "'");
                    int setScoreRecords = sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,JBCXM__CurComment__c,JBCXM__CurScoreId__c,JBCXM__MetricId__c FROM JBCXM__ScorecardFact__c where JBCXM__MetricId__c='" + JBCXM__MetricId__c[0].getField("Id").toString() + "' AND isDeleted=false"));
                    Log.info("No of records for set score" + setScoreRecords);
                    verifier.verifyEquals(srcObjRecCount, setScoreRecords);
                    break;
                case LoadToSFDCObject:
                    trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM RulesSFDCCustom__c where IsDeleted = false "));
                    Log.info("Load to sfdc records count" + trgtObjRecCount);
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    break;
                default:
                    break;
            }
        }
        verifier.assertVerification();
    }
}
