package com.gainsight.bigdata.zendesk.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.zendesk.apiImpl.ZendeskImpl;
import com.gainsight.bigdata.zendesk.pojos.TicketLookup;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.codehaus.jackson.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abhilash Thaduka on 2/24/2016.
 */
public class ZendeskTest extends NSTestBase {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/Zendesk/scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_ACCOUNTS = Application.basedir + "/testdata/newstack/Zendesk/Jobs/Job_Accounts.txt";
    DataETL dataETL = new DataETL();
    ZendeskImpl zendeskImpl;
    RulesUtil rulesUtil;


    @BeforeClass
    public void setup() throws Exception {
        zendeskImpl = new ZendeskImpl(header);
        rulesUtil = new RulesUtil();
        rulesUtil.populateObjMaps();
        metaUtil.createExtIdFieldOnAccount(sfdc);
        metaUtil.createFieldsOnAccount(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS)), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @Test()
    @TestInfo(testCaseIds = {"GS-6191"})
    public void zendeskOrgToAccountLookUp() throws Exception {
        TicketLookup ticketLookup = mapper.readValue(new File(Application.basedir + "/testdata/newstack/Zendesk/data/TC1.json"), TicketLookup.class);
        SObject[] account = sfdc.getRecords(("SELECT ID,Name FROM Account where name='Zendesk Account 1' and isDeleted=false"));
        ticketLookup.setSubdomain(env.getProperty("zendesk.subdomain"));
        ticketLookup.setOrganization(Integer.parseInt(env.getProperty("zendesk.organization")));
        ticketLookup.setOrganizationName(env.getProperty("zendesk.organizationName"));
        ticketLookup.setAccountId(account[0].getId());
        ticketLookup.setAccountName(account[0].getField("Name").toString());
        Log.info("Updated payload is: " + mapper.writeValueAsString(ticketLookup));
        boolean result = zendeskImpl.createLookup(ticketLookup);
        Assert.assertTrue(result, "lookup is not created, please check");
    }

    @Test()
    @TestInfo(testCaseIds = {"GS-6191"})
    public void deleteZendeskOrganizationLookup() throws Exception {
        String organizationID = env.getProperty("zendesk.organization");
        boolean result = zendeskImpl.deleteLookup(organizationID);
        Assert.assertTrue(result, "deletion between Zendesk Org to SFDC account lookup is not successful, please check");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-6199", "GS-6200"})
    public void createAndlinkCTA() throws Exception {
        sfdc.runApexCode("Delete [SELECT Id,Name FROM JBCXM__CTA__c where name='Test CTA - Created from Zendesk'];");
        SObject[] account = sfdc.getRecords(("SELECT ID,Name FROM Account where name='Zendesk Account 1' and isDeleted=false"));
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("Account", account[0].getId());
        valuesMap.put("Name", "Test CTA - Created from Zendesk");
        valuesMap.put("Reason", RulesUtil.getPickListMap().get("PL." + "Product Issue"));
        valuesMap.put("Priority", RulesUtil.getPickListMap().get("PL." + "Medium"));
        valuesMap.put("Stage", RulesUtil.getPickListMap().get("PL." + "New"));
        valuesMap.put("Type", RulesUtil.getCtaTypesMap().get("CT." + "Risk"));
        valuesMap.put("DueDate", "2016-11-01");
        valuesMap.put("OriginalDueDate", "2016-11-01");
        valuesMap.put("Assignee__c", sfinfo.getUserId());
        String payload = "{\"params\":\"{\\\"action\\\":\\\"workflow.updateCTAdetails\\\",\\\"data\\\":{\\\"ctaList\\\":\\\"[{\\\\\\\"JBCXM__Account__c\\\\\\\":\\\\\\\"${Account}\\\\\\\",\\\\\\\"Name\\\\\\\":\\\\\\\"${Name}\\\\\\\",\\\\\\\"JBCXM__IsRecurring__c\\\\\\\":false,\\\\\\\"JBCXM__Reason__c\\\\\\\":\\\\\\\"${Reason}\\\\\\\",\\\\\\\"JBCXM__Priority__c\\\\\\\":\\\\\\\"${Priority}\\\\\\\",\\\\\\\"JBCXM__Stage__c\\\\\\\":\\\\\\\"${Stage}\\\\\\\",\\\\\\\"JBCXM__Type__c\\\\\\\":\\\\\\\"${Type}\\\\\\\",\\\\\\\"JBCXM__DueDate__c\\\\\\\":\\\\\\\"${DueDate}\\\\\\\",\\\\\\\"JBCXM__OriginalDueDate__c\\\\\\\":\\\\\\\"${OriginalDueDate}\\\\\\\",\\\\\\\"JBCXM__Assignee__c\\\\\\\":\\\\\\\"${Assignee__c}\\\\\\\",\\\\\\\"JBCXM__Comments__c\\\\\\\":\\\\\\\"<a target=\\\\\\\\\\\\\\\"_blank\\\\\\\\\\\\\\\" href=\\\\\\\\\\\\\\\"https://gainsighttest.zendesk.com/agent/tickets/2006\\\\\\\\\\\\\\\">Zendesk Ticket 2006</a>\\\\\\\"}]\\\",\\\"isSuccessPlan\\\":false,\\\"requiresIds\\\":true}}\",\"actionType\":\"\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        // Creating CTA
        NsResponseObj responseObj = zendeskImpl.zendeskSfdcProxy(resolveStrNameSpace(actualPayload));
        Log.info("Data object is: " + responseObj.getData());
        JsonNode jsonNode = mapper.readTree(responseObj.getData().toString());
        JsonNode res = jsonNode.get("dataObj");
        JsonNode id = res.get("ids");
        Assert.assertNotNull(id, "CTA ID should not be null !!");
        boolean isIdExists = id.isArray();
        String ctaID = null;
        if (isIdExists) {
            ctaID = id.get(0).asText();
            Log.info("CTA ID is " + ctaID);
        }
        Assert.assertTrue(res.get("success").asBoolean(), "Result is not true");
        // Linking Zendesk ticket to SFDC CTA
        boolean result = zendeskImpl.linkTicketToCTA("", env.getProperty("zendesk.zendeskTicketID"), ctaID);
        Assert.assertTrue(result, "Link between Zendesk ticket and SFDC CTA is not successful !!");

        // Getting  associated CTA linked to a Zendesk ticket
        String actualCtaID = zendeskImpl.getCtaByZendeskTicket(env.getProperty("zendesk.zendeskTicketID"));
        Assert.assertEquals(actualCtaID, ctaID, "Cta's are not matching");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-6199", "GS-6200"})
    public void createAndUnlinkCTA() throws Exception {
        sfdc.runApexCode("Delete [SELECT Id,Name FROM JBCXM__CTA__c where name='Test CTA - Created from Zendesk'];");
        SObject[] account = sfdc.getRecords(("SELECT ID,Name FROM Account where name='Zendesk Account 1' and isDeleted=false"));
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("Account", account[0].getId());
        valuesMap.put("Name", "Test CTA - Created from Zendesk");
        valuesMap.put("Reason", RulesUtil.getPickListMap().get("PL." + "Product Issue"));
        valuesMap.put("Priority", RulesUtil.getPickListMap().get("PL." + "Medium"));
        valuesMap.put("Stage", RulesUtil.getPickListMap().get("PL." + "New"));
        valuesMap.put("Type", RulesUtil.getCtaTypesMap().get("CT." + "Risk"));
        valuesMap.put("DueDate", "2016-11-01");
        valuesMap.put("OriginalDueDate", "2016-11-01");
        valuesMap.put("Assignee__c", sfinfo.getUserId());
        String payload = "{\"params\":\"{\\\"action\\\":\\\"workflow.updateCTAdetails\\\",\\\"data\\\":{\\\"ctaList\\\":\\\"[{\\\\\\\"JBCXM__Account__c\\\\\\\":\\\\\\\"${Account}\\\\\\\",\\\\\\\"Name\\\\\\\":\\\\\\\"${Name}\\\\\\\",\\\\\\\"JBCXM__IsRecurring__c\\\\\\\":false,\\\\\\\"JBCXM__Reason__c\\\\\\\":\\\\\\\"${Reason}\\\\\\\",\\\\\\\"JBCXM__Priority__c\\\\\\\":\\\\\\\"${Priority}\\\\\\\",\\\\\\\"JBCXM__Stage__c\\\\\\\":\\\\\\\"${Stage}\\\\\\\",\\\\\\\"JBCXM__Type__c\\\\\\\":\\\\\\\"${Type}\\\\\\\",\\\\\\\"JBCXM__DueDate__c\\\\\\\":\\\\\\\"${DueDate}\\\\\\\",\\\\\\\"JBCXM__OriginalDueDate__c\\\\\\\":\\\\\\\"${OriginalDueDate}\\\\\\\",\\\\\\\"JBCXM__Assignee__c\\\\\\\":\\\\\\\"${Assignee__c}\\\\\\\",\\\\\\\"JBCXM__Comments__c\\\\\\\":\\\\\\\"<a target=\\\\\\\\\\\\\\\"_blank\\\\\\\\\\\\\\\" href=\\\\\\\\\\\\\\\"https://gainsighttest.zendesk.com/agent/tickets/2006\\\\\\\\\\\\\\\">Zendesk Ticket 2006</a>\\\\\\\"}]\\\",\\\"isSuccessPlan\\\":false,\\\"requiresIds\\\":true}}\",\"actionType\":\"\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        // Creating CTA
        NsResponseObj responseObj = zendeskImpl.zendeskSfdcProxy(resolveStrNameSpace(actualPayload));
        Log.info("Data object is: " + responseObj.getData());
        JsonNode jsonNode = mapper.readTree(responseObj.getData().toString());
        JsonNode res = jsonNode.get("dataObj");
        JsonNode id = res.get("ids");
        Assert.assertNotNull(id, "CTA ID should not be null !!");
        boolean isIdExists = id.isArray();
        String ctaID = null;
        if (isIdExists) {
            ctaID = id.get(0).asText();
            Log.info("CTA ID is " + ctaID);
        }
        Assert.assertTrue(res.get("success").asBoolean(), "Result is not true, please check");
        // Linking Zendesk ticket to SFDC CTA
        boolean result = zendeskImpl.linkTicketToCTA("", env.getProperty("zendesk.zendeskTicketID"), ctaID);
        Assert.assertTrue(result, "Link between Zendesk ticket and SFDC CTA is not successful !!");

        // UnLinking the CTA made with a Zendesk ticket
        boolean isSuccess = zendeskImpl.unLinkTicketToCTA(env.getProperty("zendesk.zendeskTicketID"));
        Assert.assertTrue(isSuccess, "Unlink is not successful !!");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-6196"})
    public void getAttributesForZendeskOrganization() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/Zendesk/scripts/Attributes_UIView_Create.txt"));
        SObject[] account = sfdc.getRecords(("SELECT ID,Name FROM Account where name='Zendesk Account 1' and isDeleted=false"));
        SObject[] customer = sfdc.getRecords((resolveStrNameSpace("SELECT Id,JBCXM__CustomerName__c FROM JBCXM__CustomerInfo__c where JBCXM__CustomerName__c='Zendesk Account 1'")));
        Map<String, Object> valuesMap = new HashMap<String, Object>();
        valuesMap.put("SubDomain", env.getProperty("zendesk.subdomain"));
        valuesMap.put("organizationName", env.getProperty("zendesk.organizationName"));
        valuesMap.put("AccountId", account[0].getId());
        valuesMap.put("customerId", customer[0].getId());
        valuesMap.put("GroupID", Integer.valueOf(env.getProperty("zendesk.groupID")));
        valuesMap.put("UserID", Integer.valueOf(env.getProperty("zendesk.userID")));
        valuesMap.put("RequestorID", Long.valueOf(env.getProperty("zendesk.requestorID")));
        valuesMap.put("TicketID", Integer.valueOf(env.getProperty("zendesk.zendeskTicketID")));
        String payload = "{\"params\":\"{\\\"action\\\":\\\"acctAttributes.getAcctAttributes\\\",\\\"custInfo\\\":{\\\"subDomain\\\":\\\"${SubDomain}\\\",\\\"sections\\\":[{\\\"label\\\":\\\"Summary\\\",\\\"msg\\\":\\\"\\\",\\\"name\\\":\\\"Summary\\\",\\\"type\\\":\\\"Standard\\\"}],\\\"orgName\\\":\\\"${organizationName}\\\",\\\"accountId\\\":\\\"${AccountId}\\\",\\\"customerId\\\":\\\"${customerId}\\\",\\\"ticket\\\":{\\\"assignee\\\":{\\\"group\\\":{\\\"name\\\":\\\"Support\\\",\\\"id\\\":${GroupID}},\\\"user\\\":{\\\"email\\\":\\\"spadmanabhan@gainsight.com\\\",\\\"externalId\\\":null,\\\"id\\\":${UserID},\\\"name\\\":\\\"Sunand Padmanabhan\\\"}},\\\"brand\\\":{},\\\"organization\\\":{},\\\"collaborators\\\":[],\\\"comments\\\":[{\\\"id\\\":43290993185,\\\"imageAttachments\\\":[],\\\"nonImageAttachments\\\":[],\\\"value\\\":\\\"<div class=\\\\\\\"zd-comment\\\\\\\"><p>Please ignore this ticket</p></div>\\\"}],\\\"form\\\":{\\\"id\\\":null},\\\"postSaveAction\\\":\\\"close_tab\\\",\\\"requester\\\":{\\\"email\\\":\\\"sunandonline@gmail.com\\\",\\\"externalId\\\":null,\\\"id\\\":${RequestorID},\\\"name\\\":\\\"sunand\\\"},\\\"sharedWith\\\":null,\\\"status\\\":\\\"open\\\",\\\"tags\\\":[\\\"test\\\"],\\\"custom_field_25445085\\\":\\\"test\\\",\\\"custom_field_25452265\\\":\\\"NPE\\\",\\\"priority\\\":\\\"-\\\",\\\"subject\\\":\\\"Another Set of Tickets - eb7a2b40-c9d6-445f-9400-2142f44b5279\\\",\\\"type\\\":\\\"ticket\\\",\\\"Id\\\":${TicketID}},\\\"ticketUrl\\\":\\\"https://gainsighttest.zendesk.com/agent/tickets/2006\\\",\\\"section\\\":{\\\"label\\\":\\\"Summary\\\",\\\"msg\\\":\\\"\\\",\\\"name\\\":\\\"Summary\\\",\\\"type\\\":\\\"Standard\\\"}}}\",\"actionType\":\"\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);

        NsResponseObj response = zendeskImpl.zendeskSfdcProxy(resolveStrNameSpace(actualPayload));
        Assert.assertTrue(response.isResult(), "Result is not correct, please check response for more details");
        JsonNode jsonNode = mapper.readTree(response.getData().toString());
        Assert.assertTrue(jsonNode.get("success").asBoolean(), "Data is not success, please check response for more details");
        JsonNode actualData = jsonNode.get("dataObj");
        Log.info("DataObject is " + actualData);
        String expectedAttributesData = FileUtils.readFileToString(new File(Application.basedir + "/testdata/newstack/Zendesk/ExpectedData/GS-6196.txt"));
        //Asserting actual and expected jsonNodes
        JsonFluentAssert.assertThatJson(actualData).isEqualTo(expectedAttributesData);
    }

    @Test
    @TestInfo(testCaseIds = {"GS-6195"})
    public void getSummaryWidgetConfig() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/Zendesk/scripts/CreateWidgets.txt"));
        SObject[] customerID = sfdc.getRecords((resolveStrNameSpace("SELECT Id,JBCXM__CustomerName__c FROM JBCXM__CustomerInfo__c where JBCXM__CustomerName__c='Zendesk Account 1' and isDeleted=false limit 1")));
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("CustomerID", customerID[0].getId());
        String payload = "{\"params\":\"{\\\"action\\\":\\\"summary.getsummaryconfig\\\",\\\"custInfo\\\":{\\\"customerId\\\":\\\"${CustomerID}\\\",\\\"section\\\":{\\\"label\\\":\\\"Summary\\\",\\\"msg\\\":\\\"\\\",\\\"name\\\":\\\"Summary\\\",\\\"type\\\":\\\"Standard\\\"},\\\"sections\\\":[{\\\"label\\\":\\\"Summary\\\",\\\"msg\\\":\\\"\\\",\\\"name\\\":\\\"Summary\\\",\\\"type\\\":\\\"Standard\\\"}]}}\",\"actionType\":\"\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);

        NsResponseObj response = zendeskImpl.zendeskSfdcProxy(actualPayload);
        Assert.assertTrue(response.isResult(), "Result is not correct, please check response for more details");
        JsonNode data = mapper.readTree(response.getData().toString());
        String actualSummaryWidgetConfig = data.get("dataObj").findValue(resolveStrNameSpace("JBCXM__CustomConfig__c")).asText();
        Log.info("Summary Widget Config for customer is " + actualSummaryWidgetConfig);

        String expectedSummaryWidgetConfig = FileUtils.readFileToString(new File(Application.basedir + "/testdata/newstack/Zendesk/ExpectedData/GS-6195-WidgetConfig.txt"));
        //Asserting Summary Widget Configuration for this account with actual and expected json's
        JsonFluentAssert.assertThatJson(actualSummaryWidgetConfig).isEqualTo(expectedSummaryWidgetConfig);
    }

    @Test
    @TestInfo(testCaseIds = {"GS-6195"})
    public void getLeftPanelWidgetData() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/Zendesk/scripts/CreateWidgets.txt"));
        SObject[] account = sfdc.getRecords(("SELECT ID,Name FROM Account where name='Zendesk Account 1' and isDeleted=false"));
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("AccountID", account[0].getId());
        String payload = "{\"params\":\"{\\\"action\\\":\\\"summary.getLeftPanelData\\\",\\\"custInfo\\\":{\\\"accountId\\\":\\\"${AccountID}\\\"}}\",\"actionType\":\"\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);

        NsResponseObj response = zendeskImpl.zendeskSfdcProxy(resolveStrNameSpace(actualPayload));
        Assert.assertTrue(response.isResult(), "Result is not correct, please check response for more details");
        JsonNode data = mapper.readTree(response.getData().toString());
        Assert.assertTrue(data.get("success").asBoolean(), "Data is not success, please check response for more details");
        JsonNode actualData = data.get("dataObj");
        Log.info("DataObject is " + actualData);

        String expectedAttributesData = FileUtils.readFileToString(new File(Application.basedir + "/testdata/newstack/Zendesk/ExpectedData/GS-6195.txt"));
        //Asserting all left panel widget details for this account via actual and expected jsonNodes
        JsonFluentAssert.assertThatJson(actualData).isEqualTo(expectedAttributesData);
    }

    @Test
    @TestInfo(testCaseIds = {"GS-6195"})
    public void getAllSummaryWidgetDetails() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/Zendesk/scripts/CreateWidgets.txt"));
        SObject[] account = sfdc.getRecords(("SELECT ID,Name FROM Account where name='Zendesk Account 1' and isDeleted=false limit 1"));
        SObject[] openCasesWidgetID = sfdc.getRecords((resolveStrNameSpace("SELECT Id,JBCXM__SystemName__c FROM JBCXM__Widgets__c where JBCXM__SystemName__c='OPENCASES' and isDeleted=false limit 1")));
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("AccountID", account[0].getId());
        valuesMap.put("WidgetID", openCasesWidgetID[0].getId());
        String payload = "{\"params\":\"{\\\"action\\\":\\\"summary.getsinglefieldaggregation\\\",\\\"custInfo\\\":{\\\"widgetid\\\":\\\"${WidgetID}\\\",\\\"accountId\\\":\\\"${AccountID}\\\"}}\",\"actionType\":\"\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);

        // Validating OPENCASES WIDGET Data
        NsResponseObj response = zendeskImpl.zendeskSfdcProxy(resolveStrNameSpace(actualPayload));
        Assert.assertTrue(response.isResult(), "Result is not correct, please check response for more details");
        JsonNode data = mapper.readTree(response.getData().toString());
        Assert.assertTrue(data.get("success").asBoolean(), "Data is not success, please check response for more details");
        int actualData = data.get("dataObj").get("total").asInt();
        Log.info("Total number of open cases shown in Summary widget is: " + actualData);
        Assert.assertEquals(actualData, sfdc.getRecordCount("SELECT ID, IsClosed,AccountId,Account.Name FROM Case where Account.Name='Zendesk Account 1' and isDeleted=false"));

        // Validating OPEN CTA's WIDGET Data
        Map<String, String> valuesMap2 = new HashMap<String, String>();
        valuesMap2.put("AccountID", account[0].getId());
        SObject[] openCtaWidgetID = sfdc.getRecords((resolveStrNameSpace("SELECT Id,JBCXM__SystemName__c FROM JBCXM__Widgets__c  where JBCXM__SystemName__c='OCTA' and isDeleted=false limit 1")));
        valuesMap2.put("WidgetID", openCtaWidgetID[0].getId());
        String payload2 = "{\"params\":\"{\\\"action\\\":\\\"summary.getoctaData\\\",\\\"custInfo\\\":{\\\"accountId\\\":\\\"${AccountID}\\\",\\\"widgetid\\\":\\\"${WidgetID}\\\"}}\",\"actionType\":\"\"}";
        StrSubstitutor sub2 = new StrSubstitutor(valuesMap2);
        String actualPayload2 = sub2.replace(payload2);
        NsResponseObj response2 = zendeskImpl.zendeskSfdcProxy(resolveStrNameSpace(actualPayload2));
        Assert.assertTrue(response2.isResult(), "Result is not correct, please check response for more details");
        JsonNode data2 = mapper.readTree(response2.getData().toString());
        Assert.assertTrue(data2.get("success").asBoolean(), "Data is not success, please check response for more details");

        int riskCTA = data2.get("dataObj").get("Risk").findValue("Value").asInt();
        int opportunityCTA = data2.get("dataObj").get("Opportunity").findValue("Value").asInt();
        int eventCTA = data2.get("dataObj").get("Event").findValue("Value").asInt();
        Assert.assertEquals(riskCTA, sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,JBCXM__ClosedDate__c,JBCXM__TypeName__c,JBCXM__Account__r.Name FROM JBCXM__CTA__c where JBCXM__TypeName__c='Risk' and JBCXM__Account__r.Name='Zendesk Account 1' and JBCXM__ClosedDate__c=null and isDeleted=false")), "Risk CTA's in widget are not matching");
        Assert.assertEquals(opportunityCTA, sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,JBCXM__ClosedDate__c,JBCXM__TypeName__c,JBCXM__Account__r.Name FROM JBCXM__CTA__c where JBCXM__TypeName__c='Opportunity' and JBCXM__Account__r.Name='Zendesk Account 1' and JBCXM__ClosedDate__c=null and isDeleted=false")), "Opportunity CTA's in widget are not matching");
        Assert.assertEquals(eventCTA, sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,JBCXM__ClosedDate__c,JBCXM__TypeName__c,JBCXM__Account__r.Name FROM JBCXM__CTA__c where JBCXM__TypeName__c='Event' and JBCXM__Account__r.Name='Zendesk Account 1' and JBCXM__ClosedDate__c=null and isDeleted=false")), "Event CTA's in widget are not matching");

        // Validating Health Score shown in summary widget for the customer
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/apex_scripts/scorecard/Scorecard_CleanUp.txt"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/CoPilot/ApexScripts/Scorecard_Enable.apex"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/CoPilot/ApexScripts/Scorecard_PopulateDatas.apex"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/apex_scripts/scorecard/Scorecard_enable_numeric.apex"));

        // Updating overall score "63" to the customer "Zendesk Account 1"
        sfdc.runApexCode(resolveStrNameSpace("JBCXM__ScoringSchemeDefinition__c scheme=[SELECT Id,JBCXM__Score__c FROM JBCXM__ScoringSchemeDefinition__c where JBCXM__Score__c=63.0 limit 1];\n" +
                "JBCXM__CustomerInfo__c customer = [select Id,JBCXM__CurScoreId__c,JBCXM__CustomerName__c from JBCXM__CustomerInfo__c where JBCXM__CustomerName__c='Zendesk Account 1' and isDeleted=false limit 1];\n" +
                "customer.JBCXM__CurScoreId__c=scheme.Id;\n" +
                "update customer;"));

        Map<String, String> valuesMap4 = new HashMap<String, String>();
        valuesMap4.put("AccountID", account[0].getId());
        String payload4 = "{\"params\":\"{\\\"action\\\":\\\"scorecard.getData\\\",\\\"data\\\":{\\\"accountId\\\":\\\"${AccountID}\\\"}}\",\"actionType\":\"\"}";
        StrSubstitutor sub4 = new StrSubstitutor(valuesMap4);
        String actualPayload4 = sub4.replace(payload4);
        NsResponseObj response4 = zendeskImpl.zendeskSfdcProxy(actualPayload4);
        JsonNode data4 = mapper.readTree(response4.getData().toString());
        int actualOverallScore = data4.get("dataObj").get("account").findValue("curScore").asInt();
        Assert.assertEquals(actualOverallScore, 63, "Overall Score showing in widget is not matching");

        // Validating ENGAGEMENT count in Summary Widget
        Map<String, String> valuesMap3 = new HashMap<String, String>();
        valuesMap3.put("AccountID", account[0].getId());
        SObject[] engagementWidgetID = sfdc.getRecords((resolveStrNameSpace("SELECT Id,JBCXM__SystemName__c FROM JBCXM__Widgets__c  where JBCXM__SystemName__c='ENGAGEMENT' and isDeleted=false limit 1")));
        valuesMap3.put("WidgetID", engagementWidgetID[0].getId());
        String payload3 = "{\"params\":\"{\\\"action\\\":\\\"summary.getusagedata\\\",\\\"custInfo\\\":{\\\"accountId\\\":\\\"${AccountID}\\\",\\\"widgetid\\\":\\\"${WidgetID}\\\"}}\",\"actionType\":\"\"}";
        StrSubstitutor sub3 = new StrSubstitutor(valuesMap3);
        String actualPayload3 = sub3.replace(payload3);
        NsResponseObj response3 = zendeskImpl.zendeskSfdcProxy(resolveStrNameSpace(actualPayload3));
        Assert.assertTrue(response3.isResult(), "Result is not correct, please check response for more details");
        JsonNode data3 = mapper.readTree(response3.getData().toString());
        Assert.assertTrue(data3.get("success").asBoolean(), "Data is not success, please check response for more details");

        int actualUsers = data3.get("dataObj").get("userCount").asInt();
        Assert.assertEquals(actualUsers, 0);
    }
}
