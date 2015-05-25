package com.gainsight.bigdata.dataload.tests;


import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.util.Comparator;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Giribabu on 28/04/15.
 */
public class LoadDataToMDATest extends DataLoadManager {


    private final String TEST_DATA_FILE = "testdata/newstack/dataLoader/tests/DataLoaderTests.xls";
    private TenantDetails tenantDetails;
    private ReportManager reportManager = new ReportManager();

    @BeforeClass
    public void setup() {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
    }


    @TestInfo(testCaseIds = {"GS-4760"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void loadDataToMDACommaSeparated(HashMap<String, String> testData) throws IOException {
        CollectionInfo collectionInfo = mapper.readValue(testData.get("CollectionSchema"), CollectionInfo.class);
        DataLoadMetadata metadata = mapper.readValue(testData.get("DataLoadMetadata"), DataLoadMetadata.class);

        NsResponseObj nsResponseObj = createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());

        CollectionInfo.CollectionDetails colDetails = getCollectionDetail(nsResponseObj.getData());

        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());


        CollectionInfo actualCollection = getCollectionInfo(colDetails.getCollectionId());
        Assert.assertNotNull(actualCollection);
        verifyCollectionInfo(collectionInfo, actualCollection);


        JobInfo actualJobInfo = mapper.readValue(new File(Application.basedir+testData.get("ActualDataLoadJob")), JobInfo.class);
        JobInfo expectedJobInfo = mapper.readValue(new File(Application.basedir+testData.get("ExpectedDataLoadJob")), JobInfo.class);

        FileProcessor.getDateProcessedFile(actualJobInfo);
        FileProcessor.getDateProcessedFile(expectedJobInfo);


        File dataFile = new File(Application.basedir+actualJobInfo.getDateProcess().getOutputFile());

        String jobId = dataLoadManage(metadata, dataFile);
        Assert.assertNotNull(jobId);
        waitForDataLoadJobComplete(jobId);

        DataLoadStatusInfo statusInfo = getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionInfo.getCollectionDetails().getCollectionName());

        Assert.assertEquals(statusInfo.getFailureCount(), Integer.parseInt(testData.get("FailedRecordCount")));
        Assert.assertEquals(statusInfo.getSuccessCount(), Integer.parseInt(testData.get("SuccessRecordCount")));
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);


        String reportMaster = reportManager.createDynamicTabularReport(actualCollection);
        String reportContent = reportManager.runReport(reportMaster);

        List<Map<String,String>> actualData  = reportManager.convertReportData(reportContent);

        Assert.assertEquals(testData.get("SuccessRecordCount"), actualData.size());
        System.out.println("actualData" +actualData.size());

        List<Map<String, String>> processedData = reportManager.getProcessedReportData(actualData, actualCollection);


        CSVReader expectedReader = new CSVReader(new FileReader(new File(Application.basedir+expectedJobInfo.getDateProcess().getOutputFile())));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        expectedData = reportManager.populateDefaultBooleanValue(expectedData, actualCollection);

        System.out.println("Actual Data : " + mapper.writeValueAsString(processedData));
        System.out.println("Expected Data : " + mapper.writeValueAsString(expectedData));
        System.out.println("Final Data " + mapper.writeValueAsString(Comparator.compareListData(expectedData, processedData)));

    }


}
