package com.gainsight.sfdc.administration.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.salesforce.pages.CreateSalesforceUsers;

public class AdministrationBasePage extends BasePage {

    private final String READY_INDICATOR        = "//h1[@class='pageType noSecondHeader' and contains(text(),'Administration')]";
    private final String SCORECARD_SECTION      = "//a[contains(@href, 'scorecardsetup')]";
    private final String ADMIN_RETENTION        = "//a[contains(@href,'Administration-Retention')]";
    private final String ADMIN_NPS              = "//a[contains(@href,'Administration-NPS')]";
    private final String ADMIN_CUSTOMERS        = "//a[contains(@href,'Administration-Customers')]";
    private final String ADMIN_TRANSACTIONS     = "//a[contains(@href,'Administration-Transactions')]";
    private final String ADMIN_ADOPTION         = "//a[contains(@href,'Administration-Adoption')]";
    private final String ADMIN_MILESTONES       = "//a[contains(@href,'Administration-Milestone')]";
    private final String ADMIN_FEATURES         = "//a[contains(@href,'Administration-Features')]";
    private final String ADMIN_NOTIFICATION     = "//a[contains(@href,'Administration-Notifications')]";
    private final String ADMIN_UI_SETTINGS      = "//a[contains(@href,'Administration-UIViews')]";
    private final String ADMIN_RULES_ENGINE     = "//a[contains(@href,'Administration-RulesEngine')]";
    private final String SETUP                  = "//a[@id='setupLink' and @title='Setup']";

    public AdministrationBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public CreateSalesforceUsers clickOnSetup(){
		item.click(SETUP);
		return new CreateSalesforceUsers();		
	}

	public AdminRetentionTab clickOnRetentionSubTab(){
		item.click(ADMIN_RETENTION);
		return new AdminRetentionTab();		
	}
	
	public AdminNPSTab clickOnNpsSubTab(){
		item.click(ADMIN_NPS);
		return new AdminNPSTab();		
	}
	
	public AdminCustomersTab clickOnCustomersSubTab(){
		item.click(ADMIN_CUSTOMERS);
		return new AdminCustomersTab();		
	}
	
	public AdminTransactionsTab clickOnTransactionsTab() {
		item.click(ADMIN_TRANSACTIONS);
		return new AdminTransactionsTab();
	}

	public AdminAdoptionSubTab clickOnAdoptionSubTab(){
		item.click(ADMIN_ADOPTION);
		return new AdminAdoptionSubTab();
	}
	
	public AdminMilestoneTab clickOnMilestoneTab() {
		item.click(ADMIN_MILESTONES);
		return new AdminMilestoneTab();
	}
	
	public AdminFeaturesSubTab clickOnFeaturesTab() {
		item.click(ADMIN_FEATURES);
		return new AdminFeaturesSubTab();
	}
	
	public AdminNotificationsSubTab clickOnNotificationSubTab() {
        item.click(ADMIN_NOTIFICATION);
		return new AdminNotificationsSubTab();
	}
	
	public AdminUIViewsSubTab clickOnUIViewsSettingsSubTab() {
		item.click(ADMIN_UI_SETTINGS);
		return new AdminUIViewsSubTab();
	}
	
	public AdminRulesEngineTab clickOnRulesEngineSubTab() {
		item.click(ADMIN_RULES_ENGINE);
		return new AdminRulesEngineTab();
	}

	public AdminScorecardSection clickOnScorecardSection(){
		item.click(SCORECARD_SECTION);
		return new AdminScorecardSection();
	}
}
