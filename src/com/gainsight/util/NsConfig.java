package com.gainsight.util;

import com.gainsight.testdriver.Log;
import com.google.gson.JsonObject;

/**
 * Created by Giribabu on 03/07/15.
 */
public class NsConfig {

    private JsonObject configObject;
    public NsConfig(JsonObject configObject){
        this.configObject = configObject;
    }

    public String getNsURl() {
        return getConfigValue(INsProperties.nsAppUrl);
    }

    public String getNsAdminUrl() {
        return getConfigValue(INsProperties.nsAdminUrl);
    }

    public String getNsVersion() {
        return getConfigValue(INsProperties.nsVersion);
    }

    public String getSfdcUsername() {
        return getConfigValue(INsProperties.sfdcUsername);
    }

    public String getSfdcPassword() {
        return getConfigValue(INsProperties.sfdcPassword);
    }

    public String getSfdcStoken() {
        return getConfigValue(INsProperties.sfdcStoken);
    }


    public JsonObject getConfigObject() {
        return configObject;
    }

    private String getConfigValue(String config){
        String value = System.getProperty(config, configObject.get(config).getAsString());
        Log.info(String.format("Returning value: %s for key : %s", value, config));
        return value;
    }


    private interface INsProperties {
        public static final String nsAppUrl = "ns_appUrl";
        public static final String nsAdminUrl = "ns_adminUrl";
        public static final String nsVersion = "ns_version";
        public static final String sfdcUsername = "sfdc_username";
        public static final String sfdcPassword = "sfdc_password";
        public static final String sfdcStoken = "sfdc_stoken";
    }
}
