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
	final String CURRENT_DIR=env.basedir;

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Customer 360 Features module Test Cases...");
		System.out
				.println("Starting Customer 360 Features module Test Cases...");
		apex.runApexCodeFromFile(CURRENT_DIR +
				"/apex_scripts/Features/features.apex",
				isPackageInstance());
		basepage.login();
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Via Systems", true);
		cf = (Customer360Features) cp.goToFeaturesSection();
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
	public void verifyDataFromExcel(HashMap<String, String> testData) {
		// Test if all the features in the excel are displayed (pre added features)
		HashMap<String, String> ProdList = getMapFromData(testData.get("Products"));
		System.out.println("Prodlistsize=" + ProdList.size());
		List<HashMap<String, String>> ProdFeatureList = new ArrayList();

		HashMap<String, String> tableHeaders = getMapFromData(testData.get("Headers"));

		// Verifying table header

		if (cf.isHeaderPresent()) {
			System.out.println("no of columns=" + tableHeaders.size());
			for (int h = 1; h <= tableHeaders.size(); h++) {
				System.out.println("Checking for---"+ tableHeaders.get("Column" + h));
				System.out.println(cf.FEATURES_TABLE_HEADER+ "/thead/tr/th[text()='"+ tableHeaders.get("Column" + h) + "']");
				cf.isElementPresent(By.xpath(cf.FEATURES_TABLE_HEADER+ "/thead/tr/th[text()='"+ tableHeaders.get("Column" + h) + "']"));
			}
		}
		// Verifying table data
		if (cf.isDataGridPresent()) {
		for (int i = 1; i <= ProdList.size(); i++) {

			ProdFeatureList = getMapFromDataList(testData.get(ProdList.get("Product" + i)));
			int No_of_Features=ProdFeatureList.size();
			int rowspan = 0;
			for(int j=0;j<No_of_Features;j++)
			{
				if(ProdFeatureList.get(j).get("Edit").equals("No")) rowspan++;
			}
			
			for (int f = 0; f < rowspan; f++) {
				if(ProdFeatureList.get(f).get("Edit").equals("No"))
				{
            	 if(f==0)
			      Assert.assertTrue(cf.checkFeatureRow(ProdList.get("Product"+i),ProdFeatureList.get(f).get("Feature"),ProdFeatureList.get(f).get("Licensed"),ProdFeatureList.get(f).get("Enabled"),ProdFeatureList.get(f).get("Comments"),rowspan,true));
            	 else
   			      Assert.assertTrue(cf.checkFeatureRow(ProdList.get("Product"+i),ProdFeatureList.get(f).get("Feature"),ProdFeatureList.get(f).get("Licensed"),ProdFeatureList.get(f).get("Enabled"),ProdFeatureList.get(f).get("Comments"),rowspan,false));
            	}
			}
		}
	 }
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
	public void verifyEditFeatures(HashMap<String, String> testData) {
		//In the Edit features form check on Licensed for a Feature and verify if same is reflected in features module
		HashMap<String, String> ProdList = getMapFromData(testData.get("Products"));
		
		String ProdName="";
		List<HashMap<String, String>> ProdFeatureList = new ArrayList();	
		if (cf.isDataGridPresent()) {
		for (int i = 1; i <= ProdList.size(); i++) {
			ProdName=ProdList.get("Product"+i);
			ProdFeatureList = getMapFromDataList(testData.get(ProdName));
			int rowspan = ProdFeatureList.size();
			cf.clickOnEditFeatures();

			for (int f = 0; f < rowspan; f++) {
				if(ProdFeatureList.get(f).get("Edit").equals("Yes"))
				{
					
					if(ProdFeatureList.get(f).get("Licensed").equals("Yes"))
         				cf.selectLicensed(ProdName,ProdFeatureList.get(f).get("Feature"));
         			if(ProdFeatureList.get(f).get("Enabled").equals("Yes"))
         				cf.selectEnabled(ProdName,ProdFeatureList.get(f).get("Feature"));
         			cf.addComments(ProdName,ProdFeatureList.get(f).get("Feature"),ProdFeatureList.get(f).get("Comments"));
				}
			}
 			cf.clickOnSave();
 			boolean c1=false,c2=false;
			for (int f = 0; f < rowspan; f++) {
				
         			if(!c1||cf.checkFeatureRow(ProdList.get("Product"+i),ProdFeatureList.get(f).get("Feature"),ProdFeatureList.get(f).get("Licensed"),ProdFeatureList.get(f).get("Enabled"),ProdFeatureList.get(f).get("Comments"),rowspan,true))
         					c1=true;
         			else if(cf.checkFeatureRow(ProdList.get("Product"+i),ProdFeatureList.get(f).get("Feature"),ProdFeatureList.get(f).get("Licensed"),ProdFeatureList.get(f).get("Enabled"),ProdFeatureList.get(f).get("Comments"),rowspan,false))
         				c2=true;
         			Assert.assertTrue(c1||c2,"verified that the newly added product exists in the features table");
            	 }
			}
		}
	 }
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
}
