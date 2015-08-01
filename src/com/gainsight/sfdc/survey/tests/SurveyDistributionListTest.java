package com.gainsight.sfdc.survey.tests;

import com.gainsight.bigdata.Integration.utils.PlainEmailConnector;
import com.gainsight.sfdc.survey.pages.*;
import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.sfdc.survey.pojo.SurveyDistribution;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.DateUtils;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SurveyDistributionListTest extends SurveySetup {

    private final String TEST_DATA_FILE            = "testdata/sfdc/survey/tests/surveytestdata.xls";
	private final String CREATE_ACCS               = Application.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS           = Application.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
	

    private ObjectMapper mapper = new ObjectMapper();
	PlainEmailConnector plainEmailConnector = new PlainEmailConnector(
			env.getProperty("em.host"), env.getProperty("em.userName"),
			env.getProperty("em.password"));

	@BeforeClass
	public void setUp() throws Exception {
		Log.info("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
        sfdc.runApexCode(resolveStrNameSpace(SURVEYDATA_CLEANUP));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTACTS));
    }
	
	@BeforeMethod
	public void cleanUpData(){
		sfdc.runApexCode("Delete [SELECT Id FROM JBCXM__SurveyParticipant__c];");
		sfdc.runApexCode("Delete [SELECT Id FROM JBCXM__SurveyDistributionSchedule__c];"); // Deleting survey schedules since, we cannot schedule more than 10 surveys per day
	}

	@TestInfo(testCaseIds = {"GS-2715", "GS-2716", "GS-2720", "GS-2721"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void testDisrtibutionWithContactObject(Map<String, String> testData) throws IOException {
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyPage
				.clickOnDistribute(surProp);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		Assert.assertEquals(
				getRecordCountFromContactObject(),
				sfdc.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surProp) + "' and isDeleted=false and JBCXM__Sent__c=true"))));
    }
    
	@TestInfo(testCaseIds = {"GS-2715", "GS-2716", "GS-2720", "GS-2721"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void testDisrtibutionWithContactObjectAndFilter(Map<String, String> testData) throws IOException {
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipantsFromContactobjFilter"),
				SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.filterConditions(surveyParticipants);
		SurveyDistributePage distribution = surveyPage
				.clickOnDistribute(surProp);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surProp)
						+ "' and isDeleted=false and JBCXM__Sent__c=true")));
	}
    
	@TestInfo(testCaseIds = {"GS-2728", "GS-2730", "GS-2731", "GS-2734", "GS-2736"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void testDistributionSchedule(Map<String, String> testData) throws IOException {
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipantsFromContactobjFilter"),
				SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.filterConditions(surveyParticipants);
		SurveyDistributePage distribution = surveyPage
				.clickOnDistribute(surProp);
		SurveyDistribution surveyDistribution = mapper.readValue(
				testData.get("Schedule"), SurveyDistribution.class);
		surveyDistribution.setScheduleDate(getDateWithFormat(Integer.valueOf(surveyDistribution.getScheduleDate()), 0, false));
		surveyDistribution.setScheduleName("Scheduled at" + " "
				+ distribution.getCurrentDateAndTime());
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surProp) + "'"));
	}

	@TestInfo(testCaseIds = {"GS-2715", "GS-2716", "GS-2720", "GS-2721"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void testDisrtibutionForPartialAnonymousSurvey(Map<String, String> testData) throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("PartialAnonymousProperties"),
				SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		setSurveyId(surveyPropData);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties successfully saved.");
		SurveyPage surveyPage = new SurveyPage();
		SurveyQuestionPage surveyQuestionPage = surveyPage
				.clickOnQuestions(surveyPropData);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surveyPropData));
		surQues.setSurveyProperties(surveyPropData);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPublishPage publishPage = surveyPage
				.clickOnPublish(surveyPropData);
		surveySiteURL();
		surveyPropData.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surveyPropData);
		surveyPropData.setStatus("Publish");
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipantsFromContactobjFilter"),
				SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.filterConditions(surveyParticipants);
		SurveyDistributePage distribution = surveyPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		Assert.assertEquals(
				getRecordCountFromContactObject(),
				sfdc.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData) + "' and isDeleted=false and JBCXM__Sent__c=true"))));
	}

	@TestInfo(testCaseIds = {"GS-2715", "GS-2716", "GS-2720", "GS-2721"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void testDisrtibutionForCompleteAnonymousSurvey(Map<String, String> testData) throws IOException {  	
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("CompleteAnonymousProperties"),
				SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		setSurveyId(surveyPropData);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties successfully saved.");
		SurveyPage surveyPage = new SurveyPage();
		SurveyQuestionPage surveyQuestionPage = surveyPage
				.clickOnQuestions(surveyPropData);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surveyPropData));
		surQues.setSurveyProperties(surveyPropData);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPublishPage publishPage = surveyPage
				.clickOnPublish(surveyPropData);
		surveySiteURL();
		surveyPropData.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surveyPropData);
		surveyPropData.setStatus("Publish");
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		Assert.assertEquals(
				getRecordCountFromContactObject(),
				sfdc.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData) + "' and isDeleted=false and JBCXM__Sent__c=true"))));
 }
	
	@TestInfo(testCaseIds = {"GS-2728", "GS-2730", "GS-2731", "GS-2734", "GS-2736"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void testDisrtibutionForCompleteAnonymousSurveyResendReschedule(Map<String, String> testData) throws IOException {  	
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("CompleteAnonymousProperties"),
				SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		setSurveyId(surveyPropData);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties successfully saved.");
		SurveyPage surveyPage = new SurveyPage();
		SurveyQuestionPage surveyQuestionPage = surveyPage
				.clickOnQuestions(surveyPropData);
		SurveyQuestion surQues = mapper.readValue(testData.get("Question1"),
				SurveyQuestion.class);
		surQues.setPageId(getRecentAddedPageId(surveyPropData));
		surQues.setSurveyProperties(surveyPropData);
		surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
		verifyQuestionDisplayed(surveyQuestionPage, surQues);
		SurveyPublishPage publishPage = surveyPage
				.clickOnPublish(surveyPropData);
		surveySiteURL();
		surveyPropData.setSiteURL(surveySiteURL());
		publishPage.updatePublishDetails(surveyPropData);
		surveyPropData.setStatus("Publish");
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		SurveyDistribution surveyDistribution = mapper.readValue(
				testData.get("Schedule"), SurveyDistribution.class);
		surveyDistribution.setScheduleDate(getDateWithFormat(Integer.valueOf(surveyDistribution.getScheduleDate()), 0, false));
		surveyDistribution.setScheduleType("Resend");
		surveyDistribution.setScheduleName("ReScheduled at" + " "
				+ distribution.getCurrentDateAndTime());
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surveyPropData) + "'"));
	}
	
	@TestInfo(testCaseIds = { "GS-2665","GS-2715", "GS-2716", "GS-2720", "GS-2721" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "NonAnonymousUsingGSEmail")
	public void distributeNonAnonymousSurveyUsingGSEmail(
			Map<String, String> testData) throws Exception {
		PlainEmailConnector.isAllEmailsSeen(env.getProperty("em.inbox"));
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		SObject[] contacts = sfdc
				.getRecords(resolveStrNameSpace("select Id, JBCXM__Email__c FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData)
						+ "' and JBCXM__Undelivered__c=false and isDeleted=false"));
		SObject[] emailTemplates = sfdc
				.getRecords(resolveStrNameSpace("select Id,Name,Subject from EmailTemplate where 	Name='"
						+ surveyPropData.getEmailTemplate() + "'"));
		String emailSubject = (String) emailTemplates[0].getField("Subject");
		Log.info("length of records is" + contacts.length);
		HashMap<String, String> msgDetails = new HashMap<String, String>();
		if (contacts.length > 0) {
			for (int i = 0; i < contacts.length; i++) {
				String temp1 = (String) contacts[i].getField("JBCXM__Email__c");
				msgDetails.put(temp1.trim(), emailSubject);
			}
		}
		Assert.assertTrue(
				plainEmailConnector.isEmailPresent(env.getProperty("em.inbox"),
						msgDetails, sfdcInfo.getUserEmail()),
				"Verifying Emails sent Vs Emails received in Inbox using GainSight Email Services");
	}
	
	@TestInfo(testCaseIds = { "GS-2665","GS-2667", "GS-2716", "GS-2720", "GS-2721" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "PartialAnonymousUsingGSEmail")
	public void distributePartialAnonymousSurveyUsingGSEmail(
			Map<String, String> testData) throws Exception {
		PlainEmailConnector.isAllEmailsSeen(env.getProperty("em.inbox"));
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		SObject[] contacts = sfdc
				.getRecords(resolveStrNameSpace("select Id, JBCXM__Email__c FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData)
						+ "' and JBCXM__Undelivered__c=false and isDeleted=false"));
		SObject[] emailTemplates = sfdc
				.getRecords(resolveStrNameSpace("select Id,Name,Subject from EmailTemplate where 	Name='"
						+ surveyPropData.getEmailTemplate() + "'"));
		String emailSubject = (String) emailTemplates[0].getField("Subject");
		Log.info("length of records is" + contacts.length);
		HashMap<String, String> msgDetails = new HashMap<String, String>();
		if (contacts.length > 0) {
			for (int i = 0; i < contacts.length; i++) {
				String temp1 = (String) contacts[i].getField("JBCXM__Email__c");
				msgDetails.put(temp1.trim(), emailSubject);
			}
		}
		Assert.assertTrue(
				plainEmailConnector.isEmailPresent(env.getProperty("em.inbox"),
						msgDetails, sfdcInfo.getUserEmail()),
				"Verifying Emails sent Vs Emails received in Inbox using GainSight Email Services");
	}
	
	@TestInfo(testCaseIds = { "GS-2665","GS-2667", "GS-2716", "GS-2720", "GS-2721" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CompleteAnonymousUsingGSEmail")
	public void distributeCompleteAnonymousSurveyUsingGSEmail(
			Map<String, String> testData) throws Exception {
		PlainEmailConnector.isAllEmailsSeen(env.getProperty("em.inbox"));
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		SObject[] contacts = sfdc
				.getRecords(resolveStrNameSpace("select Id, JBCXM__Email__c FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyPropData)
						+ "' and JBCXM__Undelivered__c=false and isDeleted=false"));
		SObject[] emailTemplates = sfdc
				.getRecords(resolveStrNameSpace("select Id,Name,Subject from EmailTemplate where 	Name='"
						+ surveyPropData.getEmailTemplate() + "'"));
		String emailSubject = (String) emailTemplates[0].getField("Subject");
		Log.info("length of records is" + contacts.length);
		HashMap<String, String> msgDetails = new HashMap<String, String>();
		if (contacts.length > 0) {
			for (int i = 0; i < contacts.length; i++) {
				String temp1 = (String) contacts[i].getField("JBCXM__Email__c");
				msgDetails.put(temp1.trim(), emailSubject);
			}
		}
		Assert.assertTrue(
				plainEmailConnector.isEmailPresent(env.getProperty("em.inbox"),
						msgDetails, sfdcInfo.getUserEmail()),
				"Verifying Emails sent Vs Emails received in Inbox using GainSight Email Services");
	}
	
	@TestInfo(testCaseIds = {"GS-2728", "GS-2730", "GS-2731", "GS-2734", "GS-2736", "GS-3200"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ScheduleNonAnonymousUsingGS")
	public void ScheduleNonAnonymousSurveyUsingGSEmail(
			Map<String, String> testData) throws Exception {
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		SurveyDistribution surveyDistribution = mapper.readValue(
				testData.get("Schedule"), SurveyDistribution.class);
		surveyDistribution.setScheduleDate(getDateWithFormat(Integer.valueOf(surveyDistribution.getScheduleDate()), 0, false));
		surveyDistribution.setScheduleName("Scheduled at" + " "
				+ distribution.getCurrentDateAndTime());
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surveyPropData) + "'")));
	}
	
	@TestInfo(testCaseIds = {"GS-2728", "GS-2730", "GS-2731", "GS-2734", "GS-2736", "GS-3200"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "SchedulePartialAnonymousUsingGS")
	public void SchedulePartialAnonymousSurveyUsingGSEmail(
			Map<String, String> testData) throws Exception {
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		SurveyDistribution surveyDistribution = mapper.readValue(
				testData.get("Schedule"), SurveyDistribution.class);
		surveyDistribution.setScheduleDate(getDateWithFormat(Integer.valueOf(surveyDistribution.getScheduleDate()), 0, false));
		surveyDistribution.setScheduleName("Scheduled at" + " "
				+ distribution.getCurrentDateAndTime());
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surveyPropData) + "'")));
	}
	
	@TestInfo(testCaseIds = {"GS-2728", "GS-2730", "GS-2731", "GS-2734", "GS-2736", "GS-3200"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ScheduleCompletAnonymousUsingGS")
	public void ScheduleCompleteAnonymousSurveyUsingGSEmail(
			Map<String, String> testData) throws Exception {
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		SurveyDistribution surveyDistribution = mapper.readValue(
				testData.get("Schedule"), SurveyDistribution.class);
		surveyDistribution.setScheduleDate(getDateWithFormat(Integer.valueOf(surveyDistribution.getScheduleDate()), 0, false));
		surveyDistribution.setScheduleName("Scheduled at" + " "
				+ distribution.getCurrentDateAndTime());
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surveyPropData) + "'")));
	}
	
	
	
	
	@TestInfo(testCaseIds = { "GS-2665","GS-2715","GS-2728","GS-2738","GS-2731",})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "NonAnonymousUsingGSEmail")
	public void ReSendNonAnonymousSurveyUsingGSEmailAndSchedule(
			Map<String, String> testData) throws Exception {
		PlainEmailConnector.isAllEmailsSeen(env.getProperty("em.inbox"));
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		SurveyDistribution surveyDistribution = mapper.readValue(
				testData.get("Schedule"), SurveyDistribution.class);
		surveyDistribution.setScheduleDate(getDateWithFormat(Integer.valueOf(surveyDistribution.getScheduleDate()), 0, false));
		surveyDistribution.setScheduleType("Resend");
		surveyDistribution.setScheduleName("ReScheduled at" + " "
				+ distribution.getCurrentDateAndTime());
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surveyPropData) + "'")));
	}
	
	@TestInfo(testCaseIds = { "GS-2665","GS-2715","GS-2728","GS-2738","GS-2731",})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "PartialAnonymousUsingGSEmail")
	public void ReSendPartialAnonymousSurveyUsingGSEmailAndSchedule(
			Map<String, String> testData) throws Exception {
		PlainEmailConnector.isAllEmailsSeen(env.getProperty("em.inbox"));
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
		SurveyAddParticipantsPage surveyAddParticipant = surveyQuestionPage
				.clickOnAddParticipants(surveyPropData);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		SurveyDistributePage distribution = surveyQuestionPage
				.clickOnDistribute(surveyPropData);
		distribution.clickingToBeContacted();
		distribution.sendEmail();
		SurveyDistribution surveyDistribution = mapper.readValue(
				testData.get("Schedule"), SurveyDistribution.class);
		surveyDistribution.setScheduleDate(getDateWithFormat(Integer.valueOf(surveyDistribution.getScheduleDate()), 0, false));
		surveyDistribution.setScheduleType("Resend");
		surveyDistribution.setScheduleName("ReScheduled at" + " "
				+ distribution.getCurrentDateAndTime());
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surveyPropData) + "'")));
	}
}