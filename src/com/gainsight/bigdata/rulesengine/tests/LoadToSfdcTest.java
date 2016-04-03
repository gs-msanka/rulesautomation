package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gainsight.util.MongoDBDAO;
import org.apache.commons.lang3.StringUtils;
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
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.FieldMapping;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToSFDCAction;
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

import static org.testng.Assert.assertTrue;

/**
 * @author Abhilash Thaduka
 *
 */
public class LoadToSfdcTest extends BaseTest {
	
	private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
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
	RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	private MongoDBDAO mongoDBDAO = null;



	@BeforeClass
	@Parameters("dbStoreType")
	public void setup(@Optional String dbStoreType) throws Exception {

		sfdc.connect();
		nsTestBase.init();
		tenantManager = new TenantManager();
		String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
		tenantDetails = tenantManager.getTenantDetail(null, tenantId);
		if (StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Mongo")) {
			if(tenantDetails.isRedshiftEnabled()){
				mongoDBDAO = MongoDBDAO.getGlobalMongoDBDAOInstance();
				assertTrue(mongoDBDAO.disableRedshift(tenantId), "Failed updating dataStoreType at tenant level to Mongo.");
			}
		}
		else if(StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Redshift")){
			if(!tenantDetails.isRedshiftEnabled()) {
				assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails), "Failed updating dataStoreType at tenant level to Redshift");
			}
		}
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		header.addHeader("Origin", sfdcInfo.getEndpoint());
		header.addHeader("Content-Type", "application/json");
		header.addHeader("appOrgId", sfdcInfo.getOrg());
		header.addHeader("appUserId", sfdcInfo.getUserId());
		header.addHeader("appSessionId", sfdcInfo.getSessionId());
		gsDataImpl = new GSDataImpl(header);
		rulesConfigureAndDataSetup.createDataLoadConfiguration();
	}
	
	@BeforeMethod
	public void CleanUpCustomers() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		// Deleting records from destination object
		sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM RulesSFDCCustom__c];"));
	}
	
	@TestInfo(testCaseIds = { "GS-3963", "GS-3964", "GS-3965" })
	@Test(description = "load to sfdc action(insert operation) with default values of number, string and boolean datatypes along with junk data for few datatypes and (Update operation) with string & date datatypes as unique identifiers along with default values of number, string and boolean datatypes")
	public void loadToSfdcInsertAndUpdateOperation() throws Exception{
		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("LoadToSfdc-MDA" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult());
		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-Input.json"),RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		for (FieldMapping fields1 : loadToSFDCAction.getFieldMappings()) {
			fields1.setSourceObject(collectionName);
		}
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo/json object is" +mapper.writeValueAsString(rulesPojo));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertFalse(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule execution should be partially failed because of junk data");
		
		//Asserting expected data and actual data
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-ExpectedJob.txt"),JobInfo.class);
		dataETL.execute(jobInfo);		
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS3963-ExpectedData2.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS3963-ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
		
		// Testcase GS-3965(Update operation) starts here	
		sfdc.runApexCode(resolveStrNameSpace("Delete [Select Id from JBCXM__AutomatedAlertRules__c];"));	
		// Creating another collection with data as source collection for upsert condition
		CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS-3965-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo1.getCollectionDetails().setCollectionName("GS-3965-Collection2" + date.getTime());
		String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
		Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
		String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS-3965-DataloadJob.txt")), JobInfo.class);
		File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
		DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
		NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
		Assert.assertTrue(nsResponseObj1.isResult());
		
		RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS-3965-Input.json"),RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		for (FieldMapping fields1 : loadToSFDCAction1.getFieldMappings()) {
			fields1.setSourceObject(collectionName1);
		}
		rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction1, JsonNode.class));
		rulesPojo1.getSetupRule().setSelectObject(collectionName1);
		rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
		Log.debug("Updated Pojo/json object is " +mapper.writeValueAsString(rulesPojo1));	
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo1);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo1.getRuleName()), "Rule processing ahs failed, kindly check rule execution email for more details..");
		
		//Asserting expected data and actual data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS-3965-ExpectedJob.txt"),JobInfo.class));		
		List<Map<String, String>> expectedData1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS3965-ExpectedData2.csv");
		List<Map<String, String>> actualData1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS3965-ActualData.csv");
		List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedData1, actualData1);
		Log.info("Actual : " + mapper.writeValueAsString(actualData1));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData1));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
		Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
	}
	
	@TestInfo(testCaseIds = { "GS-3984", "GS-3985" })
	@Test(description = "load to sfdc action(insert operation) with default values of number, string and boolean datatypes along with junk data for few datatypes and (Upsert operation) with string, date and datetime datatypes as unique identifiers along with default values of number, string and boolean datatypes")
	public void loadToSfdcInsertAndUpsertOperation() throws Exception{
		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("LoadToSfdc-MDA-Upsert" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult());
		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-Input.json"),RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		for (FieldMapping fields1 : loadToSFDCAction.getFieldMappings()) {
			fields1.setSourceObject(collectionName);
		}
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo/json object is" +mapper.writeValueAsString(rulesPojo));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertFalse(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule execution should be partially failed because of junk data");
		
		//Asserting expected data and actual data
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-ExpectedJob.txt"),JobInfo.class);
		dataETL.execute(jobInfo);		
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS3963-ExpectedData2.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS3963-ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
		
		// Testcase GS-3984 (Upsert operation) starts here
		
		sfdc.runApexCode(resolveStrNameSpace("Delete [Select Id from JBCXM__AutomatedAlertRules__c];"));	
		// Creating another collection with data as source collection for upsert condition
		CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS-3984-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo1.getCollectionDetails().setCollectionName("GS-3965-Collection2-Upsert" + date.getTime());
		String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
		Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
		String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS-3984-DataloadJob.txt")), JobInfo.class);
		File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
		DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
		NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
		Assert.assertTrue(nsResponseObj1.isResult());
		
		RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS-3984-Input.json"),RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		for (FieldMapping fields1 : loadToSFDCAction1.getFieldMappings()) {
			fields1.setSourceObject(collectionName1);
		}
		rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction1, JsonNode.class));
		rulesPojo1.getSetupRule().setSelectObject(collectionName1);
		rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
		Log.debug("Updated Pojo/json object is " +mapper.writeValueAsString(rulesPojo1));	
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo1);
		Assert.assertFalse(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule execution should be partially failed because of junk data");
		
		//Asserting expected data and actual data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS-3984-ExpectedJob.txt"),JobInfo.class));		
		List<Map<String, String>> expectedData1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS3984-ExpectedData2.csv");
		List<Map<String, String>> actualData1 = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS3984-ActualData.csv");
		List<Map<String, String>> differenceData1 = Comparator.compareListData(expectedData1, actualData1);
		Log.info("Actual : " + mapper.writeValueAsString(actualData1));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData1));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData1));
		Assert.assertEquals(differenceData1.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
	}
	
	@TestInfo(testCaseIds = { "GS-3966" })
	@Test(description = "load to sfdc action(insert operation) with picklist and multipicklist, lookup datatypes from sfdc object")
	public void loadToSfdcInsertOperationUsingNativeData() throws Exception{
		sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM C_Custom__c];"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3966/GS-3966-DataloadJob.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3966/GS-3966-Input.json"),RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule execution is failed, kindly check");
		
		//Asserting expected data and actual data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3966/GS-3966-ActualJob.txt"),JobInfo.class));		
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3966/ExpectedData.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3966/GS3966-ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected data and actual data");
	}
}
