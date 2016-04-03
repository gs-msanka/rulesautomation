package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
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
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.annotations.TestInfo;

import static org.testng.Assert.assertTrue;

/**
 * Class which contains testcases related to calculated fields in
 * rulesengine(setup rule page) by using REDSHIFT as source data(calculated measures, Mda Joins and regular measure fields)
 * 
 * @author Abhilash Thaduka
 *
 */
public class CalculatedFieldsAndMeasuresTestUsingRedShiftAsSourceData extends BaseTest {

	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup2.apex";
	private NSTestBase nsTestBase = new NSTestBase();
	private ObjectMapper mapper = new ObjectMapper();
	private DataETL dataETL = new DataETL();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	private RulesUtil rulesUtil = new RulesUtil();
	GSDataImpl gsDataImpl = null;
	private Date date = Calendar.getInstance().getTime();
	private TenantDetails tenantDetails = null;
	private TenantManager tenantManager;
	private static final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM RulesSFDCCustom__c];";
	MongoDBDAO mongoDBDAO = null;
	private String collectionName1;
	private String collectionName2;
   
	@BeforeClass
	@Parameters("dbStoreType")
	public void setup(@Optional String dbStoreType) throws Exception {
		basepage.login();
		nsTestBase.init();
		tenantManager = new TenantManager();
		tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
		String tenantId = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId();
		tenantDetails = tenantManager.getTenantDetail(null, tenantId);
		if (StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Mongo")) {
			if(tenantDetails.isRedshiftEnabled()){
				mongoDBDAO = MongoDBDAO.getGlobalMongoDBDAOInstance();
				Log.debug("Tenant Id:"+ tenantId);
				assertTrue(mongoDBDAO.disableRedshift(tenantId), "Failed updating dataStoreType to Mongo.");
			}
		}
		else if(StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Redshift")){
			if(!tenantDetails.isRedshiftEnabled()) {
				assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails), "Failed updating dataStoreType to Redshift");
			}
		}
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		gsDataImpl = new GSDataImpl(NSTestBase.header);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt"));
		// Loading testdata at class level in setup
		boolean isLoadTestDataGlobally = true;
		if (isLoadTestDataGlobally) {
			loadSetupData();
		}
	}

	@BeforeMethod
	public void cleanup() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
	}

	@TestInfo(testCaseIds = { "GS-4200", "GS-4232", "GS-5018" })
	@Test(description = "Test case to verify if the calculated Fields for comparision calculation type with percentage is working fine when Fields A and B are from Show Fields")
	public void testCalculatedFields() throws Exception {

		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-Input-Redshift.json"),RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");

		// Verifying the agrregated field values with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-ExpectedJob.txt"),JobInfo.class));
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/ExpectedData-4.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.debug("Actual : " + mapper.writeValueAsString(actualData));
		Log.debug("Expected : " + mapper.writeValueAsString(expectedData));
		Log.debug("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}

	@TestInfo(testCaseIds = { "GS-4236" })
	@Test(description = "Test case to verify if the calculated Fields for comparision calculation type with Actual Value is working fine when Fields A and B are from Show Fields")
	public void testCalculatedFields2() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-Input-Redshift.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the calculated field data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/ExpectedData-4.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.debug("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.debug("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.debug("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}

	@TestInfo(testCaseIds = { "GS-4048", "GS-4043", "GS-4044", "GS-4042" })
	@Test(description = "Calculated field with AVERAGE as the aggregation over time with adjust for missing data option and also COUNT and COUNT_DISTINCT for weekly granualrity")
	public void testCalculatedFields3() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-Input-Redshift.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field values with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-ExpectedJob-Postgres.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/ExpectedData-4.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.debug("Actual : " + mapper.writeValueAsString(actualData));
		Log.debug("Expected : " + mapper.writeValueAsString(expectedData));
		Log.debug("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	@TestInfo(testCaseIds = { "GS-4045" })
	@Test(description = "Creating a Calculated field with MAX as aggregation with Weekly Granularity")
	public void testCalculatedFields4() throws Exception {		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-Input-Redshift.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field vales with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-ExpectedJob-Postgres.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/ExpectedData-4.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.debug("Actual : " + mapper.writeValueAsString(actualData));
		Log.debug("Expected : " + mapper.writeValueAsString(expectedData));
		Log.debug("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	@TestInfo(testCaseIds = { "GS-4046", "GS-4047", "GS-6062", "GS-4742", "GS-4774", "GS-4775", "GS-5019", "GS-9063" })
	@Test(description = "Creating a Calculated field - Aggregation over time  with SUM,MIN as aggregation with Weekly Granularity and verifying same calculated fields are available or not in advanced criteria")
	public void testCalculatedFields5() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-Input-Redshift.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));	
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field values with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-ExpectedJob-Postgres.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/ExpectedData-4.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.debug("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.debug("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.debug("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	@TestInfo(testCaseIds = { "GS-4237"})
	@Test(description = "Test case to verify if user is able to create a Calculated fields with weekly granularity With Field A from showfield and field B is aggregated value")
	public void testCalculatedFields6() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/GS-4237-Input-Redshift.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field values with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/GS-4237-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/ExpectedData-4.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.debug("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.debug("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.debug("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
   
	@TestInfo(testCaseIds = { "GS-4238"})
	@Test(description = "Test case to verify if user is able to create a Calculated fields with weekly granularity With Field A from aggregated field and Field B from show field")
	public void testCalculatedFields7() throws Exception {		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/GS-4238-Input-Redshift.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field values with the expected data and actual data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/GS-4238-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/ExpectedData-4.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.debug("Actual : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualData));
		Log.debug("Expected : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedData));
		Log.debug("Difference is : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");	
	}
	
	@TestInfo(testCaseIds = { "GS-4204", "GS-4235" })
	@Test(description = "Test case to verify if the calculation type is comparision when Both fields are aggregated over time for  AVG, COUNT over weekly granularity")
	public void testCalculatedFields8() throws Exception {		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-Input-Redshift1.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));			
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field values with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/ExpectedData-7.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4239" })
	@Test(description = "Test case to verify if the calculation type is Actual Value when Both fields are aggregated over time with weekly configuration for AVG and AVG(adjust for missing data)")
	public void testCalculatedFields9() throws Exception {		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-Input-Redshift1.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field values with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/ExpectedData-7.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
	
	@TestInfo(testCaseIds = { "GS-4234" })
	@Test(description = "Test case tries to verify if the calculation for Comparative fields with percentage is working fine when Field A is from Aggregated Fields and field B is from Show Field over weekly granularity")
	public void testCalculatedFields10() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/GS-4234-Input-Redshift.json"), RulesPojo.class);
		rulesPojo.getSetupRule().setJoinOnCollection(collectionName2);
		rulesPojo.getSetupRule().setJoinWithCollection(collectionName1);
		rulesPojo.getSetupRule().setSelectObject(collectionName2);
		rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName2);
		Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));		
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing failed, check rule execution attachement for more details");
		
		//Verifying the agrregated field values with the expected data and actual aggregated data
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/GS-4234-ExpectedJob.txt"),JobInfo.class));	
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/ExpectedData-3.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Actual : " + mapper.writeValueAsString(actualData));
		Log.info("Expected : " + mapper.writeValueAsString(expectedData));
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above for which the aggregated data is not matching");
	}
		

	private void loadSetupData() throws Exception {
		// creating collection1
		CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchemaUsingRedShiftSource1.json")),CollectionInfo.class);
		collectionInfo1.getCollectionDetails().setCollectionName("collection1" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo1);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);

		// Adding redshift calculated measures for collection1 and updating collection schema
		List<CollectionInfo.Column> columnList = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/Columns.json")),new TypeReference<ArrayList<CollectionInfo.Column>>() {});
		actualCollectionInfo1.getColumns().addAll(columnList);
		CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo1, null);
		NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo1));
		Log.info(mapper.writeValueAsString(actualCollectionInfo1));
		Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
		actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId);
		collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();
		Log.info("collection/Table 1 - is " + collectionName1);

		// creating collection2
		CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchemaUsingRedShiftSource2.json")),CollectionInfo.class);
		collectionInfo2.getCollectionDetails().setCollectionName("collection2" + date.getTime());
		String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
		Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
		collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
		Log.info("collection/Table 2 - is " + collectionName2);

		// Forming lookup object "T2-ID" on collection2 with "ID" on collection1 and updating collection
		CollectionUtil.setLookUpDetails(actualCollectionInfo2, "T2_ID",actualCollectionInfo1, "ID", false);
		NsResponseObj nsResponseObj1 = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo2));
		Assert.assertTrue(nsResponseObj1.isResult(), "Collection update failed");

		// loading data into collection/table 1
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob3.txt"),JobInfo.class));
		JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")),JobInfo.class);
		File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
		DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, new String[] { "ID", "AccountName","CustomDate1", "CustomNumber1", "CustomNumber2","CustomNumberWithDecimals1","CustomNumberWithDecimals2" },DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
		NsResponseObj nsResponseObj2 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
		Assert.assertTrue(nsResponseObj2.isResult(), "Data is not loaded, please check log for more details"); 

		// loading data into collection/table 2
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob4.txt"),JobInfo.class));
		JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob2.txt")),JobInfo.class);
		File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1,date);
		DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo2, new String[] { "T2_ID","T2_AccountName", "T2_CustomDate1", "T2_CustomNumber1","T2_CustomNumber2", "T2_CustomNumberWithDecimals1","T2_CustomNumberWithDecimals2" },DataLoadOperationType.INSERT);
		Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1), "Data is not valid");
		NsResponseObj nsResponseObj3 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
		Assert.assertTrue(nsResponseObj3.isResult(), "Data is not loaded, please check log for more details");
	}
}

