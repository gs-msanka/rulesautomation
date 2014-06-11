package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class TenantInfo {

	String TenantId;
	String authToken;

    @JsonProperty("TenantName")
    String TenantName;
	
	@JsonProperty("ExternalTenantID")
	String externalTenantID;
	
	@JsonProperty("ExternalTenantName")
	String externalTenantName;

    @JsonProperty("tenantType")
    String tenantType;

    @JsonProperty("disabled")
    boolean disabled;


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

    public String getTenantType() {
        return tenantType;
    }

    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
}
