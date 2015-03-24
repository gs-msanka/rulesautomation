package com.gainsight.sfdc.survey.tests;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pages.SurveySetCTAPage;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import org.codehaus.jackson.map.ObjectMapper;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SurveyAcceptanceTests extends SurveySetup {

    private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyTests.xls";
    String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c];";
    ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public void setUp() {
		Log.info("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
       // sfdc.runApexCode(resolveStrNameSpace(QUERY));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S1")
    public void test1(Map<String, String> testData) throws IOException {

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

        surQues = mapper.readValue(testData.get("Question4"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question5"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question6"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question7"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question8"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question9"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question10"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);

        surQues = mapper.readValue(testData.get("Question11"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S1")
    public void test2(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        setSurveyId(surProp);
        SurveyQuestionPage surveyQuestionPage = surveyBasePage.openSurveyFromDrafts(surProp).clickOnQuestions(surProp);
        surveyQuestionPage = surveyQuestionPage.clickOnExpandView();
        SurveyQuestion surQues1 = mapper.readValue(testData.get("Question1"), SurveyQuestion.class);
        surQues1.setPageId(getRecentAddedPageId(surProp));
        surQues1.setSurveyProperties(surProp);
        SurveyQuestion surQues2 = mapper.readValue(testData.get("Question2"), SurveyQuestion.class);
        surQues2.setPageId(getRecentAddedPageId(surProp));
        surQues2.setSurveyProperties(surProp);
        setQuestionId(surQues2);
        setAnsChoicesId(surQues2);
        verifyQuestionDisplayed(surveyQuestionPage, surQues2);
        SurveyQuestion surQues3 = mapper.readValue(testData.get("Question3"), SurveyQuestion.class);
        surQues3.setPageId(getRecentAddedPageId(surProp));
        surQues3.setSurveyProperties(surProp);
        setQuestionId(surQues3);
        setAnsChoicesId(surQues3);
        verifyQuestionDisplayed(surveyQuestionPage, surQues3);
        SurveyQuestion surQues4 = mapper.readValue(testData.get("Question4"), SurveyQuestion.class);
        surQues4.setPageId(getRecentAddedPageId(surProp));
        surQues4.setSurveyProperties(surProp);
        setQuestionId(surQues4);
        setAnsChoicesId(surQues4);
        verifyQuestionDisplayed(surveyQuestionPage, surQues4);

        SurveyQuestion surQues5 = mapper.readValue(testData.get("Question5"), SurveyQuestion.class);
        surQues5.setPageId(getRecentAddedPageId(surProp));
        surQues5.setSurveyProperties(surProp);
        setQuestionId(surQues5);
        setAnsChoicesId(surQues5);
        verifyQuestionDisplayed(surveyQuestionPage, surQues5);

        SurveyQuestion surQues6 = mapper.readValue(testData.get("Question6"), SurveyQuestion.class);
        surQues6.setPageId(getRecentAddedPageId(surProp));
        surQues6.setSurveyProperties(surProp);
        setQuestionId(surQues6);
        setAnsChoicesId(surQues6);
        verifyQuestionDisplayed(surveyQuestionPage, surQues6);

        SurveyQuestion surQues7 = mapper.readValue(testData.get("Question7"), SurveyQuestion.class);
        surQues7.setPageId(getRecentAddedPageId(surProp));
        surQues7.setSurveyProperties(surProp);
        setQuestionId(surQues7);
        setAnsChoicesId(surQues7);
        verifyQuestionDisplayed(surveyQuestionPage, surQues7);

        SurveyQuestion surQues10 = mapper.readValue(testData.get("Question10"), SurveyQuestion.class);
        surQues10.setPageId(getRecentAddedPageId(surProp));
        surQues10.setSurveyProperties(surProp);
        setQuestionId(surQues10);
        setAnsChoicesId(surQues10);
        verifyQuestionDisplayed(surveyQuestionPage, surQues10);

        SurveyQuestion surQues11 = mapper.readValue(testData.get("Question11"), SurveyQuestion.class);
        surQues11.setPageId(getRecentAddedPageId(surProp));
        surQues11.setSurveyProperties(surProp);
        setQuestionId(surQues11);
        setAnsChoicesId(surQues11);
        verifyQuestionDisplayed(surveyQuestionPage, surQues11);

        SurveyQuestion surQues8 = mapper.readValue(testData.get("Question8"), SurveyQuestion.class);
        surQues8.setPageId(getRecentAddedPageId(surProp));
        surQues8.setSurveyProperties(surProp);
        setQuestionId(surQues8);
        setAnsChoicesId(surQues8);
        verifyQuestionDisplayed(surveyQuestionPage, surQues8);

        SurveyQuestion surQues9 = mapper.readValue(testData.get("Question9"), SurveyQuestion.class);
        surQues9.setPageId(getRecentAddedPageId(surProp));
        surQues9.setSurveyProperties(surProp);
        setQuestionId(surQues9);
        setAnsChoicesId(surQues9);
        verifyQuestionDisplayed(surveyQuestionPage, surQues9);

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S2")
    public void test3(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        setSurveyId(surProp);
        SurveySetCTAPage surveySetCTAPage = surveyBasePage.openSurveyFromDrafts(surProp).clickOnSetCta(surProp);
        String a = "{\"type\":\"Risk\",\"subject\":\"I am gainsight\",\"customer\":\"AAR CORP\",\"status\":\"Open\",\"reason\":\"Other - Uncategorized\",\"dueDate\":\"5\",\"comments\":null,\"taskCount\":0,\"priority\":\"Medium\",\"assignee\":\"GiribabuG\",\"attributes\":[],\"eventRecurring\":null,\"snoozeDate\":\"1\",\"snoozeReason\":\"Other\",\"scoreOfCustomer\":null,\"fromCustomer360orWidgets\":false,\"recurring\":false,\"ctaoverDue\":false,\"closed\":false,\"imp\":false}";
        CTA cta  =  mapper.readValue(a, CTA.class);

        SurveyCTARule surveyCTARule = new SurveyCTARule();
        surveyCTARule.setCta(cta);


        String b = "{\"pageTitle\":\"Untitled Page\",\"questionText\":\"Matrix Single Answer\",\"pageId\":null,\"questionId\":null,\"headerId\":null,\"headerTitle\":null,\"questionType\":\"MATRIX\",\"active\":true,\"required\":true,\"allowComments\":true,\"addOther\":true,\"singleAnswer\":true,\"image\":null,\"allowedAnswers\":[{\"answerText\":\"Ans 3\"},{\"answerText\":\"Ans 4\"},{\"answerText\":\"Ans 5\"}],\"subQuestions\":[{\"subQuestionText\":\"Sub Question 1\"}]}";
        SurveyQuestion surveyQuestion = mapper.readValue(b, SurveyQuestion.class);

        List<SurveyQuestion> s = new ArrayList<>();
        s.add(surveyQuestion);
        surveyCTARule.setSurveyQuestions(s);

        surveySetCTAPage.addRule(surveyCTARule);
        surveySetCTAPage.clickOnSetCta(surProp);
        Assert.assertTrue(surveySetCTAPage.verifyRule(surveyCTARule));

    }


        @AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
