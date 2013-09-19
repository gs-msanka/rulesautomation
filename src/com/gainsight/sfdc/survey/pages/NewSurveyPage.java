package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyData;

public class NewSurveyPage extends BasePage {

	/***
	 * Create Survey Page IDs
	 */
	private final String READY_INDICATOR = "xpath=(//input[@value='Clone'])";
	private final String SURVEY_CLONE_OPTION = "xpath=(//input[@value='Clone'])";
	private final String SURVEY_CODE = "//input[@class='jbaraDummySurveyInputCtrl surveyCodeInput']";
	private final String SURVEY_TITLE = "//textarea[@class='jbaraDummySurveyInputCtrl TitleInput']";
	private final String SURVEY_ANANYMOUS = "//input[@class='anonymousCheckBox']";
	private final String SURVEY_ANANYMOUS_TYPE = "//select[@class='anonymousTypeDD']";
	private final String SURVEY_ANANYMOUS_ACCT_NAME = "//input[@class='jbaraDummyAccountName']";
	private final String START_DATE = "//input[@class='jbaraDummySurveyStartDateInput']";
	private final String END_DATE = "//input[@class='jbaraDummySurveyEndDateInput']";
	private final String THANK_YOU = "//select[@class='jbaraDummySurveyThankyouCtrl jbaraDummySurveyInputCtrl]";
	private final String THANK_YOU_MSG = "//textarea[@name='jbaraDummySurveyInputCtrl jbaraDummySurveyThankMsgInput']";
	private final String THANK_YOU_INPUT = "//input[@class='jbaraDummySurveyInputCtrl surveyThankyouCustomRedirectInput']";
	private final String FOOTER_MESSAGE = "//textarea[@class='jbaraDummySurveyInputCtrl jbaraDummySurveyFooterMsgInputCtrl']";
	private final String SURVEY_LOGO_NEW = "//input[@id='jbaraDummyImageSelectionId']";
	private final String SURVEY_LOGO_SELECT = "//input[@id='jbaraDummyImageUploadId']";
	private final String SURVEY_LOGO_SELECT_IMAGE = "//select[@class='jbaraDummyImageSelectClass']";

	private final String CANCEL_SURVEY = "//input[@value='Cancel']"; // btn
	private final String SAVE_SURVEY = "//input[@value='Save']"; // btn
																	// dummyNewSurveySaveBtn

	/***
	 * Clone Survey Page IDs
	 */
	private final String CLONE_SURVEY_SELECT = "//select[@title='Clone From Survey']";
	private final String CLONE_LOGIC_RULES = "//input[@class='cloneLogicRulesCheckBox']";
	private final String CLONE_ALERT_RULES = "//input[@class='cloneAlertRulesCheckBox']";
	private final String CLONE_PARTICIPANTS = "//input[@class='cloneParticipantsCheckBox']";

	public NewSurveyPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public SurveyData getSurveyData() {
		SurveyData sdata = new SurveyData();
		sdata.setCode("randomid");
		sdata.setTitle("test");
		sdata.setAOption("Anonymous without Account Tracking");
		sdata.setAccountName("testing");
		sdata.setTUOption("Custom Page");
		sdata.setURL("http://gainsight.com");
		sdata.setImageName("testing");
		return sdata;
	}

	public SurveyDesignPage createNewSurvey(boolean anonymous) {
		field.setTextField(SURVEY_CODE, getSurveyData().getCode());
		field.setTextField(SURVEY_TITLE, getSurveyData().getTitle());
		if (anonymous) {
			button.click(SURVEY_ANANYMOUS);
			field.selectFromDropDown(SURVEY_ANANYMOUS_TYPE, getSurveyData()
					.getAOption());
			if (getSurveyData().getAOption().equals(
					"Anonymous without Account Tracking"))
				field.setTextField(SURVEY_ANANYMOUS_ACCT_NAME, getSurveyData()
						.getAccountName());
		}
		field.setTextField(START_DATE, "9/19/2013");
		field.setTextField(END_DATE, "9/20/2013");

		field.selectFromDropDown(THANK_YOU, getSurveyData().getTUOption());
		if(getSurveyData().getTUOption().equals("Redirect URL") || getSurveyData().getTUOption().equals("Custom Page"))
			field.setTextField(THANK_YOU_INPUT, getSurveyData().getURL());
		else
			field.setTextField(THANK_YOU_MSG, "Thank you");

/*		field.click(SURVEY_LOGO_NEW);
		// Image upload implementation
		field.click(SURVEY_LOGO_SELECT);
		field.selectFromDropDown(SURVEY_LOGO_SELECT_IMAGE,getSurveyData().getImageName());
*/
		field.setTextField(FOOTER_MESSAGE, "Gainsight footer");
		button.click(SAVE_SURVEY);

		return new SurveyDesignPage();
	}

	public SurveyBasePage cancelSurvey() {

		button.click(CANCEL_SURVEY);
		return new SurveyBasePage();
	}

	public SurveyDesignPage cloneSurvey() {

		button.click(SURVEY_CLONE_OPTION);
		field.selectFromDropDown(CLONE_SURVEY_SELECT, getSurveyData().getCode()
				+ ":" + getSurveyData().getTitle());
		field.setTextField(SURVEY_CODE, getSurveyData().getCode());
		field.setTextField(SURVEY_TITLE, getSurveyData().getTitle());
		field.click(CLONE_ALERT_RULES);
		field.click(CLONE_LOGIC_RULES);
		field.click(CLONE_PARTICIPANTS);
		button.click(SAVE_SURVEY);

		return new SurveyDesignPage();

	}

}
