package com.gainsight.sfdc.survey.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;

public class SurveyBasePage extends BasePage {


	private final String READY_INDICATOR = "//input[@class='gs-btn btn-add  create-new-survey ']";
	private final String CREATE_SURVEY = "//input[@class='gs-btn btn-add  create-new-survey ']";
	private final String CREATE_SURVEYFORM = "//div[@class='create-sureyinner']";
	private final String NEW_SURVEY = "//input[@class='opt-survey' and @type='radio' and @data-val='new' and @name='optsurvey']";
	private final String PRE_BUILTSURVEY = "//input[@class='opt-survey' and @type='radio' and @data-val='prepack' and @name='optsurvey']";
	private final String SURVEY_NAME ="//input[@class='form-control inputstyle survey-title-input create-survey-title']";
	private final String SAVE_SURVEY = "//input[@class='gs-btn btn-save']";	 
	private final String DASHBOARD_OPTION ="//div[@class='survey-menu-blocks']//a[@class='menu-option opt-dashboard active']";
	private final String DRAFTS_OPTION ="//div[@class='survey-menu-blocks']//a[@class='menu-option opt-drafts']";
	private final String PUBLISHED_OPTION = "//div[@class='survey-menu-blocks']//a[@class='menu-option opt-hosted']";
	private final String EXPIRED_OPTION = "//div[@class='survey-menu-blocks']//a[@class='menu-option opt-expired']";

	public SurveyBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);		
	}

	public SurveyPropertiesPage createSurvey(String surveyName, boolean isNew) {
        item.click("CREATE_SURVEY");
        wait.waitTillElementDisplayed(CREATE_SURVEYFORM, MIN_TIME, MAX_TIME);
        if(isNew) {
        	item.click("NEW_SURVEY");
        	driver.findElement(By.xpath(SURVEY_NAME)).sendKeys();
            driver.findElement(By.xpath(SURVEY_NAME)).sendKeys(Keys.ENTER);
            wait.waitTillElementDisplayed(SURVEY_NAME, MIN_TIME, MAX_TIME);
        }
        	item.click("PRE_BUILTSURVEY");{
        	}
        
        	item.click("SAVE_SURVEY"); 	//Save button click.
		return new SurveyPropertiesPage();
	}

	public SurveyBasePage clickOnDashboard() {
        //Click
		item.click("DASHBOARD_OPTION");
        return this;
    }

    public SurveyBasePage clickOnDrafts() {
        //Click
    	item.click("DRAFTS_OPTION");
        return this;
    }

    public SurveyBasePage clickOnPublish() {
        //Click
    	item.click("PUBLISHED_OPTION");
        return this;
    }

    public SurveyBasePage clickOnExpired() {
        //Click
    	item.click("EXPIRED_OPTION");
        //Waiting.
        return this;
    }
}
