package com.gainsight.sfdc.sfWidgets.accWidget.pages;

import com.gainsight.sfdc.pages.BasePage;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by gainsight on 26/12/14.
 */
public class AccountsPage extends BasePage {
    private final String READY_INDICATOR="//h3[text()='Recent Accounts']";


    public AccountsPage(){
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public AccountPage goToAccount(String accID){
        URI currentURL;
        try {
            currentURL = new URI(driver.getCurrentUrl());
            String accURL="https://" + currentURL.getHost()+"/"+accID;
            driver.navigate().to(accURL);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to navigate to account page");
        }
        return new AccountPage();


    }

}
