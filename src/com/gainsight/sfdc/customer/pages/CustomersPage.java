package com.gainsight.sfdc.customer.pages;

public class CustomersPage extends CustomerBasePage {
	private final String READY_INDICATOR = "//div[@id='Customers']";

	public CustomersPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public CustomersPage addCustomer(String customerName,String status,String stage,String comments) {
		
		item.click("//input[@value='New']");
		wait.waitTillElementPresent("//input[@class='jbaraDummyAccountName']",
				MIN_TIME, MAX_TIME);
		field.setTextField("//input[@class='jbaraDummyAccountName']",
				customerName);
		button.click("//img[@title='Account Name Lookup']");
		item.click("//a[text()='" + customerName + "']");
		field.setSelectField(
				"//td[text()='Status: ']/following-sibling::td//select", status);
		field.setSelectField(
				"//td[text()='Stage: ']/following-sibling::td//select", stage);
		button.click("//input[@value='Save']");
		wait.waitTillElementNotDisplayed("//input[@value='Save']", MIN_TIME, MAX_TIME);
		return this;
	}

	public boolean isCustomerPresent(String customerName) {
		int attemptNo = 1;
		boolean status = false;
		setFilter("CustomerLink", customerName);
		stalePause();
		while (attemptNo < 4) {
			int customerRownNo = table.getValueInListRow(
					"customerList_IdOfJBaraStandardView", customerName);
			if (customerRownNo != -1) {
				status = true;
				break;
			}
			sleep(2);
			attemptNo++;
		}
		return status;
	}
}
