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
import com.gainsight.sfdc.churn.pages.ChurnPage;
import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.customer.pojo.CustomerSummary;
import com.gainsight.sfdc.customer.pojo.TimeLineItem;
import com.gainsight.sfdc.pages.CustomerSuccessPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.transactions.pages.TransactionsPage;

public class AcceptanceTest extends BaseTest {
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);
	private boolean loggedIn = false;

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Acceptance Test Case...");
		basepage.login();
		loggedIn = true;
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
				.get("NewBusinessTRN"));
		Customer360Page customer360Page = addCustomerAndTransaction(testData);
		/* Renewal Transaction */
		HashMap<String, String> rnlData = getMapFromData(testData
				.get("RenewalTRN"));
		String customerName = rnlData.get("customerName");
		customer360Page = customer360Page.clickOnTransactionTab()
				.clickOnTransactionsSubTab().addRenewalTransaction(rnlData)
				.gotoCustomer360(customerName);
		CustomerSummary summary = customer360Page.getSummaryDetails();
		int fnPosition = customer360Page.getPositionOfTransaction(
				"New Business", getCurrentDate());
		int rtPosition = customer360Page.getPositionOfTransaction("Renewal",
				getCurrentDate());
		int asv = Integer.parseInt(rnlData.get("asv").trim());
		int users = Integer.parseInt(rnlData.get("userCount").trim());
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

	@Test
	public void testChurnTransaction() throws BiffException, IOException,
			ParseException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AcceptanceTests.xls", "AT6");
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> chData = getMapFromData(testData
				.get("ChurnTRN"));
		String customerName = chData.get("customerName");
		String values = customerName + "|" + chData.get("reason");
		Customer360Page customer360Page = addCustomerAndTransaction(testData);
		ChurnPage churnPage = customer360Page.clickOnChurnTab()
				.addChurnTransaction(chData);
		Assert.assertTrue(churnPage.isTransactionPresent(customerName, values));
		CustomerSummary customerSummary = churnPage.gotoCustomer360(
				customerName).getSummaryDetails();
		Assert.assertEquals("", customerSummary.getASV().trim());
		Assert.assertEquals("", customerSummary.getMRR().trim());
		Assert.assertEquals(nbData.get("otr").trim(), customerSummary.getOTR()
				.trim());
		Assert.assertTrue(customerSummary.getLifetime().contains("0 Months"));
	}

	@Test
	public void testOpportunityWithoutSettings() throws BiffException,
			IOException, ParseException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AcceptanceTests.xls", "AT15");
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> snbData = getMapFromData(testData
				.get("SecondNewBusinessTRN"));
		String oppName = testData.get("oppName");
		CustomerSuccessPage csPage = basepage.clickOnOpportunitiesTab()
				.selectRecentOpportunity(oppName).getCustomerSuccessSection();
		csPage.verifyTextPresent(testData.get("AddCustomerMessage"));
		csPage.clickOnAddCustomer();
		csPage.verifyTextPresent(testData.get("NewBusinessMessage"));
		csPage.clickOn360View().addNewBusinessTransaction(nbData).clickBack();
		csPage.verifyTextPresent(testData.get("NoSettingsMessage"));
		CustomerSummary cSummary = csPage.clickHere().addNewBusiness(snbData)
				.clickOn360View().getSummaryDetails();
		HashMap<String, String> expData = getMapFromData(testData
				.get("ExpectedSummary"));
		Assert.assertEquals(expData.get("asv").trim(), cSummary.getASV().trim());
		Assert.assertEquals(expData.get("mrr"), cSummary.getMRR().trim());
		Assert.assertEquals(expData.get("users"), cSummary.getUsers().trim());
		Assert.assertEquals(expData.get("otr"), cSummary.getOTR().trim());
		Assert.assertEquals(expData.get("arpu"),cSummary.getARPU().trim());
		Assert.assertTrue(cSummary.getRD().contains(expData.get("endDate")));
	}
	@Test
	public void testC360Operations() throws BiffException,
			IOException, ParseException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AcceptanceTests.xls", "AT4");
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> churnData = getMapFromData(testData
				.get("ChurnTRN"));
		Customer360Page customer360Page = addCustomerAndTransaction(testData);
		TimeLineItem lineItem=new TimeLineItem();
		lineItem.setBookingDate(getCurrentDate());
		lineItem.setType("New Business");
		lineItem.setMRR(nbData.get("asv"));
		lineItem.setUsers(nbData.get("users"));
		lineItem.setOTR(nbData.get("otr"));		
		Assert.assertTrue(customer360Page.isTransactionPresent(lineItem));
		customer360Page=customer360Page.addChurnTransaction(churnData);
		Assert.assertTrue(customer360Page.isTransactionPresent("Churn",getCurrentDate()));
		CustomerSummary cSummary = customer360Page.getSummaryDetails();
		Assert.assertEquals(cSummary.getASV().trim(), "");
		Assert.assertEquals(cSummary.getMRR().trim(),"");
		Assert.assertEquals(cSummary.getARPU().trim(),"");	
		Assert.assertTrue(cSummary.getStatus().contains("Churn"), "verify customer status is churn");
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

	private Customer360Page addCustomerAndTransaction(
			HashMap<String, String> testData) throws ParseException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		String customerName = nbData.get("customerName");
		String transactionValues = customerName + "|" + nbData.get("startDate")
				+ "|" + nbData.get("endDate") + "|"
				+ currencyFormat(nbData.get("asv"));

		CustomersPage customerPage = addCustomer(testData.get("customer"));
		TransactionsPage transactionsPage = customerPage
				.clickOnTransactionTab().clickOnTransactionsSubTab()
				.addNewBusiness(nbData);
		Report.logInfo("Transaction Values : " + transactionValues);
		Assert.assertTrue(transactionsPage.isTransactionPresent(customerName,
				transactionValues),
				"Verify that newly added transaction present in the grid");
		Customer360Page customer360Page = transactionsPage
				.gotoCustomer360(customerName);
		CustomerSummary summary = customer360Page.getSummaryDetails();
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
		return customer360Page;
	}
	
}
