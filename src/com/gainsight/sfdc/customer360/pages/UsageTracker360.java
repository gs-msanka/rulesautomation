package com.gainsight.sfdc.customer360.pages;

import com.gainsight.testdriver.Log;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Gainsight
 * Date: 01/01/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageTracker360 extends Customer360Page {

    private final String TAB_NAME             = "//li[@data-tabname='UsageTracker']/a[contains(text(), 'Usage Tracker')]";
    private final String GO_BUTTON            = "//div[@class='usagetracker-data']/span/a[text()='Go']";
    private final String DATE_RANGE_SELECT    = "//div[@class='usagetracker-data']/descendant::select[@class='GainsightDummyDatePeriodSelectControl']/following-sibling::button";
    private final String ACTION_SELECT        = "//div[@class='usagetracker-data']/descendant::select[@class='configSelect']/following-sibling::button";
    private final String DATA_ROW             = "//div[@class='usagetracker-discription']";
    private final String DATA_ROW_VALUE       = "Action performed: XXX in Module: XXX by user: XXXX on: XXXXXXX" ;
    private final String NO_DATA_MSG          = "//div[@class='noUsage noDataFound' and contains(text(), 'No Records Found.')]";


    public UsageTracker360() {
        wait.waitTillElementDisplayed(GO_BUTTON, MIN_TIME, MAX_TIME);
    }

    public UsageTracker360 viewUsageData(String measure, String timeInterval) {
        item.click(ACTION_SELECT);
        item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+measure+"')]");
        item.click(DATE_RANGE_SELECT);
        item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+timeInterval+"')]");
        item.click(GO_BUTTON);
        return new UsageTracker360();
    }

    public void waitforUTDataDisplay() {
        wait.waitTillElementDisplayed(DATA_ROW, MIN_TIME,MAX_TIME);
    }

    public void waitforUTInfoMsgDisplay() {
        wait.waitTillElementDisplayed(NO_DATA_MSG,MIN_TIME,MAX_TIME);
    }


    public boolean isUsageDataDisplayed(HashMap<String, String> testData) {
        boolean result= false;
        try {
            if(element.getElement(buildXPath(testData)).isDisplayed()) {
                result = true;
            } else {
                result =false;
            }
        } catch (Exception e) {
            result =false;
        }
        return result;
    }

    public boolean isInfoMessageDisplayed() {
        return element.getElement(NO_DATA_MSG).isDisplayed();
    }

    public String buildXPath(HashMap<String, String> testData) {
        String xPath = "//div[@class='usagetracker-discription']" +
                        "/span[contains(text(),'Action performed:')]" +
                        "/following-sibling::span[contains(text(), '"+testData.get("action")+"')]" +
                        "/following-sibling::span[contains(text(), 'in Module:')]" +
                        "/following-sibling::span[contains(text(), '"+testData.get("module")+"')]" +
                        "/following-sibling::span[contains(text(), 'by user:')]" +
                        "/following-sibling::span[contains(text(), '"+testData.get("user")+"')]" +
                        "/following-sibling::span[contains(text(), ' on:')]" +
                        "/following-sibling::span[contains(text(), '"+testData.get("date")+"')]";
        Log.info(xPath);
        return xPath;
        //div[@class='usagetracker-discription']/span[contains(text(),'Action performed')]/following-sibling::span[contains(text(), 'Emails Sent Count')]/following-sibling::span[contains(text(), 'in Module:')]/following-sibling::span[contains(text(), 'Customers')]/following-sibling::span[contains(text(), 'by user:')]/following-sibling::span[contains(text(), 'Giribabu')]/following-sibling::span[contains(text(), ' on:')]/following-sibling::span[contains(text(), '2')]
    }




}
