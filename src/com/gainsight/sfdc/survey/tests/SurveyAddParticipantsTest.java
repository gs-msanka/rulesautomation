package com.gainsight.sfdc.survey.tests;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;

public class SurveyAddParticipantsTest extends BaseTest {

	private final String CREATE_ACCS=env.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_Customers_For_Survey.txt";
	
	@BeforeClass
	public void setup() throws Exception {
		sfdc.connect();
		basepage.login();
		DataETL dataLoader = new DataETL();
        ObjectMapper mapper = new ObjectMapper();
        createExtIdFieldOnAccount();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
        
        JobInfo loadContacts = mapper.readValue(resolveNameSpace(env.basedir+"/testdata/sfdc/survey/jobs/Job_Contacts_DataLoad.txt"), JobInfo.class);
        dataLoader.execute(loadContacts);
	}
}
