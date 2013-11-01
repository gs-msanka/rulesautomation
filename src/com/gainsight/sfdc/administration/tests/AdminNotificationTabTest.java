package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminCustomersTab;
import com.gainsight.sfdc.administration.pages.AdminNotificationsSubTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminNotificationTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}
	
	@Test(priority=1)                                   //set Notification Frequency
	public void testsetNotificationFrequency() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminNotificationsTab");
		setNotificationFrequency(testData.get("setNotificationFrequency"));	
	}
	private AdminNotificationsSubTab setNotificationFrequency(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String day = data.get("day");
		String hour = data.get("hour");
		String Minutes =data.get("Minutes");
		AdminNotificationsSubTab adNotiSubTab = basepage.clickOnAdminTab().ClickOnNotificationSubTab();
		adNotiSubTab.setNotificationFrequency(day,hour,Minutes); 
		return adNotiSubTab;
	}
	
	@Test(priority=2)                               // set AutoSubscription    
	public void testsetAutoSubscription() throws BiffException, IOException {
		AdminNotificationsSubTab adNotiSubTab = basepage.clickOnAdminTab()
				.ClickOnNotificationSubTab();
		adNotiSubTab.setAutoSubscription();
		}
		
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
