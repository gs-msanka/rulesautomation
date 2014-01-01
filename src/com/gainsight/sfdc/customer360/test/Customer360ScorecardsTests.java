package com.gainsight.sfdc.customer360.test;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360Scorecard;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class Customer360ScorecardsTests  extends BaseTest{

	Customer360Page cp;
	Customer360Scorecard cs;
	final String TEST_DATA_FILE = "testdata/sfdc/Scorecard/ScorecardTests.xls";
	public enum Scheme {NUMERIC,GRADE,COLOR};

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Customer 360 Scorecard module Test Cases...");
		System.out
				.println("Starting Customer 360 Scorecard module Test Cases...");
		/*apex.runApexCodeFromFile("apex_scripts/Scorecard/Scorecard.apex",
				isPackageInstance());*/
		basepage.login();
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Via Systems",true);
		cs = (Customer360Scorecard) cp.goToSection("Scorecard");
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num_Add")
	public void verifyScoreWithNumeric(HashMap<String, String> testData) {
		HashMap<String,String> Groups=getMapFromData(testData.get("Groups"));
		int numOfGroups=Groups.size();
		for(int g=0;g<numOfGroups;g++)
		{
			HashMap<String,String> MeasuresForGroup=getMapFromData(testData.get(Groups.get("Group"+(g+1))));
			for(int m=0;m<MeasuresForGroup.size();m++)
			{
				HashMap<String,String> MeasureDetails=getMapFromData(testData.get(MeasuresForGroup.get("Measure"+(m+1))));	
				cs.addOrModifyMeasureScore(MeasuresForGroup.get("Measure"+(m+1)),MeasureDetails.get("Score"), "Numeric",true); //3rd parameter sould be true if we are adding score first time , false if modifying existing score
			}
		}
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Grade_Add")
	public void verifyScoreWithGrade(HashMap<String, String> testData) {
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Add")
	public void verifyScoreWithColor(HashMap<String, String> testData) {
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num_Edit")
	public void editNumericScores(HashMap<String, String> testData) {
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Grade_Edit")
	public void editGradeScores(HashMap<String, String> testData) {
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void editColorScores(HashMap<String, String> testData) {
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void testMeasureComments(HashMap<String, String> testData) {
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void testOverallSummary(HashMap<String, String> testData) {
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" , priority =1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void testCustomerGoals(HashMap<String, String> testData) {
	}
	
	@AfterClass
	public void tearDown() {
		//basepage.logout();
	}

}
