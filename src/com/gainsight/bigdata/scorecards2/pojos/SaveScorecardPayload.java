package com.gainsight.bigdata.scorecards2.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by nyarlagadda on 25/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SaveScorecardPayload {

    private String name;
    private String description;
    private String entityType;
    private String schemeId;
    private boolean overallRollup;
    private boolean active;
    private boolean groupRollup;
    private boolean historyEnabled;
    private String relationshipTypeId;

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

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public boolean isOverallRollup() {
        return overallRollup;
    }

    public void setOverallRollup(boolean overallRollup) {
        this.overallRollup = overallRollup;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isGroupRollup() {
        return groupRollup;
    }

    public void setGroupRollup(boolean groupRollup) {
        this.groupRollup = groupRollup;
    }

    public boolean isHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public String getRelationshipTypeId() {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId(String relationshipTypeId) {
        this.relationshipTypeId = relationshipTypeId;
    }
}
