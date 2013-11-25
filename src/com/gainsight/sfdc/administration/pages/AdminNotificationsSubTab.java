package com.gainsight.sfdc.administration.pages;

import com.gainsight.sfdc.pages.BasePage;

public class AdminNotificationsSubTab extends BasePage {

	
	private final String READY_INDICATOR              = "//input[@class='btn']";
	private final String  NOTIFI_FREQ_CONFIG          = "//input[@class='btn']";
	private final String  AUTOSUBSCRIPTION_CONFIG     = "//input[@class='btn notiFieldSettingsBtn']";
	private final String EMAIL_NOTIFICATIONS          = "//input[@onclick='checkAll(this)']";
	private final String SELECT_DAY                   = "//select[@name='j_id0:j_id493:j_id516']";
	private final String SELECT_HOUR                  = "//select[@name='j_id0:j_id493:j_id521']";
	private final String SELECT_MINUTES               = "//select[@name='j_id0:j_id493:j_id524']";
	private final String NOTI_FEQ_SAVE                = "//input[@onclick='disableBtn(this); saveNotificationFreq();']";
	private final String NOTI_FEQ_CANCEL              = "//input[@onclick='cancelNotificationFreq()']";
	private final String NOTI_FEQ_FORM_BLOCK          = "//div[contains(@class,'jbaraDummyNotifFreqSettingsForm') and contains(@style,'display: block')]"; 
	private final String NOTI_FEQ_FORM_NONE           = "//div[contains(@class,'jbaraDummyNotifFreqSettingsForm') and contains(@style,'display: none')]";
	private final String NFORM_IMG_PRESENT            ="//img[@onclick='cancelNotificationFreq()']";
                  	//set AutoSubscription
	private final String FORM_IMG_PRESENT    = "//img[@onclick='jbaraCloseNotiForm()']";
	private final String CREATE_BY_ID_CBOX   = "//input[@class='notificationCheckBox' and @value='CreatedById']";
	private final String OWNER_ID_CBOX       = "//input[@class='notificationCheckBox' and @value='OwnerId']";
	private final String LASTMODI_ID_CBOX    = "//input[@class='notificationCheckBox' and @value='LastModifiedById']";
	private final String AUTOSUB_SAVE        = "//input[@onclick='disableBtn(this); collectSelectedFieldsNoti();']";
	private final String AUTOSUB_FORM_BLOCK  = "//div[contains(@class,'jbaraDummyNotifSettingsForm') and contains(@style,'display: block')]";
	private final String AUTOSUB_FORM_NONE   = "//div[contains(@class,'jbaraDummyNotifSettingsForm') and contains(@style,'display: none')]";
	private final String AUTOSUB_CANCEL      = "//input[@onclick='jbaraCloseNotiForm()']";
	
	public AdminNotificationsSubTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public AdminNotificationsSubTab setNotificationFrequency(String day, String hour, String Minutes) {
		
		  button.click(NOTIFI_FREQ_CONFIG);
		  wait.waitTillElementDisplayed(NOTI_FEQ_FORM_BLOCK, MIN_TIME, MAX_TIME);
		if(item.isElementPresent(NFORM_IMG_PRESENT)) {
			field.selectCheckbox(EMAIL_NOTIFICATIONS);
			field.selectFromDropDown(SELECT_DAY, day);//Monday or sunday or any day
			field.selectFromDropDown(SELECT_HOUR, hour);//01 0r 02
			field.selectFromDropDown(SELECT_MINUTES, Minutes);//01,02...etc
			button.click(NOTI_FEQ_SAVE);
			wait.waitTillElementPresent(NOTI_FEQ_FORM_NONE, MIN_TIME, MAX_TIME);
			refreshPage();
		} else {
				System.out.println("No Element found so clickong on cancel");
				button.click(NOTI_FEQ_CANCEL);
			} return this;	
	}
	
    public AdminNotificationsSubTab setAutoSubscription() {
    	
    	button.click(AUTOSUBSCRIPTION_CONFIG);
    	wait.waitTillElementDisplayed(AUTOSUB_FORM_BLOCK, MIN_TIME, MAX_TIME);
		if(item.isElementPresent(FORM_IMG_PRESENT)) {
			field.selectCheckbox(CREATE_BY_ID_CBOX);
			field.selectCheckbox(OWNER_ID_CBOX);
			field.selectCheckbox(LASTMODI_ID_CBOX);
		  button.click(AUTOSUB_SAVE);
		wait.waitTillElementPresent(AUTOSUB_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
		}  else {
			System.out.println("No Element found so clickong on cancel");
			button.click(AUTOSUB_CANCEL);
		}
		return this;	
	 }
	
	
}
