package com.gainsight.bigdata.copilot.tests;

import com.gainsight.bigdata.copilot.apiImpl.CopilotAPIImpl;
import com.gainsight.bigdata.copilot.bean.emailTemplate.EmailTemplate;
import com.gainsight.bigdata.copilot.bean.outreach.OutReach;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartList;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;
import com.google.common.collect.Maps;
import com.sforce.soap.partner.sobject.SObject;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.apache.commons.lang.text.StrSubstitutor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by agrandhi on 29/03/16.
 */

/**
 * Class covers all strategies with Account, Contact, CustomerInfo, Case as base object
 * Each test case covers Create pl, update pl, refresh pl,
 * Create template, update template,
 * Create Outreach, update outreach, trigger outreach
 * Validate 'preventDuplicateDays' check
 * Delete pl, template, outreach
 */

public class SfdcPowerlistStrategies extends CopilotTestUtils{

    private static final String SET_USAGE_DATA_LEVEL_FILE = Application.basedir + "/testdata/sfdc/rulesEngine/scripts/Set_Account_Level_Weekly.apex";
    private static final String SET_USAGE_DATA_MEASURE_FILE = Application.basedir + "/testdata/sfdc/rulesEngine/scripts/UsageData_Measures.apex";

    @BeforeClass
    public void setUp() throws Exception {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning failed, Please check log for more details !!!");
        copilotAPI = new CopilotAPIImpl(header);
        gsDataAPI = new GSDataImpl(header);
        tenantInfo = gsDataAPI.getTenantInfo(sfinfo.getOrg());

        if (true) {
            createCustomFields();
            cleanAndGenerateData();
        }
        metaUtil.createFieldsOnUsageData(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_LEVEL_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_MEASURE_FILE));
        // Creating usage data Account Level Weekly
        sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__UsageData__c];"));
        JobInfo jobInfo = mapper.readValue(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/CoPilot/dataSet/Job/LoadToUsageData_Weekly.txt"), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @TestInfo(testCaseIds = { "GS-4610", "GS-4611", "GS-7653", "GS-7654" })
    @Test(description = "Validate AccountStrategy with Account base object E2E Scenario")
    public void accountStrategyAccountAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/T1_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = updateSmartListActionInfo(actualSmartList, "{\"type\":\"calculated\",\"valueType\":\"BOOLEAN\",\"expression\":{\"alias\":\"undefined\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Contact\",\"field\":\"Contact.Name\",\"fieldName\":\"Name\",\"objectName\":\"Contact\",\"fieldType\":\"STRING\",\"label\":\"Full Name\",\"isExternalCriteria\":true},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"giri\"}}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Account strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Account strategy Powerlist Name", "Failed to Update Account strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/T1_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
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

    @TestInfo(testCaseIds = { "GS-4612", "GS-7655" })
    @Test(description = "Validate ContactStrategy with Account base object E2E Scenario")
    public void contactStrategyAccountAsBaseObject() throws Exception {

        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/T2_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Contact strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Contact strategy Powerlist Name", "Failed to Update Contact strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/T2_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
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

    @TestInfo(testCaseIds = { "GS-4613", "GS-4615", "GS-7656" })
    @Test(description = "Validate UserStrategy with Account base object E2E Scenario")
    public void userStrategyAccountAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/T4_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Galbreath\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,1,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated User strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated User strategy Powerlist Name", "Failed to Update User strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 1, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/T4_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
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


    @TestInfo(testCaseIds = { "GS-4616", "GS-4627", "GS-4629" })
    @Test(description = "Validate EmailStrategy with Account base object E2E Scenario")
    public void emailStrategyAccountAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/T5_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Galbreath\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Email strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Email strategy Powerlist Name", "Failed to Update email strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/T5_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
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

    @TestInfo(testCaseIds = { "GS-4630", "GS-4631", "GS-7663", "GS-7664" })
    @Test(description = "Validate NoStrategy with Contact base object E2E Scenario")
    public void noStrategyContactAsBaseObject() throws Exception {

        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/ContactBaseObject/T3_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "(A AND B) OR C ", "{\"alias\":\"C\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Contact\",\"field\":\"Account.Name\",\"fieldName\":\"Account.Name\",\"objectName\":\"Contact\",\"fieldType\":\"TEXT\",\"label\":\"Account ID Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,45,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated No strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated No strategy Powerlist Name", "Failed to Update No strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 45, 2, 45);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/ContactBaseObject/T3_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 4, 41);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "PARTIAL_SUCCESS", "Outreach Run Status Failed");

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

    @TestInfo(testCaseIds = { "GS-4623", "GS-7662", "GS-7657" })
    @Test(description = "Validate AccountStrategy with CustomerInfo base object E2E Scenario")
    public void accountStrategyCustomerinfoAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/Account_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"JBCXM__CustomerInfo__c\",\"field\":\"JBCXM__MRR__c\",\"fieldName\":\"JBCXM__MRR__c\",\"objectName\":\"JBCXM__CustomerInfo__c\",\"fieldType\":\"CURRENCY\",\"label\":\"MRR\",\"isExternalCriteria\":false},\"operator\":\"eq\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"204\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,30,1);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Account strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Account strategy Powerlist Name", "Failed to Update Account strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 30, 1, 30);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/Account_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 29);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "PARTIAL_SUCCESS", "Outreach Run Status Failed");

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

    @TestInfo(testCaseIds = { "GS-4624" })
    @Test(description = "Validate ContactStrategy with CustomerInfo base object E2E Scenario")
    public void contactStrategyCustomerinfoAsBaseObject() throws Exception {

        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/Contact_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A OR B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Account\",\"field\":\"Name\",\"fieldName\":\"Name\",\"objectName\":\"Account\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,2,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Contact strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Contact strategy Powerlist Name", "Failed to Update Contact strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 2, 2, 2);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/Contact_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
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

    @TestInfo(testCaseIds = { "GS-4614", "GS-4626" })
    @Test(description = "Validate UserStrategy with CustomerInfo base object E2E Scenario")
    public void userStrategyCustomerinfoAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/User_SmartList.json"), SmartList.class), 1, 20);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A AND B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"JBCXM__CustomerInfo__c\",\"field\":\"Copilot_Customer_Number__c\",\"fieldName\":\"Copilot_Customer_Number__c\",\"objectName\":\"JBCXM__CustomerInfo__c\",\"fieldType\":\"DOUBLE\",\"label\":\"Copilot_Customer_Number\",\"isExternalCriteria\":false},\"operator\":\"ne\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"122\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,1,15);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated User strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated User strategy Powerlist Name", "Failed to Update User strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 1, 15, 15);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach with both 90days,token check & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/User_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 8, 7);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "PARTIAL_SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 15);

        //Update outreach with 90days uncheck-- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "PARTIAL_SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 8, 7);

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

    @TestInfo(testCaseIds = { "GS-4625" })
    @Test(description = "Validate EmailStrategy with CustomerInfo base object E2E Scenario")
    public void emailStrategyCustomerinfoAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/Email_SmartList.json"), SmartList.class), 14, 14);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A AND B","{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"JBCXM__CustomerInfo__c\",\"field\":\"Copilot_Customer_Number__c\",\"fieldName\":\"Copilot_Customer_Number__c\",\"objectName\":\"JBCXM__CustomerInfo__c\",\"fieldType\":\"DOUBLE\",\"label\":\"Copilot_Customer_Number\",\"isExternalCriteria\":false},\"operator\":\"ne\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"122\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,9,9);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Email strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Email strategy Powerlist Name", "Failed to Update email strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 9, 9, 9);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CustomerInfoBaseObject/Email_OutReach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 9, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 9);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 9, 0);
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

    @TestInfo(testCaseIds = { "GS-4617" })
    @Test(description = "Validate AccountStrategy with Case base object E2E Scenario")
    public void accountStrategyCaseAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CaseBaseObject/Account_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A AND B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"Case\",\"field\":\"Copilot_Case_Number__c\",\"fieldName\":\"Copilot_Case_Number__c\",\"objectName\":\"Case\",\"fieldType\":\"DOUBLE\",\"label\":\"Copilot_Case_Number\",\"isExternalCriteria\":false},\"operator\":\"ne\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"3\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,30,1);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Account strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Account strategy Powerlist Name", "Failed to Update Account strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 30, 1, 30);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/CaseBaseObject/Account_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 29);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "PARTIAL_SUCCESS", "Outreach Run Status Failed");

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

    @TestInfo(testCaseIds = { "GS-4618", "GS-7660" })
    @Test(description = "Validate ContactStrategy with Case base object E2E Scenario")
    public void contactStrategyCaseAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CaseBaseObject/Contact_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A AND B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"Case\",\"field\":\"Copilot_Case_Number__c\",\"fieldName\":\"Copilot_Case_Number__c\",\"objectName\":\"Case\",\"fieldType\":\"DOUBLE\",\"label\":\"Copilot_Case_Number\",\"isExternalCriteria\":false},\"operator\":\"ne\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"3\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,1,1);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Contact strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Contact strategy Powerlist Name", "Failed to Update Contact strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 1, 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CaseBaseObject/Contact_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 1);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "CONTACT STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "CONTACT STRATEGY OUTREACH NAME UPDATED", "Failed to Update Contact strategy outreach Name");

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

    @TestInfo(testCaseIds = { "GS-4620", "GS-4621", "GS-4153", "GS-4154", "GS-4160", "GS-100163" })
    @Test(description = "Validate UserStrategy with Case base object E2E Scenario")
    public void userStrategyCaseAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CaseBaseObject/User_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A AND B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"Case\",\"field\":\"Copilot_Case_Number__c\",\"fieldName\":\"Copilot_Case_Number__c\",\"objectName\":\"Case\",\"fieldType\":\"DOUBLE\",\"label\":\"Copilot_Case_Number\",\"isExternalCriteria\":false},\"operator\":\"ne\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"3\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,1,1);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated User strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated User strategy Powerlist Name", "Failed to Update User strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 1, 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/CaseBaseObject/User_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 1);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "USER STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "USER STRATEGY OUTREACH NAME UPDATED", "Failed to Update User strategy outreach Name");

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

    @TestInfo(testCaseIds = { "GS-4619", "GS-7611" })
    @Test(description = "Validate EmailStrategy with Case base object E2E Scenario")
    public void emailStrategyCaseAsBaseObject() throws Exception {
        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/CaseBaseObject/Email_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "A AND B", "{\"alias\":\"B\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"DOUBLE\",\"entity\":\"Case\",\"field\":\"Copilot_Case_Number__c\",\"fieldName\":\"Copilot_Case_Number__c\",\"objectName\":\"Case\",\"fieldType\":\"DOUBLE\",\"label\":\"Copilot_Case_Number\",\"isExternalCriteria\":false},\"operator\":\"ne\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"DOUBLE\",\"isNull\":false,\"value\":\"3\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,1,1);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated Email strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated Email strategy Powerlist Name", "Failed to Update Email strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 1, 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Email Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/CaseBaseObject/Email_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 1);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);

        //Get the email template references in outreaches & verify the template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Verifying outreach name - updated.
        actualOutReach = copilotAPI.updateOutReachName(actualOutReach.getCampaignId(), "EMAIL STRATEGY OUTREACH NAME UPDATED");
        Assert.assertEquals(actualOutReach.getName(), "EMAIL STRATEGY OUTREACH NAME UPDATED", "Failed to Update Email strategy outreach Name");

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


    @TestInfo(testCaseIds = {"GS-7666"})
    @Test(description = "Testcase to validate User Strategy - Base Object - UsageData , with Calc Field (Day Agg)")
    public void testUserStrategyWithUsageDataAndCalculatedFieldDayAgg() throws Exception {
        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_SmartList.json"), SmartList.class), 1, 3);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/UsageDataBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Creating out reach & verifying the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 9, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }


    @TestInfo(testCaseIds = {"GS-7667"})
    @Test(description = "Testcase to validate User Strategy - Base Object - UsageData , with Calc Field (Monthly Agg)")
    public void testUserStrategyWithUsageDataAndCalculatedFieldMonthlyAgg() throws Exception {
        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_SmartList_MonthlyAgg.json"), SmartList.class), 1, 3);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/UsageDataBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Creating out reach & verifying the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_Outreach_MonthlyAgg.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 3, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }


    @TestInfo(testCaseIds = {"GS-7668"})
    @Test(description = "Testcase to validate User Strategy - Base Object - UsageData , with Calc Field (Yearly Agg)")
    public void testUserStrategyWithUsageDataAndCalculatedFieldYearlyAgg() throws Exception {
        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_SmartList_YearlyAgg.json"), SmartList.class), 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/UsageDataBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Creating out reach & verifying the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_Outreach_YearlyAgg.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }


    @TestInfo(testCaseIds = {"GS-4632"})
    @Test(description = "Testcase to validate User Strategy - Base Object - UsageData , with Calc Field (Weekly Agg)")
    public void testUserStrategyWithUsageDataAndCalculatedFieldWeeklyAgg() throws Exception {
        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_SmartList_WeeklyAgg.json"), SmartList.class), 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/UsageDataBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Creating out reach & verifying the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/UsageDataBaseObject/User_Outreach_YearlyAgg.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }


    @TestInfo(testCaseIds = {"GS-7499", "GS-7504"})
    @Test(description = "Testcase to validate Preview Email")
    public void testPowerListClone() throws Exception {
        // Creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/PreviewEmail_SmartList.json"), SmartList.class), 1, 1);
        actualSmartList.setSmartListName("New Cloned smartList - GS-200275");
        actualSmartList.setName("New Cloned smartList - GS-200275");

        // Cloning powerList
        cloneAndValidateSmartList(actualSmartList, 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Creating out reach & verifying the samee
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/PreviewEmail_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    @TestInfo(testCaseIds = {"GS-200275", "GS-200276"})
    @Test(description = "Testcase to validate Preview Email, Sending sample test email and triggering actual outReach")
    public void testPreviewEmail() throws Exception {
        // Creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/PreviewEmail_SmartList.json"), SmartList.class), 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/AccountBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //Creating out reach & verifying the same
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(getNameSpaceResolvedFileContents(testDataDir + "test/AccountBaseObject/PreviewEmail_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);

        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        SObject[] account = sfdc.getRecords("SELECT LastName ,Id,Name,AccountId,Account.Name,Account.NumberOfEmployees,Email,Account.Copilot_Account_Date__c FROM Contact where Account.Name='Gallo Ernst & Julio Winery' and Name like '%Hari%' and  isdeleted=false limit 1");
        long epochTime = DateUtil.getEpochTime("" + account[0].getChild("Account").getChild("Copilot_Account_Date__c").getValue().toString() + "", "yyyy-MM-dd", "" + sfinfo.getUserTimeZone() + "");
        String payload = "{\"NumberOfEmployees\":" + account[0].getChild("Account").getChild("NumberOfEmployees").getValue().toString() + ",\"Name\":\"Gallo Ernst & Julio Winery\",\"Contact.Id\":\"" + account[0].getId() + "\",\"Contact.Name\":\"Hari babu Gallo Vadlamudi\",\"Contact.Account.Id\":\"" + account[0].getField("AccountId") + "\",\"Contact.AccountId\":\"" + account[0].getField("AccountId") + "\",\"Contact.Email\":\"gallovadlamudi@automation.gainsighttest.com\",\"Id\":\"" + account[0].getField("AccountId") + "\",\"Copilot_Account_Date__c\":" + epochTime + "}";
        // Forming the expected preview email template payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("DateToken", DateUtil.getDateFromEpoch(epochTime, "yyyy-MM-dd", sfinfo.getUserTimeZone()));
        valuesMap.put("NumberToken", account[0].getChild("Account").getChild("NumberOfEmployees").getValue().toString());
        valuesMap.put("StringToken", "Gallo Ernst & Julio Winery");
        valuesMap.put("CampaignID", actualOutReach.getCampaignId());
        valuesMap.put("SmartlistID", actualSmartList.getSmartListId());
        valuesMap.put("SmartlistName", actualSmartList.getSmartListName());
        valuesMap.put("templateID", actualEmailTemplate.getTemplateId());
        valuesMap.put("RuleRunDate", DateUtil.addDays(Calendar.getInstance(), 0, "yyyy-MM-dd"));
        String pay = "{\"name\":\"Preview OutReach\",\"category\":\"default\",\"smartListId\":\"${SmartlistID}\",\"smartListName\":\"${SmartlistName}\",\"defaultECA\":[{\"actions\":[{\"order\":0,\"actionType\":\"EMAIL\",\"emailTemplateName\":\"Template for all Strategies\",\"emailTemplateId\":\"${templateID}\",\"params\":{\"areaName\":\"EMAIL\"},\"fromAddress\":{\"name\":\"Abhilash\",\"emailId\":\"Abhi@gainsight.com\",\"replyTo\":\"Abhi@gainsight.com\"},\"copyToAddress\":[],\"tokenMapping\":{\"isNotNullable\":false,\"tokens\":[{\"name\":\"embd::6fd5ef90-9baa-4cd7-a132-c7db6f2ad714\",\"value\":{\"type\":\"field\",\"field\":\"NumberOfEmployees\",\"fieldName\":\"NumberOfEmployees\",\"entity\":\"Account\",\"alias\":\"\",\"fieldType\":\"INTEGER\",\"label\":\"Employees\",\"aggregation\":\"\",\"valueType\":\"INTEGER\",\"fieldLabel\":\"Account ➝ Employees\",\"objectName\":\"Account\"}},{\"name\":\"embd::5b84d9fa-ab21-4924-b5da-013e23f39af4\",\"value\":{\"type\":\"field\",\"field\":\"Name\",\"fieldName\":\"Name\",\"entity\":\"Account\",\"alias\":\"\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"aggregation\":\"\",\"valueType\":\"STRING\",\"fieldLabel\":\"Account ➝ Account Name\",\"objectName\":\"Account\"}},{\"name\":\"subj::43e4a77a-ea21-4962-a69a-dc57203dbe03\",\"value\":{\"type\":\"field\",\"field\":\"Copilot_Account_Date__c\",\"fieldName\":\"Copilot_Account_Date__c\",\"entity\":\"Account\",\"alias\":\"\",\"fieldType\":\"DATE\",\"label\":\"Copilot_Account_Date\",\"aggregation\":\"\",\"valueType\":\"DATE\",\"fieldLabel\":\"Account ➝ Copilot_Account_Date\",\"objectName\":\"Account\"}},{\"name\":\"unsubscribeText\",\"value\":{\"type\":\"value\",\"valueType\":\"STRING\",\"value\":\"Manage your subscription preferences &lt;a href=&quot;${%s}&quot;&gt;here&lt;/a&gt;\"}}]},\"reportTokenMappings\":[],\"reportNotNullable\":true,\"addUnsubscribeFooter\":true,\"isTransactional\":false,\"preventDuplicateDays\":0}]}],\"followUpECA\":[],\"status\":\"ACTIVATED\",\"totalRecipients\":0,\"settings\":{\"SEND_EMAIL_ONLY_ONCE\":true,\"TEST_RUN\":false},\"outreachEmailTemplateTypes\":[\"STANDARD\"],\"preventDuplicateDays\":0,\"logActivity\":false,\"cascadeDelete\":false,\"published\":false,\"reportTokenMappings\":[],\"campaignId\":\"${CampaignID}\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(pay);
        Log.info("Modified payload is " + actualPayload);

        // Updating outReach inorder to preview outreach
        Assert.assertTrue(copilotAPI.updateOutReachNsResponse(actualPayload).isResult(), "Outreach is not updated correctly !!");
        String actualEmailTemplateData = copilotAPI.previewOutReachAndGetNsResponse(actualOutReach.getCampaignId(), payload);
        Assert.assertNotNull(actualEmailTemplateData, "Preview data should not be null !!!");
        String expectedEmailTemplate = sub.replace("{\"canvasWidth\":\"600\",\"subject\":\"Subject is precious -- With token --${DateToken}\u200B\",\"reportsWithNoData\":[],\"width\":\"auto\",\"tokenMapping\":[{\"isNotNullable\":false,\"tokens\":[{\"name\":\"embd::6fd5ef90-9baa-4cd7-a132-c7db6f2ad714\",\"value\":{\"type\":\"field\",\"field\":\"NumberOfEmployees\",\"fieldName\":\"NumberOfEmployees\",\"entity\":\"Account\",\"alias\":\"\",\"fieldType\":\"INTEGER\",\"label\":\"Employees\",\"aggregation\":\"\",\"valueType\":\"INTEGER\",\"fieldLabel\":\"Account ➝ Employees\",\"objectName\":\"Account\"}},{\"name\":\"embd::5b84d9fa-ab21-4924-b5da-013e23f39af4\",\"value\":{\"type\":\"field\",\"field\":\"Name\",\"fieldName\":\"Name\",\"entity\":\"Account\",\"alias\":\"\",\"fieldType\":\"STRING\",\"label\":\"Account Name\",\"aggregation\":\"\",\"valueType\":\"STRING\",\"fieldLabel\":\"Account ➝ Account Name\",\"objectName\":\"Account\"}},{\"name\":\"subj::43e4a77a-ea21-4962-a69a-dc57203dbe03\",\"value\":{\"type\":\"field\",\"field\":\"Copilot_Account_Date__c\",\"fieldName\":\"Copilot_Account_Date__c\",\"entity\":\"Account\",\"alias\":\"\",\"fieldType\":\"DATE\",\"label\":\"Copilot_Account_Date\",\"aggregation\":\"\",\"valueType\":\"DATE\",\"fieldLabel\":\"Account ➝ Copilot_Account_Date\",\"objectName\":\"Account\"}},{\"name\":\"unsubscribeText\",\"value\":{\"type\":\"value\",\"valueType\":\"STRING\",\"value\":\"Manage your subscription preferences &lt;a href=&quot;${%s}&quot;&gt;here&lt;/a&gt;\"}}]}],\"failureReports\":[],\"failureTokenDisplayNames\":[],\"htmlContent\":\"&lt;!DOCTYPE html PUBLIC &quot;-//W3C//DTD HTML 4.01 Transitional//EN&quot; &quot;http://www.w3.org/TR/html4/loose.dtd&quot;&gt;&#10;&lt;html lang=&quot;en&quot; style=&quot;font-family: sans-serif; font-size: 10px; -webkit-tap-highlight-color: rgba(0, 0, 0, 0);-ms-text-size-adjust: 100%;-webkit-tap-highlight-color: rgba(0, 0, 0, 0);&quot;&gt;&#10;    &lt;head&gt;&#10;        &lt;meta http-equiv=&quot;Content-Type&quot; content=&quot;text/html; charset=UTF-8&quot;&gt;&#10;        &lt;meta name=&quot;viewport&quot; content=&quot;width=device-width, initial-scale=1&quot;&gt; &lt;!-- So that mobile will display zoomed in --&gt;&#10;        &lt;meta http-equiv=&quot;X-UA-Compatible&quot; content=&quot;IE=edge&quot;&gt; &lt;!-- enable media queries for windows phone 8 --&gt;&#10;        &lt;meta name=&quot;format-detection&quot; content=&quot;telephone=no&quot;&gt; &lt;!-- disable auto telephone linking in iOS --&gt;&#10;        &lt;title&gt;Subject is precious -- With token --&nbsp;${subj::43e4a77a-ea21-4962-a69a-dc57203dbe03}&#8203;&lt;/title&gt;&#10;        &lt;style type=&quot;text/css&quot;&gt;&#10;            html { &#10;                font-family: sans-serif;&#10;                -ms-text-size-adjust: 100%;&#10;                -webkit-text-size-adjust: 100%;&#10;                font-size: 10px;&#10;                -webkit-tap-highlight-color: rgba(0, 0, 0, 0);&#10;            }&#10;            body,.bodywrapper {&#10;                margin: 0;&#10;                padding:0;&#10;                font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;&#10;                font-size: 14px;&#10;                line-height: 1.42857143;&#10;                color: #333333;&#10;                background-color: #ffffff;&#10;            }&#10;            .emailbuilder_main{&#10;                word-break: break-word;&#10;                word-wrap: break-word;&#10;            }&#10;            .ExternalClass {&#10;                width: 100%;&#10;            }&#10;&#10;            .ExternalClass,&#10;            .ExternalClass p,&#10;            .ExternalClass span,&#10;            .ExternalClass font,&#10;            .ExternalClass td,&#10;            .ExternalClass div {&#10;                line-height: 100%;&#10;            }&#10;&#10;            .ReadMsgBody {&#10;                width: 100%;&#10;                background-color: #ebebeb;&#10;            }&#10;&#10;            .content {&#10;                /*padding-top: 5px;&#10;                padding-bottom: 5px;*/&#10;                background-color: #ffffff;&#10;            }&#10;            .container-padding {&#10;                padding-left: 24px;&#10;                padding-right: 24px;&#10;            }&#10;            .footer-text {&#10;                font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;&#10;                font-size: 12px;&#10;                line-height: 16px;&#10;                color: #4F4F4F;&#10;            }&#10;            .footer-text a {&#10;                color: #4F4F4F;&#10;            }&#10;&#10;            table {&#10;                mso-table-lspace: 0pt;&#10;                mso-table-rspace: 0pt;&#10;            }&#10;&#10;            img {&#10;                -ms-interpolation-mode: bicubic;&#10;            }&#10;&#10;            .yshortcuts a {&#10;                border-bottom: none !important;&#10;            }&#10;&#10;            @media screen and (max-width: 599px) {&#10;                table[class=&quot;force-row&quot;],&#10;                table[class=&quot;container&quot;] {&#10;                    width: 100% !important;&#10;                    max-width: 100% !important;&#10;                }&#10;            }&#10;            @media screen and (max-width: 400px) {&#10;                td[class*=&quot;container-padding&quot;] {&#10;                    padding-left: 12px !important;&#10;                    padding-right: 12px !important;&#10;                }&#10;            }&#10;            .ios-footer a {&#10;                color: #4F4F4F !important;&#10;                text-decoration: underline;&#10;            }&#10;        &lt;/style&gt;&#10;    &lt;/head&gt;&#10;    &lt;body style=&quot;font-family: &#39;Helvetica Neue&#39;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 1.42857; color: rgb(51, 51, 51); margin: 0px; padding: 0px; background-color: rgb(255, 255, 255);&quot; bgcolor=&quot;#ffffff&quot; leftmargin=&quot;0&quot; topmargin=&quot;0&quot; marginwidth=&quot;0&quot; marginheight=&quot;0&quot;&gt;&#10;        &lt;!-- 100% background wrapper (grey background) --&gt;&#10;        &lt;table border=&quot;0&quot; width=&quot;100%&quot; height=&quot;100%&quot; cellpadding=&quot;0&quot; cellspacing=&quot;0&quot; bgcolor=&quot;#ffffff&quot; class=&quot;bodywrapper&quot; style=&quot;margin: 0px; padding: 0px; font-family: &#39;Helvetica Neue&#39;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 1.42857; color: rgb(51, 51, 51); background-color: rgb(255, 255, 255);&quot;&gt;&#10;            &lt;tbody&gt;&lt;tr&gt;&#10;                &lt;td align=&quot;left&quot; valign=&quot;top&quot; bgcolor=&quot;#ffffff&quot; style=&quot;background-color: #ffffff;padding-left: 5px; padding-right: 5px;padding-top: 5px;padding-bottom: 5px&quot;&gt;&#10;                    &lt;!-- 600px container (white background) --&gt;&#10;                    &lt;table border=&quot;0&quot; width=&quot;100%&quot; cellpadding=&quot;0&quot; cellspacing=&quot;0&quot; class=&quot;container emailbuilder_container&quot; bgcolor=&quot;#ffffff&quot; style=&quot;background-color: rgb(255, 255, 255);&quot;&gt;&#10;                        &lt;tbody&gt;&lt;tr&gt;&#10;                            &lt;td class=&quot;content emailbuilder_main&quot; align=&quot;left&quot; bgcolor=&quot;#ffffff&quot; style=&quot;font-family: &#39;Helvetica Neue&#39;, Helvetica, Arial, sans-serif; font-size: 14px; line-height: 1.42857; color: rgb(51, 51, 51); word-break: break-word; word-wrap: break-word; background-color: rgb(255, 255, 255);&quot;&gt;&#10;                                &lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;Hi ${NumberToken}&lt;span style=&quot;box-sizing: border-box;&quot;&gt;&#8203;&lt;/span&gt;&lt;span style=&quot;box-sizing: border-box;&quot;&gt;&#8203;&lt;/span&gt;,&lt;/div&gt;&lt;blockquote style=&quot;box-sizing: border-box; font-size: 14px; margin: 0px 0px 0px 40px; border: none; padding: 0px;&quot;&gt;&lt;div style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px; text-align: left;&quot; class=&quot;polyfixpara&quot;&gt;&lt;/div&gt;&lt;ul style=&quot;box-sizing: border-box; margin-top: 0px; margin-bottom: 10px;&quot;&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;We have a release coming up on December 22nd for GonG release &amp;amp; January 7 for Customer release.&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;Considering the series of regressions that we have been hitting - I am anxious to know the following:&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;Do we have a clear communication from each team on what the team is planning to include in Dec 22nd release?&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;we are expected to have a 2 week sprint for the QA team to provide sign off.&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;&lt;strong style=&quot;font-weight: bold; box-sizing: border-box;&quot;&gt;For Dec 22 release - I understand that this would be a little ambitious; but I&#39;d say that not less than 1 clear week must be provided to the QA team for release sign off. This means we must have stable betas in every way by Dec 15 or earlier.&lt;/strong&gt;&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;&lt;em style=&quot;box-sizing: border-box;&quot;&gt;For Feb 4 - we must hit feature freeze by Jan 21 and have stable betas to ensure 2 week sprint for QA team to provide release sign off.&lt;/em&gt;&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; color: rgb(230, 56, 25);&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;Please review and communicate on the status of your commitments for Dec 22nd and Feb 4th releases.&lt;/span&gt;&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;If stable Dev Builds are NOT available on time; then the features will have to go in the next release. Taking this into account, please identify any risks and communicate asap.&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;Scrum Masters - please share these with your teams.&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;More on this soon - &lt;span style=&quot;box-sizing: border-box; text-decoration: underline;&quot;&gt;one thing that I&#39;d say is that for THIS release; we are prepared to take a &quot;short term hit&quot; on delivering less than what we committed for; but we MUST step up on the quality of our releases&lt;/span&gt;.&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;We are NOT prepared to cut short on the QA cycles with delayed dev drop dates, last minute change requests; prolonged beta requests [knowing that sometimes these happen due to valid reasons] etc etc.&lt;/span&gt;&lt;/li&gt;&lt;li style=&quot;box-sizing: border-box; margin-left: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;We will build out the engineering release calendar including the internal milestones for delivering builds to the QA team.&lt;/span&gt;&lt;/li&gt;&lt;/ul&gt;&lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;&lt;/div&gt;&lt;/blockquote&gt;&lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;&lt;/div&gt;&lt;table class=&quot;buttonLinkCntr&quot; width=&quot;100%&quot; cellpadding=&quot;0&quot; cellspacing=&quot;0&quot; style=&quot;border-collapse: collapse; border-spacing: 0px; box-sizing: border-box; width: 100%; max-width: 100%; background-color: transparent;&quot;&gt;&lt;tbody style=&quot;box-sizing: border-box;&quot;&gt;&lt;tr style=&quot;box-sizing: border-box;&quot;&gt;&lt;td valign=&quot;top&quot; contenteditable=&quot;false&quot; style=&quot;padding: 0px 0px 10px; box-sizing: border-box; text-align: center;&quot;&gt;&lt;table class=&quot;buttonLink standardBtn&quot; style=&quot;border-collapse: collapse; border-spacing: 0px; box-sizing: border-box; width: 75%; background-color: transparent;&quot; align=&quot;center&quot;&gt;&lt;tbody style=&quot;box-sizing: border-box;&quot;&gt;&lt;tr style=&quot;box-sizing: border-box;&quot;&gt;&lt;td contenteditable=&quot;false&quot; style=&quot;padding: 9px 0px 8px; box-sizing: border-box; cursor: pointer; word-break: break-word; text-align: center; color: rgb(255, 255, 255); line-height: 28px; font-size: 16px; display: block; border-radius: 4px; width: auto !important; background: rgb(91, 200, 91);&quot;&gt;&lt;a href=&quot;https://www.gainsight.com&quot; target=&quot;_blank&quot; style=&quot;box-sizing: border-box; cursor: pointer; display: block; font-size: 16px; text-decoration: none; color: rgb(242, 242, 242); background-color: transparent;&quot;&gt;Gainsight Home&lt;/a&gt;&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;&lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;&lt;/div&gt;&lt;div class=&quot;polyfixpara me-inline-img&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px; display: block; vertical-align: bottom;&quot;&gt;&#8203; &lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;&#8203;&lt;/span&gt;&lt;div class=&quot;imgcntr&quot; style=&quot;box-sizing: border-box; line-height: 1.42857; display: inline-block; min-width: 18px; min-height: 18px;&quot;&gt;&lt;a href=&quot;http://gainsight.com&quot; target=&quot;_blank&quot; style=&quot;box-sizing: border-box; cursor: pointer; color: rgb(17, 85, 204); text-decoration: underline; background-color: transparent;&quot;&gt;&lt;img alt=&quot;logo.png&quot; src=&quot;http://www.gainsight.com/wp-content/uploads/2015/09/logo.png&quot; style=&quot;border: 0px; box-sizing: border-box; vertical-align: middle; display: inline;&quot;&gt;&lt;/a&gt;&lt;/div&gt;&lt;span style=&quot;box-sizing: border-box; line-height: 1.42857;&quot;&gt;&#8203;&lt;/span&gt;&lt;/div&gt;&lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box;&quot;&gt;Thanks,&lt;/span&gt;&lt;/div&gt;&lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;&lt;span style=&quot;box-sizing: border-box;&quot;&gt;${StringToken}&lt;span style=&quot;box-sizing: border-box;&quot;&gt;&#8203;&lt;/span&gt;.&#8203;&lt;/span&gt;&lt;/div&gt;&lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;&lt;br style=&quot;box-sizing: border-box;&quot;&gt;&lt;/div&gt;&lt;div class=&quot;polyfixpara&quot; style=&quot;box-sizing: border-box; padding-top: 0px; padding-bottom: 0px; margin: 0px;&quot;&gt;&lt;br style=&quot;box-sizing: border-box;&quot;&gt;&lt;/div&gt;&#10;                            &lt;/td&gt;&#10;                        &lt;/tr&gt;&#10;                        &lt;tr&gt;&lt;td&gt;&lt;br&gt;&lt;/td&gt;&lt;/tr&gt;&#10;                    &lt;/tbody&gt;&lt;/table&gt;&lt;!--/600px container --&gt;&#10;                &lt;/td&gt;&#10;            &lt;/tr&gt;&#10;        &lt;/tbody&gt;&lt;/table&gt;&lt;!--/100% background wrapper--&gt;&#10;    &#10;&#10;&lt;/body&gt;&#10;&lt;/html&gt;&#10;\"}");
        Log.debug("Actual Email template data is " + actualEmailTemplateData);
        JsonFluentAssert.assertThatJson(actualEmailTemplateData).isEqualTo(expectedEmailTemplate);

        // Sending sample test email
        String sampleEmailPayload = sub.replace("{\"parameters\":{\"ruleRunDate\":\"${RuleRunDate}\",\"ruleType\":\"CAMPAIGN\",\"isTestRun\":true,\"isScheduledRun\":false,\"testRunParams\":{\"sendContactsList\":false,\"sendEmailOnTestRun\":true,\"sendSampleRecord\":{\"NumberOfEmployees\":" + account[0].getChild("Account").getChild("NumberOfEmployees").getValue().toString() + ",\"Name\":\"Gallo Ernst & Julio Winery\",\"Contact.Id\":\"" + account[0].getId() + "\",\"Contact.Name\":\"Hari babu Gallo Vadlamudi\",\"Contact.Account.Id\":\"" + account[0].getField("AccountId") + "\",\"Contact.AccountId\":\"" + account[0].getField("AccountId") + "\",\"Contact.Email\":\"gallovadlamudi@automation.gainsighttest.com\",\"Id\":\"" + account[0].getField("AccountId") + "\",\"Copilot_Account_Date__c\":" + epochTime + "},\"recipientEmailIds\":[\"abhilash@gainsight.com\"]},\"campaignId\":\"${CampaignID}\"}}");
        Log.debug("Test Email Payload is ==========>   " + sampleEmailPayload);
        triggerSampleRunOutreach(actualOutReach, sampleEmailPayload);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }
}
