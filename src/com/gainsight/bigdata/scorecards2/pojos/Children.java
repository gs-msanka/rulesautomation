package com.gainsight.bigdata.scorecards2.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nyarlagadda on 25/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Children {

    private String name;
    private String entityType;
    private String inputType;
    private String levelType;
    private int displayOrder;
    private String scorecardId;
    private String schemeId;
    private String measureMapId;
    private String measureId;
    private int weight;
    private int validity;
    private String validityType;
    private boolean group;
    private List<Children> children = new ArrayList<>();

    public List<Children> getChildren() {
        return children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getScorecardId() {
        return scorecardId;
    }

    public void setScorecardId(String scorecardId) {
        this.scorecardId = scorecardId;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getMeasureMapId() {
        return measureMapId;
    }

    public void setMeasureMapId(String measureMapId) {
        this.measureMapId = measureMapId;
    }

    public String getMeasureId() {
        return measureId;
    }

    public void setMeasureId(String measureId) {
        this.measureId = measureId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public String getValidityType() {
        return validityType;
    }

    public void setValidityType(String validityType) {
        this.validityType = validityType;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }
}
