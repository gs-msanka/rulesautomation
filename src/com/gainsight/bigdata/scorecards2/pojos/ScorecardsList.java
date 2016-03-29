package com.gainsight.bigdata.scorecards2.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by nyarlagadda on 23/02/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)

public class ScorecardsList {

    private Boolean deleted;
    private String createdBy;
    private String createdAt;
    private String modifiedBy;
    private String modifiedAt;
    private String tenantId;
    private String id;
    private String name;
    private String description;
    private Boolean active;
    private String entityType;
    private String relationshipTypeId;
    private Boolean overallRollup;
    private Boolean groupRollup;
    private String schemeId;
    private Boolean historyEnabled;
    private List<Status> status = new ArrayList<>();
    private ScoringScheme scoringScheme;



    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getRelationshipTypeId() {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId(String relationshipTypeId) {
        this.relationshipTypeId = relationshipTypeId;
    }

    public Boolean getOverallRollup() {
        return overallRollup;
    }

    public void setOverallRollup(Boolean overallRollup) {
        this.overallRollup = overallRollup;
    }

    public Boolean getGroupRollup() {
        return groupRollup;
    }

    public void setGroupRollup(Boolean groupRollup) {
        this.groupRollup = groupRollup;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public Boolean getHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(Boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }

    public ScoringScheme getScoringScheme() {
        return scoringScheme;
    }

    public void setScoringScheme(ScoringScheme scoringScheme) {
        this.scoringScheme = scoringScheme;
    }
}
