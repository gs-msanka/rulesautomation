package com.gainsight.bigdata.connectors.mapping;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.enums.Field;

public class MeasureMappingInfo {

	@JsonProperty("source")
	Map<String, String> source;
	@JsonProperty("target")
	Map<String, Object> target;

	public MeasureMappingInfo() {
		source = new HashMap<String, String>();
		target = new HashMap<String, Object>();
	}

	public MeasureMappingInfo(Field src, String aggType) {
		this();
		setSource(src);
		setTarget(src, aggType);
	}

	public void setSource(Field src) {
		source.put("type", "USAGE_FEED");
		source.put("dbName", src.getDBName());
		source.put("displayName", src.getDisplayName());
	}

	public Map<String, String> getSource() {
		return source;
	}

	public void setTarget(Field trgt, String aggType) {
		Map<String, String> prop = new HashMap<>();
		target.put("dbName", "");
		target.put("displayName", trgt.getDisplayName());
		prop.put("aggregationFunction", aggType);
		target.put("properties", prop);
	}

	public Map<String, Object> getTarget() {
		return target;
	}
}
