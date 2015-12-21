package com.gainsight.bigdata.copilot.bean.emailProp;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.schema.JsonSerializableSchema;

/**
 * Created by Giribabu on 18/12/15.
 */

@JsonSerializableSchema
public class EmailProperties {
    private String emailAddress;
    private String[] unSubscribedCategories;
    private boolean bounced;

    @JsonProperty("emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    @JsonProperty("emailAddress")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @JsonProperty("removeFromUnsubscription")
    public String[] getRemoveFromUnsubscription() {
        return unSubscribedCategories;
    }

    @JsonProperty("unsubscribedCategories")
    public void setUnSubscribedCategories(String[] unSubscribedCategories) {
        this.unSubscribedCategories = unSubscribedCategories;
    }

    @JsonProperty("removeFromBounceList")
    public boolean isRemoveFromBounceList() {
        return bounced;
    }


    public void setBounced(boolean bounced) {
        this.bounced = bounced;
    }
}
