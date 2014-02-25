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

public class Adoption_Instance_Weekly_Test extends BaseTest {
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



    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/Instance_Level_Weekly.txt";
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
            jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Instance_Weekly.txt"), JobInfo.class);
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
            for(int k = 0; k< 5;k++) {
                for(int m=0; m < 5; m++, i=i-7) {
                    //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                    // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                    dateStr     = getWeekLabelDate("Wed", i, true, false);
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

        } catch (Exception e) {
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    @Test
    public void Adoption_Instance_Weekly_ViewWeeklyInsData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDataGranularity("By Instance");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Instance | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("TOMAS MARTINEZ PATLAN | TOMAS MARTINEZ PATLAN - Instance 2 | 1,662 | 7,159 | 7,296 | 5,663 | 4,085 | 9,931 | 8,497 | 4,595 | 5,743 | 5,076"));
    }


    @Test
    public void Adoption_Instance_Weekly_ViewWeeklyAccDataSUM() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDataGranularity("By Account");
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("TOMAS MARTINEZ PATLAN | 11,054 | 3,741 | 14,374 | 10,092 | 15,809 | 7,886 | 14,658 | 13,646 | 12,781 | 9,352 | 6,454"));
    }

    @Test
    public void Adoption_Instance_Weekly_ViewWeeklyAccDataAVG() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        usage.setNoOfWeeks("9 Weeks");
        usage.setDataGranularity("By Account");
        usage = usage.displayWeeklyUsageData();
        usage = usage.selectUIView("Standard View");
        usage = usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("A and T unlimit Limited | 1,508.5 | 2,655.5 | 5,412.5 | 6,646 | 5,087 | 9,318.5 | 6,276.5 | 7,576"));
    }

    @Test
    public void Adoption_Instance_Weekly_ViewWeeklyAccDataCount() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("6 Weeks");
        usage.setDataGranularity("By Account");
        usage = usage.displayWeeklyUsageData();
        usage = usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("AUREA SOFTWARE INC | 2 | 2 | 2 | 2 | 2 "));
    }

    @Test
    public void Adoption_Instance_Weekly_TestWeeklyAdoptionSelectionFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if weekly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test
    public void Adoption_Instance_Weekly_TestAdoptionGridExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDataGranularity("By Account");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertEquals(true, usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Adoption_Instance_Weekly_ViewAccountLevelUsageInGridAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDataGranularity("By Account");
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("AUREA SOFTWARE INC | 572 | 8,936 | 9,743.5 | 2,998 | 3,382.5 | 6,735 | 5,420.5 | 7,055 | 4,328"));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("AUREA SOFTWARE INC", null);
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking if grid is displayed");
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking if adoption chart is displayed");
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("All"), "Verifying that instance selected value is 'All'.");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 17,550 | 9,715 | 2,449 | 6,991 | 5,432 | 15,844 | 8,035 | 18,596 | 9,068 | 10,711 | 14,287 | 17,158 | 16,215 | 15,759"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 3,629.5 | 1,185 | 3,785 | 7,227.5 | 5,548.5 | 572 | 8,936 | 9,743.5 | 2,998 | 3,382.5 | 6,735 | 5,420.5 | 7,055 | 4,328"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits | 8,768 | 509 | 17,110 | 7,554 | 4,447 | 15,876 | 4,206 | 14,371 | 15,376 | 14,086 | 11,913 | 8,809 | 12,088 | 11,130"));
    }

    @Test
    public void Adoption_Instance_Weekly_ViewInstanceLevelUsageInGridAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Visits");
        usage.setNoOfWeeks("3 Weeks");
        usage.setDataGranularity("By Instance");
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Instance | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Terry Store age spa | Terry Store age spa - Instance 1 | 2,020 | 8,838 | 3,647"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Terry Store age spa | Terry Store age spa - Instance 2 | 8,594 | 8,723 | 7,123"));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("Terry Store age spa", "Terry Store age spa - Instance 1");
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking if grid is displayed");
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking if adoption chart is displayed");
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("Terry Store age spa - Instance 1"), "Verifying that instance selected value is 'Terry Store age spa - Instance 1'.");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 7,357 | 322 | 5,344 | 2,337 | 2,354 | 10,199 | 3,414 | 8,956 | 3,131 | 6,459 | 11,648 | 8,422 | 10,867 | 451 | 7,279 | 3,657 | 3,979 | 7,929 | 9,856 | 4,465 | 10,471 | 2,524 | 4,681 | 8,400 | 432 | 1,233 | 4,931 | 7,051 | 1,812 | 7,984 | 4,292 | 9,781 | 9,662 | 7,789 | 63 | 6,689 | 4,610 | 8,239 | 2,090 | 5,401 | 7,895 | 9,524 | 11,520 | 10,986 | 3,341 | 4,359 | 5,691 | 4,550 | 3,567"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 1,698 | 4,807 | 4,504 | 6,023 | 3,635 | 6,054 | 9,787 | 1,227 | 1,626 | 8,109 | 11,653 | 1,260 | 2,455 | 11,890 | 2,719 | 1,526 | 4,174 | 2,388 | 7,780 | 8,376 | 7,105 | 6,020 | 8,554 | 11,084 | 7,555 | 3,967 | 1,783 | 9,129 | 8,974 | 11,465 | 11,030 | 9,513 | 5,639 | 8,443 | 1,506 | 11,571 | 4,358 | 5,481 | 7,979 | 5,610 | 1,835 | 1,493 | 6,399 | 10,364 | 6,285 | 3,185 | 2,475 | 111 | 280 | 10,73"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 2,579 | 2,203 | 1,531 | 8,744 | 2,637 | 2,941 | 6,780 | 3,662 | 1,844 | 7,821 | 5,205 | 1,204 | 7,128 | 58 | 6,363 | 2,182 | 4,495 | 6,149 | 9,053 | 2,312 | 8,348 | 6,411 | 3,090 | 3,427 | 6,015 | 1,340 | 8,355 | 10,115 | 3,559 | 4,229 | 6,361 | 4,960 | 6,332 | 5,247 | 3,608 | 9,403 | 3,872 | 11,623 | 3,245 | 11,897 | 3,899 | 11,063 | 6,823 | 1,537 | 9,510 | 2,274 | 6,944 | 5,389 | 878 | 10,773"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits | 349 | 11,245 | 7,835 | 6,823 | 2,476 | 3,793 | 9,816 | 143 | 3,240 | 8,882 | 9,770 | 7,284 | 9,204 | 1,082 | 4,394 | 2,380 | 1,463 | 6,813 | 6,730 | 6,733 | 803 | 2,231 | 4,289 | 1,614 | 4,063 | 1,198 | 10,483 | 8,332 | 9,860 | 11,120 | 4,941 | 6,295 | 9,076 | 2,985 | 2,620 | 11,946 | 6,647 | 11,249 | 2,227 | 324 | 281 | 7,774 | 9,190 | 11,865 | 547 | 822 | 7,826 | 2,020 | 8,838 | 3,647"));
    }

    @Test
    public void Adoption_Instance_Weekly_ViewInstanceLevelUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("JUAN ANGEL FLORES AGUIRRE");
        analyticsPage.setInstance("JUAN ANGEL FLORES AGUIRRE - Instance 1");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", -7, true, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isInstDropDownLoaded("All | JUAN ANGEL FLORES AGUIRRE - Instance 1 | JUAN ANGEL FLORES AGUIRRE - Instance 2"));
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("JUAN ANGEL FLORES AGUIRRE - Instance 1"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 9,266 | 8,585 | 10,994 | 10,934 | 3,986 | 10,246 | 9,685 | 11,706 | 4,997 | 3,680 | 9,866 | 5,805 | 3,898 | 6,891 | 1,358 | 3,416 | 3,212 | 11,079 | 3,652 | 1,689 | 1,715 | 6,739 | 6,616 | 6,789 | 1,840 | 6,300 | 303 | 5,743 | 11,294 | 10,509 | 11,586 | 5,203 | 1,173 | 9,999 | 5,695 | 1,187 | 6,204 | 10,308 | 8,302 | 868 | 5,219 | 2,067 | 1,067 | 11,817 | 2,741 | 6,280 | 4,193 | 804 | 10,57"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 7,898 | 11,910 | 2,382 | 31 | 1,144 | 1,527 | 8,022 | 4,482 | 6,945 | 9,540 | 779 | 390 | 7,750 | 4,141 | 547 | 10,115 | 9,098 | 11,245 | 10,460 | 6,416 | 7,718 | 4,028 | 2,965 | 4,486 | 5,745 | 560 | 3,960 | 8,855 | 11,444 | 11,874 | 317 | 6,671 | 5,214 | 829 | 712 | 6,713 | 2,989 | 2,907 | 10,309 | 5,466 | 235 | 10,757 | 8,385 | 344 | 4,999 | 9,448 | 1,312 | 10,510 | 7,807"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 7,172 | 7,220 | 10,104 | 8,155 | 1,666 | 2,369 | 71 | 11,535 | 130 | 8,395 | 3,184 | 8,762 | 591 | 2,942 | 1,562 | 277 | 9,837 | 1,742 | 3,003 | 3,969 | 10,109 | 2,102 | 5,318 | 9,516 | 11,356 | 7,441 | 5,423 | 11,096 | 4,277 | 10,698 | 4,517 | 7,123 | 4,208 | 2,774 | 7,739 | 9,780 | 8,912 | 11,520 | 6,839 | 2,087 | 9,378 | 1,810 | 11,466 | 10,399 | 7,535 | 3,876 | 9,552 | 5,179 | 4,038"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits| 4,149 | 2,434 | 7,466 | 9,383 | 7,716 | 9,751 | 8,141 | 10,040 | 9,939 | 414 | 9,439 | 3,106 | 6,035 | 11,413 | 8,376 | 1,077 | 10,341 | 5,665 | 1,168 | 9,701 | 9 | 8,489 | 6,983 | 11,805 | 5,313 | 6,605 | 1,678 | 761 | 4,006 | 10,725 | 1,179 | 469 | 172 | 2,278 | 8,889 | 824 | 6,038 | 3,148 | 4,213 | 2,569 | 11,567 | 9,785 | 11,370 | 5,289 | 4,858 | 8,035 | 7,936 | 10,965 | 1,202"));
    }


    @Test
    public void Adoption_Instance_Weekly_ViewAccountLevelUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setInstance("All");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", -7, true, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isInstDropDownLoaded("All | Vicor - Instance 1 | Vicor - Instance 2"));
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("All"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 13,846 | 14,237 | 12,110 | 10,062 | 13,539 | 12,774 | 11,048 | 19,105 | 12,051 | 17,293 | 4,054 | 11,903 | 9,190 | 9,221"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 5,150 | 543.5 | 3,382.5 | 5,851.5 | 778 | 9,314.5 | 8,743.5 | 10,176.5 | 5,982 | 6,618.5 | 2,059 | 3,783 | 5,835 | 5,305.5 | 6,125 "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits| 11,375 | 18,603 | 7,116 | 5,157 | 18,977 | 17,040 | 12,334 | 10,651 | 11,953 | 17,084 | 9,056 | 8,310 | 12,744 | 9,519 | 7,807"));
    }

    @Test
    public void Adoption_Instance_Weekly_ViewAccountLevelUsageGraphWithMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setInstance("All");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", +28, true, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isInstDropDownLoaded("All | Quince Hungary Kft - Instance 1 | Quince Hungary Kft - Instance 2"));
        Assert.assertTrue(analyticsPage.verifySelectedInstanceValue("All"));
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks."));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 18,673 | 12,789 | 12,712 | 13,162 | 20,670 | 21,988 | 1,773 | 9,954 | 10,722 | 8,367 | 9,230 | 19,761 | 12,136 | 6,314 | 4,378"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 2,956.5 | 9,118.5 | 4,026.5 | 9,051.5 | 1,652 | 4,792 | 7,432 | 8,041 | 7,301 | 5,564.5 | 3,621 | 5,203.5 | 7,133 | 8,930.5 | 9,125"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits| 15,232 | 1,649 | 13,613 | 14,541 | 13,034 | 15,445 | 5,170 | 7,828 | 16,565 | 12,434 | 9,609 | 14,075 | 14,127 | 17,680 | 13,427"));
    }

    @Test
    public void Adoption_Instance_Weekly_NoAdoptionDataFoundMsgVerification() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("ABASTECEDORA DE VALVULAS INTRUMENT");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isNoAdoptionDataMsgDisplayed());
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
