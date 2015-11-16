package com.gainsight.bigdata.dataload.tests;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.annotations.TestInfo;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.type.TypeReference;

import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Giribabu on 28/04/15.
 */
public class LoadDataToMDATest extends NSTestBase {


    private final String TEST_DATA_FILE = "testdata/newstack/dataLoader/tests/DataLoaderTests.xls";
    private TenantDetails tenantDetails;
    private ReportManager reportManager = new ReportManager();
    private DataLoadManager dataLoadManager;
    private Calendar calendar = Calendar.getInstance();
    private Date date = calendar.getTime();
    private List<String> collectionsToDelete = new ArrayList<>();

    private String testDataFiles = testDataBasePath + "/dataLoader";
    MongoDBDAO mongoDBDAO =null;
    boolean useDBName = true;
    private static DBStoreType dataBaseType = DBStoreType.MONGO;
    GSDataImpl gsDataImpl;



    @BeforeClass
    @Parameters({"dbStoreType", "useDBName"})
    public void setup(@Optional String dbStoreType, @Optional String useDBName) throws Exception {
        dataBaseType = dbStoreType !=null ? DBStoreType.valueOf(dbStoreType) : DBStoreType.MONGO;
        this.useDBName = (useDBName == null ? false : Boolean.valueOf(useDBName));
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        gsDataImpl = new GSDataImpl(header);
        TenantInfo tenantInfo = gsDataImpl.getTenantInfo(sfinfo.getOrg());
        tenantDetails =tenantManager.getTenantDetail(null, tenantInfo.getTenantId());
        dataLoadManager = new DataLoadManager(sfinfo, getDataLoadAccessKey());
        if(dataBaseType == DBStoreType.MONGO) {
            if(tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
            }
        } else if(dataBaseType == DBStoreType.REDSHIFT) {
            if(!tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
            }
        }
        //This will help to run the same suite for multiple data bases.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            mongoDBDAO = new  MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
                    nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
        }
    }

    @TestInfo(testCaseIds = {"GS-4760", "GS-3655", "GS-3681", "GS-4373", "GS-4372", "GS-4369", "GS-3634", "GS-4368"})
    @Test
    public void insertCommaSeparatedCSVFileWithDoubleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "1_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        Assert.assertNotNull(collectionId);
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }

        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));
        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-4790"})
    @Test
    public void insertCommaSeparatedCSVFileWithSingleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "2_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        Assert.assertNotNull(collectionId);
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t2/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t2/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);
        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3688"})
    @Test
    public void insertSpaceSeparatedCSVFileWithDoubleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "3_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t3/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t3/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-4791"})
    @Test
    public void insertSpaceSeparatedCSVFileWithSingleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "4_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t4/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t4/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);
        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3687"})
    @Test
    public void insertTabSeparatedCSVFileWithDoubleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "5_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t5/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t5/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());


        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-4792"})
    @Test
    public void insertTabSeparatedCSVFileWithSingleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "6_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t6/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t6/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3686"})
    @Test
    public void insertSemiColonSeparatedCSVFileWithDoubleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "7_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t7/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t7/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-4793"})
    @Test
    public void insertSemiColonSeparatedCSVFileWithSingleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "8_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t8/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t8/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3682"})
    @Test
    public void loadDataWithExtraFieldCreatedFromTenantManagement() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "9_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t9/LoadTransform.json"), JobInfo.class);

        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        List<CollectionInfo.Column> columns = mapper.readValue(new File(testDataFiles + "/tests/t9/ExtraColumns.json"), new TypeReference<ArrayList<CollectionInfo.Column>>() {
        });
        actualCollectionInfo.getColumns().addAll(columns);
        Assert.assertTrue(tenantManager.updateSubjectArea(tenantDetails.getTenantId(), actualCollectionInfo));
        actualCollectionInfo = dataLoadManager.getCollectionInfo(actualCollectionInfo.getCollectionDetails().getCollectionId());

        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t9/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        //To run the test case with dbNames as map i.e CURL behaviour
        metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 8, 0);


        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t9/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3673", "GS-3672"})
    @Test
    public void loadDataWithJavaScriptAndHtmlCode() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/tests/t10/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "10_" + date.getTime());
        if(dataBaseType == DBStoreType.MONGO) {
            CollectionUtil.getColumnByDisplayName(collectionInfo, "ID").setIndexed(true);
        }
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t10/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t10/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        List<String[]> failedRecords = dataLoadManager.getFailedRecords(jobId);
        Assert.assertNotNull(failedRecords);
        Assert.assertEquals(failedRecords.size(), 6);   //5 are actual failed records, 1 is header.

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 5, 5);
        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createDynamicTabularReport(actualCollectionInfo)), actualCollectionInfo);
        List<Map<String, String>> expData = ReportManager.truncateStringData(ReportManager.populateDefaultBooleanValue(Comparator.getParsedCsvData(new CSVReader(new FileReader(expFile))), actualCollectionInfo), actualCollectionInfo);
        Log.info("Actual     : " + mapper.writeValueAsString(actualData));
        Log.info("Expected  : " + mapper.writeValueAsString(expData));
        Assert.assertEquals(actualData.size(), expData.size());

        List<Map<String, String>> diffData = Comparator.compareListData(expData, actualData);
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3646"})
    @Test
    public void deleteAllCollectionData() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "11_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);
        verifyData(actualCollectionInfo, expFile);

        jobId = dataLoadManager.clearAllCollectionData(useDBName ? actualCollectionInfo.getCollectionDetails().getDbCollectionName() : actualCollectionInfo.getCollectionDetails().getCollectionName(), "FILE", collectionInfo.getCollectionDetails().getDataStoreType(), useDBName);
        Assert.assertNotNull(jobId, "Job Id (or) status id is null.");
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);
        List<Map<String, String>> actualData = reportManager.runReportLinksAndGetData(reportManager.createDynamicTabularReport(actualCollectionInfo));
        Assert.assertEquals(0, actualData.size());
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3858", "GS-4370"})
    @Test
    public void deleteCollectionDataWithDateField() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/tests/t12/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "12_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if (dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t12/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));
        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 20, 0);

        String tempFilePath = Application.basedir + "/testdata/newstack/dataLoader/process/t12/temp.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(tempFilePath), ',', '"', '\\', "\n");
        List<String[]> allLines = new ArrayList<>();
        allLines.add(new String[]{"Date"});
        allLines.add(new String[]{DateUtil.addDays(calendar.getTime(), -1, "yyyy-MM-dd")});
        writer.writeAll(allLines);
        writer.flush();
        writer.close();

        metadata = mapper.readValue(new File(testDataFiles + "/tests/t12/ClearMetadata.json"), DataLoadMetadata.class);
        //Building the Metadata to use dBNames.
        if(useDBName) {
            metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getDbCollectionName());
            CollectionInfo.Column column = CollectionUtil.getColumnByDisplayName(actualCollectionInfo, metadata.getKeyFields()[0]);
            metadata.setKeyFields(new String[]{column.getDbName()});
            metadata.getMappings().get(0).setTarget(column.getDbName());
            metadata.setDbNameUsed(true);
        } else {
            metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
        }

        Log.info("Metadata : " + mapper.writeValueAsString(metadata));
        jobId = dataLoadManager.dataLoadManage(metadata, tempFilePath);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));
        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 5, 0);

        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t12/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3857"})
    @Test
    public void deleteCollectionDataWithDateAccountField() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/tests/t13/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "13_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t13/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));
        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 15, 0);

        String tempFilePath = Application.basedir + "/testdata/newstack/dataLoader/process/t13/temp.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(tempFilePath), ',', '"', '\\', "\n");
        List<String[]> allLines = new ArrayList<>();
        allLines.add(new String[]{"Date", "AccountName"});
        allLines.add(new String[]{DateUtil.addDays(calendar.getTime(), -2, "yyyy-MM-dd"), "A and T unlimit Limited"});
        writer.writeAll(allLines);
        writer.flush();
        writer.close();

        //To run the test case with dbNames as mapping i.e CURL behaviour
        metadata = mapper.readValue(new File(testDataFiles + "/tests/t13/ClearMetadata.json"), DataLoadMetadata.class);
        if(useDBName) {
            metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getDbCollectionName());
            CollectionInfo.Column c1 = CollectionUtil.getColumnByDisplayName(actualCollectionInfo, metadata.getKeyFields()[0]);
            CollectionInfo.Column c2 = CollectionUtil.getColumnByDisplayName(actualCollectionInfo, metadata.getKeyFields()[1]);
            metadata.setKeyFields(new String[]{c1.getDbName(), c2.getDbName()});
            metadata.getMappings().get(0).setTarget(c1.getDbName());
            metadata.getMappings().get(1).setTarget(c2.getDbName());
            metadata.setDbNameUsed(true);
        } else  {
            metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
        }

        jobId = dataLoadManager.dataLoadManage(metadata, tempFilePath);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));
        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 6, 0); //it should but 5 there's a product issue that sends 1 record extra.

        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t13/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-4799", "GS-4801"})
    @Test
    public void updateDataWithOneKeyColumnAndViaCommaSeparated() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo_1.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "14_" + date.getTime());
        if(dataBaseType == DBStoreType.MONGO) {
            CollectionUtil.getColumnByDisplayName(collectionInfo, "Id").setIndexed(true);
        }
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t14/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t14/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);

        metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.UPDATE) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        if(useDBName) {
            metadata.setKeyFields(new String[]{CollectionUtil.getColumnByDisplayName(actualCollectionInfo, "Id").getDbName()});
        } else {
            metadata.setDataLoadOperation(DataLoadOperationType.UPDATE.name());
            metadata.setKeyFields(new String[]{"Id"});
        }
        Log.info("Metadata : " + mapper.writeValueAsString(metadata));
        jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));
        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 5, 0);

        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t14/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3690", "GS-3636"})
    @Test
    public void updateDataWithTwoKeyColumnAndViaTabSeparated() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo_1.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "15_" + date.getTime());
        if(dataBaseType == DBStoreType.MONGO) {
            CollectionUtil.getColumnByDisplayName(collectionInfo, "Id").setIndexed(true);
        }
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t15/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);
        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t15/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);

        metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.UPDATE) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        if(useDBName) {
            CollectionInfo.Column c1 = CollectionUtil.getColumnByDisplayName(actualCollectionInfo, "Id");
            CollectionInfo.Column c2 = CollectionUtil.getColumnByDisplayName(actualCollectionInfo, "AccountName");
            metadata.setKeyFields(new String[]{c1.getDbName(), c2.getDbName()});
        } else {
            metadata.setDataLoadOperation(DataLoadOperationType.UPDATE.name());
            metadata.setKeyFields(new String[]{"Id", "AccountName"});
        }

        jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));
        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 7, 0);

        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t15/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3693", "GS-3653"})
    @Test
    public void upsertToUdpateAndInsertRecordsViaSpaceSeparatorSingleKey() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo_1.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "16_" + date.getTime());
        if(dataBaseType == DBStoreType.MONGO) {
            CollectionUtil.getColumnByDisplayName(collectionInfo, "Id").setIndexed(true);
        }
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t16/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        //To run the test case with dbNames as map i.e CURL behaviour
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t16/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        metadata.setDataLoadOperation(DataLoadOperationType.UPSERT.name());
        metadata.setKeyFields(new String[]{"Id"});
        jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t16/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3696", "GS-3654"})
    @Test
    public void upsertToUpdateAllRecordsViaSemiColumnSeparatorMultiKey() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo_1.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "17_" + date.getTime());
        if(dataBaseType == DBStoreType.MONGO) {
            CollectionUtil.getColumnByDisplayName(collectionInfo, "Id").setIndexed(true);
        }
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t17/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t17/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        metadata.setDataLoadOperation(DataLoadOperationType.UPSERT.name());
        metadata.setKeyFields(new String[]{"Id", "AccountName"});
        jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t17/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-4800", "GS-3654"})
    @Test
    public void upsertToInsertAllRecordsSingleKey() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo_1.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "18_" + date.getTime());
        if(dataBaseType == DBStoreType.MONGO) {
            CollectionUtil.getColumnByDisplayName(collectionInfo, "Id").setIndexed(true);
        }
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t18/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);

        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t18/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.UPSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        if(useDBName) {
            metadata.setKeyFields(new String[]{CollectionUtil.getColumnByDisplayName(actualCollectionInfo, "Id").getDbName()});
        } else {
            metadata.setDataLoadOperation(DataLoadOperationType.UPSERT.name());
            metadata.setKeyFields(new String[]{"Id"});
        }

        jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t18/ExpectedTransform.json"), JobInfo.class);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3685"})
    @Test
    public void loadCSVFilePipeAsSeparator() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "19_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));
        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t19/LoadTransform.json"), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataLoadFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setFieldSeparator(loadTransform.getCsvFormatter().getCsvProperties().getSeparator());
        metadata.setEscapeCharacter(loadTransform.getCsvFormatter().getCsvProperties().getEscapeChar());
        metadata.setQuoteCharacter(loadTransform.getCsvFormatter().getCsvProperties().getQuoteChar());

        String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId));

        verifyJobDetails(statusId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        File expFile = new File(Application.basedir + loadTransform.getDateProcess().getOutputFile());

        verifyData(actualCollectionInfo, expFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3633"})
    @Test
    public void insertIntoExistingSubjectArea() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "20_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));
        JobInfo loadTransform1 = mapper.readValue(new File(testDataFiles + "/tests/t20/LoadTransform.json"), JobInfo.class);
        JobInfo loadTransform2 = mapper.readValue(new File(testDataFiles + "/tests/t20/LoadTransform_1.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t20/ExpectedTransform.json"), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform1, date);

        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId));
        verifyJobDetails(statusId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform2, date);
        statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId));
        verifyJobDetails(statusId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        File expectedFile = FileProcessor.getDateProcessedFile(expTransform, date);
        verifyData(actualCollectionInfo, expectedFile);
        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    //Please be notices the failed expected file if opened in excel & saved.
    //will change 9999999999999999999999 to 1E+30 due to which test case may fail with one difference.
    @TestInfo(testCaseIds = {"GS-4398", "GS-5141", "GS-5142", "GS-5145", "GS-4445"})
    @Test
    public void failedRecordsFetchForInvalidDataTypes() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "21_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        File dataLoadFile = new File(testDataFiles+"/tests/t21/CollectionData.csv");

        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId));
        verifyJobDetails(statusId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 3, 11);

        List<String[]> failedRecords = dataLoadManager.getFailedRecords(statusId);
        File outputFile = new File(testDataFiles+"/process/t21/temp.csv");
        outputFile.getParentFile().mkdirs();
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
        writer.writeAll(failedRecords);
        writer.close();

        List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(outputFile)));
        Assert.assertEquals(actualData.size(), 11);
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(testDataFiles+"/tests/t21/FailedExpectedData.csv")));
        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.debug("Diff Data : " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(diffData.size(), 0, "No of difference records should be 0.");

        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @Test
    @TestInfo(testCaseIds = {"GS-3683"})
    public void duplicateCollectionNameVerification() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        String collectionName = collectionInfo.getCollectionDetails().getCollectionName() + "22_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        ResponseObj responseObj = dataLoadManager.createSubjectAreaGetResponseObj(collectionInfo);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, responseObj.getStatusCode(), "Was expected bad request.");
        NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        Assert.assertFalse(nsResponseObj.isResult());
        Assert.assertEquals(MDAErrorCodes.SUBJECT_AREA_ALREADY_EXISTS.getGSCode(), nsResponseObj.getErrorCode());
        Assert.assertEquals("Subject Area Name Already in use please use a different name.", nsResponseObj.getErrorDesc());
    }

    @TestInfo(testCaseIds = {"GS-7832"})
    @Test
    public void testCaseWithAllTypesOfDateFormat() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/tests/t23/CollectionInfo.json"), CollectionInfo.class);
        String collectionName = collectionInfo.getCollectionDetails().getCollectionName() + "23_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        File dataFile = new File(testDataFiles+"/tests/t23/CollectionData.csv");
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);

        String statusId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        verifyJobDetails(statusId, collectionName, 45, 0);

        File expectedFile = new File(testDataFiles+"/tests/t23/ExpectedData.csv");
        verifyData(actualCollectionInfo, expectedFile);
    }

    @TestInfo(testCaseIds = {"GS-8235"})
    @Test
    public void loadDataToOnlyFewFieldsOfSubjectArea() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        String collectionName = collectionInfo.getCollectionDetails().getCollectionName() + "24_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }

        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        File dataFile = new File(testDataFiles+"/tests/t24/CollectionData.csv");

        DataLoadMetadata metadata = null;
        if(useDBName) {
            metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"AccountName", "Date", "PageVisits", "FilesDownloaded", "Active"}, DataLoadOperationType.INSERT );
        } else {
            metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
            metadata.setMappings(new ArrayList<DataLoadMetadata.Mapping>());
            DataLoadManager.addMapping(metadata, new String[]{"AccountName", "Date", "PageVisits", "FilesDownloaded", "Active"});
        }

        String statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId), "Status of data load should be completed.");

        File expectedFile = new File(testDataFiles+"/tests/t24/ExpectedData.csv");
        verifyData(actualCollectionInfo, expectedFile);
    }

    @TestInfo(testCaseIds = {"GS-8236"})
    @Test
    public void headerDoesNotExistsInCustomObject() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        String collectionName = collectionInfo.getCollectionDetails().getCollectionName() + "25_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        /* This line should always be after creating the subject area.
            If redshift is displayed & we try to create a subject area then tables gets created with what ever db store types is supplied.
            Used especially while testing postgres data load.
            To Run on  different DB's - MONGO / REDSHIFT*/
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        //In order to load data to postgres we need to change the dbstore type from back end.
        //Please make sure your global db/schema db details are correct.
        if(dataBaseType == DBStoreType.POSTGRES) {
            Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId, DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        File dataFile = new File(testDataFiles+"/tests/t25/CollectionData.csv");
        DataLoadMetadata metadata = useDBName ? CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT) : dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        if(useDBName) {
            DataLoadMetadata.Mapping mapping = new DataLoadMetadata.Mapping();
            mapping.setSource("AccountID");
            mapping.setTarget("gsd00001");
            metadata.getMappings().add(mapping);
            String jobId  = dataLoadManager.dataLoadManage(metadata, dataFile);
            Assert.assertNotNull(jobId);
            dataLoadManager.waitForDataLoadJobComplete(jobId);
            DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
            Assert.assertEquals("Target field name: gsd00001 does not exist.", statusInfo.getMessage());
        } else {
            DataLoadManager.addMapping(metadata, new String[]{"AccountID"});
            ResponseObj responseObj = dataLoadManager.dataLoadManageGetResponseObject(mapper.writeValueAsString(metadata), dataFile);
            Assert.assertEquals(responseObj.getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Http status code should be 400");

            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            Assert.assertEquals(nsResponseObj.getErrorCode(), MDAErrorCodes.COLUMN_DEF_NOT_EXISTS.getGSCode(), "Error code should be GS_3203");
            Assert.assertEquals(nsResponseObj.getErrorDesc(), "Column definition does not exist. Message: Column does not exists for target field name: AccountID");
        }
    }



    @AfterClass
    public void tearDown() {
        if(mongoDBDAO!=null) mongoDBDAO.mongoUtil.closeConnection();
        dataLoadManager.deleteAllCollections(collectionsToDelete, tenantDetails.getTenantId(), tenantManager);
    }

    /**
     * Just in case used method to delete all the collections.
     */
    //@Test
    public void deleteAllCollection() {
        String collectionName = "GS";
        List<CollectionInfo.CollectionDetails> colList = new ArrayList<>();
        for (CollectionInfo collectionInfo : dataLoadManager.getAllCollections()) {
            if (collectionInfo.getCollectionDetails().getCollectionName().startsWith(collectionName)) {
                colList.add(collectionInfo.getCollectionDetails());
            }
        }
        dataLoadManager.deleteAllCollections(tenantDetails.getTenantId(), colList, tenantManager);
    }

    /**
     * Verifies the Async Job details.
     *
     * @param jobId          - JobId to verify the details.
     * @param collectionName - Collection Name
     * @param successCount   - Number of success records.
     * @param failedCount    - Number of Failed records.
     */
    private void verifyJobDetails(String jobId, String collectionName, int successCount, int failedCount) {
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionName);
        Assert.assertEquals(statusInfo.getSuccessCount(), successCount);
        Assert.assertEquals(statusInfo.getFailureCount(), failedCount);
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);
    }

    private void verifyData(CollectionInfo actualCollectionInfo, File expFile) throws Exception {
        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createDynamicTabularReport(actualCollectionInfo)), actualCollectionInfo);
        List<Map<String, String>> expData = ReportManager.populateDefaultBooleanValue(Comparator.getParsedCsvData(new CSVReader(new FileReader(expFile))), actualCollectionInfo);
        Log.info("Actual     : " + mapper.writeValueAsString(actualData));
        Log.info("Expected  : " + mapper.writeValueAsString(expData));
        Assert.assertEquals(actualData.size(), expData.size());

        List<Map<String, String>> diffData = Comparator.compareListData(expData, actualData);
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());

    }


}
