package com.gainsight.sfdc.transactions.pages;

import com.gainsight.sfdc.pages.BasePage;

public class TransactionsBasePage extends BasePage{
	
	private final String READY_INDICATOR="//a[@class='tab' and text()='Transactions']";

	public TransactionsBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public TransactionsPage clickOnTransactionsSubTab(){
		item.click("//a[@class='tab' and text()='Transactions']");
		return new TransactionsPage();		
	}
	
	public  AnalyticsPage clickOnAnalyticsTab(){
		item.click("//a[@class='tab' and text()='Analytics']");
		return new AnalyticsPage();		
	}

}
