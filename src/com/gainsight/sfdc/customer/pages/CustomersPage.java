package com.gainsight.sfdc.customer.pages;

import com.gainsight.pageobject.core.Report;

public class CustomersPage extends CustomerBasePage {
	private final String READY_INDICATOR = "//div[@id='Customers']";
	private final String NEW_BUTTON = "//input[@value='New']";
	private final String ACCT_NAME_TEXT = "//input[@class='jbaraDummyAccountName']";
	private final String ACCT_NAME_FIELD = "//input[@class='jbaraDummyAccountName']";
	private final String ACCT_LOOKUP_IMG = "//img[@title='Account Name Lookup']";
	private final String SAVE_BUTTON = "//input[@value='Save']";
	private final String CUSTOMER_EDIT_LINK = "//table[@id='customerList_IdOfJBaraStandardView']//tr[%d]//a[text()='Edit']";
	private final String CUSTOMER_DEL_LINK = "//table[@id='customerList_IdOfJBaraStandardView']//tr[%d]//a[text()='Delete']";
	private final String CUSTOMER_TABLE = "//table[contains(@id,'customerList_IdOf') and @class='ui-jqgrid-btable']";
	private final String STATUS_FIELD = "//td[text()='Status: ']/following-sibling::td//select";
	private final String STAGE_FIELD = "//td[text()='Stage: ']/following-sibling::td//select";
	private final String COMMENTS_FIELD = "//textarea[@class='jbaraDummyCustomerInputCtrl jbaraDummyCustomerCommentInputCtrl']";
	private final String CUSTOMER_NAME_FIELD="CustomerLink";

	public CustomersPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public CustomersPage addCustomer(String customerName, String status,
			String stage, String comments) {
		item.click(NEW_BUTTON);
		wait.waitTillElementPresent(ACCT_NAME_TEXT, MIN_TIME, MAX_TIME);
		amtDateUtil.stalePause();
		field.setTextField(ACCT_NAME_FIELD, customerName);
		button.click(ACCT_LOOKUP_IMG);
		item.click("//a[text()='" + customerName + "']");
		fillFields(status, stage, comments);
		button.click(SAVE_BUTTON);
		wait.waitTillElementNotDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
		return this;
	}

	public CustomersPage editCustomer(String customerName, String status,
			String stage, String comments) {
		int rowNo = getRowNumberOfCustm(customerName, customerName);
		if (rowNo > 0) {
			item.click(String.format(CUSTOMER_EDIT_LINK, rowNo));
			fillFields(status, stage, comments);
			button.click(SAVE_BUTTON);
			wait.waitTillElementNotDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
		} else {
			throw new RuntimeException("Customer not found");
		}
		return this;
	}

	public boolean deleteCustomer(String customerName) {
		boolean status = false;
		int rowNo = getRowNumberOfCustm(customerName, customerName);
		if (rowNo > 0) {
			item.click(String.format(CUSTOMER_DEL_LINK, rowNo));
			amtDateUtil.stalePause();
			modal.accept();
			amtDateUtil.stalePause();
			try {
				modal.accept();
				Report.logInfo("Modal dialog present ,Customer can't be deleted");
			} catch (Exception e) { //need to change it to exact exception type
				Report.logInfo("Modal dialog not present ,Customer can be deleted");
				status = true;
			}
		} else {
			throw new RuntimeException("Customer not found");
		}
		return status;
	}

	public boolean isCustomerPresent(String customerName) {
		return isCustomerPresent(customerName, customerName);
	}

	public boolean isCustomerPresent(String customerName, String values) {
		int attemptNo = 0;
		boolean status = false;
		while (attemptNo < 2) {
			if (getRowNumberOfCustm(customerName, values) != -1) {
				status = true;
				break;
			}
			sleep(2);
			attemptNo++;
		}
		return status;
	}

	private int getRowNumberOfCustm(String customerName, String values) {
		setFilter(CUSTOMER_NAME_FIELD, customerName);
		amtDateUtil.stalePause();
		return table.getValueInListRow(CUSTOMER_TABLE, values);
	}

	private void fillFields(String status, String stage, String comments) {
		field.setSelectField(STATUS_FIELD, status);
		field.setSelectField(STAGE_FIELD, stage);
		if (comments != null)
			item.setText(COMMENTS_FIELD,
					comments);
	}
}
