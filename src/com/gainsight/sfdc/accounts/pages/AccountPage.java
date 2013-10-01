package com.gainsight.sfdc.accounts.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.pages.CustomerSuccessPage;

public class AccountPage extends BasePage {
	private final String READY_INDICATOR="//h2[text()='Account Detail']";
	
	public AccountPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		
	}
	public CustomerSuccessPage getCustomerSuccessSection() {
		return new CustomerSuccessPage();
	}

}
