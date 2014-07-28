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
    public final String TEST_DATA_PATH_PREFIX = env.basedir + "/"
            + generatePath(dirs);
    public SOQLUtil soql = new SOQLUtil();
    public ApexUtil apex = new ApexUtil();
    protected static BasePage basepage;
    public static String userLocale;
    public static TimeZone userTimezone;
    public String userDir = env.basedir;
    public AmountsAndDatesUtil adUtil = new AmountsAndDatesUtil();
    public HashMap<String, String> monthMap = new HashMap<String, String>();
    Calendar c = Calendar.getInstance();


    @BeforeSuite
    public void init() throws Exception {
        Report.logInfo("Initializing Environment");
        env.start();
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
        try {
            String setAsDefaultApp = env.getProperty("sfdc.setAsDefaultApp");
            String loadDefaultData = env.getProperty("sfdc.loadDefaultData");
            env.launchBrower();
            basepage = new BasePage();
            userLocale = soql.getUserLocale();
            userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
            Report.logInfo("Initializing Base Page : " + basepage);
            if ((setAsDefaultApp != null && setAsDefaultApp.equals("true")) || loadDefaultData != null && loadDefaultData.equals("true")) {
                basepage.login();
                if ((setAsDefaultApp != null && setAsDefaultApp.equals("true"))) {
                    basepage.setDefaultApplication("Gainsight");
                    basepage.addTabsToApplication("Gainsight", "Customer Success 360, Gainsight");
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
        if (env.getDriver() == null) {
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
        List<HashMap<String, String>> hm = new ArrayList();
        System.out.println(data);
        String[] dataArray = data.substring(1, data.length() - 1).split(",");
        int i = 0;
        for (String record : dataArray) {
            if (record != null) {
                System.out.println(record);
                hm.add(getMapFromData("{" + record + "}"));
                i++;
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
        System.out.println("Is Managed Package :" + namespace);
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
        boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
        if (str != null && !isPackage) {
            result = str.replaceAll("JBCXM__", "").replaceAll("JBCXM\\.", "");
            Report.logInfo(result);
            return result;
        } else {
            return str;
        }

    }

    public String getDatewithFormat(int i) {
        String date = null;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, i);
        if (userLocale.contains("en_US")) {
            DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
            date = dateFormat.format(c.getTime());

        } else if (userLocale.contains("en_IN")) {
            DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
            date = dateFormat.format(c.getTime());
        }
        Report.logInfo(String.valueOf(date));
        return date;
    }

    public String getDateFormat(int i) {
        String date = null;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, i);
        if (userLocale.contains("en_US")) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            date = dateFormat.format(c.getTime());

        } else if (userLocale.contains("en_IN")) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            date = dateFormat.format(c.getTime());
        }
        Report.logInfo(String.valueOf(date));
        return date;
    }

    public void deletePickList() {
        String DELETE_SCRIPT_FILE = env.basedir + "/testdata/sfdc/Administration/Picklist_Delte_Script.txt";
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
        if (!isPackageInstance()) {
            File tempFile = new File(env.basedir + "/resources/datagen/process/tempJob.txt");
            FileOutputStream fOut = new FileOutputStream(tempFile);
            try {
                fOut.write(resolveStrNameSpace(getFileContents(fileName)).getBytes());
                fOut.close();
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new FileReader(env.basedir + "/resources/datagen/process/tempJob.txt");
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
        usesEndDate = false;
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
            cal.add(Calendar.DATE, -7);
            weekDate = (weekDate == 1) ? 7 : weekDate - 1;
            cal.set(Calendar.DAY_OF_WEEK, weekDate);
        }
        else {
            cal.set(Calendar.DAY_OF_WEEK, days.get(weekDay));
            cal.add(Calendar.DATE, -7);
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
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
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
        Calendar cal = Calendar.getInstance()  ;
        Report.logInfo("The current date is : " + cal.getTime());
        cal.add(Calendar.MONTH, numOfMonthsToAdd);
        Report.logInfo("Month : " +String.valueOf(cal.get(Calendar.MONTH))  + " -- Year : " +String.valueOf(cal.get(Calendar.YEAR)));
        return new String[]{String.valueOf(cal.get(Calendar.MONTH)), String.valueOf(cal.get(Calendar.YEAR))};
    }


}
