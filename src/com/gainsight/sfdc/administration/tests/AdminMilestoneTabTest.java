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
import com.gainsight.sfdc.administration.pages.AdminMilestoneTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminMilestoneTabTest extends BaseTest {
	
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}

	@Test(priority=1)                         //Add Milestone
	public void testAdmincreateAdoptionMeasure() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminMilestoneTab");
		createMilestoneType(testData.get("CreateNewMilestone"));
		
	}
	private AdminMilestoneTab createMilestoneType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName =data.get("systemName");
		String shortName = data.get("shortName");
		AdminMilestoneTab adCrteMstne = basepage.clickOnAdminTab().clickOnMilestoneTab();
		adCrteMstne.createMilestoneType(name,displayOrder,systemName,shortName );
		String milestoneType = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
			Assert.assertTrue(adCrteMstne.IsMilestoneTypePresent(milestoneType),
					"Verifying Stage is added to the grid");
		return adCrteMstne;
	}
	
	@Test(priority=2)                         // Edit  Milestone   
	public void testAdminEditMeasureTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminMilestoneTab");
		editMilestoneType(testData.get("EditMilestone"));
	}

	private AdminMilestoneTab editMilestoneType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String shortName = data.get("shortName");
		AdminMilestoneTab adCrteMstne = basepage.clickOnAdminTab().clickOnMilestoneTab();
		adCrteMstne.editMilestoneType(previous, name, displayOrder, shortName);
		String edtMilestoneType = name +"|"+ displayOrder +"|"+ shortName;
		Assert.assertTrue(adCrteMstne.IsMilestoneTypeEdited(edtMilestoneType),
				"Verifying Stage is edited in the grid");
		return adCrteMstne;
	}
	
	@Test(priority=3)                        // Delete Milestone
	public void testAdminDeleteMeasureTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminMilestoneTab");
		deleteMilestoneType(testData.get("DeleteMilestone"));
	}
	private AdminMilestoneTab deleteMilestoneType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName =data.get("systemName");
		String shortName = data.get("shortName");
		AdminMilestoneTab adCrteMstne = basepage.clickOnAdminTab().clickOnMilestoneTab();			
		adCrteMstne.deleteMilestoneType(previous);
		String delMilestoneType = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertFalse(adCrteMstne.IsMilestoneTypeDeleted(delMilestoneType),
				"Verifying Stage is deleted in the grid");
		return adCrteMstne;
	}
	
		
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
