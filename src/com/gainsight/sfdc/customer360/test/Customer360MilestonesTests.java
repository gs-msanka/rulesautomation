package com.gainsight.sfdc.customer360.test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Milestones;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.TimeZone;

public class Customer360MilestonesTests extends BaseTest {
    private final String TEST_DATA_FILE = "testdata/sfdc/Milestones/MilestonesTests.xls";
	private final String CURRENT_DIR = env.basedir;

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Customer 360 Milestones module Test Cases...");
		basepage.login();
        isPackage = isPackageInstance();
        apex.runApexCodeFromFile(CURRENT_DIR+ "/apex_scripts/Milestones/Milestones.apex", isPackage);
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
	}
	 
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M1")
	public void verifyDataFromExcel(HashMap<String, String> testData) {
        apex.runApex(resolveStrNameSpace("DELETE [SELECT ID FROM JBCXM__Milestone__c Where JBCXM__Account__r.Name Like '"+testData.get("Account")+"'];"));
        apex.runApexCodeFromFile(CURRENT_DIR+ "/apex_scripts/Milestones/MilestonesForACustomer.apex",isPackageInstance());
        Customer360Page cp = basepage.clickOnC360Tab().searchCustomer(testData.get("Account"), false, false);
		Customer360Milestones cm = cp.goToUsageSection().gotoMilestonesSubTab();
		HashMap<String, String> MsHeaders = getMapFromData(testData.get("Headers"));
		// Verifying table header
		if (cm.isHeaderPresent()) {
			Report.logInfo("No of columns=" + MsHeaders.size());
			for (int h = 1; h <= MsHeaders.size(); h++) {
				Report.logInfo("Checking for---"+ MsHeaders.get("Column" + h));
				Assert.assertTrue(cm.isHeaderItemPresent(MsHeaders.get("Column"+ h)));
			}
		}
		// Verifying table data
		if (cm.isMsTableDataPresent()) {
			int noOfMilestones = testData.size()-2;
			HashMap<String, String> expData;
			System.out.println("No of Milestones to Verify : "+ noOfMilestones);
			for (int i = 1; i <= noOfMilestones; i++) {
				// get data for a Milestone from the excel
                expData = getMapFromData(testData.get("M" + i));
				String Date = getDateWithFormat(0, Integer.parseInt(expData.get("Date")), false);
                expData.put("Date", Date);
				Assert.assertTrue(cm.isMilestonePresent(expData), "Checking Milestone is Present/Displayed");
			}
		}
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M2")
	public void verifyAddAndEditMilestones(HashMap<String, String> testData) {
        Customer360Page cp = basepage.clickOnC360Tab().searchCustomer(testData.get("Account"), false, false);
        Customer360Milestones cm = cp.goToUsageSection().gotoMilestonesSubTab();
        HashMap<String, String> milestoneData1 = getMapFromData(testData.get("Milestone1"));
        HashMap<String, String> milestoneData2 = getMapFromData(testData.get("Milestone2"));
        milestoneData1.put("Date", getDateWithFormat(0,Integer.valueOf(milestoneData1.get("Date")), false));
        cm.addMilestone(milestoneData1);
        Assert.assertTrue(cm.isMilestonePresent(milestoneData1), "Checking for Milestone present 1.");
        milestoneData2.put("Date", getDateWithFormat(0,Integer.valueOf(milestoneData2.get("Date")), false));
        cm.editMileStone(milestoneData1, milestoneData2);
        Assert.assertTrue(cm.isMilestonePresent(milestoneData2), "Checking for Milestone present 2.");
	}

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M3")
    public void verifyAddMilestone(HashMap<String, String> testData) {
        Customer360Page cp = basepage.clickOnC360Tab().searchCustomer(testData.get("Account"), false, false);
        Customer360Milestones cm = cp.goToUsageSection().gotoMilestonesSubTab();
        HashMap<String, String> milestoneData = getMapFromData(testData.get("Milestone"));
        milestoneData.put("Date", getDateWithFormat(0,Integer.valueOf(milestoneData.get("Date")), false));
        cm.addMilestone(milestoneData);
        Assert.assertTrue(cm.isMilestonePresent(milestoneData), "Checking for is added successfully.");
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M4")
    public void verifyDeleteMilestone(HashMap<String, String> testData) {
        Customer360Page cp = basepage.clickOnC360Tab().searchCustomer(testData.get("Account"), false, false);
        Customer360Milestones cm = cp.goToUsageSection().gotoMilestonesSubTab();
        HashMap<String, String> milestoneData = getMapFromData(testData.get("Milestone"));
        milestoneData.put("Date", getDateWithFormat(0,Integer.valueOf(milestoneData.get("Date")), false));
        cm.addMilestone(milestoneData);
        Assert.assertTrue(cm.isMilestonePresent(milestoneData), "Checking for is added successfully.");
        cm.deleteMilestone(milestoneData);
        Assert.assertFalse(cm.isMilestonePresent(milestoneData), "Checking if milestone is deleted successfully");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M5")
    public void verifyNoMilestonesMessage(HashMap<String, String> testData) {
        apex.runApex(resolveStrNameSpace("DELETE [SELECT ID FROM JBCXM__Milestone__c Where JBCXM__Account__r.Name Like '"+testData.get("Account")+"'];"));
        Customer360Page cp = basepage.clickOnC360Tab().searchCustomer(testData.get("Account"), false, false);
        Customer360Milestones cm = cp.goToUsageSection().gotoMilestonesSubTab();
        HashMap<String, String> milestoneData = getMapFromData(testData.get("Milestone"));
        milestoneData.put("Date", getDateWithFormat(0, Integer.valueOf(milestoneData.get("Date")), false));
        cm.addMilestone(milestoneData);
        Assert.assertTrue(cm.isMilestonePresent(milestoneData), "Checking is milestone displayed");
        cm.deleteMilestone(milestoneData);
        Assert.assertFalse(cm.isMilestonePresent(milestoneData), "Checking is milestone displayed");
		Assert.assertTrue(cm.isNoMilestoneMessagePresent(), "Checking if no milestones message displayed");
	}

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
}
