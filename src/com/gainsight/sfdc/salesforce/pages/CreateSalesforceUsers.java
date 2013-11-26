package com.gainsight.sfdc.salesforce.pages;

import com.gainsight.sfdc.pages.BasePage;

public class CreateSalesforceUsers extends BasePage {
	
	
	private final String READY_INDICATOR  = "//a[@id='setupLink' and @title='Setup']";
	private final String SETUP            = "//a[@id='setupLink' and @title='Setup']";
	private final String MANAGE           = "//a[@id='Users_font']";
	private final String USERS            = "//a[@id='ManageUsers_font']";
	private final String NEW_USER_BTN     = "//td/input[@title='New User']";
	private final String FIRST_NAME       = "//input[@id='name_firstName']";
	private final String LAST_NAME        = "//input[@id='name_lastName']";
	private final String ALIAS_NAME       = "//input[@id='Alias']";
	private final String EMAIL_ID         = "//input[@id='Email']";
	private final String USERNAME         = "//input[@id='Username']";
	private final String NICKNAME         = "//input[@id='CommunityNickname']";
	private final String SLCT_USER_LICENSE= "//select[@id='user_license_id']";
	private final String SLECT_ROLE       = "//select[@id='role']";
	private final String SLECT_PROFILE    = "//select[@id='Profile']";
	private final String SAVE             = "//input[@type='submit' and @title='Save']";
	
	
	public  CreateSalesforceUsers() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public CreateSalesforceUsers createUsers(String firstName, String lastName, String email, 
			                                      String userLicense, String role) {
		//item.click(SETUP);
		item.click(MANAGE);
		item.click(USERS);
		wait.waitTillElementDisplayed(NEW_USER_BTN, MIN_TIME, MAX_TIME);
		button.click(NEW_USER_BTN);
		wait.waitTillElementDisplayed(FIRST_NAME, MIN_TIME, MAX_TIME);
		item.clearAndSetText(FIRST_NAME, firstName);
		item.clearAndSetText(LAST_NAME, lastName);
		//item.clearAndSetText(ALIAS_NAME, aliasName);
		item.clearAndSetText(EMAIL_ID, email);
	//	item.clearAndSetText(USERNAME, userName);
		//item.clearAndSetText(NICKNAME, nickName);
		field.selectFromDropDown(SLCT_USER_LICENSE, userLicense);
		field.selectFromDropDown(SLECT_ROLE, role);
		//field.selectFromDropDown(SLECT_PROFILE, profile);
		button.click(SAVE);
		
		return this;
	}
	
	
	/*public CreateSalesforceUsers deactivateUsers(String firstName, String lastName, String email, 
			                                      String userLicense, String role) {
		//item.click(SETUP);
		item.click(MANAGE);
		item.click(USERS);
		wait.waitTillElementDisplayed(NEW_USER_BTN, MIN_TIME, MAX_TIME);
		button.click(NEW_USER_BTN);
		wait.waitTillElementDisplayed(FIRST_NAME, MIN_TIME, MAX_TIME);
		item.clearAndSetText(FIRST_NAME, firstName);
		item.clearAndSetText(LAST_NAME, lastName);
		//item.clearAndSetText(ALIAS_NAME, aliasName);
		item.clearAndSetText(EMAIL_ID, email);
		field.selectFromDropDown(SLCT_USER_LICENSE, userLicense);
		field.selectFromDropDown(SLECT_ROLE, role);
		button.click(SAVE);
		
		return this;
	}*/
	

}
