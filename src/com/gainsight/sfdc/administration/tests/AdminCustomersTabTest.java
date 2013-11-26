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
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName =data.get("systemName");
		String shortName = data.get("shortName");
		String active = data.get("active");
		AdminCustomersTab adCustPage = basepage.clickOnAdminTab().clickOnCustomersSubTab();
		adCustPage.addNewStage(name,displayOrder,systemName,shortName, active); 
		String custStage = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
			Assert.assertTrue(adCustPage.isStagePresent(custStage),
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
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String shortName = data.get("shortName");
		AdminCustomersTab adCustPage = basepage.clickOnAdminTab().clickOnCustomersSubTab();
		adCustPage.editStage(previous, name, displayOrder, shortName);
		String edtCustStage = name +"|"+ displayOrder +"|"+ shortName;
			Assert.assertTrue(adCustPage.isStageEdited(edtCustStage),
					"Verifying Stage is edited in the grid");
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
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName =data.get("systemName");
		String shortName = data.get("shortName");
		AdminCustomersTab adCustPage = basepage.clickOnAdminTab().clickOnCustomersSubTab();
		adCustPage.deleteStage(previous);
		String delCustStage = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertFalse(adCustPage.isEditStageDeleted(delCustStage),
				"Verifying Stage is deleted to the grid");
		return adCustPage;
	}
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
