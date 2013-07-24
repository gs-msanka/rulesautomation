package com.gainsight.sfdc.transactions.pages;

import java.util.HashMap;

import com.gainsight.sfdc.customer.pages.Customer360Page;
import com.gainsight.sfdc.pages.BasePage;

public class TransactionsPage extends BasePage{
	private final String READY_INDICATOR = "//div[@id='Transactions-Usage']";

	public TransactionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public TransactionsPage addTransaction(HashMap<String,String> testData){
		//fields that need to be read more than once
		String opportunity=testData.get("opportunity");
		String bookingType=testData.get("bookingtype");
		String bookingDate=testData.get("bookingdate");
		String startDate=testData.get("startdate");
		String mrr=testData.get("mrr");
		String asv=testData.get("asv");
		String userCount=testData.get("usercount");
		String otr=testData.get("otr");
		String comments=testData.get("comments");
		
		String customerName=testData.get("customername");
		
		//set field values
		element.click("//input[@value='New']");
		sleep(20);
		element.switchToFrame("//iframe");
		field.setTextField("//input[contains(@class,'customer-name-text')]",
				customerName);
		button.click("//img[@title='Customer Name Lookup']");
		element.click("//a[text()='" + customerName + "']");
		
		if (opportunity != null && !opportunity.equals(""))
		field.setSelectField(
				"//input[@class='jbaraDummyOpportunitySelectCtrl']", opportunity);
		if (bookingType != null && !bookingType.equals(""))
			field.setSelectField(
					"//input[@class='jbaraDummyBookingOrderSelectCtrl']", bookingType);
		if (bookingDate != null && !bookingDate.equals("")){
			field.click("//input[@class='transactionDate transactionBookingdate']");
			sleep(2);
			field.clearAndSetText(
					"//input[@class='transactionDate transactionBookingdate']", bookingDate);
			sleep(2);
		}
		if (startDate != null && !startDate.equals("")){
			field.click("//input[@class='transactionDate transSubStartDate']");
			sleep(2);
			field.clearAndSetText(
					"//input[@class='transactionDate transSubStartDate']", startDate);
			sleep(2);
		}
		
        field.setTextField("//input[@class='transactionDate transSubEndDate']", testData.get("enddate"));
        if (mrr != null && !mrr.equals(""))
    		field.setTextField(
    				"//input[@class='recurringMRR lineItemVal']", mrr);
        if (asv != null && !asv.equals(""))
    		field.setTextField(
    				"//input[@class='recurringASV lineItemVal']", asv);
        if (userCount != null && !userCount.equals(""))
    		field.setTextField(
    				"//input[@class='lineItemVal lineItemUsers']", userCount);
        if (otr != null && !otr.equals(""))
    		field.setTextField(
    				"//input[@class='lineItemVal lineItemOnetimeRevenue']", otr);
        if (comments != null && !comments.equals(""))
    		field.setTextField(
    				"//textarea[@class='jbaraDummyCommentInputCtrl transactionComments']", opportunity);
		
		button.click("//a[text()='Save']");
		element.switchToMainWindow();
	
		return this;
	}
	
	public boolean isTransactionPresent(String customerName,String values){
		setFilter("Customer_link", customerName);
		sleep(5);
		int rowNo = table.getValueInListRow(
				"transactionList_IdOfJBaraStandardView", values);
		if (rowNo == -1)
			return false;
		else
			return true;
	}
	
	public Customer360Page selectCustomer(String customerName){
		setFilter("Customer_link", customerName);
		sleep(2);
		item.click("//a[text()='"+customerName+"']");
		return new Customer360Page();
	}
	
}
