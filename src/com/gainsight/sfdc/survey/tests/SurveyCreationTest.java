package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gainsight.sfdc.survey.pages.SurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.sforce.soap.partner.sobject.SObject;
import jxl.read.biff.BiffException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class SurveyCreationTest extends SurveySetup {

	private final String TEST_DATA_FILE         = "testdata/sfdc/survey/tests/SurveyTests.xls";
    private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public void setUp() {
        basepage.login();
        sfdc.runApexCode(resolveStrNameSpace("Select id, name from JBCXM__Survey__c"));
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = false)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void createNonAnonymousSurvey(HashMap<String, String> testData)
			throws BiffException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SurveyProperties surveyPropData = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyBasePage surBasePage =  basepage.clickOnSurveyTab();
//		@SuppressWarnings("unused")
		SurveyPropertiesPage surPropPage =  surBasePage.createSurvey(surveyPropData, true);
		surPropPage.createSurveyProperties(surveyPropData);
		
		
		
	//	SurveyDesignPage surDesignPage = 
		
		
	}
	

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = false)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet2")
	public void createPartialanonymousSurvey(HashMap<String, String> testData)
			throws BiffException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SurveyProperties sdata = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
		sdata.setStartDate(getDateWithFormat(Integer.valueOf(sdata.getStartDate()), 0, false));
		sdata.setEndDate(getDateWithFormat(Integer.valueOf(sdata.getEndDate()), 19, false));
		SurveyBasePage surBasePage =  basepage.clickOnSurveyTab();
		@SuppressWarnings("unused")
		SurveyPropertiesPage surPropPage =  surBasePage.createSurvey(sdata, true);
		surPropPage.createSurveyProperties(sdata);
		
	//	SurveyDesignPage surDesignPage = 
		
		
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled = false)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet3")
	public void createCompleteanonymousSurvey(HashMap<String, String> testData)
			throws BiffException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SurveyProperties sdata = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
		sdata.setStartDate(getDateWithFormat(Integer.valueOf(sdata.getStartDate()), 0, false));
		sdata.setEndDate(getDateWithFormat(Integer.valueOf(sdata.getEndDate()), 19, false));
		SurveyBasePage surBasePage =  basepage.clickOnSurveyTab();
		@SuppressWarnings("unused")
		SurveyPropertiesPage surPropPage =  surBasePage.createSurvey(sdata, true);
		surPropPage.createSurveyProperties(sdata);
	//	SurveyDesignPage surDesignPage =
		
	}
	
	
	
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "S1")
    public void createSurvey(Map<String, String> testData) throws IOException {
        SurveyProperties surveyProp = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
        SurveyPropertiesPage  surveyPropPage = basepage.clickOnSurveyTab().createSurvey(surveyProp, true);
        setSurveyId(surveyProp);
        SurveyQuestionPage surQuesPage = surveyPropPage.clickOnQuestions(surveyProp);



    }


	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
