package com.gainsight.bigdata.copilot.bean.webhook.sendgrid;


import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 16/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SendgridWebhookEvent {

    private String email;
    @JsonProperty("sg_event_id")
    private String eventId;
    @JsonProperty("sg_message_id")
    private String messageId;
    private Long timestamp;
    private String url;
    @JsonProperty("smtp-id")
    private String smtpId;
    private SendgridEvent event;
    private List<String> category;
    private String useragent;
    private String ip;
    private String reason;
    private String status;

    @JsonProperty("useCase")
    private String useCase;

    @JsonProperty("campaign")
    private String campaign;

    @JsonProperty("campaignName")
    private String campaignName;

    @JsonProperty("schedule")
    private String schedule;

    @JsonProperty("templateName")
    private String templateName;

    @JsonProperty("subaccount")
    private String tenantId;

    @JsonProperty("extId")
    private String externalId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSmtpId() {
        return smtpId;
    }

    public void setSmtpId(String smtpId) {
        this.smtpId = smtpId;
    }

    public SendgridEvent getEvent() {
        return event;
    }

    public void setEvent(SendgridEvent event) {
        this.event = event;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUseCase() {
        return useCase;
    }

    public void setUseCase(String useCase) {
        this.useCase = useCase;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "SendgridWebhookEvent{" +
                "email='" + email + '\'' +
                ", eventId='" + eventId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", timestamp=" + timestamp +
                ", url='" + url + '\'' +
                ", smtpId='" + smtpId + '\'' +
                ", event='" + event + '\'' +
                ", category=" + category +
                ", useragent='" + useragent + '\'' +
                ", ip='" + ip + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", useCase='" + useCase + '\'' +
                ", campaign='" + campaign + '\'' +
                ", campaignName='" + campaignName + '\'' +
                ", schedule='" + schedule + '\'' +
                ", templateName='" + templateName + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", externalId='" + externalId + '\'' +
                '}';
    }
}

