package com.gainsight.bigdata.copilot.smartlist.pojos;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class TriggerCriteria {

	private String whereLogic;
	private String refField;
	private String timeIdentifier;
	@JsonProperty("select")
	private List<Select> select;
	private String calculatedFields[];
	private String collectionId;
	private String criteria[];

	public String getWhereLogic() {
		return whereLogic;
	}

	public void setWhereLogic(String whereLogic) {
		this.whereLogic = whereLogic;
	}

	public String getRefField() {
		return refField;
	}

	public void setRefField(String refField) {
		this.refField = refField;
	}

	public String getTimeIdentifier() {
		return timeIdentifier;
	}

	public void setTimeIdentifier(String timeIdentifier) {
		this.timeIdentifier = timeIdentifier;
	}

	public String[] getCalculatedFields() {
		return calculatedFields;
	}

	public void setCalculatedFields(String[] calculatedFields) {
		this.calculatedFields = calculatedFields;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String[] getCriteria() {
		return criteria;
	}

	public void setCriteria(String[] criteria) {
		this.criteria = criteria;
	}

	public List<Select> getSelect() {
		return select;
	}

	public void setSelect(List<Select> select) {
		this.select = select;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Select {
		private String type;
		private String field;
		private String fieldName;
		private String entity;
		private String valueType;
		private String dataType;
		private String fieldType;
		private boolean groupable;
		private String objectName;
		private String label;
		private String alias;
		private String aggregation;
		@JsonProperty("properties")
		private Properties properties;
		@JsonProperty("meta")
		private Meta meta;
		@JsonProperty("isAccountIdRelatedField")
		private Boolean isAccountIdRelatedField;
		@JsonProperty("collectionId")
		private String collectionId;



		private Select() {
			super();
		}

		public Meta getMeta() {
			return meta;
		}

		public void setMeta(Meta meta) {
			this.meta = meta;
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		private static class Meta {
			@JsonProperty("isAccessible")
			private Boolean isAccessible;
			@JsonProperty("isFilterable")
			private Boolean isFilterable;
			@JsonProperty("isGroupable")
			private Boolean isGroupable;
			private int colattribtype;
			private String originalDataType;
			private int decimalPlaces;
			@JsonProperty("mappings")
			private Mappings mappings;


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


			@JsonProperty("isGroupable")
			public Boolean getIsGroupable() {
			return isGroupable;
			}

			@JsonProperty("isGroupable")
			public void setIsGroupable(Boolean isGroupable) {
			this.isGroupable = isGroupable;
			}


			public int getColattribtype() {
				return colattribtype;
			}

			public void setColattribtype(int colattribtype) {
				this.colattribtype = colattribtype;
			}

			public String getOriginalDataType() {
				return originalDataType;
			}

			public void setOriginalDataType(String originalDataType) {
				this.originalDataType = originalDataType;
			}

			public int getDecimalPlaces() {
				return decimalPlaces;
			}

			public void setDecimalPlaces(int decimalPlaces) {
				this.decimalPlaces = decimalPlaces;
			}
			public Mappings getMappings() {
				return mappings;
			}

			public void setMappings(Mappings mappings) {
				this.mappings = mappings;
			}

			private static class Mappings {
				@JsonProperty(value = "SFDC")
				SFDC sFDC;

				public SFDC getsFDC() {
					return sFDC;
				}

				public void setsFDC(SFDC sFDC) {
					this.sFDC = sFDC;
				}

				private static class SFDC {
					String key;
					String dataType;

					public String getKey() {
						return key;
					}

					public void setKey(String key) {
						this.key = key;
					}

					public String getDataType() {
						return dataType;
					}

					public void setDataType(String dataType) {
						this.dataType = dataType;
					}
				}
			}
		}

		public String getCollectionId() {
			return collectionId;
		}

		public void setCollectionId(String collectionId) {
			this.collectionId = collectionId;
		}
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getEntity() {
			return entity;
		}

		public void setEntity(String entity) {
			this.entity = entity;
		}

		public String getValueType() {
			return valueType;
		}

		public void setValueType(String valueType) {
			this.valueType = valueType;
		}

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public boolean getGroupable() {
			return groupable;
		}

		public void setGroupable(boolean groupable) {
			this.groupable = groupable;
		}

		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getAggregation() {
			return aggregation;
		}

		public void setAggregation(String aggregation) {
			this.aggregation = aggregation;
		}

		public Properties getProperties() {
			return properties;
		}

		public void setProperties(Properties properties) {
			this.properties = properties;
		}

		private static class Properties {
			@JsonProperty(value = "SFDC")
			private SFDC sFDC;

			public SFDC getsFDC() {
				return sFDC;
			}

			public void setsFDC(SFDC sFDC) {
				this.sFDC = sFDC;
			}

			private static class SFDC {
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

	}
}
