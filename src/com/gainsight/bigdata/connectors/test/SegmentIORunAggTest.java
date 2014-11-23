package com.gainsight.bigdata.connectors.test;

import java.util.HashMap;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.connectors.AccountDetails;
import com.gainsight.bigdata.connectors.ResponseValidator;
import com.gainsight.bigdata.connectors.SegmentIOTestData;
import com.gainsight.bigdata.connectors.TestDataUtils;
import com.gainsight.bigdata.connectors.pojo.AccountProperties;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.ExcelDataProvider;

public class SegmentIORunAggTest extends TestBase {

	final String TEST_DATA_FILE = "testdata/newstack/connectors/SIOExpectedOutput.xls";
	String dataSyncUrl = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC;
	String syncStatusUrl = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC_STATUS;
	String distinctEventsByAcc_UT_Url = PropertyReader.nsAppUrl + ApiUrl.UT_DISTINCT_EVENTS_BY_ACC;
	String usageTrackerGetDataUrl = PropertyReader.nsAppUrl + ApiUrl.UT_GET_DATA;
	String reportRunUrl = PropertyReader.nsAppUrl + ApiUrl.REPORT_RUN;
	String accDeleteUrl = PropertyReader.nsAppUrl + ApiUrl.ACC_DELETE;
	String ACCOUNT_ID = "001o000000E4KKRAA3";

	String ACC_USER_EVENT_DATE_AGG_RES = "ACC_USER_EVENT_DATE_AGG_RES";
	TestDataUtils testDataUtils = new TestDataUtils();
	HashMap<String, String> testData;
	String AccountDetailsID;
	String reportRequest;

	ObjectMapper mapper = new ObjectMapper();
	ResponseValidator responseValidator = new ResponseValidator();

	@Parameters("version")
	@BeforeClass
	public void setUp() throws Exception {
		init();
		testData = ExcelDataProvider.getDataFromExcel(TEST_DATA_FILE, "Sheet1").get(0);
		// new TestDataUtils().loadTestData();
		h.addHeader("actionType", "SAVE_AND_RUN");
		dataSyncUrl = ApiUrl.setURLParam(dataSyncUrl, "new");

	}

	@BeforeTest
	public void beforeTest() {
		Report.logInfo("--- START SEGMENT AGGREGATION TEST ---");
	}

	@AfterTest
	public void afterTest() {
		Report.logInfo("--- END SEGMENT AGGREGATION TEST ---");
	}

	@AfterTest
	public void cleanProject() throws Exception {
		if (AccountDetailsID != null) {
			String url = ApiUrl.setURLParam(accDeleteUrl, AccountDetailsID);
			HttpResponseObj result = wa.doDelete(url, h.getAllHeaders());
			if (result.getStatusCode() == 200) {
				Report.logInfo("Account:" + AccountDetailsID + " Deleted successfully");
			} else {
				Report.logInfo("Account:" + AccountDetailsID + " Deleted Failed");
			}
			AccountDetailsID = null;
		}
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUED_DL(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_DL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, h, ACCOUNT_ID, testData);
		reportRequest = data.getFlippedColReportRequest(properties.getFlippedColleciton());
		expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUED_IDL(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_IDL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, h, ACCOUNT_ID, testData);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet2")
	public void testAUED_DL_SIOUserNameAndEmail(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_DL_SIOUserNameAndEmail();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		Report.logInfo("Validating Day Aggregation Subject Area");
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
		Report.logInfo("Validating Usage Tracker And Summary");
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, h, ACCOUNT_ID, testData);
		Report.logInfo("--------- DONE VALIDATION OF ALL SECTIONS ----------");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet2")
	public void testAUED_IDL_SIOUserNameAndEmail(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_IDL_SIOUserNameAndEmail();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		Report.logInfo("Validating Day Aggregation Subject Area");
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
		Report.logInfo("Validating Usage Tracker And Summary");
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, h, ACCOUNT_ID, testData);
		Report.logInfo("--------- DONE VALIDATION OF ALL SECTIONS ----------");
	}

	public void runAggregation(String url, Header h, AccountDetails info) throws Exception {
		int count = 0;
		String rawBody = mapper.writeValueAsString(info);
		HttpResponseObj result = wa.doPut(url, rawBody, h.getAllHeaders());
		Report.logInfo("AGG Request::PUT:" + url + "\n Payload:" + rawBody + "\n Response:" + result.toString());
		JsonNode resContent = mapper.readTree(result.getContent());
		String statusID = resContent.get("data").get("statusId").toString();
		Assert.assertNotNull(statusID, "********AGGREGATION REQUEST FAILED*********");
		syncStatusUrl = ApiUrl.setURLParam(syncStatusUrl, statusID.replace("\"", ""));
		result = wa.doGet(syncStatusUrl, h.getAllHeaders());
		resContent = mapper.readTree(result.getContent());
		while (resContent.get("data") == null
				|| !resContent.get("data").get("status").toString().equalsIgnoreCase("\"COMPLETED\"")) {
			Thread.sleep(3000);
			if (count++ >= 20) {
				Assert.fail("Unable to get the Aggregation Request Status");
			}
			Report.logInfo("Polling Requestid: " + statusID);
			result = wa.doGet(syncStatusUrl, h.getAllHeaders());
			resContent = mapper.readTree(result.getContent());
		}
	}
}
