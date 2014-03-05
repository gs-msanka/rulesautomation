package com.gainsight.sfdc.customer360.test;

import java.util.Arrays;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasepage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360Scorecard;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import com.gainsight.utils.DataProviderArguments;

public class Customer360ScorecardsTests extends BaseTest {

	Customer360Page cp;
	Customer360Scorecard cs;
	final String TEST_DATA_FILE = "testdata/sfdc/Scorecard/ScorecardTests.xls";
	static boolean isPackageInstance = false;
	
	public enum Scheme {
		NUMERIC, GRADE, COLOR
	};
	  
	@BeforeClass
	public void setUp() {
		TestEnvironment env = new TestEnvironment();
	    isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
		Report.logInfo("Starting Customer 360 Scorecard module Test Cases...");
		System.out
				.println("Starting Customer 360 Scorecard module Test Cases...");
		CreateObjectAndFields cObjFields = new CreateObjectAndFields();
		 String Scorecard_Metrics="JBCXM__ScorecardMetric__c";
	     String[] SCMetric_ExtId=new String[]{"SCMetric ExternalID"};
	     try {
	           cObjFields.createTextFields(removeNameSpace(Scorecard_Metrics), SCMetric_ExtId, true, true, true, false, false);
	     } catch (Exception e) {       	
	           Report.logInfo("Failed to create fields");
	           e.printStackTrace();
	     }
	        
		 apex.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/scorecard.apex", isPackageInstance());
		 
		basepage.login();
		
		AdministrationBasepage adm=basepage.clickOnAdminTab();
        AdminScorecardSection as=adm.clickOnScorecardSetion();
        as.enableScorecard();
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Scorecard_enable_numeric.apex",isPackageInstance());
        apex.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Create_ScorecardMetrics.apex",isPackageInstance());
        cp = basepage.clickOnC360Tab();
		cp.searchCustomer("Scorecard Account", true);
		cs = (Customer360Scorecard) cp.goToScorecardSection();
	}
	
	  public String removeNameSpace(String s) {
	        if(!isPackageInstance) {
	            return s.replaceAll("JBCXM__", "");
	        }
	        return s;
	    }
	//@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	//@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num")
	public void addScoreWithNumeric(HashMap<String, String> testData) {
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
		Assert.assertEquals(actualOverallScore, Integer.parseInt(cs.getOverallScore()),
				"Overall Score Correct for NUMERIC scheme");
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num")
	public void addAndEditNumericScores(HashMap<String, String> testData) {
		addScoreWithNumeric(testData);
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		
		int actualOverallScore = Integer.parseInt(getMapFromData(
				testData.get("OverallScore")).get("EditedScore"));
		int currentScore=Integer.parseInt(cs.getOverallScore());
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
				if(MeasureDetails.containsKey("EditedScore")){
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("EditedScore"), "Numeric", false); 
				}
			}
		}
		Assert.assertTrue(cs.verifyOverallTrend(Trend),"Overall trend is correct for the numeric scheme");
		Assert.assertEquals(actualOverallScore, Integer.parseInt(cs.getOverallScore()),
				"Overall Score Correct for NUMERIC scheme");
	}
	
	//@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority= 3)
	//@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Grade")
	public void addScoreWithGrade(HashMap<String, String> testData) throws InterruptedException  {		
		 Thread.currentThread().sleep(3000L);		 
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
		String actualOverallScore = getMapFromData(
				testData.get("OverallScore")).get("Score");
		Assert.assertTrue(cs.getOverallScore().contains(actualOverallScore),
				"Overall Score Correct for GRADE scheme");
	}


	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Grade")
	public void addAndEditGradeScores(HashMap<String, String> testData) throws InterruptedException{
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Scorecard_enable_grade.apex", isPackageInstance());
		 cs.refreshPage();
		 cs.goToScorecardSection();
		addScoreWithGrade(testData);
		 cs.refreshPage();
		 cs.goToScorecardSection();
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		//String[] grades_arr={"F","E","D","C","B","A"};
		String actualOverallScore = getMapFromData(
				testData.get("OverallScore")).get("EditedScore");
		String currentScore=cs.getOverallScore();
		String Trend;
		if(currentScore.compareTo(actualOverallScore)>0) Trend="Up";
		else if (currentScore.compareTo(actualOverallScore)<0) Trend="Down";
		else Trend="Same";
		int numOfGroups = Groups.size();
		
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				if(MeasureDetails.containsKey("EditedScore")){
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("EditedScore"), "Grade", false); 
				}
			}
		}
		Assert.assertTrue(cs.getOverallScore().contains(actualOverallScore),
				"Overall Score Correct for GRADE scheme");
		Assert.assertTrue(cs.verifyOverallTrend(Trend), "Verified trend...");
		
	}
	
	//@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 5)
	//@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color")
	public void addScoreWithColor(HashMap<String, String> testData) throws InterruptedException {
		Thread.currentThread().sleep(3000L);		 
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
		String actualOverallScore = getMapFromData(testData.get("OverallScore")).get("Score");
		Assert.assertTrue(cs.verifyOverallScoreForColor(actualOverallScore),
				"Overall Score Correct for COLOR scheme");
	}

	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Color")
	public void addAndEditColorScores(HashMap<String, String> testData) throws InterruptedException {
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/Scorecard/Scorecard_enable_color.apex", isPackageInstance());
		cs.refreshPage();
		cs.goToScorecardSection();
		addScoreWithColor(testData);
		cs.refreshPage();
		cs.goToScorecardSection();

		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				if(MeasureDetails.containsKey("EditedScore")){
				cs.addOrModifyMeasureScore(
						MeasuresForGroup.get("Measure" + (m + 1)),
						MeasureDetails.get("EditedScore"), "Color", false); // 3rd parameter should be true we are adding score first time,false if modifying existing score
				}
			}
		}
		String actualOverallScore = getMapFromData(
				testData.get("OverallScore")).get("EditedScore");
		Assert.assertTrue(cs.verifyOverallScoreForColor(actualOverallScore),
				"Overall Score Correct for COLOR scheme");
	}

	//@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 7)
	//@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num")
	public void addCommentsToMeasure(HashMap<String, String> testData) {
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

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num" )
	public void addAndEditCommentsInMeasure(HashMap<String, String> testData) {
		addCommentsToMeasure(testData);
		cs.refreshPage();
		cs.goToScorecardSection();
		HashMap<String, String> Groups = getMapFromData(testData.get("Groups"));
		int numOfGroups = Groups.size();
		for (int g = 0; g < numOfGroups; g++) {
			HashMap<String, String> MeasuresForGroup = getMapFromData(testData
					.get(Groups.get("Group" + (g + 1))));
			for (int m = 0; m < MeasuresForGroup.size(); m++) {
				HashMap<String, String> MeasureDetails = getMapFromData(testData
						.get(MeasuresForGroup.get("Measure" + (m + 1))));
				if(MeasureDetails.containsKey("EditedComments")){
				cs.addOrEditCommentsForMeasure(MeasureDetails.get("EditedComments"),
						MeasuresForGroup.get("Measure" + (m + 1)));
				Assert.assertEquals(MeasureDetails.get("EditedComments")+MeasureDetails.get("Comments"), cs
						.verifyCommentForMeasure(MeasuresForGroup.get("Measure"
								+ (m + 1))));
			}
			}
		}
	}
	
	//@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 9)
	//@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num")
	public void addOverallSummary(HashMap<String, String> testData) {
		String actualSummary = getMapFromData(testData.get("Summary")).get(
				"Comment");
		cs.addOrEditOverallSummary(actualSummary, true);
		Assert.assertEquals(actualSummary, cs.getOverallSummary());
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num")
	public void addAndEditOverallSummary(HashMap<String, String> testData) {
		addOverallSummary(testData);
		cs.refreshPage();cs.goToScorecardSection();
		String actualSummary = getMapFromData(testData.get("Summary")).get(
				"EditedComment");
		cs.addOrEditOverallSummary(actualSummary, false);
		Assert.assertEquals(getMapFromData(testData.get("Summary")).get("EditedComment")+getMapFromData(testData.get("Summary")).get("Comment"), cs.getOverallSummary());
	}

	//@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	//@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num")
	public void addCustomerGoals(HashMap<String, String> testData) {
		String actualGoals = getMapFromData(testData.get("Goals")).get(
				"Comment");
		cs.addOrEditCustomerGoals(actualGoals, true);
		Assert.assertEquals(actualGoals, cs.getCustomerGoals());
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel" )
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Score_Num")
	public void editCustomerGoals(HashMap<String, String> testData) {
		addCustomerGoals(testData);
		cs.refreshPage();
		cs.goToScorecardSection();
		String actualGoals = getMapFromData(testData.get("Goals")).get(
				"EditedComment");
		cs.addOrEditCustomerGoals(actualGoals, false);
		Assert.assertEquals(getMapFromData(testData.get("Goals")).get("EditedComment")+getMapFromData(testData.get("Goals")).get("Comment"), cs.getCustomerGoals());
	}

	@AfterClass
	public void tearDown() {
		// basepage.logout();
	}

}
