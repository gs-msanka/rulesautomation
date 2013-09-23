package com.gainsight.sfdc.survey.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;

public class SurveyPublishTest extends SurveyCreationTest{
	

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Survey Publish Test Cases...");
		basepage.login();
	}

	@Test
	public void savePublishSurvey(){
		
		
		
	}
}
