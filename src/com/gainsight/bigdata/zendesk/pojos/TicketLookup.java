package com.gainsight.bigdata.zendesk.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by Abhilash Thaduka on 2/24/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketLookup {

    private String subdomain;
    private int organization;
    private String organizationName;
    private String accountId;
    private String accountName;

    public int getOrganization() {
        return organization;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }
}