package com.gainsight.bigdata.copilot.tests;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import au.com.bytecode.opencsv.CSVReader;

import com.gainsight.bigdata.pojo.CollectionInfo;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.copilot.apiImpl.CopilotUtil;
import com.gainsight.bigdata.copilot.apiImpl.LoadTestData;
import com.gainsight.bigdata.copilot.apiImpl.SmartListSetup;
import com.gainsight.bigdata.copilot.pojos.ActionDetails;
import com.gainsight.bigdata.copilot.smartlist.pojos.AutomatedRule;
import com.gainsight.bigdata.copilot.smartlist.pojos.SmartList;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails.DBDetail;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
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
	private TenantDetails tenantDetails;
	private DataLoadManager dataLoadManager;
	private Calendar calendar = Calendar.getInstance();
	private DataETL dataLoad = new DataETL();
	private final String CREATE_ACCS=env.basedir+"/testdata/newstack/CoPilot/ApexScripts/Create_Accounts_Customers_For_CoPilot.txt";
	private SmartListSetup smartListSetup=new SmartListSetup();
	private String collectionName=null;
	private DBDetail dbDetail=null;
	private String requestPayload=null;
	MongoDBDAO mongoDBDAO   = new  MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()),
            nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
	ReportManager reportManager=new ReportManager();
	private CopilotUtil CoUtil = new CopilotUtil();
	private TenantManager tenantManager;

	
    @BeforeClass
    public void setup() throws  IOException {
		tenantManager = new TenantManager();
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
        dataLoadManager = new DataLoadManager();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
        tenantManager.enabledRedShiftWithDBDetails(tenantDetails);
        dbDetail=mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());
    }
    
   
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
		String requestPayload=null;
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
	
	
    
	@TestInfo(testCaseIds = { "GS-4637" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC12")
	public void accountStrategyPowerListUsingMongoSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper
				.readValue(new FileReader(Application.basedir
						+ "/testdata/newstack/CoPilot/Job/demoload.txt"),
						JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.MONGO);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 9, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}
	
	
	@TestInfo(testCaseIds = { "GS-4637" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC13")
	public void accountStrategyPowerListUsingPostgresSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper
				.readValue(
						new FileReader(
								Application.basedir
										+ "/testdata/newstack/CoPilot/Job/GS-4637-1 -postgres-etl.txt"),
						JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.POSTGRES);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 9, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}

	@TestInfo(testCaseIds = { "GS-4637" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC14")
	public void accountStrategyPowerListUsingRedShiftSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper
				.readValue(
						new FileReader(
								Application.basedir
										+ "/testdata/newstack/CoPilot/Job/GS-4637-1 -RedShift-etl.txt"),
						JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.REDSHIFT);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 9, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}

	@TestInfo(testCaseIds = { "GS-4639" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC15")
	public void emailStrategyPowerListUsingMongoSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper
				.readValue(new FileReader(Application.basedir
						+ "/testdata/newstack/CoPilot/Job/demoload.txt"),
						JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.MONGO);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 9, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}

	@TestInfo(testCaseIds = { "GS-4639" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC16")
	public void emailStrategyPowerListUsingPostgresSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper
				.readValue(
						new FileReader(
								Application.basedir
										+ "/testdata/newstack/CoPilot/Job/GS-4637-1 -postgres-etl.txt"),
						JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.POSTGRES);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 9, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);	}

	@TestInfo(testCaseIds = { "GS-4639" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC17")
	public void emailStrategyPowerListUsingRedShiftSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper
				.readValue(
						new FileReader(
								Application.basedir
										+ "/testdata/newstack/CoPilot/Job/GS-4637-1 -RedShift-etl.txt"),
						JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.REDSHIFT);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 9, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}

	@TestInfo(testCaseIds = { "GS-4638" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC18")
	public void contactStrategyPowerListUsingMongoSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper.readValue(new FileReader(Application.basedir
				+ "/testdata/newstack/CoPilot/Job/GS-4638-Etl.txt"),
				JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.MONGO);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 10, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID","ContactID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}

	@TestInfo(testCaseIds = { "GS-4638" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC19")
	public void contactStrategyPowerListUsingPostgresSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper.readValue(new FileReader(Application.basedir
				+ "/testdata/newstack/CoPilot/Job/GS-4638-Etl.txt"),
				JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.POSTGRES);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 10, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID","ContactID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}

	@TestInfo(testCaseIds = { "GS-4638" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC20")
	public void contactStrategyPowerListUsingRedShiftSubjectArea(
			HashMap<String, String> testData) throws Exception {
		JobInfo load = mapper.readValue(new FileReader(Application.basedir
				+ "/testdata/newstack/CoPilot/Job/GS-4638-Etl.txt"),
				JobInfo.class);
		dataLoad.execute(load);
		collectionName = testData.get("CollectionName") + "-"
				+ calendar.getTimeInMillis();
		Log.info("Collection Name : " + collectionName);
		CollectionInfo collectionInfo = mapper.readValue(
				testData.get("CollectionSchema"), CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName(collectionName);
		String collectionId = dataLoadManager
				.createSubjectAreaAndGetId(collectionInfo);
		mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(
				tenantDetails.getTenantId(), collectionName, DBStoreType.REDSHIFT);
		Assert.assertNotNull(collectionId);
		CollectionInfo actualCollectionInfo = dataLoadManager
				.getCollectionInfo(collectionId);
		String jobFile = testData.get("ActualDataLoadJob");
		JobInfo loadTransform = mapper.readValue(new File(Application.basedir
				+ jobFile), JobInfo.class);
		File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform,
				calendar.getTime());
		DataLoadMetadata metadata = dataLoadManager
				.getDefaultDataLoadMetaData(actualCollectionInfo);
		metadata.setCollectionName(actualCollectionInfo.getCollectionDetails()
				.getCollectionName());
		String statusId = dataLoadManager
				.dataLoadManage(metadata, dataLoadFile);
		Assert.assertNotNull(statusId);
		dataLoadManager.waitForDataLoadJobComplete(statusId);
		verifyJobDetails(statusId, actualCollectionInfo
				.getCollectionDetails().getCollectionName(), 10, 0);
		String trigerCriteria = smartListSetup.getTrigerCriteria(testData,
				actualCollectionInfo);
		String actionCriteria = smartListSetup.getActionInfo(testData,
				actualCollectionInfo);
		AutomatedRule automatedRule = mapper.readValue(
				testData.get("automatedRule1"), AutomatedRule.class);
		for (ActionDetails actionDetails : automatedRule.getActionDetails()) {
			actionDetails.setActionInfo(actionCriteria);
		}
		automatedRule.setTriggerCriteria(trigerCriteria);
		String requestPayload = mapper.writeValueAsString(automatedRule);
		Log.info("Automated rule payload is "
				+ mapper.writeValueAsString(automatedRule));
		JsonNode nodeContent = CoUtil.createSmartList(testData, requestPayload);
		JsonNode nodeData = nodeContent.get("data");
		smartListID = nodeData.get("smartListId").asText();
		if (smartListID != null
				&& nodeContent.get("result").toString()
						.equalsIgnoreCase("true"))
			Log.info("SmartList is created. ID is " + smartListID);
		RulesUtil.waitForCompletion(smartListID, wa, header);
		JsonNode jsonNodeStats = CoUtil.getListStats(nodeData
				.get("smartListId").asText());
		Log.info("ContactCount is " + jsonNodeStats.get("contactCount").asInt());
		Log.info("CustomerCount is "
				+ jsonNodeStats.get("customerCount").asInt());
		Assert.assertEquals(jsonNodeStats.get("contactCount").asText(),
				testData.get("numberOfContacts"), "Verifying Contacts Count");
		Assert.assertEquals(jsonNodeStats.get("customerCount").asText(),
				testData.get("numberOfCustomers"), "Verifying Customers Count");
		String list[] = { "ID","ContactID", "Name", "Description", "LongTextArea", "Date",
				"CreatedDateTime", "Email" };
		List<Map<String, String>> actualData = ReportManager
				.getProcessedReportData(
						reportManager
								.runReportLinksAndGetData(reportManager
										.createTabularReport(
												actualCollectionInfo, list)),
						actualCollectionInfo);
		List<Map<String, String>> expData = Comparator
				.getParsedCsvData(new CSVReader(new FileReader(
						Application.basedir
								+ loadTransform.getDateProcess()
										.getOutputFile())));
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(
				expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0);
	}

	
    private void verifyJobDetails(String jobId, String collectionName, int successCount, int failedCount) {
        DataLoadStatusInfo statusInfo = dataLoadManager.getDataLoadJobStatus(jobId);
        Assert.assertEquals(statusInfo.getCollectionName(), collectionName);
        Assert.assertEquals(statusInfo.getSuccessCount(), successCount);
        Assert.assertEquals(statusInfo.getFailureCount(), failedCount);
        Assert.assertEquals(statusInfo.getStatusType(), DataLoadStatusType.COMPLETED);
    }
    
	
	@AfterClass
	public void tearDown(){
		 mongoDBDAO.mongoUtil.closeConnection();
	}
}