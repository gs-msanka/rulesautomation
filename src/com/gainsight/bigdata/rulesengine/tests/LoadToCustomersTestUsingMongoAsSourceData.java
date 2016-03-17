package com.gainsight.bigdata.rulesengine.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.FileUtil;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by msanka on 3/9/2016.
 */
public class LoadToCustomersTestUsingMongoAsSourceData extends BaseTest {

    private NSTestBase nsTestBase = new NSTestBase();
    private ObjectMapper mapper = new ObjectMapper();
    private DataETL dataETL = new DataETL();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private RulesUtil rulesUtil = new RulesUtil();
    GSDataImpl gsDataImpl = null;
    private Date date = Calendar.getInstance().getTime();
    private TenantDetails tenantDetails = null;
    private TenantManager tenantManager;

    private static final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM RulesSFDCCustom__c];";
    MongoDBDAO mongoDBDAO = null;
    String collectionName;
    private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
    private static final String ACCOUNTS_JOB_FOR_LOAD_TO_CUSTOMERS_ACTION = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts_For_Load_to_Customers_Action.txt";
    private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private static final String CREATE_ACCOUNTS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts.txt";



    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional("Mongo") String dbStoreType) throws Exception {
        basepage.login();
        sfdc.connect();
        nsTestBase.init();
        tenantManager = new TenantManager();
        tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
        if (StringUtils.isNotBlank(dbStoreType)) {
            mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()), nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
            TenantDetails.DBDetail schemaDBDetails = null;
            schemaDBDetails = mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
            if (schemaDBDetails == null || schemaDBDetails.getDbServerDetails() == null || schemaDBDetails.getDbServerDetails().get(0) == null) {
                throw new RuntimeException("DB details are not correct, please check it.");
            }
            Log.info("Connecting to schema db....");
            mongoDBDAO = new MongoDBDAO(schemaDBDetails.getDbServerDetails().get(0).getHost().split(":")[0], 27017, schemaDBDetails.getDbServerDetails().get(0).getUserName(), schemaDBDetails.getDbServerDetails().get(0).getPassword(), schemaDBDetails.getDbName());
        }
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt"));
        // Loading testdata at class level in setup
        collectionName="Mongo1458208394159";
        boolean isLoadTestDataGlobally = true;
        if (isLoadTestDataGlobally) {
            CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchemaWithMongoCalculatedMeasures.json")),CollectionInfo.class);
            collectionInfo.getCollectionDetails().setCollectionName(dbStoreType + date.getTime());
            String collectionId = gsDataImpl.createCustomObject(collectionInfo);
            Assert.assertNotNull(collectionId, "Collection ID should not be null.");
            if (StringUtils.isNotBlank(dbStoreType)) {
                Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.valueOf(StringUtils.upperCase(dbStoreType))), "Failed while updating the DB store type to postgres");
            }
            CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
            collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
            dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob5.txt"),JobInfo.class));
            JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
            File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
            DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[] { "ID", "AccountName", "CustomDate1", "CustomNumber1", "CustomNumber2", "CustomNumberWithDecimals1", "CustomNumberWithDecimals2"},	DataLoadOperationType.INSERT);
            Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
            NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
            Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
        }
    }

    @BeforeMethod
    public void cleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
    }

    @TestInfo(testCaseIds = {"GS-3149", "GS-5134"})
    @Test()
    public void testLoadToCustomers() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3149/GS-3149-input-mongo.json"), RulesPojo.class);
        rulesUtil.UpdateSourceObjectinRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-3149-Mongo.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3149/GS-3149-Mongo-ExpectedData.csv"), null)));
        List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3149/GS-3149-ActualData.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }


    @TestInfo(testCaseIds = { "GS-5135" , "GS-5152"})
    @Test()
    public void testLoadToCustomers2() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5135/GS-5135-input-mongo.json"), RulesPojo.class);
        rulesUtil.UpdateSourceObjectinRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-5135-Mongo.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5135/GS-5135-Mongo-ExpectedData.csv"), null)));
        List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-5135/GS-5135-ActualData.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }

    @TestInfo(testCaseIds = { "GS-230485" })
    @Test()
    public void testLoadToCustomers3() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-230485/GS-230485-input-mongo.json"), RulesPojo.class);
        rulesPojo.getSetupRule().setSelectObject(collectionName);
        rulesPojo.getSetupRule().setJoinWithCollection(collectionName);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
        rulesUtil.UpdateSourceObjectinRule(rulesPojo, collectionName);
        Log.debug(rulesPojo.getSetupActions().toString());
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-230485-Mongo.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-230485/GS-230485-Mongo-ExpectedData.csv"), null)));
        List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(FileUtil.resolveNameSpace(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-230485/GS-230485-ActualData.csv"), null)));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }

}
