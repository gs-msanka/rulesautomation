package com.gainsight.sfdc.adoption.pages;


import com.gainsight.sfdc.pages.BasePage;

public class AdoptionBasePage extends BasePage {

    private final String READY_INDICATOR = "//a[@data-tab='ADOPTION']";
    private final String OVER_SUB_TAB = "//a[@data-tab='ADOPTION']";
    private final String TRENDS_SUB_TAB = "//a[@data-tab='TRENDS']";
    private final String EXPLORER_SUB_TAB = "//a[@data-tab='ANALYTICS']";

    public AdoptionBasePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }


    public AdoptionUsagePage clickOnOverviewSubTab()  {
        item.click(OVER_SUB_TAB);
        return new AdoptionUsagePage();
    }

    public AdoptionAnalyticsPage clickOnTrendsSubTab() {
        item.click(TRENDS_SUB_TAB);
        return new AdoptionAnalyticsPage();
    }

    public AdoptionExplorerPage clickOnExplorerSubTab() {
        item.click(EXPLORER_SUB_TAB);
        return new AdoptionExplorerPage();
    }

    public void selectValueInDropDown(String value) {
        wait.waitTillElementDisplayed("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]", MIN_TIME, MAX_TIME);
        item.click("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]");
    }



}
