package com.gainsight.sfdc.workflow.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;

import org.testng.annotations.*;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkFlowTest extends BaseTest {
	WorkflowPage workflowPage;
    @BeforeClass
    public void setup() {
        basepage.login();
       workflowPage = basepage.clickOnWorkflowTab().clickOnListView();

    }

    @Test
    public void sampleTest() {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        Report.logInfo("Working okay");
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
    
   @Test
   public void createRiskCTA()
   {   
	   CTA riskCTA=new CTA();
	   riskCTA.setType("Risk");
	   riskCTA.setSubject("sample risk CTA");
	   riskCTA.setCustomer("Abacus Programming Corp");
	   riskCTA.setReason("Product Release");
	   riskCTA.setDueDate("12/11/2014");
	   riskCTA.setComments("abcd efgh");
	   workflowPage.createCTA(riskCTA);
   }
   
   @Test
   public void createOpportunityCTA()
   {
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
   public void createNonRecurringEventCTA()
   {
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setIsRecurring(false);
	   workflowPage.createCTA(eventCTA);
   }
   
   @Test
   public void createRecurringEventCTA()
   {
	   CTA eventCTA=new CTA();
	   eventCTA.setType("Event");
	   eventCTA.setSubject("sample Non Recurring Event CTA");
	   eventCTA.setCustomer("Abacus Programming Corp");
	   eventCTA.setReason("Product Release");
	   eventCTA.setDueDate("12/11/2014");
	   eventCTA.setComments("sample Non Recurring Event CTA");
	   eventCTA.setIsRecurring(true);
	   workflowPage.createCTA(eventCTA);
   }
}
