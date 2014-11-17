package com.gainsight.sfdc.workflow.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.rulesEngine.pojos.RuleSurveyTriggerCriteria;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.*;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

import com.sun.java.swing.plaf.windows.resources.windows_pt_BR;
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
    private final String TEST_DATA_FILE = "testdata/sfdc/workflow/tests/WorkFlow_Test.xls";
    private final String CTA_OBJECT     = "JBCXM__CTA__C";
    private final String DELETE_CTA_SCRIPT = "Delete [Select id from JBCXM__CTA__c];";


    @BeforeClass
    public void setup() {
        userLocale = soql.getUserLocale();
        sfinfo= SFDCUtil.fetchSFDCinfo();
        basepage.login();
        isPackage = isPackageInstance();
    }
    
    @BeforeMethod
    public void clearCTAs(){
    	apex.runApex(resolveStrNameSpace(DELETE_CTA_SCRIPT));
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RISK_1")
    public void createRiskCTA(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created");
    }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_EVENT_2")
    public void createNonRecurringEventCTA(HashMap<String, String> testData) throws IOException {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));    
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_OPPOR_1")
    public void createOpportunityCTA(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_DAILY")
   public void createRecurringEventCTA_Daily_EVeryWeekDay(HashMap<String, String> testData) throws IOException  {
    	 WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
         cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Daily Recurring ( Recurs EveryWeekday) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_N_DAYS")
   public void createRecurringEventCTA_Daily_EveryNDays(HashMap<String, String> testData) throws IOException  {
    	 WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
         cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Daily Recurring ( Recurs Every N Days) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_N_WEEKS")
   public void createRecurringEventCTA_Weekly_EveryNWeeks(HashMap<String, String> testData) throws IOException  {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Weekly Recurring ( Recurs Every N Weeks) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_EVERY_MONTH")
   public void createRecurringEventCTA_Monthly(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Monthly Recurring ( Recurs Every Month) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_MONTHLY_BYWEEK")
   public void createRecurringEventCTA_Monthly_ByWeek(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0));
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0));
        cta.setAssignee(sfinfo.getUserFullName());

        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Monthly Recurring ( Recurs Every n No.of Months on a specific day of Week) CTA is created");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RECUR_EVENT_YEARLY_BYMONTH")
   public void createRecurringEventCTA_Yearly_ByMonth(HashMap<String, String> testData) throws IOException {
    	WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()),0).split("/")[2]);
        recurEvent.setRecurEndDate( getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()),0).split("/")[2]);
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
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());

       	workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "OPPOR_CTA_WITH_TASKS")
   public void creatOpportunityCTAWithTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
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
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
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
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
   }

   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "OPPOR_CTA_WITH_PLAYBOOK")
   public void createOpportunityCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EVENT_CTA_WITH_PLAYBOOK")
   public void createEventCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
       Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
   }

   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RISK_CTA_WITH_MILESTONES")
   public void createMilestoneForRiskCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
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

       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);      
      workflowPage.createMilestoneForCTA(cta);
      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
      System.out.println("querying for:"+milestoneQuery);
      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: "+cta.getSubject()+", Reason: "+cta.getReason()));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EVENT_CTA_WITH_MILESTONES")
   public void createMilestoneForEventCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

      cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
      cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);      
      workflowPage.createMilestoneForCTA(cta);
      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Created'";
      System.out.println("querying for:"+milestoneQuery);
      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: "+cta.getSubject()+", Reason: "+cta.getReason()));
   }
   
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_FOR_SNOOZE")
   public void snoozeRiskCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setSnoozeDate(getDateWithFormat(Integer.valueOf(cta.getSnoozeDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);      
       workflowPage.snoozeCTA(cta);
       Assert.assertTrue(workflowPage.verifySnoozeCTA(cta), "Verifying the CTA has been set under Snoozed CTAs");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_MARK_AS_IMP")
   public void markCTAAsImp(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       workflowPage.flagCTA(cta);
       Assert.assertTrue(workflowPage.verifyImpCTA(cta), "Verifying the CTA has been set under Important CTAs");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE")
   public void createAndCloseCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);     
       
       workflowPage.closeCTA(cta, false);
       Assert.assertTrue(workflowPage.verifyClosedCTA(cta,false,null), "Verifying the CTA has been set under Closed CTAs");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE_WITH_TASKS")
   public void createAndCloseRiskCTAWithTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
     workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
    	   { 
    	   	Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
    	   	task.setStatus("Closed Won");
    	   }
       workflowPage.closeCTA(cta, true);
       Assert.assertTrue(workflowPage.verifyClosedCTA(cta,true,tasks),"Verified that the CTA and all the corresponding tasks are closed");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE")
   public void create_CloseAndOpenCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
      workflowPage.createCTA(cta);      
       workflowPage.closeCTA(cta, false);
       workflowPage.openCTA(cta,false,null);
       Assert.assertTrue(workflowPage.verifyCTADetails(cta), "Verifying the CTA has been set under Closed CTAs");
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_CLOSE_WITH_TASKS")
   public void createClose_AndOpenRiskCTA_WithTasks(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
         cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
         cta.setAssignee(sfinfo.getUserFullName());
         workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
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
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta); 
      
      CTA updatedCta=mapper.readValue(testData.get("UpdatedCTA"), CTA.class);
      
      if(updatedCta.getAssignee()==null)
    	  updatedCta.setAssignee(sfinfo.getUserFullName());
      updatedCta.setDueDate(getDateWithFormat(Integer.valueOf(updatedCta.getDueDate()),0));
      workflowPage.updateCTADetails(cta, updatedCta);
      Assert.assertTrue(workflowPage.isCTADisplayed(updatedCta), "Verifying Updated CTA Values");
   }
   
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA_RISK_1")
   public void createAndDeleteCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
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
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0));      	
        	}
        
       workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks)
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
       
       Task updatedTask=mapper.readValue(testData.get("updatedTask"),Task.class);
       updatedTask.setAssignee(sfinfo.getUserFullName());
       updatedTask.setDate(getDateWithFormat(Integer.valueOf(updatedTask.getDate()),0));
       workflowPage.updateTaskDetails(tasks.get(0), updatedTask);  //assuming that we are taking only one task for updation
       Assert.assertTrue(workflowPage.isTaskDisplayed(updatedTask),"Verified that the task is updated successfully");
   }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "SYNC_TASK_TO_SF")
    public void syncTaskToSF(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);

        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        //Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for (Task task : tasks) {
            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0));
        }

        workflowPage.addTaskToCTA(cta, tasks);
        for (Task task : tasks)
            Assert.assertTrue(workflowPage.isTaskDisplayed(task), "Verifying the task -\"" + task.getSubject() + "\" created for Risk CTA");

        workflowPage.syncTasksToSF(tasks);
        Assert.assertTrue(workflowPage.areTasksSyncedToSF(tasks), "Verified that the task is updated successfully");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void verifyHeaderTypeFilter(HashMap<String,String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        List<CTA> ctaList =  mapper.readValue(testData.get("CTAs"), new TypeReference<ArrayList<CTA>>() {});
        for(CTA cta : ctaList) {
            workflowPage.createCTA(cta);
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
            cta.setAssignee(sfinfo.getUserFullName());
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
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(ctaList1.get(0));
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));

        cta = ctaList2.get(0);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));

        cta = ctaList3.get(0);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));

        ctaList1.get(1).setAssignee(sfinfo.getUserFullName());
        ctaList1.get(1).setDueDate(getDateWithFormat(Integer.valueOf(ctaList1.get(1).getDueDate()), 0));
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
        ctaList2.get(1).setDueDate(getDateWithFormat(Integer.valueOf(ctaList2.get(1).getDueDate()), 0));
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
           workflowPage.createCTA(cta);
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
            cta.setAssignee(sfinfo.getUserFullName());
            Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        }
        CTA cta = ctaList2.get(0);
        workflowPage.createCTA(cta);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
        cta.setAssignee(sfinfo.getUserFullName());
        Assert.assertTrue(workflowPage.isCTADisplayed(cta));
        CTA updateCta = ctaList2.get(1);
        updateCta.setDueDate(getDateWithFormat(Integer.valueOf(updateCta.getDueDate()), 0));
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
        updateCta1.setDueDate(getDateWithFormat(Integer.valueOf(updateCta1.getDueDate()), 1));
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
            workflowPage.createCTA(cta);
            cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0));
            cta.setAssignee(sfinfo.getUserFullName());
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
    }


        @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}
