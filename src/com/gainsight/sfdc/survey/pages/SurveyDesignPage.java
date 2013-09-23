package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;

public class SurveyDesignPage extends BasePage{
	
	private final String READY_INDICATOR="//a[contains(text(),'Design')]";
	private final String NEW_SURVEY = "//input[@value='New']";
	private final String REORDER_SURVEY = "//input[@value='New']";
	private final String LOGIC_RULES_SURVEY = "//input[@value='New']";
	private final String ALERT_RULES_SURVEY = "//input[@value='New']";
	
	public SurveyDesignPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public AddQuestionsPage clickOnNew() {
		item.click(NEW_SURVEY);
		return new AddQuestionsPage();
	}
	
	public QuestionsReorderPage clickOnReorder(){
		
		item.click(REORDER_SURVEY);
		return new QuestionsReorderPage();
	}
	
	public LoginRulesPage clickOnLoginRules(){
		
		item.click(LOGIC_RULES_SURVEY);
		return new LoginRulesPage();
	}
	
	public AlertRulesPage clickOnAlertRules(){
		
		item.click(ALERT_RULES_SURVEY);
		return new AlertRulesPage();
	}
	
}
