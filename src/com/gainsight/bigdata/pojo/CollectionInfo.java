package com.gainsight.bigdata.pojo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class CollectionInfo {

	@JsonProperty("CollectionName")
	String collectionName;
	@JsonProperty("dbType")
	String dbType;
	@JsonProperty("dataStoreType")
	String dataStoreType;
	@JsonProperty("Columns")
	List<Columns> columns;
	
	
	
	public CollectionInfo() {
		dbType = "DATA";
		dataStoreType = "MONGO";
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

	public List<Columns> getColumns() {
		return columns;
	}
	public void setColumns(List<Columns> columns) {
		this.columns = columns;
	}

		public class Columns {
		@JsonProperty("DisplayName")
		String displayName;
		
		@JsonProperty("datatype")
		String datatype;
		
		@JsonProperty("hidden")
		boolean hidden;
		
		@JsonProperty("indexable")
		int indexable;
		
		@JsonProperty("colattribtype")
		int colattribtype;
		
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String name) {
			this.displayName = name;
		}
		public String getDatatype() {
			return datatype;
		}
		public void setDatatype(String datatype) {
			this.datatype = datatype;
		}
		public boolean getHidden() {
			return hidden;
		}
		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}
		public int getIndexable() {
			return indexable;
		}
		public void setIndexable(int indexable) {
			this.indexable = indexable;
		}
		public int getColattribtype() {
			return colattribtype;
		}
		public void setColattribtype(int colattribtype) {
			this.colattribtype = colattribtype;
		}
	}
}
