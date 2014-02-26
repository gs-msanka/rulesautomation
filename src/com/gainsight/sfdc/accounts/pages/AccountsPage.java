package com.gainsight.sfdc.accounts.pages;


import java.net.URI;
import java.net.URISyntaxException;
import com.gainsight.sfdc.pages.BasePage;

public class AccountsPage extends BasePage{
	private final String READY_INDICATOR="//h3[text()='Recent Accounts']";

	
	public AccountsPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);		
	}
	
	public AccountPage selectAccount(String accID){
		URI currentURL;
		try {
			currentURL = new URI(driver.getCurrentUrl());
			String accURL="https://" + currentURL.getHost()+"/"+accID;
			driver.navigate().to(accURL);
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to navigate to account page");
			}
		return new AccountPage();
		
		
	}

}
