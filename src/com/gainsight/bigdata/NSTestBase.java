package com.gainsight.bigdata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.gainsight.bigdata.rulesengine.ResponseObject;

import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.http.Header;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.util.PackageUtil;
import com.gainsight.util.ConfigLoader;
import com.gainsight.util.NsConfig;
import com.gainsight.util.SfdcConfig;
import org.apache.http.HttpStatus;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MetaDataUtil;
import com.sforce.soap.partner.sobject.SObject;

import static com.gainsight.bigdata.urls.AdminURLs.*;
import static com.gainsight.bigdata.urls.ApiUrls.*;

public class NSTestBase {

    public static SFDCInfo sfinfo;
    public static WebAction wa;
    public static Header header;
    public static String basedir;
    public static String testDataBasePath = Application.basedir + "/testdata/newstack";
    public static ObjectMapper mapper = new ObjectMapper();
    public static SalesforceMetadataClient metadataClient;
    public static SalesforceConnector sfdc;
    public static final Application env = new Application();
    public static MetaDataUtil metaUtil = new MetaDataUtil();
    public static String accessKey;
    public static int MAX_NO_OF_REQUESTS = 30; //Max number of attempts to check the status on server for async jobs.
    public static TenantManager tenantManager;
    public static SfdcConfig sfdcConfig = ConfigLoader.getSfdcConfig();
    public static NsConfig nsConfig = ConfigLoader.getNsConfig();
    public static final Boolean isPackage = sfdcConfig.getSfdcManagedPackage();
    public static PackageUtil packageUtil;
    public static String LOAD_SETUP_DATA_SCRIPT = "JBCXM.CEHandler.loadSetupData();";


    @BeforeSuite
    public void init() throws Exception {
        tenantManager = new TenantManager();
        //Initializing Headers
        header = new Header();
        wa = new WebAction();
        //Initializing SFDC Connection
        sfdc = new SalesforceConnector(sfdcConfig.getSfdcUsername(), sfdcConfig.getSfdcPassword()+ sfdcConfig.getSfdcStoken(),
                sfdcConfig.getSfdcPartnerUrl(), sfdcConfig.getSfdcApiVersion());
        Assert.assertTrue(sfdc.connect(), "Failed to Login, check your credentials.");
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        sfinfo = sfdc.fetchSFDCinfo();

        header.addHeader("Origin", sfinfo.getEndpoint());
        header.addHeader("Content-Type", "application/json");
        header.addHeader("appOrgId", sfinfo.getOrg());
        header.addHeader("appUserId", sfinfo.getUserId());
        header.addHeader("appSessionId", sfinfo.getSessionId());
        //Tenant Auto-Provision will not be done here has some test cases my require to test with out tenant creation.
        //Please Add the tenant Auto Provision to your Test Class Setup {BeforeClass} method.
        //Assert.assertTrue(tenantAutoProvision());
        //accessKey = getDataLoadAccessKey();

        packageUtil = new PackageUtil(sfdc.getMetadataConnection(), Double.valueOf(sfdcConfig.getSfdcApiVersion()));
        //Uninstall Application.
        if(sfdcConfig.getSfdcUnInstallApp()) {
            setSiteActiveHomePageToInMaintenance();
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
            }
            packageUtil.deployPermissionSetCode();
            metaUtil.setupPermissionsToStandardObjectAndFields(sfinfo);
        }
    }

    /**
     * Generates the access token for data load purpose.
     * If this method is used then, please update the headers that sends AccessKey{Used In DataLoad API}.
     * @return - String AccessToken that can be used for data load.
     */
    public String getDataLoadAccessKey() {
        Log.info("Getting Access Key");
        String accessKey = null;
        NsResponseObj rs = null;
        try {
            ResponseObj responseObj = wa.doPost(APP_API_TOKENS, header.getAllHeaders(), "{}");
            rs = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } catch (Exception e) {
            Log.error("Failed to get Access Token", e);
            throw new RuntimeException("Failed to get Access Token.");
        }
        if (rs.isResult()) {
            HashMap<String, String> data = (HashMap<String, String>) rs.getData();
            accessKey = data.get("accessKey");
            Log.info("AccessKey : " + accessKey);
        } else {
            Log.info("Error Code :" + rs.getErrorCode());
            Log.info("Error Desc : " + rs.getErrorDesc());
            throw new RuntimeException("Failed to get Access Token.");
        }
        return accessKey;
    }

    /**
     * Does data load authenticate call to get the authToken for further communications.
     *
     * @return - AuthToken of the tenant on success, null/throws error on failure..
     */
    public String getTenantAuthToken() {
        if (accessKey == null || sfinfo == null || sfinfo.getUserName() == null) {
            throw new RuntimeException("Access Key , sfdcInfo details are mandatory.");
        }
        header.addHeader("accessKey", accessKey);
        header.addHeader("loginName", sfinfo.getUserName());
        ResponseObj responseObj = null;
        String authToken = null;
        try {
            responseObj = wa.doGet(ADMIN_DATA_LOAD_AUTHENTICATE, header.getAllHeaders());
        } catch (Exception e) {
            Log.error("Failed to get Auth Token", e);
            throw new RuntimeException("Failed to get Auth Token" + e);
        }

        org.apache.http.Header[] aList = responseObj.getAllHeaders();
        for (org.apache.http.Header a : aList) {
            if (a.getName() != null && a.getName().equals("Authtoken")) {
                Log.info("Updated AuthToken Details");
                authToken = a.getValue();
            }
        }
        return authToken;
    }

    /**
     * @param objName   = the object from which we need the map
     * @param fieldName = the field name that needs to be queried for - it will be the key in the HashMap
     * @param shortCut  = the shortCut for each object will be unique.in the test data we need to prepend the key with the shortcut
     * @return
     */
    public HashMap<String, String> getMapFromObject(String objName, String fieldName, String shortCut) {
        String Query = "SELECT Id," + fieldName + " from " + objName;
        HashMap<String, String> objMap = new HashMap<String, String>();
        SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace(Query));
        Log.info("Total Piclist Records : " + objRecords.length);
        for (SObject sObject : objRecords) {
            Log.info("ObjectName:" + objName + "..FieldName : " + sObject.getField(resolveStrNameSpace(fieldName)) + " - With Id : " + sObject.getId());
            objMap.put(shortCut + "." + sObject.getField(fieldName).toString(), sObject.getId());
        }
        return objMap;
    }


    /**
     * @param objName   = the object from which we need the map
     * @param fieldName = the field name that needs to be queried for - it will be the key in the HashMap
     * @param shortCut  = the shortCut for each object will be unique.in the test data we need to prepend the key with the shortcut
     * @return
     */
    public HashMap<String, String> getMapFromObjectUsingFilter(String objName, String fieldName, String shortCut, String filterField, String filterValue) {
        String Query = "SELECT Id," + fieldName + " from " + objName + " WHERE " + filterField + " =  '" + filterValue + "'";
        HashMap<String, String> objMap = new HashMap<String, String>();
        SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace(Query));
        Log.info("Total Piclist Records : " + objRecords.length);
        for (SObject sObject : objRecords) {
            Log.info("ObjectName:" + objName + "..FieldName : " + sObject.getField(resolveStrNameSpace(fieldName)) + " - With Id : " + sObject.getId());
            objMap.put(shortCut + "." + sObject.getField(fieldName).toString(), sObject.getId());
        }
        return objMap;
    }

    /**
     * Method to remove the name space from the string "JBCXM__".
     *
     * @param str -The string where name space should be removed.
     * @return String - with name space removed.
     */
    public static String resolveStrNameSpace(String str) {
        return FileUtil.resolveNameSpace(str, sfdcConfig.getSfdcManagedPackage() ? sfdcConfig.getSfdcNameSpace() : null);

    }

    public String getNameSpaceResolvedFileContents(String filePath) {
        return resolveStrNameSpace(FileUtil.getFileContents(filePath));
    }

    public HashMap<String, String> getSFValues(HashMap<String, String> fAndV,
                                               String Key) {
        System.out.println("Key is : " + Key);
        String query = "select ";
        String fromValues = "";
        int i = 0;
        Iterator iterator = fAndV.keySet().iterator();
        while (iterator.hasNext()) {
            if (i == 0) fromValues = fromValues + iterator.next();
            else fromValues = fromValues + "," + iterator.next();
            i++;
        }
        //Key = ObjectName:WhereField:WhereValue
        String[] values = Key.split(":");
        query = query + fromValues + " from " + values[0] + " where " + values[1] + " = '" + values[2] + "'";
        SObject[] records = sfdc.getRecords(query);
        System.out.println("QUERY" + query);
        Iterator newIterator = fAndV.keySet().iterator();
        while (newIterator.hasNext()) {
            String key = (String) newIterator.next();
            fAndV.put(key, records[0].getChild(key).getValue().toString());
        }
        return fAndV;
    }

	public static ResponseObject convertToObject(String result)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseObject response = objectMapper.readValue(result,
				ResponseObject.class);
		return response;
	}

    /**
     * Updates the application settings nsurl with the appropriate nsurl__c.
     *
     * @param nsURL - The NSURL that need to be updated in Application settings NSURL__C.
     */
    public void updateNSURLInAppSettings(String nsURL) {
        Log.info("Setting NS URL in Application Settings");
        if (sfdc.getRecordCount(resolveStrNameSpace("select id from JBCXM__ApplicationSettings__c")) > 0) {
            String apexCode = "List<JBCXM__ApplicationSettings__c> appSettings = [select Id, Name, JBCXM__NSURL__C, " +
                    "JBCXM__ISNSEnabled__c, JBCXM__IsNSExportEnabled__c, JBCXM__IsNSReportsEnabled__c from JBCXM__Applicationsettings__c];" +
                    "appSettings.get(0).JBCXM__NSURL__C = '" + nsURL + "';" +
                    "appSettings.get(0).JBCXM__ISNSEnabled__c = true ;" +
                    "appSettings.get(0).JBCXM__IsNSExportEnabled__c = true; " +
                    "appSettings.get(0).JBCXM__IsNSReportsEnabled__c = true; " +
                    "update appSettings;";
            sfdc.runApexCode(resolveStrNameSpace(apexCode));
            Log.info("NS URL Updated Successfully");
        } else {
            Log.error("Configure Gainsight Application to update NS URL");
            throw new RuntimeException("Configure Gainsight Application to update NS URL");
        }
    }



    /**
     * Tenant Auto-provisions will be done here.
     *
     * @return - true if tenant auto-provision is success (or) tenant already exists.
     */
    public boolean tenantAutoProvision() {
        boolean result = false;
        Header h = new Header();
        h.addHeader("Origin", sfinfo.getEndpoint());
        h.addHeader("Content-Type", "application/json");

        HashMap<String, String> params = new HashMap<>();
        params.put("appSessionId", sfinfo.getSessionId());
        params.put("appUserId", sfinfo.getUserId());
        params.put("appOrgId", sfinfo.getOrg());

        HashMap<String, Object> auth_content = new HashMap<>();
        auth_content.put("auth_content", params);
        auth_content.put("appOrgName", sfinfo.getUserName() + "-" + sfinfo.getOrg());

        String payload = "{}";

        try {
            payload = mapper.writeValueAsString(auth_content);
            ResponseObj responseObj = wa.doPost(APP_API_TENANT_PROVISION, h.getAllHeaders(), payload);
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                Log.info("Tenant auto-provision is successful");
                result = true;

            } else if (responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.getErrorCode() != null && nsResponseObj.getErrorCode().equals(MDAErrorCodes.TENANT_ALREADY_EXIST.getGSCode())) {
                    result = true;
                    Log.info(nsResponseObj.getErrorDesc());
                }
            }
        } catch (Exception e) {
            Log.error("Failed tenant auto provision ", e);
            throw new RuntimeException("Failed tenant auto provision " + e.getLocalizedMessage());
        }
        if(result) {
            updateNSURLInAppSettings(nsConfig.getNsURl());
        }
        return result;
    }

    /**
     * Sets sites active home page to in-maintainance.
     */
    public void setSiteActiveHomePageToInMaintenance() {
        Application env = new Application();
        BasePage basePage;
        env.start();
        try {
            env.launchBrower();
            basePage = new BasePage();
            Log.info("Initializing Base Page : " + basePage);
            basePage = basePage.login();
            basePage = basePage.setSiteActiveHomePage("InMaintenance");
            basePage.logout();
        } catch (Exception e) {
            env.stop();
            Log.error(e.getLocalizedMessage(), e);
            throw e;
        }
    }
}
