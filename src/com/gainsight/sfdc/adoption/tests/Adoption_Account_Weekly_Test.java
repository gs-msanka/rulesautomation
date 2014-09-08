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
import java.io.IOException;
import java.util.TimeZone;

public class Adoption_Account_Weekly_Test extends BaseTest {
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String resDir = userDir + "/resources/datagen/";
    String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    String CUST_SET_DELETE = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";

    @BeforeClass
    public void setUp() throws IOException {
        basepage.login();
        userLocale = soql.getUserLocale();
        isPackage = isPackageInstance();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
        String measureFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Account_Level_Weekly.txt";

        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        Report.logInfo("Field Creation Done");
        apex.runApexCodeFromFile(measureFile, isPackage);
        apex.runApexCodeFromFile(advUsageConfigFile, isPackage);
        DataETL dataLoader = new DataETL();
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        dataLoader.cleanUp(resolveStrNameSpace(CUSTOMER_INFO), null);
        jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
        dataLoader.execute(jobInfo1);
        jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
        dataLoader.execute(jobInfo2);
        jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Account_Weekly.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);
    }

    @Test
    public void Acc_WeeklyAllMeasures() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("1 Week");
        usage.setDate(getWeekLabelDate("Sat", -7, true, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("Alltech Automotive LLC | 51|1,626|450|9,557|1,693|943|11,792|4,216|1,209"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("BOESDORFER & BOESDORFER INC |421|1,821|621|2,555|1,721|894|2,754|9,839|9,680"));
    }

    @Test
    public void Acc_WeeklyFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if weekly form is displayed");
    }

    @Test
    public void Acc__WeeklyGridExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDate(getWeekLabelDate("Sat", -7, true, true));
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertEquals(true, usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Acc_WeeklyDataInTrends() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage.setForTimeWeekPeriod("26 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Sat", -7, true, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Active Users|392|19|332|336|269|283|158|418|185|335|223|480|466|403|444|340|333|70|259|497|154|364|231|210|390|216"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("DB Size|1,454|1,093|1,287|1,868|1,754|1,109|1,411|1,640|1,715|1,126|1,419|1,364|1,550|1,909|1,370|1,831|1,181|1,840|1,611|1,484|1,635|1,711|1,363|1,953|1,352|1,288"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Emails Sent Count|701|357|579|539|367|691|368|590|301|335|691|485|489|420|774|517|527|311|646|627|389|739|461|554|516|742"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded|5,553|430|12,581|12,884|12,071|5,422|247|8,158|2,630|1,153|3,746|14,684|8,562|7,152|515|11,890|8,282|2,807|11,978|8,506|13,538|8,515|13,310|8,724|4,175|4,317"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Leads|1,977|1,944|2,046|2,363|2,108|2,330|2,443|2,092|1,868|2,134|2,365|2,269|1,500|1,628|1,684|2,233|2,292|1,574|2,196|1,682|1,667|2,449|2,473|1,916|1,578|2,075"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Campaigns|604|607|720|863|841|675|812|938|722|660|692|750|745|1,068|916|969|828|609|890|1,079|704|706|854|703|924|756"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run|10,659|4,531|5,746|5,891|3,871|4,752|7,602|14,319|194|10,499|2,351|10,944|11,556|6,112|904|11,255|3,653|706|3,836|8,113|1,732|4,177|7,782|10,997|8,800|2,636"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views|4,703|7,139|11,739|10,165|10,248|6,974|1,044|8,497|9,163|2,300|9,104|3,734|14,224|14,374|7,684|5,542|12,500|6,414|1,436|7,950|6,782|1,794|372|7,992|1,368|745"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits|9,523|8,331|2,657|3,867|13,655|4,588|9,447|10,133|8,813|8,549|10,992|10,911|6,984|12,726|602|1,886|10,070|5,292|9,585|3,283|1,227|4,867|8,996|8,006|5,093|738"));
    }

    @Test
    public void Acc_WeeklyDataInTrendsPartial() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage.setForTimeWeekPeriod("52 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Sat", 14, true, true));
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks"));
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}