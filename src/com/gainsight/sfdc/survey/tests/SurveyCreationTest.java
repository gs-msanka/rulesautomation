package com.gainsight.sfdc.survey.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionBasePage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.survey.pages.CreateSurveyPage;
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
		CreateSurveyPage newsurvey = base.clickOnNew();
		newsurvey.createNewSurvey(false, "random_id1", true);
	
	}

	@Test
	public void createClonedSurvey() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		CreateSurveyPage newsurvey = base.clickOnNew();
		newsurvey.createNewSurvey(true, "random_id1", true);
	}

}
