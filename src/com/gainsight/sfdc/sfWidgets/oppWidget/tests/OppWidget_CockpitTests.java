package com.gainsight.sfdc.sfWidgets.oppWidget.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.sfdc.sfWidgets.oppWidget.pages.OppWidget_CockpitPage;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.gainsight.sfdc.workflow.tests.WorkflowSetup;
import com.gainsight.testdriver.Application;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;

public class OppWidget_CockpitTests  extends WorkflowSetup {
	
    ObjectMapper mapper                         = new ObjectMapper();
    private final String TEST_DATA_FILE         = "testdata/sfdc/workflow/tests/WorkFlow_Test_Widgets.xls";
    private final String CREATE_USERS_SCRIPT    = Application.basedir+"/testdata/sfdc/workflow/scripts/CreateUsers.txt";
    private final String CREATE_ACCOUNTS_CUSTOMERS=Application.basedir+"/testdata/sfdc/workflow/scripts/Create_Accounts_Customers_For_CTA.txt";
    private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c];"+
                                        "Delete [select id from JBCXM__CSTask__c];"+
                                        "Delete [select id from Task];"+
                                        "Delete [Select id from JBCXM__StatePreservation__c];"+
                                        "Delete [Select id from JBCXM__Milestone__c];";
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
	  
	  @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
	  @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
	    public void createRiskCTA(HashMap<String, String> testData) throws IOException {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		 	SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
	        OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfdcInfo.getUserFullName());
	        cta.setFromCustomer360orWidgets(true);
	        oppWfPage.createCTA(cta);
	        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created");
	    }
	
	  @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
	 @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	 @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA2")
	  public void createNonRecurringEventCTA(HashMap<String, String> testData) throws IOException {
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Event CTA is created");
		    }
            
	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA3")
		    public void createOpportunityCTA(HashMap<String, String> testData) throws IOException {
		    	CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		    	SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created");
		    }

	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA4")
		   public void createRecurringEventCTA_Daily_EVeryWeekDay(HashMap<String, String> testData) throws IOException  {
		    	  CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		    	  SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		          int temp = Integer.valueOf(cta.getDueDate());
		         cta.setDueDate(getDateWithFormat(temp, 0, false));
		        CTA.EventRecurring recurEvent=cta.getEventRecurring();
		        List<String> dates = getDates(recurEvent);
		        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
		        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        cta.setDueDate(getDateWithFormat(temp, 0, true));
		        Assert.assertEquals(1, countOfRecords(cta, true, null));
		        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
		   }
		   
	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA5")
		   public void createRecurringEventCTA_Daily_EveryNDays(HashMap<String, String> testData) throws IOException  {
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        int temp = Integer.valueOf(cta.getDueDate());
		        cta.setDueDate(getDateWithFormat(temp, 0, false));
		        CTA.EventRecurring recurEvent=cta.getEventRecurring();
		        List<String> dates = getDates(recurEvent);
		        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
		        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        cta.setDueDate(getDateWithFormat(temp, 0, true));
		        Assert.assertEquals(1, countOfRecords(cta, true, null));
		        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
		   }
		   
	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA6")
		   public void createRecurringEventCTA_Weekly_EveryNWeeks(HashMap<String, String> testData) throws IOException  {
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       int temp = Integer.valueOf(cta.getDueDate());
		        cta.setDueDate(getDateWithFormat(temp, 0, false));
		        CTA.EventRecurring recurEvent=cta.getEventRecurring();
		        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
		        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        cta.setDueDate(getDateWithFormat(temp, 0, true));
		        Assert.assertEquals(1, countOfRecords(cta, true, null));
		        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
		   }
		   
	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA7")
		   public void createRecurringEventCTA_Monthly(HashMap<String, String> testData) throws IOException, InterruptedException {
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        int temp = Integer.valueOf(cta.getDueDate());
		        cta.setDueDate(getDateWithFormat(temp, 0, false));
		        CTA.EventRecurring recurEvent=cta.getEventRecurring();
		        List<String> dates = getDates(recurEvent);
		        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
		        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        cta.setDueDate(getDateWithFormat(temp, 0, true));
		        Assert.assertEquals(1, countOfRecords(cta, true, null));
		        Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
		   }
		   
	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA8")
		   public void createRecurringEventCTA_Monthly_ByWeek(HashMap<String, String> testData) throws IOException {
		         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		         SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        int temp = Integer.valueOf(cta.getDueDate());
		        cta.setDueDate(getDateWithFormat(temp, 0, false));
		        CTA.EventRecurring recurEvent=cta.getEventRecurring();
		        recurEvent.setRecurStartDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurStartDate()), 0, false));
		        recurEvent.setRecurEndDate(getDateWithFormat(Integer.valueOf(recurEvent.getRecurEndDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);

		        cta.setDueDate(getDateWithFormat(temp, 0, true));
		        Assert.assertEquals(1, countOfRecords(cta, true, null));
		        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
		   }
		   
	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA9")
		   public void createRecurringEventCTA_Yearly_ByMonth(HashMap<String, String> testData) throws IOException {
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name like '%"+cta.getCustomer().trim()+"%')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        int temp = Integer.valueOf(cta.getDueDate());
		        cta.setDueDate(getDateWithFormat(temp, 0, false));
		        CTA.EventRecurring recurEvent=cta.getEventRecurring();
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        cta.setDueDate(getDateWithFormat(temp, 0, true));
		        Assert.assertEquals(1, countOfRecords(cta, true, null));
		        //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
		   }

	        @TestInfo(testCaseIds={"GS-2083","GS-2084","GS-2087","GS-2086"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA10")
		   public void createRecurringEventCTA_Yearly_ByMonthAndWeek(HashMap<String, String> testData) throws IOException {
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        int temp = Integer.valueOf(cta.getDueDate());
		       cta.setDueDate(getDateWithFormat(temp, 0, false));
		       CTA.EventRecurring recurEvent=cta.getEventRecurring();
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);
		       cta.setDueDate(getDateWithFormat(temp, 0, true));
		       Assert.assertEquals(1, countOfRecords(cta, true, null));
		      //Assert.assertEquals(dates.size(), countOfRecords(cta, false, dates));
		   }
		   
	        @TestInfo(testCaseIds={"GS-2090","GS-2091","GS-2111"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA11")
		   public void createRiskCTAWithTasks(HashMap<String,String> testData) throws IOException{
			    CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
			    SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
			    cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());

		       	oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        }
		        
		       oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		       Assert.assertTrue(oppWfPage.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
		   }
		   
	        @TestInfo(testCaseIds={"GS-2090","GS-2091","GS-2111"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA40")
		   public void createRiskCTAWithTasks_AssignedToDifferentUsers(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());

		       	oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        }
		        
		       oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		       Assert.assertTrue(oppWfPage.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
		   }
		   
	        @TestInfo(testCaseIds={"GS-2090","GS-2091","GS-2111"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA12")
		   public void createOpportunityCTAWithTasks(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Opportunity CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        }
		        
		       oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		       Assert.assertTrue(oppWfPage.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Opportunity CTA");
		   }
		   
	        @TestInfo(testCaseIds={"GS-2090","GS-2091","GS-2111"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA13")
		   public void createEventCTAWithTasks(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        	}
		        
		       oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		       Assert.assertTrue(oppWfPage.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
		   }
	        
	       @TestInfo(testCaseIds={"GS-5554"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA14")
		   public void createRiskCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
		        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }

					task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate()))); 
					task.setFromCustomer360orWidgets(true);
		        	}
		       oppWfPage  = oppWfPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
		       for(Task task : tasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
		       }
		   }
		   
	       @TestInfo(testCaseIds={"GS-5554"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA41")
		   public void createRiskCTAWithPlaybook_DifferentAssignees(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
		        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
		       String[] users={"GiribabuG","SrividyaR","RajeshY","RamyaK","SunandP","HiteshS"};
		       int i=0;
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }

					task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate()))); 
					task.setAssignee(users[i]); if(++i >=5) i=0;
		        	task.setFromCustomer360orWidgets(true);
		        	}

		       oppWfPage  = oppWfPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
		       for(Task task : tasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
		       }
		   }
		   
	       @TestInfo(testCaseIds={"GS-5555"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA15")
		   public void createAndReplacePlaybook_RiskCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);    
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Risk CTA is created ");
		        ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }

					task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate()))); 
					task.setFromCustomer360orWidgets(true);
		        	}
		        
		        //Applying Playbook and verifying tasks
		       oppWfPage  = oppWfPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
		       for(Task task : tasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }
		       
		       //Replacing Playbook and verifying updated tasks
		       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
		       for(Task task : updatedTasks) {
		          	if(task.getAssignee()==null) {
		                task.setAssignee(sfdcInfo.getUserFullName());
		            }
				   task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate())));
				   task.setFromCustomer360orWidgets(true);
		          	}
		       cta.setDueDate(getHighestTaskDate(tasks));
		       oppWfPage = oppWfPage.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

		       for(Task task : updatedTasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }

		       for(Task task : tasks) {
		           Assert.assertFalse(oppWfPage.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }
		    }
		   
	       @TestInfo(testCaseIds={"GS-5554"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA16")
		   public void createOpportunityCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
		       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());

					task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate()))); 
					task.setFromCustomer360orWidgets(true);
		        	}

		       oppWfPage  = oppWfPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
		       for(Task task : tasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
		       }
		   }
		   
	       @TestInfo(testCaseIds={"GS-5555"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA17")
		   public void createAndReplacePlaybook_OpporCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);    
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Risk CTA is created ");
		       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());

					task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate()))); 
					task.setFromCustomer360orWidgets(true);
		        	}
		        
		        //Applying Playbook and verifying tasks
		       oppWfPage  = oppWfPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
		       for(Task task : tasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }
		       
		       //Replacing Playbook and verifying updated tasks
		       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
		       for(Task task : updatedTasks) {
		          	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
				   task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate())));
				   task.setFromCustomer360orWidgets(true);
		          	}
		       cta.setDueDate(getHighestTaskDate(tasks));
		       oppWfPage = oppWfPage.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

		       for(Task task : updatedTasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }

		       for(Task task : tasks) {
		           Assert.assertFalse(oppWfPage.isTaskDisplayedUnderCTA(cta,task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }
		    }
		   
	       @TestInfo(testCaseIds={"GS-5554"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA18")
		   public void createEventCTAWithPlaybook(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Event CTA is created ");
		       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
		       for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
				   task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate())));
				   task.setFromCustomer360orWidgets(true);
		        	}
		        
		       oppWfPage = oppWfPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
		       for(Task task : tasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Event CTA");
		       }
		   }
           
	       @TestInfo(testCaseIds={"GS-5555"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA19")
		   public void createAndReplacePlaybook_EventCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);    
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying Risk CTA is created ");
		       ArrayList<Task> tasks  = getTaskFromSFDC(testData.get("Playbook"));
		       for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
				   task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate())));
				   task.setFromCustomer360orWidgets(true);
		        	}
		        //Applying Playbook and verifying tasks
		       oppWfPage  = oppWfPage.applyPlayBook(cta, testData.get("Playbook"), tasks,true);
		       for(Task task : tasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }
		       
		       //Replacing Playbook and verifying updated tasks
		       ArrayList<Task> updatedTasks = getTaskFromSFDC(testData.get("UpdatedPlaybook"));
		       for(Task task : updatedTasks) {
		          	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
				   task.setDate(getTaskDateForPlaybook(Double.valueOf(task.getDate())));
				   task.setFromCustomer360orWidgets(true);
		          	}
		       cta.setDueDate(getHighestTaskDate(tasks));
		       oppWfPage = oppWfPage.applyPlayBook(cta, testData.get("UpdatedPlaybook"), updatedTasks,false);

		       for(Task task : updatedTasks) {
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\" "+task.getSubject()+"\" created for Risk CTA");
		       }
		    }
		   
	       @TestInfo(testCaseIds={"GS-5556"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA20")
		   public void createMilestoneForRiskCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();

		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		      oppWfPage.createMilestoneForCTA(cta);
		      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
		      System.out.println("querying for:"+milestoneQuery);
		      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		   }
		   
	       @TestInfo(testCaseIds={"GS-5556"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA21")
		   public void createMilestoneForOpportunityCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();

		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		      oppWfPage.createCTA(cta);      
		      Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		      oppWfPage.createMilestoneForCTA(cta);
		      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
		      System.out.println("querying for:"+milestoneQuery);
		      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		   }
		   
	       @TestInfo(testCaseIds={"GS-5556"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA22")
		   public void createMilestoneForEventCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();

		      cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		      cta.setAssignee(sfdcInfo.getUserFullName());
		      oppWfPage.createCTA(cta);      
		      oppWfPage.createMilestoneForCTA(cta);
		      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Created'";
		      System.out.println("querying for:"+milestoneQuery);
		      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		   }
		   
	       @TestInfo(testCaseIds={"GS-5556"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA20")
		   public void createMilestoneForRiskCTA_Resolved(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();

		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		      oppWfPage.createMilestoneForCTA(cta);
		      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
		      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		      
		      //Closing Risk CTA - to check if a RiskResolved Milestone is created.
		      oppWfPage.closeCTA(cta,false);
		      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Resolved'";
		      System.out.println("querying for:"+milestoneQuery_afterClose);
		      SObject[] milestonesAfterClose=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		     
		   }
		   
	       @TestInfo(testCaseIds={"GS-5556"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA21")
		   public void createMilestoneForOpportunityCTA_Won(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		      oppWfPage.createCTA(cta);      
		      Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		      oppWfPage.createMilestoneForCTA(cta);
		      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Identified'";
		      System.out.println("querying for:"+milestoneQuery);
		      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		      
		    //Closing Opportunity CTA - to check if a Opportunity Won Milestone is created.
		      oppWfPage.closeCTA(cta,false);
		      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Won'";
		      System.out.println("querying for:"+milestoneQuery_afterClose);
		      SObject[] milestonesAfterClose=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		   }
		   
	       @TestInfo(testCaseIds={"GS-5556"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA22")
		   public void createMilestoneForEventCTA_Completed(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();

		      cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		      cta.setAssignee(sfdcInfo.getUserFullName());
		      oppWfPage.createCTA(cta);      
		      oppWfPage.createMilestoneForCTA(cta);
		      String milestoneQuery="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Created'";
		      System.out.println("querying for:"+milestoneQuery);
		      SObject[] milestones=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestones[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		      
		    //Closing Event CTA - to check if a Event Completed Milestone is created.
		      oppWfPage.closeCTA(cta,false);
		      String milestoneQuery_afterClose="Select JBCXM__Comment__c from JBCXM__Milestone__c where JBCXM__Customer__r.JBCXM__CustomerName__c='"+cta.getCustomer()+"' and JBCXM__Milestone__r.JBCXM__SystemName__c='"+cta.getType()+" Completed'";
		      System.out.println("querying for:"+milestoneQuery_afterClose);
		      SObject[] milestonesAfterClose=sfdc.getRecords(resolveStrNameSpace(milestoneQuery));
		      System.out.println(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")));
		      Assert.assertTrue(milestonesAfterClose[0].getField(resolveStrNameSpace("JBCXM__Comment__c")).equals("Name: " + cta.getSubject() + ", Reason: " + cta.getReason()));
		   }
		   
	       @TestInfo(testCaseIds={"GS-5557"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=false)
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA23")
		   public void snoozeRiskCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	          SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
		     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setSnoozeDate(getDateWithFormat(Integer.valueOf(cta.getSnoozeDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		      oppWfPage.createCTA(cta);      
		      Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
			   SObject[] snoozeReasonId=sfdc.getRecords(resolveStrNameSpace("Select Id from JBCXM__Picklist__c where JBCXM__Category__c='Relationship Activity' and JBCXM__ShortName__c='"+cta.getSnoozeReason()+"'"));
		       oppWfPage.snoozeCTA(cta,snoozeReasonId[0].getId());
		       basepage.switchToMainWindow();
		       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
		       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
		       workflowPage = workflowPage.showSnoozeCTA();
		       Assert.assertTrue(workflowPage.isCTADisplayed(cta), "Verifying the CTA has been set under Snoozed CTAs");
		   }
		   
		   @TestInfo(testCaseIds={"GS-2100"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA24")
		   public void markCTAAsImp(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		       oppWfPage = oppWfPage.flagCTA(cta);
		       cta.setImp(true);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying the CTA has been set under Important CTAs");
		   }
		   
		   @TestInfo(testCaseIds={"GS-5558"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
		   public void createAndCloseCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
		         CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		         SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			     OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
			     cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);     
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		       oppWfPage.closeCTA(cta, false);
		       cta.setClosed(true);
		        basepage.switchToMainWindow();
		       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
		       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
		       workflowPage = workflowPage.showClosedCTA();
		       workflowPage=	workflowPage.selectGroupBy("Created Date (New)");
		       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
		   }
		   
		   @TestInfo(testCaseIds={"GS-5558"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
		   public void createAndCloseCTA_ClosedRiskStatus(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);     
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		       oppWfPage.updateCTAStatus_toClosedLost(cta);
		       cta.setClosed(true);
		       cta.setStatus("Closed Risk");
		       basepage.switchToMainWindow();
		       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
		       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
		       workflowPage = workflowPage.showClosedCTA();
		       workflowPage=	workflowPage.selectGroupBy("Created Date (New)");
		       Assert.assertTrue(workflowPage.isCTADisplayed(cta));
		   }
		   
		   @TestInfo(testCaseIds={"GS-5558"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA26")
		   public void createAndCloseCTAWithTasks(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        	task.setFromCustomer360orWidgets(true);
		        	}
		        
		        oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		    	   { 
		    	   	Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
		    	   	task.setStatus("Closed");
		    	   	task.setFromCustomer360orWidgets(true);
		    	   }
		       oppWfPage.closeCTA(cta, true);
		       cta.setClosed(true);
		       cta.setStatus("Closed Success");
		       basepage.switchToMainWindow();
		       WorkflowBasePage workflowBasePage = basepage.clickOnWorkflowTab();
		       WorkflowPage workflowPage = workflowBasePage.clickOnListView();
		       Assert.assertFalse(workflowPage.isCTADisplayed(cta));
		       workflowPage = workflowPage.showClosedCTA();
		       workflowPage=	workflowPage.selectGroupBy("Created Date (New)");
		       Assert.assertTrue(workflowPage.verifyClosedCTA(cta, true, tasks), "Verified that the CTA and all the corresponding tasks are closed");
		   }
		   
		   @TestInfo(testCaseIds={"GS-5558"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA26")
		   public void createCTA_WithTasks_AndCloseTasks(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        	task.setFromCustomer360orWidgets(true);
		        	}
		        
		        oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		    	   { 
		    	   	Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
		    	   	oppWfPage.openORCloseTask(task);
		    	   	task.setStatus("Closed");
		            Assert.assertTrue(oppWfPage.verifyTaskDetails(task), "Verified all the tasks are closed for given CTA");
		    	   }   		
		   }
		   
		   @TestInfo(testCaseIds={"GS-5558"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA25")
		   public void create_CloseAndRe_OpenCTANoOpenTasks(HashMap<String,String> testData) throws IOException{
			  CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
			  SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta));
		       oppWfPage.closeCTA(cta, false);
		       cta.setStatus("Closed Success");
		       cta.setClosed(true);
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta));
		       oppWfPage.openCTA(cta, false, null);
		       cta.setStatus("New");
		       cta.setClosed(false);
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta));
		       Assert.assertTrue(oppWfPage.verifyCTADetails(cta), "Verifying the CTA has been set under Closed CTAs");
		   }
		   @TestInfo(testCaseIds={"GS-5559"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA27")
		   public void createAndUpdateCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta); 
		      
		      CTA updatedCta=mapper.readValue(testData.get("UpdatedCTA"), CTA.class);
		      OppWidget_CockpitPage  oppWfPage_2=basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		      if(updatedCta.getAssignee()==null)
		    	  updatedCta.setAssignee(sfdcInfo.getUserFullName());
		      updatedCta.setDueDate(getDateWithFormat(Integer.valueOf(updatedCta.getDueDate()),0, false));
		      oppWfPage_2.updateCTADetails(cta, updatedCta);
		      Assert.assertTrue(oppWfPage_2.isCTADisplayed(updatedCta), "Verifying Updated CTA Values");
		   }
		   
		   @TestInfo(testCaseIds={"GS-2089"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
		   public void createAndDeleteCTA(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta); 
		      
		       oppWfPage.deleteCTA(cta);
		      Assert.assertFalse(oppWfPage.isCTADisplayed(cta), "Verifying if the CTA is delete successfully");
		      
		   }
		   
		   @TestInfo(testCaseIds={"GS-2094"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA11")
		   public void createCTAWithTasks_AndDeleteTasks(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());

		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        	task.setFromCustomer360orWidgets(true);
		        	}
		        
		        oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks){
		           Assert.assertTrue(oppWfPage.isTaskDisplayedUnderCTA(cta, task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
		           oppWfPage.deleteTask(task);
		           cta.setTaskCount(cta.getTaskCount()-1);
		           Assert.assertFalse(oppWfPage.isTaskDisplayedUnderCTA(cta, task), "Verified that the task has been deleted");
		       }
		   }
		   
		   @TestInfo(testCaseIds={"GS-2112","GS-2092"})
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA28")
		   public void createAndUpdateCTATasks(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		      
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        	task.setFromCustomer360orWidgets(true);
		        	}
		        
		        oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
		       
		       Task updatedTask=mapper.readValue(testData.get("updatedTask"),Task.class);
		       updatedTask.setAssignee(sfdcInfo.getUserFullName());
		       updatedTask.setDate(getDateWithFormat(Integer.valueOf(updatedTask.getDate()),0, false));
		       oppWfPage.updateTaskDetails(tasks.get(0), updatedTask);  //assuming that we are taking only one task for updation
		       Assert.assertTrue(oppWfPage.isTaskDisplayed(updatedTask),"Verified that the task is updated successfully");
		   }

		   /*
		   Commenting as Hitesh mentioned that this feature is failing in 4.22 & will not be supported going further.
		   @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		   @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA28")
		   public void createAndEditCTATasks(HashMap<String,String> testData) throws IOException{
		       CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		       SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
			    OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		       cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		       cta.setAssignee(sfdcInfo.getUserFullName());
		       oppWfPage.createCTA(cta);      
		       Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks  = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for(Task task : tasks) {
		        	if(task.getAssignee()==null) task.setAssignee(sfdcInfo.getUserFullName());
		        	task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()),0, false));
		        	task.setFromCustomer360orWidgets(true);
		        	}
		        
		        oppWfPage.addTaskToCTA(cta, tasks);
		       for(Task task : tasks)
		           Assert.assertTrue(oppWfPage.isTaskDisplayed(task),"Verifying the task -\""+task.getSubject()+"\" created for Risk CTA");
		       
		       Task updatedTask=mapper.readValue(testData.get("updatedTask"),Task.class);
		       updatedTask.setAssignee(sfdcInfo.getUserFullName());
		       updatedTask.setDate(getDateWithFormat(Integer.valueOf(updatedTask.getDate()),0, false));
		       oppWfPage.editTasks(cta, updatedTask,tasks.get(0));
		       Assert.assertTrue(oppWfPage.isTaskDisplayed(updatedTask),"Verified that the task is updated successfully");
		   }
		   */
            
		   @TestInfo(testCaseIds={"GS-2121"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
		    public void syncTaskToSF_Manual(HashMap<String, String> testData) throws IOException {
		    	basepage.switchToMainWindow();
		    	enableSFDCSync_Manual();
			     CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		    	 SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				 OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for (Task task : tasks) {
		            if (task.getAssignee() == null) task.setAssignee(sfdcInfo.getUserFullName());
		            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
		            task.setFromCustomer360orWidgets(true);
		        }

		        oppWfPage.addTaskToCTA(cta, tasks);
		        oppWfPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
		        
		        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        int sfTask=sfdc.getRecordCount("select id from Task where id='"+syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))+"'");
		        Assert.assertTrue(sfTask==1, "Verified that the task is created successfully in SF");
		    }
		    
		   @TestInfo(testCaseIds={"GS-2124"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
		    public void deSyncTaskFromSFButKeepTask_Manual(HashMap<String, String> testData) throws IOException {
		    	basepage.switchToMainWindow();
		    	enableSFDCSync_Manual();
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				 OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        	        
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for (Task task : tasks) {
		            if (task.getAssignee() == null) task.setAssignee(sfdcInfo.getUserFullName());
		            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
		            task.setFromCustomer360orWidgets(true);
		        }

		        oppWfPage.addTaskToCTA(cta, tasks);
		        oppWfPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
		        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
		        
		        oppWfPage.deSyncTaskFromSF(cta,tasks.get(0),true);
		        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        System.out.println("desynced taks...."+desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")));
		        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"'");
		        Assert.assertTrue((sfTask==1 &&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF..but SF task still exists");
		        Assert.assertEquals(1, sfTask);
		    }
		    
		    @TestInfo(testCaseIds={"GS-2125"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
		    public void deSyncTaskFromSFAndDeleteTask_Manual(HashMap<String, String> testData) throws IOException {
		    	basepage.switchToMainWindow();
		    	enableSFDCSync_Manual();
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				 OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        	        
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for (Task task : tasks) {
		            if (task.getAssignee() == null) task.setAssignee(sfdcInfo.getUserFullName());
		            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
		            task.setFromCustomer360orWidgets(true);
		        }

		        oppWfPage.addTaskToCTA(cta, tasks);
		        oppWfPage.syncTasksToSF(cta,tasks.get(0));  //syncing only 1 task for now...but maintaining in a array in case we need to support multiple
		        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
		        
		        oppWfPage.deSyncTaskFromSF(cta,tasks.get(0),false);
		        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));

		        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"' and isDeleted=false");
		        Assert.assertTrue(((sfTask==0)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task desynced from SF and SF task is deleted too");
		    }
		    
		    @TestInfo(testCaseIds={"GS-5561"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
		    public void syncTaskToSF_AutoSync(HashMap<String, String> testData) throws IOException {
		    	basepage.switchToMainWindow();
		    	enableSFDCSync_Auto();
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				 OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for (Task task : tasks) {
		            if (task.getAssignee() == null) task.setAssignee(sfdcInfo.getUserFullName());
		            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
		            task.setFromCustomer360orWidgets(true);
		        }

		        oppWfPage.addTaskToCTA(cta, tasks);
		        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        int sfTask=sfdc.getRecordCount("select id from Task where id='"+syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))+"'");
		        Assert.assertTrue((sfTask==1), "Verified that the task is created successfully in SF");
		        basepage.switchToMainWindow();
		        disableSFAutoSync();
		    }
		    
		    @TestInfo(testCaseIds={"GS-5562"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
		    public void deSyncTaskFromSFButKeepTask_AutoSync(HashMap<String, String> testData) throws IOException {
		    	basepage.switchToMainWindow();
		    	enableSFDCSync_Auto();
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				 OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		             
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for (Task task : tasks) {
		            if (task.getAssignee() == null) task.setAssignee(sfdcInfo.getUserFullName());
		            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
		            task.setFromCustomer360orWidgets(true);
		        }

		        oppWfPage.addTaskToCTA(cta, tasks);
		        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
		        
		        oppWfPage.deSyncTaskFromSF(cta,tasks.get(0),true);
		        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        System.out.println("desynced taks...."+desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")));
		        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"'");
		        Assert.assertTrue(((sfTask==1)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF but remains in SF");
		        basepage.switchToMainWindow();
		        disableSFAutoSync();
		    }
		    
		    @TestInfo(testCaseIds={"GS-5563"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA29")
		    public void deSyncTaskFromSFAndDeleteTask_AutoSync(HashMap<String, String> testData) throws IOException {
		    	basepage.switchToMainWindow();
		    	enableSFDCSync_Auto();
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				 OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		     
		        
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        Assert.assertTrue(oppWfPage.isCTADisplayed(cta), "Verifying risk CTA is created ");
		        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
		        for (Task task : tasks) {
		            if (task.getAssignee() == null) task.setAssignee(sfdcInfo.getUserFullName());
		            task.setDate(getDateWithFormat(Integer.valueOf(task.getDate()), 0, false));
		            task.setFromCustomer360orWidgets(true);
		        }

		        oppWfPage.addTaskToCTA(cta, tasks);
		        SObject[] syncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));
		        String taskId=syncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c")).toString();
		        
		        oppWfPage.deSyncTaskFromSF(cta,tasks.get(0),false);
		        SObject[] desyncedTasks=sfdc.getRecords(resolveStrNameSpace("SELECT JBCXM__RelatedRecordId__c FROM JBCXM__CSTask__c where JBCXM__Subject__c='"+tasks.get(0).getSubject()+"'"));

		        int sfTask=sfdc.getRecordCount("select id from Task where id='"+taskId+"' and isDeleted=false");
		        Assert.assertTrue(((sfTask==0)&&(desyncedTasks[0].getField(resolveStrNameSpace("JBCXM__RelatedRecordId__c"))==null)), "Verified that the task is desynced from SF and also deleted from SF");
		        basepage.switchToMainWindow();
				disableSFAutoSync();
		    }
		    
		    @TestInfo(testCaseIds={"GS-5560"})
		    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA30")
		    public void createRiskCTA_Overdue(HashMap<String, String> testData) throws IOException {
		        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		        SObject[] oppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='"+cta.getCustomer()+"')");
				 OppWidget_CockpitPage oppWfPage = basepage.gotoOpportunityPageWithId(oppId[0].getId()).switchToOppCSWidget().gotoCockpitSubTab();
		     
		        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
		        cta.setAssignee(sfdcInfo.getUserFullName());
		        oppWfPage.createCTA(cta);
		        cta.setOverDue(true);
		        Assert.assertTrue(oppWfPage.isOverDueCTADisplayed(cta), "Verifying risk CTA is created - which is overdue");
		    }

}
