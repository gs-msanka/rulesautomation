/**
 * 
 */
package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Abhilash Thaduka
 *
 */
public class LoadToMDAAction {

	private String objectName;
	private String operation;
	@JsonProperty("fieldMappings")
	private List<FieldMapping> fieldMappings = new ArrayList<FieldMapping>();

	@JsonProperty("objectName")
	public String getObjectName() {
		return objectName;
	}

	@JsonProperty("objectName")
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	@JsonProperty("operation")
	public String getOperation() {
		return operation;
	}

	@JsonProperty("operation")
	public void setOperation(String operation) {
		this.operation = operation;
	}

	@JsonProperty("fieldMappings")
	public List<FieldMapping> getFieldMappings() {
		return fieldMappings;
	}

	@JsonProperty("fieldMappings")
	public void setFieldMappings(List<FieldMapping> fieldMappings) {
		this.fieldMappings = fieldMappings;
	}
}
