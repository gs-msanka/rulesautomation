package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.utils.annotations.TestInfo;

/**
 * @author Abhilash Thaduka
 *
 */
public class LoadToCustomersTest extends BaseTest {

	private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
	private static final String CREATE_ACCOUNTS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_For_LoadToCustomersAction.txt";
	private static final String ACCOUNTS_JOB_FOR_LOAD_TO_CUSTOMERS_ACTION = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts_For_Load_to_Customers_Action.txt";
	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
	private RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	private ObjectMapper mapper = new ObjectMapper();
	private NSTestBase nsTestBase = new NSTestBase();
	private RulesUtil rulesUtil = new RulesUtil();
	DataETL dataETL = new DataETL();
	private RulesManagerPage rulesManagerPage;
	private String rulesManagerPageUrl;

	@BeforeClass
	public void setUpData() throws Exception {
		basepage.login();
		sfdc.connect();
		nsTestBase.init();
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS));
		JobInfo jobInfo = mapper.readValue((new FileReader(ACCOUNTS_JOB_FOR_LOAD_TO_CUSTOMERS_ACTION)), JobInfo.class);
		dataETL.execute(jobInfo);
	}
	
	@BeforeMethod
	public void CleanUpCustomers(){
		sfdc.runApexCode(resolveStrNameSpace("delete [select id from JBCXM__CustomerInfo__c where JBCXM__CustomerName__c  like 'Account RULESUI%'];"));
		
	}
	@TestInfo(testCaseIds = {"GS-3149"})
    @Test()
    public void testLoadToCustomersWithNativeData() throws Exception {
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC41.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
    }
}
