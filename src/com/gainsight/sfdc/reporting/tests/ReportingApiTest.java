package com.gainsight.sfdc.reporting.tests;


import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.sfdc.reporting.Pojo.DashboardExportMaster;
import com.gainsight.sfdc.reporting.Pojo.SuccessSnapshotExportMaster;
import com.gainsight.sfdc.reporting.apiImpl.ReportingApiImpl;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JayaPrakash on 03/02/16.
 */
public class ReportingApiTest extends NSTestBase {
    ReportingApiImpl reportingApiImpl;
    private final String DASHBOARDMASTER_PATH = Application.basedir+"/src/com/gainsight/sfdc/reporting/mda/setup/DashboardExportMaster.json";
    private final String SUCCESSSNAPSHOTMASTER_PATH = Application.basedir+"/src/com/gainsight/sfdc/reporting/mda/setup/SuccessSnapshotExportMaster.json";

    @BeforeClass
    public void setUp() throws Exception {
        reportingApiImpl = new ReportingApiImpl(header);
    }

    @Test
    @TestInfo(testCaseIds = {"GS-200180"})
    public void exportSFDCDashboard() throws Exception{
        SObject[] dashboardID = sfdc.getRecords(resolveStrNameSpace("SELECT Id FROM JBCXM__Dashboard__c where name = '4ShowMe_1By_Layout' "));
        if ((dashboardID.length < 1)){
            throw new RuntimeException("No Dashboard found with name: 4ShowMe_1By_Layout , Please create the dashboard and try again");
        }
        Log.info("Dashboard ID to export is "+dashboardID[0].getId());
        Log.info(" User Email is "+sfdc.fetchSFDCinfo().getUserEmail());
        Log.info("User ID is "+sfdc.fetchSFDCinfo().getUserId());

        DashboardExportMaster dashboardExportMaster = mapper.readValue(new File(DASHBOARDMASTER_PATH), DashboardExportMaster.class);

        dashboardExportMaster.setDashboardId(dashboardID[0].getId());
        dashboardExportMaster.getExportTemplate().setUserId(sfdc.fetchSFDCinfo().getUserId());

        List<String> emailList = new ArrayList<String>();
        emailList.add(sfdc.fetchSFDCinfo().getUserEmail());
        dashboardExportMaster.getExportTemplate().setToEmailList(emailList);

        NsResponseObj nsResponseObj = reportingApiImpl.exportDashboardGetNsReponse(mapper.writeValueAsString(dashboardExportMaster));
        Assert.assertTrue(nsResponseObj.isResult(), "Export Dashboard for SFDC 4ShowMe_1By_Layout Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }

    @Test
    public void exportMongoDashboard() throws Exception{
        SObject[] dashboardID = sfdc.getRecords(resolveStrNameSpace("SELECT Id FROM JBCXM__Dashboard__c where name = 'MONGO_MDA_1ShowMe_1By_Layout' "));
        if ((dashboardID.length < 1)){
            throw new RuntimeException("No Dashboard found with name: MONGO_MDA_1ShowMe_1By_Layout , Please create the dashboard and try again");
        }
        Log.info("Dashboard ID to export is "+dashboardID[0].getId());
        Log.info(" User Email is "+sfdc.fetchSFDCinfo().getUserEmail());
        Log.info("User ID is "+sfdc.fetchSFDCinfo().getUserId());

        DashboardExportMaster dashboardExportMaster = mapper.readValue(new File(DASHBOARDMASTER_PATH), DashboardExportMaster.class);

        dashboardExportMaster.setDashboardId(dashboardID[0].getId());
        dashboardExportMaster.getExportTemplate().setUserId(sfdc.fetchSFDCinfo().getUserId());

        List<String> emailList = new ArrayList<String>();
        emailList.add(sfdc.fetchSFDCinfo().getUserEmail());
        dashboardExportMaster.getExportTemplate().setToEmailList(emailList);

        NsResponseObj nsResponseObj = reportingApiImpl.exportDashboardGetNsReponse(mapper.writeValueAsString(dashboardExportMaster));
        Assert.assertTrue(nsResponseObj.isResult(), "Export Dashboard for MONGO_MDA_1ShowMe_1By_Layout Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }

    @Test
    public void exportRedshiftDashboard() throws Exception{
        SObject[] dashboardID = sfdc.getRecords(resolveStrNameSpace("SELECT Id FROM JBCXM__Dashboard__c where name = 'REDSHIFT_MDA_1ShowMe_1By_Layout' "));
        if ((dashboardID.length < 1)){
            throw new RuntimeException("No Dashboard found with name: REDSHIFT_MDA_1ShowMe_1By_Layout , Please create the dashboard and try again");
        }
        Log.info("Dashboard ID to export is "+dashboardID[0].getId());
        Log.info(" User Email is "+sfdc.fetchSFDCinfo().getUserEmail());
        Log.info("User ID is "+sfdc.fetchSFDCinfo().getUserId());

        DashboardExportMaster dashboardExportMaster = mapper.readValue(new File(DASHBOARDMASTER_PATH), DashboardExportMaster.class);

        dashboardExportMaster.setDashboardId(dashboardID[0].getId());
        dashboardExportMaster.getExportTemplate().setUserId(sfdc.fetchSFDCinfo().getUserId());

        List<String> emailList = new ArrayList<String>();
        emailList.add(sfdc.fetchSFDCinfo().getUserEmail());
        dashboardExportMaster.getExportTemplate().setToEmailList(emailList);

        NsResponseObj nsResponseObj = reportingApiImpl.exportDashboardGetNsReponse(mapper.writeValueAsString(dashboardExportMaster));
        Assert.assertTrue(nsResponseObj.isResult(), "Export Dashboard for REDSHIFT_MDA_1ShowMe_1By_Layout Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-200195"})
    public void exportSFDCSuccessSnapshot() throws Exception{
        SObject[] AccountDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name FROM Account where JBCXM__CustomerInfo__c!=null"));
        SObject[] SSDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__Title__c,Name FROM JBCXM__Report__c where JBCXM__Title__c = 'SFDC SS Created through Automation' limit 1"));
        if ((SSDetails.length < 1)){
            throw new RuntimeException("No Success Snaphsot found with name: SFDC SS Created through Automation,Please create the Success Snapshot and try again");
        }
        Log.info("Account ID to export is "+AccountDetails[0].getId());
        Log.info("Account Name to export is "+AccountDetails[0].getSObjectField("Name").toString());
        Log.info("Success Snapshot ID to export "+SSDetails[0].getId());

        SuccessSnapshotExportMaster successSnapshotExportMaster = mapper.readValue(new File(SUCCESSSNAPSHOTMASTER_PATH), SuccessSnapshotExportMaster.class);
        if(sfdcConfig.getSfdcManagedPackage()){
            successSnapshotExportMaster.setNamespacePrefix("JBCXM");
        }
        successSnapshotExportMaster.setAccountId(AccountDetails[0].getId());
        successSnapshotExportMaster.setAccountName(AccountDetails[0].getSObjectField("Name").toString());
        NsResponseObj nsResponseObj = reportingApiImpl.exportSuccessSnapshotGetNsReponse(mapper.writeValueAsString(successSnapshotExportMaster),SSDetails[0].getId());
        Assert.assertTrue(nsResponseObj.isResult(), "Success Snapshot Export Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-200195"})
    public void exportSFDCColorsSuccessSnapshot() throws Exception{
        SObject[] AccountDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name FROM Account where JBCXM__CustomerInfo__c!=null"));
        SObject[] SSDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__Title__c,Name FROM JBCXM__Report__c where JBCXM__Title__c = 'SFDC Colors SS Created through Automation' limit 1"));
        if ((SSDetails.length < 1)){
            throw new RuntimeException("No Success Snaphsot found with name: SFDC Colors SS Created through Automation,Please create the Success Snapshot and try again");
        }
        Log.info("Account ID to export is "+AccountDetails[0].getId());
        Log.info("Account Name to export is "+AccountDetails[0].getSObjectField("Name").toString());
        Log.info("Success Snapshot ID to export "+SSDetails[0].getId());

        SuccessSnapshotExportMaster successSnapshotExportMaster = mapper.readValue(new File(SUCCESSSNAPSHOTMASTER_PATH), SuccessSnapshotExportMaster.class);
        if(sfdcConfig.getSfdcManagedPackage()){
            successSnapshotExportMaster.setNamespacePrefix("JBCXM");
        }
        successSnapshotExportMaster.setAccountId(AccountDetails[0].getId());
        successSnapshotExportMaster.setAccountName(AccountDetails[0].getSObjectField("Name").toString());
        NsResponseObj nsResponseObj = reportingApiImpl.exportSuccessSnapshotGetNsReponse(mapper.writeValueAsString(successSnapshotExportMaster),SSDetails[0].getId());
        Assert.assertTrue(nsResponseObj.isResult(), "Success Snapshot Export Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-200195"})
    public void exportSFDCSummarizedSuccessSnapshot() throws Exception{
        SObject[] AccountDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name FROM Account where JBCXM__CustomerInfo__c!=null"));
        SObject[] SSDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__Title__c,Name FROM JBCXM__Report__c where JBCXM__Title__c = 'SFDC Summarized by SS Created through Automation' limit 1"));
        if ((SSDetails.length < 1)){
            throw new RuntimeException("No Success Snaphsot found with name: SFDC Summarized by SS Created through Automation,Please create the Success Snapshot and try again");
        }
        Log.info("Account ID to export is "+AccountDetails[0].getId());
        Log.info("Account Name to export is "+AccountDetails[0].getSObjectField("Name").toString());
        Log.info("Success Snapshot ID to export "+SSDetails[0].getId());

        SuccessSnapshotExportMaster successSnapshotExportMaster = mapper.readValue(new File(SUCCESSSNAPSHOTMASTER_PATH), SuccessSnapshotExportMaster.class);
        if(sfdcConfig.getSfdcManagedPackage()){
            successSnapshotExportMaster.setNamespacePrefix("JBCXM");
        }
        successSnapshotExportMaster.setAccountId(AccountDetails[0].getId());
        successSnapshotExportMaster.setAccountName(AccountDetails[0].getSObjectField("Name").toString());
        NsResponseObj nsResponseObj = reportingApiImpl.exportSuccessSnapshotGetNsReponse(mapper.writeValueAsString(successSnapshotExportMaster),SSDetails[0].getId());
        Assert.assertTrue(nsResponseObj.isResult(), "Success Snapshot Export Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-200195"})
    public void exportMongoSuccessSnapshot() throws Exception{
        SObject[] AccountDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name FROM Account where JBCXM__CustomerInfo__c!=null"));
        SObject[] SSDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__Title__c,Name FROM JBCXM__Report__c where JBCXM__Title__c = 'Mongo SS Created through Automation' limit 1"));
        if ((SSDetails.length < 1)){
            throw new RuntimeException("No Success Snaphsot found with name: Mongo SS Created through Automation,Please create the Success Snapshot and try again");
        }
        Log.info("Account ID to export is "+AccountDetails[0].getId());
        Log.info("Account Name to export is "+AccountDetails[0].getSObjectField("Name").toString());
        Log.info("Success Snapshot ID to export "+SSDetails[0].getId());

        SuccessSnapshotExportMaster successSnapshotExportMaster = mapper.readValue(new File(SUCCESSSNAPSHOTMASTER_PATH), SuccessSnapshotExportMaster.class);
        if(sfdcConfig.getSfdcManagedPackage()){
            successSnapshotExportMaster.setNamespacePrefix("JBCXM");
        }
        successSnapshotExportMaster.setAccountId(AccountDetails[0].getId());
        successSnapshotExportMaster.setAccountName(AccountDetails[0].getSObjectField("Name").toString());
        NsResponseObj nsResponseObj = reportingApiImpl.exportSuccessSnapshotGetNsReponse(mapper.writeValueAsString(successSnapshotExportMaster),SSDetails[0].getId());
        Assert.assertTrue(nsResponseObj.isResult(), "Success Snapshot Export Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-200195"})
    public void exportRedshiftSuccessSnapshot() throws Exception{
        SObject[] AccountDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,Name FROM Account where JBCXM__CustomerInfo__c!=null"));
        SObject[] SSDetails = sfdc.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__Title__c,Name FROM JBCXM__Report__c where JBCXM__Title__c = 'Redshift SS Created through Automation' limit 1"));
        if ((SSDetails.length < 1)){
            throw new RuntimeException("No Success Snaphsot found with name: Redshift SS Created through Automation,Please create the Success Snapshot and try again");
        }
        Log.info("Account ID to export is "+AccountDetails[0].getId());
        Log.info("Account Name to export is "+AccountDetails[0].getSObjectField("Name").toString());
        Log.info("Success Snapshot ID to export "+SSDetails[0].getId());

        SuccessSnapshotExportMaster successSnapshotExportMaster = mapper.readValue(new File(SUCCESSSNAPSHOTMASTER_PATH), SuccessSnapshotExportMaster.class);
        if(sfdcConfig.getSfdcManagedPackage()){
            successSnapshotExportMaster.setNamespacePrefix("JBCXM");
        }
        successSnapshotExportMaster.setAccountId(AccountDetails[0].getId());
        successSnapshotExportMaster.setAccountName(AccountDetails[0].getSObjectField("Name").toString());
        NsResponseObj nsResponseObj = reportingApiImpl.exportSuccessSnapshotGetNsReponse(mapper.writeValueAsString(successSnapshotExportMaster),SSDetails[0].getId());
        Assert.assertTrue(nsResponseObj.isResult(), "Success Snapshot Export Failed");
        Assert.assertEquals(nsResponseObj.getData(),"Sending as attached email");
    }




}
