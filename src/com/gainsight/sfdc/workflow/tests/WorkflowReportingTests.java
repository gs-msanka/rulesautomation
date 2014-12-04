package com.gainsight.sfdc.workflow.tests;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkFlowReportingPage;

public class WorkflowReportingTests extends BaseTest {
	private final String LEADERBOARD_DATAGEN_SCRIPT = TestEnvironment.basedir
			+ "/testdata/sfdc/workflow/scripts/CreateCTAs_ForLeaderBoard.txt";
	private final String CREATE_USERS_SCRIPT = TestEnvironment.basedir
			+ "/testdata/sfdc/workflow/scripts/CreateUsers.txt";
	private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c];"
			+ "Delete [select id from JBCXM__CSTask__c];"
			+ "Delete [select id from Task];"
			+ "Delete [Select id from JBCXM__StatePreservation__c];"
			+ "Delete [Select id from JBCXM__Milestone__c];";
	public String[] users={"GiribabuG","SrividyaR","HiteshS"};
	@BeforeClass
	public void setup() {
		basepage.login();
		isPackage = isPackageInstance();
		apex.runApexCodeFromFile(CREATE_USERS_SCRIPT, isPackage);
		apex.runApexCodeFromFile(LEADERBOARD_DATAGEN_SCRIPT, isPackage);
	}

	@Test
	public void reportForLast7Days() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		workflowPage.selectLast7Days();
		//For all the users in the test data - assert the counts
		
		for(String user : users)
		{
			/*Assert.assertEquals(getCountOfUserCTAs(user, "Risk", false, false,"7","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", false, false,"7","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", false, false,"7","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Opportunity"));*/
			
			Assert.assertEquals(getCountOfUserCTAs(user, "Risk", true, false,"7","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", true, false,"7","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", true, false,"7","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
		}
	}
	
	@Test
	public void reportForLast30Days() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		workflowPage.selectLast30Days();
		//For all the users in the test data - assert the counts
		
		for(String user : users)
		{
			/*Assert.assertEquals(getCountOfUserCTAs(user, "Risk", false, false,"30","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", false, false,"30","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", false, false,"30","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Opportunity"));*/
			
			Assert.assertEquals(getCountOfUserCTAs(user, "Risk", true, false,"30","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", true, false,"30","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", true, false,"30","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
		}
	}
	
	@Test
	public void reportForCurrentMonth() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		workflowPage.selectCurrentMonth();
		//For all the users in the test data - assert the counts
		
		for(String user : users)
		{
			/*Assert.assertEquals(getCountOfUserCTAs(user, "Risk", false, false,"CurrentMonth","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", false, false,"CurrentMonth","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", false, false,"CurrentMonth","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Opportunity"));*/
			
			Assert.assertEquals(getCountOfUserCTAs(user, "Risk", true, false,"CurrentMonth","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", true, false,"CurrentMonth","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", true, false,"CurrentMonth","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
		}
	}
	
	@Test
	public void reportForLastMonth() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		workflowPage.selectLastMonth();
		//For all the users in the test data - assert the counts
		
		for(String user : users)
		{
			/*Assert.assertEquals(getCountOfUserCTAs(user, "Risk", false, false,"LastMonth","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", false, false,"LastMonth","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", false, false,"LastMonth","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Opportunity"));*/
			
			Assert.assertEquals(getCountOfUserCTAs(user, "Risk", true, false,"LastMonth","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", true, false,"LastMonth","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", true, false,"LastMonth","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
		}
	}
	
	@Test
	public void reportForCurrentQuarter() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		workflowPage.selectCurrentQuarter();
		//For all the users in the test data - assert the counts
		
		for(String user : users)
		{
			/*Assert.assertEquals(getCountOfUserCTAs(user, "Risk", false, false,"CurrentQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", false, false,"CurrentQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", false, false,"CurrentQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Opportunity"));
			*/
			Assert.assertEquals(getCountOfUserCTAs(user, "Risk", true, false,"CurrentQuarter","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", true, false,"CurrentQuarter","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", true, false,"CurrentQuarter","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
		}
	}
	
	@Test
	public void reportForLastQuarter() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		workflowPage.selectLastQuarter();
		//For all the users in the test data - assert the counts
		
		for(String user : users)
		{
			/*Assert.assertEquals(getCountOfUserCTAs(user, "Risk", false, false,"LastQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", false, false,"LastQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", false, false,"LastQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Opportunity"));*/
			
			Assert.assertEquals(getCountOfUserCTAs(user, "Risk", true, false,"LastQuarter","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", true, false,"LastQuarter","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", true, false,"LastQuarter","0"),	workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
		}
	}
	
	@Test
	public void reportForCustomDateRange() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		workflowPage.selectCustomDate(getDateWithFormat(-120,0,false),getDateWithFormat(-90, 0,false));
		//For all the users in the test data - assert the counts
		
		for(String user : users)
		{
			/*Assert.assertEquals(getCountOfUserCTAs(user, "Risk", false, false,"120","90"),	workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", false, false,"LastQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", false, false,"LastQuarter","0"),	workflowPage.getCountOfUserClosedCTAs(user, "Opportunity"));*/
			
			Assert.assertEquals(getCountOfUserCTAs(user, "Risk", true, false,"120","90"),	workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Event", true, false,"120","90"),	workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			Assert.assertEquals(getCountOfUserCTAs(user, "Opportunity", true, false,"120","90"),	workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
		}
	}
	public int getCountOfUserCTAs(String assignee, String type, boolean isOpen,
			boolean isForCustomers,String startDays,String endDays) {//In case of Custom date use both startDays and endDays...else give only startDays and endDays as 0
		int count;
		String query = "Select id From JBCXM__CTA__C "
				+ " where isDeleted = false AND JBCXM__Type__r.Name='" + type
				+ "' AND JBCXM__Assignee__r.name='" + assignee + "' ";
		if (!isForCustomers) {
			if (isOpen) {
				query = query
						+ " AND JBCXM__Stage__r.JBCXM__IncludeInWidget__c = true ";
			} else {
				query = query
						+ " AND JBCXM__Stage__r.JBCXM__IncludeInWidget__c = false ";
			}
		}
		if(endDays.equals("0")){
			switch(startDays) { 
			case "7":   query=query+" and JBCXM__DueDate__c = LAST_WEEK"; break;
			case "30" : query=query+" and JBCXM__DueDate__c = LAST_N_DAYS:30"; break;
			case "CurrentMonth" : query=query+" and JBCXM__DueDate__c = THIS_MONTH"; break;
			case "LastMonth"		: query=query+" and JBCXM__DueDate__c = LAST_MONTH"; break;
			case "CurrentQuarter"		: query=query+" and JBCXM__DueDate__c = THIS_QUARTER"; break;
			case "LastQuarter"		: query=query+" and JBCXM__DueDate__c = LAST_QUARTER"; break;
			}
		}
			else{
				query=query+" and JBCXM__DueDate__c =  LAST_N_DAYS:"+startDays+" and JBCXM__DueDate < LAST_N_DAYS:"+endDays;
			}
		Report.logInfo("Query : " + resolveStrNameSpace(query));
		count = soql.getRecordCount(resolveStrNameSpace(query));
		return count;
	}

}
