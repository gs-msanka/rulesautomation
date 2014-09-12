package com.gainsight.sfdc.survey.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.survey.pages.NewSurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyDesignPage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.junit.AfterClass;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

public class SurveyAcceptanceTests extends BaseTest{

    private final String TEST_DATA_FILE = "testdata/sfdc/survey/tests/SurveyAcceptanceTest.xls";
    String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c WHERE JBCXM__Code__c = 'Survey Automated UI'];";

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
        apex.runApex(resolveStrNameSpace(QUERY));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-Radio-1")
    public  void addQuestionRadio(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-Checkbox-1")
    public  void addQuestionCheckbox(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-SingleSelect-1")
    public  void addQuestionSingleSelect(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-MultiSelect-1")
    public  void addQuestionMultiSelect(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }
        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-Ranking-1")
    public  void addQuestionRanking(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-SingleLine-1")
    public  void addQuestionSingleLine(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-MultiLine-1")
    public  void addQuestionMultiLine(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-NPS-1")
    public  void addQuestionNPS(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-MATRADIO-1")
    public  void addQuestionMATRADIO(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-MATCHECK-1")
    public  void addQuestionMATCHECK(HashMap<String, String> testData) {
        SurveyData sdata = new SurveyData();
        HashMap<String, String> survey = getMapFromData(testData.get("Survey"));
        sdata.setCode(survey.get("Code"));
        sdata.setTitle(survey.get("Title"));
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        if(!surveyBasePage.isSurveyPresent(sdata)) {
            NewSurveyPage newSurveyPage = surveyBasePage.clickOnNew();
            SurveyDesignPage surveyDesignPage = newSurveyPage.createNewSurvey(sdata);
            surveyBasePage = surveyDesignPage.clickOnBack();
            Assert.assertTrue(surveyBasePage.isSurveyPresent(sdata), "Checking if the survey is created");
        }

        SurveyDesignPage surveyDesignPage = surveyBasePage.clickOnDesign(sdata);
        surveyDesignPage = surveyDesignPage.addSurveyQuestion(testData);
        Assert.assertTrue(surveyDesignPage.isQuestionPresent(testData));
    }

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

}
