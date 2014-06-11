package com.gainsight.sfdc.customer.pages;

import com.gainsight.sfdc.pages.BasePage;

public class CustomerBasePage extends BasePage {
    private final String READY_INDICATOR = "//a[@data-tab='CUSTOMERS']";

    public CustomerBasePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public CustomersPage clickOnCustomersSubTab() {
        item.click("//a[@data-tab='CUSTOMERS']");
        return new CustomersPage();
    }

    public AnalyticsPage clickOnAnalyticsTab() {
        item.click("//a[text()='Analytics']");
        return new AnalyticsPage();
    }

    public MyNotificationPage clickOnMyNotificationSubTab() {
        item.click("//a[text()='Analytics']");
        return new MyNotificationPage();
    }
}
