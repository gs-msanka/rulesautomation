package com.gainsight.bigdata.rulesengine.dummyTests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
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
import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class CalculatedFieldsAndMeasuresTestUsingRedShiftAsSourceData extends BaseTest {

    private NSTestBase nsTestBase = new NSTestBase();
    private ObjectMapper mapper = new ObjectMapper();
    private DataETL dataETL = new DataETL();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    GSDataImpl gsDataImpl = null;
    private Date date = Calendar.getInstance().getTime();
    private TenantDetails tenantDetails = null;
    private TenantManager tenantManager;
    MongoDBDAO mongoDBDAO = null;
    private String collectionName1;
    private String collectionName2;

    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws Exception {
        basepage.login();
        nsTestBase.init();
        tenantManager = new TenantManager();
        tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
        if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.REDSHIFT.name())) {
            Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails), "Error while enabling redshift plese check credentials");
        }
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt"));
        // Loading testdata at class level in setup
        boolean isLoadTestDataGlobally = true;
        if (isLoadTestDataGlobally) {
            loadSetupData();
        }
    }


    @Test(dataProvider = "testData")
    public void testCalculatedFieldsUsingRedshiftAsSourceData(String fileName) throws Exception {
        Log.info("Creating rule with testdata " + fileName);
        RulesPojo rulesPojo = mapper.readValue(new File(fileName), RulesPojo.class);
        rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
        rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
        rulesPojo.getSetupRule().setSelectObject(collectionName2);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
        Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }


    private void loadSetupData() throws Exception {
        // creating collection1
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchemaUsingRedShiftSource1.json")), CollectionInfo.class);
        collectionInfo1.getCollectionDetails().setCollectionName("collection1" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);

        // Adding redshift calculated measures for collection1 and updating collection schema
        List<CollectionInfo.Column> columnList = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/Columns.json")), new TypeReference<ArrayList<CollectionInfo.Column>>() {
        });
        actualCollectionInfo1.getColumns().addAll(columnList);
        CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo1, null);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo1));
        Log.info(mapper.writeValueAsString(actualCollectionInfo1));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);
        collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
        Log.info("collection/Table 1 - is " + collectionName1);

        // creating collection2
        CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchemaUsingRedShiftSource2.json")), CollectionInfo.class);
        collectionInfo2.getCollectionDetails().setCollectionName("collection2" + date.getTime());
        String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
        Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
        collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
        Log.info("collection/Table 2 - is " + collectionName2);

        // Forming lookup object "T2-ID" on collection2 with "ID" on collection1 and updating collection
        CollectionUtil.setLookUpDetails(actualCollectionInfo2, "T2_ID", actualCollectionInfo1, "ID", false);
        NsResponseObj nsResponseObj1 = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo2));
        Assert.assertTrue(nsResponseObj1.isResult(), "Collection update failed");

        // loading data into collection/table 1
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob3.txt"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, new String[]{"ID", "AccountName", "CustomDate1", "CustomNumber1", "CustomNumber2", "CustomNumberWithDecimals1", "CustomNumberWithDecimals2"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj2 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj2.isResult(), "Data is not loaded, please check log for more details");

        // loading data into collection/table 2
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob4.txt"), JobInfo.class));
        JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob2.txt")), JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo2, new String[]{"T2_ID", "T2_AccountName", "T2_CustomDate1", "T2_CustomNumber1", "T2_CustomNumber2", "T2_CustomNumberWithDecimals1", "T2_CustomNumberWithDecimals2"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1), "Data is not valid");
        NsResponseObj nsResponseObj3 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj3.isResult(), "Data is not loaded, please check log for more details");
    }

    @DataProvider(name = "testData")
    public Object[][] getDataFromDataProvider() {

        return new Object[][]{
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-Input-Redshift.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-Input-Redshift.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-Input-Redshift.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-Input-Redshift.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-Input-Redshift.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/GS-4237-Input-Redshift.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/GS-4238-Input-Redshift.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-Input-Redshift1.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-Input-Redshift1.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/GS-4234-Input-Redshift.json"}
        };
    }
}
