package com.gainsight.sfdc.transactions.pages;

import com.gainsight.sfdc.pages.BasePage;

public class TransactionsBasePage extends BasePage{
	
	private final String READY_INDICATOR="//div[@id='tabs']//a[text()='Analytics']";

	public TransactionsBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public TransactionsPage clickOnTransactionsSubTab(){
		item.click("//div[@id='tabs']//a[text()='Transactions']");
		return new TransactionsPage();		
	}
	
	public  AnalyticsPage clickOnAnalyticsTab(){
		item.click("//div[@id='tabs']//a[text()='Analytics']");
		return new AnalyticsPage();		
	}

}
