package com.gainsight.bigdata.connectors.mapping;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.enums.Field;

public class GSDefinedMappingInfo {

	@JsonProperty("source")
	Map<String, String> source;
	@JsonProperty("target")
	Map<String, String> target;

	public GSDefinedMappingInfo() {
		source = new HashMap<String, String>();
		target = new HashMap<String, String>();
	}

	public GSDefinedMappingInfo(Field src) {
		this();
		setSource(src);
		setTarget(src);
	}

	public void setSource(Field src) {
		source.put("type", "GS_DEFINED");
		source.put("objectName", "");
		source.put("dbName", src.getDBName());
		source.put("displayName", src.getDisplayName());
	}

	public Map<String, String> getSource() {
		return source;
	}

	public void setTarget(Field trgt) {
		target.put("dbName", "");
		target.put("displayName", trgt.getDisplayName());
	}

	public Map<String, String> getTarget() {
		return target;
	}
}
