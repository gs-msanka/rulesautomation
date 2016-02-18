package com.gainsight.sfdc.reporting.sfdc.setup;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.sforce.soap.partner.sobject.SObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JayaPrakash on 08/02/16.
 */
public class LoadSuccessSnapshots extends NSTestBase {
    ReportManager reportManager = new ReportManager();
    private TenantDetails tenantDetails = null;
    private MongoDBDAO mongoDBDAO = null;
    private MongoUtil mongoUtil;
    private TenantManager tenantManager = new TenantManager();
    private TenantDetails.DBDetail dbDetail = null;
    private String[] dataBaseDetail = null;
    private String host = null;
    private String port = null;
    private String userName = null;
    private String passWord = null;
    String collectionId = "";
    private static final String COLLECTION_MASTER = "collectionmaster";

    private final String BASE_PATH = Application.basedir + "/testdata/sfdc/reporting/scripts/";
    private final String CLEAN_PATH = BASE_PATH + "SSInitScripts.txt";
    private final String ENABLE_SS_SCRIPT = BASE_PATH + "EnableSuccessSnapshot.txt";
    private final String REPORT_SS_SCRIPT = BASE_PATH + "CreateSSTitle_ReportObject.txt";
    private final String REPORTSECTION_SS_SCRIPT = BASE_PATH + "CreateSSTitle_ReportSectionObject.txt";
    private final String DASHBOARD_SS_SCRIPT = BASE_PATH + "CreateSS_DashboardObject.txt";
    private final String DASHBOARDCONTAINER_SS_SCRIPT = BASE_PATH + "CreateSS_DashboardContainerObject.txt";
    File tempFile;

    @BeforeClass
    public void loadInitSSScripts() throws Exception {
        Assert.assertTrue(sfdc.connect(), "Failed to connect SFDC");
        Log.info("Executing the Apex script to enable Success Snapshot in the Org...");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ENABLE_SS_SCRIPT)); //Enabling Success Snapshot in Application Settings
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_PATH)); //Cleaning Dashbooard and Dashboard Containers records

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

    }

    public void createSSWithTitle(String ssReportName) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(REPORT_SS_SCRIPT).replaceAll("ssName", ssReportName));
        createSSReportSections(ssReportName);
    }

    public void createSSReportSections(String ssReportName) {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(REPORTSECTION_SS_SCRIPT).replaceAll("ssName", ssReportName));
    }

    public void createSSDashboard(String ssName) {
        SObject[] ssID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Report__c WHERE JBCXM__Title__c = '" + ssName + "'"));
        if ((ssID.length < 1)) {
            throw new RuntimeException("No Success Snapshot with name: " + ssName);
        }
        sfdc.runApexCode(getNameSpaceResolvedFileContents(DASHBOARD_SS_SCRIPT).replaceAll("successSnapshotID", ssID[0].getId()));
    }

    public void createSFDCSSDashboardContainers(String ssName) throws Exception {
        SObject[] ssID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Report__c WHERE JBCXM__Title__c = '" + ssName + "'"));
        if ((ssID.length < 1)) {
            throw new RuntimeException("No Success Snapshot with name: " + ssName);
        }

        SObject[] sfdcReportIds = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__UIViews__c WHERE JBCXM__IsRB__c = true AND Name LIKE '%4_1%' "));
        if ((sfdcReportIds.length < 1)) {
            throw new RuntimeException("No SFDC Reports created");
        }
        tempFile = new File(BASE_PATH + "tempFileforDashboardContainer.txt");
        FileWriter fw = new FileWriter(tempFile);
        String fileContent = FileUtil.getFileContents(DASHBOARDCONTAINER_SS_SCRIPT);
//        System.out.println("Updated file:"+FileUtil.getFileContents(tempFile) );
        for (Integer j = 0; j < sfdcReportIds.length; j++) {
            fileContent = fileContent.replace("reportID" + j, sfdcReportIds[j].getId().toString());
        }
        fileContent = fileContent.replaceAll("successSnapshotID", ssID[0].getId());
        fileContent = fileContent.replaceAll("reportSourceType", "SFDC");
        fw.write(fileContent);
//        System.out.println("File name" +fileContent);
        sfdc.runApexCode(resolveStrNameSpace(fileContent));
    }

    public void createMONGOSDashboardContainers(String ssName) throws Exception {
        SObject[] ssID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Report__c WHERE JBCXM__Title__c = '" + ssName + "'"));
        if ((ssID.length < 1)) {
            throw new RuntimeException("No Sucess Snapshot with name: " + ssName);
        }


        List<String> mongoReportIds = new ArrayList<>();
        /*mongoReportIds.add(reportManager.getReportId("MONGO_FlatReport1",tenantDetails.getTenantId(),mongoUtil));
        mongoReportIds.add(reportManager.getReportId("MONGO_LINE_BYDATE_CUSTOMFILTER",tenantDetails.getTenantId(),mongoUtil));
        mongoReportIds.add(reportManager.getReportId("MONGO_Bubble3M2D",tenantDetails.getTenantId(),mongoUtil));
        mongoReportIds.add(reportManager.getReportId("MONGO_D3Bubble1M1D",tenantDetails.getTenantId(),mongoUtil));
        mongoReportIds.add(reportManager.getReportId("MONGO_HeatMap1M2D",tenantDetails.getTenantId(),mongoUtil));*/
        mongoReportIds.add("06f47b52-ec4f-4d14-a0d3-2b1fd64f08b8");
        mongoReportIds.add("7a281f5d-fabf-4f73-a931-dce43e67a2a8");
        mongoReportIds.add("b12f609b-bf6d-4e74-955c-b95d43a553f9");
        mongoReportIds.add("67b36c7c-1e91-407f-8f30-058d38912b5a");
        mongoReportIds.add("cf57302b-5386-4342-8780-6ab1e3bc1206");

        if ((mongoReportIds.size() < 1)) {
            throw new RuntimeException("No MONGO Reports created");
        }
        tempFile = new File(BASE_PATH + "tempFileforDashboardContainer.txt");
        FileWriter fw = new FileWriter(tempFile);
        String fileContent = FileUtil.getFileContents(DASHBOARDCONTAINER_SS_SCRIPT);
        for (Integer j = 0; j < mongoReportIds.size(); j++) {
            fileContent = fileContent.replace("reportID" + j, mongoReportIds.get(j).toString());
        }
        fileContent = fileContent.replaceAll("successSnapshotID", ssID[0].getId());
        fileContent = fileContent.replaceAll("reportSourceType", "MDA");
        fw.write(fileContent);
        sfdc.runApexCode(resolveStrNameSpace(fileContent));
    }


    @Test
    public void addSFDCSSfromApexScripts() throws Exception {
        createSSWithTitle("SFDC SS Created through Automation");
        createSSDashboard("SFDC SS Created through Automation");
//        createSFDCSSDashboardContainers("JP First SS from Apex");
        createSSDashboardContainers("SFDC SS Created through Automation", "SFDC", "SFDC","4_1");
    }

    @Test
    public void addSFDCSSColorsfromApexScripts() throws Exception {
        createSSWithTitle("SFDC Colors SS Created through Automation");
        createSSDashboard("SFDC Colors SS Created through Automation");
//        createSFDCSSDashboardContainers("JP First SS from Apex");
        createSSDashboardContainers("SFDC Colors SS Created through Automation", "SFDC", "SFDC","Colors");
    }

    @Test
    public void addSFDCSSSummarizedfromApexScripts() throws Exception {
        createSSWithTitle("SFDC Summarized by SS Created through Automation");
        createSSDashboard("SFDC Summarized by SS Created through Automation");
//        createSFDCSSDashboardContainers("JP First SS from Apex");
        createSSDashboardContainers("SFDC Summarized by SS Created through Automation", "SFDC", "SFDC","Summarized");
    }

    @Test
    public void addMongoSSfromApexScripts() throws Exception {
        createSSWithTitle("Mongo SS Created through Automation");
        createSSDashboard("Mongo SS Created through Automation");
//        createMONGOSDashboardContainers("JP First Mongo SS from Apex");
        createSSDashboardContainers("Mongo SS Created through Automation", "MDA", "MONGO",null);

        /*SourceObject = GIRI_GS_AUTOMONGO_1453894025534
        06f47b52-ec4f-4d14-a0d3-2b1fd64f08b8
        7a281f5d-fabf-4f73-a931-dce43e67a2a8
        b12f609b-bf6d-4e74-955c-b95d43a553f9
        67b36c7c-1e91-407f-8f30-058d38912b5a
        cf57302b-5386-4342-8780-6ab1e3bc1206*/

        //Connect to MongoDB
        //Get the report names which to be used
        //Fetch the report id based on the report name created from LoadMDAReports
        //Throw excecption if we dont have reports
        //BUild the List and call the Apex script
    }

    @Test
    public void addRedshiftSSfromApexScripts() throws Exception {
        createSSWithTitle("Redshift SS Created through Automation");
        createSSDashboard("Redshift SS Created through Automation");
        createSSDashboardContainers("Redshift SS Created through Automation", "MDA", "REDSHIFT",null);
        //Connect to MongoDB
        //Get the report names which to be used
        //Fetch the report id based on the report name created from LoadMDAReports
        //Handle the toggle between Mongo and Redshift
        //Throw excecption if we dont have reports
        //BUild the List and call the Apex script
//        GIRI_GS_AUTOREDSHIFT_1453893535002
    }


    public void createSSDashboardContainers(String ssName, String source, String dataSourceType,String queryLike) throws Exception {
        // ssName - Success Snapshot Name
        // source - SFDC/MDA
        // dataSourceType - SFDC/Mongo/Redshift
        SObject[] ssID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Report__c WHERE JBCXM__Title__c = '" + ssName + "'"));
        if ((ssID.length < 1)) {
            throw new RuntimeException("No Success Snapshot with name: " + ssName);
        }
        List<String> reportIds = new ArrayList<>();

        if (dataSourceType == "SFDC") {
            SObject[] uiViewsList = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__UIViews__c WHERE JBCXM__IsRB__c = true AND Name LIKE '%"+queryLike+"%' "));
            if ((uiViewsList.length < 1)) {
                throw new RuntimeException("No SFDC Reports created");
            }else {
                for(SObject sobj: uiViewsList){
                    reportIds.add(sobj.getId());
                }

            }
        } else if (dataSourceType == "MONGO") {
            reportIds.add(reportManager.getReportId("MONGO_FlatReport1",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("MONGO_LINE_BYDATE_CUSTOMFILTER",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("MONGO_Bubble3M2D",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("MONGO_D3Bubble1M1D",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("MONGO_HeatMap1M2D",tenantDetails.getTenantId(),mongoUtil));

            /*reportIds.add("06f47b52-ec4f-4d14-a0d3-2b1fd64f08b8");
            reportIds.add("7a281f5d-fabf-4f73-a931-dce43e67a2a8");
            reportIds.add("b12f609b-bf6d-4e74-955c-b95d43a553f9");
            reportIds.add("67b36c7c-1e91-407f-8f30-058d38912b5a");
            reportIds.add("cf57302b-5386-4342-8780-6ab1e3bc1206");*/
            if ((reportIds.size() < 1)) {
                throw new RuntimeException("No MONGO Reports created");
            }
        } else if (dataSourceType == "REDSHIFT") {
            reportIds.add(reportManager.getReportId("REDSHIFT_FlatReport1",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("REDSHIFT_LINE_BYDATE_CUSTOMFILTER",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("REDSHIFT_Bubble3M2D",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("REDSHIFT_D3Bubble1M1D",tenantDetails.getTenantId(),mongoUtil));
            reportIds.add(reportManager.getReportId("REDSHIFT_HeatMap1M2D",tenantDetails.getTenantId(),mongoUtil));
            /*reportIds.add("06f47b52-ec4f-4d14-a0d3-2b1fd64f08b8");
            reportIds.add("7a281f5d-fabf-4f73-a931-dce43e67a2a8");
            reportIds.add("b12f609b-bf6d-4e74-955c-b95d43a553f9");
            reportIds.add("67b36c7c-1e91-407f-8f30-058d38912b5a");
            reportIds.add("cf57302b-5386-4342-8780-6ab1e3bc1206");*/
            if ((reportIds.size() < 1)) {
                throw new RuntimeException("No REDSHIFT Reports created");
            }
        }

        tempFile = new File(BASE_PATH + "tempFileforDashboardContainer.txt");
        FileWriter fw = new FileWriter(tempFile);
        String fileContent = FileUtil.getFileContents(DASHBOARDCONTAINER_SS_SCRIPT);
        int j = 0;
        for (String reportId : reportIds) {
            fileContent = fileContent.replace("reportID" + j, reportId);
            j++;
        }
        fileContent = fileContent.replaceAll("successSnapshotID", ssID[0].getId());
        fileContent = fileContent.replaceAll("reportSourceType", source);
        fw.write(fileContent);
//        System.out.println("File name" +fileContent);
        sfdc.runApexCode(resolveStrNameSpace(fileContent));

        }



}
