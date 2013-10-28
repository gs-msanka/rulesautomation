package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.annotations.Test;

import com.gainsight.sfdc.administration.pages.AdminCustomersTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminCustomersTabTest extends BaseTest {
	
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	
	
	@Test     //Admin:-- Add Transaction Booking Types
	public void adminAddCustomerStage() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		addStage(testData.get("Booking Types"));
		addStage(testData.get("Booking Types1"));
		addStage(testData.get("Booking Types2"));
	}
	private AdminCustomersTab addStage(String testData) {
		//String testData = "Abacus | Active | New Business";
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname =data.get("systemname");
		String shortname = data.get("shortname");
		AdminCustomersTab adCustPage = basepage.clickOnAdminTab().clickOnCustomersSubTab();
		adCustPage.addStage(Name,displayorder,systemname,shortname); 
			//Assert.assertTrue(adTrPage.IsBookingTypePresent(Name),
					//"Verifying Bokking Type is added in the grid");
		return adCustPage;
	}
	

}
