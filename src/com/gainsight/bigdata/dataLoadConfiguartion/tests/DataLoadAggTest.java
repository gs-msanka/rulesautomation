package com.gainsight.bigdata.dataLoadConfiguartion.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.Integration.utils.MDAIntegrationImpl;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataLoadConfiguartion.apiImpl.DataLoadAggConfigManager;
import com.gainsight.bigdata.dataLoadConfiguartion.enums.AccountActionType;
import com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails.*;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.MDADateProcessor;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;

import static org.testng.Assert.*;

import com.gainsight.util.DBStoreType;
import com.gainsight.utils.annotations.TestInfo;
import org.testng.annotations.Optional;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Giribabu on 07/07/15.
 */
public class DataLoadAggTest extends NSTestBase {

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
    private static DBStoreType dataBaseType = DBStoreType.MONGO;
    GSDataImpl gsDataImpl;


    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws Exception {
        dataBaseType = (dbStoreType==null || dbStoreType.isEmpty()) ? dataBaseType : DBStoreType.valueOf(dbStoreType);
        assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        gsDataImpl = new GSDataImpl(header);
        TenantInfo tenantInfo = gsDataImpl.getTenantInfo(sfinfo.getOrg());
        tenantDetails       = tenantManager.getTenantDetail(null, tenantInfo.getTenantId());
        dataLoadManager     = new DataLoadManager(sfinfo, getDataLoadAccessKey());
        dataETL             = new DataETL();
        reportManager       = new ReportManager();
        dataLoadAggConfigManager = new DataLoadAggConfigManager(header);

        if(dataBaseType == DBStoreType.MONGO) {
            if(tenantDetails.isRedshiftEnabled()) {
                assertTrue(tenantManager.disableRedShift(tenantDetails));
            }
        } else if(dataBaseType == DBStoreType.REDSHIFT) {
            if(!tenantDetails.isRedshiftEnabled()) {
                assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
            }
        }

        if(true) {  //to run multiple times locally.
            MDAIntegrationImpl integrationImpl = new MDAIntegrationImpl();
            integrationImpl.authorizeMDA();
            metaUtil.createExtIdFieldOnAccount(sfdc);
            metaUtil.createExtIdFieldOnContacts(sfdc);
        }
    }

    /**
     * Account identifier is externalId,  Accounts having the same externalId.
     * 2 Different accounts having same external Id.
     * Account not added as customer.
     */
    @TestInfo(testCaseIds = {"GS-6008"})
    @Test
    public void duplicateAccountIdentifierAndAccIdentifierDoesNotExists() throws Exception {
        sfdc.runApexCode(resolveStrNameSpace(FileUtil.getFileContents(testDataFiles+"/tests/t20/DataSetup.apex")));
        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T20_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        assertNotNull(collectionId, "Collection creation failed.");
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);

        File dataLoadFile = new File(testDataFiles+"/process/t20/Final_Load.csv");
        FileProcessor.getDateProcessedFile(new File(testDataFiles + "/tests/t20/CollectionData.csv"), dataLoadFile, date, "MM-dd-yyyy", "Date");
        DataLoadMetadata metadata  =dataLoadManager.getDefaultDataLoadMetaData(collectionInfo);
        metadata.setMappings(new ArrayList<DataLoadMetadata.Mapping>());
        DataLoadManager.addMapping(metadata, new String[]{"GSA_Account_ExternalId__c", "Date", "GSA_Event", "Logins", "FileSize"});
        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        assertNotNull(jobId, "Data load for collection failed.");
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId), "Data load job is not successful.");

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t20/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        assertNotNull(statusId, "Data Load aggregation status Id should not be null.");
        assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        List<Map<String,String>> actualData  = reportManager.getProcessedReportData(reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(aggCollectionInfo))), aggCollectionInfo);

        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t20/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);
        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles + "/tests/t20/DateProcess.json"), MDADateProcessor.class);
        File expectedData = FileProcessor.getDateProcessedFile(dateProcessor, date);
        List<Map<String, String>> expected =  Comparator.getParsedCsvData(new CSVReader(new FileReader(expectedData)));

        assertEquals(Comparator.compareListData(expected, actualData).size(), 0, "Diff should be null");
    }

    /**
     * Account identifier is externalId, No duplicate found.
     * Contact identifier is externalId, 2 contacts in a account have same externalID.
     * Contact identifier doesn't exists.
     */
    @TestInfo(testCaseIds = {"GS-6008"})
    @Test
    public void duplicateContactIdentifier() throws IOException {
        sfdc.runApexCode(resolveStrNameSpace(FileUtil.getFileContents(testDataFiles+"/tests/t21/DataSetup.apex")));
        JobInfo jobInfo = mapper.readValue(new File(testDataFiles+"/tests/t21/Contacts.json"), JobInfo.class);
        dataETL.execute(jobInfo);  //Loading the contacts.

        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T21_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        assertNotNull(collectionId, "Collection creation failed.");
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);

        File dataLoadFile = new File(testDataFiles+"/process/t21/CollectionData.csv");
        FileProcessor.getDateProcessedFile(new File(testDataFiles + "/tests/t21/CollectionData.csv"), dataLoadFile, date, "MM-dd-yyyy", "Date");
        DataLoadMetadata metadata  =dataLoadManager.getDefaultDataLoadMetaData(collectionInfo);
        metadata.setMappings(new ArrayList<DataLoadMetadata.Mapping>());
        DataLoadManager.addMapping(metadata, new String[]{"GSA_Account_ExternalId__c", "GSA_Contact_ExternalId__c", "Date", "GSA_Event", "PageLoadTime", "FileSize"});
        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        assertNotNull(jobId, "Data load for collection failed.");
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId), "Data load job is not successful.");


        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t21/AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getEventIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        assertNotNull(statusId, "Data Load aggregation status Id should not be null.");
        assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        List<Map<String,String>> actualData  = reportManager.getProcessedReportData(reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(aggCollectionInfo))), aggCollectionInfo);

        JobInfo jobInfo1 = mapper.readValue(new File(testDataFiles+"/tests/t21/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo1);
        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles + "/tests/t21/DateProcess.json"), MDADateProcessor.class);
        dateProcessor.setInputFilePath(jobInfo1.getTransformationRule().getOutputFileLoc());
        File expectedData = FileProcessor.getDateProcessedFile(dateProcessor, date);
        List<Map<String, String>> expected =  Comparator.getParsedCsvData(new CSVReader(new FileReader(expectedData)));

        System.out.println("Exp "+mapper.writeValueAsString(expected));
        System.out.println("Act "+mapper.writeValueAsString(actualData));
        System.out.println("Diff " +mapper.writeValueAsString(Comparator.compareListData(expected, actualData)));

        assertEquals(Comparator.compareListData(expected, actualData).size(), 0, "Diff should be null");
    }


    /**
     * Account identifier is on custom object field (In-direct lookup, custom object field has 15 digit account id value).
     * User identifier is on contact externalId field (which also has 15 digit contat id value).
     */

    @TestInfo(testCaseIds = {"GS-6008"})
    @Test
    public void accountAndUserIdentifierWith15To18DigitConversion() throws IOException {
        sfdc.runApexCode(resolveStrNameSpace(FileUtil.getFileContents(new File(testDataFiles+"/tests/t22/T22DataSetup.apex"))));

        CollectionInfo collectionInfo = mapper.readValue(new File(COLLECTION_MASTER_SCHEMA), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_DATA_T22_" + date.getTime());

        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        assertNotNull(collectionId, "Collection creation failed.");
        collectionInfo = dataLoadManager.getCollectionInfo(collectionId);

        File dataLoadFile = new File(testDataFiles+"/process/t23/T22CollectionData.csv");
        FileProcessor.getDateProcessedFile(new File(testDataFiles + "/tests/t22/T22CollectionData.csv"), dataLoadFile, date, "MM-dd-yyyy", "Date");
        DataLoadMetadata metadata  =dataLoadManager.getDefaultDataLoadMetaData(collectionInfo);
        metadata.setMappings(new ArrayList<DataLoadMetadata.Mapping>());
        DataLoadManager.addMapping(metadata, new String[]{"GSA_AccountName", "GSA_Account_ExternalId__c", "GSA_ContactName", "GSA_Contact_ExternalId__c", "Date", "PageLoadTime", "FileSize"});
        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        assertNotNull(jobId, "Data load for collection failed.");
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId), "Data load job is not successful.");

        AccountDetail accountDetail = mapper.readValue(new File(testDataFiles+"/tests/t22/T22AccountDetail.json"), AccountDetail.class);
        HashMap<String, String> dBDisplayNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getAccountIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getUserIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForIdentifier(accountDetail.getGlobalMapping().getTimestampIdentifier(), dBDisplayNamesMap);
        DataLoadAggConfigManager.setSourceDBNameForCustomAndMeasureFields(accountDetail.getGlobalMapping().getMeasures(), dBDisplayNamesMap);

        accountDetail.setDisplayName(accountDetail.getDisplayName() + "_" + date.getTime());
        accountDetail.getProperties().setCollectionId(collectionId);
        accountDetail.getRunNowDetails().setStartDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        accountDetail.getRunNowDetails().setEndDate(DateUtil.addDays(date, Integer.valueOf(accountDetail.getRunNowDetails().getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        String statusId = dataLoadAggConfigManager.createDataLoadApiProject(accountDetail, AccountActionType.SAVE_AND_RUN.name());
        assertNotNull(statusId, "Data Load aggregation status Id should not be null.");
        assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " + endCollectionName);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);
        List<Map<String,String>> actualData  = reportManager.getProcessedReportData(reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(aggCollectionInfo))), aggCollectionInfo);

        JobInfo jobInfo1 = mapper.readValue(new File(testDataFiles+"/tests/t22/T22Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo1);
        MDADateProcessor dateProcessor  = mapper.readValue(new File(testDataFiles + "/tests/t22/T22DateProcess.json"), MDADateProcessor.class);
        dateProcessor.setInputFilePath(jobInfo1.getTransformationRule().getOutputFileLoc());
        File expectedData = FileProcessor.getDateProcessedFile(dateProcessor, date);
        List<Map<String, String>> expected =  Comparator.getParsedCsvData(new CSVReader(new FileReader(expectedData)));

        System.out.println("Exp "+mapper.writeValueAsString(expected));
        System.out.println("Act "+mapper.writeValueAsString(actualData));

        assertEquals(Comparator.compareListData(expected, actualData).size(), 0, "Diff should be null");

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
        dataLoadManager.deleteAllCollections(collectionsToDelete, tenantDetails.getTenantId(), tenantManager);
    }
}
