package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.util.FileUpload;


import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class NewSurveyPage extends SurveyBasePage {

	/***
	 * Create Survey Page IDs
	 */
	private final String READY_INDICATOR            = "//input[@class='jbaraDummySurveyInputCtrl surveyCodeInput']";
	private final String SURVEY_CLONE_OPTION        = "//input[@name='CreateSurvey' and @value='Clone']";
	private final String SURVEY_CODE_TEXT                = "//input[@class='jbaraDummySurveyInputCtrl surveyCodeInput']";
	private final String SURVEY_TITLE_TEXT               = "//textarea[@class='jbaraDummySurveyInputCtrl TitleInput']";
	private final String SURVEY_ANONYMOUS_CHECKBOX           = "//input[@class='anonymousCheckBox']";
	private final String SURVEY_ANONYMOUS_TYPE      = "//select[@class='anonymousTypeDD']";
	private final String SURVEY_ANONYMOUS_ACCT_NAME = "//input[@class='jbaraDummyAccountName']";
	private final String START_DATE_TEXT                 = "//input[@class='jbaraDummySurveyStartDateInput']";
	private final String END_DATE_TEXT                   = "//input[@class='jbaraDummySurveyEndDateInput']";
	private final String SURVEY_DESCRIPTION_TEXT         ="//textarea[@class='jbaraDummySurveyInputCtrl jbaraDummySurveyDescriptionInputCtrl']";
	private final String THANK_YOU                  = "//select[@class='jbaraDummySurveyThankyouCtrl jbaraDummySurveyInputCtrl']";
	private final String THANK_YOU_MSG              = "//textarea[@class='jbaraDummySurveyInputCtrl jbaraDummySurveyThankMsgInput']";
	private final String THANK_YOU_INPUT            = "//input[@class='jbaraDummySurveyInputCtrl surveyThankyouCustomRedirectInput']";
	private final String FOOTER_MESSAGE             = "//textarea[@class='jbaraDummySurveyInputCtrl jbaraDummySurveyFooterMsgInputCtrl']";
	private final String SURVEY_LOGO_SELECT         = "//input[@id='jbaraDummyImageSelectionId']";
	private final String SURVEY_LOGO_SELECT_IMAGE   = "//select[@class='jbaraDummyImageSelectClass']";
    private final String ACCOUNT_SEARCH_IMG         = "//img[@alt='Account Name Lookup']";
	private final String CANCEL_SURVEY_BUTTON       = "//input[@value='Cancel']";
	private final String SAVE_SURVEY_BUTTON         = "//input[@value='Save']";
	private final String SAVE_CLONE_SURVEY_BUTTON   = "//input[@class='btn dummyCloneSurveySaveBtn']";
    private final String ALLOW_INTERNAL_SUB_CHECKBOX = "//td[contains(text(), 'Allow Internal Submission:')]/following-sibling::td/input[@type='checkbox']";
    private final String LOAD_PART_CUSTOM_OBJ_CHECKBOX  = "//td[contains(text(), 'Load survey participants from a')]/following-sibling::td/input[@type='checkbox']";

	/***
	 * Clone Survey Page IDs
	 */
	private final String CLONE_SURVEY_SELECT        = "//select[@title='Clone From Survey']";
	private final String CLONE_LOGIC_RULES          = "//input[@class='cloneLogicRulesCheckBox']";
	private final String CLONE_ALERT_RULES          = "//input[@class='cloneAlertRulesCheckBox']";
	private final String CLONE_PARTICIPANTS         = "//input[@class='cloneParticipantsCheckBox']";

	public NewSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public SurveyDesignPage createNewSurvey(SurveyData surveyData) {
		field.setTextField(SURVEY_CODE_TEXT, surveyData.getCode());
		field.setTextField(SURVEY_TITLE_TEXT, surveyData.getTitle());
		if (surveyData.isAnonymous()) {
			button.click(SURVEY_ANONYMOUS_CHECKBOX);
            wait.waitTillElementDisplayed(SURVEY_ANONYMOUS_TYPE, MIN_TIME, MAX_TIME);
			field.selectFromDropDown(SURVEY_ANONYMOUS_TYPE, surveyData.getAOption());
			if (surveyData.getAOption().equalsIgnoreCase("Anonymous without Account Tracking")) {
                field.setTextField(SURVEY_ANONYMOUS_ACCT_NAME, surveyData.getAccountName());
                Report.logInfo("Window :" +driver.getWindowHandle());
                item.click(ACCOUNT_SEARCH_IMG);
                Set<String> windows = null;
                int i=0;
                do {
                    amtDateUtil.stalePause();
                    Report.logInfo("Looking again for popup.");
                    windows = driver.getWindowHandles();
                    Report.logInfo("Number of Windows :" +windows.size());
                    ++i;
                    if(i==5) {
                        break;
                    }
                } while (windows.size() < 2);
                List<String> winHan = new ArrayList<String>(windows);
                Report.logInfo("1 :" +winHan.get(0));
                Report.logInfo("2 :" +winHan.get(1));
                driver.switchTo().window(winHan.get(1));
                wait.waitTillElementDisplayed("//a[@class='actionLink' and contains(text(), '"+surveyData.getAccountName()+"')]", MIN_TIME, MAX_TIME);
                item.click("//a[@class='actionLink' and contains(text(), '"+surveyData.getAccountName()+"')]");
                driver.switchTo().window(winHan.get(0));
            }
        }
		if(surveyData.getStartDate() != null) {
            field.setTextField(START_DATE_TEXT, surveyData.getStartDate());
        }
		if(surveyData.getEndDate() != null) {
            field.setTextField(END_DATE_TEXT, surveyData.getEndDate());
        }
        if(surveyData.isAllowInternalSub()) {
            item.click(ALLOW_INTERNAL_SUB_CHECKBOX);
        }
        if(surveyData.isLoadPartFromCustomObj()) {
            item.click(LOAD_PART_CUSTOM_OBJ_CHECKBOX);
        }
		if(surveyData.getDescription() != null) {
            field.setTextField(SURVEY_DESCRIPTION_TEXT, surveyData.getDescription());
        }
        if(surveyData.getTUOption() != null && (surveyData.getTUOption().equals("Redirect URL")
                || surveyData.getTUOption().equals("Custom Page"))) {
            field.selectFromDropDown(THANK_YOU, surveyData.getTUOption());
            field.setTextField(THANK_YOU_INPUT, surveyData.getThankYou());
        } else {
            field.setTextField(THANK_YOU_MSG, surveyData.getThankYou());
        }
        if (surveyData.getFilePath() != null) {
			FileUpload upload = new FileUpload();
			upload.uploadFile(surveyData.getFilePath());
		}

		else if(surveyData.getImageName() !=null){
			field.click(SURVEY_LOGO_SELECT);
			field.selectFromDropDown(SURVEY_LOGO_SELECT_IMAGE,
                    surveyData.getImageName());
		}
		if(surveyData.getFooterMsg() != null) {
            field.setTextField(FOOTER_MESSAGE, surveyData.getFooterMsg());
        }
		button.click(SAVE_SURVEY_BUTTON);
        return new SurveyDesignPage();
	}

	public SurveyBasePage cancelSurvey() {
        button.click(CANCEL_SURVEY_BUTTON);
		return new SurveyBasePage();
	}

	public SurveyDesignPage cloneSurvey(SurveyData surveyData) throws InterruptedException {

		button.click(SURVEY_CLONE_OPTION);
		field.selectFromDropDown(CLONE_SURVEY_SELECT, surveyData.getCode() + ":"+ surveyData.getTitle());
        wait.waitTillElementDisplayed(CLONE_LOGIC_RULES, MIN_TIME, MAX_TIME);
        if(surveyData.isCloneLogicRules()) {
            field.click(CLONE_LOGIC_RULES);
        }
        if(surveyData.isCloneAlertRules()) {
            field.click(CLONE_ALERT_RULES);
        }
        if(surveyData.isCloneParticipants()) {
            field.click(CLONE_PARTICIPANTS);
        }
		button.click(SAVE_CLONE_SURVEY_BUTTON);
		return new SurveyDesignPage();

	}
	

}
