package com.gainsight.sfdc.survey.pages;

public class AlertRulesPage extends SurveyDesignPage{
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public AlertRulesPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	
}
