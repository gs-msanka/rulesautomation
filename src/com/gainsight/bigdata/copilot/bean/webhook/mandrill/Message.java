package com.gainsight.bigdata.copilot.bean.webhook.mandrill;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gainsight on 16/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Message {

    @JsonProperty("ts")
    private Long ts;
    @JsonProperty("_id")
    private String Id;
    @JsonProperty("state")
    private String state;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("email")
    private String email;
    @JsonProperty("opens")
    private List<Open> opens = new ArrayList<Open>();
    @JsonProperty("clicks")
    private List<Object> clicks = new ArrayList<>();
    @JsonProperty("_version")
    private String Version;
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("sender")
    private String sender;
    @JsonProperty("template")
    private Object template;
    @JsonProperty("subaccount")
    private String subaccount;

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Open> getOpens() {
        return opens;
    }

    public void setOpens(List<Open> opens) {
        this.opens = opens;
    }

    public List<Object> getClicks() {
        return clicks;
    }

    public void setClicks(List<Object> clicks) {
        this.clicks = clicks;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Object getTemplate() {
        return template;
    }

    public void setTemplate(Object template) {
        this.template = template;
    }

    public String getSubaccount() {
        return subaccount;
    }

    public void setSubaccount(String subaccount) {
        this.subaccount = subaccount;
    }
}
