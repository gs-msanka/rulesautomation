package com.gainsight.sfdc.workflow.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminCockpitConfigPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.*;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkFlowTest extends BaseTest {

    ObjectMapper mapper = new ObjectMapper();
    private final String TEST_DATA_FILE = "testdata/sfdc/workflow/tests/WorkFlow_Test.xls";
    private final String CTA_OBJECT     = "JBCXM__CTA__C";
    private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c];"+
    																	"Delete [select id from JBCXM__CSTask__c];"+
    																	"Delete [select id from Task];"+
    																	"Delete [Select id from JBCXM__StatePreservation__c];";

    private HashMap<Integer, String> weekDayMap = new HashMap<>();


    @BeforeClass
    public void setup() {
        userLocale = soql.getUserLocale();
        sfinfo= SFDCUtil.fetchSFDCinfo();
        basepage.login();
        isPackage = isPackageInstance();
        weekDayMap.put(1, "Sun");
        weekDayMap.put(2, "Mon");
        weekDayMap.put(3, "Tue");
        weekDayMap.put(4, "Wed");
        weekDayMap.put(5, "Thu");
        weekDayMap.put(6, "Fri");
        weekDayMap.put(7, "Sat");

    }
    
    @BeforeMethod
    public void clearCTAs(){
    	apex.runApex(resolveStrNameSpace(CLEANUP_SCRIPT));
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RISK_1")
    public void createRiskCTA(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created");
    }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_EVENT_2")
    public void createNonRecurringEventCTA(HashMap<String, String> testData) throws IOException {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_OPPOR_1")
    public void createOpportunityCTA(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_DAILY")
   public void createRecurringEventCTA_Daily_EVeryWeekDay(HashMap<String, String> testData) throws IOException  {
    	 WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
           int temp = Integer.valueOf(cta.getDueDate());
         cta.setDueDate(getDateWithFormat(temp, 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName()); 
        workflowPage.createCTA(cta);
        cta.setDueDate(getDateWithFormat(temp, 0, true));
        List<String> dates = new ArrayList<String>();
        Assert.assertEquals(1, countOfRecords(cta, true, dates));
        cta.setDueDate(getDateWithFormat(0, 0, false));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
        cta.setDueDate(getDateWithFormat(1, 0, false));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
        cta.setDueDate(getDateWithFormat(2, 0, false));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
        cta.setDueDate(getDateWithFormat(3, 0, false));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_N_DAYS")
   public void createRecurringEventCTA_Daily_EveryNDays(HashMap<String, String> testData) throws IOException  {
    	 WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
         cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0, false));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0, false));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Daily Recurring ( Recurs Every N Days) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_N_WEEKS")
   public void createRecurringEventCTA_Weekly_EveryNWeeks(HashMap<String, String> testData) throws IOException  {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0, false));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0, false));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Weekly Recurring ( Recurs Every N Weeks) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_MONTH")
   public void createRecurringEventCTA_Monthly(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0, false));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0, false));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Monthly Recurring ( Recurs Every Month) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_MONTHLY_BYWEEK")
   public void createRecurringEventCTA_Monthly_ByWeek(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0, false));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0, false));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Monthly Recurring ( Recurs Every n No.of Months on a specific day of Week) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_YEARLY_BYMONTH")
   public void createRecurringEventCTA_Yearly_ByMonth(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false).split("/")[2]);
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0, false).split("/")[2]);
        cta.setAssignee(sfinfo.getUserFullName());

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
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());

       	workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "OPPOR_CTA_WITH_TASKS")
   public void createOpportunityCTAWithTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Opportunity CTA");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EVENT_CTA_WITH_TASKS")
   public void createEventCTAWithTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RISK_CTA_WITH_PLAYBOOK")
   public void createRiskCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}

       workflowPage  = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
       }

   }

   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "OPPOR_CTA_WITH_PLAYBOOK")
   public void createOpportunityCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}

       workflowPage  = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks);
       //workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
       }
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EVENT_CTA_WITH_PLAYBOOK")
   public void createEventCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}
        
       workflowPage = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
       }
   }

   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RISK_CTA_WITH_MILESTONES")
   public void createMilestoneForRiskCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
      workflowPage.createMilestoneForCTA(cta);
      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
      System.out.println("querying for:"+milestoneQuery);
      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "OPPORTUNITY_CTA_WITH_MILESTONES")
   public void createMilestoneForOpportunityCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);      
      Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
      workflowPage.createMilestoneForCTA(cta);
      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
      System.out.println("querying for:"+milestoneQuery);
      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EVENT_CTA_WITH_MILESTONES")
   public void createMilestoneForEventCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

      cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
      cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);      
      workflowPage.createMilestoneForCTA(cta);
      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Created'";
      System.out.println("querying for:"+milestoneQuery);
      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
   }
   
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_FOR_SNOOZE")
   public void snoozeRiskCTA(HashMap<String,String> testData) throws IOException{
       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setSnoozeDate(getDateWithFormat(Integer.valueOf(cta.getSnoozeDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);      
      Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
       workflowPage.snoozeCTA(cta);
       workflowPage = workflowBasePage.clickOnListView();
        workflowPage = workflowPage.showSnoozeCTA();
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying the CTA has been set under Snoozed CTAs");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_MARK_AS_IMP")
   public void markCTAAsImp(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
       workflowPage = workflowPage.flagCTA(cta);
       cta.setImp(true);
       workflowPage = workflowPage.showFlaggedCTA();
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying the CTA has been set under Important CTAs");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE")
   public void createAndCloseCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);     
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
       workflowPage.closeCTA(cta, false);
       cta.setClosed(true);
       workflowPage = workflowBasePage.clickOnListView();
       workflowPage = workflowPage.showClosedCTA();
       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE_WITH_TASKS")
   public void createAndCloseRiskCTAWithTasks(HashMap<String,String> testData) throws IOException{
       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
    	   { 
    	   	Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
    	   	task.setStatus("Closed");
    	   }
       workflowPage.closeCTA(cta, true);
       cta.setClosed(true);
       cta.setStatus("Closed Won");
       workflowPage = workflowBasePage.clickOnListView();
       Assert.assertFalse(workflowPage.isCTADisplayed(cta));
       workflowPage = workflowPage.showClosedCTA();
       Assert.assertTrue(workflowPage.verifyClosedCTA(cta, true, tasks), "Verified that the CTA and all the corresponding tasks are closed");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE")
   public void create_CloseAndOpenCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);
       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
       workflowPage = workflowPage.closeCTA(cta, false);
       cta.setStatus("Closed Won");
       cta.setClosed(true);
       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
       workflowPage.openCTA(cta, false, null);
       cta.setStatus("Open");
       cta.setClosed(false);
       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
       Assert.assertTrue(workflowPage.verifyCTADetails(cta), "Verifying the CTA has been set under Closed CTAs");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE_WITH_TASKS")
   public void createClose_AndRe_OpenRiskCTA_WithTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
         cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
         cta.setAssignee(sfinfo.getUserFullName());
         workflowPage.createCTA(cta);      
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
      
       workflowPage.closeCTA(cta, true);
       cta.setStatus("Closed Won");
       cta.setClosed(true);
       for(Task t : tasks) t.setStatus("Closed"); 
       
       workflowPage.openCTA(cta,true,tasks);
       cta.setStatus("Open");
       cta.setClosed(false);
       for(Task t : tasks) t.setStatus("Open");
       
       Assert.assertTrue(workflowPage.verifyCTADetails(cta),"Verified that the CTA and all the corresponding tasks are Open again");
       for(Task task : tasks)
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" is open again");
   }

   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_FOR_UPDATE")
   public void createAndUpdateCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta); 
      
      CTA updatedCta=mapper.readValue(testData.get("UpdatedCTA"), CTA.class);
      
      if(updatedCta.getAssignee()==null)
    	  updatedCta.setAssignee(sfinfo.getUserFullName());
      updatedCta.setDueDate(getDateWithFormat(Integer.valueOf(updatedCta.getDueDate()),0, false));
      workflowPage.updateCTADetails(cta, updatedCta);
      Assert.assertTrue(workflowPage.isCTADisplayed(updatedCta), "Verifying Updated CTA Values");
   }
   
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RISK_1")
   public void createAndDeleteCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta); 
      
      workflowPage.deleteCTA(cta);
      Assert.assertFalse(workflowPage.isCTADisplayed(cta), "Verifying if the CTA is delete successfully");
      
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "UPDATE_TASKS")
   public void createAndUpdateCTATasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
       
       Task updatedTask=mapper.readValue(testData.get("updatedTask"),Task.class);
       updatedTask.setAssignee(sfinfo.getUserFullName());
       updatedTask.setDate(getDateWithFormat(Integer.valueOf(updatedTask.getDate()),0, false));
       workflowPage.updateTaskDetails(tasks.get(0), updatedTask);  //assuming that we are taking only one task for updation
       Assert.assertTrue(workflowPage.isTaskDisplayed(updatedTask),"Verified that the task is updated successfully");
   }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "SYNC_TASK_TO_SF")
    public void syncTaskToSF(HashMap<String, String> testData) throws IOException {
        enableSFDCSync();
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for (Task task : tasks) {
            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
        }

        workflowPage.addTaskToCTA(cta, tasks);
        workflowPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
        
        SObject[] syncedTasks=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
        int sfTask=soql.getRecordCount("select id from Task where id='"+syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))+"'");
        Assert.assertTrue((sfTask==1), "Verified that the task is created successfully in SF");
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "SYNC_TASK_TO_SF")
    public void deSyncTaskFromSFButKeepTask(HashMap<String, String> testData) throws IOException {
        enableSFDCSync();
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for (Task task : tasks) {
            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
        }

        workflowPage.addTaskToCTA(cta, tasks);
        workflowPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
        SObject[] syncedTasks=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
        
        workflowPage.deSyncTaskFromSF(cta,tasks.get(0),true);
        SObject[] desyncedTasks=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
        System.out.println("desynced taks...."+desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")));
        int sfTask=soql.getRecordCount("select id from Task where id='"+taskId+"'");
        Assert.assertTrue(((sfTask==1)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is created successfully in SF");
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "SYNC_TASK_TO_SF")
    public void deSyncTaskFromSFAndDeleteTask(HashMap<String, String> testData) throws IOException {
        enableSFDCSync();
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for (Task task : tasks) {
            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
        }

        workflowPage.addTaskToCTA(cta, tasks);
        workflowPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
        SObject[] syncedTasks=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
        
        workflowPage.deSyncTaskFromSF(cta,tasks.get(0),false);
        SObject[] desyncedTasks=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));

        int sfTask=soql.getRecordCount("select id from Task where id='"+taskId+"' and isDeleted=false");
        Assert.assertTrue(((sfTask==0)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is created successfully in SF");
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "OVERDUE_RISK")
    public void createRiskCTA_Overdue(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        cta.setOverDue(true);
        Assert.assertTrue(workflowPage.isOverDueCTADisplayed(cta), "Verifying risk CTA is created - which is overdue");
    }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void verifyHeaderTypeFilter(HashMap<String,String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        List<CTA> ctaList =  mapper.readValue(testData.get("CTAs"), new TypeReference<ArrayList<CTA>>() {});
        for(CTA cta : ctaList) {
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
            cta.setAssignee(sfinfo.getUserFullName());
            workflowPage.createCTA(cta);
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        workflowPage = workflowPage.selectCTATypeFilter("Risk");
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(1)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(2)));
        workflowPage = workflowPage.selectCTATypeFilter("Event");
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(1)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(2)));
        workflowPage = workflowPage.selectCTATypeFilter("Opportunity");
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(2)));
        workflowPage = workflowPage.unSelectCTATypeFilter("Risk");
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(2)));
        workflowPage = workflowPage.unSelectCTATypeFilter("Event");
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(2)));
        workflowPage = workflowPage.unSelectCTATypeFilter("Opportunity");
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(2)));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void verifyHeaderPriorityFilter(HashMap<String,String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        List<CTA> ctaList1 =  mapper.readValue(testData.get("CTA1"), new TypeReference<ArrayList<CTA>>() {});
        List<CTA> ctaList2  = mapper.readValue(testData.get("CTA2"), new TypeReference<ArrayList<CTA>>() {});
        List<CTA> ctaList3  = mapper.readValue(testData.get("CTA3"), new TypeReference<ArrayList<CTA>>() {});

        CTA cta = ctaList1.get(0);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(ctaList1.get(0));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));

        cta = ctaList2.get(0);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));

        cta = ctaList3.get(0);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));

        ctaList1.get(1).setAssignee(sfinfo.getUserFullName());
        ctaList1.get(1).setDueDate(getDateWithFormat(Integer.valueOf(ctaList1.get(1).getDueDate()), 0, false));
        workflowPage = workflowPage.updateCTADetails(ctaList1.get(0), ctaList1.get(1));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList1.get(1)));

        workflowPage = workflowPage.selectCTAPriorityFilter(ctaList1.get(1).getPriority());
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList1.get(1)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList2.get(0)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList3.get(0)));
        workflowPage = workflowPage.unSelectCTAPriorityFilter(ctaList1.get(1).getPriority());
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList1.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList2.get(0)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList3.get(0)));

        ctaList2.get(1).setAssignee(sfinfo.getUserFullName());
        ctaList2.get(1).setDueDate(getDateWithFormat(Integer.valueOf(ctaList2.get(1).getDueDate()), 0, false));
        workflowPage = workflowPage.updateCTADetails(ctaList2.get(0), ctaList2.get(1));

        workflowPage = workflowPage.selectCTAPriorityFilter(ctaList2.get(1).getPriority());
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList1.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList2.get(1)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList3.get(0)));
        workflowPage = workflowPage.unSelectCTAPriorityFilter(ctaList2.get(1).getPriority());
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList1.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList2.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList3.get(0)));

        workflowPage = workflowPage.selectCTAPriorityFilter(ctaList3.get(0).getPriority());
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList1.get(1)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList2.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList3.get(0)));
        workflowPage = workflowPage.unSelectCTAPriorityFilter(ctaList3.get(0).getPriority());
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList1.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList2.get(1)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList3.get(0)));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void changeAssigneeViewAndVerify(HashMap<String,String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        List<CTA> ctaList1 =  mapper.readValue(testData.get("CTA1"), new TypeReference<ArrayList<CTA>>() {});
        List<CTA> ctaList2  = mapper.readValue(testData.get("CTA2"), new TypeReference<ArrayList<CTA>>() {});
        for(CTA cta : ctaList1) {
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
            cta.setAssignee(sfinfo.getUserFullName());
            workflowPage.createCTA(cta);
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        CTA cta = ctaList2.get(0);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        CTA updateCta = ctaList2.get(1);
        updateCta.setDueDate(getDateWithFormat(Integer.valueOf(updateCta.getDueDate()), 0, false));
        workflowPage = workflowPage.updateCTADetails(cta, updateCta);
        Assert.assertTrue(workflowPage.isCTADisplayed(updateCta));
        workflowPage = workflowPage.changeAssigneeView(updateCta.getAssignee());
        Assert.assertTrue(workflowPage.isCTADisplayed(updateCta));
        for(CTA ct : ctaList1) {
            Assert.assertFalse(workflowPage.isCTADisplayed(ct));
        }
        workflowPage = workflowPage.changeAssigneeView(sfinfo.getUserFullName());
        for(CTA ct : ctaList1) {
            Assert.assertTrue(workflowPage.isCTADisplayed(ct));
        }
        Assert.assertFalse(workflowPage.isCTADisplayed(updateCta));
        workflowPage = workflowPage.changeAssigneeView(updateCta.getAssignee());
        Assert.assertTrue(workflowPage.isCTADisplayed(updateCta));
        CTA updateCta1 = ctaList2.get(2);
        updateCta1.setDueDate(getDateWithFormat(Integer.valueOf(updateCta1.getDueDate()), 1, false));
        workflowPage = workflowPage.updateCTADetails(updateCta, updateCta1);
        Assert.assertTrue(workflowPage.isCTADisplayed(updateCta1));
        workflowPage  = workflowPage.changeAssigneeView(updateCta1.getAssignee());
        Assert.assertTrue(workflowPage.isCTADisplayed(updateCta1));
        workflowPage = workflowPage.changeAssigneeView(null);
        for(CTA ct : ctaList1) {
            Assert.assertTrue(workflowPage.isCTADisplayed(ct));
        }
        Assert.assertTrue(workflowPage.isCTADisplayed(updateCta1));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void verifyHeaderShowFilter(HashMap<String,String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        List<CTA> ctaList =  mapper.readValue(testData.get("CTAs"), new TypeReference<ArrayList<CTA>>() {});
        for(CTA cta : ctaList) {
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
            cta.setAssignee(sfinfo.getUserFullName());
            workflowPage.createCTA(cta);
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        workflowPage = workflowPage.closeCTA(ctaList.get(0), false);
        ctaList.get(0).setClosed(true);
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(0)));
        workflowPage = workflowPage.showClosedCTA();
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        workflowPage = workflowPage.hideClosedCTA();
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(0)));
        ctaList.remove(0);


        workflowPage = workflowPage.flagCTA(ctaList.get(0));
        workflowPage = workflowPage.showFlaggedCTA();
        for(CTA cta : ctaList) {
            if(ctaList.get(0)==cta) {
                Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(0)));
            } else {
                Assert.assertFalse(workflowPage.isCTADisplayed(cta));
            }
        }
        workflowPage = workflowPage.disabledFlaggedCTAView();
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        ctaList.get(0).setSnoozeDate(getDateWithFormat(Integer.valueOf(ctaList.get(0).getSnoozeDate()), 0, false));

        workflowPage.snoozeCTA(ctaList.get(0));
        workflowPage = workflowPage.showSnoozeCTA();
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(1)));
        workflowPage = workflowPage.hideSnoozeCTA();
        Assert.assertFalse(workflowPage.isCTADisplayed(ctaList.get(0)));
        Assert.assertTrue(workflowPage.isCTADisplayed(ctaList.get(1)));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void verifyCTAGrouping(HashMap<String,String> testData) throws IOException, CloneNotSupportedException {
        WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
        WorkflowPage workflowPage = workflowBasePage.clickOnListView();
        List<CTA> ctaList =  mapper.readValue(testData.get("CTAs"), new TypeReference<ArrayList<CTA>>() {});
        for(CTA cta : ctaList) {
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
            cta.setAssignee(sfinfo.getUserFullName());
            workflowPage.createCTA(cta);
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        workflowPage = workflowPage.selectGroupBy("Customer");
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayedInGroup(cta.getCustomer(), cta));
        }
        Assert.assertEquals(1, workflowPage.countOfCTASInGroup(ctaList.get(0).getCustomer(), null));

        workflowPage = workflowPage.selectGroupBy("Type");
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayedInGroup(cta.getType(), cta));
        }
        Assert.assertEquals(3, workflowPage.countOfCTASInGroup(ctaList.get(0).getType(), null));

        workflowPage = workflowPage.selectGroupBy("Status");
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayedInGroup(cta.getStatus(), cta));
        }
        Assert.assertEquals(9, workflowPage.countOfCTASInGroup(ctaList.get(0).getStatus(), null));
        workflowPage = workflowPage.selectGroupBy("All");
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayedInGroup("All", cta));
        }
        Assert.assertEquals(9, workflowPage.countOfCTASInGroup("All", null));
        CTA cta = (CTA) ctaList.get(0).clone();
        cta.setDueDate(getDateWithFormat(-1, 0, false));
        cta.setStatus("In Progress");
        workflowPage = workflowPage.updateCTADetails(ctaList.get(0), cta);

        workflowPage = workflowBasePage.clickOnListView();
        workflowPage = workflowPage.selectGroupBy("New and Due");
        Assert.assertTrue(workflowPage.isCTADisplayedInGroup("Due", cta));
        Assert.assertEquals(1, workflowPage.countOfCTASInGroup("Due", null));
        Assert.assertEquals(8, workflowPage.countOfCTASInGroup("New", null));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void verifyCalenderView(HashMap<String,String> testData) throws IOException, CloneNotSupportedException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnCalendarView();
        List<CTA> ctaList =  mapper.readValue(testData.get("CTAs"), new TypeReference<ArrayList<CTA>>() {});
        for(CTA cta : ctaList) {
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
            cta.setAssignee(sfinfo.getUserFullName());
            workflowPage.createCTA(cta);
        }

        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        workflowPage = workflowPage.selectCalendarView("DAILY");
        cal.add(Calendar.DATE, 5); // Added 5 Days
        System.out.println(monthMap.get(String.valueOf(cal.get(Calendar.MONTH))));
        System.out.println(weekDayMap.get(cal.get(Calendar.DAY_OF_WEEK)));
        workflowPage = workflowPage.selectCalendarDay(cal.get(Calendar.DATE), monthMap.get(String.valueOf(cal.get(Calendar.MONTH))), weekDayMap.get(cal.get(Calendar.DAY_OF_WEEK)));
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        workflowPage = workflowPage.selectCalendarView("MONTHLY");
        workflowPage = workflowPage.selectCalendarMonth(monthMap.get(String.valueOf(cal.get(Calendar.MONTH))), cal.get(Calendar.YEAR));

        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }

        workflowPage = workflowPage.selectCalendarView("WEEKLY");

        int a = -cal.get(Calendar.DAY_OF_WEEK)+cal.getFirstDayOfWeek();
        System.out.println("no of days added to label :" +a);
        cal.add(Calendar.DATE, a);
        System.out.println(cal.get(Calendar.DATE));
        System.out.println(cal.get(Calendar.WEEK_OF_YEAR));

        workflowPage = workflowPage.selectCalendarWeek(cal.get(Calendar.DATE), cal.get(Calendar.WEEK_OF_YEAR), monthMap.get(String.valueOf(cal.get(Calendar.MONTH))));
        for(CTA cta : ctaList) {
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
    }

    private void enableSFDCSync() throws IOException {
        SObject[] appSettings=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__CockpitConfig__c FROM JBCXM__ApplicationSettings__c"));
        String JBCXM__CockpitConfig__c = appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c")).toString();
        CockpitConfig config = mapper.readValue(JBCXM__CockpitConfig__c, CockpitConfig.class);
        if(!Boolean.valueOf(config.getAutoSync())) {
            AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
            admin = admin.enableAutoSync();
            admin = admin.editAndSaveTaskMapping();
        }
    }

    public int countOfRecords(CTA cta, boolean recurring, List<String> dueDates) {
        String query = "Select id, Name, JBCXM__Account__R.Name, JBCXM__Assignee__c, " +
                "JBCXM__DueDate__c, JBCXM__IsRecurring__c, JBCXM__Reason__r.Name, " +
                "JBCXM__Priority__r.Name, JBCXM__Stage__r.Name, JBCXM__Type__r.Name From JBCXM__CTA__C where " +
                "Name like '%"+cta.getSubject()+"%'  AND JBCXM__Account__R.Name Like '%"+cta.getCustomer()+"%' " +
                "AND JBCXM__IsRecurring__c = "+recurring+" AND JBCXM__Reason__r.Name='"+cta.getReason()+"' AND " +
                "JBCXM__Priority__r.Name = '"+cta.getPriority()+"' AND JBCXM__Stage__r.Name = '"+cta.getStatus()+"' " +
                "AND isDeleted = false";
        String filter = "";
        for(String s : dueDates) {
            filter = filter+" JBCXM__DueDate__c = "+s+" OR ";
        }
        if(filter.length() > 1) {
            query = query+ " AND ( "+filter.substring(0, filter.length()-3)+" )";
        }

        Report.logInfo("Query : " +resolveStrNameSpace(query));
        return getQueryRecordCount(resolveStrNameSpace(query));
    }

    public List<String> getDates(CTA.EventRecurring recurring, boolean bulkFormat) {
        List<String> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int start = Integer.valueOf(recurring.getRecurStartDate());
        int end = Integer.valueOf(recurring.getRecurEndDate());
        cal.add(Calendar.DATE, start);
        if(recurring.getRecurringType().equalsIgnoreCase("Daily")) {
            if(recurring.getDailyRecurringInterval().equalsIgnoreCase("EveryWeekday")) {
                while (start<=end) {

                }
            } else {

            }

        } else if(recurring.getRecurringType().equalsIgnoreCase("Weekly")) {

        } else if(recurring.getRecurringType().equalsIgnoreCase("Monthly")) {

        } else if(recurring.getRecurringType().equalsIgnoreCase("Yearly")) {

        }
        return dates;
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}
