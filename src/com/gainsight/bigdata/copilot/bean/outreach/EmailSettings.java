package com.gainsight.bigdata.copilot.bean.outreach;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class EmailSettings {
    @JsonProperty("SEND_EMAIL_ONLY_ONCE")
    private boolean sendEmailOnce;

    @JsonProperty("TEST_RUN")
    private boolean testRun;

    public boolean isSendEmailOnce() {
        return sendEmailOnce;
    }

    public void setSendEmailOnce(boolean sendEmailOnce) {
        this.sendEmailOnce = sendEmailOnce;
    }

    public boolean isTestRun() {
        return testRun;
    }

    public void setTestRun(boolean testRun) {
        this.testRun = testRun;
    }
}
