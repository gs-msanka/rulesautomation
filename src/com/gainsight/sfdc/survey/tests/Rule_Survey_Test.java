package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyPublishPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pages.SurveySetCTAPage;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.workflow.pojos.CTA;


public class Rule_Survey_Test extends SurveySetup {
	
    private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyTests.xls";
    private final String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c];";
    private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public void setUp() {
		Log.info("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
    }
 
	@BeforeMethod
	public void cleanUpData(){
		sfdc.runApexCode(resolveStrNameSpace(QUERY));
	}
	
	@TestInfo(testCaseIds={"GS-2690"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S2")
    public void testSetCTA(Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyQuestionPage surveyQuestionPage = surveyPage
				.clickOnQuestions(surProp);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surProp));
		surQues.setSurveyProperties(surProp);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPage s = new SurveyPage();
		SurveySetCTAPage surveySetCTAPage = s.clickOnSetCta(surProp);
		CTA cta = mapper.readValue(testData.get("cta"), CTA.class);
		cta.setAssignee(sfinfo.getUserFullName());
		SurveyCTARule surveyCTARule = mapper.readValue(
				testData.get("SurveyCTARule"), SurveyCTARule.class);
		surveyCTARule.setCta(cta);
		SurveyQuestion surveyQuestion = mapper.readValue(
				testData.get("SurveyQuestion"), SurveyQuestion.class);
		List<SurveyQuestion> sa = new ArrayList<>();
		sa.add(surveyQuestion);
		surveyCTARule.setSurveyQuestions(sa);
		surveySetCTAPage.addRule(surveyCTARule);
		surveyPage.clickOnSetCta(surProp);
		Timer.sleep(5);
		Assert.assertTrue(surveySetCTAPage.verifyRule(surveyCTARule));
	}
	
	@TestInfo(testCaseIds = { "GS-2688", "GS-2689" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S3")
	public void testSetCTA2(Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		surProp.setDefaultAddress(sfinfo.getUserFullName());
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyQuestionPage surveyQuestionPage = surveyPage
				.clickOnQuestions(surProp);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surProp));
		surQues.setSurveyProperties(surProp);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp);
		publishPage.updatePublishDetails(surProp);
		surProp.setStatus("Publish");
		Assert.assertEquals(publishPage.getSurveyStatus(), surProp.getStatus(),
				"Verifying Survey Status");
		basepage.clickOnSurveyTab().clickOnPublishedView();
		SurveySetCTAPage setCTAPage = new SurveySetCTAPage();
		setCTAPage.clickOnCTACardView();
		Assert.assertTrue(setCTAPage.isCTAPageVisible(),
				"Verifying CTA Page Navigation");
		Assert.assertEquals(setCTAPage.getNoRulesText(), "No rules set.");
	}
	
	@TestInfo(testCaseIds = { "GS-2692", "GS-2693" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S2")
	public void testDeleteCTA(Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyQuestionPage surveyQuestionPage = surveyPage
				.clickOnQuestions(surProp);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surProp));
		surQues.setSurveyProperties(surProp);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPage s = new SurveyPage();
		SurveySetCTAPage surveySetCTAPage = s.clickOnSetCta(surProp);
		CTA cta = mapper.readValue(testData.get("cta"), CTA.class);
		cta.setAssignee(sfinfo.getUserFullName());
		SurveyCTARule surveyCTARule = mapper.readValue(
				testData.get("SurveyCTARule"), SurveyCTARule.class);
		surveyCTARule.setCta(cta);
		SurveyQuestion surveyQuestion = mapper.readValue(
				testData.get("SurveyQuestion"), SurveyQuestion.class);
		List<SurveyQuestion> sa = new ArrayList<>();
		sa.add(surveyQuestion);
		surveyCTARule.setSurveyQuestions(sa);
		surveySetCTAPage.addRule(surveyCTARule);
		surveyPage.clickOnSetCta(surProp);
		Timer.sleep(5);
		Assert.assertTrue(surveySetCTAPage.verifyRule(surveyCTARule));
		surveySetCTAPage.deleteExistingRule();
		Assert.assertEquals(surveySetCTAPage.getNoRulesText(), "No rules set.");
	}
	
	@TestInfo(testCaseIds = {"GS-2694" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S2")
	public void testAdvanceLogic(Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyQuestionPage surveyQuestionPage = surveyPage
				.clickOnQuestions(surProp);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surProp));
		surQues.setSurveyProperties(surProp);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPage s = new SurveyPage();
		SurveySetCTAPage surveySetCTAPage = s.clickOnSetCta(surProp);
		CTA cta = mapper.readValue(testData.get("cta"), CTA.class);
		cta.setAssignee(sfinfo.getUserFullName());
		SurveyCTARule surveyCTARule = mapper.readValue(
				testData.get("CTAAdvanceLogic"), SurveyCTARule.class);
		surveyCTARule.setCta(cta);
		SurveyQuestion surveyQuestion = mapper.readValue(
				testData.get("SurveyQuestion"), SurveyQuestion.class);
		List<SurveyQuestion> sa = new ArrayList<>();
		sa.add(surveyQuestion);
		surveyCTARule.setSurveyQuestions(sa);
		surveySetCTAPage.addRule(surveyCTARule);
		surveyPage.clickOnSetCta(surProp);
		Timer.sleep(5);
		Assert.assertTrue(surveySetCTAPage.verifyRule(surveyCTARule));
		Assert.assertTrue(surveySetCTAPage.getAdvancedCondtion(surveyCTARule));
	}
}

