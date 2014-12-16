package com.gainsight.sfdc.workflow.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;
import com.sforce.soap.metadata.Workflow;

public class WorkflowBasePage extends BasePage {
	
	private static final String CREATE_FORM_CUSTOMER_LINK = null;
	private final String READY_INDICATOR   			= "//input[contains(@class, 'searchTxt form-control global-search')]";
	private final String LIST_VIEW_TAB         		= "//a[@data-type='LIST']";
    private final String CALENDAR_VIEW_TAB         	= "//a[@data-type='CALENDAR']";
    private final String REPORTING_TAB              = "//a[@data-type='REPORTS']";
    private final String PLAYBOOKS_TAB     			= "//a[@class='workflow-playbooks require-tooltip']";
    private final String SHOW_SALESFORCE_HEADER_ICON= "//a[@class='tooltips h_show']";		//show salesforce header
	private final String HIDE_SALESFORCE_HEADER_ICON= "//a[@class='tooltips h_hide']";		//hide salesforce header
  
    public WorkflowBasePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }
  
    public WorkflowBasePage(String value){
    	Report.logInfo("landed in Cockpit "+value);
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
    	Report.logInfo("Started Method - showSalesForceHeader.");
    	item.click(SHOW_SALESFORCE_HEADER_ICON);
    	wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    	Report.logInfo("Ended Method - showSalesForceHeader.");
    }
    
   
}
