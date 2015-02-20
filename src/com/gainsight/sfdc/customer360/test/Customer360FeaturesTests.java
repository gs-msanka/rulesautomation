package com.gainsight.sfdc.customer360.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.customer360.pages.Customer360Features;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class Customer360FeaturesTests extends BaseTest {

	private final String TEST_DATA_FILE = "testdata/sfdc/feature/tests/FeaturesTests.xls";

	@BeforeClass
	public void setUp() {
		Log.info("Starting Customer 360 Features module Test Cases...");
        basepage.login();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(env.basedir+"/apex_scripts/Features/features.apex"));
	}

    // Test if all the features in the excel are displayed (pre added features)
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
	public void verifyDataFromExcel(HashMap<String, String> testData) {
        
		Customer360Features cf = basepage.clickOnC360Tab().searchCustomer("Features Test Account", false, false).goToFeaturesSection();
		HashMap<String, String> prodList = getMapFromData(testData.get("Products"));
		Log.info("ProdListSize=" + prodList.size());
		List<HashMap<String, String>> productFeatureList = new ArrayList();
		HashMap<String, String> tableHeaders = getMapFromData(testData.get("Headers"));
		// Verifying table header
		if (cf.isHeaderPresent()) {
			System.out.println("no of columns=" + tableHeaders.size());
			for (int h = 1; h <= tableHeaders.size(); h++) {
				Log.info("Checking for---"+ tableHeaders.get("Column" + h));
				Assert.assertTrue(cf.isHeaderColumnPresent(tableHeaders.get("Column" + h)));
			}
		} else {
            Assert.assertFalse(false, "Header is not displayed");
        }

        for(String product : prodList.keySet()) {
            productFeatureList = getMapFromDataList(testData.get(prodList.get(product)));
            for(HashMap<String, String> data : productFeatureList) {
                if(data.get("Edit").equalsIgnoreCase("No")) {
                    Assert.assertTrue(cf.checkFeatureRow(prodList.get(product), data.get("Feature"), data.get("Licensed"), data.get("Enabled"), data.get("Comments")));
                }
            }
        }
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
	public void verifyEditFeatures(HashMap<String, String> testData) {
        Customer360Features cf = basepage.clickOnC360Tab().searchCustomer("Features Test Account", false, false).goToFeaturesSection();
		//In the Edit features form check on Licensed for a Feature and verify if same is reflected in features module
		HashMap<String, String> prodList = getMapFromData(testData.get("Products"));
		List<HashMap<String, String>> prodFeatureList = new ArrayList();
		if (cf.isDataGridPresent()) {
            for (String prodName : prodList.keySet()) {
                prodFeatureList = getMapFromDataList(testData.get(prodList.get(prodName)));
                cf.clickOnEditFeatures();

                for(HashMap<String, String> data : prodFeatureList) {
                    if(data.get("Edit").equals("Yes")) {
                        if(data.get("Licensed").equals("Yes"))
                            cf.selectLicensed(prodList.get(prodName),data.get("Feature"));
                        if(data.get("Enabled").equals("Yes"))
                            cf.selectEnabled(prodList.get(prodName),data.get("Feature"));
                        cf.addComments(prodList.get(prodName), data.get("Feature"), data.get("Comments"));
                    }
                }
                cf = cf.clickOnSave();
                for(HashMap<String, String> data : prodFeatureList) {
                    if(data.get("Edit").equals("Yes")) {
                        Assert.assertTrue(cf.checkFeatureRow(prodList.get(prodName), data.get("Feature"), data.get("Licensed"), data.get("Enabled"), data.get("Comments")));
                    }

                }
            }
		}  else {
            Assert.assertTrue(false, "Data grid is not displayed.");
        }
	 }
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
}
