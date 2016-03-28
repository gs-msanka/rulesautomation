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
 * Created by agrandhi on 28/03/16.
 */
public class ContactBaseObjectStrategy extends CopilotTestUtils {

    @BeforeClass
    public void setUp() throws Exception {
        copilotAPI = new CopilotAPIImpl(header);
        gsDataAPI = new GSDataImpl(header);
        tenantInfo = gsDataAPI.getTenantInfo(sfinfo.getOrg());
    }

    @Test
    public void noStrategyContactAsBaseObject() throws Exception {

        //Create and validate smartlist
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(new File(testDataDir + "test/ContactBaseObject/T3_SmartList.json"), SmartList.class), 30, 1);
        actualSmartList = updateSmartListTriggerCriteria(actualSmartList, "(A AND B) OR C ", "{\"alias\":\"C\",\"left\":{\"keys\":[],\"type\":\"field\",\"valueType\":\"STRING\",\"entity\":\"Contact\",\"field\":\"Account.Name\",\"fieldName\":\"Account.Name\",\"objectName\":\"Contact\",\"fieldType\":\"TEXT\",\"label\":\"Account ID Name\",\"isExternalCriteria\":false},\"operator\":\"contains\",\"right\":{\"keys\":[],\"type\":\"value\",\"valueType\":\"STRING\",\"isNull\":false,\"value\":\"Gallo Ernst & Julio Winery\"}}");
        actualSmartList = runUpdatedSmartList(actualSmartList,46,2);

        //Update the smart list name & verify the updated smartlist name.
        actualSmartList = copilotAPI.updateSmartListName(actualSmartList.getSmartListId(), "Updated No strategy Powerlist Name");
        Assert.assertEquals(actualSmartList.getSmartListName(), "Updated No strategy Powerlist Name", "Failed to Update No strategy Powerlist Name");

        //Resync smart list & verify the status again.
        reSyncSmartList(actualSmartList, 46, 2, 46);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/Basic_cases/T1_T2_T3_T4_T5_EmailTemplate.json"), EmailTemplate.class));

        //Verify Email Template Name Change.
        actualEmailTemplate = copilotAPI.updateEmailTemplateName(actualEmailTemplate.getTemplateId(), "UPDATED EMAIL TEMPLATE NAME");
        Assert.assertEquals(actualEmailTemplate.getTitle(), "UPDATED EMAIL TEMPLATE NAME", "Failed to Update Template Name");

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(new File(testDataDir + "test/ContactBaseObject/T3_Outreach.json"), OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 4, 42);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "PARTIAL_SUCCESS", "Outreach Run Status Failed");

        //Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach = triggerOutreach(actualOutReach);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "FAILURE", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 0, 46);

        //Update outreach -- Re-Trigger out reach, wait for outreach process, verify the execution history of out reach.
        actualOutReach.getDefaultECA().get(0).getActions().get(0).setPreventDuplicateDays(0);
        actualOutReach.setPreventDuplicateDays(0);
        actualOutReach = copilotAPI.updateOutReach(mapper.writeValueAsString(actualOutReach));

        actualOutReach = triggerOutreach(actualOutReach);//should inform the assertion msg to this method
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status Failed");
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 46, 0);

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
}
