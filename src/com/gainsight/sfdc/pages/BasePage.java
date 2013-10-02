package com.gainsight.sfdc.pages;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.accounts.pages.AccountsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionBasePage;
import com.gainsight.sfdc.churn.pages.ChurnPage;
import com.gainsight.sfdc.customer.pages.CustomerBasePage;
import com.gainsight.sfdc.helpers.AmountsAndDatesUtil;
import com.gainsight.sfdc.helpers.Transactions;
import com.gainsight.sfdc.transactions.pages.TransactionsBasePage;
import com.gainsight.sfdc.opportunities.pages.OpportunitiesPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;

/**
 * Base Class to hold all the Top Level Navigations
 * 
 * @author gainsight1
 * 
 */
public class BasePage extends WebPage implements Constants {
	private final String READY_INDICATOR = "//div[@id='userNavButton']";
    private final String OPPORTUNITIES_TAB="//img[@title='Opportunities']";
    private final String ALL_TABS="//img[@title='All Tabs']";
    private final String ACCOUNTS_TAB="//a[@title='Accounts Tab']";
	public Transactions transactionUtil=new Transactions();
	public AmountsAndDatesUtil amtDateUtil=new AmountsAndDatesUtil();

	public BasePage login() {
		field.setTextField("username", TestEnvironment.get().getUserName());
		field.setTextField("password", TestEnvironment.get().getUserPassword());
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);

		return this;
	}

	public BasePage login(String username, String pwd) {
		field.setTextField("username", username);
		field.setTextField("password", pwd);
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);

		return this;
	}

	public BasePage logout() {
		item.click("userNavButton");
		item.click("//a[text()='Logout']");

		return this;
	}
	
	public void comeOutOfIframe() {
		element.switchToMainWindow();
	}

	// Start of Top Level Navigation
	public CustomerBasePage clickOnCustomersTab() {
		item.click("//a[contains(@title,'Customers Tab')]");
		return new CustomerBasePage();
	}

	public TransactionsBasePage clickOnTransactionTab() {
		item.click("//a[contains(@title,'Transactions Tab')]");
		return new TransactionsBasePage();
	}

	public AdoptionBasePage clickOnAdoptionTab() {
		item.click("//a[contains(@title,'Adoption Tab')]");
		return new AdoptionBasePage();
	}

	public SurveyBasePage clickOnSurveyTab() {
		item.click("//a[contains(text(),'Survey')]");
		return new SurveyBasePage();
	}
	
	public ChurnPage clickOnChurnTab() {
		item.click("//a[contains(@title,'Churn Tab')]");
		return new ChurnPage();
	}
	
	public OpportunitiesPage clickOnOpportunitiesTab() {
		if(!field.isElementPresent(OPPORTUNITIES_TAB)){
			item.click(ALL_TABS);				
		}
		item.click(OPPORTUNITIES_TAB);		
		return new OpportunitiesPage();
	}
	public AccountsPage clickOnAccountsTab() {
		item.click(ACCOUNTS_TAB);		
		return new AccountsPage();
	}

	public void setFilter(String filterFiledName, String value) {
		field.setTextByKeys("//input[@name='" + filterFiledName + "']", value);
	}


}