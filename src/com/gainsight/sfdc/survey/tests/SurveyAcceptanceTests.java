package com.gainsight.sfdc.survey.tests;

import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.gainsight.sfdc.tests.BaseTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class SurveyAcceptanceTests extends SurveySetup {

    private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyTests.xls";
    String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c];";
    ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public void setUp() {
		Log.info("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
        sfdc.runApexCode(resolveStrNameSpace(QUERY));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S1")
    public void testCreate(Map<String, String> testData) throws IOException {

        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyQuestionPage surveyQuestionPage = surveyPage.clickOnQuestions(surProp);
        SurveyQuestion surQues = mapper.readValue(testData.get("Question1"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question2"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question3"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question4"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question5"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question6"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question7"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question8"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question9"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question10"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        surQues = mapper.readValue(testData.get("Question11"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
    }


	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
