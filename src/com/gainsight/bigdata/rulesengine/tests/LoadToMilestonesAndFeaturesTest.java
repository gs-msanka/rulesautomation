package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToFeatureAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMileStoneAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;

/**
 * @author Abhilash Thaduka
 *
 */
public class LoadToMilestonesAndFeaturesTest extends BaseTest {

	private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
	private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
	private static final String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts.txt";
	private static final String DELETE_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
	private static final String LOAD_DATA_INTO_CUSTOMOBJECT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Load_Data_Into_CustomObject.txt";
	RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	private ObjectMapper mapper = new ObjectMapper();
	private RulesUtil rulesUtil = new RulesUtil();
	NSTestBase nsTestBase = new NSTestBase();
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	DataETL dataETL = new DataETL();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;

	@BeforeClass
	public void setUpData() throws Exception {
		/*
		 * Commented below browser instantiation and sfdc connections, since
		 * same are invoked in before test, need to uncomment when we run only
		 * this particular class
		 */
		// basepage.login();
		// sfdc.connect();
		// nsTestBase.init();
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		rulesConfigureAndDataSetup.createCustomObjectAndFields();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
		dataETL.execute(jobInfo);
		JobInfo loadData = mapper.readValue((new FileReader(LOAD_DATA_INTO_CUSTOMOBJECT)), JobInfo.class);
		dataETL.execute(loadData);
	}
	
    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(DELETE_RULES));
    }


	@TestInfo(testCaseIds = { "GS-3699", "GS-3700"})
	@Test
	public void testLoadToMileStoneActionWithCustomDate() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC34.json"), RulesPojo.class);
	    LoadToMileStoneAction loadToMileStoneAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToMileStoneAction.class);
		String actualDays=loadToMileStoneAction.getMilestoneDate().getDateFieldValue();
		loadToMileStoneAction.getMilestoneDate().setDateFieldValue(
				getDateWithFormat(Integer.valueOf(loadToMileStoneAction.getMilestoneDate().getDateFieldValue()), 0, false));
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToMileStoneAction, JsonNode.class));
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");	
		loadToMileStoneAction.getMilestoneDate().setDateFieldValue(
				DateUtil.getDateWithFormat(Integer.valueOf(actualDays), 0, userTimezone, RULES_DATE_FORMAT));
		rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToMileStoneAction, JsonNode.class));	
		SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
		SObject[] records = sfdc
				.getRecords((resolveStrNameSpace(setupRuleActionPage
						.queryString(rulesPojo.getSetupActions().get(0)
								.getCriterias()))));
		String names =getAccountNamesAsStringFromAccountObject(records,"Name",null,false);		
        // C_Checkbox__c,Number_Auto__c, Name are the tokens used, so querying same fields
		SObject[] milestoneRecords=sfdc.getRecords("SELECT Id,C_Checkbox__c,Number_Auto__c, Name from Account where name in ("+names+") and isDeleted=false");
		for (SObject milestoneRecord : milestoneRecords) {
			String accountName =(String) milestoneRecord.getField("Name");
			String actualTokenComments = (String) milestoneRecord.getField("C_Checkbox__c")+milestoneRecord.getField("Number_Auto__c")+
					milestoneRecord.getField("Name");
			Assert.assertTrue(rulesUtil.isMileStoneCreatedSuccessfully(loadToMileStoneAction
					.getSelectMilestone(), loadToMileStoneAction
					.getMilestoneDate().getDateFieldValue(), actualTokenComments, accountName), "Check Milestone is created with correct confifuration or not");		
		}
		
		// GS-3700 testcase starts here
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		for (SObject milestoneRecord : milestoneRecords) {
			String accountName =(String) milestoneRecord.getField("Name");
			String actualTokenComments = (String) milestoneRecord.getField("C_Checkbox__c")+milestoneRecord.getField("Number_Auto__c")+
					milestoneRecord.getField("Name");
			Assert.assertTrue(rulesUtil.isMileStoneCreatedSuccessfully(loadToMileStoneAction
					.getSelectMilestone(), loadToMileStoneAction
					.getMilestoneDate().getDateFieldValue(), actualTokenComments+"\n"+" "+actualTokenComments, accountName), "Check Milestone is created with correct confifuration or not");		
		}
	}
	
	@TestInfo(testCaseIds = {"GS-3701"})
	@Test
	public void testLoadToMileStoneActionUsingDateField() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC35.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
		LoadToMileStoneAction loadToMileStoneAction = mapper.readValue(
				rulesPojo.getSetupActions().get(0).getAction(), LoadToMileStoneAction.class);	
		SObject[] records2 = sfdc
				.getRecords((resolveStrNameSpace(setupRuleActionPage
						.queryString(rulesPojo.getSetupActions().get(0)
								.getCriterias()))));
		String names2 =getAccountNamesAsStringFromAccountObject(records2,"Name",null,false);
		SObject[] accountRecords2=sfdc.getRecords("SELECT Id,Date_Auto__c, Name from Account where name in ("+names2+") and isDeleted=false");
		for (SObject accountRecord2 : accountRecords2) {
			String accountName = (String) accountRecord2.getField("Name");
			String formattedDate;
			formattedDate = (String) accountRecord2.getField("Date_Auto__c");
			Assert.assertTrue(rulesUtil.isMileStoneCreatedSuccessfully(loadToMileStoneAction
					.getSelectMilestone(), formattedDate, loadToMileStoneAction.getComments(), accountName), "Check Milestone is created with correct confifuration or not");
		}
	}
	
	@TestInfo(testCaseIds = {"GS-3703"})
	@Test
	public void testLoadToMileStoneActionUsingDateTimeField() throws Exception{		
	    RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC36.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()),
				"Check whether Rule ran successfully or not !");
		SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
		LoadToMileStoneAction loadToMileStoneAction = mapper.readValue(
				rulesPojo.getSetupActions().get(0).getAction(), LoadToMileStoneAction.class);
		SObject[] records3 = sfdc
				.getRecords((resolveStrNameSpace(setupRuleActionPage
						.queryString(rulesPojo.getSetupActions().get(0)
								.getCriterias()))));
		String names3 = getAccountNamesAsStringFromAccountObject(records3,"Name",null,false);
		SObject[] accountRecords3 = sfdc
				.getRecords("SELECT Id,DateTime_Auto__c, Name from Account where name in ("
						+ names3 + ") and isDeleted=false");
		for (SObject accountRecord3 : accountRecords3) {
			String accountName = (String) accountRecord3.getField("Name");
			String dateTime = (String) accountRecord3
					.getField("DateTime_Auto__c");
			String dateString = dateTime.substring(0, dateTime.indexOf('T'));
			Log.info("Date from DateTime field is " + dateString);
			Assert.assertTrue(rulesUtil.isMileStoneCreatedSuccessfully(
					loadToMileStoneAction.getSelectMilestone(), dateString, loadToMileStoneAction.getComments(), accountName),
					"Check Milestone is created with correct confifuration or not");
		}	
	}
	
	@TestInfo(testCaseIds = {"GS-3708"})
	@Test
	public void testLoadToFeaturesAction2() throws Exception{		
	    RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC38.json"), RulesPojo.class);
	    LoadToFeatureAction loadToFeatureAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToFeatureAction.class);
	    rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		// Targeting 1 customer -> 2 records belong to one account where one record has account lookup as null and other record with valid lookup
		// Below query will run in heroku app, to find out how many accounts satisfying the criteria given in rule, so below query is hardcoded based upon testdata
	    SObject[] accounts=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c FROM C_Custom__c where (C_lookup__r.Data_ExternalId__c='RULESUI Account 3'  or rules_c_Text__c='RULESUI Account 3 Text 2')");
	    String names = getAccountNamesAsStringFromAccountObject(accounts,"C_lookup__r","Data_ExternalId__c",true); 
	    SObject[] temp=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c FROM C_Custom__c where C_lookup__r.Data_ExternalId__c in ("+names+")");
	    for (SObject sObject : temp) {
			Assert.assertTrue((rulesUtil.isFeatureCreatedSuccessfully(loadToFeatureAction, sObject.getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString())),
					"Check cta is created correctly or not with given configuration");		
		}
	    // Finally Asserting number of records created also, for this scenario/testdata
	    Assert.assertEquals(accounts.length, sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__CustomerFeatures__c where isDeleted=false")),
	    		"Check number of features created are satisfying according to the testdata or not !! ");
	}
	
	@TestInfo(testCaseIds = {"GS-3709"})
	@Test
	public void testLoadToFeaturesAction3() throws Exception{		
	    RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC39.json"), RulesPojo.class);
	    LoadToFeatureAction loadToFeatureAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToFeatureAction.class);
	    rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		// Targeting 1 customer -> 2 records belong to one account where one record has account lookup as null and other record with valid lookup
		// Below query will run in heroku app, to find out how many accounts satisfying the criteria given in rule, so below query is hardcoded based upon testdata
	    SObject[] accounts=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c FROM C_Custom__c where (C_lookup__r.Data_ExternalId__c='RULESUI Account 3'  or rules_c_Text__c='RULESUI Account 3 Text 2')");
	    String names = getAccountNamesAsStringFromAccountObject(accounts,"C_lookup__r","Data_ExternalId__c",true); 
	    SObject[] temp=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c FROM C_Custom__c where C_lookup__r.Data_ExternalId__c in ("+names+")");
	    for (SObject sObject : temp) {
			Assert.assertTrue((rulesUtil.isFeatureCreatedSuccessfully(loadToFeatureAction, sObject.getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString())),
					"Check cta is created correctly or not with given configuration");		
		}
	    // Finally Asserting number of records created also, for this scenario/testdata
	    Assert.assertEquals(accounts.length, sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__CustomerFeatures__c where isDeleted=false")),
	    		"Check number of features created are satisfying according to the testdata or not !! ");
	}
	
	@TestInfo(testCaseIds = {"GS-3710", "GS-3712"})
	@Test
	public void testLoadToFeaturesAction4() throws Exception{		
	    RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC40.json"), RulesPojo.class);
	    LoadToFeatureAction loadToFeatureAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToFeatureAction.class);
	    rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		// Targeting 1 customer -> 2 records belong to one account where one record has account lookup as null and other record with valid lookup
		// Below query will run in heroku app, to find out how many accounts satisfying the criteria given in rule, so below query is hardcoded based upon testdata
	    SObject[] accounts=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c FROM C_Custom__c where (C_lookup__r.Data_ExternalId__c='RULESUI Account 3'  or rules_c_Text__c='RULESUI Account 3 Text 2')");
	    String names = getAccountNamesAsStringFromAccountObject(accounts,"C_lookup__r","Data_ExternalId__c",true); 
	    SObject[] temp=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c,rules_c_Checkbox__c,rules_c_Number__c,rules_c_Text__c FROM C_Custom__c where C_lookup__r.Data_ExternalId__c in ("+names+")");
	    for (SObject sObject : temp) {
	    	// 3 token are added in testdata, so querying same fields to form comments at runtime
	    	String tokenComments=(String)sObject.getField("rules_c_Checkbox__c")+sObject.getField("rules_c_Number__c")+sObject.getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString();
	    	loadToFeatureAction.setComments(tokenComments);
	    	loadToFeatureAction.getLicensed().setUpdateType((String) sObject.getField("rules_c_Checkbox__c"));
	    	loadToFeatureAction.getEnabled().setUpdateType((String) sObject.getField("rules_c_Checkbox__c"));
	    	rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToFeatureAction, JsonNode.class));
			Assert.assertTrue((rulesUtil.isFeatureCreatedSuccessfully(loadToFeatureAction, sObject.getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString())),
					"Check cta is created correctly or not with given configuration");
			// TestCase GS-3712 starts here
			// Running rule for second time for - update existing features scenario
			Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
			loadToFeatureAction.setComments(tokenComments+"\n"+" "+tokenComments);
			rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToFeatureAction, JsonNode.class));
			Assert.assertTrue((rulesUtil.isFeatureCreatedSuccessfully(loadToFeatureAction, sObject.getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString())),
					"Check cta is created correctly or not with given configuration");
		}
	    // Finally Asserting number of records created also, for this scenario/testdata
	    Assert.assertEquals(accounts.length, sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__CustomerFeatures__c where isDeleted=false")),
	    		"Check number of features created are satisfying according to the testdata or not !! ");
	}
	
	@TestInfo(testCaseIds = {"GS-3707","GS-3711"})
	@Test
	public void testLoadToFeaturesAction1() throws Exception{		
	    RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC37.json"), RulesPojo.class);
	    LoadToFeatureAction loadToFeatureAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToFeatureAction.class);
	    rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
		
		// Targeting 2 customers -> 2 records belong to one account and other one record belongs to other other customer(lookup with value null is also included)
		// Below query will run in heroku app, to find out how many accounts satisfying the criteria given in rule
	    SObject[] accounts=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c FROM C_Custom__c where (C_lookup__r.Data_ExternalId__c='RULESUI Account 2' or rules_c_ExternalID__c='RULESUI Account 3' or rules_c_Text__c='RULESUI Account 3 Text 2') and C_lookup__r.Data_ExternalId__c!=null group by C_lookup__r.Data_ExternalId__c");
	    List<String> commentsList = new ArrayList<String>();
	    for (SObject sObject : accounts) {
			SObject[] temp=sfdc.getRecords("SELECT C_lookup__r.Data_ExternalId__c,rules_c_Checkbox__c,rules_c_Number__c,rules_c_Text__c FROM C_Custom__c where C_lookup__r.Data_ExternalId__c='"+sObject.getField("Data_ExternalId__c")+"'");
			if (temp.length==1) {
				String comments=(String)temp[0].getField("rules_c_Checkbox__c")+temp[0].getField("rules_c_Number__c")+temp[0].getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString();
				loadToFeatureAction.setComments(comments);
				rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToFeatureAction, JsonNode.class));
				Assert.assertTrue((rulesUtil.isFeatureCreatedSuccessfully(loadToFeatureAction, temp[0].getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString())),
						"Check cta is created correctly or not with given configuration");
			}else {
				SObject sObjectTemp = null;
				for (SObject sObject2 : temp) {
					String checkboxType=(String) sObject2.getField("rules_c_Checkbox__c");
					String numberType=(String) sObject2.getField("rules_c_Number__c");
					String textType=(String) sObject2.getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString();
					commentsList.add(checkboxType+numberType+textType);
					sObjectTemp=sObject2;
				}
				// Appending comments for two accounts and also inserting new line and space for comments, since comments are stored by appending new line and space in sfdc backend
				loadToFeatureAction.setComments(commentsList.get(0)+"\n"+" "+commentsList.get(1));
				rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToFeatureAction, JsonNode.class));
				Assert.assertTrue((rulesUtil.isFeatureCreatedSuccessfully(loadToFeatureAction, sObjectTemp.getChild("C_lookup__r").getChild("Data_ExternalId__c").getValue().toString())),
						"Check cta is created correctly or not with given configuration");
			}
		}
	    // Finally Asserting number of records created also, for this scenario/testdata
	    Assert.assertEquals(accounts.length, sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__CustomerFeatures__c where isDeleted=false")),
	    		"Check number of features created are satisfying according to the testdata or not !! ");
	}
	
	private String getAccountNamesAsStringFromAccountObject(SObject[] records, String accountIdentifier1, String accountIdentifier2, boolean isRelation){
		List<String> accounts = new ArrayList<String>();
		String accountNames = "";
		for (SObject record : records) {
			if (!isRelation) {
				accountNames += record.getField(accountIdentifier1) + ",";
			}else {
				accountNames += record.getChild(accountIdentifier1).getChild(accountIdentifier2).getValue().toString() + ",";
			}		
		}
		if (accountNames.endsWith(",")) {
			accountNames = accountNames.substring(0, accountNames.length() - 1);
		}
		List<String> temp = Arrays.asList(accountNames.split(","));
		for (int i = 0; i < temp.size(); i++) {
			accounts.add("'" + temp.get(i) + "'");
		}
		String names = accounts.toString().substring(
				accounts.toString().indexOf("'"),
				accounts.toString().length() - 1);
		return names;		
	}
}
