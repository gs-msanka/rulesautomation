package com.gainsight.sfdc.survey.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.rulesEngine.pojos.RuleAlertCriteria;
import com.gainsight.sfdc.rulesEngine.pojos.RuleScorecardCriteria;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.util.metadata.MetadataUtil;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;
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
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 10/09/14
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class Rule_Survey_Test extends BaseTest {

    private RuleEngineDataSetup ruleEngineDataSetup;
    private Resty resty;
    private URI uri;
    private static SFDCInfo sfdcInfo = SFDCUtil.fetchSFDCinfo();
    private static final String AUTOMATED_RULE_OBJECT   = "JBCXM__AutomatedAlertrules__c";
    private static final String ALERT_CRITERIA_KEY      = "JBCXM__AlertCriteria__c";
    private static final String SCORE_CRITERIA_KEY      = "JBCXM__ScorecardCriteria__c";
    private static final String TEST_DATA_FILE          = "testdata/sfdc/survey/tests/Survey_Rule_Test.xls";
    private final static String JOB_ACCOUNT_FILE        = TestEnvironment.basedir + "/testdata/sfdc/survey/jobs/Job_Rule_Survey_Accounts.txt";
    private final static String JOB_CUSTOMER_FILE       = TestEnvironment.basedir + "/testdata/sfdc/survey/jobs/Job_Rule_Survey_Customers.txt";
    private final static String JOB_CONTACT_FILE        = TestEnvironment.basedir + "/testdata/sfdc/survey/jobs/Job_Rule_Survey_Contacts.txt";
    private final static String SURVEY_DESIGN_FILE      = TestEnvironment.basedir + "/testdata/sfdc/survey/scripts/Rule_Survey_Create_Design.txt";
    private final static String SURVEY_PUBLISH_FILE     = TestEnvironment.basedir + "/testdata/sfdc/survey/scripts/Rule_Survey_Publish.txt";
    private final static String SURVEY_PARTICIPANT_FILE = TestEnvironment.basedir + "/testdata/sfdc/survey/scripts/Rule_Survey_Participants_Load.txt";


    private static final String SURVEY_MASTER_QUERY = "Select id, JBCXM__Code__c, JBCXM__Title__c From JBCXM__Survey__c Where JBCXM__Code__c = '%s' AND JBCXM__Title__c = '%s'";
    private static final String surveyCode   = "Customer Retention";
    private static final String surveyTitle  = "CSR";
    private String publishURL;
    private String SURVEY_ID = null;
    private DataETL dataETL = new DataETL();
    private ObjectMapper mapper = new ObjectMapper();
    MetadataUtil metadataUtil;



    @BeforeClass
    public void setup() throws IOException {
        publishURL = env.getProperty("sfdc.siteCustomURL");
        metadataUtil = new MetadataUtil();
        metadataUtil.createFieldsOnAccount();
        metadataUtil.createFieldsOnContact();

        isPackage = isPackageInstance();
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
        resty = new Resty();
        resty.withHeader("Authorization", "Bearer " + sfdcInfo.getSessionId());
        resty.withHeader("Content-Type", "application/json");
        uri = URI.create(sfdcInfo.getEndpoint() + "/services/data/v29.0/sobjects/" + resolveStrNameSpace(AUTOMATED_RULE_OBJECT));

        //Creates the survey with provided survey Code & Title & Keeps the survey in design mode - Same as UI behaviour.
        apex.runApex(resolveStrNameSpace(String.format(FileUtil.getFileContents(SURVEY_DESIGN_FILE), surveyCode, surveyTitle)));

        ruleEngineDataSetup = new RuleEngineDataSetup(surveyCode);
        ruleEngineDataSetup.loadAccountsAndCustomers(dataETL, JOB_ACCOUNT_FILE, JOB_CUSTOMER_FILE);
        JobInfo jobInfo = mapper.readValue(resolveNameSpace(JOB_CONTACT_FILE), JobInfo.class);
        dataETL.execute(jobInfo);

        //Publishes the survey with site URL provided at Application Properties,
        apex.runApex(resolveStrNameSpace(String.format(FileUtil.getFileContents(SURVEY_PUBLISH_FILE), surveyCode, surveyTitle, publishURL)));
        //Adds participants to the survey, Running using script provides the advantage of token population dynamically.
        //And also there's no need to populate to many fields for a survey participant.
        apex.runApex(resolveStrNameSpace(String.format(FileUtil.getFileContents(SURVEY_PARTICIPANT_FILE), surveyCode, surveyTitle)));

        SObject[] surveys =  soql.getRecords(resolveStrNameSpace(String.format(SURVEY_MASTER_QUERY, surveyCode, surveyTitle)));
        if(surveys.length ==1) {
            SURVEY_ID = surveys[0].getId();
        } else {
            throw new RuntimeException("** Survey Not Found **");
        }
        ruleEngineDataSetup.cleanDataSetup();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
    public void Rule1(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("Name", SURVEY_ID);
        executeRule(testData);
        assertRuleResult(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
    public void Rule2(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("Name", SURVEY_ID);
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
    public void Rule3(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("Name", SURVEY_ID);
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
    public void Rule4(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("Name", SURVEY_ID);
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
    public void Rule5(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("Name", SURVEY_ID);
        executeRule(testData);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
    public void Rule6(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("Name", SURVEY_ID);
        executeRule(testData);
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule7")
    public void Rule7(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException {
        testData.put("Name", SURVEY_ID);
        executeRule(testData);
    }



    private void executeRule(HashMap<String, String> testData) throws IOException, JSONException, InterruptedException{
        //Always runs for current user.
        testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
        testData.put("JBCXM__PlayBookIds__c", ruleEngineDataSetup.pkListMap.get(testData.get("JBCXM__PlayBookIds__c")));
        String rulePayLoad = ruleEngineDataSetup.generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), true);
        String ruleId = createRule(rulePayLoad);
    }

    private String createRule(String rule) throws IOException, JSONException {
        JSONResource res = resty.json(uri, Resty.form(rule));
        JSONObject jObj = res.toObject();
        Report.logInfo(jObj.toString());
        String ruleId = jObj.getString("id");
        Report.logInfo("Rule Id : "+ruleId);
        return ruleId;
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

    @AfterClass
    public void tearDown() {
        //basepage.logout();
    }

}
