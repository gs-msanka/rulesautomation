package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyPublishPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.testdriver.Application;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

public class SurveyPublishPageTest extends SurveySetup {
	
    private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyTests.xls";
    private final String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c];";
    private final String CREATE_ACCS = Application.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS = Application.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
    private ObjectMapper mapper = new ObjectMapper();
	
    
	@BeforeClass
	public void setUp() {
		sfdc.connect();
		sfdc.runApexCode(QUERY);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTACTS));
		basepage.login();
	}
	
	@TestInfo(testCaseIds = { "GS-2696" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S3")
	public void publishSurveyWithoutQuestions(Map<String, String> testData)
			throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp);
		publishPage.clickOnPublishSurvey();
		Assert.assertEquals(publishPage.getErrorMessage(),
				"* Fields are Mandatory.", "Verifying Error Message");
	}
	
	@TestInfo(testCaseIds={"GS-2696","GS-2697"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S3")
    public void publishSurveyWithQuestions(Map<String, String> testData) throws IOException {
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
		SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp);
		surveySiteURL();
		surProp.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surProp);
		surProp.setStatus("Publish");
		Assert.assertEquals(publishPage.getSurveyStatus(), surProp.getStatus(),
				"Verifying Survey Status After Publishing");
	}

	@TestInfo(testCaseIds={"GS-2701","GS-2702"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S3")
    public void sendTestEmails(Map<String, String> testData) throws IOException {
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
		SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp);
		surveySiteURL();
		surProp.setSiteURL(surveySiteURL());
		surProp.setStatus("Design");
		Assert.assertEquals(publishPage.getSurveyStatus(), surProp.getStatus(),
				"Verifying Survey Status Before Publishing");
		publishPage.updatePublishDetails(surProp);
		surProp.setStatus("Publish");
		Assert.assertEquals(publishPage.getSurveyStatus(), surProp.getStatus(),
				"Verifying Survey Status After Publishing");
		publishPage.sendTestEmail(testData.get("Recipients").split(","),
				sfdcInfo.getUserFullName());
		Assert.assertEquals(publishPage.getTestEmailSuccessMsg(),
				"Test emails sent successfully.");
		publishPage.closeTestEmailDialog();
	}
	
	@TestInfo(testCaseIds={"GS-2698"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S3")
    public void sendTestEmails2(Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp);
		surveySiteURL();
		surProp.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surProp);
		Assert.assertEquals(
				publishPage.getErrorMessage(),
				"There are no active questions in the survey and you cannot publish this survey. Please add at least one active question before publishing.",
				"Verifying Error Message");
	}
	
	@TestInfo(testCaseIds={"GS-2703"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S3")
    public void publishSurveyWithQuestions2(Map<String, String> testData) throws IOException {
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
		SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp);
		surveySiteURL();
		surProp.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surProp);
		publishPage.sendTestEmails(testData.get("Recipients").split(","));
		Assert.assertEquals(publishPage.getMaxTestEmailsAletMsg(),
				"You can send only 10 test emails at a time.",
				"Verifying Maximum Test Emails Alert Messages");
		publishPage.closeAlertEmailDialog();
		publishPage.closeTestEmailDialog();
	}
}
