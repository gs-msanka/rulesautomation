package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pojo.CustomerSummary;
import com.gainsight.sfdc.customer360.pojo.SummaryLabels;
import com.gainsight.sfdc.customer360.pojo.TimeLineItem;
import com.gainsight.sfdc.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.HashMap;

public class Customer360Page extends BasePage {

    private final String ACC_INS_NAME_INPUT     = "//input[contains(@class, 'search_input search-field gs-left-noradius ui-autocomplete-input') and @type='text']";
    private final String READY_INDICATOR        = "//input[contains(@placeholder, 'Customer name') and @name='search_text']";
	private final String LOADING_IMAGES         = "//div[@class='gs-loadingMsg gs-loader-container-64' and contains(@style,'display: block;')]";
	private final String NAVIGATE_SECTION       = "//div[@class='gs_navtabs']//a[text()='%s']";
	private final String SUMMARY_NUM_FIELDS     = "//div[@class='account_summaryboxtop' and text()='%s']//following-sibling::div";
	private final String SUMMARY_STR_FIELDS     = "//div[@class='gs_summary_details']//li[contains(.,'%s')]/span";
	private final String ADD_TRAN               = "//a[text()='Add Transaction']";
	private final String TRANSACTION_FRAME      = "//iframe[contains(@src,'TransactionForm')]";
    private final String CUST_SERCHBY_SELECT    = "//div[@class='gs-cusstomerseach-icon']";
    //private final String SEARCH_ICON          = "//div[@class='search_input_btn']";
    private final String CUST_SELECT_LIST       = "//li[@class='ui-menu-item' and @role = 'presentation']";
   // private final String CUST_NOTFOUND_MSG      = "//div[@class='gs_inavlidCustomerSpan']";
    private final String DEBOOK_TRN_CONFIRM     = "//div[@class='gs_tsn_confirmation']";
    private final String DEBOOK_OK_BTN	        ="//div[@class='modal-footer']/a[text()='Ok']";

    private static final String RETENTION_SECTION_TAB       = "//ul[@class='nav']/li[@class='retention']";
    private static final String USAGE_SECTION_TAB           = "//ul[@class='nav']/li[@class='usage']";
    private static final String ACCOUNT_ATTRIBUTES_SECTION_TAB = "//ul[@class='nav']/li[@class='accountattributes']";

    private static final String ALERT_SECTION_TAB           = "//ul[@class='alert_tab_nav active']/li[@data-attr='alerts']";
    private static final String EVENT_SECTION_TAB           = "//ul[@class='alert_tab_nav active']/li[@data-attr='events']";
    //private static final String USAGE_SUB_ADOPTION_TAB      = "//ul[@class='alert_tab_nav']/li[@data-tabname='Adoption']";
    //private static final String USAGE_SUB_MILESTONES_TAB    = "//ul[@class='alert_tab_nav']/li[@data-tabname='Milestones']";
    private static final String USAGE_SUB_TRACKER_TAB       = "//ul[@class='alert_tab_nav']/li[@data-tabname='UsageTracker']";
    //private static final String USAGE_SUB_SECTION_TAB       = "//li[@data-tabname='UsageTracker']/a[contains(text(), 'Usage Tracker')]";

	public Customer360Page() {
        Report.logInfo("360 Page Loading");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

    public Customer360Page(String val) {
        Report.logInfo("Sample");
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
		return this;
	}
	public Customer360Features goToFeaturesSection() {
		item.click(String.format(NAVIGATE_SECTION,"Features"));
		wait.waitTillElementDisplayed("//div[@class='gs_features']", MIN_TIME, MAX_TIME);
        return new Customer360Features();
	}
	
	public Customer360Milestones goToUsageSection() {
		item.click(String.format(NAVIGATE_SECTION,"Usage"));
		wait.waitTillElementDisplayed("//div[@class='gs_usage']", MIN_TIME, MAX_TIME);
        return new Customer360Milestones();
	}
	public Customer360Scorecard goToScorecardSection() {
		item.click(String.format(NAVIGATE_SECTION,"Scorecard"));
        waitForLoadingImagesNotPresent();
		wait.waitTillElementDisplayed("//div[@class='scorecardsbody']", MIN_TIME, MAX_TIME);
		return new Customer360Scorecard();
	}
    public Customer360Page searchCustomer(String name, Boolean isInstanceName, Boolean isContains) {
    	Report.logInfo("Searching for customer : " +name);
        wait.waitTillElementDisplayed(CUST_SERCHBY_SELECT, MIN_TIME, MAX_TIME);
        button.click(CUST_SERCHBY_SELECT);
        wait.waitTillElementDisplayed("//div[@class='gs_filter_option_section']", MIN_TIME, MAX_TIME);
        if(isInstanceName) {
            if(isContains) {
                item.click("//li[@class='instance-name-cnt']");
            } else {
                item.click("//li[@class='instance-name-starts']");
            }
        } else {
            if(isContains) {
                item.click("//li[@class='cust-name-cnt']");
            } else {
                item.click("//li[contains(@class, 'cust-name-starts')]");

            }
        }
        driver.findElement(By.xpath(ACC_INS_NAME_INPUT)).sendKeys(name);
        driver.findElement(By.xpath(ACC_INS_NAME_INPUT)).sendKeys(Keys.ENTER);
        wait.waitTillElementDisplayed(CUST_SELECT_LIST, MIN_TIME, MAX_TIME);
        driver.findElement(By.xpath("//li[@class='ui-menu-item' and @role = 'presentation']/a[contains(text(),'"+name+"')]")).click();
        Report.logInfo("Customer Search Completed. ");
        waitForLoadingImagesNotPresent();
        return this;
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

    public Retention360 clickOnRetEventsSec() {
        wait.waitTillElementDisplayed(RETENTION_SECTION_TAB, MIN_TIME, MAX_TIME);
        item.click(EVENT_SECTION_TAB);
        return new Retention360("Events Page");
    }

    public Retention360 clickOnRetAlertsSec() {
        wait.waitTillElementDisplayed(RETENTION_SECTION_TAB, MIN_TIME, MAX_TIME);
        item.click(ALERT_SECTION_TAB);
        return new Retention360("Alerts Page");
    }

    public RelatedList360 clickOnRelatedListSec(String secName) {
        amtDateUtil.stalePause();
        String xPath = "//ul[@class='nav']/li[contains(@class,'related_list')]/a[contains(text(),'"+secName+"')]";
        wait.waitTillElementDisplayed(xPath, MIN_TIME, MAX_TIME);
        item.click(xPath);
        return new RelatedList360(secName);
    }

    public Attributes clickOnAccAttributesSec() {
        wait.waitTillElementDisplayed(ACCOUNT_ATTRIBUTES_SECTION_TAB, MIN_TIME, MAX_TIME);
        item.click(ACCOUNT_ATTRIBUTES_SECTION_TAB);
        return new Attributes();
    }

    public UsageTracker360 clickOnUsageTracker() {
        wait.waitTillElementDisplayed(USAGE_SECTION_TAB, MIN_TIME,MAX_TIME);
        item.click(USAGE_SUB_TRACKER_TAB);
        return new UsageTracker360();
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
		if(item.isElementPresent(DEBOOK_TRN_CONFIRM)){
			item.click(DEBOOK_OK_BTN);
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
