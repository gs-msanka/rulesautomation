package com.gainsight.sfdc.reporting.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
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
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.gainsight.utils.annotations.TestInfo;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gainsight on 1/18/2016.
 */
public class ReportBuilderMDAMongoUITest extends BaseTest {
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
    TenantDetails tenantDetails = null;
    private static final String COLLECTION_MASTER = "collectionmaster";
    HashMap<String, String> hmap = new HashMap<String, String>();
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
        sfdc.runApexCodeFromFile(new File(Application.basedir
                + "/testdata/newstack/reporting/ReportingUI_Scripts/Create_Accounts_Customers_Reporting.txt"));
        mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
                nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
        tenantDetails = tenantManager.getTenantDetail(sfdcInfo.getOrg(), null);

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
        mongoDBDAO.deleteMongoDocumentFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER, "Auto_Mongo_MDAdata");

        Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
        CollectionInfo collectionInfoMongo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/schema/ReportCollectionInfoUIAutomationMongo.json")), CollectionInfo.class);
        String collectionId = gsDataImpl.createCustomObject(collectionInfoMongo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfoMongo = gsDataImpl.getCollectionMaster(collectionId);

        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/reporting/jobs/PreDataProcessJobMongo.json"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobMongo.json")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfoMongo, new String[]{"ID", "AccountName", "ProductCode", "ProductName", "Date", "EventTimeStamp", "Date1", "EventTimeStamp1", "Date2", "EventTimeStamp2", "Date3", "EventTimeStamp3", "Active", "PageViews", "PageVisits", "FilesDownloaded", "NoofReportsRun", "NoofMeetingsAttended", "NoofRulesTriggered", "NoofSchedulesCreated", "Industry"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj2 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj2.isResult(), "Data is not loaded, please check log for more details");
        mongoDBDAO.deleteMongoDocumentFromReportMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster", "Auto_");
        mongoDBDAO.deleteMongoDocumentFromReportMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster", "SFDCReport");
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);
    }

    @TestInfo(testCaseIds = {"GS-9040"})
    @Test
    public void gridMongoReportForGrid() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/MongoReportWithGrid.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoGridData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-9030"})
    @Test
    public void barMongoReport() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/MongoReportWithBar.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoBarData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-9031"})
    @Test
    public void pieMongoReport() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/MongopieData.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoGridData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-9032"})
    @Test
    public void columnMongoReport() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/MongoColumnData.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoGridData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-9033"})
    @Test
    public void bubbleMongoReport() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/MongoReportWithBubble.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoBubbleData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-9034"})
    @Test
    public void scatterReportForMongoData() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/scatterReportForMongoData.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoScatterData.json"));
        reportingBasePage.createNewReport();

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-9035"})
    @Test
    public void lineReportForMongoData() throws Exception {
        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/lineReportForMongoData.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoLineReportData.json"));
        reportingBasePage.createNewReport();

        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-9036"})
    @Test
    public void areaReportForMongoData() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/areaReportForMongoData.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoAreaReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

   @TestInfo(testCaseIds = {"GS-9037"})
    @Test
    public void stackedBarReportForMongoData() throws Exception {
       ReportMaster reportMaster = mapper.readValue(
               new File(Application.basedir + "/testdata/newstack/reporting/data/stackedBarReportForMongoData.json"),
               ReportMaster.class);
       String str = FileUtils.readFileToString(
               new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoStackedBarReportData.json"));
       reportingBasePage.createNewReport();
       reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
       reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
       reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

   @TestInfo(testCaseIds = {"GS-9038"})
    @Test
    public void stackedColumnReportForMongo() throws Exception {

       ReportMaster reportMaster = mapper.readValue(
               new File(Application.basedir + "/testdata/newstack/reporting/data/stackedColumnReportWith1M2D.json"),
               ReportMaster.class);
       String str = FileUtils.readFileToString(
               new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoStackedColumnReportData.json"));
       reportingBasePage.createNewReport();
       reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
       reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
       reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-9039"})
    @Test
    public void columnLineReportForMongoData() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/columnLineReportForMDAData.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoColumnLineReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-200142"})
    @Test
    public void mongoCalculatedMeasures() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingCalculatedMeasures/MDAMongoCalculatedMeasures.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoCalculationsReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-200143"})
    @Test
    public void stringAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingMDAMongo/MDAMongoAggregationString.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoStringAggReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-200144"})
    @Test
    public void numberAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingMDAMongo/MDAAggregationNumber.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoNumAggReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-200145"})
    @Test
    public void dateAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingMDAMongo/MDAAggregationDate.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoDateAggReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
    }

    @TestInfo(testCaseIds = {"GS-200146"})
    @Test
    public void dateTimeAggregation() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingMDAMongo/MDAAggregationDateTime.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoDateTimeAggReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, null);
    }

   @TestInfo(testCaseIds = {"GS-200147"})
    @Test
    public void booleanAggregation() throws Exception {

       ReportMaster reportMaster = mapper.readValue(
               new File(Application.basedir
                       + "/testdata/newstack/reporting/data/ReportingMDAMongo/MDAAggregationBoolean.json"),
               ReportMaster.class);
       String str = FileUtils.readFileToString(
               new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoBooleanAggReportData.json"));
       reportingBasePage.createNewReport();
       reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
       reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
       reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-200148"})
    @Test
    public void relativeTimeFunctionsFilers() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingMDAMongo/MDARelativeTimeFunctions.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoRelTimeReportData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-200149"})
    @Test
    public void flatReportsWithShowMe() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingMDAMongo/FlatReportsWithMaxShowMe.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoFlatReportWithSMData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);
    }

    @TestInfo(testCaseIds = {"GS-200150"})
    @Test
    public void flatReportsWithRankingAndFilters() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingMDAMongo/FlatReportsWithFilterAndRankingMDAMongo.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoFlatReportWithFRData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

   @TestInfo(testCaseIds = {"GS-200151"})
    @Test
    public void filtersOnNullForAllDataTypes() throws Exception {

       ReportMaster reportMaster = mapper.readValue(
               new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingMDAMongo/filtersOnNullForAllDataType.json"),
               ReportMaster.class);
       String str = FileUtils.readFileToString(
               new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongonullAllData.json"));
       reportingBasePage.createNewReport();
       reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
       reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
       reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-200152"})
    @Test
    public void havingFiltersWithExpressions() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingMDAMongo/HavingFiltersWithExpressions.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoHavingData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @TestInfo(testCaseIds = {"GS-200153"})
    @Test
    public void whereFiltersWithExpressions() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingMDAMongo/WhereFiltersWithExpressions.json"),
                ReportMaster.class);
        String str = FileUtils.readFileToString(
                new File(Application.basedir + "/testdata/newstack/reporting/data/reportData/mongo/MongoWhereData.json"));
        reportingBasePage.createNewReport();
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        reportingUtil.verifyReportData(reportMaster, mongoUtil, str);

    }

    @AfterClass
    public void quit() {
        mongoUtil.closeConnection();
        mongoDBDAO.mongoUtil.closeConnection();
    }
}