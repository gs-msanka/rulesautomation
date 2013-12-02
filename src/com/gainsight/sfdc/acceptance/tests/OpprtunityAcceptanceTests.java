package com.gainsight.sfdc.acceptance.tests;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import jxl.read.biff.BiffException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer.pojo.CustomerSummary;
import com.gainsight.sfdc.pages.CustomerSuccessPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

@Listeners({ com.gainsight.utils.GSTestListener.class })
public class OpprtunityAcceptanceTests extends BaseTest {
	String[] dirs = { "acceptancetests" };
	final String TEST_DATA_FILE = "testdata/sfdc/acceptancetests/AcceptanceTests.xls";
	private boolean loggedIn = false;

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Acceptance Test Case...");
		basepage.login();
		loggedIn = true;
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AT15")
	public void testOpportunityWithoutSettings(HashMap<String, String> testData)
			throws BiffException, IOException, ParseException {
		HashMap<String, String> nbData = getMapFromData(testData
				.get("NewBusinessTRN"));
		HashMap<String, String> snbData = getMapFromData(testData
				.get("SecondNewBusinessTRN"));
		String oppName = testData.get("OppName");
		CustomerSuccessPage csPage = basepage.clickOnOpportunitiesTab()
				.selectRecentOpportunity(oppName).getCustomerSuccessSection();
		csPage.verifyTextPresent(testData.get("AddCustomerMessage"));
		csPage.clickOnAddCustomer();
		csPage.verifyTextPresent(testData.get("NewBusinessMessage"));
		csPage.clickOn360View().addNewBusinessTransaction(nbData);
		basepage.goBack();
		csPage.verifyTextPresent(testData.get("NoSettingsMessage"));
		CustomerSummary cSummary = csPage.clickHere().addNewBusiness(snbData)
				.clickOn360View().getSummaryDetails();
		HashMap<String, String> expData = getMapFromData(testData
				.get("ExpectedSummary"));
		Assert.assertEquals(expData.get("asv").trim(), cSummary.getASV().trim());
		Assert.assertEquals(expData.get("mrr"), cSummary.getMRR().trim());
		Assert.assertEquals(expData.get("users"), cSummary.getUsers().trim());
		Assert.assertEquals(expData.get("otr"), cSummary.getOTR().trim());
		Assert.assertEquals(expData.get("arpu"), cSummary.getARPU().trim());
		Assert.assertTrue(cSummary.getRD().contains(expData.get("renewalDate")));
	}

	@AfterClass
	public void tearDown() {
		if (loggedIn) {
			basepage.beInMainWindow();
			basepage.logout();
		}
	}
}
