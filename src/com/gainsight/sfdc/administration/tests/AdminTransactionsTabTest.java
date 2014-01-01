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
import com.gainsight.utils.DataProviderArguments;

public class AdminTransactionsTabTest extends BaseTest {
	// private static final String String = null;
		String[] dirs = { "acceptancetests" };
		private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
				+ generatePath(dirs);
		final String TEST_DATA_FILE = "testdata/sfdc/Administration/AdminTransactionTestdata.xls";

		@BeforeClass
		public void setUp() {
			Report.logInfo("Starting  Test Case...");
			basepage.login();
			deleteAdminTransactionsThorughScript();
		}

		                     //  Add Transaction Booking Types
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Create_BookingTypes")
		public void testAdminAddBookingTypesTest(HashMap<String, String> testData) throws BiffException, IOException {
			addBookingTypes(testData.get("createBookingTypes"));
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
		           // Edit Transaction Booking Types
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=2)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Edit_BookingTypes")
		public void testAdminEditBookingTypesTest(HashMap<String, String> testData) throws BiffException, IOException {
			addBookingTypes(testData.get("createBookingTypes"));
			editbookingType(testData.get("EditBookingTypes"));
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
			Assert.assertTrue(adTrPage.isBookingTypePresent(TransBookingTypes),
					"Verifying Booking Type is edited in the grid");
			return adTrPage;
		}
		
		
		// MapBooking types
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=3)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Map_BookingTypes")
		public void testMapBookingTypesTest(HashMap<String, String> testData) throws BiffException, IOException {
			addBookingTypes(testData.get("createBookingTypes"));
			mapBookingTypes(testData.get("MapBookingTypes"));
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
		
		
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=4)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Delete_BookingTypes")
		public void testAdminDeleteBookingTypesTest(HashMap<String, String> testData) throws BiffException, IOException {
			addBookingTypes(testData.get("createBookingTypes"));
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
			Assert.assertFalse(adTrPage.isBookingTypePresent(TransBookingTypes),
					"Verifying Booking Type is deleted from the grid");
			return adTrPage;
		}
		  
	           // Add Transaction Line Item
	  @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=5)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Add_LineItem")
	public void testAddTransactionLinesItemsTest(HashMap<String, String> testData) throws BiffException, IOException {
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
			//Edit Transactions.
	 @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=6)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Edit_LineItem")
	public void testEditTransactionLineItemTest(HashMap<String, String> testData) throws BiffException, IOException {
		 addTransactionLinesItems(testData.get("CreateTransactionLineITem"));
		 EditTransactionLineItem(testData.get("EditTransactionLineItem"));
		}
	 private AdminTransactionsTab EditTransactionLineItem(String testData) {
			HashMap<String, String> data = getMapFromData(testData);
			String previous = data.get("previous");
			String name = data.get("name");
			String type = data.get("type");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab()
					.clickOnTransactionsTab();
			adTrPage.editTransactionLineItem(previous, name, type);
			String lineItem = name+"|"+type;
			Assert.assertTrue(adTrPage.isTransactionLineItemPresent(lineItem),
					"Verifying Transaction Line Item is edited in the grid");
			return adTrPage;
		}
				
			       // deleteTransactionLineItem
  @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=7)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Delete_LineItem")
	 public void testDeleteTransactionLineItemTest(HashMap<String, String> testData) throws BiffException, IOException {
	  addTransactionLinesItems(testData.get("CreateTransactionLineITem"));
	  deleteTransactionLineItem(testData.get("DeleteTransactionLineItem"));
			}
    private AdminTransactionsTab deleteTransactionLineItem(String testData) {	
			HashMap<String, String> data = getMapFromData(testData);
			String previous = data.get("previous");
			String name = data.get("name");
			String type = data.get("type");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.deleteTransactionLineItem(previous);
			String lineItem = name+"|"+type;
			Assert.assertFalse(adTrPage.isTransactionLineItemPresent(lineItem),
					"Verifying Transaction Line Item is edited in the grid");
			return adTrPage;
		}
		
		      	// Add Churn Reason
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=8)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Add_ChurnReason")
	  public void testAdminAddChurnReasonTest(HashMap<String, String> testData) throws BiffException, IOException {
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
		
		             // Edit churn Reason
		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=8)
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Edit_ChurnReason")
	 public void testAdminEditChurnReasonTest(HashMap<String, String> testData) throws BiffException, IOException {
			addChurnReason(testData.get("AddChurnReason"));
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
			Assert.assertTrue(adTrPage.isChurnPresent(edtChurnReason),
					"Verifying Churn Reason edited in the grid");
			return adTrPage;
		}
		
			 // Delete Churn Reason
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=9)
	  @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Delete_ChurnReason")
	 public void testAdminDeleteChurnReasonTest(HashMap<String, String> testData) throws BiffException, IOException {
		addChurnReason(testData.get("AddChurnReason"));
		deleteChurnReason(testData.get("DeleteChurnReason"));
			}		
	private AdminTransactionsTab deleteChurnReason(String testData) {	
			HashMap<String, String> data = getMapFromData(testData);
			String previous = data.get("previous");
			AdminTransactionsTab adTrPage = basepage.clickOnAdminTab().clickOnTransactionsTab();
			adTrPage.deleteChurnReason(previous);
			String name = data.get("name");
			String displayOrder = data.get("displayOrder");
			String shortName = data.get("shortName");
			String systemName = data.get("systemName");
			String churnReason= name +"|"+ systemName +"|"+ displayOrder +"|"+shortName ;
			Assert.assertFalse(adTrPage.isChurnPresent(churnReason),
					"Verifying Churn Reason deleted in the grid");
			return adTrPage;
		}

	                        //script to delete
		 public void deleteAdminTransactionsThorughScript() {
			  try {
			     String DELETERECORDS = "select id, JBCXM__DisplayOrder__c ,name from JBCXM__Picklist__c where  JBCXM__DisplayOrder__c >12 and JBCXM__Category__c IN ('Order Type' , 'Churn Reason')";
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
