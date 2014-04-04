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

import java.io.IOException;
import java.util.Calendar;

public class Adoption_Account_Monthly_Test extends BaseTest {

    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;

    ObjectMapper mapper = new ObjectMapper();

    String resDir = userDir+"/resources/datagen/";
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";
    private int month;
    private int year;
    String QUERY = "DELETE [SELECT ID FROM JBCXM__StatePreservation__c Where Name = 'Adoption'];";
    String CUST_SET_DELETE = "JBCXM__JbaraRestAPI.deleteActivityLogInfoRecord('DataLoadUsage');";

    @BeforeClass
    public void setUp() throws IOException {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/Account_Level_Monthly.txt";

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        apex.runApex(resolveStrNameSpace(QUERY));
       // apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(measureFile, isPackageInstance());
        apex.runApexCodeFromFile(advUsageConfigFile,isPackageInstance());
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
    public void adoption_Account_Monthly_UsageDataVerification1() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage.clearGirdFilter();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("AGENCE PRESSE | 15,020 | 12,749 | 2,868 | 8,603 |" +
                " 2,039 | 7,747 | 16,639 | 16,763 | 152 | 10,013 | 5,771 | 12,326"));
    }

    @Test
    public void adoption_Account_Monthly_UsageDataVerification2() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage.clearGirdFilter();
        usage.selectUIView("Standard View");
        usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("A and T unlimit Limited | 17,213 | 15,523 | 16,138 | 9,694 | 18,201 | 5,007 | 3,168 | 8,451 | 10,274 | 9,432 | 14,191 "));
    }


    @Test
    public void adoption_Account_Monthly_UsageDataVerificationInGirdAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage.clearGirdFilter();
        usage.selectUIView("Standard View");
        usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("IRISA ADHESIVES CORPORATION SA DE CV | 7,918 | 4,928 | 7,465 | 3,161 | 18,685 | 3,807 | 13,837 | 10,014 | 14,345 | 3,187 | 1,746 | 10,415"));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("IRISA ADHESIVES CORPORATION SA DE CV", null);
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking adoption graph is displayed");
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking grid is displayed");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 4,928 | 7,465 | 3,161 | 18,685 | 3,807 | 13,837 | 10,014 | 14,345 | 3,187 | 1,746 | 10,415"), "Checking Files Downloaded measure");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 16,292 | 10,292 | 6,509 | 14,256 | 532 | 6,101 | 6,749 | 286 | 18,785 | 6,703 | 14,140"), "Checking No of Report Run measure");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 5,382 | 9,071 | 9,657 | 14,532 | 10,825 | 18,729 | 16,560 | 15,006 | 16,651 | 6,096 | 15,236"), "Checking Page Views measure");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits | 11,991 | 8,442 | 11,895 | 16,706 | 7,995 | 6,783 | 365 | 17,703 | 8,039 | 3,281 | 5,798"), "Checking Page Visits measure");

    }

    @Test
    public void adoption_Account_Monthly_ViewUsageForCustomer() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage.setCustomerName("Anunay Fab Ltd");
        usage.setMeasureNames("All Measures");
        usage.setForTimeMonthPeriod("12 Months");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage  = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isGridDispalyed(), "Checking the adoption grid is displayed.");
        Assert.assertTrue(usage.isDataPresentInGrid("Files Downloaded | 18,382 | 3,056 | 7,429 | 9,768 | 3,984 | 11,868 | 6,039 | 12,644 | 9,540 | 7,087 | 9,794"), "Checking the adoption grid displayed below the graph(Files Downloaded).");
        Assert.assertTrue(usage.isDataPresentInGrid("No of Report Run  | 2,235 | 3,545 | 14,451 | 12,592 | 14,436 | 1,091 | 1,290 | 13,221 | 1,217 | 4,672 | 9,813"), "Checking the adoption grid displayed below the graph(No of Report Run).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Views | 15,041 | 2,640 | 16,799 | 8,223 | 3,415 | 18,570 | 13,598 | 395 | 2,193 | 7,445 | 9,421"), "Checking the adoption grid displayed below the graph(Page Views).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits | 12,487 | 3,520 | 729 | 8,756 | 11,505 | 18,441 | 6,710 | 853 | 14,468 | 13,664 | 915"), "Checking the adoption grid displayed below the graph(Page Visits).");
    }


    @Test
    public void adoption_Account_Monthly_ViewPartialUsageForCustomer() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage.setCustomerName("DAKTEL COMUNICACIONES SA DE CV");
        usage.setMeasureNames("All Measures");
        usage.setForTimeMonthPeriod("12 Months");
        setMonthAndYear(1);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage  = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isGridDispalyed(), "Checking the adoption grid is displayed.");
        Assert.assertTrue(usage.isMissingDataInfoDisplayed("Missing data for some months."), "Checking the missing data info message is displayed");
        Assert.assertTrue(usage.isDataPresentInGrid("Files Downloaded | 8,369 | 8,432 | 17,941 | 11,288 | 650 | 8,007 | 1,013 | 14,158 | 14,852 | 8,515"), "Checking the adoption grid displayed below the graph(Files Downloaded).");
        Assert.assertTrue(usage.isDataPresentInGrid("No of Report Run  | 4,666 | 2,076 | 13,933 | 99 | 10,374 | 2,529 | 5,032 | 4,104 | 10,819 | 11,004"), "Checking the adoption grid displayed below the graph(No of Report Run).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Views | 10,834 | 7,322 | 1,898 | 4,179 | 1,736 | 17,472 | 9,019 | 10,077 | 14,860 | 2,914"), "Checking the adoption grid displayed below the graph(Page Views).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits | 2,593 | 490 | 2,277 | 9,651 | 13,360 | 3,260 | 9,567 | 17,603 | 13,388 | 18,052"), "Checking the adoption grid displayed below the graph(Page Visits).");
    }

    @Test
    public void adoption_Account_Monthly_ViewUsageForCustomerNoData() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage.setCustomerName("ARGO ALMACENADORA SA DE CV");
        usage.setMeasureNames("All Measures");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage  = usage.displayCustMonthlyData();
        Assert.assertFalse(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertFalse(usage.isGridDispalyed(), "Checking the adoption grid is displayed.");
        Assert.assertTrue(usage.isNoAdoptionDataMsgDisplayed());
    }

    public void setMonthAndYear(int numOfMonthsToAdd) {
        int currentMonth = Integer.valueOf(c.get(Calendar.MONTH));
        int currentYear = Integer.valueOf(c.get(Calendar.YEAR));
        if(numOfMonthsToAdd == 0) {
            if(currentMonth==0) {
                month = 12;
                year = currentYear-1;
            } else {
                month =currentMonth;
                year =currentYear;
            }
        } else {
            if((currentMonth+numOfMonthsToAdd)-11 > 0) {
                month = currentMonth+numOfMonthsToAdd-11;
                year = currentYear+1;
            } else {
                month = currentMonth+numOfMonthsToAdd;
                year = currentYear;
            }
        }
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}