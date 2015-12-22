package com.gainsight.bigdata.copilot.bean.webhook.mandrill;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by gainsight on 16/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UserAgentParsed {

    @JsonProperty("type")
    private String type;
    @JsonProperty("ua_family")
    private String uaFamily;
    @JsonProperty("ua_name")
    private String uaName;
    @JsonProperty("ua_version")
    private Object uaVersion;
    @JsonProperty("ua_url")
    private String uaUrl;
    @JsonProperty("ua_company")
    private String uaCompany;
    @JsonProperty("ua_company_url")
    private String uaCompanyUrl;
    @JsonProperty("ua_icon")
    private String uaIcon;
    @JsonProperty("os_family")
    private String osFamily;
    @JsonProperty("os_name")
    private String osName;
    @JsonProperty("os_url")
    private String osUrl;
    @JsonProperty("os_company")
    private Object osCompany;
    @JsonProperty("os_company_url")
    private Object osCompanyUrl;
    @JsonProperty("os_icon")
    private String osIcon;
    @JsonProperty("mobile")
    private Boolean mobile;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUaFamily() {
        return uaFamily;
    }

    public void setUaFamily(String uaFamily) {
        this.uaFamily = uaFamily;
    }

    public String getUaName() {
        return uaName;
    }

    public void setUaName(String uaName) {
        this.uaName = uaName;
    }

    public Object getUaVersion() {
        return uaVersion;
    }

    public void setUaVersion(Object uaVersion) {
        this.uaVersion = uaVersion;
    }

    public String getUaUrl() {
        return uaUrl;
    }

    public void setUaUrl(String uaUrl) {
        this.uaUrl = uaUrl;
    }

    public String getUaCompany() {
        return uaCompany;
    }

    public void setUaCompany(String uaCompany) {
        this.uaCompany = uaCompany;
    }

    public String getUaCompanyUrl() {
        return uaCompanyUrl;
    }

    public void setUaCompanyUrl(String uaCompanyUrl) {
        this.uaCompanyUrl = uaCompanyUrl;
    }

    public String getUaIcon() {
        return uaIcon;
    }

    public void setUaIcon(String uaIcon) {
        this.uaIcon = uaIcon;
    }

    public String getOsFamily() {
        return osFamily;
    }

    public void setOsFamily(String osFamily) {
        this.osFamily = osFamily;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsUrl() {
        return osUrl;
    }

    public void setOsUrl(String osUrl) {
        this.osUrl = osUrl;
    }

    public Object getOsCompany() {
        return osCompany;
    }

    public void setOsCompany(Object osCompany) {
        this.osCompany = osCompany;
    }

    public Object getOsCompanyUrl() {
        return osCompanyUrl;
    }

    public void setOsCompanyUrl(Object osCompanyUrl) {
        this.osCompanyUrl = osCompanyUrl;
    }

    public String getOsIcon() {
        return osIcon;
    }

    public void setOsIcon(String osIcon) {
        this.osIcon = osIcon;
    }

    public Boolean getMobile() {
        return mobile;
    }

    public void setMobile(Boolean mobile) {
        this.mobile = mobile;
    }
}
