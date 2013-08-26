package com.gainsight.sfdc.customer.pages;

import com.gainsight.sfdc.pages.BasePage;

public class CustomerBasePage extends BasePage {
	private final String READY_INDICATOR="//a[text()='Analytics']";

	public CustomerBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public CustomersPage clickOnCustomersSubTab(){
		item.click("//div[@id='CustomersTabs']//a[text()='Customers']");
		return new CustomersPage();		
	}
	
	public AnalyticsPage clickOnAnalyticsTab(){
		item.click("//a[text()='Analytics']");
		return new AnalyticsPage();		
	}


}
