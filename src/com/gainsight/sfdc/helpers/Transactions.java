package com.gainsight.sfdc.helpers;

import java.util.HashMap;

import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.customer360.pojo.TimeLineItem;
import com.gainsight.sfdc.pages.Constants;

public class Transactions extends WebPage implements Constants {
	private final String BOOKING_DATE_FIELD = "//input[@class='transactionDate transactionBookingdate']";
	private final String START_DATE_FIELD = "//input[@class='transactionDate transSubStartDate']";
	private final String MRR_FIELD = "//input[@class='recurringMRR lineItemVal']";
	private final String ASV_FIELD = "//input[@class='recurringASV lineItemVal']";
	private final String USERS_FIELD = "//input[@class='lineItemVal lineItemUsers']";
	private final String OTR_FIELD = "//input[@class='lineItemVal lineItemOnetimeRevenue']";
	private final String END_DATE_FIELD = "//input[@class='transactionDate transSubEndDate']";
	private final String COMMENTS_FIELD = "//textarea[@class='jbaraDummyCommentInputCtrl transactionComments']";
	private final String CUSTOMER_NAME_FIELD = "//input[contains(@class,'customer-name-text')]";
	private final String NAME_LOOPUP_IMG = "//img[@title='Customer Name Lookup']";
	private final String OPPRT_SELECT = "//input[@class='jbaraDummyOpportunitySelectCtrl']";
	private final String BOOKING_TYPE_SELECT = "//select[@class='jbaraDummyBookingOrderSelectCtrl']";
	private final String NEW_RADIO = "//input[@type='radio' and @value='new']";
	private final String EXISTING_RADIO = "//input[@type='radio' and @value='existing']";
	private final String CHURN_REASON_SELECT = "//select[@class='churnReasonSelectCtrl']";
	private final String SAVE_BUTTON = "//a[text()='Save']";
	AmountsAndDatesUtil util = new AmountsAndDatesUtil();

	public void addChurnTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fillChurnFields(data.get("bookingDate"), data.get("effectiveDate"),
				data.get("reason"));
		transactionFini(data.get("comments"));

	}

	public void addDebookTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fillDebookFields(data.get("transaction"), data.get("bookingDate"),
				data.get("effectiveDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));

	}

	public void addDownsellTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fillDownsellFields(data.get("transaction"), data.get("bookingDate"),
				data.get("effectiveDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));

	}

	public void addNewBusiness(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));

	}

	public void addRenewalTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		String parent = data.get("transaction");
		if (parent != null)
			item.click(getXpathForTran(parent, "1"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));

	}

	public void addUpsellTransaction(HashMap<String, String> data) {
		addTransactionInit(data.get("customerName"), data.get("opportunity"),
				data.get("bookingType"));
		fillUpsellFields(data.get("associationType"), data.get("transaction"));
		addTransactionDates(data.get("bookingDate"), data.get("startDate"),
				data.get("endDate"));
		addTransactionAmts(data.get("mrr"), data.get("asv"),
				data.get("userCount"), data.get("otr"));
		transactionFini(data.get("comments"));

	}
	public boolean isLineItemPresent(TimeLineItem transaction) {		
		return isElementPresentBy(element.getElementBy(getXpathForTimeLineItem(transaction)), MAX_TIME);
	}

	private void addTransactionAmts(String mrr, String asv, String userCount,
			String otr) {
		if (mrr != null)
			field.setTextField(MRR_FIELD, mrr);
		if (asv != null)
			field.setTextField(ASV_FIELD, asv);
		if (userCount != null)
			field.setTextField(USERS_FIELD, userCount);
		if (otr != null)
			field.setTextField(OTR_FIELD, otr);
	}

	private void addTransactionDates(String bookingDate, String startDate,
			String endDate) {
		if (bookingDate != null) {
			util.enterDate(BOOKING_DATE_FIELD, bookingDate);
		}
		if (startDate != null) {
			util.stalePause();
			util.enterDate(START_DATE_FIELD, startDate);
		}
		if (endDate != null)
			util.enterDate(END_DATE_FIELD, endDate);

	}

	private void addTransactionInit(String customerName, String opportunity,
			String bookingType) {
		if (customerName != null) {
			field.setTextField(CUSTOMER_NAME_FIELD, customerName);
			button.click(NAME_LOOPUP_IMG);
			element.click("//a[text()='" + customerName + "']");
		}
		if (opportunity != null)
			field.setSelectField(OPPRT_SELECT, opportunity);
		if (bookingType != null)
			field.setSelectField(BOOKING_TYPE_SELECT, bookingType);
		util.stalePause();
	}

	private void fillUpsellFields(String associationType, String transaction) {
		if (associationType.equalsIgnoreCase("new")) {
			field.click(NEW_RADIO);
		} else if (associationType.equalsIgnoreCase("existing")) {
			field.click(EXISTING_RADIO);
			item.click(getXpathForTran(transaction, "2"));
		}
	}

	private void fillChurnFields(String bookingDate, String effectiveDate,
			String reason) {
		if (bookingDate != null)
			util.enterDate(BOOKING_DATE_FIELD, bookingDate);
		if (effectiveDate != null)
			util.enterDate(START_DATE_FIELD, effectiveDate);
		if (reason != null)
			field.setSelectField(CHURN_REASON_SELECT, reason);
	}

	private String getXpathForTran(String data, String type) {
		StringBuilder xpath = new StringBuilder();
		xpath.append("//span[");
		boolean isFirstKwd = true;
		String[] keywords = data.split(",");
		for (String keyword : keywords) {
			if (keyword.contains("/"))
				keyword = util.getFormattedDate(keyword);
			if (isFirstKwd) {
				xpath.append("contains(.,'" + keyword + "')");
				isFirstKwd = false;
			} else {
				xpath.append(" and contains(.,'" + keyword + "')");
			}
		}
		if (type.equals("1")) {
			xpath.append("]/input[@class='chkRenewableTransactionItem']");
		} else if (type.equals("2")) {
			xpath.append("]//input[@class='parentTransactionCls']");
		}
		return xpath.toString();
	}

	private void fillDebookFields(String transaction, String bookingDate,
			String effectiveDate) {
		if (transaction != null)
			item.click(getXpathForTran(transaction, "2"));
		if (bookingDate != null)
			util.enterDate(BOOKING_DATE_FIELD, bookingDate);
		if (effectiveDate != null)
			util.enterDate(START_DATE_FIELD, effectiveDate);
	}

	private void transactionFini(String comments) {
		if (comments != null)
			field.setTextField(COMMENTS_FIELD, comments);
		button.click(SAVE_BUTTON);
	}

	private void fillDownsellFields(String transaction, String bookingDate,
			String effectiveDate) {
		if (transaction != null)
			item.click(getXpathForTran(transaction, "2"));
		if (bookingDate != null)
			util.enterDate(BOOKING_DATE_FIELD, bookingDate);
		if (effectiveDate != null)
			util.enterDate(START_DATE_FIELD, effectiveDate);
	}
	private String getXpathForTimeLineItem(TimeLineItem transaction){
		StringBuffer xpath=new StringBuffer();		
		String sContains=" and contains(.,'";
		String cContains="') ";
		xpath.append("//div[contains(@class,'transSummaryListDiv') and contains(.,'"); 
		xpath.append(transaction.getBookingDate()+cContains);
		xpath.append(sContains+transaction.getType()+cContains);
		if (transaction.getMRR() !=null)
			xpath.append(sContains+util.formatNumber(transaction.getMRR())+cContains);
		if (transaction.getASV() !=null)
			xpath.append(sContains+util.formatNumber(transaction.getASV())+cContains);
		if (transaction.getUsers() !=null)
			xpath.append(sContains+transaction.getUsers()+cContains);
		if (transaction.getOTR() !=null)
			xpath.append(sContains+util.formatNumber(transaction.getOTR())+cContains);
		if (transaction.getTerm() !=null)
			xpath.append(sContains+transaction.getTerm()+cContains);
		xpath.append("]");
		return xpath.toString();		
	}

}
