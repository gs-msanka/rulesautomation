package com.gainsight.bigdata.copilot.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.copilot.apiImpl.CopilotAPIImpl;
import com.gainsight.bigdata.copilot.bean.emailProp.EmailProperties;
import com.gainsight.bigdata.copilot.bean.emailTemplate.EmailTemplate;
import com.gainsight.bigdata.copilot.bean.outreach.OutReach;
import com.gainsight.bigdata.copilot.bean.outreach.OutReachExecutionHistory;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartList;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.MandrillEvent;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.MandrillWebhookEvent;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.Message;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.Metadata;
import com.gainsight.bigdata.copilot.bean.webhook.sendgrid.SendgridWebhookEvent;
import com.gainsight.bigdata.copilot.enums.UnSubscribeCategory;
import com.gainsight.bigdata.copilot.enums.UnSubscribeReason;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.*;
import com.gainsight.bigdata.reportBuilder.pojos.ReportAdvanceFilter;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInfo;
import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInnerCondition;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Criteria;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.TriggerCriteria;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.util.db.H2Db;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.Comparator;
import com.gainsight.utils.Verifier;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ws.commons.util.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.*;


/**
 * Created by Giribabu on 05/12/15.
 */
public class SmartListAllApiTest extends NSTestBase {
    CopilotAPIImpl copilotAPI;
    GSDataImpl gsDataAPI;
    String testDataDir  = Application.basedir+"/testdata/newstack/CoPilot/dataSet/";
    DataETL dataETL     = new DataETL();
    RulesUtil rulesUtil = new RulesUtil();
    TenantInfo tenantInfo;
    ReportManager reportManager = new ReportManager();
    Calendar calendar = Calendar.getInstance();

    @BeforeClass
    public void setUp() throws Exception {
        copilotAPI = new CopilotAPIImpl(header);
        gsDataAPI = new GSDataImpl(header);
        tenantInfo = gsDataAPI.getTenantInfo(sfinfo.getOrg());

        if(true) {
            sfdc.runApexCode("delete [select id from jbcxm__customerInfo__c];");
            sfdc.runApexCode("delete [select id from contact];");
            metaUtil.createExtIdFieldOnAccount(sfdc);
            JobInfo accountJobInfo = mapper.readValue(new File(testDataDir+"/jobs/Accounts.json"), JobInfo.class);
            dataETL.execute(accountJobInfo);
            JobInfo contactJobInfo = mapper.readValue(new File(testDataDir+"/jobs/Contacts.json"), JobInfo.class);
            dataETL.execute(contactJobInfo);
            JobInfo customerJobInfo = mapper.readValue(new File(testDataDir+"/jobs/Customers.json"), JobInfo.class);
            dataETL.execute(customerJobInfo);
        }
    }

    /**
     * Create Smart list
     * Update Smart list
     * Delete Smart list
     * @throws Exception
     */
    @Test
    public void accountStrategyAccountHasBaseObject() throws Exception {
        //Create a smart list and execute and verify the smart list stats.
        SmartList smartList = mapper.readValue(new File(testDataDir+"test/t1/T1_SmartList.json"), SmartList.class);
        smartList.getAutomatedRule().setTriggerCriteria(resolveStrNameSpace(smartList.getAutomatedRule().getTriggerCriteria()));
        SmartList actualSmartList = copilotAPI.createSmartListExecuteAndGetSmartList(mapper.writeValueAsString(smartList));
        Assert.assertNotNull(actualSmartList.getSmartListId(), "Smart list Id should not be null.");
        rulesUtil.waitForProcessingToComplete(actualSmartList.getSmartListId());
        Assert.assertTrue(rulesUtil.isProcessionSuccess(actualSmartList.getSmartListId()), "Verifying smart list run status is successful.");
        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId()); //Once the rule is successfully run, get the stats and verify.
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), 30, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), 1, "Verifying customer count.");

        //Update the smart list trigger criteria & action info criteria and execute the smart list and verify the smartlist stats.
        List<TriggerCriteria> triggerCriteriaList = mapper.readValue(actualSmartList.getAutomatedRule().getTriggerCriteria(), new TypeReference<ArrayList<TriggerCriteria>>() {});
        TriggerCriteria triggerCriteria = triggerCriteriaList.get(0);
        triggerCriteria.setWhereLogic("A OR B");
        Criteria criteria = mapper.readValue("{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}", Criteria.class);
        triggerCriteria.getCriteria().add(criteria);
        actualSmartList.getAutomatedRule().setTriggerCriteria(mapper.writeValueAsString(triggerCriteriaList));

        ActionInfo actionInfo = mapper.readValue(actualSmartList.getAutomatedRule().getActionDetails().get(0).getActionInfo(), ActionInfo.class);
        ActionInnerCondition actionInnerCondition = mapper.readValue("{\"type\":\"calculated\",\"valueType\":\"BOOLEAN\",\"expression\":{\"alias\":\"undefined\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Contact\",\"field\":\"Contact.Name\",\"fieldName\":\"Name\",\"objectName\":\"Contact\",\"fieldType\":\"STRING\",\"label\":\"Full Name\",\"isExternalCriteria\":true},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"giri\"}}}", ActionInnerCondition.class);
        actionInfo.getCondition().getExpression().getRight().add(actionInnerCondition);
        actualSmartList.getAutomatedRule().getActionDetails().get(0).setActionInfo(mapper.writeValueAsString(actionInfo));
        actualSmartList.setShowFields(null);
        actualSmartList = copilotAPI.updateSmartList(mapper.writeValueAsString(actualSmartList));
        rulesUtil.waitForProcessingToComplete(actualSmartList.getSmartListId());
        Assert.assertTrue(rulesUtil.isProcessionSuccess(actualSmartList.getSmartListId()), "Verifying smart list run status is successful.");
        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId()); //Once the rule is successfully run, get the stats and verify.
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), 2, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), 2, "Verifying customer count.");

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "This is My new Name Idiot.");
        Assert.assertEquals(actualSmartList.getSmartListName(), "This is My new Name Idiot.");

        //Trigger the resync on smart list & verify the status again.
        String smartListReSyncStatusId = copilotAPI.reSyncSmartListData(actualSmartList.getSmartListId());  //If resync is triggered with data modification then it make sense.
        Assert.assertNotNull(smartListReSyncStatusId, "Status Id should not be null.");
        rulesUtil.waitForRuleProcessingToComplete(smartListReSyncStatusId);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(smartListReSyncStatusId), "Verifying smart list re-sync is successful.");

        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId());
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), 2, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), 2,"Verifying customer count.");

        List<HashMap<String, Object>> testData = copilotAPI.getSmartListData(actualSmartList.getSmartListId(), 0);
        Assert.assertEquals(testData.size(), 2, "Expected Value should be 2 records in the smart list data.");

        //Create email template & verify the same.
        EmailTemplate emailTemplate = mapper.readValue(new File(testDataDir+"test/t1/T1_EmailTemplate.json"), EmailTemplate.class);
        EmailTemplate actualEmailTemplate = copilotAPI.createEmailTemplate(mapper.writeValueAsString(emailTemplate));
        Assert.assertNotNull(actualEmailTemplate.getTemplateId(), "Template id should not be null.");
        Assert.assertEquals(actualEmailTemplate.getTitle(), emailTemplate.getTitle(), "Verifying email template title.");

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME");

        //Create out reach & verify the same.
        OutReach outReach = mapper.readValue(new File(testDataDir + "test/t1/T1_OutReach.json"), OutReach.class);
        outReach.setSmartListId(actualSmartList.getSmartListId());
        outReach.setSmartListName(actualSmartList.getSmartListName());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateId(actualEmailTemplate.getTemplateId());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateName(actualEmailTemplate.getTitle());

        OutReach actualOutReach = copilotAPI.createOutReach(mapper.writeValueAsString(outReach));
        Assert.assertNotNull(actualOutReach, "Out Reach creation failed.");
        Assert.assertNotNull(actualOutReach.getCampaignId(), "Out reach Id should not be null.");

        //Trigger out reach, wait for outreach process, verify the execution history of out reach.
        String outReachStatusId = copilotAPI.triggerOutReach(actualOutReach.getCampaignId());
        rulesUtil.waitForRuleProcessingToComplete(outReachStatusId);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(outReachStatusId), "Out reach process is not successful.");

        actualOutReach = copilotAPI.getOutReach(actualOutReach.getCampaignId());
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS");

        RuleExecutionHistory executionHistory = rulesUtil.getExecutionHistory(outReachStatusId);
        Assert.assertTrue(executionHistory.getProcessReport().getActionResults().get(0).getActionName().toLowerCase().contains("Send Email using Gainsight".toLowerCase()));
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getPassCount(), 2, "Passed count should be 2");
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getFailCount(), 0, "Failed cound should be 0");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        String outReachStatusId1 = copilotAPI.triggerOutReach(actualOutReach.getCampaignId());
        rulesUtil.waitForRuleProcessingToComplete(outReachStatusId1);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(outReachStatusId1), "Outreach execution failed.");

        actualOutReach = copilotAPI.getOutReach(actualOutReach.getCampaignId());
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE");

        RuleExecutionHistory executionHistory1 = rulesUtil.getExecutionHistory(outReachStatusId1);
        Assert.assertTrue(executionHistory1.getProcessReport().getActionResults().get(0).getActionName().toLowerCase().contains("Send Email using Gainsight".toLowerCase()));
        Assert.assertEquals(executionHistory1.getProcessReport().getActionResults().get(0).getPassCount(), 0, "Passed count in out reach execution should be 0");
        Assert.assertEquals(executionHistory1.getProcessReport().getActionResults().get(0).getFailCount(), 2, "Failed count in out reach execution should be 2");

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        String outReachStatusId2 = copilotAPI.triggerOutReach(actualOutReach.getCampaignId());
        rulesUtil.waitForRuleProcessingToComplete(outReachStatusId2);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(outReachStatusId2), "Outreach execution failed.");

        actualOutReach = copilotAPI.getOutReach(actualOutReach.getCampaignId());
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS");

        RuleExecutionHistory executionHistory2 = rulesUtil.getExecutionHistory(outReachStatusId2);
        Assert.assertTrue(executionHistory2.getProcessReport().getActionResults().get(0).getActionName().toLowerCase().contains("Send Email using Gainsight".toLowerCase()));
        Assert.assertEquals(executionHistory2.getProcessReport().getActionResults().get(0).getPassCount(), 2, "Passed count in out reach execution should be 0");
        Assert.assertEquals(executionHistory2.getProcessReport().getActionResults().get(0).getFailCount(), 0, "Failed count in out reach execution should be 2");

        //Get the email template references in outreaches & verify the template use in outreach.
        HashMap<String, List<HashMap<String,String>>> templateReferenceList = copilotAPI.getEmailTemplatesOutReachInfo(new String[]{actualEmailTemplate.getTemplateId()});
        List<HashMap<String, String>> templateReference = templateReferenceList.get(actualEmailTemplate.getTemplateId());
        Assert.assertNotNull(templateReference);
        Assert.assertEquals(templateReference.size(), 1);
        Assert.assertEquals(templateReference.get(0).get("outreachId"), actualOutReach.getCampaignId(), "Out reach id not matched.");
        Assert.assertEquals(templateReference.get(0).get("outreachName"), actualOutReach.getName(), "Out reach name not matched.");

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "OUT REACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "OUT REACH NAME UPDATED");

        //Delete outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach delete failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "email template delete failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "Smart list delete failed.");

        //Verify the error code on invalid outreach, email template, smart list Id.
        NsResponseObj outReachNsResponse = copilotAPI.getOutReachNsResponse(actualOutReach.getCampaignId());
        Assert.assertEquals(outReachNsResponse.getErrorCode(), MDAErrorCodes.CAMPAIGN_NOT_FOUND.getGSCode(), "Outreach not found error code not matched.");
        NsResponseObj emailTemplateNsResponse = copilotAPI.getEmailTemplateNsResponseObj(actualEmailTemplate.getTemplateId());
        Assert.assertEquals(emailTemplateNsResponse.getErrorCode(), MDAErrorCodes.EMAIL_TEMPLATE_NOT_FOUND.getGSCode(), "Email Template not found error code not matched.");
        NsResponseObj smartListNsResponse = copilotAPI.getSmartListNsResponse(actualSmartList.getSmartListId());
        Assert.assertEquals(smartListNsResponse.getErrorCode(), MDAErrorCodes.SMARTLIST_DOESNOT_EXISTS.getGSCode(), "Smart list not found error code not matched.");
    }

    @Test
    public void verifyEmailLogCollectionAPI() throws Exception {
        CollectionInfo emailLogCollection = copilotAPI.getEmailLogsCollection();
        Assert.assertEquals(emailLogCollection.getCollectionDetails().getAssetType(), "ANALYTICS");
        Assert.assertEquals(emailLogCollection.getCollectionDetails().getEntityType(), "EMAIL_LOG");
        Assert.assertEquals(emailLogCollection.getCollectionDetails().getCollectionName(), "Email Logs");
        Assert.assertEquals(emailLogCollection.getCollectionDetails().getDataStoreType(), "MONGO");
        Assert.assertEquals(emailLogCollection.getCollectionDetails().getDbType(), "DATA");
        Assert.assertNotNull(emailLogCollection.getCollectionDetails().getCollectionId(), "Collection Id should not be null.");
        Assert.assertEquals(emailLogCollection.getTenantId(), tenantInfo.getTenantId());
        Assert.assertTrue(emailLogCollection.getColumns().size() > 1, "No of columns should be atleast more than 1."); //TODO - Just leave with count verification now.
    }


    @Test
    public void verifySmartListAdvancedOptions() throws Exception {
        sfdc.runApexCode("Delete [Select id from Account where Name like '%CopilotBULKAccount%'];\n" +
                "Delete [Select id from contact where LastName like '%CopiotBULKContact%'];");

        sfdc.runApexCode(getNameSpaceResolvedFileContents(testDataDir + "/test/t2/T2_DataSet.apex"));
        SmartList smartList1 = mapper.readValue(new File(testDataDir + "test/t2/T2_SmartList.json"), SmartList.class);
        smartList1.getSchedulerInfo().getSchedules().get(0).setCronExpression("0 17 10 1/1 * ? *");
        smartList1.getSchedulerInfo().getSchedules().get(0).setStartTime(Calendar.getInstance().getTimeInMillis());
        smartList1.getAutomatedRule().setTriggerCriteria(resolveStrNameSpace(smartList1.getAutomatedRule().getTriggerCriteria()));

        SmartList smartList2 = mapper.readValue(new File(testDataDir + "test/t2/T2_SmartList.json"), SmartList.class);
        smartList2.setName(smartList2.getName() + "-1");
        smartList2.getAutomatedRule().setTriggerCriteria(resolveStrNameSpace(smartList2.getAutomatedRule().getTriggerCriteria()));
        smartList2.getSchedulerInfo().getSchedules().get(0).setStartTime(Calendar.getInstance().getTimeInMillis());
        smartList2.getSchedulerInfo().getSchedules().get(0).setCronExpression("0 20 11 1/1 * ? *");
        smartList2.getSchedulerInfo().getSchedules().get(0).setTitle(smartList2.getName());
        smartList2.getSchedulerInfo().getSchedules().get(0).setDescription(smartList2.getName());

        SmartList actualSmartList1 = copilotAPI.createSmartListExecuteAndGetSmartList(mapper.writeValueAsString(smartList1));
        SmartList actualSmartList2 = copilotAPI.createSmartListExecuteAndGetSmartList(mapper.writeValueAsString(smartList2));
        Assert.assertNotNull(actualSmartList1.getSmartListId(), "Smart list should not be null.");
        Assert.assertNotNull(actualSmartList2.getSmartListId(), "Smart list should not be null.");

        HashMap<String, List<RuleExecutionHistory>> statsMap = copilotAPI.getAllSmartListStatus();
        Assert.assertNotNull(statsMap.get(actualSmartList1.getSmartListId()));
        Assert.assertNotNull(statsMap.get(actualSmartList2.getSmartListId()));

        Assert.assertNotNull(statsMap.get(actualSmartList1.getSmartListId()).get(0).getStatus(), "Smart list Execution status should not be null.");
        Assert.assertNotNull(statsMap.get(actualSmartList2.getSmartListId()).get(0).getStatus(), "Smart list Execution status should not be null.");

        List<SmartList> smartListList = copilotAPI.getAllSmartList();
        Assert.assertTrue(smartListList.size() >= 2, "We Should have got atleast 2 smart list that we created.");

        Assert.assertNotNull(copilotAPI.getSmartList(smartListList, actualSmartList1.getSmartListId()), "Smart does not exists with id " + actualSmartList1.getSmartListId());
        Assert.assertNotNull(copilotAPI.getSmartList(smartListList, actualSmartList2.getSmartListId()), "Smart does not exists with id " + actualSmartList2.getSmartListId());

        List<Schedule> smartListSchedule1 = copilotAPI.getSmartListSchedules(actualSmartList1.getSmartListId());
        Assert.assertNotNull(smartListSchedule1);
        Assert.assertEquals(smartListSchedule1.size(), 1, "1 Schedule info should have exists.");
        Assert.assertEquals(smartListSchedule1.get(0).getTimeZoneName(), actualSmartList1.getSchedulerInfo().getSchedules().get(0).getTimeZoneName(), "Smart List Schdule time zone failed.");
        Assert.assertEquals(smartListSchedule1.get(0).getCronExpression(), actualSmartList1.getSchedulerInfo().getSchedules().get(0).getCronExpression());
        Assert.assertEquals(smartListSchedule1.get(0).getJobContext().get("scheduleType"), actualSmartList1.getSchedulerInfo().getSchedules().get(0).getJobContext().get("scheduleType"));

        List<Schedule> smartListSchedule2 = copilotAPI.getSmartListSchedules(actualSmartList2.getSmartListId());
        Assert.assertNotNull(smartListSchedule2);
        Assert.assertEquals(smartListSchedule2.size(), 1, "1 Schedule info should have exists.");
        Assert.assertEquals(smartListSchedule2.get(0).getTimeZoneName(), actualSmartList2.getSchedulerInfo().getSchedules().get(0).getTimeZoneName(), "Smart List Schdule time zone failed.");
        Assert.assertEquals(smartListSchedule2.get(0).getCronExpression(), actualSmartList2.getSchedulerInfo().getSchedules().get(0).getCronExpression());
        Assert.assertEquals(smartListSchedule2.get(0).getJobContext().get("scheduleType"), actualSmartList2.getSchedulerInfo().getSchedules().get(0).getJobContext().get("scheduleType"));

        rulesUtil.waitForProcessingToComplete(actualSmartList1.getSmartListId());
        Assert.assertTrue(rulesUtil.isProcessingCompleted(actualSmartList1.getSmartListId()), "Smart List execution failed.");

        String payload = "[{\"left\":{\"type\":\"field\",\"field\":\"Email\",\"fieldName\":\"Email\",\"entity\":\"Contact\",\"valueType\":\"STRING\",\"dataType\":\"STRING\",\"fieldType\":\"EMAIL\",\"groupable\":true,\"objectName\":\"Contact\",\"label\":\"Email\",\"alias\":\"\",\"aggregation\":\"\",\"properties\":{\"SFDC\":{\"keys\":[\"SFDC_USER_EMAIL\"]}},\"meta\":{\"isAccessible\":true,\"isFilterable\":true,\"isSortable\":true,\"isGroupable\":true,\"originalDataType\":\"EMAIL\",\"isCreateable\":true,\"precision\":0,\"decimalPlaces\":\"0\",\"relationshipName\":\"\",\"isFormulaField\":false,\"isUpdateable\":true,\"isRichText\":false}},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"email0\"}}]";
        List<HashMap<String, Object>> dataSet = copilotAPI.searchSmartList(actualSmartList1.getSmartListId(), payload);
        Assert.assertEquals(dataSet.size(), 50);
        List<HashMap<String, Object>> dataSet1 = copilotAPI.getSmartListData(actualSmartList1.getSmartListId(), 15);
        Assert.assertEquals(dataSet1.size(), 15);
    }

    @Test
    public void emailTemplateAdvancedOptions() throws Exception {
        EmailTemplate emailTemplate = mapper.readValue(new File(testDataDir+"test/t3/T3_EmailTemplate1.json"), EmailTemplate.class);
        EmailTemplate actualEmailTemplate = copilotAPI.createEmailTemplate(mapper.writeValueAsString(emailTemplate));
        Assert.assertNotNull(actualEmailTemplate.getTemplateId(), "Template id should not be null.");
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(emailTemplate, actualEmailTemplate), "Email template verification failed.");

        EmailTemplate emailTemplate1 = mapper.readValue(new File(testDataDir+"test/t3/T3_EmailTemplate2.json"), EmailTemplate.class);
        EmailTemplate actualEmailTemplate1 = copilotAPI.createEmailTemplate(mapper.writeValueAsString(emailTemplate1));
        Assert.assertNotNull(actualEmailTemplate1.getTemplateId(), "Template id should not be null.");
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(emailTemplate1, actualEmailTemplate1), "Email template verification failed.");

        EmailTemplate updateEmailTemplate1 = mapper.readValue(new File(testDataDir + "test/t3/T3_EmailTemplate2_Update.json"), EmailTemplate.class);
        updateEmailTemplate1.setTemplateId(actualEmailTemplate1.getTemplateId());
        copilotAPI.updateEmailTemplate(mapper.writeValueAsString(updateEmailTemplate1));
        EmailTemplate actualUpdateEmailTemplate = copilotAPI.getEmailTemplate(actualEmailTemplate1.getTemplateId());
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(updateEmailTemplate1, actualUpdateEmailTemplate), "Updated Email Template Verification Failed.");

        List<EmailTemplate> allEmailTemplates = copilotAPI.getAllEmailTemplates();
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(emailTemplate, copilotAPI.getEmailTemplate(allEmailTemplates, actualEmailTemplate.getTemplateId())));
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(updateEmailTemplate1, copilotAPI.getEmailTemplate(allEmailTemplates, actualEmailTemplate1.getTemplateId())));
    }

    @Test
    public void outReachAdvancedOptions() throws Exception {
        SmartList smartList = mapper.readValue(new File(testDataDir + "test/t4/T4_SmartList.json"), SmartList.class);
        smartList.getAutomatedRule().setTriggerCriteria(resolveStrNameSpace(smartList.getAutomatedRule().getTriggerCriteria()));
        SmartList actualSmartList = copilotAPI.createSmartListExecuteAndGetSmartList(mapper.writeValueAsString(smartList));
        Assert.assertNotNull(actualSmartList.getSmartListId(), "Smart list Id should not be null.");
        rulesUtil.waitForProcessingToComplete(actualSmartList.getSmartListId());
        Assert.assertTrue(rulesUtil.isProcessionSuccess(actualSmartList.getSmartListId()), "Verifying smart list run status is successful.");
        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId()); //Once the rule is successfully run, get the stats and verify.
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), 30, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), 1, "Verifying customer count.");

        JobInfo jobInfo = mapper.readValue(new File(testDataDir + "test/t4/T4_DataTRansform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        List<HashMap<String, Object>> actualData = copilotAPI.getSmartListData(actualSmartList.getSmartListId(), 0);
        List<Map<String, String>> parsedData = mapper.convertValue(actualData, List.class);
        List<Map<String, String>> expData  = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir + jobInfo.getTransformationRule().getOutputFileLoc())));

        System.out.println(expData.equals(parsedData));
        System.out.println(expData.equals(actualData));
        Assert.assertEquals(Comparator.compareListData(parsedData, expData).size(), 0, "No of Diff Records should be zero.");

        List<String> emailList = getValuesOfKeyAsList(expData, "Contact.Email");
        List<EmailProperties> emailProperties = copilotAPI.getEmailValidateProperties(emailList);
        Assert.assertTrue(copilotAPI.knockOffEmail(emailProperties));

        EmailTemplate emailTemplate = mapper.readValue(new File(testDataDir+"test/t4/T4_EmailTemplate.json"), EmailTemplate.class);
        EmailTemplate actualEmailTemplate = copilotAPI.createEmailTemplate(mapper.writeValueAsString(emailTemplate));
        Assert.assertNotNull(actualEmailTemplate.getTemplateId(), "Template id should not be null.");
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(emailTemplate, actualEmailTemplate), "Email template verification failed.");

        OutReach outReach = mapper.readValue(new File(testDataDir+ "test/t4/T4_OutReach.json"), OutReach.class);
        outReach.setSmartListId(actualSmartList.getSmartListId());
        outReach.setSmartListName(actualSmartList.getSmartListName());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateId(actualEmailTemplate.getTemplateId());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateName(actualEmailTemplate.getTitle());

        ObjectMapper mapper1 = new ObjectMapper().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        OutReach actualOutReach = copilotAPI.createOutReach(mapper1.writeValueAsString(outReach));
        String outReachStatusId = copilotAPI.triggerOutReach(actualOutReach.getCampaignId());

        rulesUtil.waitForRuleProcessingToComplete(outReachStatusId);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(outReachStatusId));
        RuleExecutionHistory executionHistory = rulesUtil.getLastExecution(actualOutReach.getCampaignId());
        Assert.assertNotNull(executionHistory);
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getFailCount(), 0);
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getPassCount(), 30);


        CollectionInfo emailLogCollectionMaster = copilotAPI.getEmailLogsCollection();
        Assert.assertNotNull(emailLogCollectionMaster, "Failed to fetch email logs collection info.");
        emailLogCollectionMaster = gsDataAPI.getCollectionMaster(emailLogCollectionMaster.getCollectionDetails().getCollectionId());
        Assert.assertNotNull(emailLogCollectionMaster, "Failed to fetch email logs collection info.");

        ReportMaster reportMaster = reportManager.createTabularReportMaster(emailLogCollectionMaster, new String[]{"Use Case", "Batch Id",
                "Batch Name", "Account Id", "Account Name", "Contact Id", "Contact Name", "Email Address", "Template Name", "Sent"});

        // "Execution Instance Id", "External Email ID", "Internal Email ID", "Template Id",

        ReportAdvanceFilter reportAdvanceFilter = mapper.readValue("{\"filters\":[{\"dbName\":\"bid\",\"alias\":\"A\",\"dataType\":\"string\",\"filterOperator\":\"EQ\",\"filterValues\":[\"*****************\"],\"type\":0,\"logicalOperator\":\"AND\",\"collectionId\":\"************************\"}],\"expression\":\"A\"}", ReportAdvanceFilter.class);
        reportAdvanceFilter.getReportFilters().get(0).setFilterValues(Arrays.asList(new Object[]{actualOutReach.getCampaignId()}));
        reportAdvanceFilter.getReportFilters().get(0).setCollectionId(emailLogCollectionMaster.getCollectionDetails().getCollectionId());

        reportMaster.getReportInfo().get(0).setWhereAdvanceFilter(reportAdvanceFilter);

        //Verifying Email Logs Data.
        Log.info("Verifying Email Logs Data....");
        List<Map<String, String>> emailLogs = reportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(mapper.writeValueAsString(reportMaster)), emailLogCollectionMaster);

        System.out.println("Actual "+mapper.writeValueAsString(emailLogs));
        System.out.println("**************************");
        H2Db db = new H2Db("jdbc:h2:~/TEST"  ,"sa","");
        String emailLogExpectedFile =Application.basedir+"/testdata/newstack/CoPilot/dataSet/process/t4/EmailLogsExpectedData.csv";
        String tempTableName = "Temp"+calendar.getTimeInMillis();
        try {
            String query = "Select \"Contact Id\",  \"Contact Name\",  \"Email Address\"," +
                    " \"Account Id\", \"Account Name\",  \"Use Case\", \"Sent\", \"Batch Name\", \"Batch Id\", \"Template Name\"  from "+tempTableName;

            String createTableFromCSV = "CREATE TABLE "+tempTableName+" AS SELECT * FROM CSVREAD('"+ Application.basedir+jobInfo.getTransformationRule().getOutputFileLoc()+"')";
            db.executeStmt(createTableFromCSV);

            String alterTable = "ALTER TABLE "+tempTableName+" ADD COLUMN (\"Sent\" varchar(5) DEFAULT '1', \"Batch Name\" varchar(100) DEFAULT '"+actualOutReach.getName()+"'," +
                    "\"Batch Id\" varchar(100) DEFAULT '"+actualOutReach.getCampaignId()+"', \"Template Name\" varchar(200) DEFAULT '"+actualEmailTemplate.getTitle()+"'," +
                    " \"Use Case\" varchar(25) DEFAULT 'Campaigns')";

            db.executeStmt(alterTable);
            db.executeStmt("ALTER TABLE "+tempTableName+" ALTER COLUMN \"Contact.Id\" RENAME TO \"Contact Id\"");
            db.executeStmt("ALTER TABLE "+tempTableName+" ALTER COLUMN \"Contact.Name\" RENAME TO \"Contact Name\"");
            db.executeStmt("ALTER TABLE "+tempTableName+" ALTER COLUMN \"Contact.Email\" RENAME TO \"Email Address\"");
            db.executeStmt("ALTER TABLE "+tempTableName+" ALTER COLUMN Id RENAME TO \"Account Id\"");
            db.executeStmt("ALTER TABLE "+tempTableName+" ALTER COLUMN Name RENAME TO \"Account Name\"");
            ResultSet resultSet = db.executeQry(query);
            db.executeStmt("call CSVWRITE ( '" + emailLogExpectedFile + "', '" + query + "' ) ");

        } finally {
            //Closing Db Connection
            db.close();
            new File(System.getProperty("user.home")+"/TEST.h2.db").delete();
        }
        List<Map<String, String>>  expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(emailLogExpectedFile)));
        Log.info("Expected :" + mapper.writeValueAsString(expectedData));
        Assert.assertEquals(Comparator.compareListData(expectedData, emailLogs).size(), 0, "Email Log Data Has Not Matched.");

        //Verifying schedule creation and update of a outreach.
        Schedule schedule = mapper.readValue(new File(testDataDir + "test/t4/T4_Schedule.json"), Schedule.class);
        schedule.setTitle(actualOutReach.getName());
        schedule.setDescription(actualOutReach.getName());

        schedule.setStartTime(DateUtil.addDays(getCalenderWithTimeSetToZero(schedule.getTimeZoneName()), (int) schedule.getStartTime()).getTimeInMillis());
        schedule.setEndTime(DateUtil.addDays(getCalenderWithTimeSetToZero(schedule.getTimeZoneName()), (int) schedule.getEndTime()).getTimeInMillis());

        Schedule actualSchedule = copilotAPI.scheduleOutReach(mapper.writeValueAsString(schedule));
        assertScheduleInformation(actualSchedule, schedule);
        schedule.setCronExpression("0 20 15 ? * 2-6 *");
        schedule.setScheduleId(actualSchedule.getScheduleId());
        actualSchedule = copilotAPI.updateSchedule(mapper.writeValueAsString(schedule));
        assertScheduleInformation(actualSchedule, schedule);

        ReportMaster reportMaster1 = reportManager.createTabularReportMaster(emailLogCollectionMaster,
                new String[]{"Account Id", "Account Name", "Batch Id", "Batch Name", "Contact Id", "Contact Name", "Email Address", "Internal Email ID"});

        ReportAdvanceFilter whereFilter = mapper.readValue("{\"filters\":[{\"dbName\":\"bid\",\"alias\":\"A\",\"dataType\":\"string\",\"filterOperator\":\"EQ\",\"filterValues\":[\"****************************\"],\"type\":0,\"logicalOperator\":\"AND\",\"collectionId\":\"*******************************\"},{\"dbName\":\"e\",\"alias\":\"B\",\"dataType\":\"string\",\"filterOperator\":\"EQ\",\"filterValues\":[\"********************************************\"],\"type\":0,\"logicalOperator\":\"AND\",\"collectionId\":\"***************************************\"}],\"expression\":\"A AND B\"}", ReportAdvanceFilter.class);
        whereFilter.getReportFilters().get(0).setFilterValues(Arrays.asList(new Object[]{actualOutReach.getCampaignId()}));
        whereFilter.getReportFilters().get(0).setCollectionId(emailLogCollectionMaster.getCollectionDetails().getCollectionId());
        whereFilter.getReportFilters().get(1).setFilterValues(Arrays.asList(new Object[]{"galbreathgali@automation.gainsighttest.com"}));
        whereFilter.getReportFilters().get(1).setCollectionId(emailLogCollectionMaster.getCollectionDetails().getCollectionId());
        reportMaster1.getReportInfo().get(0).setWhereAdvanceFilter(whereFilter);

        List<Map<String, String>> filteredEmailLogData = reportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(mapper.writeValueAsString(reportMaster1)), emailLogCollectionMaster);
        Assert.assertEquals(filteredEmailLogData.size(), 1, "Only one email should be returned.");
        String gsId  = filteredEmailLogData.get(0).get("Internal Email ID");
        Assert.assertNotNull(gsId, "gsid should not be null.");
        Log.info("Internal Email ID : " + gsId);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("category", UnSubscribeCategory.SUCCESS_COMMUNICATION.getValue()));
        nameValuePairs.add(new BasicNameValuePair("category", UnSubscribeCategory.SURVEY.getValue()));
        nameValuePairs.add(new BasicNameValuePair("reason", UnSubscribeReason.EMAIL_INAPPROPRIATE.getValue()));
        nameValuePairs.add(new BasicNameValuePair("t", Base64.encode(tenantInfo.getTenantId().getBytes()).toString()));
        String d = "{\"gsid\":\""+gsId+"\"}";
        nameValuePairs.add(new BasicNameValuePair("d", Base64.encode(d.getBytes()).toString()));

        ResponseObj responseObj = copilotAPI.unSubcribe(nameValuePairs);
        Assert.assertTrue(responseObj.getStatusCode() == HttpStatus.SC_OK);
        Assert.assertEquals(responseObj.getContent(), "Your preferences have been updated.");
        CollectionInfo unSubscribedEmailCollectionMaster = gsDataAPI.getCollectionMasterByName("Unsubscribed Emails");
        ReportMaster unSUBReportMaster = reportManager.createTabularReportMaster(unSubscribedEmailCollectionMaster, new String[]{"Account ID", "Account Name", "Category", "Contact ID", "Contact Name", "Email Address", "Reason"});
        ReportAdvanceFilter unSUBreportAdvanceFilter = mapper.readValue("{\"filters\":[{\"dbName\":\"e\",\"alias\":\"A\",\"dataType\":\"string\",\"filterOperator\":\"EQ\",\"filterValues\":[\"galbreathgali@automation.gainsighttest.com\"],\"type\":0,\"logicalOperator\":\"AND\",\"collectionId\":\"**********\"}],\"expression\":\"A\"}", ReportAdvanceFilter.class);
        unSUBreportAdvanceFilter.getReportFilters().get(0).setCollectionId(unSubscribedEmailCollectionMaster.getCollectionDetails().getCollectionId());
        unSUBReportMaster.getReportInfo().get(0).setWhereAdvanceFilter(unSUBreportAdvanceFilter);

        List<Map<String, String>> unSUBActualData = reportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(mapper.writeValueAsString(unSUBReportMaster)), unSubscribedEmailCollectionMaster);

        JobInfo unSUBTransformJob = mapper.readValue(new File(testDataDir + "/test/t4/T4_UnSubscribeDataTransform.json"), JobInfo.class);
        dataETL.execute(unSUBTransformJob);

        List<Map<String, String>> expectedUnSubExpectedData =  Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir+unSUBTransformJob.getTransformationRule().getOutputFileLoc())));
        Assert.assertEquals(Comparator.compareListData(expectedUnSubExpectedData, unSUBActualData).size(), 0, "No of diff records should be zero - This is checking the un-subscribed collection.");

    }

    @Test
    public void sendgridOutReachWebHookTest() throws Exception {
        SmartList smartList = mapper.readValue(new File(testDataDir + "test/t5/T5_SmartList.json"), SmartList.class);
        smartList.getAutomatedRule().setTriggerCriteria(resolveStrNameSpace(smartList.getAutomatedRule().getTriggerCriteria()));
        SmartList actualSmartList = copilotAPI.createSmartListExecuteAndGetSmartList(mapper.writeValueAsString(smartList));
        Assert.assertNotNull(actualSmartList.getSmartListId(), "Smart list Id should not be null.");
        rulesUtil.waitForProcessingToComplete(actualSmartList.getSmartListId());
        Assert.assertTrue(rulesUtil.isProcessionSuccess(actualSmartList.getSmartListId()), "Verifying smart list run status is successful.");
        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId()); //Once the rule is successfully run, get the stats and verify.
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), 30, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), 1, "Verifying customer count.");

        JobInfo jobInfo = mapper.readValue(new File(testDataDir + "test/t5/T5_DataTransform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        List<HashMap<String, Object>> actualData = copilotAPI.getSmartListData(actualSmartList.getSmartListId(), 0);
        List<Map<String, String>> parsedData = mapper.convertValue(actualData, List.class);
        List<Map<String, String>> expData  = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir + jobInfo.getTransformationRule().getOutputFileLoc())));

        System.out.println(expData.equals(parsedData));
        System.out.println(expData.equals(actualData));
        Assert.assertEquals(Comparator.compareListData(parsedData, expData).size(), 0, "No of Diff Records should be zero.");

        List<String> emailList = getValuesOfKeyAsList(expData, "Contact.Email");
        List<EmailProperties> emailProperties = copilotAPI.getEmailValidateProperties(emailList);
        Assert.assertTrue(copilotAPI.knockOffEmail(emailProperties));

        EmailTemplate emailTemplate = mapper.readValue(new File(testDataDir+"test/t5/T5_EmailTemplate.json"), EmailTemplate.class);
        EmailTemplate actualEmailTemplate = copilotAPI.createEmailTemplate(mapper.writeValueAsString(emailTemplate));
        Assert.assertNotNull(actualEmailTemplate.getTemplateId(), "Template id should not be null.");
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(emailTemplate, actualEmailTemplate), "Email template verification failed.");

        OutReach outReach = mapper.readValue(new File(testDataDir+ "test/t5/T5_OutReach.json"), OutReach.class);
        outReach.setSmartListId(actualSmartList.getSmartListId());
        outReach.setSmartListName(actualSmartList.getSmartListName());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateId(actualEmailTemplate.getTemplateId());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateName(actualEmailTemplate.getTitle());

        ObjectMapper mapper1 = new ObjectMapper().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        OutReach actualOutReach = copilotAPI.createOutReach(mapper1.writeValueAsString(outReach));
        String outReachStatusId = copilotAPI.triggerOutReach(actualOutReach.getCampaignId());

        rulesUtil.waitForRuleProcessingToComplete(outReachStatusId);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(outReachStatusId));

        RuleExecutionHistory executionHistory = rulesUtil.getLastExecution(actualOutReach.getCampaignId());
        Assert.assertNotNull(executionHistory);
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getFailCount(), 0);
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getPassCount(), 30);


        CollectionInfo emailLogCollectionMaster = copilotAPI.getEmailLogsCollection();
        Assert.assertNotNull(emailLogCollectionMaster, "Failed to fetch email logs collection info.");
        emailLogCollectionMaster = gsDataAPI.getCollectionMaster(emailLogCollectionMaster.getCollectionDetails().getCollectionId());
        Assert.assertNotNull(emailLogCollectionMaster, "Failed to fetch email logs collection info.");

        ReportMaster reportMaster = reportManager.createTabularReportMaster(emailLogCollectionMaster, new String[]{"Use Case", "Batch Id",
                "Batch Name", "Account Id", "Account Name", "Contact Id", "Contact Name", "Email Address", "Template Name", "External Email ID"});

        ReportAdvanceFilter reportAdvanceFilter = mapper.readValue("{\"filters\":[{\"dbName\":\"bid\",\"alias\":\"A\",\"dataType\":\"string\",\"filterOperator\":\"EQ\",\"filterValues\":[\"*****************\"],\"type\":0,\"logicalOperator\":\"AND\",\"collectionId\":\"************************\"}],\"expression\":\"A\"}", ReportAdvanceFilter.class);
        reportAdvanceFilter.getReportFilters().get(0).setFilterValues(Arrays.asList(new Object[]{actualOutReach.getCampaignId()}));
        reportAdvanceFilter.getReportFilters().get(0).setCollectionId(emailLogCollectionMaster.getCollectionDetails().getCollectionId());

        reportMaster.getReportInfo().get(0).setWhereAdvanceFilter(reportAdvanceFilter);


        List<Map<String, String>> emailLogs = reportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(mapper.writeValueAsString(reportMaster)), emailLogCollectionMaster);
        System.out.println("Actual " + mapper.writeValueAsString(emailLogs));
        String emailLogsFilePath = testDataDir+"process/t5/EmailLogData.csv";
        FileUtil.writeToCSV(emailLogs, emailLogsFilePath);

        String sourceWebHookFilePath = testDataDir + "test/t5/T5_WebhookEvents.csv";
        String finalWebHookEventFilePath = testDataDir+"process/t5/Final_WebHookEventData.csv";

        H2Db db = new H2Db("jdbc:h2:~/TEST"  ,"sa","");
        String SOURCE_WEBHOOK_EVENT_TABLE = "SOURCEWEBHOOK";
        String EMAIL_LOG_DATE_TABLE = "EMAILLOG";
        String FINAL_WEB_HOOK_TABLE = "WEBHOOK";
        try {

            db.executeStmt("DROP TABLE IF EXISTS "+SOURCE_WEBHOOK_EVENT_TABLE);
            db.executeStmt("DROP TABLE IF EXISTS "+EMAIL_LOG_DATE_TABLE);
            db.executeStmt("DROP TABLE IF EXISTS "+FINAL_WEB_HOOK_TABLE);

            db.executeStmt("CREATE TABLE SOURCEWEBHOOK AS SELECT * FROM CSVREAD('"+sourceWebHookFilePath+"')");
            db.executeStmt("CREATE TABLE EMAILLOG AS SELECT * FROM CSVREAD('"+emailLogsFilePath + "')");

            db.executeStmt("CREATE TABLE "+FINAL_WEB_HOOK_TABLE+" AS SELECT SOURCEWEBHOOK.\"CONTACTEMAIL\" as \"email\", SOURCEWEBHOOK.\"USECASE\" as \"useCase\"," +
                    " SOURCEWEBHOOK.\"EVENT\" as \"event\", EMAILLOG.\"Batch Id\" as \"campaign\", EMAILLOG.\"External Email ID\" as \"extId\"  " +
                    " FROM SOURCEWEBHOOK LEFT OUTER JOIN EMAILLOG ON SOURCEWEBHOOK.\"CONTACTEMAIL\"= EMAILLOG.\"Email Address\" " +
                    " AND SOURCEWEBHOOK.\"ACCOUNTNAME\" = EMAILLOG.\"Account Name\"");


            long timestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

            db.executeStmt("ALTER TABLE "+FINAL_WEB_HOOK_TABLE+" ADD COLUMN (\"subaccount\" varchar(50) DEFAULT '"+tenantInfo.getTenantId()+"'," +
                    " \"timestamp\" number(30) DEFAULT "+timestamp+", \"ip\" varchar(50) DEFAULT '111.93.24.238')");


            db.executeStmt("call CSVWRITE ( '" + finalWebHookEventFilePath + "', 'SELECT * from "+FINAL_WEB_HOOK_TABLE+"' ) ");

            List<Map<String, String>> eventMap = Comparator.getParsedCsvData(new CSVReader(new FileReader(finalWebHookEventFilePath)));
            List<SendgridWebhookEvent> webhookEvents = mapper.convertValue(eventMap, new TypeReference<ArrayList<SendgridWebhookEvent>>() {
            });
            List<String> events  = new ArrayList<>();
            for(SendgridWebhookEvent event : webhookEvents) {
                events.add("["+mapper.writeValueAsString(event)+"]");
            }

            Assert.assertTrue(copilotAPI.sendSendGridWebHookEvents(mapper.writeValueAsString(webhookEvents)));
            Assert.assertTrue(copilotAPI.sendSendGridWebHookEvents(events), "Seems all the events are not successful.");

            Thread.sleep(10000L);
            List<OutReachExecutionHistory> outReachExecutionHistoryList = copilotAPI.getOutReachExecutionHistory(actualOutReach.getCampaignId());
            Assert.assertTrue(outReachExecutionHistoryList.size()>0, "Atleast one outreach execution history should exists.");
            OutReachExecutionHistory expOutReachExeHistory = mapper.readValue(new File(testDataDir+"test/t5/T5_OutReachExeHistory.json"), OutReachExecutionHistory.class);
            assertOutReachExecutionHistory(outReachExecutionHistoryList.get(0), expOutReachExeHistory);


        } finally {
            db.close();
            new File(System.getProperty("user.home")+"/TEST.h2.db").delete();
        }
    }

    @Test
    public void mandrillOutReachWebHookTest() throws Exception {
        SmartList smartList = mapper.readValue(new File(testDataDir + "test/t6/T6_SmartList.json"), SmartList.class);
        smartList.getAutomatedRule().setTriggerCriteria(resolveStrNameSpace(smartList.getAutomatedRule().getTriggerCriteria()));
        SmartList actualSmartList = copilotAPI.createSmartListExecuteAndGetSmartList(mapper.writeValueAsString(smartList));
        Assert.assertNotNull(actualSmartList.getSmartListId(), "Smart list Id should not be null.");
        rulesUtil.waitForProcessingToComplete(actualSmartList.getSmartListId());
        Assert.assertTrue(rulesUtil.isProcessionSuccess(actualSmartList.getSmartListId()), "Verifying smart list run status is successful.");
        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId()); //Once the rule is successfully run, get the stats and verify.
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), 30, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), 1, "Verifying customer count.");

        JobInfo jobInfo = mapper.readValue(new File(testDataDir + "test/t6/T6_DataTransform.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        List<HashMap<String, Object>> actualData = copilotAPI.getSmartListData(actualSmartList.getSmartListId(), 0);
        List<Map<String, String>> parsedData = mapper.convertValue(actualData, List.class);
        List<Map<String, String>> expData  = Comparator.getParsedCsvData(new CSVReader(new FileReader(Application.basedir + jobInfo.getTransformationRule().getOutputFileLoc())));

        System.out.println(expData.equals(parsedData));
        System.out.println(expData.equals(actualData));
        Assert.assertEquals(Comparator.compareListData(parsedData, expData).size(), 0, "No of Diff Records should be zero.");

        List<String> emailList = getValuesOfKeyAsList(expData, "Contact.Email");
        List<EmailProperties> emailProperties = copilotAPI.getEmailValidateProperties(emailList);
        Assert.assertTrue(copilotAPI.knockOffEmail(emailProperties));

        EmailTemplate emailTemplate = mapper.readValue(new File(testDataDir+"test/t6/T6_EmailTemplate.json"), EmailTemplate.class);
        EmailTemplate actualEmailTemplate = copilotAPI.createEmailTemplate(mapper.writeValueAsString(emailTemplate));
        Assert.assertNotNull(actualEmailTemplate.getTemplateId(), "Template id should not be null.");
        Assert.assertTrue(copilotAPI.verifyEmailTemplate(emailTemplate, actualEmailTemplate), "Email template verification failed.");

        OutReach outReach = mapper.readValue(new File(testDataDir+ "test/t6/T6_OutReach.json"), OutReach.class);
        outReach.setSmartListId(actualSmartList.getSmartListId());
        outReach.setSmartListName(actualSmartList.getSmartListName());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateId(actualEmailTemplate.getTemplateId());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateName(actualEmailTemplate.getTitle());

        ObjectMapper mapper1 = new ObjectMapper().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        OutReach actualOutReach = copilotAPI.createOutReach(mapper1.writeValueAsString(outReach));
        String outReachStatusId = copilotAPI.triggerOutReach(actualOutReach.getCampaignId());

        rulesUtil.waitForRuleProcessingToComplete(outReachStatusId);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(outReachStatusId));

        RuleExecutionHistory executionHistory = rulesUtil.getLastExecution(actualOutReach.getCampaignId());
        Assert.assertNotNull(executionHistory);
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getFailCount(), 0);
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getPassCount(), 30);


        CollectionInfo emailLogCollectionMaster = copilotAPI.getEmailLogsCollection();
        Assert.assertNotNull(emailLogCollectionMaster, "Failed to fetch email logs collection info.");
        emailLogCollectionMaster = gsDataAPI.getCollectionMaster(emailLogCollectionMaster.getCollectionDetails().getCollectionId());
        Assert.assertNotNull(emailLogCollectionMaster, "Failed to fetch email logs collection info.");

        ReportMaster reportMaster = reportManager.createTabularReportMaster(emailLogCollectionMaster, new String[]{"Use Case", "Batch Id",
                "Batch Name", "Account Id", "Account Name", "Contact Id", "Contact Name", "Email Address", "Template Name", "External Email ID"});

        ReportAdvanceFilter reportAdvanceFilter = mapper.readValue("{\"filters\":[{\"dbName\":\"bid\",\"alias\":\"A\",\"dataType\":\"string\",\"filterOperator\":\"EQ\",\"filterValues\":[\"*****************\"],\"type\":0,\"logicalOperator\":\"AND\",\"collectionId\":\"************************\"}],\"expression\":\"A\"}", ReportAdvanceFilter.class);
        reportAdvanceFilter.getReportFilters().get(0).setFilterValues(Arrays.asList(new Object[]{actualOutReach.getCampaignId()}));
        reportAdvanceFilter.getReportFilters().get(0).setCollectionId(emailLogCollectionMaster.getCollectionDetails().getCollectionId());

        reportMaster.getReportInfo().get(0).setWhereAdvanceFilter(reportAdvanceFilter);


        List<Map<String, String>> emailLogs = reportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(mapper.writeValueAsString(reportMaster)), emailLogCollectionMaster);
        System.out.println("Actual " + mapper.writeValueAsString(emailLogs));
        String emailLogsFilePath = testDataDir+"process/t6/EmailLogData.csv";
        FileUtil.writeToCSV(emailLogs, emailLogsFilePath);

        String sourceWebHookFilePath = testDataDir + "test/t6/T6_WebhookEvents.csv";
        String finalWebHookEventFilePath = testDataDir+"process/t6/Final_WebHookEventData.csv";

        H2Db db = new H2Db("jdbc:h2:~/TEST"  ,"sa","");
        String SOURCE_WEBHOOK_EVENT_TABLE = "SOURCEWEBHOOK";
        String EMAIL_LOG_DATE_TABLE = "EMAILLOG";
        String FINAL_WEB_HOOK_TABLE = "WEBHOOK";
        try {

            db.executeStmt("DROP TABLE IF EXISTS "+SOURCE_WEBHOOK_EVENT_TABLE);
            db.executeStmt("DROP TABLE IF EXISTS "+EMAIL_LOG_DATE_TABLE);
            db.executeStmt("DROP TABLE IF EXISTS "+FINAL_WEB_HOOK_TABLE);

            db.executeStmt("CREATE TABLE SOURCEWEBHOOK AS SELECT * FROM CSVREAD('"+sourceWebHookFilePath+"')");
            db.executeStmt("CREATE TABLE EMAILLOG AS SELECT * FROM CSVREAD('"+emailLogsFilePath + "')");

            db.executeStmt("CREATE TABLE "+FINAL_WEB_HOOK_TABLE+" AS SELECT SOURCEWEBHOOK.\"CONTACTEMAIL\" as \"email\", SOURCEWEBHOOK.\"USECASE\" as \"useCase\"," +
                    " SOURCEWEBHOOK.\"EVENT\" as \"event\", EMAILLOG.\"Batch Id\" as \"campaign\", EMAILLOG.\"External Email ID\" as \"extId\"  " +
                    " FROM SOURCEWEBHOOK LEFT OUTER JOIN EMAILLOG ON SOURCEWEBHOOK.\"CONTACTEMAIL\"= EMAILLOG.\"Email Address\" " +
                    " AND SOURCEWEBHOOK.\"ACCOUNTNAME\" = EMAILLOG.\"Account Name\"");

            //As Mandrill only gives seconds removing the millisecond part in time.
            int timestamp = (int)(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000);

            db.executeStmt("ALTER TABLE " + FINAL_WEB_HOOK_TABLE + " ADD COLUMN (\"subaccount\" varchar(50) DEFAULT '" + tenantInfo.getTenantId() + "'," +
                    " \"ts\" number(30) DEFAULT " + timestamp + ", \"ip\" varchar(50) DEFAULT '111.93.24.238')");


            db.executeStmt("call CSVWRITE ( '" + finalWebHookEventFilePath + "', 'SELECT * from " + FINAL_WEB_HOOK_TABLE + "' ) ");

            List<Map<String, String>> eventMap = Comparator.getParsedCsvData(new CSVReader(new FileReader(finalWebHookEventFilePath)));

            List<MandrillWebhookEvent> mandrillWebhookEvents = constructMandrillEvents(eventMap);
            Assert.assertTrue(copilotAPI.sendMandrillWebHookEventsInBulk(mandrillWebhookEvents));
            //Assert.assertTrue(copilotAPI.sendMandrillWebHookEventsOneByOne(mandrillWebhookEvents));

            Thread.sleep(5000L);
            List<OutReachExecutionHistory> outReachExecutionHistoryList = copilotAPI.getOutReachExecutionHistory(actualOutReach.getCampaignId());
            Assert.assertTrue(outReachExecutionHistoryList.size()>0, "Atleast one outreach execution history should exists.");
            OutReachExecutionHistory expOutReachExeHistory = mapper.readValue(new File(testDataDir + "test/t6/T6_OutReachExeHistory.json"), OutReachExecutionHistory.class);
            assertOutReachExecutionHistory(outReachExecutionHistoryList.get(0), expOutReachExeHistory);


        } finally {
            db.close();
            new File(System.getProperty("user.home")+"/TEST.h2.db").delete();
        }
    }


     //@Test
     public void deleteAll() throws Exception {
         deleteAllOutreaches();
         deleteAllEmailTemplates();
         deleteAllSmartList();
     }


    //@Test
    public void deleteAllSmartList() throws Exception {
        List<SmartList> smartLists = copilotAPI.getAllSmartList();
        for(SmartList smartList : smartLists) {
            System.out.println(copilotAPI.deleteSmartList(smartList.getSmartListId()));
        }
    }


    //@Test
    public void deleteAllEmailTemplates() throws Exception {
        List<EmailTemplate> emailTemplates = copilotAPI.getAllEmailTemplates();
        for(EmailTemplate emailTemplate : emailTemplates) {
            System.out.println(copilotAPI.deleteEmailTemplate(emailTemplate.getTemplateId()));
        }
    }

    //@Test
    public void deleteAllOutreaches() throws Exception {
        List<OutReach> outReaches = copilotAPI.getAllOutReach();
        for(OutReach outReach : outReaches) {
            System.out.println(copilotAPI.deleteOutReach(outReach.getCampaignId()));
        }
    }

    private List<MandrillWebhookEvent> constructMandrillEvents(List<Map<String, String>> dataList) {
        List<MandrillWebhookEvent> eventList = new ArrayList<>();
        for(Map<String, String> data : dataList) {
            Metadata metadata = new Metadata();
            metadata.setCampaign(data.get("campaign"));
            metadata.setExternalId(data.get("extId"));
            metadata.setUseCase(data.get("useCase"));
            Message message  =  new Message();
            message.setEmail(data.get("email"));
            message.setMetadata(metadata);
            message.setSubaccount(data.get("subaccount"));
            message.setTs(Long.valueOf(data.get("ts")));
            message.setSender(data.get("email"));
            MandrillWebhookEvent webhookEvent = new MandrillWebhookEvent();
            webhookEvent.setEvent(MandrillEvent.valueOf(data.get("event")));
            webhookEvent.setTs(Long.valueOf(data.get("ts")));
            webhookEvent.setIp(data.get("ip"));
            webhookEvent.setMessage(message);
            eventList.add(webhookEvent);
        }
        return eventList;
    }

    private List<String> getValuesOfKeyAsList(List<Map<String,String>> dataList, String key) {
        if(dataList ==null || key ==null) {
            throw new IllegalArgumentException("Either of data list & key should not be null.");
        }
        List<String> valueList = new ArrayList<>();
        for(Map<String, String> data : dataList) {
            if(data.containsKey(key) && data.get(key)!=null && !data.get(key).isEmpty()) {
                valueList.add(data.get(key));
            }
        }
        return valueList;
    }

    private Calendar getCalenderWithTimeSetToZero(String timeZone) {
        Calendar tempCal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        tempCal.set(Calendar.HOUR, 0);
        tempCal.set(Calendar.MINUTE, 0);
        tempCal.set(Calendar.SECOND, 0);
        tempCal.set(Calendar.MILLISECOND, 0);
        tempCal.set(Calendar.HOUR_OF_DAY, 0);
        return tempCal;
    }

    private void assertScheduleInformation(Schedule actual, Schedule expected) {
        Assert.assertNotNull(actual, "Actual schedule should not be null.");
        Assert.assertNotNull(expected, "Expected schedule should not be null.");
        Assert.assertNotNull(actual.getScheduleId(), "Schedule Id in actual should not be null.");
        Verifier verifier = new Verifier();
        verifier.verifyEquals(actual.getStartTime(), expected.getStartTime(), "Start Time of schedule not matched.");
        verifier.verifyEquals(actual.getEndTime(), expected.getEndTime(), "End Time of schedule not matched.");
        verifier.verifyEquals(actual.getTimeZoneName(), expected.getTimeZoneName(), "Time Zone Not matched.");
        verifier.verifyEquals(actual.getJobType(), expected.getJobType(), "Job type not matched and should be CAMPAIGN");
        verifier.verifyEquals(actual.getJobIdentifier(), expected.getJobIdentifier(), "Job Identifier Not Matched.");
        verifier.verifyEquals(actual.isPastRunsAlso(), expected.isPastRunsAlso(), "Pass Run Not Matched.");
        verifier.verifyEquals(actual.getJobContext().get("scheduleType"), expected.getJobContext().get("scheduleType"), "Schedule Type not Matched.");
        if(verifier.isVerificationFailed()) {
            Log.error(verifier.getAssertMessages().toString());
            Assert.assertTrue(false, verifier.getAssertMessages().toString());
        }
    }

    private void assertOutReachExecutionHistory(OutReachExecutionHistory actual, OutReachExecutionHistory expected) {
        Assert.assertNotNull(actual, "Actual Out Reach Execution History should be null.");
        Assert.assertNotNull(expected, "Expected Out Reach Execution History should be null.");
        Verifier verifier = new Verifier();
        verifier.verifyEquals(actual.getnAccounts(), expected.getnAccounts(), "Account Count Not Matched.");
        verifier.verifyEquals(actual.getnContacts(), expected.getnContacts(), "Contact Count Not Matched.");
        verifier.verifyEquals(actual.getnSent(), expected.getnSent(), "Sent Count not matched");
        verifier.verifyEquals(actual.getNoOfUnSubscribed(), expected.getNoOfUnSubscribed(), "Un-Subscribed Count not matched");
        verifier.verifyEquals(actual.getPassCount(), expected.getPassCount(),"Passed Count not matched");
        verifier.verifyEquals(actual.getFailCount(), expected.getFailCount(), "Failed Count not matched");
        verifier.verifyEquals(actual.getnOpened(), expected.getnOpened(), "Opened Count not matched");
        verifier.verifyEquals(actual.getnRejected(), expected.getnRejected(), "Rejected Count not matched");
        verifier.verifyEquals(actual.getExecutionType(), expected.getExecutionType(), "Execution Type not matched");
        verifier.verifyEquals(actual.getnClicked(), expected.getnClicked(), "No of clicked count not matched");
        verifier.verifyEquals(actual.getNoOfSoftBounce(), expected.getNoOfSoftBounce(), "Softbounce Count not matched");
        verifier.verifyEquals(actual.getnSpam(), expected.getnSpam(), "Spam Count not matched");
        verifier.verifyEquals(actual.getNoOfHardBounce(), expected.getNoOfHardBounce(), "Hard Bounce Count not matched");
        verifier.verifyEquals(actual.getStatus(), expected.getStatus(), "Execution status not matched");
        if(verifier.isVerificationFailed()) {
            Log.error(verifier.getAssertMessages().toString());
            Assert.assertTrue(false, verifier.getAssertMessages().toString());
        }
    }






}
