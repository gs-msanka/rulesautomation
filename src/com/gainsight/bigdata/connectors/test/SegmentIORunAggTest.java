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

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.connectors.AccountDetails;
import com.gainsight.bigdata.connectors.ResponseValidator;
import com.gainsight.bigdata.connectors.SegmentIOTestData;
import com.gainsight.bigdata.connectors.TestDataUtils;
import com.gainsight.bigdata.connectors.pojo.AccountProperties;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.ExcelDataProvider;

public class SegmentIORunAggTest extends NSTestBase {

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
		testData = ExcelDataProvider.getDataFromExcel(TEST_DATA_FILE, "Sheet1").get(0);
		// new TestDataUtils().loadTestData();
		header.addHeader("actionType", "SAVE_AND_RUN");
		dataSyncUrl = ApiUrl.setURLParam(dataSyncUrl, "new");

	}

	@BeforeTest
	public void beforeTest() {
		Log.info("--- START SEGMENT AGGREGATION TEST ---");
	}

	@AfterTest
	public void afterTest() {
		Log.info("--- END SEGMENT AGGREGATION TEST ---");
	}

	@AfterTest
	public void cleanProject() throws Exception {
		if (AccountDetailsID != null) {
			String url = ApiUrl.setURLParam(accDeleteUrl, AccountDetailsID);
			ResponseObj result = wa.doDelete(url, header.getAllHeaders());
			if (result.getStatusCode() == 200) {
				Log.info("Account:" + AccountDetailsID + " Deleted successfully");
			} else {
				Log.info("Account:" + AccountDetailsID + " Deleted Failed");
			}
			AccountDetailsID = null;
		}
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUED_DL(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_DL();
		runAggregation(dataSyncUrl, header, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		responseValidator.validateReport(reportRunUrl, header, reportRequest, expectedResponse);
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, header, ACCOUNT_ID, testData);
		reportRequest = data.getFlippedColReportRequest(properties.getFlippedColleciton());
		expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		responseValidator.validateReport(reportRunUrl, header, reportRequest, expectedResponse);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUED_IDL(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_IDL();
		runAggregation(dataSyncUrl, header, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		responseValidator.validateReport(reportRunUrl, header, reportRequest, expectedResponse);
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, header, ACCOUNT_ID, testData);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet2")
	public void testAUED_DL_SIOUserNameAndEmail(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_DL_SIOUserNameAndEmail();
		runAggregation(dataSyncUrl, header, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		Log.info("Validating Day Aggregation Subject Area");
		responseValidator.validateReport(reportRunUrl, header, reportRequest, expectedResponse);
		Log.info("Validating Usage Tracker And Summary");
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, header, ACCOUNT_ID, testData);
		Log.info("--------- DONE VALIDATION OF ALL SECTIONS ----------");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet2")
	public void testAUED_IDL_SIOUserNameAndEmail(HashMap<String, String> testData) throws Exception {
		SegmentIOTestData data = new SegmentIOTestData();
		AccountDetails info = data.getMapping_AUED_IDL_SIOUserNameAndEmail();
		runAggregation(dataSyncUrl, header, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_AGG_RES);
		Log.info("Validating Day Aggregation Subject Area");
		responseValidator.validateReport(reportRunUrl, header, reportRequest, expectedResponse);
		Log.info("Validating Usage Tracker And Summary");
		String url = ApiUrl.setURLParam(usageTrackerGetDataUrl, properties.getAccountDetailsID());
		responseValidator.validateUsageTrackerData(url, header, ACCOUNT_ID, testData);
		Log.info("--------- DONE VALIDATION OF ALL SECTIONS ----------");
	}

	public void runAggregation(String url, Header header, AccountDetails info) throws Exception {
		int count = 0;
		String rawBody = mapper.writeValueAsString(info);
		ResponseObj result = wa.doPut(url, rawBody, header.getAllHeaders());
		Log.info("AGG Request::PUT:" + url + "\n Payload:" + rawBody + "\n Response:" + result.toString());
		JsonNode resContent = mapper.readTree(result.getContent());
		String statusID = resContent.get("data").get("statusId").toString();
		Assert.assertNotNull(statusID, "********AGGREGATION REQUEST FAILED*********");
		syncStatusUrl = ApiUrl.setURLParam(syncStatusUrl, statusID.replace("\"", ""));
		result = wa.doGet(syncStatusUrl, header.getAllHeaders());
		resContent = mapper.readTree(result.getContent());
		while (resContent.get("data") == null
				|| !resContent.get("data").get("status").toString().equalsIgnoreCase("\"COMPLETED\"")) {
			Thread.sleep(3000);
			if (count++ >= 20) {
				Assert.fail("Unable to get the Aggregation Request Status");
			}
			Log.info("Polling Requestid: " + statusID);
			result = wa.doGet(syncStatusUrl, header.getAllHeaders());
			resContent = mapper.readTree(result.getContent());
		}
	}
}
