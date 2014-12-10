package com.gainsight.sfdc.adoption.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by gainsight on 08/12/14.
 */
public class AdoptionDataSetup extends BaseTest {
    public final String resDir                      = TestEnvironment.basedir + "/testdata/sfdc/UsageData/";
    private final String STATE_PRESERVATION_SCRIPT  = "DELETE [SELECT ID, Name FROM JBCXM__StatePreservation__c where name ='AdoptionTab'];";
    private final String CUST_SET_DELETE            = "JBCXM.ConfigBroker.resetActivityLogInfo('DataLoadUsage', null, true);";
    private final String measureFile                = resDir+"Scripts/Usage_Measure_Create.txt";
    private final String JOB_Account                = resDir + "Jobs/Job_Adop_Accounts.txt";
    private final String JOB_Customers              = resDir + "Jobs/Job_Adop_Customers.txt";

    ObjectMapper mapper = new ObjectMapper();
    JobInfo jobInfo;
    DataETL dataLoader;

    public AdoptionDataSetup() {
        sfinfo = SFDCUtil.fetchSFDCinfo();
        dataLoader = new DataETL();
        isPackage = isPackageInstance();
        userLocale = sfinfo.getUserLocale();
        userTimezone = TimeZone.getTimeZone(sfinfo.getUserTimeZone());
    }

    public void initialSetup() {
        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_SCRIPT));
        apex.runApex(resolveStrNameSpace(CUST_SET_DELETE));
        createExtIdFieldOnAccount();
        createFieldsOnUsageData();
        apex.runApexCodeFromFile(measureFile, isPackage);
        try {
            dataLoader.cleanUp(resolveStrNameSpace("Account"), "Name Like 'Adoption Test - Account%'");
        } catch (IOException e) {
            Report.logInfo(e.getLocalizedMessage());
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
            Report.logInfo(e.getLocalizedMessage());
            throw new RuntimeException("Failed to load Accounts, Customer for usage data");
        }
    }

    public void loadUsageData(String jobFileName) {
        try {
            jobInfo = mapper.readValue(resolveNameSpace(jobFileName), JobInfo.class);
            dataLoader.execute(jobInfo);
        } catch (IOException e){
            e.printStackTrace();
            Report.logInfo(e.getLocalizedMessage());
            throw new RuntimeException("Failed to Usage Data");
        }

    }

    /**
     * To trigger adoption aggregation.
     * @param isWeekly - true - Runs weekly aggregation, else - Runs Monthly aggregation.
     * @param isStartDayOfWeek - Week label is based on start of week or end of week.
     * @param weekStartsOn - Week starts on Sun, Mon, Tue
     * @param noOfPeriods - No of weeks/months to run aggregation.  {Good to send multiples of 5}
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    public void runAdoptionAggregation(int noOfPeriods, Boolean isWeekly, boolean isStartDayOfWeek, String weekStartsOn) {
        try {
            Calendar cal = Calendar.getInstance(userTimezone);
            BufferedReader reader;
            String fileName = TestEnvironment.basedir + "/testdata/sfdc/UsageData/Scripts/Aggregation_Script.txt";
            String line = null;
            String code = "";
            reader = new BufferedReader(new FileReader(fileName));
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            String year = "", month = "", day = "15";
            String dateStr;
            int noOfTimesToLoop = (Integer.valueOf(noOfPeriods%5) ==0)  ? noOfPeriods/5 : noOfPeriods/5+1;

            if(isWeekly) {
                int i = -7;
                for (int k = 0; k < noOfTimesToLoop; k++) {
                    for (int m = 0; m < 5; m++, i = i - 7) {
                        //if the start day of the week configuration is changed then method parameter should be changed appropriately..
                        // Sun, Mon, Tue, Wed, Thu, Fri, Sat.
                        dateStr = getWeekLabelDate(weekStartsOn, i, isStartDayOfWeek, false);
                        year = (dateStr != null && dateStr.split("-").length > 0) ? String.valueOf(dateStr.split("-")[0]) : String.valueOf(cal.get(Calendar.YEAR));
                        month = (dateStr != null && dateStr.split("-").length > 1) ? String.valueOf(dateStr.split("-")[1]) : String.valueOf(cal.get(Calendar.MONTH));
                        day = (dateStr != null && dateStr.split("-").length > 2) ? String.valueOf(dateStr.split("-")[2]) : String.valueOf(cal.get(Calendar.DATE));
                        code = stringBuilder.toString();
                        code = code.replaceAll("THEMONTHCHANGE", month).replaceAll("THEYEARCHANGE", year).replace("THEDAYCHANGE", day);
                        Report.logInfo("Running Aggregation On : " + year + "-" + month + "-" + day);
                        apex.runApex(resolveStrNameSpace(code));
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
                        Report.logInfo("Running Aggregation On : " +year+"-"+month+"-"+day);
                        apex.runApex(resolveStrNameSpace(code));
                        cal.add(Calendar.MONTH, -1);
                    }
                    Thread.sleep(15000L);
                    waitForBatchExecutionToComplete("AdoptionAggregation");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Report.logInfo(e.getLocalizedMessage());
            throw new RuntimeException("Failed to run aggregation");
        }
    }

    public void updateUtilizationCal(boolean toMeasure) {
        String apexCode = "JBCXM__ApplicationSettings__c appSettings = [SELECT ID, JBCXM__AdoptionAggregationType__c, JBCXM__UsageUtilizationCalc__c, JBCXM__AdoptionGranularity__c,\n" +
                "JBCXM__AdoptionAggregationColumns__c, JBCXM__AdoptionMeasureColMap__c, JBCXM__WeekStartsOn__c,\n" +
                "JBCXM__UsesEndDateAsWeekName__c from JBCXM__ApplicationSettings__c LIMIT 1];\n";
        apexCode += toMeasure ? "appSettings.JBCXM__UsageUtilizationCalc__c = 'MEASURE';" : "appSettings.JBCXM__UsageUtilizationCalc__c = 'STANDARD';\n";
        apexCode += "update appSettings;";
        apex.runApex(apexCode, isPackage);
    }

    public void updateUsersDisplayInUsageGrids(boolean display) {
        String apexCode = "JBCXM__ApplicationSettings__c appSettings = [SELECT ID, JBCXM__AdoptionAggregationType__c, JBCXM__UsageUtilizationCalc__c, JBCXM__AdoptionGranularity__c,\n" +
                "JBCXM__AdoptionAggregationColumns__c, JBCXM__AdoptionMeasureColMap__c, JBCXM__WeekStartsOn__c,\n" +
                "JBCXM__UsesEndDateAsWeekName__c, JBCXM__LicensedUserNotInAdoptionGrid__c from JBCXM__ApplicationSettings__c LIMIT 1];\n" +
                "appSettings.JBCXM__LicensedUserNotInAdoptionGrid__c = "+!(display)+"\n;" +
                "update appSettings;";
        apex.runApex(apexCode, isPackage);
    }
}
