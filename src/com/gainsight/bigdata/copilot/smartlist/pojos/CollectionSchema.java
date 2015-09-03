package com.gainsight.bigdata.copilot.smartlist.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionSchema {

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
    private boolean deleted;
    private boolean readOnly;
    private boolean columnsProcessed;
    @JsonProperty("_id")
    private documentID id;
    @JsonProperty("_class")
    private String classMaster;
    private createdDate createdDate;
    private createdDate modifiedDate;
    
    private class createdDate{
    	private String $date;

		public String get$date() {
			return $date;
		}

		public void set$date(String $date) {
			this.$date = $date;
		}
    }
    
    private class modifiedDate{
    	private String $date;

		public String get$date() {
			return $date;
		}

		public void set$date(String $date) {
			this.$date = $date;
		}
    }
    public documentID getId() {
		return id;
	}

	public void setId(documentID id) {
		this.id = id;
	}

	public String getClassMaster() {
		return classMaster;
	}

	public void setClassMaster(String classMaster) {
		this.classMaster = classMaster;
	}

	public static class documentID{
    	 @JsonProperty("$oid")
    	private String objectID;

		public String getObjectID() {
			return objectID;
		}

		public void setObjectID(String objectID) {
			this.objectID = objectID;
		}
    }
    

    public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isColumnsProcessed() {
		return columnsProcessed;
	}

	public void setColumnsProcessed(boolean columnsProcessed) {
		this.columnsProcessed = columnsProcessed;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@JsonProperty("Columns")
    private List<Column> columns;

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
    public static class CollectionDetails {
		@JsonProperty("CollectionName")
		private String collectionName;
		@JsonProperty("dbType")
        private String dbType;
		@JsonProperty("dataStoreType")
        private String dataStoreType;
        @JsonProperty("dbCollectionName")
        private String dbCollectionName;
        @JsonProperty("CollectionID")
        private String collectionId;

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
  
    @JsonIgnoreProperties(ignoreUnknown = true)
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

        private String defaultValue;
        private String groupName;
        private String measureValueBucket;

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

        private Mapping mappings;
        
        
        private static class Mapping{
        	@JsonProperty(value="SFDC")
    		SFDC sFDC;

    		public SFDC getsFDC() {
    			return sFDC;
    		}

    		public void setsFDC(SFDC sFDC) {
    			this.sFDC = sFDC;
    		}
    		private static class SFDC{
    			String key;
    			String dataType;
    			String format;
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
    			public String getFormat() {
    				return format;
    			}
    			public void setFormat(String format) {
    				this.format = format;
    			}
    			
    		} 
        	
        }

        private int maxLength;
        private boolean hasLookup; 

        public boolean isHasLookup() {
			return hasLookup;
		}

		public void setHasLookup(boolean hasLookup) {
			this.hasLookup = hasLookup;
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

        public Mapping getMappings() {
            return mappings;
        }

        public void setMappings(Mapping mappings) {
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
}
