package com.gainsight.bigdata.dataLoadConfiguartion.pojo;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Giribabu on 18-06-2015.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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

    public RunNowDetails getRunNowDetails() {
        return runNowDetails;
    }

    public void setRunNowDetails(RunNowDetails runNowDetails) {
        this.runNowDetails = runNowDetails;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public AccountDetailProperties getProperties() {
        return properties;
    }

    public void setProperties(AccountDetailProperties properties) {
        this.properties = properties;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    @Override
    public String toString() {
        return "AccountDetail{" +
                "createdBy='" + createdBy + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", modifiedByName='" + modifiedByName + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", snId='" + snId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", accountType='" + accountType + '\'' +
                ", displayName='" + displayName + '\'' +
                ", defaultAccount=" + defaultAccount +
                ", status='" + status + '\'' +
                ", masterSyncInfoId='" + masterSyncInfoId + '\'' +
                ", stagingSyncInfoId='" + stagingSyncInfoId + '\'' +
                ", globalMapping=" + globalMapping +
                ", usageConfiguration=" + usageConfiguration +
                ", schedulerDetails=" + schedulerDetails +
                ", notificationDetails=" + notificationDetails +
                ", properties=" + properties +
                ", deleted=" + deleted +
                '}';
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class GlobalMapping {
        private List<Mapping> systemDefined;
        private List<Mapping> gsDefined;
        private List<Mapping> custom;
        private List<Mapping> measures;
        private Identifier accountIdentifier;
        private Identifier userIdentifier;
        private Identifier eventIdentifier;
        private Identifier instanceIdentifier;
        private Identifier timestampIdentifier;
        private List<EventMeasureMapping> eventMeasureMappings;

        public List<Mapping> getSystemDefined() {
            return systemDefined;
        }

        public void setSystemDefined(List<Mapping> systemDefined) {
            this.systemDefined = systemDefined;
        }

        public List<Mapping> getGsDefined() {
            return gsDefined;
        }

        public void setGsDefined(List<Mapping> gsDefined) {
            this.gsDefined = gsDefined;
        }

        public List<Mapping> getCustom() {
            return custom;
        }

        public void setCustom(List<Mapping> custom) {
            this.custom = custom;
        }

        public List<Mapping> getMeasures() {
            return measures;
        }

        public void setMeasures(List<Mapping> measures) {
            this.measures = measures;
        }

        public Identifier getAccountIdentifier() {
            return accountIdentifier;
        }

        public void setAccountIdentifier(Identifier accountIdentifier) {
            this.accountIdentifier = accountIdentifier;
        }

        public Identifier getUserIdentifier() {
            return userIdentifier;
        }

        public void setUserIdentifier(Identifier userIdentifier) {
            this.userIdentifier = userIdentifier;
        }

        public Identifier getEventIdentifier() {
            return eventIdentifier;
        }

        public void setEventIdentifier(Identifier eventIdentifier) {
            this.eventIdentifier = eventIdentifier;
        }

        public Identifier getInstanceIdentifier() {
            return instanceIdentifier;
        }

        public void setInstanceIdentifier(Identifier instanceIdentifier) {
            this.instanceIdentifier = instanceIdentifier;
        }

        public Identifier getTimestampIdentifier() {
            return timestampIdentifier;
        }

        public void setTimestampIdentifier(Identifier timestampIdentifier) {
            this.timestampIdentifier = timestampIdentifier;
        }

        public List<EventMeasureMapping> getEventMeasureMappings() {
            return eventMeasureMappings;
        }

        public void setEventMeasureMappings(List<EventMeasureMapping> eventMeasureMappings) {
            this.eventMeasureMappings = eventMeasureMappings;
        }

        @Override
        public String toString() {
            return "GlobalMapping{" +
                    "eventMeasureMappings=" + eventMeasureMappings +
                    ", timestampIdentifier=" + timestampIdentifier +
                    ", instanceIdentifier=" + instanceIdentifier +
                    ", eventIdentifier=" + eventIdentifier +
                    ", userIdentifier=" + userIdentifier +
                    ", accountIdentifier=" + accountIdentifier +
                    ", measures=" + measures +
                    ", custom=" + custom +
                    ", gsDefined=" + gsDefined +
                    ", systemDefined=" + systemDefined +
                    '}';
        }
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class Identifier {
        private Source source;
        private Target target;
        private boolean lookup;
        private boolean directLookup;
        private boolean digitConversionEnable;
        private Map<String, String> properties;

        public Source getSource() {
            return source;
        }

        public void setSource(Source source) {
            this.source = source;
        }

        public Target getTarget() {
            return target;
        }

        public void setTarget(Target target) {
            this.target = target;
        }

        public boolean isLookup() {
            return lookup;
        }

        public void setLookup(boolean lookup) {
            this.lookup = lookup;
        }

        public boolean isDirectLookup() {
            return directLookup;
        }

        public void setDirectLookup(boolean directLookup) {
            this.directLookup = directLookup;
        }

        public boolean isDigitConversionEnable() {
            return digitConversionEnable;
        }

        public void setDigitConversionEnable(boolean digitConversionEnable) {
            this.digitConversionEnable = digitConversionEnable;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }

        @Override
        public String toString() {
            return "Identifier{" +
                    "properties=" + properties +
                    ", digitConversionEnable=" + digitConversionEnable +
                    ", directLookup=" + directLookup +
                    ", lookup=" + lookup +
                    ", target=" + target +
                    ", source=" + source +
                    '}';
        }
    }

    public static class EventMeasureMapping {
        private String event;
        private String aggregationFunction;
        private String aggregationKey;
        private String flippedMeasureDisplayName;
        private String flippedMeasureDbName;

        @Override
        public String toString() {
            return "EventMeasureMapping{" +
                    "flippedMeasureDbName='" + flippedMeasureDbName + '\'' +
                    ", flippedMeasureDisplayName='" + flippedMeasureDisplayName + '\'' +
                    ", aggregationKey='" + aggregationKey + '\'' +
                    ", aggregationFunction='" + aggregationFunction + '\'' +
                    ", event='" + event + '\'' +
                    '}';
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getAggregationFunction() {
            return aggregationFunction;
        }

        public void setAggregationFunction(String aggregationFunction) {
            this.aggregationFunction = aggregationFunction;
        }

        public String getAggregationKey() {
            return aggregationKey;
        }

        public void setAggregationKey(String aggregationKey) {
            this.aggregationKey = aggregationKey;
        }

        public String getFlippedMeasureDisplayName() {
            return flippedMeasureDisplayName;
        }

        public void setFlippedMeasureDisplayName(String flippedMeasureDisplayName) {
            this.flippedMeasureDisplayName = flippedMeasureDisplayName;
        }

        public String getFlippedMeasureDbName() {
            return flippedMeasureDbName;
        }

        public void setFlippedMeasureDbName(String flippedMeasureDbName) {
            this.flippedMeasureDbName = flippedMeasureDbName;
        }
    }


    public static class Mapping {
        private Source source;
        private Target target;

        public Source getSource() {
            return source;
        }

        public void setSource(Source source) {
            this.source = source;
        }

        public Target getTarget() {
            return target;
        }

        public void setTarget(Target target) {
            this.target = target;
        }

        @Override
        public String toString() {
            return "mapping{" +
                    "target=" + target +
                    ", source=" + source +
                    '}';
        }
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class AccountDetailProperties {
        private String collectionId;
        private String timeZone;

        @JsonProperty("VIEW_FOR_BDA")
        private String viewForBDA; //dbCollectionName for the Day Agg collection.

        @JsonProperty("VIEW_FOR_BDA_COLLECTION_MASTER_ID")
        private String viewForBDACollectionMaserId; //Collection Master - collectionId for the Day Agg collection

        @JsonProperty("MONGO_TO_STAGE")
        private String mongoToStage; //Staging table for postgres/redshift where data is transferred from the source collection which can be either mongo or redshift

        @JsonProperty("REDSHIFT_DAY_AGG_TABLE")
        private String redShiftDayAggTable; // In case redshift is enabled, this is the table which contains Day Agg collection data

        @JsonProperty("REDSHIFT_FM_TABLE")
        private String redShiftFlippedMeasureTable; // In case redshift is enabled, this is the table which contains Flipped Measures collection data

        private String showFetchData;
        private String processPeopleEngagement;

        public String getViewForBDA() {
            return viewForBDA;
        }

        public void setViewForBDA(String viewForBDA) {
            this.viewForBDA = viewForBDA;
        }

        public String getViewForBDACollectionMaserId() {
            return viewForBDACollectionMaserId;
        }

        public void setViewForBDACollectionMaserId(String viewForBDACollectionMaserId) {
            this.viewForBDACollectionMaserId = viewForBDACollectionMaserId;
        }

        public String getMongoToStage() {
            return mongoToStage;
        }

        public void setMongoToStage(String mongoToStage) {
            this.mongoToStage = mongoToStage;
        }

        public String getRedShiftDayAggTable() {
            return redShiftDayAggTable;
        }

        public void setRedShiftDayAggTable(String redShiftDayAggTable) {
            this.redShiftDayAggTable = redShiftDayAggTable;
        }

        public String getRedShiftFlippedMeasureTable() {
            return redShiftFlippedMeasureTable;
        }

        public void setRedShiftFlippedMeasureTable(String redShiftFlippedMeasureTable) {
            this.redShiftFlippedMeasureTable = redShiftFlippedMeasureTable;
        }

        public String getShowFetchData() {
            return showFetchData;
        }

        public void setShowFetchData(String showFetchData) {
            this.showFetchData = showFetchData;
        }

        public String getProcessPeopleEngagement() {
            return processPeopleEngagement;
        }

        public void setProcessPeopleEngagement(String processPeopleEngagement) {
            this.processPeopleEngagement = processPeopleEngagement;
        }

        public String getCollectionId() {
            return collectionId;

        }
        public void setCollectionId(String collectionId) {
            this.collectionId = collectionId;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class Source {
        private String type;
        private String objectName;
        private String dbName;
        private String displayName;
        private Object properties;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getObjectName() {
            return objectName;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Object getProperties() {
            return properties;
        }

        public void setProperties(Object properties) {
            this.properties = properties;
        }

        @Override
        public String toString() {
            return "Source{" +
                    "properties=" + properties +
                    ", displayName='" + displayName + '\'' +
                    ", dbName='" + dbName + '\'' +
                    ", objectName='" + objectName + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public static class Target {
        private String dbName;
        private String displayName;
        private Object properties;

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Object getProperties() {
            return properties;
        }

        public void setProperties(Object properties) {
            this.properties = properties;
        }

        @Override
        public String toString() {
            return "Target{" +
                    "properties=" + properties +
                    ", displayName='" + displayName + '\'' +
                    ", dbName='" + dbName + '\'' +
                    '}';
        }
    }

    public static class RunNowDetails {
        private String type;
        private String startDate;
        private String endDate;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return "RunNowDetails{" +
                    "endDate='" + endDate + '\'' +
                    ", startDate='" + startDate + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public static class UsageConfiguration {
        private String configType = "ACCOUNTLEVEL";
        private String frequency= "MONTHLY";
        private String day = "MONDAY";
        private String weekType = "START";

        public String getConfigType() {
            return configType;
        }

        public void setConfigType(String configType) {
            this.configType = configType;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getWeekType() {
            return weekType;
        }

        public void setWeekType(String weekType) {
            this.weekType = weekType;
        }

        @Override
        public String toString() {
            return "UsageConfiguration{" +
                    "configType='" + configType + '\'' +
                    ", frequency='" + frequency + '\'' +
                    ", day='" + day + '\'' +
                    ", weekType='" + weekType + '\'' +
                    '}';
        }
    }

    public static class SchedulerDetails {
        private String type;
        private Long startDate;
        private Long endDate;
        private String period;
        private String periodValue;
        private String cronExpression;
        private boolean recursive;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getStartDate() {
            return startDate;
        }

        public void setStartDate(Long startDate) {
            this.startDate = startDate;
        }

        public Long getEndDate() {
            return endDate;
        }

        public void setEndDate(Long endDate) {
            this.endDate = endDate;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getPeriodValue() {
            return periodValue;
        }

        public void setPeriodValue(String periodValue) {
            this.periodValue = periodValue;
        }

        public String getCronExpression() {
            return cronExpression;
        }

        public void setCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
        }

        public boolean isRecursive() {
            return recursive;
        }

        public void setRecursive(boolean recursive) {
            this.recursive = recursive;
        }

        @Override
        public String toString() {
            return "SchedulerDetails{" +
                    "type='" + type + '\'' +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", period='" + period + '\'' +
                    ", periodValue='" + periodValue + '\'' +
                    ", cronExpression='" + cronExpression + '\'' +
                    ", recursive=" + recursive +
                    '}';
        }
    }

    public static class NotificationDetails {
        private String[] successRecipients;
        private String[] failureRecipients;

        public String[] getSuccessRecipients() {
            return successRecipients;
        }

        public void setSuccessRecipients(String[] successRecipients) {
            this.successRecipients = successRecipients;
        }

        public String[] getFailureRecipients() {
            return failureRecipients;
        }

        public void setFailureRecipients(String[] failureRecipients) {
            this.failureRecipients = failureRecipients;
        }

        @Override
        public String toString() {
            return "NotificationDetails{" +
                    "successRecipients=" + Arrays.toString(successRecipients) +
                    ", failureRecipients=" + Arrays.toString(failureRecipients) +
                    '}';
        }
    }









}
