package com.gainsight.sfdc.workflow.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class WorkflowBasePage extends BasePage{
	
	private final String READY_INDICATOR   			= "//a[@title='Cockpit Tab - Selected']";	
	private final String LISTVIEW_TAB         		= "//a[@class='workflow-list require-tooltip']";	
    private final String CALENDARVIEW_TAB         	= "//a[@class='workflow-calendar require-tooltip']";
    private final String OPERATIONALREPORTING_TAB   = "//a[@class='workflow-reports require-tooltip']";
    private final String PLAYBOOKS_TAB     			= "//a[@class='workflow-playbooks require-tooltip']";
    private final String SHOW_SALESFORCE_HEADER_ICON="//a[@class='tooltips h_show']";		//show salesforce header
	private final String HIDE_SALESFORCE_HEADER_ICON="//a[@class='tooltips h_hide']";		//hide salesforce header
    
    public WorkflowBasePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public WorkflowBasePage(String val) {
        Report.logInfo("Page Constructor Instantiated From :" +val);
    }
    
    /**
     * Clicks on playbooks sub tab under Workflow tab.
     * @return
     */
    public WorkflowPlaybooksPage clickOnPlaybooksTab() {
        item.click(PLAYBOOKS_TAB);
        return new WorkflowPlaybooksPage();
    }
    
    public void showSalesForceHeader(){
    	Report.logInfo("Started Method - showSalesForceHeader.");
    	item.click(SHOW_SALESFORCE_HEADER_ICON);
    	wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    	Report.logInfo("Ended Method - showSalesForceHeader.");
    	
    }

}
