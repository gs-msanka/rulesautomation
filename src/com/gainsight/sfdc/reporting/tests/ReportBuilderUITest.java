package com.gainsight.sfdc.reporting.tests;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBDetail;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBServerDetail;
import com.gainsight.sfdc.pojos.SObject;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.reporting.utils.ReportingUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SfdcRestApi;
import com.gainsight.testdriver.Application;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.gainsight.utils.annotations.TestInfo;

public class ReportBuilderUITest extends BaseTest {

	private ReportingBasePage reportingBasePage;
	private SfdcRestApi sfdcRestApi = new SfdcRestApi();
	private ObjectMapper mapper = new ObjectMapper();
	private MongoDBDAO mongoDBDAO = null;
	private MongoUtil mongoUtil;
	private NSTestBase nsTestBase = new NSTestBase();
	private TenantManager tenantManager = new TenantManager();
	private ReportingUtil reportingUtil = new ReportingUtil();
	private DBDetail dbDetail = null;
	private String[] dataBaseDetail = null;
	private String host = null;
	private String port = null;
	private String userName = null;
	private String passWord = null;
	private String reportingBuilderPageUrl;
	private static final String COLLECTION_MASTER = "collectionmaster";
	HashMap<String, String> hmap = new HashMap<String, String>();

	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
		nsTestBase.init();
		nsTestBase.tenantAutoProvision();
		reportingBuilderPageUrl = visualForcePageUrl + "ReportBuilder";
		reportingBasePage = new ReportingBasePage();
        //Modifying api names to display names
        List<SObject> soList = sfdcRestApi.getSfdcObjects();

        for (SObject sObject : soList) {
            hmap.put(sObject.getName(), sObject.getLabel());
        }
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
		mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
		
		tenantDetails = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());
		if (!tenantDetails.isRedshiftEnabled()) {
			Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
		}
	//	Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
		mongoUtil = new MongoUtil(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
		mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
		
		// Till now it is one time job. We are not loading the whole MDA data
		// for every test case.
		// But i have written the code so that in future if we need we can un
		// comment the code.
	/*	mongoDBDAO.deleteMongoDocumentFromCollectionMaster(tenantDetails.getTenantId(), COLLECTION_MASTER, "Auto_RedShift_MDADataJOIN2");
			reportManager = new ReportManager();
		dataLoadManager = new DataLoadManager(sfdcInfo, nsTestBase.getDataLoadAccessKey());

		dataTransForm = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJobRedShiftJoin2.json"),
				JobInfo.class);
		if (true) { // Locally to run multiple time, we can make it false
			CollectionInfo collectionInfo = mapper.readValue(
					new File(Application.basedir
							+ "/testdata/newstack/reporting/schema/RedShiftJoin2.json"),
					CollectionInfo.class);
			collectionInfo.getCollectionDetails().setCollectionName(
	//				collectionInfo.getCollectionDetails().getCollectionName() + "_" + date.getTime());
					collectionInfo.getCollectionDetails().getCollectionName());
			collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
			Assert.assertNotNull(collectionId);

			dataETL.execute(dataTransForm);
			String statusId = dataLoadManager.dataLoadManage(dataLoadManager.getDefaultDataLoadMetaData(collectionInfo),
					new File(Application.basedir + dataTransForm.getDateProcess().getOutputFile()));
			Assert.assertNotNull(statusId);
			dataLoadManager.waitForDataLoadJobComplete(statusId);
		}*/

	}

	@TestInfo(testCaseIds={"GS-9040"})
	@Test
	public void reportingUIWithBackedJson() throws Exception {

		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomation.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9030"})
	@Test
	public void barReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/barReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9031"})
	@Test
	public void pieReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/pieReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);
		
		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9032"})
	@Test
	public void columnReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/columnReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);
	}

	@TestInfo(testCaseIds={"GS-9033"})
	@Test
	public void bubbleReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/bubbleReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9034"})
	@Test
	public void scatterReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/scatterReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);
	}

	@TestInfo(testCaseIds={"GS-9035"})
	@Test
	public void lineReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/lineReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);
	}

	@TestInfo(testCaseIds={"GS-9036"})
	@Test
	public void areaReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/areaReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9037"})
	@Test
	public void stackedBarReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/stackedBarReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9038"})
	@Test
	public void stackedColumnReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/stackedColumnReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);
	}

	@TestInfo(testCaseIds={"GS-9039"})
	@Test
	public void columnLineReportWith1M2D() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/columnLineReportWith1M2D.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9041"})
	@Test
	public void reportUsingRedShiftCW() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShift.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9042"})
	@Test
	public void reportUsingRedShiftCM() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(
						Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCM.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9043"})
	@Test
	public void reportUsingRedShiftCQ() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(
						Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCQ.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9044"})
	@Test
	public void reportUsingRedShiftCY() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(
						Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomationRedShiftCY.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9045"})
	@Test
	public void dateTimeSummarizedByCW() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCW.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9046"})
	@Test
	public void dateTimeSummarizedByCM() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCM.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9047"})
	@Test
	public void dateTimeSummarizedByCQ() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCQ.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds={"GS-9048"})
	@Test
	public void dateTimeSummarizedByCY() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/data/DateTimeSummarizedByCY.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

	@TestInfo(testCaseIds = { "GS-9058" })
	@Test
	public void stringAggregation() throws Exception {
		mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
				tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

		ReportMaster reportMaster = mapper.readValue(
				new File(Application.basedir
						+ "/testdata/newstack/reporting/data/ReportingAggeration/MDAAggregation.json"),
				ReportMaster.class);
		reportingBasePage.openReportingPage(reportingBuilderPageUrl);

		reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

	}

    @TestInfo(testCaseIds = {"GS-100274"})
    @Test
    public void numberAggregation() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDAAggregationNumber.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100275"})
    @Test
    public void dateAggregation() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDAAggregationDate.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100276"})
    @Test
    public void dateTimeAggregation() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDAAggregationDateTime.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100277"})
    @Test
    public void booleanAggregation() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDAAggregationBoolean.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100278"})
    @Test
    public void relativeTimeFunctionsFilers() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/MDARelativeTimeFunctions.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100279"})
    @Test
    public void flatReportsWithShowMe() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/FlatReportsWithMaxShowMe.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100280"})
    @Test
    public void flatReportsWithRankingAndFilters() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir
                        + "/testdata/newstack/reporting/data/ReportingAggeration/FlatReportsWithFilterAndRanking.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100281"})
    @Test
    public void filtersOnNullForAllDataTypes() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/filtersOnNullForAllDataType.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100282"})
    @Test
    public void havingFiltersWithExpressions() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/HavingFiltersWithExpressions.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @TestInfo(testCaseIds = {"GS-100283"})
    @Test
    public void whereFiltersWithExpressions() throws Exception {
        mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
                tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

        ReportMaster reportMaster = mapper.readValue(
                new File(Application.basedir + "/testdata/newstack/reporting/data/WhereFiltersWithExpressions.json"),
                ReportMaster.class);
        reportingBasePage.openReportingPage(reportingBuilderPageUrl);

        reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil, null);

    }

    @AfterClass
    public void quit() {
        mongoUtil.closeConnection();
		mongoDBDAO.mongoUtil.closeConnection();
	}
}
