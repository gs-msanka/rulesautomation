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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

public class Adoption_Instance_Weekly_Test extends BaseTest {
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    String resDir = userDir + "/resources/datagen/";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";
    String STATE_PRESERVATION_SCRIPT = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    String CUST_SET_DELETE = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";

    @BeforeClass
    public void setUp() throws InterruptedException, IOException {
        basepage.login();
        String measureFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile = env.basedir + "/testdata/sfdc/UsageData/Scripts/Instance_Level_Weekly.txt";

        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));

        //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        DataETL dataLoader = new DataETL();
        apex.runApexCodeFromFile(measureFile, isPackageInstance());
        apex.runApexCodeFromFile(advUsageConfigFile, isPackageInstance());
        dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
        dataLoader.cleanUp(resolveStrNameSpace(CUSTOMER_INFO), null);
        jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
        dataLoader.execute(jobInfo1);
        jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
        dataLoader.execute(jobInfo2);
        jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Instance_Weekly.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);

        BufferedReader reader;
        String fileName = env.basedir + "/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
        String line = null;
        String code = "";
        reader = new BufferedReader(new FileReader(fileName));
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        int year, month, day;
        String dateStr;
        //Max of only 5 jobs can run in an organization at a given time
        //Care to be taken that there are no apex jobs are running in the organization.
        int i = -7;
        for (int k = 0; k < 1; k++) {
            for (int m = 0; m < 5; m++, i = i - 7) {
                //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                dateStr = getWeekLabelDate("Wed", i, true, false);
                System.out.println(dateStr);
                year = (dateStr != null && dateStr.split("\\|").length > 0) ? Integer.valueOf(dateStr.split("\\|")[0]) : c.get(Calendar.YEAR);
                month = (dateStr != null && dateStr.split("\\|").length > 1) ? Integer.valueOf(dateStr.split("\\|")[1]) : c.get(Calendar.MONTH);
                day = (dateStr != null && dateStr.split("\\|").length > 2) ? Integer.valueOf(dateStr.split("\\|")[2]) : c.get(Calendar.DATE);
                code = stringBuilder.toString();
                code = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                        .replaceAll("THEYEARCHANGE", String.valueOf(year))
                        .replace("THEDAYCHANGE", String.valueOf(day));
                apex.runApex(resolveStrNameSpace(code));
            }
            for (int l = 0; l < 200; l++) {
                String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                        "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                        "and ApexClass.Name = 'AdoptionAggregation'";
                int noOfRunningJobs = getQueryRecordCount(query);
                if (noOfRunningJobs == 0) {
                    Report.logInfo("Aggregate Jobs are finished.");
                    isAggBatchsCompleted = true;
                    break;
                } else {
                    Report.logInfo("Waiting for aggregation batch to complete");
                    Thread.sleep(30000L);
                }
            }
        }
        
    }


    @Test
    public void Ins_WeeklyInsData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("1 Week");
        usage.setDataGranularity("Instance");
        usage.setDate(getWeekLabelDate("Wed", 0, true, true));
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        Assert.assertEquals(true, usage.isDataPresentInGrid("CARHLUC CARROCERIAS Y REMOLQUES SA DE CV|CARHLUC CARROCERIAS Y REMOLQUES SA DE CV - Instance 2|374|1,568|414|10,388|1,868|1,084|1,766|3,388|7,535"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("CARHLUC CARROCERIAS Y REMOLQUES SA DE CV|CARHLUC CARROCERIAS Y REMOLQUES SA DE CV - Instance 1|306|1,756|373|10,009|1,849|990|1,007|6,942|4,777"));
    }


    @Test
    public void Ins_WeeklyAccData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("1 Week");
        usage.setDataGranularity("Account");
        usage.setDate(getWeekLabelDate("Wed", -7, true, true));
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertEquals(true, usage.isDataPresentInGrid("DAKTEL COMUNICACIONES SA DE CV|226|1,093|2|20,968|2,118|916|5,077|2|11,786"));
        Assert.assertEquals(true, usage.isDataPresentInGrid("Baja Inc|369|1,546|2|14,002|2,162|912|3,728|2|7,830"));
    }

    @Test
    public void Ins_WeeklyFormDisplayed() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        Assert.assertTrue(usage.isWeeklyFormEleDisplayed(), "Checking if Monthly form is displayed");
        Assert.assertTrue(usage.isDataGranularitySelectionDisplayed(), "Checking instance level selection displayed");
    }

    @Test
    public void Ins_WeeklyInsExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("6 Weeks");
        usage.setDate(getWeekLabelDate("Wed", 0, true, true));
        usage.setDataGranularity("Instance");
        usage = usage.displayWeeklyUsageData();
        usage.selectUIView("Standard View");
        Assert.assertTrue(usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertTrue(usage.exportGrid(), "Checking grid export.");
    }

    @Test
    public void Ins_WeeklyAccExport() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnOverviewSubTab();
        usage.setMeasure("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        usage.setNoOfWeeks("12 Weeks");
        usage.setDate(getWeekLabelDate("Wed", -7, true, true));
        usage.setDataGranularity("Account");
        usage = usage.displayWeeklyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed(), "checking adoption grid is displayed");
        Assert.assertEquals(true, usage.exportGrid(), "Checking grid export.");
    }

    //This can be decoupled in to 2.
    @Test
    public void Ins_WeeklyAccAndInsUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("DeeTag USA");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", 0, true, true));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage.setForTimeWeekPeriod("26 Weeks");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDisplayed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Active Users|196.5|304|244|95|378.5"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("DB Size|1,240.5|1,517.5|1,957.5|1,378.5|1,801.5"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Emails Sent Count|2|2|2|2|2"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded|5,626|9,207|16,873|13,843|7,383"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Leads|1,697|1,895|1,975|1,658|1,714|"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Campaigns|965|998|762|842|748"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run|4,983.5|10,092|1,381.5|8,090|8,156"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views|2|2|2|2|2"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits|21,116|20,164|14,306|14,430|12,506|"));
        analyticsPage.setInstance("DeeTag USA - Instance 2");
        analyticsPage.setForTimeMonthPeriod("52 Months");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDisplayed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Active Users|349|442|39|326|267|445|14|211|485|313|2|17|16|148|261|39|221|18|357|112|364|468|33|92|392|267"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("DB Size|1,443|1,544|1,287|1,781|1,649|1,212|1,608|1,484|1,569|1,020|1,427|1,803|1,994|1,887|1,101|1,513|1,024|1,213|1,097|1,965|1,222|1,417|1,970|1,663|1,637|1,960"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Emails Sent Count|506|737|477|479|506|537|551|738|779|445|549|390|492|334|771|416|689|373|732|453|484|762|630|394|547|673"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded|5,694|8,196|3,234|9,279|1,248|1,872|7,755|596|573|2,724|11,292|715|3,077|4,315|7,463|4,145|8,513|7,052|6,099|9,859|4,851|1,064|9,779|7,557|5,790|9,931"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Leads|1,881|2,296|2,070|2,077|1,692|2,065|1,731|1,557|1,856|2,222|1,985|1,686|2,051|2,432|2,168|1,931|1,941|1,576|1,617|2,426|1,697|2,212|2,250|1,658|1,995|1,545"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Campaigns|653|1,046|883|673|979|1,085|860|611|785|600|856|943|954|805|760|743|1,003|925|1,056|756|965|998|671|713|748|616"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run|24|316|9,312|40|5,501|10,952|8,846|7,989|8,888|10,843|10,651|8,159|8,817|1,573|10,110|3,658|9,291|11,418|1,371|2,367|1,838|9,070|2,352|5,309|4,556|5,336"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views|11,393|5,166|4,584|10,707|11,054|1,981|9,159|5,424|1,123|8,257|4,063|247|8,858|10,506|4,802|1,323|7,404|11,655|2,718|2,642|4,742|10,310|2,020|2,279|11,805|1,455"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits|2,091|4,457|11,923|7,752|10,687|9,527|11,360|6,555|717|5,535|9,718|9,934|4,945|3,390|11,043|11,845|1,258|1,326|3,962|6,194|9,392|9,641|9,788|5,520|9,480|5,067"));
    }


    @Test
    public void Ins_WeeklyAccUsageGraph() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Vicor");
        analyticsPage.setForTimeWeekPeriod("26 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", 0, true, true));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Active Users|325|45|162.5|75.5|372"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("DB Size|1,361|1,653.5|1,622|1,596|1,559|"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Emails Sent Count|2|2|2|2|2"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Files Downloaded|4,054|11,903|9,190|9,221|19,632"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Leads|1,834|2,134|1,570|1,676|1,654"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Campaigns|815|707|1,099|683|766"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("No of Report Run|2,059|3,783|5,835|5,305.5|6,125"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Views|2|2|2|2|2"));
        Assert.assertTrue(analyticsPage.isDataPresentInGrid("Page Visits|9,056|8,310|12,744|9,519|7,807"));
    }

    @Test
    public void Ins_WeeklyAccUsageGraphMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Quince Hungary Kft");
        analyticsPage.setForTimeWeekPeriod("26 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", +28, true, true));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks"));
    }

    @Test
    public void Ins_WeeklyInsUsageGraphMissingInfo() {
        AdoptionAnalyticsPage analyticsPage = basepage.clickOnAdoptionTab().clickOnTrendsSubTab();
        analyticsPage.setCustomerName("Cadbury Beverages Div Cadbury");
        analyticsPage.setForTimeWeekPeriod("26 Weeks");
        analyticsPage.setWeekLabelDate(getWeekLabelDate("Wed", +7, true, true));
        analyticsPage.setMeasureNames("Active Users|DB Size|Emails Sent Count|Leads|No of Campaigns|Page Views|No of Report Run|Files Downloaded|Page Visits");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isGridDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks"));
        analyticsPage.setInstance("Cadbury Beverages Div Cadbury - Instance 1");
        analyticsPage = analyticsPage.displayCustWeeklyData();
        Assert.assertTrue(analyticsPage.isChartDisplayed());
        Assert.assertTrue(analyticsPage.isMissingDataInfoDisplayed("Missing data for some weeks"));
        Assert.assertTrue(analyticsPage.isGridDisplayed());
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}
