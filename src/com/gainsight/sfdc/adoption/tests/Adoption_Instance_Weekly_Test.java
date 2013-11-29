package com.gainsight.sfdc.adoption.tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;

public class Adoption_Instance_Weekly_Test extends BaseTest {

    @BeforeClass
    public void setUp() {
        Report.logInfo("Starting Adoption Usage Test Cases...");
        basepage.login();
    }

    @Test
    public void viewWeeklyInsData() {

        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Visits");
        usage.setNoOfWeeks("9 Weeks");
        usage.setByDataGran("By Instance");
        usage.setDate("6/30/2013");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Instance | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Test acc 1 | Test acc 1 - INSTANCELEVEL - Sandbox | 02/02/2014 | 0 | 0 | 0% | 40 | 50 | 45 | 30 "));
    }


    @Test
    public void viewWeeklyAccData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("12 Weeks");
        usage.setByDataGran("By Account");
        usage.setDate("7/28/2013");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed | YTD Unique"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Test acc 1043 | 02/02/2014 | 0 | 0 | 0% | 27.5 | 50 | 42.5"));
    }

    @Test
    public void testWeeklyAdopSelectionFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        Assert.assertEquals(true, usage.isWeeklyFormEleDisplayed());
    }

    @Test
    public void testDataGranularitySelectionDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        Assert.assertEquals(true, usage.isDataGranularitySelectionDisplayed());
    }

    @Test
    public void testcustAnalyticUsageData() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage = usage.displayCustWeeklyData("Test acc 103",
                null, "Page Visits | No of Report Run", "78 Weeks", "9/1/2013");
        Assert.assertEquals(true, usage.isChartDisplayed());
        Assert.assertEquals(true, usage.isGridDispalyed());
    }

    @Test
    public void testAdoptionGridExport() {
        Report.logInfo("Checking adoption grid export");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("12 Weeks");
        usage.setByDataGran("By Account");
        usage.setDate("7/28/2013");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.exportGrid());
        Report.logInfo("Checked adoption grid export");
    }

    @Test
    public void testInstanceDropDownloaded() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage.selectCustomer("Test acc 1");
        String insNames = "Test acc 1 - INSTANCELEVEL - Prodcution | Test acc 1 - INSTANCELEVEL - Sandbox";
        Assert.assertEquals(true, usage.isInstDropDownLoaded(insNames));
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
