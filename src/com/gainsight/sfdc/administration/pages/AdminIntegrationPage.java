package com.gainsight.sfdc.administration.pages;

import java.util.Iterator;
import java.util.Set;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.pages.Constants;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.openqa.selenium.By;

public class AdminIntegrationPage extends BasePage {
	
	private static final String READY_INDICATOR     = "//a[contains(@text,'Gainsight Matrix Data Platform')]";
	private static final String SIDE_SECTION        = "//div[contains(@class, 'main-cnt')]";
    private static final String MDA_AUTH_SEC_TITLE  = "//div[@class='title md-platform-title']/div[@class='mda-title' and text()='Authorize MDA']";
	private static final String OAUTH_ENABLE        = "//input[@id='MA-Salesforce-myonoffswitch']/following-sibling::label";
	private static final String AUTHORIZE           = "btnAuthorize";
	private static final String AUTHORIZE_DISABLED  = "//button[@id='btnAuthorize' and @disabled='disabled']";
	private static final String REVOKE              = "btnRevoke";
    private static final String MDA_APP_NAME        = "//h2[contains(text(),'dev_app')]";
    private static final String ALLOW_OAUTH_ACCESS  = "//input[@value=' Allow ']";
    private static final String CLOSE_OAUTH_WINDOW = "//input[@value='Close']";
	private static final String MARKETO_ENABLE      = "";
	private static final String GSEMAIL_ENABLE      = "//div[@class='gs-cta-head EMAIL_SERVICE']//div[@class='onoffswitch-switch']";
	private static final String GSEMAIL_ON          = "//button[@class='btnEnable gs-btn btn-add' and @apptype='EMAIL_SERVICE']";
	private static final String GSEMAIL_OFF         = "//button[@class='btnDisable gs-btn btn-cancel' and @action='DISABLE']";
	private static final String GSEMAIL_SECTION     = "//div[@class='data-title' and text()='Gainsight Email Service']";

    private static final String AUTHORIZE_MDA_SECTION   = "//div[@class='gs-cta-head SFDC']/descendant::div[@class='data-title' and contains(text(), 'Authorize MDA')]";
    private static final String DATA_LOAD_CONFIG_SEC    = "//div[contains(@class, DATA_API)]/descendant::div[@class='data-title' and contains(text(), Data Load Configuration)]";
    private static final String GENERATE_CURL           = "//input[@type='button' and @value='Generate cURL']";


    public void AdminIntegrationPage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public void clickOnEnableGSMDP() {
        item.click(OAUTH_ENABLE);
        wait.waitTillElementDisplayed(SIDE_SECTION, MIN_TIME, MAX_TIME);
    }

    public void enableGSEmail() {
        item.click(GSEMAIL_ENABLE);
        wait.waitTillElementDisplayed(GSEMAIL_SECTION, MIN_TIME, MAX_TIME);
        item.click(GSEMAIL_ON);

    }

    public AdminIntegrationPage clickOnAuthorizeMDASection() {
        item.click(AUTHORIZE_MDA_SECTION);
        sleep(STALE_PAUSE);
        wait.waitTillElementDisplayed(MDA_AUTH_SEC_TITLE, MIN_TIME, MAX_TIME);
        return this;
    }

    public AdminIntegrationPage clickOnDataLoadConfiguration() {
        item.click(DATA_LOAD_CONFIG_SEC);
        wait.waitTillElementDisplayed(GENERATE_CURL, MIN_TIME, MAX_TIME);
        return this;
    }

    /**
     * Authorizes MDA - Matrix Data Platform.
     */
    public AdminIntegrationPage authorizeMDA() {
        if (element.getElement(By.id(REVOKE)).isDisplayed()) {
            item.click(AUTHORIZE);
            sleep(15);      // This is required as there's no element available in the pop-up UI as ready indicator.
            Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
            Iterator<String> iterator = windowId.iterator();
            String mainWinID = iterator.next();
            String oauthWinID = iterator.next();
            driver.switchTo().window(oauthWinID);
            Log.info("Page Title : " + driver.getTitle());
            if (isElementPresentAndDisplay(By.xpath(CLOSE_OAUTH_WINDOW))) {
                item.click(CLOSE_OAUTH_WINDOW);
                driver.switchTo().window(mainWinID);
            } else {
                wait.waitTillElementDisplayed(MDA_APP_NAME, MIN_TIME, MAX_TIME);
                item.click(ALLOW_OAUTH_ACCESS);
                if (item.isElementPresent("//p[contains(text(),'Authorization successful')]")) {
                    item.click(CLOSE_OAUTH_WINDOW);
                    driver.switchTo().window(mainWinID);
                } else {
                    Log.error("OAUTH Failed.");
                    throw new RuntimeException("OAuth Failed");
                }
            }
            wait.waitTillElementDisplayed("//span[contains(text(), 'Authorized Gainsight Matrix Data Architecture, You can now activate connectors.')]", MIN_TIME, MAX_TIME);
            wait.waitTillElementDisplayed(REVOKE, MIN_TIME, MAX_TIME);
            Log.info("OAuth Enabled successfully.");
        } else {
            Log.info("OAuth already enabled");
        }
        return this;
    }

    public AdminIntegrationPage clickOnRevoke() {
        if (item.isElementPresent(AUTHORIZE_DISABLED)) {
            item.click(REVOKE);
            sleep(STALE_PAUSE);
            wait.waitTillElementDisplayed(AUTHORIZE, MIN_TIME, MAX_TIME);
        } else {
            Log.error("Not yet authorized");
        }
        return this;
    }
}
