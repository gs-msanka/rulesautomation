package com.gainsight.sfdc.survey.tests;

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
		surveyDistribution.setScheduleName("Scheduled at" + " "
				+ DateUtils.getCurrentDateAndTime("yyyy/MM/dd HH:mm:ss"));
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
		surveyDistribution.setScheduleType("Resend");
		surveyDistribution.setScheduleName("ReScheduled at" + " "
				+ DateUtils.getCurrentDateAndTime("yyyy/MM/dd HH:mm:ss"));
		distribution.createSchedule(surveyDistribution);
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")),
				sfdc.getRecordCount("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyDistributionScheduleId__c='"
						+ getSurveyDistributionID(surveyPropData) + "'"));
	}
}
