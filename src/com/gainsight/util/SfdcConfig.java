package com.gainsight.util;

import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vmenon on 1/6/15.
 */
public class SfdcConfig  {

   /* private String sfdcUsername;
    private String sfdcPassword;

    private String sfdcStoken;
    private String sfdcAppurl;
    private String sfdcDeleteRecords;
    private String sfdcManagedPackage;
    private String sfdcSetDefaultApp;
    private String sfdcLoadDefaultData;
    private String sfdcNameSpace;
    private String sfdcUpdateWidgetLayouts;
    private String sfdcUnInstallApp;
    private String sfdcInstallApp;
    private String sfdcPackageVersionNumber;
    private String sfdcPackagePassword;
    private String sfdcPartnerUrl;
    private String sfdcSiteCustomURL;
    private String sfdcApiVersion;*/
    private JsonObject configObject;

    public SfdcConfig(JsonObject configObject){
        this.configObject = configObject;
    }

    public String getSfdcUsername() {
        return getConfigValue(ISfdcProperties.sfdcUsername);
    }

    public String getSfdcPassword() {
        return getConfigValue(ISfdcProperties.sfdcPassword);
    }

    public String getSfdcStoken() {
        return getConfigValue(ISfdcProperties.sfdcStoken);
    }

    public String getSfdcAppurl() {
        return getConfigValue(ISfdcProperties.sfdcAppurl);
    }

    public String getSfdcDeleteRecords() {
        return getConfigValue(ISfdcProperties.sfdcDeleteRecords);
    }

    public boolean getSfdcManagedPackage() {
        String value = getConfigValue(ISfdcProperties.sfdcManagedPackage);
        boolean managedPackage = false;
        if(value.equalsIgnoreCase("true"))
            managedPackage = true;
        return managedPackage;
    }

    public String getSfdcSetDefaultApp() {
        return getConfigValue(ISfdcProperties.sfdcSetDefaultApp);
    }

    public String getSfdcLoadDefaultData() {
        return getConfigValue(ISfdcProperties.sfdcLoadDefaultData);
    }

    public String getSfdcNameSpace() {
        return getConfigValue(ISfdcProperties.sfdcNameSpace);
    }

    public String getSfdcUpdateWidgetLayouts() {
        return getConfigValue(ISfdcProperties.sfdcUpdateWidgetLayouts);
    }

    public String getSfdcUnInstallApp() {
        return getConfigValue(ISfdcProperties.sfdcUnInstallApp);
    }

    public String getSfdcInstallApp() {
        return getConfigValue(ISfdcProperties.sfdcInstallApp);
    }

    public String getSfdcPackageVersionNumber() {
        return getConfigValue(ISfdcProperties.sfdcPackageVersionNumber);
    }

    public String getSfdcPackagePassword() {
        return getConfigValue(ISfdcProperties.sfdcPackagePassword);
    }

    public String getSfdcPartnerUrl() {
        return getConfigValue(ISfdcProperties.sfdcPartnerUrl);
    }

    public String getSfdcSiteCustomURL() {
        return getConfigValue(ISfdcProperties.sfdcSiteCustomURL);
    }

    public String getSfdcApiVersion() {
        return getConfigValue(ISfdcProperties.sfdcApiVersion);
    }

    private String getConfigValue(String config){
        return System.getProperty(config, configObject.get(config).getAsString());
    }

    private interface ISfdcProperties {
        public static final String sfdcUsername = "sfdc.username";
        public static final String sfdcPassword = "sfdc.password";
        public static final String sfdcStoken = "sfdc.stoken";
        public static final String sfdcAppurl = "sfdc.appurl";
        public static final String sfdcDeleteRecords = "sfdc.deleteRecords";
        public static final String sfdcManagedPackage = "sfdc.managedPackage";
        public static final String sfdcSetDefaultApp = "sfdc.setAsDefaultApp";
        public static final String sfdcLoadDefaultData = "sfdc.loadDefaultData";
        public static final String sfdcNameSpace = "sfdc.nameSpace";
        public static final String sfdcUpdateWidgetLayouts= "sfdc.updateWidgetLayouts" ;
        public static final String sfdcUnInstallApp= "sfdc.unInstallApp";
        public static final String sfdcInstallApp = "sfdc.installApp";
        public static final String sfdcPackageVersionNumber="sfdc.packageVersionNumber";
        public static final String sfdcPackagePassword="sfdc.packagePassword";
        public static final String sfdcPartnerUrl="sfdc.partnerUrl";
        public static final String sfdcSiteCustomURL="sfdc.siteCustomURL";
        public static final String sfdcApiVersion ="sfdc.apiVersion";
    }
}
