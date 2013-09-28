package com.gainsight.sfdc.survey.pages;

public class CloseSurveyPage extends SurveyBasePage{
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public CloseSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public void clickOnYes(){
		
		
	}
}
