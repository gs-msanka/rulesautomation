package com.gainsight.bigdata.reportBuilder.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.*;
import com.gainsight.util.Comparator;
import org.codehaus.jackson.JsonParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Giribabu on 10/08/15.
 */
public class ReportBuilderTest extends NSTestBase {
    private ReportManager reportManager;
    private TenantDetails tenantDetails;
    private DataLoadManager dataLoadManager;
    private Date date = Calendar.getInstance().getTime();
    DataETL dataETL = new DataETL();
    String collectionId = "";
    JobInfo dataTransForm;

    @BeforeClass
    public void setup() throws IOException {
        //Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        //tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        reportManager = new ReportManager();
        dataLoadManager= new DataLoadManager(sfinfo, getDataLoadAccessKey());

        dataTransForm = mapper.readValue(new File(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJob1.json"), JobInfo.class);
        if(true) {      //Locally to run multiple time, we can make it false
            CollectionInfo collectionInfo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/reporting/schema/ReportCollectionInfo1.json"), CollectionInfo.class);
            collectionInfo.getCollectionDetails().setCollectionName(collectionInfo.getCollectionDetails().getCollectionName() + "_" + date.getTime());
            collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
            System.out.println("CollectionID : " + collectionId);
            Assert.assertNotNull(collectionId);

            dataETL.execute(dataTransForm);
            String statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo), new File(Application.basedir + dataTransForm.getDateProcess().getOutputFile()));
            Assert.assertNotNull(statusId);
            dataLoadManager.waitForDataLoadJobComplete(statusId);
        }
    }

    @Test
    public void createTabularReport() throws Exception {
        CollectionInfo collectionInfo = dataLoadManager.getCollectionInfo(collectionId);

        ReportMaster reportMaster = reportManager.createTabularReportMaster(collectionInfo, null);
        reportMaster.getReportInfo().get(0).setReportName(collectionInfo.getCollectionDetails().getCollectionName() + "_"+date.getTime());

        System.out.println("Report Name : " + reportMaster.getReportInfo().get(0).getReportName());

        String reportId = reportManager.saveReport(mapper.writeValueAsString(reportMaster));
        Assert.assertNotNull(reportId);

        List<Map<String, String>>reportData = reportManager.runReportLinksAndGetData(mapper.writeValueAsString(reportMaster));
        reportData = reportManager.getProcessedReportData(reportData, collectionInfo);

        String expectedDataFile =  dataTransForm.getDateProcess().getOutputFile();
        CSVReader expectedReader = new CSVReader(new FileReader(Application.basedir+"/"+expectedDataFile));
        List<Map<String, String>> expectedData = com.gainsight.util.Comparator.getParsedCsvData(expectedReader);
        Assert.assertEquals(expectedData.size(), reportData.size());

        expectedData = reportManager.populateDefaultBooleanValue(expectedData, collectionInfo);

        //Un-comment just in case to debug
        /*
            Log.debug("EEEE" +mapper.writeValueAsString(expectedData));
            Log.debug("AAAA" +mapper.writeValueAsString(reportData));
        */
        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, reportData);
        Assert.assertEquals(diffData.size(), 0);

    }


    @Test
    public void createReportWithDBNames() throws Exception {
        ReportMaster reportMaster = mapper.readValue(new File(Application.basedir+"/testdata/newstack/reporting/data/SampleReport.json"), ReportMaster.class);
        CollectionInfo collectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        reportMaster = reportManager.getDBNamesPopulatedReportMaster(reportMaster, collectionInfo);
        reportMaster.getReportInfo().get(0).setReportName("Babu" + date.getTime());
        String reportId = reportManager.saveReport(mapper.writeValueAsString(reportMaster));
        Assert.assertNotNull(reportId);

    }





}
