package com.gainsight.bigdata.copilot.bean.outreach;

import com.gainsight.bigdata.rulesengine.bean.RuleAction.ActionTrueCase;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OutReach {

    private String createdBy;
    private String createdByName;
    private String modifiedBy;
    private String modifiedByName;
    private String tenantId;
    private String modifiedDateStr;
    private String createdDateStr;
    private String name;
    private String category;
    private String smartListId;
    private String smartListName;
    private String status;
    private int totalRecipients;
    private String[] outreachEmailTemplateTypes;
    @JsonProperty("preventDuplicateDays")
    private int preventDuplicateDays;
    private boolean cascadeDelete;
    private boolean published;
    private String campaignId;
    private List<DefaultECA> defaultECA;
    //TODO - This key is not used, Don't Know actual data type.
    private Object followUpECA;
    private List<ReportTokenMapping> reportTokenMappings;
    private EmailSettings settings;
    private long lastRunDate;
    private String lastRunStatus;

    public String getLastRunStatus() {
        return lastRunStatus;
    }

    public void setLastRunStatus(String lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }

    public long getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(long lastRunDate) {
        this.lastRunDate = lastRunDate;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public EmailSettings getSettings() {
        return settings;
    }

    public void setSettings(EmailSettings settings) {
        this.settings = settings;
    }

    public List<ReportTokenMapping> getReportTokenMappings() {
        return reportTokenMappings;
    }

    public void setReportTokenMappings(List<ReportTokenMapping> reportTokenMappings) {
        this.reportTokenMappings = reportTokenMappings;
    }

    public List<DefaultECA> getDefaultECA() {
        return defaultECA;
    }

    public void setDefaultECA(List<DefaultECA> defaultECA) {
        this.defaultECA = defaultECA;
    }

    public Object getFollowUpECA() {
        return followUpECA;
    }

    public void setFollowUpECA(Object followUpECA) {
        this.followUpECA = followUpECA;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSmartListId() {
        return smartListId;
    }

    public void setSmartListId(String smartListId) {
        this.smartListId = smartListId;
    }

    public String getSmartListName() {
        return smartListName;
    }

    public void setSmartListName(String smartListName) {
        this.smartListName = smartListName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalRecipients() {
        return totalRecipients;
    }

    public void setTotalRecipients(int totalRecipients) {
        this.totalRecipients = totalRecipients;
    }

    public String[] getOutreachEmailTemplateTypes() {
        return outreachEmailTemplateTypes;
    }

    public void setOutreachEmailTemplateTypes(String[] outreachEmailTemplateTypes) {
        this.outreachEmailTemplateTypes = outreachEmailTemplateTypes;
    }

    public int getPreventDuplicateDays() {
        return preventDuplicateDays;
    }

    public void setPreventDuplicateDays(int preventDuplicateDays) {
        this.preventDuplicateDays = preventDuplicateDays;
    }

    public boolean isCascadeDelete() {
        return cascadeDelete;
    }

    public void setCascadeDelete(boolean cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public static class DefaultECA {
        List<ActionTrueCase> actions;

        public List<ActionTrueCase> getActions() {
            return actions;
        }

        public void setActions(List<ActionTrueCase> actions) {
            this.actions = actions;
        }
    }
}
