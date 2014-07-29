package com.gainsight.sfdc.churn.pages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.pages.BasePage;

public class ChurnPage extends BasePage {
	private final String AUTO_SELECT_LIST = "//td[@class='jbaraTrSearchCustomerListTd']";
	private final String MORE_ICON = "//div[@title='More...' and @class='gs-moreopt-btn btnShowActions']";
	private final String READY_INDICATOR = "//div[@class='dummyChurnAnalyticsDetail']";
	private final String NEW_BUTTON = "//a[contains(text(), 'New Transaction')]";
	private final String CUSTOMER_NAME_FIELD = "//div[@class='requiredInput']/input[contains(@class,'customer-name-text')]";
	private final String NAME_LOOPUP_IMG = "//img[@title='Customer Name Lookup']";
	private final String BOOKING_DATE_FIELD = "//input[@class='transactionDate transactionBookingdate gs-calendar']";
	private final String START_DATE_FIELD = "//input[@class='transactionDate transSubStartDate gs-calendar']";
	private final String CHURN_REASON_SELECT = "//button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']/span[contains(text(),'Select Reason')]";
	private final String CHURN_REASON_SELECT_OPTION = "//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/li/label/span[contains(text(),'%s')]";
	//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/li/label/span[contains(text(),'Other')]
	private final String COMMENTS_FIELD = "//textarea[@class='jbaraDummyCommentInputCtrl transactionComments']";
	private final String CHURN_GRID = "//div[@class='mainPanelDiv churnGridGaphsDisplayDiv']";
	private final String SAVE_BUTTON = "//a[text()='Save']";
	private final String CUSTOMER_FILTER = "Customer_link";
	private final String CHURN_TABLE = "//table[contains(@id,'ChurnAnalyticsList_IdOf') and @class='ui-jqgrid-btable']";
	private final String VIEW="//label/span[text()='%s']";
	private final String VIEW_BUTTON="//span[@class='ChurnUIViewsSelectionList']/button";

	public ChurnPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public ChurnPage addChurnTransaction(HashMap<String, String> data) {
		element.click(MORE_ICON);
		element.click(NEW_BUTTON);
		element.switchToFrame("//iframe[contains(@src,'apex/TransactionForm')]");
		field.setTextField(CUSTOMER_NAME_FIELD, data.get("customerName"));
		/*button.click(NAME_LOOPUP_IMG);
		element.click("//a[text()='" + data.get("customerName") + "']");*/
		driver.findElement(By.xpath(CUSTOMER_NAME_FIELD)).sendKeys(Keys.ENTER);
		wait.waitTillElementDisplayed(AUTO_SELECT_LIST, MIN_TIME, MAX_TIME);
		driver.findElement(By.xpath(CUSTOMER_NAME_FIELD)).sendKeys(Keys.ARROW_DOWN);
	    button.click(AUTO_SELECT_LIST);
	    wait.waitTillElementDisplayed(AUTO_SELECT_LIST, MIN_TIME, MAX_TIME);
	    List<WebElement> eleList = element.getAllElement("//td[@class='jbaraTrSearchCustomerListTd']//a[@class='customSearchRefined']");

        boolean customerSelected = false;
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()) {
                ele.click();
                customerSelected = true; break;
            }
        }
        if(!customerSelected) throw new RuntimeException("Unable to select customer (or) customer not found" );
		amtDateUtil.stalePause();
		String bookingDate = data.get("bookingDate");
		if (bookingDate != null)
			enterDate(BOOKING_DATE_FIELD, bookingDate);
		String effectiveDate = data.get("effectiveDate");
		if (effectiveDate != null)
			enterDate(START_DATE_FIELD, effectiveDate);
		button.click(CHURN_REASON_SELECT);
		 List<WebElement> churnReasonList = element.getAllElement(String.format(CHURN_REASON_SELECT_OPTION,data.get("reason")));

	        boolean churnReasonSelected = false;
	        for(WebElement crl : churnReasonList) {
	            if(crl.isDisplayed()) {
	            	crl.click();
	                churnReasonSelected = true; break;
	            }
	        }
	        if(!churnReasonSelected) throw new RuntimeException("Unable to select Churn Reason (or) Churn reason not found" );
		String comments = data.get("comments");
		if (comments != null)
			field.setTextField(COMMENTS_FIELD, comments);
		button.click(SAVE_BUTTON);
		element.switchToMainWindow();
		wait.waitTillElementDisplayed(CHURN_GRID, MIN_TIME, MAX_TIME);

		return this;
	}

	public Customer360Page gotoCustomer360(String customerName) {
		setFilter(CUSTOMER_FILTER, customerName);
		amtDateUtil.stalePause();
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
	
	public ChurnPage selectView(String viewName){
		item.click(VIEW_BUTTON);
		item.click(String.format(VIEW, viewName));
		wait.waitTillElementDisplayed(CHURN_GRID, MIN_TIME, MAX_TIME);
		return this;
	}

}