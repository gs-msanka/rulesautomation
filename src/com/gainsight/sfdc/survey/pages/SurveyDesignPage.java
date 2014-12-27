package com.gainsight.sfdc.survey.pages;


public class SurveyDesignPage extends SurveyBasePage {
	
	private final String DESIGN_PAGE  = "//div[@class='survey-sub-menu survey-menu']";

	public SurveyDesignPage() {
		wait.waitTillElementPresent(DESIGN_PAGE, MIN_TIME, MAX_TIME);
	}





	
}
