package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

/**
 * Created by gainsight on 10/07/15.
 */
public class AccountDetail {

    private String createdBy;
    private String createdByName;
    private String modifiedByName;
    private String modifiedBy;
    private String tenantId;
    private String snId;
    private String accountId;
    private String accountType; //SFDC / GOOGLE_ANALYTICS / SEGMENT_IO / MIXPANEL / DATA_API
    private String displayName;
    private boolean defaultAccount;
    private String status;  //AUTHORIZED, IN_COMPLETE, REVOKED, COMPELTED
    private String masterSyncInfoId;
    private String stagingSyncInfoId;
    private GlobalMapping globalMapping;
    private UsageConfiguration usageConfiguration; //"usageConfiguration" : {"configType" : "ACCOUNTLEVEL", "frequency" : "WEEKLY", "day" : "SATURDAY", "weekType" : "END" }
    private SchedulerDetails schedulerDetails;
    private NotificationDetails notificationDetails;
    private RunNowDetails runNowDetails;
    private AccountDetailProperties properties;
    private boolean deleted;


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

    public String getModifiedByName() {
        return modifiedByName;
    }

    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSnId() {
        return snId;
    }

    public void setSnId(String snId) {
        this.snId = snId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDefaultAccount() {
        return defaultAccount;
    }

    public void setDefaultAccount(boolean defaultAccount) {
        this.defaultAccount = defaultAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMasterSyncInfoId() {
        return masterSyncInfoId;
    }

    public void setMasterSyncInfoId(String masterSyncInfoId) {
        this.masterSyncInfoId = masterSyncInfoId;
    }

    public String getStagingSyncInfoId() {
        return stagingSyncInfoId;
    }

    public void setStagingSyncInfoId(String stagingSyncInfoId) {
        this.stagingSyncInfoId = stagingSyncInfoId;
    }

    public GlobalMapping getGlobalMapping() {
        return globalMapping;
    }

    public void setGlobalMapping(GlobalMapping globalMapping) {
        this.globalMapping = globalMapping;
    }

    public UsageConfiguration getUsageConfiguration() {
        return usageConfiguration;
    }

    public void setUsageConfiguration(UsageConfiguration usageConfiguration) {
        this.usageConfiguration = usageConfiguration;
    }

    public SchedulerDetails getSchedulerDetails() {
        return schedulerDetails;
    }

    public void setSchedulerDetails(SchedulerDetails schedulerDetails) {
        this.schedulerDetails = schedulerDetails;
    }

    public NotificationDetails getNotificationDetails() {
        return notificationDetails;
    }

    public void setNotificationDetails(NotificationDetails notificationDetails) {
        this.notificationDetails = notificationDetails;
    }

    public RunNowDetails getRunNowDetails() {
        return runNowDetails;
    }

    public void setRunNowDetails(RunNowDetails runNowDetails) {
        this.runNowDetails = runNowDetails;
    }

    public AccountDetailProperties getProperties() {
        return properties;
    }

    public void setProperties(AccountDetailProperties properties) {
        this.properties = properties;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
