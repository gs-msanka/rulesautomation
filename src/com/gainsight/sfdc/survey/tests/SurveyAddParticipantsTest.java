package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyAddParticipantsPage;
import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.utils.DataProviderArguments;

public class SurveyAddParticipantsTest extends SurveySetup {

	private final String CREATE_ACCS=env.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String CREATE_CONTACTS=env.basedir+"/testdata/sfdc/survey/scripts/CreateContacts.txt";
	private final String TEST_DATA_FILE         = "testdata/sfdc/Survey/tests/surveytestdata.xls";
	ObjectMapper mapper = new ObjectMapper();
	
	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
     /*   createExtIdFieldOnAccount();
        createExtIdFieldOnContacts();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTACTS));  */ 
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
	public void loadContactsThroughContactObject(HashMap<String, String> testData) throws IOException{
	    SurveyAddParticipants addparticipant =mapper.readValue(testData.get("AddParticipant"), SurveyAddParticipants.class);
        
        SurveyProperties sData=mapper.readValue(testData.get("SurveyDetails"), SurveyProperties.class);
        //SurveyAddParticipantsPage addPtpPage=basepage.clickOnSurveyTab().clickOnPublished().clickOnSurveyFromPublished(sData).clickOnAddParticipants(sData);
        
//        addPtpPage.loadFromContactObj(addparticipant, sData);
        
        
	}
	
	@Test
	public void loadContactsThroughCustomObject() throws IOException{
	
	}
	
	@Test
	public void loadContactsThroughCSVFile() throws IOException{
	
	}
}
