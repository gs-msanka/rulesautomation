package com.gainsight.sfdc.survey.pages;

public class PreviewSurveyPage extends SurveyBasePage{
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public PreviewSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

}
