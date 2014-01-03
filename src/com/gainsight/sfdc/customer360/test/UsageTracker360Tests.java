package com.gainsight.sfdc.customer360.test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.UsageTracker360;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.junit.Test;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 03/01/14
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class UsageTracker360Tests  extends BaseTest {
    private final String TEST_DATA_FILE = "testdata/sfdc/usageTracker/data/UsageTracker_360_TestData.xls";
    private final String DATA_SCRIPT_FILE  = env.basedir+"/testdata/sfdc/usageTracker/scripts/Usage_Tracker_Data.txt";
    private final String SETUP_SCRIPT_FILE  = env.basedir+"/testdata/sfdc/usageTracker/scripts/Usage_Tracker_Measures_Create.txt";
    @BeforeClass
    public void setUp() {
        basepage.login();
        userLocale = soql.getUserLocale();
        apex.runApexCodeFromFile(SETUP_SCRIPT_FILE, isPackageInstance());
        apex.runApexCodeFromFile(DATA_SCRIPT_FILE, isPackageInstance());
    }

    @org.testng.annotations.Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "UT_360_1")
    public void infoMsgVerification(HashMap<String, String> testData) {
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("customer"), true);
        UsageTracker360 uTPage = c360Page.clickOnUsageTracker();
        uTPage.viewUsageData(testData.get("measure"), testData.get("time"));
        Assert.assertTrue(uTPage.isInfoMessageDisplayed(), "Checking weather the information message is displayed.");
    }

    @org.testng.annotations.Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "UT_360_2")
    public void dataVerification(HashMap<String, String> testData) {
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("customer"), true);
        UsageTracker360 uTPage = c360Page.clickOnUsageTracker();
        uTPage = uTPage.viewUsageData(testData.get("measure"), testData.get("time"));
        uTPage.waitforUTDataDisplay();
        List<HashMap<String, String>> udList = new ArrayList<HashMap<String, String>>();
        try {
            for(int i =1; i < 10 ; i++) {
                HashMap<String, String> usageData = getMapFromData(testData.get("UsageData"+i));
                usageData.put("date", getDatewithFormat(Integer.valueOf(usageData.get("date"))));
                udList.add(usageData);
            }
        } catch (Exception e) {
            Report.logInfo("All Usage Data Records Loaded");
        }
        for(HashMap<String, String> uD : udList) {
            Assert.assertTrue(uTPage.isUsageDataDisplayed(uD), "Checking for usage data display");
        }
    }

    @org.testng.annotations.Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "UT_360_3")
    public void dataVerificationandFilter(HashMap<String, String> testData) {
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("customer"), true);
        UsageTracker360 uTPage = c360Page.clickOnUsageTracker();
        uTPage = uTPage.viewUsageData(testData.get("measure"), testData.get("time"));
        uTPage.waitforUTDataDisplay();
        List<HashMap<String, String>> udList = new ArrayList<HashMap<String, String>>();
        try {
            for(int i =1; i < 10 ; i++) {
                HashMap<String, String> usageData = getMapFromData(testData.get("UsageData"+i));
                usageData.put("date", getDatewithFormat(Integer.valueOf(usageData.get("date"))));
                udList.add(usageData);
            }
        } catch (Exception e) {
            Report.logInfo("All Usage Data Records Loaded");
        }
        for(HashMap<String, String> uD : udList) {
            Assert.assertTrue(uTPage.isUsageDataDisplayed(uD), "Checking for usage data display");
        }
    }

    @org.testng.annotations.Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "UT_360_4")
    public void infoandDataVerification(HashMap<String, String> testData) {
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("customer"), true);
        UsageTracker360 uTPage = c360Page.clickOnUsageTracker();
        uTPage = uTPage.viewUsageData(testData.get("measure"), testData.get("time"));
        uTPage.waitforUTDataDisplay();
        List<HashMap<String, String>> udList = new ArrayList<HashMap<String, String>>();
        try {
            for(int i =1; i < 10 ; i++) {
                HashMap<String, String> usageData = getMapFromData(testData.get("UsageData"+i));
                usageData.put("date", getDatewithFormat(Integer.valueOf(usageData.get("date"))));
                udList.add(usageData);
            }
        } catch (Exception e) {
            Report.logInfo("All Usage Data Records Loaded");
        }
        for(HashMap<String, String> uD : udList) {
            Assert.assertTrue(uTPage.isUsageDataDisplayed(uD), "Checking for usage data displayed");
        }
        uTPage = uTPage.viewUsageData(testData.get("measure1"), testData.get("time1"));
        uTPage.waitforUTInfoMsgDisplay();
        Assert.assertTrue(uTPage.isInfoMessageDisplayed(), "Checking weather the information message is displayed.");
    }


    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
