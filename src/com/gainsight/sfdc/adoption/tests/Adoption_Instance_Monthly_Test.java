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

public class Adoption_Instance_Monthly_Test extends BaseTest {
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    static String resDir = "./resources/datagen/";
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
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/Instance_Level_Monthly.txt";

        try{
            //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
            apex.runApex(resolveStrNameSpace(QUERY));
            createExtIdFieldOnAccount();
            createFieldsOnUsageData();
            DataETL dataLoader = new DataETL();
            apex.runApexCodeFromFile(measureFile, isPackageInstance());
            apex.runApexCodeFromFile(advUsageConfigFile,isPackageInstance());
            dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
            dataLoader.cleanUp(resolveStrNameSpace(CUSTOMER_INFO), null);
            jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
            dataLoader.execute(jobInfo1);
            jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
            dataLoader.execute(jobInfo2);
            jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Instance_Monthly.txt"), JobInfo.class);
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
                    apex.runApex(resolveStrNameSpace(code));
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
    public void Adoption_Instance_Monthly_ViewMonthlyInsData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage.setDataGranularity("By Instance");
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed(), "Verifying Adoption grid is displayed");
        Assert.assertTrue(usage.isGridHeaderMapped("Customer | Instance | Renewal Date"), "Verifying Grid Headers");
        Assert.assertTrue(usage.isDataPresentInGrid("AUREA SOFTWARE INC | AUREA SOFTWARE INC - Instance 1 | 2,544 | 5,274 | 1,199 | 5,799 | 4,013 | 1,712 | 8,866 | 1,224 | 3,315 | 818") , "Verifying Account Instance Level data");
        Assert.assertTrue(usage.isDataPresentInGrid("AUREA SOFTWARE INC | AUREA SOFTWARE INC - Instance 2 | 971 | 819 | 5,360 | 3,469 | 7,837 | 3,381 | 1,790 | 6,207 | 4,266 | 1,295"), "Verifying Account Instance Level data");
        Assert.assertTrue(usage.isDataPresentInGrid("AUREA SOFTWARE INC | AUREA SOFTWARE INC - Instance 3 | 5,038 | 5,229 | 4,930 | 956 | 4,315 | 7,593 | 6,615 | 901 | 3,196 | 9,419"), "Verifying Account Instance Level data");
    }


    @Test
    public void Adoption_Instance_Monthly_ViewMonthlyAccDataSUM() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Visits");
        usage.setDataGranularity("By Account");
        setMonthAndYear(-2);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("SPIN MASTER MEXICO SA DE CV | 12,962 | 7,811 | 15,017 | 8,870 | 19,288 | 16,666 | 21,031 | 14,102 | 23,625"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("RAUL MORALES RAMIREZ  | 15,121 | 11,533 | 21,067 | 18,197 | 18,109 | 5,655 | 6,652 | 21,898 | 13,152 | 12,336"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Scottish Shellfish Marketing Group Ltd | 12,404 | 10,494 | 14,970 | 13,639 | 14,922 | 14,324 | 17,016 | 5,875 | 8,613 | 15,947 | "));

    }

    @Test
    public void Adoption_Instance_Monthly_ViewMonthlyAccDataAVG() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        usage.setDataGranularity("By Account");
        setMonthAndYear(-1);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage = usage.selectUIView("Standard View");
        usage = usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC | 4,105 | 6,917.33 | 7,642.67 | 5,520.33 | 4,906.33 | 4,394 | 5,624 | 4,590 | 5,915 | 4,386.67 | "));
        Assert.assertEquals(true, usage.isDataPresentInGrid("CATMADER SL | 5,951.33 | 3,420 | 4,434.33 | 2,569.33 | 4,552.67 | 4,405 | 3,957.33 | 3,816 | 2,521.33 | 7,659.67"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("SISTEMAS MECANICOS E HIDRAULICOS SA DE CV | 6,040 | 5,184 | 3,747.33 | 3,967.67 | 5,704 | 6,289.33 | 3,659.33 | 7,555.33 | 2,146.67 | 5,717"));
    }

    @Test
    public void Adoption_Instance_Monthly_ViewMonthlyAccDataCount() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setDataGranularity("By Account");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        usage = usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("ORGANIZACION EMPRESARIAL POSTES Y CONSTRUCCIONES S | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("SIGMADIS BRIGNAIS | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("ATTEBURY GRAIN | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3"));
    }

    @Test
    public void Adoption_Instance_Monthly_TestMonthlyAdoptionSelectionFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        Assert.assertTrue(usage.isMonthlyFormEleDisplayed(), "Checking if Monthly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test
    public void Adoption_Instance_Monthly_TestAdoptionGridExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setDataGranularity("By Account");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertEquals(true, usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Adoption_Instance_Monthly_ViewAccountLevelUsageInGridAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage.setDataGranularity("By Account");
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC | 5,947 | 20,056 | 13,748 | 24,397 | 17,531 | 14,816 | 20,244 | 24,126 | 11,092 | 11,197 | "));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("BOESDORFER & BOESDORFER INC", null);
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking if grid is displayed");
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking if adoption chart is displayed");
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("All"), "Verifying that instance selected value is 'All'.");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded   | 5,947 | 20,056 | 13,748 | 24,397 | 17,531 | 14,816 | 20,244 | 24,126 | 11,092 | 11,197 | 20,964"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run  | 4,105 | 6,917.33 | 7,642.67 | 5,520.33 | 4,906.33 | 4,394 | 5,624 | 4,590 | 5,915 | 4,386.67 | 7,149.67"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views  |  3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits |  16,179 | 19,222 | 14,374 | 22,214 | 20,684 | 16,894 | 17,277 | 9,675 | 11,663 | 7,795 | 15,610"));
    }

    @Test
    public void Adoption_Instance_Monthly_ViewInstanceLevelUsageInGridAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Visits");
        setMonthAndYear(0);
        usage.setMonth(String.valueOf(month));
        usage.setYear(String.valueOf(year));
        usage.setDataGranularity("By Instance");
        usage = usage.displayMonthlyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Instance | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Baja Inc | Baja Inc - Instance 2 |  828 | 8,607 | 3,383 | 999 | 1,043 | 6,954 | 2,688 | 6,802 | 4,414 | 7,902 | 374"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Baja Inc | Baja Inc - Instance 3 |  5,461 | 6,951 | 5,799 | 410 | 1,871 | 7,881 | 9,336 | 6,366 | 4,234 | 3,709 | 7,056"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Baja Inc | Baja Inc - Instance 1 |  9,627 | 4,681 | 9,531 | 3,687 | 2,530 | 1,005 | 733 | 164 | 9,430 | 651 | 1,234\n"));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("Baja Inc", "Baja Inc - Instance 1");
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking if grid is displayed");
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking if adoption chart is displayed");
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("Baja Inc - Instance 1"), "Verifying that instance selected value is 'Baja Inc - Instance 1.");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 702 | 3,830 | 2,066 | 8,987 | 9,907 | 6,301 | 9,032 | 3,899 | 5,950 | 2,727"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run |  5,709 | 124 | 2,204 | 2,414 | 271 | 6,451 | 9,977 | 9,875 | 1,360 | 8,079"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views |  8,880 | 7,044 | 6,921 | 5,724 | 8,425 | 1,013 | 7,224 | 9,444 | 1,848 | 4,785 | "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits |  9,627 | 4,681 | 9,531 | 3,687 | 2,530 | 1,005 | 733 | 164 | 9,430 | 651"));
    }

    @Test
    public void Adoption_Instance_Monthly_ViewInstanceLevelUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("COMERGALV SA DE CV");
        analyticsPage.setInstance("COMERGALV SA DE CV - Instance 2");
        analyticsPage.setForTimeMonthPeriod("24 Months");
        setMonthAndYear(0);
        analyticsPage.setMonth(String.valueOf(month));
        analyticsPage.setYear(String.valueOf(year));
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isInstDropDownLoaded("All | COMERGALV SA DE CV - Instance 1 | COMERGALV SA DE CV - Instance 2 | COMERGALV SA DE CV - Instance 3"));
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("COMERGALV SA DE CV - Instance 2"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded |  4,822 | 9,041 | 756 | 1,277 | 5,095 | 3,391 | 7,458 | 9,425 | 6,224 | 4,347 | 8,854 | 7,003 | 1,922 | 4,520 | 4,381 | 8,871 | 3,200 | 7,183 | 3,392 | 4,174 | 4,525 | 5,262"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 4,249 | 5,018 | 7,332 | 9,730 | 7,162 | 6,732 | 7,637 | 974 | 3,480 | 4,325 | 555 | 5,300 | 7,601 | 13 | 8,980 | 7,315 | 6,963 | 168 | 9,384 | 2,122 | 4,982 | 8,287 | "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 9,875 | 8,297 | 233 | 7,803 | 6,349 | 369 | 8,548 | 4,209 | 4,536 | 7,552 | 3,695 | 825 | 6,014 | 3,452 | 4,083 | 9,178 | 8,318 | 9,772 | 3,563 | 1,714 | 5,627 | 3,027"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits| 214 | 27 | 4,894 | 1,343 | 1,834 | 7,981 | 8,657 | 7,863 | 3,455 | 8,875 | 2,785 | 7,110 | 8,159 | 1,953 | 911 | 8,995 | 5,500 | 9,136 | 610 | 727 | 3,775 | 4,551"));
    }


    @Test
    public void Adoption_Instance_Monthly_ViewAccountLevelUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setInstance("All");
        analyticsPage.setForTimeMonthPeriod("6 Months");
        setMonthAndYear(0);
        analyticsPage.setMonth(String.valueOf(month));
        analyticsPage.setYear(String.valueOf(year));
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isInstDropDownLoaded("All | Vicor - Instance 1 | Vicor - Instance 2 | Vicor - Instance 3"));
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("All"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 10,570 | 22,848 | 17,962 | 13,491 | 17,580"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run  | 5,556 | 7,059.67 | 1,894.67 | 2,548.67 | 4,677.67"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 3 | 3 | 3 | 3 | 3"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits | 13,191 | 11,343 | 20,328 | 17,064 | 15,560\n"));
    }

    @Test
    public void Adoption_Instance_Monthly_ViewAccountLevelUsageGraphWithMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setInstance("All");
        setMonthAndYear(2);
        analyticsPage.setForTimeMonthPeriod("6 Months");
        analyticsPage.setMonth(String.valueOf(month));
        analyticsPage.setYear(String.valueOf(year));
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isInstDropDownLoaded("All | Quince Hungary Kft - Instance 1 | Quince Hungary Kft - Instance 2 | Quince Hungary Kft - Instance 3"));
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("All"));
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some months."));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 14,388 | 11,338 | 14,327"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run  | 5,942.33 | 2,937.67 | 6,811"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views |  3 | 3 | 3 | "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits|  10,337 | 19,539 | 7,867"));
    }

    @Test
    public void Adoption_Instance_Monthly_NoAdoptionDataFoundMsgVerification() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("ABASTECEDORA DE METALES Y DERIVADOS SA DE CV");
        analyticsPage = analyticsPage.displayCustMonthlyData();
        Assert.assertTrue(analyticsPage.isNoAdoptionDataMsgDisplayed());
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
        Report.logInfo("Adoption_Account_Monthly_Test  End Time : " +c.getTime());
        basepage.logout();
    }
}