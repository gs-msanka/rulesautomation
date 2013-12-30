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
import com.gainsight.utils.DataProviderArguments;

public class AdminMilestoneTabTest extends BaseTest {
	
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	final String TEST_DATA_FILE = "testdata/sfdc/Administration/AdminMilestoneTestdata.xls";
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		deleteMilestoneFromScript();
		basepage.login();
	}

		                     //Add Milestone
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AdminMilestoneTab")
	public void testAdmincreateAdoptionMeasure(HashMap<String, String> testData) throws BiffException, IOException {
		
		createMilestoneFromScript();
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
	
	            //Edit MileStone
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=2)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AdminMilestoneTab")
	public void testAdminEditMeasureTest(HashMap<String, String> testData) throws BiffException, IOException {
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
		Assert.assertTrue(adCrteMstne.IsMilestoneTypePresent(edtMilestoneType),
				"Verifying Stage is edited in the grid");
		return adCrteMstne;
	}
	
		                // Delete Milestone
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=3)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AdminMilestoneTab")
	public void testAdminDeleteMeasureTest(HashMap<String, String> testData) throws BiffException, IOException {
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
		Assert.assertFalse(adCrteMstne.IsMilestoneTypePresent(delMilestoneType),
				"Verifying Stage is deleted in the grid");
		return adCrteMstne;
	}
	
	
	 public void createMilestoneFromScript() {
	       try {
	           String file = System.getProperty("user.dir")+"/testdata/sfdc/Administration/Milestone_Create_Script.txt";
	           Report.logInfo("File :" +file);
	           Report.logInfo("Pack :" +isPackageInstance());
	           apex.runApexCodeFromFile(file, isPackageInstance());
	          // isEventCreateScriptExecuted = true;
	       } catch (Exception e) {
	           Report.logInfo(e.getLocalizedMessage());
	       }
	   }
		
	 
	 public void deleteMilestoneFromScript() {
     try {
         String file = System.getProperty("user.dir")+"/testdata/sfdc/Administration/Milestone_Delete_Script.txt";
         Report.logInfo("File :" +file);
         Report.logInfo("Pack :" +isPackageInstance());
         apex.runApexCodeFromFile(file, isPackageInstance());
        // isEventCreateScriptExecuted = true;
     } catch (Exception e) {
         Report.logInfo(e.getLocalizedMessage());
     }
	 }
	 
	@AfterClass
	public void tearDown() {
		basepage.logout();
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
