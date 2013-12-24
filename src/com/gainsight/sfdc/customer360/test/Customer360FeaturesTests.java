package com.gainsight.sfdc.customer360.test;

import java.util.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.openqa.selenium.By;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360Features;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class Customer360FeaturesTests extends BaseTest {

	Customer360Page cp;
	Customer360Features cf;
	final String TEST_DATA_FILE = "testdata/sfdc/Features/FeaturesTests.xls";

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Customer 360 Features module Test Cases...");
		System.out
				.println("Starting Customer 360 Features module Test Cases...");
		apex.runApexCodeFromFile(
				"apex_scripts/Features/features.apex",
				isPackageInstance());
		basepage.login();
		cp = basepage.clickOnC360Tab();
		cp.gotoCustomer360("Via Systems");
		cf = (Customer360Features) cp.goToSection("Features");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
	public void verifyDataFromExcel(HashMap<String, String> testData) {
		// Test if all the features in the excel are displayed on clicking edit
		// feature
		HashMap<String, String> ProdList = getMapFromData(testData
				.get("Products"));
		System.out.println("Prodlistsize=" + ProdList.size());
		List<HashMap<String, String>> ProdFeatureList = new ArrayList();

		HashMap<String, String> tableHeaders = getMapFromData(testData
				.get("Headers"));

		// Verifying table header

		if (cf.isHeaderPresent()) {
			System.out.println("no of columns=" + tableHeaders.size());
			for (int h = 1; h <= tableHeaders.size(); h++) {
				System.out.println("Checking for---"
						+ tableHeaders.get("Column" + h));
				System.out.println(cf.FEATURES_TABLE_HEADER
						+ "/thead/tr/th[text()='"
						+ tableHeaders.get("Column" + h) + "']");
				cf.isElementPresent(By.xpath(cf.FEATURES_TABLE_HEADER
						+ "/thead/tr/th[text()='"
						+ tableHeaders.get("Column" + h) + "']"));
			}
		}

		// Verifying table data
		if (cf.isDataGridPresent()) {
		for (int i = 1; i <= ProdList.size(); i++) {

			// get data for a product from the excel
			ProdFeatureList = getMapFromDataList(testData.get(ProdList
					.get("Product" + i)));
			int rowspan = 0;
			// if the no.of features is >1 then check if rowspan = no.of
			// features for cell containing the product name

			if (ProdFeatureList.size() > 1) {
				rowspan = ProdFeatureList.size();
			}
            if(cf.checkProductWithRowspan(ProdList.get("product"+i),rowspan)){
			// and then just check if all the data is present as per the test input form the xpath and then check once per row
			for (int f = 0; f < ProdFeatureList.size(); f++) {
				cf.checkFeatureForProduct(ProdList.get("Product" + i),
						ProdFeatureList.get(f).get("eature"));
				cf.checkLicensedForProduct(ProdList.get("Product" + i),
						ProdFeatureList.get(f).get("Licensed"));
				cf.checkEnabledForProduct(ProdList.get("Product" + i),
						ProdFeatureList.get(f).get("Enabled"));
				cf.checkCommentsForProduct(ProdList.get("Product" + i),
						ProdFeatureList.get(f).get("Comments"));
			}
            }
		}
	}
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F2")
	public void verifyEditFeatures(HashMap<String, String> testData) {
		//In the Edit features form check on Licensed for a Feature and verify if same is reflected in features module
		String ProdName="P4";
		HashMap<String, String> ProdList = getMapFromData(testData.get(ProdName));
		cf.clickOnEditFeatures();
		if(ProdList.get("Licensed").equals("Yes"))
		cf.selectLicensed(ProdName,ProdList.get("Feature"));
		if(ProdList.get("Enabled").equals("Yes"))
		cf.selectEnabled(ProdName,ProdList.get("Feature"));
		cf.addComments(ProdName,ProdList.get("Feature"),ProdList.get("Comments"));
		cf.clickOnSave();
		Assert.assertTrue(cf.checkLicensedForProduct(ProdName, ProdList.get("Licensed")),
				"Verify that licensed is checked for this feature");
		Assert.assertTrue(cf.checkEnabledForProduct(ProdName, ProdList.get("Enabled")), "Verified that enabled is checked for this feature");
		Assert.assertTrue(cf.checkCommentsForProduct(ProdName,ProdList.get("Comments")), "Verified that comment is edited correctly");
	}

	/*@Test
	public void verifyEnablingEnabled() {
		cf.clickOnEditFeatures();
		cf.selectEnabled();
		cf.clickOnSave();
		Assert.assertTrue(cf.isLicenseChecked(),
				"Verify that licensed is checked for this feature");
	}

	@Test
	public void verifyEnablingLicensedAndEnabled() {
		cf.clickOnEditFeatures();
		cf.selectEnabled();
		cf.selectLicensed();
		cf.clickOnSave();
		Assert.assertTrue(cf.isLicenseChecked(),
				"Verify that licensed is checked for this feature");
	}

	@Test
	public void verifyComments() {

		cf.clickOnEditFeatures();
		cf.addComments("Sample Comments");
		cf.clickOnSave();
		Assert.assertTrue(cf.isCommentPresent("Sample Comments"),
				"Verify that licensed is checked for this feature");
	}
*/
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
}
