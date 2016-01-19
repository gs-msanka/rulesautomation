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
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.gainsight.utils.annotations.TestInfo;
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
        sfdc.runApexCodeFromFile(new File(Application.basedir + "/testdata/newstack/reporting/ReportingUI_Scripts/Create_Accounts_Customers_Reporting.txt"));
        //Modifying api names to display names
        List<SObject> soList = sfdcRestApi.getSfdcObjects();

        for (SObject sObject : soList) {
            hmap.put(sObject.getName(), sObject.getLabel());
        }
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
        if (!tenantDetails.isRedshiftEnabled()) {
            Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
        }
        mongoUtil = new MongoUtil(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
        mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
      /*  mongoDBDAO.deleteMongoDocumentFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER, "Auto_Mongo_MDAdata");

        Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
        CollectionInfo collectionInfoMongo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/schema/ReportCollectionInfoUIAutomationMongo.json")), CollectionInfo.class);
        String collectionId = gsDataImpl.createCustomObject(collectionInfoMongo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfoMongo = gsDataImpl.getCollectionMaster(collectionId);

        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/reporting/jobs/PreDataProcessJobMongo.json"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobMongo.json")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfoMongo, new String[]{"ID", "AccountName", "ProductCode", "ProductName", "Date", "EventTimeStamp", "Active", "PageViews", "PageVisits", "FilesDownloaded", "NoofReportsRun", "NoofRulesTriggered", "NoofSchedulesCreated", "Industry"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj2 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj2.isResult(), "Data is not loaded, please check log for more details");*/
        mongoDBDAO.deleteMongoDocumentFromReportMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster", "Auto_");
    }

    @TestInfo(testCaseIds = {"GS-9040"})
    @Test
    public void reportingUIWithBackedJson() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomation.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

    }

    @TestInfo(testCaseIds = {"GS-9030"})
    @Test
    public void barReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/barReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

    }

    @TestInfo(testCaseIds = {"GS-9031"})
    @Test
    public void pieReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/pieReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

    }

    @TestInfo(testCaseIds = {"GS-9032"})
    @Test
    public void columnReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/columnReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
    }

    @TestInfo(testCaseIds = {"GS-9033"})
    @Test
    public void bubbleReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/bubbleReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

    }

    @TestInfo(testCaseIds = {"GS-9034"})
    @Test
    public void scatterReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/scatterReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
    }

    @TestInfo(testCaseIds = {"GS-9035"})
    @Test
    public void lineReportWith1M2D() throws Exception {
        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/lineReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
    }

    @TestInfo(testCaseIds = {"GS-9036"})
    @Test
    public void areaReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/areaReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

    }

    @TestInfo(testCaseIds = {"GS-9037"})
    @Test
    public void stackedBarReportWith1M2D() throws Exception {
        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/stackedBarReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

    }

    @TestInfo(testCaseIds = {"GS-9038"})
    @Test
    public void stackedColumnReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/stackedColumnReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
    }

    @TestInfo(testCaseIds = {"GS-9039"})
    @Test
    public void columnLineReportWith1M2D() throws Exception {

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/columnLineReportWith1M2D.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

    }

    @AfterClass
    public void quit() {
        mongoUtil.closeConnection();
        mongoDBDAO.mongoUtil.closeConnection();
    }
}