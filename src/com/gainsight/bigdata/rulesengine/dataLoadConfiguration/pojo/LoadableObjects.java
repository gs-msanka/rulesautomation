/**
 * 
 */
package com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Abhilash Thaduka
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadableObjects {

	private String objectType;
	private List<DataLoadObject> dataLoadObject = new ArrayList<DataLoadObject>();

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public List<DataLoadObject> getDataLoadObject() {
		return dataLoadObject;
	}

	public void setDataLoadObject(List<DataLoadObject> dataLoadObject) {
		this.dataLoadObject = dataLoadObject;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DataLoadObject {

		private String objectName = "";
		private List<Field> fields = new ArrayList<Field>();
		private List<Field> removeFields = new ArrayList<Field>();

		public List<Field> getRemoveFields() {
			return removeFields;
		}

		public void setRemoveFields(List<Field> removeFields) {
			this.removeFields = removeFields;
		}

		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		public List<Field> getFields() {
			return fields;
		}

		public void setFields(List<Field> fields) {
			this.fields = fields;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Field {

		private String fieldName = "";

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

	}
}
