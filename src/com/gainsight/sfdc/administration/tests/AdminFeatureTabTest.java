package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminFeaturesSubTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminFeatureTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}
	
	
	
	@Test(priority=1)                         //create Feature Type
	public void testAdmincreateFeatureType() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminFeatureTab");
		createFeatureType(testData.get("CreateFeatureType"));
	}
	private AdminFeaturesSubTab createFeatureType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String productName = data.get("productName");
		String systemname =data.get("systemname");
		AdminFeaturesSubTab adFeturTpe = basepage.clickOnAdminTab().ClickOnFeaturesTab();
		adFeturTpe.createFeatureType(Name, systemname ,productName ); 
			Assert.assertTrue(adFeturTpe.IsFeatureTypePresent(Name),
					"Verifying Feature Type is added to the grid");
		return adFeturTpe;
	}
	@Test(priority=2)                         // Edit  Feature Type     
	public void testAdminEditFeatureTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminFeatureTab");
		editFeatureType(testData.get("EditFeatureType"));
	}
	private AdminFeaturesSubTab editFeatureType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String Name = data.get("Name");
		String productName = data.get("productName");
		String systemname =data.get("systemname");
		AdminFeaturesSubTab adFeturTpe = basepage.clickOnAdminTab()
				.ClickOnFeaturesTab();
		adFeturTpe.editFeatureType( previous, Name, systemname, productName);
		return adFeturTpe;
	}
	@Test(priority=3)                        // Delete Feature Type
	public void testAdminDeletFeatureTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminFeatureTab");
		deleteFeatureType(testData.get("DeleteFeatureType"));
	}
	private AdminFeaturesSubTab deleteFeatureType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		AdminFeaturesSubTab adFeturTpe = basepage.clickOnAdminTab()
				.ClickOnFeaturesTab();
		adFeturTpe.deleteFeatureType(previous);
		return adFeturTpe;
	}
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
