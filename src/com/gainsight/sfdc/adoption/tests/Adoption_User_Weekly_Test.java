package com.gainsight.sfdc.adoption.tests;

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

import java.io.FileReader;
import java.io.IOException;
import java.util.TimeZone;

public class Adoption_User_Weekly_Test extends BaseTest {
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    String resDir = userDir + "/resources/datagen/";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";
    String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    String CUST_SET_DELETE = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";

    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        isPackage = isPackageInstance();
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
        String measureFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/User_Level_Weekly.txt";

        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(measureFile, isPackage);
        apex.runApexCodeFromFile(advUsageConfigFile, isPackage);
        DataETL dataLoader = new DataETL();
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        dataLoader.cleanUp(resolveStrNameSpace(CUSTOMER_INFO), null);
        jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
        dataLoader.execute(jobInfo1);
        jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
        dataLoader.execute(jobInfo2);
        jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_User_Weekly.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);

        runAdoptionAggregation(10, true, false, "Wed");
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