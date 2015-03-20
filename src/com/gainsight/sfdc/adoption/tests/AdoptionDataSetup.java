package com.gainsight.sfdc.adoption.tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Log;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;

/**
 * Created by gainsight on 08/12/14.
 */
public class AdoptionDataSetup extends BaseTest {
    public final String resDir                      = env.basedir + "/testdata/sfdc/usageData/";
    private final String STATE_PRESERVATION_SCRIPT  = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    private final String CUST_SET_DELETE            = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";
    private final String MEASURES_FILE                = resDir+"scripts/Usage_Measure_Create.txt";
    private final String JOB_Account                = resDir + "jobs/Job_Adop_Accounts.txt";
    private final String JOB_Customers              = resDir + "jobs/Job_Adop_Customers.txt";

    ObjectMapper mapper = new ObjectMapper();
    JobInfo jobInfo;
    DataETL dataLoader;

    public AdoptionDataSetup() {
        dataLoader = new DataETL();
    }

    public void initialSetup() {
        sfdc.runApexCode(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        sfdc.runApexCode(resolveStrNameSpace(CUST_SET_DELETE));
        try {
          //  createExtIdFieldOnAccount();
           // createFieldsOnUsageData();
            sfdc.runApexCode(resolveStrNameSpace(FileUtil.getFileContents(MEASURES_FILE)));
            dataLoader.cleanUp(resolveStrNameSpace("Account"), "Name Like 'Adoption Test - Account%'");
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed to delete accounts related to adoption data");
        }
    }

    public void loadUsageAccountAndCustomersData() {
        try {
            jobInfo = mapper.readValue(resolveNameSpace(JOB_Account), JobInfo.class);
            dataLoader.execute(jobInfo);
            jobInfo = mapper.readValue(resolveNameSpace(JOB_Customers), JobInfo.class);
            dataLoader.execute(jobInfo);
        } catch (IOException e) {
            e.printStackTrace();
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException("Failed to load Accounts, Customer for usage data");
        }
    }

    public void loadUsageData(String jobFileName) {
        try {
            jobInfo = mapper.readValue(resolveNameSpace(jobFileName), JobInfo.class);
            dataLoader.execute(jobInfo);
        } catch (IOException e){
            e.printStackTrace();
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException("Failed to Usage Data");
        }

    }

    /**
     * To trigger adoption aggregation.
     * @param isWeekly - true - Runs weekly aggregation, else - Runs Monthly aggregation.
     * @param usesEndDate - Week label is based on start of week or end of week.
     * @param weekStartsOn - Week starts on Sun, Mon, Tue
     * @param noOfPeriods - No of weeks/months to run aggregation.  {Good to send multiples of 5}
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    public void runAdoptionAggregation(int noOfPeriods, Boolean isWeekly, boolean usesEndDate, String weekStartsOn) {
        try {
            Calendar cal = Calendar.getInstance(userTimezone);
            BufferedReader reader;
            String fileName = env.basedir + "/testdata/sfdc/usageData/scripts/Aggregation_Script.txt";
            String line = null;
            String code = "";
            reader = new BufferedReader(new FileReader(fileName));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            String year = "", month = "", day = "15";
            int noOfTimesToLoop = (Integer.valueOf(noOfPeriods%5) ==0)  ? noOfPeriods/5 : noOfPeriods/5+1;

            if(isWeekly) {
                int i = 0;
                for (int k = 0; k < noOfTimesToLoop; k++) {
                    for (int m = 0; m < 5; m++, i = i - 7) {
                        //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                        // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                        Calendar ca = DateUtil.getWeekLabelDate(weekStartsOn, userTimezone, i, usesEndDate);
                        year = String.valueOf(ca.get(Calendar.YEAR));
                        month = String.valueOf((ca.get(Calendar.MONTH)+1));
                        day = String.valueOf(ca.get(Calendar.DATE));
                        code = stringBuilder.toString();
                        code = code.replaceAll("THEMONTHCHANGE", month).replaceAll("THEYEARCHANGE", year).replace("THEDAYCHANGE", day);
                        Log.info("Running Aggregation On : " + year + "-" + month + "-" + day);
                        sfdc.runApexCode(resolveStrNameSpace(code));
                    }
                    Thread.sleep(15000L);
                    waitForBatchExecutionToComplete("AdoptionAggregation");
                }
            } else {
                for (int k = 0; k < noOfTimesToLoop; k++) {
                    for (int i = 0; i < 5; i++) {
                        month = String.valueOf(cal.get(Calendar.MONTH)+1);    //Added one as java return 0 for January month.
                        year = String.valueOf(cal.get(Calendar.YEAR));
                        code = stringBuilder.toString();
                        code = code.replaceAll("THEMONTHCHANGE", month).replaceAll("THEYEARCHANGE", year).replace("THEDAYCHANGE", day);
                        Log.info("Running Aggregation On : " +year+"-"+month+"-"+day);
                        sfdc.runApexCode(resolveStrNameSpace(code));
                        cal.add(Calendar.MONTH, -1);
                    }
                    Thread.sleep(15000L);
                    waitForBatchExecutionToComplete("AdoptionAggregation");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException("Failed to run aggregation");
        }
    }

    public void updateUtilizationCal(String val) {
        String apexCode = "JBCXM__ApplicationSettings__c appSettings = [SELECT ID, JBCXM__AdoptionAggregationType__c, JBCXM__UsageUtilizationCalc__c, JBCXM__AdoptionGranularity__c,\n" +
                "JBCXM__AdoptionAggregationColumns__c, JBCXM__AdoptionMeasureColMap__c, JBCXM__WeekStartsOn__c,\n" +
                "JBCXM__UsesEndDateAsWeekName__c from JBCXM__ApplicationSettings__c LIMIT 1];\n";
        apexCode += (val != null) ? "appSettings.JBCXM__UsageUtilizationCalc__c = '"+val+"';" : "appSettings.JBCXM__UsageUtilizationCalc__c = null;";
        apexCode += "update appSettings;";
        sfdc.runApexCode(resolveStrNameSpace(apexCode));
    }

    public void updateUsersDisplayInUsageGrids(boolean display) {
        String apexCode = "JBCXM__ApplicationSettings__c appSettings = [SELECT ID, JBCXM__AdoptionAggregationType__c, JBCXM__UsageUtilizationCalc__c, JBCXM__AdoptionGranularity__c,\n" +
                "JBCXM__AdoptionAggregationColumns__c, JBCXM__AdoptionMeasureColMap__c, JBCXM__WeekStartsOn__c,\n" +
                "JBCXM__UsesEndDateAsWeekName__c, JBCXM__LicensedUserNotInAdoptionGrid__c from JBCXM__ApplicationSettings__c LIMIT 1];\n" +
                "appSettings.JBCXM__LicensedUserNotInAdoptionGrid__c = "+!(display)+"\n;" +
                "update appSettings;";
        sfdc.runApexCode(resolveStrNameSpace(apexCode));
    }
}
