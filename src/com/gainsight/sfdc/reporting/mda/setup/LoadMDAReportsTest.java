package com.gainsight.sfdc.reporting.mda.setup;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.gainsight.utils.SqlUtil;
import com.sforce.soap.partner.sobject.SObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by JayaPrakash on 01/09/15.
 * This class will create MDA Report Masters, adds the Reports to Home Page and to CS 360 page
 *
 */

public class LoadMDAReportsTest extends NSTestBase {
    private ReportManager reportManager;
    private TenantDetails tenantDetails = null;
    private DataLoadManager dataLoadManager;
    private Date date = Calendar.getInstance().getTime();
    private MongoDBDAO mongoDBDAO = null;
    private MongoUtil mongoUtil;
    private DataETL dataETL = new DataETL();
    private TenantDetails.DBDetail dbDetail = null;
    private String[] dataBaseDetail = null;
    private String host = null;
    private String port = null;
    private String userName = null;
    private String passWord = null;
    String collectionId = "";
    JobInfo dataTransForm;
    private static final String COLLECTION_MASTER = "collectionmaster";


    private final String REPORTMASTERBASEPATH = Application.basedir+"/src/com/gainsight/sfdc/reporting/mda/setup";

    private final String CREATE_LAYOUT_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateLayout.txt";
    private final String CREATE_MDACONTAINER_SCRIPT = Application.basedir+"/src/com/gainsight/sfdc/reporting/mda/setup/CreateMDAContainer.txt";
    private final String CREATE_MDACS360SECTION_SCRIPT = Application.basedir+"/src/com/gainsight/sfdc/reporting/mda/setup/CreateMDACS360Section.txt";
    private final String MDADASHBOARDSCLEANUP  = Application.basedir+"/src/com/gainsight/sfdc/reporting/mda/setup/MDADashboardsCleanup.txt";

    private final String SCATTERCHART2M2D_PATH = REPORTMASTERBASEPATH+"/Charts/Scatter2M2D.json";
    private final String BUBBLECHART3M2D_PATH = REPORTMASTERBASEPATH+"/Charts/Bubble3M2D.json";
    private final String COLUMNCHART1M1D_PATH = REPORTMASTERBASEPATH+"/Charts/Column1M1D.json";
    private final String D3BUBBLECHART1M1D_PATH = REPORTMASTERBASEPATH+"/Charts/D3BUBBLE1M1D.json";
    private final String HEATMAPCHART1M2D_PATH = REPORTMASTERBASEPATH+"/Charts/HEATMAP1M2D.json";
    private final String LINECHART1M1D_PATH = REPORTMASTERBASEPATH+"/Charts/Line1M1D.json";

    private final String GRID2M2D_PATH = REPORTMASTERBASEPATH+"/AggTabularReports/Grid2M2D.json";
    private final String GRID3M2D_PATH = REPORTMASTERBASEPATH+"/AggTabularReports/Grid3M2D.json";
    private final String GRID1M1D_PATH = REPORTMASTERBASEPATH+"/AggTabularReports/Grid1M1D.json";
    private final String PIVOT1M4D_PATH = REPORTMASTERBASEPATH+"/AggTabularReports/Pivot1M4D.json";
    private final String PIVOT2M4D_PATH = REPORTMASTERBASEPATH+"/AggTabularReports/Pivot2M4D.json";

    private final String AREA_BYDATEFILTER_PATH = REPORTMASTERBASEPATH+"/Charts/Area_ByDateFilter.json";
    private final String GRID_ByCUSTOMDATEFILTER_PATH = REPORTMASTERBASEPATH+"/AggTabularReports/Grid_ByCustomeDateFilter.json";
    private final String LINE_ByCUSTOMDATEFILTER_PATH = REPORTMASTERBASEPATH+"/Charts/Line_ByCustomDateFilter.json";
    private final String STACKEDCOLUMN_BYDATEFILTER_PATH = REPORTMASTERBASEPATH+"/Charts/StackedColumn_ByDateFilter.json"; ;
    private final String GRID_BYDATEFILTER_PATH = REPORTMASTERBASEPATH+"/AggTabularReports/Grid_ByDateFilter.json";

    private final String LINE_SUMMARIZEDBYWEEK_PATH = REPORTMASTERBASEPATH+"/Charts/Line_SummarizedByWeek.json";
    private final String LINE_SUMMARIZEDBYMONTH_PATH = REPORTMASTERBASEPATH+"/Charts/Line_SummarizedByMonth.json";
    private final String LINE_SUMMARIZEDBYQUARTER_PATH = REPORTMASTERBASEPATH+"/Charts/Line_SummarizedByQuarter.json";
    private final String LINE_SUMMARIZEDBYYEAR_PATH = REPORTMASTERBASEPATH+"/Charts/Line_SummarizedByYear.json";

    private final String FLATREPORT1_PATH = REPORTMASTERBASEPATH+"/FlatReports/FlatReport1.json";
    private final String FLATREPORT2_PATH = REPORTMASTERBASEPATH+"/FlatReports/FlatReport2.json";
    private final String FLATREPORT3_PATH = REPORTMASTERBASEPATH+"/FlatReports/FlatReport3.json";


    private String collectionName;
    private String collectionID;
    private String reportName;
    private String reportID;
    SqlUtil sql;

    @BeforeSuite
    public void cleanup(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(MDADASHBOARDSCLEANUP)); //Cleaning the MDA Dashboards and MDA C360Sectons in the org
    }


    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws IOException, SQLException, ClassNotFoundException {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning...");
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        tenantDetails = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());

        mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
                nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());

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

        if(dbStoreType !=null && dbStoreType.equalsIgnoreCase("mongo")) {
            String dbcollName = mongoDBDAO.getDbCollectionName(tenantDetails.getTenantId(),"GIRI_GS_AUTOMONGO");
            String strName = dbcollName;
            String[] strArray = new String[] {strName};
            if(dbcollName!=null){
                mongoUtil.dropCollections(strArray);
                mongoDBDAO.deleteMongoDocumentFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER, "GIRI_GS_AUTOMONGO");
                mongoDBDAO.deleteMongoDocumentFromReportMaster(
                        tenantManager.getTenantDetail(sfinfo.getOrg(), null).getTenantId(), "reportmaster", "MONGO_");
            }
            if(tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
            }
        } else if(dbStoreType !=null && dbStoreType.equalsIgnoreCase("redshift")) {
            String dbcollName = mongoDBDAO.getDbCollectionName(tenantDetails.getTenantId(),"GIRI_GS_AUTOREDSHIFT");
            if(dbcollName!=null){
                sql=new SqlUtil(env.getProperty("ns_redshift_host"), env.getProperty("ns_redshift_dbName"), env.getProperty("ns_redshift_userName"), env.getProperty("ns_redshift_password"));
                sql.executeSqlStatement("DROP TABLE "+dbcollName); //provide the dbcollectionname
                sql.closeConnection();
                mongoDBDAO.deleteMongoDocumentFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER, "GIRI_GS_AUTOREDSHIFT");
                mongoDBDAO.deleteMongoDocumentFromReportMaster(
                        tenantManager.getTenantDetail(sfinfo.getOrg(), null).getTenantId(), "reportmaster", "REDSHIFT_");
            }
            if(!tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
            }
        }

        reportManager = new ReportManager();
        dataLoadManager= new DataLoadManager(sfinfo, getDataLoadAccessKey());
        //collectionId = "2425c925-5ea4-4893-83a5-3d44341bf8e7"; //Collection name - GIRI_GS_AUTO_1443507290837

        dataTransForm = mapper.readValue(new File(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJob1.json"), JobInfo.class);
        if(true) {      //Locally to run multiple time, we can make it false
            CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/reporting/schema/ReportCollectionInfo1.json"), CollectionInfo.class);
            collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + dbStoreType);

            collectionInfo.getCollectionDetails().setDataStoreType(dbStoreType);
            String DBSTORE = collectionInfo.getCollectionDetails().getDataStoreType();
            System.out.println("Data Store is "+DBSTORE);

            collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
            System.out.println("CollectionID : " + collectionId);
            Assert.assertNotNull(collectionId);

            dataETL.execute(dataTransForm);
            String statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), new File(Application.basedir + dataTransForm.getDateProcess().getOutputFile()));
            Assert.assertNotNull(statusId);
            dataLoadManager.waitForDataLoadJobComplete(statusId);
//            collectionInfo.getCollectionDetails().getDbCollectionName();

        }



    }

    /**
     * @param ReportMasterPath
     * @param ReportName
     * @throws Exception
     */
    public void createReportWithAnyCombo(String ReportMasterPath,String ReportName,String LayoutName,String CS360SectionName) throws Exception{
        ReportMaster reportMaster = mapper.readValue(new File(ReportMasterPath), ReportMaster.class);
        CollectionInfo collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        reportMaster = reportManager.getDBNamesPopulatedReportMaster(reportMaster, collectionInfo);
        reportMaster.getReportInfo().get(0).setSchemaName(collectionInfo.getCollectionDetails().getCollectionName());
        reportMaster.getReportInfo().get(0).setCollectionID(collectionInfo.getCollectionDetails().getCollectionId());
//        reportName = ReportName + "_" + date.getTime();
        reportName = ReportName;
        reportMaster.getReportInfo().get(0).setReportName(reportName);
        String reportId = reportManager.saveReport(mapper.writeValueAsString(reportMaster));
        Assert.assertNotNull(reportId);


        String ReportID = reportId;
        collectionName = collectionInfo.getCollectionDetails().getCollectionName();
        collectionID = collectionInfo.getCollectionDetails().getCollectionId();

        SObject[] HomeLayoutID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Dashboard__c WHERE NAME ='"+LayoutName +"'"));

        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_MDACONTAINER_SCRIPT).replaceAll("LayoutID", HomeLayoutID[0].getId()).replaceAll("viewID", ReportID));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_MDACS360SECTION_SCRIPT).replaceAll("CS360SectionName", CS360SectionName).replaceAll("CollnID", collectionID).replaceAll("CollnName", collectionName).replaceAll("ReptID", reportId).replaceAll("ReptName", reportName));
    }

    @Test
    @Parameters("dbStoreType")
    public void createReportsWith2M2D(@Optional String dbStoreType) throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_2ShowMe_2By_Layout"));

        createReportWithAnyCombo(SCATTERCHART2M2D_PATH, dbStoreType+"_" + "Scatter2M2D", dbStoreType + "_" + "MDA_2ShowMe_2By_Layout", dbStoreType + "MDA_2ShowMe_2By_CS360Section");
        createReportWithAnyCombo(GRID2M2D_PATH, dbStoreType+"_" +"Grid2M2D", dbStoreType+"_"+"MDA_2ShowMe_2By_Layout",dbStoreType+"_"+"MDA_2ShowMe_2By_CS360Section");


    }

    @Test
    @Parameters("dbStoreType")
    public void createReportsWith3M2D(@Optional String dbStoreType) throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_3ShowMe_2By_Layout"));

        createReportWithAnyCombo(BUBBLECHART3M2D_PATH, dbStoreType+"_" +"Bubble3M2D",dbStoreType+"_" +"MDA_3ShowMe_2By_Layout",dbStoreType+"_" +"MDA_3ShowMe_2By_CS360Section");
        createReportWithAnyCombo(GRID3M2D_PATH, dbStoreType+"_" +"Grid3M2D",dbStoreType+"_" +"MDA_3ShowMe_2By_Layout",dbStoreType+"_" +"MDA_3ShowMe_2By_CS360Section");
    }

    @Test
    @Parameters("dbStoreType")
    public void createReportsWith1M1D(@Optional String dbStoreType) throws Exception{
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_1ShowMe_1By_Layout"));

        createReportWithAnyCombo(D3BUBBLECHART1M1D_PATH, dbStoreType+"_" +"D3Bubble1M1D", dbStoreType+"_" +"MDA_1ShowMe_1By_Layout",dbStoreType+"_" +"MDA_1ShowMe_1By_CS360Section");
        createReportWithAnyCombo(COLUMNCHART1M1D_PATH, dbStoreType+"_" +"ColumnChart1M1D", dbStoreType+"_" +"MDA_1ShowMe_1By_Layout", dbStoreType+"_" +"MDA_1ShowMe_1By_CS360Section");
        createReportWithAnyCombo(LINECHART1M1D_PATH, dbStoreType+"_" +"LineChart1M1D", dbStoreType+"_" +"MDA_1ShowMe_1By_Layout", dbStoreType+"_" +"MDA_1ShowMe_1By_CS360Section");
        createReportWithAnyCombo(GRID1M1D_PATH,dbStoreType+"_" +"Grid1M1D",dbStoreType+"_" +"MDA_1ShowMe_1By_Layout",dbStoreType+"_" +"MDA_1ShowMe_1By_CS360Section");
    }

    @Test
    @Parameters("dbStoreType")
    public void createReportsWith1M2D(@Optional String dbStoreType) throws Exception{
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_1ShowMe_2By_Layout"));

        createReportWithAnyCombo(HEATMAPCHART1M2D_PATH,dbStoreType+"_" +"HeatMap1M2D",dbStoreType+"_" +"MDA_1ShowMe_2By_Layout",dbStoreType+"_" +"MDA_1ShowMe_2By_CS360Section");
    }

    @Test
    @Parameters("dbStoreType")
    public void createPivotReports(@Optional String dbStoreType) throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_Pivot_Layout"));

        createReportWithAnyCombo(PIVOT1M4D_PATH, dbStoreType+"_" +"PivotReport1M4D", dbStoreType+"_" +"MDA_Pivot_Layout", dbStoreType+"_" +"MDA_Pivot_CS360Section");
        createReportWithAnyCombo(PIVOT2M4D_PATH, dbStoreType+"_" +"PivotReport2M4D", dbStoreType+"_" +"MDA_Pivot_Layout",dbStoreType+"_" +"MDA_Pivot_CS360Section");
    }

    @Test
    @Parameters("dbStoreType")
    public void createReportsWithDateinBy(@Optional String dbStoreType) throws Exception{
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_DateinBy_Layout"));

        createReportWithAnyCombo(AREA_BYDATEFILTER_PATH, dbStoreType+"_" +"AREA_BYDATE_FILTER", dbStoreType+"_" +"MDA_DateinBy_Layout", dbStoreType+"_" +"MDA_DateinBy_CS360Section");
        createReportWithAnyCombo(GRID_ByCUSTOMDATEFILTER_PATH, dbStoreType+"_" +"GRID_BYDATE_CUSTOMFILTER", dbStoreType+"_" +"MDA_DateinBy_Layout", dbStoreType+"_" +"MDA_DateinBy_CS360Section");
        createReportWithAnyCombo(LINE_ByCUSTOMDATEFILTER_PATH, dbStoreType+"_" +"LINE_BYDATE_CUSTOMFILTER", dbStoreType+"_" +"MDA_DateinBy_Layout", dbStoreType+"_" +"MDA_DateinBy_CS360Section");
        createReportWithAnyCombo(STACKEDCOLUMN_BYDATEFILTER_PATH, dbStoreType+"_" +"STACKEDCOLUMN_BYDATE_FILTER", dbStoreType+"_" +"MDA_DateinBy_Layout", dbStoreType+"_" +"MDA_DateinBy_CS360Section");
        createReportWithAnyCombo(GRID_BYDATEFILTER_PATH, dbStoreType+"_" +"GRID_BYDATE_FILTER", dbStoreType+"_" +"MDA_DateinBy_Layout", dbStoreType+"_" +"MDA_DateinBy_CS360Section");
    }

    @Test
    @Parameters("dbStoreType")
    public void createFlatReports(@Optional String dbStoreType) throws Exception{
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_FlatReports_Layout"));

        createReportWithAnyCombo(FLATREPORT1_PATH, dbStoreType+"_" +"FlatReport1", dbStoreType+"_" +"MDA_FlatReports_Layout", dbStoreType+"_" +"MDA_FlatReports_CS360Section");
        createReportWithAnyCombo(FLATREPORT2_PATH, dbStoreType+"_" +"FlatReport2", dbStoreType+"_" +"MDA_FlatReports_Layout", dbStoreType+"_" +"MDA_FlatReports_CS360Section");
        createReportWithAnyCombo(FLATREPORT3_PATH, dbStoreType+"_" +"FlatReport3", dbStoreType+"_" +"MDA_FlatReports_Layout", dbStoreType+"_" +"MDA_FlatReports_CS360Section");
    }


    //This is applicable only for Redshift Collections - Need to add condition for Mongo and Postgres collections
    @Test
    @Parameters("dbStoreType")
    public void createSummarizedByReports(@Optional String dbStoreType) throws Exception{
        CollectionInfo collectionInfo = dataLoadManager.getCollectionInfo(collectionId);

         if (dbStoreType != null && dbStoreType.equalsIgnoreCase("redshift") && collectionInfo.getCollectionDetails().getDataStoreType().equalsIgnoreCase("redshift")) {
             sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", dbStoreType+"_" +"MDA_SummarizedBy_Layout"));

             createReportWithAnyCombo(LINE_SUMMARIZEDBYWEEK_PATH, dbStoreType+"_" +"Line_SummarizedByWeek", dbStoreType+"_" +"MDA_SummarizedBy_Layout", dbStoreType+"_" +"MDA_SummarizedBy_CS360Section");
             createReportWithAnyCombo(LINE_SUMMARIZEDBYMONTH_PATH, dbStoreType+"_" +"Line_SummarizedByMonth", dbStoreType+"_" +"MDA_SummarizedBy_Layout", dbStoreType+"_" +"MDA_SummarizedBy_CS360Section");
             createReportWithAnyCombo(LINE_SUMMARIZEDBYQUARTER_PATH, dbStoreType+"_" +"Line_SummarizedByQuarter", dbStoreType+"_" +"MDA_SummarizedBy_Layout", dbStoreType+"_" +"MDA_SummarizedBy_CS360Section");
             createReportWithAnyCombo(LINE_SUMMARIZEDBYYEAR_PATH, dbStoreType+"_" +"Line_SummarizedByYear", dbStoreType+"_" +"MDA_SummarizedBy_Layout", dbStoreType+"_" +"MDA_SummarizedBy_CS360Section");
            }
         }

    @AfterClass
    public void quit() {
        mongoUtil.closeConnection();
        mongoDBDAO.mongoUtil.closeConnection();
    }

}


