package com.gainsight.sfdc.transactions.pages;

import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.pages.BasePage;

public class TransactionsPage extends BasePage {
	private final String READY_INDICATOR = "//div[@id='Transactions-Usage']";

	public TransactionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public TransactionsPage addNewBusiness(String[] dataArray) {
		// fields that need to be read more than once

		String customerName = dataArray[0];
		String opportunity = dataArray[1];
		String bookingType = dataArray[2];
		String bookingDate = dataArray[3];
		String startDate = dataArray[4];
		String endDate = dataArray[5];
		String mrr = dataArray[6];
		String asv = dataArray[7];
		String userCount = dataArray[8];
		String otr = dataArray[9];
		String comments = dataArray[10];

		// set field values
		addTransaction_init(customerName,opportunity, bookingType);
		if (bookingDate != null && !bookingDate.isEmpty()) {
			enterDate(
					"//input[@class='transactionDate transactionBookingdate']",
					bookingDate);

		}
		if (startDate != null && !startDate.isEmpty()) {
			stalePause();
			enterDate("//input[@class='transactionDate transSubStartDate']",
					startDate);

		}
		enterDate("//input[@class='transactionDate transSubEndDate']", endDate);
		if (mrr != null && !mrr.isEmpty())
			field.setTextField("//input[@class='recurringMRR lineItemVal']",
					mrr);
		if (asv != null && !asv.isEmpty())
			field.setTextField("//input[@class='recurringASV lineItemVal']",
					asv);
		if (userCount != null && !userCount.isEmpty())
			field.setTextField("//input[@class='lineItemVal lineItemUsers']",
					userCount);
		if (otr != null && !otr.isEmpty())
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

	private void addTransaction_init(String customerName,String opportunity,String bookingType) {
		element.click("//input[@value='New']");
		element.switchToFrame("//iframe");
		field.setTextField("//input[contains(@class,'customer-name-text')]",
				customerName);
		button.click("//img[@title='Customer Name Lookup']");
		element.click("//a[text()='" + customerName + "']");
		if (opportunity != null && !opportunity.isEmpty())
			field.setSelectField(
					"//input[@class='jbaraDummyOpportunitySelectCtrl']",
					opportunity);
		if (bookingType != null && !bookingType.isEmpty())
			field.setSelectField(
					"//input[@class='jbaraDummyBookingOrderSelectCtrl']",
					bookingType);

	}

	private void addTransaction_fini(String comments) {
		if (comments != null && !comments.isEmpty())
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
