package com.gainsight.sfdc.customer360.pages;

import java.util.HashMap;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.gainsight.sfdc.customer360.pojo.CustomerSummary;
import com.gainsight.sfdc.customer360.pojo.SummaryLabels;
import com.gainsight.sfdc.customer360.pojo.TimeLineItem;
import com.gainsight.sfdc.pages.BasePage;

public class Customer360Page extends BasePage {

    private final String ACC_INS_NAME_INPUT     = "//input[contains(@class, 'search_input search-field gs-left-noradius ui-autocomplete-input') and @type='text']";
    private final String READY_INDICATOR        = "//input[contains(@placeholder, 'Customer name') and @name='search_text']";
	private final String LOADING_IMAGES         = "//div[@class='gs-loadingMsg gs-loader-container-64' and contains(@style,'display: block;')]";
	private final String NAVIGATE_SECTION       = "//div[@class='gs_navtabs']//a[text()='%s']";
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
    //private final String SUMMARY_NUM_FIELDS     = "//div[@class='account_summaryboxtop' and text()='%s']//following-sibling::div";
    //private final String SUMMARY_STR_FIELDS     = "//div[@class='gs_summary_details']//li[contains(.,'%s')]/span";
    //private static final String USAGE_SUB_ADOPTION_TAB      = "//ul[@class='alert_tab_nav']/li[@data-tabname='Adoption']";
    //private static final String USAGE_SUB_MILESTONES_TAB    = "//ul[@class='alert_tab_nav']/li[@data-tabname='Milestones']";
    private static final String USAGE_SUB_TRACKER_TAB       = "//ul[@class='alert_tab_nav']/li[@data-tabname='UsageTracker']";
    //private static final String USAGE_SUB_SECTION_TAB       = "//li[@data-tabname='UsageTracker']/a[contains(text(), 'Usage Tracker')]";
    private static final String summaryLeftSection = "//div[@class='gs_summary_details']/descendant::span[@class='gs-label-name' and contains(text(), '%s')]/following-sibling::span";
    private static final String summaryWidgets = "//div[@class='gs_account_summarymain']/descendant::div[contains(@class, 'gs-sum-widgethead') and text()='%s']/following-sibling::div";


    public Customer360Page() {
        Log.info("360 Page Loading");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

    public Customer360Page(String val) {
        Log.info("Sample");
    }

    /**
     * Needs more attention.
     * @return
     */
	public CustomerSummary getSummaryDetails() {
        CustomerSummary summary = new CustomerSummary();
		SummaryLabels labels = new SummaryLabels();
		summary.setASV(stripNumber(field.getTextFieldText(String.format(summaryLeftSection, labels.getASV()))));
		summary.setMRR(stripNumber(field.getTextFieldText(String.format(summaryLeftSection, labels.getMRR()))));
		summary.setOTR(stripNumber(field.getTextFieldText(String.format(summaryLeftSection, labels.getOTR()))));
		summary.setOCD(field.getTextFieldText(String.format(summaryLeftSection,labels.getOCD())));
		summary.setRD(field.getTextFieldText(String.format(summaryLeftSection, labels.getRD())));
		summary.setStatus(field.getTextFieldText(String.format(summaryLeftSection, "Status")));
        Log.info("Stage : " + field.getTextFieldText(String.format(summaryWidgets, labels.getStage())));
        summary.setStage(field.getTextFieldText(String.format(summaryWidgets, labels.getStage())));
        summary.setUsers(field.getTextFieldText(String.format(summaryWidgets, labels.getUsers())));

        /*summary.setARPU(stripNumber(field.getTextFieldText(String.format(
                SUMMARY_NUM_FIELDS, labels.getARPU()))));
        summary.setLifetime(field.getTextFieldText(String.format(
                SUMMARY_STR_FIELDS, labels.getLifeTime())));
        summary.setTimeToRenew(field.getTextFieldText(String.format(
                SUMMARY_STR_FIELDS, labels.getDaysToRenew())));
        */
        return summary;
	}
	
	public Customer360Features goToSponsorSection() {
		item.click(String.format(NAVIGATE_SECTION,"Sponsor Tracking"));
		wait.waitTillElementDisplayed("//div[@class='gs_sponsortracking']", MIN_TIME, MAX_TIME);
        return new Customer360Features();
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
	
	public Workflow360Page goToCockpitSection(){
		item.click(String.format(NAVIGATE_SECTION,"Cockpit"));
		waitForLoadingImagesNotPresent();
		wait.waitTillElementDisplayed("//div[@class='workflow-summary']", MIN_TIME, MAX_TIME);
		return new Workflow360Page("360 Page");
	}
    public Customer360Page searchCustomer(String name, Boolean isInstanceName, Boolean isContains) {
    	Log.info("Searching for customer : " +name);
        wait.waitTillElementDisplayed(CUST_SERCHBY_SELECT, MIN_TIME, MAX_TIME);
        for(int i=0;i<3;i++){
        button.click(CUST_SERCHBY_SELECT);
        Timer.sleep(2); //Few times, customer selection is fails.
        wait.waitTillElementDisplayed("//div[@class='gs_filter_option_section']", MIN_TIME, MAX_TIME);
        if(isInstanceName) {
            if(isContains) {
            	if(item.isElementPresent("//div[@class='gs_filter_option_section']"))
                		item.click("//li[@class='instance-name-cnt']");
               else  continue; 
            } else {
            	if(item.isElementPresent("//div[@class='gs_filter_option_section']"))
            			item.click("//li[@class='instance-name-starts']");
            	else  continue; 
            }
        } else {
            if(isContains) {
            	if(item.isElementPresent("//div[@class='gs_filter_option_section']"))
            			item.click("//li[@class='cust-name-cnt']");
            	else  continue; 
            } else {
            	if(item.isElementPresent("//div[@class='gs_filter_option_section']"))
            			item.click("//li[contains(@class, 'cust-name-starts')]");
            	else  continue; 
            }
        }
        }
        driver.findElement(By.xpath(ACC_INS_NAME_INPUT)).sendKeys(name);
        driver.findElement(By.xpath(ACC_INS_NAME_INPUT)).sendKeys(Keys.ENTER);
        wait.waitTillElementDisplayed(CUST_SELECT_LIST, MIN_TIME, MAX_TIME);
        driver.findElement(By.xpath("//li[@class='ui-menu-item' and @role = 'presentation']/a[contains(text(),'"+name+"')]")).click();
        Log.info("Customer Search Completed. ");
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

    public RelatedList360 clickOnRelatedListSec(String secName) {
        Timer.sleep(2);
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
					+ amtUtil.formatNumber(transaction.getMRR())
					+ cContains);
		if (transaction.getASV() != null)
			xpath.append(sContains
					+ amtUtil.formatNumber(transaction.getASV())
					+ cContains);
		if (transaction.getUsers() != null)
			xpath.append(sContains + transaction.getUsers() + cContains);
		if (transaction.getOTR() != null)
			xpath.append(sContains
					+ amtUtil.formatNumber(transaction.getOTR())
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
