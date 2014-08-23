package com.gainsight.bigdata.pojo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class CollectionInfo {

	@JsonProperty("CollectionDetails")
	CollectionDetails collectionDetails;
	@JsonProperty("Columns")
	List<Columns> columns;

	public List<Columns> getColumns() {
		return columns;
	}

	public void setColumns(List<Columns> columns) {
		this.columns = columns;
	}

	public CollectionDetails getCollectionDetails() {
		return collectionDetails;
	}

	public void setCollectionDetails(CollectionDetails collectionDetails) {
		this.collectionDetails = collectionDetails;
	}

	public class CollectionDetails {
		@JsonProperty("CollectionName")
		String collectionName;
		@JsonProperty("dbType")
		String dbType;
		@JsonProperty("dataStoreType")
		String dataStoreType;

		public CollectionDetails() {
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
	}

	public class Columns {
		@JsonProperty("DisplayName")
		String displayName;

		@JsonProperty("datatype")
		String datatype;

		@JsonProperty("hidden")
		boolean hidden;

		@JsonProperty("colattribtype")
		int colattribtype;

		@JsonProperty("formula")
		String formula;

		@JsonProperty("useThousandSeparator")
		int useThousandSeparator;

		@JsonProperty("negativeNumber")
		String negativeNumber;

		@JsonProperty("decimalPlaces")
		int decimalPlaces;

		public Columns() {
			hidden = false;
			decimalPlaces = 0;
			formula = null;
			useThousandSeparator = 1;
			negativeNumber = "MINUSVALUE";
		}

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

		public int getColattribtype() {
			return colattribtype;
		}

		public void setColattribtype(int colattribtype) {
			this.colattribtype = colattribtype;
		}

		public String getFormula() {
			return formula;
		}

		public void setFormula(String formula) {
			this.formula = formula;
		}

		public int getUseThousandSeparator() {
			return useThousandSeparator;
		}

		public void setUseThousandSeparator(int useThousandSeparator) {
			this.useThousandSeparator = useThousandSeparator;
		}

		public String getNegativeNumber() {
			return negativeNumber;
		}

		public void setNegativeNumber(String negativeNumber) {
			this.negativeNumber = negativeNumber;
		}

		public int getDecimalPlaces() {
			return decimalPlaces;
		}

		public void setDecimalPlaces(int decimalPlaces) {
			this.decimalPlaces = decimalPlaces;
		}

	}
}
