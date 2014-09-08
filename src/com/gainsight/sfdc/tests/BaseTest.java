package com.gainsight.sfdc.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.helpers.AmountsAndDatesUtil;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import com.gainsight.utils.ApexUtil;
import com.gainsight.utils.SOQLUtil;
import com.gainsight.utils.TestDataHolder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BaseTest {
    protected TestDataHolder testDataLoader = new TestDataHolder();
    String[] dirs = {"testdata", "sfdc"};
    protected TestEnvironment env = new TestEnvironment();
    public final String TEST_DATA_PATH_PREFIX = TestEnvironment.basedir + "/"
            + generatePath(dirs);
    public static SOQLUtil soql = new SOQLUtil();
    public ApexUtil apex = new ApexUtil();
    protected static BasePage basepage;
    public static String userLocale;
    public static TimeZone userTimezone;
    public String userDir = TestEnvironment.basedir;
    public Boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    public AmountsAndDatesUtil adUtil = new AmountsAndDatesUtil();
    Calendar c = Calendar.getInstance();
    public static final Map<String, String> monthMap;
    static
    {
        monthMap = new HashMap<String, String>();
        monthMap.put("0", "Jan");
        monthMap.put("1", "Feb");
        monthMap.put("2", "Mar");
        monthMap.put("3", "Apr");
        monthMap.put("4", "May");
        monthMap.put("5", "Jun");
        monthMap.put("6", "Jul");
        monthMap.put("7", "Aug");
        monthMap.put("8", "Sep");
        monthMap.put("9", "Oct");
        monthMap.put("10", "Nov");
        monthMap.put("11", "Dec");
    }

    @BeforeSuite
    public void init() throws Exception {
        Report.logInfo("Initializing Environment");
        env.start();
        try {
            String setAsDefaultApp = env.getProperty("sfdc.setAsDefaultApp");
            String loadDefaultData = env.getProperty("sfdc.loadDefaultData");
            env.launchBrower();
            basepage = new BasePage();
            userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
            userLocale = soql.getUserLocale();
            Report.logInfo("Initializing Base Page : " + basepage);
            if ((setAsDefaultApp != null && setAsDefaultApp.equals("true")) || loadDefaultData != null && loadDefaultData.equals("true")) {
                basepage.login();
                if ((setAsDefaultApp != null && setAsDefaultApp.equals("true"))) {
                    basepage.setDefaultApplication("Gainsight");
                    basepage.addTabsToApplication("Gainsight", "Customer Success 360, Gainsight, Transactions, Cockpit, Gainsight");
                }
                if (loadDefaultData != null && loadDefaultData.equals("true")) {
                    basepage.loadDefaultData();
                }
                basepage.logout();
            }

        } catch (Exception e) {
            env.stop();
            Report.logInfo(e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @AfterSuite
    public void fini() {
        env.stop();
    }

    @BeforeClass
    public void failureRecovery() {
        if (TestEnvironment.getDriver() == null) {
            env.start();
        }
    }

    @BeforeMethod
    public void beInMainWindow() {
        basepage.beInMainWindow();
    }

    public String generatePath(String[] dirs) {
        String path = "";
        for (String dir : dirs) {
            path = path + dir + File.separator;
        }
        return path;
    }

    public String currencyFormat(String amt) {
        DecimalFormat moneyFormat = new DecimalFormat("$###,###");
        return moneyFormat.format(new Long(amt)).replace("$", "$ ");
    }


    public HashMap<String, String> getMapFromData(String data) {
        HashMap<String, String> hm = new HashMap<String, String>();
        System.out.println(data);
        String[] dataArray = data.substring(1, data.length() - 1).split("\\|");
        for (String record : dataArray) {
            if (record != null) {
                System.out.println(record);
                String[] pair = record.split("\\:");
                hm.put(pair[0], pair[1].trim());
            }
        }
        return hm;
    }

    public List<HashMap<String, String>> getMapFromDataList(String data) {
        List<HashMap<String, String>> hm = new ArrayList<HashMap<String, String>>();
        System.out.println(data);
        String[] dataArray = data.substring(1, data.length() - 1).split(",");
        for (String record : dataArray) {
            if (record != null) {
                System.out.println(record);
                hm.add(getMapFromData("{" + record + "}"));
            }
        }
        return hm;
    }

    public int calcMRR(int ASV) {
        return (int) Math.round(ASV / 12.0);
    }

    public int calcARPU(int ASV, int users) {
        return (ASV / 12) / users;
    }

    public String makeRowValues(String... values) {
        String row = "";
        int counter = 1;
        int size = values.length;
        for (String value : values) {
            if (counter == size) {
                row = row + value;
            } else {
                row = row + value + "|";
            }
            counter++;
        }
        return row;
    }

    /**
     * @return true if the execution context is packaged environment.
     */
    public boolean isPackageInstance() {
        Boolean namespace = Boolean.valueOf(env
                .getProperty("sfdc.managedPackage"));
        //Report.logInfo("Is Managed Package :" + namespace);
        return namespace;
    }

    /**
     * This Method queries the data base with the query specified.
     *
     * @param query - The Query to Execute on salesforce.
     * @return integer - Count of records that the query returned.
     */
    public int getQueryRecordCount(String query) {
        int result = 0;
        result = soql.getRecordCount(resolveStrNameSpace(query));
        return result;
    }

    /**
     * Method to remove the name space from the string "JBCXM__".
     *
     * @param str -The string where name space should be removed.
     * @return String - with name space removed.
     */
    public String resolveStrNameSpace(String str) {
        String result = "";
        if (str != null && !isPackage) {
            result = str.replaceAll("JBCXM__", "").replaceAll("JBCXM\\.", "");
            Report.logInfo(result);
            return result;
        } else {
            return str;
        }
    }

    public String getDateWithFormat(int noOfDaysToAdd, int noOfMonthsToAdd) {
        String date = null;
        Report.logInfo("User Time Zone :" +userTimezone);
        Report.logInfo("User Locale :" +userLocale);
        Calendar c = Calendar.getInstance(userTimezone);
        Report.logInfo("Time : " +c.getTime() );
        Report.logInfo("Time Zone : " +c.getTimeZone() );
        c.add(Calendar.DATE, noOfDaysToAdd);
        c.add(Calendar.MONTH, noOfMonthsToAdd);
        DateFormat dateFormat = null;
        if (userLocale.contains("en_US")) {
            dateFormat = new SimpleDateFormat("M/d/yyyy");

        } else if (userLocale.contains("en_IN")) {
            dateFormat = new SimpleDateFormat("d/M/yyyy");

        }
        dateFormat.setTimeZone(userTimezone);
        date = dateFormat.format(c.getTime());

        Report.logInfo(String.valueOf(date));
        return date;
    }

    public void deletePickList() {
        String DELETE_SCRIPT_FILE = TestEnvironment.basedir + "/testdata/sfdc/Administration/Picklist_Delte_Script.txt";
        apex.runApexCodeFromFile(DELETE_SCRIPT_FILE, isPackageInstance());
    }

    public String getFileContents(String fileName) {
        String code = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            code = stringBuilder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public FileReader resolveNameSpace(String fileName) throws FileNotFoundException {
        if (!isPackage) {
            File tempFile = new File(TestEnvironment.basedir + "/resources/datagen/process/tempJob.txt");
            FileOutputStream fOut = new FileOutputStream(tempFile);
            try {
                fOut.write(resolveStrNameSpace(getFileContents(fileName)).getBytes());
                fOut.close();
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new FileReader(TestEnvironment.basedir + "/resources/datagen/process/tempJob.txt");
        } else {
            return new FileReader(fileName);
        }
    }


    public void createExtIdFieldOnAccount() {
        CreateObjectAndFields fieldsCreator = new CreateObjectAndFields();
        try {
            fieldsCreator.createTextFields("Account", new String[]{"Data ExternalId"}, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create ext id field on account object :" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void createExtIdFieldForScoreCards() {
        CreateObjectAndFields cObjFields    = new CreateObjectAndFields();
        String Scorecard_Metrics            = "JBCXM__ScorecardMetric__c";
        String[] SCMetric_ExtId             = new String[]{"SCMetric ExternalID"};
        try {
            cObjFields.createTextFields(resolveStrNameSpace(Scorecard_Metrics), SCMetric_ExtId, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create fields");
            e.printStackTrace();
            throw new RuntimeException("Unable to create fields for scorecard section");
        }
    }

    //same method is used by rules engine test cases also.
    public void createFieldsOnUsageData() {
        String object = "JBCXM__Usagedata__c";
        String[] numberFields1 = new String[]{"Page Views", "Page Visits", "No of Report Run", "Files Downloaded"};
        String[] numberFields2 = new String[]{"Emails Sent Count", "Leads", "No of Campaigns", "DB Size", "Active Users"};
        CreateObjectAndFields cObjFields = new CreateObjectAndFields();
        try {
            cObjFields.createNumberField(resolveStrNameSpace(object), numberFields1, false);
            cObjFields.createNumberField(resolveStrNameSpace(object), numberFields2, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create Fields on the object :" + object);
            e.printStackTrace();
        }
    }

    /**
     * This parameter returns the String with comprises of yyyy|mm|dd format.
     *
     * @param weekDay   - Expected values Sun, Mon, Tue, Wed, Thu, Fri, Sat.
     * @param daysToAdd - number of days to add for current day.
     * @return String of format "yyyy|mm|dd".
     */
    public String getWeekLabelDate(String weekDay, int daysToAdd, boolean usesEndDate, boolean userFormat) {
        Calendar cal = Calendar.getInstance();
        Map<String, Integer> days = new HashMap<String, Integer>();
        days.put("Sun", 1);
        days.put("Mon", 2);
        days.put("Tue", 3);
        days.put("Wed", 4);
        days.put("Thu", 5);
        days.put("Fri", 6);
        days.put("Sat", 7);
        System.out.println(cal.getTime());
        if(usesEndDate) {
            int weekDate = days.get(weekDay);
            int calLabel = cal.get(Calendar.DAY_OF_WEEK);
            weekDate = (weekDate == 1) ? 7 : weekDate - 1;
            cal.set(Calendar.DAY_OF_WEEK, weekDate);
            if(weekDate < calLabel) {
                cal.add(Calendar.DATE, 7);
            }
        }
        else {
            int a = cal.get(Calendar.DAY_OF_WEEK);
            cal.set(Calendar.DAY_OF_WEEK, days.get(weekDay));
            if(a <  days.get(weekDay)) {
                cal.add(Calendar.DATE, -7);
            }
        }
        cal.add(Calendar.DATE, daysToAdd);
        Date date = cal.getTime();
        SimpleDateFormat simpleDateFormat =null;
        if (userFormat) {
            if (userLocale.contains("en_US")) {
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

            } else if (userLocale.contains("en_IN")) {
                simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            }
        } else {
            //Default format used for bulk data load.
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        simpleDateFormat.setTimeZone(userTimezone);
        String sDate = simpleDateFormat.format(date);
        Report.logInfo(sDate);
        return sDate;
    }

    /*
    Returns month & year adding/subtracting.
    Jan = 0
    Dec = 11
     */
    public String[] getMonthAndYear(int numOfMonthsToAdd) {
        Calendar cal = Calendar.getInstance(userTimezone)  ;
        Report.logInfo("The current date is : " + cal.getTime());
        cal.add(Calendar.MONTH, numOfMonthsToAdd);
        Report.logInfo("Modified Date : " + cal.getTime());
        Report.logInfo("Month : " +String.valueOf(cal.get(Calendar.MONTH))  + " -- Year : " +String.valueOf(cal.get(Calendar.YEAR)));
        return new String[]{String.valueOf(cal.get(Calendar.MONTH)), String.valueOf(cal.get(Calendar.YEAR))};
    }

    /**
     *
     * @param className
     */
    public void waitForBatchExecutionToComplete(String className) throws InterruptedException {
        for (int l = 0; l < 200; l++) {
            String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob " +
                    "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') " +
                    "and ApexClass.Name = '"+className+"'";
            int noOfRunningJobs = getQueryRecordCount(query);
            if (noOfRunningJobs == 0) {
                Report.logInfo("Aggregate Jobs are finished.");
                break;
            } else {
                Report.logInfo("Waiting for aggregation batch to complete");
                Thread.sleep(30000L);
            }
        }
    }

    /**
     * To trigger adoption aggregation.
     * @param isWeekly - true - Runs weekly aggregation, else - Runs Monthly aggregation.
     * @param isStartDayOfWeek - Week label is based on start of week or end of week.
     * @param weekStartsOn - Week starts on Sun, Mon, Tue
     * @param noOfPeriods - No of weeks/months to run aggregation.  {Good to send multiples of 5}
     * @throws IOException
     * @throws InterruptedException
     */
    public void runAdoptionAggregation(int noOfPeriods, Boolean isWeekly, boolean isStartDayOfWeek, String weekStartsOn) throws IOException, InterruptedException {
        Calendar cal = Calendar.getInstance();
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
                    year = (dateStr != null && dateStr.split("-").length > 0) ? String.valueOf(dateStr.split("-")[0]) : String.valueOf(c.get(Calendar.YEAR));
                    month = (dateStr != null && dateStr.split("-").length > 1) ? String.valueOf(dateStr.split("-")[1]) : String.valueOf(c.get(Calendar.MONTH));
                    day = (dateStr != null && dateStr.split("-").length > 2) ? String.valueOf(dateStr.split("-")[2]) : String.valueOf(c.get(Calendar.DATE));
                    code = stringBuilder.toString();
                    code = code.replaceAll("THEMONTHCHANGE", month).replaceAll("THEYEARCHANGE", year).replace("THEDAYCHANGE", day);
                    Report.logInfo("Running Aggregation On : " +year+"-"+month+"-"+day);
                    apex.runApex(resolveStrNameSpace(code));
                }
                Thread.sleep(30000L);
                waitForBatchExecutionToComplete("AdoptionAggregation");
            }
        } else {
            cal.add(Calendar.MONTH, -1);
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
                Thread.sleep(30000L);
                waitForBatchExecutionToComplete("AdoptionAggregation");
            }
        }
    }

    public void runMetricSetup(String fileName, String scheme) throws IOException {
        BufferedReader reader;
        String line = null;
        String code = "";
        reader = new BufferedReader(new FileReader(fileName));
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        code = String.format(stringBuilder.toString(), scheme);
        apex.runApex(resolveStrNameSpace(code));
    }

    public void overAllCustomerRollUp(Boolean enable) {
        String s = "List<JBCXM__ScorecardConfig__c> enable_sc=[SELECT JBCXM__ScorecardEnabled__c,JBCXM__CustomerRollup__c " +
                                "FROM JBCXM__ScorecardConfig__c where Name='SCORECARD CONFIGURATION' LIMIT 1];";
        if(enable) {
            s+="\n"+"enable_sc.get(0).JBCXM__CustomerRollup__c='WEIGHT';"+"\n";
            s+="\n"+"enable_sc.get(0).JBCXM__OverrideCustomer__c=false;"+"\n";

        } else {
            s+="\n"+"enable_sc.get(0).JBCXM__CustomerRollup__c='DISABLED';"+"\n";
            s+="\n"+"enable_sc.get(0).JBCXM__OverrideCustomer__c=true;"+"\n";
        }
        s+="update(enable_sc);"+"\n";
        apex.runApex(resolveStrNameSpace(s));
    }

}
