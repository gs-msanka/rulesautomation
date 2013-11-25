package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;
import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminRetentionTab;
import com.gainsight.sfdc.administration.pages.AdminTransactionsTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminRetentionTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}
	
	
	@Test(priority=1)                             	// Create Alert Type    
	public void testAdminCreateAlertTypeTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
	@Test(priority=2)//@Test   //(dependsOnMethods={"adminCreateAlertTypeTest"})                  // Edit Alert Type
	public void testAdminEditAlertType1Test() throws BiffException, IOException {
		Report.logInfo("calling edit");
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
		Assert.assertTrue(adRetPage.isAlertTypeEdited(editAlertType),
				"Verifying Alert Type edited in the grid");	
		return adRetPage;
	}
		
	@Test(priority=3)//@Test  (dependsOnMethods={"adminCreateAlertTypeTest","adminEditAlertType1Test"})     // delete Alert Type
	public void testAdminDeleteAlertType2Test() throws BiffException, IOException {
		Report.logInfo("calling Delete");
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		String s = testData.get("DeleteAlertType");
		HashMap<String, String> data = getMapFromData(s);
		String previous = data.get("previous");
		String name = data.get("name");
		String displayOrder = data.get("displayOrder");
		String systemName = data.get("systemName");
		String shortName = data.get("shortName");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab().clickOnRetentionSubTab();
		adRetPage.deleteAlertType(previous);
		String delAlertType = name +"|"+ systemName +"|"+ displayOrder +"|"+ shortName;
		Assert.assertFalse(adRetPage.isAlertTypeDeleted(delAlertType),
				"Verifying Alert Type deleted from the grid");	
	}
	
	@Test(priority=4)//@Test                                             	// Create Alert Severity
	public void testAdminCreateAlertSeverityTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		createAlertSeverity(testData.get("CreateAlertSeverity"));
		//createAlertSeverity(testData.get("CreateAlertSeverity1"));
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
		@Test(priority=5)//@Test (dependsOnMethods={"adminCreateAlertSeverityTest"})        // Edit Alert Severity
	public void testAdminEditAlertSeverityTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
		Assert.assertTrue(adRetPage.isAlertSeverityEdited(editAlertSeverity),
				"Verifying Alert Severity added in the grid");
		return adRetPage;
	}
	@Test(priority=6)//@Test (dependsOnMethods={"adminCreateAlertSeverityTest","adminEditAlertSeverityTest"})  // Delete Alert Severity
	public void testAdminDeleteAlertSeverityTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
		Assert.assertFalse(adRetPage.isAlertSeverityDeleted(delAlertSeverity),
				"Verifying Alert Severity added in the grid");
		return adRetPage;
	}
	      @Test(priority=7)              	                               // Create Alert Reason
	public void testAdminCreateAlertReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		//createAlertReason(testData.get("CreateAlertReason"));
		createAlertReason(testData.get("CreateAlertReason1"));
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
	
	@Test(priority=8)//@Test  (dependsOnMethods={"adminCreateAlertReasonTest"})       // Edit Alert Reason
	public void testAdminEditAlertReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
		Assert.assertTrue(adRetPage.isAlertReasonEdited(edtAlertReason),
				"Verifying Alert Reason added in the grid");
		return adRetPage;
	}
	@Test(priority=9)//@Test    (dependsOnMethods={"adminCreateAlertReasonTest","adminEditAlertReasonTest"})   //Delete Alert Reason
	public void testAdminDeleteAlertReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
			Assert.assertFalse(adRetPage.isAlertReasonDeleted(delAlertReason),
					"Verifying Alert Reason added in the grid");
		return adRetPage;
	}
	
	@Test(priority=10)//@Test                                                                // Create Alert Status
	public void testAdminCreateAlertStatusTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		//createAlertStatus(testData.get("CreateAlertStatus"));
		createAlertStatus(testData.get("CreateAlertStatus1"));
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
	@Test(priority=11)//@Test (dependsOnMethods={"adminCreateAlertStatusTest"})       	// Edit Alert Status
	public void testAdminEditAlertStatusTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
			Assert.assertTrue(adRetPage.isAlertStatusEdited(edtAlertStatus),
					"Verifying Alert Status added in the grid");
		return adRetPage;
	}
	@Test(priority=12)//@Test  (dependsOnMethods={"adminCreateAlertStatusTest","adminEditAlertStatusTest"})        // Delete Alert Status
	public void testAdminDeleteAlertStatusTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
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
			Assert.assertFalse(adRetPage.isAlertStatusDeleted(delAlertStatus),
					"Verifying Alert Status added in the grid");
			return adRetPage;
	}
	@Test(priority=13)//@Test                                                              // Create Event Type
	public void testAdminCreateEventTypeTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin EventType");
		//createEventType(testData.get("CreateEventType"));
		createEventType(testData.get("CreateEventType1"));
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
				"Verify that newly added customer present in the grid");
		return adRetPage;
	}
	@Test(priority=14)//@Test (dependsOnMethods={"adminCreateEventTypeTest"})         	// Edit Event Type
	public void testAdminEditEventTypeTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin EventType");
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
			Assert.assertTrue(adRetPage.isEventTypeEdited(edtEventType),
					"Verify that newly added customer present in the grid");
		return adRetPage;
	}
	@Test(priority=15)//@Test (dependsOnMethods={"adminCreateEventTypeTest","adminEditEventTypeTest"})         // Delete Event Type
	public void testAdminDeleteEventTypeTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin EventType");
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
			Assert.assertFalse(adRetPage.isEventTypeDeleted(delEventType),
					"Verify that newly added customer present in the grid");
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