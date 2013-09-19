package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;

public class AddQuestionsPage extends BasePage {
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public AddQuestionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	

}
