package com.gainsight.sfdc.survey.tests;

import com.gainsight.sfdc.survey.pages.*;
import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.sfdc.survey.pojo.SurveyDistribution;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SurveyDistributionListTest extends SurveySetup {

    private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/surveytestdata.xls";
	private final String CREATE_ACCS=env.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS=env.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
	private final String SURVEYDATA_CLEANUP= "Delete [SELECT Id,Name,JBCXM__Title__c FROM JBCXM__Survey__c];";
	private final String CUSTOM_OBJECT_CLEANUP ="Delete [SELECT Id FROM EmailCustomObjct__c];";
    private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public void setUp() throws Exception {
		Log.info("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
		sfdc.runApexCode(resolveStrNameSpace(CUSTOM_OBJECT_CLEANUP));
        sfdc.runApexCode(resolveStrNameSpace(SURVEYDATA_CLEANUP));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTACTS));
		Log.info("Creating Custom Object to Load Contacts");
    	Create_Custom_Obj_For_Addparticipants();
    	DataETL dataLoader = new DataETL();
    	JobInfo loadContacts = mapper.readValue(resolveNameSpace(env.basedir+"/testdata/sfdc/survey/jobs/Job_contacts_into_custom_object.txt"), JobInfo.class);
    	dataLoader.execute(loadContacts);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel") 
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void TestDisrtibutionWithContactObject(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyQuestionPage surveyQuestionPage = surveyPage.clickOnQuestions(surProp); // Here creating a Question
        SurveyQuestion surQues = mapper.readValue(testData.get("Question1"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);
        SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp); // Here doing actions related to publish page
        publishPage.updatePublishDetails(surProp);
        surProp.setStatus("Publish");
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipant"), SurveyAddParticipants.class); 
        addparcp.loadFromContactObj(surveyparts);
        addparcp.loadParticipants();
        GetRecordCountFromContactObject(); //Getting count from Contact Object
        SurveyDistributePage Distribution=surveyPage.clickOnDistribute(surProp);  //with contact obj and no filters
        Distribution.GetContactsCount();
        Distribution.ClickingToBeContacted();
        Distribution.SendEmail();
        Assert.assertEquals(GetRecordCountFromContactObject(), Distribution.GetContactsCount());
        
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel") 
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void TestDisrtibutionWithContactObjectAndFilter(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyQuestionPage surveyQuestionPage = surveyPage.clickOnQuestions(surProp); // Here creating a Question
        SurveyQuestion surQues = mapper.readValue(testData.get("Question1"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);
        SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp); // Here doing actions related to publish page
        publishPage.updatePublishDetails(surProp);
        surProp.setStatus("Publish");
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipantsFromContactobjFilter"), SurveyAddParticipants.class); 
        addparcp.loadFromContactObj(surveyparts);
        addparcp.ContactFilterConditions(surveyparts);
        RecordCountFromContactObjectWithFilterCond(); //Getting count from Contact Object
        SurveyDistributePage Distribution=surveyPage.clickOnDistribute(surProp);  //with contact obj and Filter By Email
        Distribution.GetContactsCount();
        Distribution.ClickingToBeContacted();
        Distribution.SendEmail();
        Assert.assertEquals(RecordCountFromContactObjectWithFilterCond(), Distribution.GetContactsCount());
        
    }
    
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Distribution")
    public void TestDisrtibutionSchedule(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyQuestionPage surveyQuestionPage = surveyPage.clickOnQuestions(surProp); // Here creating a Question
        SurveyQuestion surQues = mapper.readValue(testData.get("Question1"), SurveyQuestion.class);
        surQues.setPageId(getRecentAddedPageId(surProp));
        surQues.setSurveyProperties(surProp);
        surveyQuestionPage = createSurveyQuestion(surQues, surveyQuestionPage);
        verifyQuestionDisplayed(surveyQuestionPage, surQues);
        SurveyPublishPage publishPage = surveyPage.clickOnPublish(surProp); // Here doing actions related to publish page
        publishPage.updatePublishDetails(surProp);
        surProp.setStatus("Publish");
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipantsFromContactobjFilter"), SurveyAddParticipants.class);
        addparcp.loadFromContactObj(surveyparts);
        addparcp.ContactFilterConditions(surveyparts);
        RecordCountFromContactObjectWithFilterCond(); //Getting count from Contact Object
        SurveyDistributePage Distribution=surveyPage.clickOnDistribute(surProp);  
        SurveyDistribution SurveyDist=mapper.readValue(testData.get("Schedule"), SurveyDistribution.class); 
        Distribution.CreateSchedule(SurveyDist);
        Assert.assertEquals(Distribution.GetContactsCount(), Distribution.GetScheduledCount()); // Verifying Whether schedule has been created or not
        } 

 }

