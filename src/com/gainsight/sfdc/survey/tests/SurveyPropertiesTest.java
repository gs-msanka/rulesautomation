package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.utils.DataProviderArguments;

public class SurveyPropertiesTest extends SurveySetup{
	
	private final String TEST_DATA_FILE       = "testdata/sfdc/survey/tests/SurveyProperties_Test.xls";
	ObjectMapper mapper=new ObjectMapper();
	  @BeforeClass
	    public void setup() throws Exception {
	    	sfdc.connect();
	        basepage.login();
	       
	    }
	  

	    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	    public void nonanonymous(HashMap<String, String> testData) throws IOException {
	    	SurveyProperties sProps= mapper.readValue(testData.get("SurveyProp"),SurveyProperties.class);
	    	sProps.setStartDate(getDateWithFormat(Integer.getInteger(sProps.getStartDate()), 0, false));
	    	sProps.setEndDate(getDateWithFormat(Integer.getInteger(sProps.getEndDate()), 0, false));
	        SurveyPropertiesPage surPropPage = basepage.clickOnSurveyTab().createSurvey(sProps.getSurveyTitle(), true);	   
	        surPropPage.fillAndSaveSurveyProperties(sProps);
	    
	        SurveyBasePage surBasePage = surPropPage.refreshPropPage();
	        surBasePage.clickOnDrafts();
	        SurveyPropertiesPage sPropsVerification=surBasePage.clickOnSurveyFromDrafts(sProps.getSurveyTitle());
	        Assert.assertTrue(sPropsVerification.verifySurveyProperties(sProps),"All Survey Properties Matched- Case Success!");
	    }
	    
	    //anonymous
	    //partialAnonymous 
	    //
	   
	    
	

}
