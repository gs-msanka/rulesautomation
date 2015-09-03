package com.gainsight.bigdata.copilot.smartlist.pojos;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;


@JsonPropertyOrder({ "params", "actionType", "recipientStrategy",
		"identifierType", "queries", "recipientFieldName" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionInfo {

	@JsonProperty("params")
	private Params params;
	@JsonProperty("actionType")
	private String actionType;
	@JsonProperty("recipientStrategy")
	private String recipientStrategy;
	@JsonProperty("identifierType")
	private String identifierType;
	@JsonProperty("queries")
	private List<Query> queries = new ArrayList<Query>();
	@JsonProperty("recipientFieldName")
	private String recipientFieldName;

	@JsonProperty("params")
	public Params getParams() {
		return params;
	}

	@JsonProperty("params")
	public void setParams(Params params) {
		this.params = params;
	}

	@JsonProperty("actionType")
	public String getActionType() {
		return actionType;
	}

	@JsonProperty("actionType")
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	@JsonProperty("recipientStrategy")
	public String getRecipientStrategy() {
		return recipientStrategy;
	}

	@JsonProperty("recipientStrategy")
	public void setRecipientStrategy(String recipientStrategy) {
		this.recipientStrategy = recipientStrategy;
	}

	@JsonProperty("identifierType")
	public String getIdentifierType() {
		return identifierType;
	}

	@JsonProperty("identifierType")
	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	@JsonProperty("queries")
	public List<Query> getQueries() {
		return queries;
	}

	@JsonProperty("queries")
	public void setQueries(List<Query> queries) {
		this.queries = queries;
	}

	@JsonProperty("recipientFieldName")
	public String getRecipientFieldName() {
		return recipientFieldName;
	}

	@JsonProperty("recipientFieldName")
	public void setRecipientFieldName(String recipientFieldName) {
		this.recipientFieldName = recipientFieldName;
	}

	public static class ExternalIdentifier {

		@JsonProperty("type")
		private String type;
		@JsonProperty("field")
		private String field;
		@JsonProperty("entity")
		private String entity;
		@JsonIgnore
		@JsonProperty("uniqueName")
		private String uniqueName;
		@JsonIgnore
		@JsonProperty("parentObj")
		private String parentObj;
		@JsonProperty("valueType")
		private String valueType;
		@JsonProperty("aggregation")
		private String aggregation;

		@JsonProperty("type")
		public String getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(String type) {
			this.type = type;
		}

		@JsonProperty("field")
		public String getField() {
			return field;
		}

		@JsonProperty("field")
		public void setField(String field) {
			this.field = field;
		}

		@JsonProperty("entity")
		public String getEntity() {
			return entity;
		}

		@JsonProperty("entity")
		public void setEntity(String entity) {
			this.entity = entity;
		}

		@JsonIgnore
		@JsonProperty("uniqueName")
		public String getUniqueName() {
			return uniqueName;
		}

		@JsonIgnore
		@JsonProperty("uniqueName")
		public void setUniqueName(String uniqueName) {
			this.uniqueName = uniqueName;
		}

		@JsonIgnore
		@JsonProperty("parentObj")
		public String getParentObj() {
			return parentObj;
		}

		@JsonIgnore
		@JsonProperty("parentObj")
		public void setParentObj(String parentObj) {
			this.parentObj = parentObj;
		}

		@JsonProperty("valueType")
		public String getValueType() {
			return valueType;
		}

		@JsonProperty("valueType")
		public void setValueType(String valueType) {
			this.valueType = valueType;
		}

		@JsonProperty("aggregation")
		public String getAggregation() {
			return aggregation;
		}

		@JsonProperty("aggregation")
		public void setAggregation(String aggregation) {
			this.aggregation = aggregation;
		}
	}

	@JsonPropertyOrder({ "type", "field", "entity", "uniqueName", "parentObj",
			"valueType", "aggregation" })
	public static class Identifier {

		@JsonProperty("type")
		private String type;
		@JsonProperty("field")
		private String field;
		@JsonProperty("entity")
		private String entity;
		@JsonProperty("uniqueName")
		private String uniqueName;
		@JsonProperty("parentObj")
		private String parentObj;
		@JsonProperty("valueType")
		private String valueType;
		@JsonProperty("aggregation")
		private String aggregation;

		@JsonProperty("type")
		public String getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(String type) {
			this.type = type;
		}

		@JsonProperty("field")
		public String getField() {
			return field;
		}

		@JsonProperty("field")
		public void setField(String field) {
			this.field = field;
		}

		@JsonProperty("entity")
		public String getEntity() {
			return entity;
		}

		@JsonProperty("entity")
		public void setEntity(String entity) {
			this.entity = entity;
		}

		@JsonProperty("uniqueName")
		public String getUniqueName() {
			return uniqueName;
		}

		@JsonProperty("uniqueName")
		public void setUniqueName(String uniqueName) {
			this.uniqueName = uniqueName;
		}

		@JsonProperty("parentObj")
		public String getParentObj() {
			return parentObj;
		}

		@JsonProperty("parentObj")
		public void setParentObj(String parentObj) {
			this.parentObj = parentObj;
		}

		@JsonProperty("valueType")
		public String getValueType() {
			return valueType;
		}

		@JsonProperty("valueType")
		public void setValueType(String valueType) {
			this.valueType = valueType;
		}

		@JsonProperty("aggregation")
		public String getAggregation() {
			return aggregation;
		}

		@JsonProperty("aggregation")
		public void setAggregation(String aggregation) {
			this.aggregation = aggregation;
		}
	}

	@JsonPropertyOrder({ "type", "field", "fieldName", "entity", "valueType",
			"dataType", "fieldType", "groupable", "objectName", "label",
			"alias", "aggregation", "properties", "meta", "isExternalCriteria",
			"isReferenceField", "isJoinField" })
	public static class LookUpFieldInfo {

		@JsonProperty("type")
		private String type;
		@JsonProperty("field")
		private String field;
		@JsonProperty("fieldName")
		private String fieldName;
		@JsonProperty("entity")
		private String entity;
		@JsonProperty("valueType")
		private String valueType;
		@JsonProperty("dataType")
		private String dataType;
		@JsonProperty("fieldType")
		private String fieldType;
		@JsonProperty("groupable")
		private Boolean groupable;
		@JsonProperty("objectName")
		private String objectName;
		@JsonProperty("label")
		private String label;
		@JsonProperty("alias")
		private String alias;
		@JsonProperty("aggregation")
		private String aggregation;
		@JsonProperty("properties")
		private Properties properties;
		@JsonProperty("meta")
		private Meta meta;
		@JsonProperty("isExternalCriteria")
		private Boolean isExternalCriteria;
		@JsonProperty("isReferenceField")
		private Boolean isReferenceField;
		@JsonProperty("isJoinField")
		private Boolean isJoinField;

		@JsonProperty("type")
		public String getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(String type) {
			this.type = type;
		}

		@JsonProperty("field")
		public String getField() {
			return field;
		}

		@JsonProperty("field")
		public void setField(String field) {
			this.field = field;
		}

		@JsonProperty("fieldName")
		public String getFieldName() {
			return fieldName;
		}

		@JsonProperty("fieldName")
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		@JsonProperty("entity")
		public String getEntity() {
			return entity;
		}

		@JsonProperty("entity")
		public void setEntity(String entity) {
			this.entity = entity;
		}

		@JsonProperty("valueType")
		public String getValueType() {
			return valueType;
		}

		@JsonProperty("valueType")
		public void setValueType(String valueType) {
			this.valueType = valueType;
		}

		@JsonProperty("dataType")
		public String getDataType() {
			return dataType;
		}

		@JsonProperty("dataType")
		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		/**
		 * 
		 * @return The fieldType
		 */
		@JsonProperty("fieldType")
		public String getFieldType() {
			return fieldType;
		}

		@JsonProperty("fieldType")
		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		@JsonProperty("groupable")
		public Boolean getGroupable() {
			return groupable;
		}

		@JsonProperty("groupable")
		public void setGroupable(Boolean groupable) {
			this.groupable = groupable;
		}

		@JsonProperty("objectName")
		public String getObjectName() {
			return objectName;
		}

		@JsonProperty("objectName")
		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		@JsonProperty("label")
		public String getLabel() {
			return label;
		}

		@JsonProperty("label")
		public void setLabel(String label) {
			this.label = label;
		}

		@JsonProperty("alias")
		public String getAlias() {
			return alias;
		}

		@JsonProperty("alias")
		public void setAlias(String alias) {
			this.alias = alias;
		}

		@JsonProperty("aggregation")
		public String getAggregation() {
			return aggregation;
		}

		@JsonProperty("aggregation")
		public void setAggregation(String aggregation) {
			this.aggregation = aggregation;
		}

		@JsonProperty("properties")
		public Properties getProperties() {
			return properties;
		}

		@JsonProperty("properties")
		public void setProperties(Properties properties) {
			this.properties = properties;
		}

		@JsonProperty("meta")
		public Meta getMeta() {
			return meta;
		}

		@JsonProperty("meta")
		public void setMeta(Meta meta) {
			this.meta = meta;
		}

		@JsonProperty("isExternalCriteria")
		public Boolean getIsExternalCriteria() {
			return isExternalCriteria;
		}

		@JsonProperty("isExternalCriteria")
		public void setIsExternalCriteria(Boolean isExternalCriteria) {
			this.isExternalCriteria = isExternalCriteria;
		}

		@JsonProperty("isReferenceField")
		public Boolean getIsReferenceField() {
			return isReferenceField;
		}

		@JsonProperty("isReferenceField")
		public void setIsReferenceField(Boolean isReferenceField) {
			this.isReferenceField = isReferenceField;
		}

		@JsonProperty("isJoinField")
		public Boolean getIsJoinField() {
			return isJoinField;
		}

		@JsonProperty("isJoinField")
		public void setIsJoinField(Boolean isJoinField) {
			this.isJoinField = isJoinField;
		}
	}

	@JsonPropertyOrder({ "isAccessible", "isFilterable", "isSortable",
			"isGroupable", "originalDataType", "isCreateable", "precision",
			"relationshipName" })
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Meta {

		@JsonProperty("isAccessible")
		private Boolean isAccessible;
		@JsonProperty("isFilterable")
		private Boolean isFilterable;
		@JsonProperty("isSortable")
		private Boolean isSortable;
		@JsonProperty("isGroupable")
		private Boolean isGroupable;
		@JsonProperty("originalDataType")
		private String originalDataType;
		@JsonProperty("isCreateable")
		private Boolean isCreateable;
		@JsonProperty("precision")
		private Integer precision;
		@JsonProperty("relationshipName")
		private String relationshipName;

		@JsonProperty("isAccessible")
		public Boolean getIsAccessible() {
			return isAccessible;
		}

		@JsonProperty("isAccessible")
		public void setIsAccessible(Boolean isAccessible) {
			this.isAccessible = isAccessible;
		}

		@JsonProperty("isFilterable")
		public Boolean getIsFilterable() {
			return isFilterable;
		}

		@JsonProperty("isFilterable")
		public void setIsFilterable(Boolean isFilterable) {
			this.isFilterable = isFilterable;
		}

		@JsonProperty("isSortable")
		public Boolean getIsSortable() {
			return isSortable;
		}

		@JsonProperty("isSortable")
		public void setIsSortable(Boolean isSortable) {
			this.isSortable = isSortable;
		}

		@JsonProperty("isGroupable")
		public Boolean getIsGroupable() {
			return isGroupable;
		}

		@JsonProperty("isGroupable")
		public void setIsGroupable(Boolean isGroupable) {
			this.isGroupable = isGroupable;
		}

		@JsonProperty("originalDataType")
		public String getOriginalDataType() {
			return originalDataType;
		}

		@JsonProperty("originalDataType")
		public void setOriginalDataType(String originalDataType) {
			this.originalDataType = originalDataType;
		}

		@JsonProperty("isCreateable")
		public Boolean getIsCreateable() {
			return isCreateable;
		}

		@JsonProperty("isCreateable")
		public void setIsCreateable(Boolean isCreateable) {
			this.isCreateable = isCreateable;
		}

		@JsonProperty("precision")
		public Integer getPrecision() {
			return precision;
		}

		@JsonProperty("precision")
		public void setPrecision(Integer precision) {
			this.precision = precision;
		}

		@JsonProperty("relationshipName")
		public String getRelationshipName() {
			return relationshipName;
		}

		@JsonProperty("relationshipName")
		public void setRelationshipName(String relationshipName) {
			this.relationshipName = relationshipName;
		}
	}

	public static class Params {

		@JsonProperty("areaName")
		private String areaName;

		@JsonProperty("areaName")
		public String getAreaName() {
			return areaName;
		}

		@JsonProperty("areaName")
		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
	}

	@JsonPropertyOrder({ "SFDC" })
	public static class Properties {

		@JsonProperty("SFDC")
		private SFDC SFDC;

		@JsonProperty("SFDC")
		public SFDC getSFDC() {
			return SFDC;
		}

		@JsonProperty("SFDC")
		public void setSFDC(SFDC SFDC) {
			this.SFDC = SFDC;
		}
	}

	@JsonPropertyOrder({ "externalIdentifier", "lookUpFieldInfos",
			"identifier", "query" })
	public static class Query {

		@JsonProperty("externalIdentifier")
		private ExternalIdentifier externalIdentifier;
		@JsonProperty("lookUpFieldInfos")
		private List<LookUpFieldInfo> lookUpFieldInfos = new ArrayList<LookUpFieldInfo>();
		@JsonProperty("identifier")
		private Identifier identifier;
		@JsonProperty("query")
		private String query;

		@JsonProperty("externalIdentifier")
		public ExternalIdentifier getExternalIdentifier() {
			return externalIdentifier;
		}

		@JsonProperty("externalIdentifier")
		public void setExternalIdentifier(ExternalIdentifier externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
		}

		@JsonProperty("lookUpFieldInfos")
		public List<LookUpFieldInfo> getLookUpFieldInfos() {
			return lookUpFieldInfos;
		}

		@JsonProperty("lookUpFieldInfos")
		public void setLookUpFieldInfos(List<LookUpFieldInfo> lookUpFieldInfos) {
			this.lookUpFieldInfos = lookUpFieldInfos;
		}

		@JsonProperty("identifier")
		public Identifier getIdentifier() {
			return identifier;
		}

		@JsonProperty("identifier")
		public void setIdentifier(Identifier identifier) {
			this.identifier = identifier;
		}

		@JsonProperty("query")
		public String getQuery() {
			return query;
		}

		@JsonProperty("query")
		public void setQuery(String query) {
			this.query = query;
		}
	}

	public static class SFDC {

		@JsonProperty("keys")
		private List<String> keys = new ArrayList<String>();

		@JsonProperty("keys")
		public List<String> getKeys() {
			return keys;
		}

		@JsonProperty("keys")
		public void setKeys(List<String> keys) {
			this.keys = keys;
		}
	}
}
