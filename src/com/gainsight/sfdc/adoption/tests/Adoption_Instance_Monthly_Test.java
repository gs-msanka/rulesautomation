package com.gainsight.sfdc.adoption.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
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

public class Adoption_Instance_Monthly_Test extends AdoptionDataSetup {
    private final String resDir                 = env.basedir + "/testdata/sfdc/usageData/";
    private final String ADV_USAGE_CONFIG     = resDir+"scripts/Instance_Level_Monthly.txt";
    private final String JOB_UsageData          = resDir + "jobs/Job_Adop_Inst_Monthly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/usageData/tests/Adop_Inst_Monthly_Test.xls";
    AdoptionDataSetup dataSetup;


    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        sfdc.connect();
        dataSetup = new AdoptionDataSetup();
        sfdc.runApexCode(resolveStrNameSpace(FileUtil.getFileContents(ADV_USAGE_CONFIG)));
        dataSetup.initialSetup();
        dataSetup.loadUsageAccountAndCustomersData();
        dataSetup.loadUsageData(JOB_UsageData);
        dataSetup.updateUtilizationCal("STANDARD");
        dataSetup.updateUsersDisplayInUsageGrids(true);
        dataSetup.runAdoptionAggregation(10, false, false, null);
    }

    @TestInfo(testCaseIds={"GS-5008"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_InsMonth_1Measure1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
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

    @TestInfo(testCaseIds={"GS-5009"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void T2_InsMonth_1Measure1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5010"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T3_InsMonth_1MeasureNPeriodsCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5011"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void T4_InsMonth_1MeasureNPeriodsPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5012"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void T5_InsMonth_NMeasures1PeriodCurrentPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5013"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void T6_InsMonth_NMeasures1PeriodPreviousPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5509"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void T7_InsMonth_NMeasuresNPeriodsExportMessageCheck(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
    }

    @TestInfo(testCaseIds={"GS-2747"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void T8_InsMonth_GSUtilCalc1Period(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("STANDARD");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
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

    @TestInfo(testCaseIds={"GS-5510"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void T9_InsMonth_GSUtilCalcNPeriod(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("STANDARD");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
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

    @TestInfo(testCaseIds={"GS-5029"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void T10_InsMonth_UsageByMeasure1Period(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("MEASURE");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5512"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T11")
    public void T11_InsMonth_UsageByMeasureNPeriods(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        dataSetup.updateUtilizationCal("MEASURE");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-2767"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T12")
    public void T12_InsMonth_UsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(true); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5030"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T13")
    public void T13_InsMonth_NoUsersInEngagementGrid(Map<String, String> testData) {
        dataSetup.updateUsersDisplayInUsageGrids(false); //Can be removed if bug in product is fixed i.e if users is disabled then Usage% is not displayed
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isGridHeaderMapped(testData.get("Headers")));
        Assert.assertFalse(usage.isGridHeaderMapped(testData.get("Header1")));
    }

    @TestInfo(testCaseIds={"GS-5031"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T14")
    public void T14_InsMonth_UsersInTrendGrid(Map<String, String> testData) {
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
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data2")));
    }

    @TestInfo(testCaseIds={"GS-5032"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T15")
    public void T15_InsMonth_Trend_1Measure12PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        List<List<String>> tableData = usage.getAdoptionTableData();
        String[] data = testData.get("UD_Data1").split(":::");
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data2").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data3").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data4").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data5").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
    }

    @TestInfo(testCaseIds={"GS-5033"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T16")
    public void T16_InsMonth_Trend_1Measure6PeriodsPreviousPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        List<List<String>> tableData = usage.getAdoptionTableData();
        String[] data = testData.get("UD_Data1").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data2").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data3").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data4").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
        data = testData.get("UD_Data5").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        Assert.assertTrue(usage.isDataPresentInGridData(tableData, data[1]));
    }

    @TestInfo(testCaseIds={"GS-5513"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T17")
    public void T17_InsMonth_Trend_NMeasure6PeriodsCurrentPeriod(Map<String, String> testData) {
        AdoptionAnalyticsPage usage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        usage.setCustomerName(testData.get("Customer"));
        usage.setMeasureNames(testData.get("Measures"));
        usage.setForTimeMonthPeriod(testData.get("Period"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayCustMonthlyData();
        Assert.assertTrue(usage.isChartDisplayed(), "Verifying the adoption chart is displayed for the user.");
        List<List<String>> tableData = usage.getAdoptionTableData();
        String[] data = testData.get("UD_Data1").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data2").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data3").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data4").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
        data = testData.get("UD_Data5").split(":::");
        usage.viewCustomerInstanceData(data[0]);
        for(String s : Arrays.copyOfRange(data, 1, data.length)) {
            Assert.assertTrue(usage.isDataPresentInGridData(tableData, s));
        }
    }

    @TestInfo(testCaseIds={"GS-5034"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T18")
    public void T18_InsMonth_NoUsagePercentageAndUsersInGrid(Map<String, String> testData) {
        dataSetup.updateUtilizationCal(null);
        dataSetup.updateUsersDisplayInUsageGrids(false);
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        usage.setDataGranularity(testData.get("Granularity"));
        Calendar cal = DateUtil.addMonths(userTimezone, Integer.valueOf(testData.get("Date")));
        usage.setMonth(DateUtil.getMonthName(cal));
        usage.setYear(String.valueOf(cal.get(Calendar.YEAR)));
        usage = usage.displayMonthlyUsageData();
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