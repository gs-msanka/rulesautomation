package com.gainsight.bigdata.rulesengine.bean.RuleSetup;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 03/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class Field {

    private String type;
    private String field;
    private String fieldName;
    private String entity;
    private String valueType;
    private String dataType;
    private String fieldType;
    @JsonProperty("groupable")
    private boolean groupable;
    private String objectName;
    private String label;
    private String alias;
    private String aggregation;
    private Object properties;
    @JsonProperty("meta")
    private FieldMetadata fieldMetadata;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("isAccountIdRelatedField")
    private boolean accountIdRelatedField;
    private String collectionId;
    //TODO - See if this is valid here or not.
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("isExternalCriteria")
    private boolean externalCriteria;
    //TODO - See if this is valid here or not.
    private String format;
    //TODO - Used in ActionInfo - Queries - ExternalIdentifier.
    private String uniqueName;
    //TODO - Used in ActionInfo - Queries - ExternalIdentifier.
    private String parentObj;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("isReferenceField")
    private boolean referenceField;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("isJoinField")
    private boolean joinField;
    //TODO - Not Used any where but key exists in Crieria -> left
    private Object[] keys;
    @JsonProperty("isCalculatedField")
    private boolean calculatedField;
    //Used in CopilotFilterInfo Class.
    private String fieldLabel;

    public boolean isAccountIdRelatedField() {
        return accountIdRelatedField;
    }

    public void setAccountIdRelatedField(boolean accountIdRelatedField) {
        this.accountIdRelatedField = accountIdRelatedField;
    }

    public boolean isExternalCriteria() {
        return externalCriteria;
    }

    public void setExternalCriteria(boolean externalCriteria) {
        this.externalCriteria = externalCriteria;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getParentObj() {
        return parentObj;
    }

    public void setParentObj(String parentObj) {
        this.parentObj = parentObj;
    }

    public boolean isReferenceField() {
        return referenceField;
    }

    public void setReferenceField(boolean referenceField) {
        this.referenceField = referenceField;
    }

    public boolean isJoinField() {
        return joinField;
    }

    public void setJoinField(boolean joinField) {
        this.joinField = joinField;
    }

    public Object[] getKeys() {
        return keys;
    }

    public void setKeys(Object[] keys) {
        this.keys = keys;
    }

    public boolean isCalculatedField() {
        return calculatedField;
    }

    public void setCalculatedField(boolean calculatedField) {
        this.calculatedField = calculatedField;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }


    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public Object getProperties() {
        return properties;
    }

    public void setProperties(Object properties) {
        this.properties = properties;
    }

    public FieldMetadata getFieldMetadata() {
        return fieldMetadata;
    }

    public void setFieldMetadata(FieldMetadata fieldMetadata) {
        this.fieldMetadata = fieldMetadata;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
