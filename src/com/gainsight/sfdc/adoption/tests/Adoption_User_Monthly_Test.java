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

public class Adoption_User_Monthly_Test extends BaseTest {
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
        String advUsageConfigFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/User_Level_Monthly.txt";

        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
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
        jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_User_Monthly.txt"), JobInfo.class);
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
    public void Usr_MonthlyAllMeasure1() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("1 Month");
        String[] monthAndYear = setMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.selectUIView("Standard View");
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("AGENCE PRESSE|220|1,418|10|33,144|1,545|1,089|2,916|10|34,285"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("COMERCIALIZADORA RIMATOM SA de CV|356|1,517|10|37,956|1,645|1,057|2,744|10|29,692"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC|334|1,528|10|22,367|1,504|1,046|2,727|10|29,739"));
    }

    @Test
    public void Usr_MonthlyAllMeasure2() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("1 Month ");
        String[] monthAndYear = setMonthAndYear(-1);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.selectUIView("Standard View");
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("A and T unlimit Limited|302|1,639|10|33,569|1,761|1,069|3,162|10|19,619"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Baja Inc|247|1,654|10|29,344|1,539|1,078|3,305|10|28,623"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Cadbury Beverages Div Cadbury|340|1,626|10|33,402|1,515|1,086|3,625|10|31,983"));
    }

    @Test
    public void Usr_MonthlyForCustomer() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName("DALE VALOR A MEXICO SC");
        usage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setForTimeMonthPeriod("6 Months");
        String[] monthAndYear = setMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDrillDownMsgDisplayed("Click on a data point in the graph above to view detailed data"));
        Assert.assertTrue(usage.isDataPresentInGrid("Active Users|310.2|251.5|244.6|275.8|305|302.7"));
        Assert.assertTrue(usage.isDataPresentInGrid("DB Size|1,508|1,246.5|1,541|1,450.5|1,475|1,475"));
        Assert.assertTrue(usage.isDataPresentInGrid("Emails Sent Count|10|10|10|10|10|10"));
        Assert.assertTrue(usage.isDataPresentInGrid("Files Downloaded|32,532|33,790|27,550|22,874|30,540|34,753"));
        Assert.assertTrue(usage.isDataPresentInGrid("Leads|1,549|1,533|1,610|1,567|1,554|1,564"));
        Assert.assertTrue(usage.isDataPresentInGrid("No of Campaigns|1,090|1,091|1,100|1,084|1,016|1,047"));
        Assert.assertTrue(usage.isDataPresentInGrid("No of Report Run|2,863.2|3,433.7|3,420.8|2,974.6|3,289.9|2,954.3"));
        Assert.assertTrue(usage.isDataPresentInGrid("Page Views|10|10|10|10|10|10"));
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits|28,622|33,812|28,806|35,895|27,000|25,145"));
    }

    @Test
    public void Usr_MonthlyForCustomerPartialData() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName("DAKTEL COMUNICACIONES SA DE CV");
        usage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setForTimeMonthPeriod("6 Months");
        String[] monthAndYear = setMonthAndYear(2);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDrillDownMsgDisplayed("Click on a data point in the graph above to view detailed data"));
        Assert.assertTrue(usage.isMissingDataInfoDisplayed("Missing data for some months."));
        Assert.assertTrue(usage.isDataPresentInGrid("Active Users|202.8|284.4|244.8|297.7"));
        Assert.assertTrue(usage.isDataPresentInGrid("DB Size|1,521|1,562|1,639|1,324.5"));
        Assert.assertTrue(usage.isDataPresentInGrid("Emails Sent Count|10|10|10|10|"));
        Assert.assertTrue(usage.isDataPresentInGrid("Files Downloaded|34,114|34,977|31,752|22,780"));
        Assert.assertTrue(usage.isDataPresentInGrid("Leads|1,557|1,640|1,675|1,840"));
        Assert.assertTrue(usage.isDataPresentInGrid("No of Campaigns|1,034|1,032|1,094|987|"));
        Assert.assertTrue(usage.isDataPresentInGrid("No of Report Run|3,116.7|3,557.1|3,722.3|3,187.3"));
        Assert.assertTrue(usage.isDataPresentInGrid("Page Views|10|10|10|10"));
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits|20,702|25,941|30,636|29,140|"));

    }

    @Test
    public void Usr_MonthlyCustomerNoData() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName("ARGO ALMACENADORA SA DE CV");
        usage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setForTimeMonthPeriod("12 Months");
        String[] monthAndYear = setMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isMissingDataInfoDisplayed("Missing data for some months."));
        Assert.assertTrue(usage.isDrillDownMsgDisplayed("Click on a data point in the graph above to view detailed data"));
    }

    @Test
    public void Usr_MonthlyExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("1 Month");
        String[] monthAndYear = setMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.exportGrid());
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}