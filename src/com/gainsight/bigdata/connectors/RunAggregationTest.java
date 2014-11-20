package com.gainsight.bigdata.connectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.connectors.enums.ConnConstants;
import com.gainsight.bigdata.connectors.pojo.AccountProperties;
import com.gainsight.bigdata.connectors.pojo.UsageTracker;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.utils.DataProviderArguments;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class RunAggregationTest extends TestBase {

	String DataSync_URL;
	String Sync_Status_URL;
	String DistinctEventsByAcc_UT_URL;
	String GetData_UT_URL;

	String version;
	final String Report_Run_URL = PropertyReader.nsAppUrl + "/api/reports/run/preparation";
	String ACCOUNT_ID = "001o000000E4KKRAA3";
	final String TEST_DATA_FILE = "testdata/newstack/connectors/TestInputs.xls";

	ObjectMapper mapper = new ObjectMapper();

	@Parameters("version")
	@BeforeClass
	public void setUp(@Optional("") String version) throws Exception {
		this.version = version;
		DataSync_URL = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC;
		Sync_Status_URL = PropertyReader.nsAppUrl + ApiUrl.ACC_SYNC_STATUS;
		DistinctEventsByAcc_UT_URL = PropertyReader.nsAppUrl + ApiUrl.UT_DISTINCT_EVENTS_BY_ACC;
		GetData_UT_URL = PropertyReader.nsAppUrl + ApiUrl.UT_GET_DATA;
		init();
	}

	@Test
	public void testAccDateSync() throws Exception {
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(DataSync_URL, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccNDate_DirectLookup();
		runAggregation(url, h, info);
	}

	@Test
	public void testAccUserDateSync() throws Exception {
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(DataSync_URL, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccUserNDate_DirectLookup();
		runAggregation(url, h, info);
	}

	@Test
	public void testAccUserDateEvent() throws Exception {
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(DataSync_URL, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccUserEventDate_DirectLookup();
		runAggregation(url, h, info);
	}

	@Test
	public void testAccDateEvent() throws Exception {
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(DataSync_URL, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccEventDate_DirectLookup();
		runAggregation(url, h, info);
	}

	@Test
	public void testAccUserDateEvent_AccIndirect() throws Exception {
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(DataSync_URL, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccUserEventDate_AccInDirectLookup();
		runAggregation(url, h, info);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testAccUserDateEvent1(HashMap<String, String> testData) throws Exception {
		String url;
		h.addHeader("actionType", "SAVE_AND_RUN");
		url = ApiUrl.setURLParam(DataSync_URL, "new");
		DataAPITestData data = new DataAPITestData();
		AccountDetails info = data.getMappingWithAccUserEventDate_DirectLookup();
		AccountProperties properties = runAggregation(url, h, info);
		validateUsageTrackerData(properties.getAccountID(), testData);
		String reqString = testData.get("USER_LEVEL_ DAY_AGG_REPORT_INFO");
		JsonNode reqJson = mapper.readTree(reqString);
		reqJson.with("ReportInfo");
		ObjectNode reportInfo = (ObjectNode) ((ArrayNode) reqJson.get("ReportInfo")).get(0);
		reportInfo.put("SchemaName", properties.getAccountName() + " Day Agg");
		reportInfo.put("CollectionID", properties.getDayAggCollection());
		HttpResponseObj result = wa.doPost(Report_Run_URL, reqJson.toString(), h.getAllHeaders());
		System.out.println(mapper.writeValueAsString(result));

	}

	public AccountProperties runAggregation(String url, Header h, AccountDetails info) throws Exception {
		String rawBody = mapper.writeValueAsString(info);
		System.out.println(rawBody);
		HttpResponseObj result = wa.doPut(url, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode resContent = mapper.readTree(result.getContent());
		Report.logInfo("Status ID" + resContent.get("data").get("statusId").toString());
		String statusID = resContent.get("data").get("statusId").toString().replace("\"", "");
		Sync_Status_URL = ApiUrl.setURLParam(Sync_Status_URL, statusID);
		result = wa.doGet(Sync_Status_URL, h.getAllHeaders());
		resContent = mapper.readTree(result.getContent());
		while (resContent.get("data") == null
				|| resContent.get("data").get("status").toString().replace("\"", "").equalsIgnoreCase("COMPLETED")) {
			System.out.println("Polling the status");
			result = wa.doGet(Sync_Status_URL, h.getAllHeaders());
			resContent = mapper.readTree(result.getContent());
		}
		Report.logInfo("Async Status Response:" + result.toString());
		return getAccountProperties(info.displayName);
	}

	public AccountProperties getAccountProperties(String accountName) throws Exception {
		DB dbCon = new TestDataUtils().getRemoteDBConnection();
		AccountProperties accProp = new AccountProperties();
		String accountID;
		String dayAggCollection;
		String flippedColleciton;
		// Get AccountDetails ID
		DBObject object = mongoFind(dbCon, "accountDetail", "displayName", accountName);
		accountID = object.get("accountId").toString();
		accProp.setAccountName(accountName);
		accProp.setAccountID(accountID);
		String flippedMeasuresSyncInfoId = object.get("flippedMeasuresSyncInfoId").toString();
		dayAggCollection = ((DBObject) object.get("properties")).get("VIEW_FOR_BDA_COLLECTION_MASTER_ID").toString();
		accProp.setDayAggCollection(dayAggCollection);
		object = mongoFind(dbCon, "syncInfo", "syncInfoId", flippedMeasuresSyncInfoId);
		String flipColDBName = ((DBObject) object.get("target")).get("name").toString();
		object = mongoFind(dbCon, "collectionmaster", "CollectionDetails.dbCollectionName", flipColDBName);
		flippedColleciton = ((DBObject) object.get("CollectionDetails")).get("CollectionID").toString();
		accProp.setFlippedColleciton(flippedColleciton);
		System.out.println("AccountDetailsID:" + accountID + "$$DayAggCol:" + dayAggCollection + "$$FlippedCol:"
				+ flippedColleciton);

		return accProp;

	}

	public void validateUsageTrackerData(String accountID, Map<String, String> testData) throws Exception {

		// Validate ALL_EVENTS
		String allEventsActual = getUsageTrackerData(accountID, ConnConstants.TrackerData.ALL_EVENTS);
		String allEventsExpected = testData.get(ConnConstants.TrackerData.ALL_EVENTS.getDataType());
		jsonOutputCompare(allEventsActual, allEventsExpected);

		// Validate ALL_USERS
		String allUsersActual = getUsageTrackerData(accountID, ConnConstants.TrackerData.ALL_USERS);
		String allUsersExpected = testData.get(ConnConstants.TrackerData.ALL_EVENTS.getDataType());
		jsonOutputCompare(allUsersActual, allUsersExpected);

		// Validate EVENTS_BY_ACCOUNT
		String eventsByAccountActual = getUsageTrackerData(accountID, ConnConstants.TrackerData.EVENTS_BY_ACCOUNT);
		String eventsByAccountExpected = testData.get(ConnConstants.TrackerData.ALL_EVENTS.getDataType());
		jsonOutputCompare(eventsByAccountActual, eventsByAccountExpected);

		// Validate ACCOUNT_USAGE
		String usageByAccountActual = getUsageTrackerData(accountID, ConnConstants.TrackerData.ACCOUNT_USAGE);
		String usageByAccountExpected = testData.get(ConnConstants.TrackerData.ALL_EVENTS.getDataType());
		jsonOutputCompare(usageByAccountActual, usageByAccountExpected);

	}

	public String getUsageTrackerData(String accountID, ConnConstants.TrackerData tracker)
			throws Exception {
		UsageTracker ut = new UsageTracker(tracker, ACCOUNT_ID);
		String url = ApiUrl.setURLParam(GetData_UT_URL, accountID);
		HttpResponseObj result = wa.doPost(url, mapper.writeValueAsString(ut), h.getAllHeaders());
		JsonNode resContent = mapper.readTree(result.getContent());
		System.out.println(result.getContent());
		return resContent.get("data").toString();
	}

	public DBObject mongoFind(DB dbCon, String collectionName, String filterName, String filterValue)
			throws Exception {
		DBCollection collection = dbCon.getCollection(collectionName);
		BasicDBObject doc = new BasicDBObject(filterName, filterValue);
		DBCursor cursor = collection.find(doc);
		return (DBObject) cursor.next();
	}

	private boolean jsonOutputCompare(String actual, String expected)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(actual);
		JsonNode tree2 = mapper.readTree(expected);

		if (tree1.equals(tree2)) {
			Report.logInfo("Report output Matched");
			return true;
		} else {
			Report.logInfo("Report output Did Not Match. Expected JSON:\n"
					+ expected + "\n Actual JSON:" + actual);
			return false;
		}
	}
}
