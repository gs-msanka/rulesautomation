package com.gainsight.sfdc.administration.pages;

import com.gainsight.sfdc.pages.BasePage;

public class AdministrationBasepage extends BasePage{

	
	// private final String 
	
	public AdministrationBasepage() {
		//wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	
	public AdminRetentionTab clickOnRetentionSubTab(){
		item.click("//a[contains(@href,'Administration-Retention')]"); 
		return new AdminRetentionTab();		
	}
	
	public AdminNPSTab clickOnNpsSubTab(){
		item.click("//a[contains(@href,'Administration-NPS')]"); 
		return new AdminNPSTab();		
	}
	
	public AdminCustomersTab clickOnCustomersSubTab(){
		item.click("//a[contains(@href,'Administration-Customers')]"); 
		return new AdminCustomersTab();		
	}
	
	public AdminTransactionsTab clickOnTransactionsTab()
	{
		item.click("//a[contains(@href,'Administration-Transactions')]");
		return new AdminTransactionsTab();
	}

	public AdminAdoptionSubTab clickOnAdoptionSubTab()
	{
		item.click("//a[contains(@href,'Administration-Adoption')]");
		
		return new AdminAdoptionSubTab();
	}
	
	public AdminMilestoneTab clickOnMilestoneTab()
	{
		
		item.click("//a[contains(@href,'Administration-Milestone')]");
		return new AdminMilestoneTab();
	}
	
	/*public AdminUISettingsTab ClickOnUISettingsTab()
	{
		
		item.click("//a[contains(@href,'Administration-UI Settings')]");
		
		return new AdminUISettingsTab();
	}*/
	
	public AdminFeaturesSubTab ClickOnFeaturesTab()
	{
		
		item.click("//a[contains(@href,'Administration-Features')]");
		
		return new AdminFeaturesSubTab();
	}
	
	public AdminNotificationsSubTab ClickOnNotificationSubTab()
	{
		
		item.click("//a[contains(@href,'Administration-Notifications')]");
		
		return new AdminNotificationsSubTab();
	}
	
	/*public AdminUIViewssSubTab ClickOnUIViewssettingsSubTab()
	{
		
		item.click("//a[contains(@href,'Administration-UIViews')]");
		
		return new AdminUIViewssSubTab();
	}*/
	
	
}
