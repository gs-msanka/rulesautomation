package com.gainsight.sfdc.administration.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.salesforce.pages.CreateSalesforceUsers;

public class AdministrationBasepage extends BasePage{

	
	
	
	public AdministrationBasepage() {
		//wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public CreateSalesforceUsers clickOnSetup(){
		item.click("//a[@id='setupLink' and @title='Setup']"); 
		return new CreateSalesforceUsers();		
	}
	//a[@id='setupLink' and @title='Setup']
	
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
	
	
	public AdminFeaturesSubTab clickOnFeaturesTab()
	{
		
		item.click("//a[contains(@href,'Administration-Features')]");
		
		return new AdminFeaturesSubTab();
	}
	
	public AdminNotificationsSubTab clickOnNotificationSubTab()
	{
		
		item.click("//a[contains(@href,'Administration-Notifications')]");
		
		return new AdminNotificationsSubTab();
	}
	
	/*public AdminUIViewssSubTab clickOnUIViewssettingsSubTab()
	{
		
		item.click("//a[contains(@href,'Administration-UIViews')]");
		
		return new AdminUIViewssSubTab();
	}*/
	
	public AdminRulesEngineTab clickOnRulesEngineSubTab() {
	
		item.click("//a[contains(@href,'Administration-RulesEngine')]");
		
		return new AdminRulesEngineTab();
	}


	
	
	
	
	
	
	
	
	
}
