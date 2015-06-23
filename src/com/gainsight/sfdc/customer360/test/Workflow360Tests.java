package com.gainsight.sfdc.customer360.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gainsight.testdriver.Application;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Workflow360Page;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.gainsight.sfdc.workflow.tests.WorkflowSetup;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

public class Workflow360Tests extends WorkflowSetup{
	
	private final String TEST_DATA_FILE         = "testdata/sfdc/workflow/tests/WorkFlow_Test_360.xls";
    private final String CREATE_USERS_SCRIPT    = Application.basedir+"/testdata/sfdc/workflow/scripts/CreateUsers.txt";
    private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c where JBCXM__Account__c in (select id from Account where Name='CTA Account 360')];"+
                                        "Delete [select id from JBCXM__CSTask__c where JBCXM__Account__c in (select id from Account where Name='CTA Account 360')];";
    private final String CREATE_ACCOUNTS_CUSTOMERS=Application.basedir+"/testdata/sfdc/workflow/scripts/Create_Accounts_Customers_For_CTA.txt";
    
    ObjectMapper mapper                         = new ObjectMapper();

    @BeforeClass
    public void setup() throws Exception {
    	sfdc.connect();
        basepage.login();
        metaUtil.createExtIdFieldOnAccount(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        metaUtil.createExtIdFieldOnUser(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_USERS_SCRIPT));
        cleanPlaybooksData();
        loadDefaultPlaybooks();
    }
    
    @BeforeMethod
    public void clearCTAsForThisAccount(){
        sfdc.runApexCode(resolveStrNameSpace(CLEANUP_SCRIPT));
    }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
    public void createRiskCTA_in360(HashMap<String, String> testData) throws IOException {
	     CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	     Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
	     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
         cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
         cta.setAssignee(sfinfo.getUserFullName());
         workflow360.createCTA(cta);
         Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created");
    }
	
	  @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA2")
	    public void createNonRecurringEventCTA_in360(HashMap<String, String> testData) throws IOException {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Event CTA is created");
	    }

	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA3")
	    public void createOpportunityCTA_in360(HashMap<String, String> testData) throws IOException {
	    	CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	    	Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
	    }

	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA4")
	   public void createRecurringEventCTA_Daily_EVeryWeekDay_in360(HashMap<String, String> testData) throws IOException  {
	    	  CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	    	  Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	          int temp = Integer.valueOf(cta.getDueDate());
	         cta.setDueDate(getDateWithFormat(temp, 0, false));
	        CTA.EventRecurring recurEvent=cta.getEventRecurring();
	        List<String> dates = getDates(recurEvent);
	        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
	        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName()); 
	        workflow360.createCTA(cta);
	        cta.setDueDate(getDateWithFormat(temp, 0, true));
	        Assert.assertEquals(1, countOfRecords(cta, true, null));
	        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
	   }
	   
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA5")
	   public void createRecurringEventCTA_Daily_EveryNDays_in360(HashMap<String, String> testData) throws IOException  {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        int temp = Integer.valueOf(cta.getDueDate());
	        cta.setDueDate(getDateWithFormat(temp, 0, false));
	        CTA.EventRecurring recurEvent=cta.getEventRecurring();
	        List<String> dates = getDates(recurEvent);
	        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
	        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        cta.setDueDate(getDateWithFormat(temp, 0, true));
	        Assert.assertEquals(1, countOfRecords(cta, true, null));
	        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
	   }
	   
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA6")
	   public void createRecurringEventCTA_Weekly_EveryNWeeks_in360(HashMap<String, String> testData) throws IOException  {
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       int temp = Integer.valueOf(cta.getDueDate());
	        cta.setDueDate(getDateWithFormat(temp, 0, false));
	        CTA.EventRecurring recurEvent=cta.getEventRecurring();
	        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
	        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        cta.setDueDate(getDateWithFormat(temp, 0, true));
	        Assert.assertEquals(1, countOfRecords(cta, true, null));
	        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
	   }
	   
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA7")
	   public void createRecurringEventCTA_Monthly_in360(HashMap<String, String> testData) throws IOException, InterruptedException {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        int temp = Integer.valueOf(cta.getDueDate());
	        cta.setDueDate(getDateWithFormat(temp, 0, false));
	        CTA.EventRecurring recurEvent=cta.getEventRecurring();
	        List<String> dates = getDates(recurEvent);
	        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
	        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        cta.setDueDate(getDateWithFormat(temp, 0, true));
	        Assert.assertEquals(1, countOfRecords(cta, true, null));
	        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
	   }
	   
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA8")
	   public void createRecurringEventCTA_Monthly_ByWeek_in360(HashMap<String, String> testData) throws IOException {
	         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	         Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        int temp = Integer.valueOf(cta.getDueDate());
	        cta.setDueDate(getDateWithFormat(temp, 0, false));
	        CTA.EventRecurring recurEvent=cta.getEventRecurring();
	        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
	        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);

	        cta.setDueDate(getDateWithFormat(temp, 0, true));
	        Assert.assertEquals(1, countOfRecords(cta, true, null));
	        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
	   }
	   
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA9")
	   public void createRecurringEventCTA_Yearly_ByMonth_in360(HashMap<String, String> testData) throws IOException {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        int temp = Integer.valueOf(cta.getDueDate());
	        cta.setDueDate(getDateWithFormat(temp, 0, false));
	        CTA.EventRecurring recurEvent=cta.getEventRecurring();
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        cta.setDueDate(getDateWithFormat(temp, 0, true));
	        Assert.assertEquals(1, countOfRecords(cta, true, null));
	        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
	   }

	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA10")
	   public void createRecurringEventCTA_Yearly_ByMonthAndWeek_in360(HashMap<String, String> testData) throws IOException {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        int temp = Integer.valueOf(cta.getDueDate());
	       cta.setDueDate(getDateWithFormat(temp, 0, false));
	       CTA.EventRecurring recurEvent=cta.getEventRecurring();
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);
	       cta.setDueDate(getDateWithFormat(temp, 0, true));
	       Assert.assertEquals(1, countOfRecords(cta, true, null));
	      //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA11")
	   public void createRiskCTAWithTasks_in360(HashMap<String,String> testData) throws IOException{
		    CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		    Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
		    cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());

	       	workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        }
	        
	       workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	       Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA40")
	   public void createRiskCTAWithTasks_AssignedToDifferentUsers_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());

	       	workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        }
	        
	       workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	       Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA12")
	   public void createOpportunityCTAWithTasks_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Opportunity CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        }
	        
	       workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	       Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Opportunity CTA");
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA13")
	   public void createEventCTAWithTasks_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Event CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        	}
	        
	       workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	       Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
	   }
	   //TBD FROM HERE
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA14")
	   public void createRiskCTAWithPlaybook_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Event CTA is created ");
	        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	        	task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	        	task.setFromCustomer360orWidgets(true);
	        	}
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
	       }
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA41")
	   public void createRiskCTAWithPlaybook_DifferentAssignees_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Event CTA is created ");
	        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
	       String[] users={"GiribabuG","SrividyaR","RajeshY","RamyaK","SunandP","HiteshS"};
	       int i=0;
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	        	task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	        	task.setAssignee(users[i]); if(++i >=5) i=0;
	        	task.setFromCustomer360orWidgets(true);
	        	}

	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
	       }
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA15")
	   public void createAndReplacePlaybook_RiskCTA_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);    
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Risk CTA is created ");
	        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	            task.setFromCustomer360orWidgets(true);
	        	}
	        
	        //Applying Playbook and verifying tasks
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	       
	       //Replacing Playbook and verifying updated tasks
	       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
	       for(Task task : updatedTasks) {
	          	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	            task.setFromCustomer360orWidgets(true);
	          	}
	       cta.setDueDate(getHighestTaskDate(tasks));
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

	       for(Task task : updatedTasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }

	       for(Task task : tasks) {
	           Assert.assertFalse(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	    }
	   
	  
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA16")
	   public void createOpportunityCTAWithPlaybook_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Event CTA is created ");
	       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	            task.setFromCustomer360orWidgets(true);
	        	}

	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       //workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
	       }
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA17")
	   public void createAndReplacePlaybook_OpporCTA_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);    
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Risk CTA is created ");
	       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	            task.setFromCustomer360orWidgets(true);
	        	}
	        
	        //Applying Playbook and verifying tasks
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	       
	       //Replacing Playbook and verifying updated tasks
	       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
	       for(Task task : updatedTasks) {
	          	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	           task.setFromCustomer360orWidgets(true);
	          	}
	       cta.setDueDate(getHighestTaskDate(tasks));
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

	       for(Task task : updatedTasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }

	       for(Task task : tasks) {
	           Assert.assertFalse(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	    }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA18")
	   public void createEventCTAWithPlaybook_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Event CTA is created ");
	       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
	       for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	            task.setFromCustomer360orWidgets(true);
	        	}
	        
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
	       }
	   }

	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA19")
	   public void createAndReplacePlaybook_EventCTA_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);    
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying Risk CTA is created ");
	       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
	       for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	           task.setFromCustomer360orWidgets(true);
	        	}
	        //Applying Playbook and verifying tasks
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	       
	       //Replacing Playbook and verifying updated tasks
	       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
	       for(Task task : updatedTasks) {
	          	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	           task.setFromCustomer360orWidgets(true);
	          	}
	       cta.setDueDate(getHighestTaskDate(tasks));
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

	       for(Task task : updatedTasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	    }
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA20")
	   public void createMilestoneForRiskCTA_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();

	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	      workflow360.createMilestoneForCTA(cta);
	      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
	      System.out.println("querying for:"+milestoneQuery);
	      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA21")
	   public void createMilestoneForOpportunityCTA_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();

	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	      workflow360.createCTA(cta);      
	      Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	      workflow360.createMilestoneForCTA(cta);
	      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
	      System.out.println("querying for:"+milestoneQuery);
	      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA22")
	   public void createMilestoneForEventCTA_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();

	      cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	      cta.setAssignee(sfinfo.getUserFullName());
	      workflow360.createCTA(cta);      
	      workflow360.createMilestoneForCTA(cta);
	      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Created'";
	      System.out.println("querying for:"+milestoneQuery);
	      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA20")
	   public void createMilestoneForRiskCTA_Resolved_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();

	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	      workflow360.createMilestoneForCTA(cta);
	      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
	      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	      
	      //Closing Risk CTA - to check if a RiskResolved Milestone is created.
	      workflow360.closeCTA(cta,false);
	      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Resolved'";
	      System.out.println("querying for:"+milestoneQuery_afterClose);
	      SObject[] milestonesAfterClose=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	     
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA21")
	   public void createMilestoneForOpportunityCTA_Won_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	      workflow360.createCTA(cta);      
	      Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	      workflow360.createMilestoneForCTA(cta);
	      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
	      System.out.println("querying for:"+milestoneQuery);
	      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	      
	    //Closing Opportunity CTA - to check if a Opportunity Won Milestone is created.
	      workflow360.closeCTA(cta,false);
	      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Won'";
	      System.out.println("querying for:"+milestoneQuery_afterClose);
	      SObject[] milestonesAfterClose=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA22")
	   public void createMilestoneForEventCTA_Completed_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();

	      cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	      cta.setAssignee(sfinfo.getUserFullName());
	      workflow360.createCTA(cta);      
	      workflow360.createMilestoneForCTA(cta);
	      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Created'";
	      System.out.println("querying for:"+milestoneQuery);
	      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	      
	    //Closing Event CTA - to check if a Event Completed Milestone is created.
	      workflow360.closeCTA(cta,false);
	      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Completed'";
	      System.out.println("querying for:"+milestoneQuery_afterClose);
	      SObject[] milestonesAfterClose=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA23")
	   public void snoozeRiskCTA_in360(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
          Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
	     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setSnoozeDate(getDateWithFormat(Integer.valueOf(cta.getSnoozeDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	      workflow360.createCTA(cta);      
	      Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	       workflow360.snoozeCTA(cta);
	       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
	       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
	       workflowPage = workflowPage.showSnoozeCTA();
	       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying the CTA has been set under Snoozed CTAs");
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA24")
	   public void markCTAAsImp(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	       workflow360 = workflow360.flagCTA(cta);
	       cta.setImp(true);
	       /*WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
	       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
	       workflowPage = workflowPage.showFlaggedCTA();*/
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying the CTA has been set under Important CTAs");
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
	   public void createAndCloseCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
	         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	         Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
		     cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);     
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	       workflow360.closeCTA(cta, false);
	       cta.setClosed(true);
	       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
	       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
	       workflowPage = workflowPage.showClosedCTA();
	       workflowPage=	workflowPage.selectGroupBy("Created Date (New)");
	       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
	   }
	   
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
	   public void createAndCloseCTA_ClosedRiskStatus(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);     
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	       workflow360.updateCTAStatus_toClosedLost(cta);
	       cta.setClosed(true);
	       cta.setStatus("Closed Risk");
	       
	       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
	       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
	       workflowPage = workflowPage.showClosedCTA();
	       workflowPage=	workflowPage.selectGroupBy("Created Date (New)");
	       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA26")
	   public void createAndCloseCTAWithTasks(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        	task.setFromCustomer360orWidgets(true);
	        	}
	        
	        workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	    	   { 
	    	   	Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
	    	   	task.setStatus("Closed");
	    	   	task.setFromCustomer360orWidgets(true);
	    	   }
	       workflow360.closeCTA(cta, true);
	       cta.setClosed(true);
	       cta.setStatus("Closed Success");
	       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
	       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
	       //Assert.assertFalse(workflowPage.isCTADisplayed(cta));
	       workflowPage = workflowPage.showClosedCTA();
	       workflowPage=	workflowPage.selectGroupBy("Created Date (New)");
	       Assert.assertTrue(workflowPage.verifyClosedCTA(cta, true, tasks), "Verified that the CTA and all the corresponding tasks are closed");
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA26")
	   public void createCTA_WithTasks_AndCloseTasks(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        	task.setFromCustomer360orWidgets(true);
	        	}
	        
	        workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	    	   { 
	    	   	Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
	    	   	workflow360.openORCloseTask(task);
	    	   	task.setStatus("Closed");
	            Assert.assertTrue(workflow360.verifyTaskDetails(task), "Verified all the tasks are closed for given CTA");
	    	   }   		
	   }
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
	   public void create_CloseAndRe_OpenCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
		  CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		  Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);
	       Assert.assertTrue(workflow360.isCTADisplayed(cta));
	       workflow360.closeCTA(cta, false);
	       cta.setStatus("Closed Success");
	       cta.setClosed(true);
	       Assert.assertTrue(workflow360.isCTADisplayed(cta));
	       workflow360.openCTA(cta, false, null);
	       cta.setStatus("New");
	       cta.setClosed(false);
	       Assert.assertTrue(workflow360.isCTADisplayed(cta));
	       Assert.assertTrue(workflow360.verifyCTADetails(cta), "Verifying the CTA has been set under Closed CTAs");
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA27")
	   public void createAndUpdateCTA(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta); 
	      
	      CTA updatedCta=mapper.readValue(testData.get("UpdatedCTA"), CTA.class);
	      Workflow360Page workflow360_2=basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false).goToCockpitSection();
	      if(updatedCta.getAssignee()==null)
	    	  updatedCta.setAssignee(sfinfo.getUserFullName());
	      updatedCta.setDueDate(getDateWithFormat(Integer.valueOf(updatedCta.getDueDate()),0, false));
	      workflow360_2.updateCTADetails(cta, updatedCta);
	      Assert.assertTrue(workflow360_2.isCTADisplayed(updatedCta), "Verifying Updated CTA Values");
	   }
	   
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
	   public void createAndDeleteCTA(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta); 
	      
	       workflow360.deleteCTA(cta);
	      Assert.assertFalse(workflow360.isCTADisplayed(cta), "Verifying if the CTA is delete successfully");
	      
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA11")
	   public void createCTAWithTasks_AndDeleteTasks(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());

	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        	task.setFromCustomer360orWidgets(true);
	        	}
	        
	        workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks){
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
	           workflow360.deleteTask(task);
	           cta.setTaskCount(cta.getTaskCount()-1);
	           Assert.assertFalse(workflow360.isTaskDisplayedUnderCTA(cta, task), "Verified that the task has been deleted");
	       }
	   }
	   
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA28")
	   public void createAndUpdateCTATasks(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	      
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        	task.setFromCustomer360orWidgets(true);
	        	}
	        
	        workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
	       
	       Task updatedTask=mapper.readValue(testData.get("updatedTask"),Task.class);
	       updatedTask.setAssignee(sfinfo.getUserFullName());
	       updatedTask.setDate(getDateWithFormat(Integer.valueOf(updatedTask.getDate()),0, false));
	       workflow360.updateTaskDetails(tasks.get(0), updatedTask);  //assuming that we are taking only one task for updation
	       Assert.assertTrue(workflow360.isTaskDisplayed(updatedTask),"Verified that the task is updated successfully");
	   }

    /*
       Commenting as Hitesh mentioned that this feature is failing in 4.22 & will not be supported going further.
	   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA28")
	   public void createAndEditCTATasks(HashMap<String,String> testData) throws IOException{
	       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	       Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
		    Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	       cta.setAssignee(sfinfo.getUserFullName());
	       workflow360.createCTA(cta);      
	       Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for(Task task : tasks) {
	        	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
	        	task.setFromCustomer360orWidgets(true);
	        	}
	        
	        workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks)
	           Assert.assertTrue(workflow360.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
	       
	       Task updatedTask=mapper.readValue(testData.get("updatedTask"),Task.class);
	       updatedTask.setAssignee(sfinfo.getUserFullName());
	       updatedTask.setDate(getDateWithFormat(Integer.valueOf(updatedTask.getDate()),0, false));
	       workflow360.editTasks(cta, updatedTask,tasks.get(0));
	       Assert.assertTrue(workflow360.isTaskDisplayed(updatedTask),"Verified that the task is updated successfully");
	   }
	  */
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
	    public void syncTaskToSF_Manual(HashMap<String, String> testData) throws IOException {
	    	enableSFDCSync_Manual();
		     CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	    	 Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			 Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for (Task task : tasks) {
	            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
	            task.setFromCustomer360orWidgets(true);
	        }

	        workflow360.addTaskToCTA(cta, tasks);
	        workflow360.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
	        
	        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        int sfTask=sfdc.getRecordCount("select id from Task where id='"+syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))+"'");
	        Assert.assertTrue(sfTask==1, "Verified that the task is created successfully in SF");
	    }
	    
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
	    public void deSyncTaskFromSFButKeepTask_Manual(HashMap<String, String> testData) throws IOException {
	    	enableSFDCSync_Manual();
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			 Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        	        
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for (Task task : tasks) {
	            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
	            task.setFromCustomer360orWidgets(true);
	        }

	        workflow360.addTaskToCTA(cta, tasks);
	        workflow360.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
	        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
	        
	        workflow360.deSyncTaskFromSF(cta,tasks.get(0),true);
	        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        System.out.println("desynced taks...."+desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")));
	        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"'");
	        Assert.assertTrue((sfTask==1 &&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF..but SF task still exists");
	        Assert.assertEquals(1, sfTask);
	    }
	    
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
	    public void deSyncTaskFromSFAndDeleteTask_Manual(HashMap<String, String> testData) throws IOException {
	    	enableSFDCSync_Manual();
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			 Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        	        
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for (Task task : tasks) {
	            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
	            task.setFromCustomer360orWidgets(true);
	        }

	        workflow360.addTaskToCTA(cta, tasks);
	        workflow360.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
	        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
	        
	        workflow360.deSyncTaskFromSF(cta,tasks.get(0),false);
	        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));

	        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"' and isDeleted=false");
	        Assert.assertTrue(((sfTask==0)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task desynced from SF and SF task is deleted too");
	    }
	    
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
	    public void syncTaskToSF_AutoSync(HashMap<String, String> testData) throws IOException {
	    	enableSFDCSync_Auto();
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			 Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for (Task task : tasks) {
	            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
	            task.setFromCustomer360orWidgets(true);
	        }

	        workflow360.addTaskToCTA(cta, tasks);
	        //workflowPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
	        
	        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        int sfTask=sfdc.getRecordCount("select id from Task where id='"+syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))+"'");
	        Assert.assertTrue((sfTask==1), "Verified that the task is created successfully in SF");
	        basepage.switchToMainWindow();
	        disableSFAutoSync();
	    }
	    
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
	    public void deSyncTaskFromSFButKeepTask_AutoSync(HashMap<String, String> testData) throws IOException {
	    	enableSFDCSync_Auto();
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			 Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	             
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for (Task task : tasks) {
	            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
	            task.setFromCustomer360orWidgets(true);
	        }

	        workflow360.addTaskToCTA(cta, tasks);
	        workflow360.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
	        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
	        
	        workflow360.deSyncTaskFromSF(cta,tasks.get(0),true);
	        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        System.out.println("desynced taks...."+desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")));
	        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"'");
	        Assert.assertTrue(((sfTask==1)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF but remains in SF");
	        basepage.switchToMainWindow();
	        disableSFAutoSync();
	    }
	    
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
	    public void deSyncTaskFromSFAndDeleteTask_AutoSync(HashMap<String, String> testData) throws IOException {
	    	enableSFDCSync_Auto();
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			 Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	     
	        
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created ");
	        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
	        for (Task task : tasks) {
	            if (task.getAssignee() == null) task.setAssignee(sfinfo.getUserFullName());
	            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
	            task.setFromCustomer360orWidgets(true);
	        }

	        workflow360.addTaskToCTA(cta, tasks);
	        workflow360.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
	        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
	        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
	        
	        workflow360.deSyncTaskFromSF(cta,tasks.get(0),false);
	        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));

	        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"' and isDeleted=false");
	        Assert.assertTrue(((sfTask==0)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF and also deleted from SF");
	        basepage.switchToMainWindow();
	        disableSFAutoSync();
	    }
	    
	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA30")
	    public void createRiskCTA_Overdue(HashMap<String, String> testData) throws IOException {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	        Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
			 Workflow360Page workflow360 = customer360Page.goToCockpitSection();
	     
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        workflow360.createCTA(cta);
	        cta.setOverDue(true);
	        Assert.assertTrue(workflow360.isOverDueCTADisplayed(cta), "Verifying risk CTA is created - which is overdue");
	    }
}
