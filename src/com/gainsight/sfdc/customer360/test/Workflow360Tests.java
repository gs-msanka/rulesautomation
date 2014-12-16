package com.gainsight.sfdc.customer360.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Workflow360Page;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.gainsight.sfdc.workflow.tests.WorkFlowTest;
import com.gainsight.sfdc.workflow.tests.WorkflowSetup;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

public class Workflow360Tests extends WorkflowSetup{
	
	private final String TEST_DATA_FILE         = "testdata/sfdc/workflow/tests/WorkFlow_Test_360.xls";
    private final String CREATE_USERS_SCRIPT    = TestEnvironment.basedir+"/testdata/sfdc/workflow/scripts/CreateUsers.txt";
    private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c where JBCXM__Account__c in (select id from Account where Name='CTA Account 360')];"+
                                        "Delete [select id from JBCXM__CSTask__c where JBCXM__Account__c in (select id from Account where Name='CTA Account 360')];";
    private final String CREATE_ACCOUNTS_CUSTOMERS=TestEnvironment.basedir+"/testdata/sfdc/workflow/scripts/Create_Accounts_Customers_For_CTA.txt";
    
    ObjectMapper mapper                         = new ObjectMapper();
    private HashMap<Integer, String> weekDayMap = new HashMap<>();
    
    @BeforeClass
    public void setup() {
        sfinfo= SFDCUtil.fetchSFDCinfo();
        userLocale = sfinfo.getUserLocale();
        userTimezone = TimeZone.getTimeZone(sfinfo.getUserTimeZone());
        basepage.login();
        isPackage = isPackageInstance();
        apex.runApexCodeFromFile(CREATE_ACCOUNTS_CUSTOMERS,isPackage);
        apex.runApexCodeFromFile(CREATE_USERS_SCRIPT, isPackage);
    }
    
    @BeforeMethod
    public void clearCTAsForThisAccount(){
    	apex.runApex(resolveStrNameSpace(CLEANUP_SCRIPT));
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
	        	}
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
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
	        	}

	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
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
	        	}
	        
	        //Applying Playbook and verifying tasks
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	       
	       //Replacing Playbook and verifying updated tasks
	       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
	       for(Task task : updatedTasks) {
	          	if(task.getAssignee()==null) {
	                task.setAssignee(sfinfo.getUserFullName());
	            }
	            task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	          	}
	       cta.setDueDate(getHighestTaskDate(tasks));
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

	       for(Task task : updatedTasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
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
	        	}

	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       //workflow360.addTaskToCTA(cta, tasks);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
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
	        	}
	        
	        //Applying Playbook and verifying tasks
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	       
	       //Replacing Playbook and verifying updated tasks
	       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
	       for(Task task : updatedTasks) {
	          	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	          	}
	       cta.setDueDate(getHighestTaskDate(tasks));
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

	       for(Task task : updatedTasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
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
	        	}
	        
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
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
	        	}
	        //Applying Playbook and verifying tasks
	       workflow360  = workflow360.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
	       for(Task task : tasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
	       }
	       
	       //Replacing Playbook and verifying updated tasks
	       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
	       for(Task task : updatedTasks) {
	          	if(task.getAssignee()==null) task.setAssignee(sfinfo.getUserFullName());
	           task.setDate(getTaskDateForPlaybook(Integer.valueOf(task.getDate())));
	          	}
	       cta.setDueDate(getHighestTaskDate(tasks));
	       workflow360 = workflow360.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

	       for(Task task : updatedTasks) {
	           Assert.assertTrue(workflow360.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
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
	      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
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
	      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
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
	      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
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
	      System.out.println("querying for:"+milestoneQuery);
	      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	      
	      //Closing Risk CTA - to check if a RiskResolved Milestone is created.
	      workflow360.closeCTA(cta,false);
	      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Resolved'";
	      System.out.println("querying for:"+milestoneQuery_afterClose);
	      SObject[] milestonesAfterClose=soql.getRecords(resolveStrNameSpace(milestoneQuery));
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
	      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	      
	    //Closing Opportunity CTA - to check if a Opportunity Won Milestone is created.
	      workflow360.closeCTA(cta,false);
	      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Won'";
	      System.out.println("querying for:"+milestoneQuery_afterClose);
	      SObject[] milestonesAfterClose=soql.getRecords(resolveStrNameSpace(milestoneQuery));
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
	      SObject[] milestones=soql.getRecords(resolveStrNameSpace(milestoneQuery));
	      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
	      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
	      
	    //Closing Event CTA - to check if a Event Completed Milestone is created.
	      workflow360.closeCTA(cta,false);
	      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Completed'";
	      System.out.println("querying for:"+milestoneQuery_afterClose);
	      SObject[] milestonesAfterClose=soql.getRecords(resolveStrNameSpace(milestoneQuery));
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
}
