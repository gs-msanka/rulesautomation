package com.gainsight.sfdc.survey.tests;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.utils.DataProviderArguments;

public class SurveyAddParticipantsTest extends BaseTest {

	private final String CREATE_ACCS=env.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	private final String TEST_DATA_FILE         = "testdata/sfdc/Survey/tests/surveytestdata.xls";
	
	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
        createExtIdFieldOnAccount();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "AddParticipant_T1")
	public void loadContactsThroughContactObject() throws IOException{
		DataETL dataLoader = new DataETL();
		ObjectMapper mapper = new ObjectMapper();
		JobInfo loadContacts = mapper.readValue(resolveNameSpace(env.basedir+"/testdata/sfdc/survey/jobs/Job_Contacts_DataLoad.txt"), JobInfo.class);
        dataLoader.execute(loadContacts);
        
	}
	
	@Test
	public void loadContactsThroughCustomObject() throws IOException{
	
	}
	
	@Test
	public void loadContactsThroughCSVFile() throws IOException{
	
	}
	
	
	
}
