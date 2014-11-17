package com.gainsight.bigdata.connectors;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.connectors.pojo.UsageTracker;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.HttpResponseObj;

public class RunAggregation extends TestBase {

	String dataSyncUrl;
	String SYNC_STATUS_URL;
	String getDistinctEventsByAccUT;
	String getDataUT;

	String version;

	String DL_AID_CID_SFDC_SFDC_SFDC_0 = "13a8ce98-91c7-473b-9ec6-6b1ae9798bde";
	String ACCOUNT_ID = "001F000001A9doeIAB";
	UsageTracker ut;

	@Parameters("version")
	@BeforeClass
	public void setUp(@Optional("") String version) throws Exception {
		this.version = version;
		dataSyncUrl = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC;
		SYNC_STATUS_URL = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC_STATUS;
		getDistinctEventsByAccUT = PropertyReader.nsAppUrl + ApiUrl.UT_DISTINCT_EVENTS_BY_ACC;
		getDataUT = PropertyReader.nsAppUrl + ApiUrl.UT_GET_DATA;
		init();
	}

	/*@Test
	public void testDirectLookup() throws Exception {
		HttpResponseObj result;
		String url;
		ObjectMapper mapper = new ObjectMapper();
		url = ApiUrl.setURLParam(dataSyncUrl, "new");
		result = wa.doGet(url, h.getAllHeaders());
		System.out.println(result.toString());
		JsonNode content = mapper.readTree(result.getContent());
		JsonNode data = content.findPath("data");
		JsonNode globalMapping = data.get("globalMapping");
		AccountDetails ad = new AccountDetails();
		ad.setGlobalMapping(globalMapping);
		url = ApiUrl.setURLParam(getDistinctEventsByAccUT, DL_AID_CID_SFDC_SFDC_SFDC_0) + "?gsaccountid=" + ACCOUNT_ID;
		result = wa.doGet(url, h.getAllHeaders());
		System.out.println("Usage Tracker Data" + result.getContent());

		url = ApiUrl.setURLParam(getDataUT, DL_AID_CID_SFDC_SFDC_SFDC_0);
		ut = new UsageTracker(UsageTrackerType.ACTIVITY_TRACKER.getValue(), ACCOUNT_ID);
		System.out.println("Usage Tracker Data" + mapper.writeValueAsString(ut));
		result = wa.doPost(url, mapper.writeValueAsString(ut), h.getAllHeaders());
		System.out.println("Response Data:" + result.getContent());

		ut = new UsageTracker(UsageTrackerType.ALL_USERS.getValue(), ACCOUNT_ID);
		System.out.println("ALL_USERS Data:" + mapper.writeValueAsString(ut));
		result = wa.doPost(url, mapper.writeValueAsString(ut), h.getAllHeaders());
		System.out.println("Response Data:" + result.getContent());

		ut = new UsageTracker(UsageTrackerType.ACCOUNT_USAGE.getValue(), ACCOUNT_ID);
		System.out.println("ACCOUNT_USAGE Data:" + mapper.writeValueAsString(ut));
		result = wa.doPost(url, mapper.writeValueAsString(ut), h.getAllHeaders());
		System.out.println("Response Data:" + result.getContent());

		ut = new UsageTracker(UsageTrackerType.ALL_EVENTS.getValue(), ACCOUNT_ID);
		System.out.println("ALL_EVENTS Data:" + mapper.writeValueAsString(ut));
		result = wa.doPost(url, mapper.writeValueAsString(ut), h.getAllHeaders());
		System.out.println("Response Data:" + result.getContent());

		ut = new UsageTracker(UsageTrackerType.EVENTS_BY_ACCOUNT.getValue(), ACCOUNT_ID);
		System.out.println("EVENTS_BY_ACCOUNT Data:" + mapper.writeValueAsString(ut));
		result = wa.doPost(url, mapper.writeValueAsString(ut), h.getAllHeaders());
		System.out.println("Response Data:" + result.getContent());
	}
*/
	@Test
	public void testAccDateSync() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(dataSyncUrl, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccNDate_DirectLookup();
		String rawBody = mapper.writeValueAsString(info);
		System.out.println(rawBody);
		HttpResponseObj result = wa.doPut(url, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode resContent = mapper.readTree(result.getContent());
		Report.logInfo("Status ID" + resContent.get("data").get("statusId").toString());
		String statusID = resContent.get("data").get("statusId").toString().replace("\"", "");
		SYNC_STATUS_URL = ApiUrl.setURLParam(SYNC_STATUS_URL, statusID);
		result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
		resContent = mapper.readTree(result.getContent());
		int counter = 0;
		while (resContent.get("data") == null || resContent.get("data").get("status").toString().replace("\"", "").equalsIgnoreCase("COMPLETED")) {
			if (counter++ > 30)
				return;
			System.out.println("Polling the status");
			result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
			resContent = mapper.readTree(result.getContent());
		}

		Report.logInfo("Async Status Response:" + result.toString());
	}
	
	@Test
	public void testAccUserDateSync() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(dataSyncUrl, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccUserNDate_DirectLookup();
		String rawBody = mapper.writeValueAsString(info);
		System.out.println(rawBody);
		HttpResponseObj result = wa.doPut(url, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode resContent = mapper.readTree(result.getContent());
		Report.logInfo("Status ID" + resContent.get("data").get("statusId").toString());
		String statusID = resContent.get("data").get("statusId").toString().replace("\"", "");
		SYNC_STATUS_URL = ApiUrl.setURLParam(SYNC_STATUS_URL, statusID);
		result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
		resContent = mapper.readTree(result.getContent());
		int counter = 0;
		while (resContent.get("data") == null || resContent.get("data").get("status").toString().replace("\"", "").equalsIgnoreCase("COMPLETED")) {
			if (counter++ > 30)
				return;
			System.out.println("Polling the status");
			result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
			resContent = mapper.readTree(result.getContent());
		}

		Report.logInfo("Async Status Response:" + result.toString());
	}
	
	@Test
	public void testAccUserDateEvent() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(dataSyncUrl, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccUserEventDate_DirectLookup();
		String rawBody = mapper.writeValueAsString(info);
		System.out.println(rawBody);
		HttpResponseObj result = wa.doPut(url, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode resContent = mapper.readTree(result.getContent());
		Report.logInfo("Status ID" + resContent.get("data").get("statusId").toString());
		String statusID = resContent.get("data").get("statusId").toString().replace("\"", "");
		SYNC_STATUS_URL = ApiUrl.setURLParam(SYNC_STATUS_URL, statusID);
		result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
		resContent = mapper.readTree(result.getContent());
		int counter = 0;
		while (resContent.get("data") == null || resContent.get("data").get("status").toString().replace("\"", "").equalsIgnoreCase("COMPLETED")) {
			if (counter++ > 30)
				return;
			System.out.println("Polling the status");
			result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
			resContent = mapper.readTree(result.getContent());
		}

		Report.logInfo("Async Status Response:" + result.toString());
	}
	
	@Test
	public void testAccDateEvent() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(dataSyncUrl, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccEventDate_DirectLookup();
		String rawBody = mapper.writeValueAsString(info);
		System.out.println(rawBody);
		HttpResponseObj result = wa.doPut(url, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode resContent = mapper.readTree(result.getContent());
		Report.logInfo("Status ID" + resContent.get("data").get("statusId").toString());
		String statusID = resContent.get("data").get("statusId").toString().replace("\"", "");
		SYNC_STATUS_URL = ApiUrl.setURLParam(SYNC_STATUS_URL, statusID);
		result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
		resContent = mapper.readTree(result.getContent());
		int counter = 0;
		while (resContent.get("data") == null || resContent.get("data").get("status").toString().replace("\"", "").equalsIgnoreCase("COMPLETED")) {
			if (counter++ > 30)
				return;
			System.out.println("Polling the status");
			result = wa.doGet(SYNC_STATUS_URL, h.getAllHeaders());
			resContent = mapper.readTree(result.getContent());
		}

		Report.logInfo("Async Status Response:" + result.toString());
	}
}
