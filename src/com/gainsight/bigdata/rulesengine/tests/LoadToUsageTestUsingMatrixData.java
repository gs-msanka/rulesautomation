package com.gainsight.bigdata.rulesengine.tests;

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
import org.omg.CORBA.PRIVATE_MEMBER;
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
 * Created by msanka on 3/21/2016.
 */
public class LoadToUsageTestUsingMatrixData extends BaseTest {

    private static final String USAGE_DATA_MEASURE_FILE = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/UsageData_Measures.apex";
    private static final String ACCOUNT_LEVEL_WEEKLY_FILE = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Set_Account_Level_Weekly.apex";
    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
    private ObjectMapper mapper = new ObjectMapper();
    private DataETL dataETL = new DataETL();
    private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private NSTestBase nsTestBase = new NSTestBase();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private RulesUtil rulesUtil = new RulesUtil();
    private TenantDetails tenantDetails = null;
    private TenantManager tenantManager;
    MongoDBDAO mongoDBDAO = null;
    private String collectionName;
    private  String dbStoreType;
    private Date date = Calendar.getInstance().getTime();
    GSDataImpl gsDataImpl = null;
    private static final String RULE_SCRIPTS_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/";
    private static final String GLOBAL_TEST_DATA_DIR = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/";
    private static final String TEST_DATA_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/";



    /***
     * This function does initial setup required to execute the test cases. Typical setup includes these items:
     * login To Salesforce, Creation and clean up activities on sfdc objects,
     * init the tenant info and creating database collections required(if any).
     * @param dbStoreType defines database type of collection. Valid values are Mongo, Postgres and Redshift.
     * @throws Exception
     */
    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional("Mongo") String dbStoreType) throws Exception {
        basepage.login();
        nsTestBase.init();
        tenantManager = new TenantManager();
        String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
        tenantDetails = tenantManager.getTenantDetail(null, tenantId);
        this.dbStoreType = dbStoreType;
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
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        metaUtil.createFieldsOnUsageData(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(USAGE_DATA_MEASURE_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
    }


    @BeforeMethod
    public void cleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
        sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__UsageData__c];"));
    }

    @TestInfo(testCaseIds = { "GS-5148", "GS-5151" })
    @Test(description = "LoadToUsage data action with ->  Account Level - Weekly with advanced criteria and Verifying Duplication of data is not happening while running rule for second time")
    public void loadToUsageActionWithAccountLevelWeeklyData() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "CollectionSchemaUsageData.json")),CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(this.dbStoreType + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        dataETL.execute(mapper.readValue(resolveNameSpace(GLOBAL_TEST_DATA_DIR + "DataloadJob8.txt"),JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR +"DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[] { "ID", "AccountName", "CustomDate1", "CustomNumber1", "CustomNumber2","CustomNumber3","CustomNumber4", "CustomAggregationName"},	DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ACCOUNT_LEVEL_WEEKLY_FILE));
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-5148/GS-5148-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-5148/GS-5148-Matrix-ExpectedJob.txt"),JobInfo.class));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(TEST_DATA_DIR + "GS-5148/ExpectedData.csv")));
        List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(TEST_DATA_DIR + "GS-5148/ActualData.csv")));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Actual : " + mapper.writeValueAsString(actualData));
        Log.info("Expected : " + mapper.writeValueAsString(expectedData));
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");

        //GS-5151 starts here
        //Running rule for second time to verify duplication of data is not happening while running rule again
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-5148/GS-5148-Matrix-ExpectedJob.txt"),JobInfo.class));
        List<Map<String, String>> expectedData1 = Comparator.getParsedCsvData(new CSVReader(new FileReader(TEST_DATA_DIR + "/GS-5148/ExpectedData.csv")));
        List<Map<String, String>> actualData1 = Comparator.getParsedCsvData(new CSVReader(new FileReader(TEST_DATA_DIR + "GS-5148/ActualData.csv")));
        List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedData1, actualData1);
        Log.info("Actual : " + mapper.writeValueAsString(actualData1));
        Log.info("Expected : " + mapper.writeValueAsString(expectedData1));
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
        Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }

    @TestInfo(testCaseIds = { "GS-5150" })
    @Test(description = "LoadToUsage data action with ->  Instance Level - Monthly Data")
    public void loadToUsageActionWithInstanceLevelMonthlyData() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(RULE_SCRIPTS_DIR + "Set_Instance_Level_Monthly.apex"));
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "CollectionSchemaUsageData1.json")),CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(this.dbStoreType + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        dataETL.execute(mapper.readValue(resolveNameSpace(GLOBAL_TEST_DATA_DIR + "DataloadJob9.txt"),JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR +"DataloadJobMonthly.txt")), JobInfo.class);
        dataETL.execute(loadTransform);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[] { "ID", "AccountName","InstanceId", "InstanceName", "CustomDate1", "CustomNumber1", "CustomNumber2","CustomNumber3","CustomNumber4", "CustomAggregationName"},	DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), new File(TEST_DATA_DIR +"GS-5150/Rules_UsageData_Instance-Output.csv")), "Data is not valid");
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), new File(TEST_DATA_DIR +"GS-5150/Rules_UsageData_Instance-Output.csv"));
        Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-5150/GS-5150-Matrix-input.json"), RulesPojo.class);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed !!!");

        dataETL.execute(mapper.readValue(resolveNameSpace(TEST_DATA_DIR + "GS-5150/GS-5150-Matrix-ExpectedJob.txt"),JobInfo.class));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(TEST_DATA_DIR + "GS-5150/ExpectedData.csv")));
        List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(TEST_DATA_DIR + "GS-5150/ActualData.csv")));
        List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
        Log.info("Actual : " + mapper.writeValueAsString(actualData));
        Log.info("Expected : " + mapper.writeValueAsString(expectedData));
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
