package com.gainsight.sfdc.survey.tests;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyAddParticipantsPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

public class SurveyAddParticipantsTest extends SurveySetup {

	private final String CREATE_ACCS=env.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS=env.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
	private final String TEST_DATA_FILE         = "testdata/sfdc/Survey/tests/surveytestdata.xls";
	private final String SURVEYDATA_CLEANUP= "Delete [SELECT Id,Name,JBCXM__Title__c FROM JBCXM__Survey__c];";
	private final String CUSTOM_OBJECT_CLEANUP ="Delete [SELECT Id FROM EmailCustomObjct__c];";
	ObjectMapper mapper = new ObjectMapper();
	
	private final String CSV = "/testdata/sfdc/survey/SurveyQues_ResponseCount.csv";
	
	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
/*		sfdc.runApexCode(resolveStrNameSpace(CUSTOM_OBJECT_CLEANUP));
        sfdc.runApexCode(resolveStrNameSpace(SURVEYDATA_CLEANUP));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTACTS));
		Log.info("Creating Custom Object to Load Contacts");
		create_Custom_Object_For_Addparticipants();
    	DataETL dataLoader = new DataETL();
    	JobInfo loadContacts = mapper.readValue(resolveNameSpace(env.basedir+"/testdata/sfdc/survey/jobs/Job_contacts_into_custom_object.txt"), JobInfo.class);
    	JobInfo extractsContacts = mapper.readValue(resolveNameSpace(env.basedir+"/testdata/sfdc/survey/jobs/Job_Rule_Survey_ExtractContats.txt"), JobInfo.class);
    	dataLoader.execute(loadContacts);
    	dataLoader.execute(extractsContacts);*/
	}
	
	@BeforeMethod
	public void cleanUpData(){
		sfdc.runApexCode("Delete [SELECT Id FROM JBCXM__SurveyParticipant__c];");
	}
 
	@TestInfo(testCaseIds = {"GS-2732", "GS-2714"}) 
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=false)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
    public void addParticipantsFromCustomObjectWithFilterConditions(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyAddParticipantsPage surveyAddParticipant = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyParticipants=mapper.readValue(testData.get("AddParticipant"), SurveyAddParticipants.class); 
        surveyAddParticipant.loadFromCustomObject(surveyParticipants);
        surveyAddParticipant.customFilterConditions(surveyParticipants);
        Assert.assertEquals(getCountFromSurveyParticipantObject(surProp), getCountfromCustomObject());
        }
    
    @TestInfo(testCaseIds = {"GS-2732"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=false)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
    public void addPartcipantsFromCustomObjectWOFilterCondtions(Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipant"), SurveyAddParticipants.class);
		surveyAddParticipant.loadFromCustomObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		// Assert.assertTrue(count());
		Assert.assertEquals(
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name FROM EmailCustomObjct__c where isDeleted=false")),
				getCountFromSurveyParticipantObject(surProp));
	}

    @TestInfo(testCaseIds = {"GS-2706", "GS-2708", "GS-3456"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=false)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
	public void addParticipantsFromContactObjectWithFilterConditons(
			Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipantsFromStandardobj"),
				SurveyAddParticipants.class); // survey add
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.filterConditions(surveyParticipants);
		Assert.assertEquals(
				getCountFromSurveyParticipantObject(surProp),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='QA' and isDeleted=false")));
	}
    
	@TestInfo(testCaseIds = { "GS-2704", "GS-2707" })
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = false)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
	public void addParticipantFromContactObject(
			Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipantsFromStandardobj"),
				SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.loadParticipants();
		Assert.assertEquals(surveyAddParticipant.getMessage(),
				"Selected participants are added successfully", "Verifying message from UI");
		Assert.assertEquals(
				getCountFromSurveyParticipantObject(surProp),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='QA' and isDeleted=false")));
	}
    
	@TestInfo(testCaseIds = {"GS-2706", "GS-2708", "GS-3456"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=false) 
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
	public void addParticipantsFromContactObjectWithFilterConditons2(
			Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"),
				SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage
				.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(
				testData.get("AddParticipantsFromStandardobjFilter"),
				SurveyAddParticipants.class);
		surveyAddParticipant.loadFromContactObject(surveyParticipants);
		surveyAddParticipant.filterConditions(surveyParticipants);
		Assert.assertEquals(surveyAddParticipant.getMessage(),
				"Selected participants are added successfully",
				"Verifying message from UI");
		Assert.assertEquals(
				getCountFromSurveyParticipantObject(surProp),
				sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,Name,Title FROM Contact where TITLE='"
						+ surveyParticipants.getSearchFilter()
						+ "' and isDeleted=false")));
	}
	
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true) 
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
	public void addParticipantsFromCSV(
			Map<String, String> testData) throws IOException {
		SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
		SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
		setSurveyId(surProp);
		SurveyAddParticipantsPage surveyAddParticipant = surveyPage.clickOnAddParticipants(surProp);
		SurveyAddParticipants surveyParticipants = mapper.readValue(testData.get("ParticipantsFromCSV"), SurveyAddParticipants.class);
		surveyAddParticipant.loadContactsFromCSV(surveyParticipants);
		surveyAddParticipant.contactsFromCSVWithID(surveyParticipants);
		Assert.assertEquals(getRecordCountFromContactObject(), getCountFromSurveyParticipantObject(surProp), "Verifying survey participants count");
    }
    
    @Test(enabled=false)
    public void test(){
    	System.out.println("Demo Test Here");
    	
    }
}