package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 28/11/13
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Adoption_User_Monthly_Test extends BaseTest {
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Usage_Measure_Create.txt";
        String advUsageConfigFile  = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/User_Level_Monthly.txt";
        try{
            apex.runApexCodeFromFile(measureFile);
            apex.runApexCodeFromFile(advUsageConfigFile);
            /**
             * Data Should be loaded here.
             */
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
            for(int i =0; i < 5; i++) {
                if(month == 0) {
                    month = 12;
                    year = year -1;
                }
                code = stringBuilder.toString();
                code = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                        .replaceAll("THEYEARCHANGE", String.valueOf(year))
                        .replace("THEDAYCHANGE", String.valueOf(day));
                apex.runApex(code);
                month = month-1; //Need to move backward for executing the aggregation.
            }
            reader.close();
            for(int i= 0; i < 30; i++) {
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
        } catch (Exception e) {
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }





    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}