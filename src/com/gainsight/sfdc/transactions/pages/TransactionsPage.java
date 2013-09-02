package com.gainsight.sfdc.transactions.pages;

import java.util.HashMap;

import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.pages.BasePage;

public class TransactionsPage extends BasePage {
	private final String READY_INDICATOR = "//div[@id='Transactions-Usage']";

	public TransactionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public TransactionsPage addNewBusiness(HashMap<String, String> data) {
		// fields that need to be read more than once

		String customerName = data.get("customerName");
		String opportunity = data.get("opportunity");
		String bookingType = data.get("bookingType");
		String bookingDate = data.get("bookingDate");
		String startDate = data.get("startDate");
		String endDate = data.get("endDate");
		String mrr = data.get("mrr");
		String asv = data.get("asv");
		String userCount = data.get("userCount");
		String otr = data.get("otr");
		String comments = data.get("comments");

		// set field values
		addTransaction_init(customerName, opportunity, bookingType);
		if (!bookingDate.equals("nil")) {
			enterDate(
					"//input[@class='transactionDate transactionBookingdate']",
					bookingDate);

		}
		if (!startDate.equals("nil")) {
			stalePause();
			enterDate("//input[@class='transactionDate transSubStartDate']",
					startDate);

		}
		enterDate("//input[@class='transactionDate transSubEndDate']", endDate);
		if (!mrr.equals("nil"))
			field.setTextField("//input[@class='recurringMRR lineItemVal']",
					mrr);
		if (!asv.equals("nil"))
			field.setTextField("//input[@class='recurringASV lineItemVal']",
					asv);
		if (!userCount.equals("nil"))
			field.setTextField("//input[@class='lineItemVal lineItemUsers']",
					userCount);
		if (!otr.equals("nil"))
			field.setTextField(
					"//input[@class='lineItemVal lineItemOnetimeRevenue']", otr);
		addTransaction_fini(comments);

		return this;
	}

	public boolean isTransactionPresent(String customerName, String values) {
		int attemptNo = 1;
		boolean status = false;
		setFilter("Customer_link", customerName);
		stalePause();
		while (attemptNo < 4) {
			int rowNo = table.getValueInListRow(
					"transactionList_IdOfJBaraStandardView", values);
			if (rowNo != -1) {
				status = true;
				break;
			}
			attemptNo++;
			sleep(3);
		}
		return status;

	}

	public Customer360Page selectCustomer(String customerName) {
		setFilter("Customer_link", customerName);
		stalePause();
		item.click("//a[text()='" + customerName + "']");
		return new Customer360Page();
	}

	private void addTransaction_init(String customerName, String opportunity,
			String bookingType) {
		element.click("//input[@value='New']");
		element.switchToFrame("//iframe");
		field.setTextField("//input[contains(@class,'customer-name-text')]",
				customerName);
		button.click("//img[@title='Customer Name Lookup']");
		element.click("//a[text()='" + customerName + "']");
		if (!opportunity.equals("nil"))
			field.setSelectField(
					"//input[@class='jbaraDummyOpportunitySelectCtrl']",
					opportunity);
		if (!bookingType.equals("nil"))
			field.setSelectField(
					"//input[@class='jbaraDummyBookingOrderSelectCtrl']",
					bookingType);

	}

	private void addTransaction_fini(String comments) {
		if (!comments.equals("nil"))
			field.setTextField(
					"//textarea[@class='jbaraDummyCommentInputCtrl transactionComments']",
					comments);
		button.click("//a[text()='Save']");
		wait.waitTillElementPresent(
				"//div[@class='gridPanelDiv' and contains(@style,'inline')]",
				MIN_TIME, MAX_TIME);
		element.switchToMainWindow();

	}

}
