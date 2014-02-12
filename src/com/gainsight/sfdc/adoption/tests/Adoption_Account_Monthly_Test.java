package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.utils.ApexUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class Adoption_Account_Monthly_Test extends BaseTest {

    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;

    ObjectMapper mapper = new ObjectMapper();
    static String resDir = "./resources/datagen/";
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";


    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/Account_Level_Monthly.txt";

        try {
            //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
            DataETL dataLoader = new DataETL();
            dataLoader.cleanUp(isPackageInstance() ? USAGE_NAME : removeNameSpace(USAGE_NAME), null);
            dataLoader.cleanUp(isPackageInstance() ? CUSTOMER_INFO : removeNameSpace(CUSTOMER_INFO), null);
            jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
            dataLoader.execute(jobInfo1);
            jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
            dataLoader.execute(jobInfo2);
            jobInfo3 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Account_Monthly.txt"), JobInfo.class);
            apex.runApexCodeFromFile(measureFile);
            apex.runApexCodeFromFile(advUsageConfigFile);
            dataLoader.execute(jobInfo3);
        } catch (Exception e) {
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void usageDataVerification() {
        AdoptionUsagePage adopGridPage = basepage.clickOnAdoptionTab().clickOnUsageGridSubTab();
        adopGridPage.refreshPage();
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}