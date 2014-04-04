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
    String resDir = userDir+"/resources/datagen/";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";
    String QUERY = "DELETE [SELECT ID FROM JBCXM__StatePreservation__c Where Name = 'Adoption'];";
    String CUST_SET_DELETE = "JBCXM__JbaraRestAPI.deleteActivityLogInfoRecord('DataLoadUsage');";


    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/User_Level_Weekly.txt";

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        apex.runApex(resolveStrNameSpace(QUERY));
        //apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));
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
        jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_User_Weekly.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);

        BufferedReader reader;
        String fileName = env.basedir+"/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
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
        for(int k = 0; k< 10;k++) {
            for(int m=0; m < 5; m++, i=i-7) {
                //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                dateStr     = getWeekLabelDate("Wed", i, false, false);
                System.out.println(dateStr);
                year        = (dateStr != null && dateStr.split("\\|").length > 0) ? Integer.valueOf(dateStr.split("\\|")[0]) : c.get(Calendar.YEAR);
                month       = (dateStr != null && dateStr.split("\\|").length > 1) ? Integer.valueOf(dateStr.split("\\|")[1]) : c.get(Calendar.MONTH);
                day         = (dateStr != null && dateStr.split("\\|").length > 2) ? Integer.valueOf(dateStr.split("\\|")[2]) : c.get(Calendar.DATE);
                code        = stringBuilder.toString();
                code        = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                        .replaceAll("THEYEARCHANGE", String.valueOf(year))
                        .replace("THEDAYCHANGE", String.valueOf(day));

                apex.runApex(resolveStrNameSpace(code));
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
    }

    @Test
    public void Adoption_User_Weekly_ViewWeeklyAccDataSUM() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDate(getWeekLabelDate("Wed", -7, false, true));
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        usage.clearGirdFilter();
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("AGENCE PRESSE | 26,172 | 25,949 | 23,208 | 27,226 | 31,385 | 30,238 | 25,315 | 20,589 | 24,967 | 21,709 | 21,170"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("IRISA ADHESIVES CORPORATION SA DE CV | 28,173 | 28,896 | 16,495 | 28,503 | 29,397 | 25,042 | 21,669 | 35,760 | 22,122 | 24,849 | 25,979"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Tioga Hardwoods Inc | 22,057 | 20,889 | 22,397 | 30,660 | 20,618 | 30,400 | 22,491 | 22,429 | 22,467 | 21,924 | 24,736"));
    }

    @Test
    public void Adoption_User_Weekly_ViewWeeklyAccDataAVG() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        usage.setNoOfWeeks("9 Weeks");
        usage.setDate(getWeekLabelDate("Wed", -7, false, true));
        usage = usage.displayWeeklyUsageData();
        usage = usage.selectUIView("Standard View");
        usage.clearGirdFilter();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("ATTEBURY GRAIN | 2,816.9 | 2,124 | 2,645.5 | 2,390.2 | 1,961.4 | 2,206.1 | 2,121.1 | 1,978.3"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC | 3,118.6 | 1,933.7 | 2,283.8 | 2,203 | 2,276.2 | 2,833.6 | 2,537.5 | 1,998.5"));
    }

    @Test
    public void Adoption_User_Weekly_ViewWeeklyAccDataCount() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("6 Weeks");
        usage.setDate(getWeekLabelDate("Wed", -7, false, true));
        usage = usage.displayWeeklyUsageData();
        usage = usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        usage.clearGirdFilter();
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Anunay Fab Ltd | 10 | 10 | 10 | 10 | 10"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("COMERGALV SA DE CV | 10 | 10 | 10 | 10 | 10"));
        
    }

    @Test
    public void Adoption_User_Weekly_TestWeeklyAdoptionSelectionFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if weekly form is displayed");
    }

    @Test
    public void Adoption_User_Weekly_TestAdoptionGridExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDate(getWeekLabelDate("Wed", -7, false, true));
        usage = usage.displayWeeklyUsageData();
        usage.clearGirdFilter();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertEquals(true, usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Adoption_User_Weekly_ViewAccountLevelUsageInGridAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        usage.setNoOfWeeks("3 Weeks");
        usage.setDate(getWeekLabelDate("Wed", -7, false, true));
        usage = usage.displayWeeklyUsageData();
        usage.clearGirdFilter();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Anunay Fab Ltd | 2,208.7 | 2,216.1 | 2,005.5"));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("Anunay Fab Ltd", null);
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking if grid is displayed");
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking if adoption chart is displayed");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 26,017 | 26,387 | 21,366 | 29,567 | 19,593 | 27,756 | 19,649 | 21,517 | 30,200 | 28,528 | 21,483 | 27,463 | 25,986 | 25,683 | 22,381 | 27,715 | 24,864 | 18,555 | 22,886 | 25,323 | 27,291 | 24,890 | 25,839 | 24,976 | 29,966 | 20,922 | 28,054 | 23,329 | 23,877 | 23,166 | 19,938 | 28,791 | 26,049 | 30,406 | 17,526 | 15,217 | 26,570 | 24,535 | 24,788 | 23,592 | 23,731 | 22,698 | 27,700 | 21,663 | 29,094 | 23,585 | 20,568"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 2,434.2 | 1,874.6 | 2,111.8 | 2,106.6 | 2,188.4 | 2,350 | 2,563.6 | 2,482.5 | 2,914.9 | 1,820.3 | 2,217.1 | 2,760.5 | 2,732.4 | 2,408.9 | 2,786.7 | 2,137.4 | 3,466.2 | 2,905.8 | 1,832.8 | 2,827.1 | 2,805.8 | 1,700.8 | 3,175.8 | 2,800.7 | 1,932.2 | 2,876.6 | 1,878.5 | 2,310.3 | 2,751.2 | 2,549.3 | 2,140.2 | 1,861.1 | 2,751.1 | 2,260.6 | 3,309.7 | 2,445.3 | 3,098.2 | 2,734.7 | 2,701.8 | 1,962.2 | 2,283.1 | 2,743.3 | 2,313.4 | 2,020 | 2,208.7 | 2,216.1 | 2,005.5"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits | 25,092 | 18,511 | 30,787 | 23,606 | 31,931 | 26,868 | 18,272 | 21,662 | 23,101 | 28,171 | 23,264 | 24,337 | 19,345 | 16,362 | 27,753 | 22,089 | 25,532 | 17,956 | 31,338 | 17,940 | 27,286 | 15,901 | 27,724 | 34,441 | 14,634 | 23,957 | 21,645 | 27,743 | 28,518 | 28,686 | 18,469 | 29,514 | 19,259 | 27,151 | 19,189 | 26,659 | 29,845 | 23,982 | 16,955 | 21,526 | 23,343 | 13,833 | 32,996 | 26,144 | 27,595 | 29,127 | 22,019"));
    }

    @Test
    public void Adoption_User_Weekly_ViewAccountLevelUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", -7, false, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded |  28,602 | 31,140 | 28,858 | 22,942 | 30,287 | 26,364 | 9,702 | 28,979 | 24,906 | 24,490 | 33,690 | 24,702 | 20,314 | 25,199 | 17,518 | 30,004 | 16,058 | 18,850 | 29,392 | 24,336 | 27,704 | 23,548 | 24,596 | 23,534 | 21,130 | 28,241 | 24,277 | 27,945 | 25,595 | 16,332 | 17,594 | 19,155 | 23,586 | 26,739 | 29,785 | 24,797 | 19,073 | 20,709 | 14,200 | 27,924 | 36,662 | 26,034 | 28,815 | 28,182 | 22,480"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 3,274.2 | 2,547.5 | 2,820 | 2,739.6 | 1,874.9 | 2,565.1 | 3,703 | 2,788.9 | 1,886.8 | 2,656.1 | 2,965.5 | 3,122.4 | 2,863.5 | 2,428.8 | 1,651.8 | 2,828.5 | 2,805.6 | 1,852.8 | 2,385 | 2,973.2 | 2,328.8 | 3,305.6 | 2,917 | 3,031.6 | 2,254 | 2,595 | 2,245.4 | 3,139.3 | 2,196.7 | 2,458.9 | 3,419.5 | 3,033.6 | 1,982.5 | 2,687.2 | 2,641.1 | 2,767.6 | 2,639.3 | 2,417 | 2,620.1 | 2,118.3 | 2,852.9 | 3,024 | 2,646.8 | 2,986.2 | 2,605.3 | 3,032.2"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits| 26,626 | 24,753 | 32,810 | 35,859 | 24,573 | 23,014 | 28,598 | 31,578 | 19,129 | 24,379 | 26,067 | 25,330 | 23,456 | 24,445 | 22,693 | 28,279 | 22,463 | 27,505 | 28,467 | 26,058 | 24,720 | 31,802 | 15,476 | 26,776 | 30,105 | 29,242 | 28,735 | 24,456 | 23,019 | 25,619 | 36,538 | 25,779 | 24,374 | 22,958 | 26,573 | 24,524 | 19,283 | 25,051 | 27,345 | 22,413 | 34,008 | 27,902 | 23,641 | 22,907"));
    }

    @Test
    public void Adoption_User_Weekly_ViewAccountLevelUsageGraphWithMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", 28, false, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks."));
    }

    @Test
    public void Adoption_User_Weekly_NoAdoptionDataFoundMsgVerification() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", -7, false, true));
        analyticsPage.setCustomerName("ABASTECEDORA DE VALVULAS INTRUMENT");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isNoAdoptionDataMsgDisplayed());
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }

}