package com.gainsight.sfdc.customer360.test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasepage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360Scorecard;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

public class Customer360ScorecardsTests extends BaseTest {

	private final String TEST_DATA_FILE = "testdata/sfdc/Scorecard/Grade_Scheme_Data.xls";
    private final String SETUP_FILE = env.basedir+"/apex_scripts/Scorecard/scorecard.apex";
    private final String CLEAN_FILE = env.basedir+"/apex_scripts/Scorecard/Scorecard_CleanUp.txt";
    private final String GRADE_SCHEME_FILE = env.basedir+"/apex_scripts/Scorecard/Scorecard_enable_grade.apex";
    private final String METRICS_CREATE_FILE = env.basedir+"/apex_scripts/Scorecard/Create_ScorecardMetrics.apex";
    private String SCHEME = "Grade";

    Customer360Page cp;
    Customer360Scorecard cs;

	@BeforeClass
	public void setUp() {
        Report.logInfo("Starting Customer 360 Scorecard module Test Cases...");
		CreateObjectAndFields cObjFields    = new CreateObjectAndFields();
        String Scorecard_Metrics            = "JBCXM__ScorecardMetric__c";
        String[] SCMetric_ExtId             = new String[]{"SCMetric ExternalID"};
        try {
            cObjFields.createTextFields(resolveStrNameSpace(Scorecard_Metrics), SCMetric_ExtId, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create fields");
            e.printStackTrace();
            throw new RuntimeException("Unable to create fields for scorecard section");
        }

        apex.runApexCodeFromFile(CLEAN_FILE, isPackageInstance());
        apex.runApexCodeFromFile(SETUP_FILE, isPackageInstance());
        basepage.login();
        AdministrationBasepage adm = basepage.clickOnAdminTab();
        AdminScorecardSection as = adm.clickOnScorecardSetion();
        as.enableScorecard();
		apex.runApexCodeFromFile(GRADE_SCHEME_FILE,isPackageInstance());
        try {
            runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("Failed to create metrics for scorecards");
        }
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-1")
	public void addScoreToMetric1(HashMap<String, String> testData) {
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(SCHEME);
        HashMap<String, String> measure = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-2")
    public void addScoreToMetric2(HashMap<String, String> testData) {
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(SCHEME);
        HashMap<String, String> measure = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T-3")
    public void addScoreToMetric3(HashMap<String, String> testData) {
        apex.runApex(resolveStrNameSpace("DELETE [SELECT Id FROM JBCXM__ScorecardFact__c];"));
        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Customer360Scorecard customer360Scorecard = customer360Page.goToScorecardSection();
        customer360Scorecard.setScheme(SCHEME);
        HashMap<String, String> measure = getMapFromData(testData.get("Measure1"));
        HashMap<String, String> customerHealth = getMapFromData(testData.get("CustomerHealth"));
        customer360Scorecard = customer360Scorecard.updateMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score"), true);
        Assert.assertTrue(customer360Scorecard.verifyMeasureScore(measure.get("GroupName"), measure.get("MeasureName"), measure.get("Score")));
        Assert.assertTrue(customer360Scorecard.verifyOverallScore(customerHealth.get("Score")));
    }

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

}
