package com.gainsight.sfdc.workflow.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;

import com.gainsight.utils.DataProviderArguments;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkFlowTest extends BaseTest {

    ObjectMapper mapper = new ObjectMapper();
    private final String TEST_DATA_FILE = "testdata/sfdc/workflow/WorkFlow_Test.xls";

    @BeforeClass
    public void setup() {
        userLocale = soql.getUserLocale();
        basepage.login();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RISK_1")
    public void createRiskCTA(HashMap<String, String> testData) throws IOException {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       workflowPage.createCTA(cta);
       cta.setAssignee("Giribabu"); // Current User Should be set here, Framework needs extension to get user name.
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created");
    }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_EVENT_2")
    public void createNonRecurringEventCTA(HashMap<String, String> testData) throws IOException {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        workflowPage.createCTA(cta);
        cta.setAssignee("Giribabu"); // Current User Should be set here, Framework needs extension to get user name.
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        workflowPage.isCTADisplayed(cta);
    }

    @Test
    public void createOpportunityCTA() {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA opporCTA=new CTA();
        opporCTA.setType("Opportunity");
        opporCTA.setSubject("sample Opportunity CTA");
        opporCTA.setCustomer("Abacus Programming Corp");
        opporCTA.setReason("Product Release");
        opporCTA.setDueDate("12/11/2014");
        opporCTA.setComments("sample Opportunity CTA");
        workflowPage.createCTA(opporCTA);
    }

   @Test
   public void createRecurringEventCTA_Daily_EVeryWeekDay() {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setRecurring(true);
	   /*eventCTA.setRecurringType("Daily");
	   eventCTA.setDailyRecurringInterval("EveryWeekDay");
	   eventCTA.setRecurStartDate("");
	   eventCTA.setRecurEndDate("");*/
	   workflowPage.createCTA(eventCTA);
   }
   
   @Test
   public void createRecurringEventCTA_Daily_EveryNDays() {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setRecurring(true);
	   /*eventCTA.setRecurringType("Daily");
	   eventCTA.setDailyRecurringInterval("2");
	   eventCTA.setRecurStartDate("");
	   eventCTA.setRecurEndDate("");   */
	   workflowPage.createCTA(eventCTA);
   }
   
   @Test
   public void createRecurringEventCTA_Weekly_EveryNWeeks() {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setRecurring(true);
	   /*eventCTA.setRecurringType("Weekly");
	   eventCTA.setWeeklyRecurringInterval("Week_2_Mon");
	   eventCTA.setRecurStartDate("");
	   eventCTA.setRecurEndDate("");        */
	   workflowPage.createCTA(eventCTA);
   }
   
   @Test
   public void createRecurringEventCTA_Monthly() {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setRecurring(true);
	   /*eventCTA.setRecurringType("Monthly");
	   eventCTA.setMonthlyRecurringInterval("Day_2_Month_2");
	   eventCTA.setRecurStartDate("");
	   eventCTA.setRecurEndDate("");      */
	   workflowPage.createCTA(eventCTA);
   }
   
   @Test
   public void createRecurringEventCTA_Monthly_ByWeek() {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setRecurring(true);
	 /*  eventCTA.setRecurringType("Monthly");
	   eventCTA.setMonthlyRecurringInterval("Week_2_Month_2");
	   eventCTA.setRecurStartDate("");
	   eventCTA.setRecurEndDate(""); */
	   workflowPage.createCTA(eventCTA);
   }
   
   @Test
   public void createRecurringEventCTA_Yearly_ByMonth() {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setRecurring(true);
	   /*eventCTA.setRecurringType("Yearly");
	   eventCTA.setMonthlyRecurringInterval("Day_2_Month_2");
	   eventCTA.setRecurStartDate("");
	   eventCTA.setRecurEndDate("");    */
	   workflowPage.createCTA(eventCTA);
   }
   
   @Test
   public void createRecurringEventCTA_Yearly_ByMonthAndWeek() {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setRecurring(true);
	   /* eventCTA.setRecurringType("Yearly");
	   eventCTA.setMonthlyRecurringInterval("Week_2_Month_2");
	   eventCTA.setRecurStartDate("");
	   eventCTA.setRecurEndDate(""); */
	   workflowPage.createCTA(eventCTA);
   }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }

}
