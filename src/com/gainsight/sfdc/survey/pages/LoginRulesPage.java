package com.gainsight.sfdc.survey.pages;

public class LoginRulesPage extends SurveyDesignPage {
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public LoginRulesPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	

}
