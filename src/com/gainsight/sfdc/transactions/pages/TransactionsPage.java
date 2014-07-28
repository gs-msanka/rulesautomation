package com.gainsight.sfdc.transactions.pages;

import java.util.HashMap;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.pages.BasePage;

public class TransactionsPage extends BasePage {
	private final String READY_INDICATOR = "//div[@class='gs-moreopt-btn']";
	private final String BOOKING_DATE_FIELD = "//input[@class= 'transactionDate transactionBookingdate gs-calendar']";
	private final String START_DATE_FIELD = "//input[@class='transactionDate transSubStartDate gs-calendar']";
	private final String TRANS_GRID = "//div[contains(@id,'gbox_transactionList_IdOf')]";
	private final String TRANS_DLT_LINK = "//a[@class='deleteIconTiny commonSpecsIcons']";
	private final String CHURN_REASON_SELECT = "//select[@class='churnReasonSelectCtrl']";
	private final String TRANS_EDIT_LINK = "//table[contains(@id,'transactionList_IdOf')]//tr[%d]//a[text()='Edit']";
	private final String NEW_BUTTON = "//a[contains(text(), 'New Transaction')]";
	private final String TRANS_TABLE = "//table[contains(@id,'transactionList_Id') and @class='ui-jqgrid-btable']";
	private final String CUSTOMER_FILTER = "Customer_link";
	private final String VIEW="//label/span[text()='%s']";
	private final String VIEW_BUTTON="//span[@class='TransUIViewsSelectionList']/button";

	public TransactionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public TransactionsPage addChurnTransaction(HashMap<String, String> data) {
		addTransactionInit();
		transactionUtil.addChurnTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage addDebookTransaction(HashMap<String, String> data) {
		addTransactionInit();
		transactionUtil.addDebookTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage addDownsellTransaction(HashMap<String, String> data) {
		addTransactionInit();
		transactionUtil.addDownsellTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage addNewBusiness(HashMap<String, String> data) {
		addTransactionInit();
		transactionUtil.addNewBusiness(data);
		transactionFini();
		return this;
	}

	public TransactionsPage addRenewalTransaction(HashMap<String, String> data) {
		addTransactionInit();
		transactionUtil.addRenewalTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage addUpsellTransaction(HashMap<String, String> data) {
		addTransactionInit();
		transactionUtil.addUpsellTransaction(data);
		transactionFini();
		return this;
	}

	private void transactionFini() {
		element.switchToMainWindow();
		wait.waitTillElementPresent(TRANS_GRID, MIN_TIME, MAX_TIME);	
	}

	private void addTransactionInit() {
		element.click(READY_INDICATOR);
		element.click(NEW_BUTTON);
		element.switchToFrame("//iframe[contains(@src,'apex/TransactionForm')]");
	}

	public TransactionsPage deleteTransaction(String customerName, String values) {
		int rowNo = getRowNumberOfTran(customerName, values);
		if (rowNo>=0){
		element.click(String.format(TRANS_DLT_LINK, rowNo));
		amtDateUtil.stalePause();
		modal.accept();
		return this;
		}
		else {
			throw new RuntimeException("Transaction not found : with "+ customerName+" "+values);
		}
		
	}

	public TransactionsPage editChurnTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		fillChurnFields(data.get("bookingDate"), data.get("effectiveDate"),
				data.get("reason"));
		transactionFini();
		return this;
	}

	private void fillChurnFields(String bookingDate, String effectiveDate,
			String reason) {
		if (bookingDate != null)
			enterDate(BOOKING_DATE_FIELD, bookingDate);
		if (effectiveDate != null)
			enterDate(START_DATE_FIELD, effectiveDate);
		if (reason != null)
			field.setSelectField(CHURN_REASON_SELECT, reason);
	}

	public TransactionsPage editNewBusinessTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		transactionUtil.addChurnTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage editUpsellTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		transactionUtil.addUpsellTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage editDownsellTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		transactionUtil.addDownsellTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage editDebookTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		transactionUtil.addDebookTransaction(data);
		transactionFini();
		return this;
	}

	public TransactionsPage editRenewalransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		transactionUtil.addRenewalTransaction(data);
		transactionFini();
		return this;
	}

	private void editTransactionInit(String customerName, String keyValues) {
		int rowNo = getRowNumberOfTran(customerName, keyValues);
		element.click(String.format(TRANS_EDIT_LINK, rowNo));
		element.click(NEW_BUTTON);
		element.switchToFrame("//iframe");
	}

	private int getRowNumberOfTran(String customerName, String values) {
		setFilter(CUSTOMER_FILTER, customerName);
		amtDateUtil.stalePause();
		return table.getValueInListRow(TRANS_TABLE, values);
	}

	public boolean isTransactionPresent(String customerName, String values) {
		if (getRowNumberOfTran(customerName, values) == -1) {
			return false;
		} else {
			return true;
		}
	}

	public Customer360Page gotoCustomer360(String customerName) {
		setFilter(CUSTOMER_FILTER, customerName);
		amtDateUtil.stalePause();
		item.click("//a[text()='" + customerName + "']");
		return new Customer360Page();
	}
	
	public TransactionsPage selectView(String viewName){
		item.click(VIEW_BUTTON);
		item.click(String.format(VIEW, viewName));
		wait.waitTillElementDisplayed(TRANS_GRID, MIN_TIME, MAX_TIME);
		return this;
		
	}

}