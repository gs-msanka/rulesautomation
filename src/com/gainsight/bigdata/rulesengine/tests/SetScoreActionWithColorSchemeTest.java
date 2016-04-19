/**
 * 
 */
package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.FileUtil;
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
public class SetScoreActionWithColorSchemeTest extends BaseTest{
	
	private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
	private static final String METRICS_CREATE_FILE =  Application.basedir + "/apex_scripts/scorecard/Create_ScorecardMetrics.apex";
	private static final String SCORECARD_CLEAN_FILE = Application.basedir + "/apex_scripts/scorecard/Scorecard_CleanUp.txt";
	private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
	private static final String COLOR_SCHEME_FILE = Application.basedir + "/apex_scripts/scorecard/Scorecard_enable_color.apex";
	private RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
	private NSTestBase nsTestBase = new NSTestBase();
	private DataETL dataETL = new DataETL();
	private ObjectMapper mapper = new ObjectMapper();
	private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
	private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
	private AdminScorecardSection adminScorecardSection;
	private RulesUtil rulesUtil = new RulesUtil();
	private String scoreCardDomain;
	private String rulesManagerPageUrl;
	private RulesManagerPage rulesManagerPage;
	
	@BeforeClass
	public void setup() throws Exception {
		nsTestBase.init();
		scoreCardDomain = visualForcePageUrl + "scorecardsetup";
		rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		metaUtil.createExtIdFieldForScoreCards(sfdc);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(SCORECARD_CLEAN_FILE));
		adminScorecardSection=new AdminScorecardSection("test");	
		adminScorecardSection.openScoreCardSectionPage(scoreCardDomain);
		adminScorecardSection.enableScorecard();
		runMetricSetup(METRICS_CREATE_FILE, "Color");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(COLOR_SCHEME_FILE));	
		rulesConfigureAndDataSetup.createCustomObjectAndFields();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
		sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
		JobInfo jobInfo = mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3385/GS-3385-Job-LoadDataIntoCustomObject.txt"),JobInfo.class);
		dataETL.execute(jobInfo);
	}

	@BeforeMethod
	public void cleanup() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_SCRIPT));
		// Deleting scores for previous data
		sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__ScorecardFact__c];"));

	}
	
	@TestInfo(testCaseIds = { "GS-3385" })
	@Test(description = "Set score action with color scheme enabled")
	public void setScoreWithColorSchemeEnabled() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3385/GS-3385-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule Processing Failed, check rule attachment for more details");
		
		//Verifying color scheme in hexadecimal format for the customers where the rule criteria satisfied.
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3385/GS-3385-ExpectedJob-ColorScheme.txt"),JobInfo.class));
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3385/GS-3825-ExpectedDataForColorScheme.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3385/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
	}
	
	@TestInfo(testCaseIds = { "GS-3825" })
	@Test(description = "Set static score with color scheme and comments using scorecard slider")
	public void setScoreWithStaticvalueWithColorScheme() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3825/GS-3825-InputForColorScheme.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing Failed, Check rule execution attachment for more details");
		
		//Verifying color scheme in hexadecimal format for the customers where the rule criteria satisfied.
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3825/GS-3825-ExpectedJobForColorScheme.txt"),JobInfo.class));
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3825/GS3825-ExpectedDataForColorSchema.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3825/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
	}
	
	@TestInfo(testCaseIds = { "GS-3837" })
	@Test(description = "Set score with color scheme from aggregated field")
	public void setScoreUsingAggregatedFieldWithColorScheme() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3837/GS-3837-Input.json"), RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule processing Failed, Check rule execution attachment for more details");
		
		//Verifying scores for the customers satisfying the rule criteria
		dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3837/GS-3837-ExpectedJob-For-ColorScheme.txt"),JobInfo.class));
		List<Map<String, String>> expectedData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3837/GS-3837-ExpectedDataForColorSchema.csv");
		List<Map<String, String>> actualData = Comparator.getParsedCsvDataWithHeaderNamespaceResolved(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3837/ActualData.csv");
		List<Map<String, String>> differenceData = Comparator.compareListData(expectedData, actualData);
		Log.info("Difference is : " + mapper.writeValueAsString(differenceData));
		Assert.assertEquals(differenceData.size(), 0, "Check the Diff above which is not matching between expected testdata from csv and actual data from csv");
	}

}
