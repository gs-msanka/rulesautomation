package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
		Log.info("Starting  Test Case...");
		deletePickList();
		basepage.login();
		
	}
	
	            //create Feature Type
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Create_Features")
	public void testAdmincreateFeatureType(HashMap<String, String> testData) throws BiffException, IOException {
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
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Edit_Features")
	public void testAdminEditFeatureTest(HashMap<String, String> testData) throws BiffException, IOException {
		createFeatureType(testData.get("CreateFeatureType"));
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
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Delete_Features")
	public void testAdminDeletFeatureTest(HashMap<String, String> testData) throws BiffException, IOException {
		createFeatureType(testData.get("CreateFeatureType"));
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
	
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
