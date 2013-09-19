package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;

public class CreateSurveyPage extends BasePage{
	
	private final String READY_INDICATOR="//div[@id='dummySurveyCreationHeaderDiv']/table/tbody/tr/td[2]";

	public CreateSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public SurveyDesignPage createNewSurvey(boolean clone, String code, boolean anonymous){
		
		if(clone)
			button.click("xpath=(//input[@name='CreateSurvey'])[2]");
		
		field.setTextField("//input[@name='j_id0:j_id8:mainPageBlock:j_id65']", code);
		field.setTextField("//textarea[@name='j_id0:j_id8:mainPageBlock:j_id69']", "testing");
		if(anonymous){
			button.click("//input[@name='j_id0:j_id8:mainPageBlock:j_id71']");
//			field.selectFromDropDown("//select[@name='j_id0:j_id8:mainPageBlock:j_id73']", "Anonymous with Account Tracking");
		}
		field.setText("//input[@id='j_id0:j_id8:mainPageBlock:jbaraDummySurveyStartDateInputId']", "9/17/2013");
		field.setTextField("//input[@id='j_id0:j_id8:mainPageBlock:jbaraDummySurveyEndDateInputId']", "9/18/2013");
		button.click("//input[@name='j_id0:j_id8:mainPageBlock:j_id112']");
		
		return new SurveyDesignPage();
	}

}
