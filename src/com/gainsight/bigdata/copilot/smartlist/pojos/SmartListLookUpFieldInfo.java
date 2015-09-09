package com.gainsight.bigdata.copilot.smartlist.pojos;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import com.gainsight.bigdata.copilot.smartlist.pojos.ActionInfo.SmartListProperties;

@JsonPropertyOrder({ "type", "field", "fieldName", "entity", "valueType",
	"dataType", "fieldType", "groupable", "objectName", "label",
	"alias", "aggregation", "properties", "meta", "isExternalCriteria",
	"isReferenceField", "isJoinField" })
public class SmartListLookUpFieldInfo {

@JsonProperty("type")
private String type;
@JsonProperty("field")
private String field;
@JsonProperty("fieldName")
private String fieldName;
@JsonProperty("entity")
private String entity;
@JsonProperty("valueType")
private String valueType;
@JsonProperty("dataType")
private String dataType;
@JsonProperty("fieldType")
private String fieldType;
@JsonProperty("groupable")
private Boolean groupable;
@JsonProperty("objectName")
private String objectName;
@JsonProperty("label")
private String label;
@JsonProperty("alias")
private String alias;
@JsonProperty("aggregation")
private String aggregation;
@JsonProperty("properties")
private SmartListProperties properties;
@JsonProperty("meta")
private SmartListMeta meta;
@JsonProperty("isExternalCriteria")
private Boolean isExternalCriteria;
@JsonProperty("isReferenceField")
private Boolean isReferenceField;
@JsonProperty("isJoinField")
private Boolean isJoinField;

@JsonProperty("type")
public String getType() {
	return type;
}

@JsonProperty("type")
public void setType(String type) {
	this.type = type;
}

@JsonProperty("field")
public String getField() {
	return field;
}

@JsonProperty("field")
public void setField(String field) {
	this.field = field;
}

@JsonProperty("fieldName")
public String getFieldName() {
	return fieldName;
}

@JsonProperty("fieldName")
public void setFieldName(String fieldName) {
	this.fieldName = fieldName;
}

@JsonProperty("entity")
public String getEntity() {
	return entity;
}

@JsonProperty("entity")
public void setEntity(String entity) {
	this.entity = entity;
}

@JsonProperty("valueType")
public String getValueType() {
	return valueType;
}

@JsonProperty("valueType")
public void setValueType(String valueType) {
	this.valueType = valueType;
}

@JsonProperty("dataType")
public String getDataType() {
	return dataType;
}

@JsonProperty("dataType")
public void setDataType(String dataType) {
	this.dataType = dataType;
}

/**
 * 
 * @return The fieldType
 */
@JsonProperty("fieldType")
public String getFieldType() {
	return fieldType;
}

@JsonProperty("fieldType")
public void setFieldType(String fieldType) {
	this.fieldType = fieldType;
}

@JsonProperty("groupable")
public Boolean getGroupable() {
	return groupable;
}

@JsonProperty("groupable")
public void setGroupable(Boolean groupable) {
	this.groupable = groupable;
}

@JsonProperty("objectName")
public String getObjectName() {
	return objectName;
}

@JsonProperty("objectName")
public void setObjectName(String objectName) {
	this.objectName = objectName;
}

@JsonProperty("label")
public String getLabel() {
	return label;
}

@JsonProperty("label")
public void setLabel(String label) {
	this.label = label;
}

@JsonProperty("alias")
public String getAlias() {
	return alias;
}

@JsonProperty("alias")
public void setAlias(String alias) {
	this.alias = alias;
}

@JsonProperty("aggregation")
public String getAggregation() {
	return aggregation;
}

@JsonProperty("aggregation")
public void setAggregation(String aggregation) {
	this.aggregation = aggregation;
}

@JsonProperty("properties")
public SmartListProperties getProperties() {
	return properties;
}

@JsonProperty("properties")
public void setProperties(SmartListProperties properties) {
	this.properties = properties;
}

@JsonProperty("meta")
public SmartListMeta getMeta() {
	return meta;
}

@JsonProperty("meta")
public void setMeta(SmartListMeta meta) {
	this.meta = meta;
}

@JsonProperty("isExternalCriteria")
public Boolean getIsExternalCriteria() {
	return isExternalCriteria;
}

@JsonProperty("isExternalCriteria")
public void setIsExternalCriteria(Boolean isExternalCriteria) {
	this.isExternalCriteria = isExternalCriteria;
}

@JsonProperty("isReferenceField")
public Boolean getIsReferenceField() {
	return isReferenceField;
}

@JsonProperty("isReferenceField")
public void setIsReferenceField(Boolean isReferenceField) {
	this.isReferenceField = isReferenceField;
}

@JsonProperty("isJoinField")
public Boolean getIsJoinField() {
	return isJoinField;
}

@JsonProperty("isJoinField")
public void setIsJoinField(Boolean isJoinField) {
	this.isJoinField = isJoinField;
}
}
