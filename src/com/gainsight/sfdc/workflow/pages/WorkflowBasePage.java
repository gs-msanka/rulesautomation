package com.gainsight.sfdc.workflow.pages;

import com.gainsight.testdriver.Log;
import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.pages.BasePage;

public class WorkflowBasePage extends BasePage {
	
	private static final String CREATE_FORM_CUSTOMER_LINK = null;
	private final String READY_INDICATOR   			= "//input[contains(@class, 'searchTxt form-control global-search')]";
	private final String LIST_VIEW_TAB         		= "//a[@data-type='LIST']";
    private final String CALENDAR_VIEW_TAB         	= "//a[@data-type='CALENDAR']";
    private final String REPORTING_TAB              = "//a[@data-type='REPORTS']";
    private final String PLAYBOOKS_TAB     			= "//a[contains(@class,'workflow-playbooks require-tooltip')]";
    private final String SHOW_SALESFORCE_HEADER_ICON= "//a[@class='tooltips h_show']";		//show salesforce header
	private final String HIDE_SALESFORCE_HEADER_ICON= "//a[@class='tooltips h_hide']";		//hide salesforce header
  
    public WorkflowBasePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }
  
    public WorkflowBasePage(String value){
    	Log.info("landed in Cockpit " + value);
    }
    public WorkflowPage clickOnListView() {
        item.click(LIST_VIEW_TAB);
        return new WorkflowPage();
    }

    public WorkflowPage clickOnCalendarView() {
        item.click(CALENDAR_VIEW_TAB);
        return new WorkflowPage("Calendar");
    }

    public WorkFlowReportingPage clickOnReportingView() {
        item.click(REPORTING_TAB);
        return new WorkFlowReportingPage();
    }

    /**
     * Clicks on playbooks sub tab under Workflow tab.
     * @return
     */
    public WorkflowPlaybooksPage clickOnPlaybooksTab() {
        item.click(PLAYBOOKS_TAB);
        return new WorkflowPlaybooksPage();
    }

    /**
     * Clicks on Show Salesforce icon in Workflow tab.
     * @return
     */
    public void showSalesForceHeader(){
    	Log.info("Started Method - showSalesForceHeader.");
    	item.click(SHOW_SALESFORCE_HEADER_ICON);
    	wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    	Log.info("Ended Method - showSalesForceHeader.");
    }

    public void selectValueInDropDown(String value) {
        boolean selected = false;
        for(WebElement ele : element.getAllElement("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]")) {
            Log.info("Checking : "+ele.isDisplayed());
            if(ele.isDisplayed()) {
                ele.click();
                selected = true;
                break;
            }
        }
        if(selected != true) {
            throw new RuntimeException("Unable to select element : //input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]" );
        }
    }


}
