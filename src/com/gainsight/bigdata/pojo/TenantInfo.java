package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantInfo {

    @JsonProperty("TenantId")
    private String tenantId;

	private String authToken;
    private String tenantName;
	
	@JsonProperty("ExternalTenantID")
	private String externalTenantID;
	
	@JsonProperty("ExternalTenantName")
	private String externalTenantName;
    private String tenantType;
    private boolean disabled;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
