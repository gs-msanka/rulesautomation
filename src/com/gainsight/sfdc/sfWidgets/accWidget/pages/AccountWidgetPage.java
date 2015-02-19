package com.gainsight.sfdc.sfWidgets.accWidget.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pojo.TimeLineItem;
import com.gainsight.sfdc.pages.BasePage;

import java.util.HashMap;

/**
 * Created by gainsight on 26/12/14.
 */
public class AccountWidgetPage extends BasePage {

    private final String IFRAME = "//iframe[contains(@title,'CustomerSuccess')]";
    private final String READY_INDICATOR="//h3[contains(text(),'Customer Success')]";
    private final String TRANSACTIONS_TAB = "//a[@id='Transactions']";
    private final String ADOPTION_TAB = "//a[@id='UserAdoption']";
    private final String C360_LINK = "//a[@id='cust360']";
    private final String SAVE_BUTTON = "//a[text()='Save']";
    private final String EDIT_BUTTON = "//a[text()='Edit']";
    private final String CUSTOMER_ADD_BUTTON="//input[@value='Add']";
    private final String CLICK_HERE_LINK="//a[text()='click here']";
    private final String TRANSACTION_DIV="OppWidgetTransactions";
    private final String WIDGET_TEXT="//body[contains(.,'%s')]";
    private final String NEW_BUTTON="//a/span[text()='New']";
    private final String OVERLAY_BLOCK="//div[@class='overlayBackground jbaraDummyOverLayFormForOpp']";
    private final String COCKPIT_SUBTAB="//a[@class='Cockpit']";
  	private final String COCKPIT_INDICATOR="//div[@class='gs_section_title']/h1[contains(text(),'Cockpit')]";
  	private final String FEATURES_TAB = "//a[@class='Features']";
  	
    public AccountWidgetPage() {
        //wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public void verifyTextPresent(String text) {
        field.switchToFrame(IFRAME);
        wait.waitTillElementPresent(String.format(WIDGET_TEXT,text), MIN_TIME, MAX_TIME);
        field.switchToMainWindow();
    }
    public String getTransactionMessage() {
        field.switchToFrame(IFRAME);
        System.out.println(field.getText(TRANSACTION_DIV));
        String text = field.getText(TRANSACTION_DIV);
        field.switchToMainWindow();
        return text;
    }

    public AccountWidgetPage selectAdoptionSection() {
        field.switchToFrame(IFRAME);
        item.click(ADOPTION_TAB);
        field.switchToMainWindow();
        return this;
    }
    
    public AccWidget_FeaturesPage selectFeaturesSubTab() {
        item.click(FEATURES_TAB);
        return new AccWidget_FeaturesPage();
    }


     public AccWidget_CockpitPage gotoCockpitSubTab(){
        item.click(COCKPIT_SUBTAB);
        wait.waitTillElementDisplayed(COCKPIT_INDICATOR, MIN_TIME,MAX_TIME);
        return new AccWidget_CockpitPage();
    }

    public AccountWidgetPage selectTransactionSection() {
        field.switchToFrame(IFRAME);
        item.click(TRANSACTIONS_TAB);
        field.switchToMainWindow();
        return this;
    }

    public Customer360Page clickOn360View() {
        field.switchToFrame(IFRAME);
        item.click(C360_LINK);
        field.switchToMainWindow();
        return new Customer360Page();
    }

    public AccountWidgetPage addNewBusiness(HashMap<String, String> data) {
        field.switchToFrame(IFRAME);
        transactionUtil.addNewBusiness(data);
        wait.waitTillElementNotDisplayed(OVERLAY_BLOCK, MIN_TIME, MAX_TIME);
        field.switchToMainWindow();
        return this;
    }

    public AccountWidgetPage clickOnSave() {
        field.switchToFrame(IFRAME);
        item.click(SAVE_BUTTON);
        wait.waitTillElementDisplayed(EDIT_BUTTON, MIN_TIME, MAX_TIME);
        field.switchToMainWindow();
        return this;
    }

    public boolean isEditButtonPresent() {
        field.switchToFrame(IFRAME);
        boolean flag = field.isElementPresent(EDIT_BUTTON);
        field.switchToMainWindow();
        return flag;
    }

    public AccountWidgetPage clickOnAddCustomer() {
        field.switchToFrame(IFRAME);
        item.click(CUSTOMER_ADD_BUTTON);
        Timer.sleep(2);
        Timer.sleep(2);
        field.switchToMainWindow();
        return this;
    }

    public AccountWidgetPage clickHere() {
        field.switchToFrame(IFRAME);
        item.click(CLICK_HERE_LINK);
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
        field.switchToMainWindow();
        return this;
    }

    public AccountWidgetPage clickTransactionNew(){
        field.switchToFrame(IFRAME);
        item.click(NEW_BUTTON);
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
        field.switchToMainWindow();
        return this;
    }

    public boolean isLineItemPresent(TimeLineItem transaction){
        field.switchToFrame(IFRAME);
        boolean flag=transactionUtil.isLineItemPresent(transaction);
        field.switchToMainWindow();
        return flag;

    }
}