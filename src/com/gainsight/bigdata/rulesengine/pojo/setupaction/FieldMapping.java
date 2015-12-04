package com.gainsight.bigdata.rulesengine.pojo.setupaction;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by vmenon on 9/14/2015.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMapping {
	private String sourceObject = "";
	private String sourceField = "";
	private String destination = "";
	private boolean pickList = false;
	private boolean custom = false;
	private boolean defaultValue = false;
	private String defaultValueInput;
	private boolean defaultBooleanValue;
	private boolean identifier;
	
	public boolean isIdentifier() {
		return identifier;
	}

	public void setIdentifier(boolean identifier) {
		this.identifier = identifier;
	}

	public boolean isDefaultBooleanValue() {
		return defaultBooleanValue;
	}

	public void setDefaultBooleanValue(boolean defaultBooleanValue) {
		this.defaultBooleanValue = defaultBooleanValue;
	}

	private List<PickListMappings> pickListMappings = new ArrayList<>();

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValueInput() {
		return defaultValueInput;
	}

	public void setDefaultValueInput(String defaultValueInput) {
		this.defaultValueInput = defaultValueInput;
	}

	public List<PickListMappings> getPickListMappings() {
		return pickListMappings;
	}

	public void setPickListMappings(List<PickListMappings> pickListMappings) {
		this.pickListMappings = pickListMappings;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public boolean isPickList() {
		return pickList;
	}

	public void setPickList(boolean pickList) {
		this.pickList = pickList;
	}

	public String getSourceField() {
		return sourceField;
	}

	public void setSourceField(String sourceField) {
		this.sourceField = sourceField;
	}

	public String getSourceObject() {
		return sourceObject;
	}

	public void setSourceObject(String source) {
		this.sourceObject = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public static class PickListMappings {

		private String source = "";
		private String destination = "";

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}
	}
}