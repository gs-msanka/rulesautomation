package com.gainsight.sfdc.customer360.test;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Milestones;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class Customer360MilestonesTests extends BaseTest {
	Customer360Page cp;
	Customer360Milestones cm;
	final String TEST_DATA_FILE = "testdata/sfdc/Milestones/MilestonesTests.xls";

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Customer 360 Milestones module Test Cases...");
		System.out
				.println("Starting Customer 360 Milestones module Test Cases...");
		apex.runApexCodeFromFile( System.getProperty("user.dir")+"apex_scripts/Milestones/Milestones.apex",
				isPackageInstance());
		basepage.login();
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Via Systems", true);
		cm = (Customer360Milestones) cp.goToSection("Usage");
		cm.gotoMilestonesSubtab();
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M1")
	public void verifyDataFromExcel(HashMap<String, String> testData) {
		
		HashMap<String, String> MsHeaders = getMapFromData(testData
				.get("Headers"));

		// Verifying table header

		if (cm.isHeaderPresent()) {
			System.out.println("no of columns=" + MsHeaders.size());
			for (int h = 1; h <= MsHeaders.size(); h++) {
				System.out.println("Checking for---"
						+ MsHeaders.get("Column" + h));
				Assert.assertTrue(cm.isHeaderItemPresent(MsHeaders.get("Column" + h)));
			}
		}

		// Verifying table data
		if (cm.isMsTableDataPresent()) {

			// Hardcoding (in the excel itself) the num of Milestones to
			// 4...need to see how to get the no of rows from excel
			int NumOfMilestones = Integer.parseInt(getMapFromData(
					testData.get("numberOfRows")).get("Number"));
			HashMap<String, String> MsData;
			for (int i = 1; i <= NumOfMilestones; i++) {

				// get data for a Milestone from the excel
				MsData = getMapFromData(testData.get("M" + i));

				// check if all the data is present as per the test input
				Assert.assertTrue(cm.checkMilestoneDate(MsData.get("Date"), i));
				Assert.assertTrue(cm.checkMilestoneColor(MsData.get("MilestoneColor"), i));
				Assert.assertTrue(cm.checkMilestoneName(MsData.get("Milestone"), i));
				Assert.assertTrue(cm.checkMilestoneOpportunity(MsData.get("Opportunity"), i));
				Assert.assertTrue(cm.checkMilestoneComments(MsData.get("Comments"), i));
				
			}
		}

	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority=2)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M2")
	public void verifyAddMilestones(HashMap<String, String> testData) {

		String MsName = "M5";
		int MsNum = 5;
		HashMap<String, String> MsList = getMapFromData(testData.get(MsName));
		cm.clickOnAddMilestones();
		cm.setDateInField(MsList.get("Date"));
		cm.selectMileStone(MsList.get("Milestone"));
		cm.selectOpportunityForMilestone(MsList.get("Opportunity"));
		cm.addComments(MsList.get("Comments"));
		cm.clickOnSave();

		// Verify if the data is added correctly
		cm.isMsTableDataPresent();

		Assert.assertTrue(cm.checkMilestoneDate(MsList.get("Date"), MsNum));
		Assert.assertTrue(cm.checkMilestoneColor(MsList.get("MilestoneColor"), MsNum));
		Assert.assertTrue(cm.checkMilestoneName(MsList.get("Milestone"), MsNum));
		Assert.assertTrue(cm.checkMilestoneOpportunity(MsList.get("Opportunity"), MsNum));
		Assert.assertTrue(cm.checkMilestoneComments(MsList.get("Comments"), MsNum));

	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" ,priority=3)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M2")
	public void verifyEditMilestones(HashMap<String, String> testData) {
		String MsName = "M6";
		int MsNum = 5;
		HashMap<String, String> MsList = getMapFromData(testData.get(MsName));
		cm.clickOnEditMilestone(MsNum);
		cm.setDateInField(MsList.get("Date"));
		cm.selectMileStone(MsList.get("Milestone"));
		cm.selectOpportunityForMilestone(MsList.get("Opportunity"));
		cm.addComments(MsList.get("Comments"));
		cm.clickOnSave();

		// Verify if the data is edited correctly
		cm.isMsTableDataPresent();

		Assert.assertTrue(cm.checkMilestoneDate(MsList.get("Date"), MsNum));
		Assert.assertTrue(cm.checkMilestoneColor(MsList.get("MilestoneColor"), MsNum));
		Assert.assertTrue(cm.checkMilestoneName(MsList.get("Milestone"), MsNum));
		Assert.assertTrue(cm.checkMilestoneOpportunity(MsList.get("Opportunity"), MsNum));
		Assert.assertTrue(cm.checkMilestoneComments(MsList.get("Comments"), MsNum));

	}

	@Test(priority = 4)
	public void verifyDeleteMilestones() {
		int MsNum = 5;
		cm.clickOnDeleteMilestone(MsNum);

		// Verify if the data is deleted correctly
		cm.isMsTableDataPresent();
		Assert.assertTrue(cm.isRowPresentAfterDelete(MsNum));
	}
	
	
	@Test(priority = 5)
	public void verifyNoMilestonesMessage(){
		//Assuming there are 4 milestones left in the page
		int MsNum=4;
		for(int i =1;i<= MsNum;i++)
		{
			cm.clickOnDeleteMilestone(1);
		}
		Assert.assertTrue(cm.isNoMilestoneMessagePresent());
	}
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
}
