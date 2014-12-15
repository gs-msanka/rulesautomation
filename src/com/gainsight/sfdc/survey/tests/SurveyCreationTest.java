package com.gainsight.sfdc.survey.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyDesignPage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.Utilities;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.utils.DataProviderArguments;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

import jxl.read.biff.BiffException;

public class SurveyCreationTest extends BaseTest {
	private final String TEST_DATA_FILE         = "testdata/sfdc/Survey/tests/surveytestdata.xls";
	String QUERY = "DELETE [SELECT Id FROM JBCXM__Survey__c WHERE JBCXM__Title__c  = 'Survey_01'];";


	@BeforeClass
	public void setUp() {
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
        basepage.login();
        //apex.runApex(resolveStrNameSpace(QUERY));
		//sdata.setStartDate(getDateWithFormat(0, 0, false));
	//	sdata.setEndDate(getDateWithFormat(30, 0, false));
	//	sdata.setTUOption("Message");

	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void createNonanonymousSurvey(HashMap<String, String> testData)
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
