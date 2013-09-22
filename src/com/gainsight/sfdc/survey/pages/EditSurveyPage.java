package com.gainsight.sfdc.survey.pages;

public class EditSurveyPage extends SurveyBasePage{
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public EditSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

}
