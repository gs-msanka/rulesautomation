package com.gainsight.bigdata.copilot.bean.webhook.mandrill;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by gainsight on 16/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Open {

    @JsonProperty("ts")
    private Integer ts;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("location")
    private Object location;
    @JsonProperty("ua")
    private String ua;

    public Integer getTs() {
        return ts;
    }

    public void setTs(Integer ts) {
        this.ts = ts;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }
}
