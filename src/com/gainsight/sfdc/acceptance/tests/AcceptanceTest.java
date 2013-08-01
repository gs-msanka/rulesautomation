package com.gainsight.sfdc.acceptance.tests;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.customer.pages.CustomersPage;
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
		testDataLoader.addDataLocation(TestEnvironment.basedir + TEST_DATA_PATH_PREFIX
				+ generatePath(dirs) + "addCustomer.csv");
		return testDataLoader.getAllDataRows();
	}

	@Test(dataProvider = "addCustomerDataProvider")
	public void testAddNewCustomer(HashMap<String, String> testData) {
		addCustomer(testData.get("customerName"), testData.get("status"),
				testData.get("stage"), testData.get("comments"));
	}

	// Add Customer & Transaction.
	@DataProvider(name = "addCustomerAndTransactionDataProvider")
	public Object[][] getTestData2() throws FileNotFoundException {
		String[] dirs = { "acceptancetests" };
		testDataLoader.addDataLocation(TestEnvironment.basedir + TEST_DATA_PATH_PREFIX
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
		
		/*addCustomer(testData.get("customername"), testData.get("status"),
				testData.get("stage"), testData.get("comments"));*/
		TransactionsPage transactionsPage = basepage.clickOnTransactionTab()
				.clickOnTransactionsSubTab().addTransaction(testData);
		Timer.sleep(10);
		Report.logInfo("Transaction Values : " + transactionValues);
		
		//TransactionsPage transactionsPage = basepage.clickOnTransactionTab().clickOnTransactionsSubTab();
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,transactionValues),
				"Verify that newly added customer present in the grid");
		Customer360Page customerPage=transactionsPage.selectCustomer(customerName);
		Timer.sleep(5);
		customerPage.verifyCustomerSummary(testData);		

	}

	@AfterClass
	public void tearDown(){
		basepage.logout();
	}

	private CustomersPage addCustomer(String customerName, String status,
			String stage, String comments) {
		CustomersPage customersPage = basepage.clickOnCustomersTab()
				.clickOnCustomersSubTab()
				.addCustomer(customerName, status, stage, comments);
		Timer.sleep(10);
		Assert.assertTrue(customersPage.isCustomerPresent(customerName),
				"Verify that newly added customer present in the grid");
		return customersPage;
	}
}
