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

public class Adoption_Instance_Monthly_Test extends BaseTest {
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
    private int month;
    private int year;
    String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    String CUST_SET_DELETE = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";

    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        String measureFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Instance_Level_Monthly.txt";

        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        DataETL dataLoader = new DataETL();
        apex.runApexCodeFromFile(measureFile, isPackageInstance());
        apex.runApexCodeFromFile(advUsageConfigFile, isPackageInstance());
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        dataLoader.cleanUp(resolveStrNameSpace(CUSTOMER_INFO), null);
        jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
        dataLoader.execute(jobInfo1);
        jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
        dataLoader.execute(jobInfo2);
        jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Instance_Monthly.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);

        String fileName = env.basedir + "/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String code = "";
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int day = 15;
        //Max of only 5 jobs can run in an organization at a given time
        //Care to be taken that there are no apex jobs are running in the organization.
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < 5; i++) {
                if (month == 0) {
                    month = 12;
                    year = year - 1;
                }
                code = stringBuilder.toString();
                code = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                        .replaceAll("THEYEARCHANGE", String.valueOf(year))
                        .replace("THEDAYCHANGE", String.valueOf(day));
                apex.runApex(resolveStrNameSpace(code));
                month = month - 1; //Need to move backward for executing the aggregation.
            }
            reader.close();
            Thread.sleep(30000L);
            for (int i = 0; i < 200; i++) {
                String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                        "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                        "and ApexClass.Name = 'AdoptionAggregation'";
                int noOfRunningJobs = getQueryRecordCount(query);
                if (noOfRunningJobs == 0) {
                    Report.logInfo("Aggregate Jobs are finished.");
                    isAggBatchsCompleted = true;
                    break;
                } else {
                    Report.logInfo("Waiting");
                    Thread.sleep(30000L);
                }
            }
        }
    }

    @Test
    public void Ins_MonthlyInsData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("1 Month");
        String[] monthAndYear = setMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.setDataGranularity("Instance");
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC|BOESDORFER & BOESDORFER INC - Instance 3|19|1,699|795|9,164|1,713|1,010|9,758|6,153|3,885"));
        Assert.assertTrue(usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC|BOESDORFER & BOESDORFER INC - Instance 2|80|1,419|458|5,089|2,070|787|6,242|4,926|2,862"));
        Assert.assertTrue(usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC|BOESDORFER & BOESDORFER INC - Instance 1|122|1,865|619|6,711|1,679|789|5,449|2,291|8,863"));
    }


    @Test
    public void Ins_MonthlyAccData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("1 Month");
        String[] monthAndYear = setMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.setDataGranularity("Account");
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid("Addis Housewares Ltd|295|1,491|3|21,630|1,923|1,011|6,629|3|19,862"));
        Assert.assertTrue(usage.isDataPresentInGrid("AGENCE PRESSE|405|1,789|3|13,491|1,868|843|2,971|3|13,982"));
    }



    @Test
    public void Ins_MonthlyFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isMonthlyFormEleDisplayed(), "Checking if Monthly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test
    public void Ins_MonthlyAccExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("12 Months");
        String[] monthAndYear = setMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.setDataGranularity("Account");
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertTrue(usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertTrue(usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Ins_MonthlyInsExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("6 Months");
        String[] monthAndYear = setMonthAndYear(-1);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.setDataGranularity("Instance");
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertTrue(usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertTrue(usage.exportGrid(), "Checking grid export.");
    }

    //This can be decoupled in to 2.
    @Test
    public void Ins_MonthlyAccAndInsUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("DeeTag USA");
        analyticsPage.setForTimeMonthPeriod("6 Months");
        String[] monthAndYear = setMonthAndYear(0);
        analyticsPage.setMonth(monthMap.get(monthAndYear[0]));
        analyticsPage.setYear(String.valueOf(monthAndYear[1]));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDisplayed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Active Users||115.67|287|209.33|194|252.67"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("DB Size||1,588|1,259|1,945|1,637|1,960"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Emails Sent Count||3|3|3|3|3"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded||16,362|26,184|14,585|16,098|17,938"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Leads||1,917|1,697|1,895|1,658|1,545"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Campaigns||867|998|762|842|1,003"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run||4,358.33|6,955|5,851|4,699|7,171.33"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views||3|3|3|3|3"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits||11,948|10,076|13,180|8,661|20,707"));
        analyticsPage.setInstance("DeeTag USA - Instance 1");
        analyticsPage.setForTimeMonthPeriod("12 Months");
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDisplayed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Active Users|313|490|16|333|39|103|357|219|468|455|392|126"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("DB Size|1,020|1,828|1,994|1,575|1,513|1,133|1,097|1,070|1,417|1,945|1,637|1,136"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Emails Sent Count|445|398|492|311|416|411|732|379|762|584|547|324"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded|2,617|6,066|2,624|7,469|3,819|6,520|8,520|4,658|9,762|6,941|2,125|86"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Leads|2,222|2,116|2,051|2,072|1,931|2,291|1,617|1,917|2,212|1,975|1,995|2,349"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Campaigns|600|682|954|872|743|652|1,056|867|998|762|748|1,003"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run|1,962|8,877|216|4,144|1,696|3,031|7,142|2,401|4,364|3,698|8,994|4,730"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views|8,394|9,071|5,477|9,707|2,104|2,779|7,404|9,827|7,590|2,731|8,771|5,556"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits|5,603|3,012|8,514|1,550|5,900|9,628|5,343|8,534|22|3,159|2,757|4,054"));
    }


    @Test
    public void Ins_MonthlyAccUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setForTimeMonthPeriod("6 Months");
        String[] monthAndYear = setMonthAndYear(0);
        analyticsPage.setMonth(monthMap.get(monthAndYear[0]));
        analyticsPage.setYear(String.valueOf(monthAndYear[1]));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());

    }

    @Test
    public void Ins_MonthlyAccUsageGraphMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setForTimeMonthPeriod("6 Months");
        String[] monthAndYear = setMonthAndYear(+2);
        analyticsPage.setMonth(monthMap.get(monthAndYear[0]));
        analyticsPage.setYear(String.valueOf(monthAndYear[1]));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some"));
    }

    @Test
    public void Ins_MonthlyInsUsageGraphMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Cadbury Beverages Div Cadbury");
        analyticsPage.setForTimeMonthPeriod("24 Months");
        String[] monthAndYear = setMonthAndYear(+2);
        analyticsPage.setMonth(monthMap.get(monthAndYear[0]));
        analyticsPage.setYear(String.valueOf(monthAndYear[1]));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some"));
        analyticsPage.setInstance("Cadbury Beverages Div Cadbury - Instance 1");
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some"));
        Assert.assertTrue(analyticsPage.isGridDisplayed());
    }

    @AfterClass
    public void tearDown() {
        Report.logInfo("Adoption_Account_Monthly_Test  End Time : " + c.getTime());
        basepage.logout();
    }
}