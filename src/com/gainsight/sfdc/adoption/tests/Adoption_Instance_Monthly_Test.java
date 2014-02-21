package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 22/11/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Adoption_Instance_Monthly_Test extends BaseTest {
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    static String resDir = "./resources/datagen/";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";



    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/Instance_Level_Monthly.txt";

        try{
            //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
            createFieldsOnUsageData();
            DataETL dataLoader = new DataETL();
            apex.runApexCodeFromFile(measureFile, isPackageInstance());
            apex.runApexCodeFromFile(advUsageConfigFile,isPackageInstance());
            dataLoader.cleanUp(resolveStrNameSpace(USAGE_NAME), null);
            dataLoader.cleanUp(resolveStrNameSpace(CUSTOMER_INFO), null);
            jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
            dataLoader.execute(jobInfo1);
            jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
            dataLoader.execute(jobInfo2);
            jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_Instance_Monthly.txt"), JobInfo.class);
            apex.runApexCodeFromFile(measureFile);
            apex.runApexCodeFromFile(advUsageConfigFile);
            dataLoader.execute(jobInfo3);

            String fileName = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String code = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            int day = 15;
            //Max of only 5 jobs can run in an organization at a given time
            //Care to be taken that there are no apex jobs are running in the organization.
            for(int k=0;k<5;k++) {
                for(int i =0; i < 5; i++) {
                    if(month == 0) {
                        month = 12;
                        year = year -1;
                    }
                    code = stringBuilder.toString();
                    code = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                            .replaceAll("THEYEARCHANGE", String.valueOf(year))
                            .replace("THEDAYCHANGE", String.valueOf(day));
                    apex.runApex(resolveStrNameSpace(code));
                    month = month-1; //Need to move backward for executing the aggregation.
                }
                reader.close();
                Thread.sleep(30000L);
                for(int i= 0; i < 200; i++) {
                    String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                            "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                            "and ApexClass.Name = 'AdoptionAggregation'";
                    int noOfRunningJobs = getQueryRecordCount(query);
                    if(noOfRunningJobs==0) {
                        Report.logInfo("Aggregate Jobs are finished.");
                        isAggBatchsCompleted = true;
                        break;
                    } else {
                        Report.logInfo("Waiting");
                        Thread.sleep(30000L);
                    }
                }
            }
        } catch (Exception e) {
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void viewMonthlyInstanceData() {
        Report.logInfo("Executing the sample test case.");
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        /*usage.setMeasure("Page Views");
        String mon = String.valueOf(c.get(Calendar.MONTH)+1);
        String year = String.valueOf(c.get(Calendar.YEAR));
        usage.setMonth("Mar");
        usage.setByDataGran("By Instance");
        usage.setYear(year);
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows weather instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Instance | Renewal Date"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("East Bay Brass Foundry | Production | 01/01/2010 | 40 | 0 | 0% | 8,597 | 2 | 2,673 | 87 "));*/
    }

    /*@Test
    public void viewMonthlyAccountData() {
        AdoptionUsagePage usage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        usage.setMeasure("Page Visits");
        String mon = String.valueOf(c.get(Calendar.MONTH)+1);
        String year = String.valueOf(c.get(Calendar.YEAR));
        usage.setMonth("Jan");
        usage.setByDataGran("By Account");
        usage.setYear(year);
        usage = usage.displayMonthlyUsageData();
        Assert.assertEquals(true, usage.isAdoptionGridDisplayed());
        //Checking the header rows wether instance is displayed in the header.
        Assert.assertEquals(true, usage.isGridHeaderMapped("Customer | Renewal Date | Licensed | YTD Unique"));
        //Checking the adoption data for a customer instance.
        Assert.assertEquals(true, usage.isDataPresentInGrid("CONDUCTORES Y DERIVADOS ROSS SA DE CV | 01/01/2010 | 0 |" +
                                                                " 0 | 0% | 1,859,127 | 923,762 | 101,951 | 45,329 | 1,256,028 "));
    }*/


    @AfterClass
    public void tearDown(){
        Report.logInfo("Adoption_Account_Monthly_Test  End Time : " +c.getTime());
        basepage.logout();
    }
}