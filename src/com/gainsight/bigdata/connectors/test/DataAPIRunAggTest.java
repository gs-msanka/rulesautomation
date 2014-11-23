package com.gainsight.bigdata.connectors.test;

import java.util.HashMap;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.connectors.*;
import com.gainsight.bigdata.connectors.pojo.AccountProperties;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.utils.DataProviderArguments;

public class DataAPIRunAggTest extends TestBase {

	final String TEST_DATA_FILE = "testdata/newstack/connectors/DataAPIExpectedOutput.xls";
	String dataSyncUrl = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC;
	String syncStatusUrl = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC_STATUS;
	String distinctEventsByAcc_UT_Url = PropertyReader.nsAppUrl + ApiUrl.UT_DISTINCT_EVENTS_BY_ACC;
	String usageTrackerGetDataUrl = PropertyReader.nsAppUrl + ApiUrl.UT_GET_DATA;
	String reportRunUrl = PropertyReader.nsAppUrl + ApiUrl.REPORT_RUN;
	String accDeleteUrl = PropertyReader.nsAppUrl + ApiUrl.ACC_DELETE;
	String ACCOUNT_ID = "001o000000E4KKRAA3";

	String ACC_DATE_DAY_AGG = "ACC_DATE_DAY_AGG";
	String ACC_USER_DATE_DAY_AGG = "ACC_USER_DATE_DAY_AGG";
	String ACC_EVENT_DATE_DAY_AGG = "ACC_EVENT_DATE_DAY_AGG";
	String ACC_USER_EVENT_DATE_DAY_AGG = "ACC_USER_EVENT_DATE_DAY_AGG";
	HashMap<String, String> testData;
	String AccountDetailsID;
	String reportRequest;

	TestDataUtils testDataUtils = new TestDataUtils();
	ResponseValidator responseValidator = new ResponseValidator();

	ObjectMapper mapper = new ObjectMapper();

	@Parameters("version")
	@BeforeClass
	public void setUp() throws Exception {
		init();
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

	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account Date(AD)
	 * Lookup Type: Direct Lookup(DL)
	 * @param testData
	 * @throws Exception
	 */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAD_DL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AD_DL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}

	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account Date(AD)
	 * Lookup Type: Indirect Lookup(IDL)
	 * @param testData
	 * @throws Exception
	 */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAD_IDL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AD_IDL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}

	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account,User,Date(AUD)
	 * Lookup Type: Direct Lookup(DL)
	 * @param testData
	 * @throws Exception
	 */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUD_DL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AUD_DL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}

	
	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account,User,Date(AUD)
	 * Lookup Type: Indirect Lookup(IDL)
	 * @param testData
	 * @throws Exception
	 */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUD_IDL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AUD_IDL();
		runAggregation(dataSyncUrl, h, info);
		// After running aggregation, get aggregated account details id and output collection details
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}

	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account,Event,Date(AED)
	 * Lookup Type: Direct Lookup(DL)
	 * @param testData
	 * @throws Exception
	 */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAED_DL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AED_DL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_EVENT_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}

	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account,Event,Date(AED)
	 * Lookup Type: Indirect Lookup(IDL)
	 * @param testData
	 * @throws Exception
	 */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAED_IDL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AED_IDL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_EVENT_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}
	
	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account,User, Event,Date(AUED)
	 * Lookup Type: Direct Lookup(DL)
	 * @param testData
	 * @throws Exception
	 */

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUED_DL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AUED_DL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}
	
	/**
	 * Test Case: DataAPI/GA Aggregation with the below mapping info and lookup type
	 * Mapping: Account, User, Event, Date(AUED)
	 * Lookup Type: Indirect Lookup(IDL)
	 * @param testData
	 * @throws Exception
	 */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAUED_IDL(HashMap<String, String> testData) throws Exception {
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMapping_AUED_IDL();
		runAggregation(dataSyncUrl, h, info);
		AccountProperties properties = testDataUtils.getAccountProperties(info.getDisplayName(), false);
		AccountDetailsID = properties.getAccountDetailsID();
		reportRequest = data.getDayAggColReportRequest(properties.getDayAggCollection());
		String expectedResponse = testData.get(ACC_USER_EVENT_DATE_DAY_AGG);
		responseValidator.validateReport(reportRunUrl, h, reportRequest, expectedResponse);
	}

	/**
	 * 
	 * @param url : Run Aggregation end point
	 * @param h : Header values
	 * @param info : Payload for Run Aggregation
	 * @throws Exception
	 */
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
		// If the aggregation process is not yet completed.
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
