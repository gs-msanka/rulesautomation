package com.gainsight.bigdata.copilot.smartlist.pojos;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class SmartListExternalIdentifier {

	@JsonProperty("type")
	private String type;
	@JsonProperty("field")
	private String field;
	@JsonProperty("entity")
	private String entity;
	@JsonIgnore
	@JsonProperty("uniqueName")
	private String uniqueName;
	@JsonIgnore
	@JsonProperty("parentObj")
	private String parentObj;
	@JsonProperty("valueType")
	private String valueType;
	@JsonProperty("aggregation")
	private String aggregation;

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

	@JsonProperty("entity")
	public String getEntity() {
		return entity;
	}

	@JsonProperty("entity")
	public void setEntity(String entity) {
		this.entity = entity;
	}

	@JsonIgnore
	@JsonProperty("uniqueName")
	public String getUniqueName() {
		return uniqueName;
	}

	@JsonIgnore
	@JsonProperty("uniqueName")
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@JsonIgnore
	@JsonProperty("parentObj")
	public String getParentObj() {
		return parentObj;
	}

	@JsonIgnore
	@JsonProperty("parentObj")
	public void setParentObj(String parentObj) {
		this.parentObj = parentObj;
	}

	@JsonProperty("valueType")
	public String getValueType() {
		return valueType;
	}

	@JsonProperty("valueType")
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	@JsonProperty("aggregation")
	public String getAggregation() {
		return aggregation;
	}

	@JsonProperty("aggregation")
	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}
}
