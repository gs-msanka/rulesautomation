package com.gainsight.sfdc.pages;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.adoption.pages.AdoptionBasePage;
import com.gainsight.sfdc.churn.pages.ChurnPage;
import com.gainsight.sfdc.customer.pages.CustomerBasePage;
import com.gainsight.sfdc.transactions.pages.TransactionsBasePage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;

/**
 * Base Class to hold all the Top Level Navigations
 * 
 * @author gainsight1
 * 
 */
public class BasePage extends WebPage implements Constants {
	private final String READY_INDICATOR = "//div[@id='userNavButton']";

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

	// End of Top Level Navigation

	public void setFilter(String filterFiledName, String value) {
		field.setTextByKeys("//input[@name='" + filterFiledName + "']", value);
	}

	public String currencyFormat(String amt) {
		DecimalFormat moneyFormat = new DecimalFormat("$0");
		return moneyFormat.format(new Long(amt));
	}

	public void enterDate(String identifier, String date) {
		field.click(identifier);
		field.click("//td[@class='weekday']");
		field.clearAndSetText(identifier, date);
	}

	/**
	 * Even we are handling stale element exceptions at framework level it is
	 * time consuming hence if see any element happens to bound to this
	 * exception because of frequent DOM updates, please call this method before
	 * performing any action on that element so that webdriver finds elements
	 * after DOM got updated( 2 seconds is optimal time)
	 */
	public void stalePause() {
		sleep(2);
	}

	public String getFormattedDate(String dateStr) {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date;
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("unable to  parse date string "
					+ e.getMessage());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		return sdf.format(date);
	}

}