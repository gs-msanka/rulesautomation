package com.gainsight.bigdata.connectors.mapping;

import java.util.HashMap;
import java.util.Map;

import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;

public class FieldInfo {
	Map<String, Object> fieldInfo;
	Map<String, Object> source;
	Map<String, Object> target;
	boolean lookup;
	boolean directLookup;
	Map<String, String> properties;

	public FieldInfo(Field src, Field trgt) {
		fieldInfo = new HashMap<String, Object>();
		if (src != null) {
			setSource(src);
		}
		if (trgt != null) {
			setTarget(trgt);
		}
	}

	public FieldInfo(Field src, Field trgt, SfdcIdentifier prop) {
		this(src, trgt, prop, true, true);
	}

	public FieldInfo(Field src, Field trgt, SfdcIdentifier prop, boolean lookup, boolean directLookup) {
		fieldInfo = new HashMap<String, Object>();
		if (src == null) {
			return;
		}
		setSource(src);
		setTarget(trgt);
		setTargetSFDCMapping(trgt);
		fieldInfo.put("lookup", lookup);
		fieldInfo.put("directLookup", directLookup);
		if (prop != null) {
			setProperties(prop);
		}
	}

	public Map<String, Object> getFieldInfo() {
		return fieldInfo;
	}

	public void setSource(Field src) {
		if (source == null) {
			source = new HashMap<String, Object>();
		}
		source.put("displayName", src.getDisplayName());
		source.put("dbName", src.getDBName());
		fieldInfo.put("source", source);
	}

	public void setTarget(Field trgt) {
		if (target == null) {
			target = new HashMap<String, Object>();
		}
		target.put("displayName", trgt.getDisplayName());
		target.put("dbName", trgt.getDBName());
		fieldInfo.put("target", target);
	}

	public void setProperties(SfdcIdentifier prop) {
		if (properties == null) {
			properties = new HashMap<String, String>();
		}
		properties.put("lookupKey", prop.getLookupKey());
		properties.put("lookupObject", prop.getLookupObject());
		properties.put("key", prop.getKey());
		fieldInfo.put("properties", properties);
	}

	public void setSourceType(String sourceType) {
		this.source.put("type", sourceType);
	}

	public void setSourceObject(String sourceObject) {
		this.source.put("objectName", sourceObject);
	}

	public void setTargetAggFunc(String aggFunc) {
		Map<String, String> prop = new HashMap<>();
		prop.put("aggregationFunction", aggFunc);
		target.put("properties", prop);
	}

	public void setTargetSFDCMapping(Field trgt) {
		if (trgt.equals(Field.SYS_TIMESTAMP)) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("sfdcmapping", "date");
			target.put("properties", properties);
		}
	}
}
