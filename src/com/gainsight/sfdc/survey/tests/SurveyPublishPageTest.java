package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.bigdata.Integration.utils.PlainEmailConnector;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pages.SurveyPublishPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;

public class SurveyPublishPageTest extends SurveySetup {
	
    private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyTests.xls";
    private final String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c];";
    private final String CREATE_ACCS = Application.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS = Application.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
    private ObjectMapper mapper = new ObjectMapper();
	PlainEmailConnector plainEmailConnector = new PlainEmailConnector(
			env.getProperty("em.host"), env.getProperty("em.userName"),
			env.getProperty("em.password"));

 
	@BeforeClass
	public void setUp() throws Exception {
		sfdc.connect();
		sfdc.runApexCode(resolveStrNameSpace(QUERY));
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
		Assert.assertEquals(publishPage.getSurveyStatus(surProp), surProp.getStatus(),
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
		Assert.assertEquals(publishPage.getSurveyStatus(surProp), surProp.getStatus(),
				"Verifying Survey Status Before Publishing");
		publishPage.updatePublishDetails(surProp);
		surProp.setStatus("Publish");
		Assert.assertEquals(publishPage.getSurveyStatus(surProp), surProp.getStatus(),
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
	
	@TestInfo(testCaseIds={"GS-2696","GS-2697"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Surveypublish1")
    public void publishSurveyWithQuestionsandGSEmailServices(Map<String, String> testData) throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("Survey"), SurveyProperties.class);
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
		Assert.assertEquals(publishPage.getSurveyStatus(surveyPropData),
				surveyPropData.getStatus(),
				"Verifying Survey Status After Publishing");
	}
	
	@TestInfo(testCaseIds={"GS-2701","GS-2702"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Surveypublish1")
    public void publishSurveyAndSendTestEmailUsingGSEmailServices(Map<String, String> testData) throws Exception {
		
		plainEmailConnector.isAllEmailsSeen(env.getProperty("em.inbox"));
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("Survey"), SurveyProperties.class);
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
		sfdc.runApexCode(resolveStrNameSpace("delete[SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
				+ setSurveyId(surveyPropData) + "' and isDeleted=false];"));
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
		Assert.assertEquals(publishPage.getSurveyStatus(surveyPropData),
				surveyPropData.getStatus(),
				"Verifying Survey Status After Publishing");
		publishPage.sendTestEmail(testData.get("Recipients").split(","),
				sfdcInfo.getUserFullName());
		Assert.assertEquals(publishPage.getTestEmailSuccessMsg(),
				"Email distribution request processed successfully.");
		publishPage.closeTestEmailDialog();
		SObject[] contacts = sfdc
				.getRecords(resolveStrNameSpace("select Id, JBCXM__Email__c FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData) + "' and isDeleted=false"));
		SObject[] emailTemplates = sfdc
				.getRecords(resolveStrNameSpace("select Id,Name,Subject from EmailTemplate where 	Name='"
						+ surveyPropData.getEmailTemplate() + "'"));
		String emailSubject = (String) emailTemplates[0].getField("Subject");
		Log.info("length of records is" + contacts.length);
		HashMap<String, String> msgDetails = new HashMap<String, String>();
		if (contacts.length > 0) {
			for (int i = 0; i < contacts.length; i++) {
				String temp1 = (String) contacts[i].getField(resolveStrNameSpace("JBCXM__Email__c"));
				msgDetails.put(temp1.trim(), emailSubject);
			}
		}
		Log.info("UserEmail is" +sfdcInfo.getUserEmail());
		Assert.assertTrue(plainEmailConnector.isEmailPresent(
				env.getProperty("em.inbox"), msgDetails, sfdcInfo.getUserEmail()));
	}
	
	@TestInfo(testCaseIds={"GS-2703"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Surveypublish1")
    public void publishSurveyWithQuestionsAndGSEmail2(Map<String, String> testData) throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("Survey"), SurveyProperties.class);
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
		try {
			publishPage.sendTestEmails(testData.get("Recipients").split(","));
			Assert.assertEquals(publishPage.getMaxTestEmailsAletMsg(),
					"You can send only 10 test emails at a time.",
					"Verifying Maximum Test Emails Alert Messages");
		} finally {
			publishPage.closeAlertEmailDialog();
			publishPage.closeTestEmailDialog();
		}
	}
	
	@TestInfo(testCaseIds = { "GS-2696" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Surveypublish1")
	public void publishSurveyWithGSEmailAndWithoutQuestions(Map<String, String> testData)
			throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("Survey"), SurveyProperties.class);
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
		SurveyPublishPage publishPage = surPropPage
				.clickOnPublish(surveyPropData);
		publishPage.clickOnPublishSurvey();
		Assert.assertEquals(publishPage.getErrorMessage(),
				"* Fields are Mandatory.", "Verifying Error Message");
	}
	
	@TestInfo(testCaseIds={"GS-2698"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Surveypublish1")
    public void sendTestEmailsWithGSEmail(Map<String, String> testData) throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("Survey"), SurveyProperties.class);
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
		SurveyPublishPage publishPage = surPropPage
				.clickOnPublish(surveyPropData);
		surveySiteURL();
		surveyPropData.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surveyPropData);
		Assert.assertEquals(
				publishPage.getErrorMessage(),
				"There are no active questions in the survey and you cannot publish this survey. Please add at least one active question before publishing.",
				"Verifying Error Message");
	}

}
