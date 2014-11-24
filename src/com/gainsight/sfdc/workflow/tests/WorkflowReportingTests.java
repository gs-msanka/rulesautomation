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

	@BeforeClass
	public void setup() {
		basepage.login();
		isPackage = isPackageInstance();
		apex.runApexCodeFromFile(CREATE_USERS_SCRIPT, isPackage);
		apex.runApexCodeFromFile(LEADERBOARD_DATAGEN_SCRIPT, isPackage);
	}

	@BeforeMethod
	public void clearCTAs() {
		apex.runApex(resolveStrNameSpace(CLEANUP_SCRIPT));
	}

	@Test
	public void reportSampleTest() throws IOException {
		WorkFlowReportingPage workflowPage = basepage.clickOnWorkflowTab()
				.clickOnReportingView();
		Assert.assertEquals(
				getCountOfUserCTAs("Giribabu Golla", "Risk", false, false),
				workflowPage.getCountOfUserClosedCTAs("Giribabu Golla", "Risk"));
	}

	public int getCountOfUserCTAs(String assignee, String type, boolean isOpen,
			boolean isForCustomers) {
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
		Report.logInfo("Query : " + resolveStrNameSpace(query));
		count = soql.getRecordCount(resolveStrNameSpace(query));
		return count;
	}

}
