package com.gainsight.bigdata.rulesengine.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.annotations.TestInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;

/**
 * Created by msanka on 4/19/2016.
 */
public class CallToActionTestUsingMatrixData extends BaseTest{

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
    GSDataImpl gsDataImpl = null;
    private String collectionName;
    private static final String RULE_SCRIPTS_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/";
    private static final String GLOBAL_TEST_DATA_DIR = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/";
    private static final String TEST_DATA_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/";
    private static final String RULE_JOBS = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/";
    private static final String EXPECTED_UI_TESTDATA_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/";


    @BeforeClass
    @Parameters("dbStoreType")
    public void setUp(@Optional("Redshift") String dbStoreType) throws Exception {
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__ActionTemplates__c];"));
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        tenantManager = new TenantManager();
        String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
        tenantDetails = tenantManager.getTenantDetail(null, tenantId);
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
        rulesUtil.populateObjMaps();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        boolean isLoadTestDataGlobally = true;
        if (isLoadTestDataGlobally) {
            CollectionInfo collectionInfo = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "CollectionSchemaForCTA.json")),CollectionInfo.class);
            collectionInfo.getCollectionDetails().setCollectionName(dbStoreType + date.getTime());
            String collectionId = gsDataImpl.createCustomObject(collectionInfo);
            Assert.assertNotNull(collectionId, "Collection ID should not be null.");
            CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
            collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
            dataETL.execute(mapper.readValue(resolveNameSpace(GLOBAL_TEST_DATA_DIR + "DataLoadJobMatrixCTA.txt"),JobInfo.class));
            JobInfo loadTransform = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "DataloadJob.txt")), JobInfo.class);
            File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
            DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[] { "ID", "AccountName", "CustomDate1", "PageViews", "Logins", "Description"},	DataLoadOperationType.INSERT);
            Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
            NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
            Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
        }

    }


    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_DATA));
    }

    @TestInfo(testCaseIds = {"GS-4185", "GS-4186", "GS-4257", "GS-4256", "GS-4257"})
    @Test(enabled = true)
    public void testCtaWithUpsertPriorityAndCommentsAlwaysOption() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
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

        List<Map<String, String>> countMap = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
        int  srcObjRecCount = Integer.valueOf(countMap.get(0).get("count"));
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"Verify the no.of accounts for which cta is created.");

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
            Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"Verify the no.of accounts for which cta is created.");
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
            Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"Verify the no.of accounts for which cta is created.");        }
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
    }

    @TestInfo(testCaseIds = { "GS-4257"})
    @Test(enabled = true)
    public void testCtaWithUpdateCommentsNeverOption() throws Exception {
        SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
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
        Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()), "verify whether cta action configured resulted correct cta or not");
        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-4185/CTA-ExpectedJob.txt"),JobInfo.class));

        List<Map<String, String>> countMap = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-4185/ExpectedData.csv");
        int  srcObjRecCount = Integer.valueOf(countMap.get(0).get("count"));
        Assert.assertEquals(sfdc.getRecordCount(resolveStrNameSpace(("select id, name FROM JBCXM__CTA__c where Name='" + ctaAction.getName() + "' and JBCXM__Source__c='Rules' and isdeleted=false"))), srcObjRecCount ,"Verify the no.of accounts for which cta is created.");

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


}
