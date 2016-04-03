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
import com.gainsight.util.*;
import com.gainsight.util.Comparator;
import com.gainsight.utils.annotations.TestInfo;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import static org.testng.Assert.assertTrue;

/**
 * Created by msanka on 3/15/2016.
 */
public class LoadToCustomersTestUsingRedshiftAsSourceData extends BaseTest {

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
    private String collectionName1;
    private String collectionName2;
    String collectionName;
    private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
    private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private static final String GLOBAL_TEST_DATA_DIR = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/";
    private static final String TEST_DATA_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/";
    private static final String RULE_JOBS = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/";
    private static final String RULE_SCRIPTS_DIR = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/";
    MongoDBDAO mongoDBDAO = null;



    /***
     * This function does initial setup required to execute the test cases. Typical setup includes these items:
     * login To Salesforce, Creation and clean up activities on sfdc objects,
     * init the tenant info and creating database collections required(if any).
     * @throws Exception
     */
    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws Exception {
        
        nsTestBase.init();
        tenantManager = new TenantManager();
        String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
        tenantDetails = tenantManager.getTenantDetail(null,tenantId );
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
            loadSetupData();
        }
    }

    @BeforeMethod
    public void cleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
    }

    @TestInfo(testCaseIds = {"GS-3149","GS-5134","GS-230485", "GS-5152"})
    @Test()
    public void testLoadToCustomers() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-3149/GS-3149-input-redshift.json"), RulesPojo.class);
        rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
        rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName2);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "GS-3149-Mongo.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-3149/GS-3149-Redshift-ExpectedData.csv");
        List<Map<String, String>> actualData =   Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-3149/GS-3149-ActualData.csv");
        List<Map<String, String>> differenceData = com.gainsight.util.Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }

    @TestInfo(testCaseIds = { "GS-5135" })
    @Test()
    public void testLoadToCustomers2() throws Exception {
        RulesPojo rulesPojo = mapper.readValue(new File(TEST_DATA_DIR + "GS-5135/GS-5135-input-redshift.json"), RulesPojo.class);
        rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
        rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
        rulesEngineUtil.updateSourceObjectInRule(rulesPojo, collectionName2);
        Log.debug("updated input testdata/pojo is " +mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(RULE_JOBS + "GS-5135-Redshift.txt"),JobInfo.class);
        dataETL.execute(jobInfo);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-5135/GS-5135-Redshift-ExpectedData.csv");
        List<Map<String, String>> actualData =   Comparator.getParsedCsvDataWithHeaderNamespaceResolved(TEST_DATA_DIR + "GS-5135/GS-5135-ActualData.csv");
        List<Map<String, String>> differenceData = com.gainsight.util.Comparator.compareListData(expectedData, actualData);
        Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
        Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
    }

    private void loadSetupData() throws Exception {
        // creating collection1
        Log.debug("*********************************");
        Log.debug("Creating the collections");
        Log.debug("*********************************");
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "CollectionSchemaUsingRedShiftSource1.json")),CollectionInfo.class);
        collectionInfo1.getCollectionDetails().setCollectionName("collection1" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);

        // Adding redshift calculated measures for collection1 and updating collection schema
        List<CollectionInfo.Column> columnList = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "Columns.json")),new TypeReference<ArrayList<CollectionInfo.Column>>() {});
        actualCollectionInfo1.getColumns().addAll(columnList);
        CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo1, null);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo1));
        Log.info(mapper.writeValueAsString(actualCollectionInfo1));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);
        collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
        Log.info("collection/Table 1 - is " + collectionName1);

        // creating collection2
        CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "CollectionSchemaUsingRedShiftSource2.json")),CollectionInfo.class);
        collectionInfo2.getCollectionDetails().setCollectionName("collection2" + date.getTime());
        String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
        Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
        collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
        Log.info("collection/Table 2 - is " + collectionName2);

        // Forming lookup object "T2-ID" on collection2 with "ID" on collection1 and updating collection
        CollectionUtil.setLookUpDetails(actualCollectionInfo2, "T2_ID",actualCollectionInfo1, "ID", false);
        NsResponseObj nsResponseObj1 = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo2));
        Assert.assertTrue(nsResponseObj1.isResult(), "Collection update failed");

        // loading data into collection/table 1
        dataETL.execute(mapper.readValue(resolveNameSpace(GLOBAL_TEST_DATA_DIR + "DataloadJob7.txt"),JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "DataloadJob.txt")),JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, new String[] { "ID", "AccountName","CustomDate1", "CustomNumber1", "CustomNumber2","CustomNumberWithDecimals1","CustomNumberWithDecimals2" }, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj2 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj2.isResult(), "Data is not loaded, please check log for more details");

        // loading data into collection/table 2
        dataETL.execute(mapper.readValue(resolveNameSpace(GLOBAL_TEST_DATA_DIR + "DataloadJob6.txt"),JobInfo.class));
        JobInfo loadTransform1 = mapper.readValue((new FileReader(GLOBAL_TEST_DATA_DIR + "DataloadJob2.txt")),JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1,date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo2, new String[] { "T2_ID","T2_AccountName", "T2_CustomDate1", "T2_CustomNumber1","T2_CustomNumber2", "T2_CustomNumberWithDecimals1","T2_CustomNumberWithDecimals2" }, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1), "Data is not valid");
        NsResponseObj nsResponseObj3 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj3.isResult(), "Data is not loaded, please check log for more details");
    }
}