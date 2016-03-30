 	package com.gainsight.sfdc.workflow.tests;

import java.io.IOException;

import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.workflow.pages.WorkFlowReportingPage;

import static com.gainsight.testdriver.Application.basedir;

	public class WorkflowReportingTests extends BaseTest {
	private final String LEADERBOARD_DATAGEN_SCRIPT = basedir
			+ "/testdata/sfdc/workflow/scripts/CreateCTAs_ForLeaderBoard.txt";
	private final String CREATE_USERS_SCRIPT = basedir
			+ "/testdata/sfdc/workflow/scripts/CreateUsers.txt";
	private final String CREATE_ACCS = basedir
			+ "/testdata/sfdc/workflow/scripts/Create_Accounts_Customers_For_CTA.txt";
	private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c];"
			+ "Delete [select id from JBCXM__CSTask__c];"
			+ "Delete [select id from Task];"
			+ "Delete [Select id from JBCXM__StatePreservation__c];"
			+ "Delete [Select id from JBCXM__Milestone__c];";
	public String[] users = {"GiribabuG", "SrividyaR"};

	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
		DataETL dataLoader = new DataETL();
		ObjectMapper mapper = new ObjectMapper();
		metaUtil.createExtIdFieldOnAccount(sfdc);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
	    metaUtil.createExtIdFieldOnUser(sfdc);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_USERS_SCRIPT));
		sfdc.runApexCode(resolveStrNameSpace(CLEANUP_SCRIPT));
		metaUtil.createExternalIdFieldOnCTA(sfdc);
		JobInfo loadCTAs = mapper.readValue(resolveNameSpace(basedir
				+ "/testdata/sfdc/workflow/jobs/Job_leaderboard_DataLoad.txt"),
				JobInfo.class);
		dataLoader.execute(loadCTAs);
		JobInfo loadCSTasks = mapper
				.readValue(
						resolveNameSpace(basedir
								+ "/testdata/sfdc/workflow/jobs/Job_leaderboard_DataLoad_Tasks.txt"),
						JobInfo.class);
		dataLoader.execute(loadCSTasks);

	}

	@TestInfo(testCaseIds = {"GS-3399"})
	@Test()
	public void reportForLast7Days() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectLast7Days();
		// For all the users in the test data - assert the counts

		for (String user : users) {
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false, "7",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
								"7", "0", false, false, false), workflowPage
								.getCountOfUserClosedCTAs(user, "Risk"));

			if (getCountOfUserCTAs_OR_Tasks(user, "Event", false, false, "7",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", false,
								false, "7", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Event"));

			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false, false,
					"7", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
								false, "7", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user,
								"Opportunity"));

			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false, "7",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
								"7", "0", false, false, false), workflowPage
								.getCountOfUserOpenCTAs(user, "Risk"));

			if (getCountOfUserCTAs_OR_Tasks(user, "Event", true, false, "7",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
								"7", "0", false, false, false), workflowPage
								.getCountOfUserOpenCTAs(user, "Event"));

			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true, false,
					"7", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
								false, "7", "0", false, false, false),
						workflowPage
								.getCountOfUserOpenCTAs(user, "Opportunity"));

			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "7", "0",
					true, true, false) != 0)

				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"7", "0", true, true, false), workflowPage
								.getCountOfUserTasks(user, false, true));

			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "7", "0",
					true, false, true) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"7", "0", true, false, true), workflowPage
								.getCountOfUserTasks(user, true, false));

			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "7", "0",
					true, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"7", "0", true, false, false), workflowPage
								.getCountOfUserTasks(user, false, false));
		}
	}

	@TestInfo(testCaseIds = {"GS-3399"})
	@Test() 
	public void reportForLast30Days() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectLast30Days();
		// For all the users in the test data - assert the counts

		for (String user : users) {
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false, "30",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
								"30", "0", false, false, false), workflowPage
								.getCountOfUserClosedCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", false, false, "30",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", false,
								false, "30", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false, false,
					"30", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
								false, "30", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user,
								"Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false, "30",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
								"30", "0", false, false, false), workflowPage
								.getCountOfUserOpenCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", true, false, "30",
					"0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
								"30", "0", false, false, false), workflowPage
								.getCountOfUserOpenCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true, false,
					"30", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
								false, "30", "0", false, false, false),
						workflowPage
								.getCountOfUserOpenCTAs(user, "Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "30", "0",
					true, true, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"30", "0", true, true, false), workflowPage
								.getCountOfUserTasks(user, false, true));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "30", "0",
					true, false, true) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"30", "0", true, false, true), workflowPage
								.getCountOfUserTasks(user, true, false));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "30", "0",
					true, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"30", "0", true, false, false), workflowPage
								.getCountOfUserTasks(user, false, false));
		}
	}

	@TestInfo(testCaseIds = {"GS-3399"}) 
	@Test()
	public void reportForCurrentMonth() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectCurrentMonth();
		// For all the users in the test data - assert the counts
    
		for (String user : users) {
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
					"CurrentMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
								"CurrentMonth", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", false, false,
					"CurrentMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", false,
								false, "CurrentMonth", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false, false,
					"CurrentMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
								false, "CurrentMonth", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user,
								"Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
					"CurrentMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
								"CurrentMonth", "0", false, false, false),
						workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
					"CurrentMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
								"CurrentMonth", "0", false, false, false),
						workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true, false,
					"CurrentMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
								false, "CurrentMonth", "0", false, false, false),
						workflowPage
								.getCountOfUserOpenCTAs(user, "Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"CurrentMonth", "0", true, true, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"CurrentMonth", "0", true, true, false),
						workflowPage.getCountOfUserTasks(user, false, true));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"CurrentMonth", "0", true, false, true) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"CurrentMonth", "0", true, false, true),
						workflowPage.getCountOfUserTasks(user, true, false));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"CurrentMonth", "0", true, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"CurrentMonth", "0", true, false, false),
						workflowPage.getCountOfUserTasks(user, false, false));
		}
	}

	@TestInfo(testCaseIds = {"GS-3399"}) 
	@Test()
	public void reportForLastMonth() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectLastMonth();
		// For all the users in the test data - assert the counts

		for (String user : users) {
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
					"LastMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
								"LastMonth", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", false, false,
					"LastMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", false,
								false, "LastMonth", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false, false,
					"LastMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
								false, "LastMonth", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user,
								"Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
					"LastMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
								"LastMonth", "0", false, false, false),
						workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
					"LastMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
								"LastMonth", "0", false, false, false),
						workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true, false,
					"LastMonth", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
								false, "LastMonth", "0", false, false, false),
						workflowPage
								.getCountOfUserOpenCTAs(user, "Opportunity"));

			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"LastMonth", "0", true, true, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"LastMonth", "0", true, true, false),
						workflowPage.getCountOfUserTasks(user, false, true));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"LastMonth", "0", true, false, true) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"LastMonth", "0", true, false, true),
						workflowPage.getCountOfUserTasks(user, true, false));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"LastMonth", "0", true, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"LastMonth", "0", true, false, false),
						workflowPage.getCountOfUserTasks(user, false, false));
		}
	}

	@TestInfo(testCaseIds = {"GS-3399"})
	@Test()
	public void reportForCurrentQuarter() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectCurrentQuarter();
		// For all the users in the test data - assert the counts

		for (String user : users) {
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
					"CurrentQuarter", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
								"CurrentQuarter", "0", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", false, false,
					"CurrentQuarter", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", false,
								false, "CurrentQuarter", "0", false, false,
								false), workflowPage.getCountOfUserClosedCTAs(
								user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false, false,
					"CurrentQuarter", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
								false, "CurrentQuarter", "0", false, false,
								false), workflowPage.getCountOfUserClosedCTAs(
								user, "Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
					"CurrentQuarter", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
								"CurrentQuarter", "0", false, false, false),
						workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
					"CurrentQuarter", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
								"CurrentQuarter", "0", false, false, false),
						workflowPage.getCountOfUserOpenCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true, false,
					"CurrentQuarter", "0", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
								false, "CurrentQuarter", "0", false, false,
								false), workflowPage.getCountOfUserOpenCTAs(
								user, "Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"CurrentQuarter", "0", true, true, false) != 0)

				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"CurrentQuarter", "0", true, true, false),
						workflowPage.getCountOfUserTasks(user, false, true));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"CurrentQuarter", "0", true, false, true) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"CurrentQuarter", "0", true, false, true),
						workflowPage.getCountOfUserTasks(user, true, false));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"CurrentQuarter", "0", true, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"CurrentQuarter", "0", true, false, false),
						workflowPage.getCountOfUserTasks(user, false, false));
		}
	}

	@TestInfo(testCaseIds = {"GS-3399"})
	@Test()
	public void reportForLastQuarter() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectLastQuarter();
		// For all the users in the test data - assert the counts

		for (String user : users) {
			if(getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
					"LastQuarter", "0", false, false, false)!=0)
			Assert.assertEquals(
					getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
							"LastQuarter", "0", false, false, false),
					workflowPage.getCountOfUserClosedCTAs(user, "Risk"));
			if(getCountOfUserCTAs_OR_Tasks(user, "Event", false, false,
					"LastQuarter", "0", false, false, false)!=0)
			Assert.assertEquals(
					getCountOfUserCTAs_OR_Tasks(user, "Event", false, false,
							"LastQuarter", "0", false, false, false),
					workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			if(getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
					false, "LastQuarter", "0", false, false, false)!=0)
			Assert.assertEquals(
					getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
							false, "LastQuarter", "0", false, false, false),
					workflowPage.getCountOfUserClosedCTAs(user, "Opportunity")); 
			
            if(getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
					"LastQuarter", "0", false, false, false)!=0)
			Assert.assertEquals(
					getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
							"LastQuarter", "0", false, false, false),
					workflowPage.getCountOfUserOpenCTAs(user, "Risk"));
            if(getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
							"LastQuarter", "0", false, false, false)!=0)
			Assert.assertEquals(
					getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
							"LastQuarter", "0", false, false, false),
					workflowPage.getCountOfUserOpenCTAs(user, "Event"));
            if(getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
					false, "LastQuarter", "0", false, false, false)!=0)
			Assert.assertEquals(
					getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
							false, "LastQuarter", "0", false, false, false),
					workflowPage.getCountOfUserOpenCTAs(user, "Opportunity"));
			
            if(getCountOfUserCTAs_OR_Tasks(user, "", false, false,
					"LastQuarter", "0", true, true, false)!=0)
			Assert.assertEquals(
					getCountOfUserCTAs_OR_Tasks(user, "", false, false,
							"LastQuarter", "0", true, true, false),
					workflowPage.getCountOfUserTasks(user, false, true));
		}
	}

	@TestInfo(testCaseIds = {"GS-3400"})
	@Test()
	public void reportForCustomDateRange() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectCustomDate(getDateWithFormat(-30, 0, false),
				getDateWithFormat(-1, 0, false));
		// For all the users in the test data - assert the counts

		for (String user : users) {
			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false, "30",
					"1", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", false, false,
								"30", "1", false, false, false), workflowPage
								.getCountOfUserClosedCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", false, false, "30",
					"1", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", false,
								false, "30", "1", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false, false,
					"30", "1", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", false,
								false, "30", "1", false, false, false),
						workflowPage.getCountOfUserClosedCTAs(user,
								"Opportunity"));

			if (getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false, "30",
					"1", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Risk", true, false,
								"30", "1", false, false, false), workflowPage
								.getCountOfUserOpenCTAs(user, "Risk"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Event", true, false, "30",
					"1", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Event", true, false,
								"30", "1", false, false, false), workflowPage
								.getCountOfUserOpenCTAs(user, "Event"));
			if (getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true, false,
					"30", "1", false, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "Opportunity", true,
								false, "30", "1", false, false, false),
						workflowPage
								.getCountOfUserOpenCTAs(user, "Opportunity"));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "30", "1",
					true, true, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"30", "1", true, true, false), workflowPage
								.getCountOfUserTasks(user, false, true));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "30", "1",
					true, false, true) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"30", "1", true, false, true), workflowPage
								.getCountOfUserTasks(user, true, false));
			if (getCountOfUserCTAs_OR_Tasks(user, "", false, false, "30", "1",
					true, false, false) != 0)
				Assert.assertEquals(
						getCountOfUserCTAs_OR_Tasks(user, "", false, false,
								"30", "1", true, false, false), workflowPage
								.getCountOfUserTasks(user, false, false));
		}
	}

	// Case where there are no CTAs at all....should check for No CTAs found
	// message
	@TestInfo(testCaseIds = {"GS-3400"})
	@Test() 
	public void reportForCustomDateRange_NoData() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView().clickOnLeaderBoard();
		workflowPage.selectCustomDate(getDateWithFormat(-300, 0, false),
				getDateWithFormat(-280, 0, false));
		Assert.assertTrue(workflowPage.checkforNoDataMessage(),
				"No data found message found!");
	}

	public int getCountOfUserCTAs_OR_Tasks(String assignee, String type,
			boolean isOpen, boolean isForCustomers, String startDays,
			String endDays, boolean needTaskCount, boolean needTotalTaskCount,
			boolean needOpenTaskCount) {// In case of Custom date use both
										// startDays and endDays...else give
										// only startDays and endDays as 0
		int count;
		String query = "";
		if (needTaskCount) {
			query = query
					+ "Select id from JBCXM__CSTask__c where JBCXM__CTA__c in ( Select id From JBCXM__CTA__C "
					+ " where isDeleted = false  AND JBCXM__Assignee__r.name='"
					+ assignee + "'";
		} else
			query = query + "Select id From JBCXM__CTA__C "
					+ " where isDeleted = false AND JBCXM__Type__r.Name='"
					+ type + "' AND JBCXM__Assignee__r.name='" + assignee
					+ "' ";

		if (!isForCustomers && !needTaskCount) {
			if (isOpen) {
				query = query
						+ " AND JBCXM__Stage__r.JBCXM__IncludeInWidget__c = true ";
			} else {
				query = query
						+ " AND JBCXM__Stage__r.JBCXM__IncludeInWidget__c = false ";
			}
		}
		if (endDays.equals("0")) {
			switch (startDays) {
			case "7":
				query = query + " and JBCXM__CreatedDate__c = LAST_N_DAYS:7";
				break;
			case "30":
				query = query + " and JBCXM__CreatedDate__c = LAST_N_DAYS:30";
				break;
			case "CurrentMonth":
				query = query + " and JBCXM__CreatedDate__c = THIS_MONTH";
				break;
			case "LastMonth":
				query = query + " and JBCXM__CreatedDate__c = LAST_MONTH";
				break;
			case "CurrentQuarter":
				query = query + " and JBCXM__CreatedDate__c = THIS_QUARTER";
				break;
			case "LastQuarter":
				query = query + " and JBCXM__CreatedDate__c = LAST_QUARTER";
				break;
			}
		} else {
			query = query + " and JBCXM__CreatedDate__c =  LAST_N_DAYS:"
					+ startDays + " and JBCXM__CreatedDate__c < LAST_N_DAYS:"
					+ endDays;
		}
		if (needTaskCount) {
			if (needTotalTaskCount)
				query = query + ")";
			else {
				if (needOpenTaskCount)
					query = query + " ) and JBCXM__Status__c='Open'";
				else
					query = query + " ) and JBCXM__Status__c='Closed'";
			}
		}
		Log.info("Query : " + resolveStrNameSpace(query));
		count = sfdc.getRecordCount(resolveStrNameSpace(query));
		Log.info("total is " +count);
		return count;
	}

}
