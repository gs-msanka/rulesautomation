package com.gainsight.bigdata.copilot.tests;

import java.util.HashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.gainsight.bigdata.copilot.apiImpl.CopilotUtil;
import com.gainsight.bigdata.copilot.apiImpl.LoadTestData;
import com.gainsight.bigdata.copilot.smartlist.pojos.SmartList;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartListTest extends LoadTestData {

	private final String TEST_DATA_FILE = "/testdata/newstack/CoPilot/TestCase-Data/List.xls";
	String req = null;
	ResponseObj resp;
	String StatsString = null;
	SmartList smList = new SmartList();
	String smartListID = null;
	
	@TestInfo(testCaseIds = { "GS-4610" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC1")
	public void createList1(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targeting 2 Accounts->It will filter 1 Contact Each, so total 2
		// Contacts.
		// Expected : Contact Count : 3 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count

	}

	@TestInfo(testCaseIds = { "GS-4614" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC2")
	public void createList2(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 2 Accounts->It will filter by ASV<=20.
		// CustomerInfo:CreatedbyEmail is same for 2 Accounts. So Contact Count
		// is 1.
		// Expected : Contact Count : 1 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4617" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC3")
	public void createList3(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 4 Accounts-> Contact Email Filters 3 Contacts-2 Accounts.
		// Contact Email Opt out filters 2 Contacts.
		// Expected : Contact Count : 2 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4620" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC4")
	public void createList4(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 2 Accounts using OR Condition-> Case created by filters 1
		// user-2 Accounts.
		// Expected : Contact Count : 1 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4630" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC5")
	public void createList5(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 4 Accounts-> Advance Logic uses both AND , OR
		// condition->Filters 2 Contacts,1 Customer.
		// Expected : Contact Count : 2 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4611" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC6")
	public void createList6(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 1 Accounts, 1 contact.
		// Expected : Contact Count :1 , Customer Count : 1
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4612" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC7")
	public void createList7(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 2 Accounts, filter 1 Account, 1 contact.
		// Expected : Contact Count :1 , Customer Count : 1
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4613" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC8")
	public void createList8(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 2 Accounts, 2 same contact emails.
		// Expected : Contact Count :1 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4615" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC9")
	public void createList9(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targetting 2 Accounts, 2 same contact emails.
		// Expected : Contact Count :1 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

	@TestInfo(testCaseIds = { "GS-4616" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC10")
	public void createList10(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targeting 2 Accounts, 2 same contact emails.
		// Expected : Contact Count :1 , Customer Count : 2
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}
	
	@TestInfo(testCaseIds = { "GS-4618" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC11")
	public void createList11(HashMap<String, String> testData) throws Exception {

		CopilotUtil CoUtil = new CopilotUtil();
		JsonNode nodeContent = CoUtil.createSmartList(testData);
		JsonNode nodeData = nodeContent.get("data");

		// Verifying Response
		smartListID = nodeData.get("smartListId").asText();

		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);

		RulesUtil.waitForCompletion(smartListID, wa, header);
		// Verify the Stats : Contact Count and Customer Count
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());

		// Targeting 1 Contact , 1 Case
		// Expected : Contact Count :1 , Customer Count : 1
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts")); // Contact Count
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers")); // Customer Count
	}

}
