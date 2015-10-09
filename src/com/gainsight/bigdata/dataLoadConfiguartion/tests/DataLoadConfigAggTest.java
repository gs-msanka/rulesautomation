package com.gainsight.bigdata.dataLoadConfiguartion.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.Integration.utils.MDAIntegrationImpl;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataLoadConfiguartion.apiImpl.DataLoadAggConfigManager;
import com.gainsight.bigdata.dataLoadConfiguartion.enums.AccountActionType;
import com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails.*;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.MDADateProcessor;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.annotations.TestInfo;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Giribabu on 07/07/15.
 */
public class DataLoadConfigAggTest extends NSTestBase {

    DataLoadManager dataLoadManager;
    DataLoadAggConfigManager dataLoadAggConfigManager;
    Date date = Calendar.getInstance().getTime();
    TenantDetails tenantDetails;
    DataETL dataETL;
    ReportManager reportManager;
    List<String> accountIdsToDelete = new ArrayList<>();
    List<String> collectionsToDelete = new ArrayList<>();

    final String COLLECTION_MASTER_SCHEMA = Application.basedir+"/testdata/newstack/connectors/dataApi/data/CollectionInfo.json";
    private String testDataFiles = testDataBasePath+"/connectors/dataApi";

    JobInfo eventsJobInfo;

    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws Exception {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        tenantDetails       = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        tenantDetails       = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());

        dataLoadManager     = new DataLoadManager();
        dataETL             = new DataETL();
        reportManager       = new ReportManager();
        dataLoadAggConfigManager = new DataLoadAggConfigManager(header);

        if(dbStoreType !=null && dbStoreType.equalsIgnoreCase("mongo")) {
            if(tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
            }
        } else if(dbStoreType !=null && dbStoreType.equalsIgnoreCase("redshift")) {
            if(!tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
            }
        }

        eventsJobInfo =  mapper.readValue(new File(testDataFiles+"/jobs/Events.json"), JobInfo.class);
        if(false) {  //to run multiple times locally.
            MDAIntegrationImpl integrationImpl = new MDAIntegrationImpl();
            integrationImpl.authorizeMDA();
            metaUtil.createExtIdFieldOnAccount(sfdc);
            metaUtil.createExtIdFieldOnContacts(sfdc);
            dataETL.cleanUp(resolveStrNameSpace("JBCXM__CustomerInfo__c"), null);
            dataETL.cleanUp("Contact", " Email like '%gainsighttest.com' ");
            JobInfo accountJobInfo = mapper.readValue(new File(testDataFiles+"/jobs/Accounts.json"), JobInfo.class);
            dataETL.execute(accountJobInfo);
            JobInfo contactJobInfo = mapper.readValue(new File(testDataFiles+"/jobs/Contacts.json"), JobInfo.class);
            dataETL.execute(contactJobInfo);
            JobInfo customerJobInfo = mapper.readValue(new File(testDataFiles+"/jobs/Customers.json"), JobInfo.class);
            dataETL.execute(customerJobInfo);
        }
        dataETL.execute(eventsJobInfo);
    }

    /**
     * Account Id has account identifier.
     * Date - timestamp identifier.
     * Measures - Sum.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3886"})
    @Test
    public void accountIdDateMappedAndMeasureAsSum() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T1_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t1/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t1/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor, date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t1/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc()) ;

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Id has account identifier
     * Date - timestamp identifier.
     * Measdure - average.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3887"})
    @Test
    public void accountIdDateMappedAndMeasureAsAvg() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T2_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t2/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t2/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t2/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Id has account identifier.
     * Date  - time identifier.
     * Measures - Count.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3888"})
    @Test
    public void accountIdDateMappedAndMeasureAsCount() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T3_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t3/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t3/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t3/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account id has account identifier.
     * Contact id has user identifier.
     * Time - has timestamp identifier.
     * Measures - Sum, Avg.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3889"})
    @Test
    public void accountIdContactIdDateMappedAndMeasuresAsSumAndAvg() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T4_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t4/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t4/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t4/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account ExternalId - account Identifier.
     * Contact External Id - user identifier.
     * Date - timestamp identifier.
     * Measures - Sum , Avg.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3891"})
    @Test
    public void accountExtIdContactExtIdDateMappedAndMeasuresAsSumAndAvg() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T5_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t5/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t5/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t5/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Id has account identidier.
     * Datetime has timestamp identifier.
     * Measures - Sum.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3899"})
    @Test
    public void accIdTimeStampEventMeasureSum() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T6_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t6/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t6/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t6/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Name has account identifier.
     * Event Identifier
     * DateTime - timestamp identifier.
     * Measures - Avg.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3900"})
    @Test
    public void accNameTimeStampEventMeasureAvg() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T7_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t7/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t7/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t7/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account External Id has Account identifier.
     * Time has timestamp identifier.
     * Event identifier.
     * Mesures - Count.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3901"})
    @Test
    public void accExternalIdTimeStampEventMeasureCount() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T8_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t8/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t8/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t8/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Id has account identifier.
     * Contact Id has contact identifier.
     * DateTime has timestamp identifier.
     * Event identifier.
     * Mesaures - Sum, Avg, Count, Min and Max.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3903"})
    @Test
    public void accIdContactIdTimeStampEventMeasuresWithSumAvgCountMinMax() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T9_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t9/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t9/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t9/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account External Id - Account Identifier.
     * Contact External Id - Contact Idenfifier.
     * DateTime - Timestamp identiifier.
     * Event Identifier.
     * Measures - Sum, Avg.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3905"})
    @Test
    public void accExtIdContactExtIdTimeStampEventMeasureSumAvg() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T10_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t10/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t10/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t10/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Id has account identifier.
     * Time has timestamp identifier.
     * Measures - SUM.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3909"})
    @Test
    public void accountIdTimeStampMappedAndMeasureAsSum() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T11_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t11/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t11/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t11/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Id has account identifier.
     * User id has user identifier.
     * TimeStamp has timestamp identifier.
     * Measures - SUM.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3910"})
    @Test
    public void accountIdContactIdTimeStampMeasureSum() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T12_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t12/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t12/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t12/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Contact Id has Account Identifier.
     * Contact Id has user Identifier.
     * Date has timestamp identifier.
     * Measures - Sum, Avg, Count.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3892"})
    @Test
    public void contactIDHasAccountAndUserIdentifierMeasureSumAvgCount() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T13_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t13/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t13/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t13/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Contact Email has Account Identifier.
     * Contact Email has user identifier.
     * Date has time stamp identifier.
     * Measures - Avg, Count, Min and Max.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3893"})
    @Test
    public void contactEmailHasAccountAndUserIdentifierMeasureAvgCountMinMax() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T14_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t14/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t14/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t14/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Contact External ID has account identifier.
     * Contact External Id has user identifier.
     * Date has Time stamp identifier.
     * Measures - Avg, Count, Min and Max.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3894"})
    @Test
    public void contactExternalIdHasAccountAndUserIdentifierMeasureAvgCountMinMax() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T15_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t15/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t15/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t15/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Contact Id Has Account Identifier.
     * Contact Id Has User Identifier.
     * DareTime Has timestamp Identifier.
     * Event Identifier.
     * Measures - Sum, Average and Count.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3906"})
    @Test
    public void contactIDHasAccountAndUserIdentifierWithTimeStampEventMeasureAsSumAvgCount() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T16_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t16/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t16/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t16/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Contact Email Has Account Identifeir.
     * Contact Email Has User Identifier.
     * DateTime has timestamp Identifier.
     * Event Identifier.
     * Measures - Average, Count, MIN and MAX.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3907"})
    @Test
    public void contactEmailHasAccountAndUserIdentifierTimeStampMeasureAvgCountMinMax() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T17_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t17/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t17/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t17/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Contact External Id Has Account Identifier.
     * <br>Contact External Id Has User Identifier.
     * <br>DateTime Has timestamp Identifier.
     * <br>Event Identifier.
     * <br>Measure with Average, Count, Min and MAx.
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3908"})
    @Test
    public void contactExternalIdHasAccountAndUserIdentifierTimeStampMeasureAvgCountMinMax() throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T18_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t18/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles+"/tests/t18/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t18/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    /**
     * Account Identifier - External ID of Account. <br>
     * Contact Identifier - External ID of Contact. <br>
     * Measures With All Agg Types - SUM, AVG, COUNT, MIN, MAX.<br>
     * Date has timestamp identifier. <br>
     * Event Identifier.<br>
     * Custom Fields - No Properties, then add properties, then remove properties.<br>
     * @throws IOException
     */
    @TestInfo(testCaseIds = {"GS-3896", "GS-3897", "GS-3898"})
    @Test
    public void accountAndContactExternalIDWithCustomFieldsAndMeasuresWithAllAggregationTypes() throws IOException {

        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T19_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t19/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getCustom(), dBDisplayNamesMap);

        List<Mapping> customMapping = accountDetail.getGlobalMapping().getCustom();
        accountDetail.getGlobalMapping().setCustom(new ArrayList<Mapping>());

        setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles + "/tests/t19/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor, date);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles + "/tests/t19/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountDetail.getGlobalMapping().setCustom(customMapping);
        statusId = dataLoadAggConfigManager.updateDataLoadApiProject(mapper.writeValueAsString(accountDetail), AccountActionType.SAVE_AND_RUN.name(), accountDetail.getAccountId());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        MDADateProcessor dateProcessor1  = mapper.readValue(new File(testDataFiles + "/tests/t19/DateProcess_1.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor1, date);

        JobInfo jobInfo1 = mapper.readValue(new File(testDataFiles + "/tests/t19/Transform_1.json"), JobInfo.class);
        dataETL.execute(jobInfo1);

        CollectionInfo newAggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(newAggCollectionInfo, jobInfo1.getTransformationRule().getOutputFileLoc());

        accountDetail.getGlobalMapping().setCustom(new ArrayList<Mapping>());
        statusId = dataLoadAggConfigManager.updateDataLoadApiProject(mapper.writeValueAsString(accountDetail), AccountActionType.SAVE_AND_RUN.name(), accountDetail.getAccountId());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");

        aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        statusId = dataLoadAggConfigManager.updateDataLoadApiProject(mapper.writeValueAsString(accountDetail), AccountActionType.SAVE_AND_RUN.name(), accountDetail.getAccountId());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }




    @AfterClass
    public void tearDown() {
        if(accountIdsToDelete.size() > 0) {
            Log.info("Deleting Accounts...");
            for(String accId : accountIdsToDelete) {
                if(dataLoadAggConfigManager.deleteAccount(accId)) {
                    Log.info("Account Deleted Successfully ::: " +accId);
                } else {
                    Log.error("Account Delete Failed ::: " +accId);
                }
            }
        }
        dataLoadManager.deleteAllCollections(collectionsToDelete, tenantDetails.getTenantId());
    }

    //To Delete All the projects of a tenant - - Run This test case to delete all the accounts in a project..
    //@Test
    private void deleteAllProjects() {
        String projectName = ""; // empty.
        NsResponseObj nsResponseObj = dataLoadAggConfigManager.getAllDataAPIProjects();
        List<AccountDetail> accountDetailList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<AccountDetail>>() {
        });
        Log.info("No of Projects to delete " + accountDetailList.size());
        for(AccountDetail accountDetail : accountDetailList) {
            if (!projectName.equals("") && !projectName.startsWith(accountDetail.getDisplayName())) {
                continue;
            }
            if (dataLoadAggConfigManager.deleteAccount(accountDetail.getAccountId())) {
                Log.info("Account Deleted Successfully ::: " + accountDetail.getAccountId());
            } else {
                Log.error("Account Delete Failed ::: " + accountDetail.getAccountId());
            }
        }

    }

    private void verifyCollectionData(CollectionInfo aggCollectionInfo, String expFilePath) throws IOException {
        List<Map<String,String>> actualData  = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(aggCollectionInfo)));
        Log.info("ActualData Size : " + actualData.size());
        actualData = reportManager.getProcessedReportData(actualData, aggCollectionInfo);
        //Log.info("Actual Data : " + mapper.writeValueAsString(actualData));

        CSVReader expectedReader = new CSVReader(new FileReader(Application.basedir+expFilePath));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        //Log.info("Expected Data  " +mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.info("Un-Matched Records " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(diffData.size(), 0, "No of unmatched records should be zero.");

    }

    private void loadDataToCollection(DataLoadMetadata metadata, String filePath) {
        String statusID = dataLoadManager.dataLoadManage(metadata, new File(Application.basedir + filePath));
        Assert.assertNotNull(statusID);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusID), "Wait for the data load complete failed.");
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(statusID);
        Assert.assertEquals(0, statusInfo.getFailureCount(), "Failed records should be zero.");
    }

    private void setSourceDBNameForIdentifier(Identifier identifier, HashMap<String, String> dBDisplayNamesMap) {
        String name = identifier.getSource().getDisplayName();
        if(name == null) {
            throw new IllegalArgumentException("Source display name can't be null.");
        }
        if(dBDisplayNamesMap == null || !dBDisplayNamesMap.containsKey(name)) {
            throw new IllegalArgumentException("DB Names Map should not be null, db display names map doesn't contains " +name);
        }
        identifier.getSource().setDbName(dBDisplayNamesMap.get(name));
    }

    private void setSourceDBNameForCustomAndMeasureFields(List<Mapping> mappingList, HashMap<String, String> dBDisplayNamesMap) {
        if(mappingList ==null || mappingList.size() ==0) {
            return;
        }
        if(dBDisplayNamesMap == null) {
            throw new IllegalArgumentException("DB Names Map should not be null");
        }
        for(Mapping mapping : mappingList) {
            if(!dBDisplayNamesMap.containsKey(mapping.getSource().getDisplayName())) {
                throw new RuntimeException("DB Names map doesn't contain " +mapping.getSource().getDisplayName());
            }
            mapping.getSource().setDbName(dBDisplayNamesMap.get(mapping.getSource().getDisplayName()));
        }
    }

}
