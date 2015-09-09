package com.gainsight.bigdata.copilot.smartlist.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({ "isAccessible", "isFilterable", "isSortable",
	"isGroupable", "originalDataType", "isCreateable", "precision",
	"relationshipName" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {

@JsonProperty("isAccessible")
private Boolean isAccessible;
@JsonProperty("isFilterable")
private Boolean isFilterable;
@JsonProperty("isSortable")
private Boolean isSortable;
@JsonProperty("isGroupable")
private Boolean isGroupable;
@JsonProperty("originalDataType")
private String originalDataType;
@JsonProperty("isCreateable")
private Boolean isCreateable;
@JsonProperty("precision")
private Integer precision;
@JsonProperty("relationshipName")
private String relationshipName;

@JsonProperty("isAccessible")
public Boolean getIsAccessible() {
	return isAccessible;
}

@JsonProperty("isAccessible")
public void setIsAccessible(Boolean isAccessible) {
	this.isAccessible = isAccessible;
}

@JsonProperty("isFilterable")
public Boolean getIsFilterable() {
	return isFilterable;
}

@JsonProperty("isFilterable")
public void setIsFilterable(Boolean isFilterable) {
	this.isFilterable = isFilterable;
}

@JsonProperty("isSortable")
public Boolean getIsSortable() {
	return isSortable;
}

@JsonProperty("isSortable")
public void setIsSortable(Boolean isSortable) {
	this.isSortable = isSortable;
}

@JsonProperty("isGroupable")
public Boolean getIsGroupable() {
	return isGroupable;
}

@JsonProperty("isGroupable")
public void setIsGroupable(Boolean isGroupable) {
	this.isGroupable = isGroupable;
}

@JsonProperty("originalDataType")
public String getOriginalDataType() {
	return originalDataType;
}

@JsonProperty("originalDataType")
public void setOriginalDataType(String originalDataType) {
	this.originalDataType = originalDataType;
}

@JsonProperty("isCreateable")
public Boolean getIsCreateable() {
	return isCreateable;
}

@JsonProperty("isCreateable")
public void setIsCreateable(Boolean isCreateable) {
	this.isCreateable = isCreateable;
}

@JsonProperty("precision")
public Integer getPrecision() {
	return precision;
}

@JsonProperty("precision")
public void setPrecision(Integer precision) {
	this.precision = precision;
}

@JsonProperty("relationshipName")
public String getRelationshipName() {
	return relationshipName;
}

@JsonProperty("relationshipName")
public void setRelationshipName(String relationshipName) {
	this.relationshipName = relationshipName;
}
}

