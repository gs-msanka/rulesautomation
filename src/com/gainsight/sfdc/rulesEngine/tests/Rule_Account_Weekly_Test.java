package com.gainsight.sfdc.rulesEngine.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.rulesEngine.pojos.RuleAlertCriteria;
import com.gainsight.sfdc.rulesEngine.pojos.RuleScorecardCriteria;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.ExcelDataProvider;
import jxl.read.biff.BiffException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final String ALERT_CRITERIA_KEY      = "JBCXM__AlertCriteria__c";
    private static final String SCORE_CRITERIA_KEY      = "JBCXM__ScorecardCriteria__c";
    private static final String SCORE_SCHEME_FILE       = TestEnvironment.basedir+"/apex_scripts/Scorecard/Scorecard_enable_numeric.apex";
    private static final String METRICS_CREATE_FILE     = TestEnvironment.basedir+"/apex_scripts/Scorecard/Create_ScorecardMetrics.apex";
    private static final String SCORECARD_CLEAN_FILE    = TestEnvironment.basedir+"/apex_scripts/Scorecard/Scorecard_CleanUp.txt";
    private final static String JOB_ACCOUNT_LOAD        = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Job_Accounts.txt";
    private final static String JOB_CUSTOMER_LOAD       = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Job_Customers.txt";


    private static SFDCInfo sfdcInfo = SFDCUtil.fetchSFDCinfo();
    private RuleEngineDataSetup ruleEngineDataSetup;
    private DataETL dataETL;
    private Resty resty;
    private URI uri;
    private static final String SCHEME = "Score";
    private static final String USAGE_LEVEL = "ACCOUNTLEVEL";

    //Please update this list if new test case need to be added.
    private String[] sheetNames = new String[]{"Rule1", "Rule2", "Rule3", "Rule4", "Rule5", "Rule6", "Rule7", "Rule8", "Rule9"
            , "Rule10", "Rule11", "Rule12", "Rule13", "Rule14"};

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
        apex.runApexCodeFromFile(SCORE_SCHEME_FILE, isPackage);
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
        for(int i=0; i< sheetNames.length; i++) {
            List<HashMap<String, String>> dummyList = ExcelDataProvider.getDataFromExcel(TestEnvironment.basedir + "/" + TEST_DATA_FILE, sheetNames[i]);
            for(HashMap<String, String> testData : dummyList) {
                executeRule(testData);
                if((i+1)%5 ==0) {
                    waitForBatchExecutionToComplete("StatefulBatchHandler");
                }
            }
        }
        //Waiting for all the rule execution to be completed.
        waitForBatchExecutionToComplete("StatefulBatchHandler");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
    public void Equals_NotEquals_LessThan_GreaterThan_AccountField_Rule1(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
    public void Equals_NotEquals_LessThan_GreaterThan_Static_Rule2(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
    public void UsageDroppedOverPeriod_Rule3(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
    public void UsageIncreasedOverPeriod_Rule4(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
    public void Sum_Of_Measure_ExcludeNull_AccountField_Rule5(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
    public void Sum_Of_Measure_ExcludeNull_StaticValue_Rule6(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule7")
    public void Sum_Of_Measure_IncludeNull_AccountField_Rule7(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule8")
    public void Sum_Of_Measure_StaticValue_IncludeNull_Rule8(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule9")
    public void Avg_Of_Measure_AccountField_ExcludeNull_Rule9(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule10")
    public void Avg_Of_Measure_StaticValue_ExcludeNull_Rule10(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule11")
    public void Avg_Of_Measure_AccountField_IncludeNull_Rule11(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule12")
    public void Avg_Of_Measure_StaticValue_IncludeNull_Rule12(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule13")
    public void Measure_OverPeriod_AccountField_Rule13(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule14")
    public void Measure_OverPeriod_StaticValue_Rule14(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        //Just for debugging purpose, because we don't know what payload is delivered i.e. rule is created.
        ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        assertRuleResult(testData);
    }

    private void executeRule(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException{
        //Always runs for current user.
        testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
        testData.put("JBCXM__PlayBookIds__c", ruleEngineDataSetup.pkListMap.get(testData.get("JBCXM__PlayBookIds__c")));
        String rule = ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
        String ruleId = createRule(rule);
        ruleEngineDataSetup.runRule(ruleId, USAGE_LEVEL, "Sat", -7, true);
    }

    private void assertRuleResult(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
        testData.put("JBCXM__PlayBookIds__c", ruleEngineDataSetup.pkListMap.get(testData.get("JBCXM__PlayBookIds__c")));

        ObjectMapper mapper = new ObjectMapper();
        RuleAlertCriteria ruleAlertCriteria = mapper.readValue(testData.get(ALERT_CRITERIA_KEY), RuleAlertCriteria.class);
        if(testData.get("JBCXM__AlertCriteria__c") != null && testData.get("JBCXM__AlertCriteria__c")!="") {
            if(Boolean.valueOf(testData.get("IsCTARule"))) {
                Assert.assertTrue(ruleEngineDataSetup.verifyCTAExists(testData.get("Account"), testData.get("JBCXM__TaskDefaultOwner__c"), Integer.valueOf(testData.get("Count")), ruleAlertCriteria));
            } else {
                Assert.assertTrue(ruleEngineDataSetup.verifyAlertExists( testData.get("Account"), Integer.valueOf(testData.get("Count")), ruleAlertCriteria));
            }
        }
        if(testData.get("JBCXM__ScorecardCriteria__c") != null && testData.get("JBCXM__ScorecardCriteria__c")!="") {
            ArrayList<RuleScorecardCriteria.ActionList> actionLists = ruleEngineDataSetup.getScorecardActions(testData.get(SCORE_CRITERIA_KEY));
            for(RuleScorecardCriteria.ActionList action : actionLists) {
                Assert.assertTrue(ruleEngineDataSetup.verifyMetricScoreAndComments(testData.get("Account"), action));
            }
        }
    }

    private String createRule(String rule) throws IOException, JSONException {
        JSONResource res = resty.json(uri, Resty.form(rule));
        JSONObject jObj = res.toObject();
        Report.logInfo(jObj.toString());
        String ruleId = jObj.getString("id");
        Report.logInfo("Rule Id : "+ruleId);
        return ruleId;
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}

