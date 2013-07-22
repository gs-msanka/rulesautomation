package com.gainsight.sfdc.customer.pages;

public class CustomersPage extends CustomerBasePage {
	private final String READY_INDICATOR = "//div[@id='Customers']";

	public CustomersPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public CustomersPage addCustomer(String customerName, String status,
			String stage, String comments) {
		item.click("//input[@value='New']");
        wait.waitTillElementPresent("//input[@class='jbaraDummyAccountName']", MIN_TIME, MAX_TIME);
        field.setTextField("//input[@class='jbaraDummyAccountName']",
                        customerName);
        button.click("//img[@title='Account Name Lookup']");
        item.click("//a[text()='" + customerName + "']");
        field.setSelectField(
                        "//td[text()='Status: ']/following-sibling::td//select", status);
        field.setSelectField(
                        "//td[text()='Stage: ']/following-sibling::td//select", stage);
        button.click("//input[@value='Save']");
        return this;
	}

	public boolean isCustomerPresent(String customerName) {
		setFilter("CustomerLink", customerName);
		sleep(2);
		int customerRownNo = table.getValueInListRow("customerList_IdOfJBaraStandardView", customerName);
		if (customerRownNo == -1)
			return false;
		else
			return true;
	}

	public CustomersPage setFilter(String filterFiledName, String value) {
		field.setTextField("//input[@name='" + filterFiledName + "']", value);
		return this;
	}

}
