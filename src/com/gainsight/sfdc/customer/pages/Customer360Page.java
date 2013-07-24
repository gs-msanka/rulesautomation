package com.gainsight.sfdc.customer.pages;

import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.Assert;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer.pojo.CustomerSummary;
import com.gainsight.sfdc.pages.BasePage;

public class Customer360Page extends BasePage {
	private final String READY_INDICATOR = "//div[@class='dummysummaryDetail']";

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
		summary.setARPU(stripNumber(field
				.getTextFieldText("//div[@id='jbCustomerSnapshot']//div[5]/span")));
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

		return summary;
	}

	public int getPositionOfTransaction(String stage, String endDate) {
		String xpath = "//div[@class='transSummaryListDiv timelineLiListOpenClass']/span[contains(.,'"
				+ stage + "') and contains(.,'" + endDate + "')]";
		return item.getElement(By.xpath(xpath)).getLocation().x;
	}

	public boolean isTransactionPresent(String stage, String endDate) {
		String xpath = "//div[@class='transSummaryListDiv timelineLiListOpenClass']/span[contains(.,'"
				+ stage + "') and contains(.,'" + endDate + "')]";
		return isElementPresentBy(element.getElementBy(xpath), MAX_TIME);
	}

	public void verifyCustomerSummary(HashMap<String, String> testData) {
		CustomerSummary summary = getSummaryDetails();
		Report.logInfo("Customer Summary:\n" + summary.toString());
		Assert.assertEquals(testData.get("asv").trim(), summary.getASV().trim());
//		Assert.assertEquals(testData.get("users").trim(), summary.getUsers().trim());
		Assert.assertEquals(testData.get("otr").trim(), summary.getOTR().trim());
		/*Assert.assertEquals(testData.get("startdate"), summary.getOCD());
		Assert.assertEquals(testData.get("enddate"), summary.getRD());*/
	}

	private String stripNumber(String text) {
		return text.replace("$", "").replace(",", "");
	}

}
