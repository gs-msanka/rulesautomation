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

import java.io.IOException;
import java.util.TimeZone;

public class Adoption_Account_Monthly_Test extends BaseTest {

    ObjectMapper mapper = new ObjectMapper();
    String resDir = userDir + "/resources/datagen/";
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    String CUST_SET_DELETE = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";

    @BeforeClass
    public void setUp() throws IOException {
        basepage.login();
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
        String measureFile          = env.basedir + "/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir + "/testdata/sfdc/UsageData/Scripts/Account_Level_Monthly.txt";

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
        jobInfo3 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Account_Monthly.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);

    }

    @Test
    public void Acc_MonthlyAllMeas1() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("1 Month");
        String[] monthAndYear = getMonthAndYear(-1);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.selectUIView("Standard View");
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("AGENCE PRESSE|90|1,612|623|17,052|2,019|1,095|8,790|12,326 | 13,248"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("A and T unlimit Limited|308|1,947|329|5,292|2,388|636|14,191|13,479|14,309"));
    }

    @Test
    public void Acc_MonthlyAllMeas2() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("1 Month");
        Report.logInfo(String.valueOf(monthMap.size()));
        for(String val : monthMap.values()) {
            Report.logInfo(val);
        }
        String[] monthAndYear = getMonthAndYear(-2);
        Report.logInfo(monthMap.get(monthAndYear[0])+ " --- " + String.valueOf(monthAndYear[1]));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage.selectUIView("Standard View");
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("CARNES KM SA DE CV|285|1,176|340|7,615|2,023|824|16,828|15,950|8,57"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("DAKTEL COMUNICACIONES SA DE CV|186|1,499|389|14,852|1,742|1,050|10,819|14,860|13,388"));
    }

    @Test
    public void Acc_MonthlyForCustomer() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName("DALE VALOR A MEXICO SC");
        usage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setForTimeMonthPeriod("12 Months");
        String[] monthAndYear = getMonthAndYear(-1);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid("Active Users|398|432|71|105|392|477|231|107|200|338|19|190"));
        Assert.assertTrue(usage.isDataPresentInGrid("DB Size|1,541|1,771|1,481|1,854|1,819|1,640|1,053|1,556|1,294|1,136|1,046|1,766"));
        Assert.assertTrue(usage.isDataPresentInGrid("Emails Sent Count|779|650|681|639|658|561|761|613|403|618|507|655"));
        Assert.assertTrue(usage.isDataPresentInGrid("Files Downloaded|8,162|13,897|773|15,287|10,512|18,363|7,644|4,784|3,734|14,972|8,380|4,359"));
        Assert.assertTrue(usage.isDataPresentInGrid("Leads|2,452|2,396|1,553|1,735|1,723|2,204|1,641|2,411|2,357|1,884|1,611|1,562"));
        Assert.assertTrue(usage.isDataPresentInGrid("No of Campaigns|687|917|942|619|688|875|916|1,078|1,010|1,009|620|911"));
        Assert.assertTrue(usage.isDataPresentInGrid("No of Report Run|5,933|10,619|4,371|7,125|263|16,577|14,204|11,882|14,724|510|16,783|16,783"));
        Assert.assertTrue(usage.isDataPresentInGrid("Page Views|4,755|15,885|4,132|13,415|17,894|2,854|13,565|9,077|11,508|1,777|11,600|16,775"));
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits|9,649|9,757|15,121|417|16,888|14,444|6,138|11,256|4,580|17,510|13,350|5,107"));
    }

    @Test
    public void Acc_MonthlyForCustomerPartialData() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName("DAKTEL COMUNICACIONES SA DE CV");
        usage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setForTimeMonthPeriod("12 Months");
        String[] monthAndYear = getMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits | 2,593 | 490 | 2,277 | 9,651 | 13,360 | 3,260 | 9,567 | 17,603 | 13,388 | 18,052"), "Checking the adoption grid displayed below the graph(Page Visits).");
    }

    @Test
    public void Acc_MonthlyForCustomerNoData() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName("ARGO ALMACENADORA SA DE CV");
        usage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setForTimeMonthPeriod("12 Months");
        String[] monthAndYear = getMonthAndYear(0);
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isMissingDataInfoDisplayed("Missing data for some"));
    }


    @Test
    public void Acc_MonthlyExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfMonths("12 Months");
        String[] monthAndYear = getMonthAndYear(-1);
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