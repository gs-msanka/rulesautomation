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
import com.gainsight.utils.DataProviderArguments;

public class AdminCustomersTabTest extends BaseTest {
	
	String[] dirs = { "acceptancetests" };
	//private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
		//	+ generatePath(dirs);
	final String TEST_DATA_FILE = "testdata/sfdc/Administration/AdminCustomersTestdata.xls";
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
		deleteCustomersStagesThorughScript();
	}
	
	             //Add Stage
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Customer_Stage_Create")
	public void testAdminAddCustomerStage(HashMap<String, String> testData) throws BiffException, IOException {
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
	
		               // Edit  Stage      
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=2)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Customer_Stage_Edit")
	public void testAdminEditStageTest(HashMap<String, String> testData) throws BiffException, IOException {
		addNewStage(testData.get("CreateNewStage"));
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
			Assert.assertTrue(adCustPage.isStagePresent(edtCustStage),
					"Verifying Stage is edited in the grid");
		return adCustPage;
	}
	
		                 //  Delete Stage
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=3)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Customer_Stage_Delete")
	public void testAdminDeleteStageTest(HashMap<String, String> testData) throws BiffException, IOException {
		addNewStage(testData.get("CreateNewStage"));
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
		Assert.assertFalse(adCustPage.isStagePresent(delCustStage),
				"Verifying Stage is deleted to the grid");
		return adCustPage;
	}
	
	//script to delete
	 public void deleteCustomersStagesThorughScript() {
		  try {
		     String DELETERECORDS = "select id, JBCXM__DisplayOrder__c ,name from JBCXM__Picklist__c where (JBCXM__Category__c like 'Customer Stage' OR JBCXM__Category__c = null) and JBCXM__DisplayOrder__c >10 ";
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
