package com.gainsight.sfdc.adoption.tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;

public class AdoptionUsageTest extends BaseTest {

    @BeforeClass
    public void setUp() {
        Report.logInfo("Starting Adoption Usage Test Cases...");
        basepage.login();
    }


    @Test
    public void viewWeeklyInsData() {

        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("9 Weeks");
        usage.setByDataGran("By Instance");
        usage.setDate("7/1/2013");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Instance | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Test acc 1 | Test acc 1 -SandBox Instance | 19/02/2013 | 0 | 0 | 0% | 5 "));
    }


    @Test
    public void viewWeeklyAccData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Visits");
        usage.setNoOfWeeks("12 Weeks");
        usage.setByDataGran("By Account");
        usage.setDate("14/1/2013");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed | YTD Unique"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Test acc 9 | 19/02/2013 | 0 | 0 | 0% | 70 | 30"));
    }

    @Test
    public void testcustAnalyticUsageData() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage = usage.displayCustWeeklyData("Test acc 1", null, "Page Visits | No of Report Run", "78 Weeks", "30/9/2013");
        Assert.assertEquals(true, usage.isInstDropDownLoaded());
        Assert.assertEquals(true, usage.isChartDisplayed());
        Assert.assertEquals(true, usage.isGridDispalyed());
    }

    @Test
    public void testAdoptionGridExport() {
        Report.logInfo("Checking adoption grid export");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        Assert.assertEquals(true, usage.exportGrid());
        Report.logInfo("Checked adoption grid export");
    }

    @Test
    public void testInstanceDropDownloaded() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage.selectCustomer("Test acc 1");
        Assert.assertEquals(true, usage.isInstDropDownLoaded());
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

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
