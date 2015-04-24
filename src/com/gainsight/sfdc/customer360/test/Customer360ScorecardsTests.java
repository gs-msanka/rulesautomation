package com.gainsight.sfdc.customer360.test;

import java.io.IOException;
import java.util.HashMap;

import com.gainsight.testdriver.Log;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360Scorecard;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

public class Customer360ScorecardsTests extends BaseTest {

	private final String TEST_DATA_FILE     = "testdata/sfdc/scorecards/tests/Grade_Scheme_Data.xls";
    private final String SETUP_FILE         = env.basedir+"/apex_scripts/scorecard/scorecard.apex";
    private final String CLEAN_FILE         = env.basedir+"/apex_scripts/scorecard/Scorecard_CleanUp.txt";
    private final String GRADE_SCHEME_FILE  = env.basedir+"/apex_scripts/scorecard/Scorecard_enable_grade.apex";
    private final String METRICS_CREATE_FILE = env.basedir+"/apex_scripts/scorecard/Create_ScorecardMetrics.apex";
    private String SCHEME = "Grade";

	@BeforeClass
	public void setUp() throws Exception {
        Log.info("Starting Customer 360 Scorecard module Test Cases...");
        metaUtil.createExtIdFieldForScoreCards(sfdc,sfinfo);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SETUP_FILE));
        basepage.login();
        AdministrationBasePage adm = basepage.clickOnAdminTab();
        AdminScorecardSection as = adm.clickOnScorecardSection();
        as.enableScorecard();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(GRADE_SCHEME_FILE));
        try {
            runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("Failed to create metrics for scorecards");
        }
    }
	
	@TestInfo(testCaseIds={"GS-790"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-1")
    public void addScoreToMeasureWithOutWeight(HashMap<String, String> testData) {
        overAllCustomerRollUp(true);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        HashMap<String, String> measure = getMapFromData(testData.get("Measure"));
        customer360Scorecard.setScheme(testData.get("Scheme"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
    }
	
	@TestInfo(testCaseIds={"GS-790"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-2")
    public void addScoreToMeasureWithWeight(HashMap<String, String> testData) {
        overAllCustomerRollUp(true);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        HashMap<String, String> measure = getMapFromData(testData.get("Measure"));
        customer360Scorecard.setScheme(testData.get("Scheme"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
    }

	@TestInfo(testCaseIds={})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-3")
    public void removeScoreForMeasures(HashMap<String, String> testData) {
        overAllCustomerRollUp(true);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));
        HashMap<String, String> measure1 = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> measure2 = getMapFromData(testData.get("Measure2"));
        HashMap<String, String> measure3 = getMapFromData(testData.get("Measure3"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth"));

        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure1.get("GroupName"), measure1.get("MeasureName"), measure1.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure1.get("GroupName"), measure1.get("MeasureName"), measure1.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure1.get("GroupName"), measure1.get("MeasureName"), measure1.get("Score")));

        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure2.get("GroupName"), measure2.get("MeasureName"), measure2.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure2.get("GroupName"), measure2.get("MeasureName"), measure2.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure2.get("GroupName"), measure2.get("MeasureName"), measure2.get("Score")));

        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure3.get("GroupName"), measure3.get("MeasureName"), measure3.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure3.get("GroupName"), measure3.get("MeasureName"), measure3.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure3.get("GroupName"), measure3.get("MeasureName"), measure3.get("Score")));

        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
       //Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));

        customer360Scorecard = customer360Scorecard.removeMeasureScore(measure1.get("GroupName"), measure1.get("MeasureName"));
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure1.get("GroupName"), measure1.get("MeasureName"), "NA"));

        customer360Scorecard = customer360Scorecard.removeMeasureScore(measure2.get("GroupName"), measure2.get("MeasureName"));
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure2.get("GroupName"), measure2.get("MeasureName"), "NA"));

        customer360Scorecard = customer360Scorecard.removeMeasureScore(measure3.get("GroupName"), measure3.get("MeasureName"));
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure3.get("GroupName"), measure3.get("MeasureName"), "NA"));

        Assert.assertTrue(customer360Scorecard.verifyOverallScore("NA"));
    }
	
	@TestInfo(testCaseIds={"GS-791"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-4")
    public void scorecardUpdates(HashMap<String, String> testData) {
        overAllCustomerRollUp(true);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));

        HashMap<String, String> measure = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth1"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure2"));
        customerHealth = getMapFromData(testData.get("CustomerHealth2"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure3"));
        customerHealth = getMapFromData(testData.get("CustomerHealth3"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure4"));
        customerHealth = getMapFromData(testData.get("CustomerHealth4"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure5"));
        customerHealth = getMapFromData(testData.get("CustomerHealth5"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure6"));
        customerHealth = getMapFromData(testData.get("CustomerHealth6"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
    }
	
	@TestInfo(testCaseIds={"GS-789"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-5")
    public void updateMeasureComments(HashMap<String, String> testData) {
        overAllCustomerRollUp(true);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));
        customer360Scorecard = customer360Scorecard.openDetailView();

        HashMap<String, String> measure = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth1"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        customer360Scorecard = customer360Scorecard.updateMeasureComments(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Comments"));
        Assert.assertTrue(customer360Scorecard.verifyCommentsOfMeasure(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Comments")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));

        measure = getMapFromData(testData.get("Measure2"));
        customerHealth = getMapFromData(testData.get("CustomerHealth2"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        customer360Scorecard = customer360Scorecard.updateMeasureComments(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Comments"));
        Assert.assertTrue(customer360Scorecard.verifyCommentsOfMeasure(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Comments")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure3"));
        customerHealth = getMapFromData(testData.get("CustomerHealth3"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        customer360Scorecard = customer360Scorecard.updateMeasureComments(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Comments"));
        Assert.assertTrue(customer360Scorecard.verifyCommentsOfMeasure(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Comments")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
    }

	@TestInfo(testCaseIds={})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-6")
    public void addUpdateRemoveMeasureScore(HashMap<String, String> testData) {
        overAllCustomerRollUp(true);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));

        HashMap<String, String> measure = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth1"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure2"));
        customerHealth = getMapFromData(testData.get("CustomerHealth2"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), false);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureTrend(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        measure = getMapFromData(testData.get("Measure3"));
        customerHealth = getMapFromData(testData.get("CustomerHealth3"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), false);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureTrend(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureColor(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));

        customer360Scorecard.removeMeasureScore(measure.get("GroupName"), measure.get("MeasureName"));
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), "NA"));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore("NA"));
    }

	@TestInfo(testCaseIds={"GS-794"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-7")
    public void updateOverallScoreSummary(HashMap<String, String> testData) {
        overAllCustomerRollUp(true);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));
        String overallScorecardSummary = testData.get("OverallScoreSummary");
        customer360Scorecard = customer360Scorecard.updateCustomerSummary(overallScorecardSummary);
        Assert.assertTrue(customer360Scorecard.verifyOverAllSummary(overallScorecardSummary), "Checking Over All Summary.");
    }

	@TestInfo(testCaseIds={"GS-800"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-8")
    public void disabledOverAllHealthUpdateCheck(HashMap<String, String> testData) {
        overAllCustomerRollUp(false);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));

        String overallScorecardSummary = testData.get("OverallScoreSummary");
        customer360Scorecard = customer360Scorecard.updateCustomerSummary(overallScorecardSummary);
        Assert.assertTrue(customer360Scorecard.verifyOverAllSummary(overallScorecardSummary), "Checking Over All Summary.");

        HashMap<String, String> measure = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));

        measure = getMapFromData(testData.get("Measure2"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), false);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyMeasureTrend(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));

        measure = getMapFromData(testData.get("Measure3"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
    }

	@TestInfo(testCaseIds={"GS-800"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-10")
    public void updateOverAllCustomerHealth(HashMap<String, String> testData) {
        overAllCustomerRollUp(false);
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));

        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth1"));
        customer360Scorecard = customer360Scorecard.updateOverAllScore(customerHealth.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        customer360Scorecard = customer360Scorecard.updateCustomerSummary(customerHealth.get("Summary"));
        Assert.assertTrue(customer360Scorecard.verifyOverAllSummary(customerHealth.get("Summary")), "Checking Over All Summary.");
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));

        customerHealth = getMapFromData(testData.get("CustomerHealth2"));
        customer360Scorecard = customer360Scorecard.updateOverAllScore(customerHealth.get("Score"), false);
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));

        customerHealth = getMapFromData(testData.get("CustomerHealth3"));
        customer360Scorecard = customer360Scorecard.updateOverAllScore(customerHealth.get("Score"), false);
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));

        customerHealth = getMapFromData(testData.get("CustomerHealth4"));
        customer360Scorecard = customer360Scorecard.updateOverAllScore(customerHealth.get("Score"), false);
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreTrend(customerHealth.get("Trend")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScoreColor(customerHealth.get("Score")));

        customer360Scorecard = customer360Scorecard.removeOverAllCustomerScore();
        Assert.assertTrue(customer360Scorecard.verifyOverallScore("NA"));
    }

    /* Goals in 360 page as problem with java script - in selenium.
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-9")
    public void customerGoalsUpdate(HashMap<String, String> testData) {
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(testData.get("Scheme"));
        String goals =  testData.get("Goals");
        String summary = testData.get("OverallScoreSummary");
        customer360Scorecard = customer360Scorecard.updateCustomerGoals(goals);
        Assert.assertTrue(customer360Scorecard.verifyCustomerGoals(goals));
    }
    */

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

}
