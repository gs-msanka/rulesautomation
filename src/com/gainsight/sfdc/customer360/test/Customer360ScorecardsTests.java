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

public class Customer360ScorecardsTests extends BaseTest {

	Customer360Page cp;
	Customer360Scorecard cs;
	final String TEST_DATA_FILE = "testdata/sfdc/Scorecard/ScorecardTests.xls";

	public enum Scheme {
		NUMERIC, GRADE, COLOR
	};

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Customer 360 Scorecard module Test Cases...");
		System.out
				.println("Starting Customer 360 Scorecard module Test Cases...");
		
		  apex.runApexCodeFromFile("apex_scripts/Scorecard/scorecard.apex",
		  isPackageInstance());
		 
		basepage.login();
		cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Via Systems", true);
		cs = (Customer360Scorecard) cp.goToSection("Scorecard");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num_Add")
	public void verifyScoreWithNumeric(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("Score"), "Numeric", true); //3rd parameter sould be true we are adding score first time,false if modifying existing score
			}
		}
		int actualOverallScore = Integer.parseInt(getMapFromData(
				testData.get("OverallScore")).get("Score"));
		Assert.assertTrue(cs.verifyOverallTrend("Up"),"Overall trend is Up , since we are adding scores for first time");
		Assert.assertEquals(actualOverallScore, cs.getOverallScore(),
				"Overall Score Correct for NUMERIC scheme");
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 2 , dependsOnMethods ="verifyScoreWithNumeric")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num_Edit")
	public void editNumericScores(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		
		int actualOverallScore = Integer.parseInt(getMapFromData(
				testData.get("OverallScore")).get("Score"));
		int currentScore=cs.getOverallScore();
		String Trend;
		if(currentScore<actualOverallScore) Trend="Up";
		else if (currentScore>actualOverallScore) Trend="Down";
		else Trend="Same";
		
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("Score"), "Numeric", false); 
			}
		}
		Assert.assertTrue(cs.verifyOverallTrend(Trend),"Overall trend is correct for the numeric scheme");
		Assert.assertEquals(actualOverallScore, cs.getOverallScore(),
				"Overall Score Correct for NUMERIC scheme");
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Grade_Add")
	public void verifyScoreWithGrade(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("Score"), "Grade", true); // 3rd parameter sould be true we are adding score first time,false if modifying existing score
			}
		}
		int actualOverallScore = Integer.parseInt(getMapFromData(
				testData.get("OverallScore")).get("Score"));
		Assert.assertEquals(actualOverallScore, cs.getOverallScore(),
				"Overall Score Correct for GRADE scheme");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Add")
	public void verifyScoreWithColor(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("Score"), "Color", true); // 3rd parameter sould be true we are adding score first time,false if modifying existing score
			}
		}
		int actualOverallScore = Integer.parseInt(getMapFromData(
				testData.get("OverallScore")).get("Score"));
		Assert.assertEquals(actualOverallScore, cs.getOverallScore(),
				"Overall Score Correct for COLOR scheme");
	}

	

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Grade_Edit")
	public void editGradeScores(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("Score"), "Grade", false); 
			}
		}
		int actualOverallScore = Integer.parseInt(getMapFromData(
				testData.get("OverallScore")).get("Score"));
		Assert.assertEquals(actualOverallScore, cs.getOverallScore(),
				"Overall Score Correct for GRADE scheme");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void editColorScores(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("Score"), "Color", false); // 3rd parameter sould be true we are adding score first time,false if modifying existing score
			}
		}
		int actualOverallScore = Integer.parseInt(getMapFromData(
				testData.get("OverallScore")).get("Score"));
		Assert.assertEquals(actualOverallScore, cs.getOverallScore(),
				"Overall Score Correct for COLOR scheme");
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 3, dependsOnMethods="verifyScoreWithNumeric")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num_Add")
	public void testAddCommentsToMeasure(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrEditCommentsForMeasure(MeasureDetails.get("Comments"),
						MeasuresForGroup.get("Measure" + (m + 1)));
				Assert.assertEquals(MeasureDetails.get("Comments"), cs
						.verifyCommentForMeasure(MeasuresForGroup.get("Measure"
								+ (m + 1))));
			}
		}
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 4, dependsOnMethods="testAddCommentsToMeasure")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num_Edit" )
	public void testEditCommentsToMeasure(HashMap<String, String> testData) {
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				cs.addOrEditCommentsForMeasure(MeasureDetails.get("Comments"),
						MeasuresForGroup.get("Measure" + (m + 1)));
				Assert.assertEquals(MeasureDetails.get("EditedComment"), cs
						.verifyCommentForMeasure(MeasuresForGroup.get("Measure"
								+ (m + 1))));
			}
		}
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void addOverallSummary(HashMap<String, String> testData) {
		String actualSummary = getMapFromData(testData.get("Summary")).get(
				"Comment");
		cs.addOrEditOverallSummary(actualSummary, true);
		Assert.assertEquals(actualSummary, cs.getOverallSummary());
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void editOverallSummary(HashMap<String, String> testData) {
		String actualSummary = getMapFromData(testData.get("Summary")).get(
				"Comment");
		cs.addOrEditOverallSummary(actualSummary, false);
		Assert.assertEquals(actualSummary, cs.getOverallSummary());
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void addCustomerGoals(HashMap<String, String> testData) {
		String actualGoals = getMapFromData(testData.get("Goals")).get(
				"Comment");
		cs.addOrEditCustomerGoals(actualGoals, true);
		Assert.assertEquals(actualGoals, cs.getCustomerGoals());
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color_Edit")
	public void editCustomerGoals(HashMap<String, String> testData) {
		String actualGoals = getMapFromData(testData.get("Goals")).get(
				"Comment");
		cs.addOrEditCustomerGoals(actualGoals, false);
		Assert.assertEquals(actualGoals, cs.getCustomerGoals());
	}

	@AfterClass
	public void tearDown() {
		// basepage.logout();
	}

}
