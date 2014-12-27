package com.gainsight.sfdc.sfWidgets.accWidget.pages;

import com.gainsight.sfdc.pages.BasePage;

/**
 * Created by gainsight on 26/12/14.
 */
public class AccountPage  extends BasePage {
    private final String READY_INDICATOR="//h2[text()='Account Detail']";

    public AccountPage(){
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);

    }
    public AccountWidgetPage getAccountWidget() {
        return new AccountWidgetPage();
    }

}
