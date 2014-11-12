package com.gainsight.bigdata.connectors.mapping;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;

public class CommonMappingInfo {

	@JsonProperty("source")
	Map<String, String> source;
	@JsonProperty("target")
	Map<String, String> target;
	@JsonProperty("properties")
	Map<String, String> properties;
	@JsonProperty("lookup")
	boolean lookup = true;
	@JsonProperty("directLookup")
	boolean directLookup = true;

	public CommonMappingInfo() {
		source = new HashMap<String, String>();
		target = new HashMap<String, String>();
		properties = new HashMap<String, String>();
	}

	public CommonMappingInfo(Field src, Field trgt, SfdcIdentifier prop) {
		this();
		if (src == null) {
			return;
		}
		setSource(src);
		setTarget(trgt);
		if (prop != null) {
			setProperties(prop);
		}
	}

	public void setSource(Field src) {
		source.put("dbName", src.getDBName());
		source.put("displayName", src.getDisplayName());
	}

	public void setTarget(Field trgt) {
		target.put("dbName", trgt.getDBName());
		target.put("displayName", trgt.getDisplayName());
	}

	public void setProperties(SfdcIdentifier prop) {
		properties.put("lookupKey", prop.getLookupKey());
		properties.put("lookupObject", prop.getLookupObject());
		properties.put("key", prop.getKey());
	}

	public void setLookup(boolean lookup) {
		this.lookup = lookup;
	}

	public void setDirectLookup(boolean directLookup) {
		this.directLookup = directLookup;
	}

}
