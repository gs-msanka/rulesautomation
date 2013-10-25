package com.gainsight.sfdc.adoption.pages;


import com.gainsight.sfdc.pages.BasePage;

public class AdoptionBasePage extends BasePage {
    private final String READY_INDICATOR="//a[text()='Analytics']";

    public AdoptionBasePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }
    /**
     * Click on Adoption Grid Page
     * @return AdoptinosUsagePage
     */
    public AdoptionUsagePage clickOnUsageGridSubTab(){
        item.click("//a[text()='Usage']");
        return new AdoptionUsagePage();
    }

    /**
     * Clicks on adoption analytics page.
     * @return adoption analytics page.
     */
    public AdoptionAnalyticsPage clickOnUsageAnalyticsTab(){
        item.click("//a[text()='Analytics']");
        return new AdoptionAnalyticsPage();
    }
}
