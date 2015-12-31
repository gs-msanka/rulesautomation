package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToSFDCAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.annotations.TestInfo;

/**
 * @author Abhilash Thaduka
 *
 */
public class CalculatedFieldsAndMeasuresTestUsingMatrixData extends BaseTest{
	
	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup2.apex";
	private NSTestBase nsTestBase = new NSTestBase();
	private ObjectMapper mapper = new ObjectMapper();
	private DataETL dataETL = new DataETL();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	private  RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	private RulesUtil rulesUtil = new RulesUtil();
	GSDataImpl gsDataImpl = null;
	private Date date = Calendar.getInstance().getTime();
	private TenantDetails tenantDetails = null;
	private TenantManager tenantManager;
	private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM RulesSFDCCustom__c];";
	MongoDBDAO mongoDBDAO = null;
	private static DBStoreType dataBaseType;
	
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
		}else if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.REDSHIFT.name())) {
			Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
		}else if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.POSTGRES.name())) {
			dataBaseType = DBStoreType.valueOf(dbStoreType);
			mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(),Integer.valueOf(nsConfig.getGlobalDBPort()),nsConfig.getGlobalDBUserName(),nsConfig.getGlobalDBPassword(),nsConfig.getGlobalDBDatabase());
			TenantDetails.DBDetail schemaDBDetails = null;
			schemaDBDetails = mongoDBDAO.getSchemaDBDetail(tenantDetails.getTenantId());		 
			if (schemaDBDetails == null || schemaDBDetails.getDbServerDetails() == null || schemaDBDetails.getDbServerDetails().get(0) == null) {
				throw new RuntimeException("DB details are not correct, please check it.");
			}
			Log.info("Connecting to schema db....");
			mongoDBDAO = new MongoDBDAO(schemaDBDetails.getDbServerDetails().get(0).getHost().split(":")[0], 27017, schemaDBDetails.getDbServerDetails().get(0).getUserName(), schemaDBDetails.getDbServerDetails().get(0).getPassword(),schemaDBDetails.getDbName());
		}
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		gsDataImpl = new GSDataImpl(NSTestBase.header);
		rulesConfigureAndDataSetup.createCustomObjectAndFieldsInSfdc();
		rulesConfigureAndDataSetup.createDataLoadConfiguration();
	}

	@BeforeMethod
	public void cleanup() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
	}
	
	@TestInfo(testCaseIds = { "GS-4200" })
	@Test(description = "Test case to verify if the calculated Fields for comparision calculation type with percentage is working fine when Fields A and B are from Show Fields")
	public void testCalculatedFields() throws Exception {
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-4200" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		// Updating DB storage type to postgres from back end.
		if (dataBaseType == DBStoreType.POSTGRES) {
			Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId,DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
		}	
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");	
		
		// Setting the collectionName value to the source object selection and others in input json
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-Input-Postgres.json"), RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		loadToSFDCAction.getFieldMappings().get(1).setSourceObject(collectionName);
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));
				
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/ExpectedData-2.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	
	@TestInfo(testCaseIds = { "GS-4236" })
	@Test(description = "Test case to verify if the calculated Fields for comparision calculation type with Actual Value is working fine when Fields A and B are from Show Fields")
	public void testCalculatedFields2() throws Exception {
		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-4236" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		// Updating DB storage type to postgres from back end.
		if (dataBaseType == DBStoreType.POSTGRES) {
			Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId,DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
		}
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");	
		
		// Setting the collectionName value to the source object selection and others in input json
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-Input-Postgres.json"), RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		loadToSFDCAction.getFieldMappings().get(1).setSourceObject(collectionName);
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));
				
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/ExpectedData-2.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	@TestInfo(testCaseIds = { "GS-4048", "GS-4043", "GS-4044", "GS-4042" })
	@Test(description = "Calculated field with AVERAGE as the aggregation over time with adjust for missing data option and also COUNT and COUNT_DISTINCT for weekly granualrity")
	public void testCalculatedFields3() throws Exception {
	// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-4048" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		// Updating DB storage type to postgres from back end.
		if (dataBaseType == DBStoreType.POSTGRES) {
			Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId,DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
		}
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");	
		
		// Setting the collectionName value to the source object selection and others in input json
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-Input-Postgres.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));			
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-ExpectedJob-Postgres.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/ExpectedData-2.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	
	@TestInfo(testCaseIds = { "GS-4045" })
	@Test(description = "Creating a Calculated field with MAX as aggregation with Weekly Granularity")
	public void testCalculatedFields4() throws Exception {		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-4045" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		// Updating DB storage type to postgres from back end.
		if (dataBaseType == DBStoreType.POSTGRES) {
			Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId,DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
		}	
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");	
		
		// Setting the collectionName value to the source object selection and others in input json
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-Input-Postgres.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));			
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-ExpectedJob-Postgres.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/ExpectedData-2.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	@TestInfo(testCaseIds = { "GS-4046", "GS-4047", "GS-6062" })
	@Test(description = "Creating a Calculated field - Aggregation over time  with SUM,MIN as aggregation with Weekly Granularity adn verifying same calculated fields are available or not in advanced criteria")
	public void testCalculatedFields5() throws Exception {		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-4046" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		// Updating DB storage type to postgres from back end.
		if (dataBaseType == DBStoreType.POSTGRES) {
			Assert.assertTrue(mongoDBDAO.updateCollectionDBStoreType(tenantDetails.getTenantId(), collectionId,DBStoreType.POSTGRES), "Failed while updating the DB store type to postgres");
		}	
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");	
		
		// Setting the collectionName value to the source object selection and others in input json
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-Input-Postgres.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));			
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-ExpectedJob-Postgres.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/ExpectedData-2.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*@TestInfo(testCaseIds = { "GS-4232" })
	@Test(description = "Test case to verify using MDA Subject Area if the calculated Fields for comparision calculation type with percentage is working fine when Fields A and B are from Show Fields")
	public void testCalculatedFieldsUsingMatrixData() throws Exception {
			
		// Setting the collectionName value to the source object selection and others in input json
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4232/GS-4232-Input.json"), RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		loadToSFDCAction.getFieldMappings().get(1).setSourceObject(collectionName);
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));
				
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4232/GS-4232-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4232/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4232/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4234" })
	@Test(description = "Test case tries to verify if the calculation for Comparative fields with percentage is working fine when Field A is from Aggregated Fields and field B is from Show Field using matrix data")
	public void testCalculatedFieldsWithAggregatedAndShowFieldUsingMatrixData() throws Exception {
		
		// Creating collection with data as source collection
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4232/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-4234-" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4232/DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj.isResult());
		
		// Setting the collectionName value to the source object selection and others in input json
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/GS-4234-Input.json"), RulesPojo.class);
		LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		loadToSFDCAction.getFieldMappings().get(1).setSourceObject(collectionName);
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));
				
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/GS-4234-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4774", "GS-4775" })
	@Test
	public void createCalculatedFieldsOnRedshiftSubectArea() throws Exception {
		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4774/CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-4774" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
	
		List<CollectionInfo.Column> columnList = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4774/Columns.json")),new TypeReference<ArrayList<CollectionInfo.Column>>() {});
		actualCollectionInfo.getColumns().addAll(columnList);
		CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo, null);
		NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
		Log.info(mapper.writeValueAsString(actualCollectionInfo));
		Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
		actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
		Log.info("collectionName is " +collectionName);	
		
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"ID", "Name", "PageViews", "totaldownloads","overallpayments","uniquedownloads"}, DataLoadOperationType.INSERT);
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4774/DataloadJob.txt")), JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
		NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj1.isResult(), "Data load is failed");
		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4774/Inputdata.json"), RulesPojo.class);	
		LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
		for (FieldMapping fields : loadToSFDCAction.getFieldMappings()) {
			fields.setSourceObject(collectionName);
		}
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
		rulesPojo.getSetupRule().setSelectObject(collectionName);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
		Log.debug("Updated Pojo is" +mapper.writeValueAsString(rulesPojo));	
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed");
			
		// Verifying the agrregated field values with the expected data and actual data for calculated Formula fields
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4774/GS-4774-ExpectedJob.txt"), JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4774/ExpectedData.csv")));
		List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4774/ActualData.csv")));
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which calculated fields/Formula fields data is not matching");
	}
*/
	
	@AfterClass
	public void tearDown() {
		if (mongoDBDAO != null) {
			mongoDBDAO.mongoUtil.closeConnection();
		}
	}
}
