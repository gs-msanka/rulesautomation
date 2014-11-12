package com.gainsight.bigdata.connectors.mapping;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.enums.Field;

public class CustomMappingInfo {
	@JsonProperty("source")
	Map<String, String> source;
	@JsonProperty("target")
	Map<String, String> target;

	public CustomMappingInfo() {
		source = new HashMap<String, String>();
		target = new HashMap<String, String>();
	}

	public CustomMappingInfo(Field source, Field target) {
		this();
		setSource(source);
		setTarget(target);
	}

	public void setSource(Field src) {
		source.put("type", "USAGE_FEED");
		source.put("dbName", src.getDBName());
		source.put("displayName", src.getDisplayName());
	}

	public Map<String, String> getSource() {
		return source;
	}

	public void setTarget(Field trgt) {
		target.put("dbName", trgt.getDBName());
		target.put("displayName", trgt.getDisplayName());
	}

	public Map<String, String> getTarget() {
		return target;
	}

}
