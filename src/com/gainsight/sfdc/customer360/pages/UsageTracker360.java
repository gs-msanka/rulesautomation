package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Gainsight
 * Date: 01/01/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageTracker360 extends Customer360Page {

    String TAB_NAME             = "//li[@data-tabname='UsageTracker']/a[contains(text(), 'Usage Tracker')]";
    String GO_BUTTON            = "//button[@class='go getUT' and text()='Go']";
    String DATE_RANGE_SELECT    = "//select[@class='GainsightDummyDatePeriodSelectControl']";
    String ACTION_SELECT        = "//select[@class='configSelect']";
    String DATA_ROW             = "//div[@class='usagetracker-discription']";
    String DATA_ROW_VALUE       = "Action performed: XXX in Module: XXX by user: XXXX on: XXXXXXX" ;
    String NO_DATA_MSG          = "//div[@class='noUsage noDataFound' and contains(text(), 'No Records Found.')]";


    public UsageTracker360() {
        wait.waitTillElementDisplayed(GO_BUTTON, MIN_TIME, MAX_TIME);
    }

    public UsageTracker360 viewUsageData(String measure, String timeInterval) {
        item.selectFromDropDown(ACTION_SELECT, measure);
        item.selectFromDropDown(DATE_RANGE_SELECT, timeInterval);
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
        Report.logInfo(xPath);
        return xPath;
        //div[@class='usagetracker-discription']/span[contains(text(),'Action performed')]/following-sibling::span[contains(text(), 'Emails Sent Count')]/following-sibling::span[contains(text(), 'in Module:')]/following-sibling::span[contains(text(), 'Customers')]/following-sibling::span[contains(text(), 'by user:')]/following-sibling::span[contains(text(), 'Giribabu')]/following-sibling::span[contains(text(), ' on:')]/following-sibling::span[contains(text(), '2')]
    }




}
