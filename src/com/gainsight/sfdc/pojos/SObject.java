package com.gainsight.sfdc.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SObject {

	private String name;
	private String label;
	private String keyPrefix;
	private String baseObject;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getBaseObject() {
		return baseObject;
	}

	public void setBaseObject(String baseObject) {
		this.baseObject = baseObject;
	}

}
