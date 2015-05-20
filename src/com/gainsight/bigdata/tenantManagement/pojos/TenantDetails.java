package com.gainsight.bigdata.tenantManagement.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by Giribabu on 07/05/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantDetails {

    private String createdBy;
    private String createdByName;
    private String modifiedBy;
    private String modifiedByName;
    private String modifiedDateStr;
    private String createdDateStr;
    @JsonProperty("TenantName")
    private String tenantName;
    @JsonProperty("TenantId")
    private String tenantId;
    @JsonProperty("ExternalTenantID")
    private String externalTenantID;
    @JsonProperty("ExternalTenantName")
    private String externalTenantName;
    @JsonProperty("ReportReadLimit")
    private int reportReadLimit = 1000;
    @JsonProperty("DimensionBrowserReadLimit")
    private int dimensionBrowserReadLimit = 500;
    @JsonProperty("schemaDBDetail")
    private DBDetail schemaDBDetail;
    @JsonProperty("dataDBDetail")
    private DBDetail dataDBDetail;
    @JsonProperty("postgresDBDetail")
    private DBDetail postgresDBDetail;
    private boolean disabled = false;
    private String tenantType = "OTHERS";
    private boolean systemDefined = false;
    private boolean redshiftEnabled = false;
    private Config configs;
    private EmailSetting emailSetting;
    private Profile profile;

    public static class Profile {
        private String firstName;
        private String lastName;
        private String mailId;
        private String address;
        private String city;
        private String state;
        private String country;
        private String zip;
        private String phone;
        private String website;
        private String company;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMailId() {
            return mailId;
        }

        public void setMailId(String mailId) {
            this.mailId = mailId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }
    }

    public static class DBDetail {
        private String dbName;
        @JsonProperty("sslEnabled")
        private boolean sslEnabled;
        @JsonProperty("dbServerDetails")
        private List<DBServerDetail> dbServerDetails;
        //private List<HashMap<String, String>> dbDetails;


        public List<DBServerDetail> getDbServerDetails() {
            return dbServerDetails;
        }

        public void setDbServerDetails(List<DBServerDetail> dbServerDetails) {
            this.dbServerDetails = dbServerDetails;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public boolean isSslEnabled() {
            return sslEnabled;
        }

        public void setSslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
        }
    }

    public static class DBServerDetail {
        private String host;
        private String userName;
        private String password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Config {
        @JsonProperty("DISALLOW_EXCEL_GENERATION")
        private String disAllowExcelGeneration;
        @JsonProperty("RULES_MDP_RECORD_LIMIT")
        private String rulesMDPRecordLimit;
        @JsonProperty("DEBUG_ENABLED_AT")
        private String debugEnabledAT;
        @JsonProperty("DEBUG_ENABLED_FOR_SEC")
        private String debugEnabledForSec;

        public String getDisAllowExcelGeneration() {
            return disAllowExcelGeneration;
        }

        public void setDisAllowExcelGeneration(String disAllowExcelGeneration) {
            this.disAllowExcelGeneration = disAllowExcelGeneration;
        }

        public String getRulesMDPRecordLimit() {
            return rulesMDPRecordLimit;
        }

        public void setRulesMDPRecordLimit(String rulesMDPRecordLimit) {
            this.rulesMDPRecordLimit = rulesMDPRecordLimit;
        }

        public String getDebugEnabledAT() {
            return debugEnabledAT;
        }

        public void setDebugEnabledAT(String debugEnabledAT) {
            this.debugEnabledAT = debugEnabledAT;
        }

        public String getDebugEnabledForSec() {
            return debugEnabledForSec;
        }

        public void setDebugEnabledForSec(String debugEnabledForSec) {
            this.debugEnabledForSec = debugEnabledForSec;
        }
    }

    public static class EmailSetting {
        private String provider = "MANDRILL";
        private String[] ips;
        private String mailDomain;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String[] getIps() {
            return ips;
        }

        public void setIps(String[] ips) {
            this.ips = ips;
        }

        public String getMailDomain() {
            return mailDomain;
        }

        public void setMailDomain(String mailDomain) {
            this.mailDomain = mailDomain;
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedByName() {
        return modifiedByName;
    }

    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    public String getModifiedDateStr() {
        return modifiedDateStr;
    }

    public void setModifiedDateStr(String modifiedDateStr) {
        this.modifiedDateStr = modifiedDateStr;
    }

    public String getCreatedDateStr() {
        return createdDateStr;
    }

    public void setCreatedDateStr(String createdDateStr) {
        this.createdDateStr = createdDateStr;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getExternalTenantID() {
        return externalTenantID;
    }

    public void setExternalTenantID(String externalTenantID) {
        this.externalTenantID = externalTenantID;
    }

    public String getExternalTenantName() {
        return externalTenantName;
    }

    public void setExternalTenantName(String externalTenantName) {
        this.externalTenantName = externalTenantName;
    }

    public int getReportReadLimit() {
        return reportReadLimit;
    }

    public void setReportReadLimit(int reportReadLimit) {
        this.reportReadLimit = reportReadLimit;
    }

    public int getDimensionBrowserReadLimit() {
        return dimensionBrowserReadLimit;
    }

    public void setDimensionBrowserReadLimit(int dimensionBrowserReadLimit) {
        this.dimensionBrowserReadLimit = dimensionBrowserReadLimit;
    }

    public DBDetail getSchemaDBDetail() {
        return schemaDBDetail;
    }

    public void setSchemaDBDetail(DBDetail schemaDBDetail) {
        this.schemaDBDetail = schemaDBDetail;
    }

    public DBDetail getDataDBDetail() {
        return dataDBDetail;
    }

    public void setDataDBDetail(DBDetail dataDBDetail) {
        this.dataDBDetail = dataDBDetail;
    }

    public DBDetail getPostgresDBDetail() {
        return postgresDBDetail;
    }

    public void setPostgresDBDetail(DBDetail postgresDBDetail) {
        this.postgresDBDetail = postgresDBDetail;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getTenantType() {
        return tenantType;
    }

    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
    }

    public boolean isSystemDefined() {
        return systemDefined;
    }

    public void setSystemDefined(boolean systemDefined) {
        this.systemDefined = systemDefined;
    }

    public Config getConfigs() {
        return configs;
    }

    public void setConfigs(Config configs) {
        this.configs = configs;
    }

    public EmailSetting getEmailSetting() {
        return emailSetting;
    }

    public void setEmailSetting(EmailSetting emailSetting) {
        this.emailSetting = emailSetting;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean isRedshiftEnabled() {
        return redshiftEnabled;
    }

    public void setRedshiftEnabled(boolean redshiftEnabled) {
        this.redshiftEnabled = redshiftEnabled;
    }
}
