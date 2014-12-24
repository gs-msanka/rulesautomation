package com.gainsight.sfdc.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;

public class BaseTest {
    public static final Application env = new Application();
	protected static BasePage basepage;
	
	public static SalesforceConnector sfdc;
    public static SFDCInfo sfinfo;
    public static String USER_DATE_FORMAT;
    public static final String BULK_DATE_FORMAT = "yyyy-mm-dd";
    public static TimeZone userTimezone;
    public static final Boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    public static final String NAMESPACE = env.getProperty("sfdc.nameSpace");
    public static SalesforceMetadataClient metadataClient;
    
    @BeforeSuite
    public void init() throws Exception {
    	Log.info("Fetching All SFDC Connections");
    	sfdc = new SalesforceConnector(PropertyReader.userName, 
    			PropertyReader.password + PropertyReader.stoken, 
    			PropertyReader.partnerUrl,
    			PropertyReader.sfdcApiVersion);
    	
    	sfdc.connect();
    	
    	//MetadataClient is initialized
    	metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
    	
    	sfinfo = sfdc.fetchSFDCinfo();
        USER_DATE_FORMAT = DateUtil.localMapValues().containsKey(sfinfo.getUserLocale()) ? DateUtil.localMapValues().get(sfinfo.getUserLocale()).split(" ")[0] : "yyyy-mm-dd";
        userTimezone = TimeZone.getTimeZone(sfinfo.getUserTimeZone());
        
        Log.info("Initializing Selenium Environment");
        env.start();
        try {
            String setAsDefaultApp = env.getProperty("sfdc.setAsDefaultApp");
            String loadDefaultData = env.getProperty("sfdc.loadDefaultData");
            env.launchBrower();
            basepage = new BasePage();
            Log.info("Initializing Base Page : " + basepage);
            if ((setAsDefaultApp != null && setAsDefaultApp.equals("true")) || loadDefaultData != null && loadDefaultData.equals("true")) {
                basepage.login();
                if ((setAsDefaultApp != null && setAsDefaultApp.equals("true"))) {
                    basepage.setDefaultApplication("Gainsight");
                    basepage.addTabsToApplication("Gainsight", "Customer Success 360, Gainsight, Transactions, Cockpit, Gainsight, Actions");
                }
                if (loadDefaultData != null && loadDefaultData.equals("true")) {
                    basepage.loadDefaultData();
                }
                basepage.logout();
            }
        } catch (Exception e) {
            env.stop();
            Log.info(e.getLocalizedMessage());
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
        if (Application.getDriver() == null) {
            env.start();
        }
    }

    /*@BeforeMethod
    public void beInMainWindow() {
        basepage.beInMainWindow();
    }*/

    public String currencyFormat(String amt) {
        DecimalFormat moneyFormat = new DecimalFormat("$###,###");
        return moneyFormat.format(new Long(amt)).replace("$", "$ ");
    }

    public HashMap<String, String> getMapFromData(String data) {
        HashMap<String, String> hm = new HashMap<String, String>();
        Log.info("Supplied Data :  " +data);
        String[] dataArray = data.substring(data.indexOf("{")+1, data.lastIndexOf("}")).split("\\|");
        for (String record : dataArray) {
            if (record != null) {
                Log.info("Record to split : " +record);
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

    /**
     * This Method queries the data base with the query specified.
     *
     * @param query - The Query to Execute on salesforce.
     * @return integer - Count of records that the query returned.
     */
    public int getQueryRecordCount(String query) {
        int result = 0;
        result = sfdc.getRecordCount(resolveStrNameSpace(query));
        return result;
    }

    /**
     * Method to remove the name space from the string "JBCXM__".
     *
     * @param str -The string where name space should be removed.
     * @return String - with name space removed.
     */
    public String resolveStrNameSpace(String str) {
        return FileUtil.resolveNameSpace(str, isPackage ? NAMESPACE : null);
    }
   
    public String getDateWithFormat(int days, int months, boolean bulkFormat) {
        String date = null;
        if(days !=0 && months == 0) {
            date = DateUtil.addDays(userTimezone, days, bulkFormat ? BULK_DATE_FORMAT : USER_DATE_FORMAT);
        } else if(days ==0 && months !=0) {
            date = DateUtil.addMonths(userTimezone, months, bulkFormat ? BULK_DATE_FORMAT : USER_DATE_FORMAT);
        } else if(months !=0 && days !=0) {
            date = DateUtil.addDays(DateUtil.addMonths(userTimezone, months), days, bulkFormat ? BULK_DATE_FORMAT : USER_DATE_FORMAT);
        }
        System.out.println("Formatted Date :" +date);
        return date;
    }

    public void deletePickList() {
        String script = "Delete [Select id, name from JBCXM__PickList__c];";
        sfdc.runApexCode(resolveStrNameSpace(script));
    }

    public FileReader resolveNameSpace(String fileName) {
        try {
            if (!isPackage) {
                return FileUtil.resolveNameSpace(new File(fileName), NAMESPACE);
            } else {
                return new FileReader(fileName);
            }
        } catch (FileNotFoundException e) {
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException("File Not Found : " +fileName);
        }
    }


    public void createExtIdFieldOnAccount() throws Exception {
        metadataClient.createTextFields("Account", new String[]{"Data ExternalId"}, true, true, true, false, false);
    }

    public void createExtIdFieldForScoreCards() throws Exception {
        String Scorecard_Metrics            = "JBCXM__ScorecardMetric__c";
        String[] SCMetric_ExtId             = new String[]{"SCMetric ExternalID"};
        metadataClient.createTextFields(resolveStrNameSpace(Scorecard_Metrics), SCMetric_ExtId, true, true, true, false, false);
    }

    public void createExtIdFieldOnUser() throws Exception{
        String UserObj = "User";
        String[] user_ExtId = new String[]{"User ExternalId"};
        metadataClient.createTextFields(resolveStrNameSpace(UserObj), user_ExtId, true, true, true, false, false);
    }

    public void createExternalIdFieldOnCTA() throws Exception{
        String CtaObj = "JBCXM__CTA__c";
        String[] Cta_ExtId = new String[]{"CTA ExternalID"};
        metadataClient.createTextFields(resolveStrNameSpace(CtaObj), Cta_ExtId, true, true, true, false, false);
    }


    //same method is used by rules engine test cases also.
    public void createFieldsOnUsageData() throws Exception {
        String object = "JBCXM__Usagedata__c";
        String[] numberFields1 = new String[]{"Page Views", "Page Visits", "No of Report Run", "Files Downloaded"};
        String[] numberFields2 = new String[]{"Emails Sent Count", "Leads", "No of Campaigns", "DB Size", "Active Users"};
        metadataClient.createNumberField(resolveStrNameSpace(object), numberFields1, false);
        metadataClient.createNumberField(resolveStrNameSpace(object), numberFields2, false);
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
            if (sfinfo.getUserLocale().contains("en_US")) {
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

            } else if (sfinfo.getUserLocale().contains("en_IN")) {
                simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            }
        } else {
            //Default format used for bulk data load.
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        //Commented as timezone is not required to handle.{Rule, Usage Aggregation etc}
        //simpleDateFormat.setTimeZone(userTimezone);
        String sDate = simpleDateFormat.format(date);
        Log.info(sDate);
        return sDate;
    }

    /*
    Returns month & year adding/subtracting.
    Jan = 0
    Dec = 11
     */
    public String[] getMonthAndYear(int numOfMonthsToAdd) {
        Calendar cal = Calendar.getInstance(userTimezone)  ;
        Log.info("The current date is : " + cal.getTime());
        cal.add(Calendar.MONTH, numOfMonthsToAdd);
        Log.info("Modified Date : " + cal.getTime());
        Log.info("Month : " +String.valueOf(cal.get(Calendar.MONTH))  + " -- Year : " +String.valueOf(cal.get(Calendar.YEAR)));
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
                Log.info("Aggregate Jobs are finished.");
                break;
            } else {
                Log.info("Waiting for aggregation batch to complete");
                Thread.sleep(15000L);
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
        sfdc.runApexCode(resolveStrNameSpace(code));
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
        sfdc.runApexCode(resolveStrNameSpace(s));
    }
}
