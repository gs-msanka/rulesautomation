package com.gainsight.sfdc.reporting.tests;

import java.io.File;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfoSFDC;
import com.gainsight.sfdc.pojos.SObject;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.reporting.utils.ReportingUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SfdcRestApi;
import com.gainsight.testdriver.Application;
import com.gainsight.utils.annotations.TestInfo;

public class ReportBuilderSFDCUITest extends BaseTest {

    private ReportingBasePage reportingBasePage;
    private SfdcRestApi sfdcRestApi = new SfdcRestApi();
    private ObjectMapper mapper = new ObjectMapper();
    private NSTestBase nsTestBase = new NSTestBase();
    private ReportingUtil reportingUtil = new ReportingUtil();

    private String reportingBuilderPageUrl;
    HashMap<String, String> hmap = new HashMap<String, String>();

    @BeforeClass
    public void setup() throws Exception {
        sfdc.connect();
        basepage.login();
        nsTestBase.init();
        nsTestBase.tenantAutoProvision();
        reportingBuilderPageUrl = visualForcePageUrl + "ReportBuilder";
        reportingBasePage = new ReportingBasePage();
        sfdc.runApexCode("Delete [SELECT Id FROM JBCXM__UIViews__c where Name Like 'SFDC%'];");
        // Modifying api names to display names
        List<SObject> soList = sfdcRestApi.getSfdcObjects();

        for (SObject sObject : soList) {
            hmap.put(sObject.getName(), sObject.getLabel());
        }
    }

    @TestInfo(testCaseIds = {"GS-100293"})
    @Test
    public void testSFDCMethod() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcjson.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100284"})
    @Test
    public void sfdcJsonPie() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonPie.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100285"})
    @Test
    public void sfdcJsonBar() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonBar.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100286"})
    @Test
    public void sfdcJsonColumn() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonColumn.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100287"})
    @Test
    public void sfdcJsonBubble() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonBubble.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100288"})
    @Test
    public void sfdcJsonScatter() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonScatter.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100289"})
    @Test
    public void sfdcJsonStackedBar() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonStackedBar.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100290"})
    @Test
    public void sfdcJsonStackedColumn() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonStackedColumn.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100291"})
    @Test
    public void sfdcJsonLine() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonLine.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100292"})
    @Test
    public void sfdcJsonArea() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonArea.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @TestInfo(testCaseIds = {"GS-100294"})
    @Test
    public void sfdcJsonColumnLine() throws Exception {

        ReportInfoSFDC reportFilterSFDC = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons/sfdcJsonColumnLine.json"),
                ReportInfoSFDC.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSONSFDC(reportFilterSFDC, reportingBasePage, hmap);

        SfdcJsonPreparation sfdcJsonPreparation = new SfdcJsonPreparation();
        sfdcJsonPreparation.connectSFDCDBAndCompareJson(reportFilterSFDC);
    }

    @AfterClass
    public void quit() {

    }
}
