package com.gainsight.sfdc.survey.pages;

public class SurveyDesignPage extends SurveyBasePage{
	
	private final String READY_INDICATOR="//a[contains(text(),'Design')]";
	private final String NEW_SURVEY = "//input[@value='New']";
	private final String REORDER_SURVEY = "//input[@value='New']";
	private final String LOGIC_RULES_SURVEY = "//input[@value='New']";
	private final String ALERT_RULES_SURVEY = "//input[@value='New']";
	
	public SurveyDesignPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public AddQuestionsPage clickOnNewQuestion() {
		item.click(NEW_SURVEY);
		return new AddQuestionsPage();
	}
	
	public QuestionsReorderPage clickOnReorder(){
		
		item.click(REORDER_SURVEY);
		return new QuestionsReorderPage();
	}
	
	public LogicRulesPage clickOnLoginRules(){
		
		item.click(LOGIC_RULES_SURVEY);
		return new LogicRulesPage();
	}
	
	public AlertRulesPage clickOnAlertRules(){
		
		item.click(ALERT_RULES_SURVEY);
		return new AlertRulesPage();
	}
	
}
