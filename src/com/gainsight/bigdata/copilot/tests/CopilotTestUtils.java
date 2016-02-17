    package com.gainsight.bigdata.copilot.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.copilot.apiImpl.CopilotAPIImpl;
import com.gainsight.bigdata.copilot.bean.emailTemplate.EmailTemplate;
import com.gainsight.bigdata.copilot.bean.outreach.OutReach;
import com.gainsight.bigdata.copilot.bean.outreach.OutReachExecutionHistory;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartList;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.MandrillEvent;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.MandrillWebhookEvent;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.Message;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.Metadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.RuleExecutionHistory;
import com.gainsight.bigdata.pojo.Schedule;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInfo;
import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionInnerCondition;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Criteria;
import com.gainsight.bigdata.rulesengine.bean.RuleSetup.TriggerCriteria;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.Verifier;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import com.gainsight.util.MetaDataUtil;
import com.gainsight.bigdata.pojo.ObjectFields;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Srividya on 1/11/2016.
 */
public class CopilotTestUtils extends NSTestBase {
    CopilotAPIImpl copilotAPI;
    GSDataImpl gsDataAPI;
    String testDataDir  = Application.basedir+"/testdata/newstack/CoPilot/dataSet/";
    DataETL dataETL     = new DataETL();
    RulesUtil rulesUtil = new RulesUtil();
    TenantInfo tenantInfo;
    ReportManager reportManager = new ReportManager();
    Calendar calendar = Calendar.getInstance();

     public CopilotTestUtils(){
     }

    /**
     * Create,run and Validate the contact and customer count for the smartlist created
     * @param smartList - the smartlist that has been executed
     * @param contactCount
     * @param customerCount
     * @return
     * @throws Exception
     */
     protected SmartList createAndValidateSmartList(SmartList smartList,int contactCount,int customerCount) throws Exception{
        //Create a smart list and execute and verify the smart list stats.
        smartList.getAutomatedRule().setTriggerCriteria(resolveStrNameSpace(smartList.getAutomatedRule().getTriggerCriteria()));
        SmartList actualSmartList = copilotAPI.createSmartListExecuteAndGetSmartList(mapper.writeValueAsString(smartList));
        Assert.assertNotNull(actualSmartList.getSmartListId(), "Smart list Id should not be null.");
        return runSmartList(actualSmartList,contactCount,customerCount);
    }

    /**
     * Run the Powerlist and verify the contact and customer counts from metadata
     * @param actualSmartList
     * @param contactCount
     * @param customerCount
     * @return
     * @throws Exception
     */
    protected SmartList runSmartList(SmartList actualSmartList,int contactCount,int customerCount) throws Exception {
        rulesUtil.waitForProcessingToComplete(actualSmartList.getSmartListId());
        Assert.assertTrue(rulesUtil.isProcessionSuccess(actualSmartList.getSmartListId()), "Verifying smart list run status is successful.");
        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId()); //Once the rule is successfully run, get the stats and verify.
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), contactCount, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), customerCount, "Verifying customer count.");
        return actualSmartList;
    }

    /**
     * Run the smartlist after updating it
     * @param actualSmartList
     * @param contactCount
     * @param custCount
     * @return
     * @throws Exception
     */
    protected SmartList runUpdatedSmartList(SmartList actualSmartList,int contactCount,int custCount) throws Exception{
        actualSmartList = copilotAPI.updateSmartList(mapper.writeValueAsString(actualSmartList));
        return runSmartList(actualSmartList,contactCount,custCount);
    }

    /**
     * Update just the trigger Criteria of the powerlist
     * @param actualSmartList
     * @param whereCondition
     * @param criteria
     * @return
     * @throws Exception
     */
    protected SmartList updateSmartListTriggerCriteria(SmartList actualSmartList,String whereCondition,String criteria) throws Exception{
        List<TriggerCriteria> triggerCriteriaList = mapper.readValue(actualSmartList.getAutomatedRule().getTriggerCriteria(), new TypeReference<ArrayList<TriggerCriteria>>() {});
        TriggerCriteria triggerCriteria = triggerCriteriaList.get(0);
        triggerCriteria.setWhereLogic(whereCondition);
        Criteria crit = mapper.readValue(criteria, Criteria.class);
        triggerCriteria.getCriteria().add(crit);
        actualSmartList.getAutomatedRule().setTriggerCriteria(mapper.writeValueAsString(triggerCriteriaList));
        return actualSmartList;
    }

    /**
     * Update just the action info of a powerlist
     * @param actualSmartList
     * @param innerCondition
     * @return
     * @throws Exception
     */
    protected SmartList updateSmartListActionInfo(SmartList actualSmartList,String innerCondition) throws Exception {
        ActionInfo actionInfo = mapper.readValue(actualSmartList.getAutomatedRule().getActionDetails().get(0).getActionInfo(), ActionInfo.class);
        ActionInnerCondition actionInnerCondition = mapper.readValue(innerCondition, ActionInnerCondition.class);
        actionInfo.getCondition().getExpression().getRight().add(actionInnerCondition);
        actualSmartList.getAutomatedRule().getActionDetails().get(0).setActionInfo(mapper.writeValueAsString(actionInfo));
        actualSmartList.setShowFields(null);
        return actualSmartList;
    }

    /**
     * Re-sync the smartlist such that the meta data is modified by this action
     * @param actualSmartList
     * @param newContactCount
     * @param newCustCount
     * @param smartListCount
     * @throws Exception
     */
    protected void reSyncSmartList(SmartList actualSmartList,int newContactCount,int newCustCount,int smartListCount) throws Exception{
        String smartListReSyncStatusId = copilotAPI.reSyncSmartListData(actualSmartList.getSmartListId());  //If resync is triggered with data modification then it make sense.
        Assert.assertNotNull(smartListReSyncStatusId, "Status Id should not be null.");
        rulesUtil.waitForRuleProcessingToComplete(smartListReSyncStatusId);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(smartListReSyncStatusId), "Verifying smart list re-sync is successful.");

        actualSmartList = copilotAPI.getSmartList(actualSmartList.getSmartListId());
        Assert.assertEquals(actualSmartList.getStats().getContactCount(), newContactCount, "Verifying contact count.");
        Assert.assertEquals(actualSmartList.getStats().getCustomerCount(), newCustCount, "Verifying customer count.");

        List<HashMap<String, Object>> testData = copilotAPI.getSmartListData(actualSmartList.getSmartListId(), 0);
        Assert.assertEquals(testData.size(), smartListCount, "Expected Value should be "+smartListCount+" records in the smart list data.");
    }

    /**
     * Create and check if the email template is successfully created
     * @param emailTemplate
     * @return
     * @throws Exception
     */
    protected EmailTemplate createAndValidateEmailTemplate(EmailTemplate emailTemplate) throws Exception{
        EmailTemplate actualEmailTemplate = copilotAPI.createEmailTemplate(mapper.writeValueAsString(emailTemplate));
        Assert.assertNotNull(actualEmailTemplate.getTemplateId(), "Template id should not be null.");
        Assert.assertEquals(actualEmailTemplate.getTitle(), emailTemplate.getTitle(), "Verifying email template title.");
        return actualEmailTemplate;

    }

    /**
     * Creating mandrill webhook events to simulate webhooks
     * @param dataList
     * @return
     */
    protected List<MandrillWebhookEvent> constructMandrillEvents(List<Map<String, String>> dataList) {
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

    /**
     * Create,trigger and validate outreach creation
     * @param outReach
     * @param actualSmartList
     * @param actualEmailTemplate
     * @return
     * @throws Exception
     */
    protected OutReach createAndTriggerOutreach(OutReach outReach,SmartList actualSmartList,EmailTemplate actualEmailTemplate) throws Exception {

        outReach.setSmartListId(actualSmartList.getSmartListId());
        outReach.setSmartListName(actualSmartList.getSmartListName());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateId(actualEmailTemplate.getTemplateId());
        outReach.getDefaultECA().get(0).getActions().get(0).setEmailTemplateName(actualEmailTemplate.getTitle());

        OutReach actualOutReach = copilotAPI.createOutReach(mapper.writeValueAsString(outReach));
        Assert.assertNotNull(actualOutReach, "Out Reach creation failed.");
        Assert.assertNotNull(actualOutReach.getCampaignId(), "Out reach Id should not be null.");
        actualOutReach=triggerOutreach(actualOutReach);

       return actualOutReach;

    }

    /**
     * Outreach trigger and validate the processing status
     * @param actualOutReach
     * @return
     * @throws Exception
     */
    protected OutReach triggerOutreach(OutReach actualOutReach) throws Exception{
        //Trigger out reach, wait for outreach process, verify the execution history of out reach.
        String statusId=copilotAPI.triggerOutReach(actualOutReach.getCampaignId());
        actualOutReach.setStatusId(statusId);
        rulesUtil.waitForRuleProcessingToComplete(statusId);
        Assert.assertTrue(rulesUtil.isRuleProcessingSuccessful(statusId), "Out reach process is not successful.");

        actualOutReach = copilotAPI.getOutReach(actualOutReach.getCampaignId());
        actualOutReach.setStatusId(statusId);
        return actualOutReach;

    }

    /**
     * Check the outreach execution history
     * @param outReachStatusId
     * @param passCount
     * @param failCount
     */
    protected void verifyOutReachExecutionHistory(String outReachStatusId,int passCount,int failCount){
        RuleExecutionHistory executionHistory = rulesUtil.getExecutionHistory(outReachStatusId);
        Assert.assertTrue(executionHistory.getProcessReport().getActionResults().get(0).getActionName().toLowerCase().contains("Send Email using Gainsight".toLowerCase()));
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getPassCount(), passCount, "Passed count should be "+passCount);
        Assert.assertEquals(executionHistory.getProcessReport().getActionResults().get(0).getFailCount(), failCount, "Failed count should be "+failCount);
    }

    /**
     * Verify the template being used in Outreach
     * @param emailTemplate
     * @param outreach
     * @throws Exception
     */
    protected void verifyTemplateUsedInOutreach(EmailTemplate emailTemplate,OutReach outreach) throws Exception{
        HashMap<String, List<HashMap<String,String>>> templateReferenceList = copilotAPI.getEmailTemplatesOutReachInfo(new String[]{emailTemplate.getTemplateId()});
        List<HashMap<String, String>> templateReference = templateReferenceList.get(emailTemplate.getTemplateId());
        Assert.assertNotNull(templateReference);
        Assert.assertEquals(templateReference.size(), 1);
        Assert.assertEquals(templateReference.get(0).get("outreachId"), outreach.getCampaignId(), "Out reach id not matched.");
        Assert.assertEquals(templateReference.get(0).get("outreachName"), outreach.getName(), "Out reach name not matched.");

    }

    /**
     * Utility method to get the values from a hashmap as list
     * @param dataList
     * @param key
     * @return
     */
    protected List<String> getValuesOfKeyAsList(List<Map<String,String>> dataList, String key) {
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

    /**
     * Verify the schedule information.
     * Cron expression, timezone, Job identifier, starttime, end time, and job context etc.
     * @param actual
     * @param expected
     */
    protected void assertScheduleInformation(Schedule actual, Schedule expected) {
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

    /**
     * Validates outreach execution history.
     * Accounts, Contacts, Sent, Opened and many more outreach execution parameter's.
     * @param actual
     * @param expected
     */
    protected void assertOutReachExecutionHistory(OutReachExecutionHistory actual, OutReachExecutionHistory expected) {
        Assert.assertNotNull(actual, "Actual Out Reach Execution History should be null.");
        Assert.assertNotNull(expected, "Expected Out Reach Execution History should be null.");
        Verifier verifier = new Verifier();
        verifier.verifyEquals(actual.getnAccounts(), expected.getnAccounts(), "Account Count Not Matched.");
        verifier.verifyEquals(actual.getnContacts(), expected.getnContacts(), "Contact Count Not Matched.");
        verifier.verifyEquals(actual.getnSent(), expected.getnSent(), "Sent Count not matched");
        verifier.verifyEquals(actual.getNoOfUnSubscribed(), expected.getNoOfUnSubscribed(), "Un-Subscribed Count not matched");
        verifier.verifyEquals(actual.getPassCount(), expected.getPassCount(), "Passed Count not matched");
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

    /**
     * DataGen for Co-pilot tests
     * Create required Custom fields on Account,Contact,Customer_Info and Case objects:
     * @throws Exception
     */
    public void createCustomFields() throws Exception {

        metadataClient.createCustomObject("C_ContactObject");
        metadataClient.createCustomObject("C_ContactObject2");

        ObjectFields objField_account,objField_contact,objField_case,objField_cusObj,objField_CusInfo,objField_ContactObj2;
        MetaDataUtil md=new MetaDataUtil();
        objField_account = mapper.readValue(new File(testDataDir+"jsons/Account_Fields.json"), ObjectFields.class);
        md.createFieldsOnObject(sfdc, "Account", objField_account);

        objField_CusInfo=mapper.readValue(new File(testDataDir + "jsons/Customer_Fields.json"), ObjectFields.class);
        md.createFieldsOnObject(sfdc, "JBCXM__CustomerInfo__c", objField_CusInfo);

        objField_cusObj=mapper.readValue(new File(testDataDir + "jsons/Cust_Fields.json"), ObjectFields.class);
        md.createFieldsOnObject(sfdc, "C_ContactObject__c", objField_cusObj);

        objField_contact= mapper.readValue(new File(testDataDir + "jsons/Contact_Fields.json"), ObjectFields.class);
        md.createFieldsOnObject(sfdc, "Contact", objField_contact);

        objField_case= mapper.readValue(new File(testDataDir + "jsons/Case_Fields.json"), ObjectFields.class);
        md.createFieldsOnObject(sfdc, "Case", objField_case);

        //Create Only ExternalID for "C_ContactObject2__c"
        objField_ContactObj2=mapper.readValue("{\"externalID_Text\":[\"p_c_ExternalID\"]}x`", ObjectFields.class);
        md.createFieldsOnObject(sfdc, "C_ContactObject2__c", objField_ContactObj2);
    }

    /**
     * Generate Data for Account,Case.Contact.CustomerInfo & 2 custom objects
     * @throws Exception
     */
    public void cleanAndGenerateData() throws Exception {
        //Clean Data
        sfdc.runApexCode("delete [select id from jbcxm__customerInfo__c];"+
                "delete [select id from Opportunity];"+
                "delete [select id from Case];"+
                "delete [select id from contact];"+
                "delete [select id from C_ContactObject__c];"+
                "delete [select id from C_ContactObject2__c];"+
                "delete [select id from Account];");
        dataETL = new DataETL();
        loadToObject(dataETL, testDataDir + "/Job/job_C_ContactObject2.txt");
        //Initializing Account,Customer Info,Case,Contact
        loadToObject(dataETL, testDataDir + "/Job/job_Account-Init.txt");
        loadToObject(dataETL, testDataDir + "/Job/job_CustomerInfo-Init.txt");
        loadToObject(dataETL, testDataDir + "/Job/job_Contact-Init.txt");
        loadToObject(dataETL, testDataDir + "/Job/job_Case-Init.txt");
        //Updating Account,Customer Info,Case,Contact (Loading twice since there are lookups between these objects)
        loadToObject(dataETL, testDataDir + "/Job/job_Account.txt");
        loadToObject(dataETL, testDataDir + "/Job/job_CustomerInfo.txt");
        loadToObject(dataETL, testDataDir + "/Job/job_Contact.txt");
        loadToObject(dataETL, testDataDir + "/Job/job_Case.txt");
        loadToObject(dataETL, testDataDir + "/Job/job_C_ContactObject1.txt");
    }

    /**
     * Utility method
     * @param dataETL
     * @param job
     * @throws IOException
     */
    public void loadToObject(DataETL dataETL, String job) throws IOException {
        ObjectMapper mapper     = new ObjectMapper();
        if(job != null && job != "") {
            JobInfo jobInfo= mapper.readValue((new FileReader(job)), JobInfo.class);
            dataETL.execute(jobInfo);
        }
    }
}
