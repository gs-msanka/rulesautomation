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

import java.io.FileReader;
import java.util.Calendar;

public class Adoption_Account_Weekly_Test extends BaseTest {
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
    String QUERY = "DELETE [SELECT ID FROM JBCXM__StatePreservation__c Where Name = 'Adoption'];";

    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/Account_Level_Weekly.txt";

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
            jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Account_Weekly.txt"), JobInfo.class);
            dataLoader.execute(jobInfo3);
        } catch (Exception e) {
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void Adoption_Account_Weekly_ViewWeeklyData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Files Downloaded");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDate(getWeekLabelDate("Sat", 0, true, true));
        usage = usage.displayWeeklyUsageData();
        usage = usage.selectCustomersView("All");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("Alltech Automotive LLC | 10,741 | 4,266 | 6,714 | 9,823 | 8,738 | 4,387 | 7,156 | 4,966 | 8,327 | 2,234 | 9,557"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC | 13,197 | 14,013 | 587 | 2,316 | 12,003 | 12,073 | 10,618 | 2,031 | 3,182 | 6,829 | 2,555"));
    }

    @Test
    public void testWeeklyAdoptionSelectionFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if weekly form is displayed");
    }

    @Test
    public void testAdoptionGridExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Views");
        usage.setNoOfWeeks("12 Weeks");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertEquals(true, usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Adoption_Account_Weekly_ViewAccountLevelUsageInGridAndGraph() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("No of Report Run");
        usage.setNoOfWeeks("6 Weeks");
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("AUREA SOFTWARE INC | 12,370 | 3,394 | 7,298 | 8,089 | 6,242 | 6,005"));
        AdoptionAnalyticsPage analyticsPage = usage.navToUsageByCust("AUREA SOFTWARE INC", null);
        Assert.assertTrue(analyticsPage.isGridDispalyed(), "Checking if grid is displayed");
        Assert.assertTrue(analyticsPage.isChartDisplayed(), "Checking if adoption chart is displayed");
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 14,637 | 11,833 | 11,376 | 8,455 | 4,293 | 14,042 | 3,082 | 11,977 | 1,116 | 3,415 | 11,087 | 10,787 | 3,246 | 8,240 | 9,621 | 11,192 | 9,856 | 11,328 | 1,670 | 2,957 | 14,366 | 13,112 | 4,569 | 10,392 | 8,712 | 2,628 | 9,211 | 1,614 | 6,028 | 10,358 | 2,086 | 8,938 | 11,115 | 4,247 | 12,232 | 3,578 | 202 | 3,755 | 1,282 | 12,366 | 13,893 | 13,736 | 7,383 | 10,373 | 4,459 | 12,780 | 7,987 | 14,783 | 819 | 7,734 | 6,607 | 7,858"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 6,313 | 10,529 | 2,620 | 14,739 | 5,945 | 4,541 | 2,102 | 8,913 | 3,269 | 8,920 | 12,598 | 902 | 14,013 | 6,653 | 4,178 | 2,062 | 12,142 | 3,093 | 5,883 | 8,617 | 10,721 | 7,723 | 7,639 | 5,874 | 1,835 | 6,342 | 4,283 | 3,285 | 2,684 | 13,650 | 925 | 8,867 | 9,605 | 8,304 | 14,962 | 4,157 | 7,665 | 10,330 | 3,410 | 8,376 | 330 | 6,006 | 2,689 | 8,217 | 3,811 | 8,635 | 12,370 | 3,394 | 7,298 | 8,089 | 6,242 | 6,005"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 3,614 | 12,639 | 8,953 | 10,260 | 3,143 | 13,198 | 5,490 | 11,493 | 2,842 | 1,257 | 1,342 | 7,180 | 7,402 | 2,420 | 5,100 | 8,729 | 1,407 | 6,530 | 7,085 | 5,103 | 12,626 | 358 | 12,297 | 3,866 | 9,075 | 9,646 | 1,732 | 4,293 | 4,888 | 8,301 | 455 | 11,904 | 1,768 | 9,813 | 3,369 | 13,565 | 6,433 | 6,672 | 14,872 | 4,128 | 8,903 | 3,806 | 5,990 | 10,472 | 1,883 | 2,205 | 5,205 | 627 | 2,780 | 13,131 | 14,961 | 3,789"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits | 7,936 | 10,302 | 12,672 | 4,250 | 429 | 1,519 | 5,673 | 12,148 | 14,582 | 8,426 | 1,535 | 5,995 | 401 | 4,298 | 10,356 | 12,702 | 13,050 | 12,521 | 5,383 | 6,234 | 3,472 | 9,371 | 1,887 | 6,749 | 7,848 | 3,853 | 5,049 | 2,242 | 9,209 | 9,402 | 11,398 | 13,684 | 12,313 | 7,880 | 13,099 | 10,806 | 4,211 | 1,780 | 2,535 | 5,398 | 14,299 | 3,060 | 917 | 9,717 | 50 | 13,702 | 13,245 | 8,040 | 4,977 | 368 | 12,895 | 13,260"));
    }

    @Test
    public void Adoption_Account_Weekly_ViewAccountLevelUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Sat", -7, true, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded | 12,096 | 2,503 | 12,928 | 7,014 | 5,836 | 2,981 | 2,089 | 8,791 | 2,612 | 13,125 | 9,539 | 10,318 | 1,935 | 8,424 | 13,889 | 2,186 | 5,588 | 4,638 | 2,454 | 4,297 | 3,399 | 2,177 | 11,141 | 1,881 | 7,075 | 12,373 | 5,417 | 9,617 | 12,620 | 13,070 | 2,331 | 11,238 | 2,209 | 7,156 | 4,821 | 13,836 | 10,112 | 4,983 | 7,962 | 11,517 | 10,235 | 4,502 | 5,308 | 3,403 | 7,663 | 9,393 | 12,097 | 10,011 | 12,836 | 11,909 | 980 "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run | 2,629 | 4,207 | 1,571 | 10,245 | 5,613 | 10,275 | 11,045 | 817 | 3,128 | 504 | 13,970 | 13,714 | 6,383 | 9,823 | 14,483 | 12,304 | 12,423 | 120 | 3,511 | 595 | 10,415 | 13,616 | 958 | 14,653 | 10,166 | 9,894 | 8,743 | 10,189 | 8,229 | 812 | 9,474 | 14,609 | 12,286 | 14,461 | 4,473 | 631 | 1,289 | 11,135 | 5,963 | 8,306 | 264 | 2,194 | 14,667 | 8,684 | 2,721 | 12,478 | 10,247 | 9,344 | 13,353 | 8,518 | 11,990"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 6,566 | 6,955 | 9,594 | 9,806 | 4,377 | 9,880 | 7,808 | 10,827 | 2,448 | 8,581 | 7,528 | 14,258 | 13,988 | 10,446 | 4,062 | 4,784 | 8,917 | 14,473 | 1,567 | 4,365 | 13,154 | 348 | 13,940 | 10,962 | 7,563 | 9,565 | 9,351 | 11,772 | 3,786 | 2,006 | 6,503 | 6,096 | 12,482 | 14,700 | 8,977 | 14,227 | 10,433 | 3,855 | 9,893 | 9,741 | 8,504 | 1,478 | 2,219 | 4,341 | 1,753 | 14,991 | 10,002 | 11,394 | 2,835 | 12,820 | 6,035 "));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits| 6,231 | 4,862 | 11,102 | 2,251 | 3,105 | 2,566 | 12,261 | 9,346 | 6,933 | 3,370 | 12,759 | 916 | 1,669 | 2,977 | 12,855 | 4,048 | 7,854 | 14,505 | 7,299 | 10,972 | 11,625 | 5,759 | 11,528 | 5,712 | 73 | 4,250 | 11,565 | 8,604 | 8,793 | 7,792 | 11,372 | 7,197 | 10,386 | 7,589 | 3,344 | 11,902 | 3,276 | 5,171 | 11,400 | 11,358 | 2,918 | 12,542 | 14,593 | 7,991 | 11,749 | 275 | 11,071 | 12,869 | 9,315 | 7,624 | 9,115"));
    }

    @Test
    public void Adoption_Account_Weekly_ViewAccountLevelUsageGraphWithMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Sat", +28, true, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDispalyed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks."));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded  | 3,351 | 4,521 | 773 | 10,732 | 1,896 | 1,231 | 14,170 | 3,324 | 838 | 9,061 | 2,041 | 1,888 | 10,986 | 2,624 | 7,710 | 4,848 | 7,677 | 11,269 | 4,043 | 11,935 | 11,956 | 5,553 | 430 | 12,581 | 12,884 | 12,071 | 5,422 | 247 | 8,158 | 2,630 | 1,153 | 3,746 | 14,684 | 8,562 | 7,152 | 515 | 11,890 | 8,282 | 2,807 | 11,978 | 8,506 | 13,538 | 8,515 | 13,310 | 8,724 | 4,175 | 4,317"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run  | 2,611 | 9,256 | 9,125 | 13,844 | 6,961 | 10,603 | 6,217 | 9,621 | 9,394 | 2,598 | 12,327 | 10,384 | 14,936 | 4,664 | 2,111 | 10,097 | 1,434 | 6,836 | 7,187 | 5,163 | 12,566 | 10,659 | 4,531 | 5,746 | 5,891 | 3,871 | 4,752 | 7,602 | 14,319 | 194 | 10,499 | 2,351 | 10,944 | 11,556 | 6,112 | 904 | 11,255 | 3,653 | 706 | 3,836 | 8,113 | 1,732 | 4,177 | 7,782 | 10,997 | 8,800 | 2,636"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views | 10,305 | 1,076 | 12,535 | 13,614 | 13,266 | 517 | 1,419 | 11,966 | 5,871 | 13,164 | 10,397 | 11,199 | 8,701 | 10,848 | 3,354 | 13,729 | 5,459 | 35 | 11,866 | 6,395 | 5,318 | 4,703 | 7,139 | 11,739 | 10,165 | 10,248 | 6,974 | 1,044 | 8,497 | 9,163 | 2,300 | 9,104 | 3,734 | 14,224 | 14,374 | 7,684 | 5,542 | 12,500 | 6,414 | 1,436 | 7,950 | 6,782 | 1,794 | 372 | 7,992 | 1,368 | 745"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits | 12,279 | 3,099 | 350 | 5,280 | 14,809 | 3,580 | 8,267 | 8,322 | 2,722 | 11,738 | 204 | 3,435 | 4,414 | 2,649 | 9,585 | 10,240 | 2,589 | 6,786 | 10,320 | 535 | 8,922 | 9,523 | 8,331 | 2,657 | 3,867 | 13,655 | 4,588 | 9,447 | 10,133 | 8,813 | 8,549 | 10,992 | 10,911 | 6,984 | 12,726 | 602 | 1,886 | 10,070 | 5,292 | 9,585 | 3,283 | 1,227 | 4,867 | 8,996 | 8,006 | 5,093 | 738"));
    }

    @Test
    public void Adoption_Account_Weekly_NoAdoptionDataFoundMsgVerification() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnUsageAnalyticsTab();
        analyticsPage.setCustomerName("ABASTECEDOR HOSPITALARIO DEL");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isNoAdoptionDataMsgDisplayed());
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}