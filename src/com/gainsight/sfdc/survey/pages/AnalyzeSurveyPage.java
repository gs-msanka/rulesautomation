package com.gainsight.sfdc.survey.pages;

public class AnalyzeSurveyPage extends SurveyBasePage{
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public AnalyzeSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

}
