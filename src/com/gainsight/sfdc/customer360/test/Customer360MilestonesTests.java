package com.gainsight.sfdc.customer360.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
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
	final String CURRENT_DIR = env.basedir;
	Calendar cal;
	DateFormat df=null;
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Customer 360 Milestones module Test Cases...");
		System.out
				.println("Starting Customer 360 Milestones module Test Cases...");
		apex.runApexCodeFromFile(CURRENT_DIR
				+ "/apex_scripts/Milestones/Milestones.apex",
				isPackageInstance());
		basepage.login();
		TimeZone tz=TimeZone.getTimeZone(soql.getUserTimeZone());
		cal=Calendar.getInstance(tz);			
		String Lc=soql.getUserLocale();
		if(Lc.equals("en_US"))	df=new SimpleDateFormat("M/d/yyyy");
		else if(Lc.equals("en_IN"))	df=new SimpleDateFormat("d/M/yyyy");
		else df=new SimpleDateFormat("M/d/yyyy");
		df.setTimeZone(tz);
		Report.logInfo("Your Org's TimeZone:"+soql.getUserTimeZone()+" && Locale:"+Lc);
	}
	 
	@AfterMethod
	private void refresh() {
	        basepage.refreshPage();
	    }
	 
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M1")
	public void verifyDataFromExcel(HashMap<String, String> testData) {
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Milestones Account", true);
		cm = cp.goToUsageSection();
		cm.gotoMilestonesSubtab();
		HashMap<String, String> MsHeaders = getMapFromData(testData.get("Headers"));
		// Verifying table header
		if (cm.isHeaderPresent()) {
			System.out.println("no of columns=" + MsHeaders.size());
			for (int h = 1; h <= MsHeaders.size(); h++) {
				System.out.println("Checking for---"+ MsHeaders.get("Column" + h));
				Assert.assertTrue(cm.isHeaderItemPresent(MsHeaders.get("Column"+ h)));
			}
		}

		// Verifying table data
		if (cm.isMsTableDataPresent()) {

			// Number of milestone data rows = testData-(1 row for header)
			int NumOfMilestones = testData.size()-1;
			HashMap<String, String> MsData;
			System.out.println("number of miles to go:"+NumOfMilestones);
			for (int i = 1; i <= NumOfMilestones; i++) {

				// get data for a Milestone from the excel
				MsData = getMapFromData(testData.get("M" + i));		
				int monthsToAdd=Integer.parseInt(MsData.get("Date"));
				cal.add(Calendar.MONTH,monthsToAdd);
				Date msDate=cal.getTime();
				Assert.assertTrue(cm.checkMilestoneRow(df.format(msDate), MsData.get("MilestoneColor"), MsData.get("Milestone"), MsData.get("Opportunity"),MsData.get("Comments")));
				cal.add(Calendar.MONTH, -(monthsToAdd));
			}
		}

	}

	/*@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 2)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M2")*/
	public void addMilestones(HashMap<String, String> testData) {

		int MsNum = cm.getCurrentNoOfRows()+1;
		String MsName="M"+MsNum;
		HashMap<String, String> MsList = getMapFromData(testData.get(MsName));
		int monthsToAdd=Integer.parseInt(MsList.get("Date"));
		cal.add(Calendar.MONTH,monthsToAdd);
		Date msDate=cal.getTime();
		cm.clickOnAddMilestones();
		cm.setDateInField(df.format(msDate));
		cm.selectMileStone(MsList.get("Milestone"));
		cm.selectOpportunityForMilestone(MsList.get("Opportunity"));
		cm.addComments(MsList.get("Comments"));
		cm.clickOnSave();

		// Verify if the data is added correctly
		cm.isMsTableDataPresent();
		Assert.assertTrue(cm.checkMilestoneRow(df.format(msDate), MsList.get("MilestoneColor"), MsList.get("Milestone"), MsList.get("Opportunity"),MsList.get("Comments")));
		cal.add(Calendar.MONTH,-(monthsToAdd));

	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 3)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M2")
	public void verifyAddandEditMilestones(HashMap<String, String> testData) {
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Milestones Account", true);
		cm = cp.goToUsageSection();
		cm.gotoMilestonesSubtab();
		
		addMilestones(testData);
		
		int MsNum = cm.getCurrentNoOfRows();
		String MsName="M"+MsNum;
		HashMap<String, String> MsList = getMapFromData(testData.get(MsName));
		int monthsToAdd=Integer.parseInt(MsList.get("Date"));
		cal.add(Calendar.MONTH,monthsToAdd);
		Date msDate=cal.getTime();
		//cm.clickOnAddMilestones();
		cm.clickOnEditMilestone(MsNum);
		cm.setDateInField(df.format(msDate));
		cm.selectMileStone(MsList.get("Milestone"));
		cm.selectOpportunityForMilestone(MsList.get("Opportunity"));
		cm.addComments(MsList.get("Comments"));
		cm.clickOnSave();

		// Verify if the data is edited correctly
		cm.isMsTableDataPresent();
		
		Assert.assertTrue(cm.checkMilestoneRow(df.format(msDate), MsList.get("MilestoneColor"), MsList.get("Milestone"), MsList.get("Opportunity"),MsList.get("Comments")));
		cal.add(Calendar.MONTH,-(monthsToAdd));

	}

	@Test(priority = 4)
	public void verifyDeleteMilestones() {
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Milestones Account", true);
		cm = cp.goToUsageSection();
		cm.gotoMilestonesSubtab();
		int MsNum = cm.getCurrentNoOfRows();
		cm.clickOnDeleteMilestone(MsNum);

		// Verify if the data is deleted correctly
		cm.isMsTableDataPresent();
		Assert.assertTrue(cm.isRowPresentAfterDelete(MsNum));
	}

	@Test(priority = 5)
	public void verifyNoMilestonesMessage() {
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Milestones Account", true);
		cm = cp.goToUsageSection();
		cm.gotoMilestonesSubtab();
		// Assuming there are 4 milestones left in the page
		int MsNum = cm.getCurrentNoOfRows();
		for (int i = 1; i <= MsNum; i++) {
			cm.clickOnDeleteMilestone(1);
		}
		Assert.assertTrue(cm.isNoMilestoneMessagePresent());
	}

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
}
