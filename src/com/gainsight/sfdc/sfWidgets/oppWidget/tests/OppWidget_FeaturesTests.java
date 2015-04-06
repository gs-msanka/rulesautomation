package com.gainsight.sfdc.sfWidgets.oppWidget.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.sfWidgets.oppWidget.pages.OppWidget_FeaturesPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

public class OppWidget_FeaturesTests extends BaseTest{

	
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
		
		SObject[] OppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='Features Test Account')");
	OppWidget_FeaturesPage oppFutrePage = basepage.gotoOpportunityPageWithId(OppId[0].getId()).switchToOppCSWidget().selectOppFeaturesSubTab();

			HashMap<String, String> prodList = getMapFromData(testData.get("Products"));
			Log.info("ProdListSize=" + prodList.size());
			List<HashMap<String, String>> productFeatureList = new ArrayList();
			HashMap<String, String> tableHeaders = getMapFromData(testData.get("Headers"));
			// Verifying table header
			if (oppFutrePage.isHeaderPresent()) {
				System.out.println("no of columns=" + tableHeaders.size());
				for (int h = 1; h <= tableHeaders.size(); h++) {
					Log.info("Checking for---"+ tableHeaders.get("Column" + h));
					Assert.assertTrue(oppFutrePage.isHeaderColumnPresent(tableHeaders.get("Column" + h)));
				}
			} else {
	            Assert.assertFalse(false, "Header is not displayed");
	        }

	        for(String product : prodList.keySet()) {
	            productFeatureList = getMapFromDataList(testData.get(prodList.get(product)));
	            for(HashMap<String, String> data : productFeatureList) {
	                if(data.get("Edit").equalsIgnoreCase("No")) {
	                    Assert.assertTrue(oppFutrePage.checkFeatureRow(prodList.get(product), data.get("Feature"), data.get("Licensed"), data.get("Enabled"), data.get("Comments")));
	                }
	            }
	        }
		}

		@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
		@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
		public void verifyEditFeatures(HashMap<String, String> testData) {
			SObject[] OppId=sfdc.getRecords("select id from Opportunity where AccountId  in (select id from Account where Name='Features Test Account')");
			       
			OppWidget_FeaturesPage oppFutrePage = basepage.gotoOpportunityPageWithId(OppId[0].getId()).switchToOppCSWidget().selectOppFeaturesSubTab();
//OpportunityPage oppPage = basepage.gotoOpportunityPageWithId(OppId);
	//OppWidget_FeaturesPage oppFutrePage=oppPage.switchToOppCSWidget().selectOppFeaturesSubTab();
			
			//In the Edit features form check on Licensed for a Feature and verify if same is reflected in features module
			HashMap<String, String> prodList = getMapFromData(testData.get("Products"));
			List<HashMap<String, String>> prodFeatureList = new ArrayList();
			if (oppFutrePage.isDataGridPresent()) {
	            for (String prodName : prodList.keySet()) {
	                prodFeatureList = getMapFromDataList(testData.get(prodList.get(prodName)));
	                oppFutrePage.clickOnEditFeatures();

	                for(HashMap<String, String> data : prodFeatureList) {
	                    if(data.get("Edit").equals("Yes")) {
	                        if(data.get("Licensed").equals("Yes"))
	                        	oppFutrePage.selectLicensed(prodList.get(prodName),data.get("Feature"));
	                        if(data.get("Enabled").equals("Yes"))
	                        	oppFutrePage.selectEnabled(prodList.get(prodName),data.get("Feature"));
	                        oppFutrePage.addComments(prodList.get(prodName), data.get("Feature"), data.get("Comments"));
	                    }
	                }
	                oppFutrePage = oppFutrePage.clickOnSave();
	                for(HashMap<String, String> data : prodFeatureList) {
	                    if(data.get("Edit").equals("Yes")) {
	                        Assert.assertTrue(oppFutrePage.checkFeatureRow(prodList.get(prodName), data.get("Feature"), data.get("Licensed"), data.get("Enabled"), data.get("Comments")));	                    }

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
