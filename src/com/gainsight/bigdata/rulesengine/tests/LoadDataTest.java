package com.gainsight.bigdata.rulesengine.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class which contains test methods related to loading of testdata to different datastorages
 * which can be used for manual testing/Feature testing
 * Created by Abhilash Thaduka on 1/19/2016.
 */
public class LoadDataTest {

    private static final String SFDC_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObject.txt";
    private NSTestBase nsTestBase;
    private DataETL dataETL = new DataETL();
    private ObjectMapper mapper = new ObjectMapper();
    private static final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup;
    private TenantManager tenantManager;
    private Date date = Calendar.getInstance().getTime();
    private TenantDetails tenantDetails = null;
    private static DBStoreType dataBaseType;
    private String DbType;
    GSDataImpl gsDataImpl = null;
    private MongoDBDAO mongoDBDAO = null;

    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws Exception {
        nsTestBase = new NSTestBase();
        nsTestBase.init();
        rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        DbType = dbStoreType;
        tenantManager = new TenantManager();
        tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(NSTestBase.sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
        if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.MONGO.name())) {
            Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
        } else if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.REDSHIFT.name())) {
            Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
        } else if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.POSTGRES.name())) {
            dataBaseType = DBStoreType.valueOf(dbStoreType);
            mongoDBDAO = new MongoDBDAO(NSTestBase.nsConfig.getGlobalDBHost(), Integer.valueOf(NSTestBase.nsConfig.getGlobalDBPort()), NSTestBase.nsConfig.getGlobalDBUserName(), NSTestBase.nsConfig.getGlobalDBPassword(), NSTestBase.nsConfig.getGlobalDBDatabase());
            TenantDetails.DBDetail schemaDBDetails = null;
            schemaDBDetails = mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
            if (schemaDBDetails == null || schemaDBDetails.getDbServerDetails() == null || schemaDBDetails.getDbServerDetails().get(0) == null) {
                throw new RuntimeException("DB details are not correct, please check it.");
            }
            Log.info("Connecting to schema db....");
            mongoDBDAO = new MongoDBDAO(schemaDBDetails.getDbServerDetails().get(0).getHost().split(":")[0], 27017, schemaDBDetails.getDbServerDetails().get(0).getUserName(), schemaDBDetails.getDbServerDetails().get(0).getPassword(), schemaDBDetails.getDbName());
        }
    }

    @Test(description = "loads data to Mda for datastorages like mongo, postgres,redshift with all datatypes")
    public void loadDataToMda() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/CollectionSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(DbType + "=" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        // Updating DB storage type to postgres from back end.
        if (DbType != null && DbType.equalsIgnoreCase(((DBStoreType.POSTGRES.name())))) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        Log.info("--------------- collection name is ---------------" + collectionName);
        dataETL.execute(mapper.readValue(nsTestBase.getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob3.txt"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult(), "Error while loading data, Check log for more details");
    }

    @Test(description = "loads data to  mongo database for all datatypes along with calculated measures")
    public void loadDataToMongo() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/CollectionSchemaWithMongoCalculatedMeasures.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("MongoWithCalculatedMeasures--" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        Log.info("--------------- collection name is ---------------" + collectionName);
        dataETL.execute(mapper.readValue(nsTestBase.getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob3.txt"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"ID", "AccountName", "CustomDate1", "CustomNumber1", "CustomNumber2", "CustomNumberWithDecimals1", "CustomNumberWithDecimals2", "CreatedDateTime", "BooleanField"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult(), "Error while loading data, Check log for more details");
    }

    @Test(description = "loads data to Redshift database for all datatypes along with calculated measures and joins")
    public void loadDataToRedshift() throws Exception {
        // creating collection1
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/CollectionSchemaUsingRedShiftSource1.json")), CollectionInfo.class);
        collectionInfo1.getCollectionDetails().setCollectionName("RedshiftCollection1" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);

        // Adding redshift calculated measures for collection1 and updating collection schema
        List<CollectionInfo.Column> columnList = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/Columns.json")), new TypeReference<ArrayList<CollectionInfo.Column>>() {
        });
        actualCollectionInfo1.getColumns().addAll(columnList);
        CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo1, null);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo1));
        Log.info(mapper.writeValueAsString(actualCollectionInfo1));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
        Log.info("------------Collection/Table 1 -------- is " + collectionName1);

        // creating collection2
        CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/CollectionSchemaUsingRedShiftSource2.json")), CollectionInfo.class);
        collectionInfo2.getCollectionDetails().setCollectionName("RedshiftCollection2" + date.getTime());
        String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
        Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
        String collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
        Log.info("----------Collection/Table 2 ------------- is " + collectionName2);

        // Forming lookup object "T2-ID" on collection2 with "ID" on collection1 and updating collection
        CollectionUtil.setLookUpDetails(actualCollectionInfo2, "T2_ID", actualCollectionInfo1, "ID", false);
        NsResponseObj nsResponseObj1 = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo2));
        Assert.assertTrue(nsResponseObj1.isResult(), "Collection update failed");

        // loading data into collection/table 1
        dataETL.execute(mapper.readValue(nsTestBase.getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob3.txt"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, new String[]{"ID", "AccountName", "CustomDate1", "CustomNumber1", "CustomNumber2", "CustomNumberWithDecimals1", "CustomNumberWithDecimals2", "CreatedDateTime", "BooleanField"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj2 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj2.isResult(), "Data is not loaded, please check log for more details");

        // loading data into collection/table 2
        dataETL.execute(mapper.readValue(nsTestBase.getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob4.txt"), JobInfo.class));
        JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/Manual-TestData/DataloadJob2.txt")), JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo2, new String[]{"T2_ID", "T2_AccountName", "T2_CustomDate1", "T2_CustomNumber1", "T2_CustomNumber2", "T2_CustomNumberWithDecimals1", "T2_CustomNumberWithDecimals2", "T2_CreatedDateTime", "T2_BooleanField"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1), "Data is not valid");
        NsResponseObj nsResponseObj3 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj3.isResult(), "Data is not loaded, please check log for more details");
    }

    @Test(description = "loads data to sfdc with all datatypes")
    public void loadDataToSfdc() throws IOException {
        NSTestBase.sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        Log.info("Loading data into C_Custom__c sfdc object");
        JobInfo jobInfo = mapper.readValue(nsTestBase.getNameSpaceResolvedFileContents(SFDC_JOB), JobInfo.class);
        dataETL.execute(jobInfo);
        Assert.assertEquals(NSTestBase.sfdc.getRecordCount("SELECT Id FROM C_Custom__c where isdeleted=false"), 9, "Total number of records loaded are not matched");
    }
}
