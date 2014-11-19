package com.gainsight.sfdc.workflow.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.administration.pages.AdminCockpitConfigPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.*;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

import io.lamma.*;
import io.lamma.Date;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class WorkFlowTest extends BaseTest {


    private final String TEST_DATA_FILE         = "testdata/sfdc/workflow/tests/WorkFlow_Test.xls";
    private final String CREATE_USERS_SCRIPT    = TestEnvironment.basedir+"/testdata/sfdc/workflow/scripts/CreateUsers.txt";
    private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c];"+
                                        "Delete [select id from JBCXM__CSTask__c];"+
                                        "Delete [select id from Task];"+
                                        "Delete [Select id from JBCXM__StatePreservation__c];"+
                                        "Delete [Select id from JBCXM__Milestone__c];";
    ObjectMapper mapper                         = new ObjectMapper();
    private HashMap<Integer, String> weekDayMap = new HashMap<>();
    @BeforeClass
    public void setup() {
        userLocale = soql.getUserLocale();
        sfinfo= SFDCUtil.fetchSFDCinfo();
        basepage.login();
        isPackage = isPackageInstance();
        apex.runApexCodeFromFile(CREATE_USERS_SCRIPT, isPackage);
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
    public void createRiskCTA(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created");
    }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA2")
    public void createNonRecurringEventCTA(HashMap<String, String> testData) throws IOException {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA3")
    public void createOpportunityCTA(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA4")
   public void createRecurringEventCTA_Daily_EVeryWeekDay(HashMap<String, String> testData) throws IOException  {
    	 WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
           int temp = Integer.valueOf(cta.getDueDate());
         cta.setDueDate(getDateWithFormat(temp, 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        List<String> dates = getDates(recurEvent);
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName()); 
        workflowPage.createCTA(cta);
        cta.setDueDate(getDateWithFormat(temp, 0, true));
        Assert.assertEquals(1, countOfRecords(cta, true, null));
        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA5")
   public void createRecurringEventCTA_Daily_EveryNDays(HashMap<String, String> testData) throws IOException  {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        int temp = Integer.valueOf(cta.getDueDate());
        cta.setDueDate(getDateWithFormat(temp, 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        List<String> dates = getDates(recurEvent);
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        cta.setDueDate(getDateWithFormat(temp, 0, true));
        Assert.assertEquals(1, countOfRecords(cta, true, null));
        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA6")
   public void createRecurringEventCTA_Weekly_EveryNWeeks(HashMap<String, String> testData) throws IOException  {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        int temp = Integer.valueOf(cta.getDueDate());
        cta.setDueDate(getDateWithFormat(temp, 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        List<String> dates = getDates(recurEvent);
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        cta.setDueDate(getDateWithFormat(temp, 0, true));
        Assert.assertEquals(1, countOfRecords(cta, true, null));
        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA7")
   public void createRecurringEventCTA_Monthly(HashMap<String, String> testData) throws IOException, InterruptedException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        int temp = Integer.valueOf(cta.getDueDate());
        cta.setDueDate(getDateWithFormat(temp, 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        List<String> dates = getDates(recurEvent);
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        cta.setDueDate(getDateWithFormat(temp, 0, true));
        Assert.assertEquals(1, countOfRecords(cta, true, null));
        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA8")
   public void createRecurringEventCTA_Monthly_ByWeek(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        int temp = Integer.valueOf(cta.getDueDate());
        cta.setDueDate(getDateWithFormat(temp, 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        List<String> dates = getDates(recurEvent);
        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);

        cta.setDueDate(getDateWithFormat(temp, 0, true));
        Assert.assertEquals(1, countOfRecords(cta, true, null));
        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA9")
   public void createRecurringEventCTA_Yearly_ByMonth(HashMap<String, String> testData) throws IOException {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
        int temp = Integer.valueOf(cta.getDueDate());
        cta.setDueDate(getDateWithFormat(temp, 0, false));
        CTA.EventRecurring recurEvent=cta.getEventRecurring();
        List<String> dates = getDates(recurEvent);
        cta.setAssignee(sfinfo.getUserFullName());
        workflowPage.createCTA(cta);
        cta.setDueDate(getDateWithFormat(temp, 0, true));
        Assert.assertEquals(1, countOfRecords(cta, true, null));
        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
   }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA10")
   public void createRecurringEventCTA_Yearly_ByMonthAndWeek(HashMap<String, String> testData) throws IOException {
       WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       int temp = Integer.valueOf(cta.getDueDate());
       cta.setDueDate(getDateWithFormat(temp, 0, false));
       CTA.EventRecurring recurEvent=cta.getEventRecurring();
       List<String> dates = getDates(recurEvent);
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);
       cta.setDueDate(getDateWithFormat(temp, 0, true));
       Assert.assertEquals(1, countOfRecords(cta, true, null));
      //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA11")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA12")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA13")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA14")
   public void createRiskCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
        	task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
        	}

       workflowPage  = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
       }

   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA15")
   public void createAndReplacePlaybook_RiskCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);    
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Risk CTA is created ");
        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
        	}
        
        //Applying Playbook and verifying tasks
       workflowPage  = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }
       
       //Replacing Playbook and verifying updated tasks
       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
       for(Task task : updatedTasks) {
          	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
          	}
       cta.setDueDate(getHighestTaskDate(tasks));
       workflowPage = workflowPage.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

       for(Task task : updatedTasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }

       for(Task task : tasks) {
           Assert.assertFalse(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }
    }
   
  
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA16")
   public void createOpportunityCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
        	}

       workflowPage  = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
       //workflowPage.addTaskToCTA(cta, tasks);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
       }
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA17")
   public void createAndReplacePlaybook_OpporCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);    
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Risk CTA is created ");
       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
        for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
        	}
        
        //Applying Playbook and verifying tasks
       workflowPage  = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }
       
       //Replacing Playbook and verifying updated tasks
       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
       for(Task task : updatedTasks) {
          	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
          	}
       cta.setDueDate(getHighestTaskDate(tasks));
       workflowPage = workflowPage.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

       for(Task task : updatedTasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }

       for(Task task : tasks) {
           Assert.assertFalse(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }
    }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA18")
   public void createEventCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);      
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
       for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
        	}
        
       workflowPage = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
       }
   }

   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA19")
   public void createAndReplacePlaybook_EventCTA(HashMap<String,String> testData) throws IOException{
	   WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);    
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying Risk CTA is created ");
       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
       for(Task task : tasks) {
        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
        	}
        
        //Applying Playbook and verifying tasks
       workflowPage  = workflowPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
       for(Task task : tasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }
       
       //Replacing Playbook and verifying updated tasks
       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
       for(Task task : updatedTasks) {
          	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
          	}
       cta.setDueDate(getHighestTaskDate(tasks));
       workflowPage = workflowPage.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

       for(Task task : updatedTasks) {
           Assert.assertTrue(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }

       for(Task task : tasks) {
           Assert.assertFalse(workflowPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
       }
    }
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA20")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA21")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA22")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA20")
   public void createMilestoneForRiskCTA_Resolved(HashMap<String,String> testData) throws IOException{
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
      
      //Closing Risk CTA - to check if a RiskResolved Milestone is created.
      workflowPage.closeCTA(cta,false);
      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Resolved'";
      System.out.println("querying for:"+milestoneQuery_afterClose);
      SObject[] milestonesAfterClose=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
     
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA21")
   public void createMilestoneForOpportunityCTA_Won(HashMap<String,String> testData) throws IOException{
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
      
    //Closing Opportunity CTA - to check if a Opportunity Won Milestone is created.
      workflowPage.closeCTA(cta,false);
      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Won'";
      System.out.println("querying for:"+milestoneQuery_afterClose);
      SObject[] milestonesAfterClose=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA22")
   public void createMilestoneForEventCTA_Completed(HashMap<String,String> testData) throws IOException{
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
      
    //Closing Event CTA - to check if a Event Completed Milestone is created.
      workflowPage.closeCTA(cta,false);
      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Completed'";
      System.out.println("querying for:"+milestoneQuery_afterClose);
      SObject[] milestonesAfterClose=soql.getRecords(resolveStrNameSpace(milestoneQuery));
      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA23")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA24")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
   public void createAndCloseCTA_ClosedLostStatus(HashMap<String,String> testData) throws IOException{
       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
       cta.setAssignee(sfinfo.getUserFullName());
       workflowPage.createCTA(cta);     
       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
       workflowPage.updateCTAStatus_toClosedLost(cta);
       cta.setClosed(true);
       cta.setStatus("Closed Lost");
       workflowPage = workflowBasePage.clickOnListView();
       workflowPage = workflowPage.showClosedCTA();
       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA26")
   public void createAndCloseCTAWithTasks(HashMap<String,String> testData) throws IOException{
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA26")
   public void createCTA_WithTasks_AndCloseTasks(HashMap<String,String> testData) throws IOException{
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
    	   	workflowPage.openORCloseTask(task);
    	   	task.setStatus("Closed");
            Assert.assertTrue(workflowPage.verifyTaskDetails(task), "Verified all the tasks are closed for given CTA");
    	   }   		
   }
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
   public void create_CloseAndRe_OpenCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA26")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA27")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA11")
   public void createCTAWithTasks_AndDeleteTasks(HashMap<String,String> testData) throws IOException{
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
       for(Task task : tasks){
           Assert.assertTrue(workflowPage.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
           workflowPage.deleteTask(task);
           cta.setTaskCount(cta.getTaskCount()-1);
           Assert.assertFalse(workflowPage.isTaskDisplayedUnderCTA(cta, task), "Verified that the task has been deleted");
       }
   }
   
   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA28")
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
   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA28")
   public void createAndEditCTATasks(HashMap<String,String> testData) throws IOException{
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
       workflowPage.editTasks(cta, updatedTask,tasks.get(0));
       Assert.assertTrue(workflowPage.isTaskDisplayed(updatedTask),"Verified that the task is updated successfully");
   }
   
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
    public void syncTaskToSF_Manual(HashMap<String, String> testData) throws IOException {
        enableSFDCSync_Manual();
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
    public void deSyncTaskFromSFButKeepTask_Manual(HashMap<String, String> testData) throws IOException {
        enableSFDCSync_Manual();
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
        Assert.assertTrue(((sfTask==1)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF..but SF task still exists");
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
    public void deSyncTaskFromSFAndDeleteTask_Manual(HashMap<String, String> testData) throws IOException {
        enableSFDCSync_Manual();
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
        Assert.assertTrue(((sfTask==0)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task desynced from SF and SF task is deleted too");
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
    public void syncTaskToSF_AutoSync(HashMap<String, String> testData) throws IOException {
        enableSFDCSync_Auto();
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
        //workflowPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
        
        SObject[] syncedTasks=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
        int sfTask=soql.getRecordCount("select id from Task where id='"+syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))+"'");
        Assert.assertTrue((sfTask==1), "Verified that the task is created successfully in SF");
        disableSFAutoSync();
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
    public void deSyncTaskFromSFButKeepTask_AutoSync(HashMap<String, String> testData) throws IOException {
        enableSFDCSync_Auto();
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
        Assert.assertTrue(((sfTask==1)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF but remains in SF");
        disableSFAutoSync();
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
    public void deSyncTaskFromSFAndDeleteTask_AutoSync(HashMap<String, String> testData) throws IOException {
        enableSFDCSync_Auto();
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
        Assert.assertTrue(((sfTask==0)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF and also deleted from SF");
        disableSFAutoSync();
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA30")
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA31")
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA32")
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA33")
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA34")
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA35")
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
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA36")
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

    private void enableSFDCSync_Manual() throws IOException {
         SObject[] appSettings=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__CockpitConfig__c FROM JBCXM__ApplicationSettings__c"));
        if(appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=null && appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=""){
        String JBCXM__CockpitConfig__c = appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c")).toString();
        CockpitConfig config = mapper.readValue(JBCXM__CockpitConfig__c, CockpitConfig.class);
        boolean autoSync_FromConfig=Boolean.valueOf(config.getAutoSync());
        
        if(config.getPriorityMapping()=="{}" || !autoSync_FromConfig) { //priority mapping is empty ==> no SF to GS task mapping
            AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
            if(autoSync_FromConfig){ //in case autosync is enabled...disabling it...since this is only manual Sync case
            	admin=admin.disableAutoSync();            	
            }
            admin = admin.editAndSaveTaskMapping();
        }
    }
        else{
        	  AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
        	  admin = admin.editAndSaveTaskMapping();
        }
    }
    
    private void enableSFDCSync_Auto() throws IOException {
        SObject[] appSettings=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__CockpitConfig__c FROM JBCXM__ApplicationSettings__c"));
        if(appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=null && appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c"))!=""){
       String JBCXM__CockpitConfig__c = appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c")).toString();
       CockpitConfig config = mapper.readValue(JBCXM__CockpitConfig__c, CockpitConfig.class);
       boolean autoSync_FromConfig=Boolean.valueOf(config.getAutoSync());
       
       if(!autoSync_FromConfig) { //If autosync is true from config ==> already synced...so not entering the method
           AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
           admin=admin.enableAutoSync();
           admin = admin.editAndSaveTaskMapping();
       }
   }
        else{
            AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
            admin=admin.enableAutoSync();
            admin = admin.editAndSaveTaskMapping();
        }
        
   }
    private void disableSFAutoSync() throws IOException {
        SObject[] appSettings=soql.getRecords(resolveStrNameSpace("SELECT JBCXM__CockpitConfig__c FROM JBCXM__ApplicationSettings__c"));
        String JBCXM__CockpitConfig__c = appSettings[0].getField(resolveStrNameSpace("JBCXM__CockpitConfig__c")).toString();
        CockpitConfig config = mapper.readValue(JBCXM__CockpitConfig__c, CockpitConfig.class);
        if(Boolean.valueOf(config.getAutoSync()))  { //If auto sync is already disabled..nothing to do
            AdminCockpitConfigPage admin = basepage.clickOnAdminTab().clickOnCockpitConfigSubTab();
          	 admin = admin.disableAutoSync();
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
        if(dueDates != null) {
            for(String s : dueDates) {
                filter = filter+" JBCXM__DueDate__c = "+s+" OR ";
            }
            if(filter.length() > 1) {
                query = query+ " AND ( "+filter.substring(0, filter.length()-3)+" )";
            }
        }
        Report.logInfo("Query : " +resolveStrNameSpace(query));
        return getQueryRecordCount(resolveStrNameSpace(query));
    }

    public static List<String> getDates(CTA.EventRecurring recurring) {
        int start = Integer.valueOf(recurring.getRecurStartDate());
        int end = Integer.valueOf(recurring.getRecurEndDate());
        HashMap<String , Integer> monthlyMap = new HashMap<>();
        monthlyMap.put("Jan", 1);
        monthlyMap.put("Feb", 2);
        monthlyMap.put("Mar", 3);
        monthlyMap.put("Apr", 4);
        monthlyMap.put("May", 5);
        monthlyMap.put("Jun", 6);
        monthlyMap.put("Jul", 7);
        monthlyMap.put("Aug", 8);
        monthlyMap.put("Sep", 9);
        monthlyMap.put("Oct", 10);
        monthlyMap.put("Nov", 11);
        monthlyMap.put("Dec", 12);
        HashMap<String, Integer> weekDayMap = new HashMap<>();
        weekDayMap.put("Mon", 1);
        weekDayMap.put("Tue", 2);
        weekDayMap.put("Wed", 3);
        weekDayMap.put("Thu", 4);
        weekDayMap.put("Fri", 5);
        weekDayMap.put("Sat", 6);
        weekDayMap.put("Sun", 7);
        Date startDate = Date.today();
        Date endDate = Date.today();
        Date cal = Date.today();
        if(!recurring.getRecurringType().equalsIgnoreCase("Yearly")) {
            startDate= startDate.plusDays(start);
            endDate = endDate.plusDays(end);
        }

        List<String> dates = new ArrayList<>();
        if(recurring.getRecurringType().equalsIgnoreCase("Daily")) {
            if(recurring.getDailyRecurringInterval().equalsIgnoreCase("EveryWeekday")) {
                List<io.lamma.Date> a = Dates.from(startDate).to(endDate).except(HolidayRules.weekends()).build();
                return getFormatDates(a);
            } else {
                int byNoDays =  Integer.valueOf(recurring.getDailyRecurringInterval());
                List<io.lamma.Date> a = Dates.from(startDate).to(endDate).byDays(byNoDays).build();
                return getFormatDates(a);
            }
        } else if(recurring.getRecurringType().equalsIgnoreCase("Weekly")) {
            String exp[] = recurring.getWeeklyRecurringInterval().split("_");
            int byNoWeeks =  Integer.valueOf(exp[0]);
            for(int x=1; x<exp.length; x++) {
                dates.addAll(getFormatDates(Dates.from(startDate).to(endDate).byWeeks(byNoWeeks).on(DayOfWeek.of(weekDayMap.get(exp[x]))).build()));
            }
            return dates;
        }

        else if(recurring.getRecurringType().equalsIgnoreCase("Monthly")) {
            String exp[] = recurring.getMonthlyRecurringInterval().split("_");
            if(exp[0].equalsIgnoreCase("Day")) {
                int day = Integer.valueOf(exp[1].substring(0, exp[1].length()-2));
                int months = Integer.valueOf(exp[2]);
                List<io.lamma.Date> a =Dates.from(startDate).to(endDate).byMonths(months).on(Locators.nthDay(day)).build();
                return getFormatDates(a);
            } else {
                int day = Integer.valueOf(exp[1].substring(0, exp[1].length()-2));
                String weekDay = exp[2].substring(0,3);
                int months = Integer.valueOf(exp[3]);
                List<io.lamma.Date> a = Dates.from(startDate).to(endDate).byMonths(months).on(Locators.nth(day, DayOfWeek.of(weekDayMap.get(weekDay)))).build();
                return getFormatDates(a);
            }
        } else if(recurring.getRecurringType().equalsIgnoreCase("Yearly")) {
            String exp[] = recurring.getYearlyRecurringInterval().split("_");
            if(exp[0].equalsIgnoreCase("Day")) {
                int day = Integer.valueOf(exp[1].substring(0, exp[1].length()-2));
                String weekDay = exp[2].substring(0, 3);
                String month = exp[3].substring(0, 3);
                List<io.lamma.Date> a = Dates.from(start, cal.mm(), cal.dd()).to(end, cal.mm(), cal.dd()).byYear().byYear().on(Locators.nth(day, DayOfWeek.of(weekDayMap.get(weekDay))).of(Month.of(monthlyMap.get(month)))).build();
                return getFormatDates(a);
            } else {
                int day = Integer.valueOf(exp[1]);
                String month = exp[0].substring(0, 3);
                List<io.lamma.Date> a = Dates.from(start, cal.mm(), cal.dd()).to(end, cal.mm(), cal.dd()).byYear().on(Locators.nthDay(day).of(Month.of(monthlyMap.get(month)))).build();
                return getFormatDates(a);

            }
        }
        return dates;
    }

    private static List<String> getFormatDates(List<io.lamma.Date>  dates) {
        List<String> fDates = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(io.lamma.Date date : dates) {
            Calendar cal = Calendar.getInstance();
            cal.set(date.yyyy(), date.mm()-1, date.dd(), 0, 0, 0);
            fDates.add(dateFormat.format(cal.getTime()));
        }
        System.out.println(dates);
        return fDates;
    }

    private  String getTaskDateForPlaybook(int day) {
        Date today = Date.today();
        Date endDate = Date.today().plusDays(day);
        if(endDate.dayOfWeek() == DayOfWeek.SATURDAY) {
            endDate= endDate.plusDays(1);
        }
        int a = (Dates.from(today).to(endDate).build().size()) - (Dates.from(today).to(endDate).except(HolidayRules.weekends()).build().size());
        return getDateWithFormat(day+a, 0, false);
    } 
    private ArrayList<Task> getTaskFromSFDC(String playbookName) {
        ArrayList<Task> tasks = new ArrayList<>();
        SObject[] records = soql.getRecords("Select name, JBCXM__TaskJSON__c, JBCXM__PlaybookId__r.Name from JBCXM__PlaybookTasks__c where  JBCXM__PlaybookId__r.Name='"+playbookName+"'");
        if(!(records.length > 0)) {
            throw new RuntimeException("No tasks where found for the playbook");
        }
        for(SObject record : records) {
            if(record.getField(resolveStrNameSpace("JBCXM__TaskJSON__c"))==null) continue;
            String s = record.getField(resolveStrNameSpace("JBCXM__TaskJSON__c")).toString();
            try {
                List<PlaybookTask> pTemps = mapper.readValue(s, new TypeReference<ArrayList<PlaybookTask>>() {});
                Task t = new Task();
                for(PlaybookTask pk : pTemps) {
                    if(pk.getFieldName().equalsIgnoreCase("Subject__C")) {
                        t.setSubject(pk.getValue().get(0).get("key"));
                    } else if(pk.getFieldName().equalsIgnoreCase("Date__c")) {
                        t.setDate(pk.getValue().get(0).get("key"));
                    } else if(pk.getFieldName().equalsIgnoreCase("Priority__c")) {
                        t.setPriority(pk.getValue().get(0).get("key"));
                    } else if(pk.getFieldName().equalsIgnoreCase("Status__c")) {
                        t.setStatus(pk.getValue().get(0).get("key"));
                    }
                }
                tasks.add(t);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed While Parsing.");
            }
        }
       return tasks;
    }

    private String getHighestTaskDate(List<Task> tasks) {
        java.util.Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");;
        if (userLocale.contains("en_US")) {
            dateFormat = new SimpleDateFormat("M/d/yyyy");

        } else if (userLocale.contains("en_IN")) {
            dateFormat = new SimpleDateFormat("d/M/yyyy");
        }
        try {
            date = dateFormat.parse(tasks.get(0).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Please check the date format "+e.getErrorOffset());
        }
        for(Task t : tasks) {
            try {
                java.util.Date temp = dateFormat.parse(t.getDate());
                if(temp.getTime() > date.getTime()) {
                    date = temp;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("Please check the date format "+e.getErrorOffset());
            }
        }
        return dateFormat.format(date);
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}