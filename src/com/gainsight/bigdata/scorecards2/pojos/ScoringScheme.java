package com.gainsight.bigdata.scorecards2.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by nyarlagadda on 24/02/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoringScheme {

    private boolean deleted;
    private String createdBy;
    private String createdAt;
    private String tenantId;
    private String id;
    private String name;
    private String type;
    private boolean active;
    private int rangeFrom;
    private boolean definitionChanged;
    private int displayOrder;
    private int rangeTo;
    @JsonProperty("scoringSchemeDefinitionList")
    private List<ScoringSchemeDefinitionList> scoringSchemeDefinitionList;

    public List<ScoringSchemeDefinitionList> getScoringSchemeDefinitionList() {
        return scoringSchemeDefinitionList;
    }

    public void setScoringSchemeDefinitionList(List<ScoringSchemeDefinitionList> scoringSchemeDefinitionList) {
        this.scoringSchemeDefinitionList = scoringSchemeDefinitionList;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(int rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public boolean isDefinitionChanged() {
        return definitionChanged;
    }

    public void setDefinitionChanged(boolean definitionChanged) {
        this.definitionChanged = definitionChanged;
    }

    public int getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(int rangeTo) {
        this.rangeTo = rangeTo;
    }


}
