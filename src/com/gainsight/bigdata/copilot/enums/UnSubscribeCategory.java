package com.gainsight.bigdata.copilot.enums;

/**
 * Created by Giribabu on 16/12/15.
 */
public enum UnSubscribeCategory {
    SUCCESS_COMMUNICATION("success_communication"),
    SURVEY("survey");

    String value;

    UnSubscribeCategory(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
