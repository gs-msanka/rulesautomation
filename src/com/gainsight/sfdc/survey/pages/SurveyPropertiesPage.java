/*SurveyProperties page -> Navigating to properties page and filling the fields in this page
 * and save the page 
 */


package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.testdriver.Log;

import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.survey.pojo.SurveyProperties;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyPropertiesPage extends SurveyPage {

    //Survey Form Selectors
    private final String SURVEY_NAME_INPUT              = "//input[contains(@class, 'survey-prop-title')]";
    private final String EMAIL_SERVICE_SELECT           = "//select[@name='emailService']/following-sibling::button";
    private final String START_DATE_INPUT               = "strdt";
    private final String END_DATE_INPUT                 = "enddt";
    private final String ANONYMOUS_CHECKBOX             = "//input[@name='isAnonymous' and @type='checkbox']";
    private final String ANONYMOUS_TYPE_SELECT          = "//select[@name='anonymousType']/following-sibling::button";
    private final String ANONYMOUS_ACCOUNT_INPUT        = "//input[contains(@class, 'search_input search-field')]";
    private final String INTERNAL_SUBMISSION_CHECKBOX   = "//input[@name='internalSubmission']";
    private final String DESCRIPTION_INPUT              = "//textarea[contains(@class, 'survey-prop-description')]";
    private final String THANK_MSG_SELECT               = "//select[contains(@class, 'survey-prop-msgtype')]/following-sibling::button";
    private final String THANK_MSG_INPUT                = "//input[contains(@class, 'survey-prop-msgval')]";
    private final String THANK_URL_INPUT                = "//input[contains(@class, 'survey-direct-url')]";
    private final String THANK_PAGE_INPUT               = "//input[contains(@class, 'survey-custom-url')]";
    private final String FOOTER_MSG_INPUT               = "//textarea[contains(@class, 'survey-prop-footermsg')]";
    private final String SURVEY_CODE_INPUT              = "//input[contains(@class, 'survey-prop-code')]";
    private final String SURVEY_TITLE_INPUT             = "//input[contains(@class, 'survey-prop-exttitle')]";
    private final String BG_COLOR_RADIO                 = "//input[@class='color' and @name='fm-check' and @value ='%s']";
    private final String SURVEY_SAVE_BUTTON             = "//input[@class='gs-btn btn-save' and @value='Save']";



    public SurveyPropertiesPage(SurveyProperties surveyProp) {
        super(surveyProp.getSurveyName());
        waitForSurveyPropertiesFormToLoad(surveyProp);
    }

    public SurveyPropertiesPage CreateSurveyProperties(SurveyProperties surveyProp) {
    	Log.info("Started Filling Survey Form");
        if(surveyProp.getSurveyName() != null) {
            field.clearAndSetText(SURVEY_NAME_INPUT, surveyProp.getSurveyName());
        }
        if(surveyProp.getEmailService() != null) {
            button.click(EMAIL_SERVICE_SELECT);
            selectValueInDropDown(surveyProp.getEmailService());
        }
        if(surveyProp.getStartDate() != null) {
            field.clearAndSetText(START_DATE_INPUT, surveyProp.getStartDate());
        }

        if(surveyProp.getEndDate() != null) {
            field.clearAndSetText(END_DATE_INPUT, surveyProp.getEndDate());
        }

        if(surveyProp.isAnonymous()) {
            item.selectCheckBox(ANONYMOUS_CHECKBOX);
            item.click(ANONYMOUS_TYPE_SELECT);
            selectValueInDropDown(surveyProp.getType());
            if(surveyProp.getType() != null && surveyProp.getType().equals("Anonymous without account tracking")) {
                if(surveyProp.getAnonymousAccount() != null) {
                    //TODO - Search for accounts.
                } else {
                    new RuntimeException("Account Name is mandatory for this type of survey");
                }
            }
        }

        if(surveyProp.isAllowInternalSub()) {
/*            item.selectCheckBox(INTERNAL_SUBMISSION_CHECKBOX);
        } else {
            item.selectCheckBox(INTERNAL_SUBMISSION_CHECKBOX);*/
            item.click(INTERNAL_SUBMISSION_CHECKBOX);
        }

        item.clearAndSetText(DESCRIPTION_INPUT, surveyProp.getDescription());
        if(surveyProp.getThankYouType() != null) {
            item.click(THANK_MSG_SELECT);
            selectValueInDropDown(surveyProp.getThankYouType());
            if(surveyProp.getThankYouType().equals("Message")) {
                field.clearAndSetText(THANK_MSG_INPUT, surveyProp.getThankYouNote());
            } else if(surveyProp.getThankYouType().equals("Direct through URL")) {
                field.clearAndSetText(THANK_URL_INPUT, surveyProp.getThankYouNote());
            } else if(surveyProp.getThankYouType().equals("Custom page")) {
                field.clearAndSetText(THANK_PAGE_INPUT, surveyProp.getThankYouNote());
            }
        }
        item.clearAndSetText(FOOTER_MSG_INPUT, surveyProp.getFooterMsg());
        //item.clearAndSetText(SURVEY_CODE_INPUT, surveyProp.getSurveyCode());
        item.clearAndSetText(SURVEY_TITLE_INPUT, surveyProp.getSurveyTitle());
        item.click(String.format(BG_COLOR_RADIO, surveyProp.getBgColor()));
        item.click(SURVEY_SAVE_BUTTON);
        waitTillNoLoadingIcon();
        return this;
    }

    public void waitForSurveyPropertiesFormToLoad(SurveyProperties surveyProp) {
        Log.info("Waiting for survey properties to load");
        String tempVar;
        boolean loaded = false;
        for(int i=0; i< 10; i++) {
            tempVar = field.getTextFieldValue(SURVEY_NAME_INPUT);
            if(tempVar != null && tempVar!= "" && tempVar.contains(surveyProp.getSurveyName())) {
                Log.info("Survey Properties loaded");
                loaded = true;
                break;
            } else {
                Log.info("Waiting for survey properties to load");
                Timer.sleep(2);
            }
        }
        if(!loaded) {
            throw new RuntimeException("Survey Properties not loaded yet.");
        }
    }

    public boolean verifySurveyProperties(SurveyProperties surveyProp) {
        boolean result = false;
        //TODO - More implementation yet to come
        return result;
    }
}
