package com.gainsight.sfdc.pages;

import java.net.URI;
import java.net.URISyntaxException;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.accounts.pages.AccountsPage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.administration.pages.AdministrationBasepage;
import com.gainsight.sfdc.adoption.pages.AdoptionBasePage;
import com.gainsight.sfdc.churn.pages.ChurnPage;
import com.gainsight.sfdc.customer.pages.CustomerBasePage;
import com.gainsight.sfdc.helpers.AmountsAndDatesUtil;
import com.gainsight.sfdc.helpers.Transactions;
import com.gainsight.sfdc.opportunities.pages.OpportunitiesPage;
import com.gainsight.sfdc.retention.pages.RetentionBasePage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.transactions.pages.TransactionsBasePage;

/**
 * Base Class to hold all the Top Level Navigations
 * 
 * @author gainsight1
 * 
 */
public class BasePage extends WebPage implements Constants {
	private final String READY_INDICATOR    = "//div[@id='userNavButton']";
	private final String OPPORTUNITIES_TAB  = "//img[@title='Opportunities']";
	private final String ALL_TABS           = "//img[@title='All Tabs']";
	private final String ACCOUNTS_TAB       = "//a[@title='Accounts Tab']";
	private final String DEFAULT_APP_RADIO  = "//td[text()='%s']/following-sibling::td//input[@type='radio']";
	private final String TAB_SELECT         = "//td[contains(@class,'labelCol requiredInput') and contains(.,'%s')]//following-sibling::td//select";
	private final String C360_TAB           = "//a[contains(@title,'Customer Success 360')]";
    private final String CUSTOMER_TAB       = "//a[contains(@title,'Customers Tab')]";
    private final String TRANSACTIONS_TAB   = "//a[contains(@title,'Transactions Tab')]";
    private final String RETENTION_TAB      = "//a[contains(@title, 'Retention Tab')]";
    private final String CHURN_TAB          = "//a[contains(@title,'Churn Tab')]";
    private final String ADOPTION_TAB       = "//a[contains(@title,'Adoption Tab')]";
    private final String SUREVEY_TAB        = "//a[contains(text(),'Survey')]";
    private final String ADMINISTRATION_TAB = "//a[contains(@title,'Administration')]";
    public Transactions transactionUtil     = new Transactions();
	public AmountsAndDatesUtil amtDateUtil  = new AmountsAndDatesUtil();

	public BasePage login() {
		if(!driver.getCurrentUrl().contains("login")){
			driver.get(env.getDefaultUrl());
		}
		field.setTextField("username", TestEnvironment.get().getUserName());
		field.setTextField("password", TestEnvironment.get().getUserPassword());
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		return this;
	}

	public BasePage login(String username, String pwd) {
		if(!driver.getCurrentUrl().contains("login")){
			driver.get(env.getDefaultUrl());
		}
		field.setTextField("username", username);
		field.setTextField("password", pwd);
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		return this;
	}

	public BasePage logout() {
		element.switchToMainWindow();
		item.click("userNavButton");
		item.click("//a[text()='Logout']");
		return this;
	}

	public void beInMainWindow() {
		element.switchToMainWindow();
	}

	// Start of Top Level Navigation
	public CustomerBasePage clickOnCustomersTab() {
		item.click(CUSTOMER_TAB);
		return new CustomerBasePage();
	}

	public TransactionsBasePage clickOnTransactionTab() {
		item.click(TRANSACTIONS_TAB);
		return new TransactionsBasePage();
	}

	public AdoptionBasePage clickOnAdoptionTab() {
		item.click(ADOPTION_TAB);
		return new AdoptionBasePage();
	}

    public RetentionBasePage clickOnRetentionTab() {
        item.click(RETENTION_TAB);
        return new RetentionBasePage();
    }

	public SurveyBasePage clickOnSurveyTab() {
		item.click(SUREVEY_TAB);
		return new SurveyBasePage();
	}

	public ChurnPage clickOnChurnTab() {
		item.click(CHURN_TAB);
		return new ChurnPage();
	}
	public Customer360Page clickOnC360Tab() {
		item.click(C360_TAB);
		return new Customer360Page();
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
		item.click(ADMINISTRATION_TAB);
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
        field.selectFromDropDown(String.format(TAB_SELECT, "Customer Success 360"), "Default On");
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
			wait.waitTillElementDisplayed("loadSampleDatatable", MIN_TIME, MAX_TIME);
		}
		
	}

	public void goBack() {
		driver.navigate().back();
		
	}
}