package com.gainsight.sfdc.customer.pages;

import java.util.HashMap;

import org.openqa.selenium.By;

import com.gainsight.sfdc.customer.pojo.CustomerSummary;
import com.gainsight.sfdc.customer.pojo.TimeLineItem;
import com.gainsight.sfdc.pages.BasePage;

public class Customer360Page extends BasePage {
	private final String READY_INDICATOR = "//div[@class='dummysummaryDetail']";
	private final String NEW_TRANSACTION_IMG="//img[@alt='Transaction']";
	private final String TRANSACTION_FRAME="//iframe";
	private final String BACK_LINK="//a[contains(text(),'Back')]";
	private final String NEW_MENU="//div[text()='New ']";

	public Customer360Page() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public CustomerSummary getSummaryDetails() {
		CustomerSummary summary = new CustomerSummary();
		summary.setASV(stripNumber(field
				.getTextFieldText("//div[@id='jbCustomerSnapshot']//div[1]/span")));
		summary.setMRR(stripNumber(field
				.getTextFieldText("//div[@id='jbCustomerSnapshot']//div[2]/span")));
		summary.setOTR(stripNumber(field
				.getTextFieldText("//div[@id='jbCustomerSnapshot']//div[3]/span")));
		summary.setUsers(stripNumber(field
				.getTextFieldText("//div[@id='jbCustomerSnapshot']//div[4]/span")));
		summary.setARPU(stripNumber(field.getTextFieldText(
				"//div[@id='jbCustomerSnapshot']//div[5]/span").replace("ARPU",
				"")));
		summary.setStage(field.getTextFieldText(
				"//div[@id='jbCustomerSnapshot']//div[6]/span[2]").replace(
				"Stage:", ""));
		summary.setLifetime(field
				.getTextFieldText("//div[@class='jbCustomerSnapDiv jbCustomerSnapDivLifetime'][2]"));
		summary.setTimeToRenew(field
				.getTextFieldText("//div[@class='jbCustomerSnapDiv jbCustomerSnapDivLifetime'][1]"));
		summary.setOCD(field
				.getTextFieldText("//span[@class='jbCustomerSnapOCDclass']"));
		summary.setRD(field
				.getTextFieldText("//span[@class='jbCustomerSnapNextRenclass']"));
		summary.setStatus(field
				.getTextFieldText("//span[@class='ptCustomerStatus']"));

		return summary;
	}

	public int getPositionOfTransaction(String stage, String bookingDate) {
		String xpath = "//div[contains(@class,'transSummaryListDiv') and contains(.,'"
				+ stage + "') and contains(.,'" + bookingDate + "')]";

		return getWebElement(By.xpath(xpath)).getLocation().x;
	}

	public boolean isTransactionPresent(String stage, String endDate) {
		String xpath = "//div[contains(@class,'transSummaryListDiv') and contains(.,'"
				+ stage + "') and contains(.,'" + endDate + "')]";
		return isElementPresentBy(element.getElementBy(xpath), MAX_TIME);
	}
		
	public boolean isLineItemPresent(TimeLineItem transaction) {		
		return transactionUtil.isLineItemPresent(transaction);
	}
	
	public Customer360Page addNewBusinessTransaction(HashMap<String,String> nbData){
		item.click(NEW_MENU);
		item.click(NEW_TRANSACTION_IMG);
		field.switchToFrame(TRANSACTION_FRAME);
		transactionUtil.addNewBusiness(nbData);
		field.switchToMainWindow();	
		wait.waitTillElementNotDisplayed(TRANSACTION_FRAME, MIN_TIME, MAX_TIME);			
		return this;
	}
	public Customer360Page addChurnTransaction(HashMap<String,String> churnData){
		item.click(NEW_MENU);
		item.click(NEW_TRANSACTION_IMG);
		field.switchToFrame(TRANSACTION_FRAME);
		transactionUtil.addChurnTransaction(churnData);
		field.switchToMainWindow();	
		wait.waitTillElementNotDisplayed(TRANSACTION_FRAME, MIN_TIME, MAX_TIME);			
		return this;
	}
	public void clickBack(){
		item.click(BACK_LINK);		
	}

	private String stripNumber(String text) {
		return text.replace("$", "").replace(",", "");
	}

	public Customer360Page addRenewalTransaction(HashMap<String,String> dbData) {
		item.click(NEW_MENU);
		item.click(NEW_TRANSACTION_IMG);
		field.switchToFrame(TRANSACTION_FRAME);
		transactionUtil.addDebookTransaction(dbData);
		modal.accept();
		field.switchToMainWindow();	
		wait.waitTillElementNotDisplayed(TRANSACTION_FRAME, MIN_TIME, MAX_TIME);			
		return this;
	}	

}
