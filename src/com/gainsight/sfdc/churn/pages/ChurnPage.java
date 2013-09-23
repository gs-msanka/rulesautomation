package com.gainsight.sfdc.churn.pages;

import java.util.HashMap;

import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.pages.BasePage;

public class ChurnPage extends BasePage {
	private final String READY_INDICATOR = "//div[@class='dummyChurnAnalyticsDetail']";
	private final String NEW_BUTTON = "//input[@value='New']";
	private final String CUSTOMER_NAME_FIELD = "//input[contains(@class,'customer-name-text')]";
	private final String NAME_LOOPUP_IMG = "//img[@title='Customer Name Lookup']";
	private final String BOOKING_DATE_FIELD = "//input[@class='transactionDate transactionBookingdate']";
	private final String START_DATE_FIELD = "//input[@class='transactionDate transSubStartDate']";
	private final String CHURN_REASON_SELECT = "//select[@class='churnReasonSelectCtrl']";
	private final String COMMENTS_FIELD = "//textarea[@class='jbaraDummyCommentInputCtrl transactionComments']";
	private final String CHURN_GRID = "//div[@class='mainPanelDiv churnGridGaphsDisplayDiv']";
	private final String SAVE_BUTTON = "//a[text()='Save']";
	private final String CUSTOMER_FILTER = "Customer_link";
	private final String CHURN_TABLE = "ChurnAnalyticsList_IdOfJBaraStandardView";

	public ChurnPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public ChurnPage addChurnTransaction(HashMap<String, String> data) {
		element.click(NEW_BUTTON);
		element.switchToFrame("//iframe");
		field.setTextField(CUSTOMER_NAME_FIELD, data.get("customerName"));
		button.click(NAME_LOOPUP_IMG);
		element.click("//a[text()='" + data.get("customerName") + "']");
		stalePause();
		String bookingDate=data.get("bookingDate");
		if(!bookingDate.equals("nil"))
		enterDate(BOOKING_DATE_FIELD,bookingDate );
		String effectiveDate=data.get("effectiveDate");
		if(!effectiveDate.equals("nil"))			
		enterDate(START_DATE_FIELD, effectiveDate);
		field.setSelectField(CHURN_REASON_SELECT, data.get("reason"));
		String comments = data.get("comments");
		if (!comments.equals("nil"))
			field.setTextField(COMMENTS_FIELD, comments);
		button.click(SAVE_BUTTON);
		element.switchToMainWindow();
		wait.waitTillElementDisplayed(CHURN_GRID, MIN_TIME, MAX_TIME);

		return this;
	}

	public Customer360Page gotoCustomer360(String customerName) {
		setFilter(CUSTOMER_FILTER, customerName);
		stalePause();
		item.click("//a[text()='" + customerName + "']");
		return new Customer360Page();
	}

	public boolean isTransactionPresent(String customerName, String values) {
		if (table.getValueInListRow(CHURN_TABLE, values) == -1) {
			return false;
		} else {
			return true;
		}
	}

}