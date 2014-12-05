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

public class Adoption_Instance_Weekly_Test extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    private final String USAGE_NAME         = "JBCXM__UsageData__c";
    private final String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    private final String CUST_SET_DELETE        = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";
    private final String resDir                 = TestEnvironment.basedir + "/testdata/sfdc/UsageData/";
    private final String measureFile            = resDir+"Scripts/Usage_Measure_Create.txt";
    private final String advUsageConfigFile     = resDir+"Scripts/Instance_Level_Weekly.txt";
    private final String JOB_Account            = resDir + "Jobs/Job_Adop_Accounts.txt";
    private final String JOB_Customers          = resDir + "Jobs/Job_Adop_Customers.txt";
    private final String JOB_UsageData          = resDir + "Jobs/Job_Adop_Inst_Weekly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/UsageData/Tests/Adop_Inst_Weekly_Test.xls";

    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        isPackage = isPackageInstance();
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());

        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));
        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(measureFile, isPackage);
        apex.runApexCodeFromFile(advUsageConfigFile, isPackage);
        DataETL dataLoader = new DataETL();
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        dataLoader.cleanUp(resolveStrNameSpace("Account"), "Name Like 'Adoption Test - Account%'");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(JOB_Account), JobInfo.class);
        dataLoader.execute(jobInfo);
        jobInfo = mapper.readValue(resolveNameSpace(JOB_Customers), JobInfo.class);
        dataLoader.execute(jobInfo);
        jobInfo = mapper.readValue(resolveNameSpace(JOB_UsageData), JobInfo.class);
        dataLoader.execute(jobInfo);
        runAdoptionAggregation(10, true, true, "Wed");
        //usage.setDate(getWeekLabelDate("Wed", -7, true, true));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_InstWeekly1Measure1Period_ByInstance(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void T2_InstWeekly1Measure1Period_ByAccount(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T3_InstWeeklyMultipleMeasure1Period_ByInstance(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T4_InstWeeklyMultipleMeasure1Period_ByAccount(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T5_InstWeekly_VerifyUtilization_ByInstance(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T6_InstWeekly_VerifyUtilization_ByAccount(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayWeeklyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T7_InstWeeklyTrend_MultipleMeasures_AccountAndInstance(Map<String, String> testData) {

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T8_InstWeeklyTrend_SingleMeasures_AccountAndInstance(Map<String, String> testData) {

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T9_InstWeeklyTrend_AccountMissingInfoMsg(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if Weekly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T10_InstWeeklyTrend_InstanceMissingInfoMsg(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if Weekly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T11_InstWeekly_Export_ByAccount(Map<String, String> testData) {


    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T12_InstWeekly_Export_ByInstance(Map<String, String> testData) {


    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T13_InstWeekly_FormDisplayed(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if Weekly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T14_InstMonthlySingleMeasureMultiplePeriods_ByInstance(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T15_InstMonthlySingleMeasureMultiplePeriods_ByAccount(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();

    }


    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}
