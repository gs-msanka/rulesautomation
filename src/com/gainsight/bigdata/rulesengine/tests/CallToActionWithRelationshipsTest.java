package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRulePage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.pageobject.core.Element;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.annotations.TestInfo;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


/**
 * Created by Abhilash Thaduka on 1/18/2016.
 */
public class CallToActionWithRelationshipsTest extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_DATA_INTO_CUSTOMOBJECT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObjectForRelationshipCTA.txt";
    private static final String ENABLE_RELATIONSHIP = Application.basedir + "/apex_scripts/Relationships/EnableRelationship.apex";
    private static final String CREATE_RELATIONSHIP = Application.basedir + "/apex_scripts/Relationships/CreateRelationship.apex";
    private static final String CLEANUP_DATA = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private ObjectMapper mapper = new ObjectMapper();
    private RulesUtil rulesUtil = new RulesUtil();
    private String rulesManagerPageUrl;
    private NSTestBase nsTestBase = new NSTestBase();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private RulesManagerPage rulesManagerPage;
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup;
    private static final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private DataETL dataETL = new DataETL();
    private TenantManager tenantManager;
    private Date date = Calendar.getInstance().getTime();
    private TenantDetails tenantDetails = null;
    private static DBStoreType dataBaseType;
    GSDataImpl gsDataImpl = null;
    private MongoDBDAO mongoDBDAO = null;
    String collectionName;

    @BeforeClass
    @Parameters("dbStoreType")
    public void setUp(@Optional String dbStoreType) throws Exception {
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        metaUtil.createExtIdFieldOnAccount(sfdc);
        rulesManagerPage = new RulesManagerPage();
        rulesUtil.populateObjMaps();
        rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ENABLE_RELATIONSHIP));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_RELATIONSHIP));
        JobInfo loadData = mapper.readValue((new FileReader(LOAD_DATA_INTO_CUSTOMOBJECT)), JobInfo.class);
        dataETL.execute(loadData);
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        tenantManager = new TenantManager();
        String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
        tenantDetails = tenantManager.getTenantDetail(null, tenantId);
        if (StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Mongo")) {
            if(tenantDetails.isRedshiftEnabled()){
                mongoDBDAO = MongoDBDAO.getGlobalMongoDBDAOInstance();
                assertTrue(mongoDBDAO.disableRedshift(tenantId), "Failed updating dataStoreType at tenant level to Mongo.");
            }
        }
        else if(StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Redshift")){
            if(!tenantDetails.isRedshiftEnabled()) {
                assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails), "Failed updating dataStoreType at tenant level to Redshift");
            }
        }
        // Loading testdata globally
        boolean isLoadTestDataGlobally = true;
        if (isLoadTestDataGlobally) {
            CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchemaWithRelationshipMapping.json")), CollectionInfo.class);
            collectionInfo.getCollectionDetails().setCollectionName(dbStoreType + date.getTime());
            String collectionId = gsDataImpl.createCustomObject(collectionInfo);
            Assert.assertNotNull(collectionId, "Collection ID should not be null.");
            CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
            collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
            dataETL.execute(mapper.readValue(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/ExtractJob.txt"), JobInfo.class));
            JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
            File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
            DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
            Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
            NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
            Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
        }
    }

    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_DATA));
    }

    @TestInfo(testCaseIds = {"GS-8086"})
    @Test(description = "testcase to verify Relationship ID and Relationship Account fields are auto-populated in show fields for Relationship CTA's ")
    public void testRelationshipFieldsInShowArea() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-8086/GS-8086-Input.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.navigateToSetupRule(rulesPojo);
        SetupRulePage setupRulePage = new SetupRulePage();
        Element element = new Element();
        Assert.assertTrue(element.isElementDisplayed(String.format(setupRulePage.SHOW_FIELD, "Relationship::Id")), "Relationship ID is not auto-populated in showField");
        Assert.assertTrue(element.isElementDisplayed(String.format(setupRulePage.SHOW_FIELD, "Relationship::Account")), "Relationship Acccount field is not auto-populated in showField");
    }

    @TestInfo(testCaseIds = {"GS-8087", "GS-8090"})
    @Test(description = "testcase to create Relationship CTA using relationship object ")
    public void createCtaUsingSfdcRelationshipObject() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-8087/GS-8087-Input.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check response object printed in log for more details !!");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), 9, "Total CTA's created are not matching");

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 54, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }

    @TestInfo(testCaseIds = {"GS-8088"})
    @Test(description = "testcase to create Relationship CTA using linked object ")
    public void createCtaUsingSfdcLinkedObject() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-8088/GS-8088-Input.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check response object printed in log for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), 9, "Total CTA's created are not matching");

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 27, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta");
    }


    @TestInfo(testCaseIds = {"GS-200059"})
    @Test(description = "testcase to create Relationship CTA using Mda linked collection")
    public void createCtaUsingMdaLinkedCollection() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-200059/GS-200059-Input.json"), RulesPojo.class);
        rulesPojo.getSetupRule().setSelectObject(collectionName);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
        Log.debug("Updated testdata is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, Please check response object printed in log for more details !");
        JsonNode action = rulesPojo.getSetupActions().get(0).getAction();
        CTAAction ctaAction = mapper.readValue(action, CTAAction.class);
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), 9, "Total CTA's created are not matching");

        //Verifying CS tasks of a CTA for the playbook applied
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/CSTasks.txt"), JobInfo.class));
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/playbookTasks.txt"), JobInfo.class);
        CTAAction act = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), CTAAction.class);
        jobInfo.getExtractionRule().setWhereCondition(" where JBCXM__PlaybookId__r.Name='" + act.getPlaybook() + "'");
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/PlayBookTasks.csv");
        List<Map<String, String>> actualTasks = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/CSTasks.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedTasks, actualTasks);
        Assert.assertEquals(actualTasks.size(), 54, "Total Number of CSTasks are not matching for the cta's created");
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the CS-Tasks are not matching for the cta's");
    }
}