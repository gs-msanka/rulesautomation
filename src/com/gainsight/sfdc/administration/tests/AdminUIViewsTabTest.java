/*package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminUIViewssSubTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminUIViewsTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}
	
	@Test(priority=1)  //Add Available Fileds
	public void testAdminAddCustomerStage() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminUIViewsTab");
		selectTabName(testData.get("addAvailableFields"));
	
	}
	private AdminUIViewssSubTab selectTabName(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String tabName = data.get("tabName");
		String ViewName =data.get("ViewName");
		String fieldName = data.get("fieldName");
		String selectffield = data.get("selectffield");
		System.out.println("selectffield has these values in test:" +selectffield);
		String foperator =data.get("foperator");
		String selectfvalue = data.get("selectfvalue");
		AdminUIViewssSubTab adUIview = basepage.clickOnAdminTab().clickOnUIViewssettingsSubTab();
		adUIview.selectTabName(tabName,ViewName,fieldName,selectffield,foperator,selectfvalue); 
		return adUIview;
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
	
	
	
}
*/