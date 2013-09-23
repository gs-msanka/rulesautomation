package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyData;
import com.gainsight.sfdc.util.FileUpload;

public class NewSurveyPage extends BasePage {

	/***
	 * Create Survey Page IDs
	 */
	private final String READY_INDICATOR = "//input[@class='jbaraDummySurveyInputCtrl surveyCodeInput']";
	private final String SURVEY_CLONE_OPTION = "//div[@id='dummySurveyCreationHeaderDiv']/table/tbody/tr[2]/td/input";
	private final String SURVEY_CODE = "//input[@class='jbaraDummySurveyInputCtrl surveyCodeInput']";
	private final String SURVEY_TITLE = "//textarea[@class='jbaraDummySurveyInputCtrl TitleInput']";
	private final String SURVEY_ANANYMOUS = "//input[@class='anonymousCheckBox']";
	private final String SURVEY_ANANYMOUS_TYPE = "//select[@class='anonymousTypeDD']";
	private final String SURVEY_ANANYMOUS_ACCT_NAME = "//input[@class='jbaraDummyAccountName']";
	private final String START_DATE = "//input[@class='jbaraDummySurveyStartDateInput']";
	private final String END_DATE = "//input[@class='jbaraDummySurveyEndDateInput']";
	private final String SURVEY_DESCRIPTION="//textarea[@class='jbaraDummySurveyInputCtrl jbaraDummySurveyDescriptionInputCtrl']";
	private final String THANK_YOU = "//select[@class='jbaraDummySurveyThankyouCtrl jbaraDummySurveyInputCtrl']";
	private final String THANK_YOU_MSG = "//textarea[@class='jbaraDummySurveyInputCtrl jbaraDummySurveyThankMsgInput']";
	private final String THANK_YOU_INPUT = "//input[@class='jbaraDummySurveyInputCtrl surveyThankyouCustomRedirectInput']";
	private final String FOOTER_MESSAGE = "//textarea[@class='jbaraDummySurveyInputCtrl jbaraDummySurveyFooterMsgInputCtrl']";
	private final String SURVEY_LOGO_SELECT = "//input[@id='jbaraDummyImageSelectionId']";
	private final String SURVEY_LOGO_SELECT_IMAGE = "//select[@class='jbaraDummyImageSelectClass']";

	private final String CANCEL_SURVEY = "//input[@value='Cancel']";
	private final String SAVE_SURVEY = "//input[@value='Save']";																
	private final String SAVE_CLONE_SURVEY = "//input[@class='btn dummyCloneSurveySaveBtn']";

	/***
	 * Clone Survey Page IDs
	 */
	private final String CLONE_SURVEY_SELECT = "//select[@title='Clone From Survey']";
	private final String CLONE_LOGIC_RULES = "//div[@id='dummySurveyCloningDiv']/table/tbody/tr[5]/td[2]/input[@class='cloneLogicRulesCheckBox']";
	private final String CLONE_ALERT_RULES = "//div[@id='dummySurveyCloningDiv']/table/tbody/tr[6]/td[2]/input[@class='cloneAlertRulesCheckBox']";
	private final String CLONE_PARTICIPANTS = "//div[@id='dummySurveyCloningDiv']/table/tbody/tr[7]/td[2]/input[@class='cloneParticipantsCheckBox']";
	
	public NewSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public SurveyDesignPage createNewSurvey(SurveyData sdata) {
		field.setTextField(SURVEY_CODE, sdata.getCode());
		field.setTextField(SURVEY_TITLE, sdata.getTitle());
		if (sdata.getAnanymous()) {
			button.click(SURVEY_ANANYMOUS);
			field.selectFromDropDown(SURVEY_ANANYMOUS_TYPE, sdata.getAOption());
			if (sdata.getAOption().equals("Anonymous without Account Tracking"))
				field.setTextField(SURVEY_ANANYMOUS_ACCT_NAME,
						sdata.getAccountName());
		}
		field.setTextField(START_DATE, sdata.getStartDate());
		field.setTextField(END_DATE, sdata.getEndDate());
		field.click(SURVEY_DESCRIPTION);
		field.setTextField(SURVEY_DESCRIPTION, "testing");
		if (sdata.getTUOption().equals("Redirect URL")
				|| sdata.getTUOption().equals("Custom Page")){
			field.selectFromDropDown(THANK_YOU, sdata.getTUOption());
			field.setTextField(THANK_YOU_INPUT, sdata.getURL());
		}
		else
			field.setTextField(THANK_YOU_MSG, "Thank you");

		if (sdata.getFilePath() != null) {
			FileUpload upload = new FileUpload();
			upload.uploadFile(sdata.getFilePath());
		}

		else {
			field.click(SURVEY_LOGO_SELECT);
			field.selectFromDropDown(SURVEY_LOGO_SELECT_IMAGE,
					sdata.getImageName());
		}
		field.setTextField(FOOTER_MESSAGE, "Gainsight footer");
		button.click(SAVE_SURVEY);

		return new SurveyDesignPage();
	}

	public SurveyBasePage cancelSurvey() {

		button.click(CANCEL_SURVEY);
		return new SurveyBasePage();
	}

	public SurveyDesignPage cloneSurvey(SurveyData sdata) throws InterruptedException {

		button.click(SURVEY_CLONE_OPTION);
		field.selectFromDropDown(CLONE_SURVEY_SELECT, sdata.getCode() + ":"
				+ sdata.getTitle());
		wait.waitInSeconds(2000);
		field.click(CLONE_LOGIC_RULES);
		wait.waitInSeconds(2000);
		field.click(CLONE_ALERT_RULES);
		wait.waitInSeconds(2000);
		field.click(CLONE_PARTICIPANTS);
		button.click(SAVE_CLONE_SURVEY);
		wait.waitInSeconds(2000);
		return new SurveyDesignPage();

	}
	

}
