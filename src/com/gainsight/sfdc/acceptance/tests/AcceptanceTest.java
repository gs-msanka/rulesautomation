package com.gainsight.sfdc.acceptance.tests;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.transactions.pages.TransactionsPage;

public class AcceptanceTest extends BaseTest {

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
		basepage.login();
		addCustomer(testData.get("customerName"), testData.get("status"),
				testData.get("stage"), testData.get("comments"));
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
			HashMap<String, String> testData) {
		String customerName = testData.get("customername");
		String transactionValues = customerName 
				+ "|" + testData.get("startdate") + "|"
				+ testData.get("enddate")+ "|" + currencyFormat(testData.get("asv"));
		basepage.login();
		/*addCustomer(testData.get("customername"), testData.get("status"),
				testData.get("stage"), testData.get("comments"));*/
		TransactionsPage transactionsPage = basepage.clickOnTransactionTab()
				.clickOnTransactionsSubTab();//.addTransaction(testData);
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,transactionValues),
				"Verify that newly added customer present in the grid");
		Customer360Page customerPage=transactionsPage.selectCustomer(customerName);
		customerPage.verifyCustomerSummary(testData);		

	}

	private CustomersPage addCustomer(String customerName, String status,
			String stage, String comments) {
		CustomersPage customersPage = basepage.clickOnCustomersTab()
				.clickOnCustomersSubTab()
				.addCustomer(customerName, status, stage, comments);
		Assert.assertTrue(customersPage.isCustomerPresent(customerName),
				"Verify that newly added customer present in the grid");
		return customersPage;
	}
	
}
