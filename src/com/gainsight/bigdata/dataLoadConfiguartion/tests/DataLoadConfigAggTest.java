package com.gainsight.bigdata.dataLoadConfiguartion.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.Integration.utils.MDAIntegrationImpl;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataLoadConfiguartion.mapping.IdentifierMapper;
import com.gainsight.bigdata.dataLoadConfiguartion.apiImpl.DataLoadAggConfigManager;
import com.gainsight.bigdata.dataLoadConfiguartion.enums.AccountActionType;
import com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails.*;
import com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails.GlobalMapping;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.MDADateProcessor;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    TenantDetails tenantDetails;
    DataETL dataETL;
    ReportManager reportManager;
    List<String> accountIdsToDelete = new ArrayList<>();
    List<String> collectionsToDelete = new ArrayList<>();

    final String TEST_DATA_FILE = "testdata/newstack/connectors/dataApi/tests/DataLoadAPITests.xls";
    final String COLLECTION_MASTER_SCHEMA = "/testdata/newstack/connectors/dataApi/data/CollectionInfo.json";
    JobInfo eventsJobInfo;

    @BeforeClass
    public void setup() throws Exception {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        //tenantDetails       = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        dataLoadManager     = new DataLoadManager();
        dataETL             = new DataETL();
        reportManager       = new ReportManager();
        dataLoadAggConfigManager = new DataLoadAggConfigManager();
        MDAIntegrationImpl integrationImpl = new MDAIntegrationImpl();
        integrationImpl.authorizeMDA();
        metaUtil.createExtIdFieldOnAccount(sfdc);
        metaUtil.createExtIdFieldOnContacts(sfdc);
        dataETL.cleanUp(resolveStrNameSpace("JBCXM__CustomerInfo__c"), null);
        dataETL.cleanUp("Contact", " Email like '%gainsighttest.com' ");
        JobInfo accountJobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Accounts.json"), JobInfo.class);
        dataETL.execute(accountJobInfo);
        JobInfo contactJobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Contacts.json"), JobInfo.class);
        dataETL.execute(contactJobInfo);
        JobInfo customerJobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Customers.json"), JobInfo.class);
        dataETL.execute(customerJobInfo);
        JobInfo eventsJobInfo =  mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Events.json"), JobInfo.class);
        dataETL.execute(eventsJobInfo);
    }

    @TestInfo(testCaseIds = {"GS-3886"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void AccountIdDateMappedAndMeasureAsSum(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + date.getTime());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " +endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t1/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t1/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc()) ;

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3887"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void AccountIdDateMappedAndMeasureAsAvg(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);
        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " +endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t2/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t2/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3888"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void AccountIdDateMappedAndMeasureAsCount(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t3/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t3/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo,jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3889"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void AccountIdContactIdDateMappedAndMeasuresAsSumAndAvg(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);
        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t4/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t4/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3891"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void AccountExtIdContactExtIdDateMappedAndMeasuresAsSumAndAvg(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accountDetail.setProperties(accDetailProp);
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName : " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t5/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t5/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3899"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void accIdTimeStampEventMeasureSum(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName : " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t6/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t6/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3900"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void accNameTimeStampEventMeasureAvg(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " +endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t7/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t7/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3901"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void accExternalIdTimeStampEventMeasureCount(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" + accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor = mapper.readValue(new File(Application.basedir + "/testdata/newstack/connectors/dataApi/tests/t8/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor, cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/connectors/dataApi/tests/t8/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3903"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void accIdContactIdTimeStampEventMeasuresWithSumAvgCountMinMax(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" + accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t9/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t9/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3905"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void accExtIdContactExtIdTimeStampEventMeasureSumAvg(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" + accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t10/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t10/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3909"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void AccountIdTimeStampMappedAndMeasureAsSum(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t11/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t11/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3910"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void AccountIdContactIdTimeStampMeasureSum(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());


        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t12/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t12/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3892"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void contactIDHasAccountAndUserIdentifierMeasureSumAvgCount(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t13/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t13/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3893"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void contactEmailHasAccountAndUserIdentifierMeasureAvgCountMinMax(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t14/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t14/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3894"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void contactExternalIdHasAccountAndUserIdentifierMeasureAvgCountMinMax(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t15/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t15/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }



    @TestInfo(testCaseIds = {"GS-3906"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T16")
    public void contactIDHasAccountAndUserIdentifierWithTimeStampEventMeasureAsSumAvgCount(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t16/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t16/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }


    @TestInfo(testCaseIds = {"GS-3907"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T17")
    public void contactEmailHasAccountAndUserIdentifierTimeStampMeasureAvgCountMinMax(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t17/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t17/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-3908"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T18")
    public void contactExternalIdHasAccountAndUserIdentifierTimeStampMeasureAvgCountMinMax(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t18/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t18/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, jobInfo.getTransformationRule().getOutputFileLoc());

        accountIdsToDelete.add(accountDetail.getAccountId());
        collectionsToDelete.add(collectionInfo.getCollectionDetails().getCollectionId());
    }




    @TestInfo(testCaseIds = {"GS-3896"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T19")
    public void accExtIdContactExtIdTimeStampEventMeasureSumAvgAndCutomFields(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        String collectionName = testData.get("CollectionName") + "_" + date.getTime();
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        collectionInfo = createAndVerifyCollection(collectionInfo);
        loadDataToCollection(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), eventsJobInfo.getTransformationRule().getOutputFileLoc());

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        setAccountIdentifier(collectionInfo, globalMapping, testData);
        setUserIdentifier(collectionInfo, globalMapping, testData);
        setEventIdentifier(collectionInfo, globalMapping, testData);
        setTimeIdentifier(collectionInfo, globalMapping, testData);
        setMeasures(collectionInfo, globalMapping, testData);
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));
        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));
        accountDetail.setProperties(accDetailProp);
        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.setRunNowDetails(runNowDetails);

        String accountDetailsJson = mapper.writeValueAsString(accountDetail);
        Log.info("Final Project Schema :" +accountDetailsJson);

        String statusId =  dataLoadAggConfigManager.createDataLoadApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        Assert.assertNotNull(statusId, "Status Id should not be null");
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");

        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());
        Assert.assertNotNull(accountDetail, "Account Detail Should not be null...");
        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " +endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t19/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor, date);

        JobInfo expectedDataTransformJob = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t19/Transform.json"), JobInfo.class);
        dataETL.execute(expectedDataTransformJob);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        verifyCollectionData(aggCollectionInfo, expectedDataTransformJob.getTransformationRule().getOutputFileLoc());

        /*setCustomFields(collectionInfo, accountDetail.getGlobalMapping(), testData);
        Log.info("Final Project Json : " +mapper.writeValueAsString(accountDetail));

        String statusId =  dataLoadAggConfigManager.updateDataLoadApiProject(mapper.writeValueAsString(accountDetail), AccountActionType.SAVE_AND_RUN.name(), accountDetail.getAccountId());
        Assert.assertNotNull(statusId, "Status Id should not be null");
        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId));
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");
          */
        expectedDataTransformJob = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t19/Transform1.json"), JobInfo.class);
        dataETL.execute(expectedDataTransformJob);
        //verifyCollectionData(endCollectionName, expectedDataTransformJob.getTransformationRule().getOutputFileLoc());

    }

    @Test
    public void getstatus() throws Exception {
        dataLoadAggConfigManager.waitForAggregationJobToComplete("27d5a15b-9a27-4d34-bc44-5c1c509f16c7");
    }

    @AfterSuite
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
    }

    //To Delete All the projects of a tenant - - Run This test case to delete all the accounts in a project..
    @Test
    private void deleteAllProjects() {
        String projectName = ""; // empty.
        NsResponseObj nsResponseObj = dataLoadAggConfigManager.getAllDataAPIProjects();
        List<AccountDetail> accountDetailList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<AccountDetail>>() {});
        Log.info("No of Projects to delete " +accountDetailList.size());
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
        Log.info("Actual Data : " + mapper.writeValueAsString(actualData));

        CSVReader expectedReader = new CSVReader(new FileReader(Application.basedir+expFilePath));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        Log.info("Expected Data  " +mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.info("Un-Matched Records " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(diffData.size(), 0, "No of unmatched records should be zero.");

    }

    private CollectionInfo createAndVerifyCollection(CollectionInfo collectionInfo) throws IOException {
        Log.info("Creating collection : " +collectionInfo.getCollectionDetails().getCollectionName());
        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));
        Log.info("Collection created successfully.");
        Log.info("Collection Id : " +actualCollection.getCollectionDetails().getCollectionId());
        return actualCollection;
    }

    private void loadDataToCollection(DataLoadMetadata metadata, String filePath) {
        String statusID = dataLoadManager.dataLoadManage(metadata, new File(Application.basedir+filePath));
        Assert.assertNotNull(statusID);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusID), "Wait for the data load complete failed.");
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(statusID);
        Assert.assertEquals(0, statusInfo.getFailureCount(), "Failed records should be zero.");
    }

    private void setAccountIdentifier(CollectionInfo collectionInfo, GlobalMapping globalMapping, HashMap<String, String> testData) throws IOException {
        if(!testData.containsKey("AccountIdentifier") ||!testData.containsKey("AccountIdentifierProperties")) {
            throw new IllegalArgumentException("Account Identifier, AccountIdentifierProperties should not be null");
        }
        Log.info("Setting Account Identifier...");
        HashMap<String, String> accIdMap = mapper.readValue(testData.get("AccountIdentifier"), HashMap.class);
        HashMap<String, String> accIdProp = mapper.readValue(testData.get("AccountIdentifierProperties"), HashMap.class);
        Identifier accIdentifier = IdentifierMapper.getAccountIdentifier(dataLoadManager.getColumnByDisplayName(collectionInfo, accIdMap.get("column")), accIdMap.get("targetDisplayName"), accIdProp, Boolean.valueOf(accIdMap.get("directLookUp")), Boolean.valueOf(accIdMap.get("lookup")), Boolean.valueOf(accIdMap.get("digitConversionEnable")));
        globalMapping.setAccountIdentifier(accIdentifier);
        Log.info("Account Identifier Set to project.");
    }

    private void setUserIdentifier(CollectionInfo collectionInfo, GlobalMapping globalMapping, HashMap<String, String> testData) throws IOException {
        if(!testData.containsKey("UserIdentifier") || !testData.containsKey("UserIdentifierProperties")) {
            throw new IllegalArgumentException("UserIdentifier, UserIdentifierProperties should not be null");
        }
        Log.info("Setting User Identifier...");
        HashMap<String, String> userIdMap = mapper.readValue(testData.get("UserIdentifier"), HashMap.class);
        HashMap<String, String> userIdProp = mapper.readValue(testData.get("UserIdentifierProperties"), HashMap.class);
        Identifier userIdentifier = IdentifierMapper.getUserIdentifier(dataLoadManager.getColumnByDisplayName(collectionInfo, userIdMap.get("column")), userIdMap.get("targetDisplayName"), userIdProp, Boolean.valueOf(userIdMap.get("directLookUp")),Boolean.valueOf(userIdMap.get("lookup")), Boolean.valueOf(userIdMap.get("digitConversionEnable")));
        globalMapping.setUserIdentifier(userIdentifier);
        Log.info("User Identifier Set to project.");
    }

    private void setTimeIdentifier(CollectionInfo collectionInfo, GlobalMapping globalMapping, HashMap<String, String> testData) throws IOException {
        if(!testData.containsKey("TimeIdentifier") ) {
            throw new IllegalArgumentException("TimeIdentifier should not be null");
        }
        HashMap<String, String> timeIdMap = mapper.readValue(testData.get("TimeIdentifier"), HashMap.class);
        Identifier timeIdentifier = IdentifierMapper.getTimeIdentifier(dataLoadManager.getColumnByDisplayName(collectionInfo, timeIdMap.get("column")), timeIdMap.get("targetDisplayName"));
        globalMapping.setTimestampIdentifier(timeIdentifier);
        Log.info("Time Identifier Set to project.");
    }

    private void setEventIdentifier(CollectionInfo collectionInfo, GlobalMapping globalMapping, HashMap<String, String> testData) throws IOException {
        if(!testData.containsKey("EventIdentifier") ) {
            throw new IllegalArgumentException("EventIdentifier should not be null");
        }
        HashMap<String, String> eventIdMap = mapper.readValue(testData.get("EventIdentifier"), HashMap.class);
        Identifier eventIdentifier = IdentifierMapper.getEventIdentifier(dataLoadManager.getColumnByDisplayName(collectionInfo, eventIdMap.get("column")), eventIdMap.get("targetDisplayName"));
        globalMapping.setEventIdentifier(eventIdentifier);
        Log.info("EventIdentifier Set to project.");
    }

    private void setMeasures(CollectionInfo collectionInfo, GlobalMapping globalMapping, HashMap<String, String> testData) throws IOException {
        int i=1;
        String measureKey = "MeasureIdentifier";
        List<Mapping> measureList = new ArrayList<>();
        while (testData.containsKey(measureKey+i)) {
            HashMap<String, String> measureIdMap = mapper.readValue(testData.get(measureKey+i), HashMap.class);
            Mapping measure = IdentifierMapper.getMeasureMapping(dataLoadManager.getColumnByDisplayName(collectionInfo, measureIdMap.get("column")), measureIdMap.get("targetDisplayName"), measureIdMap.get("aggFunction"));
            measureList.add(measure);
            ++i;
        }

        globalMapping.setMeasures(measureList);
        Log.info(i + " Measure's are add to project");
    }

    private void setCustomFields(CollectionInfo collectionInfo, GlobalMapping globalMapping, HashMap<String, String> testData) throws IOException {
        int i=1;
        String measureKey = "CustomIdentifier";
        List<Mapping> customFieldList = new ArrayList<>();
        while (testData.containsKey(measureKey+i)) {
            HashMap<String, String> customField1 = mapper.readValue(testData.get(measureKey+i), HashMap.class);
            Mapping customMapping = IdentifierMapper.getCustomMapping(dataLoadManager.getColumnByDisplayName(collectionInfo, customField1.get("column")), customField1.get("targetDisplayName"), null);
            customFieldList.add(customMapping);
            ++i;
        }
        globalMapping.setCustom(customFieldList);
        Log.info(i + " Custom Field's are add to project");
    }

    private void removeCustomField(GlobalMapping globalMapping, String customFieldDisplayeName) {

    }









}
