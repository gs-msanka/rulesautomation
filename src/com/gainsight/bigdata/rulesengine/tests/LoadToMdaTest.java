package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.FieldMapping;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDACollection;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.http.Header;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.DBStoreType;
import com.gainsight.utils.annotations.TestInfo;

/**
 * @author Abhilash Thaduka
 *
 */
public class LoadToMdaTest extends BaseTest {

	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
	private ObjectMapper mapper = new ObjectMapper();
	private NSTestBase nsTestBase = new NSTestBase();
	private Header header = new Header();
	private RulesUtil rulesUtil = new RulesUtil();
	ReportManager reportManager = new ReportManager();
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	Date date = Calendar.getInstance().getTime();
	private DataETL dataETL = new DataETL();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;
	GSDataImpl gsDataImpl = null;
	private TenantDetails tenantDetails = null;
	private TenantManager tenantManager;
	
	
	@BeforeClass
	@Parameters("dbStoreType")
	public void setup(@Optional String dbStoreType) throws Exception {
		basepage.login();
		sfdc.connect();
		nsTestBase.init();
		tenantManager = new TenantManager();
		tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
		if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.MONGO.name())) {
			Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
		} else if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.REDSHIFT.name())) {
			Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
		}
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		header.addHeader("Origin", sfdcInfo.getEndpoint());
		header.addHeader("Content-Type", "application/json");
		header.addHeader("appOrgId", sfdcInfo.getOrg());
		header.addHeader("appUserId", sfdcInfo.getUserId());
		header.addHeader("appSessionId", sfdcInfo.getSessionId());
		gsDataImpl = new GSDataImpl(header);
	}

	@BeforeMethod
	public void CleanUpCustomers() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
	}

	/*
	 * Insert: DataTypes Supported for field mapping: String, date, datetime, Number, Number with decimals, boolean
	 * Data Types Supported for default value mapping: String, Number, Number with decimals, boolean.
	 * 
	 * For Update: DataTypes Supported for field mapping: String, date,
	 * datetime, Number, Number with decimals, boolean.
	 * Data Types Supported for  default value mapping: String, Number, Number with decimals, boolean 
	 * key Identifiers: string field, one number field and one date field as key identifiers.
	 */
	@TestInfo(testCaseIds = { "GS-3977", "GS-3944", "GS-3989", "GS-3955", "GS-3978", "GS-3989" })
	@Test
	public void loadToMdaInsertAndUpdateScenarioUsingMdaData() throws Exception {
		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-3977-MDA" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult());

		// Creating collection with no data for destination
		CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo2.getCollectionDetails().setCollectionName("GS-3977EmptyCollection-" + date.getTime());
		String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
		Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
		String collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
		// Setting collection permisssion in ruleslodable object
		LoadToMDACollection loadToMDACollection = new LoadToMDACollection();
		loadToMDACollection.setType("MDA");
		loadToMDACollection.setObjectName(actualCollectionInfo2.getCollectionDetails().getCollectionId());
		loadToMDACollection.setObjectLabel(actualCollectionInfo2.getCollectionDetails().getCollectionName());
		List<LoadToMDACollection.Field> fields = new ArrayList<>();
		LoadToMDACollection.Field field = null;
		for (CollectionInfo.Column column : actualCollectionInfo2.getColumns()) {
			field = new LoadToMDACollection.Field();
			field.setDataType(column.getDatatype().toUpperCase());
			field.setName(column.getDbName());
			fields.add(field);
		}
		loadToMDACollection.setFields(fields);
		rulesUtil.saveCustomObjectInRulesConfig(mapper.writeValueAsString(loadToMDACollection));

		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-Input.json"),RulesPojo.class);
		LoadToMDAAction loadToMDAAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
		loadToMDAAction.setObjectName(collectionName2);
		for (FieldMapping fields1 : loadToMDAAction.getFieldMappings()) {
			fields1.setSourceObject(collectionName);
		}
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		String demo=mapper.writeValueAsString(rulesPojo);
		Log.debug("Updated Pojo is" +demo);
		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		dataETL.execute(mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-ExpectedJob.txt")),JobInfo.class));

		// Below are the list of all datatypes verifying for actual data
		List<Map<String, String>> expData = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir	+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/ExpectedData2.csv"))),actualCollectionInfo);
		String list[] = { "CustomNumberWithDecimals1", "CustomeDate2", "CustomString2","Name", "CustomNumber1", "CustomBooleanField1",
				"CustomString1", "CustomDateTime1", "CustomBooleanField2","CustomDateTime2", "CustomeDate1",
				"CustomNumberWithDecimals2" ,"CustomNumber2" };
		List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createTabularReport(actualCollectionInfo2,list)),actualCollectionInfo2);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
			
		// Testcase GS-3978, GS-3989 starts here
		
		sfdc.runApexCode(resolveStrNameSpace("Delete [Select Id from JBCXM__AutomatedAlertRules__c];"));	
		// Creating another collection with data as source collection for upsert condition
		CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/GS-3978-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo1.getCollectionDetails().setCollectionName("GS-3978-MDA" + date.getTime());
		String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
		Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
		String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/GS-3978-DataloadJob.txt")), JobInfo.class);
		File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
		DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
		NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
		Assert.assertTrue(nsResponseObj1.isResult());
		
		RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/GS-3978-Input.json"),RulesPojo.class);
		LoadToMDAAction loadToMDAAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
		loadToMDAAction1.setObjectName(collectionName2);
		for (FieldMapping fields1 : loadToMDAAction1.getFieldMappings()) {
			fields1.setSourceObject(collectionName1);
		}
		rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction1, JsonNode.class));
		rulesPojo1.getSetupRule().setSelectObject(collectionName1);
		rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
		String demo1=mapper.writeValueAsString(rulesPojo1);
		Log.debug("Updated Pojo is" +demo1);
		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo1);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo1.getRuleName()), "Check whether Rule ran successfully or not ");
		dataETL.execute(mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/GS-3978-ExpectedJob.txt")),JobInfo.class));

		// Below are the list of all datatypes verifying for actual data
		List<Map<String, String>> expData1 = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir	+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/ExpectedData2.csv"))),actualCollectionInfo1);
		String list1[] = { "CustomNumberWithDecimals1", "CustomeDate2", "CustomString2","Name", "CustomNumber1", "CustomBooleanField1",
				"CustomString1", "CustomDateTime1", "CustomBooleanField2","CustomDateTime2", "CustomeDate1",
				"CustomNumberWithDecimals2" ,"CustomNumber2" };
		List<Map<String, String>> actualData1 = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createTabularReport(actualCollectionInfo2,list1)),actualCollectionInfo2);
		Log.info("Actual : " + mapper.writeValueAsString(actualData1));
		Log.info("Expected : " + mapper.writeValueAsString(expData1));
		List<Map<String, String>> diffData1 = Comparator.compareListData(expData1, actualData1);
		Log.info("Diff : " + mapper.writeValueAsString(diffData1));
		Assert.assertEquals(diffData1.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
	}

	/*
	 * For Upsert: DataTypes Supported for field mapping: String, date,
	 * datetime, Number, Number with decimals, boolean.
	 * Data Types Supported for  default value mapping: String, Number, Number with decimals, boolean 
	 * key Identifiers: string field, one number field and one date field as key identifiers.
	 * So,New records which don't satify the condition should get inserted
	 */
	@TestInfo(testCaseIds = { "GS-3979", "GS-3991"})
	@Test
	public void loadToMdaUpsertScenarioUsingMdaData() throws Exception {
		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-3979-MDA-3979" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();	
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult());

		// Creating collection with no data for destination
		CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo2.getCollectionDetails().setCollectionName("GS-3979EmptyCollection-3979" + date.getTime());
		String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
		Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
		String collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
		// Setting collection permisssion in ruleslodable object
		LoadToMDACollection loadToMDACollection = new LoadToMDACollection();
		loadToMDACollection.setType("MDA");
		loadToMDACollection.setObjectName(actualCollectionInfo2.getCollectionDetails().getCollectionId());
		loadToMDACollection.setObjectLabel(actualCollectionInfo2.getCollectionDetails().getCollectionName());
		List<LoadToMDACollection.Field> fields = new ArrayList<>();
		LoadToMDACollection.Field field = null;
		for (CollectionInfo.Column column : actualCollectionInfo2.getColumns()) {
			field = new LoadToMDACollection.Field();
			field.setDataType(column.getDatatype().toUpperCase());
			field.setName(column.getDbName());
			fields.add(field);
		}
		loadToMDACollection.setFields(fields);
		rulesUtil.saveCustomObjectInRulesConfig(mapper.writeValueAsString(loadToMDACollection));

		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-Input.json"),RulesPojo.class);
		LoadToMDAAction loadToMDAAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
		loadToMDAAction.setObjectName(collectionName2);
		for (FieldMapping fields1 : loadToMDAAction.getFieldMappings()) {
			fields1.setSourceObject(collectionName);
		}
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		String demo=mapper.writeValueAsString(rulesPojo);
		Log.debug("Updated Pojo is" +demo);
		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		dataETL.execute(mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-ExpectedJob.txt")),JobInfo.class));

		// Below are the list of all datatypes verifying for actual data
		List<Map<String, String>> expData = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir	+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/ExpectedData2.csv"))),actualCollectionInfo);
		String list[] = { "CustomNumberWithDecimals1", "CustomeDate2", "CustomString2","Name", "CustomNumber1", "CustomBooleanField1",
				"CustomString1", "CustomDateTime1", "CustomBooleanField2","CustomDateTime2", "CustomeDate1",
				"CustomNumberWithDecimals2" ,"CustomNumber2" };
		List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createTabularReport(actualCollectionInfo2,list)),actualCollectionInfo2);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expData));
		List<Map<String, String>> diffData = Comparator.compareListData(expData, actualData);
		Log.info("Diff : " + mapper.writeValueAsString(diffData));
		Assert.assertEquals(diffData.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
			
		// Testcase GS-3979, GS-3991 starts here
		
		sfdc.runApexCode(resolveStrNameSpace("Delete [Select Id from JBCXM__AutomatedAlertRules__c];"));	
		// Creating another collection with data as source collection for upsert condition
		CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/GS-3979-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo1.getCollectionDetails().setCollectionName("GS-3979-MDA2-3979" + date.getTime());
		String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
		Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
		String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/GS-3979-DataloadJob.txt")), JobInfo.class);
		File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
		DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
		NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
		Assert.assertTrue(nsResponseObj1.isResult());
		
		RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/GS-3979-Input.json"),RulesPojo.class);
		LoadToMDAAction loadToMDAAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
		loadToMDAAction1.setObjectName(collectionName2);
		for (FieldMapping fields1 : loadToMDAAction1.getFieldMappings()) {
			fields1.setSourceObject(collectionName1);
		}
		rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction1, JsonNode.class));
		rulesPojo1.getSetupRule().setSelectObject(collectionName1);
		rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
		String demo1=mapper.writeValueAsString(rulesPojo1);
		Log.debug("Updated Pojo is" +demo1);
		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo1);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo1.getRuleName()), "Check whether Rule ran successfully or not !");
		dataETL.execute(mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/GS-3979-ExpectedJob.txt")),JobInfo.class));

		// Below are the list of all datatypes verifying for actual data
		List<Map<String, String>> expData1 = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir	+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/ExpectedData2.csv"))),actualCollectionInfo1);
		String list1[] = { "CustomNumberWithDecimals1", "CustomeDate2", "CustomString2","Name", "CustomNumber1", "CustomBooleanField1",
				"CustomString1", "CustomDateTime1", "CustomBooleanField2","CustomDateTime2", "CustomeDate1",
				"CustomNumberWithDecimals2" ,"CustomNumber2" };
		//actualCollectionInfo2
		List<Map<String, String>> actualData1 = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createTabularReport(actualCollectionInfo2,list1)),actualCollectionInfo2);
		Log.info("Actual : " + mapper.writeValueAsString(actualData1));
		Log.info("Expected : " + mapper.writeValueAsString(expData1));
		List<Map<String, String>> diffData1 = Comparator.compareListData(expData1, actualData1);
		Log.info("Diff : " + mapper.writeValueAsString(diffData1));
		Assert.assertEquals(diffData1.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
	}
}
