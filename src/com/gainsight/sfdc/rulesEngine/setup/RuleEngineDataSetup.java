package com.gainsight.sfdc.rulesEngine.setup;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.rulesEngine.pojos.AutomatedRule;
import com.gainsight.sfdc.rulesEngine.pojos.RuleAlertCriteria;
import com.gainsight.sfdc.rulesEngine.pojos.RuleScorecardCriteria;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.sforce.soap.partner.sobject.SObject;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RuleEngineDataSetup extends BaseTest {

	// load Usage Data
	// Create Rules as per data loaded

    private final static String USAGE_MEASURES_CREATE           = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Scripts/UsageData_Measures.apex";
    private final static String ACCOUNT_MONTHLY_USAGE_CONFIG    = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Scripts/Set_Account_Level_Monthly.apex";
    private final static String ACCOUNT_WEEKLY_USAGE_CONFIG     = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Scripts/Set_Account_Level_Weekly.apex";
    private final static String INSTANCE_MONTHLY_USAGE_CONFIG   = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Scripts/Set_Instance_Level_Monthly.apex";
    private final static String INSTANCE_WEEKLY_USAGE_CONFIG    = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Scripts/Set_Instance_Level_Weekly.apex";
    private final static String CLEANUP_FILE                    = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Scripts/RulesData_CleanUp.apex";
    private final static String JOB_ACCOUNT_LOAD                = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Job_Accounts.txt";
    private final static String JOB_CUSTOMER_LOAD               = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Job_Customers.txt";
    private final static String JOB_USAGE_LOAD                  = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Job_UsageData.txt";
	private final static String USAGE_OBJECT                    = "JBCXM__UsageData__C";
    private final static String CUSTOMER_OBJECT                 = "JBCXM__CustomerInfo__c";
    private final static String PICK_LIST_QUERY                 = "Select id, Name, JBCXM__Category__c, JBCXM__SystemName__c from JBCXM__PickList__c " +
                                                                    " where JBCXM__Category__c like 'Alert%'order by JBCXM__Category__c, Name";
    private final static String CTA_TYPES_QUERY                 = "Select id, Name, JBCXM__Type__c, JBCXM__DisplayOrder__c, JBCXM__Color__c from JBCXM__CTATypes__c";
    private final static String SCORECARD_METRIC_QUERY          = "SELECT Id, Name FROM JBCXM__ScorecardMetric__c";
    private final static String SCORECARD_SCHEME_DEF_QUERY      = "SELECT Name, Id  FROM JBCXM__ScoringSchemeDefinition__c";
    public static boolean isPackageInstance = false;

    public HashMap<String, String> pkListMap;
    public HashMap<String, String> ctaTypeMap;
    public HashMap<String, String> scorecardMetricMap;
    public HashMap<String, String> scorecardSchemeDefMap;
    public HashMap<String, String> playbooksMap;
    private ObjectMapper mapper;


    public RuleEngineDataSetup() {
        Report.logInfo("In RuleEngine Setup");
        mapper = new ObjectMapper();
		isPackageInstance       = isPackageInstance();
        pkListMap               = getPickListSetupData();
        ctaTypeMap              = getCTATypes();
        scorecardMetricMap      = getScorecardMetrics();
        scorecardSchemeDefMap   = getScoringSchemeDefinition();
        playbooksMap            = getPlaybooks();
        Report.logInfo("End RuleEngine Setup Constructor");
	}

    public void loadAccountsAndCustomers(DataETL dataETL) throws IOException {
        ObjectMapper mapper     = new ObjectMapper();
        dataETL.cleanUp(CUSTOMER_OBJECT, null);
        JobInfo jobInfo= mapper.readValue(resolveNameSpace(JOB_ACCOUNT_LOAD), JobInfo.class);
        dataETL.execute(jobInfo);
        jobInfo = mapper.readValue(resolveNameSpace(JOB_CUSTOMER_LOAD), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    public void loadUsageData(DataETL dataETL, String fileName, Boolean isWeekly) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JobInfo jobInfo     = null;
        String line         = "";
        String code         = "";
        BufferedReader reader;
        BufferedWriter writer;
        String outFile = TestEnvironment.basedir + "/testdata/sfdc/RulesEngine/Jobs/Temp_job.txt";
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
        dataETL.cleanUp(USAGE_OBJECT, null);
        dataETL.execute(jobInfo);
    }

    public void initialCleanUp() {
        apex.runApexCodeFromFile(CLEANUP_FILE, isPackageInstance);
    }

    public void deleteAlertsAndCTA() {
        String query = "Delete [Select Id from JBCXM__Alert__c]; \n" +
                        "Delete [Select Id from JBCXM__CTA__C];";
        apex.runApex(query, isPackageInstance);
    }

    public void clearPreviousTestData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Delete [Select Id from JBCXM__AutomatedAlertRules__c ];");
        stringBuilder.append("\n");
        stringBuilder.append("Delete [select id from JBCXM__Alert__c where JBCXM__Account__r.AccountNumber = 'RulesAccount'];");
        stringBuilder.append("\n");
        stringBuilder.append("Delete [select id from JBCXM__CTA__c where JBCXM__Account__r.AccountNumber = 'RulesAccount'];");
        stringBuilder.append("\n");
        apex.runApex(stringBuilder.toString(), isPackageInstance);
    }

    private HashMap<String, String> getPickListSetupData() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = soql.getRecords(resolveStrNameSpace(PICK_LIST_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField(resolveStrNameSpace("JBCXM__SystemName__c")).toString(), pk.getId());
        }
        return result;
    }

    private HashMap<String, String> getCTATypes() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = soql.getRecords(resolveStrNameSpace(CTA_TYPES_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField("Name").toString(), pk.getId());
        }
        return result;
    }

    private HashMap<String, String> getScorecardMetrics() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = soql.getRecords(resolveStrNameSpace(SCORECARD_METRIC_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField("Name").toString(), pk.getId());
        }
        return result;
    }

    private HashMap<String, String> getScoringSchemeDefinition() {
        HashMap<String, String> result = new HashMap<String, String>();
        SObject[] pickList = soql.getRecords(resolveStrNameSpace(SCORECARD_SCHEME_DEF_QUERY));
        for(SObject pk : pickList) {
            result.put(pk.getField("Name").toString(), pk.getId());
        }
        return result;
    }

    public String generateRuleJson(HashMap<String, String> testData, Boolean isCTA) throws IOException {
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
        rule.setJBCXM__TriggerCriteria__c(testData.get("JBCXM__TriggerCriteria__c"));
        rule.setJBCXM__AdvanceCriteria__c(testData.get("JBCXM__AdvanceCriteria__c"));
        rule.setJBCXM__ScorecardCriteria__c(testData.get("JBCXM__ScorecardCriteria__c"));
        rule.setJBCXM__SelectFields__c(testData.get("JBCXM__SelectFields__c"));
        //scorecard criteria transition.
        if(testData.get("JBCXM__ScorecardCriteria__c") != null && testData.get("JBCXM__ScorecardCriteria__c")!= "") {
            RuleScorecardCriteria ruleScCriteria = mapper.readValue(testData.get("JBCXM__ScorecardCriteria__c"), (RuleScorecardCriteria.class));
            ArrayList<RuleScorecardCriteria.ActionInfo> actionInfoList = ruleScCriteria.getActionInfo();
            System.out.println(actionInfoList.size());
            //Updating all the action list & hard coded to "0" as there will be only one action list.
            for(int i=0; i< actionInfoList.size(); i++) {
                ArrayList<RuleScorecardCriteria.ActionList> actionLists = actionInfoList.get(i).getActionList();
                actionLists.get(0).setScore(scorecardSchemeDefMap.get(actionLists.get(0).getScore()));
                actionLists.get(0).setMetric(scorecardMetricMap.get(actionLists.get(0).getMetric()));
            }
            rule.setJBCXM__ScorecardCriteria__c(mapper.writeValueAsString(ruleScCriteria));
        }
        result = mapper.writeValueAsString(rule);
        Report.logInfo("Rule Json String : " +result);
        return result;
    }

    public ArrayList<RuleScorecardCriteria.ActionList> getScorecardActions(String scorecardCriteria) throws IOException {
        RuleScorecardCriteria ruleScCriteria = mapper.readValue(scorecardCriteria, (RuleScorecardCriteria.class));
        ArrayList<RuleScorecardCriteria.ActionInfo> actionInfo = ruleScCriteria.getActionInfo();
        Report.logInfo(String.valueOf(actionInfo.size()));
        ArrayList<RuleScorecardCriteria.ActionList> actionLists = new ArrayList<RuleScorecardCriteria.ActionList>();
        //Hard coded to "0" as there will be only one action list.
        for(int i=0; i< actionInfo.size(); i++) {
            actionLists.add(actionInfo.get(i).getActionList().get(0));
        }
        return actionLists;
    }

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
        Report.logInfo("Query : "+query);
        SObject[] sObjects = soql.getRecords(query);
        Report.logInfo("No of Records Found :" +sObjects.length);
        return sObjects;
    }

    public boolean verifyAlertExists(String account, int count, RuleAlertCriteria alertCriteria) {
        SObject[] sObjects = getAlertRecords(account, alertCriteria);
        if(sObjects.length >= count) {
            return true;
        }
        return false;
    }

    public String getAlertId(String account, RuleAlertCriteria alertCriteria) {
        SObject[] sObjects = getAlertRecords(account, alertCriteria);
        if(sObjects != null && sObjects.length> 0) {
            return sObjects[0].getId();
        }
        Report.logInfo("No Alert found with Criteria");
        return null;
    }

    public String getCTAId(String account, String owner, RuleAlertCriteria ruleAlertCriteria) {
        SObject[] sObjects = getCTARecords(account, owner, ruleAlertCriteria);
        if(sObjects != null && sObjects.length> 0) {
            return sObjects[0].getId();
        }
        Report.logInfo("No CTA found with Criteria");
        return null;
    }

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
        Report.logInfo("Query : "+query);
        SObject[] sObjects = soql.getRecords(query);
        Report.logInfo("No of Records Found :" +sObjects.length);
        return sObjects;
    }

    public boolean verifyCTAExists(String account, String owner, int count, RuleAlertCriteria alertCriteria) {
        SObject[] sObjects = getCTARecords(account, owner, alertCriteria);
        if(sObjects.length >= count) {
            return true;
        }
        return false;
    }

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
        Report.logInfo("Query : " + query);
        SObject[]  recordList = soql.getRecords(query);
        Report.logInfo("No of Records Found :" +recordList.length);
        if(recordList.length > 0) {
            if(action.getComment() != null || !action.getComment().isEmpty()) {
                Report.logInfo(recordList[0].toString());
                Report.logInfo(recordList[0].getField(resolveStrNameSpace(SCORE_COMMENTS)).toString());
                String actualComments = recordList[0].getField(resolveStrNameSpace(SCORE_COMMENTS)).toString().toLowerCase();
                Report.logInfo("Actual Comments : " +actualComments);
                Report.logInfo("Expected Comments : " +action.getComment());
                if(actualComments.toLowerCase().contains(action.getComment())) {
                    return true;
                }
            }
            return true;
        }
        Report.logInfo("Account : "+account+ " is not having the score of "+action.getScore()+" on metric/measure "+action.getMetric());
        return false;
    }

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
        SObject[]  recordList = soql.getRecords(query);
        Report.logInfo("No of Records Found :" +recordList.length);
        if(recordList.length > 0) {
            if(comments != null || !comments.isEmpty()) {
                String actualComments = recordList[0].getField(resolveStrNameSpace("JBCXM__ScorecardComment__c")).toString().toLowerCase();
                Report.logInfo("Actual Comments : " +actualComments);
                Report.logInfo("Expected Comments : " +comments);
                if(actualComments.toLowerCase().contains(comments)) {
                    return true;
                }
            }
            return true;
        }
        Report.logInfo("Account : "+account+ " is not having the score of "+score);
        return false;
    }

    public HashMap<String, String> getPlaybooks() {
        HashMap<String, String> playbooks = new HashMap<String, String>();
        String query = "select id, Name, JBCXM__PlayBookType__c from JBCXM__Playbook__c";
        SObject[] sObjects = soql.getRecords(resolveStrNameSpace(query));
        for(int i=0; i < sObjects.length; i++){
            playbooks.put(sObjects[i].getField("Name").toString(),sObjects[i].getId());
        }
        return playbooks;
    }

    public void runRule(String ruleId, String usageLevel, int week, int month) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Map<String,Object>  ruleParams=new Map<String,Object>(); \n");
        strBuilder.append("ruleParams.put('ruleId','"+ruleId+"'); \n");
        strBuilder.append("ruleParams.put('ruleRunDate', '"+getDateWithFormat(week,month)+"'); \n");
        strBuilder.append("ruleParams.put('isAlertCreate',true); \n");
        strBuilder.append("ruleParams.put('usageLevel','"+usageLevel+"'); \n");
        strBuilder.append("ruleParams.put('criteriaList',new List<Object>()); \n");
        strBuilder.append("ruleParams.put('areaName','usageData'); \n");
        strBuilder.append("ruleParams.put('actionType','runRule'); \n");
        strBuilder.append("JBCXM.CEHandler.handleCall(ruleParams); \n");
        Report.logInfo("Running Rule : " + strBuilder.toString());
        apex.runApex(resolveStrNameSpace(strBuilder.toString()));
    }

    public void runRule(String ruleId, String usageLevel, String weekDay, int daysToAdd, boolean usesEndDate) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Map<String,Object>  ruleParams=new Map<String,Object>(); \n");
        strBuilder.append("ruleParams.put('ruleId','"+ruleId+"'); \n");
        strBuilder.append("ruleParams.put('ruleRunDate', '"+getWeekLabelDate("Sat", daysToAdd, true, true)+"'); \n");
        strBuilder.append("ruleParams.put('isAlertCreate',true); \n");
        strBuilder.append("ruleParams.put('usageLevel','"+usageLevel+"'); \n");
        strBuilder.append("ruleParams.put('criteriaList',new List<Object>()); \n");
        strBuilder.append("ruleParams.put('areaName','usageData'); \n");
        strBuilder.append("ruleParams.put('actionType','runRule'); \n");
        strBuilder.append("JBCXM.CEHandler.handleCall(ruleParams); \n");
        Report.logInfo("Running Rule : " + strBuilder.toString());
        apex.runApex(resolveStrNameSpace(strBuilder.toString()));
    }

    //Pending
    //No Need to write code to check alert tasks as CTA will replace it.
    //Verify tasks created under particular CTA.
    //Gainsight Tasks, Salesforce Tasks.
    //Only Playbook Id, CTA Id should be sent & code should take care by querying Salesforce for playbook details.
}
