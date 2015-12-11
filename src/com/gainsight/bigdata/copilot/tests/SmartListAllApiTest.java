package com.gainsight.bigdata.copilot.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.copilot.apiImpl.CopilotAPIImpl;
import com.gainsight.bigdata.copilot.bean.emailTemplate.EmailTemplate;
import com.gainsight.bigdata.copilot.bean.outreach.OutReach;
import com.gainsight.bigdata.copilot.bean.smartlist.ActionDetails;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartList;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartListRule;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.*;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionFieldInfo;
import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInfo;
import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInnerCondition;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Criteria;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.TriggerCriteria;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.utils.MongoUtil;
import org.apache.http.HttpStatus;
import org.bson.Document;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
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

    @BeforeClass
    public void setUp() throws Exception {
        copilotAPI = new CopilotAPIImpl(header);
        gsDataAPI = new GSDataImpl(header);
        tenantInfo = gsDataAPI.getTenantInfo(sfinfo.getOrg());
        if(false) {
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

        ResponseObj responseObj = copilotAPI.saveSmartList(mapper.writeValueAsString(actualSmartList));

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
        Assert.assertEquals(emailLogCollection.getCollectionDetails().getDbType(), "dbType");
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

        HashMap<String, RuleExecutionHistory> statsMap = copilotAPI.getAllSmartListStatus();
        Assert.assertNotNull(statsMap.get(actualSmartList1.getSmartListId()));
        Assert.assertNotNull(statsMap.get(actualSmartList2.getSmartListId()));

        Assert.assertNotNull(statsMap.get(actualSmartList1.getSmartListId()).getStatus(), "Smart list Execution status should not be null.");
        Assert.assertNotNull(statsMap.get(actualSmartList2.getSmartListId()).getStatus(), "Smart list Execution status should not be null.");

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
    public void outReachAdvancedOptions() {

    }




    @Test
    public void deleteAllSmartList() throws Exception {
        List<SmartList> smartLists = copilotAPI.getAllSmartList();
        for(SmartList smartList : smartLists) {
            System.out.println(copilotAPI.deleteSmartList(smartList.getSmartListId()));
        }
    }


    @Test
    public void deleteAllEmailTemplates() throws Exception {
        List<EmailTemplate> emailTemplates = copilotAPI.getAllEmailTemplates();
        for(EmailTemplate emailTemplate : emailTemplates) {
            System.out.println(copilotAPI.deleteSmartList(emailTemplate.getTemplateId()));
        }
    }

    @Test
    public void deleteAllOutreaches() throws Exception {
        List<OutReach> outReaches = copilotAPI.getAllOutReach();
        for(OutReach outReach : outReaches) {
            System.out.println(copilotAPI.deleteSmartList(outReach.getCampaignId()));
        }
    }
    @Test
    public void sample() throws Exception {
        GSDataImpl gsData = new GSDataImpl(header);
        CollectionInfo collectionInfo = gsData.getCollectionMaster("5cd07a69-ce2e-4d44-9443-7bd306ffdf2b");

        System.out.println(collectionInfo.toString());
    }





}
