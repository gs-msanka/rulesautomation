package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import com.gainsight.utils.wait.CommonWait;
import com.gainsight.utils.wait.ExpectedCommonWaitCondition;

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
import com.gainsight.sfdc.survey.pages.SurveyResponsePage;
import com.gainsight.sfdc.survey.pages.SurveySiteCofiguration;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.survey.pojo.SurveyResponseAns;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.sforce.soap.partner.sobject.SObject;


public class Rule_Survey_Test extends SurveySetup {
	
    private final String TEST_DATA_FILE                    = "testdata/sfdc/survey/tests/SurveyTests.xls";
    private final String QUERY                             = "DELETE [SELECT ID FROM JBCXM__Survey__c];";
    private final String RULES_CLEANUP                     = "DELETE [SELECT ID FROM JBCXM__AutomatedAlertRules__c];";
    private final String CTA_CLEANUP                       = "DELETE [SELECT Id FROM JBCXM__CTA__c];";
    private final static String SURVEY_QUESTIONS_FILE      = Application.basedir + "/testdata/sfdc/survey/scripts/SurveyQuestions.txt";
    private final static String SURVEY_PUBLISH_FILE        = Application.basedir + "/testdata/sfdc/survey/scripts/SurveyPublish.txt";
    private final static String SURVEY_PARTICIPANTS_FILE   = Application.basedir + "/testdata/sfdc/survey/scripts/Surveyparticipants.txt";
    private static final String SURVEY_MASTER_QUERY = "Select id, JBCXM__Code__c, JBCXM__Title__c From JBCXM__Survey__c Where JBCXM__Code__c = 'Survey created through automation' AND JBCXM__Title__c = 'Survey created through automation'";
	private final String CREATE_ACCS = Application.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS = Application.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
    private String SURVEY_ID = null;
    private ObjectMapper mapper = new ObjectMapper();
    

	@BeforeClass
	public void setUp() {
		Log.info("Starting Survey Creation");
		sfdc.connect();
		basepage.login();
/*		SurveySiteCofiguration ss=new SurveySiteCofiguration();
		ss.navigateToSetup();*/
		sfdc.runApexCode(resolveStrNameSpace(QUERY));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTACTS));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(SURVEY_QUESTIONS_FILE));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(SURVEY_PUBLISH_FILE));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(SURVEY_PARTICIPANTS_FILE));
		SObject[] surveys = sfdc
				.getRecords(resolveStrNameSpace(SURVEY_MASTER_QUERY));
		if (surveys.length == 1) {
			SURVEY_ID = surveys[0].getId();
		} else {
			throw new RuntimeException("Survey Not Found");
		}
	}
 
	@BeforeMethod
	public void cleanUpData(){
		sfdc.runApexCode(resolveStrNameSpace(CTA_CLEANUP));
		sfdc.runApexCode(resolveStrNameSpace(RULES_CLEANUP));
	}
    
	@TestInfo(testCaseIds={"GS-2690","GS-2691","GS-2694"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
	public void surveyRuleRadioQuestionType(HashMap<String, String> testData) throws Exception {
		testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
		testData.put("Name", SURVEY_ID);
		populateObjMaps();
		setupRule(testData);
		final CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		SurveyCTARule surveyCTARule = mapper.readValue(testData.get("Type"),
				SurveyCTARule.class);
		SurveyResponsePage surveyResponse = new SurveyResponsePage();
		SurveyResponseAns surveyAns = mapper.readValue(testData.get("Answers"), SurveyResponseAns.class);
		surveyResponse.openSurveyForm(surveyCTARule, testData, surveyAns);
		Assert.assertTrue(sfdc
				.getRecordCount(resolveStrNameSpace("select Id FROM JBCXM__CTA__c where IsDeleted=false and JBCXM__Priority__r.JBCXM__SystemName__c='"
						+ cta.getPriority()
						+ "' and JBCXM__Reason__r.JBCXM__SystemName__c='"
						+ cta.getReason()
						+ "' and JBCXM__Type__r.JBCXM__Type__c='"
						+ cta.getType()
						+ "' and JBCXM__Stage__r.JBCXM__SystemName__c='"
						+ cta.getStatus() + "'")) == 1);
	}
	
	@TestInfo(testCaseIds={"GS-2690","GS-2691"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
	public void surveyRuleMatixSingleQuestionType(HashMap<String, String> testData) throws Exception {
		testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
		testData.put("Name", SURVEY_ID);
		populateObjMaps();
		setupRule(testData);
		final CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		SurveyCTARule surveyCTARule = mapper.readValue(testData.get("Type"),
				SurveyCTARule.class);
		SurveyResponsePage surveyResponse = new SurveyResponsePage();
		SurveyResponseAns surveyAns = mapper.readValue(testData.get("Answers"),
				SurveyResponseAns.class);
		surveyResponse.openSurveyForm(surveyCTARule, testData, surveyAns);
		Assert.assertTrue(sfdc
				.getRecordCount(resolveStrNameSpace("select Id FROM JBCXM__CTA__c where IsDeleted=false and JBCXM__Priority__r.JBCXM__SystemName__c='"
						+ cta.getPriority()
						+ "' and JBCXM__Reason__r.JBCXM__SystemName__c='"
						+ cta.getReason()
						+ "' and JBCXM__Type__r.JBCXM__Type__c='"
						+ cta.getType()
						+ "' and JBCXM__Stage__r.JBCXM__SystemName__c='"
						+ cta.getStatus() + "'")) == 1);
	}
	
	@TestInfo(testCaseIds={"GS-2690","GS-2691"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
	public void surveyRuleSingleSelectQuestionType(HashMap<String, String> testData) throws Exception {
		testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
		testData.put("Name", SURVEY_ID);
		populateObjMaps();
		setupRule(testData);
		final CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		SurveyCTARule surveyCTARule = mapper.readValue(testData.get("Type"),
				SurveyCTARule.class);
		SurveyResponsePage surveyResponse = new SurveyResponsePage();
		SurveyResponseAns surveyAns = mapper.readValue(testData.get("Answers"), SurveyResponseAns.class);
		surveyResponse.openSurveyForm(surveyCTARule, testData, surveyAns);
		Assert.assertTrue(sfdc
				.getRecordCount(resolveStrNameSpace("select Id FROM JBCXM__CTA__c where IsDeleted=false and JBCXM__Priority__r.JBCXM__SystemName__c='"
						+ cta.getPriority()
						+ "' and JBCXM__Reason__r.JBCXM__SystemName__c='"
						+ cta.getReason()
						+ "' and JBCXM__Type__r.JBCXM__Type__c='"
						+ cta.getType()
						+ "' and JBCXM__Stage__r.JBCXM__SystemName__c='"
						+ cta.getStatus() + "'")) == 1);
	}
	
	@TestInfo(testCaseIds={"GS-2690","GS-2691","GS-2694","GS-2695"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
	public void surveyRuleSingleSelect(HashMap<String, String> testData) throws Exception {
		testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
		testData.put("Name", SURVEY_ID);
		populateObjMaps();
		setupRule(testData);
		CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		SurveyCTARule surveyCTARule = mapper.readValue(testData.get("Type"),
				SurveyCTARule.class);
		SurveyResponsePage surveyResponse = new SurveyResponsePage();
		SurveyResponseAns surveyAns = mapper.readValue(testData.get("Answers"), SurveyResponseAns.class);
		surveyResponse.openSurveyForm(surveyCTARule, testData, surveyAns);
		Assert.assertTrue(sfdc
				.getRecordCount(resolveStrNameSpace("select Id FROM JBCXM__CTA__c where IsDeleted=false and JBCXM__Priority__r.JBCXM__SystemName__c='"
						+ cta.getPriority()
						+ "' and JBCXM__Reason__r.JBCXM__SystemName__c='"
						+ cta.getReason()
						+ "' and JBCXM__Type__r.JBCXM__Type__c='"
						+ cta.getType()
						+ "' and JBCXM__Stage__r.JBCXM__SystemName__c='"
						+ cta.getStatus() + "'")) == 1);
	}
	
	@TestInfo(testCaseIds={"GS-2690","GS-2691"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
	public void surveyRuleMultiSelect(HashMap<String, String> testData) throws Exception {
		testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
		testData.put("Name", SURVEY_ID);
		populateObjMaps();
		setupRule(testData);
		CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		SurveyCTARule surveyCTARule = mapper.readValue(testData.get("Type"),
				SurveyCTARule.class);
		SurveyResponsePage surveyResponse = new SurveyResponsePage();
		SurveyResponseAns surveyAns = mapper.readValue(testData.get("Answers"), SurveyResponseAns.class);
		surveyResponse.openSurveyForm(surveyCTARule, testData, surveyAns);
		Assert.assertTrue(sfdc
				.getRecordCount(resolveStrNameSpace("select Id FROM JBCXM__CTA__c where IsDeleted=false and JBCXM__Priority__r.JBCXM__SystemName__c='"
						+ cta.getPriority()
						+ "' and JBCXM__Reason__r.JBCXM__SystemName__c='"
						+ cta.getReason()
						+ "' and JBCXM__Type__r.JBCXM__Type__c='"
						+ cta.getType()
						+ "' and JBCXM__Stage__r.JBCXM__SystemName__c='"
						+ cta.getStatus() + "'")) == 1);
	}
	
	@TestInfo(testCaseIds={"GS-2690","GS-2691"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule6")
	public void surveyRuleMatrixMultipleAnswers	(HashMap<String, String> testData) throws Exception {
		testData.put("JBCXM__TaskDefaultOwner__c", sfdcInfo.getUserId());
		testData.put("Name", SURVEY_ID);
		populateObjMaps();
		setupRule(testData);
		CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		SurveyCTARule surveyCTARule = mapper.readValue(testData.get("Type"),
				SurveyCTARule.class);
		SurveyResponsePage surveyResponse = new SurveyResponsePage();
		SurveyResponseAns surveyAns = mapper.readValue(testData.get("Answers"), SurveyResponseAns.class);
		surveyResponse.openSurveyForm(surveyCTARule, testData, surveyAns);
		Assert.assertTrue(sfdc
				.getRecordCount(resolveStrNameSpace("select Id FROM JBCXM__CTA__c where IsDeleted=false and JBCXM__Priority__r.JBCXM__SystemName__c='"
						+ cta.getPriority()
						+ "' and JBCXM__Reason__r.JBCXM__SystemName__c='"
						+ cta.getReason()
						+ "' and JBCXM__Type__r.JBCXM__Type__c='"
						+ cta.getType()
						+ "' and JBCXM__Stage__r.JBCXM__SystemName__c='"
						+ cta.getStatus() + "'")) == 1);
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
		cta.setAssignee(sfdcInfo.getUserFullName());
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
		surProp.setDefaultAddress(sfdcInfo.getUserFullName());
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
		Assert.assertEquals(publishPage.getSurveyStatus(surProp), surProp.getStatus(),
				"Verifying Survey Status");
		basepage.clickOnSurveyTab();
		surveyBasePage.openSurveyFromPublished(surProp).clickOnSetCta(surProp);
		SurveySetCTAPage setCTAPage = new SurveySetCTAPage();
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
		cta.setAssignee(sfdcInfo.getUserFullName());
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
		cta.setAssignee(sfdcInfo.getUserFullName());
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

