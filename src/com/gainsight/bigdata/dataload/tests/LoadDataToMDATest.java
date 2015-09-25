package com.gainsight.bigdata.dataload.tests;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.annotations.TestInfo;
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

    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws IOException {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        dataLoadManager = new DataLoadManager();
        if (dbStoreType != null && dbStoreType.equalsIgnoreCase("mongo")) {
            Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
        } else if (dbStoreType != null && dbStoreType.equalsIgnoreCase("redshift")) {
            Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
        }
    }

    @TestInfo(testCaseIds = {"GS-4760", "GS-3655", "GS-3681", "GS-4373", "GS-4372", "GS-4369", "GS-3634", "GS-4368"})
    @Test
    public void insertCommaSeparatedCSVFileWithDoubleQuote() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "1_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        String jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
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
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t2/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t2/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t3/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t3/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t4/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t4/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t5/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t5/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t6/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t6/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t7/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t7/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t8/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t8/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t9/LoadTransform.json"), JobInfo.class);

        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        String jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
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
        jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
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
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t10/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t10/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);

        String jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        List<String> failedRecords = dataLoadManager.getFailedRecords(jobId);
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/LoadTransform.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t1/ExpectedTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        File expFile = FileProcessor.getDateProcessedFile(expTransform, date);
        String jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);
        verifyData(actualCollectionInfo, expFile);

        jobId = dataLoadManager.clearAllCollectionData(actualCollectionInfo.getCollectionDetails().getCollectionName(), "FILE", collectionInfo.getCollectionDetails().getDataStoreType());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t12/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        String jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
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

        DataLoadMetadata metadata = mapper.readValue(new File(testDataFiles + "/tests/t12/ClearMetadata.json"), DataLoadMetadata.class);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t13/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        String jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
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

        DataLoadMetadata metadata = mapper.readValue(new File(testDataFiles + "/tests/t13/ClearMetadata.json"), DataLoadMetadata.class);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t14/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        String jobId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t14/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setDataLoadOperation(DataLoadOperationType.UPDATE.name());
        metadata.setKeyFields(new String[]{"Id"});
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
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t15/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setDataLoadOperation(DataLoadOperationType.UPDATE.name());
        metadata.setKeyFields(new String[]{"Id", "AccountName"});
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
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t16/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
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
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t17/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
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
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertNotNull(actualCollectionInfo);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));

        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t18/LoadTransform.json"), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());

        String jobId = dataLoadManager.dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(jobId), "Wait for the data load complete failed.");
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(jobId));

        verifyJobDetails(jobId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t18/LoadTransform_1.json"), JobInfo.class);
        dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());

        metadata.setDataLoadOperation(DataLoadOperationType.UPSERT.name());
        metadata.setKeyFields(new String[]{"Id"});
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));
        JobInfo loadTransform = mapper.readValue(new File(testDataFiles + "/tests/t19/LoadTransform.json"), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        dataLoadFile = FileProcessor.getFormattedCSVFile(loadTransform.getCsvFormatter());

        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
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
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));
        JobInfo loadTransform1 = mapper.readValue(new File(testDataFiles + "/tests/t20/LoadTransform.json"), JobInfo.class);
        JobInfo loadTransform2 = mapper.readValue(new File(testDataFiles + "/tests/t20/LoadTransform_1.json"), JobInfo.class);
        JobInfo expTransform = mapper.readValue(new File(testDataFiles + "/tests/t20/ExpectedTransform.json"), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform1, date);

        String statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId));

        verifyJobDetails(statusId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 9, 0);

        dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform2, date);
        statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId));

        verifyJobDetails(statusId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 10, 0);

        File expectedFile = FileProcessor.getDateProcessedFile(expTransform, date);
        verifyData(actualCollectionInfo, expectedFile);

        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }

    @TestInfo(testCaseIds = {"GS-4398"})
    @Test
    public void failedRecordsFetchForInvalidDataTypes() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "21_" + date.getTime());
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollectionInfo));


        File dataLoadFile = new File(testDataFiles+"/tests/t21/CollectionData.csv");

        String statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo), dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        Assert.assertTrue(dataLoadManager.isdataLoadJobCompleted(statusId));

        //Failed records should be 12 & Success records should be 2.
        verifyJobDetails(statusId, actualCollectionInfo.getCollectionDetails().getCollectionName(), 3, 11);

        List<String> failedRecords = dataLoadManager.getFailedRecords(statusId);

        collectionsToDelete.add(actualCollectionInfo.getCollectionDetails().getCollectionId());
    }


    @AfterClass
    public void tearDown() {
        dataLoadManager.deleteAllCollections(collectionsToDelete, tenantDetails.getTenantId());
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
        dataLoadManager.deleteAllCollections(tenantDetails.getTenantId(), colList);
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
