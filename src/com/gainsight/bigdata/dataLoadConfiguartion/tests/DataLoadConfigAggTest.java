package com.gainsight.bigdata.dataLoadConfiguartion.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.Integration.utils.MDAIntegrationImpl;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataLoadConfiguartion.mapping.IdentifierMapper;
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
    TenantDetails tenantDetails;
    DataETL dataETL;
    ReportManager reportManager;

    final String TEST_DATA_FILE = "testdata/newstack/connectors/dataApi/tests/DataLoadAPITests.xls";

    @BeforeClass
    public void setup() throws Exception {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        tenantDetails       = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
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
        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Accounts.json"), JobInfo.class);
        dataETL.execute(jobInfo);
        jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Contacts.json"), JobInfo.class);
        dataETL.execute(jobInfo);
        jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Customers.json"), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @TestInfo(testCaseIds = {"GS-3886"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void AccountIdDateMappedAndMeasureAsSum(HashMap<String, String> testData) throws IOException {
        JobInfo jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Events1.json"), JobInfo.class);
        dataETL.execute(jobInfo);
        jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/jobs/Events2.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t1/CollectionInfo.json"), CollectionInfo.class);

        String collectionName = testData.get("CollectionName")+"_"+cal.getTimeInMillis();
        Log.info("Collection Name : " + collectionName);
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        NsResponseObj nsResponseObj = dataLoadManager.createSubjectArea(collectionInfo);
        Assert.assertNotNull(nsResponseObj);
        Assert.assertTrue(nsResponseObj.isResult());
        CollectionInfo.CollectionDetails colDetails = dataLoadManager.getCollectionDetail(nsResponseObj.getData());
        Assert.assertNotNull(colDetails.getDbCollectionName());
        Assert.assertNotNull(colDetails.getCollectionId());

        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(collectionInfo);

        String statusID = dataLoadManager.dataLoadManage(metadata, new File(Application.basedir+"/testdata/newstack/connectors/dataApi/process/Final_RawEventsData.csv"));
        Assert.assertTrue(dataLoadManager.waitForDataLoadJobComplete(statusID), "Wait for the data load complete failed.");
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(statusID);
        Log.info("CollectionID : " +statusInfo.getCollectionId());
        Assert.assertEquals(0, statusInfo.getFailureCount(), "Failed records should be zero.");


        collectionInfo = dataLoadManager.getCollectionInfo(colDetails.getCollectionId());

        HashMap<String, String> accIdMap = mapper.readValue(testData.get("AccountIdentifier"), HashMap.class);
        HashMap<String, String> accIdProp = mapper.readValue(testData.get("AccountIdentifierProperties"), HashMap.class);
        HashMap<String, String> timeIdMap = mapper.readValue(testData.get("TimeIdentifier"), HashMap.class);
        HashMap<String, String> measureIdMap = mapper.readValue(testData.get("MeasureIdentifier"), HashMap.class);

        Identifier accIdentifier = IdentifierMapper.getAccountIdentifier(dataLoadManager.getColumnByDisplayName(collectionInfo, accIdMap.get("column")), accIdMap.get("targetDisplayName"), accIdProp, Boolean.valueOf(accIdMap.get("directLookUp")), Boolean.valueOf(accIdMap.get("digitConversionEnable")));
        Identifier timeIdentifier = IdentifierMapper.getTimeIdentifier(dataLoadManager.getColumnByDisplayName(collectionInfo, timeIdMap.get("column")), timeIdMap.get("targetDisplayName"));
        Mapping measure          = IdentifierMapper.getMeasureMapping(dataLoadManager.getColumnByDisplayName(collectionInfo, measureIdMap.get("column")), measureIdMap.get("targetDisplayName"), measureIdMap.get("aggFunction"));

        AccountDetailProperties accDetailProp = new AccountDetailProperties();
        accDetailProp.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
        accDetailProp.setTimeZone(testData.get("TimeZone"));

        AccountDetail accountDetail = new AccountDetail();
        GlobalMapping globalMapping = new GlobalMapping();
        globalMapping.setAccountIdentifier(accIdentifier);
        globalMapping.setTimestampIdentifier(timeIdentifier);
        globalMapping.setMeasures(Arrays.asList(new Mapping[]{measure}));
        globalMapping.setSystemDefined(IdentifierMapper.getSFSystemDefined());
        globalMapping.setEventIdentifier(new Identifier());
        globalMapping.setUserIdentifier(new Identifier());
        globalMapping.setCustom(new ArrayList<Mapping>());

        accountDetail.setGlobalMapping(globalMapping);
        accountDetail.setUsageConfiguration(new UsageConfiguration());
        accountDetail.setProperties(accDetailProp);
        accountDetail.setAccountType(testData.get("AccountType"));
        accountDetail.setDisplayName(testData.get("ProjectName") + "_" + cal.getTimeInMillis());
        accountDetail.setNotificationDetails(mapper.readValue(testData.get("NotificationDetails"), NotificationDetails.class));

        RunNowDetails runNowDetails = mapper.readValue(testData.get("RunNowDetails"), RunNowDetails.class);
        runNowDetails.setStartDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getStartDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));
        runNowDetails.setEndDate(DateUtil.addDays(cal.getTime(), Integer.valueOf(runNowDetails.getEndDate()), DateUtil.DEFAULT_UTC_DATE_FORMAT));

        accountDetail.setRunNowDetails(runNowDetails);
        String accountDetailsJson = mapper.writeValueAsString(accountDetail);

        Log.info("Final Project Schema :" +accountDetailsJson);

        nsResponseObj = dataLoadAggConfigManager.createDataApiProject(accountDetailsJson, AccountActionType.SAVE_AND_RUN.name());
        System.out.println(nsResponseObj.isResult());
        HashMap<String, String> response = mapper.convertValue(nsResponseObj.getData(), HashMap.class);
        String statusId =  response.get("statusId");
        Assert.assertNotNull(statusId, "Status Id should not be null");

        Assert.assertTrue(dataLoadAggConfigManager.waitForAggregationJobToComplete(statusId), "Wait for the Aggregation job Failed.");
        Assert.assertTrue(dataLoadAggConfigManager.isDataAggregationCompleteWithSuccess(statusId), "Status of Aggregation job is not complete.");

        String endCollectionName = accountDetail.getDisplayName() + " Day Agg";
        Log.info("endCollectionName: " +endCollectionName);

        MDADateProcessor dateProcessor  = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t1/DateProcess.json"), MDADateProcessor.class);
        FileProcessor.getDateProcessedFile(dateProcessor,cal.getTime());

        jobInfo = mapper.readValue(new File(Application.basedir+"/testdata/newstack/connectors/dataApi/tests/t1/Transform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        CollectionInfo aggCollectionInfo = dataLoadManager.getCollection(endCollectionName);

        List<Map<String,String>> actualData  = reportManager.convertReportData(reportManager.runReport(reportManager.createDynamicTabularReport(aggCollectionInfo)));
        com.gainsight.testdriver.Log.info("ActualData Size : " + actualData.size());
        actualData = reportManager.getProcessedReportData(actualData, aggCollectionInfo);
        Log.info("Actual Data : " + mapper.writeValueAsString(actualData));

        CSVReader expectedReader = new CSVReader(new FileReader(Application.basedir+jobInfo.getTransformationRule().getOutputFileLoc()));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(expectedReader);
        Log.info("Expected Data  " +mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.info("Un-Matched Records " +mapper.writeValueAsString(diffData));
        Assert.assertEquals(diffData.size(), 0, "No of unmatched records should be zero.");

        accountDetail = dataLoadAggConfigManager.getAccountDetailByProjectName(accountDetail.getDisplayName());

        Assert.assertNotNull(accountDetail, "Account Detail Should not be null...");

        Assert.assertTrue(dataLoadAggConfigManager.deleteAccount(accountDetail.getAccountId()), "Project delete failed...");
    }

    //To Delete All the projects of a tenant.
    private void deleteAllProjects() {
        NsResponseObj nsResponseObj = dataLoadAggConfigManager.getAllDataAPIProjects();
        List<AccountDetail> accountDetailList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<AccountDetail>>() {});
        for(AccountDetail accountDetail : accountDetailList) {
            System.out.println(dataLoadAggConfigManager.deleteAccount(accountDetail.getAccountId()));
        }
    }

}
