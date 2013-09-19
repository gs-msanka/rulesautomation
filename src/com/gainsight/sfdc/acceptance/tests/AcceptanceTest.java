package com.gainsight.sfdc.acceptance.tests;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.customer.pojo.CustomerSummary;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.transactions.pages.TransactionsPage;

public class AcceptanceTest extends BaseTest {
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	private boolean loggedIn=false;

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Acceptance Test Case...");
		basepage.login();
		loggedIn=true;
	}

	@Test
	public void testAddNewCustomer() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AcceptanceTests.xls", "AT1");
		addCustomer(testData.get("customer"));
	}

	@Test
	public void testAddNewCustomerAndTransaction() throws ParseException,
			BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AcceptanceTests.xls", "AT2");
		HashMap<String, String> nbData = getMapFromData(testData
				.get("firstTRN"));
		String customerName = nbData.get("customerName");
		String transactionValues = customerName + "|" + nbData.get("startDate")
				+ "|" + nbData.get("endDate") + "|"
				+ currencyFormat(nbData.get("asv"));

		addCustomer(testData.get("customer"));
		TransactionsPage transactionsPage = basepage.clickOnTransactionTab()
				.clickOnTransactionsSubTab().addNewBusiness(nbData);
		Report.logInfo("Transaction Values : " + transactionValues);
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,
				transactionValues),
				"Verify that newly added transaction present in the grid");
		Customer360Page customerPage = transactionsPage
				.gotoCustomer360(customerName);
		CustomerSummary summary = customerPage.getSummaryDetails();
		Report.logInfo("Customer Summary:\n" + summary.toString());
		int asv = Integer.parseInt(nbData.get("asv").trim());
		int users = Integer.parseInt(nbData.get("userCount").trim());
		Assert.assertEquals(nbData.get("asv").trim(), summary.getASV().trim());
		Assert.assertEquals(calcMRR(asv), new Double(summary.getMRR().trim()));
		Assert.assertEquals(users + "", summary.getUsers().trim());
		Assert.assertEquals(nbData.get("otr").trim(), summary.getOTR().trim());
		// Assert.assertEquals(calcARPU(asv,users),new
		// Double(summary.getARPU().trim()));
		Assert.assertTrue(summary.getOCD().contains(getCurrentDate()));
		Assert.assertTrue(summary.getRD().contains(
				getFormattedDate(nbData.get("endDate"), 1)));
		/* Renewal Transaction */
		HashMap<String, String> rnlData = getMapFromData(testData
				.get("renewalTRN"));
		customerPage = customerPage.clickOnTransactionTab()
				.clickOnTransactionsSubTab().addRenewalTransaction(rnlData)
				.gotoCustomer360(customerName);
		summary = customerPage.getSummaryDetails();
		int fnPosition = customerPage.getPositionOfTransaction("New Business",
				getCurrentDate());
		int rtPosition = customerPage.getPositionOfTransaction("Renewal",
				getCurrentDate());
		asv = Integer.parseInt(rnlData.get("asv").trim());
		users = Integer.parseInt(rnlData.get("userCount").trim());
		Assert.assertEquals(rnlData.get("asv").trim(), summary.getASV().trim());
		Assert.assertEquals(calcMRR(asv), new Double(summary.getMRR().trim()));
		Assert.assertEquals(users + "", summary.getUsers().trim());
		Assert.assertEquals(
				(Integer.parseInt(rnlData.get("otr")) + Integer.parseInt(nbData
						.get("otr"))) + "", summary.getOTR().trim());
		// Assert.assertEquals(calcARPU(asv,users),new
		// Double(summary.getARPU().trim()));
		Assert.assertTrue(summary.getOCD().contains(getCurrentDate()));
		Assert.assertTrue(summary.getRD().contains(
				getFormattedDate(rnlData.get("endDate"), 1)));
		Assert.assertTrue(fnPosition > rtPosition,
				"Verify the timeline position of renewal transaction");

	}

	@AfterClass
	public void tearDown() {
		if (loggedIn)
		basepage.logout();
	}

	private CustomersPage addCustomer(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String customerName = data.get("customerName");
		String status = data.get("status");
		String stage = data.get("stage");
		String comments = data.get("comments");
		CustomersPage customersPage = basepage.clickOnCustomersTab()
				.clickOnCustomersSubTab()
				.addCustomer(customerName, status, stage, comments);
		Assert.assertTrue(customersPage.isCustomerPresent(customerName),
				"Verify that newly added customer present in the grid");
		return customersPage;
	}
}
