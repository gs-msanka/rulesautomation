package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;

public class SurveyBasePage extends BasePage {

	private final String READY_INDICATOR = "//a[contains(text(),'Survey')]";
	private final String NEW_SURVEY = "//input[@value='New']";

	public SurveyBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public NewSurveyPage clickOnNew() {
		item.click(NEW_SURVEY);
		return new NewSurveyPage();
	}

	
	
	

}
