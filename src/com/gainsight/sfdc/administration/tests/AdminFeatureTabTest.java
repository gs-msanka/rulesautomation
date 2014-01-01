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
import com.gainsight.utils.DataProviderArguments;

public class AdminFeatureTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	final String TEST_DATA_FILE = "testdata/sfdc/Administration/AdminFeaturesTestdata.xls";
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
		deleteFeaturesFromScript();
	}
	
	            //create Feature Type
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AdminFeaturesTab")
	public void testAdmincreateFeatureType(HashMap<String, String> testData) throws BiffException, IOException {
		createFeaturesFromScript();
		createFeatureType(testData.get("CreateFeatureType"));
	}
	
	private AdminFeaturesSubTab createFeatureType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String name = data.get("name");
		String productName = data.get("productName");
		String systemName =data.get("systemName");
		AdminFeaturesSubTab adFeturTpe = basepage.clickOnAdminTab().clickOnFeaturesTab();
		adFeturTpe.createFeatureType(name, systemName ,productName ); 
		String featureType = name +"|"+ systemName +"|"+ productName;
			Assert.assertTrue(adFeturTpe.IsFeatureTypePresent(featureType),
					"Verifying Feature Type is added to the grid");
		return adFeturTpe;
	}
	
	                 // Edit  Feature Type     
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=2)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AdminFeaturesTab")
	public void testAdminEditFeatureTest(HashMap<String, String> testData) throws BiffException, IOException {
		editFeatureType(testData.get("EditFeatureType"));
	}
	private AdminFeaturesSubTab editFeatureType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String productName = data.get("productName");
		String systemName =data.get("systemName");
		AdminFeaturesSubTab adFeturTpe = basepage.clickOnAdminTab().clickOnFeaturesTab();
		adFeturTpe.editFeatureType( previous, name, systemName ,productName);
		String edtFeatureType = name +"|"+ systemName +"|"+ productName;
		Assert.assertTrue(adFeturTpe.IsFeatureTypePresent(edtFeatureType),
				"Verifying Feature Type is edited in the grid");
		
		return adFeturTpe;
	}
	
	                       // Delete Feature Type
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=2)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AdminFeaturesTab")
	public void testAdminDeletFeatureTest(HashMap<String, String> testData) throws BiffException, IOException {
		deleteFeatureType(testData.get("DeleteFeatureType"));
	}
	private AdminFeaturesSubTab deleteFeatureType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String productName = data.get("productName");
		String systemName =data.get("systemName");
		AdminFeaturesSubTab adFeturTpe = basepage.clickOnAdminTab().clickOnFeaturesTab();			
		adFeturTpe.deleteFeatureType(previous);
		String delFeatureType = name +"|"+ systemName +"|"+ productName;
		Assert.assertFalse(adFeturTpe.IsFeatureTypePresent(delFeatureType),
				"Verifying Feature Type is deleted in the grid");
		return adFeturTpe;
	}
	
	public void createFeaturesFromScript() {
	       try {
	           String file = env.basedir+"/testdata/sfdc/Administration/Features_Create_Script.txt";
	           Report.logInfo("File :" +file);
	           Report.logInfo("Pack :" +isPackageInstance());
	           apex.runApexCodeFromFile(file, isPackageInstance());
	          // isEventCreateScriptExecuted = true;
	       } catch (Exception e) {
	           Report.logInfo(e.getLocalizedMessage());
	       }
	   }
		
	 
	 public void deleteFeaturesFromScript() {
  try {
     String DELETERECORDS = "select id from JBCXM__Features__c";
     if(!isPackageInstance()) {
         DELETERECORDS = removeNameSpace(DELETERECORDS);
     }
     soql.deleteQuery(DELETERECORDS);
  } catch (Exception e) {
      Report.logInfo(e.getLocalizedMessage());
  }
	 }
	
	
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
