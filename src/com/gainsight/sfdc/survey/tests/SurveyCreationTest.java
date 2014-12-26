package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

import jxl.read.biff.BiffException;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class SurveyCreationTest extends BaseTest {
	private final String TEST_DATA_FILE         = "testdata/sfdc/Survey/tests/surveytestdata.xls";
	String QUERY = "DELETE [SELECT Id FROM JBCXM__Survey__c WHERE JBCXM__Title__c  = 'Survey_01'];";


	@BeforeClass
	public void setUp() {
        basepage.login();

	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void createNonAnonymousSurvey(HashMap<String, String> testData)
			throws BiffException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SurveyProperties sdata = mapper.readValue(testData.get("Survey"), SurveyProperties.class);	
		sdata.setStartDate(getDateWithFormat(Integer.valueOf(sdata.getStartDate()), 0, false));
		sdata.setEndDate(getDateWithFormat(Integer.valueOf(sdata.getEndDate()), 19, false));
		SurveyBasePage surBasePage =  basepage.clickOnSurveyTab();
		@SuppressWarnings("unused")
		SurveyPropertiesPage surPropPage =  surBasePage.createSurvey(sdata.getSurveyTitle(), true);
		surPropPage.fillAndSaveSurveyProperties(sdata);
		
		
		
	//	SurveyDesignPage surDesignPage = 
		
		
	}
	

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet2")
	public void createPartialanonymousSurvey(HashMap<String, String> testData)
			throws BiffException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SurveyProperties sdata = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
		sdata.setStartDate(getDateWithFormat(Integer.valueOf(sdata.getStartDate()), 0, false));
		sdata.setEndDate(getDateWithFormat(Integer.valueOf(sdata.getEndDate()), 19, false));
		SurveyBasePage surBasePage =  basepage.clickOnSurveyTab();
		@SuppressWarnings("unused")
		SurveyPropertiesPage surPropPage =  surBasePage.createSurvey(sdata.getSurveyTitle(), true);
		surPropPage.fillAndSaveSurveyProperties(sdata);
		
	//	SurveyDesignPage surDesignPage = 
		
		
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet3")
	public void createCompleteanonymousSurvey(HashMap<String, String> testData)
			throws BiffException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SurveyProperties sdata = mapper.readValue(testData.get("Survey"), SurveyProperties.class);
		sdata.setStartDate(getDateWithFormat(Integer.valueOf(sdata.getStartDate()), 0, false));
		sdata.setEndDate(getDateWithFormat(Integer.valueOf(sdata.getEndDate()), 19, false));
		SurveyBasePage surBasePage =  basepage.clickOnSurveyTab();
		@SuppressWarnings("unused")
		SurveyPropertiesPage surPropPage =  surBasePage.createSurvey(sdata.getSurveyTitle(), true);
		surPropPage.fillAndSaveSurveyProperties(sdata);
		
	//	SurveyDesignPage surDesignPage = 
		
		
	}
	
	
	
    @Test
    public void sampleTest() {

    }


	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
