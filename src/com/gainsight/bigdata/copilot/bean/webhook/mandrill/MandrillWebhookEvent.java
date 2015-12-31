package com.gainsight.bigdata.copilot.bean.webhook.mandrill;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 16/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MandrillWebhookEvent {

    @JsonProperty("event")
    private MandrillEvent event;
    @JsonProperty("ts")
    private Long ts;
    @JsonProperty("user_agent")
    private String userAgent;
    @JsonProperty("user_agent_parsed")
    private UserAgentParsed userAgentParsed;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("url")
    private String url;
    @JsonProperty("location")
    private Location location;
    @JsonProperty("_id")
    private String Id;
    @JsonProperty("msg")
    private Message message;

    public MandrillEvent getEvent() {
        return event;
    }

    public void setEvent(MandrillEvent event) {
        this.event = event;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public UserAgentParsed getUserAgentParsed() {
        return userAgentParsed;
    }

    public void setUserAgentParsed(UserAgentParsed userAgentParsed) {
        this.userAgentParsed = userAgentParsed;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
