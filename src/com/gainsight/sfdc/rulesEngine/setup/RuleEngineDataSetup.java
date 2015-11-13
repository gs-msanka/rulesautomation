package com.gainsight.sfdc.rulesEngine.setup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.jetty.util.URIUtil;
import org.testng.Assert;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import com.gainsight.sfdc.rulesEngine.pojos.AutomatedRule;
import com.gainsight.sfdc.rulesEngine.pojos.RuleAdvancedCriteria;
import com.gainsight.sfdc.rulesEngine.pojos.RuleAlertCriteria;
import com.gainsight.sfdc.rulesEngine.pojos.RuleScorecardCriteria;
import com.gainsight.sfdc.rulesEngine.pojos.RuleSurveyTriggerCriteria;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;

public class RuleEngineDataSetup extends BaseTest {

	// load Usage Data
	// Create Rules as per data loaded

    private final static String USAGE_MEASURES_CREATE           = env.basedir + "/testdata/sfdc/rulesEngine/scripts/UsageData_Measures.apex";
    private final static String ACCOUNT_MONTHLY_USAGE_CONFIG    = env.basedir + "/testdata/sfdc/rulesEngine/scripts/Set_Account_Level_Monthly.apex";
    private final static String ACCOUNT_WEEKLY_USAGE_CONFIG     = env.basedir + "/testdata/sfdc/rulesEngine/scripts/Set_Account_Level_Weekly.apex";
    private final static String INSTANCE_MONTHLY_USAGE_CONFIG   = env.basedir + "/testdata/sfdc/rulesEngine/scripts/Set_Instance_Level_Monthly.apex";
    private final static String INSTANCE_WEEKLY_USAGE_CONFIG    = env.basedir + "/testdata/sfdc/rulesEngine/scripts/Set_Instance_Level_Weekly.apex";
    private final static String CLEANUP_FILE                    = env.basedir + "/testdata/sfdc/rulesEngine/scripts/RulesData_CleanUp.apex";
    private final static String JOB_USAGE_LOAD                  = env.basedir + "/testdata/sfdc/rulesEngine/jobs/Job_UsageData.txt";
	private final static String USAGE_OBJECT                    = "JBCXM__UsageData__C";
    private final static String CUSTOMER_OBJECT                 = "JBCXM__CustomerInfo__c";
    private final static String PICK_LIST_QUERY                 = "Select id, Name, JBCXM__Category__c, JBCXM__SystemName__c from JBCXM__PickList__c Order by JBCXM__Category__c, Name";
    private final static String CTA_TYPES_QUERY                 = "Select id, Name, JBCXM__Type__c, JBCXM__DisplayOrder__c, JBCXM__Color__c from JBCXM__CTATypes__c";
    private final static String SCORECARD_METRIC_QUERY          = "SELECT Id, Name FROM JBCXM__ScorecardMetric__c";
    private final static String SCORECARD_SCHEME_DEF_QUERY      = "SELECT Name, Id  FROM JBCXM__ScoringSchemeDefinition__c";
    private final static String CUSTOMER_DELETE_QUERY           = "Delete [Select Id From JBCXM__CustomerInfo__c Where JBCXM__Account__r.AccountNumber='RulesAccount'];";

    static String userDir = System.getProperty("basedir", ".");
    public HashMap<String, String> pkListMap;
    public HashMap<String, String> ctaTypeMap;
    public HashMap<String, String> scorecardMetricMap;
    public HashMap<String, String> scorecardSchemeDefMap;
    public HashMap<String, String> playbooksMap;
    private ObjectMapper mapper;

    private PartnerConnection connection;
    public HashMap<String, String> surveyQuestionMap;
    public HashMap<String, String> surveyAnswerMap;


    private static Properties loadProperties(String propFile) {
        Properties props = new Properties();
        // Optional properties file to override everything
        try {
            props.load(new FileReader(propFile));
        } catch (Exception e) {
            // ignore errors
            throw new RuntimeException("Failed to read the file : " + e.getLocalizedMessage());
        }
        return props;
    }
    public RuleEngineDataSetup() {
        Log.info("In RuleEngine Setup");
        mapper = new ObjectMapper();
        connection = sfdc.getPartnerConnection();
        pkListMap               = getPickListSetupData();
        ctaTypeMap              = getCTATypes();
        scorecardMetricMap      = getScorecardMetrics();
        scorecardSchemeDefMap   = getScoringSchemeDefinition();
        playbooksMap            = getPlaybooks();
        Log.info("End RuleEngine Setup Constructor");

	}

    public RuleEngineDataSetup(String surveyCode) {
        Log.info("Survey Rule's Engine Setup");
        mapper = new ObjectMapper();
        pkListMap               = getPickListSetupData();
        ctaTypeMap              = getCTATypes();
        scorecardMetricMap      = getScorecardMetrics();
        scorecardSchemeDefMap   = getScoringSchemeDefinition();
        playbooksMap            = getPlaybooks();
        surveyQuestionMap       = getSurveyQuestionMap(surveyCode);
        surveyAnswerMap         = getSurveyAnswerMap(surveyCode);
        connection = sfdc.getPartnerConnection();
        Log.info("End of Survey Rule's Engine Setup");
    }

    /**
     * SurveyQuestion+SurveyAnswer as Key and Id as value.
     * @param surveyCode
     * @return
     */
    public HashMap<String, String> getSurveyAnswerMap(String surveyCode) {
        String query = "SELECT id, JBCXM__IsActive__c, JBCXM__SurveyMaster__r.JBCXM__Code__c," +
                            " JBCXM__SurveyQuestion__r.JBCXM__Title__c, JBCXM__Title__c FROM JBCXM__SurveyAllowedAnswers__c" +
                            " where JBCXM__SurveyMaster__r.JBCXM__Code__c = '"+surveyCode+"'";
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] sFDCAnswers = sfdc.getRecords(resolveStrNameSpace(query));
        for(int i=0; i < sFDCAnswers.length; i++) {
            SObject sObject = sFDCAnswers[i];
            XmlObject xmlObject = (XmlObject)sObject.getField("JBCXM__SurveyQuestion__r");
            if(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")) == null) continue;
            Log.info(xmlObject.getField("JBCXM__Title__c").toString().trim());
            Log.info(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString().trim());
            Log.info(sObject.getId());
            result.put(xmlObject.getField("JBCXM__Title__c").toString().trim()+
                    sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString().trim(), sObject.getId());
        }
        return result;
    }

    /**
     * QuestionTitle+Parent Question Title as Key & ID as value.
     * @param surveyCode
     * @return
     */
    //Please try to maintain question's title different.
    private HashMap<String, String> getSurveyQuestionMap(String surveyCode) {
    String query = "SELECT Id, JBCXM__ParentQuestion__c, JBCXM__ParentQuestion__r.JBCXM__Title__c," +
                    " JBCXM__SurveyMaster__r.JBCXM__Code__c, JBCXM__Title__c, " +
                    " JBCXM__Type__c, JBCXM__IsActive__c FROM JBCXM__SurveyQuestion__c where JBCXM__IsActive__c= true AND" +
                    " JBCXM__SurveyMaster__r.JBCXM__Code__c='"+surveyCode+"'";
        SObject[] sFDCSurveyQuestions = sfdc.getRecords(resolveStrNameSpace(query));
        HashMap<String, String> result = new HashMap<String, String>();
        SObject sObject;
        for(int i=0; i<sFDCSurveyQuestions.length; i++) {
            sObject = sFDCSurveyQuestions[i];
            if(sObject.getField(resolveStrNameSpace("JBCXM__ParentQuestion__c")) != null ) {
                XmlObject xmlObject = (XmlObject)sObject.getField("JBCXM__ParentQuestion__r");
                result.put(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString().trim()+
                        xmlObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString().trim(), sObject.getId());
            } else {
                result.put(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString().trim(), sObject.getId());
            }
        }
        return result;
    }

    /**
     * Loads account's and Customer's
     * @param dataETL
     * @throws IOException
     */
    public void loadAccountsAndCustomers(DataETL dataETL, String accountJobName, String customerJobName) throws IOException {
        ObjectMapper mapper     = new ObjectMapper();
        if(accountJobName != null && accountJobName != "") {
            JobInfo jobInfo= mapper.readValue((new FileReader(accountJobName)), JobInfo.class);
            dataETL.execute(jobInfo);
        }
        if(customerJobName != null && customerJobName != "") {
            sfdc.runApexCode(resolveStrNameSpace(CUSTOMER_DELETE_QUERY));
            //dataETL.cleanUp(CUSTOMER_OBJECT, resolveStrNameSpace(CUSTOMER_DELETE_QUERY));
            JobInfo jobInfo = mapper.readValue((new FileReader(customerJobName)), JobInfo.class);
            dataETL.execute(jobInfo);
        }

    }

    /**
     * Load's Usage Data.
     * @param dataETL
     * @param fileName
     * @param isWeekly
     * @throws IOException
     */
    public void loadUsageData(DataETL dataETL, String fileName, Boolean isWeekly) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JobInfo jobInfo     = null;
        String line         = "";
        String code         = "";
        BufferedReader reader;
        BufferedWriter writer;
        String outFile = env.basedir + "/testdata/sfdc/rulesEngine/jobs/Temp_job.txt";
        reader = new BufferedReader(new FileReader(JOB_USAGE_LOAD));
        writer = new BufferedWriter(new FileWriter(outFile));
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        code = stringBuilder.toString().replace("ISMONTHLY", String.valueOf(!isWeekly)).replace("ISWEEKLY", String.valueOf(isWeekly)).replace("FILE_NAME", fileName);
        writer.write(code);
        writer.close();
        jobInfo = mapper.readValue(resolveNameSpace(outFile),JobInfo.class);
        dataETL.cleanUp(resolveStrNameSpace(USAGE_OBJECT), null);
        dataETL.execute(jobInfo);
    }


    /**
     * Delete all the Rules, Alerts, CTAs that are setup in the org.
     */
    public void cleanDataSetup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_FILE));
    }

    private HashMap<String, String> getPickListSetupData() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = sfdc.getRecords(resolveStrNameSpace(PICK_LIST_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField(resolveStrNameSpace("JBCXM__SystemName__c")).toString(), pk.getId());
        }
        return result;
    }

    private HashMap<String, String> getCTATypes() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = sfdc.getRecords(resolveStrNameSpace(CTA_TYPES_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField("Name").toString(), pk.getId());
        }
        return result;
    }

    private HashMap<String, String> getScorecardMetrics() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = sfdc.getRecords(resolveStrNameSpace(SCORECARD_METRIC_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField("Name").toString(), pk.getId());
        }
        return result;
    }

    /**
     * Get the scorecard scheme definition, with name, id as key & value.
     * @return
     */
    private HashMap<String, String> getScoringSchemeDefinition() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = sfdc.getRecords(resolveStrNameSpace(SCORECARD_SCHEME_DEF_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField("Name").toString(), pk.getId());
        }
        return result;
    }

    /**
     * Generates the actual payload that's required for rule creation.
     * @param testData
     * @param isCTA
     * @param isSurveyRule
     * @return
     * @throws IOException
     */
    //I am sick of this method, please some one help me to refactor this..
    public String generateRuleJson(HashMap<String, String> testData, Boolean isCTA, Boolean isSurveyRule) throws IOException {
        String result = "";
        AutomatedRule rule = new AutomatedRule();
        rule.setName(testData.get("Name"));
        rule.setJBCXM__Description__c(testData.get("JBCXM__Description__c"));
        rule.setJBCXM__ruleType__c(testData.get("JBCXM__ruleType__c"));
        rule.setJBCXM__TriggeredUsageOn__c(testData.get("JBCXM__TriggeredUsageOn__c"));
        rule.setJBCXM__SourceType__c(testData.get("JBCXM__SourceType__c"));
        //Alert(or)CTA criteria transition.
        if(testData.get("JBCXM__AlertCriteria__c") !=null && testData.get("JBCXM__AlertCriteria__c")!="") {
            RuleAlertCriteria ruleAlertCriteria = mapper.readValue(testData.get("JBCXM__AlertCriteria__c"), RuleAlertCriteria.class);
            ruleAlertCriteria.setAlertSeverity(pkListMap.get(ruleAlertCriteria.getAlertSeverity()));
            ruleAlertCriteria.setAlertReason(pkListMap.get(ruleAlertCriteria.getAlertReason()));
            ruleAlertCriteria.setAlertStatus(pkListMap.get(ruleAlertCriteria.getAlertStatus()));
            if(isCTA) {
                ruleAlertCriteria.setAlertType(ctaTypeMap.get(ruleAlertCriteria.getAlertType()));
            } else {
                ruleAlertCriteria.setAlertType(pkListMap.get(ruleAlertCriteria.getAlertType()));
            }
            rule.setJBCXM__AlertCriteria__c(mapper.writeValueAsString(ruleAlertCriteria));
        }
        rule.setJBCXM__Status__c(Boolean.valueOf(testData.get("JBCXM__Status__c")));
        rule.setJBCXM__PlayBookIds__c(testData.get("JBCXM__PlayBookIds__c"));
        rule.setJBCXM__TaskDefaultOwner__c(testData.get("JBCXM__TaskDefaultOwner__c"));
        if(isSurveyRule) {
            String triggerCriteria = testData.get("JBCXM__TriggerCriteria__c");
            ArrayList<RuleSurveyTriggerCriteria> criteriaList  = mapper.readValue(triggerCriteria, new TypeReference<ArrayList<RuleSurveyTriggerCriteria>>() {});
            Log.info(mapper.writeValueAsString(criteriaList));
            for(RuleSurveyTriggerCriteria criteria : criteriaList) {
                String question  = criteria.getId();
                if(criteria.getpId()!=null) {
                    Log.info(criteria.getId()+criteria.getpId());
                    Log.info(surveyQuestionMap.get(criteria.getId()+criteria.getpId()));
                    criteria.setId(surveyQuestionMap.get(criteria.getId()+criteria.getpId()));
                    criteria.setpId(surveyQuestionMap.get(criteria.getpId()));
                } else {
                    criteria.setId(surveyQuestionMap.get(criteria.getId()));
                }
                RuleSurveyTriggerCriteria.AnswerRecords answerRecords = criteria.getAnswerRecords();
                Log.info(mapper.writeValueAsString(answerRecords));
                String[] ans = answerRecords.getIdList();
                for(int i=0 ; i <ans.length; i++) {
                    ans[i] = surveyAnswerMap.get(question+ans[i]);
                }
                answerRecords.setIdList(ans);
                criteria.setAnswerRecords(answerRecords);
            }
            Log.info("Survey Rule Trigger Criteria : " +mapper.writeValueAsString(criteriaList));
            rule.setJBCXM__TriggerCriteria__c(mapper.writeValueAsString(criteriaList));
        } else {
            rule.setJBCXM__TriggerCriteria__c(testData.get("JBCXM__TriggerCriteria__c"));
        }
        if(testData.get("JBCXM__AdvanceCriteria__c") != null && testData.get("JBCXM__AdvanceCriteria__c") != "") {
            RuleAdvancedCriteria advancedCriteria = mapper.readValue(testData.get("JBCXM__AdvanceCriteria__c"), RuleAdvancedCriteria.class);
            ArrayList<RuleAdvancedCriteria.FilterCriteria> filterCriterias = advancedCriteria.getFilterCriteria();
            for(RuleAdvancedCriteria.FilterCriteria filterCriteria : filterCriterias) {
                if(filterCriteria.getObjectName().equalsIgnoreCase("CustomerInfo__c")) {
                    String values = filterCriteria.getValue();
                    Pattern pattern = Pattern.compile("'[\\w]{1,30}'");
                    Matcher matcher = pattern.matcher(values);

                    if((filterCriteria.getName().equalsIgnoreCase("Stage__c") ||
                            filterCriteria.getName().equalsIgnoreCase("Status__c"))) {
                        while(matcher.find()) {
                            String dd = matcher.group().substring(1, matcher.group().length()-1);
                            values = values.replace(dd,pkListMap.get(dd));
                        }
                    } else if((filterCriteria.getName().equalsIgnoreCase("CurScoreId__c") ||
                            filterCriteria.getName().equalsIgnoreCase("PrevScoreId__c"))) {
                        while(matcher.find()) {
                            String dd = matcher.group().substring(1, matcher.group().length()-1);
                            values = values.replace(dd,scorecardSchemeDefMap.get(dd));
                        }
                    }
                    filterCriteria.setValue(values);
                }
            }
            advancedCriteria.setFilterCriteria(filterCriterias);
            rule.setJBCXM__AdvanceCriteria__c(mapper.writeValueAsString(advancedCriteria));
        }

        rule.setJBCXM__SelectFields__c(testData.get("JBCXM__SelectFields__c"));
        //scorecard criteria transition.
        if(testData.get("JBCXM__ScorecardCriteria__c") != null && testData.get("JBCXM__ScorecardCriteria__c")!= "") {
            RuleScorecardCriteria ruleScCriteria = mapper.readValue(testData.get("JBCXM__ScorecardCriteria__c"), (RuleScorecardCriteria.class));
            ArrayList<RuleScorecardCriteria.ActionInfo> actionInfoList = ruleScCriteria.getActionInfo();
            Log.info(String.valueOf(actionInfoList.size()));
            //Updating all the action list & hard coded to "0" as there will be only one action list.
            for(RuleScorecardCriteria.ActionInfo actionInfo : actionInfoList) {
                //Changing the Actions list.
                ArrayList<RuleScorecardCriteria.ActionList> actionLists = actionInfo.getActionList();
                for(RuleScorecardCriteria.ActionList actionList : actionLists) {
                    actionList.setScore(scorecardSchemeDefMap.get(actionList.getScore()));
                    actionList.setMetric(scorecardMetricMap.get(actionList.getMetric()));
                }
                //Changing the Conditions List
                ArrayList<RuleScorecardCriteria.ConditionList> conditionLists = actionInfo.getConditionList();
                for(RuleScorecardCriteria.ConditionList conditionList :conditionLists ) {
                    if(conditionList.getObjectName().equalsIgnoreCase("CustomerInfo__c")) {
                        if(conditionList.getName().equalsIgnoreCase("Stage__c") || conditionList.getName().equalsIgnoreCase("Status__c")) {
                            String values = conditionList.getValue();
                            Pattern pattern = Pattern.compile("'[\\w]{1,25}'");
                            Matcher matcher = pattern.matcher(values);
                            while(matcher.find()) {
                                String dd = matcher.group().substring(1, matcher.group().length()-1);
                                values = values.replace(dd,pkListMap.get(dd));
                            }
                            conditionList.setValue(values);
                        } else if((conditionList.getName().equalsIgnoreCase("PrevScoreId__c") || conditionList.getName().equalsIgnoreCase("CurScoreId__c"))) {
                            String values = conditionList.getValue();
                            Pattern pattern = Pattern.compile("'[\\w]{1,25}'");
                            Matcher matcher = pattern.matcher(values);
                            while(matcher.find()) {
                                String dd = matcher.group().substring(1, matcher.group().length()-1);
                                values = values.replace(dd,scorecardSchemeDefMap.get(dd));
                            }
                            conditionList.setValue(values);
                        }
                    }
                }
                actionInfo.setConditionList(conditionLists);
            }
            ruleScCriteria.setActionInfo(actionInfoList);
            rule.setJBCXM__ScorecardCriteria__c(mapper.writeValueAsString(ruleScCriteria));
        }
        result = resolveStrNameSpace(mapper.writeValueAsString(rule));
        Log.info("Rule Json String : " +resolveStrNameSpace(result));
        return resolveStrNameSpace(result);
    }

    /**
     * String to POJO class mapping.
     * @param scorecardCriteria
     * @return
     * @throws IOException
     */
    public ArrayList<RuleScorecardCriteria.ActionList> getScorecardActions(String scorecardCriteria) throws IOException {
        RuleScorecardCriteria ruleScCriteria = mapper.readValue(scorecardCriteria, (RuleScorecardCriteria.class));
        ArrayList<RuleScorecardCriteria.ActionInfo> actionInfo = ruleScCriteria.getActionInfo();
        Log.info(String.valueOf(actionInfo.size()));
        ArrayList<RuleScorecardCriteria.ActionList> actionLists = new ArrayList<RuleScorecardCriteria.ActionList>();
        //Hard coded to "0" as there will be only one action list.
        for(int i=0; i< actionInfo.size(); i++) {
            actionLists.add(actionInfo.get(i).getActionList().get(0));
        }
        return actionLists;
    }

    /**
     * Get the alert records based on rule criteria.
     * @param account
     * @param alertCriteria
     * @return
     */
    private SObject[] getAlertRecords(String account, RuleAlertCriteria alertCriteria) {
        String query;
        StringBuilder stringBuilder = new StringBuilder("Select id, JBCXM__Account__r.name, JBCXM__Comment__c From JBCXM__Alert__c ");
        if(account != null && !account.isEmpty()) {
            stringBuilder.append("Where JBCXM__Account__r.name = '"+account.trim()+"'");
            if(alertCriteria.getAlertSeverity() != null) {
                stringBuilder.append("AND JBCXM__Severity__r.JBCXM__SystemName__c = '"+alertCriteria.getAlertSeverity()+"' ");
            }
            if(alertCriteria.getAlertReason() != null) {
                stringBuilder.append("AND JBCXM__Reason__r.JBCXM__SystemName__c = '"+alertCriteria.getAlertReason()+"' ");
            }
            if(alertCriteria.getAlertStatus() != null) {
                stringBuilder.append("AND JBCXM__Status__r.JBCXM__SystemName__c = '"+alertCriteria.getAlertStatus()+"' ");
            }
            if(alertCriteria.getAlertType() != null) {
                stringBuilder.append("AND JBCXM__Type__r.JBCXM__SystemName__c =  '"+alertCriteria.getAlertType()+"' ");
            }
            query = resolveStrNameSpace(stringBuilder.toString());
        } else {
            throw new RuntimeException("Account Should not be null or Empty ");
        }
        Log.info("Query : "+query);
        SObject[] sObjects = sfdc.getRecords(query);
        Log.info("No of Records Found :" +sObjects.length);
        return sObjects;
    }

    /**
     * Verifies if an alert exists for a account on a criteria.
     * @param account
     * @param count
     * @param alertCriteria
     * @return
     */
    public boolean verifyAlertExists(String account, int count, RuleAlertCriteria alertCriteria) {
        SObject[] sObjects = getAlertRecords(account, alertCriteria);
        if(sObjects.length >= count) {
            return true;
        }
        return false;
    }

    /**
     * Returns the Alert ID for the criteria supplied.
     * @param account
     * @param alertCriteria
     * @return
     */
    public String getAlertId(String account, RuleAlertCriteria alertCriteria) {
        SObject[] sObjects = getAlertRecords(account, alertCriteria);
        if(sObjects != null && sObjects.length> 0) {
            return sObjects[0].getId();
        }
        Log.info("No Alert found with Criteria");
        return null;
    }

    /**
     * Returns the CTA Id for the criteria supplied.
     * @param account
     * @param owner
     * @param ruleAlertCriteria
     * @return
     */
    public String getCTAId(String account, String owner, RuleAlertCriteria ruleAlertCriteria) {
        SObject[] sObjects = getCTARecords(account, owner, ruleAlertCriteria);
        if(sObjects != null && sObjects.length> 0) {
            return sObjects[0].getId();
        }
        Log.info("No CTA found with Criteria");
        return null;
    }

    /**
     * Gets all the CTA records for a account based on criteria.
     * @param account
     * @param owner
     * @param alertCriteria
     * @return
     */
    private SObject[] getCTARecords(String account, String owner, RuleAlertCriteria alertCriteria) {
        String query;
        StringBuilder stringBuilder = new StringBuilder("Select id From JBCXM__CTA__c ");
        if(account != null && !account.isEmpty()) {
            stringBuilder.append("Where JBCXM__Account__r.name = '"+account.trim()+"'");
            if(alertCriteria.getAlertSeverity() != null) {
                stringBuilder.append("AND JBCXM__Priority__r.JBCXM__SystemName__c = '"+alertCriteria.getAlertSeverity()+"' ");
            }
            if(alertCriteria.getAlertReason() != null) {
                stringBuilder.append("AND JBCXM__Reason__r.JBCXM__SystemName__c = '"+alertCriteria.getAlertReason()+"' ");
            }
            if(alertCriteria.getAlertStatus() != null) {
                stringBuilder.append("AND JBCXM__Stage__r.JBCXM__SystemName__c = '"+alertCriteria.getAlertStatus()+"' ");
            }
            if(alertCriteria.getAlertType() != null) {
                stringBuilder.append("AND JBCXM__Type__r.Name = '"+alertCriteria.getAlertType()+"' ");
            }
            if(owner != null && !owner.isEmpty()) {
                stringBuilder.append("AND JBCXM__Assignee__c= '"+owner+"' ");
            }
            query = resolveStrNameSpace(stringBuilder.toString());
        } else {
            throw new RuntimeException("Account Should not be null or Empty");
        }
        Log.info("Query : "+query);
        SObject[] sObjects = sfdc.getRecords(query);
        Log.info("No of Records Found :" +sObjects.length);
        return sObjects;
    }

    /**
     * Verifies if the cta is present for a account based on criteria.
     * @param account
     * @param owner
     * @param count
     * @param alertCriteria
     * @return
     */
    public boolean verifyCTAExists(String account, String owner, int count, RuleAlertCriteria alertCriteria) {
        SObject[] sObjects = getCTARecords(account, owner, alertCriteria);
        if(sObjects.length >= count) {
            return true;
        }
        return false;
    }

    /**
     * Verifies Account's metric score and comments.
     * @param account
     * @param action
     * @return
     */
    public boolean verifyMetricScoreAndComments(String account, RuleScorecardCriteria.ActionList action) {
        String query;
        String SCORE_COMMENTS = "JBCXM__CurComment__c";
        StringBuilder stringBuilder = new StringBuilder("Select id, JBCXM__Account__r.name, JBCXM__CurScoreId__r.Name, " +
                                                        "JBCXM__MetricId__r.Name, JBCXM__CurComment__c From JBCXM__ScorecardFact__c ");
        if(account != null && !account.isEmpty()) {
            stringBuilder.append("Where JBCXM__Account__r.name = '"+account.trim()+"'");
            if(action.getMetric() != null && !action.getMetric().isEmpty()) {
                stringBuilder.append(" AND JBCXM__MetricId__r.Name='" + action.getMetric() + "'");
            }
            if(action.getScore() != null && !action.getScore().isEmpty()) {
                stringBuilder.append(" AND JBCXM__CurScoreId__r.Name='"+action.getScore().trim()+"'");
            }
            query = resolveStrNameSpace(stringBuilder.toString());
        } else {
            throw new RuntimeException("Account Should not be null or Empty");
        }
        Log.info("Query : " + query);
        SObject[]  recordList = sfdc.getRecords(query);
        Log.info("No of Records Found :" +recordList.length);
        if(recordList.length > 0) {
            if(action.getComment() != null && !action.getComment().isEmpty()) {
                if(recordList[0].getField(resolveStrNameSpace(SCORE_COMMENTS)) != null) {
                    Log.info(recordList[0].getField(resolveStrNameSpace(SCORE_COMMENTS)).toString());
                    String actualComments = recordList[0].getField(resolveStrNameSpace(SCORE_COMMENTS)).toString().toLowerCase();
                    Log.info("Actual Comments : " +actualComments);
                    Log.info("Expected Comments : " +action.getComment());
                    if(actualComments.toLowerCase().contains(action.getComment())) {
                        return true;
                    }
                }

            }
            return true;
        }
        Log.info("Account : "+account+ " is not having the score of "+action.getScore()+" on metric/measure "+action.getMetric());
        return false;
    }

    /**
     * Verifies the account score and scorecard summary/comments.
     * @param account
     * @param score
     * @param comments
     * @return
     */
    public boolean verifyOverAllScoreAndSummary(String account, String score, String comments) {
        String query = null;
        StringBuilder strBuilder = new StringBuilder("Select id, JBCXM__CurScoreId__r.Name, JBCXM__ScorecardComment__c From JBCXM__CustomerInfo__c");
        if(account != null && !account.isEmpty()) {
            strBuilder.append("WHERE JBCXM__Account__r.Name='"+account.trim()+"'");
            if(score != null) {
                strBuilder.append("AND JBCXM__CurScoreId__r.Name='"+score.trim()+"'");
            }
        } else {
            throw new RuntimeException("Account should not be null or empty");
        }
        query = resolveStrNameSpace(strBuilder.toString());
        SObject[]  recordList = sfdc.getRecords(query);
        Log.info("No of Records Found :" +recordList.length);
        if(recordList.length > 0) {
            if(comments != null && !comments.isEmpty()) {
                String actualComments = recordList[0].getField(resolveStrNameSpace("JBCXM__ScorecardComment__c")).toString().toLowerCase();
                Log.info("Actual Comments : " +actualComments);
                Log.info("Expected Comments : " +comments);
                if(actualComments.toLowerCase().contains(comments)) {
                    return true;
                }
            }
            return true;
        }
        Log.info("Account : "+account+ " is not having the score of "+score);
        return false;
    }

    /**
     * Gets all the playbooks - with name as key , playbook ID as value.
     * @return
     */
    public HashMap<String, String> getPlaybooks() {
        HashMap<String, String> playbooks = new HashMap<String, String>();
        String query = "select id, Name, JBCXM__PlayBookType__c from JBCXM__Playbook__c";
        SObject[] sObjects = sfdc.getRecords(resolveStrNameSpace(query));
        for(int i=0; i < sObjects.length; i++){
            playbooks.put(sObjects[i].getField("Name").toString(),sObjects[i].getId());
        }
        return playbooks;
    }

    /**
     * Run the rule.
     * @param ruleId - Rule ID.
     * @param usageLevel - Adoption Level.
     * @param week - Current data - no of weeks.
     * @param month - Current data - no of months.
     */
    public void runRule(String ruleId, String usageLevel, int week, int month) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Map<String,Object>  ruleParams=new Map<String,Object>(); \n");
        strBuilder.append("ruleParams.put('ruleId','"+ruleId+"'); \n");
        strBuilder.append("ruleParams.put('ruleRunDate', '"+getDateWithFormat(week,month, false)+"'); \n");
        strBuilder.append("ruleParams.put('isAlertCreate',true); \n");
        strBuilder.append("ruleParams.put('usageLevel','"+usageLevel+"'); \n");
        strBuilder.append("ruleParams.put('criteriaList',new List<Object>()); \n");
        strBuilder.append("ruleParams.put('areaName','usageData'); \n");
        strBuilder.append("ruleParams.put('actionType','runRule'); \n");
        strBuilder.append("JBCXM.CEHandler.handleCall(ruleParams); \n");
        Log.info("Running Rule : " + strBuilder.toString());
        sfdc.runApexCode(resolveStrNameSpace(strBuilder.toString()));
    }

    /**
     * Run the rule.
     * @param ruleId - The Rule to Run.
     * @param usageLevel - Level on which the rule need to be triggered. ACCOUNTLEVEL, INSTANCELEVEL, BOTH.
     * @param weekDay - Week Label.
     * @param daysToAdd - current date -  no of days passed.
     * @param usesEndDate - Week Label based on end/start.
     */
    public void runRule(String ruleId, String usageLevel, String weekDay, int daysToAdd, boolean usesEndDate) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Map<String,Object>  ruleParams=new Map<String,Object>(); \n");
        strBuilder.append("ruleParams.put('ruleId','"+ruleId+"'); \n");
        strBuilder.append("ruleParams.put('ruleRunDate', '"+ DateUtil.getWeekLabelDate(weekDay, USER_DATE_FORMAT, userTimezone, daysToAdd, usesEndDate)+"'); \n");
        strBuilder.append("ruleParams.put('isAlertCreate',true); \n");
        strBuilder.append("ruleParams.put('usageLevel','"+usageLevel+"'); \n");
        strBuilder.append("ruleParams.put('criteriaList',new List<Object>()); \n");
        strBuilder.append("ruleParams.put('areaName','usageData'); \n");
        strBuilder.append("ruleParams.put('actionType','runRule'); \n");
        strBuilder.append("JBCXM.CEHandler.handleCall(ruleParams); \n");
        Log.info("Running Rule : " + strBuilder.toString());
        sfdc.runApexCode(resolveStrNameSpace(strBuilder.toString()));
    }

    public void executeRule(HashMap<String, String> testData, SFDCInfo sfdcInfo, Resty resty, URI uri)  {
        //Always runs for current user.
        try {
            testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
            testData.put("JBCXM__PlayBookIds__c", pkListMap.get(testData.get("JBCXM__PlayBookIds__c")));
            String rule = generateRuleJson(testData, Boolean.valueOf(testData.get("IsCTARule")), false);
            String ruleId = createRule(rule, resty, uri);
            //runRule(ruleId, USAGE_LEVEL, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException("Failed to execute rule");
        }
    }

    public void printSetUpData(SFDCInfo sfdcInfo, String account) throws IOException, JSONException, ConnectionException {
        Log.info("Print Test Case Set up Data");
        Resty resty = new Resty();
        resty.withHeader("Authorization", "Bearer " + sfdcInfo.getSessionId());

        URI uri = URI.create(sfdcInfo.getEndpoint()+"/services/data/v31.0/query/?q="+ URIUtil.encodePath(buildQueryOnObject("Account")+((account != null) ? " Where Name = '"+account+"'" : "" )+ "+Limit+20"));
        Log.info("Url To Fire :" +uri.toString());
        JSONResource res = resty.json(uri);
        JSONObject jObj = res.toObject();
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(jObj.toString(), Object.class);
        Log.info("Account Data : \n "+mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        uri = URI.create(sfdcInfo.getEndpoint()+"/services/data/v31.0/query/?q="+ URIUtil.encodePath(buildQueryOnObject(resolveStrNameSpace("JBCXM__CustomerInfo__c"))+((account != null) ? " Where "+resolveStrNameSpace("JBCXM__Account__r.Name")+" = '"+account+"'" : "" )+ "+Limit+50"));
        Log.info("Url To Fire :" +uri.toString());
        res = resty.json(uri);
        jObj = res.toObject();
        json = mapper.readValue(jObj.toString(), Object.class);
        Log.info("Customer Data : \n "+mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        uri = URI.create(sfdcInfo.getEndpoint()+"/services/data/v31.0/query/?q="+ URIUtil.encodePath(buildQueryOnObject(resolveStrNameSpace("JBCXM__UsageData__c"))+((account != null) ? " Where "+resolveStrNameSpace("JBCXM__Account__r.Name")+" = '"+account+"'" : "" )+ "+Limit+50"));
        Log.info("Url To Fire :" +uri.toString());
        res = resty.json(uri);
        jObj = res.toObject();
        json = mapper.readValue(jObj.toString(), Object.class);
        Log.info("Usage Data : \n "+mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }

    public String buildQueryOnObject(String sObject) throws ConnectionException, IOException {
        Log.info("Started building query");
        StringBuilder query = new StringBuilder("Select+");
        DescribeSObjectResult desSObject = connection.describeSObject(sObject);
        Log.info("Object Described :"+desSObject.getName());
        Field[] fields = desSObject.getFields();
        for(Field field : fields) {
            if(field.getType().toString().equalsIgnoreCase("reference")) {
                query.append(field.getRelationshipName() + ".Name, ");
            }  else {
                query.append(field.getName() + ", ");
            }
        }
        query.deleteCharAt(query.lastIndexOf(","));
        query.append(" From "+desSObject.getName());
        return query.toString();
    }

    public void assertRuleResult(HashMap<String, String> testData, SFDCInfo sfdcInfo) throws IOException, JSONException, InterruptedException, ConnectionException {
        String ALERT_CRITERIA_KEY      = "JBCXM__AlertCriteria__c";
        String SCORE_CRITERIA_KEY      = "JBCXM__ScorecardCriteria__c";

        testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
        testData.put("JBCXM__PlayBookIds__c", pkListMap.get(testData.get("JBCXM__PlayBookIds__c")));
        printSetUpData(sfdcInfo, testData.get("Account"));
        if(testData.get(ALERT_CRITERIA_KEY) != null && testData.get(ALERT_CRITERIA_KEY)!="") {
            RuleAlertCriteria ruleAlertCriteria = mapper.readValue(testData.get(ALERT_CRITERIA_KEY), RuleAlertCriteria.class);
            if(Boolean.valueOf(testData.get("IsCTARule"))) {
                Assert.assertTrue(verifyCTAExists(testData.get("Account"), testData.get("JBCXM__TaskDefaultOwner__c"), Integer.valueOf(testData.get("Count")), ruleAlertCriteria));
            } else {
                Assert.assertTrue(verifyAlertExists(testData.get("Account"), Integer.valueOf(testData.get("Count")), ruleAlertCriteria));
            }
        }
        if(testData.get(SCORE_CRITERIA_KEY) != null && testData.get(SCORE_CRITERIA_KEY)!="") {
            ArrayList<RuleScorecardCriteria.ActionList> actionLists = getScorecardActions(testData.get(SCORE_CRITERIA_KEY));
            for(RuleScorecardCriteria.ActionList action : actionLists) {
                Assert.assertTrue(verifyMetricScoreAndComments(testData.get("Account"), action));
            }
        }
    }

    private String createRule(String rule, Resty resty, URI uri) throws IOException, JSONException {
        JSONResource res = resty.json(uri, Resty.form(rule));
        JSONObject jObj = res.toObject();
        Log.info(jObj.toString());
        String ruleId = jObj.getString("id");
        Log.info("Rule Id : " + ruleId);
        return ruleId;
    }

    public void updateUsageDateToTriggerRule(String accName) {
        String s = "JBCXM__UsageData__c usagedata= [Select id, Name, JBCXM__Date__c, JBCXM__Processed__c From JBCXM__UsageData__c \n" +
                "where JBCXM__Account__r.Name='%s' Order by JBCXM__Date__c Desc Limit 1];\n" +
                "usagedata.JBCXM__Processed__c = true;\n" +
                "update usagedata;";
        sfdc.runApexCode(resolveStrNameSpace(String.format(s, accName)));
    }

    //Pending
    //No Need to write code to check alert tasks as CTA will replace it.
    //Verify tasks created under particular CTA.
    //Gainsight Tasks, Salesforce Tasks.
    //Only Playbook Id, CTA Id should be sent & code should take care by querying Salesforce for playbook details.
}
