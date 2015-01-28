package com.gainsight.sfdc.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.util.PackageUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.*;

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
    public static PackageUtil packageUtil;
    
    @BeforeSuite
    public void init() throws Exception {
    	Log.info("Fetching All SFDC Connections");
    	sfdc = new SalesforceConnector(PropertyReader.userName, PropertyReader.password + PropertyReader.stoken,
    			PropertyReader.partnerUrl, PropertyReader.sfdcApiVersion);
    	
    	sfdc.connect();
    	//MetadataClient is initialized
    	metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        packageUtil = new PackageUtil(sfdc.getMetadataConnection(), Double.valueOf(PropertyReader.sfdcApiVersion));
        //Uninstall Application.
        if(Boolean.valueOf(env.getProperty("sfdc.unInstallApp"))) {
            packageUtil.unInstallApplication();
        }
        //Install Application.
        if(Boolean.valueOf(env.getProperty("sfdc.installApp"))) {
            packageUtil.installApplication(env.getProperty("sfdc.packageVersionNumber"), env.getProperty("sfdc.packagePassword"));
        }

        if(Boolean.valueOf(env.getProperty("sfdc.updateWidgetLayouts"))) {
            packageUtil.updateWidgetLayouts(true, true, true);
        }

        packageUtil.deployPermissionSetCode();
        sfinfo = sfdc.fetchSFDCinfo();
        System.out.println("Sfdc Info : " +sfdc.getLoginResult().getUserInfo().getUserFullName());
        USER_DATE_FORMAT = DateUtil.localMapValues().containsKey(sfinfo.getUserLocale()) ? DateUtil.localMapValues().get(sfinfo.getUserLocale()).split(" ")[0] : "yyyy-mm-dd";
        userTimezone = TimeZone.getTimeZone(sfinfo.getUserTimeZone());
        DateUtil.timeZone =  userTimezone;
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

    public void createExtIdFieldOnAccount() throws Exception {
        String[] fields = new String[]{"Data ExternalId"};
        metadataClient.createTextFields("Account", fields, true, true, true, false, false);
        addFieldPermissionsToUsers("Account", convertFieldNameToAPIName(fields));
    }

    private String[] convertFieldNameToAPIName(String[] args) {
        String[] temp = new String[args.length];
        int i=0;
        for(String s : args) {
            temp[i] = s.replaceAll(" ", "_") + "__c";
            Log.info("Field Name : " +temp[i]);
            i++;
        }
        return temp;
    }

    public void createFieldsOnContact() throws Exception {
        metadataClient.createTextFields("Contact", new String[]{"Contact ExternalID"}, true, true, true, false, false);
        metadataClient.createNumberField("Contact", new String[]{"NoOfReferrals", "NumForDate", "NumberField"},false);
        metadataClient.createFields("Contact", new String[]{"Active"}, true, false, false);
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InvolvedIn", new String[]{"Marketing", "Sales", "Forecast", "Finance", "Budget"});
        metadataClient.createPickListField("Contact", fields, true);
        metadataClient.createNumberField("Contact", new String[]{"DealCloseRate"}, true);
        String[] permField = new String[]{"Contact ExternalID", "NoOfReferrals", "NumForDate",
                            "NumberField", "Active", "InvolvedIn","DealCloseRate"};
        addFieldPermissionsToUsers("Contact", convertFieldNameToAPIName(permField));
    }

    public void createFieldsOnAccount() throws Exception {
        metadataClient.createTextFields("Account", new String[]{"Data ExternalId"}, true, true, true, false, false);
        metadataClient.createFields("Account", new String[]{"IsActive"}, true, false, false);
        metadataClient.createDateField("Account", new String[]{"InputDate"}, false);
        metadataClient.createDateField("Account", new String[]{"InputDateTime"}, true);
        metadataClient.createNumberField("Account", new String[]{"AccPercentage"}, true);
        metadataClient.createNumberField("Account", new String[]{"ActiveUsers"}, false);
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InRegions", new String[]{"India", "America", "England", "France", "Italy", "Germany", "Japan" , "China", "Australia", "Russia", "Africa", "Arab "});
        metadataClient.createPickListField("Account", fields, true);
        ArrayList<HashMap<String, String>> fFields = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> fField1 = new HashMap<String, String>();
        fField1.put("Type", "CheckBox");
        fField1.put("Formula", "IsActive__c");
        fField1.put("FieldName", "FIsActive");
        fField1.put("Description", "Is Active Field");
        fField1.put("HelpText", "Is Active Field");
        fFields.add(fField1);
        HashMap<String, String> fField2 = new HashMap<String, String>();
        fField2.put("Type", "Currency");
        fField2.put("Formula", "AnnualRevenue");
        fField2.put("FieldName", "FCurrency");
        fField2.put("Description", "AnnualRevenue");
        fField2.put("HelpText", "Formula AnnualRevenue");
        fFields.add(fField2);
        HashMap<String, String> fField3 = new HashMap<String, String>();
        fField3.put("Type", "Date");
        fField3.put("Formula", "InputDate__c");
        fField3.put("FieldName", "FDate");
        fField3.put("Description", "Formula InputDate__c");
        fField3.put("HelpText", "Formula InputDate__c");
        fFields.add(fField3);
        HashMap<String, String> fField4 = new HashMap<String, String>();
        fField4.put("Type", "DateTime");
        fField4.put("Formula", "InputDateTime__c");
        fField4.put("FieldName", "FDateTime");
        fField4.put("Description", "Formula InputDateTime__c");
        fField4.put("HelpText", "Formula InputDateTime__c");
        fFields.add(fField4);
        metadataClient.createFormulaFields("Account", fFields);
        fFields.clear();
        HashMap<String, String> fField5 = new HashMap<String, String>();
        fField5.put("Type", "Number");
        fField5.put("Formula", "ActiveUsers__c");
        fField5.put("FieldName", "FNumber");
        fField5.put("Description", "Formula ActiveUsers__c");
        fField5.put("HelpText", " Formula ActiveUsers__c");
        fFields.add(fField5);
        HashMap<String, String> fField6 = new HashMap<String, String>();
        fField6.put("Type", "Percent");
        fField6.put("Formula", "AccPercentage__c");
        fField6.put("FieldName", "FPercent");
        fField6.put("Description", "Field AccPercentage__c");
        fField6.put("HelpText", "Field AccPercentage__c");
        fFields.add(fField6);
        HashMap<String, String> fField7 = new HashMap<String, String>();
        fField7.put("Type", "Text");
        fField7.put("Formula", "Name");
        fField7.put("FieldName", "FText");
        fField7.put("Description", "Formula Name");
        fField7.put("HelpText", "Formula Name");
        fFields.add(fField7);
        metadataClient.createFormulaFields("Account", fFields);

        String[] permFields = new String[]{"Data ExternalId", "IsActive", "InputDate", "InputDateTime",
                                    "AccPercentage", "ActiveUsers", "InRegions", "FIsActive", "FCurrency", "FDate", "FDateTime", "FNumber", "FPercent", "FText"};
        addFieldPermissionsToUsers("Account", convertFieldNameToAPIName(permFields));
    }


    public void createExtIdFieldForScoreCards() throws Exception {
        String Scorecard_Metrics            = "JBCXM__ScorecardMetric__c";
        String[] SCMetric_ExtId             = new String[]{"SCMetric ExternalID"};
        metadataClient.createTextFields(resolveStrNameSpace(Scorecard_Metrics), SCMetric_ExtId, true, true, true, false, false);
        addFieldPermissionsToUsers(resolveStrNameSpace(Scorecard_Metrics), convertFieldNameToAPIName(SCMetric_ExtId));
    }

    public void createExtIdFieldOnUser() throws Exception{
        String UserObj = "User";
        String[] user_ExtId = new String[]{"User ExternalId"};
        metadataClient.createTextFields(resolveStrNameSpace(UserObj), user_ExtId, true, true, true, false, false);
        addFieldPermissionsToUsers(UserObj, convertFieldNameToAPIName(user_ExtId));
    }

    public void createExternalIdFieldOnCTA() throws Exception{
        String CtaObj = "JBCXM__CTA__c";
        String[] Cta_ExtId = new String[]{"CTA ExternalID"};
        metadataClient.createTextFields(resolveStrNameSpace(CtaObj), Cta_ExtId, true, true, true, false, false);
        addFieldPermissionsToUsers(resolveStrNameSpace(CtaObj), convertFieldNameToAPIName(Cta_ExtId));
    }

    //same method is used by rules engine test cases also.
    public void createFieldsOnUsageData() throws Exception {
        String object = "JBCXM__Usagedata__c";
        String[] numberFields1 = new String[]{"Page Views", "Page Visits", "No of Report Run", "Files Downloaded"};
        String[] numberFields2 = new String[]{"Emails Sent Count", "Leads", "No of Campaigns", "DB Size", "Active Users"};
        metadataClient.createNumberField(resolveStrNameSpace(object), numberFields1, false);
        metadataClient.createNumberField(resolveStrNameSpace(object), numberFields2, false);
        addFieldPermissionsToUsers(resolveStrNameSpace(object),convertFieldNameToAPIName(ArrayUtils.addAll(numberFields1, numberFields2)));
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

    /**
     * Creates a permission set on the org with name "GS_Automation_Permission" & assigns to all the system admins & licensed users.
     * Code deployment is done for this feature to work.
     * Please check out ----- packageUtil.deployPermissionSetCode();
     * @param object - Full Object API Name
     * @param fields - Array of fields.
     * @throws Exception - Connection exception, Runtime Exception if status is failed.
     */
    public void addFieldPermissionsToUsers(String object, String[] fields) throws Exception {
        WebAction webAction = new WebAction();
        Header header = new Header();
        header.addHeader("Authorization", "Bearer "+sfinfo.getSessionId());
        header.addHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String[]> objMap = new HashMap<>();
        objMap.put(object, fields);
        Map<String, Object> payLoad1 = new HashMap<>();
        payLoad1.put("modulename", "GS_Auto_Permissions");
        List<Object> tmp = new ArrayList<Object>();
        tmp.add(objMap);
        payLoad1.put("data", tmp);
        Map<String, Object> payLoad = new HashMap<>();
        payLoad.put("params", mapper.writeValueAsString(payLoad1));
        Log.info(mapper.writeValueAsString(payLoad));
        ResponseObj responseObj = webAction.doPost(sfinfo.getEndpoint() + "/services/apexrest/GSAutomation/orgInfo/", header.getAllHeaders(), mapper.writeValueAsString(payLoad));
        Map<String, Object> resContent  = new HashMap<>();
        resContent = mapper.readValue(responseObj.getContent(), resContent.getClass());
        if(!resContent.get("status").toString().equalsIgnoreCase("Success")) {
            Log.error("Failed to create field permissions");
            Log.error(responseObj.getContent());
            throw new RuntimeException(resContent.get("errMsg").toString());
        }
        Log.info("Field permissions added successfully.");
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
