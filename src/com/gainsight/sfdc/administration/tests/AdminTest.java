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

public class AdminTest extends BaseTest {

	// private static final String String = null;
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}

	@Test(priority=16)  //@Test     //  Add Transaction Booking Types
	public void testAdminAddBookingTypesTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		addBookingTypes(testData.get("Booking Types"));
		addBookingTypes(testData.get("Booking Types1"));
		//addBookingTypes(testData.get("Booking Types2"));
	}
	private AdminTransactionsTab addBookingTypes(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname = data.get("systemname");
		String shortname = data.get("shortname");
		String bookingtypeselect = data.get("bookingtypeselect");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
		adTrPage.addBookingTypes(Name, displayorder, systemname, shortname,bookingtypeselect);
		Assert.assertTrue(adTrPage.isBookingTypePresent(Name),
				"Verifying Bokking Type is added in the grid");
		return adTrPage;
	}
	@Test(priority=17)  //@Test (dependsOnMethods={"adminAddBookingTypes"})     // Edit Transaction Booking Types         
	public void testAdminEditBookingTypesTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		//editbookingType(testData.get("EditBookingTypes"));
		editbookingType(testData.get("EditBookingTypes1"));
	}

	private AdminTransactionsTab editbookingType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String dummy = data.get("previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.editbookingType(dummy, Name, displayorder, shortname);
		return adTrPage;
	}
	@Test(priority=18)  //@Test(dependsOnMethods={"adminAddBookingTypes","adminEditBookingTypes"})       // MapBooking types
	public void testMapBookingTypesTest() throws Throwable, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		String s = testData.get("MapBooking Types");
		HashMap<String, String> data = getMapFromData(s);
		String previous = data.get("previous");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.mapBookingTypes(previous);
	}
	@Test(priority=19)  //@Test (dependsOnMethods={"adminAddBookingTypes","adminEditBookingTypes","mapBookingTypes"})    // Delete Booking Types
	public void testAdminDeleteBookingTypesTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		deleteBookingTypes(testData.get("DeleteBookingTypes"));
	}
	private AdminTransactionsTab deleteBookingTypes(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String previous = data.get("previous");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.deleteBookingTypes(previous);
		return adTrPage;
	}
    @Test(priority=20)  // @Test                    // Add Transaction Line Item
	public void testAddTransactionLinesItemsTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		addTransactionLinesItems(testData.get("CreateTransactionLineITem"));
	}
	private AdminTransactionsTab addTransactionLinesItems(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String type = data.get("type");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.addTransactionLinesItems(Name, type);
		Assert.assertTrue(adTrPage.isTransactionLineItemPresent(Name),
				"Verifying Transaction Line Item is added to the grid");
		return adTrPage;
	}
		@Test(priority=21)  //@Test (dependsOnMethods={"addTransactionLinesItems"})        // Edit TransactionLineItem
	public void testEditTransactionLineItemTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		String s = testData.get("EditTransactionLineItem");
		HashMap<String, String> data = getMapFromData(s);
		String previous = data.get("previous");
		String Name = data.get("Name");
		String type = data.get("type");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.editTransactionLineItem(previous, Name, type);
	}
	@Test(priority=22)  //	@Test (dependsOnMethods={"addTransactionLinesItems","editTransactionLineItem"})   // deleteTransactionLineItem
	public void testDeleteTransactionLineItemTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		String s = testData.get("DeleteTransactionLineItem");
		HashMap<String, String> data = getMapFromData(s);
		String previous = data.get("previous");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.deleteTransactionLineItem(previous);
	}
		@Test(priority=23)  //@Test                                             // Add Churn Reason
	public void testAdminAddChurnReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		addChurnReason(testData.get("AddChurnReason"));
	}
	private AdminTransactionsTab addChurnReason(String testData) {
		// String testData = "Abacus | Active | New Business";
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname = data.get("systemname");
		String shortname = data.get("shortname");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.addChurnReason(Name, displayorder, systemname, shortname);
		Assert.assertTrue(adTrPage.IsChurnPresent(Name),
				"Verifying Churn Reason added in the grid");
		return adTrPage;
	}
	@Test(priority=24)  // @Test (dependsOnMethods={"adminAddChurnReason"})   	// Edit churn Reason
	public void testAdminEditChurnReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		String s = testData.get("EditChurnReason");
		HashMap<String, String> data = getMapFromData(s);
		String Previous = data.get("Previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.editChurnReason(Previous, Name, displayorder, shortname);
	}
	@Test(priority=25)  //@Test (dependsOnMethods={"adminAddChurnReason","adminEditChurnReason"})        // Delete Churn Reason
	public void testAdminDeleteChurnReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
		String s = testData.get("DeleteChurnReason");
		HashMap<String, String> data = getMapFromData(s);
		String Previous = data.get("Previous");
		AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
				.clickOnTransactionsTab();
		adTrPage.deleteChurnReason(Previous);
	}
	@Test(priority=1)                             	// Create Alert Type    
	public void testAdminCreateAlertTypeTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		createAlertType(testData.get("CreateAlertType"));
		createAlertType(testData.get("CreateAlertType1"));
		//createAlertType(testData.get("CreateAlertType2"));
	}
	private AdminRetentionTab createAlertType(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname = data.get("systemname");
		String shortname = data.get("shortname");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.createAlertType(Name, displayorder, systemname, shortname,
				includeinWidget);
		Assert.assertTrue(adRetPage.IsAlertTypePresent(Name),
				"Verifying Alert Type added in the grid");
		return adRetPage;
	}
	@Test(priority=2)//@Test   //(dependsOnMethods={"adminCreateAlertTypeTest"})                  // Edit Alert Type
	public void testAdminEditAlertType1Test() throws BiffException, IOException {
		Report.logInfo("calling edit");
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		String s = testData.get("EditAlertType");
		HashMap<String, String> data = getMapFromData(s);
		String Previous = data.get("Previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.editAlertType(Previous, Name, displayorder, shortname,
				includeinWidget);
	}
	@Test(priority=3)//@Test  (dependsOnMethods={"adminCreateAlertTypeTest","adminEditAlertType1Test"})     // delete Alert Type
	public void testAdminDeleteAlertType2Test() throws BiffException, IOException {
		Report.logInfo("calling Delete");
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		String s = testData.get("DeleteAlertType");
		HashMap<String, String> data = getMapFromData(s);
		String Previous = data.get("Previous");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.deleteAlertType(Previous);
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
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname = data.get("systemname");
		String shortname = data.get("shortname");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.createAlertSeverity(Name, displayorder, systemname,
				shortname, includeinWidget);
		Assert.assertTrue(adRetPage.IsAlertSeverityPresent(Name),
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
		String Previous = data.get("Previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		String includeinWidget = data.get("includeinWidget");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.editAlertSeverity(Previous, Name, displayorder, shortname,
				includeinWidget);
		return adRetPage;
	}
	@Test(priority=6)//@Test (dependsOnMethods={"adminCreateAlertSeverityTest","adminEditAlertSeverityTest"})  // Delete Alert Severity
	public void testAdminDeleteAlertSeverityTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		String s = testData.get("DeleteAlertSeverity");
		HashMap<String, String> data = getMapFromData(s);
		String Previous = data.get("Previous");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.deleteAlertSeverity(Previous);
	}
	      @Test(priority=7)              	                           // Create Alert Reason
	public void testAdminCreateAlertReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		//createAlertReason(testData.get("CreateAlertReason"));
		createAlertReason(testData.get("CreateAlertReason1"));
	}
	private AdminRetentionTab createAlertReason(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname = data.get("systemname");
		String shortname = data.get("shortname");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.createAlertReason(Name, displayorder, systemname, shortname);
		Assert.assertTrue(adRetPage.IsAlertReasonPresent(Name),
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
		String Previous = data.get("Previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.editAlertReason(Previous, Name, displayorder, shortname);
		return adRetPage;
	}
	@Test(priority=9)//@Test    (dependsOnMethods={"adminCreateAlertReasonTest","adminEditAlertReasonTest"})   //Delete Alert Reason
	public void testAdminDeleteAlertReasonTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		String s = testData.get("DeleteAlertReason");
		HashMap<String, String> data = getMapFromData(s);
		String Previous = data.get("Previous");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.deleteAlertReason(Previous);
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
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname = data.get("systemname");
		String shortname = data.get("shortname");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.createAlertStatus(Name, displayorder, systemname, shortname);
		Assert.assertTrue(adRetPage.IsAlertStatusPresent(Name),
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
		String Previous = data.get("Previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.editAlertStatus(Previous, Name, displayorder, shortname);
		return adRetPage;
	}
	@Test(priority=12)//@Test  (dependsOnMethods={"adminCreateAlertStatusTest","adminEditAlertStatusTest"})        // Delete Alert Status
	public void testAdminDeleteAlertStatusTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin Retention");
		String s = testData.get("deleteAlertStatus");
		HashMap<String, String> data = getMapFromData(s);
		String Previous = data.get("Previous");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.deleteAlertStatus(Previous);
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
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String systemname = data.get("systemname");
		String shortname = data.get("shortname");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.createEventType(Name, displayorder, systemname, shortname);
		Assert.assertTrue(adRetPage.IsEventTypePresent(Name),
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
		String Previous = data.get("Previous");
		String Name = data.get("Name");
		String displayorder = data.get("displayorder");
		String shortname = data.get("shortname");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.editEventType(Previous, Name, displayorder, shortname);
		return adRetPage;
	}
	@Test(priority=15)//@Test (dependsOnMethods={"adminCreateEventTypeTest","adminEditEventTypeTest"})         // Delete Event Type
	public void testAdminDeleteEventTypeTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "Admin EventType");
		String s1 = testData.get("deleteEventType");
		HashMap<String, String> data = getMapFromData(s1);
		String Previous = data.get("Previous");
		AdminRetentionTab adRetPage = basepage.clickOnAdminTab()
				.clickOnRetentionSubTab();
		adRetPage.deleteEventType(Previous);
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