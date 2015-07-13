package com.gainsight.sfdc.adoption.tests;

import java.io.IOException;
import java.util.Map;

import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.utils.annotations.TestInfo;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.utils.DataProviderArguments;

public class Adoption_User_Weekly_Test extends AdoptionDataSetup {
    private final String resDir                 = env.basedir + "/testdata/sfdc/usageData/";
    private final String ADV_CONFIG_FILE     = resDir+"scripts/User_Level_Weekly.txt";
    private final String JOB_UsageData          = resDir + "jobs/Job_Adop_User_Weekly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/usageData/tests/Adop_User_Weekly_Test.xls";
    private final String WEEKDAY                = "Wed";
    private final boolean isEndDate             = false;
    AdoptionDataSetup dataSetup;


    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        sfdc.connect();
        dataSetup = new AdoptionDataSetup();
        sfdc.runApexCode(resolveStrNameSpace(FileUtil.getFileContents(ADV_CONFIG_FILE)));
        dataSetup.initialSetup();
        dataSetup.loadUsageAccountAndCustomersData();
        dataSetup.loadUsageData(JOB_UsageData);
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        dataSetup.runAdoptionAggregation(15, true, isEndDate, WEEKDAY);
    }

    @TestInfo(testCaseIds={"GS-5070"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_UserWeekly_1Measure1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date")) * 7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5071"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void T2_UserWeekly_1Measure1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5072"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T3_UserWeekly_1MeasureNPeriodsCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5073"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void T4_UserWeekly_1MeasureNPeriodsPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5074"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void T5_UserWeekly_NMeasures1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5075"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void T6_UserWeekly_NMeasures1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5077"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void T7_UserWeekly_NMeasuresNPeriodsExportMessageCheck(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.exportGrid(), "Verifying Export");
    }

    @TestInfo(testCaseIds={"GS-2757"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void T8_UserWeekly_GSUtilCalc1Period(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5522"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void T9_UserWeekly_GSUtilCalcNPeriod(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5088"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void T10_UserWeekly_UsageByMeasure1Period(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("MEASURE");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5523"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void T11_UserWeekly_UsageByMeasureNPeriods(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("MEASURE");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-2778"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void T12_UserWeekly_UsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @TestInfo(testCaseIds={"GS-5080"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void T13_UserWeekly_NoUsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
    }

    @TestInfo(testCaseIds={"GS-5083"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void T14_UserWeekly_UsersInTrendGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage.setWeekLabelDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5524"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void T15_UserWeekly_Trend_1Measure12PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage.setWeekLabelDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @TestInfo(testCaseIds={"GS-5084"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T16")
    public void T16_UserWeekly_Trend_1Measure6PeriodsPreviousPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage.setWeekLabelDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @TestInfo(testCaseIds={"GS-5525"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T17")
    public void T17_UserWeekly_Trend_NMeasure6PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setWeekLabelDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
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

    @TestInfo(testCaseIds={"GS-5085"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T18")
    public void T18_UserWeekly_NoUsagePercentageAndUsersInGrid(Map<String, String> testData) {
        dataSetup.updateUtilizationCal(null);
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
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