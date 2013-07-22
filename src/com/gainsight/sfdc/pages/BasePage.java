package com.gainsight.sfdc.pages;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.customer.pages.CustomerBasePage;

public class BasePage extends WebPage implements Constants{
	private final String READY_INDICATOR="//div[@id='userNavButton']";
	
	public BasePage login(){		
		field.setTextField("username", TestEnvironment.get().getUserName());
		field.setTextField("password", TestEnvironment.get().getUserPassword());
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		
		return this;
	}
	
	public BasePage login(String username,String pwd){
		field.setTextField("username", username);
		field.setTextField("password", pwd);
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		
		return this;
	}
	
	public BasePage logout(){
		item.click("userNavButton");
		item.click("//a[text()='Logout']");
		
		return this;
	}
	
	public CustomerBasePage clickOnCustomersTab(){
		item.click("//a[contains(@title,'Customers Tab')]");
		return new CustomerBasePage();		
	}

}