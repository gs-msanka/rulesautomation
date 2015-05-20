package com.gainsight.sfdc.adoption.tests;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.adoption.pages.AdoptionAnalyticsPage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class Adoption_Account_Monthly_Test extends BaseTest {

    public final String resDir                      = env.basedir + "/testdata/sfdc/usageData/";
    private final String ADV_USAGE_CONFIG     = resDir+"scripts/Account_Level_Monthly.txt";
    private final String JOB_UsageData          = resDir + "jobs/Job_Adop_Acc_Monthly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/usageData/tests/Adop_Acc_Monthly_Test.xls";
    AdoptionDataSetup dataSetup;

    @BeforeClass
    public void setUp() throws IOException {
        basepage.login();
        sfdc.connect();
        dataSetup = new AdoptionDataSetup();
        sfdc.runApexCode(resolveStrNameSpace(FileUtil.getFileContents(ADV_USAGE_CONFIG)));
        dataSetup.initialSetup();
        dataSetup.loadUsageAccountAndCustomersData();
        dataSetup.loadUsageData(JOB_UsageData);
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        dataSetup.runAdoptionAggregation(1, false, false, null);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_AccMonth_1Measure1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void T2_AccMonth_1Measure1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T3_AccMonth_1MeasureNPeriodsCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void T4_AccMonth_1MeasureNPeriodsPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void T5_AccMonth_NMeasures1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void T6_AccMonth_NMeasures1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void T7_AccMonth_NMeasuresNPeriodsExportMessageCheck(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.exportGrid());
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void T8_AccMonth_GSUtilCalc1Period(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void T9_AccMonth_GSUtilCalcNPeriod(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        //Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void T10_AccMonth_UsageByMeasure1Period(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("MEASURE");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void T11_AccMonth_UsageByMeasureNPeriods(Map<String, String> testData) {
        dataSetup.updateUtilizationCal("MEASURE");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void T12_AccMonth_UsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void T13_AccMonth_NoUsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Header")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void T14_AccMonth_UsersInTrendGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true);
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void T15_AccMonth_Trend_1Measure12PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T16")
    public void T16_AccMonth_Trend_1Measure6PeriodsPreviousPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T17")
    public void T17_AccMonth_Trend_NMeasure6PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayCustMonthlyData();
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
    public void T18_AccMonth_NoUsagePercentageAndUsersInGrid(Map<String, String> testData) {
        dataSetup.updateUtilizationCal(null);
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Header")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header2")));
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}