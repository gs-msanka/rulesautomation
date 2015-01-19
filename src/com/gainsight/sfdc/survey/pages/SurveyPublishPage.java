package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyPublishPage extends BasePage{
	
	private static final String SURVEY_STATUS="//div[@class='col-sm-5 status-msg']";
	private static final String ENTER_SITE_URL="//input[@class='form-control site-urls']";
	private static final String EMAIL_TEMPLATE_SELECT="//span[@class='ui-multiselect-selected-label' and text()='Select email template']";
	private static final String EMAIL_TEMPLATE_SELECT_VALUE="//li[contains(@class,'ui-multiselect-option')]//span[contains(text(),'%s')]";
	private static final String DEFAULT_ADDRESS_SELECT="//span[@class='ui-multiselect-selected-label' and text()='Select from email']";
	private static final String DEFAULT_ADDRESS_SELECT_VALUE="//li[contains(@class,'ui-multiselect-option')]//span[contains(text(),'%s')]";
	private static final String HIDE_PUBLISH_DATE_ONOFF="//label[@for='hpd']/span[@class='onoffswitch-switch']";
	private static final String PUBLISH_BUTTON="//input[@class='gs-btn btn-save btn-host-survey' and @value='Publish']";
	private static final String SENT_TEST_EMAIL_BUTTON="//input[@class='gs-btn btn-add btn-send-test-mail' and @value='Send Test Email']";
	private static final String SUCCESS_MESSAGE="//div[@class='errorMessage alert alert-success' and contains(@style,'opacity') and contains(text(),'Survey published successfully')]";
	private static final String SEND_TEST_MAIL_TITLE="//span[@class='ui-dialog-title' and contains(text(),'Send Test Email')]";
	private static final String SEND_TEST_MAIL_FROM_ADDRs="";
	private static final String SEND_TEST_MAIL_FROM_ADDRs_VALUE="";
	private static final String SEND_TEST_MAIL_TO_ADDRS="";
	private static final String SEND_TEST_MAIL_BUTTON_IN_POPUP="";
	private static final String SEND_TEST_MAIL_CLOSE_POPUP="";
	private static final String SEND_TEST_MAIL_ADD_MORE_RECEIPNTS="";
	private static final String SEND_TEST_MAIL_SUCCESS_MSG="";
	private static final String SEND_TEST_MAIL_CANCEL_BTN="";
	
	public String getSurveyStatus(){
		return field.getTextFieldValue(SURVEY_STATUS);
	}
	
	public SurveyPublishPage setSiteURL(SurveyProperties sProp){
		field.clearAndSetText(ENTER_SITE_URL, sProp.getSiteURL());
		return this;
	}
	
	public SurveyPublishPage selectEmailTemplate(SurveyProperties sProp){
		item.click(EMAIL_TEMPLATE_SELECT);
		item.click(String.format(EMAIL_TEMPLATE_SELECT_VALUE,sProp.getEmailTemplate()));
		return this;
	}
	
	public SurveyPublishPage selectDefaultAddress(SurveyProperties sProp){
		item.click(DEFAULT_ADDRESS_SELECT);
		item.click(String.format(DEFAULT_ADDRESS_SELECT_VALUE, sProp.getDefaultAddress()));
		return this;
	}
	
	public SurveyPublishPage clickOnHidePubishDate(SurveyProperties sProp){
		item.click(HIDE_PUBLISH_DATE_ONOFF);
		return this;
	}
	
	public SurveyPublishPage clickOnPublish(){
		item.click(PUBLISH_BUTTON);
		waitTillNoLoadingIcon();
		wait.waitTillElementDisplayed(SUCCESS_MESSAGE, MIN_TIME,  MAX_TIME);
		return this;
	}
	
	public SurveyPublishPage sendTestEmail(String recipient){
			item.click(SENT_TEST_EMAIL_BUTTON);
			wait.waitTillElementDisplayed(SEND_TEST_MAIL_TITLE, MIN_TIME, MAX_TIME);
			item.click(SEND_TEST_MAIL_FROM_ADDRs);
			item.click(SEND_TEST_MAIL_FROM_ADDRs_VALUE);
			field.clearAndSetText(SEND_TEST_MAIL_TO_ADDRS, recipient);
			button.click(SEND_TEST_MAIL_BUTTON_IN_POPUP);
			waitTillNoLoadingIcon();
			wait.waitTillElementDisplayed(SEND_TEST_MAIL_SUCCESS_MSG, MIN_TIME, MAX_TIME);
			item.click(SEND_TEST_MAIL_CANCEL_BTN);
			return this;
	}
	
	
}
