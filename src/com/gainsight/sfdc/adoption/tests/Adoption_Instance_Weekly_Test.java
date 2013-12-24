package com.gainsight.sfdc.adoption.tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Adoption_Instance_Weekly_Test extends BaseTest {
    Calendar c                      = Calendar.getInstance();
    Boolean isAggBatchsCompleted    = false;
    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Usage_Measure_Create.txt";
        String advUsageConfigFile   = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Instance_Level_Weekly.txt";
        try{
          //  apex.runApexCodeFromFile(measureFile);
           // apex.runApexCodeFromFile(advUsageConfigFile);
            /**
             * Data Should be loaded here.
             */
            BufferedReader reader;
            String fileName = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
            String line     = null;
            String code     = "";
            reader          = new BufferedReader(new FileReader(fileName));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            int year, month, day;
            String dateStr;
            //Max of only 5 jobs can run in an organization at a given time
            //Care to be taken that there are no apex jobs are running in the organization.
            int i= -7;
            for(int k = 0; k< 45;k++) {
                for(int m=0; m < 5; m++, i=i-7) {
                    //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                    // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                    dateStr     = getWeekLabelDate("Wed", i);
                    System.out.println(dateStr);
                    year        = (dateStr != null && dateStr.split("\\|").length > 0) ? Integer.valueOf(dateStr.split("\\|")[0]) : c.get(Calendar.YEAR);
                    month       = (dateStr != null && dateStr.split("\\|").length > 1) ? Integer.valueOf(dateStr.split("\\|")[1]) : c.get(Calendar.MONTH);
                    day         = (dateStr != null && dateStr.split("\\|").length > 2) ? Integer.valueOf(dateStr.split("\\|")[2]) : c.get(Calendar.DATE);
                    code        = stringBuilder.toString();
                    code        = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                            .replaceAll("THEYEARCHANGE", String.valueOf(year))
                            .replace("THEDAYCHANGE", String.valueOf(day));
                    if(!isPackageInstance()) {
                        code    = code.replace("JBCXM__", "").replace("JBCXM.", "");
                    }
                    apex.runApex(code);
                }
                Thread.sleep(60000L);
                for(int l= 0; l < 200; l++) {
                    String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                            "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                            "and ApexClass.Name = 'AdoptionAggregation'";
                    int noOfRunningJobs = getQueryRecordCount(query);
                    if(noOfRunningJobs==0) {
                        Report.logInfo("Aggregate Jobs are finished.");
                        isAggBatchsCompleted = true;
                        break;
                    } else {
                        Report.logInfo("Waiting for aggregation batch to complete");
                        Thread.sleep(30000L);
                    }
                }
            }

        } catch (Exception e) {
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    @Test
    public void viewWeeklyInsData() {

        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
    }
        /*
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

    /**
     * This parameter returns the String with comprises of yyyy|mm|dd format.
     * @param weekStartDay - Expected values Sun, Mon, Tue, Wed, Thu, Fri, Sat.
     * @param daysToAdd - number of days to add for current day.
     * @return String of format "yyyy|mm|dd".
     */
    public String getWeekLabelDate(String weekStartDay, int daysToAdd) {
        String date= null;
        try {
            Calendar c = Calendar.getInstance();
            Map<String,Integer> days = new HashMap<String, Integer>();
            days.put("Sun", 1);
            days.put("Mon", 2);
            days.put("Tue", 3);
            days.put("Wed", 4);
            days.put("Thu", 5);
            days.put("Fri", 6);
            days.put("Sat", 7);
            c.set(Calendar.DAY_OF_WEEK, days.get(weekStartDay));
            System.out.println(c.get(Calendar.DATE));
            c.add(Calendar.DATE, daysToAdd);
            int day = c.get(Calendar.DATE);
            int month = c.get(Calendar.MONTH);
            month += 1;
            int year = c.get(Calendar.YEAR);
            date = year + "|" + month + "|"+day;
        } catch (NullPointerException e) {
            Report.logInfo(e.getLocalizedMessage());
        }
        return date;
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
