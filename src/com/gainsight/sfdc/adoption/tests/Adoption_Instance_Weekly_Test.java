package com.gainsight.sfdc.adoption.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

public class Adoption_Instance_Weekly_Test extends AdoptionDataSetup {
    private final String resDir                 = env.basedir + "/testdata/sfdc/usageData/";
    private final String ADV_CONFIG_FILE     = resDir+"scripts/Instance_Level_Weekly.txt";
    private final String JOB_UsageData          = resDir + "jobs/Job_Adop_Inst_Weekly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/usageData/tests/Adop_Inst_Weekly_Test.xls";
    private final String WEEKDAY                = "Wed";
    private final boolean isEndDate             = true;
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
        dataSetup.updateUtilizationAndUserDisplayInGrid("STANDARD", true);
        runAdoptionAggregation(15, true, isEndDate, WEEKDAY);
    }

    @TestInfo(testCaseIds={"GS-5036"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_InsWeek_1Measure1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date")) * 7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data4")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data5")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data6")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data7")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data8")));
    }

    @TestInfo(testCaseIds={"GS-5037"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void T2_InsWeek_1Measure1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5038"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T3_InsWeek_1MeasureNPeriodsCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5039"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void T4_InsWeek_1MeasureNPeriodsPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5040"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void T5_InsWeek_NMeasures1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5041"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void T6_InsWeek_NMeasures1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5042"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void T7_InsWeek_NMeasuresNPeriodsExportMessageCheck(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
    }

    @TestInfo(testCaseIds={"GS-2749"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void T8_InsWeek_GSUtilCalc1Period(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("STANDARD");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data4")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data5")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data6")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data7")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data8")));
    }

    @TestInfo(testCaseIds={"GS-5514"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void T9_InsWeek_GSUtilCalcNPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("STANDARD");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data3")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data4")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data5")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data6")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data7")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data8")));
    }

    @TestInfo(testCaseIds={"GS-5043"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void T10_InsWeek_UsageByMeasure1Period(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("MEASURE");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5515"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void T11_InsWeek_UsageByMeasureNPeriods(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("MEASURE");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5516"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void T12_InsWeek_UsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5044"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void T13_InsWeek_NoUsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(false); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
    }

    @TestInfo(testCaseIds={"GS-5047"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void T14_InsWeek_UsersInTrendGrid(Map<String, String> testData) {
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

    @TestInfo(testCaseIds={"GS-5045"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void T15_InsWeek_Trend_1MeasureNPeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage.setWeekLabelDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        List<List<String>> tableData = usage.getAdoptionTableData();
        String[] data = testData.get("UD_Data1").split(":::");
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data2").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data3").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data4").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data5").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
    }

    @TestInfo(testCaseIds={"GS-5046"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T16")
    public void T16_InsWeek_Trend_1MeasureNPeriodsPreviousPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage.setWeekLabelDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        List<List<String>> tableData = usage.getAdoptionTableData();
        String[] data = testData.get("UD_Data1").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data2").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data3").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data4").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data5").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
    }

    @TestInfo(testCaseIds={"GS-5517"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T17")
    public void T17_InsWeek_Trend_NMeasureNPeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeWeekPeriod(testData.get("Period"));
        usage.setWeekLabelDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayCustWeeklyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        List<List<String>> tableData = usage.getAdoptionTableData();
        String[] data = testData.get("UD_Data1").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data2").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data3").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data4").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data5").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        tableData = usage.getAdoptionTableData();
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
    }

    @TestInfo(testCaseIds={"GS-5048"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T18")
    public void T18_InsWeek_NoUsagePercentageAndUsersInGrid(Map<String, String> testData) {
        dataSetup.updateUtilizationCal(null);
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfWeeks(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        usage.setDate(DateUtil.getWeekLabelDate(WEEKDAY, USER_DATE_FORMAT, userTimezone, Integer.valueOf(testData.get("Date"))*7, isEndDate));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Header")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header2")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }


    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}
