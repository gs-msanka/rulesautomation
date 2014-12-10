package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class Adoption_Account_Weekly_Test extends AdoptionDataSetup {
    private final String resDir                 = TestEnvironment.basedir + "/testdata/sfdc/UsageData/";
    private final String advUsageConfigFile     = resDir+"Scripts/Account_Level_Weekly.txt";
    private final String JOB_UsageData          = resDir + "Jobs/Job_Adop_Acc_Weekly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/UsageData/Tests/Adop_Acc_Weekly_Test.xls";
    private final String WEEKDAY                = "Sat";
    private final boolean isEndDate             = true;
    AdoptionDataSetup dataSetup;

    @BeforeClass
    public void setUp() throws IOException {
        basepage.login();
        dataSetup = new AdoptionDataSetup();
        /*apex.runApexCodeFromFile(advUsageConfigFile, isPackage);
        dataSetup.initialSetup();
        dataSetup.loadUsageAccountAndCustomersData();
        dataSetup.loadUsageData(JOB_UsageData);
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        dataSetup.runAdoptionAggregation(1, true, isEndDate, WEEKDAY); */
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_AccWeek_1Measure1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void T2_AccWeek_1Measure1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T3_AccWeek_1MeasureNPeriodsCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void T4_AccWeek_1MeasureNPeriodsPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void T5_AccWeek_NMeasures1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void T6_AccWeek_NMeasures1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void T7_AccWeek_NMeasuresNPeriodsExportMessageCheck(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date")) * 7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.exportGrid(), "Verifying Export");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void T8_AccWeek_GSUtilCalc1Period(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void T9_AccWeek_GSUtilCalcNPeriod(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void T10_AccWeek_UsageByMeasure1Period(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("MEASURE");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void T11_AccWeek_UsageByMeasureNPeriods(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("MEASURE");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void T12_AccWeek_UsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void T13_AccWeek_NoUsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Header")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void T14_AccWeek_UsersInTrendGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage .setWeekLabelDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void T15_AccWeek_Trend_1Measure12PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage .setWeekLabelDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T16")
    public void T16_AccWeek_Trend_1Measure6PeriodsPreviousPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage .setWeekLabelDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T17")
    public void T17_AccWeek_Trend_NMeasure6PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage .setWeekLabelDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, isEndDate, true));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data4")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data5")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data6")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data7")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data8")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data9")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T18")
    public void T18_AccWeek_NoUsagePercentageAndUsersInGrid(Map<String, String> testData) {
        dataSetup.updateUtilizationCal(null);
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(getWeekLabelDate(WEEKDAY, Integer.valueOf(testData.get("Date"))*7, true, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Header")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }



    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}