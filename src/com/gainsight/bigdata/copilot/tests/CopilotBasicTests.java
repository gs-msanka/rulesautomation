package com.gainsight.bigdata.copilot.tests;

import com.gainsight.bigdata.copilot.apiImpl.CopilotAPIImpl;
import com.gainsight.bigdata.copilot.bean.emailTemplate.EmailTemplate;
import com.gainsight.bigdata.copilot.bean.outreach.OutReach;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartList;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by Sridivya on 1/11/2016.
 * Class covers the basic tests , with simple data sets, no special combinations
 * Just 5 cases for the 5 strategies - Account,Contact,User,Email and No strategy
 * Each of the case is end-end
 */
public class CopilotBasicTests extends CopilotTestUtils {

    /**
     * Create Smart list
     * Update Smart list
     * Delete Smart list
     *
     * @throws Exception
     */

    @BeforeClass
    public void setUp() throws Exception {
        copilotAPI = new CopilotAPIImpl(header);
        gsDataAPI = new GSDataImpl(header);
        tenantInfo = gsDataAPI.getTenantInfo(sfinfo.getOrg());
/*
        if (true) {
            createCustomFields();
            cleanAndGenerateData();
        }
        */
    }

    @Test
    public void accountStrategyAccountAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = updateSmartListActionInfo(actualSmartList, "{\"type\":\"calculated\",\"valueType\":\"BOOLEAN\",\"expression\":{\"alias\":\"undefined\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Contact\",\"field\":\"Contact.Name\",\"fieldName\":\"Name\",\"objectName\":\"Contact\",\"fieldType\":\"STRING\",\"label\":\"Full Name\",\"isExternalCriteria\":true},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"giri\"}}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Account strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Account strategy Powerlist Name", "Failed to Update Account strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 2);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "ACCOUNT STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "ACCOUNT STRATEGY OUTREACH NAME UPDATED", "Failed to Update Account strategy outreach Name");

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
    public void contactStrategyAccountAsBaseObject() throws Exception {

        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T2_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = updateSmartListActionInfo(actualSmartList, "{\"type\":\"calculated\",\"valueType\":\"BOOLEAN\",\"expression\":{\"alias\":\"undefined\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Contact\",\"field\":\"Contact.Name\",\"fieldName\":\"Name\",\"objectName\":\"Contact\",\"fieldType\":\"STRING\",\"label\":\"Full Name\",\"isExternalCriteria\":true},\"operator\":\"ne\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"giri\"}}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Contact strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Contact strategy Powerlist Name", "Failed to Update Contact strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T2_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 2);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "CONTACT STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "CONTACT STRATEGY OUTREACH NAME UPDATED", "Failed to Update Contact strategy Outreach Name");

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
    public void noStrategyContactAsBaseObject() throws Exception {

        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T3_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "(A AND B) OR C ", "{\"alias\":\"C\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Contact\",\"field\":\"Account.Name\",\"fieldName\":\"Account.Name\",\"objectName\":\"Contact\",\"fieldType\":\"TEXT\",\"label\":\"Account ID Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,45,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated No strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated No strategy Powerlist Name", "Failed to Update No strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 45, 2, 45);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T3_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 45, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 45);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 45, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "NO STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "NO STRATEGY OUTREACH NAME UPDATED", "Failed to Update No strategy Outreach Name");

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
    public void userStrategyAccountAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T4_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Galbreath\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,1,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated User strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated User strategy Powerlist Name", "Failed to Update User strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 1, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T4_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 2);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "USER STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "USER STRATEGY OUTREACH NAME UPDATED", "Failed to Update User strategy Outreach Name");

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
    public void emailStrategyAccountAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T5_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Galbreath\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Email strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Email strategy Powerlist Name", "Failed to Update email strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T5_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 2);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "EMAIL STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "EMAIL STRATEGY OUTREACH NAME UPDATED", "Failed to Update Email strategy Outreach Name");

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
    public void accountStrategyCustomerinfoAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T6_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A AND B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"JBCXM__CustomerInfo__c\",\"field\":\"JBCXM__MRR__c\",\"fieldName\":\"JBCXM__MRR__c\",\"objectName\":\"JBCXM__CustomerInfo__c\",\"fieldType\":\"CURRENCY\",\"label\":\"MRR\",\"isExternalCriteria\":false},\"operator\":\"eq\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"204\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,30,1);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Account strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Account strategy Powerlist Name", "Failed to Update Account strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 30, 1, 30);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T6_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 30, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 30);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 30, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "ACCOUNT STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "ACCOUNT STRATEGY OUTREACH NAME UPDATED", "Failed to Update Account strategy outreach Name");

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
    public void contactStrategyCustomerinfoAsBaseObject() throws Exception {

        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T7_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Contact strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Contact strategy Powerlist Name", "Failed to Update Contact strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T7_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 2);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "CONTACT STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "CONTACT STRATEGY OUTREACH NAME UPDATED", "Failed to Update Contact strategy Outreach Name");

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
    public void userStrategyCustomerinfoAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T8_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,1,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated User strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated User strategy Powerlist Name", "Failed to Update User strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 1, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T8_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 2);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "USER STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "USER STRATEGY OUTREACH NAME UPDATED", "Failed to Update User strategy Outreach Name");

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
    public void emailStrategyCustomerinfoAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/Basic_cases/T9_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Email strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Email strategy Powerlist Name", "Failed to Update email strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/Basic_cases/T9_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 2);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 2, 0);
        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "EMAIL STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "EMAIL STRATEGY OUTREACH NAME UPDATED", "Failed to Update Email strategy Outreach Name");

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

}
