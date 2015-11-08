package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.ObjectFields;
import com.gainsight.bigdata.pojo.CollectionInfo.Column;
import com.gainsight.bigdata.pojo.CollectionInfo.LookUpDetail;
import com.gainsight.bigdata.pojo.RuleExecutionDataObject;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.DataLoadConfigPojo;
import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.LoadableObjects;
import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.LoadableObjects.DataLoadObject;
import com.gainsight.bigdata.rulesengine.pages.DataLoadConfiguration;
import com.gainsight.bigdata.rulesengine.pages.EditRulePage;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.enums.ActionType;
import com.gainsight.bigdata.rulesengine.pojo.enums.RedShiftFormulaType;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToFeatureAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMileStoneAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.SetScoreAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBDetail;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBServerDetail;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.pageobject.core.Element;
import com.gainsight.sfdc.BaseSalesforceConnector;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.gsEmail.setup.GSEmailSetup;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.Verifier;
import com.sforce.soap.partner.sobject.SObject;


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
    private static final String SCHEME = "Score";
    private static final String METRICS_CREATE_FILE =  Application.basedir + "/apex_scripts/scorecard/Create_ScorecardMetrics.apex";
    private static final String SCORECARD_CLEAN_FILE = Application.basedir + "/apex_scripts/scorecard/Scorecard_CleanUp.txt";
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
    private String userName = null;
	private String passWord = null;
    private String collectionDBName = null;
    private RulesUtil rulesUtil = new RulesUtil();
    public List<String> collectionNames = new ArrayList<String>();
    private TenantManager tenantManager;
    private Calendar calendar = Calendar.getInstance();


    @BeforeClass
    @Parameters("dbStoreType")
    public void setUp(@Optional String dbStoreType) throws Exception {
        basepage.login();
        sfdc.connect();
        nsTestBase.init();
        nsTestBase.tenantAutoProvision();
        tenantManager=new TenantManager();
        GSEmailSetup gs=new GSEmailSetup();
        gs.enableOAuthForOrg();
        MongoDBDAO mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()), nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
        try {
        tenantDetails = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null);
        tenantDetails = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());
        tenantManager.disableRedShift(tenantDetails);
        dataLoadManager = new DataLoadManager();
        rulesConfigureAndDataSetup.createCustomObjectAndFieldsInSfdc();
        metaUtil.createExtIdFieldForScoreCards(sfdc);
        AdministrationBasePage administrationBasePage = basepage.clickOnAdminTab();
        AdminScorecardSection adminScorecardSection = administrationBasePage.clickOnScorecardSection();
        adminScorecardSection.enableScorecard();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(NUMERIC_SCHEME_FILE));
        runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
        metaUtil.createFieldsOnUsageData(sfdc);
        metaUtil.createFieldsForAccount(sfdc, sfdc.fetchSFDCinfo());
        metaUtil.createFieldsOnAccount(sfdc);
        metaUtil.createExtIdFieldOnAccount(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_FEATURES));
		dbDetail = mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
        List<DBServerDetail> dbDetails = dbDetail.getDbServerDetails();
        for (DBServerDetail dbServerDetail : dbDetails) {
            dataBaseDetail = dbServerDetail.getHost().split(":");
            host = dataBaseDetail[0];
            port = dataBaseDetail[1];
            userName=dbServerDetail.getUserName();
			passWord=dbServerDetail.getPassword();
			}
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
        Log.info("Host is" + host + " and Port is " + port);
        // Updating timeZone to America/Los_Angeles in Application settings
        rulesConfigureAndDataSetup.updateTimeZoneInAppSettings("America/Los_Angeles");
		if (dbStoreType != null
				&& dbStoreType.equalsIgnoreCase(DBStoreType.MONGO.name())) {
			Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
		} else if (dbStoreType != null
				&& dbStoreType.equalsIgnoreCase(DBStoreType.REDSHIFT.name())) {
			Assert.assertTrue(tenantManager
					.enabledRedShiftWithDBDetails(tenantDetails));
		}
	}

    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
    }

    @Test
    public void testAllActionsUsingNativeData() throws Exception {
    	MongoDBDAO mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
    	try{
    	Assert.assertTrue(mongoDBDAO.deleteAllRecordsFromMongoCollectionBasedOnTenantID(tenantDetails.getTenantId(), RULES_LOADABLE_OBJECT), "Check whether Delete operation is success or not");
    	}
    	finally{
    	mongoDBDAO.mongoUtil.closeConnection();
    	}
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
    	MongoDBDAO mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
    	try{
    	Assert.assertTrue(mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER), "Check whether Delete operation is success or not");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        Assert.assertTrue(mongoDBDAO.deleteAllRecordsFromMongoCollectionBasedOnTenantID(tenantDetails.getTenantId(), RULES_LOADABLE_OBJECT), "Check whether Delete operation is success or not");
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
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
    	MongoDBDAO mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
		try {
			Assert.assertTrue(mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER), "Check whether Delete operation is success or not");
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
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
    	MongoDBDAO mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
		try {
			Assert.assertTrue(mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
					tenantDetails.getTenantId(), COLLECTION_MASTER), "Check whether Delete operation is success or not");
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
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
    	MongoDBDAO mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
    	try{
    	Assert.assertTrue(mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER), "Check whether Delete operation is success or not");
    	Assert.assertTrue(mongoDBDAO.deleteAllRecordsFromMongoCollectionBasedOnTenantID(tenantDetails.getTenantId(), RULES_LOADABLE_OBJECT), "Check whether Delete operation is success or not");
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
        rulesConfigureAndDataSetup.createMdaSubjectAreaWithData();
        rulesConfigureAndDataSetup.createCustomObjectAndFieldsInSfdc();
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
                            .getRecords(resolveStrNameSpace("SELECT Id FROM JBCXM__ScorecardMetric__c where Name = '" + setScoreAction.getSelectMeasure() + "'"));
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
    	MongoDBDAO mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
    	try{
    	Assert.assertTrue(mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER), "Check whether Delete operation is success or not");
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
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
                            .getRecords(resolveStrNameSpace("SELECT Id FROM JBCXM__ScorecardMetric__c where Name = '" + setScoreAction.getSelectMeasure() + "'"));
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
    
   // @Test // TODO - WIP
	public void verifyDataLoadConfiguration() throws Exception{
		MongoDBDAO mongoConnection = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
		try{
		Assert.assertTrue(mongoConnection.deleteAllRecordsFromMongoCollectionBasedOnTenantID(tenantDetails.getTenantId(), RULES_LOADABLE_OBJECT), "Check whether Delete operation is success or not");
		Assert.assertTrue(mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER), "Check whether Delete operation is success or not");
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
		rulesConfigureAndDataSetup.createMultipleSubjectAreasForDataLoadConfiguration(tenantDetails, mongoDBDAO);
		DataLoadConfigPojo dataLoadConfigPojo = mapper.readValue(
				new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC6.json"),DataLoadConfigPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnConfigure();
		for (LoadableObjects loadableObject : dataLoadConfigPojo.getLoadableObjects()) {
			DataLoadConfiguration dataLoadConfiguration = new DataLoadConfiguration();
			dataLoadConfiguration.selectDataSource(loadableObject.getObjectType());
			for (DataLoadObject dataLoadObject : loadableObject.getDataLoadObject()) {
				dataLoadConfiguration.selectSourceObject(dataLoadObject.getObjectName());
				dataLoadConfiguration.clickOnNativeObjectSelectionSymbol();
				dataLoadConfiguration.clickOnParticularObject(dataLoadObject.getObjectName());
				dataLoadConfiguration.selectFieldsFromList(dataLoadObject);
				dataLoadConfiguration.clickOnSaveButton();
			}
		}
	}
	

	/**
	 * TestCase to verify Gainsight package objects(JBCXM) are available under
	 * dataload configuration Dropdownlist or not.
	 * 
	 * This testcase will work in Beta or managed package only(Since, In Dev org
	 * Rules Team is not handling this case)
	 * @throws IOException 
	 */
	@Test
	public void testGainsightObjectsArePresentInDataLoadConfigurationList() throws IOException {
		List<String> jbcxmObjects=BaseSalesforceConnector.getAllGainSightObjects(sfdc.getPartnerConnection());
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnConfigure();
		DataLoadConfiguration dataLoadConfiguration = new DataLoadConfiguration();
		dataLoadConfiguration.selectSourceObjectFromNativeData();
		dataLoadConfiguration.clickOnNativeObjectSelection();
		Element element = new Element();
		env.setTimeout(0);
		Verifier verifier = new Verifier();
		for (int i = 0; i < jbcxmObjects.size(); i++) {
			verifier.verifyFalse((element.isElementPresent("//li[contains(@class, 'ui-multiselect-option')]/descendant::input[@value='"+jbcxmObjects.get(i)+"']/following-sibling::span")),
					"Check whether Gainsight Package Objects are present under DataLoadConfiguration List !!!");
		}
	}
	
	@Test
	public void testAdditionAndRemovalOFFieldsInDataLoadConfig() throws Exception{
		MongoDBDAO mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
		try {
			Assert.assertTrue(mongoDBDAO.deleteAllRecordsFromMongoCollectionBasedOnTenantID(
					tenantDetails.getTenantId(), RULES_LOADABLE_OBJECT), "Check whether Delete operation is success or not");
		} finally {
			mongoDBDAO.mongoUtil.closeConnection();
		}
		DataLoadConfigPojo dataLoadConfigPojo = mapper.readValue(
				new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC7.json"),DataLoadConfigPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnConfigure();
		for (LoadableObjects loadableObject : dataLoadConfigPojo.getLoadableObjects()) {
			DataLoadConfiguration dataLoadConfiguration = new DataLoadConfiguration();
			dataLoadConfiguration.selectDataSource(loadableObject.getObjectType());
			for (DataLoadObject dataLoadObject : loadableObject.getDataLoadObject()) {
				dataLoadConfiguration.selectSourceObject(dataLoadObject.getObjectName());
				dataLoadConfiguration.clickOnNativeObjectSelectionSymbol();
				dataLoadConfiguration.clickOnParticularObject(dataLoadObject.getObjectName());
				dataLoadConfiguration.selectFieldsFromList(dataLoadObject);
				dataLoadConfiguration.removeFieldsFromList(dataLoadObject);
				dataLoadConfiguration.clickOnSaveButton();
				int expectedList = dataLoadObject.getFields().size() - dataLoadObject.getRemoveFields().size();
				System.out.println("Expected Size is " + expectedList);
				Element element = new Element();
				List<WebElement> actualList = element.getAllElement("//select[contains(@class,'selectedFields fieldSelect')]/descendant::option");
				Log.info("Actual Selected Fileds List in UI is " + actualList.size());
				Assert.assertEquals(actualList.size(), expectedList,  "Check Dropdown list Size in UI with actual testdata");
			}
		}
	}
	
	@Test
	public void dailyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC9.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		} 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!");
	}
	
	
	@Test
	public void weeklyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC10.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		} 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!"); 
	}
	
	@Test
	public void monthlyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC11.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		} 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!");   
	}
	
	@Test
	public void yearlyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC12.json"), RulesPojo.class);
        RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		}
	 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!");
	}
	
	@Test
	public void testCTAActionWithMdaJoins() throws Exception{
		String redShiftCollection1=Long.toString(calendar.getTimeInMillis());
		String redShiftCollection2=Long.toString(calendar.getTimeInMillis());
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC13.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(redShiftCollection1 + "2");
		rulesPojo.getSetupRule().setJoinWithCollection(redShiftCollection2  + "1");
		rulesPojo.getSetupRule().setSelectObject(redShiftCollection1 + "2");	
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(redShiftCollection1 + "2");
		String jsonNode = mapper.writeValueAsString(rulesPojo);
		Log.info(jsonNode);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)),JobInfo.class);
		dataETL.execute(jobInfo);
		JobInfo load = mapper.readValue(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob.txt"),JobInfo.class);
		dataETL.execute(load);
		String collectionName = redShiftCollection1 + "1";
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob1.txt"),JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
		String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusId), "verify whether dataload job status status != IN_PROGRESS");
		Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId),"verify whether dataload job is completed or not");
		

		JobInfo load1 = mapper.readValue(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob.txt"),JobInfo.class);
		dataETL.execute(load1);
		String collectionName1 = redShiftCollection2  + "2";
		Log.info("Collection Name : " + collectionName1);
		CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo1.getCollectionDetails().setCollectionName(collectionName1);
		String collectionId1 = dataLoadManager.createSubjectAreaAndGetId(collectionInfo1);
		Assert.assertNotNull(collectionId1);
		CollectionInfo actualCollectionInfo1 = dataLoadManager.getCollectionInfo(collectionId1);
		JobInfo loadTransform1 = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob1.txt"),JobInfo.class);
		File dataLoadFile1 = FileProcessor.getDateProcessedFile(loadTransform1,calendar.getTime());
		DataLoadMetadata metadata1 = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo1);
		metadata1.setCollectionName(actualCollectionInfo1.getCollectionDetails().getCollectionName());
		String statusId1 = dataLoadManager.dataLoadManage(metadata1,dataLoadFile1);
		Assert.assertNotNull(statusId1);
		Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusId1), "verify whether dataload job status status != IN_PROGRESS");
		Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId1),"verify whether dataload job is completed or not");
		
		CollectionInfo actualCollectionInfoForCollection1 = dataLoadManager.getCollectionInfo(collectionId);
		CollectionInfo actualCollectionInfoForCollection2 = dataLoadManager.getCollectionInfo(collectionId1);
		Map<String, String> hm=CollectionUtil.getDisplayAndDBNamesMap(actualCollectionInfoForCollection2);	
		// Forming lookup object on ID field of both baseobject and lookup object
		CollectionUtil.setLookUpDetails(actualCollectionInfoForCollection2, "ID", actualCollectionInfoForCollection1, "ID", false);
		Assert.assertTrue(tenantManager.updateSubjectArea(tenantDetails.getTenantId(),actualCollectionInfoForCollection2), "check collectionmaster is updated or not via api");
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		List<RuleAction> ruleActions = rulesPojo.getSetupActions();
		for (RuleAction ruleAction : ruleActions) {
			JsonNode actionObject = ruleAction.getAction();
			CTAAction ctaAction = mapper.readValue(actionObject, CTAAction.class);
			Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(),
					sfdcInfo.getUserId(), ctaAction.getType(),ctaAction.getReason(), ctaAction.getComments(),rulesPojo.getRuleName(), ctaAction.getPlaybook()));
		}
	}
	
	
	@Test
	public void testRuleInactive() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC14.json"),RulesPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.switchOffRuleByName(rulesPojo.getRuleName());
		Assert.assertTrue(rulesManagerPage.isRuleInActive(rulesPojo.getRuleName()), "Check whether rule is active or inactive!! ");
	}
	
	@Test
	public void testCloningOFARule() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC15.json"),RulesPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.cloneARuleByName(rulesPojo.getRuleName(), rulesPojo.getRuleName()+ "CLONED");
		Assert.assertTrue(rulesManagerPage.isRuleInActive(rulesPojo.getRuleName()+ "CLONED"), "Check whether rule is cloned or not !!");
		SObject[] rule1Criteria=sfdc.getRecords(resolveStrNameSpace("select ID,JBCXM__TriggerCriteria__c from JBCXM__AutomatedAlertRules__c where Name='"+rulesPojo.getRuleName()+"'"));
		SObject[] rule2Criteria=sfdc.getRecords(resolveStrNameSpace("select ID,JBCXM__TriggerCriteria__c from JBCXM__AutomatedAlertRules__c where Name='"+rulesPojo.getRuleName()+ "CLONED"+"'"));
		Assert.assertEquals(rule1Criteria[0].getField("JBCXM__TriggerCriteria__c"), rule2Criteria[0].getField("JBCXM__TriggerCriteria__c"), "verify json data for autual rule and cloned rule");
	}
	
	
	@Test
	public void testDeleteRule() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC16.json"),RulesPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.deleteRuleByName(rulesPojo.getRuleName());
		env.setTimeout(2);
		Assert.assertFalse(rulesManagerPage.isRulePresentByName(rulesPojo.getRuleName()), "Check whether rule is present or not in UI after deletion!! ");
		
	}

	@Test
	public void testEditARule() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC17.json"),RulesPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.editRuleByName(rulesPojo.getRuleName());
		Assert.assertTrue(rulesManagerPage.isEditRulePagePresent(), "Check whether clicking on edit rule lands on editrule page or not!!");
	}
	
	
	@Test
	public void verifyRecordsOnDateFiltersUsingNativeData() throws Exception {
		ObjectFields objField = new ObjectFields();
		List<String> Date = new ArrayList<String>();
		// Creating 10 date type fields on Account object
		for (int i = 0; i < 10; i++) {
			Date.add("DateField" + i);
		}
		objField.setDates(Date);
		metaUtil.createFieldsOnObject(sfdc, "Account", objField);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		String LOAD_ACCOUNTS_JOB2 = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts2.txt";
		JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB2)), JobInfo.class);
		dataETL.execute(jobInfo);
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir
				+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC18.json"), RulesPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);		
		String executionData=rulesUtil.runRuleAndGetData(rulesPojo.getRuleName());
		RuleExecutionDataObject[] ruleExecutionDataObject=mapper.readValue(executionData, RuleExecutionDataObject[].class);
		int totalNumberOfRecordsProcessed=Integer.valueOf(ruleExecutionDataObject[0].getExecutionMessages().get(1).
				substring(ruleExecutionDataObject[0].getExecutionMessages().get(1).lastIndexOf(":")+2).trim());
		Log.info("Total records fetched are " + totalNumberOfRecordsProcessed);
		Assert.assertEquals(totalNumberOfRecordsProcessed, 9, "Verify records fetched are valid or not");
	}

	@Test
	public void verifyRecordsOnDateTimeFiltersUsingNativeData() throws Exception {
		ObjectFields objField = new ObjectFields();
		List<String> DateTime = new ArrayList<String>();
		// Creating 10 dateTime type fields on Account object
		for (int i = 0; i < 10; i++) {
			
			DateTime.add("DateTimeField" + i);
		}
		objField.setDateTimes(DateTime);
		metaUtil.createFieldsOnObject(sfdc, "Account", objField);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_DateTimeFiltersUsingNativeData.txt";
		JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
		dataETL.execute(jobInfo);
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir
				+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC19.json"), RulesPojo.class);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		String executionData=rulesUtil.runRuleAndGetData(rulesPojo.getRuleName());
		RuleExecutionDataObject[] ruleExecutionDataObject=mapper.readValue(executionData, RuleExecutionDataObject[].class);
		int totalNumberOfRecordsProcessed=Integer.valueOf(ruleExecutionDataObject[0].getExecutionMessages().get(1).
				substring(ruleExecutionDataObject[0].getExecutionMessages().get(1).lastIndexOf(":")+2).trim());
		Log.info("Total records fetched are " + totalNumberOfRecordsProcessed);
		Assert.assertEquals(totalNumberOfRecordsProcessed, 9, "Verify records fetched are valid or not");
	}
	
	@Test
	public void verifyRecordsOnDateFiltersUsingMdaData() throws Exception {	
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC20.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setSelectObject("TC20" + calendar.getTimeInMillis());
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject("TC20" + calendar.getTimeInMillis());
		
        JobInfo load = mapper.readValue(new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob_3.txt"), JobInfo.class);
		dataETL.execute(load);
		String collectionName = "TC20" + calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionSchema_3.json")), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob_3_parsing.txt"), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
		String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusId), "verify whether dataload job status status != IN_PROGRESS");
		Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId),"verify whether dataload job is completed or not");
		
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		String LOAD_ACCOUNTS_JOB2 = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts2.txt";
		JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB2)), JobInfo.class);
		dataETL.execute(jobInfo);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		String executionData=rulesUtil.runRuleAndGetData(rulesPojo.getRuleName());
		RuleExecutionDataObject[] ruleExecutionDataObject=mapper.readValue(executionData, RuleExecutionDataObject[].class);
		int totalNumberOfRecordsProcessed=Integer.valueOf(ruleExecutionDataObject[0].getExecutionMessages().get(1).
				substring(ruleExecutionDataObject[0].getExecutionMessages().get(1).lastIndexOf(":")+2).trim());
		Log.info("Total records fetched are " + totalNumberOfRecordsProcessed);
		Assert.assertEquals(totalNumberOfRecordsProcessed, 9, "Verify records fetched are valid or not");
	}
	
	@Test
	public void verifyRecordsOnDateTimeFiltersUsingMdaData() throws Exception {	
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC21.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setSelectObject("TC21" + calendar.getTimeInMillis());
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject("TC21" + calendar.getTimeInMillis());	
        JobInfo load = mapper.readValue(new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob_3.txt"), JobInfo.class);
		dataETL.execute(load);
		String collectionName = "TC21" + calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionSchema_3.json")), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob_3_parsing.txt"), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
		String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusId), "verify whether dataload job status status != IN_PROGRESS");
		Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId),"verify whether dataload job is completed or not");		
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		String LOAD_ACCOUNTS_JOB2 = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts2.txt";
		JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB2)), JobInfo.class);
		dataETL.execute(jobInfo);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		String executionData=rulesUtil.runRuleAndGetData(rulesPojo.getRuleName());
		RuleExecutionDataObject[] ruleExecutionDataObject=mapper.readValue(executionData, RuleExecutionDataObject[].class);
		int totalNumberOfRecordsProcessed=Integer.valueOf(ruleExecutionDataObject[0].getExecutionMessages().get(1).
				substring(ruleExecutionDataObject[0].getExecutionMessages().get(1).lastIndexOf(":")+2).trim());
		Log.info("Total records fetched are " + totalNumberOfRecordsProcessed);
		Assert.assertEquals(totalNumberOfRecordsProcessed, 9, "Verify records fetched are valid or not");
	}

	
	@Test
	public void testRedShiftCalculatedMeasuresInSetupRule() throws Exception{
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC22.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setSelectObject("TC22" + calendar.getTimeInMillis());
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject("TC22" + calendar.getTimeInMillis());
        JobInfo load = mapper.readValue(new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob_4.txt"), JobInfo.class);
		dataETL.execute(load);
		String collectionName = "TC22" + calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionSchema_4.json")), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob1.txt"), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
		String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusId), "verify whether dataload job status status != IN_PROGRESS");
		Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId),"verify whether dataload job is completed or not");
		
		Map<String, String> map=CollectionUtil.getDisplayAndDBNamesMap(actualCollectionInfo);
		CollectionInfo calculatedFeildsSchema=CollectionUtil.getcalculatedExpression(actualCollectionInfo,
				"calculatedMeasure1", map.get("number4"), map.get("number3"),
				map.get("Score"), RedShiftFormulaType.FORMULA1);
		Assert.assertTrue(tenantManager.updateSubjectArea(tenantDetails.getTenantId(),actualCollectionInfo), "check collectionmaster is updated or not via api");
		String schemaWithCalculatedFields = mapper.writeValueAsString(calculatedFeildsSchema);
		Log.info("Updated Collection schema is " + schemaWithCalculatedFields);
		RulesManagerPage rulesManagerPage = basepage.clickOnAdminTab().clickOnRulesEnginePage();
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		String executionData=rulesUtil.runRuleAndGetData(rulesPojo.getRuleName());
		RuleExecutionDataObject[] ruleExecutionDataObject=mapper.readValue(executionData, RuleExecutionDataObject[].class);
		int totalNumberOfRecordsProcessed=Integer.valueOf(ruleExecutionDataObject[0].getExecutionMessages().get(1).
				substring(ruleExecutionDataObject[0].getExecutionMessages().get(1).lastIndexOf(":")+2).trim());
		Log.info("Total records fetched are " + totalNumberOfRecordsProcessed);
		Assert.assertEquals(totalNumberOfRecordsProcessed, 6, "Verify records matched or not");
	}
}
