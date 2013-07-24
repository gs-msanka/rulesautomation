package com.gainsight.sfdc.pages;

import java.text.DecimalFormat;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.customer.pages.CustomerBasePage;
import com.gainsight.sfdc.transactions.pages.TransactionsBasePage;

/**
 * Base Class to hold all the Top Level Navigations
 * @author gainsight1
 *
 */
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
	
	//Start of Top Level Navigation
	public CustomerBasePage clickOnCustomersTab(){
		item.click("//a[contains(@title,'Customers Tab')]");
		return new CustomerBasePage();		
	}
	
	public TransactionsBasePage clickOnTransactionTab(){
		item.click("//a[contains(@title,'Transactions Tab')]");
		return new TransactionsBasePage();		
	}
	//End of Top Level Navigation
	
	public void setFilter(String filterFiledName, String value) {
		field.clearAndSetText("//input[@name='" + filterFiledName + "']", value);	
	}
	
	public String currencyFormat(String amt){
		DecimalFormat moneyFormat = new DecimalFormat("$0");
		return moneyFormat.format(new Long(amt));
	}
}