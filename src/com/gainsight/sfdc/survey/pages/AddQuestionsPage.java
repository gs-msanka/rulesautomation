package com.gainsight.sfdc.survey.pages;


public class AddQuestionsPage extends SurveyDesignPage {
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public AddQuestionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	

}
