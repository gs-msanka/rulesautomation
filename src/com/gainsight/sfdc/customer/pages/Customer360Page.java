package com.gainsight.sfdc.customer.pages;

import java.util.HashMap;
import org.openqa.selenium.By;
import com.gainsight.sfdc.customer.pojo.CustomerSummary;
import com.gainsight.sfdc.customer.pojo.SummaryLabels;
import com.gainsight.sfdc.customer.pojo.TimeLineItem;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.customer360Page.pages.Customer360Features;


public class Customer360Page extends BasePage {
	//private final String READY_INDICATOR = "//div[@class='custmor_comp_name']";
	// changing the ready indicator to search box on the right corner of page
	private final String READY_INDICATOR = "//div[@class='gs_search_section']";
	private final String LOADING_IMAGES = "//div[@class='gs-loadingMsg gs-loader-container-64' and contains(@style,'display: block;')]";
	private final String NAVIGATE_SECTION = "//div[@class='gs_navtabs']//a[text()='%s']";
	private final String SUMMARY_NUM_FIELDS = "//div[@class='account_summaryboxtop' and text()='%s']//following-sibling::div";
	private final String SUMMARY_STR_FIELDS = "//div[@class='gs_summary_details']//li[contains(.,'%s')]/span";
	private final String ADD_TRAN = "//a[text()='Add Transaction']";
	private final String TRANSACTION_FRAME = "//iframe";

	public Customer360Page() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		// wait.waitTillElementNotPresent(LOADING_IMAGES, MIN_TIME, MAX_TIME);
	}

	public CustomerSummary getSummaryDetails() {
		CustomerSummary summary = new CustomerSummary();
		SummaryLabels labels = new SummaryLabels();
		summary.setASV(stripNumber(field.getTextFieldText(String.format(
				SUMMARY_NUM_FIELDS, labels.getASV()))));
		summary.setMRR(stripNumber(field.getTextFieldText(String.format(
				SUMMARY_NUM_FIELDS, labels.getMRR()))));
		summary.setOTR(stripNumber(field.getTextFieldText(String.format(
				SUMMARY_NUM_FIELDS, labels.getOTR()))));
		summary.setUsers(field.getTextFieldText(String.format(
				SUMMARY_NUM_FIELDS, labels.getUsers())));
		summary.setARPU(stripNumber(field.getTextFieldText(String.format(
				SUMMARY_NUM_FIELDS, labels.getARPU()))));
		summary.setStage(field.getTextFieldText(String.format(
				SUMMARY_STR_FIELDS, labels.getStage())));
		summary.setLifetime(field.getTextFieldText(String.format(
				SUMMARY_STR_FIELDS, labels.getLifeTime())));
		summary.setTimeToRenew(field.getTextFieldText(String.format(
				SUMMARY_STR_FIELDS, labels.getDaysToRenew())));
		summary.setOCD(field.getTextFieldText(String.format(SUMMARY_STR_FIELDS,
				labels.getOCD())));
		summary.setRD(field.getTextFieldText(String.format(SUMMARY_STR_FIELDS,
				labels.getRD())));
		summary.setStatus(field
				.getTextFieldText("//div[@class='gs_header']//dt"));
		return summary;
	}

	public Customer360Page goToSection(String name) {
		item.click(String.format(NAVIGATE_SECTION, name));
		wait.waitTillElementNotPresent(LOADING_IMAGES, MIN_TIME, MAX_TIME);
		if (name.equals("Features")) {
			return new Customer360Features();
		}
		return this;
	}
	
	public Customer360Page gotoCustomer360(String customerName) {

		/*
		 * field.setTextByKeys("//input[@name='search_text']", customerName);
		 * item.click("//div[@class='search_input_btn']");
		 * wait.waitTillElementDisplayed("//li[@role='presentation']", MIN_TIME,
		 * MAX_TIME); item.click("//a[text()='"+customerName+"']");
		 */
		field.clearAndSetText(
				"//input[@class='search_input ui-autocomplete-input']",
				customerName);
		/*
		 * wait.waitTillElementDisplayed("//li[@role='presentation']", MIN_TIME,
		 * MAX_TIME); item.click("//a[text()='"+customerName+"']");
		 */
		driver.get("https://jbcxm.ap1.visual.force.com/apex/CustomerSuccess360?cid=0019000000jhBghAAE");

		return new Customer360Page();
	}
	
	public int getPositionOfTransaction(String stage, String bookingDate) {
		String xpath = "//div[contains(@class,'transaction ') and contains(.,'"
				+ bookingDate + "') and contains(.,'" + stage + "')]";
		return getWebElement(By.xpath(xpath)).getLocation().y;
	}

	public boolean isTransactionPresent(TimeLineItem lineItem) {
		String xpath = getXpathForTimeLineItem(lineItem);
		return isElementPresentBy(element.getElementBy(xpath), MAX_TIME);
	}

	public Customer360Page addNewBusinessTransaction(
			HashMap<String, String> nbData) {
		transInit();
		transactionUtil.addNewBusiness(nbData);
		transFini();
		return this;
	}

	public Customer360Page addChurnTransaction(
			HashMap<String, String> churnData) {
		transInit();
		transactionUtil.addChurnTransaction(churnData);
		transFini();
		return this;
	}

	public Customer360Page addRenewalTransaction(HashMap<String, String> rData) {
		transInit();
		transactionUtil.addRenewalTransaction(rData);
		modal.accept();
		transFini();
		return this;
	}

	public Customer360Page addDebookTransaction(HashMap<String, String> dbData) {
		transInit();
		transactionUtil.addDebookTransaction(dbData);
		if(modal.exists()){
			modal.accept();
		}
		transFini();
		return this;
	}

	private String stripNumber(String text) {
		return text.replace("$", "").replace(",", "");
	}

	private void transInit() {
		item.click(ADD_TRAN);
		wait.waitTillElementPresent(TRANSACTION_FRAME, MIN_TIME, MAX_TIME);
		field.switchToFrame(TRANSACTION_FRAME);
	}

	private void transFini() {
		field.switchToMainWindow();
		wait.waitTillElementNotPresent(TRANSACTION_FRAME, MIN_TIME, MAX_TIME);
		wait.waitTillElementNotPresent(LOADING_IMAGES, MIN_TIME, MAX_TIME);
	}

	private String getXpathForTimeLineItem(TimeLineItem transaction) {
		StringBuffer xpath = new StringBuffer();
		String sContains = " and contains(.,'";
		String cContains = "') ";
		xpath.append("//div[contains(@class,'transaction ') and contains(.,'");
		xpath.append(transaction.getBookingDate() + cContains);
		xpath.append(sContains + transaction.getType() + cContains);
		if (transaction.getMRR() != null)
			xpath.append(sContains
					+ amtDateUtil.formatNumber(transaction.getMRR())
					+ cContains);
		if (transaction.getASV() != null)
			xpath.append(sContains
					+ amtDateUtil.formatNumber(transaction.getASV())
					+ cContains);
		if (transaction.getUsers() != null)
			xpath.append(sContains + transaction.getUsers() + cContains);
		if (transaction.getOTR() != null)
			xpath.append(sContains
					+ amtDateUtil.formatNumber(transaction.getOTR())
					+ cContains);
		if (transaction.getTerm() != null)
			xpath.append(sContains + transaction.getTerm() + cContains);
		if (transaction.getTerm() != null)
			xpath.append(sContains + transaction.getTerm() + cContains);
		if (transaction.getStartDate() != null)
			xpath.append(sContains + transaction.getStartDate() + cContains);
		if (transaction.getEndDate() != null)
			xpath.append(sContains + transaction.getEndDate() + cContains);
		xpath.append("]");
		return xpath.toString();
	}
}
