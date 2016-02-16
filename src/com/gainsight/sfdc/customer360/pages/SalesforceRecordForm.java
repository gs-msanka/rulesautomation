package com.gainsight.sfdc.customer360.pages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 03/01/14
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SalesforceRecordForm extends BasePage {
    private final String READY_INDICATOR    = "//div[@id='userNavButton']";
    private final String SAVE_BUTTON = "//td[@id='topButtonRow']/input[@name='save']";
    private final String EDIT_BUTTON = "//td[@id='topButtonRow']/input[@name='edit']";
    private final String RECORD_NAME="//html/body//div[@id='contentWrapper']//input[@id='Name']";
    private final String CONTACT_NAME="//html/body//div[@id='contentWrapper']//span/input[@id='cas3']";
    private final String CONTACT_ORIGIN="//html/body//div[@id='contentWrapper']//span/select[@id='cas11']";

    
    

    
    public SalesforceRecordForm() {
        wait.waitTillElementDisplayed(READY_INDICATOR,MIN_TIME, MAX_TIME);
    }

    public boolean verifyRecordViewIsDisplayed(String objectId) {
        wait.waitTillElementDisplayed(EDIT_BUTTON, MIN_TIME, MAX_TIME);
        String s = driver.getCurrentUrl();
        Log.info("Window URL :" + s);
        Log.info("Object Id :" +objectId);
        Pattern p = Pattern.compile("(.salesforce.com/"+objectId+"...............)");
        Matcher m = p.matcher(s);
        return m.find();
    }

    public boolean verifyRecordAddIsDisplayed(String objectId) {
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
        String s = driver.getCurrentUrl();
        System.out.println("url======="+s);
        Log.info("Window URL :" +s);
        Log.info("Object Id :" +objectId);
        Pattern p = Pattern.compile("^https://[^/?]+\\.salesforce\\.com/"+objectId+"/e\\?saveURL(.*)\\.visual\\.force\\.com/apex/CustomerSuccess360");
        Matcher m = p.matcher(s);
        return m.find();
    }

    public boolean verifyRecordEditViewIsDisplayed(String objectId) {
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
        String s = driver.getCurrentUrl();
        Log.info("Window URL :" +s);
        Log.info("Object Id :" +objectId);
        Pattern p = Pattern.compile("^https://[^/?]+\\.salesforce\\.com/"+objectId+".............../e\\?retURL(.*)\\.visual\\.force\\.com%2Fapex%2FCustomerSuccess360");
        Matcher m = p.matcher(s);
        return m.find();
    }

    public Customer360Page clickOnSave() {
        item.click(SAVE_BUTTON);
        return new Customer360Page();
    }
    
    public SalesforceRecordForm clickOnCaseSave() {
        item.click(SAVE_BUTTON);
        return new SalesforceRecordForm();
    }
    
    public void recordName(String labelName) {
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
        element.setText(RECORD_NAME, labelName);        
    }
    public void contactName(String labelName) {
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
        element.setText(CONTACT_NAME, labelName);        
    }
    public void contactCaseOrigin(String labelName) {
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
        element.selectFromDropDown(CONTACT_ORIGIN, labelName);      
    }
    



}
