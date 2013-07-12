package com.gainsight.sfdc.customer.pages;

import com.gainsight.sfdc.pages.BasePage;

public class CustomerBasePage extends BasePage {
	private final String READY_INDICATOR="//a[text()='Analytics']";

	public CustomerBasePage() {
	wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public CustomersPage clickOnCustomersSubTab(){
		link.click("//a[text()='Customers']");
		return new CustomersPage();		
	}
	public AnalyticsPage clickOnAnalyticsTab(){
		link.click("//a[text()='Analytics']");
		return new AnalyticsPage();		
	}


}
