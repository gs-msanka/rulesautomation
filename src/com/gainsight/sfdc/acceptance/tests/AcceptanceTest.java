package com.gainsight.sfdc.acceptance.tests;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.customer.pojo.CustomerSummary;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.transactions.pages.TransactionsPage;

public class AcceptanceTest extends BaseTest {

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Acceptance Test Case...");
		basepage.login();
	}

	/* Test to verify account is added as a customer in Gainsight */
	@DataProvider(name = "addCustomerDataProvider")
	public Object[][] getTestData1() throws FileNotFoundException {
		String[] dirs = { "acceptancetests" };
		testDataLoader.addDataLocation(TEST_DATA_PATH_PREFIX
				+ generatePath(dirs) + "addCustomer.csv");
		return testDataLoader.getAllDataRows();
	}

	@Test(dataProvider = "addCustomerDataProvider")
	public void testAddNewCustomer(HashMap<String, String> testData) {
		addCustomer(testData.get("customer"));
	}

	// Add Customer & Transaction.
	@DataProvider(name = "addCustomerAndTransactionDataProvider")
	public Object[][] getTestData2() throws FileNotFoundException {
		String[] dirs = { "acceptancetests" };
		testDataLoader.addDataLocation(TEST_DATA_PATH_PREFIX
				+ generatePath(dirs) + "addCustomerAndTransaction.csv");
		return testDataLoader.getAllDataRows();
	}

	@Test(dataProvider = "addCustomerAndTransactionDataProvider")
	public void testAddNewCustomerAndTransaction(
			HashMap<String, String> testData) throws ParseException {
		// data
		String[] dataArray = getArrayFromData(testData.get("firstTRN"));
		String customerName = dataArray[0];
		String startDate = dataArray[4];
		String endDate = dataArray[5];
		String asv = dataArray[7];
		String userCount = dataArray[8];
		String transactionValues = customerName + "|" + startDate + "|"
				+ endDate + "|" + currencyFormat(asv);
		// flow
		addCustomer(testData.get("customer"));
		TransactionsPage transactionsPage = basepage.clickOnTransactionTab()
				.clickOnTransactionsSubTab().addNewBusiness(dataArray);
		Report.logInfo("Transaction Values : " + transactionValues);
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,
				transactionValues),
				"Verify that newly added transaction present in the grid");
		Customer360Page customerPage = transactionsPage
				.selectCustomer(customerName);
		CustomerSummary summary = customerPage.getSummaryDetails();
		Report.logInfo("Customer Summary:\n" + summary.toString());
		int ASV = Integer.parseInt(asv.trim());
		int users = Integer.parseInt(userCount.trim());
		Assert.assertEquals(testData.get("asv").trim(), summary.getASV().trim());
		Assert.assertTrue(Math.ceil(ASV / 12.0) == new Double(summary.getMRR()
				.trim()));
		Assert.assertEquals(users + "", summary.getUsers().trim());
		Assert.assertEquals(testData.get("otr").trim(), summary.getOTR().trim());
		// Assert.assertEquals(Math.ceil((ASV/12.0)/users),new
		// Double(summary.getARPU().trim()));
		Assert.assertTrue(summary.getOCD().contains(getCurrentDate()));
		Assert.assertTrue(summary.getRD().contains(
				getFormattedDate(testData.get("enddate"), 1)));

	}

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	private CustomersPage addCustomer(String testData) {
		String[] dataArray = getArrayFromData(testData);
		String customerName = dataArray[0];
		String status = dataArray[1];
		String stage = dataArray[2];
		String comments = dataArray[3];
		CustomersPage customersPage = basepage.clickOnCustomersTab()
				.clickOnCustomersSubTab()
				.addCustomer(customerName, status, stage, comments);
		Assert.assertTrue(customersPage.isCustomerPresent(customerName),
				"Verify that newly added customer present in the grid");
		return customersPage;
	}
}
