package com.gainsight.util.config;

import com.gainsight.testdriver.Log;
import com.gainsight.utils.config.IConfig;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vmenon on 1/6/15.
 */
public class SfdcConfig implements IConfig {

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

    public boolean getSfdcManagedPackage() {
        String value = getConfigValue(ISfdcProperties.sfdcManagedPackage);
        boolean managedPackage = false;
        if(value.equalsIgnoreCase("true"))
            managedPackage = true;
        return managedPackage;
    }

    public Boolean getSfdcSetupGainsightApp() {
        String value = getConfigValue(ISfdcProperties.sfdcSetupGainsightApp);
        boolean setupApp = false;
        if(value.equalsIgnoreCase("true"))
            setupApp = true;
        return setupApp;
    }

    public String getSfdcNameSpace() {
        return getConfigValue(ISfdcProperties.sfdcNameSpace);
    }

    public Boolean getSfdcUpdateWidgetLayouts() {
        String value = getConfigValue(ISfdcProperties.sfdcUpdateWidgetLayouts);
        boolean update = false;
        if(value.equalsIgnoreCase("true"))
            update = true;
        return update;
    }

    public Boolean getSfdcUnInstallApp() {
        String value = getConfigValue(ISfdcProperties.sfdcUnInstallApp);
        boolean unInstallApp = false;
        if(value.equalsIgnoreCase("true"))
            unInstallApp = true;
        return unInstallApp;
    }

    public Boolean getSfdcInstallApp() {
        String value = getConfigValue(ISfdcProperties.sfdcInstallApp);
        boolean installApp = false;
        if(value.equalsIgnoreCase("true"))
            installApp = true;
        return installApp;
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
        String value = System.getProperty(config, configObject.get(config).getAsString());
        Log.info(String.format("Returning value: %s for key : %s",value,config));
        return value;
    }

    private interface ISfdcProperties {
        public static final String sfdcUsername = "sfdc_username";
        public static final String sfdcPassword = "sfdc_password";
        public static final String sfdcStoken = "sfdc_stoken";
        public static final String sfdcManagedPackage = "sfdc_managedPackage";
        public static final String sfdcSetupGainsightApp = "sfdc_setupGainsightApp";
        public static final String sfdcNameSpace = "sfdc_nameSpace";
        public static final String sfdcUpdateWidgetLayouts= "sfdc_updateWidgetLayouts" ;
        public static final String sfdcUnInstallApp= "sfdc_unInstallApp";
        public static final String sfdcInstallApp = "sfdc_installApp";
        public static final String sfdcPackageVersionNumber="sfdc_packageVersionNumber";
        public static final String sfdcPackagePassword="sfdc_packagePassword";
        public static final String sfdcPartnerUrl="sfdc_partnerUrl";
        public static final String sfdcSiteCustomURL="sfdc_siteCustomURL";
        public static final String sfdcApiVersion ="sfdc_apiVersion";
    }
}
