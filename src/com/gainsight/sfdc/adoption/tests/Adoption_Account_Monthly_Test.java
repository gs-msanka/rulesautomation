package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.ApexUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;

public class Adoption_Account_Monthly_Test extends BaseTest {
    Calendar c                      = Calendar.getInstance();
    Boolean isAggBatchsCompleted    = false;
    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Usage_Measure_Create.txt";
        String advUsageConfigFile  = System.getProperty("user.dir")+"/testdata/sfdc/UsageData/Account_Level_Monthly.txt";
        try{
            apex.runApexCodeFromFile(measureFile);
            apex.runApexCodeFromFile(advUsageConfigFile);
            /**
             *
             *
             *
             * Data Should be loaded here.
             */
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