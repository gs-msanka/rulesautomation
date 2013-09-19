package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;

public class SurveyDesignPage extends BasePage{
	
	private final String READY_INDICATOR="//a[contains(text(),'Design')]";

	public SurveyDesignPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	

}
