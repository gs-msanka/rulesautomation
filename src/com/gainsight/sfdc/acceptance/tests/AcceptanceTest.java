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

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Acceptance Test Case...");
		basepage.login();
	}
    @Test
	public void testAddNewCustomer() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AcceptanceTests.xls", "AT1");
		addCustomer(testData.get("customer"));
	}
    @Test
	public void testAddNewCustomerAndTransaction() throws ParseException, BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AcceptanceTests.xls", "AT2");
		HashMap<String, String> data = getMapFromData(testData.get("firstTRN"));
		String customerName = data.get("customerName");
		String startDate =data.get("startDate");
		String endDate = data.get("endDate");
		String asv = data.get("asv");
		String userCount =data.get("userCount");
		String transactionValues = customerName + "|" + startDate + "|"
				+ endDate + "|" + currencyFormat(asv);
		// flow
		addCustomer(testData.get("customer"));
		TransactionsPage transactionsPage = basepage.clickOnTransactionTab()
				.clickOnTransactionsSubTab().addNewBusiness(data);
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
		Assert.assertEquals(data.get("asv").trim(), summary.getASV().trim());
		Assert.assertTrue(Math.ceil(ASV / 12.0) == new Double(summary.getMRR()
				.trim()));
		Assert.assertEquals(users + "", summary.getUsers().trim());
		Assert.assertEquals(data.get("otr").trim(), summary.getOTR().trim());
		// Assert.assertEquals(Math.ceil((ASV/12.0)/users),new
		// Double(summary.getARPU().trim()));
		Assert.assertTrue(summary.getOCD().contains(getCurrentDate()));
		Assert.assertTrue(summary.getRD().contains(
				getFormattedDate(data.get("enddate"), 1)));
		
	}

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	private CustomersPage addCustomer(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String customerName = data.get("customerName");
		String status = data.get("status");
		String stage =  data.get("stage");
		String comments = data.get("comments");
		CustomersPage customersPage = basepage.clickOnCustomersTab()
				.clickOnCustomersSubTab()
				.addCustomer(customerName, status, stage, comments);
		Assert.assertTrue(customersPage.isCustomerPresent(customerName),
				"Verify that newly added customer present in the grid");
		
		return customersPage;
	}
}
