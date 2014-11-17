package com.gainsight.bigdata.connectors.mapping;

import java.util.Map;

import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;

public class MappingInfo {

	public Map<String, Object> getAccountIdentifier(Field src, Field trgt, SfdcIdentifier prop) {
		return new FieldInfo(src, trgt, prop).getFieldInfo();
	}

	public Map<String, Object> getAccountIdentifier(Field src, Field trgt, SfdcIdentifier prop, boolean lookup,
			boolean directLookup) {
		return new FieldInfo(src, trgt, prop, lookup, directLookup).getFieldInfo();
	}

	public Map<String, Object> getUserIdentifier(Field src, Field trgt, SfdcIdentifier prop) {
		return new FieldInfo(src, trgt, prop).getFieldInfo();
	}

	public Map<String, Object> getUserIdentifier(Field src, Field trgt, SfdcIdentifier prop, boolean lookup,
			boolean directLookup) {
		return new FieldInfo(src, trgt, prop, lookup, directLookup).getFieldInfo();
	}

	public Map<String, Object> getTimestampIdentifier(Field src, Field trgt, SfdcIdentifier prop) {
		return new FieldInfo(src, trgt, prop).getFieldInfo();
	}

	public Map<String, Object> getTimestampIdentifier(Field src, Field trgt, SfdcIdentifier prop, boolean lookup,
			boolean directLookup) {
		return new FieldInfo(src, trgt, prop, lookup, directLookup).getFieldInfo();
	}

	public Map<String, Object> getEventIdentifier(Field src, Field trgt, SfdcIdentifier prop) {
		return new FieldInfo(src, trgt, prop).getFieldInfo();
	}

	public Map<String, Object> getEventIdentifier(Field src, Field trgt, SfdcIdentifier prop, boolean lookup,
			boolean directLookup) {
		return new FieldInfo(src, trgt, prop, lookup, directLookup).getFieldInfo();
	}

	public Map<String, Object> getCustomIdentifier(Field src, Field trgt) {
		FieldInfo mappingInfo = new FieldInfo(src, trgt);
		mappingInfo.setSourceType("USAGE_FEED");
		return mappingInfo.getFieldInfo();
	}

	public Map<String, Object> getGSIdentifier(Field src) {
		FieldInfo mappingInfo = new FieldInfo(src, src);
		mappingInfo.setSourceType("GS_DEFINED");
		mappingInfo.setSourceObject("");
		return mappingInfo.getFieldInfo();
	}

	public Map<String, Object> getMeasure(Field src, String aggFunc) {
		FieldInfo mappingInfo = new FieldInfo(src, src);
		mappingInfo.setSourceType("USAGE_FEED");
		mappingInfo.target.put("dbName", "");
		mappingInfo.setTargetAggFunc(aggFunc);
		return mappingInfo.getFieldInfo();
	}

}
