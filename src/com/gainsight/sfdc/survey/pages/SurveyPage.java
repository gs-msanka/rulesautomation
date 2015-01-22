/*SurveyDesignPage is secondary navigation of SurveyBasePage
The following are the actions done in SurveyBasePage:
a) Navigations to all secondary menu items, click on secondary menu options, 
verify if particular page is loaded
SurveyDesignPage extends the SurveyBasePage. */

package com.gainsight.sfdc.survey.pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;



public class SurveyPage extends SurveyBasePage {
	
	private final String DESIGN_PAGE  = "//div[@class='survey-sub-menu survey-menu']";
	private final String PROPERTIES_PAGE = "//a[@class='sub-menu-option  sub-opt-properties']";
	private final String QUESTIONS_PAGE = "//a[@class='sub-menu-option  sub-opt-questions']";
	private final String SETCTA_PAGE= "//a[@class='sub-menu-option  sub-opt-setalerts']";
	private final String PUBLISHSURVEY_PAGE = "//a[@class='sub-menu-option  sub-opt-hostsurvey']";
	private final String ADDPARTICIPANTS_PAGE = "//a[@class='sub-menu-option  sub-opt-addparticipants']";
	private final String DISTRIBUTE_PAGE = "//a[@class='sub-menu-option  sub-opt-distribute']";
	private final String ANALYZE_PAGE = "//a[@class='sub-menu-option  sub-opt-analyse']";
	private final String PROPERTIESPAGE_DISPLAY ="//div[@class='survey-editor-body properties']";
	private final String QUESTIONSPAGE_DISPLAY= "//div[@class='question-composer survey-editor-body']";
	private final String SETCTAPAGE_DISPLAY="//div[@class='survey-editor-body setalerts']";
	private final String PUBLISHSURVEYPAGE_DISPLAY="//div[@class='survey-editor-body host-survey']";
	private final String ADDPARTICIPANTSPAGE_DISPLAY="//div[@class='survey-editor-body add-participants']";
	private final String DISTRIBUTEPAGE_DISPLAY ="//div[@class ='survey-editor-body distribute']";
	private final String ANALYZEPAGE_DISPLAY ="//div[@class='survey-editor-body analyse']";
	

	public SurveyPage() {
		wait.waitTillElementPresent(DESIGN_PAGE, MIN_TIME, MAX_TIME);
	}

	public SurveyPage clickOnProperties() {
        //Click
    	item.click(PROPERTIES_PAGE);
    	wait.waitTillElementPresent(PROPERTIESPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return this;
    }

	public SurveyQuestionPage clickOnQuestions() {
        //Click
    	item.click(QUESTIONS_PAGE);
    	wait.waitTillElementPresent(QUESTIONSPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return new SurveyQuestionPage();
    }
	
	public SurveySetCTAPage clickOnSetCta() {
        //Click
    	item.click(SETCTA_PAGE);
    	wait.waitTillElementPresent(SETCTAPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return new SurveySetCTAPage();
    }
	
	public SurveyPublishPage clickOnPublish() {
        //Click
    	item.click(PUBLISHSURVEY_PAGE);
    	wait.waitTillElementPresent(PUBLISHSURVEYPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return new SurveyPublishPage();
    }
	
	public SurveyAddParticipantsPage clickOnAddparticipants() {
        //Click
    	item.click(ADDPARTICIPANTS_PAGE);
    	wait.waitTillElementPresent(ADDPARTICIPANTSPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return new SurveyAddParticipantsPage();
    }
	
	public SurveyDistributePage clickOnDistribute() {
        //Click
    	item.click(DISTRIBUTE_PAGE);
    	wait.waitTillElementPresent(DISTRIBUTEPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return new SurveyDistributePage();
    }
	
	public SurveyAnalyzePage clickOnAnalyze() {
        //Click
    	item.click(ANALYZE_PAGE);
    	wait.waitTillElementPresent(ANALYZEPAGE_DISPLAY, MIN_TIME, MAX_TIME);
        return new SurveyAnalyzePage();
    }


	
}
