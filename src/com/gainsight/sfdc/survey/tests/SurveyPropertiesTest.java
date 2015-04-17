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
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

public class SurveyPropertiesTest extends SurveySetup{
	
	private final String TEST_DATA_FILE = "testdata/sfdc/survey/tests/SurveyProperties_Test.xls";
	private final String SURVEYDATA_CLEANUP = "Delete [SELECT Id,Name,JBCXM__Title__c FROM JBCXM__Survey__c];";
	ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public void setUp() throws Exception {
		Log.info("Adding properties in Survey Properties Tab");
		sfdc.connect();
		basepage.login();
		sfdc.runApexCode(resolveStrNameSpace(SURVEYDATA_CLEANUP));
	}
    
	@TestInfo(testCaseIds={"GS-2662","GS-2664","GS-2665","GS-2666","GS-2668","GS-2669","GS-2665","GS-2670","GS-2671"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void TestNonAnonymousSurvey(HashMap<String, String> testData)
			throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.CreateSurveyProperties(surveyPropData);
	}

}
