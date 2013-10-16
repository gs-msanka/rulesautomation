package com.gainsight.sfdc.pages;

import java.net.URI;
import java.net.URISyntaxException;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.accounts.pages.AccountsPage;
import com.gainsight.sfdc.administration.pages.AdministrationBasepage;
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
	private final String OPPORTUNITIES_TAB = "//img[@title='Opportunities']";
	private final String ALL_TABS = "//img[@title='All Tabs']";
	private final String ACCOUNTS_TAB = "//a[@title='Accounts Tab']";
	private final String DEFAULT_APP_RADIO = "//td[text()='%s']/following-sibling::td//input[@type='radio']";
	private final String TAB_SELECT = "//td[contains(@class,'labelCol requiredInput') and contains(.,'%s')]//following-sibling::td//select";
	public Transactions transactionUtil = new Transactions();
	public AmountsAndDatesUtil amtDateUtil = new AmountsAndDatesUtil();

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

	public void beInMainWindow() {
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
		if (!field.isElementPresent(OPPORTUNITIES_TAB)) {
			item.click(ALL_TABS);
		}
		item.click(OPPORTUNITIES_TAB);
		return new OpportunitiesPage();
	}

	public AccountsPage clickOnAccountsTab() {
		item.click(ACCOUNTS_TAB);
		return new AccountsPage();
	}

	public AdministrationBasepage clickOnAdminTab() {
		driver.manage().window().maximize();
		item.click("//a[contains(@title,'Administration')]");
		return new AdministrationBasepage();
	}

	public void setFilter(String filterFiledName, String value) {
		field.setTextByKeys("//input[@name='" + filterFiledName + "']", value);
	}

	public void setDefaultApplication(String appName) {
		item.click("userNavButton");
		link.clickLink("Setup");
		wait.waitTillElementPresent("//a[text()='Manage Users']", MIN_TIME,
				MAX_TIME);
		link.clickLink("Manage Users");
		link.clickLink("Profiles");
		wait.waitTillElementPresent("//a[contains(.,'System Administrator')]",
				MIN_TIME, MAX_TIME);
		item.click("//td[@id='bodyCell']//tr[contains(.,'System Administrator')]//a[contains(.,'Edit')]");
		wait.waitTillElementPresent("//input[@title='Save']", MIN_TIME,
				MAX_TIME);
		item.click(String.format(DEFAULT_APP_RADIO, appName));
		field.selectFromDropDown(String.format(TAB_SELECT, "Administration"),
				"Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Adoption"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Churn"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Customers"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "NPS"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Retention"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Survey"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Transactions"), "Default On");
		item.click("//input[@title='Save']");
		wait.waitTillElementPresent("//a[contains(.,'System Administrator')]",
				MIN_TIME, MAX_TIME);
	}

	public void loadDefaultData() throws URISyntaxException {
		URI uri=new URI(driver.getCurrentUrl());
		String hostName="https://"+uri.getHost();
		driver.get(hostName+"/apex/loadsetupdata");
		String intializeButton="//input[@value='INITIALIZE']";
		if(element.isElementPresent(intializeButton)){
			item.click(intializeButton);
			wait.waitTillElementDisplayed("loadSetupDatatable", MIN_TIME, MAX_TIME);
		}
		driver.get(hostName+"/apex/loadsampledata");
		String loadButton="//input[@value='Load']";
		if(element.isElementPresent(loadButton)){
			item.click(loadButton);
			wait.waitTillElementDisplayed("loadSetupDatatable", MIN_TIME, MAX_TIME);
		}
		
	}
}