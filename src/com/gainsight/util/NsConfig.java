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

    public String getSfdcPartnerUrl() {
        return getConfigValue(INsProperties.sfdcPartnerUrl);
    }

    public String getSfdcApiVersion() {
        return getConfigValue(INsProperties.sfdcApiVersion);
    }


    public String getGlobalDBHost() {
        return getConfigValue(INsProperties.globalDBHost);
    }

    public String getGlobalDBPort() {
        return getConfigValue(INsProperties.globalDBPort);
    }

    public String getGlobalDBDatabase() {
        return getConfigValue(INsProperties.globalDBDatabase);
    }

    public String getGlobalDBUserName() {
        return getConfigValue(INsProperties.globalDBUserName);
    }

    public String getGlobalDBPassword() {
        return getConfigValue(INsProperties.globalDBPassword);
    }

    public String getGlobalDBIsSSLEnabled() {
        return getConfigValue(INsProperties.globalDBIsSSLEnabled);
    }

    public String getSchedulerDBHost() {
        return getConfigValue(INsProperties.SchedulerDBHost);
    }

    public String getSchedulerDBPort() {
        return getConfigValue(INsProperties.SchedulerDBPort);
    }

    public String getSchedulerDBDatabase() {
        return getConfigValue(INsProperties.SchedulerDBDatabase);
    }

    public String getSchedulerDBUserName() {
        return getConfigValue(INsProperties.SchedulerDBUserName);
    }

    public String getSchedulerDBPassword() {
        return getConfigValue(INsProperties.SchedulerDBPassword);
    }

    public String getSchedulerDBIsSSLEnabled() {
        return getConfigValue(INsProperties.SchedulerDBIsSSLEnabled);
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
        public static final String sfdcUsername = "ns_sfdc_username";
        public static final String sfdcPassword = "ns_sfdc_password";
        public static final String sfdcStoken = "ns_sfdc_stoken";
        public static final String sfdcPartnerUrl = "ns_sfdc_partnerUrl";
        public static final String sfdcApiVersion = "ns_sfdc_apiVersion";
        public static final String globalDBHost = "mongo_global_host";
        public static final String globalDBPort = "mongo_global_port";
        public static final String globalDBDatabase = "mongo_global_db";
        public static final String globalDBUserName = "mongo_global_username";
        public static final String globalDBPassword = "mongo_global_password";
        public static final String globalDBIsSSLEnabled = "mongo_global_ssl_enable"; 
        public static final String SchedulerDBHost = "mongo_scheduler_host";
        public static final String SchedulerDBPort = "mongo_scheduler_port";
        public static final String SchedulerDBDatabase = "mongo_scheduler_db";
        public static final String SchedulerDBUserName = "mongo_scheduler_username";
        public static final String SchedulerDBPassword = "mongo_scheduler_password";
        public static final String SchedulerDBIsSSLEnabled = "mongo_scheduler_ssl_enable";



    }
}
