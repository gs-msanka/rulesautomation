package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminTransactionsTab;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminTransactionsTabTest extends BaseTest {
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
			addBookingTypes(testData.get("Booking Types1"));
			
		}
		private AdminTransactionsTab addBookingTypes(String testData) {
			HashMap<String, String> data = getMapFromData(testData);
			String name = data.get("name");
			String displayOrder = data.get("displayOrder");
			String systemName = data.get("systemName");
			String shortName = data.get("shortName");
			String selectBookingType = data.get("selectBookingType");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.addBookingTypes(name, displayOrder, systemName, shortName,selectBookingType);
			String TransBookingTypes= name +"|"+ systemName +"|"+ displayOrder +"|Custom-"+selectBookingType ;
			Assert.assertTrue(adTrPage.isBookingTypePresent(TransBookingTypes),
					"Verifying Booking Type is added in the grid");
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
			String name = data.get("name");
			String displayOrder = data.get("displayOrder");
			String shortName = data.get("shortName");
			String selectBookingType = data.get("selectBookingType");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
					.clickOnTransactionsTab();
			adTrPage.editbookingType(dummy, name, displayOrder, shortName, selectBookingType);
			String TransBookingTypes= name +"|"+ displayOrder +"|Custom-"+selectBookingType ;
			Assert.assertTrue(adTrPage.isBookingTypeEdited(TransBookingTypes),
					"Verifying Booking Type is edited in the grid");
			return adTrPage;
		}
		
		@Test(priority=18)  //@Test(dependsOnMethods={"adminAddBookingTypes","adminEditBookingTypes"})       // MapBooking types
		public void testMapBookingTypesTest() throws Throwable, IOException {
			HashMap<String, String> testData = testDataLoader.getDataFromExcel(
					TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
			mapBookingTypes(testData.get("MapBooking Types"));
		}
			private AdminTransactionsTab mapBookingTypes(String testData) {
			HashMap<String, String> data = getMapFromData(testData);
			String previous = data.get("previous");
			String mapBookingType = data.get("mapBookingType");
			
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
					                                      .clickOnTransactionsTab();
			adTrPage.mapBookingTypes(previous,mapBookingType);
			
			return adTrPage;
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
			String name = data.get("name");
			String displayOrder = data.get("displayOrder");
			String systemName = data.get("systemName");
			String selectBookingType = data.get("selectBookingType");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.deleteBookingTypes(previous);
			String TransBookingTypes= name +"|"+ systemName +"|"+ displayOrder +"|Custom-"+selectBookingType ;
			Assert.assertFalse(adTrPage.isBookingTypeDeleted(TransBookingTypes),
					"Verifying Booking Type is deleted from the grid");
			return adTrPage;
		}
		
	  @Test(priority=20)  // @Test                                             // Add Transaction Line Item
		public void testAddTransactionLinesItemsTest() throws BiffException, IOException {
			HashMap<String, String> testData = testDataLoader.getDataFromExcel(
					TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
			addTransactionLinesItems(testData.get("CreateTransactionLineITem"));
		}
		private AdminTransactionsTab addTransactionLinesItems(String testData) {
			HashMap<String, String> data = getMapFromData(testData);
			String name = data.get("name");
			String type = data.get("type");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.addTransactionLinesItems(name, type);
			String lineItem = name+"|"+type;
			Assert.assertTrue(adTrPage.isTransactionLineItemPresent(lineItem),
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
			String name = data.get("name");
			String type = data.get("type");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
					.clickOnTransactionsTab();
			adTrPage.editTransactionLineItem(previous, name, type);
			String lineItem = name+"|"+type;
			Assert.assertTrue(adTrPage.isTransactionLineItemEdited(lineItem),
					"Verifying Transaction Line Item is edited in the grid");
		}
			
		@Test(priority=22)  //	@Test (dependsOnMethods={"addTransactionLinesItems","editTransactionLineItem"})   // deleteTransactionLineItem
		public void testDeleteTransactionLineItemTest() throws BiffException, IOException {
			HashMap<String, String> testData = testDataLoader.getDataFromExcel(
					TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
			String s = testData.get("DeleteTransactionLineItem");
			HashMap<String, String> data = getMapFromData(s);
			String previous = data.get("previous");
			String name = data.get("name");
			String type = data.get("type");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.deleteTransactionLineItem(previous);
			String lineItem = name+"|"+type;
			Assert.assertFalse(adTrPage.isTransactionLineItemDeleted(lineItem),
					"Verifying Transaction Line Item is edited in the grid");
		}
		
			@Test(priority=23)  //@Test                                             // Add Churn Reason
		public void testAdminAddChurnReasonTest() throws BiffException, IOException {
			HashMap<String, String> testData = testDataLoader.getDataFromExcel(
					TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
			addChurnReason(testData.get("AddChurnReason"));
		}
		private AdminTransactionsTab addChurnReason(String testData) {
			HashMap<String, String> data = getMapFromData(testData);
			String name = data.get("name");
			String displayOrder = data.get("displayOrder");
			String systemName = data.get("systemName");
			String shortName = data.get("shortName");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.addChurnReason(name, displayOrder, systemName, shortName);
			String churnReason= name +"|"+ systemName +"|"+ displayOrder +"|"+shortName ;
			Assert.assertTrue(adTrPage.isChurnPresent(churnReason),
					"Verifying Churn Reason added in the grid");
			return adTrPage;
		}
		@Test(priority=24)  // @Test (dependsOnMethods={"adminAddChurnReason"})   	// Edit churn Reason
		public void testAdminEditChurnReasonTest() throws BiffException, IOException {
			HashMap<String, String> testData = testDataLoader.getDataFromExcel(
					TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
			editChurnReason(testData.get("EditChurnReason"));
		}
			private AdminTransactionsTab editChurnReason(String testData) {
			HashMap<String, String> data = getMapFromData(testData);
			String previous = data.get("previous");
			String name = data.get("name");
			String shortName = data.get("shortName");
			String displayOrder = data.get("displayOrder");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.editChurnReason(previous, name, displayOrder, shortName);
			String edtChurnReason= name +"|"+ shortName +"|"+displayOrder ;
			Assert.assertTrue(adTrPage.isChurnEdited(edtChurnReason),
					"Verifying Churn Reason edited in the grid");
			return adTrPage;
		}
		@Test(priority=25)  //@Test (dependsOnMethods={"adminAddChurnReason","adminEditChurnReason"})   // Delete Churn Reason
		public void testAdminDeleteChurnReasonTest() throws BiffException, IOException {
			HashMap<String, String> testData = testDataLoader.getDataFromExcel(
					TESTDATA_DIR + "AdministrationTestdata.xls", "AdminTrans");
			String s = testData.get("DeleteChurnReason");
			HashMap<String, String> data = getMapFromData(s);
			String previous = data.get("previous");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.deleteChurnReason(previous);
			String name = data.get("name");
			String displayOrder = data.get("displayOrder");
			String shortName = data.get("shortName");
			String systemName = data.get("systemName");
			String churnReason= name +"|"+ systemName +"|"+ displayOrder +"|"+shortName ;
			Assert.assertFalse(adTrPage.isChurnDeleted(churnReason),
					"Verifying Churn Reason deleted in the grid");
		}
		
		@AfterClass
		public void tearDown() {
			basepage.logout();
		}
}
