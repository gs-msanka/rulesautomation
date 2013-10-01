package com.gainsight.sfdc.accounts.pages;

import com.gainsight.sfdc.pages.BasePage;

public class AccountsPage extends BasePage{
	private final String READY_INDICATOR="//h3[text()='Recent Accounts']";
	private final String SELECT_FILTER="//select[@id='fcf']";
	private final String GO_BUTTON="//input[@name='go']";
	private final String NEW_ACCOUNT_BUTTON="//input[@value='New Account']";
	
	public AccountsPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);		
	}
	
	public AccountPage selectAccount(String name){
		field.setSelectField(SELECT_FILTER,"All Accounts");
		item.click(GO_BUTTON);
		wait.waitTillElementPresent(NEW_ACCOUNT_BUTTON, MIN_TIME, MAX_TIME);
		//For acceptance test automation pagination is not implemented
		item.click("//a/span[text()='"+name+"']");
		return new AccountPage();
	}

}
