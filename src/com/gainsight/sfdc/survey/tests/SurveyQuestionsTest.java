package com.gainsight.sfdc.survey.tests;


import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyDesignPage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.Utilities;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.utils.DataProviderArguments;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import jxl.read.biff.BiffException;

public class SurveyQuestionsTest extends BaseTest {
	private final String TEST_DATA_FILE         = "testdata/sfdc/Survey/tests/surveytestdata.xls";
//	String QUERY = "DELETE [SELECT Id FROM JBCXM__Survey__c WHERE JBCXM__Title__c  = 'Survey_01']";


	@BeforeClass
	public void setUp() {
        basepage.login();
//        userLocale = soql.getUserLocale();
//        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
//        basepage.login();
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
		SurveyBasePage surBasePage=basepage.clickOnSurveyTab();
		SurveyPropertiesPage surpropPage = surBasePage.createSurvey(sdata.getSurveyTitle(), true);
		surpropPage.fillAndSaveSurveyProperties(sdata);
		SurveyDesignPage sideNav = surpropPage.getSideNavInstance();
		SurveyQuestionPage surQuePage = sideNav.clickOnQuestions();
		surQuePage.SurveyDefaultPageVerification();
/*Defined test data in such a way that all question types (there are totally 8 question types in survey) are in same sheet.
 * The below steps perform reading all 8 question types from test data and then add all question types*/

	    ArrayList<SurveyQuestion> surQues =new ArrayList<SurveyQuestion>();
        for(int i=1;i<=8;i++){
        	surQues.add(mapper.readValue(testData.get("Question"+i), SurveyQuestion.class));
        }
        int quesNumber=0;
        for(SurveyQuestion sq : surQues){
        	surQuePage.AddQuestionAndSave(sq,++quesNumber);
        }		
	}
}

