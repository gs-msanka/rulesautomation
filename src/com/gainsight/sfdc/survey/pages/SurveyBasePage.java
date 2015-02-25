/*Survey Base page is home page of survey 

The following are the actions done in SurveyBasePage:
a) Create Survey
b) Navigations to all primary menu items, click on primary options, verify if particular page is loaded
SurveyBasePage extends the BasePage. BasePage is home page and contains clicking on all tabs*/

package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;

public class SurveyBasePage extends BasePage {


	private final String READY_INDICATOR = "//input[@class='gs-btn btn-add  create-new-survey ']";
	private final String CREATE_SURVEY = "//input[@class='gs-btn btn-add  create-new-survey ']";
	private final String CREATE_SURVEYFORM = "//div[@class='create-sureyinner']";
	private final String NEW_SURVEY = "//input[@class='opt-survey' and @type='radio' and @data-val='new' and @name='optsurvey']";
	private final String PRE_BUILTSURVEY = "//input[@class='opt-survey' and @type='radio' and @data-val='prepack' and @name='optsurvey']";
	private final String IMPLEMENTATION_SURVEY = "//select[@class='form-control sel-prepack-survey']/following-sibling::button]";
	private final String NPS_SURVEY = "";
	private final String SURVEY_NAME ="//input[@class='form-control inputstyle survey-title-input create-survey-title']";
	private final String SAVE_SURVEY = "//input[@class='gs-btn btn-save']";	 
	private final String DASHBOARD_OPTION ="//div[@class='survey-menu-blocks']//a[@class='menu-option opt-dashboard active']";
	private final String DRAFTS_OPTION ="//div[@class='survey-menu-blocks']//a[@class='menu-option opt-drafts']";
	private final String PUBLISHED_OPTION = "//div[@class='survey-menu-blocks']//a[@class='menu-option opt-hosted']";
	private final String EXPIRED_OPTION = "//div[@class='survey-menu-blocks']//a[@class='menu-option opt-expired']";
	private final String DASHBOARDPAGE_DISPLAY ="//span[@class='surveyHeaderText' and 'Ongoing Surveys']";
	private final String DRAFTSPAGE_DISPLAY ="//span[@class='surveyHeaderText' and 'Drafts']";
	private final String PUBLISHPAGE_DISPLAY ="//span[@class='surveyHeaderText' and 'Hosted Surveys']";
	private final String EXPIREDPAGE_DISPLAY ="//span[@class='surveyHeaderText' and 'Expired Surveys']";
	private final String CLICK_ON_SURVEY = "//h3[@class='box-title survey-title' and contains(text(),'%s')]"; 

	public SurveyBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);		
	}

	public SurveyPropertiesPage createSurvey(SurveyProperties sData, boolean isNew) {
        item.click(CREATE_SURVEY);
        
        if(isNew) {
        	item.click(NEW_SURVEY);
        	field.clearAndSetText(SURVEY_NAME, sData.getSurveyTitle());
            wait.waitTillElementDisplayed(SURVEY_NAME, MIN_TIME, MAX_TIME);
        }    
        	item.click(SAVE_SURVEY); 	//Save button click.
		return new SurveyPropertiesPage(sData);
	}
	
		public SurveyBasePage clickOnDashboard() {
        //Click
		item.click(DASHBOARD_OPTION);
		wait.waitTillElementPresent(DASHBOARDPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return this;
    }

    public SurveyBasePage clickOnDrafts() {
        //Click
    	item.click(DRAFTS_OPTION);
    	wait.waitTillElementPresent(DRAFTSPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return this;
    }

    public SurveyBasePage clickOnExpired() {
        //Click
    	item.click(EXPIRED_OPTION);
    	wait.waitTillElementPresent(EXPIREDPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        //Waiting.
        return this;
    }
    public SurveyPropertiesPage clickOnSurveyFromDrafts(SurveyProperties sData){
    	item.click(String.format(CLICK_ON_SURVEY, sData.getSurveyTitle()));
    	return new SurveyPropertiesPage(sData);
    }
    
    public SurveyAnalyzePage clickOnSurveyFromDashboards(SurveyProperties sData){
    	item.click(String.format(CLICK_ON_SURVEY, sData.getSurveyTitle()));
    	return new SurveyAnalyzePage(sData);
    }

	public SurveyAnalyzePage clickOnSurveyFromPublished(SurveyProperties sData) {
		item.click(String.format(CLICK_ON_SURVEY, sData.getSurveyTitle()));
		return new SurveyAnalyzePage(sData);
	}

	public SurveyBasePage clickOnPublished() {
		item.click(PUBLISHED_OPTION);
    	wait.waitTillElementPresent(PUBLISHPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return this;
	}

}
