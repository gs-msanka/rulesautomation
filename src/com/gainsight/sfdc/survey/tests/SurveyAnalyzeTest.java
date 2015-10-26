package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyAddParticipantsPage;
import com.gainsight.sfdc.survey.pages.SurveyAnalyzePage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyDistributePage;
import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pages.SurveyPublishPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.sfdc.survey.pojo.SurveyAnalyze;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

public class SurveyAnalyzeTest extends SurveySetup {
	
	private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyAnalyze.xls";
	private ObjectMapper mapper = new ObjectMapper();
	
	
	@BeforeClass
	public void setUp() {
		sfdc.connect();
		basepage.login();
		sfdc.runApexCode(resolveStrNameSpace("Delete [SELECT Id FROM JBCXM__SurveyParticipant__c];"));
	}
	
	@TestInfo(testCaseIds={"GS-2737", "GS-2739", "GS-5762"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Analyze1")
    public void analyzeCountForNonAnonymousSurvey(Map<String, String> testData) throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties successfully saved.");
		setSurveyId(surveyPropData);
		SurveyQuestionPage surveyQuestionPage = surPropPage
				.clickOnQuestions(surveyPropData);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surveyPropData));
		surQues.setSurveyProperties(surveyPropData);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPublishPage publishPage = surPropPage
				.clickOnPublish(surveyPropData);
		surveySiteURL();
		surveyPropData.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surveyPropData);
		surveyPropData.setStatus("Publish");
		SurveyAddParticipantsPage surveyAddParticipant = surPropPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surPropPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		surPropPage.clickOnAnalyze();
		SurveyAnalyzePage surveyAnalyze = new SurveyAnalyzePage();
		SurveyAnalyze surveyAnalyzeDetails = mapper.readValue(
				testData.get("Analyze"), SurveyAnalyze.class);
		surveyAnalyze.mouseOnToAnalyzeCharts(surveyAnalyzeDetails);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData) + "' and isDeleted=false and JBCXM__Sent__c=true"))),
				surveyAnalyze.getAnalyzeCount(),
				"Verifying Count at Analyze highcharts with distribution count");
	}
    
	@TestInfo(testCaseIds={"GS-2667","GS-2737", "GS-2739", "GS-5762"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Analyze1")
    public void analyzeCountForPartialAnonymousSurvey(Map<String, String> testData) throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("PartialAnonymousProperties"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties successfully saved.");
		setSurveyId(surveyPropData);
		SurveyQuestionPage surveyQuestionPage = surPropPage
				.clickOnQuestions(surveyPropData);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surveyPropData));
		surQues.setSurveyProperties(surveyPropData);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPublishPage publishPage = surPropPage
				.clickOnPublish(surveyPropData);
		surveySiteURL();
		surveyPropData.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surveyPropData);
		surveyPropData.setStatus("Publish");
		SurveyAddParticipantsPage surveyAddParticipant = surPropPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surPropPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		surPropPage.clickOnAnalyze();
		SurveyAnalyzePage surveyAnalyze = new SurveyAnalyzePage();
		SurveyAnalyze surveyAnalyzeDetails = mapper.readValue(
				testData.get("Analyze"), SurveyAnalyze.class);
		surveyAnalyze.mouseOnToAnalyzeCharts(surveyAnalyzeDetails);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData) + "' and isDeleted=false and JBCXM__Sent__c=true"))),
				surveyAnalyze.getAnalyzeCount(),
				"Verifying Count at Analyze highcharts with distribution count");
	}
    
	@TestInfo(testCaseIds={"GS-2667","GS-2737", "GS-2739", "GS-5762"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Analyze1")
    public void analyzeCountForCompleteAnonymousSurvey(Map<String, String> testData) throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("CompleteAnonymousProperties"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties successfully saved.");
		setSurveyId(surveyPropData);
		SurveyQuestionPage surveyQuestionPage = surPropPage
				.clickOnQuestions(surveyPropData);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surveyPropData));
		surQues.setSurveyProperties(surveyPropData);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPublishPage publishPage = surPropPage
				.clickOnPublish(surveyPropData);
		surveySiteURL();
		surveyPropData.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surveyPropData);
		surveyPropData.setStatus("Publish");
		SurveyAddParticipantsPage surveyAddParticipant = surPropPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surPropPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		surPropPage.clickOnAnalyze();
		SurveyAnalyzePage surveyAnalyze = new SurveyAnalyzePage();
		SurveyAnalyze surveyAnalyzeDetails = mapper.readValue(
				testData.get("Analyze"), SurveyAnalyze.class);
		surveyAnalyze.mouseOnToAnalyzeCharts(surveyAnalyzeDetails);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData) + "' and isDeleted=false and JBCXM__Sent__c=true"))),
				surveyAnalyze.getAnalyzeCount(),
				"Verifying Count at Analyze highcharts with distribution count");
	}
}
