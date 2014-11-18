package com.gainsight.sfdc.workflow.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by gainsight on 17/11/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CockpitConfig {
    private String autoSync = "false";
    private String newCTAAge = "7";
    private String snoozeEnabled = "true";
    private Object statusMapping = "{}";
    private Object priorityMapping = "{}";
    private String defaultSFStatus = "";
    private String defaultGSStatus = "Open";
    private String feedObj = "";


    public Object getStatusMapping() {
        return statusMapping;
    }

    public void setStatusMapping(Object statusMapping) {
        this.statusMapping = statusMapping;
    }

    public Object getPriorityMapping() {
        return priorityMapping;
    }

    public void setPriorityMapping(Object priorityMapping) {
        this.priorityMapping = priorityMapping;
    }

    public String getAutoSync() {
        return autoSync;
    }

    public void setAutoSync(String autoSync) {
        this.autoSync = autoSync;
    }

    public String getNewCTAAge() {
        return newCTAAge;
    }

    public void setNewCTAAge(String newCTAAge) {
        this.newCTAAge = newCTAAge;
    }

    public String getSnoozeEnabled() {
        return snoozeEnabled;
    }

    public void setSnoozeEnabled(String snoozeEnabled) {
        this.snoozeEnabled = snoozeEnabled;
    }


    public String getDefaultSFStatus() {
        return defaultSFStatus;
    }

    public void setDefaultSFStatus(String defaultSFStatus) {
        this.defaultSFStatus = defaultSFStatus;
    }

    public String getDefaultGSStatus() {
        return defaultGSStatus;
    }

    public void setDefaultGSStatus(String defaultGSStatus) {
        this.defaultGSStatus = defaultGSStatus;
    }

    public String getFeedObj() {
        return feedObj;
    }

    public void setFeedObj(String feedObj) {
        this.feedObj = feedObj;
    }
}
