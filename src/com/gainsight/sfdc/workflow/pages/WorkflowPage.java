package com.gainsight.sfdc.workflow.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkflowPage extends WorkflowBasePage {

    private final String READY_INDICATOR  = "//div[@title='Add CTA']";
    private final String CALENDAR_VIEW_READY_INDICATOR = "//ul[@class='calendar-tab']";
    private final String LOADING_ICON = "//div[contains(@class, 'gs-loader-image-64')]";

    public WorkflowPage() {
        waitForPageLoad();
    }

    public WorkflowPage(String view) {
        waitForPageLoad();
        wait.waitTillElementDisplayed(CALENDAR_VIEW_READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    private void waitForPageLoad() {
        Report.logInfo("Loading Cockpit Page");
        env.setTimeout(10);
        wait.waitTillElementNotPresent(LOADING_ICON, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
        Report.logInfo("Cockpit Page Loaded Successfully");
    }
}
