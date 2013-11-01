package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import jxl.read.biff.BiffException;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminAdoptionSubTab;
import com.gainsight.sfdc.administration.pages.AdminCustomersTab;
import com.gainsight.sfdc.administration.pages.AdminTransactionsTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminCustomersTabTest extends BaseTest {
	
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}
	
	@Test(priority=1)  //Add Stage
	public void testAdminAddCustomerStage() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminCustomersTab");
		addNewStage(testData.get("CreateNewStage"));
		
	}
	private AdminCustomersTab addNewStage(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname =data.get("systemname");
		String shortname = data.get("shortname");
		String active = data.get("active");
		AdminCustomersTab adCustPage = basepage.clickOnAdminTab().clickOnCustomersSubTab();
		adCustPage.addNewStage(Name,displayorder,systemname,shortname, active); 
			Assert.assertTrue(adCustPage.isStagePresent(Name),
					"Verifying Stage is added to the grid");
		return adCustPage;
	}
	
	@Test(priority=2)                                           // Edit  Stage      
	public void testAdminEditStageTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminCustomersTab");
		editStage(testData.get("EditStage"));
	}

	private AdminCustomersTab editStage(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String dummy = data.get("previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		AdminCustomersTab adCustPage = basepage.clickOnAdminTab()
				.clickOnCustomersSubTab();
		adCustPage.editStage(dummy, Name, displayorder, shortname);
		return adCustPage;
	}
	
	@Test(priority=3)                                                // Delete Stage
	public void testAdminDeleteStageTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminCustomersTab");
		deleteStage(testData.get("DeleteStage"));
	}
	private AdminCustomersTab deleteStage(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		AdminCustomersTab adCustPage = basepage.clickOnAdminTab()
				.clickOnCustomersSubTab();
		adCustPage.deleteStage(previous);
		return adCustPage;
	}
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
