package com.gainsight.bigdata.copilot.enums;

/**
 * Created by Giribabu on 16/12/15.
 */
public enum UnSubscribeReason {

    NO_LONGER_WANT("no_longer_want"),
    I_NEVER_SIGNED("i_never_signed"),
    EMAIL_INAPPROPRIATE("emails_inappropriate"),
    EMAIL_SPAM("emails_spam"),
    OTHER("other");


    String value;

    UnSubscribeReason(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

}
