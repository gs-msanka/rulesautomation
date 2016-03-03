package com.gainsight.bigdata.rulesengine.bean.RuleSetup;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 03/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMetadata {

    @JsonProperty("isAccessible")
    private boolean accessible;
    @JsonProperty("isFilterable")
    private boolean filterable;
    @JsonProperty("isGroupable")
    private boolean groupable;
    @JsonProperty("isSortable")
    private boolean sortable;
    @JsonProperty("isCreateable")
    private boolean createable;
    @JsonProperty("isFormulaField")
    private boolean formulaField;
    @JsonProperty("isRichText")
    private boolean richText;
    @JsonProperty("isUpdateable")
    private boolean updateable;
    //TODO - Used in copilot
    private String referenceTo;
    //TODO - Used in copilot
    private String[] referenceList;
    private String mapName;

    @JsonProperty("colattribtype")
    private int colAttribType;
    private String originalDataType;
    private int decimalPlaces;
    private int precision;
    private String relationshipName;
    //TODO - Used in copilot.
    private Object mappings;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String[] getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(String[] referenceList) {
        this.referenceList = referenceList;
    }

    public String getReferenceTo() {
        return referenceTo;
    }

    public void setReferenceTo(String referenceTo) {
        this.referenceTo = referenceTo;
    }

    public Object getMappings() {
        return mappings;
    }

    public void setMappings(Object mappings) {
        this.mappings = mappings;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public void setFilterable(boolean filterable) {
        this.filterable = filterable;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public boolean isCreateable() {
        return createable;
    }

    public void setCreateable(boolean createable) {
        this.createable = createable;
    }

    public boolean isFormulaField() {
        return formulaField;
    }

    public void setFormulaField(boolean formulaField) {
        this.formulaField = formulaField;
    }

    public boolean isRichText() {
        return richText;
    }

    public void setRichText(boolean richText) {
        this.richText = richText;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }

    public int getColAttribType() {
        return colAttribType;
    }

    public void setColAttribType(int colAttribType) {
        this.colAttribType = colAttribType;
    }

    public String getOriginalDataType() {
        return originalDataType;
    }

    public void setOriginalDataType(String originalDataType) {
        this.originalDataType = originalDataType;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }
}
