/*SurveyDesignPage is secondary navigation of SurveyBasePage
The following are the actions done in SurveyBasePage:
a) Navigations to all secondary menu items, click on secondary menu options, 
verify if particular page is loaded
SurveyDesignPage extends the SurveyBasePage. */

package com.gainsight.sfdc.survey.pages;


import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


public class SurveyPage extends SurveyBasePage {

    //Global Selectors
	private final String Survey_TITLE                   = "//h3[@class='leftinner-title']/span[text()='%s']";
    private final String BACK_ICON                      = "//a[@class='pull-left backbtn']";
    //Left Navigation Selectors
    private final String PROPERTIES_SECTION_LINK        = "//a[@ref-link='properties']";
    private final String QUESTIONS_SECTION_LINK         = "//a[@ref-link='questions']";
    private final String SET_CTA_SECTION_LINK           = "//a[@ref-link='setalerts']";
    private final String PUBLISH_SECTION_LINK           = "//a[@ref-link='hostsurvey']";
    private final String ADD_PARTICIPANTS_SECTION_LINK  = "//a[@ref-link='addparticipants']";
    private final String DISTRIBUTE_SECTION_LINK        = "//a[@ref-link='distribute']";
    private final String ANALYZE_SECTION_LINK           = "//a[@ref-link='analyse']";

	public SurveyPage(String surveyName) {
        super(surveyName);
        Timer.sleep(2);
		wait.waitTillElementPresent(String.format(Survey_TITLE, surveyName), MIN_TIME, MAX_TIME);
	}
	
	public SurveyPage() {
        System.out.println("Dummy Constructor - Survey Page");
    }
	public SurveyProperties clickOnProperties() {
    	item.click(PROPERTIES_SECTION_LINK);
        return new SurveyProperties();
    }

	public SurveyQuestionPage clickOnQuestions(SurveyProperties surveyProp) {
    	item.click(QUESTIONS_SECTION_LINK);
        return new SurveyQuestionPage(surveyProp);
    }
	
	public SurveySetCTAPage clickOnSetCta(SurveyProperties surveyProp) {
    	item.click(SET_CTA_SECTION_LINK);
        return new SurveySetCTAPage(surveyProp);
    }
	
	public SurveyPublishPage clickOnPublish(SurveyProperties surveyProp) {
    	item.click(PUBLISH_SECTION_LINK);
        return new SurveyPublishPage(surveyProp.getSurveyName());
    }
	
	public SurveyAddParticipantsPage clickOnAddParticipants(SurveyProperties sData) {
    	item.click(ADD_PARTICIPANTS_SECTION_LINK);
        return new SurveyAddParticipantsPage(sData);
    }
	
	public SurveyDistributePage clickOnDistribute() {
    	item.click(DISTRIBUTE_SECTION_LINK);
        return new SurveyDistributePage();
    }
	
	public SurveyAnalyzePage clickOnAnalyze(SurveyProperties sData) {
    	item.click(ANALYZE_SECTION_LINK);
        return new SurveyAnalyzePage(sData);
    }

    public SurveyBasePage goToSurveyBasePage() {
        item.click(BACK_ICON);
        return new SurveyBasePage();
    }
}