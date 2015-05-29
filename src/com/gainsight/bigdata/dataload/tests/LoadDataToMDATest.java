package com.gainsight.bigdata.dataload.tests;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

    @BeforeClass
    public void setup() {
        tenantManager.deleteTenant(sfinfo.getOrg(), null);
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        dataLoadManager = new DataLoadManager();
    }

    @TestInfo(testCaseIds = {"GS-4760"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void insertCommaSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-4790"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void insertCommaSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-3688"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void insertSpaceSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-4791"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void insertSpaceSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-3687"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void insertTabSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-4792"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void insertTabSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-3686"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void insertSemiColonSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-4793"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void insertSemiColonSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        executeTest(testData);
    }


    @TestInfo(testCaseIds = {"GS-3682"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void loadDataWithExtraFieldCreatedFromTenantManagement(HashMap<String, String> testData) throws IOException {
        Calendar calendar = Calendar.getInstance();
        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);
        collectionInfo.getCollectionDetails().setCollectionName(testData.get("CollectionName"));
        metadata.setCollectionName(testData.get("CollectionName"));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob")), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, null);
        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        List<Map<String, String>> actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(actualData.size(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Log.info("ActualData Size : " + actualData.size());

        List<Map<String, String>> processedData = reportManager.getProcessedReportData(actualData, actualCollection);


        CSVReader csvReader = new CSVReader(new FileReader(expectedDataFile));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(csvReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, processedData).size());

        actualCollection = tenantManager.getSubjectAreaMetadata(tenantDetails.getTenantId(), colDetails.getCollectionId());

        CollectionInfo.Column column1 = mapper.readValue(testData.get("Column1"), CollectionInfo.Column.class);
        CollectionInfo.Column column2 = mapper.readValue(testData.get("Column2"), CollectionInfo.Column.class);
        actualCollection.getColumns().add(column1);
        actualCollection.getColumns().add(column2);
        Assert.assertTrue(tenantManager.updateSubjectArea(tenantDetails.getTenantId(), actualCollection));
        actualCollection = tenantManager.getSubjectAreaMetadata(tenantDetails.getTenantId(), colDetails.getCollectionId());

        DataLoadMetadata reInsertMetadata = mapper.readValue(testData.get("DataLoadMetadata1"), DataLoadMetadata.class);
        reInsertMetadata.setCollectionName(testData.get("CollectionName"));

        actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob1")), JobInfo.class);
        expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob1")), JobInfo.class);

        dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, null);
        expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);
        jobId = dataLoadManager.dataLoadManage(reInsertMetadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        ReportManager.addKeysWithEmptyValues(expectedData, new String[]{column1.getDisplayName(), column2.getDisplayName()});

        actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        processedData = reportManager.getProcessedReportData(actualData, actualCollection);
        csvReader = new CSVReader(new FileReader(expectedDataFile));
        expectedData.addAll(Comparator.getParsedCsvData(csvReader));
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, processedData).size());

    }


    @TestInfo(testCaseIds = {"GS-3673", "GS-3672"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void loadDataWithJavaScriptAndHtmlCode(HashMap<String, String> testData) throws IOException {
        Calendar calendar = Calendar.getInstance();

        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);
        collectionInfo.getCollectionDetails().setCollectionName(testData.get("CollectionName"));
        metadata.setCollectionName(testData.get("CollectionName"));

        System.out.println(mapper.writeValueAsString(collectionInfo));
        System.out.println(mapper.writeValueAsString(metadata));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));

        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob")), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, null);
        if (actualJobInfo.getCsvFormatter() != null) {
            dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }

        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        List<Map<String, String>> actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(actualData.size(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Log.info("ActualData Size : " + actualData.size());

        List<Map<String, String>> processedData = reportManager.getProcessedReportData(actualData, actualCollection);


        CSVReader expectedReader = new CSVReader(new FileReader(expectedDataFile));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        DataLoadManager.trimStringDataColumns(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, processedData).size());

        List<String> failedRecords = dataLoadManager.getFailedRecords(jobId);
        Assert.assertNotNull(failedRecords);
        Assert.assertEquals(failedRecords.size() - 1, Integer.parseInt(testData.get("FailedRecordCount")));

    }

    @TestInfo(testCaseIds = {"GS-3681"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void loadDataWithNoColumnInformation(HashMap<String, String> testData) throws IOException {
       executeTest(testData);
    }

    @TestInfo(testCaseIds = {"GS-3646"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void deleteAllCollectionData(HashMap<String, String> testData) throws IOException {
        Calendar calendar = Calendar.getInstance();

        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);
        collectionInfo.getCollectionDetails().setCollectionName(testData.get("CollectionName"));
        metadata.setCollectionName(testData.get("CollectionName"));

        System.out.println(mapper.writeValueAsString(collectionInfo));
        System.out.println(mapper.writeValueAsString(metadata));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));

        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob")), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, null);
        if (actualJobInfo.getCsvFormatter() != null) {
            dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }

        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        List<Map<String, String>> actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(actualData.size(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Log.info("ActualData Size : " + actualData.size());

        List<Map<String, String>> processedData = reportManager.getProcessedReportData(actualData, actualCollection);


        CSVReader expectedReader = new CSVReader(new FileReader(expectedDataFile));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        DataLoadManager.trimStringDataColumns(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, processedData).size());

        jobId = dataLoadManager.clearAllCollectionData(collectionInfo.getCollectionDetails().getCollectionName(), "FILE", collectionInfo.getCollectionDetails().getDataStoreType());
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getMessage(), "Data truncated successfully");
        Assert.assertEquals(statusInfo.getCollectionId(), colDetails.getCollectionId());

        actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(0, actualData.size());

        Assert.assertTrue(tenantManager.deleteSubjectArea(tenantDetails.getTenantId(), colDetails.getCollectionId()));
    }

    @TestInfo(testCaseIds = {"GS-3858"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void deleteCollectionDataWithDateField(HashMap<String, String> testData) throws IOException {
        Calendar calendar = Calendar.getInstance();
        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);
        collectionInfo.getCollectionDetails().setCollectionName(testData.get("CollectionName"));
        metadata.setCollectionName(testData.get("CollectionName"));

        System.out.println(mapper.writeValueAsString(collectionInfo));
        System.out.println(mapper.writeValueAsString(metadata));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));

        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob")), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, null);
        if (actualJobInfo.getCsvFormatter() != null) {
            dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }

        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        List<Map<String, String>> actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(actualData.size(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Log.info("ActualData Size : " + actualData.size());

        List<Map<String, String>> processedData = reportManager.getProcessedReportData(actualData, actualCollection);

        CSVReader expectedReader = new CSVReader(new FileReader(expectedDataFile));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        //DataLoadManager.trimStringDataColumns(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, processedData).size());

        metadata = mapper.readValue(testData.get("DataLoadMetadata1"), DataLoadMetadata.class);
        metadata.setCollectionName(testData.get("CollectionName"));

        File file = new File(Application.basedir + "/resources/datagen/process/GS-3858.csv");

        CSVWriter writer = new CSVWriter(new FileWriter(file), ',', '"', '\\', "\n");
        List<String[]> allLines = new ArrayList<>();
        allLines.add(new String[]{"Date"});
        allLines.add(new String[]{DateUtil.addDays(Calendar.getInstance(), -1, "yyyy-MM-dd")});
        writer.writeAll(allLines);
        writer.flush();
        writer.close();

        jobId = dataLoadManager.dataLoadManage(metadata, file);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);
        Assert.assertEquals(statusInfo.getSuccessCount(), 1); //This seems product issue.

        expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob1")), JobInfo.class);
        expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        actualData = reportManager.getProcessedReportData(actualData, actualCollection);

        expectedReader = new CSVReader(new FileReader(expectedDataFile));
        expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, actualData).size());
    }

    @TestInfo(testCaseIds = {"GS-3857"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void deleteCollectionDataWithDateAccountField(HashMap<String, String> testData) throws IOException {
        Calendar calendar = Calendar.getInstance();
        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);
        collectionInfo.getCollectionDetails().setCollectionName(testData.get("CollectionName"));
        metadata.setCollectionName(testData.get("CollectionName"));

        System.out.println(mapper.writeValueAsString(collectionInfo));
        System.out.println(mapper.writeValueAsString(metadata));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));

        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob")), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, null);
        if (actualJobInfo.getCsvFormatter() != null) {
            dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }

        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        List<Map<String, String>> actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(actualData.size(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Log.info("ActualData Size : " + actualData.size());

        List<Map<String, String>> processedData = reportManager.getProcessedReportData(actualData, actualCollection);

        CSVReader expectedReader = new CSVReader(new FileReader(expectedDataFile));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        //DataLoadManager.trimStringDataColumns(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, processedData).size());

        metadata = mapper.readValue(testData.get("DataLoadMetadata1"), DataLoadMetadata.class);
        metadata.setCollectionName(testData.get("CollectionName"));

        File file = new File(Application.basedir + "/resources/datagen/process/GS-3857.csv");

        CSVWriter writer = new CSVWriter(new FileWriter(file), ',', '"', '\\', "\n");
        List<String[]> allLines = new ArrayList<>();
        allLines.add(new String[]{"Date", "AccountName"});
        allLines.add(new String[]{DateUtil.addDays(Calendar.getInstance(), -2, "yyyy-MM-dd"), "A and T unlimit Limited"});
        writer.writeAll(allLines);
        writer.flush();
        writer.close();

        jobId = dataLoadManager.dataLoadManage(metadata, file);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);
        Assert.assertEquals(statusInfo.getSuccessCount(), 1); //This seems product issue.

        expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob1")), JobInfo.class);
        expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        actualData = reportManager.getProcessedReportData(actualData, actualCollection);

        expectedReader = new CSVReader(new FileReader(expectedDataFile));
        expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, actualData).size());
    }

    @TestInfo(testCaseIds = {"GS-4799", "GS-4801"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T16")
    public void updateDataWithOneKeyColumnAndViaCommaSeparated(HashMap<String, String> testData) throws IOException {
        dataInsertAndUpdate(testData);
    }

    @TestInfo(testCaseIds = {"GS-3690", "GS-3636"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T17")
    public void updateDataWithTwoKeyColumnAndViaTabSeparated(HashMap<String, String> testData) throws IOException {
        dataInsertAndUpdate(testData);
    }

    @TestInfo(testCaseIds = {"GS-3693", "GS-3653"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T18")
    public void upsertToUdpateAndInsertRecordsViaSpaceSeparatorSingleKey(HashMap<String, String> testData) throws IOException {
        dataInsertAndUpdate(testData);
    }

    @TestInfo(testCaseIds = {"GS-3696", "GS-3654"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T19")
    public void upsertToUpdateAllRecordsViaSemiColumnSeparatorMultiKey(HashMap<String, String> testData) throws IOException {
        dataInsertAndUpdate(testData);
    }

    @TestInfo(testCaseIds = {"GS-4800", "GS-3654"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T20")
    public void upsertToInsertAllRecordsSingleKey(HashMap<String, String> testData) throws IOException {
        dataInsertAndUpdate(testData);
    }


    private void dataInsertAndUpdate(HashMap<String, String> testData) throws IOException {
        Date date = Calendar.getInstance().getTime();
        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);
        collectionInfo.getCollectionDetails().setCollectionName(testData.get("CollectionName"));
        metadata.setCollectionName(testData.get("CollectionName"));

        System.out.println(mapper.writeValueAsString(collectionInfo));
        System.out.println(mapper.writeValueAsString(metadata));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));

        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob")), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, date);
        if (actualJobInfo.getCsvFormatter() != null) {
            dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }

        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, date);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        List<Map<String, String>> actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(actualData.size(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Log.info("ActualData Size : " + actualData.size());

        actualData = reportManager.getProcessedReportData(actualData, actualCollection);


        CSVReader expectedReader = new CSVReader(new FileReader(expectedDataFile));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        DataLoadManager.trimStringDataColumns(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(actualData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, actualData).size());
        ////Update Records..
        metadata = mapper.readValue(testData.get("DataLoadMetadata1"), DataLoadMetadata.class);
        metadata.setCollectionName(testData.get("CollectionName"));

        actualJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ActualDataLoadJob1")), JobInfo.class);
        expectedJobInfo = mapper.readValue(new File(Application.basedir + testData.get("ExpectedDataLoadJob1")), JobInfo.class);

        dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, date);
        if (actualJobInfo.getCsvFormatter() != null) {
            dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }


        jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount1")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount1")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        actualData = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));

        actualData = reportManager.getProcessedReportData(actualData, actualCollection);

        expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, date);

        expectedReader = new CSVReader(new FileReader(expectedDataFile));
        expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        DataLoadManager.trimStringDataColumns(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(actualData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, actualData).size());
    }



    private void executeTest(HashMap<String, String> testData) throws IOException {
        Calendar calendar = Calendar.getInstance();
        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);
        collectionInfo.getCollectionDetails().setCollectionName(testData.get("CollectionName"));
        metadata.setCollectionName(testData.get("CollectionName"));

        System.out.println(mapper.writeValueAsString(collectionInfo));
        System.out.println(mapper.writeValueAsString(metadata));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));

        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir+testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir+testData.get("ExpectedDataLoadJob")), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, null);
        if(actualJobInfo.getCsvFormatter()!=null) {
             dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }

        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, null);

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());
        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);

        List<Map<String,String>> actualData  = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(actualCollection)));
        Assert.assertEquals(actualData.size(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Log.info("ActualData Size : " + actualData.size());

        List<Map<String, String>> processedData = reportManager.getProcessedReportData(actualData, actualCollection);


        CSVReader expectedReader = new CSVReader(new FileReader(expectedDataFile));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        DataLoadManager.trimStringDataColumns(expectedData, actualCollection);
        Log.info("Actual Data : " + mapper.writeValueAsString(processedData));
        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));

        Assert.assertEquals(0, Comparator.compareListData(expectedData, processedData).size());
    }



}
