package com.gainsight.sfdc.survey.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.survey.pages.NewSurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyDesignPage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.Utilities;
import org.junit.AfterClass;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SurveyCreationTest extends BaseTest {
	SurveyData sdata = new SurveyData();
	private String surveyname = "";
    String QUERY = "DELETE [SELECT Id FROM JBCXM__Survey__c WHERE JBCXM__Title__c  = 'test'];";
	@BeforeClass
	public void setUp() {
        userLocale = soql.getUserLocale();
		Report.logInfo("Starting Survey Creation / Clone Test Cases...");
		basepage.login();
        apex.runApex(resolveStrNameSpace(QUERY));
		sdata.setTitle("test");
		sdata.setStartDate(getDateWithFormat(0, 0));
		sdata.setEndDate(getDateWithFormat(30, 0));
		sdata.setTUOption("Message");
	}

	@Test
	public void createNewSurvey() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage surveyPage = base.clickOnNew();
		surveyname=Utilities.getRandomString();
		sdata.setCode(surveyname);
		SurveyDesignPage designPage = surveyPage.createNewSurvey(sdata);
        base = designPage.clickOnBack();
        Assert.assertTrue(base.isSurveyPresent(sdata), "Checking weather survey is displayed in the list");
	}

	@Test
	public void createNewSurveyWithAnonymousAccountTracking() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage surveyPage = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		sdata.setAnonymous(true);
		sdata.setAOption("Anonymous with Account Tracking");
        SurveyDesignPage designPage = surveyPage.createNewSurvey(sdata);
        base = designPage.clickOnBack();
        Assert.assertTrue(base.isSurveyPresent(sdata), "Checking weather survey is displayed in the list");
	}

	@Test
	public void createNewSurveyWithAnonymousNoAccountTracking() {
		SurveyBasePage base = basepage.clickOnSurveyTab();
		NewSurveyPage surveyPage = base.clickOnNew();
		sdata.setCode(Utilities.getRandomString());
		sdata.setAnonymous(true);
		sdata.setAOption("Anonymous without Account Tracking");
		sdata.setAccountName("AAR Corp Hardware Abscoa Division");
        SurveyDesignPage designPage = surveyPage.createNewSurvey(sdata);
        base = designPage.clickOnBack();
        Assert.assertTrue(base.isSurveyPresent(sdata), "Checking weather survey is displayed in the list");

	}

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}

}
