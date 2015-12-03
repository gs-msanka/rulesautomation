package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToFeatureAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDACollection;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.http.Header;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.annotations.TestInfo;

/**
 * @author Abhilash Thaduka
 *
 */
public class LoadToMdaTest extends BaseTest {

	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
	private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
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
	
	
	@BeforeClass
	public void setup() throws Exception {
		nsTestBase.init();
/*		basepage.login();
		sfdc.connect();
		nsTestBase.init();
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();*/
		header.addHeader("Origin", sfdcInfo.getEndpoint());
		header.addHeader("Content-Type", "application/json");
		header.addHeader("appOrgId", sfdcInfo.getOrg());
		header.addHeader("appUserId", sfdcInfo.getUserId());
		header.addHeader("appSessionId", sfdcInfo.getSessionId());
		gsDataImpl = new GSDataImpl(header);
	}
	
/*    @Test
    public void loadToMdaActionUsingNativeData() throws Exception {
        rulesConfigureAndDataSetup.createEmptySubjectArea();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/loadToMdaActionUsingNativeData.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule Created and Ran Successfully!");
        String subjectArea = null;
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        for (RuleAction ruleAction : ruleActions) {
            JsonNode actionObject = ruleAction.getAction();
            LoadToMDAAction loadToMDAAction = mapper.readValue(actionObject, LoadToMDAAction.class);
            subjectArea = loadToMDAAction.getObjectName();
        }
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollection(subjectArea);
        collectionDBName = actualCollectionInfo.getCollectionDetails().getDbCollectionName();
        Log.info(collectionDBName);
        String subjectAreaName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        Log.info("subjectAreaName is " + subjectAreaName);
        collectionNames.add(subjectAreaName);
        JobInfo loadTransform = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/LoadToMda_EpectedDate_Job.txt"), JobInfo.class);
        dataETL.execute(loadTransform);
        String list[] = {"ID", "Name", "Description", "LongTextArea", "AnnualRevenue", "Email"};
        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager
                .createTabularReport(actualCollectionInfo, list)), actualCollectionInfo);
        List<Map<String, String>> expData = Comparator
                .getParsedCsvData(new CSVReader(new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/LoadToMDA-Output.csv")));
        Log.info("Actual : " + mapper.writeValueAsString(actualData));
        Log.info("Expected : " + mapper.writeValueAsString(expData));
        List<Map<String, String>> diffData = Comparator.compareListData(expData, actualData);
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(diffData.size(), 0);
    }*/
	
//	@BeforeMethod
	public void CleanUpCustomers() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));

	}
	
	@TestInfo(testCaseIds = { "GS-3977" })
	@Test
	public void loadToMda1() throws Exception {
		
/*		JobInfo jobInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-3977.txt")), JobInfo.class);
        dataETL.execute(jobInfo);*/
/*		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/GS-3977-DataloadJob.txt")), JobInfo.class);
        dataETL.execute(jobInfo);

		CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-CollectionSchema.json")),CollectionInfo.class);
		collectionInfo.getCollectionDetails().setCollectionName("GS-3977-" + date.getTime());
		String collectionId = gsDataImpl.createCustomObject(collectionInfo);
		Assert.assertNotNull(collectionId, "Collection ID should not be null.");
		CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
		String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();

		LoadToMDACollection loadToMDACollection = new LoadToMDACollection();
		loadToMDACollection.setType("MDA");
		loadToMDACollection.setObjectName(actualCollectionInfo.getCollectionDetails().getCollectionId());
		loadToMDACollection.setObjectLabel(actualCollectionInfo.getCollectionDetails().getCollectionName());
		List<LoadToMDACollection.Field> fields = new ArrayList<>();
		LoadToMDACollection.Field field = null;
		for (CollectionInfo.Column column : actualCollectionInfo.getColumns()) {
			field = new LoadToMDACollection.Field();
			field.setDataType(column.getDatatype().toUpperCase());
			field.setName(column.getDbName());
			fields.add(field);
		}
		loadToMDACollection.setFields(fields);
		rulesUtil.saveCustomObjectInRulesConfig(mapper.writeValueAsString(loadToMDACollection));
		
		
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-Input.json"),RulesPojo.class);
		
		LoadToMDAAction loadToMDAAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
		loadToMDAAction.setObjectName(collectionName);
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction, JsonNode.class));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule Created and Ran Successfully!");
		List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createDynamicTabularReport(actualCollectionInfo)), actualCollectionInfo);*/
        
        DataLoadManager dataLoadManager = new DataLoadManager(sfdcInfo, nsTestBase.getDataLoadAccessKey());
        
        
        List<Map<String, String>> expData = Comparator
                .getParsedCsvData(new CSVReader(new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI_ExpectedData/LoadToMDA3.csv")));
        String list[] = {"Name", "CustomString1", "CustomString2", "CustomeDate1", "CustomDateTime1", "CustomBooleanField1",
        		"CustomBooleanField2","CustomNumber1","CustomNumber2","CustomNumberWithDecimals1","CustomNumberWithDecimals2"};
        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager
                .createTabularReport(dataLoadManager
        				.getCollectionInfo("c6f69523-7bd4-4fdc-a526-e5cf3c820af8"), list)), gsDataImpl.getCollectionMaster("c6f69523-7bd4-4fdc-a526-e5cf3c820af8"));
        Log.info("Actual : " + mapper.writeValueAsString(actualData));
        Log.info("Expected : " + mapper.writeValueAsString(expData));
        List<Map<String, String>> diffData = Comparator.compareListData(expData, actualData);
        Log.info("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(diffData.size(), 0);
	}
}
