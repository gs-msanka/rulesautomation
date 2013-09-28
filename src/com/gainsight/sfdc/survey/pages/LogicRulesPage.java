
package com.gainsight.sfdc.survey.pages;

public class LogicRulesPage extends SurveyDesignPage {
	
	private final String READY_INDICATOR="//select[@title='Question Type']";

	public LogicRulesPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	

}
