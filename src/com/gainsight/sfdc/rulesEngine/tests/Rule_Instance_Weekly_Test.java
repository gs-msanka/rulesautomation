package com.gainsight.sfdc.rulesEngine.tests;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import us.monoid.json.JSONException;
import us.monoid.web.Resty;

import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.ws.ConnectionException;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 09/09/14
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rule_Instance_Weekly_Test extends BaseTest {


    private static final String SET_USAGE_DATA_LEVEL_FILE = Application.basedir+"/testdata/sfdc/rulesEngine/scripts/Set_Instance_Level_Weekly.apex";
    private static final String SET_USAGE_DATA_MEASURE_FILE = Application.basedir+"/testdata/sfdc/rulesEngine/scripts/UsageData_Measures.apex";
    private static final String USAGE_DATA_FILE         = "/testdata/sfdc/rulesEngine/data/Rules_UsageData_Instance.csv";
    private static final String TEST_DATA_FILE          = "testdata/sfdc/rulesEngine/tests/Rule_Instance_Weekly_Test.xls";
    private static final String AUTOMATED_RULE_OBJECT   = "JBCXM__AutomatedAlertrules__c";
    private static final String ALERT_CRITERIA_KEY      = "JBCXM__AlertCriteria__c";
    private static final String SCORE_CRITERIA_KEY      = "JBCXM__ScorecardCriteria__c";
    private static final String SCORE_SCHEME_FILE       = Application.basedir+"/apex_scripts/scorecard/Scorecard_enable_numeric.apex";
    private static final String METRICS_CREATE_FILE     = Application.basedir+"/apex_scripts/scorecard/Create_ScorecardMetrics.apex";
    private static final String SCORECARD_CLEAN_FILE    = Application.basedir+"/apex_scripts/scorecard/Scorecard_CleanUp.txt";
    private final static String JOB_ACCOUNT_LOAD        = Application.basedir + "/testdata/sfdc/rulesEngine/jobs/Job_Accounts.txt";
    private final static String JOB_CUSTOMER_LOAD       = Application.basedir + "/testdata/sfdc/rulesEngine/jobs/Job_Customers.txt";

    private RuleEngineDataSetup ruleEngineDataSetup;
    private DataETL dataETL;
    private Resty resty;
    private URI uri;
    private static final String SCHEME = "Score";
    private static final String USAGE_LEVEL = "INSTANCELEVEL";


    @BeforeClass
    public void setUp() throws Exception {
        resty = new Resty();
        resty.withHeader("Authorization", "Bearer " + sfdcInfo.getSessionId());
        resty.withHeader("Content-Type", "application/json");
        uri = URI.create(sfdcInfo.getEndpoint()+"/services/data/v29.0/sobjects/"+resolveStrNameSpace(AUTOMATED_RULE_OBJECT));
        basepage.login();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SCORECARD_CLEAN_FILE));
        AdministrationBasePage adm = basepage.clickOnAdminTab();
        AdminScorecardSection as = adm.clickOnScorecardSection();
        as.enableScorecard();
        metaUtil.createExtIdFieldForScoreCards(sfdc);
        metaUtil.createFieldsOnAccount(sfdc, sfdcInfo);
        metaUtil.createFieldsOnUsageData(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SCORE_SCHEME_FILE));
        runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_LEVEL_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_MEASURE_FILE));
        ruleEngineDataSetup = new RuleEngineDataSetup();
        ruleEngineDataSetup.cleanDataSetup();
        dataETL = new DataETL();
        ruleEngineDataSetup.loadAccountsAndCustomers(dataETL, JOB_ACCOUNT_LOAD, JOB_CUSTOMER_LOAD);
        ruleEngineDataSetup.loadUsageData(dataETL, USAGE_DATA_FILE, true);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
    public void Rule1_Equals_NotEquals_LessThan_GreaterThan_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
    public void Rule2_Equals_NotEquals_LessThan_GreaterThan_Static(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
    public void Rule3_UsageDroppedOverPeriod(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
    public void Rule4_UsageIncreasedOverPeriod(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
    public void Rule5_Sum_Of_Measure_ExcludeNull_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
    public void Rule6_Sum_Of_Measure_ExcludeNull_StaticValue(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule7")
    public void Rule7_Sum_Of_Measure_IncludeNull_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule8")
    public void Rule8_Sum_Of_Measure_StaticValue_IncludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule9")
    public void Rule9_Avg_Of_Measure_AccountField_ExcludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule10")
    public void Rule10_Avg_Of_Measure_StaticValue_ExcludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule11")
    public void Rule11_Avg_Of_Measure_AccountField_IncludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule12")
    public void Rule12_Avg_Of_Measure_StaticValue_IncludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule13")
    public void Rule13_Measure_OverPeriod_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule14")
    public void Rule14_Measure_OverPeriod_StaticValue(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule15")
    public void Rule15(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule16")
    public void Rule16(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule17")
    public void Rule17(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule18")
    public void Rule18(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule19")
    public void Rule19(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule20")
    public void Rule20(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule21")
    public void Rule21(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule22")
    public void Rule22(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule23")
    public void Rule23(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule24")
    public void Rule24(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule25")
    public void Rule25(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule26")
    public void Rule26(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule27")
    public void Rule27(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule28")
    public void Rule28(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule29")
    public void Rule29(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule30")
    public void Rule30(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule31")
    public void Rule31(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule32")
    public void Rule32(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule33")
    public void Rule33(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    /**
     * Alerts are no more supported.
     @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
     @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule34")
     public void Rule34(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
     executeRule(testData);
     }

     @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
     @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule35")
     public void Rule35(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
     executeRule(testData);
     }

     */

    private void executeRule(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.cleanDataSetup();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        try {
            ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
        } catch (ConnectionException e) {
            e.printStackTrace();
            Log.info("Connection Failed");
            Assert.assertTrue(false);
        }

    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}

