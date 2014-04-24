package com.gainsight.sfdc.retention.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class RetentionBasePage extends BasePage{
    private final String READY_INDICATOR    = "//a[@title='Retention Tab - Selected']";
    private final String ALERTS_TAB         = "//div[@class='ge-tabbar']/a[text()='Alerts']";
    private final String EVENTS_TAB         = "//a[text()='Events']";
    private final String PLAYBOOKS_TAB      = "//a[text()='Playbooks']";


    public RetentionBasePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public RetentionBasePage(String val) {
        Report.logInfo("Page Constructor Instantiated From :" +val);
    }

    /**
     * Clicks on alerts sub tab under retention tab.
     * @return
     */
    public AlertsPage clickOnAlertsTab() {
        item.click(ALERTS_TAB);
        return new AlertsPage();
    }

    /**
     * Clicks on events sub tab under retention tab.
     * @return
     */
    public EventsPage clickOnEventsTab() {
        //driver.manage().window().maximize();
        item.click(EVENTS_TAB);
        return new EventsPage();
    }

    /**
     * Clicks on playbooks sub tab under retention tab.
     * @return
     */
    public PlayBooksPage clickOnPlaybooksTab() {
        item.click(PLAYBOOKS_TAB);
        return new PlayBooksPage();
    }



}
