package com.gainsight.sfdc.transactions.pages;

import java.util.HashMap;

import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.pages.BasePage;

public class TransactionsPage extends BasePage {
	private final String READY_INDICATOR = "//div[@id='Transactions-Usage']";
	private final String BOOKING_DATE_FIELD="//input[@class='transactionDate transactionBookingdate']";
	private final String START_DATE_FIELD="//input[@class='transactionDate transSubStartDate']";
	private final String MRR_FIELD="//input[@class='recurringMRR lineItemVal']";
	private final String ASV_FIELD="//input[@class='recurringASV lineItemVal']";
	private final String USERS_FIELD="//input[@class='lineItemVal lineItemUsers']";
	private final String OTR_FIELD="//input[@class='lineItemVal lineItemOnetimeRevenue']";
	private final String END_DATE_FIELD="//input[@class='transactionDate transSubEndDate']";
	private final String COMMENTS_FIELD="//textarea[@class='jbaraDummyCommentInputCtrl transactionComments']";
	private final String TRANS_GRID="//div[@class='gridPanelDiv' and contains(@style,'inline')]";
	private final String CUSTOMER_NAME_FIELD="//input[contains(@class,'customer-name-text')]";
	private final String NAME_LOOPUP_IMG="//img[@title='Customer Name Lookup']";
	private final String OPPRT_SELECT="//input[@class='jbaraDummyOpportunitySelectCtrl']";
	private final String BOOKING_TYPE_SELECT="//select[@class='jbaraDummyBookingOrderSelectCtrl']";
	private final String NEW_RADIO="//input[@type='radio' and @value='new']";
	private final String EXISTING_RADIO="//input[@type='radio' and @value='existing']";
	private final String TRANS_DLT_LINK="//table[@id='transactionList_IdOfJBaraStandardView']//tr[%d]//a[text()='Delete']";
	private final String CHURN_REASON_SELECT="//select[@class='churnReasonSelectCtrl']";
	private final String TRANS_EDIT_LINK="//table[@id='transactionList_IdOfJBaraStandardView']//tr[%d]//a[text()='Edit']";
	private final String  NEW_BUTTON="//input[@value='New']";
	private final String TRANS_TABLE="transactionList_IdOfJBaraStandardView";
	private final String CUSTOMER_FILTER="Customer_link";

	public TransactionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public TransactionsPage addChurnTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fileChurnFields(data.get("bookingDate"), data.get("effectiveDate"),
				data.get("reason"));
		transactionFini(data.get("comments"));
		return this;
	}

	public TransactionsPage addDebookTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fillDebookFields(data.get("transaction"), data.get("bookingDate"),
				data.get("effectiveDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}

	private void fillDebookFields(String transaction, String bookingDate,
			String effectiveDate) {
		if (!transaction.equals("nil"))
			item.click(getXpathForTran(transaction, "2"));
		if (!bookingDate.equals("nil"))
			enterDate(
					BOOKING_DATE_FIELD,
					bookingDate);
		if (!effectiveDate.equals("nil"))
			enterDate(START_DATE_FIELD,
					effectiveDate);
	}

	public TransactionsPage addDownsellTransaction(HashMap<String, String> data) {		
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fillDownsellFields(data.get("transaction"),data.get("bookingDate"),data.get("effectiveDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}
	
	private void fillDownsellFields(String transaction,String bookingDate,String effectiveDate){
		if (!transaction.equals("nil"))
		item.click(getXpathForTran(transaction, "2"));
		if (!bookingDate.equals("nil"))
			enterDate(
					BOOKING_DATE_FIELD,
					bookingDate);		
		if(!effectiveDate.equals("nil"))
		enterDate(START_DATE_FIELD,
				effectiveDate);		
	}

	public TransactionsPage addNewBusiness(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}

	public TransactionsPage addRenewalTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		item.click(getXpathForTran(data.get("transaction"), "1"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}

	private void addTransactionAmts(String mrr, String asv, String userCount,
			String otr) {
		if (!mrr.equals("nil"))
			field.setTextField(MRR_FIELD,
					mrr);
		if (!asv.equals("nil"))
			field.setTextField(ASV_FIELD,
					asv);
		if (!userCount.equals("nil"))
			field.setTextField(USERS_FIELD,
					userCount);
		if (!otr.equals("nil"))
			field.setTextField(
					OTR_FIELD, otr);
	}

	private void addTransactionDates(String bookingDate, String startDate,
			String endDate) {
		if (!bookingDate.equals("nil")) {
			enterDate(
					BOOKING_DATE_FIELD,
					bookingDate);
		}
		if (!startDate.equals("nil")) {
			stalePause();
			enterDate(START_DATE_FIELD,
					startDate);
		}
		if (!endDate.equals("nil"))
			enterDate(END_DATE_FIELD,
					endDate);

	}

	private void transactionFini(String comments) {
		if (!comments.equals("nil"))
			field.setTextField(
					COMMENTS_FIELD,
					comments);
		button.click("//a[text()='Save']");
		wait.waitTillElementPresent(
				TRANS_GRID,
				MIN_TIME, MAX_TIME);
		element.switchToMainWindow();
	}

	private void addTransactionInit(String customerName, String opportunity,
			String bookingType) {
		element.click(NEW_BUTTON);
		element.switchToFrame("//iframe");
		field.setTextField(CUSTOMER_NAME_FIELD,
				customerName);
		button.click(NAME_LOOPUP_IMG);
		element.click("//a[text()='" + customerName + "']");
		if (!opportunity.equals("nil"))
			field.setSelectField(
					OPPRT_SELECT,
					opportunity);
		if (!bookingType.equals("nil"))
			field.setSelectField(
					BOOKING_TYPE_SELECT,
					bookingType);
	}

	public TransactionsPage addUpsellTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fillUpsellFields(data.get("associationType"),data.get("transaction"));
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}
	private void fillUpsellFields(String associationType,String transaction){
		if (associationType.equalsIgnoreCase("new")) {
			field.click(NEW_RADIO);
		} else if(associationType.equalsIgnoreCase("existing")) {
			field.click(EXISTING_RADIO);
			item.click(getXpathForTran(transaction, "2"));
		}
	}

	public TransactionsPage deleteTransaction(String customerName, String values) {
		int rowNo = getRowNumberOfTran(customerName, values);
		element.click(String.format(TRANS_DLT_LINK,rowNo));
		stalePause();
		modal.accept();
		return this;
	}

	public TransactionsPage editChurnTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		fileChurnFields(data.get("bookingDate"), data.get("effectiveDate"),
				data.get("reason"));
		transactionFini(data.get("comments"));
		return this;
	}

	private void fileChurnFields(String bookingDate, String effectiveDate,
			String reason) {
		if (!bookingDate.equals("nil"))
			enterDate(
					BOOKING_DATE_FIELD,
					bookingDate);
		if (!effectiveDate.equals("nil"))
			enterDate(START_DATE_FIELD,
					effectiveDate);
		if (!reason.equals("nil"))
			field.setSelectField(CHURN_REASON_SELECT,
					reason);
	}

	public TransactionsPage editNewBusinessTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}

	public TransactionsPage editUpsellTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		String comments = data.get("comments");
		editTransactionInit(customerName, keyValues);
		transactionFini(comments);
		return this;
	}

	public TransactionsPage editDownsellTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		fillUpsellFields(data.get("associationType"),data.get("transaction"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}

	public TransactionsPage editDebookTransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		editTransactionInit(customerName, keyValues);
		fillDebookFields(data.get("transaction"), data.get("bookingDate"),
				data.get("effectiveDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;

	}

	public TransactionsPage editRenewalransaction(String customerName,
			String keyValues, HashMap<String, String> data) {
		String transaction=data.get("transaction");
		editTransactionInit(customerName, keyValues);
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		if(!transaction.equals("nil"))
		item.click(getXpathForTran(data.get("transaction"), "1"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));
		return this;
	}

	private void editTransactionInit(String customerName, String keyValues) {
		int rowNo = getRowNumberOfTran(customerName, keyValues);
		element.click(String.format(TRANS_EDIT_LINK,rowNo));
		element.click(NEW_BUTTON);
		element.switchToFrame("//iframe");
	}

	private int getRowNumberOfTran(String customerName, String values) {
		setFilter(CUSTOMER_FILTER, customerName);
		stalePause();
		return table.getValueInListRow(TRANS_TABLE,
				values);
	}

	private String getXpathForTran(String data, String type) {
		StringBuilder xpath=new StringBuilder();
		xpath.append("//span[");
		boolean isFirstKwd = true;
		String[] keywords = data.split(",");
		for (String keyword : keywords) {
			if(keyword.contains("/"))
				keyword=getFormattedDate(keyword);
			if (isFirstKwd) {
				xpath.append("contains(.,'" + keyword + "')");
				isFirstKwd = false;
			} else {
				xpath.append( " and contains(.,'" + keyword + "')");
			}
		}
		if (type.equals("1")) {
			xpath.append("]/input[@class='chkRenewableTransactionItem']");
		} else if (type.equals("2")) {
			xpath.append("]//input[@class='parentTransactionCls']");
		}
		return xpath.toString();
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
		stalePause();
		item.click("//a[text()='" + customerName + "']");
		return new Customer360Page();
	}

}
