package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.administration.pages.AdminRetentionTab;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class AdminRetentionTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	final String TEST_DATA_FILE = "testdata/sfdc/Administration/AdminRetentionTestdata.xls";
	@BeforeClass
	public void setUp() {
		Log.info("Starting  Test Case...");
		deletePickList();
		basepage.login();
		
	}
	            //create Alert Type
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AType_Create")
	public void testAdminCreateAlertTypeTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertType(testData.get("CreateAlertType"));
	}
	private AdminRetentionTab createAlertType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();	
		adRetPage.createAlertType(name, displayOrder, systemName, shortName,includeinWidget);
		String alertType = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isAlertTypePresent(alertType),
				"Verifying Alert Type added in the grid");
		return adRetPage;
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=2)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AType_Edit")
	public void testAdminEditAlertType1Test(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertType(testData.get("CreateAlertType"));
		editAlertType(testData.get("EditAlertType"));
	}
		private AdminRetentionTab editAlertType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();
		adRetPage.editAlertType(previous, name, displayOrder, shortName,includeinWidget);
		String editAlertType = name +"|"+displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isAlertTypePresent(editAlertType),
				"Verifying Alert Type edited in the grid");	
		return adRetPage;
	}
		//Delete Alert Type
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=3)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AType_Delete")
		public void testAdminDeleteAlertType2Test(HashMap<String, String> testData) throws BiffException, IOException {
			createAlertType(testData.get("CreateAlertType"));
			deleteAlertType(testData.get("DeleteAlertType"));
		}
		
		private AdminRetentionTab deleteAlertType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();
		adRetPage.deleteAlertType(previous);
		String delAlertType = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertFalse(adRetPage.isAlertTypePresent(delAlertType),
				"Verifying Alert Type deleted from the grid");	
		return adRetPage;
	}
	
	                                  	// Create Alert Severity
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=4)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ASeverity_Create")
	public void testAdminCreateAlertSeverityTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertSeverity(testData.get("CreateAlertSeverity"));
	}
	private AdminRetentionTab createAlertSeverity(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.createAlertSeverity(name, displayOrder, systemName,shortName, includeinWidget);
		String alertSeverity = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isAlertSeverityPresent(alertSeverity),
				"Verifying Alert Severity added in the grid");
		return adRetPage;
	}
		      // Edit Alert Severity
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=5)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ASeverity_Edit")
		public void testAdminEditAlertSeverityTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertSeverity(testData.get("CreateAlertSeverity"));		
		editAlertSeverity(testData.get("EditAlertSeverity"));
	}
	private AdminRetentionTab editAlertSeverity(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String shortName = data.get("shortName");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();
		adRetPage.editAlertSeverity(previous, name, displayOrder, shortName, includeinWidget);
		String editAlertSeverity = name +"|"+ displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isAlertSeverityPresent(editAlertSeverity),
				"Verifying Alert Severity added in the grid");
		return adRetPage;
	}
		
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=6)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ASeverity_Delete")
	public void testAdminDeleteAlertSeverityTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertSeverity(testData.get("CreateAlertSeverity"));
		deleteAlertSeverity(testData.get("DeleteAlertSeverity"));
    }
	
	private AdminRetentionTab deleteAlertSeverity(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();
		adRetPage.deleteAlertSeverity(previous);
		String delAlertSeverity = name +"|"+systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertFalse(adRetPage.isAlertSeverityPresent(delAlertSeverity),
				"Verifying Alert Severity added in the grid");
		return adRetPage;
	}
	                       // Create Alert Reason	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=7)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AReason_Create")
	public void testAdminCreateAlertReasonTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertReason(testData.get("CreateAlertReason"));
    }
	
	private AdminRetentionTab createAlertReason(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();	
		adRetPage.createAlertReason(name, displayOrder, systemName, shortName);
		String alertReason = name +"|"+systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isAlertReasonPresent(alertReason),
				"Verifying Alert Reason added in the grid");
		return adRetPage;
	}
	
	                  // Edit Alert Reason
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=8)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AReason_Edit")
	public void testAdminEditAlertReasonTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertReason(testData.get("CreateAlertReason"));
		editAlertReason(testData.get("EditAlertReason"));
    }
	
	private AdminRetentionTab editAlertReason(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.editAlertReason(previous, name, displayOrder, shortName);
		String edtAlertReason = name +"|"+displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isAlertReasonPresent(edtAlertReason),
				"Verifying Alert Reason added in the grid");
		return adRetPage;
	}
	                 //Delete Alert Reason
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=9)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AReason_Delete")
	public void testAdminDeleteAlertReasonTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertReason(testData.get("CreateAlertReason"));
		deleteAlertReason(testData.get("DeleteAlertReason"));
    }
	
	private AdminRetentionTab deleteAlertReason(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();
		 adRetPage.deleteAlertReason(previous);
		 String delAlertReason = name +"|"+systemName +"|"+ displayOrder +"|"+ shortName;
			Assert.assertFalse(adRetPage.isAlertReasonPresent(delAlertReason),
					"Verifying Alert Reason added in the grid");
		return adRetPage;
	}
	
	                                              // Create Alert Status
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=10)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AStatus_Create")
	public void testAdminCreateAlertStatusTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertStatus(testData.get("CreateAlertStatus"));
    }
	
	private AdminRetentionTab createAlertStatus(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();				
		adRetPage.createAlertStatus(name, displayOrder, systemName, shortName);
		 String alertStatus = name +"|"+systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isAlertStatusPresent(alertStatus),
				"Verifying Alert Status added in the grid");
		return adRetPage;
	}
	      	          // Edit Alert Status
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=11)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AStatus_Edit")
	public void testAdminEditAlertStatusTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertStatus(testData.get("CreateAlertStatus"));
		editAlertStatus(testData.get("editAlertStatus"));
    }
	
	private AdminRetentionTab editAlertStatus(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();				
		adRetPage.editAlertStatus(previous, name, displayOrder,  shortName);
		 String edtAlertStatus = name +"|"+ displayOrder +"|"+ shortName;
			Assert.assertTrue(adRetPage.isAlertStatusPresent(edtAlertStatus),
					"Verifying Alert Status added in the grid");
		return adRetPage;
	}
	                 // Delete Alert Status
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=12)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AStatus_Delete")
	public void testAdminDeleteAlertStatusTest(HashMap<String, String> testData) throws BiffException, IOException {
		createAlertStatus(testData.get("CreateAlertStatus"));
		deleteAlertStatus(testData.get("deleteAlertStatus"));
    }
	
	private AdminRetentionTab deleteAlertStatus(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();
		adRetPage.deleteAlertStatus(previous);
		 String delAlertStatus = name +"|"+systemName +"|"+ displayOrder +"|"+ shortName;
			Assert.assertFalse(adRetPage.isAlertStatusPresent(delAlertStatus),
					"Verifying Alert Status added in the grid");
			return adRetPage;
	}
                            // Create Event Type
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=13)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EType_Create")
	public void testAdminCreateEventTypeTest(HashMap<String, String> testData) throws BiffException, IOException {
		createEventType(testData.get("CreateEventType"));
    }
	
	private AdminRetentionTab createEventType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.createEventType(name, displayOrder, systemName, shortName);
		 String eventType = name +"|"+systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertTrue(adRetPage.isEventTypePresent(eventType),
				"Verify that newly Event Type present in the grid");
		return adRetPage;
	}
	         	// Edit Event Type
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=14)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EType_Edit")
	public void testAdminEditEventTypeTest(HashMap<String, String> testData) throws BiffException, IOException {
		createEventType(testData.get("CreateEventType"));
		editEventType(testData.get("editEventType"));
    }
	private AdminRetentionTab editEventType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();			
		adRetPage.editEventType(previous, name, displayOrder, shortName);
		 String edtEventType = name +"|"+ displayOrder +"|"+ shortName;
			Assert.assertTrue(adRetPage.isEventTypePresent(edtEventType),
					"Verify that Event Type present in the grid");
		return adRetPage;
	}
	                  // Delete Event Type
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=15)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "EType_Delete")
	public void testAdminDeleteEventTypeTest(HashMap<String, String> testData) throws BiffException, IOException {
		createEventType(testData.get("CreateEventType"));
		deleteEventType(testData.get("deleteEventType"));
    }
	private AdminRetentionTab deleteEventType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();			
		adRetPage.deleteEventType(previous);
		 String delEventType = name +"|"+systemName +"|"+ displayOrder +"|"+ shortName;
			Assert.assertFalse(adRetPage.isEventTypePresent(delEventType),
					"Verify that Event Type present in the grid");
			return adRetPage;
	}
	
	
/*	@Test          	// Configuration
	public void taskConfiguration() {
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.taskConfiguration();
	} */

			
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

}