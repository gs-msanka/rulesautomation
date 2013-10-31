package com.gainsight.sfdc.survey.tests;

import org.junit.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.survey.pages.NewSurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.Utilities;

public class SurveyCreationTest extends BaseTest {
	SurveyData sdata = new SurveyData();
	private String surveyname = "";
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
		sdata.setTitle("test");
		sdata.setStartDate(Utilities.generateDate(0));
		sdata.setEndDate(Utilities.generateDate(2));
		sdata.setAnanymous(false);
		sdata.setImageName("TestImage.png");
		sdata.setTUOption("Message");
	}

	@Test
	public void createNewSurvey() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		surveyname=Utilities.getRandomString();
		sdata.setCode(surveyname);
		newsurvey.createNewSurvey(sdata);
	}

	@Test
	public void createNewSurveyWithAnanymousAccountTracking() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		sdata.setAnanymous(true);
		sdata.setAOption("Anonymous with Account Tracking");
		newsurvey.createNewSurvey(sdata);

	}

	@Test
	public void createNewSurveyWithAnanymousNoAccountTracking() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		sdata.setAnanymous(true);
		sdata.setAOption("Anonymous without Account Tracking");
		sdata.setAccountName("sfdc_test");
		newsurvey.createNewSurvey(sdata);

	}

	@Test
	public void createNewSurveyWithThankYouRedirectURL() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		sdata.setTUOption("Redirect URL");
		sdata.setURL("http://gainsight.com");
		newsurvey.createNewSurvey(sdata);

	}

	@Test
	public void createNewSurveyWithThankYouCustomPage() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		sdata.setTUOption("Custom Page");
		sdata.setURL("gainsight");
		newsurvey.createNewSurvey(sdata);

	}

	@Test
	public void createNewSurveyWithNewSurveyLogo() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		sdata.setFilePath(TestEnvironment.basedir + "/testdata/sfdc/images/TestImage.png");
		newsurvey.createNewSurvey(sdata);

	}

	@Test(dependsOnMethods={"createNewSurvey"})
	public void createClonedSurvey() throws InterruptedException {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		sdata.setCode(surveyname);
		newsurvey.cloneSurvey(sdata);

	}

	@Test
	public void cancelSurvey() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage newsurvey = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		newsurvey.cancelSurvey();
	}
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

}
