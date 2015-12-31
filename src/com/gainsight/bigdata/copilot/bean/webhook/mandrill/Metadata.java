package com.gainsight.bigdata.copilot.bean.webhook.mandrill;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by gainsight on 16/12/15.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Metadata {

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

    @JsonProperty("extId")
    private String externalId;

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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
