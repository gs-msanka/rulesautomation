package com.gainsight.sfdc.reporting.tests;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBDetail;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBServerDetail;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.reporting.utils.ReportingUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;

public class ReportBuilderUITest extends BaseTest {

	private ReportingBasePage reportingBasePage;
	private ObjectMapper mapper = new ObjectMapper();
	private MongoDBDAO mongoDBDAO = null;
	private MongoUtil mongoUtil;
	private NSTestBase nsTestBase = new NSTestBase();
	private TenantManager tenantManager = new TenantManager();
	private ReportingUtil reportingUtil = new ReportingUtil();
	private ReportManager reportManager;
	private DataLoadManager dataLoadManager;
	private Date date = Calendar.getInstance().getTime();
	DataETL dataETL = new DataETL();
	String collectionId = "";
	JobInfo dataTransForm;
	private DBDetail dbDetail = null;
	private String[] dataBaseDetail = null;
	private String host = null;
	private String port = null;
	private String userName = null;
	private String passWord = null;
	private String reportingBuilderPageUrl;

	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
		nsTestBase.init();
		reportingBuilderPageUrl = visualForcePageUrl + "ReportBuilder";
		reportingBasePage = new ReportingBasePage();
		mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
				nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
		TenantDetails tenantDetails = tenantManager.getTenantDetail(sfdcInfo.getOrg(), null);

		dbDetail = mongoDBDAO.getDataDBDetail(tenantDetails.getTenantId());
		List<DBServerDetail> dbDetails = dbDetail.getDbServerDetails();
		for (DBServerDetail dbServerDetail : dbDetails) {
			dataBaseDetail = dbServerDetail.getHost().split(":");
			host = dataBaseDetail[0];
			port = dataBaseDetail[1];
			userName = dbServerDetail.getUserName();
			passWord = dbServerDetail.getPassword();
		}

		tenantDetails = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());
		if (!tenantDetails.isRedshiftEnabled()) {
			Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
		}

		// Till now it is one time job. We are not loading the whole MDA data
		// for every test case.
		// But i have written the code so that in future if we need we can un
		// comment the code.
		/*reportManager = new ReportManager();
		dataLoadManager = new DataLoadManager(sfdcInfo, nsTestBase.getDataLoadAccessKey());

		dataTransForm = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobRedShift.json"),
				JobInfo.class);
		if (true) { // Locally to run multiple time, we can make it false
			CollectionInfo collectionInfo = mapper.readValue(
					new File(Application.basedir
							+ "/testdata/newstack/reporting/schema/ReportCollectionInfoUIAutomationRedShift.json"),
					CollectionInfo.class);
			collectionInfo.getCollectionDetails().setCollectionName(
					collectionInfo.getCollectionDetails().getCollectionName() + "_" + date.getTime());
			collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
			Assert.assertNotNull(collectionId);

			dataETL.execute(dataTransForm);
			String statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo),
					new File(Application.basedir + dataTransForm.getDateProcess().getOutputFile()));
			Assert.assertNotNull(statusId);
			dataLoadManager.waitForDataLoadJobComplete(statusId);
		}*/

		mongoUtil = new MongoUtil(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
		mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());

	}

	@Test
	public void reportingUIWithBackedJson() throws Exception {

		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomation.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void barReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/barReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void pieReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/pieReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);
		
		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void columnReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/columnReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
	}

	@Test
	public void bubbleReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/bubbleReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void scatterReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/scatterReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
	}

	@Test
	public void lineReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/lineReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
	}

	@Test
	public void areaReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/areaReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void stackedBarReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/stackedBarReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void stackedColumnReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/stackedColumnReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
	}

	@Test
	public void columnLineReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/columnLineReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void reportUsingRedShiftCW() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShift.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void reportUsingRedShiftCM() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(
						Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCM.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void reportUsingRedShiftCQ() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(
						Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCQ.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void reportUsingRedShiftCY() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(
						Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCY.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void dateTimeSummarizedByCW() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCW.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void dateTimeSummarizedByCM() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCM.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void dateTimeSummarizedByCQ() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCQ.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void dateTimeSummarizedByCY() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCY.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	@Test
	public void stringAggregation() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir
						+ "/testdata/newstack/reporting/data/ReportingAggeration/MDAAggregation.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}

	/*@Test
	public void numberAggregation() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir
						+ "/testdata/newstack/reporting/data/ReportingAggeration/MDAAggregationNumber.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);

	}*/

	@AfterClass
	public void quit() {
		mongoUtil.closeConnection();
		mongoDBDAO.mongoUtil.closeConnection();
	}
}
