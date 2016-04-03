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
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
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

import static org.testng.Assert.assertTrue;

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
    MongoDBDAO mongoDBDAO = null;
    String collectionName;
    private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
    private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private static final String RULE_SCRIPTS_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/";
    private static final String GLOBAL_TEST_DATA_DIR = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/";
    private static final String TEST_DATA_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/";
    private static final String RULE_JOBS = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/";


    /***
     * This function does initial setup required to execute the test cases. Typical setup includes these items:
     * login To Salesforce, Creation and clean up activities on sfdc objects,
     * init the tenant info and creating database collections required(if any).
     * @param dbStoreType defines the database type of the collection to be created. Valid values are Mongo, Postgres and Redshift.
     * @throws Exception
     */

    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional("Mongo") String dbStoreType) throws Exception {

        nsTestBase.init();
        tenantManager = new TenantManager();
        String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
        tenantDetails = tenantManager.getTenantDetail(null, tenantId);
        if (StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Mongo")) {
            if(tenantDetails.isRedshiftEnabled()){
                mongoDBDAO = MongoDBDAO.getGlobalMongoDBDAOInstance();
                Log.debug("Tenant Id:"+ tenantId);
                assertTrue(mongoDBDAO.disableRedshift(tenantId), "Failed updating dataStoreType to Mongo.");
            }
        }
        else if(StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Redshift")){
            if(!tenantDetails.isRedshiftEnabled()) {
                assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails), "Failed updating dataStoreType to Redshift");
            }
        }
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(RULE_SCRIPTS_DIR + "Create_Accounts_Customers.txt"));
        // Loading testdata at class level in setup
        boolean isLoadTestDataGlobally = true;
        if (isLoadTestDataGlobally) {
            CollectionInfo collectionInfo = mapper.readValue((new FileReader( GLOBAL_TEST_DATA_DIR + "CollectionSchemaWithMongoCalculatedMeasures.json")),CollectionInfo.class);
            collectionInfo.getCollectionDetails().setCollectionName(dbStoreType + date.getTime());
            String collectionId = gsDataImpl.createCustomObject(collectionInfo);
            Assert.assertNotNull(collectionId, "Collection ID should not be null.");
            CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
            collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
            dataETL.execute(mapper.readValue(resolveNameSpace( GLOBAL_TEST_DATA_DIR + "DataloadJob5.txt"),JobInfo.class));
            JobInfo loadTransform = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "DataloadJob.txt")), JobInfo.class);
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
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-3149/GS-3149-input-mongo.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "GS-3149-Mongo.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-3149/GS-3149-Mongo-ExpectedData.csv");
        List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-3149/GS-3149-ActualData.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }


    @TestInfo(testCaseIds = { "GS-5135" , "GS-5152"})
    @Test()
    public void testLoadToCustomers2() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-5135/GS-5135-input-mongo.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "GS-5135-Mongo.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-5135/GS-5135-Mongo-ExpectedData.csv");
        List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-5135/GS-5135-ActualData.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }

    @TestInfo(testCaseIds = { "GS-230485" })
    @Test()
    public void testLoadToCustomers3() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-230485/GS-230485-input-mongo.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.debug(rulesPojo.getSetupActions().toString());
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "GS-230485-Mongo.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-230485/GS-230485-Mongo-ExpectedData.csv");
        List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-230485/GS-230485-ActualData.csv");
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }

    @AfterClass
    public void tearDown() {
        if (mongoDBDAO != null) {
            mongoDBDAO.mongoUtil.closeConnection();
        }
    }
}
