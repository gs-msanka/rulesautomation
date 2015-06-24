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
    private Calendar calendar = Calendar.getInstance();

    @BeforeClass
    public void setup() {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        dataLoadManager = new DataLoadManager();
    }

    @TestInfo(testCaseIds = {"GS-4760"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void insertCommaSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo),  getFlatCollectionData(collectionInfo));
        Log.info("Diff : " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-4790"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void insertCommaSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-3688"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void insertSpaceSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-4791"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void insertSpaceSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-3687"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void insertTabSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-4792"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void insertTabSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-3686"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void insertSemiColonSeparatedCSVFileWithDoubleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-4793"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void insertSemiColonSeparatedCSVFileWithSingleQuote(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }


    @TestInfo(testCaseIds = {"GS-3682"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void loadDataWithExtraFieldCreatedFromTenantManagement(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        Assert.assertEquals(0, Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo),  getFlatCollectionData(collectionInfo)).size());

        CollectionInfo.Column col1 = mapper.readValue(testData.get("Column1"), CollectionInfo.Column.class);
        CollectionInfo.Column col2 = mapper.readValue(testData.get("Column2"), CollectionInfo.Column.class);
        collectionInfo = addColumnsToCollectionViaTenantMgt(tenantDetails.getTenantId(),
               collectionInfo.getCollectionDetails().getCollectionId(), new CollectionInfo.Column[]{col1, col2});

        jobId = loadDataToCollection(testData.get("ActualDataLoadJob1"), testData.get("DataLoadMetadata1"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));

        List<Map<String, String>> expectedData = getExpectedData(testData.get("ExpectedDataLoadJob1"), collectionInfo);
        List<Map<String, String>> actualData = getFlatCollectionData(collectionInfo);
        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.info("Diff : " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    private CollectionInfo addColumnsToCollectionViaTenantMgt(String tenantId, String collectionId, CollectionInfo.Column[] columns) throws IOException {
        CollectionInfo collectionInfo = tenantManager.getSubjectAreaMetadata(tenantId, collectionId);
        for(CollectionInfo.Column column : columns) {
            collectionInfo.getColumns().add(column);
        }
        Assert.assertTrue(tenantManager.updateSubjectArea(tenantId, collectionInfo));
        collectionInfo = tenantManager.getSubjectAreaMetadata(tenantId, collectionId);
        return collectionInfo;
    }


    @TestInfo(testCaseIds = {"GS-3673", "GS-3672"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void loadDataWithJavaScriptAndHtmlCode(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());

        List<String> failedRecords = dataLoadManager.getFailedRecords(jobId);
        Assert.assertNotNull(failedRecords);
        Assert.assertEquals(failedRecords.size() - 1, Integer.parseInt(testData.get("FailedRecordCount")));

    }

    @TestInfo(testCaseIds = {"GS-3681"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void loadDataWithNoColumnInformation(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    @TestInfo(testCaseIds = {"GS-3646"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void deleteAllCollectionData(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());

        jobId = dataLoadManager.clearAllCollectionData(collectionInfo.getCollectionDetails().getCollectionName(), "FILE", collectionInfo.getCollectionDetails().getDataStoreType());
        Assert.assertNotNull(jobId, "Job Id (or) status id is null.");
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);

        Assert.assertEquals(statusInfo.getMessage(), "Data truncated successfully");
        Assert.assertEquals(statusInfo.getCollectionId(), collectionInfo.getCollectionDetails().getCollectionId());
        Assert.assertEquals(0, getFlatCollectionData(collectionInfo).size());
        Assert.assertTrue(tenantManager.deleteSubjectArea(tenantDetails.getTenantId(), collectionInfo.getCollectionDetails().getCollectionId()));
    }

    @TestInfo(testCaseIds = {"GS-3858"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void deleteCollectionDataWithDateField(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());

       String tempFilePath = Application.basedir + "/resources/datagen/process/GS-3858.csv";

        CSVWriter writer = new CSVWriter(new FileWriter(tempFilePath), ',', '"', '\\', "\n");
        List<String[]> allLines = new ArrayList<>();
        allLines.add(new String[]{"Date"});
        allLines.add(new String[]{DateUtil.addDays(calendar.getTime(), -1, "yyyy-MM-dd")});
        writer.writeAll(allLines);
        writer.flush();
        writer.close();

        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata1"), DataLoadMetadata.class);
        metadata.setCollectionName(collectionName);

        jobId = dataLoadManager.dataLoadManage(metadata, tempFilePath);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);
        Assert.assertEquals(statusInfo.getSuccessCount(), 1); //This seems product issue.
        diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob1"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());

    }

    @TestInfo(testCaseIds = {"GS-3857"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void deleteCollectionDataWithDateAccountField(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());

        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata1"), DataLoadMetadata.class);
        metadata.setCollectionName(collectionName);

        String tempFilePath = Application.basedir + "/resources/datagen/process/GS-3857.csv";

        CSVWriter writer = new CSVWriter(new FileWriter(tempFilePath), ',', '"', '\\', "\n");
        List<String[]> allLines = new ArrayList<>();
        allLines.add(new String[]{"Date", "AccountName"});
        allLines.add(new String[]{DateUtil.addDays(calendar.getTime(), -2, "yyyy-MM-dd"), "A and T unlimit Limited"});
        writer.writeAll(allLines);
        writer.flush();
        writer.close();

        jobId = dataLoadManager.dataLoadManage(metadata, tempFilePath);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);
        Assert.assertEquals(statusInfo.getSuccessCount(), 1); //This seems product issue.
        diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob1"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
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

    /**
     * Load the data to MDA i.e. a test case.
     *
     * @param testData
     * @throws IOException
     */
    private void dataInsertAndUpdate(HashMap<String, String> testData) throws IOException {
        String collectionName = testData.get("CollectionName")+"-"+calendar.getTimeInMillis();
        Log.info("Collection Name : " +collectionName);;
        CollectionInfo collectionInfo = createAndVerifyCollection(testData.get("CollectionSchema"), collectionName);
        String jobId = loadDataToCollection(testData.get("ActualDataLoadJob"), testData.get("DataLoadMetadata"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount")), Integer.valueOf(testData.get("FailedRecordCount")));
        List<Map<String, String>> diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());

        ////Update Records..
        jobId = loadDataToCollection(testData.get("ActualDataLoadJob1"), testData.get("DataLoadMetadata1"), collectionName);
        Assert.assertNotNull(jobId);
        dataLoadManager.waitForDataLoadJobComplete(jobId);
        verifyJobDetails(jobId, collectionName, Integer.valueOf(testData.get("SuccessRecordCount1")), Integer.valueOf(testData.get("FailedRecordCount1")));
        diffData = Comparator.compareListData(getExpectedData(testData.get("ExpectedDataLoadJob1"), collectionInfo), getFlatCollectionData(collectionInfo));
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size());
    }

    /**
     * Creates a subject area / Collection & verifies the collection.
     *
     * @param collectionSchema
     * @param collectionName
     * @return Collection Schema.
     * @throws IOException
     */
    private CollectionInfo createAndVerifyCollection(String collectionSchema, String collectionName) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(collectionSchema, CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        Log.info("Collection Schema : " + mapper.writeValueAsString(collectionInfo));

        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        CollectionInfo actualCollection = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        Assert.assertTrue(dataLoadManager.verifyCollectionInfo(collectionInfo, actualCollection));

       return actualCollection;
    }

    /**
     * Loads the dataFile to MDA & returns the job Id.
     *
     * @param jobFile
     * @param DLMetadata
     * @param collectionName
     * @return JOB id of the submitted request.
     * @throws IOException
     */
    private String loadDataToCollection(String jobFile, String DLMetadata, String collectionName) throws IOException {
        DataLoadMetadata metadata = mapper.readValue(DLMetadata, DataLoadMetadata.class);
        metadata.setCollectionName(collectionName);
        Log.info("Metadata : " +mapper.writeValueAsString(metadata));

        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir+jobFile), JobInfo.class);

        File dataLoadFile = FileProcessor.getDateProcessedFile(actualJobInfo, calendar.getTime());
        if(actualJobInfo.getCsvFormatter()!=null) {
            dataLoadFile = FileProcessor.getFormattedCSVFile(actualJobInfo.getCsvFormatter());
        }

        String jobId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        return jobId;
    }

    /**
     * Verifies the Async Job details.
     *
     * @param jobId - JobId to verify the details.
     * @param collectionName - Collection Name
     * @param successCount - Number of success records.
     * @param failedCount - Number of Failed records.
     */
    private void verifyJobDetails(String jobId, String collectionName, int successCount, int failedCount) {
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionName);
        Assert.assertEquals(statusInfo.getSuccessCount(), successCount);
        Assert.assertEquals(statusInfo.getFailureCount(), failedCount);
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);
    }

    /**
     * Reads the csv file, does date processing, populated default boolean values, trims the text columns to the size specified.
     *
     * @param jobFile - Job File
     * @param collectionInfo - Collection Info i.e. subject are schema.
     * @return List of key values i.e. table data parsed as json.
     * @throws IOException
     */
    private List<Map<String,String>>  getExpectedData(String jobFile, CollectionInfo collectionInfo) throws IOException {
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir+jobFile), JobInfo.class);
        File expectedDataFile = FileProcessor.getDateProcessedFile(expectedJobInfo, calendar.getTime());
        CSVReader expectedReader = new CSVReader(new FileReader(expectedDataFile));

        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, collectionInfo);
        DataLoadManager.trimStringDataColumns(expectedData, collectionInfo);

        Log.info("Expected Data : " + mapper.writeValueAsString(expectedData));
        return expectedData;
    }

    /**
     * Creates a flat report, runs the report, changes the DBNames with Display Names & dos date processing.
     * @param collectionInfo - Collection Schema
     * @return List<Map> - Table data as list of key values.
     * @throws IOException
     */
    private List<Map<String, String>> getFlatCollectionData(CollectionInfo collectionInfo) throws IOException {
        List<Map<String,String>> actualData  = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(collectionInfo)));
        Log.info("ActualData Size : " + actualData.size());
        actualData = reportManager.getProcessedReportData(actualData, collectionInfo);
        Log.info("Actual Data : " +mapper.writeValueAsString(actualData));
        return actualData;
    }
}
