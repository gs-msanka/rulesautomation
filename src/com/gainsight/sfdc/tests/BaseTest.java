package com.gainsight.sfdc.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import com.gainsight.sfdc.util.PackageUtil;

import com.gainsight.util.NsConfig;
import com.gainsight.util.SfdcConfig;
import com.gainsight.util.ConfigLoader;

import org.testng.Assert;
import org.testng.annotations.*;

import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MetaDataUtil;

public class BaseTest {
    public static final Application env = new Application();
	protected static BasePage basepage;
	
	public static SalesforceConnector sfdc;
    public static SFDCInfo sfdcInfo;
    public static String USER_DATE_FORMAT;
    public static final String BULK_DATE_FORMAT = "yyyy-mm-dd";
    public static TimeZone userTimezone;
    public static SfdcConfig sfdcConfig = ConfigLoader.getSfdcConfig();
    public static final Boolean isPackage = sfdcConfig.getSfdcManagedPackage();
    public static final String NAMESPACE = sfdcConfig.getSfdcNameSpace();
    public static SalesforceMetadataClient metadataClient;
    public static PackageUtil packageUtil;
    public static NsConfig nsConfig = ConfigLoader.getNsConfig();
    public static MetaDataUtil metaUtil = new MetaDataUtil();
    public static String LOAD_SETUP_DATA_SCRIPT = "JBCXM.CEHandler.loadSetupData();";


    @BeforeSuite
    public void init() throws Exception {
    	Log.info("Fetching All SFDC Connections");
    	sfdc = new SalesforceConnector(sfdcConfig.getSfdcUsername(), sfdcConfig.getSfdcPassword()+ sfdcConfig.getSfdcStoken(),
    			sfdcConfig.getSfdcPartnerUrl(), sfdcConfig.getSfdcApiVersion());
    	
    	Assert.assertTrue(sfdc.connect(), "SFDC Connection Failed..., Check credentials.");

    	//MetadataClient is initialized
    	metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        packageUtil = new PackageUtil(sfdc.getMetadataConnection(), Double.valueOf(sfdcConfig.getSfdcApiVersion()));
        //Uninstall Application.
        sfdcInfo = sfdc.fetchSFDCinfo();

        if(sfdcConfig.getSfdcUnInstallApp()) {
            sfdc.runApexCodeFromFile(new File(Application.basedir+"/resources/sfdcmetadata/permissionSetScripts/DeletePermissionAssignment.txt"));
            packageUtil.unInstallApplication(sfdcConfig.getSfdcManagedPackage(), sfdcConfig.getSfdcNameSpace());
        }
        //Install Application.
        if(sfdcConfig.getSfdcInstallApp()) {
            packageUtil.installApplication(sfdcConfig.getSfdcPackageVersionNumber(), sfdcConfig.getSfdcPackagePassword());
        }

        if(sfdcConfig.getSfdcUpdateWidgetLayouts()) {
            packageUtil.updateWidgetLayouts(true, true, true, sfdcConfig.getSfdcManagedPackage(), sfdcConfig.getSfdcNameSpace());
        }

        if(sfdcConfig.getSfdcSetupGainsightApp()) {
            packageUtil.setupGainsightApplicationAndTabs(sfdcConfig.getSfdcManagedPackage(), sfdcConfig.getSfdcNameSpace());
            sfdc.runApexCode(resolveStrNameSpace(LOAD_SETUP_DATA_SCRIPT));
            if(sfdcConfig.getSfdcManagedPackage()) {
                sfdc.runApexCodeFromFile(new File(Application.basedir+"/resources/sfdcmetadata/permissionSetScripts/AssignPermissionSetScript.txt"));
            } else {
                sfdc.runApexCode("AdminHandler.assignPermissionToallGainsightObject();");
            }
            packageUtil.deployPermissionSetCode();
            metaUtil.setupPermissionsToStandardObjectAndFields(sfdcInfo);
        }

        Log.info("Sfdc Info : " +sfdc.getLoginResult().getUserInfo().getUserFullName());
        USER_DATE_FORMAT = DateUtil.localMapValues().containsKey(sfdcInfo.getUserLocale()) ? DateUtil.localMapValues().get(sfdcInfo.getUserLocale()).split(" ")[0] : "yyyy-mm-dd";
        userTimezone = TimeZone.getTimeZone(sfdcInfo.getUserTimeZone());
        DateUtil.timeZone =  userTimezone;
        Log.info("Initializing Selenium Environment");
        env.start();
        try {
            env.launchBrower();
            basepage = new BasePage();
            Log.info("Initializing Base Page : " + basepage);
        } catch (Exception e) {
            env.stop();
            Log.error(e.getLocalizedMessage(), e);
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

    @BeforeMethod
    public void setDefaultTimeout() {
        basepage.beInMainWindow();
        env.setTimeout(30);
    }


    public String currencyFormat(String amt) {
        DecimalFormat moneyFormat = new DecimalFormat("$###,###");
        return moneyFormat.format(new Long(amt)).replace("$", "$ ");
    }

    public HashMap<String, String> getMapFromData(String data) {
        HashMap<String, String> hm = new HashMap<String, String>();
        Log.info("Supplied Data :  " + data);
        String[] dataArray = data.substring(data.indexOf("{")+1, data.lastIndexOf("}")).split("\\|");
        for (String record : dataArray) {
            if (record != null) {
                Log.info("Record to split : " + record);
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
         date = DateUtil.addDays(DateUtil.addMonths(userTimezone, months), days, bulkFormat ? BULK_DATE_FORMAT : USER_DATE_FORMAT);
        System.out.println("Formatted Date :" +date);
        return date;
    }

    public void deletePickList() {
        String script = "Delete [Select id, name from JBCXM__PickList__c];";
        sfdc.runApexCode(resolveStrNameSpace(script));
    }

    public String getNameSpaceResolvedFileContents(String filePath) {
        return resolveStrNameSpace(FileUtil.getFileContents(filePath));
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

    public void addNSURLToRemoteSiteSettings() throws Exception {
        System.out.println("creating remote site!");
        metadataClient.createRemoteSiteSetting("GSRemoteSite", env.getProperty("ns.appurl"));
    }

   
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
        Log.info("Running Metric Code:" +code);
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
