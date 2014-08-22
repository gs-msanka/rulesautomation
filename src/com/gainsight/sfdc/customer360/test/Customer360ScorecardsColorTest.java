package com.gainsight.sfdc.customer360.test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 14/08/14
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Customer360ScorecardsColorTest extends BaseTest {

    private final String TEST_DATA_FILE         = "testdata/sfdc/Scorecard/Color_Scheme_Data.xls";
    private final String SETUP_FILE             = env.basedir+"/apex_scripts/Scorecard/scorecard.apex";
    private final String CLEAN_FILE             = env.basedir+"/apex_scripts/Scorecard/Scorecard_CleanUp.txt";
    private final String COLOR_SCHEME_FILE      = env.basedir+"/apex_scripts/Scorecard/Scorecard_enable_color.apex";
    private final String METRICS_CREATE_FILE    = env.basedir+"/apex_scripts/Scorecard/Create_ScorecardMetrics.apex";
    private final String SCHEME                 = "Color";

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

		AdministrationBasePage adm = basepage.clickOnAdminTab();
        AdminScorecardSection as = adm.clickOnScorecardSection();
        as.enableScorecard();
		apex.runApexCodeFromFile(COLOR_SCHEME_FILE,isPackageInstance());
        try {
            runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("Failed to create metrics for scorecards");
        }
    }

}
