package com.gainsight.sfdc.reporting.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.pojos.SObject;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.reporting.utils.ReportingUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SfdcRestApi;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.gainsight.utils.annotations.TestInfo;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by Gainsight on 1/18/2016.
 */
public class ReportBuilderMDARedShiftUITest extends BaseTest {
    private ReportingBasePage reportingBasePage;
    private SfdcRestApi sfdcRestApi = new SfdcRestApi();
    private ObjectMapper mapper = new ObjectMapper();
    private MongoDBDAO mongoDBDAO = null;
    private MongoUtil mongoUtil;
    private NSTestBase nsTestBase = new NSTestBase();
    private GSDataImpl gsDataImpl;
    private TenantManager tenantManager = new TenantManager();
    private ReportingUtil reportingUtil = new ReportingUtil();
    private DataETL dataETL = new DataETL();
    private TenantDetails.DBDetail dbDetail = null;
    private String[] dataBaseDetail = null;
    private String host = null;
    private String port = null;
    private String userName = null;
    private String passWord = null;
    private String reportingBuilderPageUrl;
    private static final String COLLECTION_MASTER = "collectionmaster";
    Date date = Calendar.getInstance().getTime();

    @BeforeClass
    public void setup() throws Exception {
        sfdc.connect();
        basepage.login();
        nsTestBase.init();
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        nsTestBase.tenantAutoProvision();
        reportingBuilderPageUrl = visualForcePageUrl + "ReportBuilder";
        reportingBasePage = new ReportingBasePage();
        mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
                nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
        TenantDetails tenantDetails = tenantManager.getTenantDetail(sfdcInfo.getOrg(), null);

        dbDetail = mongoDBDAO.getDataDBDetail(tenantDetails.getTenantId());
        List<TenantDetails.DBServerDetail> dbDetails = dbDetail.getDbServerDetails();
        for (TenantDetails.DBServerDetail dbServerDetail : dbDetails) {
            dataBaseDetail = dbServerDetail.getHost().split(":");
            host = dataBaseDetail[0];
            port = dataBaseDetail[1];
            userName = dbServerDetail.getUserName();
            passWord = dbServerDetail.getPassword();
        }
        mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());

        tenantDetails = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());
        mongoUtil = new MongoUtil(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
        mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
        mongoDBDAO.deleteMongoDocumentFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER, "Auto_RedShift");


        if (!tenantDetails.isRedshiftEnabled()) {
            Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
        }
        // creating collection1
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/schema/ReportCollectionInfoUIAutomationRedShift.json")), CollectionInfo.class);
        String collectionId = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);

        // Adding redshift calculated measures for collection1 and updating collection schema
        List<CollectionInfo.Column> columnList = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/schema/RedShiftCalculatedColumns.json")), new TypeReference<ArrayList<CollectionInfo.Column>>() {
        });
        actualCollectionInfo1.getColumns().addAll(columnList);
        CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo1, null);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo1));
        Log.info(mapper.writeValueAsString(actualCollectionInfo1));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
        Log.info("collection/Table 1 - is " + collectionName1);

        // creating collection2
        CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/schema/RedShiftJoin1.json")), CollectionInfo.class);
        String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
        Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
        String collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
        Log.info("collection/Table 2 - is " + collectionName2);

        // creating collection3
        CollectionInfo collectionInfo3 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/schema/RedShiftJoin2.json")), CollectionInfo.class);
        String collectionId3 = gsDataImpl.createCustomObject(collectionInfo3);
        Assert.assertNotNull(collectionId3, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo3 = gsDataImpl.getCollectionMaster(collectionId3);
        String collectionName3 = actualCollectionInfo3.getCollectionDetails().getCollectionName();
        Log.info("collection/Table 2 - is " + collectionName3);

        // Forming lookup object "T2-ID" on collection2 with "ID" on collection1 and updating collection
        CollectionUtil.setLookUpDetails(actualCollectionInfo2, "Join1ID", actualCollectionInfo1, "ID", false);
        NsResponseObj nsResponseObj1 = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo2));
        Assert.assertTrue(nsResponseObj1.isResult(), "Collection update failed");

        // Forming lookup object "T2-ID" on collection2 with "ID" on collection1 and updating collection
        CollectionUtil.setLookUpDetails(actualCollectionInfo3, "Join2User", actualCollectionInfo2, "Join1User", false);
        NsResponseObj nsResponseObj2 = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo3));
        Assert.assertTrue(nsResponseObj2.isResult(), "Collection update failed");

        // loading data into collection/table 1
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/reporting/jobs/PreDataProcessJobRedShift.json"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobRedShift.json")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, new String[]{"ID", "AccountName", "ProductCode", "ProductName", "Date", "EventTimeStamp", "Active", "PageViews", "PageVisits", "FilesDownloaded", "NoofReportsRun", "NoofRulesTriggered", "NoofSchedulesCreated", "Industry"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj3 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj3.isResult(), "Data is not loaded, please check log for more details");

        // loading data into collection/table 2
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/reporting/jobs/PreDataProcessJobRedShiftJoin1.json"), JobInfo.class));
        JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobRedShiftJoin1.json")), JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo2, new String[]{"Join1ID", "Join1User", "Join1Name1", "Join1Name2", "Join1Date1", "Join1EventTimeStamp1", "Join1Boolean", "Join1Number1", "Join1Number2", "Join1Number3"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1), "Data is not valid");
        NsResponseObj nsResponseObj4 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj4.isResult(), "Data is not loaded, please check log for more details");

        // loading data into collection/table 3
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/reporting/jobs/PreDataProcessJobRedShiftJoin2.json"), JobInfo.class));
        JobInfo loadTransform2 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobRedShiftJoin2.json")), JobInfo.class);
        File dataFile2 = FileProcessor.getDateProcessedFile(loadTransform2, date);
        DataLoadMetadata metadata2 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo3, new String[]{"Join2ID", "Join2User", "Join2Name1", "Join2Name2", "Join2Date1", "Join2EventTimeStamp1", "Join2Boolean", "Join2Number1", "Join2Number2", "Join2Number3"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata2), dataFile2), "Data is not valid");
        NsResponseObj nsResponseObj5 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata2), dataFile2);
        Assert.assertTrue(nsResponseObj5.isResult(), "Data is not loaded, please check log for more details");

        reportingBasePage.openReportingPage(reportingBuilderPageUrl);
    }

    @TestInfo(testCaseIds = {"GS-9041"})
    @Test
    public void reportUsingRedShiftCW() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SummarizedByWeekRedshift.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCWData.json"));
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-9042"})
    @Test
    public void reportUsingRedShiftCM() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(
                        Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCM.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCMData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);
    }

   @TestInfo(testCaseIds = {"GS-9043"})
    @Test
    public void reportUsingRedShiftCQ() throws Exception {

       ReportMaster reportMaster = mapper.readValue(
               new File(
                       Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCQ.json"),
               ReportMaster.class);
       String str = FileUtils.readFileToString(
               new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCQData.json"));
       reportingBasePage.createNewReport();

       reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
       reportingUtil.verifyReportData(reportMaster, mongoUtil, null);
    }

    @TestInfo(testCaseIds = {"GS-9044"})
    @Test
    public void reportUsingRedShiftCY() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(
                        Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCY.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCYData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);
    }

    @TestInfo(testCaseIds = {"GS-9045"})
    @Test
    public void dateTimeSummarizedByCW() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCW.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftDateTimeCWData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);
    }

    @TestInfo(testCaseIds = {"GS-9046"})
    @Test
    public void dateTimeSummarizedByCM() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCM.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftDateTimeCMData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-9047"})
    @Test
    public void dateTimeSummarizedByCQ() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCQ.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftDateTimeCQData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

   @TestInfo(testCaseIds = {"GS-9048"})
    @Test
    public void dateTimeSummarizedByCY() throws Exception {

       ReportMaster reportMaster = mapper.readValue(
               new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCY.json"),
               ReportMaster.class);
       String str = FileUtils.readFileToString(
               new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftDateTimeCYData.json"));
       reportingBasePage.createNewReport();

       reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
       reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-9058"})
    @Test
    public void stringMDARedShiftAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDARedShiftAggregationString.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftStringAggData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-100274"})
    @Test
    public void numberMDARedshiftAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDARedShiftAggregationNumber.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftNumberAggData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-100275"})
    @Test
    public void dateMDARedShiftAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDARedShiftAggregationDate.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftDateAggData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-100276"})
    @Test
    public void dateTimeAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDARedShiftAggregationDateTime.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftDateTimeAggData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-100277"})
    @Test
    public void booleanAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDARedShiftAggregationBoolean.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftBooleanAggData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

   @TestInfo(testCaseIds = {"GS-100278"})
    @Test
    public void relativeTimeFunctionsFilers() throws Exception {

       ReportMaster reportMaster = mapper.readValue(
               new File(Application.basedir
                       + "/testdata/newstack/reporting/data/ReportingAggeration/MDARedShiftRelativeTimeFunctions.json"),
               ReportMaster.class);
       String str = FileUtils.readFileToString(
               new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftRelativeTimeFunData.json"));
       reportingBasePage.createNewReport();

       reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
       reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

   }

    @TestInfo(testCaseIds = {"GS-100279"})
    @Test
    public void flatReportsWithShowMe() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/FlatMDARedshiftReportsWithMaxShowMe.json"),
                ReportMaster.class);
        /*String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCQData.json"));*/
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100280"})
    @Test
    public void flatReportsWithRankingAndFilters() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/FlatReportsWithFilterAndRanking.json"),
                ReportMaster.class);
       /* String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCQData.json"));*/
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100281"})
    @Test
    public void filtersOnNullForAllDataTypes() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/filtersOnNullRedShiftForAllDataType.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftNullForAllData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100282"})
    @Test
    public void havingFiltersWithExpressions() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/HavingFiltersWithExpressions.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftHavingData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100283"})
    @Test
    public void whereFiltersWithExpressions() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/WhereFiltersWithExpressions.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftWhereData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100386"})
    @Test
    public void mdaJoins() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingMDAJoins/reportsWithMDAJoins.json"),
                ReportMaster.class);
        /*String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCQData.json"));*/
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);

    }

    @Test
    public void mdaCalculatedRedShift() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingCalculatedMeasures/MDARedShiftCalculatedMeasures.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/redshift/RedshiftCalculatedafieldsData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @AfterClass
    public void quit() {
        mongoUtil.closeConnection();
        mongoDBDAO.mongoUtil.closeConnection();
    }
}