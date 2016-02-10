package com.gainsight.sfdc.reporting.mda.setup;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.reporting.tests.MDAConnectBackend;
import com.gainsight.testdriver.Application;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.sforce.soap.partner.sobject.SObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Created by Gainsight on 2/4/2016.
 */
public class CreateLayoutAndContainersForSFDCAndMda extends NSTestBase {
    private MongoDBDAO mongoDBDAO = null;
    private MongoUtil mongoUtil;
    private TenantManager tenantManager = new TenantManager();
    private TenantDetails.DBDetail dbDetail = null;
    private String[] dataBaseDetail = null;
    private String host = null;
    private String userName = null;
    private String passWord = null;
    TenantDetails tenantDetails = null;

    private final String REPORTINGDATABASEPATH = Application.basedir + "/testdata/newstack/reporting/data";
    private final String SFDCREPORTINGDATABASEPATH = Application.basedir + "/testdata/newstack/reporting/data/SfdcJsons";

    private final String CREATE_LAYOUT_SCRIPT = Application.basedir + "/testdata/sfdc/reporting/scripts/CreateLayout.txt";
    private final String CREATE_MDACONTAINER_SCRIPT = Application.basedir + "/src/com/gainsight/sfdc/reporting/mda/setup/CreateMDAContainer.txt";

    private final String GRIDREPORT = REPORTINGDATABASEPATH + "/MongoReportWithGrid.json";
    private final String BARREPORT = REPORTINGDATABASEPATH + "/MongoReportWithBar.json";
    private final String PIEREPORT = REPORTINGDATABASEPATH + "/MongoReportWithPie.json";
    private final String COLUMNREPORT = REPORTINGDATABASEPATH + "/columnReportWith1M2D.json";
    private final String BUBBLEREPORT = REPORTINGDATABASEPATH + "/MongoReportWithBubble.json";
    private final String SCATTERREPORT = REPORTINGDATABASEPATH + "/scatterReportForMongoData.json";
    private final String LINEREPORT = REPORTINGDATABASEPATH + "/lineReportForMongoData.json";
    private final String AREAREPORT = REPORTINGDATABASEPATH + "/areaReportForMongoData.json";
    private final String STACKEBARDREPORT = REPORTINGDATABASEPATH + "/stackedBarReportForMongoData.json";
    private final String STACKEDCOLUMNREPORT = REPORTINGDATABASEPATH + "/stackedColumnReportWith1M2D.json";
    private final String COLUMNLINEREPORT = REPORTINGDATABASEPATH + "/columnLineReportForMDAData.json";
    private final String CALCULATEDREPORT = REPORTINGDATABASEPATH + "/ReportingCalculatedMeasures/MDAMongoCalculatedMeasures.json";
    private final String RELATIVETIMEREPORT = REPORTINGDATABASEPATH + "/ReportingMDAMongo/MDARelativeTimeFunctions.json";
    private final String SHOWMEREPORT = REPORTINGDATABASEPATH + "/ReportingMDAMongo/FlatReportsWithMaxShowMe.json";
    private final String NULLREPORT = REPORTINGDATABASEPATH + "/ReportingMDAMongo/filtersOnNullForAllDataType.json";
    private final String HAVINGREPORT = REPORTINGDATABASEPATH + "/ReportingMDAMongo/HavingFiltersWithExpressions.json";
    private final String WHEREMGREPORT = REPORTINGDATABASEPATH + "/ReportingMDAMongo/WhereFiltersWithExpressions.json";
    private final String FLATWITHRFMG = REPORTINGDATABASEPATH + "/ReportingMDAMongo/FlatReportsWithFilterAndRankingMDAMongo.json";
    private final String BEAGGMGREPORT = REPORTINGDATABASEPATH + "/ReportingMDAMongo/MDAAggregationBoolean.json";
    private final String DTAGGMGREPORT = REPORTINGDATABASEPATH + "/ReportingMDAMongo/MDAAggregationDateTime.json";

    private final String CWRSREPORT = REPORTINGDATABASEPATH + "/SummarizedByWeekRedshift.json";
    private final String CMRSREPORT = REPORTINGDATABASEPATH + "/ReportingUIAutomationRedShiftCM.json";
    private final String CQRSREPORT = REPORTINGDATABASEPATH + "/ReportingUIAutomationRedShiftCQ.json";
    private final String CYRSREPORT = REPORTINGDATABASEPATH + "/ReportingUIAutomationRedShiftCY.json";
    private final String DTCWRSREPORT = REPORTINGDATABASEPATH + "/DateTimeSummarizedByCW.json";
    private final String DTCMRSREPORT = REPORTINGDATABASEPATH + "/DateTimeSummarizedByCM.json";
    private final String DTCQRSREPORT = REPORTINGDATABASEPATH + "/DateTimeSummarizedByCQ.json";
    private final String DTCYRSREPORT = REPORTINGDATABASEPATH + "/DateTimeSummarizedByCY.json";
    private final String STRINGRS = REPORTINGDATABASEPATH + "/ReportingAggeration/MDARedShiftAggregationString.json";
    private final String NMRSREPORT = REPORTINGDATABASEPATH + "/ReportingAggeration/MDARedShiftAggregationNumber.json";
    private final String DRSAGGREPORT = REPORTINGDATABASEPATH + "/ReportingAggeration/MDARedShiftAggregationDate.json";
    private final String DTRSAGGREPORT = REPORTINGDATABASEPATH + "/ReportingAggeration/MDARedShiftAggregationDateTime.json";
    private final String BERSAGGREPORT = REPORTINGDATABASEPATH + "/ReportingAggeration/MDARedShiftAggregationBoolean.json";
    private final String RTRSREPORT = REPORTINGDATABASEPATH + "/ReportingAggeration/MDARedShiftRelativeTimeFunctions.json";
    private final String FLATRSREPORT = REPORTINGDATABASEPATH + "/ReportingAggeration/FlatMDARedshiftReportsWithMaxShowMe.json";
    private final String FLATWITHRF = REPORTINGDATABASEPATH + "/ReportingAggeration/FlatReportsWithFilterAndRanking.json";
    private final String NULLRSREPORT = REPORTINGDATABASEPATH + "/filtersOnNullRedShiftForAllDataType.json";
    private final String HAVINGRSREPORT = REPORTINGDATABASEPATH + "/HavingFiltersWithExpressions.json";
    private final String WHERERSREPORT = REPORTINGDATABASEPATH + "/WhereFiltersWithExpressions.json";
    private final String MDAJOINSRSREPORT = REPORTINGDATABASEPATH + "/ReportingMDAJoins/reportsWithMDAJoins.json";

    private final String SFDCGRIDREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcjson.json";
    private final String SFDCBARREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonBar.json";
    private final String SFDCPIEREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonPie.json";
    private final String SFDCCOLUMNREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonColumn.json";
    private final String SFDCBUBBLEREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonBubble.json";
    private final String SFDCSCATTERREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonScatter.json";
    private final String SFDCLINEREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonLine.json";
    private final String SFDCAREAREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonArea.json";
    private final String SFDCSTACKEBARDREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonStackedBar.json";
    private final String SFDCSTACKEDCOLUMNREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonStackedColumn.json";
    private final String SFDCCOLUMNLINEREPORT = SFDCREPORTINGDATABASEPATH + "/sfdcJsonColumnLine.json";

    private String[] mongoReports = new String[20];
    private String[] redshiftReports = new String[20];
    private String[] sfdcReports = new String[11];

    MDAConnectBackend mdaConnectBackend = new MDAConnectBackend();

    @BeforeClass
    public void setup() throws Exception {
        NSTestBase nsTestBase = new NSTestBase();
        nsTestBase.init();
        mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
                nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);

        dbDetail = mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
        List<TenantDetails.DBServerDetail> dbDetails = dbDetail.getDbServerDetails();
        for (TenantDetails.DBServerDetail dbServerDetail : dbDetails) {
            dataBaseDetail = dbServerDetail.getHost().split(":");
            host = dataBaseDetail[0];
            userName = dbServerDetail.getUserName();
            passWord = dbServerDetail.getPassword();
        }

        mongoUtil = new MongoUtil(host, 27017, userName, passWord, dbDetail.getDbName());
        mongoReports[0] = GRIDREPORT;
        mongoReports[1] = BARREPORT;
        mongoReports[2] = PIEREPORT;
        mongoReports[3] = COLUMNREPORT;
        mongoReports[4] = BUBBLEREPORT;
        mongoReports[5] = SCATTERREPORT;
        mongoReports[6] = LINEREPORT;
        mongoReports[7] = AREAREPORT;
        mongoReports[8] = STACKEBARDREPORT;
        mongoReports[9] = STACKEDCOLUMNREPORT;
        mongoReports[10] = COLUMNLINEREPORT;
        mongoReports[11] = CALCULATEDREPORT;
        mongoReports[12] = RELATIVETIMEREPORT;
        mongoReports[13] = SHOWMEREPORT;
        mongoReports[14] = NULLREPORT;
        mongoReports[15] = HAVINGREPORT;
        mongoReports[16] = WHEREMGREPORT;
        mongoReports[17] = FLATWITHRFMG;
        mongoReports[18] = BEAGGMGREPORT;
        mongoReports[19] = DTAGGMGREPORT;

        redshiftReports[0] = CWRSREPORT;
        redshiftReports[1] = CMRSREPORT;
        redshiftReports[2] = CQRSREPORT;
        redshiftReports[3] = CYRSREPORT;
        redshiftReports[4] = DTCWRSREPORT;
        redshiftReports[5] = DTCMRSREPORT;
        redshiftReports[6] = DTCQRSREPORT;
        redshiftReports[7] = DTCYRSREPORT;
        redshiftReports[8] = STRINGRS;
        redshiftReports[9] = NMRSREPORT;
        redshiftReports[10] = DRSAGGREPORT;
        redshiftReports[11] = DTRSAGGREPORT;
        redshiftReports[12] = BERSAGGREPORT;
        redshiftReports[13] = RTRSREPORT;
        redshiftReports[14] = FLATRSREPORT;
        redshiftReports[15] = FLATWITHRF;
        redshiftReports[16] = NULLRSREPORT;
        redshiftReports[17] = HAVINGRSREPORT;
        redshiftReports[18] = WHERERSREPORT;
        redshiftReports[19] = MDAJOINSRSREPORT;

        sfdcReports[0] = SFDCGRIDREPORT;
        sfdcReports[1] = SFDCBARREPORT;
        sfdcReports[2] = SFDCPIEREPORT;
        sfdcReports[3] = SFDCCOLUMNREPORT;
        sfdcReports[4] = SFDCBUBBLEREPORT;
        sfdcReports[5] = SFDCSCATTERREPORT;
        sfdcReports[6] = SFDCLINEREPORT;
        sfdcReports[7] = SFDCAREAREPORT;
        sfdcReports[8] = SFDCSTACKEBARDREPORT;
        sfdcReports[9] = SFDCSTACKEDCOLUMNREPORT;
        sfdcReports[10] = SFDCCOLUMNLINEREPORT;
    }

    public void createReportWithAnyCombo(String ReportName, String LayoutName) throws Exception {

        String reportId = mdaConnectBackend.getReportId(tenantDetails.getTenantId(), ReportName, mongoUtil);
        String ReportID = reportId;


        SObject[] HomeLayoutID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Dashboard__c WHERE NAME ='" + LayoutName + "'"));

        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_MDACONTAINER_SCRIPT).replaceAll("LayoutID", HomeLayoutID[0].getId()).replaceAll("viewID", ReportID));
    }

    @Test
    public void creatLayoutForMongo() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "MongoLayout"));

        for (String reportPath : mongoReports) {
            ReportMaster reportMaster = mapper.readValue(
                    new File(reportPath),
                    ReportMaster.class);

            createReportWithAnyCombo(reportMaster.getReportInfo().get(0).getReportName(), "MongoLayout");
        }

    }

    @Test
    public void creatLayoutForRedShift() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "RedShiftLayout"));


        for (String reportPath : redshiftReports) {
            ReportMaster reportMaster = mapper.readValue(
                    new File(reportPath),
                    ReportMaster.class);

            createReportWithAnyCombo(reportMaster.getReportInfo().get(0).getReportName(), "RedShiftLayout");
        }

    }

    @Test
    public void creatLayoutForSfdc() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "SFDCLayout"));


        for (String reportPath : sfdcReports) {
            ReportMaster reportMaster = mapper.readValue(
                    new File(reportPath),
                    ReportMaster.class);

            createReportWithAnyCombo(reportMaster.getReportInfo().get(0).getReportName(), "SFDCLayout");
        }

    }
}
