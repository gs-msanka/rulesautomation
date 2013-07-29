package com.gainsight.sfdc.adoption.pages;

import com.gainsight.sfdc.customer.pages.AnalyticsPage;
import com.gainsight.sfdc.pages.BasePage;

public class AdoptionBasePage extends BasePage {
	private final String READY_INDICATOR="//a[text()='Analytics']";

	public AdoptionBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public AdoptionUsagePage clickOnUsageSubTab(){
		item.click("//a[text()='Usage']");
		return new AdoptionUsagePage();		
	} 
	
	public AnalyticsPage clickOnAnalyticsTab(){
		item.click("//a[text()='Analytics']");
		return new AnalyticsPage();		
	}
}
