package com.gainsight.sfdc.survey.pages;

public class DeleteSurveyPage extends SurveyBasePage{
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public DeleteSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

}
