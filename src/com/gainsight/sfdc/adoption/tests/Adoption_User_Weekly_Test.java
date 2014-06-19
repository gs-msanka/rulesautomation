package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

public class Adoption_User_Weekly_Test extends BaseTest {
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    String resDir = userDir + "/resources/datagen/";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";
    String QUERY = "DELETE [SELECT ID FROM JBCXM__StatePreservation__c Where Name = 'Adoption'];";
    String CUST_SET_DELETE = "JBCXM__JbaraRestAPI.deleteActivityLogInfoRecord('DataLoadUsage');";


    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        String measureFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/User_Level_Weekly.txt";

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        apex.runApex(resolveStrNameSpace(QUERY));
        //apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(measureFile, isPackageInstance());
        apex.runApexCodeFromFile(advUsageConfigFile, isPackageInstance());
        DataETL dataLoader = new DataETL();
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        dataLoader.cleanUp(resolveStrNameSpace(CUSTOMER_INFO), null);
        jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
        dataLoader.execute(jobInfo1);
        jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
        dataLoader.execute(jobInfo2);
        jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_User_Weekly.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);

        BufferedReader reader;
        String fileName = env.basedir + "/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
        String line = null;
        String code = "";
        reader = new BufferedReader(new FileReader(fileName));
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        int year, month, day;
        String dateStr;
        //Max of only 5 jobs can run in an organization at a given time
        //Care to be taken that there are no apex jobs are running in the organization.
        int i = -7;
        for (int k = 0; k < 2; k++) {
            for (int m = 0; m < 5; m++, i = i - 7) {
                //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                dateStr = getWeekLabelDate("Wed", i, false, false);
                System.out.println(dateStr);
                year = (dateStr != null && dateStr.split("\\|").length > 0) ? Integer.valueOf(dateStr.split("\\|")[0]) : c.get(Calendar.YEAR);
                month = (dateStr != null && dateStr.split("\\|").length > 1) ? Integer.valueOf(dateStr.split("\\|")[1]) : c.get(Calendar.MONTH);
                day = (dateStr != null && dateStr.split("\\|").length > 2) ? Integer.valueOf(dateStr.split("\\|")[2]) : c.get(Calendar.DATE);
                code = stringBuilder.toString();
                code = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                        .replaceAll("THEYEARCHANGE", String.valueOf(year))
                        .replace("THEDAYCHANGE", String.valueOf(day));

                apex.runApex(resolveStrNameSpace(code));
            }
            for (int l = 0; l < 200; l++) {
                String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                        "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                        "and ApexClass.Name = 'AdoptionAggregation'";
                int noOfRunningJobs = getQueryRecordCount(query);
                if (noOfRunningJobs == 0) {
                    Report.logInfo("Aggregate Jobs are finished.");
                    isAggBatchsCompleted = true;
                    break;
                } else {
                    Report.logInfo("Waiting for aggregation batch to complete");
                    Thread.sleep(30000L);
                }
            }
        }
    }

    @Test
    public void Acc_WeeklyAllMeasures1() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("1 Week");
        usage.setDate(getWeekLabelDate("Wed", -7, false, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("Alltech Automotive LLC |315|1,557|10|26,681|1,514|1,100|1,734|10|25,439"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC |214|1,567|10|25,829|1,639|1,093|1,999|10|23,299"));
    }

    @Test
    public void Acc_WeeklyAllMeasures2() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("1 Week");
        usage.setDate(getWeekLabelDate("Wed", -14, false, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("Alltech Automotive LLC |273|1,288|10|19,046|1,642|1,084|2,651|10|23,706"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC |269|1,465|10|26,463|1,567|1,082|2,538|10|29,702"));
    }

    @Test
    public void Acc_WeeklyFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if weekly form is displayed");
    }

    @Test
    public void Acc__WeeklyGridExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDate(getWeekLabelDate("Wed", -7, false, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertEquals(true, usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Acc_WeeklyDataInTrends() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage.setForTimeWeekPeriod("26 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", -7, false, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isDrillDownMsgDisplayed("Click on a data point in the graph above to view detailed data."));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Active Users|242|224.4|325.7|299.2|214.8|233|364.8|225.1|200.8|239"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("DB Size|1,603.5|1,480.5|1,332|1,213.5|1,348|1,286|1,786|1,471.5|1,657.5|1,555.5"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Emails Sent Count|10|10|10|10|10|10|10|10|10|10"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded|30,904|35,281|32,686|28,509|16,835|23,312|24,146|26,614|19,006|20,184"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Leads|1,658|1,587|1,644|1,534|1,581|1,506|1,534|1,562|1,509|1,539"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Campaigns|1,074|1,027|951|1,060|1,083|1,044|978|1,093|1,083|1,065"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run|1,552.7|2,882.7|2,315.7|2,107.5|2,820.1|1,697.2|2,046.6|2,287.6|3,279.3|2,561.9"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views|10|10|10|10|10|10|10|10|10|10"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits|26,583|19,803|27,634|26,252|23,528|28,913|23,332|14,764|28,597|27,048"));
    }

    @Test
    public void Acc_WeeklyDataInTrendsPartial() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", 28, false, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks"));
        Assert.assertTrue(analyticsPage.isDrillDownMsgDisplayed("Click on a data point in the graph above to view detailed data."));
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }

}