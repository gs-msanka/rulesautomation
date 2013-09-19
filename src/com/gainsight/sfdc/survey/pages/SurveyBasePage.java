package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;

public class SurveyBasePage extends BasePage{

	private final String READY_INDICATOR="//a[contains(text(),'Survey')]";

	public SurveyBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public CreateSurveyPage clickOnNew(){
		item.click("//input[@value='New']");
		return new CreateSurveyPage();		
	} 
	
}
