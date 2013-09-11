package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class DimensionBrowserInfo {

	@JsonProperty("TenantId")
	String tenantId;
	@JsonProperty("CollectionName")
	String collectionName;
	@JsonProperty("Column")
	String column;
	
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
}
