package com.gainsight.sfdc.survey.tests;

import com.gainsight.sfdc.survey.pages.*;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SurveyQuestionsTest extends SurveySetup {

    private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyTests.xls";
    private final String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c];";
    private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public void setUp() {
		Log.info("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
        sfdc.runApexCode(resolveStrNameSpace(QUERY));
    }
	
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "LogicRulesQuestions")
    public void TestLogicRuleQuestions(Map<String, String> testData) throws IOException {

        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyQuestionPage surveyQuestionPage = surveyPage.clickOnQuestions(surProp);
        SurveyQuestion surQues = mapper.readValue(testData.get("Question1"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);


        surQues = mapper.readValue(testData.get("Question2"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question3"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);
        LogicRules(surveyQuestionPage);
        Assert.assertTrue(surveyQuestionPage.existsElement(), "Verifying LogicRule Creation");
    }

}
