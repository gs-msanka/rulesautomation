package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionFromAddress {
    Object name;  //Object can be String value / a Field
    Object emailId; //Object can be String value / a Field
    Object replyTo; //Object can be String value / a Field

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getEmailId() {
        return emailId;
    }

    public void setEmailId(Object emailId) {
        this.emailId = emailId;
    }

    public Object getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Object replyTo) {
        this.replyTo = replyTo;
    }
}
