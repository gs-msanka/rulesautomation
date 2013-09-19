package com.gainsight.sfdc.survey.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.survey.pages.NewSurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.tests.BaseTest;

public class SurveyCreationTest extends BaseTest{
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Survey Test Cases...");
		basepage.login();
	}
	
	@Test
	public void createNewSurvey() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		newsurvey.createNewSurvey(false);
	}
	
	@Test
	public void createNewSurveyWithAnanymousAccountTracking() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		newsurvey.createNewSurvey(true);
	
	}
	
	@Test
	public void createNewSurveyWithAnanymousNoAccountTracking() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		newsurvey.createNewSurvey(true);
	
	}

	@Test
	public void createClonedSurvey() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		newsurvey.cloneSurvey();
		
	}
	
	@Test
	public void cancelSurvey() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		newsurvey.cancelSurvey();
		
	}

}
