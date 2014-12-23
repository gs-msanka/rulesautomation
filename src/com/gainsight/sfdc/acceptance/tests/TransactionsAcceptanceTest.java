package com.gainsight.sfdc.acceptance.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.churn.pages.ChurnPage;
import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pojo.CustomerSummary;
import com.gainsight.sfdc.customer360.pojo.TimeLineItem;
import com.gainsight.sfdc.helpers.AmountsAndDatesUtil;
import com.gainsight.sfdc.pages.CustomerSuccessPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.transactions.pages.TransactionsPage;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;
import jxl.read.biff.BiffException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.TimeZone;

public class TransactionsAcceptanceTest extends BaseTest {
	String[] dirs = { "acceptancetests" };
	final String TEST_DATA_FILE = "testdata/sfdc/acceptancetests/AcceptanceTests.xls";
	private String accQuery = "Select id from Account where name='%s'";
	private boolean loggedIn = false;
	private String currentDate;
	private final String STANDARD_VIEW="Standard View";
    private final String TRANS_SETUP = "/apex_scripts/acceptance_tests/transactions.apex";
    private final String SUMMARY_CONFIG = "/testdata/sfdc/c360Summary/C360SummaryUpdate.txt";
	
	@BeforeClass
	public void setUp() throws Exception {
		try {
            Report.logInfo("Starting Acceptance Test Case...");
			apex.runApexCodeFromFile(TestEnvironment.basedir+TRANS_SETUP , isPackage);
            apex.runApexCodeFromFile(TestEnvironment.basedir+SUMMARY_CONFIG, isPackage);
			/*
			 * PackageUtil.updateAccountLayout(
			 * "unpackaged/layouts/Account-Account Layout.layout",
			 * "CustomerSuccess",
			 * "resources/package/account360widget.txt",isPackageInstance());
			 */
			basepage.login();			
			currentDate=AmountsAndDatesUtil.getCurrentDate();
			loggedIn = true;
		} catch (Exception e) {
			Report.logInfo(e.getMessage());
			throw e;
		}
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT1")
	public void testAddNewCustomer(HashMap<String, String> testData)
			throws BiffException, IOException {
		HashMap<String, String> data = getMapFromData(testData.get("Customer"));
		String customerName = data.get("customerName");
		String status = data.get("status");
		String stage = data.get("stage");
		String comments = data.get("comments");
		CustomersPage customersPage = basepage.clickOnCustomersTab()
				.clickOnCustomersSubTab()
				.addCustomer(customerName, status, stage, comments);
		Assert.assertTrue(customersPage.isCustomerPresent(customerName),
				"Verify that newly added customer present in the grid");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT2")
	public void testAddNewCustomerAndTransaction(
			HashMap<String, String> testData) throws ParseException,
			BiffException, IOException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		Customer360Page customer360Page = addNewBusinessTransaction(testData);
		/* Renewal Transaction */
		HashMap<String, String> rnlData = getMapFromData(testData
				.get("RenewalTRN"));
		
		String customerName = rnlData.get("customerName");
		customer360Page = customer360Page.clickOnTransactionTab()
				.clickOnTransactionsSubTab().selectView(STANDARD_VIEW).selectView(STANDARD_VIEW).addRenewalTransaction(rnlData)
				.gotoCustomer360(customerName);
		CustomerSummary summary = customer360Page.getSummaryDetails();
		int fnPosition = customer360Page.getPositionOfTransaction(
				"New Business", currentDate);
		int rtPosition = customer360Page.getPositionOfTransaction("Renewal",
				currentDate);
		int asv = Integer.parseInt(rnlData.get("asv").trim());
		int users = Integer.parseInt(rnlData.get("userCount").trim());
		Assert.assertEquals(rnlData.get("asv").trim(), summary.getASV().trim());
		Assert.assertEquals(calcMRR(asv),
				Integer.parseInt(summary.getMRR().trim()));
		Assert.assertEquals(users + "", summary.getUsers().trim());
		Assert.assertEquals(
				(Integer.parseInt(rnlData.get("otr")) + Integer.parseInt(nbData
						.get("otr"))) + "", summary.getOTR().trim());
		// Assert.assertEquals(calcARPU(asv, users),
		// Integer.parseInt(summary.getARPU().trim()));
		Assert.assertTrue(summary.getOCD().contains(currentDate));
		Assert.assertTrue(summary.getRD().contains(
				AmountsAndDatesUtil.getFormattedDate(rnlData.get("endDate"), 1)));
		Assert.assertTrue(fnPosition > rtPosition,
				"Verify the timeline position of renewal transaction");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT8")
	public void testTransactionFlow(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> snbData = getMapFromData(testData
				.get("SecondNewBusinessTRN"));
		HashMap<String, String> dbData = getMapFromData(testData
				.get("DebookTRN"));
		String customerName = nbData.get("customerName");
		String transactionValues = customerName + "|" + nbData.get("startDate")
				+ "|" + nbData.get("endDate") + "|"
				+ currencyFormat(nbData.get("asv"));
		TransactionsPage transactionsPage = basepage.clickOnTransactionTab()
				.clickOnTransactionsSubTab().selectView(STANDARD_VIEW).addNewBusiness(nbData);
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,
				transactionValues),
				"Verify that newly added transaction present in the grid");
		transactionsPage = transactionsPage.addNewBusiness(snbData);
		transactionValues = customerName + "|" + snbData.get("startDate") + "|"
				+ snbData.get("endDate") + "|"
				+ currencyFormat(snbData.get("asv"));
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,
				transactionValues),
				"Verify that newly added transaction present in the grid");
		Customer360Page c360Page = transactionsPage
				.gotoCustomer360(customerName);
		CustomerSummary cSummary = c360Page.getSummaryDetails();
		Assert.assertTrue(cSummary.getRD().contains(
				AmountsAndDatesUtil.getFormattedDate(snbData.get("endDate"), 1)));
		c360Page = c360Page.addDebookTransaction(dbData);
		c360Page.refreshPage();
		cSummary = c360Page.getSummaryDetails();
		HashMap<String, String> expData = getMapFromData(testData
				.get("ExpectedSummary"));
		Assert.assertEquals(expData.get("asv").trim(), cSummary.getASV().trim());
		Assert.assertEquals(expData.get("mrr"), cSummary.getMRR().trim());
		Assert.assertEquals(expData.get("users"), cSummary.getUsers().trim());
		Assert.assertEquals(expData.get("otr"), cSummary.getOTR().trim());
		//Assert.assertEquals(expData.get("arpu"), cSummary.getARPU().trim());
		Assert.assertTrue(cSummary.getRD().contains(expData.get("renewalDate")));
		TimeLineItem lineItem = new TimeLineItem();
		lineItem.setBookingDate(currentDate);
		lineItem.setType("Debook");
		lineItem.setMRR(dbData.get("asv"));
		lineItem.setUsers(dbData.get("users"));
		lineItem.setOTR(dbData.get("otr"));
		Assert.assertTrue(c360Page.isTransactionPresent(lineItem));
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT9")
	public void testStatusAfterChurn(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> chData = getMapFromData(testData
				.get("ChurnTRN"));
		String customerName = chData.get("customerName");
		String values = customerName + "|" + chData.get("reason");
		Customer360Page customer360Page = addNewBusinessTransaction(testData);
		ChurnPage churnPage = customer360Page.clickOnChurnTab().selectView(STANDARD_VIEW).selectView(STANDARD_VIEW)
				.addChurnTransaction(chData);
		Assert.assertTrue(churnPage.isTransactionPresent(customerName, values));
		customer360Page = churnPage.gotoCustomer360(customerName);
		CustomerSummary customerSummary = customer360Page.getSummaryDetails();
		Assert.assertEquals("0", customerSummary.getASV().trim());
		Assert.assertEquals("0", customerSummary.getMRR().trim());
		Assert.assertEquals(nbData.get("otr").trim(), customerSummary.getOTR()
				.trim());
		//Assert.assertEquals("0", customerSummary.getLifetime().trim());
		Assert.assertTrue(customerSummary.getStatus().contains("Churn"));
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT6")
	public void testChurnTransaction(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> chData = getMapFromData(testData
				.get("ChurnTRN"));
		String customerName = chData.get("customerName");
		String values = customerName + "|" + chData.get("reason");
		Customer360Page customer360Page = addNewBusinessTransaction(testData);
		ChurnPage churnPage = customer360Page.clickOnChurnTab().selectView(STANDARD_VIEW)
				.addChurnTransaction(chData);
		Assert.assertTrue(churnPage.isTransactionPresent(customerName, values));
		CustomerSummary customerSummary = churnPage.gotoCustomer360(
				customerName).getSummaryDetails();
		Assert.assertEquals("0", customerSummary.getASV().trim());
		Assert.assertEquals("0", customerSummary.getMRR().trim());
		Assert.assertEquals(nbData.get("otr").trim(), customerSummary.getOTR()
				.trim());
		//Assert.assertEquals("0", customerSummary.getLifetime().trim());
		Assert.assertTrue(customerSummary.getStatus().contains("Churn"));
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT4")
	public void testC360Operations(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> churnData = getMapFromData(testData
				.get("ChurnTRN"));
		Customer360Page customer360Page = addNewBusinessTransaction(testData);
		TimeLineItem lineItem = new TimeLineItem();
		lineItem.setBookingDate(currentDate);
		lineItem.setType("New Business");
		lineItem.setMRR(nbData.get("asv"));
		lineItem.setUsers(nbData.get("users"));
		lineItem.setOTR(nbData.get("otr"));
		Assert.assertTrue(customer360Page.isTransactionPresent(lineItem));
		customer360Page = customer360Page.addChurnTransaction(churnData);
		TimeLineItem churnItem = new TimeLineItem();
		churnItem.setType("Churn");
		churnItem.setBookingDate(currentDate);
		Assert.assertTrue(customer360Page.isTransactionPresent(churnItem));
		CustomerSummary cSummary = customer360Page.getSummaryDetails();
		Assert.assertEquals(cSummary.getASV().trim(), "0");
		Assert.assertEquals(cSummary.getMRR().trim(), "0");
	//	Assert.assertEquals(cSummary.getARPU().trim(), "0");
		Assert.assertTrue(cSummary.getStatus().contains("Churn"),
				"verify customer status is churn");
	}

	// @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class,
	// dataProvider = "excel")
	// @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT5")
	public void testAddCustomerFromAccPage(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		String accName = testData.get("AccName");
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		String accID = getAccID(accName);
		CustomerSuccessPage csPage = basepage.clickOnAccountsTab()
				.selectAccount(accID).getCustomerSuccessSection();
		csPage.verifyTextPresent(testData.get("AddCustomerMessage"));
		csPage.clickOnAddCustomer();
		CustomersPage cPage = csPage.clickOnCustomersTab()
				.clickOnCustomersSubTab();
		Assert.assertTrue(cPage.isCustomerPresent(accName));
		csPage = cPage.clickOnAccountsTab().selectAccount(accID)
				.getCustomerSuccessSection();
		csPage.selectTransactionSection().clickTransactionNew()
				.addNewBusiness(nbData);
		TimeLineItem lineItem = new TimeLineItem();
		lineItem.setBookingDate(currentDate);
		lineItem.setType("New Business");
		lineItem.setMRR(nbData.get("mrr"));
		lineItem.setUsers(nbData.get("users"));
		lineItem.setOTR(nbData.get("otr"));
		Assert.assertTrue(csPage.isLineItemPresent(lineItem));
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT32")
	public void testAddBookingTypeDebook(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		testCustomBookingType(testData);
	}

    /*
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT33")
	public void testAddBookingTypeUpsell(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		testCustomBookingType(testData);
	}
    */
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT34")
	public void testAddBookingTypeDownsell(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		testCustomBookingType(testData);
	}

	@AfterClass
	public void tearDown() {
		if (loggedIn) {
			basepage.beInMainWindow();
			basepage.logout();
		}
	}

	private Customer360Page addNewBusinessTransaction(
			HashMap<String, String> testData) throws ParseException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		
		String customerName = nbData.get("customerName");
		String transactionValues = customerName + "|" + nbData.get("startDate")
				+ "|" + nbData.get("endDate") + "|"
				+ currencyFormat(nbData.get("asv"));
		TransactionsPage transactionsPage = basepage.clickOnTransactionTab()
				.clickOnTransactionsSubTab().selectView(STANDARD_VIEW).addNewBusiness(nbData);
		Report.logInfo("Transaction Values : " + transactionValues);
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,
				transactionValues),
				"Verify that newly added transaction present in the grid");
		Customer360Page customer360Page = transactionsPage
				.gotoCustomer360(customerName);
		CustomerSummary summary = customer360Page.getSummaryDetails();
		Report.logInfo("Customer Summary:\n" + summary.toString());
		validateSummary(nbData, summary);
		return customer360Page;
	}

	private void validateSummary(HashMap<String, String> data,
			CustomerSummary summary) throws ParseException {
		int asv = Integer.parseInt(data.get("asv").trim());
		int users = Integer.parseInt(data.get("userCount").trim());
        if(asv >= 100000) {
            double s = (double)asv/1000;
            data.put("asv", String.valueOf((int)Math.round(s))+"K");
        }
		Assert.assertEquals(data.get("asv").trim(), summary.getASV().trim());
		Assert.assertEquals(calcMRR(asv),
				Integer.parseInt(summary.getMRR().trim()));
		Assert.assertEquals(users + "", summary.getUsers().trim());
		Assert.assertEquals(data.get("otr").trim(), summary.getOTR().trim());
		// Assert.assertEquals(calcARPU(asv, users),
		// Integer.parseInt(summary.getARPU()
		// .trim()));
		String bookingDate = data.get("bookingDate");

		if (data.get("bookingDate") == null) {
			Assert.assertTrue(summary.getOCD().contains(currentDate));
		} else {
			Assert.assertTrue(summary.getOCD().contains(
					AmountsAndDatesUtil.getFormattedDate(bookingDate,0)));
		}
	
		Assert.assertTrue(summary.getRD().contains(
				AmountsAndDatesUtil.getFormattedDate(data.get("endDate"), 1)));
	}

	private void testCustomBookingType(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		HashMap<String, String> bTypeData = getMapFromData(testData
				.get("BookingType"));
		HashMap<String, String> tData = getMapFromData(testData
				.get("CustomTRN"));
		HashMap<String, String> eSummary = getMapFromData(testData
				.get("ExpectedSummary"));
		String bType = bTypeData.get("bookingType");
		Customer360Page c360Page = addNewBusinessTransaction(testData);
		TransactionsPage transactionsPage = c360Page.clickOnTransactionTab()
				.clickOnTransactionsSubTab().selectView(STANDARD_VIEW);
		if (bType.equals("Debook")) {
			transactionsPage.addDebookTransaction(tData);
		} else if (bType.equals("Downsell")) {
			transactionsPage.addDownsellTransaction(tData);
		} else if (bType.equals("Upsell")) {
			transactionsPage.addUpsellTransaction(tData);
		}
		CustomerSummary c360Summary = transactionsPage.gotoCustomer360(
				tData.get("customerName")).getSummaryDetails();
		Assert.assertEquals(eSummary.get("asv").trim(), c360Summary.getASV()
				.trim());
		Assert.assertEquals(eSummary.get("mrr").trim(), c360Summary.getMRR()
				.trim());
		Assert.assertEquals(eSummary.get("users").trim() + "", c360Summary
				.getUsers().trim());
		Assert.assertEquals(eSummary.get("otr").trim(), c360Summary.getOTR()
				.trim());
		//Assert.assertEquals(eSummary.get("arpu").trim(), c360Summary.getARPU().trim());
		String ocDate = eSummary.get("ocDate");
		if (ocDate == null) {
			Assert.assertTrue(c360Summary.getOCD().contains(currentDate));
		} else {
			
			Assert.assertTrue(c360Summary.getOCD().contains(
					AmountsAndDatesUtil.getFormattedDate(ocDate,0)));
		}
		Assert.assertTrue(c360Summary.getRD().contains(
				eSummary.get("renewalDate")));
	}

	private String getAccID(String name) {
		SObject[] records = soql.getRecords(String.format(accQuery, name));
		return records[0].getId();
	}

	
}
