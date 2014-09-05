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

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 02/09/14
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */

public class Rule_Account_Monthly_Test extends BaseTest {


    private static final String SET_USAGE_DATA_LEVEL_FILE = TestEnvironment.basedir+"/testdata/sfdc/RulesEngine/Scripts/Set_Account_Level_Monthly.apex";
    private static final String SET_USAGE_DATA_MEASURE_FILE = TestEnvironment.basedir+"/testdata/sfdc/RulesEngine/Scripts/UsageData_Measures.apex";
    private static final String USAGE_DATA_FILE         = "/testdata/sfdc/RulesEngine/Data/Rules_UsageData_Account.csv";
    private static final String TEST_DATA_FILE          = "testdata/sfdc/RulesEngine/Tests/Rule_Account_Test.xls";
    private static final String AUTOMATED_RULE_OBJECT   = "JBCXM__AutomatedAlertrules__c";
    private static final String ALERT_CRITERIA_KEY      = "JBCXM__AlertCriteria__c";
    private static final String SCORE_CRITERIA_KEY      = "JBCXM__ScorecardCriteria__c";
    private static final String GRADE_SCHEME_FILE       = TestEnvironment.basedir+"/apex_scripts/Scorecard/Scorecard_enable_grade.apex";
    private static final String METRICS_CREATE_FILE     = TestEnvironment.basedir+"/apex_scripts/Scorecard/Create_ScorecardMetrics.apex";
    private static final String SCORECARD_CLEAN_FILE    = TestEnvironment.basedir+"/apex_scripts/Scorecard/Scorecard_CleanUp.txt";

    private static SFDCInfo sfdcInfo = SFDCUtil.fetchSFDCinfo();
    private RuleEngineDataSetup ruleEngineDataSetup;
    private DataETL dataETL;
    private Resty resty;
    private URI uri;
    private static final String SCHEME = "Grade";
    private static final String USAGE_LEVEL = "ACCOUNTLEVEL";
    private boolean isPackageInstance = isPackageInstance();


    @BeforeClass
    public void setUp() throws IOException {
        resty = new Resty();
        resty.withHeader("Authorization", "Bearer " + sfdcInfo.getSessionId());
        resty.withHeader("Content-Type", "application/json");
        uri = URI.create(sfdcInfo.getEndpoint()+"/services/data/v29.0/sobjects/"+resolveStrNameSpace(AUTOMATED_RULE_OBJECT));
        basepage.login();
        apex.runApexCodeFromFile(SCORECARD_CLEAN_FILE, isPackageInstance);
        AdministrationBasePage adm = basepage.clickOnAdminTab();
        AdminScorecardSection as = adm.clickOnScorecardSection();
        as.enableScorecard();
        createExtIdFieldForScoreCards();
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(GRADE_SCHEME_FILE, isPackageInstance);
        runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        apex.runApexCodeFromFile(SET_USAGE_DATA_LEVEL_FILE, isPackageInstance);
        apex.runApexCodeFromFile(SET_USAGE_DATA_MEASURE_FILE, isPackageInstance);
        ruleEngineDataSetup = new RuleEngineDataSetup();
        ruleEngineDataSetup.initialCleanUp();
        dataETL = new DataETL();
        ruleEngineDataSetup.loadAccountsAndCustomers(dataETL);
        ruleEngineDataSetup.loadUsageData(dataETL, USAGE_DATA_FILE, false);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
    public void Equals_NotEquals_LessThan_GreaterThan_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
    public void Equals_NotEquals_LessThan_GreaterThan_Static(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
    public void UsageDroppedOverPeriod(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
    public void UsageIncreasedOverPeriod(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
    public void Sum_Of_Measure_ExcludeNull_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
    public void Sum_Of_Measure_ExcludeNull_StaticValue(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule7")
    public void Sum_Of_Measure_IncludeNull_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule8")
    public void Sum_Of_Measure_StaticValue_IncludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule9")
    public void Avg_Of_Measure_AccountField_ExcludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule10")
    public void Avg_Of_Measure_StaticValue_ExcludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule11")
    public void Avg_Of_Measure_AccountField_IncludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule12")
    public void Avg_Of_Measure_StaticValue_IncludeNull(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule13")
    public void Measure_OverPeriod_AccountField(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule14")
    public void Measure_OverPeriod_StaticValue(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        executeRule(testData);
    }

    private void executeRule(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException{
        //Always runs for current user.
        testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
        testData.put("JBCXM__PlayBookIds__c", ruleEngineDataSetup.pkListMap.get(testData.get("JBCXM__PlayBookIds__c")));

        ObjectMapper mapper = new ObjectMapper();
        String rule = ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")));
        RuleAlertCriteria ruleAlertCriteria = mapper.readValue(testData.get(ALERT_CRITERIA_KEY), RuleAlertCriteria.class);
        String ruleId = createRule(rule);
        ruleEngineDataSetup.runRule(ruleId, USAGE_LEVEL, 0, -1);
        waitForBatchExecutionToComplete("StatefulBatchHandler");
        if(testData.get("JBCXM__AlertCriteria__c") != null && testData.get("JBCXM__AlertCriteria__c")!="") {
            if(Boolean.valueOf(testData.get("IsCTARule"))) {
                Assert.assertTrue(ruleEngineDataSetup.verifyCTAExists(testData.get("Account"), sfdcInfo.getUserId(), Integer.valueOf(testData.get("Count")), ruleAlertCriteria));
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
