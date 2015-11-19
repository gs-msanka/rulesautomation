package com.gainsight.sfdc.reporting.tests;

import java.io.File;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBDetail;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBServerDetail;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.reporting.utils.ReportingUtil;
import com.gainsight.sfdc.tests.BaseTest;
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
	private DBDetail dbDetail = null;
	private String[] dataBaseDetail = null;
	private String host = null;
	private String port = null;
	private String userName = null;
	private String passWord = null;

	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
		nsTestBase.init();
		MongoDBDAO mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
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

		// Till now it is one time job. We are not loading the whole MDA data
		// for every test case.
		// But i have written the code so that in future if we need we can un
		// comment the code.
		/*reportManager = new ReportManager();
		dataLoadManager = new DataLoadManager(sfdcInfo, nsTestBase.getDataLoadAccessKey());

		dataTransForm = mapper.readValue(
				new File(Application.basedir + "/testdata/newstack/reporting/jobs/DataProcessJob1.json"),
				JobInfo.class);
		if (true) { // Locally to run multiple time, we can make it false
			CollectionInfo collectionInfo = mapper.readValue(
					new File(Application.basedir
							+ "/testdata/newstack/reporting/schema/ReportCollectionInfoUIAutomation.json"),
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

	}

	@Test
	public void reportingUIWithBackedJson() throws Exception {
		try {

			mongoUtil = new MongoUtil(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
			mongoDBDAO = new MongoDBDAO(host, Integer.valueOf(port), userName, passWord, dbDetail.getDbName());
			mongoDBDAO.deleteCollectionSchemaFromCollectionMaster(
					tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), "reportmaster");

			ReportMaster reportMaster = mapper.readValue(
					new File(Application.basedir + "/testdata/newstack/reporting/data/ReportingUIAutomation.json"),
					ReportMaster.class);
			reportingBasePage = basepage.clickOnAdminTab().clickOnReportsTab();

			reportingUtil.createReportFromUiAndVerifyBackedJSON(reportMaster, reportingBasePage, mongoUtil);
		} finally {
			mongoUtil.closeConnection();
			mongoDBDAO.mongoUtil.closeConnection();
		}

	}
}
