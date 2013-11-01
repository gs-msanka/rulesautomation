package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminAdoptionSubTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminAdoptionTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}
	
	
/*	@Test(priority=5)                        // measureCloumnMapping
	public void testAdminMapMeasureTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminAdoptionTab");
		measureCloumnMapping(testData.get("DeleteMeasure"));
	}
	private AdminAdoptionSubTab measureCloumnMapping(String testData) {
	HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		AdminAdoptionSubTab adAdopMsure = basepage.clickOnAdminTab()
				.clickOnAdoptionSubTab();
		adAdopMsure.measureCloumnMapping();
		Assert.assertTrue(adAdopMsure.isAdoptionMeasureColPresent(previous),
				"Verifying Stage is added to the grid");
		return adAdopMsure;
	}
	
	
	@Test(priority=1)                         //Add Measure
	public void testAdmincreateAdoptionMeasure() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminAdoptionTab");
		createAdoptionMeasure(testData.get("CreateNewMeasure"));
		
	}
	private AdminAdoptionSubTab createAdoptionMeasure(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname =data.get("systemname");
		String shortname = data.get("shortname");
		AdminAdoptionSubTab adAdopMsure = basepage.clickOnAdminTab().clickOnAdoptionSubTab();
		adAdopMsure.createAdoptionMeasure(Name,displayorder,systemname,shortname ); 
			Assert.assertTrue(adAdopMsure.isAdoptionMeasurePresent(Name),
					"Verifying Measure is added to the grid");
		return adAdopMsure;
	}
	
	@Test(priority=2)                         // Edit  Measure      
	public void testAdminEditMeasureTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminAdoptionTab");
		editAdoptionMeasure(testData.get("EditMeasure"));
	}

	private AdminAdoptionSubTab editAdoptionMeasure(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		AdminAdoptionSubTab adAdopMsure = basepage.clickOnAdminTab()
				.clickOnAdoptionSubTab();
		adAdopMsure.editAdoptionMeasure(previous, Name, displayorder, shortname);
		return adAdopMsure;
	}
	
	@Test(priority=3)                        // Delete Measure
	public void testAdminDeleteMeasureTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminAdoptionTab");
		deleteAdoptionMeasure(testData.get("DeleteMeasure"));
	}
	private AdminAdoptionSubTab deleteAdoptionMeasure(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		AdminAdoptionSubTab adAdopMsure = basepage.clickOnAdminTab()
				.clickOnAdoptionSubTab();
		adAdopMsure.deleteAdoptionMeasure(previous);
		return adAdopMsure;
	}
	

	@Test(priority=4)               //Usage Configuration                                
	public void testusageConfigurationTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminAdoptionTab");
		usageConfiguration(testData.get("UsageConfiguration1"));
	}
	private AdminAdoptionSubTab usageConfiguration(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String UsageConfig = data.get("UsageConfig");
		String Granularity = data.get("Granularity");
		String WeekStartsOn = data.get("WeekStartsOn");
		String WeekLabel    = data.get("WeekLabel");
		AdminAdoptionSubTab usgeConfig = basepage.clickOnAdminTab()
				.clickOnAdoptionSubTab();
		usgeConfig.usageConfiguration(UsageConfig, Granularity ,WeekStartsOn ,WeekLabel);
		return usgeConfig;
	} */
	

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	
	
	
}
