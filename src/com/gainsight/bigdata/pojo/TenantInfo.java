package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class TenantInfo {

	String TenantName;
	String TenantId;
	
	@JsonProperty("ExternalTenantID")
	String externalTenantID;
	
	@JsonProperty("ExternalTenantName")
	String externalTenantName;

	public String getTenantName() {
		return TenantName;
	}

	public void setTenantName(String tenantName) {
		TenantName = tenantName;
	}

	public String getTenantId() {
		return TenantId;
	}

	public void setTenantId(String tenantId) {
		TenantId = tenantId;
	}

	public String getExternalTenantID() {
		return externalTenantID;
	}

	public void setExternalTenantID(String externalTenantID) {
		this.externalTenantID = externalTenantID;
	}

	public String getExternalTenantName() {
		return externalTenantName;
	}

	public void setExternalTenantName(String externalTenantName) {
		this.externalTenantName = externalTenantName;
	}

}
