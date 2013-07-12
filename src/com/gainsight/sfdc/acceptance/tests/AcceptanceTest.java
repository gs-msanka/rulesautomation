package com.gainsight.sfdc.acceptance.tests;

import java.io.FileNotFoundException;
import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.tests.BaseTest;

public class AcceptanceTest extends BaseTest {

	@DataProvider(name = "addCustomerDataProvider")
	public Object[][] getTestData() throws FileNotFoundException {
		String[] dirs = { "acceptancetests"};
		System.out.println(TEST_DATA_PATH_PREFIX
				+ generatePath(dirs)+"addCustomer.csv");
		testDataLoader.addDataLocation(TEST_DATA_PATH_PREFIX
				+ generatePath(dirs)+"addCustomer.csv");
		return testDataLoader.getAllDataRows();
	}

	@Test(dataProvider = "addCustomerDataProvider")
	public void testAddNewCustomer(HashMap<String, String> testData) {
		CustomersPage customersPage = basepage
				.login()
				.clickOnCustomersTab()
				.clickOnCustomersSubTab()
				.addCustomer(testData.get("customerName"),
						testData.get("status"), testData.get("stage"),
						testData.get("comments"));
		Assert.assertTrue(
				customersPage.isCustomerPresent(testData.get("customerName")),
				"Verify that newly added customer present in the grid");
	}
}
