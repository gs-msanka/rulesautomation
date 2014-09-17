package com.gainsight.sfdc.rulesEngine.tests;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.utils.DataProviderArguments;
import jxl.read.biff.BiffException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.monoid.json.JSONException;
import us.monoid.web.Resty;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 05/09/14
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rule_Account_Weekly_Test extends BaseTest {


    private static final String SET_USAGE_DATA_LEVEL_FILE = TestEnvironment.basedir+"/testdata/sfdc/RulesEngine/Scripts/Set_Account_Level_Weekly.apex";
    private static final String SET_USAGE_DATA_MEASURE_FILE = TestEnvironment.basedir+"/testdata/sfdc/RulesEngine/Scripts/UsageData_Measures.apex";
    private static final String USAGE_DATA_FILE         = "/testdata/sfdc/RulesEngine/Data/Rules_UsageData_Account.csv";
    private static final String TEST_DATA_FILE          = "testdata/sfdc/RulesEngine/Tests/Rule_Account_Weekly_Test.xls";
    private static final String AUTOMATED_RULE_OBJECT   = "JBCXM__AutomatedAlertrules__c";
    private static final String NUMERIC_SCHEME_FILE     = TestEnvironment.basedir+"/apex_scripts/Scorecard/Scorecard_enable_numeric.apex";
    private static final String METRICS_CREATE_FILE     = TestEnvironment.basedir+"/apex_scripts/Scorecard/Create_ScorecardMetrics.apex";
    private static final String SCORECARD_CLEAN_FILE    = TestEnvironment.basedir+"/apex_scripts/Scorecard/Scorecard_CleanUp.txt";
    private final static String JOB_ACCOUNT_LOAD        = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Job_Accounts.txt";
    private final static String JOB_CUSTOMER_LOAD       = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Job_Customers.txt";


    public static SFDCInfo sfdcInfo = SFDCUtil.fetchSFDCinfo();
    private RuleEngineDataSetup ruleEngineDataSetup;
    private DataETL dataETL;
    public Resty resty;
    public URI uri;
    private static final String SCHEME = "Score";
    private static final String USAGE_LEVEL = "ACCOUNTLEVEL";

    //Please update this list if new test case need to be added.
    private String[] sheetNames = new String[]{"Rule1", "Rule2", "Rule3", "Rule4", "Rule5", "Rule6", "Rule7", "Rule8", "Rule9",
                                                "Rule10", "Rule11", "Rule12", "Rule13", "Rule14", "Rule15", "Rule16", "Rule17",
                                                "Rule18", "Rule19", "Rule20", "Rule21", "Rule22", "Rule23", "Rule24", "Rule25", "Rule26",
                                                "Rule27", "Rule28", "Rule29", "Rule30", "Rule31", "Rule32", "Rule33", "Rule34", "Rule35"};

    /**
     * Make sure that your test cases should always validate different expected value.
     * In Order to reduce time, we are running all the test cases in setup & during assertions in test cases.
     * 14 Test cases took almost 60minutes/.
     * @throws java.io.IOException
     */


    @BeforeClass
    public void setUp() throws IOException, BiffException, JSONException, InterruptedException {
        isPackage = isPackageInstance();
        resty = new Resty();
        resty.withHeader("Authorization", "Bearer " + sfdcInfo.getSessionId());
        resty.withHeader("Content-Type", "application/json");
        uri = URI.create(sfdcInfo.getEndpoint()+"/services/data/v29.0/sobjects/"+resolveStrNameSpace(AUTOMATED_RULE_OBJECT));
        basepage.login();
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
        apex.runApexCodeFromFile(SCORECARD_CLEAN_FILE, isPackage);
        AdministrationBasePage adm = basepage.clickOnAdminTab();
        AdminScorecardSection as = adm.clickOnScorecardSection();
        as.enableScorecard();
        createExtIdFieldForScoreCards();
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(NUMERIC_SCHEME_FILE, isPackage);
        runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        apex.runApexCodeFromFile(SET_USAGE_DATA_LEVEL_FILE, isPackage);
        apex.runApexCodeFromFile(SET_USAGE_DATA_MEASURE_FILE, isPackage);
        ruleEngineDataSetup = new RuleEngineDataSetup();
        ruleEngineDataSetup.initialCleanUp();
        dataETL = new DataETL();
        ruleEngineDataSetup.loadAccountsAndCustomers(dataETL, JOB_ACCOUNT_LOAD, JOB_CUSTOMER_LOAD);
        ruleEngineDataSetup.loadUsageData(dataETL, USAGE_DATA_FILE, true);

        //Run all the rules one by one, Do Assertions in test cases.
        //ExcelDataProvider.getDataFromExcel("", "");
        /*for(int i=0; i< sheetNames.length; i++) {
            List<HashMap<String, String>> dummyList = ExcelDataProvider.getDataFromExcel(TestEnvironment.basedir + "/" + TEST_DATA_FILE, sheetNames[i]);
            for(HashMap<String, String> testData : dummyList) {
                executeRule(testData);
                if((i+1)%5 ==0) {
                    waitForBatchExecutionToComplete("StatefulBatchHandler");
                }
            }
        }
        //Waiting for all the rule execution to be completed.
        waitForBatchExecutionToComplete("StatefulBatchHandler");*/
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
    public void Equals_NotEquals_LessThan_GreaterThan_AccountField_Rule1(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
    public void Equals_NotEquals_LessThan_GreaterThan_Static_Rule2(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
    public void UsageDroppedOverPeriod_Rule3(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
    public void UsageIncreasedOverPeriod_Rule4(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
    public void Sum_Of_Measure_ExcludeNull_AccountField_Rule5(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
    public void Sum_Of_Measure_ExcludeNull_StaticValue_Rule6(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule7")
    public void Sum_Of_Measure_IncludeNull_AccountField_Rule7(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule8")
    public void Sum_Of_Measure_StaticValue_IncludeNull_Rule8(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule9")
    public void Avg_Of_Measure_AccountField_ExcludeNull_Rule9(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule10")
    public void Avg_Of_Measure_StaticValue_ExcludeNull_Rule10(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule11")
    public void Avg_Of_Measure_AccountField_IncludeNull_Rule11(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule12")
    public void Avg_Of_Measure_StaticValue_IncludeNull_Rule12(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule13")
    public void Measure_OverPeriod_AccountField_Rule13(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule14")
    public void Measure_OverPeriod_StaticValue_Rule14(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule15")
    public void Rule15(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule16")
    public void Rule16(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule17")
    public void Rule17(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule18")
    public void Rule18(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule19")
    public void Rule19(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule20")
    public void Rule20(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule21")
    public void Rule21(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule22")
    public void Rule22(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule23")
    public void Rule23(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule24")
    public void Rule24(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule25")
    public void Rule25(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule26")
    public void Rule26(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule27")
    public void Rule27(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule28")
    public void Rule28(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule29")
    public void Rule29(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule30")
    public void Rule30(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule31")
    public void Rule31(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule32")
    public void Rule32(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule33")
    public void Rule33(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule34")
    public void Rule34(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule35")
    public void Rule35(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        ruleEngineDataSetup.deleteRuleAlertAndCTA();
        ruleEngineDataSetup.executeRule(testData, sfdcInfo, resty, uri);
        ruleEngineDataSetup.updateUsageDateToTriggerRule(testData.get("Account"));
        ruleEngineDataSetup.assertRuleResult(testData, sfdcInfo);
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}

