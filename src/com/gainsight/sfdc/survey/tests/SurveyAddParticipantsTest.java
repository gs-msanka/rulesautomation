package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.Map;



import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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

public class SurveyAddParticipantsTest extends SurveySetup {

	private final String CREATE_ACCS=env.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS=env.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
	private final String TEST_DATA_FILE         = "testdata/sfdc/Survey/tests/surveytestdata.xls";
	private final String SURVEYDATA_CLEANUP= "Delete [SELECT Id,Name,JBCXM__Title__c FROM JBCXM__Survey__c];";
	private final String CUSTOM_OBJECT_CLEANUP ="Delete [SELECT Id FROM EmailCustomObjct__c];";
	ObjectMapper mapper = new ObjectMapper();
	
	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
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
 
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel") // Done Runned
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
    public void AddPartFromCustomObjectWithFilterCond(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipant"), SurveyAddParticipants.class); //survey addpart
        addparcp.loadFromCustomObject(surveyparts);
        addparcp.CustomFilterConditions(surveyparts);
        Assert.assertEquals(addparcp.getMessage(), "No participants found");
        }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel") // Done Runned
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
    public void AddPartFromCustomObjectWOFilterCond(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipant"), SurveyAddParticipants.class); //survey addpart
        addparcp.loadFromCustomObject(surveyparts);
        addparcp.loadParticipants();
        Assert.assertEquals(addparcp.getMessage(), "No participants found");
        }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel") //Done Runned
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
    public void AddPartFromContactObjectWithFilterCond(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipantsFromStandardobj"), SurveyAddParticipants.class); //survey add
        addparcp.loadFromContactObj(surveyparts);
        addparcp.ContactFilterConditions(surveyparts);
        Assert.assertEquals(addparcp.getMessage(), "No participants found");
	}
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel") //Done Runnded
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
    public void AddPartFromContactObjectWOFilterCond(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipantsFromStandardobj"), SurveyAddParticipants.class); //survey add
        addparcp.loadFromContactObj(surveyparts);
        addparcp.loadParticipants();
        Assert.assertEquals(addparcp.getMessage(), "No participants found");
    }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel") 
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
    public void AddPartFromContactObjectFilterbyEmail(Map<String, String> testData) throws IOException {
        SurveyBasePage surveyBasePage = basepage.clickOnSurveyTab();
        SurveyProperties surProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPage surveyPage = surveyBasePage.createSurvey(surProp, true);
        setSurveyId(surProp);
        SurveyAddParticipantsPage addparcp = surveyPage.clickOnAddParticipants(surProp);
        SurveyAddParticipants surveyparts=mapper.readValue(testData.get("AddParticipantsFromStandardobjFilter"), SurveyAddParticipants.class); //survey add
        addparcp.loadFromContactObj(surveyparts);
        addparcp.ContactFilterConditions(surveyparts);
        Assert.assertEquals(addparcp.getMessage(), "No participants found");
	}

	@Test
	public void loadContactsThroughCSVFile() throws IOException{
	
	}
}
