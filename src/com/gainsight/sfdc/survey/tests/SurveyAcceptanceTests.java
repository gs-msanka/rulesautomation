package com.gainsight.sfdc.survey.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.survey.pages.AddQuestionsPage;
import com.gainsight.sfdc.survey.pages.NewSurveyPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyDesignPage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.Utilities;

public class SurveyAcceptanceTests extends BaseTest{
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
	//AAT_0011 - Non Anonymous Survey questions design
	@Test
	public void nonAnonymousSurveyQuestionsDesignTest(){	
		SurveyBasePage base = basepage.clickOnSurveyTab();	
		NewSurveyPage newsurvey = base.clickOnNew();
		surveyname=Utilities.getRandomString();
		sdata.setCode(surveyname);
		SurveyDesignPage design = newsurvey.createNewSurvey(sdata);
		AddQuestionsPage addquestion = design.clickOnNewQuestion();
		addquestion.multiChoiceSingleAnswerRadioQuestion("testing?");
		addquestion.addQuestion();
	}

}
