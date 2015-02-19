package com.gainsight.sfdc.sfWidgets.accWidget.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.administration.pages.AdminFeaturesSubTab;
import com.gainsight.sfdc.customer360.pages.Customer360Features;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360SummaryWidget;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccWidget_CockpitPage;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccWidget_FeaturesPage;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccountPage;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccountWidgetPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

public class AccWidget_FeaturesTests extends BaseTest{

	
	final static String  TEST_DATA_FILE          =  "testdata/sfdc/AccountWidget/tests/FeaturesTests.xls";
   final String CREATE_FEATURES_SCRIPT        = Application.basedir+"/testdata/sfdc/AccountWidget/Scripts/features.apex";
	@BeforeClass
	public void setUp() {
		Log.info("Starting  Test Case...");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_FEATURES_SCRIPT));
		basepage.login();
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
    public void createFeatures(HashMap<String, String> testData){
		String AccId=sfdc.getRecords("select id from Account where name='Features Test Account'")[0].getId();
		AccountPage accPage = basepage.gotoAccountPageWithId(AccId);
		AccWidget_FeaturesPage accFutrePage=accPage.switchToAccountWidget().selectFeaturesSubTab();
		     
			//Customer360Features cf = basepage.clickOnC360Tab().searchCustomer("Features Test Account", false, false).goToFeaturesSection();
			HashMap<String, String> prodList = getMapFromData(testData.get("Products"));
			Log.info("ProdListSize=" + prodList.size());
			List<HashMap<String, String>> productFeatureList = new ArrayList();
			HashMap<String, String> tableHeaders = getMapFromData(testData.get("Headers"));
			// Verifying table header
			if (accFutrePage.isHeaderPresent()) {
				System.out.println("no of columns=" + tableHeaders.size());
				for (int h = 1; h <= tableHeaders.size(); h++) {
					Log.info("Checking for---"+ tableHeaders.get("Column" + h));
					Assert.assertTrue(accFutrePage.isHeaderColumnPresent(tableHeaders.get("Column" + h)));
				}
			} else {
	            Assert.assertFalse(false, "Header is not displayed");
	        }

	        for(String product : prodList.keySet()) {
	            productFeatureList = getMapFromDataList(testData.get(prodList.get(product)));
	            for(HashMap<String, String> data : productFeatureList) {
	                if(data.get("Edit").equalsIgnoreCase("No")) {
	                    Assert.assertTrue(accFutrePage.checkFeatureRow(prodList.get(product), data.get("Feature"), data.get("Licensed"), data.get("Enabled"), data.get("Comments")));
	                }
	            }
	        }
		}

		/*@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
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
		 }*/
		
	
	
	
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

	
	
}
