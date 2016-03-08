package com.gainsight.sfdc.customer360.test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.ObjectFields;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.administration.pages.AdminCustomer360Section;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.adoption.tests.AdoptionDataSetup;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.RelatedList360;
import com.gainsight.sfdc.customer360.pages.SalesforceRecordForm;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.reporting.utils.ReportingUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MetaDataUtil;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.MongoUtil;
import com.gainsight.utils.annotations.TestInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

public class RelatedListTest extends BaseTest {
    private final String TEST_DATA_FILE = "testdata/sfdc/relatedlist/tests/RelatedList_360.xls";
    private final String ACCOUNT_CREATE_FILE = Application.basedir + "/testdata/sfdc/relatedlist/scripts/Account_Create.txt";
    private final String CUSTOBJ_SFDC_REPORT_BUILDER = Application.basedir + "/testdata/sfdc/relatedlist/scripts/custobjsfdcReportbuilder.txt";
    private final String CONTACTOBJ_SFDC_REPORT_BUILDER = Application.basedir + "/testdata/sfdc/relatedlist/scripts/contactobjsfdcReportbuilder.txt";
    private final String CASEOBJ_SFDC_REPORT_BUILDER = Application.basedir + "/testdata/sfdc/relatedlist/scripts/caseobjsfdcReportbuilder.txt";
    private final String CUSTOBJ_RL_CS360Section_SFDC_CONFIGURATION = Application.basedir + "/testdata/sfdc/relatedlist/scripts/custobjcs360sfdcRelatedListReport.txt";
    private final String CONTACTOBJ_RL_CS360Section_SFDC_CONFIGURATION = Application.basedir + "/testdata/sfdc/relatedlist/scripts/contactobjcs360sfdcRelatedListReport.txt";
    private final String CASEOBJ_RL_CS360Section_SFDC_CONFIGURATION = Application.basedir + "/testdata/sfdc/relatedlist/scripts/caseobjcs360sfdcRelatedListReport.txt";
    private final String REPORTING_ACCOUNTS = Application.basedir + "/testdata/newstack/reporting/ReportingUI_Scripts/Create_Accounts_Customers_Reporting.txt";
    private ObjectMapper mapper = new ObjectMapper();
    private MongoDBDAO mongoDBDAO = null;
    private GSDataImpl gsDataImpl;
    private TenantManager tenantManager = new TenantManager();
    private ReportingUtil reportingUtil = new ReportingUtil();
    private DataETL dataETL = new DataETL();
    private MongoUtil mongoUtil;
    TenantDetails tenantDetails = null;
    HashMap<String, String> hmap = new HashMap<String, String>();
    Date date = Calendar.getInstance().getTime();
    private NSTestBase nsTestBase = new NSTestBase();
    private ReportingBasePage reportingBasePage;
    AdoptionDataSetup dataSetup;
    SalesforceMetadataClient customobj;
    MetaDataUtil metaDataUtil = new MetaDataUtil();
    ObjectFields objectFields = new ObjectFields();
    List<HashMap<String, String>> lookups = null;
    String collectionName;
    @BeforeClass
    public void setUp() throws Exception {
//        nsTestBase.init();
//        gsDataImpl = new GSDataImpl(NSTestBase.header);
        basepage.login();
        reportingBasePage = new ReportingBasePage();
//        tenantDetails = tenantManager.getTenantDetail(sfdcInfo.getOrg(), null);
//        TenantDetails.DBDetail schemaDBDetails = null;
//        mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()), nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
//        schemaDBDetails = mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
//        mongoUtil = new MongoUtil(schemaDBDetails.getDbServerDetails().get(0).getHost().split(":")[0], 27017, schemaDBDetails.getDbServerDetails().get(0).getUserName(), schemaDBDetails.getDbServerDetails().get(0).getPassword(), schemaDBDetails.getDbName());
        creatingCustomObject();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ACCOUNT_CREATE_FILE));
//        nsTestBase.tenantAutoProvision();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(REPORTING_ACCOUNTS));
        /*CollectionInfo collectionInfoMongo = mapper.readValue((new FileReader(Application.basedir + "/testdata/sfdc/relatedlist/scripts/ReportCollectionInfoUIAutomationMongo.json")), CollectionInfo.class);
        collectionInfoMongo.getCollectionDetails().setCollectionName("relatedList" + date);
        System.out.println("collectionName is " + collectionInfoMongo.getCollectionDetails().getCollectionName());
        collectionName = collectionInfoMongo.getCollectionDetails().getCollectionName();
        String collectionId = gsDataImpl.createCustomObject(collectionInfoMongo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfoMongo = gsDataImpl.getCollectionMaster(collectionId);
        dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/reporting/jobs/PreDataProcessJobMongo.json"), JobInfo.class));
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobMongo.json")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfoMongo, new String[]{"ID", "AccountName", "ProductCode", "ProductName", "Date", "EventTimeStamp", "Active", "PageViews", "PageVisits", "FilesDownloaded", "NoofReportsRun", "NoofRulesTriggered", "NoofSchedulesCreated", "Industry"}, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj2 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj2.isResult(), "Data is not loaded,please check log for more details");*/

    }

    @TestInfo(testCaseIds = {"GS-200266"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_1")
    public void cusobjSfdcDataVerification(HashMap<String, String> testData) throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CUSTOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CUSTOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String sfdcrelatedlistname = testData.get("Section");
        String tableheader = testData.get("TableHeader");
        HashMap<String, String> colHeaders = getMapFromData(tableheader);
        List<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String data = null;
        for (int i = 1; i <= 10; i++) {
            if (testData.get("TableRow" + i) != null) {
                data = testData.get("TableRow" + i);
                Log.info(data);
                dataList.add(getMapFromData(data));
            }
        }
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = c360Page.clickOnRelatedListSec(sfdcrelatedlistname);

        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for sfdc custom object in CS360");
        Assert.assertTrue(rLPage.isTableDataExisting(dataList, sfdcrelatedlistname), "table data is not matching for sfdc custom object in CS360");
        c360Page.refreshPage();
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for sfdc custom object in CS360");

    }

    @TestInfo(testCaseIds = {"GS-200267"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_3")
    public void custobjAddFunc(HashMap<String, String> testData) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CUSTOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CUSTOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String relatedListName = testData.get("Section");
        Customer360Page cPage = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        String cusobjrecordname = testData.get("RecordName");
        SalesforceRecordForm sal = rLPage.clickOnAdd(relatedListName);
        sal.recordName(cusobjrecordname);
        sal.clickOnSave();
        String tableheader = testData.get("TableHeader");
        HashMap<String, String> colHeaders = getMapFromData(tableheader);
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, relatedListName), "table header data is not matching for sfdc custom object in CS360");
        rLPage.closeWindow();

    }

    @TestInfo(testCaseIds = {"GS-200266"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_4")
    public void contactobjSfdcDataVerification(HashMap<String, String> testData) throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String sfdcrelatedlistname = testData.get("Section");
        String tableheader = testData.get("TableHeader");
        HashMap<String, String> colHeaders = getMapFromData(tableheader);
        List<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String data = null;
        for (int i = 1; i <= 10; i++) {
            if (testData.get("TableRow" + i) != null) {
                data = testData.get("TableRow" + i);
                Log.info(data);
                dataList.add(getMapFromData(data));
            }
        }
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = c360Page.clickOnRelatedListSec(sfdcrelatedlistname);
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for contact object in CS360");
        Assert.assertTrue(rLPage.isTableDataExisting(dataList, sfdcrelatedlistname), "table data is not matching for contact object in CS360");
        c360Page.refreshPage();
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for contact object in CS360");

    }

    @TestInfo(testCaseIds = {"GS-200267"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_6")
    public void stdContactAddFunc(HashMap<String, String> testData) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String relatedListName = testData.get("Section");
        Customer360Page cPage = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        SalesforceRecordForm sal = rLPage.clickOnAdd(relatedListName);
        Assert.assertTrue(sal.verifyRecordAddIsDisplayed(testData.get("ObjectId")));
        rLPage.closeWindow();

    }

    @TestInfo(testCaseIds = {"GS-200268"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_6")
    public void stdContactViewFunc(HashMap<String, String> testData) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String relatedListName = testData.get("Section");
        Customer360Page cPage = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        SalesforceRecordForm salesPage = rLPage.viewRecord(relatedListName, testData.get("Values"));
        Assert.assertTrue(salesPage.verifyRecordViewIsDisplayed(testData.get("ObjectId")), "Verifying the Page Url is contact record view or not");
        rLPage.closeWindow();
    }

    @TestInfo(testCaseIds = {"GS-200268"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_6")
    public void stdContactEditViewFunc(HashMap<String, String> testData) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String relatedListName = testData.get("Section");
        Customer360Page cPage = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        SalesforceRecordForm salesPage = rLPage.editRecord(relatedListName, testData.get("Values"));
        Assert.assertTrue(salesPage.verifyRecordEditViewIsDisplayed(testData.get("ObjectId")), "Verifying the Page Url is contact record view or not");
        rLPage.closeWindow();
    }

    @TestInfo(testCaseIds = {"GS-200141"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_5")
    public void caseobjSfdcDataVerification(HashMap<String, String> testData) throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ACCOUNT_CREATE_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(REPORTING_ACCOUNTS));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CASEOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CASEOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String sfdcrelatedlistname = testData.get("Section");
        String tableheader = testData.get("TableHeader");
        HashMap<String, String> colHeaders = getMapFromData(tableheader);
        List<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String data = null;
        for (int i = 1; i <= 10; i++) {
            if (testData.get("TableRow" + i) != null) {
                data = testData.get("TableRow" + i);
                Log.info(data);
                dataList.add(getMapFromData(data));
            }
        }
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = c360Page.clickOnRelatedListSec(sfdcrelatedlistname);
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for case object in CS360");
        Assert.assertTrue(rLPage.isTableDataExisting(dataList, sfdcrelatedlistname), "table data is not matching for case object in CS360");
        c360Page.refreshPage();
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for case object in CS360");

    }

    @TestInfo(testCaseIds = {"GS-200141", "GS-4817"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_7")
    public void caseobjAddFunc(HashMap<String, String> testData) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CASEOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CASEOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String relatedListName = testData.get("Section");
        Customer360Page cPage = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        String contactName = testData.get("ContactName");
        String contactOrigin = testData.get("ContactOrigin");
        SalesforceRecordForm sal = rLPage.clickOnAdd(relatedListName);
        sal.contactName(contactName);
        sal.contactCaseOrigin(contactOrigin);
        sal.clickOnCaseSave();
        rLPage.closeWindow();

    }

    @TestInfo(testCaseIds = {"GS-990"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_8")
    public void standObjNoDataInfoVerif(HashMap<String, String> testData) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_SFDC_REPORT_BUILDER));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACTOBJ_RL_CS360Section_SFDC_CONFIGURATION));
        String relatedListName = testData.get("Section");
        Customer360Page cPage = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        Assert.assertTrue(rLPage.isNoDataMsgDisplayed(relatedListName));
    }

    @TestInfo(testCaseIds = {"GS-200269"})
    @Test(enabled=false ,dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_2")
    public void cusobjMdaDataVerification(HashMap<String, String> testData) throws Exception {
        mongoDBDAO.deleteMongoDocumentFromReportMaster(tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster", "Auto_");
        ReportMaster reportMaster = mapper.readValue(new File(Application.basedir + "/testdata/newstack/reporting/data/RelatedListMDAData.json"), ReportMaster.class);
        reportingBasePage.openReportingPage(visualForcePageUrl + "ReportBuilder");
        reportMaster.getReportInfo().get(0).setReportReadLimit(tenantDetails.getReportReadLimit());
        reportMaster.getReportInfo().get(0).setSchemaName(collectionName);
        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
        String sfdcrelatedlistname = testData.get("Section");
        String sfdcrelatedlistname0 = testData.get("Section0");
        String objectname = testData.get("object");

        basepage.clickOnAdminTab();
        AdministrationBasePage adbasepage = new AdministrationBasePage();
        adbasepage.clickOnC360TabAdmin();
        AdminCustomer360Section cs360basepage = new AdminCustomer360Section();
        cs360basepage.addNewSectionCS360();
        cs360basepage.enableNewSectionForRelatedList(sfdcrelatedlistname, collectionName,objectname);
        String tableheader = testData.get("TableHeader");
        HashMap<String, String> colHeaders = getMapFromData(tableheader);
        List<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String data = null;
        for (int i = 1; i <= 10; i++) {
            if (testData.get("TableRow" + i) != null) {
                data = testData.get("TableRow" + i);
                Log.info(data);
                dataList.add(getMapFromData(data));
            }
        }
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = c360Page.clickOnExternalReportSec(sfdcrelatedlistname0, sfdcrelatedlistname);
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for mda custom object in CS360");
        Assert.assertTrue(rLPage.isTableDataExisting(dataList, sfdcrelatedlistname), "table data is not matching for mda custom object in CS360");
        c360Page.refreshPage();
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, sfdcrelatedlistname), "table header data is not matching for mda custom object in CS360");


    }

    public void creatingCustomObject() throws Exception {
        customobj = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        ObjectFields objField = new ObjectFields();
        // creating CustomObject
        metadataClient.createCustomObject("sfdcrelatedlist");

        List<String> numberFields = new ArrayList<String>();
        numberFields.add("ActiveUsers");
        numberFields.add("FNumber");
        objField.setNumberFields(numberFields);

        List<String> checkBoxes = new ArrayList<String>();
        checkBoxes.add("IsActive");
        objField.setCheckBoxes(checkBoxes);

        List<String> Percent = new ArrayList<String>();
        Percent.add("AccPercentage");
        objField.setPercents(Percent);

        List<String> Currency = new ArrayList<String>();
        Currency.add("CurrencyField");
        objField.setCurrencies(Currency);

        lookups = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> e = new HashMap<>();
        e.put("Name", "lookupfieldname");
        e.put("ReferenceTo", "Account");
        e.put("relationshipName", "relation_lookup_Test");
        lookups.add(e);
        objField.setLookups(lookups);
        metaDataUtil.createFieldsOnObject(sfdc, "sfdcrelatedlist__c", objField);
        String[] addFieldsPerm = new String[]{"ActiveUsers", "FNumber", "IsActive", "CurrencyField", "AccPercentage", "lookupfieldname"};
        metaUtil.addFieldPermissionsToUsers("sfdcrelatedlist__c", metaUtil.convertFieldNameToAPIName(addFieldsPerm), sfdcInfo, true);

    }

    /*@AfterClass
    public void quit() {
        mongoUtil.closeConnection();
        mongoDBDAO.mongoUtil.closeConnection();
    }*/

}
