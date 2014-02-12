package com.gainsight.sfdc.adoption.tests;

import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import org.codehaus.jackson.map.ObjectMapper;
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
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    static String resDir = "./resources/datagen/";
    String OBJECT_NAME = "JBCXM__UsageData__c";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";


    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/Instance_Level_Weekly.txt";
        try{
            //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
            DataETL dataLoader = new DataETL();
            dataLoader.cleanUp(isPackageInstance() ? USAGE_NAME : removeNameSpace(USAGE_NAME), null);
            dataLoader.cleanUp(isPackageInstance() ? CUSTOMER_INFO : removeNameSpace(CUSTOMER_INFO), null);
            jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
            dataLoader.execute(jobInfo1);
            jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
            dataLoader.execute(jobInfo2);
            dataLoader.cleanUp(isPackageInstance() ? OBJECT_NAME : removeNameSpace(OBJECT_NAME), null);
            jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Instance_Weekly.txt"), JobInfo.class);
            apex.runApexCodeFromFile(measureFile);
            apex.runApexCodeFromFile(advUsageConfigFile);
            dataLoader.execute(jobInfo3);

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
            for(int k = 0; k< 12;k++) {
                for(int m=0; m < 5; m++, i=i-7) {
                    //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                    // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                    dateStr     = getWeekLabelDate("Wed", i, true);
                    System.out.println(dateStr);
                    year        = (dateStr != null && dateStr.split("\\|").length > 0) ? Integer.valueOf(dateStr.split("\\|")[0]) : c.get(Calendar.YEAR);
                    month       = (dateStr != null && dateStr.split("\\|").length > 1) ? Integer.valueOf(dateStr.split("\\|")[1]) : c.get(Calendar.MONTH);
                    day         = (dateStr != null && dateStr.split("\\|").length > 2) ? Integer.valueOf(dateStr.split("\\|")[2]) : c.get(Calendar.DATE);
                    code        = stringBuilder.toString();
                    code        = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                            .replaceAll("THEYEARCHANGE", String.valueOf(year))
                            .replace("THEDAYCHANGE", String.valueOf(day));
                    if(!isPackageInstance()) {
                        code    = removeNameSpace(code).replace("JBCXM.", "");
                    }
                    apex.runApex(code);
                }
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
        public String getWeekLabelDate(String weekDay, int daysToAdd, boolean usesEndDate) {
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
                int weekDate = days.get(weekDay);
                if(usesEndDate) {
                    weekDate = (weekDate ==1) ? 7 : weekDate-1;
                }
                c.set(Calendar.DAY_OF_WEEK, weekDate);
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
