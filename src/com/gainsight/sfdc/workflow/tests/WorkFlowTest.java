package com.gainsight.sfdc.workflow.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.rulesEngine.pojos.RuleSurveyTriggerCriteria;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.*;
import com.gainsight.utils.DataProviderArguments;
import com.sun.jna.platform.win32.Netapi32Util.UserInfo;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
       
        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created");
    }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_EVENT_2")
    public void createNonRecurringEventCTA(HashMap<String, String> testData) throws IOException {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        workflowPage.createCTA(cta);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_OPPOR_1")
    public void createOpportunityCTA(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        workflowPage.createCTA(cta);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_DAILY")
   public void createRecurringEventCTA_Daily_EVeryWeekDay(HashMap<String, String> testData) throws IOException  {
    	 WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Daily Recurring ( Recurs EveryWeekday) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_N_DAYS")
   public void createRecurringEventCTA_Daily_EveryNDays(HashMap<String, String> testData) throws IOException  {
    	 WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Daily Recurring ( Recurs Every N Days) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_N_WEEKS")
   public void createRecurringEventCTA_Weekly_EveryNWeeks(HashMap<String, String> testData) throws IOException  {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Weekly Recurring ( Recurs Every N Weeks) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_MONTH")
   public void createRecurringEventCTA_Monthly(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Monthly Recurring ( Recurs Every Month) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_MONTHLY_BYWEEK")
   public void createRecurringEventCTA_Monthly_ByWeek(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }

        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Monthly Recurring ( Recurs Every n No.of Months on a specific day of Week) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_YEARLY_BYMONTH")
   public void createRecurringEventCTA_Yearly_ByMonth(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        if(cta.getAssignee()==null) {
            cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
        }
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0).split("/")[2]);
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0).split("/")[2]);

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Yearly Recurring ( Recurs On a specific day of a specific month yearly) CTA is created");
   }
   
   @Test
   public void createRecurringEventCTA_Yearly_ByMonthAndWeek() {
      //<TBD>
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RISK_CTA_WITH_TASKS")
   public void createRiskCTAWithTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       workflowPage.createCTA(cta);
      
       if(cta.getAssignee()==null) {
           cta.setAssignee(sfinfo.getUserName()); //  Setting the current logged in user..if there is no data provided in test data
       } 
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the tasks for Risk CTA");
   }
    @AfterClass
    public void tearDown() {
        basepage.logout();
    }

}
