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
import java.util.Calendar;

public class Adoption_User_Monthly_Test extends BaseTest {
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    String resDir = userDir+"/resources/datagen/";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";
    private int month;
    private int year;
    String QUERY = "DELETE [SELECT ID FROM JBCXM__StatePreservation__c Where Name = 'Adoption'];";



    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/User_Level_Monthly.txt";
        try{
            //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
            apex.runApex(resolveStrNameSpace(QUERY));
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
            jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_User_Monthly.txt"), JobInfo.class);
            dataLoader.execute(jobInfo3);

            String fileName = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
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
            for(int k=0;k<5;k++) {
                for(int i =0; i < 5; i++) {
                    if(month == 0) {
                        month = 12;
                        year = year -1;
                    }
                    code = stringBuilder.toString();
                    code = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                            .replaceAll("THEYEARCHANGE", String.valueOf(year))
                            .replace("THEDAYCHANGE", String.valueOf(day));
                    apex.runApex(code);
                    month = month-1; //Need to move backward for executing the aggregation.
                }
                reader.close();
                Thread.sleep(30000L);
                for(int i= 0; i < 200; i++) {
                    String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                            "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                            "and ApexClass.Name = 'AdoptionAggregation'";
                    int noOfRunningJobs = getQueryRecordCount(query);
                    if(noOfRunningJobs==0) {
                        Report.logInfo("Aggregate Jobs are finished.");
                        isAggBatchsCompleted = true;
                        break;
                    } else {
                        Report.logInfo("Waiting");
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
    public void Adoption_User_Monthly_UsageDataVerificationSum() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("AGENCE PRESSE | 31,016 | 29,953 | 26,452 | 33,155 | 44,518 | 37,036 | 38,272 | 30,545 | 26,845 | 41,687 | 33,144"));
    }

    @Test
    public void Adoption_User_Monthly_UsageDataVerificationAvg() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("TOMAS MARTINEZ PATLAN | 2,889 | 2,291.6 | 2,470 | 3,948.6 | 2,944 | 2,853.2 | 2,787.4 | 2,565.4 | 3,055.1 | 3,465.8 | 3,091.5"));
    }

    @Test
    public void Adoption_User_Monthly_UsageDataVerificationCount() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("PRODUCCIONES AGRICOLA DE TABASCO SA CV | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10"));
    }


    @Test
    public void Adoption_User_Monthly_UsageDataVerificationInGirdAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        setMonthAndYear(2);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("PROD Y DIS VER FRESCAS GUIJOSA SC RL CV | 28,855 | 28,510 | 34,614 | 29,865 | 33,386 | 24,705 | 20,189 | 31,537 | 29,690"));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("PROD Y DIS VER FRESCAS GUIJOSA SC RL CV", null);
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking adoption graph is displayed");
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking grid is displayed");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded  | 33,098 | 30,167 | 28,855 | 28,510 | 34,614 | 29,865 | 33,386 | 24,705 | 20,189 | 31,537 | 29,690"), "Checking Files Downloaded measure");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 3,481.8 | 3,268.9 | 3,184.4 | 3,443.5 | 2,339.2 | 4,108.9 | 2,651.1 | 3,490.1 | 2,902.8 | 3,518.7 | 3,780.7"), "Checking No of Report Run measure");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views  | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10"), "Checking Page Views measure");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits  | 31,398 | 26,094 | 32,895 | 33,841 | 36,179 | 24,606 | 21,731 | 27,974 | 21,303 | 17,996 | 24,286"), "Checking Page Visits measure");

    }

    @Test
    public void Adoption_User_Monthly_ViewUsageForCustomer() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage.setCustomerName("JUAN ANGEL FLORES AGUIRRE");
        usage.setMeasureNames("All Measures");
        usage.setForTimeMonthPeriod("6 Months");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage  = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isGridDispalyed(), "Checking the adoption grid is displayed.");
        Assert.assertTrue(usage.isDataPresentInGrid("Files Downloaded  | 26,669 | 32,966 | 22,179 | 26,908 | 32,334"), "Checking the adoption grid displayed below the graph(Files Downloaded).");
        Assert.assertTrue(usage.isDataPresentInGrid("No of Report Run  | 3,994.6 | 2,849.5 | 3,625 | 3,720.7 | 4,040.8"), "Checking the adoption grid displayed below the graph(No of Report Run).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Views  | 10 | 10 | 10 | 10 | 10"), "Checking the adoption grid displayed below the graph(Page Views).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits  | 24,561 | 27,229 | 26,234 | 33,826 | 23,799\n"), "Checking the adoption grid displayed below the graph(Page Visits).");
    }


    @Test
    public void Adoption_User_Monthly_ViewPartialUsageForCustomer() {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        usage.setCustomerName("CRIBAS Y ARRENDAMIENTOS SA DE CV");
        usage.setMeasureNames("All Measures");
        usage.setForTimeMonthPeriod("18 Months");
        setMonthAndYear(1);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage  = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isGridDispalyed(), "Checking the adoption grid is displayed.");
        Assert.assertTrue(usage.isMissingDataInfoDisplayed("Missing data for some months."), "Checking the missing data info message is displayed");
        Assert.assertTrue(usage.isDataPresentInGrid("Files Downloaded  | 41,300 | 32,545 | 31,705 | 28,718 | 33,373 | 20,211 | 27,773 | 29,329 | 32,262 | 34,078 | 34,355 | 34,115 | 25,939 | 24,249 | 22,101 | 27,047 | "), "Checking the adoption grid displayed below the graph(Files Downloaded).");
        Assert.assertTrue(usage.isDataPresentInGrid("No of Report Run  | 3,153.8 | 2,245.2 | 2,375.2 | 3,686.1 | 3,437.1 | 3,125.3 | 3,079.2 | 3,179.2 | 2,149.3 | 3,377.9 | 3,141 | 3,167.7 | 3,185.6 | 2,966.6 | 3,225.7 | 1,922.2"), "Checking the adoption grid displayed below the graph(No of Report Run).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Views  | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10"), "Checking the adoption grid displayed below the graph(Page Views).");
        Assert.assertTrue(usage.isDataPresentInGrid("Page Visits | 33,648 | 28,934 | 28,970 | 37,752 | 35,012 | 26,814 | 41,918 | 33,119 | 24,256 | 42,444 | 32,677 | 26,606 | 29,274 | 36,111 | 28,615 | 23,643"), "Checking the adoption grid displayed below the graph(Page Visits).");
    }

    @Test
    public void Adoption_User_Monthly_ViewUsageForCustomerNoData() {
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
        int currentMonth = Integer.valueOf(c.get(Calendar.MONTH))+1;
        int currentYear = Integer.valueOf(c.get(Calendar.YEAR));
        if(numOfMonthsToAdd == 0) {
            month = currentMonth;
            year = currentYear;
        } else if(numOfMonthsToAdd > 0) {
            if((currentMonth+numOfMonthsToAdd) > 12) {
                month = currentMonth+numOfMonthsToAdd-12;
                year = currentYear+1;
            } else {
                month = currentMonth+numOfMonthsToAdd;
                year = currentYear;
            }
        } else if(numOfMonthsToAdd < 0) {
            if(currentMonth+numOfMonthsToAdd <=0 ) {
                month = 12 + currentMonth+numOfMonthsToAdd;
                year = currentYear-1;
            } else if(currentMonth+numOfMonthsToAdd >0) {
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