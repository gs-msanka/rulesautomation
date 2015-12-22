package com.gainsight.bigdata.pojo;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionInfo {

    private String createdBy;
    private String modifiedBy;
    private String createdByName;
    private String modifiedByName;
    private String modifiedDateStr;
    private String createdDateStr;
    @JsonProperty("TenantId")
    private String tenantId;
    @JsonProperty("CollectionDetails")
    private CollectionDetails collectionDetails;
    @JsonProperty("tanentReportReadLimit")
    private int tenantReportReadLimit;
    @JsonProperty("TenantName")
    private String tenantName;

    @JsonProperty("Columns")
    private List<Column> columns;

    @JsonProperty("CollectionDescription")
    private String collectionDescription;

    public String getCollectionDescription() {
        return collectionDescription;
    }

    public void setCollectionDescription(String collectionDescription) {
        this.collectionDescription = collectionDescription;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getModifiedByName() {
        return modifiedByName;
    }

    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    public String getModifiedDateStr() {
        return modifiedDateStr;
    }

    public void setModifiedDateStr(String modifiedDateStr) {
        this.modifiedDateStr = modifiedDateStr;
    }

    public String getCreatedDateStr() {
        return createdDateStr;
    }

    public void setCreatedDateStr(String createdDateStr) {
        this.createdDateStr = createdDateStr;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public int getTenantReportReadLimit() {
        return tenantReportReadLimit;
    }

    public void setTenantReportReadLimit(int tenantReportReadLimit) {
        this.tenantReportReadLimit = tenantReportReadLimit;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public CollectionDetails getCollectionDetails() {
		return collectionDetails;
	}

	public void setCollectionDetails(CollectionDetails collectionDetails) {
		this.collectionDetails = collectionDetails;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class CollectionDetails {
		@JsonProperty("CollectionName")
		private String collectionName;
		@JsonProperty("dbType")
        private String dbType ="DATA";
		@JsonProperty("dataStoreType")
        private String dataStoreType ="MONGO";
        @JsonProperty("dbCollectionName")
        private String dbCollectionName;
        @JsonProperty("CollectionID")
        private String collectionId;
        private String assetType;
        private String entityType;

        public String getAssetType() {
            return assetType;
        }

        public void setAssetType(String assetType) {
            this.assetType = assetType;
        }

        public String getEntityType() {
            return entityType;
        }

        public void setEntityType(String entityType) {
            this.entityType = entityType;
        }

        public String getDbCollectionName() {
            return dbCollectionName;
        }

        public void setDbCollectionName(String dbCollectionName) {
            this.dbCollectionName = dbCollectionName;
        }

        public String getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(String collectionId) {
            this.collectionId = collectionId;
        }

        public String getCollectionName() {
			return collectionName;
		}

		public void setCollectionName(String collectionName) {
			this.collectionName = collectionName;
		}

		public String getDataStoreType() {
			return dataStoreType;
		}

		public void setDataStoreType(String dataStoreType) {
			this.dataStoreType = dataStoreType;
		}

		public String getDbType() {
			return dbType;
		}

		public void setDbType(String dbType) {
			this.dbType = dbType;
		}
	}

    public static class MappingsSFDC {
        @JsonProperty("SFDC")
        private Map<String, Object> sfdc;

        public Map<String, Object> getSfdc() {
            return sfdc;
        }

        public void setSfdc(Map<String, Object> sfdc) {
            this.sfdc = sfdc;
        }
    }

    public static class Operation {
        @JsonProperty("operands")
        private List<Map<String, String>> operand;
        private String operator;
        private String aggregateFunction;

        public String getAggregateFunction() {
            return aggregateFunction;
        }

        public void setAggregateFunction(String aggregateFunction) {
            this.aggregateFunction = aggregateFunction;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public List<Map<String, String>> getOperand() {
            return operand;
        }

        public void setOperand(List<Map<String, String>> operand) {
            this.operand = operand;
        }
    }

    public static class ColumnFormula {
        private String calculationType;
        private String measureName;
        private int level;
        private Operation operation;

        public Operation getOperation() {
            return operation;
        }

        public void setOperation(Operation operation) {
            this.operation = operation;
        }

        public String getCalculationType() {
            return calculationType;
        }

        public void setCalculationType(String calculationType) {
            this.calculationType = calculationType;
        }

        public String getMeasureName() {
            return measureName;
        }

        public void setMeasureName(String measureName) {
            this.measureName = measureName;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class Column {
        private String name;
        boolean hidden = false;
        private boolean indexed = false;
        private boolean primary = false;
        @JsonProperty("distinctMemberCount")
        private int distinctMemberCount = 0;
        @JsonProperty("dimensionBrowserCollection")
        private String dimensionBrowserCollection;
        private String alignment;
        private String aggFunction;
        private String numberType;
        @JsonProperty("decimalPlaces")
        private int decimalPlaces = 0;
        @JsonProperty("thousandSeparatorUsed")
        private boolean thousandSeparatorUsed;
        @JsonProperty("negativeNumber")
        private String negativeNumber;
        @JsonProperty("systemDefined")
        private boolean systemDefined = false;
        private boolean encrypted = false;
        private boolean deleted = false;
        @JsonProperty("DBName")
        private String dbName;
        private String datatype;
        private String dataType;
        @JsonProperty("DisplayName")
        private String displayName;
        @JsonProperty("colattribtype")
        private int columnAttributeType = 0;

        @JsonProperty("columnAttribute")
        private int columnAttribute;
        private int useThousandSeparator;
        private int maxLength =250;

        private String defaultValue;
        private String groupName;
        private String measureValueBucket;
        private String description;
        private boolean hasLookup=false;
        private String calculatedExpression;
        
		public String getCalculatedExpression() {
			return calculatedExpression;
		}

		public void setCalculatedExpression(String calculatedExpression) {
			this.calculatedExpression = calculatedExpression;
		}

        private LookUpDetail lookupDetail;

		public boolean isHasLookup() {
			return hasLookup;
		}

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setHasLookup(boolean hasLookup) {
            this.hasLookup = hasLookup;
        }

		public LookUpDetail getLookupDetail() {
			return lookupDetail;
		}

		public int getColumnAttribute() {
			return columnAttribute;
		}

		public void setColumnAttribute(int columnAttribute) {
			this.columnAttribute = columnAttribute;
		}


		public void setLookupDetail(LookUpDetail lookupDetail) {
			this.lookupDetail = lookupDetail;
		}

        public String getDatatype() {
            return datatype;
        }

        public void setDatatype(String datatype) {
            this.datatype = datatype;
        }

        public String getMeasureValueBucket() {
            return measureValueBucket;
        }

        public void setMeasureValueBucket(String measureValueBucket) {
            this.measureValueBucket = measureValueBucket;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public int getUseThousandSeparator() {
            return useThousandSeparator;
        }

        public void setUseThousandSeparator(int useThousandSeparator) {
            this.useThousandSeparator = useThousandSeparator;
        }

        private MappingsSFDC mappings;
        private List<ColumnFormula> formula;

        public List<ColumnFormula> getFormula() {
            return formula;
        }

        public void setFormula(List<ColumnFormula> formula) {
            this.formula = formula;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        private Map<String, String> format;

        public Map<String, String> getFormat() {
            return format;
        }

        public void setFormat(Map<String, String> format) {
            this.format = format;
        }

        public MappingsSFDC getMappings() {
            return mappings;
        }

        public void setMappings(MappingsSFDC mappings) {
            this.mappings = mappings;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public boolean isIndexed() {
            return indexed;
        }

        public void setIndexed(boolean indexed) {
            this.indexed = indexed;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public int getDistinctMemberCount() {
            return distinctMemberCount;
        }

        public void setDistinctMemberCount(int distinctMemberCount) {
            this.distinctMemberCount = distinctMemberCount;
        }

        public String getDimensionBrowserCollection() {
            return dimensionBrowserCollection;
        }

        public void setDimensionBrowserCollection(String dimensionBrowserCollection) {
            this.dimensionBrowserCollection = dimensionBrowserCollection;
        }

        public String getAlignment() {
            return alignment;
        }

        public void setAlignment(String alignment) {
            this.alignment = alignment;
        }

        public String getAggFunction() {
            return aggFunction;
        }

        public void setAggFunction(String aggFunction) {
            this.aggFunction = aggFunction;
        }

        public String getNumberType() {
            return numberType;
        }

        public void setNumberType(String numberType) {
            this.numberType = numberType;
        }

        public int getDecimalPlaces() {
            return decimalPlaces;
        }

        public void setDecimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
        }

        public boolean isThousandSeparatorUsed() {
            return thousandSeparatorUsed;
        }

        public void setThousandSeparatorUsed(boolean thousandSeparatorUsed) {
            this.thousandSeparatorUsed = thousandSeparatorUsed;
        }

        public String getNegativeNumber() {
            return negativeNumber;
        }

        public void setNegativeNumber(String negativeNumber) {
            this.negativeNumber = negativeNumber;
        }

        public boolean isSystemDefined() {
            return systemDefined;
        }

        public void setSystemDefined(boolean systemDefined) {
            this.systemDefined = systemDefined;
        }

        public boolean isEncrypted() {
            return encrypted;
        }

        public void setEncrypted(boolean encrypted) {
            this.encrypted = encrypted;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public int getColumnAttributeType() {
            return columnAttributeType;
        }

        public void setColumnAttributeType(int columnAttributeType) {
            this.columnAttributeType = columnAttributeType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LookUpDetail {

		private String name;
		private String lookupId;
		private String collectionId;
		private String dbCollectionName;
		private String fieldDBName;
		private String collectionName;
		private String columnDisplayName;

		public String getCollectionName() {
			return collectionName;
		}

		public void setCollectionName(String collectionName) {
			this.collectionName = collectionName;
		}

		public String getColumnDisplayName() {
			return columnDisplayName;
		}

		public void setColumnDisplayName(String columnDisplayName) {
			this.columnDisplayName = columnDisplayName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLookupId() {
			return lookupId;
		}

		public void setLookupId(String lookupId) {
			this.lookupId = lookupId;
		}

		public String getCollectionId() {
			return collectionId;
		}

		public void setCollectionId(String collectionId) {
			this.collectionId = collectionId;
		}

		public String getDbCollectionName() {
			return dbCollectionName;
		}

		public void setDbCollectionName(String dbCollectionName) {
			this.dbCollectionName = dbCollectionName;
		}

		public String getFieldDBName() {
			return fieldDBName;
		}

		public void setFieldDBName(String fieldDBName) {
			this.fieldDBName = fieldDBName;
		}

        @Override
        public String toString() {
            return "LookUpDetail{" +
                    "name='" + name + '\'' +
                    ", lookupId='" + lookupId + '\'' +
                    ", collectionId='" + collectionId + '\'' +
                    ", dbCollectionName='" + dbCollectionName + '\'' +
                    ", fieldDBName='" + fieldDBName + '\'' +
                    ", collectionName='" + collectionName + '\'' +
                    ", columnDisplayName='" + columnDisplayName + '\'' +
                    '}';
        }
    }
}
