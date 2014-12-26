package com.gainsight.sfdc.survey.tests;

import com.gainsight.testdriver.Log;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.gainsight.sfdc.tests.BaseTest;

public class SurveyAcceptanceTests extends BaseTest{

    private final String TEST_DATA_FILE = "testdata/sfdc/survey/tests/SurveyAcceptanceTest.xls";
    String QUERY  = "DELETE [SELECT ID FROM JBCXM__Survey__c WHERE JBCXM__Code__c = 'Survey Automated UI'];";

	@BeforeClass
	public void setUp() {
		Log.info("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
    }


    //Your Test Cases

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
