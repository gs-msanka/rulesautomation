package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
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

public class Adoption_User_Monthly_Test extends BaseTest {
    Calendar c = Calendar.getInstance();
    Boolean isAggBatchsCompleted = false;
    String USAGE_NAME = "JBCXM__UsageData__c";
    String CUSTOMER_INFO = "JBCXM__CustomerInfo__c";
    static ObjectMapper mapper = new ObjectMapper();
    static String resDir = "./resources/datagen/";
    String OBJECT_NAME = "JBCXM__UsageData__c";
    static JobInfo jobInfo1;
    static JobInfo jobInfo2;
    static JobInfo jobInfo3;
    String CONDITION = "WHERE JBCXM__Account__r.Jigsaw = 'AUTO_SAMPLE_DATA'";


    @BeforeClass
    public void setUp() {
        basepage.login();
        String measureFile          = env.basedir+"/testdata/sfdc/UsageData/Scripts/Usage_Measure_Create.txt";
        String advUsageConfigFile   = env.basedir+"/testdata/sfdc/UsageData/Scripts/User_Level_Monthly.txt";
        try{
            //Measure's Creation, Advanced Usage Data Configuration, Adoption data load part will be carried here.
            createFieldsOnUsageData();
            DataETL dataLoader = new DataETL();
            dataLoader.cleanUp(isPackageInstance() ? USAGE_NAME : removeNameSpace(USAGE_NAME), null);
            dataLoader.cleanUp(isPackageInstance() ? CUSTOMER_INFO : removeNameSpace(CUSTOMER_INFO), null);
            jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Accounts.txt"), JobInfo.class);
            dataLoader.execute(jobInfo1);
            jobInfo2 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Customers.txt"), JobInfo.class);
            dataLoader.execute(jobInfo2);
            dataLoader.cleanUp(isPackageInstance() ? OBJECT_NAME : removeNameSpace(OBJECT_NAME), null);
            jobInfo3 = mapper.readValue(new FileReader(resDir + "jobs/Job_User_Monthly.txt"), JobInfo.class);
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
                    apex.runApex(code);
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
    public void usageDataVerfication() {
        Report.logInfo("This is a sample test case");
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}