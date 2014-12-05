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

import java.io.IOException;
import java.util.Map;
import java.util.TimeZone;

public class Adoption_Instance_Monthly_Test extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    private final String USAGE_OBJECT = "JBCXM__UsageData__c";

    private final String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    private final String CUSTOM_SETTINGS_SCRIPT = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";
    private final String resDir                 = TestEnvironment.basedir + "/testdata/sfdc/UsageData/";
    private final String measureFile            = resDir+"Scripts/Usage_Measure_Create.txt";
    private final String advUsageConfigFile     = resDir+"Scripts/Instance_Level_Monthly.txt";
    private final String JOB_Account            = resDir + "Jobs/Job_Adop_Accounts.txt";
    private final String JOB_Customers          = resDir + "Jobs/Job_Adop_Customers.txt";
    private final String JOB_UsageData          = resDir + "Jobs/Job_Adop_Inst_Monthly.txt";
    private final String TEST_DATA_FILE         = "testdata/sfdc/UsageData/Tests/Adop_Inst_Monthly_Test.xls";

    @BeforeClass
    public void setUp() throws IOException, InterruptedException {
        basepage.login();
        isPackage = isPackageInstance();
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());

        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUSTOM_SETTINGS_SCRIPT));
        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(measureFile, isPackage);
        //This is to set the Standard Gainsight Utilization Percentage Calculation.
        apex.runApex(getFileContents(advUsageConfigFile).replace("USAGE_PERCENTAGE_CALCULATION", "STANDARD"), isPackage);
        DataETL dataLoader = new DataETL();
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_OBJECT), null);
        dataLoader.cleanUp(resolveStrNameSpace("Account"), "Name Like 'Adoption Test - Account%'");

        JobInfo jobInfo = mapper.readValue(resolveNameSpace(JOB_Account), JobInfo.class);
        dataLoader.execute(jobInfo);
        jobInfo = mapper.readValue(resolveNameSpace(JOB_Customers), JobInfo.class);
        dataLoader.execute(jobInfo);
        jobInfo = mapper.readValue(resolveNameSpace(JOB_UsageData), JobInfo.class);
        dataLoader.execute(jobInfo);
        runAdoptionAggregation(10, false, false, null);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void T1_InstMonthly1Measure1Period_ByInstance(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void T2_InstMonthly1Measure1Period_ByAccount(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T3_InstMonthlyMultipleMeasure1Period_ByInstance(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
        usage.setMeasure(testData.get("Measures"));
        usage.setNoOfMonths(testData.get("Period"));
        String[] monthAndYear = getMonthAndYear(Integer.valueOf(testData.get("Date")));
        usage.setMonth(monthMap.get(monthAndYear[0]));
        usage.setYear(String.valueOf(monthAndYear[1]));
        usage = usage.displayMonthlyUsageData();
        Assert.assertTrue(usage.isAdoptionGridDisplayed());
        Assert.assertTrue(usage.isDataPresentInGrid(testData.get("UD_Data1")));
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


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T4_InstMonthlyMultipleMeasure1Period_ByAccount(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T5_InstMonthly_VerifyUtilization_ByInstance(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.selectUIView(testData.get("UI_View"));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T6_InstMonthly_VerifyUtilization_ByAccount(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T7_InstMonthlyTrend_MultipleMeasures_AccountAndInstance(Map<String, String> testData) {

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T8_InstMonthlyTrend_SingleMeasures_AccountAndInstance(Map<String, String> testData) {

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T9_InstMonthlyTrend_AccountMissingInfoMsg(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isMonthlyFormEleDisplayed(), "Checking if Monthly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T10_InstMonthlyTrend_InstanceMissingInfoMsg(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isMonthlyFormEleDisplayed(), "Checking if Monthly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T11_InstMonthly_Export_ByAccount(Map<String, String> testData) {


    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T12_InstMonthly_Export_ByInstance(Map<String, String> testData) {


    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void T13_InstMonthly_FormDisplayed(Map<String, String> testData) {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isMonthlyFormEleDisplayed(), "Checking if Monthly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}