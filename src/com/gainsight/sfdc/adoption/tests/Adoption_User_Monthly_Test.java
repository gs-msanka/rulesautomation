package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.utils.DataProviderArguments;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TimeZone;

public class Adoption_User_Monthly_Test extends AdoptionDataSetup {
    ObjectMapper mapper = new ObjectMapper();
    private final String USAGE_NAME         = "JBCXM__UsageData__c";
    private final String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    private final String CUST_SET_DELETE        = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";
    private final String resDir                 = TestEnvironment.basedir + "/testdata/sfdc/UsageData/";
    private final String measureFile            = resDir+"Scripts/Usage_Measure_Create.txt";
    private final String advUsageConfigFile     = resDir+"Scripts/User_Level_Monthly.txt";
    private final String JOB_Account            = resDir + "Jobs/Job_Adop_Accounts.txt";
    private final String JOB_Customers          = resDir + "Jobs/Job_Adop_Customers.txt";
    private final String JOB_UsageData          = resDir + "Jobs/Job_Adop_User_Monthly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/UsageData/Tests/Adop_User_Monthly_Test.xls";

    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        AdoptionDataSetup dataSetup = new AdoptionDataSetup();
        apex.runApexCodeFromFile(advUsageConfigFile, isPackage);
        dataSetup.initialSetup();
        dataSetup.loadUsageAccountAndCustomersData();
        dataSetup.loadUsageData(JOB_UsageData);
        runAdoptionAggregation(10, false, false, null);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_AccountMonthlySingleMeasuresSinglePeriod(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T2_AccountMonthlyMultipleMeasuresSinglePeriod(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T3_AccountMonthlySingleMeasureMultiplePeriod(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T4_AccountMonthlyGSStandUtil_Verification(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T5_AccountMonthlyMeasureUtil_Verification(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertEquals(true, usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T6_AccountMonthlyMultipleMeasureMultiplePeriods_Export(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.exportGrid());
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void T7_AccountMonthlySingleMeasureMultiplePeriodTrends(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void T8_AccountMonthlyMultipleMeasureMultiplePeriodTrends(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void T9_AccountMonthly_DisableUsersInEngagement(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }



    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}