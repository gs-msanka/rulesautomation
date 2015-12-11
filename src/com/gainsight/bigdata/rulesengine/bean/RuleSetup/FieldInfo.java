package com.gainsight.bigdata.rulesengine.bean.RuleSetup;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class FieldInfo {
    private String type;
    private String valueType;
    private String value;
    private String fieldName;
    private String field;
    private boolean tokenBased;
    private String entity;
    private RuleSetupExpression expression;
    private Object[] keys;
    private String objectName;
    private String fieldType;
    private String aggregation;
    private String collectionId;
    private int minValue;
    private int maxValue;
    private boolean disallowNulls;
    private String dataType;
    private boolean groupable;
    private String label;
    private String alias;
    private String uniqueName;
    private int length;
    private String fieldLabel;
    private String cid;
    private String selectedSchemaSource;
    private Object messages;
    private FieldInfo defaultValue;
    @JsonProperty("isExternalCriteria")
    private boolean externalCriteria;

    public boolean isExternalCriteria() {
        return externalCriteria;
    }

    public void setExternalCriteria(boolean externalCriteria) {
        this.externalCriteria = externalCriteria;
    }

    public FieldInfo getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(FieldInfo defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getMessages() {
        return messages;
    }

    public void setMessages(Object messages) {
        this.messages = messages;
    }

    public String getSelectedSchemaSource() {
        return selectedSchemaSource;
    }

    public void setSelectedSchemaSource(String selectedSchemaSource) {
        this.selectedSchemaSource = selectedSchemaSource;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object[] getKeys() {
        return keys;
    }

    public void setKeys(Object[] keys) {
        this.keys = keys;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isDisallowNulls() {
        return disallowNulls;
    }

    public void setDisallowNulls(boolean disallowNulls) {
        this.disallowNulls = disallowNulls;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public boolean isTokenBased() {
        return tokenBased;
    }

    public void setTokenBased(boolean tokenBased) {
        this.tokenBased = tokenBased;
    }

    public RuleSetupExpression getExpression() {
        return expression;
    }

    public void setExpression(RuleSetupExpression expression) {
        this.expression = expression;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
