package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This is simple class to hold on just the
 * high level tenant information like org id, org name, tenant id, tenant name & know about redshift enabled.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantInfo {

    private String tenantId;
    private String tenantName;

	private String orgId;
	private String orgName;
    private boolean redShiftEnabled;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public boolean isRedShiftEnabled() {
        return redShiftEnabled;
    }

    public void setRedShiftEnabled(boolean redShiftEnabled) {
        this.redShiftEnabled = redShiftEnabled;
    }

    @Override
    public String toString() {
        return "TenantInfo{" +
                "tenantId='" + tenantId + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", orgId='" + orgId + '\'' +
                ", orgName='" + orgName + '\'' +
                ", redShiftEnabled=" + redShiftEnabled +
                '}';
    }
}
