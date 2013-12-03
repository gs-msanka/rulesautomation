package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 28/11/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Adoption_User_Weekly_Test extends BaseTest {
    Calendar c                      = Calendar.getInstance();
    Boolean isAggBatchsCompleted    = false;

    /**
     *
     */
    @BeforeClass
    public void setUp() {
        String measureFile          = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Usage_Measure_Create.txt";
        String advUsageConfigFile   = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/User_Level_Weekly.txt";
        basepage.login();
        try{
            //delete of data
            apex.runApexCodeFromFile(measureFile);
            apex.runApexCodeFromFile(advUsageConfigFile);
            /**
             * Data Should be loaded here.
             */
            BufferedReader reader;
            String fileName = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
            String line     = null;
            String code     = "";
            reader          = new BufferedReader(new FileReader(fileName));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            int year, month, day;
            String dateStr;
            //Max of only 5 jobs can run in an organization at a given time
            //Care to be taken that there are no apex jobs are running in the organization.
            int i= -7;
            for(int k = 0; k< 11;k++) {
                for(int m=0; m < 5; m++, i=i-7) {
                    //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                    // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                    dateStr     = getWeekLabelDate("Sun", i);
                    System.out.println(dateStr);
                    year        = (dateStr != null && dateStr.split("\\|").length > 0) ? Integer.valueOf(dateStr.split("\\|")[0]) : c.get(Calendar.YEAR);
                    month       = (dateStr != null && dateStr.split("\\|").length > 1) ? Integer.valueOf(dateStr.split("\\|")[1]) : c.get(Calendar.MONTH);
                    day         = (dateStr != null && dateStr.split("\\|").length > 2) ? Integer.valueOf(dateStr.split("\\|")[2]) : c.get(Calendar.DATE);
                    code        = stringBuilder.toString();
                    code        = code.replaceAll("THEMONTHCHANGE", String.valueOf(month))
                            .replaceAll("THEYEARCHANGE", String.valueOf(year))
                            .replace("THEDAYCHANGE", String.valueOf(day));
                    apex.runApex(code);
                }

                for(int l= 0; l < 30; l++) {
                    String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                            "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                            "and ApexClass.Name = 'AdoptionAggregation'";
                    int noOfRunningJobs = getQueryRecordCount(query);
                    if(noOfRunningJobs==0) {
                        Report.logInfo("Aggregate Jobs are finished.");
                        isAggBatchsCompleted = true;
                        break;
                    } else {
                        Report.logInfo("Waiting for aggregation batch to complete");
                        Thread.sleep(30000L);
                    }
                }
            }

        } catch (Exception e) {
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * This parameter returns the String with comprises of yyyy|mm|dd format.
     * @param weekStartDay - Expected values Sun, Mon, Tue, Wed, Thu, Fri, Sat.
     * @param daysToAdd - number of days to add for current day.
     * @return String of format "yyyy|mm|dd".
     */
    public String getWeekLabelDate(String weekStartDay, int daysToAdd) {
        String date= null;
        try {
            Calendar c = Calendar.getInstance();
            Map<String,Integer> days = new HashMap<String, Integer>();
            days.put("Sun", 1);
            days.put("Mon", 2);
            days.put("Tue", 3);
            days.put("Wed", 4);
            days.put("Thu", 5);
            days.put("Fri", 6);
            days.put("Sat", 7);
            c.set(Calendar.DAY_OF_WEEK, days.get(weekStartDay));
            System.out.println(c.get(Calendar.DATE));
            c.add(Calendar.DATE, daysToAdd);
            int day = c.get(Calendar.DATE);
            int month = c.get(Calendar.MONTH);
            month += 1;
            int year = c.get(Calendar.YEAR);
            date = year + "|" + month + "|"+day;
        } catch (NullPointerException e) {
            Report.logInfo(e.getLocalizedMessage());
        }
        return date;
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}